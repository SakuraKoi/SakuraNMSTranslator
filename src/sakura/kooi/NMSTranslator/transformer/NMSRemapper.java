package sakura.kooi.NMSTranslator.transformer;

import sakura.kooi.NMSTranslator.NMSTranslatorMain;
import sakura.kooi.NMSTranslator.lib.asm.commons.Remapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NMSRemapper extends Remapper {
	private final Pattern craftBukkitRegex;
	private final Pattern nmsRegex;
	private final String nmsVersion;

	public NMSRemapper() {
		craftBukkitRegex = Pattern.compile("org/bukkit/craftbukkit/.*?/");
		nmsRegex = Pattern.compile("net/minecraft/server/.*?/");
		nmsVersion = NMSTranslatorMain.getNmsVersion();
	}

	@Override
	public String map(final String typeName) {
		Matcher matcher = nmsRegex.matcher(typeName);
		if (matcher.find())
			return matcher.replaceFirst("net/minecraft/server/" + nmsVersion + "/");
		matcher = craftBukkitRegex.matcher(typeName);
		if (matcher.find())
			return matcher.replaceFirst("org/bukkit/craftbukkit/" + nmsVersion + "/");
		return super.map(typeName);
	}
}
