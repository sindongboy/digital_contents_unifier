package com.skplanet.nlp.unifier.dc.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.skplanet.nlp.unifier.dc.data.crawl.CrawlTargetMaker;
import com.skplanet.nlp.unifier.dc.data.meta.Meta;
import org.apache.commons.collections.MultiMap;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 수집 요청을 위한 드라이버 클래스
 *
 * Usage : refer to {@link CommandLineInterface}
 * it takes three arguments [options] which are following,
 * -s service name list
 * -c category
 * -v version
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 4/14/15
 */
public class CrawlRequester {
    // logger
    private static final Logger LOGGER = Logger.getLogger(CrawlRequester.class.getName());

    /**
     * driver program
     * @param args service and category
     */
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        // get service list from user
        cli.addOption("s", "services", true, "service name list delim by \"^\", ex) hoppin^tstore^xlife etc.", true);
        // get category from user
        cli.addOption("c", "category", true, "category name, ex) movie, dramak, dramaf, animation etc.", true);
        // get version (yyyymmdd)
        cli.addOption("v", "version", true, "version - YYYYMMDD, ex) 20140302", true);
        // get output file name
        cli.addOption("o", "output", true, "output file name/path", true);
        cli.parseOptions(args);

        // set service list
        List<String> serviceList = new ArrayList<String>();
        for (String service : cli.getOption("s").split("\\^")) {
            serviceList.add(service);
        }

        long btime, etime;

        // set category and request crawling target collection
        LOGGER.info("processing meta files ....");
        btime = System.currentTimeMillis();
        CrawlTargetMaker crawl = new CrawlTargetMaker();
        MultiMap resultMap = crawl.request(serviceList, cli.getOption("c"), cli.getOption("v"));
        etime = System.currentTimeMillis();
        LOGGER.info("processing meta files done (" + (etime - btime) + " msec.");

        LOGGER.info("writing crawl target ....");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(cli.getOption("o"))));
            btime = System.currentTimeMillis();

            Set<String> keyset = resultMap.keySet();
            for (String key : keyset) {
                String title = key;
                List<String> services = new ArrayList<String>();
                List<String> ids = new ArrayList<String>();


                ArrayList<Meta> metaCollection = (ArrayList<Meta>) resultMap.get(key);
                for (Meta meta : metaCollection) {
                    services.add(Prop.getServiceCodeFromServiceName(meta.getService()));
                    ids.add(meta.getId());
                }
                writer.write(title + "\t");
                for (int i = 0; i < ids.size(); i++) {
                    if (i == ids.size() - 1) {
                        writer.write(services.get(i) + "_" + ids.get(i));
                        writer.newLine();
                    } else {
                    writer.write(services.get(i) + "_" + ids.get(i) + "^");
                    }
                }
            }

            writer.close();
            etime = System.currentTimeMillis();
        } catch (IOException e) {
            LOGGER.error("Failed to dump crawl target list at : " + cli.getOption("o"), e);
        }
        LOGGER.info("writing crawl target done (" + (etime - btime) + " msec.)");

    }

}
