package com.skplanet.nlp.unifier.dc.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.skplanet.nlp.unifier.dc.data.meta.Meta;
import com.skplanet.nlp.unifier.dc.map.ContentsMapper;
import com.skplanet.nlp.unifier.dc.util.HDFSUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * Digital Contents Unifier Driver Program
 *
 * it will unify all the data source for the given category,
 * and data sources being unified is configured in 'unification.conf'
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 4/11/15
 */
public class DCUnifier extends AbsUnifier {
    // logger
    private static final Logger LOGGER = Logger.getLogger(DCUnifier.class.getName());

    /**
     * Driver Program
     * @param args category name and version
     */
    public static void main(String[] args) {
        // command line utility
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("c", "category", true, "category name", true);
        cli.addOption("v", "version", true, "version", true);
        cli.parseOptions(args);

        // env. like category name and version
        String category = cli.getOption("c");
        String version = cli.getOption("v");

        Config mappingConfig = ConfigFactory.load(Prop.UNIFICATION_CONFIG_NAME);
        // get hdfs path where the unified meta being stored
        String hdfsPath = mappingConfig.getString("path." + category);
        // get the data source list being unified
        List<String> dataSourceList = mappingConfig.getStringList("unify." + category + ".set");

        // mapper
        ContentsMapper mapper;
        // hdfs utility
        HDFSUtil hdfs = new HDFSUtil();

        // loop through each data source
        long begin, end;
        for (String dataSource : dataSourceList) {
            String[] fields = dataSource.split("-");
            // field[0] : service name
            // field[1] : category name
            // field[2] : data source name
            mapper = new ContentsMapper(fields[0], fields[1], fields[2], version);

            // -------------------------
            // -------------------------
            // mapping begin
            LOGGER.info("mapping data source start: " + dataSource);
            begin = System.currentTimeMillis();
            Collection<Meta> unifiedMetaCollection = mapper.map();
            end = System.currentTimeMillis();
            LOGGER.info("mapping done, in " + (end - begin) + " msec.");
            // mapping done
            // -------------------------
            // -------------------------

            // writing unified meta into hdfs
            LOGGER.info("writing unified meta: hdfs://" + hdfsPath + "/" + category + "-umeta-" + version);
            begin = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer();
            sb.append("category.name = \"" + category + "\"\n\n");
            sb.append("umeta = [\n");
            for (Meta meta : unifiedMetaCollection) {
                sb.append(meta.toIndentedString(1)).append("\n");
            }
            sb.append("]\n");
            hdfs.write(hdfsPath + "/" + category + "-umeta-" + version, sb.toString());
            //hdfs.write(Prop.DC_ROOT + "/versions", version);
            end = System.currentTimeMillis();
            LOGGER.info("unified meta writing done, in " + (end - begin) + " msec.");
        }
    }
}
