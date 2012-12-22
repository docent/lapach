package org.lolownia.dev.lapach;

import org.apache.commons.cli.*;

import java.io.Console;
import java.util.Arrays;

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
        String feedUrl = cmdLine.getArgs()[0];

        boolean hasUsername = cmdLine.hasOption("u");
        boolean hasPassword = cmdLine.hasOption("p");

        String username = cmdLine.getOptionValue("u");
        String password = cmdLine.getOptionValue("p");
        if (hasUsername && !hasPassword) {
            password = readHttpPassword();
        }


        Downloader downloader = new Downloader(feedUrl, username, password);
        downloader.download();
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("u", "username", true, "User for feed retrieval");
        options.addOption("p", "password", true, "Password for feed retrieval");
        options.addOption("t", "threads", true, "Number of concurrent threads");
        return options;
    }

    private static void showUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("lapach", options);
    }

    private static String readHttpPassword() {
        Console cons;
        char[] passwordChars;
        String passwordStr;
        if ((cons = System.console()) != null &&
                (passwordChars = cons.readPassword("[%s]", "HTTP password:")) != null) {
            passwordStr = new String(passwordChars);
        } else {
            throw new RuntimeException("Could not read password from console");
        }
        Arrays.fill(passwordChars, ' ');
        return passwordStr;
    }
}

