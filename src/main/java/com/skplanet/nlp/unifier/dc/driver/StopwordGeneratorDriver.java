package com.skplanet.nlp.unifier.dc.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.unifier.dc.util.StopwordGenerator;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Stopword Generator Driver Program
 *
 * refer to {@link com.skplanet.nlp.unifier.dc.util.StopwordGenerator}
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 4/21/15
 */
public class StopwordGeneratorDriver {
    private static final Logger LOGGER = Logger.getLogger(StopwordGeneratorDriver.class.getName());

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("s", "service", true, "service name", true);
        cli.addOption("c", "category", true, "category name", true);
        cli.addOption("u", "source", true, "data source", true);
        cli.addOption("v", "version", true, "version", true);
        cli.addOption("o", "output file path", true, "output file if you want to write the result to a file", false);
        cli.parseOptions(args);

        String service = cli.getOption("s");
        String category = cli.getOption("c");
        String source = cli.getOption("u");
        String version = cli.getOption("v");
        StopwordGenerator stopwordGenerator = new StopwordGenerator(
                service,
                category,
                source,
                version
        );
        long btime, etime;

        LOGGER.info("stopword generating ....");
        btime = System.currentTimeMillis();
        Set<String> stopwordCandidates = stopwordGenerator.generate();
        etime = System.currentTimeMillis();
        LOGGER.info("stopword generating done in " + (etime - btime) + " msec.");

        LOGGER.info("writing stopwords ....");
        btime = System.currentTimeMillis();
        BufferedWriter writer;
        if (cli.hasOption("o")) {
            try {
                writer = new BufferedWriter(new FileWriter(new File(cli.getOption("o"))));
                for (String stopword : stopwordCandidates) {
                    writer.write(stopword);
                    writer.newLine();
                }
                writer.close();
            } catch (IOException e) {
                LOGGER.error("Failed to write the result to a file : " + cli.getOption("o"), e);
            }
        } else {
            for (String stopword : stopwordCandidates) {
                System.out.println(stopword);
            }
        }
        etime = System.currentTimeMillis();
        LOGGER.info("writing stopwords done in " + (etime - btime) + " msec.");
    }
}
