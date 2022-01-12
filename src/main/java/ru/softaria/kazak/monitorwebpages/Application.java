package ru.softaria.kazak.monitorwebpages;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.util.Map;

public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);
    private static final String TOOL_NAME = "monitorwebpages";

    public static void main(String[] args) {
        Options options = initOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);
            Map<String, String> todayPages = new LocalHtmlMonitor(cmd.getOptionValue("today")).getPagesStateTable();
            Map<String, String> yesterdayPages = new LocalHtmlMonitor(cmd.getOptionValue("yesterday")).getPagesStateTable();
            String secretaryName = cmd.getOptionValue("secretary");
            String report = WebPagesComparator.generateReport(yesterdayPages, todayPages, secretaryName);
            if (cmd.hasOption("output")) {
                try (FileWriter outputFile = new FileWriter(cmd.getOptionValue("output"))) {
                    outputFile.write(report);
                }
            } else {
                System.out.println(report);
            }
        } catch (ParseException e) {
            logger.error(e.getMessage());
            formatter.printHelp(TOOL_NAME, options);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private static Options initOptions() {
        Options options = new Options();

        Option yesterdayPages = new Option("y", "yesterday", true, "input file with yesterday's pages");
        yesterdayPages.setRequired(true);
        options.addOption(yesterdayPages);

        Option todayPages = new Option("t", "today", true, "input file with today's pages");
        todayPages.setRequired(true);
        options.addOption(todayPages);

        Option outputFile = new Option("o", "output", true, "output file name");
        options.addOption(outputFile);

        Option secretaryName = new Option("s", "secretary", true, "name of the secretary");
        secretaryName.setRequired(true);
        options.addOption(secretaryName);

        return options;
    }

}
