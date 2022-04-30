package iti.kukumo.plugin.api.adapters;

import java.io.InputStream;
import java.util.*;

import lombok.Getter;

@Getter
public class ResourceLocalizationAdapter implements ResourceLocalizationProvider {

    @FunctionalInterface
    public interface ResourceNameResolver {
        String resolveName(String baseName, Locale locale);
    }

    protected static final ResourceNameResolver DEFAULT_RESOURCE_NAME_RESOLVER = (baseName, locale) -> {
        var addition = locale.toLanguageTag();
        var extensionIndex = baseName.lastIndexOf('.');
        if (extensionIndex == -1) {
            return "%s_%s".formatted(baseName,addition);
        } else {
            var baseNameWithoutExtension = baseName.substring(0, extensionIndex);
            var extension = baseName.substring(extensionIndex);
            return "%s_%s%s".formatted(baseNameWithoutExtension,addition,extension);
        }
    };



    private final String resourceFile;
    private final List<Locale> locales;
    private final ResourceNameResolver resourceNameResolver;



    public ResourceLocalizationAdapter(
        String resourceFile,
        List<String> languageTags,
        ResourceNameResolver resourceNameResolver
    ) {
        this.resourceFile = resourceFile;
        this.locales = languageTags.stream().map(LocalizationProvider::localeFor).toList();
        this.resourceNameResolver = resourceNameResolver;
    }


    public ResourceLocalizationAdapter(
        String resourceFile,
        List<String> languageTags
    ) {
        this(resourceFile, languageTags, DEFAULT_RESOURCE_NAME_RESOLVER);
    }


    public InputStream openResource(Locale locale) {
        var localizedResourceFile = resourceNameResolver.resolveName(resourceFile,locale);
        InputStream inputStream = classLoader().getResourceAsStream(localizedResourceFile);
        if (inputStream == null) {
            throw new IllegalArgumentException(
                "Cannot find resource file %s (localization of %s for %s)"
                    .formatted(localizedResourceFile, resourceFile, locale)
            );
        }
        return inputStream;
    }



    private ClassLoader classLoader() {
        return this.getClass().getClassLoader();
    }

}
