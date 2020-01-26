package sakura.kooi.NMSTranslator.transformer;

import com.google.common.io.ByteStreams;
import org.bukkit.plugin.java.JavaPlugin;
import sakura.kooi.NMSTranslator.lib.asm.*;
import sakura.kooi.NMSTranslator.lib.asm.commons.ClassRemapper;

import java.io.IOException;
import java.io.InputStream;

public class Transformer {
	public static <T> byte[] transformImpl(JavaPlugin plugin, String pkg, final Class<T> cls, final String implName) throws IOException {
		final byte[] bytecode = getBytecode(plugin, cls);
		return transformImpl(pkg, bytecode, implName);
	}

	private static <T> byte[] transformImpl(String pkg, final byte[] bytecode, final String implName) {
		final ClassReader cr1 = new ClassReader(bytecode);
		final ClassWriter cw1 = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final NMSRemapper nmsRemapper = new NMSRemapper();
		final ClassRemapper classRemapperNMS = new ClassRemapper(cw1, nmsRemapper);
		cr1.accept(classRemapperNMS, 0);
		final ClassReader cr2 = new ClassReader(cw1.toByteArray());
		final ClassWriter cw2 = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final ImplClassRemapper implRemapper = new ImplClassRemapper(pkg+"/", pkg+"/"+implName);
		final ClassRemapper classRemapperImpl = new ClassRemapper(cw2, implRemapper);
		cr2.accept(classRemapperImpl, 0);
		return cw2.toByteArray();
	}

	private static byte[] getBytecode(JavaPlugin plugin, final Class<?> cls) throws IOException {
		final String path = cls.getName().replace('.', '/') + ".class";
		final InputStream input = plugin.getResource(path);
		if (input == null) throw new IOException("Resource '"+path+"' not found");
		return ByteStreams.toByteArray(input);
	}
}
