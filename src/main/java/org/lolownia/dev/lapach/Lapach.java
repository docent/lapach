package org.lolownia.dev.lapach;

import org.apache.commons.cli.*;

public class Lapach {
    public static void main(String[] args) {
        parseCmdLine(args);
    }

    private static void parseCmdLine(String[] args) {
        CommandLineParser parser = new PosixParser();
        Options options = createOptions();
        CommandLine cmdLine;
        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException e) {
            showUsage(options);
            throw new RuntimeException(e);
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("u", "username", true, "User for feed retrieval");
        return options;
    }

    private static void showUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("lapach", options);
    }
}

