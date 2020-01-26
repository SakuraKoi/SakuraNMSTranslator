package sakura.kooi.NMSTranslator.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("UnnecessaryInterfaceModifier")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedVersion {
	public String lowestVersion() default "1.0";

	public String highestVersion() default "99.9.9";

	public String implVersion() default "None";
}
