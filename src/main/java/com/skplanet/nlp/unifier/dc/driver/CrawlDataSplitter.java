package com.skplanet.nlp.unifier.dc.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 5/15/15
 */
public class CrawlDataSplitter {
    private static final Logger LOGGER = Logger.getLogger(CrawlDataSplitter.class.getName());

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("c", "category", true, "category name", true);
        cli.addOption("v", "version", true, "version", true);
        cli.addOption("o", "output", true, "crawled data base dir", true);
        cli.parseOptions(args);

        String category = cli.getOption("c");
        String version = cli.getOption("v");
        String outputBaseDir = cli.getOption("o");
        Config configuration = ConfigFactory.load("split.conf");

        List<String> metaSourceList = configuration.getStringList(category + ".meta");
        List<String> episodeSourceList = configuration.getStringList(category + ".episode");
        List<String> commentSourceList = configuration.getStringList(category + ".comment");

        Configuration urlGetter = Configuration.getInstance();

        BufferedReader reader;
        BufferedWriter writer;
        File outputFile;
        String line;

        // meta sources
        long begin, end;
        int count = 0;
        LOGGER.info("Crawl Data Split Start (Meta)");
        begin = System.currentTimeMillis();
        Map<String, List<String>> id2svc = new HashMap<String, List<String>>();
        if (metaSourceList.size() > 0) {
            for (String metaSource : metaSourceList) {
                LOGGER.info("get raw meta file: " + category + "-" + metaSource + ".meta.raw-" + version);
                URL rawURL = urlGetter.getResource(category + "-" + metaSource + ".meta.raw-" + version);
                if (rawURL == null) {
                    LOGGER.error("Raw Meta File Doesn't exist: " + category + "-" + metaSource + ".meta.raw-" + version, new FileNotFoundException());
                }
                File rawMetaFile = new File(rawURL.getFile());
                try {
                    reader = new BufferedReader(new FileReader(rawMetaFile));
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().length() == 0) {
                            continue;
                        }

                        if (count % 100 == 0) {
                            LOGGER.debug("processing : " + count);
                        }
                        count++;

                        String[] fields = line.split("\\t");
                        List<String> serviceList = new ArrayList<String>();

                        // ---------------------------------------------------------- //
                        // 중요!!! KMDB 의 경우, 전체 DB를 덤프해오기 때문에, 모든 서비스 파을을 동일하게 생성한다.
                        // ---------------------------------------------------------- //
                        if (metaSource.equals(Prop.DATA_SOURCE_NAME_KMDB)) {
                            // for hoppin
                            outputFile = new File(outputBaseDir + "/"
                                    + Prop.SERVICE_NAME_HOPPIN + "/"
                                    + category + "/"
                                    + Prop.getServiceNameFromServiceCode(Prop.SERVICE_CODE_HOPPIN) + "-"
                                    + category + "-"
                                    + metaSource + ".meta-"
                                    + version
                            );
                            writer = new BufferedWriter(new FileWriter(outputFile, true));
                            writer.write(line);
                            writer.newLine();
                            writer.close();
                            if (!serviceList.contains(Prop.SERVICE_CODE_HOPPIN)) {
                                serviceList.add(Prop.SERVICE_CODE_HOPPIN);
                            }

                            // for tstore
                            outputFile = new File(outputBaseDir + "/"
                                    + Prop.SERVICE_NAME_TSTORE + "/"
                                    + category + "/"
                                    + Prop.getServiceNameFromServiceCode(Prop.SERVICE_CODE_TSTORE) + "-"
                                    + category + "-"
                                    + metaSource + ".meta-"
                                    + version
                            );
                            writer = new BufferedWriter(new FileWriter(outputFile, true));
                            writer.write(line);
                            writer.newLine();
                            writer.close();
                            if (!serviceList.contains(Prop.SERVICE_CODE_TSTORE)) {
                                serviceList.add(Prop.SERVICE_CODE_TSTORE);
                            }

                            // for skb
                            outputFile = new File(outputBaseDir + "/"
                                    + Prop.SERVICE_NAME_SKB + "/"
                                    + category + "/"
                                    + Prop.getServiceNameFromServiceCode(Prop.SERVICE_CODE_SKB) + "-"
                                    + category + "-"
                                    + metaSource + ".meta-"
                                    + version
                            );
                            writer = new BufferedWriter(new FileWriter(outputFile, true));
                            writer.write(line);
                            writer.newLine();
                            writer.close();
                            if (!serviceList.contains(Prop.SERVICE_CODE_SKB)) {
                                serviceList.add(Prop.SERVICE_CODE_SKB);
                            }
                        } else {
                            String[] services = fields[fields.length - 1].split("\\^");
                            for (String service : services) {
                                // for hoppin
                                if (service.startsWith(Prop.SERVICE_CODE_HOPPIN)) {
                                    outputFile = new File(outputBaseDir + "/"
                                            + Prop.SERVICE_NAME_HOPPIN + "/"
                                            + category + "/"
                                            + Prop.getServiceNameFromServiceCode(Prop.SERVICE_CODE_HOPPIN) + "-"
                                            + category + "-"
                                            + metaSource + ".meta-"
                                            + version
                                    );
                                    if (outputFile.exists()) {
                                        writer = new BufferedWriter(new FileWriter(outputFile, true));
                                    } else {
                                        writer = new BufferedWriter(new FileWriter(outputFile));
                                    }
                                    writer.write(line);
                                    writer.newLine();
                                    writer.close();
                                    if (!serviceList.contains(Prop.SERVICE_CODE_HOPPIN)) {
                                        serviceList.add(Prop.SERVICE_CODE_HOPPIN);
                                    }
                                    continue;
                                }

                                // for tstore
                                if (service.startsWith(Prop.SERVICE_CODE_TSTORE)) {
                                    outputFile = new File(outputBaseDir + "/"
                                            + Prop.SERVICE_NAME_TSTORE + "/"
                                            + category + "/"
                                            + Prop.getServiceNameFromServiceCode(Prop.SERVICE_CODE_TSTORE) + "-"
                                            + category + "-"
                                            + metaSource + ".meta-"
                                            + version
                                    );
                                    if (outputFile.exists()) {
                                        writer = new BufferedWriter(new FileWriter(outputFile, true));
                                    } else {
                                        writer = new BufferedWriter(new FileWriter(outputFile));
                                    }
                                    writer.write(line);
                                    writer.newLine();
                                    writer.close();
                                    if (!serviceList.contains(Prop.SERVICE_CODE_TSTORE)) {
                                        serviceList.add(Prop.SERVICE_CODE_TSTORE);
                                    }
                                    continue;
                                }

                                // for skb
                                if (service.startsWith(Prop.SERVICE_CODE_SKB)) {
                                    outputFile = new File(outputBaseDir + "/"
                                            + Prop.SERVICE_NAME_SKB + "/"
                                            + category + "/"
                                            + Prop.getServiceNameFromServiceCode(Prop.SERVICE_CODE_SKB) + "-"
                                            + category + "-"
                                            + metaSource + ".meta-"
                                            + version
                                    );
                                    if (outputFile.exists()) {
                                        writer = new BufferedWriter(new FileWriter(outputFile, true));
                                    } else {
                                        writer = new BufferedWriter(new FileWriter(outputFile));
                                    }
                                    writer.write(line);
                                    writer.newLine();
                                    writer.close();
                                    if (!serviceList.contains(Prop.SERVICE_CODE_SKB)) {
                                        serviceList.add(Prop.SERVICE_CODE_SKB);
                                    }
                                }
                            }
                        }
                        id2svc.put(metaSource + "_" + fields[0], serviceList);
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    LOGGER.error("raw meta file doesn't exist: " + rawMetaFile.getName(), e);
                } catch (IOException e) {
                    LOGGER.error("failed to read raw meta file: " + rawMetaFile.getName(), e);
                }
            }
        }
        end = System.currentTimeMillis();
        LOGGER.info("Crawl Data Split (Meta) Done in " + (end - begin) + " msec.");

        // episode sources
        LOGGER.info("Crawl Data Split Start (Episode)");
        begin = System.currentTimeMillis();
        if (episodeSourceList.size() > 0) {
            for (String episodeSource : episodeSourceList) {
                LOGGER.debug("get raw episode: " + category + "-" + episodeSource + ".episode.raw-" + version);
                File rawEpisodeFile = new File(urlGetter.getResource(category + "-" + episodeSource + ".episode.raw-" + version).getFile());
                try {
                    reader = new BufferedReader(new FileReader(rawEpisodeFile));
                    count = 0;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().length() == 0) {
                            continue;
                        }

                        if (count % 100 == 0) {
                            LOGGER.debug("processing : " + count);
                        }
                        count++;

                        String[] fields = line.split("\\t");

                        List<String> services = id2svc.get(episodeSource + "_" + fields[0]);

                        for (String service : services) {
                            // hoppin
                            if (service.startsWith(Prop.SERVICE_CODE_HOPPIN)) {
                                outputFile = new File( outputBaseDir + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "/"
                                        + category + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "-"
                                        + category + "-"
                                        + episodeSource + ".episode-"
                                        + version
                                );
                                writer = new BufferedWriter(new FileWriter(outputFile, true));
                                writer.write(line);
                                writer.newLine();
                                writer.close();
                                continue;
                            }
                            // tstore
                            if (service.startsWith(Prop.SERVICE_CODE_TSTORE)) {
                                outputFile = new File( outputBaseDir + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "/"
                                        + category + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "-"
                                        + category + "-"
                                        + episodeSource + ".episode-"
                                        + version
                                );
                                writer = new BufferedWriter(new FileWriter(outputFile, true));
                                writer.write(line);
                                writer.newLine();
                                writer.close();
                            }
                            // skb
                            if (service.startsWith(Prop.SERVICE_CODE_SKB)) {
                                outputFile = new File( outputBaseDir + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "/"
                                        + category + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "-"
                                        + category + "-"
                                        + episodeSource + ".episode-"
                                        + version
                                );
                                writer = new BufferedWriter(new FileWriter(outputFile, true));
                                writer.write(line);
                                writer.newLine();
                                writer.close();
                            }
                        }
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    LOGGER.error("raw episode file doesn't exist: " + rawEpisodeFile.getName(), e);
                } catch (IOException e) {
                    LOGGER.error("failed to read raw episode file: " + rawEpisodeFile.getName(), e);
                }
            }

        }
        end = System.currentTimeMillis();
        LOGGER.info("Crawl Data Split (Episode) Done in " + (end - begin) + " msec.");


        // comment sources
        LOGGER.info("Crawl Data Split Start (Comment)");
        begin = System.currentTimeMillis();
        if (commentSourceList.size() > 0) {
            for (String commentSource : commentSourceList) {
                LOGGER.debug("get raw comment: " + category + "-" + commentSource + ".comment.raw-" + version);
                File rawCommentFile = new File(urlGetter.getResource(category + "-" + commentSource + ".comment.raw-" + version).getFile());
                try {
                    reader = new BufferedReader(new FileReader(rawCommentFile));
                    count = 0;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().length() == 0) {
                            continue;
                        }

                        if (count % 100 == 0) {
                            LOGGER.debug("processing : " + count);
                        }
                        count++;

                        String[] fields = line.split("\\t");

                        List<String> services = id2svc.get(commentSource + "_" + fields[0]);

                        if (services == null) {
                            continue;
                        }

                        for (String service : services) {
                            // hoppin
                            if (service.startsWith(Prop.SERVICE_CODE_HOPPIN)) {
                                outputFile = new File(outputBaseDir + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "/"
                                        + category + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "-"
                                        + category + "-"
                                        + commentSource + ".comment-"
                                        + version
                                );
                                writer = new BufferedWriter(new FileWriter(outputFile, true));
                                writer.write(line);
                                writer.newLine();
                                writer.close();
                                continue;
                            }

                            // tstore
                            if (service.startsWith(Prop.SERVICE_CODE_TSTORE)) {
                                outputFile = new File(outputBaseDir + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "/"
                                        + category + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "-"
                                        + category + "-"
                                        + commentSource + ".comment-"
                                        + version
                                );
                                writer = new BufferedWriter(new FileWriter(outputFile, true));
                                writer.write(line);
                                writer.newLine();
                                writer.close();
                            }

                            // skb
                            if (service.startsWith(Prop.SERVICE_CODE_SKB)) {
                                outputFile = new File(outputBaseDir + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "/"
                                        + category + "/"
                                        + Prop.getServiceNameFromServiceCode(service) + "-"
                                        + category + "-"
                                        + commentSource + ".comment-"
                                        + version
                                );
                                writer = new BufferedWriter(new FileWriter(outputFile, true));
                                writer.write(line);
                                writer.newLine();
                                writer.close();
                            }
                        }
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    LOGGER.error("raw comment file doesn't exist: " + rawCommentFile.getName(), e);
                } catch (IOException e) {
                    LOGGER.error("failed to read raw comment file: " + rawCommentFile.getName(), e);
                }
            }
        }
        end = System.currentTimeMillis();
        LOGGER.info("Crawl Data Split (Comment) Done in " + (end - begin) + " msec.");

    }
}
