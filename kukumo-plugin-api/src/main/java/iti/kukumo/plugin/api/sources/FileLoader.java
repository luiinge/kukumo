package iti.kukumo.plugin.api.sources;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;
import iti.kukumo.plugin.api.*;

public class FileLoader {

    private static final String CLASSPATH_PROTOCOL = "classpath";
    private static final String FILE_PROTOCOL = "file";


    public <T> Stream<FileSource<T>> discoverResourceFiles(SourceCriteria<T> criteria) {
        if (criteria.hasFile()) {
            return discoverResourceFilesWithinFile(criteria);
        } else {
            return discoverResourceFilesInPath(criteria);
        }
    }


    private <T> Stream<FileSource<T>> discoverResourceFilesInPath(SourceCriteria<T> criteria) {
        if (criteria.startingPath().endsWith("/") || criteria.startingPath().endsWith("\\")) {
            return discoverResourceFiles(criteria.withStartingPath(dropLast(criteria.startingPath())));
        } else if (criteria.startingPath().startsWith(CLASSPATH_PROTOCOL+":")) {
            return discoverResourceFilesInClasspath(criteria);
        } else {
            return discoverResourceFilesFromURL(criteria, url(criteria));
        }
    }


    private <T> Stream<FileSource<T>> discoverResourceFilesInClasspath(SourceCriteria<T> criteria) {
        var classPath = criteria.startingPath().replace(CLASSPATH_PROTOCOL+":", "");
        var absoluteClassPath = classLoaderFolder(
            Thread.currentThread().getContextClassLoader(),
            FileLoader.class.getClassLoader()
        ) + classPath;
        return discoverResourceFilesWithinFile(criteria.withFile(new File(absoluteClassPath)));
    }



    private <T> Stream<FileSource<T>> discoverResourceFilesFromURL(
        SourceCriteria<T> criteria,
        URL url
    ) {
        if (url.getProtocol().equals(FILE_PROTOCOL)) {
            return discoverResourceFilesWithinFile(criteria.withFile(fileFromURL(url)));
        } else {
            return Stream.empty();
        }
    }


    private <T> Stream<FileSource<T>> discoverResourceFilesWithinFile(SourceCriteria<T> criteria) {
        var fileCriteria = criteria.fileCriteria();
        var file = fileCriteria.file();
        if (file.isDirectory()) {
            return Stream.of(or(file.listFiles(),new File[0]))
                .map(criteria::withFile)
                .flatMap(this::discoverResourceFilesWithinFile);
        } else if (criteria.filenameFilter().test(file.getName())) {
            return Stream.of(
                new FileSource<>(
                    criteria.contentType(),
                    Path.of(fileCriteria.absolutePath()),
                    Path.of(fileCriteria.relativePath()),
                    ()->newInputStream(file)
                )
            );
        } else {
            return Stream.empty();
        }
    }




    private String classLoaderFolder(ClassLoader... classLoaders) {
        var classLoader = Stream.of(classLoaders)
            .filter(it -> it.getResource(".") != null)
            .findFirst()
            .orElseThrow(()->new KukumoPluginException("Resources cannot be loaded from class loaders"));
        try {
            return Objects.requireNonNull(classLoader.getResource(".")).toURI().getPath();
        } catch (URISyntaxException | RuntimeException e) {
            throw new KukumoPluginException(e,"Error resolving classpath folder");
        }
    }


    private File fileFromURL(URL url) {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new KukumoPluginException(e, "Error creating file from url "+url);
        }
    }


    private URL url(SourceCriteria<?> criteria) {
        try {
            var path = Path.of(criteria.startingPath());
            var uri = path.isAbsolute() ?
                path.toUri() :
                new File(new File(System.getProperty("user.dir")), criteria.startingPath()).toURI();
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new KukumoPluginException(e, "Error getting the URL from the criteria");
        }
    }


    private InputStream newInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new KukumoPluginException(e);
        }
    }


    private static String dropLast(String string) {
        return string.substring(0,string.length()-1);
    }

    private static <T> T or(T value, T fallback) {
        return value == null ? fallback : value;
    }
    
}