package sakura.kooi.NMSTranslator;

import org.bukkit.plugin.java.JavaPlugin;
import sakura.kooi.NMSTranslator.api.SupportedVersion;
import sakura.kooi.NMSTranslator.lib.common.reflect.ClassPath;
import sakura.kooi.NMSTranslator.transformer.Transformer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public final class ImplLoader {
    private final JavaPlugin plugin;
    private final String pkg;
    private final boolean saveImpls;
    private ClassPath classScanner;
    private ImplClassLoader classLoader;
    private final File implFolder;
    private final HashMap<String, Class<?>> implCache = new HashMap<>();

    private ImplLoader(JavaPlugin plugin, String pkg, boolean saveImpls) throws IOException {
        this.plugin = plugin;
        this.pkg = pkg;
        this.saveImpls = saveImpls;
        this.implFolder = new File(plugin.getDataFolder(), "implements");
        this.classScanner = ClassPath.from(plugin.getClass().getClassLoader());
        this.classLoader = new ImplClassLoader(plugin.getClass().getClassLoader());
    }

    private File getImplFile(final String impl) {
        return new File(implFolder, impl + "_" + NMSTranslatorMain.getNmsVersion() + ".class");
    }
    public static ImplLoader forPlugin(JavaPlugin plugin, String pkg, boolean saveImpls) throws IOException {
        ImplLoader implLoader = new ImplLoader(plugin, pkg, saveImpls);
        if (saveImpls && !implLoader.implFolder.exists()) {
            plugin.getLogger().info("ImplLoader | Creating implements directory...");
            implLoader.implFolder.mkdirs();
        }
        return implLoader;
    }

    @SuppressWarnings("unchecked")
    public <T> T of(@Nonnull Class<T> interfaceClass, @Nullable Class<? extends T> fallback) throws ReflectiveOperationException {
        String implName = interfaceClass.getSimpleName();
        if (implCache.containsKey(implName)) {
            plugin.getLogger().info(() -> "ImplLoader | Creating " + implName + " implements from cache...");
            try {
                return (T) implCache.get(implName).newInstance();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                plugin.getLogger().severe(() -> "ImplLoader | Error occurred while constructing " + implName + " object...");
                if (fallback == null) {
                    throw e;
                }
                plugin.getLogger().warning(() -> "ImplLoader | Creating " + implName + "fallback instance...");
                return fallback.newInstance();
            }
        }

        byte[] bytecode;
        if (saveImpls && getImplFile(implName).exists()) {
            plugin.getLogger().info(() -> "ImplLoader | Creating " + implName + " implements from disk...");
            // load from disk
            try {
                bytecode = Files.readAllBytes(getImplFile(implName).toPath());
            } catch (IOException e) { // rebuild
                e.printStackTrace();
                plugin.getLogger().severe(() -> "ImplLoader | Error occurred while loading " + implName + ".class from disk...");
                bytecode = buildImplements(implName);
            }
        } else { // build
            bytecode = buildImplements(implName);
        }
        // not avaliable
        if (bytecode == null || bytecode.length == 0) {
            plugin.getLogger().severe(() -> "ImplLoader | Implement for " + implName + " not available.");
            if (fallback == null) throw new NullPointerException("Implement for "+implName+" not available");
            // fallback
            plugin.getLogger().warning(() -> "ImplLoader | Creating " + implName + "fallback instance...");
            return fallback.newInstance();
        }
        // create new instance
        Class<?> cls = classLoader.loadClass(bytecode, pkg+"."+implName);
        implCache.put(implName, cls);
        return (T) cls.newInstance();
    }

    private byte[] buildImplements(String implName) {
        plugin.getLogger().info(() -> "ImplLoader | Building implementation for " + implName + "...");
        // scan package
        final LinkedList<ClassPath.ClassInfo> classes = new LinkedList<>(classScanner
                .getTopLevelClasses(pkg + "." + implName));
        classes.sort(Comparator.comparing(ClassPath.ClassInfo::getName));
        plugin.getLogger().info(() -> "ImplLoader | Scan finished. " + classes.size() + " class(es) available...");
        byte[] implement = null;
        for (final ClassPath.ClassInfo info : classes) {
            final Class<?> cls = info.load();
            // read annotation
            final SupportedVersion support = cls.getAnnotation(SupportedVersion.class);
            if (support == null) {
                continue;
            }
            // check version
            if (NMSTranslatorMain.getCurrentVersion().isAtLeast(support.lowestVersion())) {
                if (!NMSTranslatorMain.getCurrentVersion().isHigherThan(support.highestVersion())) {
                    // matched -> remap nms
                    byte[] bytecode;
                    try {
                        bytecode = Transformer.transformImpl(plugin, pkg.replace('.', '/'), cls, implName);
                    } catch (final IOException e1) {
                        continue;
                    }
                    plugin.getLogger().info(() -> "ImplLoader | Using " + support.implVersion() + " as "+implName+" implementation...");
                    implement = bytecode;
                    break;
                }
            }
        }
        if (saveImpls && implement != null) {
            try {
                plugin.getLogger().info(() -> "ImplLoader | Saving "+implName+" implementation to disk...");
                Files.write(getImplFile(implName).toPath(), implement);
            } catch (IOException e) {
                e.printStackTrace();
                plugin.getLogger().severe(() -> "ImplLoader | Error occurred while saving " + implName + ".class to disk...");
            }
        }
        return implement;
    }
}
