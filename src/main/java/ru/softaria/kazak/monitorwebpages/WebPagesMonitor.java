package ru.softaria.kazak.monitorwebpages;

import java.util.Map;

/**
 * WebPagesMonitor is the interface for
 * generating table of web pages state
 */
public interface WebPagesMonitor {
    Map<String, String> getPagesStateTable() throws Exception;
}
