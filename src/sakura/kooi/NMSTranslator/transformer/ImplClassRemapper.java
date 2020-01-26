package sakura.kooi.NMSTranslator.transformer;

import sakura.kooi.NMSTranslator.lib.asm.commons.Remapper;

class ImplClassRemapper extends Remapper {
	private final String className;
	private final String pkg;

	public ImplClassRemapper(String pkg, String after) {
		this.pkg = pkg;
		this.className = after;
	}

	@Override
	public String map(final String typeName) {
		if (typeName.startsWith(pkg))
			return className;
		return typeName;
	}
}
