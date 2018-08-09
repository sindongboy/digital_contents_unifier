package com.skplanet.nlp.unifier.dc.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.unifier.dc.data.meta.Meta;
import com.skplanet.nlp.unifier.dc.data.meta.MetaLoader;
import com.skplanet.nlp.unifier.dc.data.meta.UnifiedMeta;
import com.skplanet.nlp.unifier.dc.util.HDFSUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hocon to Json Tester
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 6/23/15
 */
public class UnifiedMetaHocon2Json {
    public static void main(String[] args) throws ParseException, IOException {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("s", "service", true, "service name", true);
        cli.addOption("c", "service", true, "service name", true);
        cli.addOption("v", "service", true, "service name", true);
        cli.parseOptions(args);

        HDFSUtil hdfsUtil = new HDFSUtil();
        Config hdfsConfig = ConfigFactory.load("dcu-hadoop.conf");

        String service = cli.getOption("s");
        String category = cli.getOption("c");
        String version = cli.getOption("v");
        String savePath = hdfsConfig.getString("unified-meta-" + category + "-hdfs-path");

        MetaLoader umetaLoader = new MetaLoader(service, category, null, version);
        List<Meta> umetaCollections = (ArrayList) umetaLoader.loadUnifiedMeta();

        //BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[0])));
        JSONParser parser = new JSONParser();
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (Meta meta : umetaCollections) {
            String json = ((UnifiedMeta) meta).toJson();
            Object obj = parser.parse(json);
            JSONObject jobj = (JSONObject) obj;
            sb.append(jobj.toString()).append(",");
            sb.append(jobj.toString());
        }
        sb.append("]");

        Object finalObj = parser.parse(sb.toString());
        JSONArray finalArray = (JSONArray) finalObj;



        hdfsUtil.write(savePath + "/" + category + "-umeta-" + version + ".json", sb.toString());
    }
}
