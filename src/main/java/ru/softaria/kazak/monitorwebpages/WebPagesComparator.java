package ru.softaria.kazak.monitorwebpages;

import org.apache.logging.log4j.core.lookup.StrSubstitutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.joining;

/**
 * WebPagesComparator is the utility class
 * for generating report about web pages
 */
public final class WebPagesComparator {
    private static String NOTHING = "без изменений";

    private WebPagesComparator() { throw new IllegalStateException("Calling constructor of utility class"); }

    public static String generateReport(Map<String, String> yesterdayTable, Map<String, String> todayTable,
                                        String secretaryName) throws IOException {
        Set<String> missedPages = new HashSet<>(yesterdayTable.keySet());
        missedPages.removeAll(todayTable.keySet());

        Set<String> newPages = new HashSet<>(todayTable.keySet());
        newPages.removeAll(yesterdayTable.keySet());

        Set<String> changedPages = getChangedPages(yesterdayTable, todayTable);

        return generateReportMessage(missedPages, newPages, changedPages, secretaryName);
    }

    private static Set<String> getChangedPages(Map<String, String> yesterdayTable, Map<String, String> todayTable) {
        Set<String> intersection = new HashSet<>(yesterdayTable.keySet());
        intersection.retainAll(todayTable.keySet());
        Set<String> changedPages = new HashSet<>();
        for (String pageUrl : intersection) {
            if (!yesterdayTable.get(pageUrl).equals(todayTable.get(pageUrl))) {
                changedPages.add(pageUrl);
            }
        }
        return changedPages;
    }

    private static String generateReportMessage(Set<String> missedPages, Set<String> newPages, Set<String> changedPages,
                                                String secretaryName) throws IOException {
        ClassLoader classLoader = WebPagesComparator.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("messagePattern.txt")).getFile());
        String messagePattern = java.nio.file.Files.readString(Paths.get(file.toURI()));
        Map<String, String> patternValues = Map.of("secretary", secretaryName,
                                                "missedPages", setToString(missedPages),
                                                "newPages", setToString(newPages),
                                                "changedPages", setToString(changedPages));
        StrSubstitutor sub = new StrSubstitutor(patternValues, "%(", ")");
        return sub.replace(messagePattern);
    }

    // https://stackoverflow.com/questions/50427207/convert-set-of-integers-to-string-in-java
    private static String setToString(Set<String> stringSet) {
        if (stringSet.isEmpty()) {
            return NOTHING;
        }
        return stringSet.stream().map(String::valueOf).collect(joining(", "));
    }
}
