package iti.kukumo.plugin.api;

import imconfig.Config;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;
import iti.kukumo.plugin.api.adapters.*;
import iti.kukumo.plugin.api.annotations.LocalizableWith;
import org.jexten.ExtensionManager;

public class Localizer {

    private final ExtensionManager extensionManager;

    Localizer(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }


    /**
     * Retrieve a {@link Config} object (via a {@link ResourceLocalizationProvider} suitable for
     * the specified target class).
     * <p>
     * Target class must be annotated with {@link LocalizableWith}
     */
    public Optional<Config> localizedProperties(Class<?> target, Locale locale) {
        return resourceLocalizationProviderFor(target,locale)
            .map(it->it.openResource(locale))
            .map(this::properties)
            .map(Config.factory()::fromProperties);
    }



    /**
     * Retrieve a new {@link InputStream} object (via a {@link ResourceLocalizationProvider}
     * suitable for the specified target class).
     * <p>
     * Target object class must be annotated with {@link LocalizableWith}
     */
    public Optional<InputStream> localizedResource(Class<?> target, Locale locale) {
        return resourceLocalizationProviderFor(target,locale)
            .map(it->it.openResource(locale));
    }



    /**
     * Retrieve an instance of a specific subtype of {@link LocalizationProvider}
     */
    public <T extends LocalizationProvider> Optional<T> localization(
        Class<T> localization,
        Locale locale
    ) {
        return Stream.of(localization)
            .flatMap(extensionManager::getExtensions)
            .filter(it -> it.providesLanguageLocale(locale))
            .min(Comparator.comparingInt(it -> distanceToExactLocale(it, locale)))
            .map(localization::cast);
    }




    /**
     * Retrieve an instance of a {@link ResourceLocalizationProvider} suitable for
     * the specified target class.
     * <p>
     * Target class must be annotated with {@link LocalizableWith}
     */
    public Optional<ResourceLocalizationProvider> resourceLocalizationProviderFor(
        Class<?> target,
        Locale locale
    ) {
        return Stream.ofNullable(target.getAnnotation(LocalizableWith.class))
            .map(LocalizableWith::value)
            .filter(ResourceLocalizationProvider.class::isAssignableFrom)
            .flatMap(extensionManager::getExtensions)
            .filter(it -> it.providesLanguageLocale(locale))
            .min(Comparator.comparingInt(it -> distanceToExactLocale(it, locale)))
            .map(ResourceLocalizationProvider.class::cast);
    }


    private int distanceToExactLocale(LocalizationProvider localization, Locale locale) {
        if (localization.providesVariantLocale(locale)) return 0;
        if (localization.providesCountryLocale(locale)) return 1;
        if (localization.providesLanguageLocale(locale)) return 2;
        return Integer.MAX_VALUE;
    }


    private Properties properties(InputStream inputStream) {
        try(var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            var properties = new Properties();
            properties.load(reader);
            return properties;
        } catch (IOException e) {
            throw new KukumoPluginException(e,"Error reading localization properties");
        }
    }


}
