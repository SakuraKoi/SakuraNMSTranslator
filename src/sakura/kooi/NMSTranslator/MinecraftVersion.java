package sakura.kooi.NMSTranslator;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

// From ProtocolLib
public class MinecraftVersion implements Comparable<MinecraftVersion>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Pattern VERSION_PATTERN = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-\\.]+)\\s*\\)");
	public static final MinecraftVersion EXPLORATION_UPDATE = new MinecraftVersion("1.11");
	public static final MinecraftVersion FROSTBURN_UPDATE = new MinecraftVersion("1.10");
	public static final MinecraftVersion COMBAT_UPDATE = new MinecraftVersion("1.9");
	public static final MinecraftVersion BOUNTIFUL_UPDATE = new MinecraftVersion("1.8");
	public static final MinecraftVersion SKIN_UPDATE = new MinecraftVersion("1.7.8");
	public static final MinecraftVersion WORLD_UPDATE = new MinecraftVersion("1.7.2");
	public static final MinecraftVersion HORSE_UPDATE = new MinecraftVersion("1.6.1");
	public static final MinecraftVersion REDSTONE_UPDATE = new MinecraftVersion("1.5.0");
	public static final MinecraftVersion SCARY_UPDATE = new MinecraftVersion("1.4.2");

	private final int major;
	private final int minor;
	private final int build;
	private final String development;
	private static MinecraftVersion currentVersion;

	public MinecraftVersion(final Server server) {
		this(extractVersion(server.getVersion()));
	}

	private MinecraftVersion(final String versionOnly) {
		this(versionOnly, true);
	}

	private MinecraftVersion(final String versionOnly, final boolean parseSnapshot) {
		final String[] section = versionOnly.split("-");
		int[] numbers;
		numbers = parseVersion(section[0]);
		major = numbers[0];
		minor = numbers[1];
		build = numbers[2];
		development = section.length > 1 ? section[1] : null;
	}

	public MinecraftVersion(final int major, final int minor, final int build) {
		this(major, minor, build, null);
	}

	private MinecraftVersion(final int major, final int minor, final int build, final String development) {
		this.major = major;
		this.minor = minor;
		this.build = build;
		this.development = development;
	}

	private int[] parseVersion(final String version) {
		final String[] elements = version.split("\\.");
		final int[] numbers = new int[3];
		if (elements.length < 1)
			throw new IllegalStateException("Corrupt MC version: " + version);
		for (int i = 0; i < Math.min(numbers.length, elements.length); ++i) {
			numbers[i] = Integer.parseInt(elements[i].trim());
		}
		return numbers;
	}

	private int getMajor() {
		return major;
	}

	private int getMinor() {
		return minor;
	}

	private int getBuild() {
		return build;
	}

	private String getDevelopmentStage() {
		return development;
	}

	private String getVersion() {
		if (getDevelopmentStage() == null)
			return String.format("%s.%s.%s", getMajor(), getMinor(), getBuild());
		return String.format("%s.%s.%s-%s", getMajor(), getMinor(), getBuild(), getDevelopmentStage());
	}

	@Override
	public int compareTo(final MinecraftVersion o) {
		if (o == null)
			return 1;
		return ComparisonChain.start().compare(getMajor(), o.getMajor()).compare(getMinor(), o.getMinor())
				.compare(getBuild(), o.getBuild())
				.compare(getDevelopmentStage(), o.getDevelopmentStage(), Ordering.natural().nullsLast()).result();
	}

	public boolean isAtLeast(final MinecraftVersion other) {
		return other != null && compareTo(other) >= 0;
	}

	public boolean isAtLeast(final String other) {
		return other != null && compareTo(new MinecraftVersion(other)) >= 0;
	}

	public boolean isHigherThan(final MinecraftVersion other) {
		return other != null && compareTo(other) < 0;
	}

	public boolean isHigherThan(final String other) {
		return other != null && compareTo(new MinecraftVersion(other)) > 0;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj instanceof MinecraftVersion) {
			final MinecraftVersion other = (MinecraftVersion) obj;
			return getMajor() == other.getMajor() && getMinor() == other.getMinor()
					&& getBuild() == other.getBuild()
					&& Objects.equal(getDevelopmentStage(), other.getDevelopmentStage());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getMajor(), getMinor(), getBuild());
	}

	@Override
	public String toString() {
		return String.format("(MC: %s)", getVersion());
	}

	private static String extractVersion(final String text) {
		final Matcher version = MinecraftVersion.VERSION_PATTERN.matcher(text);
		if (version.matches() && version.group(1) != null)
			return version.group(1);
		throw new IllegalStateException("Cannot parse version String '" + text + "'");
	}

	private static MinecraftVersion fromServerVersion(final String serverVersion) {
		return new MinecraftVersion(extractVersion(serverVersion));
	}

	public static void setCurrentVersion(final MinecraftVersion version) {
		MinecraftVersion.currentVersion = version;
	}

	public static MinecraftVersion getCurrentVersion() {
		if (MinecraftVersion.currentVersion == null) {
			MinecraftVersion.currentVersion = fromServerVersion(Bukkit.getVersion());
		}
		return MinecraftVersion.currentVersion;
	}
}
