package ru.softaria.kazak.monitorwebpages.exceptions;

public class FormatFileException extends Exception {
    public FormatFileException(String filename) {
        super("Incorrect format of file " + filename);
    }
}
