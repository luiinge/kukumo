package iti.kukumo.plugin.api;

import java.util.ServiceLoader;

public final class ApiLoader {

    private ApiLoader() { }

    public static <T> T load(Class<T> type) {
        return ServiceLoader.load(ApiLoader.class.getModule().getLayer(), type)
            .findFirst()
            .orElseThrow(
                ()->new KukumoPluginException("Cannot find an implementation for "+type)
            );
    }
}
