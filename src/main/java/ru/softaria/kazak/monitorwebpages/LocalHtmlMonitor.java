package ru.softaria.kazak.monitorwebpages;

import ru.softaria.kazak.monitorwebpages.exceptions.FormatFileException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * LocalHtmlReader is the class which read HTML files from
 * local storage and create table of states of pages
 *
 * Format of input file:
 * <URL> <path to file>
 *
 * Example:
 * https://www.google.com/ pagescache/google_12_01_2021.html
 */
public class LocalHtmlMonitor implements WebPagesMonitor {
    private final String filename;

    public LocalHtmlMonitor(String filename) {
        this.filename = filename;
    }

    /**
     *
     *  Key of output table: Url of page
     *  Value of output table: md5 hash of the page content
     *
     * @return Table with the information of web pages
     * @throws FormatFileException Input file contents is incorrect. See the example above
     * @throws IOException Input file doesn't exist or there is a problem with some HTML file
     */
    @Override
    public Map<String, String> getPagesStateTable() throws FormatFileException, IOException {
        Map<String, String> outputTable = new HashMap<>();
        try (Scanner myReader = new Scanner(new File(filename))) {
            while (myReader.hasNextLine()) {
                String dataLine = myReader.nextLine();
                PairString htmlFileLocation = splitLine(dataLine);
                outputTable.put(htmlFileLocation.getLeft(), calcFileHash(htmlFileLocation.getRight()));
            }
        }
        return outputTable;
    }

    private String calcFileHash(String filename) throws IOException {
        try (InputStream is = Files.newInputStream(Paths.get(filename))) {
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        }
    }

    private PairString splitLine(String dataLine) throws FormatFileException {
        String[] pair = dataLine.split(" ", 2);
        if (pair.length != 2) {
            throw new FormatFileException(filename);
        }
        return new PairString(pair[0], pair[1]);
    }
}

class PairString {
    private final String left;
    private final String right;

    public PairString(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }
}
