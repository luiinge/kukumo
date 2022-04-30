package iti.kukumo.plugin.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;

import org.junit.jupiter.api.*;

import iti.kukumo.plugin.api.KukumoPluginException;
import iti.kukumo.plugin.api.sources.*;

class TestSourceFileLoader {

    FileLoader fileLoader = new FileLoader();
    TextContentType textContentType = new TextContentType();



    @Test
    @DisplayName("txt files can be discovered from the classpath")
    void discoverTextFilesFromClasspath() {
        var founded = fileLoader.discoverResourceFiles(new SourceCriteria<>(
            textContentType,
            "classpath:discovery",
            it -> it.endsWith(".txt"))
        ).sorted(Comparator.comparing(FileSource::absolutePath))
        .toList();
        Path workingPath = Path.of(classLoaderFolder(Thread.currentThread().getContextClassLoader()));


        assertEquals(3, founded.size());
        assertEquals(textContentType,founded.get(0).contentType());
        assertEquals(workingPath.resolve("discovery/file1.txt"),founded.get(0).absolutePath());
        assertEquals(Path.of("file1.txt"),founded.get(0).relativePath());

        assertEquals(textContentType,founded.get(1).contentType());
        assertEquals(workingPath.resolve("discovery/file2.txt"),founded.get(1).absolutePath());
        assertEquals(Path.of("file2.txt"),founded.get(1).relativePath());

        assertEquals(textContentType,founded.get(2).contentType());
        assertEquals(workingPath.resolve("discovery/subfolder/file4.txt"),founded.get(2).absolutePath());
        assertEquals(Path.of("subfolder/file4.txt"),founded.get(2).relativePath());


    }


    @Test
    @DisplayName("specific file can be discovered from a relative path")
    void discoverSpecificFileFromRelativePath() {
        var founded = fileLoader.discoverResourceFiles(new SourceCriteria<>(
                textContentType,
                "src/test/resources/discovery/file1.txt",
                it -> true)
            ).sorted(Comparator.comparing(FileSource::absolutePath))
            .toList();
        Path workingPath = Path.of(classLoaderFolder(Thread.currentThread().getContextClassLoader()))
            .getParent().getParent();
        System.out.println(workingPath);
        assertEquals(1, founded.size());
        assertEquals(textContentType,founded.get(0).contentType());
        assertEquals(workingPath.resolve("src/test/resources/discovery/file1.txt"),founded.get(0).absolutePath());
        assertEquals(Path.of("src/test/resources/discovery/file1.txt"),founded.get(0).relativePath());
    }


    private String classLoaderFolder(ClassLoader classLoader) {
        try {
            return Objects.requireNonNull(classLoader.getResource(".")).toURI().getPath();
        } catch (URISyntaxException | RuntimeException e) {
            throw new KukumoPluginException(e,"Error resolving classpath folder");
        }
    }

}
