package iti.kukumo.plugin.api.sources;

import java.io.File;
import java.util.function.Predicate;
import iti.kukumo.plugin.api.contributions.ContentType;
import lombok.*;

@Getter
public class SourceCriteria<T> {

    private final ContentType<T> contentType;
    private final String startingPath;
    private final Predicate<String> filenameFilter;
    private final FileCriteria fileCriteria;

    private SourceCriteria(
        ContentType<T> contentType,
        String startingPath,
        Predicate<String> filenameFilter,
        FileCriteria fileCriteria
    ) {
        this.contentType = contentType;
        this.startingPath = startingPath;
        this.filenameFilter = filenameFilter;
        this.fileCriteria = fileCriteria;
    }


    public SourceCriteria(
        ContentType<T> contentType,
        String startingPath,
        Predicate<String> filenameFilter
    ) {
        this(contentType,startingPath,filenameFilter,null);
    }


    public SourceCriteria<T> withFile(File file) {
        return new SourceCriteria<>(
            contentType,
            startingPath,
            filenameFilter,
            new FileCriteria(file,startingPath.replaceAll("[^:]*:",""))
        );
    }


    public SourceCriteria<T> withStartingPath(String otherStartingPath) {
        return new SourceCriteria<>(contentType,otherStartingPath,filenameFilter,fileCriteria);
    }


    public boolean hasFile() {
        return fileCriteria != null;
    }


    @Getter
    static class FileCriteria {

        private final String absolutePath;
        private final String relativePath;
        private final File file;

        FileCriteria(File file, String startingPath) {
            this.file = file;
            this.absolutePath = file.getAbsolutePath();
            if (absolutePath.endsWith(startingPath)) {
                this.relativePath = startingPath;
            } else if (absolutePath.contains(startingPath)) {
                this.relativePath = absolutePath
                    .substring(absolutePath.indexOf(startingPath))
                    .substring(startingPath.length()+1);
            } else {
                this.relativePath = absolutePath
                    .substring(0,absolutePath.length()-1)
                    .substring(startingPath.length());
            }
        }
    }







}

