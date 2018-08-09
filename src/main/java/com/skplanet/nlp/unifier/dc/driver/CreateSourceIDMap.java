package com.skplanet.nlp.unifier.dc.driver;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.skplanet.nlp.unifier.dc.data.meta.Meta;
import com.skplanet.nlp.unifier.dc.data.meta.MetaLoader;
import com.skplanet.nlp.unifier.dc.data.meta.UnifiedMeta;
import com.skplanet.nlp.unifier.dc.util.HDFSUtil;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 5/13/15
 */
public class CreateSourceIDMap {
    private static final Logger LOGGER = Logger.getLogger(CreateSourceIDMap.class.getName());

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("c", "category", true, "category name", true);
        cli.addOption("v", "version", true, "version", true);
        cli.addOption("u", "source", true, "data source", true);
        cli.addOption("o", "output", true, "output file", true);
        cli.parseOptions(args);

        HDFSUtil hdfs = new HDFSUtil();

        String category = cli.getOption("c");
        String version = cli.getOption("v");
        String dataSourceName = cli.getOption("u");
        MetaLoader unifiedMetaLoader = new MetaLoader(
                null,
                category,
                null,
                version
        );

        Collection<Meta> unifiedMetaCollection = unifiedMetaLoader.loadUnifiedMeta();
        Multimap<String, String> idMap = getIDMap(unifiedMetaCollection, dataSourceName);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(new File(cli.getOption("o"))));
            for (String uId : idMap.keySet()) {
                List<String> sIdList = (List<String>) idMap.get(uId);
                StringBuffer sb = new StringBuffer();
                sb.append(uId).append("\t");
                if (sIdList.size() > 0) {
                    for (int i = 0; i < sIdList.size(); i++) {
                        if (i == sIdList.size() - 1) {
                            sb.append(sIdList.get(i)).append("\n");
                        } else {
                            sb.append(sIdList.get(i)).append("^");
                        }
                    }
                    writer.write(sb.toString());
                }
            }
            writer.close();
        } catch (IOException e) {
            LOGGER.error("failed to write a result to a file : " + cli.getOption("o"));
            System.exit(1);
        }

    }

    /**
     * Get Source ID Map to the Unified ID
     * @param unifiedMetaCollection unified meta collection
     * @param dataSourceName data source name
     * @return source id map
     */
    static Multimap getIDMap(Collection<Meta> unifiedMetaCollection, String dataSourceName) {
        String dataSourceCode = Prop.getDataSourceCodeFromDataSourceName(dataSourceName) + "_";
        ListMultimap<String, String> result = ArrayListMultimap.create();
        for (Meta unifiedMeta : unifiedMetaCollection) {
            String uId = unifiedMeta.getId();
            List<String> sourceIdList = ((UnifiedMeta) unifiedMeta).getSourceIdList();
            for (String sourceId : sourceIdList) {
                if (sourceId.startsWith(dataSourceCode)) {
                    result.put(uId, sourceId.replace(dataSourceCode, ""));
                }
            }
        }

        return result;
    }
}
