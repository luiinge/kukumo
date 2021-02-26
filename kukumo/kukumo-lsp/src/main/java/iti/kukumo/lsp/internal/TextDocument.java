package iti.kukumo.lsp.internal;

/**
 * Store a text document allowing the manipulation of the text
 * using text ranges.
 */
public class TextDocument {

    private static final char EOL = '\n';

    // the raw text document, including eol characters
    private String rawDocument;
    // the positions of each eol character in the overall raw document
    private int[] endOfLines;


    public TextDocument(String rawDocument) {
        this.rawDocument = rawDocument;
        this.endOfLines = locateEndOfLines(rawDocument);
    }

    public String extractLine(int lineNumber) {
        int start = start(lineNumber);
        int end = start(lineNumber+1);
        if (lineNumber < endOfLines.length) {
            end--;
        }
        return rawDocument.substring(start,end);
    }

    public String extractRange(TextRange range) {
        int start = start(range.startLine()) + range.startLinePosition();
        int end = start(range.endLine()) + range.endLinePosition();
        return rawDocument.substring(start,end);
    }


    public TextDocument replaceRange(TextRange range, String text) {
        int start = start(range.startLine()) + range.startLinePosition();
        int end = start(range.endLine()) + range.endLinePosition();
        this.rawDocument = rawDocument.substring(0,start) + text + rawDocument.substring(end);
        this.endOfLines = locateEndOfLines(rawDocument);
        return this;
    }


    public String rawText() {
        return rawDocument;
    }


    public boolean isEmpty() {
        return numberOfLines() == 0;
    }


    public int numberOfLines() {
        if (endOfLines.length == 0) {
            return 0;
        }
        // the text may or not end with a eol char
        return endOfLines[endOfLines.length-1] < rawDocument.length()-1 ?
            endOfLines.length + 1 :
            endOfLines.length;
    }

   
    
    public String[] extractLines() {
        String[] lines = new String[numberOfLines()];
        for (int i=0; i<lines.length; i++) {
            lines[i] = extractLine(i);
        }
        return lines;
    }

    
    private int start(int line) {
        if (line > endOfLines.length) {
            return rawDocument.length();
        }
        return line == 0 ? 0 : endOfLines[line-1]+1;
    }




    private static int[] locateEndOfLines(String rawDocument) {
        int index = 0;
        int start = 0;
        int occurrences = -1;
        while (index != -1) {
            occurrences ++;
            index = rawDocument.indexOf(EOL,start);
            start = index + 1;
        }
        int[] indexes = new int[occurrences];
        index = -1;
        for (int i=0;i<occurrences;i++) {
            index = rawDocument.indexOf(EOL,index+1);
            indexes[i] = index;
        }
        return indexes;
    }




}