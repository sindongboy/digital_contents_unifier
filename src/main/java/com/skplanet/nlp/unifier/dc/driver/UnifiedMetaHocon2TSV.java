package com.skplanet.nlp.unifier.dc.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.skplanet.nlp.unifier.dc.data.meta.Meta;
import com.skplanet.nlp.unifier.dc.data.meta.MetaLoader;
import com.skplanet.nlp.unifier.dc.data.meta.UnifiedMeta;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Hocon Type Unified Meta to TSV formatted Unified Meta
 *
 * it only requires legacy system --> other module will use hocon/json type meta someday
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 5/13/15
 */
public class UnifiedMetaHocon2TSV {
    private static final Logger LOGGER = Logger.getLogger(UnifiedMetaHocon2TSV.class.getName());

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("s", "service", true, "service name", true);
        cli.addOption("c", "category", true, "category name", true);
        cli.addOption("v", "version", true, "version", true);
        cli.addOption("a", "all", false, "no service filter", false);
        cli.addOption("m", "m2kadd", true, "m2k_add", false);
        cli.parseOptions(args);

        Configuration configuration = Configuration.getInstance();

        // getting source meta.
        // 메타에 정의된 컨텐츠만 tsv 로 떨구기 위해서
        LOGGER.info("meta item loading: " + cli.getOption("s") + ":" + cli.getOption("c") + ":" + cli.getOption("v"));
        URL metaUrl = configuration.getResource(cli.getOption("s") + "-" + cli.getOption("c") + ".meta-" + cli.getOption("v"));
        File metaFile = new File(metaUrl.getFile());
        Set<String> metaItem = new HashSet<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(metaFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                String[] fields = line.trim().split("\\t");
                metaItem.add(Prop.getDataSourceCodeFromDataSourceName(cli.getOption("s")) + "_" + fields[0].trim());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            LOGGER.error("can't find meta file: " + metaFile.getName(), e);
        } catch (IOException e) {
            LOGGER.error("can't read meta file: " + metaFile.getName(), e);
        }
        LOGGER.info("meta item loaded: " + metaItem.size());


        MetaLoader unifiedMetaLoader = new MetaLoader(
                null,
                cli.getOption("c"),
                null,
                cli.getOption("v")
        );

        LOGGER.info("m2k add file loading");
        Map<String, String> m2kAddMap = new HashMap<String, String>();
        if (cli.hasOption("m")) {
            File m2kAddFile = new File(cli.getOption("m"));
            String line;
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(m2kAddFile));
                while ((line = reader.readLine()) != null) {
                    if (line.trim().length() == 0) {
                        continue;
                    }

                    String[] fields = line.trim().split("\\t");
                    if (fields.length != 2) {
                        continue;
                    }

                    m2kAddMap.put(fields[0], fields[1]);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                LOGGER.error("m2k Add file doesn't exist: " + m2kAddFile.getName(), e);
            } catch (IOException e) {
                LOGGER.error("can't read m2k Add file: " + m2kAddFile.getName(), e);
            }
        }
        LOGGER.info("m2k add file loaded: " + m2kAddMap.size());

        Collection<Meta> unifiedMetaCollection = unifiedMetaLoader.loadUnifiedMeta();
        LOGGER.info("loaded unified meta size: " + unifiedMetaCollection.size());

        for (Meta meta : unifiedMetaCollection) {
            List<String> sourceIds = ((UnifiedMeta) meta).getSourceIdList();
            boolean isExist = false;
            for (String sid : sourceIds) {
                if (metaItem.contains(sid)) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                continue;
            }
            String result;
            if (cli.hasOption("a")) {
                result = ((UnifiedMeta) meta).toTSV();
            } else {
                if (m2kAddMap.size() > 0) {
                    String addKeywords = m2kAddMap.get(((UnifiedMeta) meta).getId());
                    result = ((UnifiedMeta) meta).toTSV(cli.getOption("s"), addKeywords);
                } else {
                    result = ((UnifiedMeta) meta).toTSV(cli.getOption("s"));
                }
            }
            if (!result.split("\\t")[1].equals("null")) {
                System.out.print(result);
            }
        }
    }
}
