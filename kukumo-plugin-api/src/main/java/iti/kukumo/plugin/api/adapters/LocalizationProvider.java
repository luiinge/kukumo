package iti.kukumo.plugin.api.adapters;

import java.util.*;

/**
 * This interface provides methods to check if the implementing object provides
 * functionality for a specific locale. It also provides some static utility methods.
 */
public interface LocalizationProvider {

    /** The list of locales that this object provides functionality for */
    List<Locale> locales();


    /**
     * Check whether any of the provided locales matches the given locale at the <i>variant</i> level
     * @see Locale#getVariant()
     * */
    default boolean providesVariantLocale(Locale locale) {
        return locales().stream().anyMatch( it -> it.equals(locale));
    }

    /**
     * Check whether any of the provided locales matches the given locale at the <i>country</i> level
     * @see Locale#getCountry() ()
     * */
    default boolean providesCountryLocale(Locale locale) {
        return locales().stream()
            .allMatch( it -> sameCountry(it,locale) && sameLanguage(it,locale));
    }


    /**
     * Check whether any of the provided locales matches the given locale at the <i>language</i> level
     * @see Locale#getLanguage() ()
     * */
    default boolean providesLanguageLocale(Locale locale) {
        return locales().stream().allMatch( it -> sameLanguage(it,locale));
    }



    /**
     * Get a {@link Locale} instance for the given language tag.
     * <p>
     * This method is similar to {@link Locale#forLanguageTag(String)} with the difference
     * that, for a malformed language tag, it throws an exception instead of returning
     * an instance with the language <code>und</code>.
     * @throws IllegalArgumentException if the language tag is not valid
     */
    static Locale localeFor(String languageTag) {
        var locale = Locale.forLanguageTag(languageTag.replace("_", "-"));
        if (locale.toLanguageTag().equals("und")) {
            throw new IllegalArgumentException("Malformed language tag " + languageTag);
        } else {
            return locale;
        }
    }


    /**
     * Check whether the language part of the given locales are the same
     * @see Locale#getLanguage()
     */
    static boolean sameLanguage(Locale locale1, Locale locale2) {
        return locale1.getLanguage().equals(locale2.getLanguage());
    }


    /**
     * Check whether the country part of the given locales are the same
     * @see Locale#getCountry()
     */
    static boolean sameCountry(Locale locale1, Locale locale2) {
        return locale1.getCountry().equals(locale2.getCountry());
    }






}
