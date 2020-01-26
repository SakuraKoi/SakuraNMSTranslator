package sakura.kooi.NMSTranslator;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class NMSTranslatorMain extends JavaPlugin {
    @Getter
    private static String nmsVersion;
    @Getter
    private static MinecraftVersion currentVersion;

    @Override
    public void onEnable() {
        nmsVersion = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        currentVersion = MinecraftVersion.getCurrentVersion();
    }

    @Override
    public void onDisable() {

    }
}
