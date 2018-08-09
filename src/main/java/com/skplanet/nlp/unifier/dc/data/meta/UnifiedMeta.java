package com.skplanet.nlp.unifier.dc.data.meta;

import com.skplanet.nlp.unifier.dc.config.Prop;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 4/22/15
 */
public class UnifiedMeta extends AbsMeta {
    private static final Logger LOGGER = Logger.getLogger(MovieMetaHoppin.class.getName());

    private String category = null;

    private List<String> sourceIdList = null;

    /**
     * Value Overriding Constructor
     *
     * @param item item to be overriding
     */
    public UnifiedMeta(final Config item, final String category) {
        super(item);
        this.category = category;
        if (item == null) {
            this.sourceIdList = new ArrayList<String>();
        }
        this.keywords = item.getStringList("keywords");
        // source id list
        // ex. HP_00000001, TS_00000001
        this.sourceIdList = item.getStringList("sid-list");
    }

    /**
     * Add Source Id
     * @param sourceId source id
     */
    public void addSourceId(String sourceId) {
        if (!this.sourceIdList.contains(sourceId)) {
            this.sourceIdList.add(sourceId);
            LOGGER.info("[TADW-UPDATE] :: " + this.category + "\t" + getId() + "\tU\tN");
        } else {
            LOGGER.debug("source id already exist: " + sourceId);
        }
    }

    /**
     * Get Source Id List
     * @return source id list
     */
    public List<String> getSourceIdList() {
        return this.sourceIdList;
    }

    /**
     * Get Keyword List
     * @return keyword list
     */
    public List<String> getKeywordList() {
        if (this.keywords == null) {
            this.keywords = new ArrayList<String>();
        }
        return this.keywords;
    }

    /**
     * Add Keyword to the keyword list
     * @param keyword keyword to added
     */
    public void addKeywords(String keyword) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<String>();
        }

        if (!this.keywords.contains(keyword)) {
            this.keywords.add(keyword);
        }
    }

    /**
     * Update Score and Score Counts with the given source score and score counts
     * @param dataSourceName data source name
     * @param score score
     * @param scoreCount score count
     */
    public void updateScoresAndScoreCounts(String dataSourceName, String score, String scoreCount) {
        int pScoreCountIndex;
        if (scoreCount.equals("null")) {
            scoreCount = "0";
        }
        if (score.equals("null")) {
            score = "0.00";
        }
        String dataSourceCode = Prop.getDataSourceCodeFromDataSourceName(dataSourceName);
        for (String pScore : this.scores) {
            if (pScore.startsWith(dataSourceCode)) {
                pScoreCountIndex = this.scores.indexOf(pScore);
                // replace score
                if (Integer.parseInt(this.scoreCounts.get(pScoreCountIndex).substring(3))
                        < Integer.parseInt(scoreCount)) {
                    // replace score
                    this.scores.remove(pScoreCountIndex);
                    this.scores.add(dataSourceCode + "_" + score);
                    this.scoreCounts.remove(pScoreCountIndex);
                    this.scoreCounts.add(dataSourceCode + "_" + scoreCount);
                    // [TADW] log it if sub-meta fields are changed.
                    LOGGER.info("[TADW-UPDATE] :: " + this.category + "\t" + this.getId() + "\tN\tU");
                    return;
                }
                // ignore
                else {
                    return;
                }
            }
        }

        // no exist, then add
        this.scores.add(dataSourceCode + "_" + score);
        this.scoreCounts.add(dataSourceCode + "_" + scoreCount);
    }

    /**
     * Update Purchase Count if available
     * @param dataSourceCode data source code
     * @param purchaseCount purchase count
     */
    public void updatePurchaseCount(String dataSourceCode, String purchaseCount) {
        if (purchaseCount.equals("null")) {
            return;
        }
        int updateIndex = -1;
        for (int i = 0; i < this.purchaseCount.size(); i++) {
            if (this.purchaseCount.get(i).startsWith(dataSourceCode)) {
                updateIndex = i;
                break;
            }
        }

        if (updateIndex < 0) {
            this.purchaseCount.add(dataSourceCode + "_" + purchaseCount);
        } else {
            this.purchaseCount.remove(updateIndex);
            this.purchaseCount.add(dataSourceCode + "_" + purchaseCount);
        }
    }

    /**
     * Not Applicable
     *
     * @return N/A
     */
    @Override
    public String getService() {
        LOGGER.debug("Unified Meta doesn't have API for getting SERVICE CODE");
        return null;
    }

    /**
     * Get Category Name
     *
     * @return category name
     */
    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public String toIndentedString(int indent) {
        StringBuffer sb = new StringBuffer();
        StringBuffer tab = new StringBuffer();

        for (int i = 0; i < indent; i++) {
            tab.append("\t");
        }

        sb.append(tab).append("{\n");
        // contents id list
        sb.append(tab).append("\tid = \"").append(this.id).append("\"\n");
        // source id list
        sb.append(tab).append("\tsid-list = [\n");
        for (String sid : this.sourceIdList) {
            sb.append(tab).append("\t\t\"").append(sid).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\ttitle = \"").append(this.title).append("\"\n");
        sb.append(tab).append("\torg-title = \"").append(this.orgTitle).append("\"\n");
        sb.append(tab).append("\tdate = \"").append(this.date).append("\"\n");
        sb.append(tab).append("\tsynopsis = \"").append(this.synopsis).append("\"\n");
        sb.append(tab).append("\trate = \"").append(this.rate).append("\"\n");
        sb.append(tab).append("\tgenres = [\n");
        for (String genre : this.genres) {
            sb.append(tab).append("\t\t\"").append(genre).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\tdirectors = [\n");
        for (String director : this.directors) {
            sb.append(tab).append("\t\t\"").append(director).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\tactors = [\n");
        for (String actor : this.actors) {
            sb.append(tab).append("\t\t\"").append(actor).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\tkeywords = [\n");
        for (String keyword : this.keywords) {
            sb.append(tab).append("\t\t\"").append(keyword).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        //sb.append(tab).append("\tscore = ").append(this.score).append("\n");
        sb.append(tab).append("\tscores = [\n");
        for (String score : this.scores) {
            sb.append(tab).append("\t\t\"").append(score).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        //sb.append(tab).append("\tscore-count = ").append(this.scoreCount).append("\n");
        sb.append(tab).append("\tscore-counts = [\n");
        for (String scoreCount : this.scoreCounts) {
            sb.append(tab).append("\t\t\"").append(scoreCount).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        //sb.append(tab).append("\tpurchase = ").append(this.purchaseCount).append("\n");
        sb.append(tab).append("\tpurchase = [").append("\n");
        for (String purchase : this.purchaseCount) {
            sb.append(tab).append("\t\t\"").append(purchase).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\tnations = [\n");
        for (String nation : this.nations) {
            sb.append(tab).append("\t\t\"").append(nation).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("}");

        return sb.toString();
    }

    /**
     * Hocon to TSV format unified meta
     *
     * @return tsv format
     */
    public String toTSV() {
        StringBuffer sb = new StringBuffer();
        // cid
        sb.append(this.getId()).append("\t");
        // sid list
        List<String> sourceIdListTmp = this.getSourceIdList();
        List<String> sourceIdList = new ArrayList<String>();
        for (String sourceId : sourceIdListTmp) {
            /*
            // tmp. only tstore ids
            if (!sourceId.startsWith("TS")) {
                continue;
            }
            sourceIdList.add(sourceId.replace("TS_", ""));
            */
            sourceIdList.add(sourceId);
        }

        if (sourceIdList.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < sourceIdList.size(); i++) {
                /*
                if (i == sourceIdList.size() - 1) {
                    sb.append(sourceIdList.get(i).replace("HP_", "")).append("\t");
                } else {
                    sb.append(sourceIdList.get(i).replace("HP_", "")).append("^");
                }
                */
                if (i == sourceIdList.size() - 1) {
                    sb.append(sourceIdList.get(i)).append("\t");
                } else {
                    sb.append(sourceIdList.get(i)).append("^");
                }
            }
        }

        // title
        sb.append(this.getTitle()).append("\t");
        // synopsis
        if (this.getSynopsis().length() == 0) {
            sb.append("null\t");
        } else {
            sb.append(this.getSynopsis()).append("\t");
        }
        // date
        sb.append(this.getDate()).append("\t");
        // rate
        sb.append(Prop.getStandardRateName(this.getRate())).append("\t");
        // genres
        if (this.getGenres().isEmpty()) {
            sb.append("null\t");
        } else {
            List<String> genreList = this.getGenres();
            for (int i = 0; i < genreList.size(); i++) {
                if (i == genreList.size() - 1) {
                    sb.append(genreList.get(i)).append("\t");
                } else {
                    sb.append(genreList.get(i)).append("^");
                }
            }
        }
        // genre bigram
        List<String> genreList = this.getGenres();
        if (genreList.isEmpty() || genreList.size() == 1) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < genreList.size() - 1; i++) {
                sb.append(genreList.get(i));
                if (i + 1 < genreList.size()) {
                    sb.append("_" + genreList.get(i + 1));
                }
                if (i + 1 < genreList.size() - 1) {
                    sb.append("^");
                }
            }
            sb.append("\t");
        }
        // directors
        List<String> directors = this.getDirectors();
        if (directors.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < directors.size(); i++) {
                if (i == directors.size() - 1) {
                    sb.append(directors.get(i)).append("\t");
                } else {
                    sb.append(directors.get(i)).append("^");
                }
            }
        }
        // actors
        List<String> actors = this.getActors();
        if (actors.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < actors.size(); i++) {
                if (i == actors.size() - 1) {
                    sb.append(actors.get(i)).append("\t");
                } else {
                    sb.append(actors.get(i)).append("^");
                }
            }
        }
        // nations
        List<String> nations = this.getNations();
        if (nations.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < nations.size(); i++) {
                if (i == nations.size() - 1) {
                    sb.append(nations.get(i)).append("\t");
                } else {
                    sb.append(nations.get(i)).append("^");
                }
            }
        }
        // kmdb keywords
        List<String> keywords = ((UnifiedMeta) this).getKeywordList();
        if (keywords.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < keywords.size(); i++) {
                if (i == keywords.size() - 1) {
                    sb.append(keywords.get(i)).append("\t");
                } else {
                    sb.append(keywords.get(i)).append("^");
                }
            }
        }

        // scores
        List<String> scores = this.getScore();
        String hoppinScore = null;
        String tstoreScore = null;
        String naverScore = null;
        String kmdbScore = null;
        String daumScore = null;
        String skbScore = null;

        for (String score : scores) {
            if (score.startsWith("HP_")) {
                hoppinScore = score.replace("HP_", "");
            } else if (score.startsWith("TS_")) {
                tstoreScore = score.replace("TS_", "");
            } else if (score.startsWith("NV_")) {
                naverScore = score.replace("NV_", "");
            } else if (score.startsWith("KM_")) {
                kmdbScore = score.replace("KM_", "");
            } else if (score.startsWith("DM_")) {
                daumScore = score.replace("DM_", "");
            } else {
                skbScore = score.replace("SB_", "");
            }
        }

        if (hoppinScore != null) {
            sb.append(hoppinScore).append("^");
        } else {
            sb.append("null^");
        }

        if (tstoreScore != null) {
            sb.append(tstoreScore).append("^");
        } else {
            sb.append("null^");
        }

        if (naverScore != null) {
            sb.append(naverScore).append("^");
        } else {
            sb.append("null^");
        }

        if (kmdbScore != null) {
            sb.append(kmdbScore).append("^");
        } else {
            sb.append("null^");
        }

        if (daumScore != null) {
            sb.append(daumScore).append("^");
        } else {
            sb.append("null\t");
        }

        if (skbScore != null) {
            sb.append(skbScore).append("\t");
        } else {
            sb.append("null\t");
        }


        // score counts
        List<String> scoreCounts = this.getScoreCount();
        String hoppinCount = null;
        String tstoreCount = null;
        String naverCount = null;
        String kmdbCount = null;
        String daumCount = null;
        String skbCount = null;

        for (String scoreCount : scoreCounts) {
            if (scoreCount.startsWith("HP_")) {
                hoppinCount = scoreCount.replace("HP_", "");
            } else if (scoreCount.startsWith("TS_")) {
                tstoreCount = scoreCount.replace("TS_", "");
            } else if (scoreCount.startsWith("NV_")) {
                naverCount = scoreCount.replace("NV_", "");
            } else if (scoreCount.startsWith("KM_")) {
                kmdbCount = scoreCount.replace("KM_", "");
            } else if (scoreCount.startsWith("DM_")) {
                daumCount = scoreCount.replace("DM_", "");
            } else {
                skbCount = scoreCount.replace("SB_", "");
            }
        }

        if (hoppinCount != null) {
            sb.append(hoppinCount).append("^");
        } else {
            sb.append("null^");
        }

        if (tstoreCount != null) {
            sb.append(tstoreCount).append("^");
        } else {
            sb.append("null^");
        }

        if (naverCount != null) {
            sb.append(naverCount).append("^");
        } else {
            sb.append("null^");
        }

        if (kmdbCount != null) {
            sb.append(kmdbCount).append("^");
        } else {
            sb.append("null^");
        }

        if (daumCount != null) {
            sb.append(daumCount).append("^");
        } else {
            sb.append("null^");
        }

        if (skbCount != null) {
            sb.append(skbCount).append("\t");
        } else {
            sb.append("null\t");
        }


        // purchase
        //sb.append(this.getPurchaseCount()).append("\t");

        List<String> purchaseCountList = this.getPurchaseCount();
        if (purchaseCountList.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < purchaseCountList.size(); i++) {
                if (i == purchaseCountList.size() - 1) {
                    sb.append(purchaseCountList.get(i)).append("\t");
                } else {
                    sb.append(purchaseCountList.get(i)).append("^");
                }
            }
        }

        // TODO: 영화에서 M2K_ADD,REM 로딩해서 매핑하는 로직 필요
        // m2k_add : not applicable for an, dk, df(temp)
        sb.append("null\t");
        // m2k_rem : not applicable for an, dk, df(temp)
        sb.append("null\n");

        return sb.toString();
    }

    public String toTSV(String service) {
        StringBuffer sb = new StringBuffer();
        String svcRepId = Prop.getServiceCodeFromServiceName(service);
        // cid
        sb.append(this.getId()).append("\t");

        // sid list --> filtering by the given service id
        List<String> sourceIdListTmp = this.getSourceIdList();
        List<String> sourceIdList = new ArrayList<String>();

        for (String sourceId : sourceIdListTmp) {
            if (sourceId.startsWith(svcRepId)) {
                sourceIdList.add(sourceId);
            }
        }

        if (sourceIdList.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < sourceIdList.size(); i++) {
                if (i == sourceIdList.size() - 1) {
                    sb.append(sourceIdList.get(i).replace(svcRepId + "_", "")).append("\t");
                } else {
                    sb.append(sourceIdList.get(i).replace(svcRepId + "_", "")).append("^");
                }
            }
        }

        // title
        sb.append(this.getTitle()).append("\t");
        // synopsis
        if (this.getSynopsis().length() == 0) {
            sb.append("null\t");
        } else {
            sb.append(this.getSynopsis()).append("\t");
        }
        // date
        sb.append(this.getDate()).append("\t");
        // rate
        sb.append(Prop.getStandardRateName(this.getRate())).append("\t");
        // genres
        if (this.getGenres().isEmpty()) {
            sb.append("null\t");
        } else {
            List<String> genreList = this.getGenres();
            for (int i = 0; i < genreList.size(); i++) {
                if (i == genreList.size() - 1) {
                    sb.append(genreList.get(i)).append("\t");
                } else {
                    sb.append(genreList.get(i)).append("^");
                }
            }
        }
        // genre bigram
        List<String> genreList = this.getGenres();
        if (genreList.isEmpty() || genreList.size() == 1) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < genreList.size() - 1; i++) {
                sb.append(genreList.get(i));
                if (i + 1 < genreList.size()) {
                    sb.append("_" + genreList.get(i + 1));
                }
                if (i + 1 < genreList.size() - 1) {
                    sb.append("^");
                }
            }
            sb.append("\t");
        }
        // directors
        List<String> directors = this.getDirectors();
        if (directors.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < directors.size(); i++) {
                if (i == directors.size() - 1) {
                    sb.append(directors.get(i)).append("\t");
                } else {
                    sb.append(directors.get(i)).append("^");
                }
            }
        }
        // actors
        List<String> actors = this.getActors();
        if (actors.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < actors.size(); i++) {
                if (i == actors.size() - 1) {
                    sb.append(actors.get(i)).append("\t");
                } else {
                    sb.append(actors.get(i)).append("^");
                }
            }
        }
        // nations
        List<String> nations = this.getNations();
        if (nations.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < nations.size(); i++) {
                if (i == nations.size() - 1) {
                    sb.append(nations.get(i)).append("\t");
                } else {
                    sb.append(nations.get(i)).append("^");
                }
            }
        }
        // kmdb keywords
        List<String> keywords = ((UnifiedMeta) this).getKeywordList();
        if (keywords.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < keywords.size(); i++) {
                if (i == keywords.size() - 1) {
                    sb.append(keywords.get(i)).append("\t");
                } else {
                    sb.append(keywords.get(i)).append("^");
                }
            }
        }

        // scores
        List<String> scores = this.getScore();
        String hoppinScore = null;
        String tstoreScore = null;
        String naverScore = null;
        String kmdbScore = null;
        String daumScore = null;
        String skbScore = null;

        for (String score : scores) {
            if (score.startsWith("HP_")) {
                hoppinScore = score.replace("HP_", "");
            } else if (score.startsWith("TS_")) {
                // double its score for ranging from 0 to 10
                double adjustedScore = (Double.parseDouble(score.replace("TS_", "")))*2.0;
                if (adjustedScore > 10.0) {
                    adjustedScore = 10.00;
                }
                tstoreScore = adjustedScore + "";
            } else if (score.startsWith("NV_")) {
                naverScore = score.replace("NV_", "");
            } else if (score.startsWith("KM_")) {
                kmdbScore = score.replace("KM_", "");
            } else if (score.startsWith("DM_")) {
                daumScore = score.replace("DM_", "");
            } else {
                skbScore = score.replace("SB_", "");
            }
        }

        if (hoppinScore != null) {
            sb.append(hoppinScore).append("^");
        } else {
            sb.append("null^");
        }

        if (tstoreScore != null) {
            sb.append(tstoreScore).append("^");
        } else {
            sb.append("null^");
        }

        if (naverScore != null) {
            sb.append(naverScore).append("^");
        } else {
            sb.append("null^");
        }

        if (kmdbScore != null) {
            sb.append(kmdbScore).append("^");
        } else {
            sb.append("null^");
        }

        if (daumScore != null) {
            sb.append(daumScore).append("^");
        } else {
            sb.append("null^");
        }
        if (skbScore != null) {
            sb.append(skbScore).append("\t");
        } else {
            sb.append("null\t");
        }

        // score counts
        List<String> scoreCounts = this.getScoreCount();
        String hoppinCount = null;
        String tstoreCount = null;
        String naverCount = null;
        String kmdbCount = null;
        String daumCount = null;
        String skbCount = null;

        for (String scoreCount : scoreCounts) {
            if (scoreCount.startsWith("HP_")) {
                hoppinCount = scoreCount.replace("HP_", "");
            } else if (scoreCount.startsWith("TS_")) {
                tstoreCount = scoreCount.replace("TS_", "");
            } else if (scoreCount.startsWith("NV_")) {
                naverCount = scoreCount.replace("NV_", "");
            } else if (scoreCount.startsWith("KM_")) {
                kmdbCount = scoreCount.replace("KM_", "");
            } else if (scoreCount.startsWith("DM_")) {
                daumCount = scoreCount.replace("DM_", "");
            } else {
                skbCount = scoreCount.replace("SB_", "");
            }
        }

        if (hoppinCount != null) {
            sb.append(hoppinCount).append("^");
        } else {
            sb.append("null^");
        }

        if (tstoreCount != null) {
            sb.append(tstoreCount).append("^");
        } else {
            sb.append("null^");
        }

        if (naverCount != null) {
            sb.append(naverCount).append("^");
        } else {
            sb.append("null^");
        }

        if (kmdbCount != null) {
            sb.append(kmdbCount).append("^");
        } else {
            sb.append("null^");
        }

        if (daumCount != null) {
            sb.append(daumCount).append("^");
        } else {
            sb.append("null^");
        }

        if (skbCount != null) {
            sb.append(skbCount).append("\t");
        } else {
            sb.append("null\t");
        }

        // purchase
        List<String> purchaseCount = this.getPurchaseCount();
        String hoppinPurchase = null;
        String tstorePurchase = null;
        for (String purchase : purchaseCount) {
            if (purchase.startsWith("HP_")) {
                hoppinPurchase = purchase.replace("HP_", "");
            } else if (purchase.startsWith("TS_")) { // 서비스가 추가 되면 추가됨.
                tstorePurchase = purchase.replace("TS_", "");
            } else {
                // 통합메타 항목에서 삭제 되어야만 한다. ( ex. KM_null, NV_null ...)
            }
        }

        // hoppin purchase
        if (hoppinPurchase != null) {
            sb.append(hoppinPurchase).append("^");
        } else {
            sb.append("null^");
        }

        // tstore purchase
        if (tstorePurchase != null) {
            sb.append(tstorePurchase).append("\t");
        } else {
            sb.append("null\t");
        }

        // TODO: 영화에서 M2K_ADD,REM 로딩해서 매핑하는 로직
        // m2k_add : not applicable for an, dk, df(temp)
        sb.append("null\t");
        // m2k_rem : not applicable for an, dk, df(temp)
        sb.append("null\n");

        return sb.toString();
    }

    public String toTSV(String service, String addKeyword) {
        StringBuffer sb = new StringBuffer();
        String svcRepId = Prop.getServiceCodeFromServiceName(service);
        // cid
        sb.append(this.getId()).append("\t");

        // sid list --> filtering by the given service id
        List<String> sourceIdListTmp = this.getSourceIdList();
        List<String> sourceIdList = new ArrayList<String>();

        for (String sourceId : sourceIdListTmp) {
            if (sourceId.startsWith(svcRepId)) {
                sourceIdList.add(sourceId);
            }
        }

        if (sourceIdList.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < sourceIdList.size(); i++) {
                if (i == sourceIdList.size() - 1) {
                    sb.append(sourceIdList.get(i).replace(svcRepId + "_", "")).append("\t");
                } else {
                    sb.append(sourceIdList.get(i).replace(svcRepId + "_", "")).append("^");
                }
            }
        }

        // title
        sb.append(this.getTitle()).append("\t");
        // synopsis
        if (this.getSynopsis().length() == 0) {
            sb.append("null\t");
        } else {
            sb.append(this.getSynopsis()).append("\t");
        }
        // date
        sb.append(this.getDate()).append("\t");
        // rate
        sb.append(Prop.getStandardRateName(this.getRate())).append("\t");
        // genres
        if (this.getGenres().isEmpty()) {
            sb.append("null\t");
        } else {
            List<String> genreList = this.getGenres();
            for (int i = 0; i < genreList.size(); i++) {
                if (i == genreList.size() - 1) {
                    sb.append(genreList.get(i)).append("\t");
                } else {
                    sb.append(genreList.get(i)).append("^");
                }
            }
        }
        // genre bigram
        List<String> genreList = this.getGenres();
        if (genreList.isEmpty() || genreList.size() == 1) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < genreList.size() - 1; i++) {
                sb.append(genreList.get(i));
                if (i + 1 < genreList.size()) {
                    sb.append("_" + genreList.get(i + 1));
                }
                if (i + 1 < genreList.size() - 1) {
                    sb.append("^");
                }
            }
            sb.append("\t");
        }
        // directors
        List<String> directors = this.getDirectors();
        if (directors.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < directors.size(); i++) {
                if (i == directors.size() - 1) {
                    sb.append(directors.get(i)).append("\t");
                } else {
                    sb.append(directors.get(i)).append("^");
                }
            }
        }
        // actors
        List<String> actors = this.getActors();
        if (actors.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < actors.size(); i++) {
                if (i == actors.size() - 1) {
                    sb.append(actors.get(i)).append("\t");
                } else {
                    sb.append(actors.get(i)).append("^");
                }
            }
        }
        // nations
        List<String> nations = this.getNations();
        if (nations.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < nations.size(); i++) {
                if (i == nations.size() - 1) {
                    sb.append(nations.get(i)).append("\t");
                } else {
                    sb.append(nations.get(i)).append("^");
                }
            }
        }
        // kmdb keywords
        List<String> keywords = ((UnifiedMeta) this).getKeywordList();
        if (keywords.isEmpty()) {
            sb.append("null\t");
        } else {
            for (int i = 0; i < keywords.size(); i++) {
                if (i == keywords.size() - 1) {
                    sb.append(keywords.get(i)).append("\t");
                } else {
                    sb.append(keywords.get(i)).append("^");
                }
            }
        }

        // scores
        List<String> scores = this.getScore();
        String hoppinScore = null;
        String tstoreScore = null;
        String naverScore = null;
        String kmdbScore = null;
        String daumScore = null;
        String skbScore = null;

        for (String score : scores) {
            if (score.startsWith("HP_")) {
                hoppinScore = score.replace("HP_", "");
            } else if (score.startsWith("TS_")) {
                // double its score for ranging from 0 to 10
                double adjustedScore = (Double.parseDouble(score.replace("TS_", "")))*2.0;
                if (adjustedScore > 10.0) {
                    adjustedScore = 10.00;
                }
                tstoreScore = adjustedScore + "";
            } else if (score.startsWith("NV_")) {
                naverScore = score.replace("NV_", "");
            } else if (score.startsWith("KM_")) {
                kmdbScore = score.replace("KM_", "");
            } else if (score.startsWith("DM_")) {
                daumScore = score.replace("DM_", "");
            } else {
                skbScore = score.replace("SB_", "");
            }
        }

        if (hoppinScore != null) {
            sb.append(hoppinScore).append("^");
        } else {
            sb.append("null^");
        }

        if (tstoreScore != null) {
            sb.append(tstoreScore).append("^");
        } else {
            sb.append("null^");
        }

        if (naverScore != null) {
            sb.append(naverScore).append("^");
        } else {
            sb.append("null^");
        }

        if (kmdbScore != null) {
            sb.append(kmdbScore).append("^");
        } else {
            sb.append("null^");
        }

        if (daumScore != null) {
            sb.append(daumScore).append("^");
        } else {
            sb.append("null^");
        }

        if (skbScore != null) {
            sb.append(skbScore).append("\t");
        } else {
            sb.append("null\t");
        }

        // score counts
        List<String> scoreCounts = this.getScoreCount();
        String hoppinCount = null;
        String tstoreCount = null;
        String naverCount = null;
        String kmdbCount = null;
        String daumCount = null;
        String skbCount = null;

        for (String scoreCount : scoreCounts) {
            if (scoreCount.startsWith("HP_")) {
                hoppinCount = scoreCount.replace("HP_", "");
            } else if (scoreCount.startsWith("TS_")) {
                tstoreCount = scoreCount.replace("TS_", "");
            } else if (scoreCount.startsWith("NV_")) {
                naverCount = scoreCount.replace("NV_", "");
            } else if (scoreCount.startsWith("KM_")) {
                kmdbCount = scoreCount.replace("KM_", "");
            } else if (scoreCount.startsWith("DM_")) {
                daumCount = scoreCount.replace("DM_", "");
            } else {
                skbCount = scoreCount.replace("SB_", "");
            }
        }

        if (hoppinCount != null) {
            sb.append(hoppinCount).append("^");
        } else {
            sb.append("null^");
        }

        if (tstoreCount != null) {
            sb.append(tstoreCount).append("^");
        } else {
            sb.append("null^");
        }

        if (naverCount != null) {
            sb.append(naverCount).append("^");
        } else {
            sb.append("null^");
        }

        if (kmdbCount != null) {
            sb.append(kmdbCount).append("^");
        } else {
            sb.append("null^");
        }

        if (daumCount != null) {
            sb.append(daumCount).append("^");
        } else {
            sb.append("null^");
        }

        if (skbCount != null) {
            sb.append(skbCount).append("\t");
        } else {
            sb.append("null\t");
        }

        // purchase
        List<String> purchaseCount = this.getPurchaseCount();
        String hoppinPurchase = null;
        String tstorePurchase = null;
        for (String purchase : purchaseCount) {
            if (purchase.startsWith("HP_")) {
                hoppinPurchase = purchase.replace("HP_", "");
            } else if (purchase.startsWith("TS_")) { // 서비스가 추가 되면 추가됨.
                tstorePurchase = purchase.replace("TS_", "");
            } else {
                // 통합메타 항목에서 삭제 되어야만 한다. ( ex. KM_null, NV_null ...)
            }
        }

        // hoppin purchase
        if (hoppinPurchase != null) {
            sb.append(hoppinPurchase).append("^");
        } else {
            sb.append("null^");
        }

        // tstore purchase
        if (tstorePurchase != null) {
            sb.append(tstorePurchase).append("\t");
        } else {
            sb.append("null\t");
        }

        // m2k_add : not applicable for an, dk, df(temp)
        if (addKeyword != null) {
            sb.append(addKeyword + "\t");
        } else {
            sb.append("null\t");
        }
        // m2k_rem : not applicable for an, dk, df(temp)
        sb.append("null\n");

        return sb.toString();
    }


    /**
     * HOCON to JSON converter
     * @return json rendered hocon object
     */
    public String toJson() {
        String hoconString = this.toIndentedString(1);
        Config item = ConfigFactory.parseString(hoconString);
        return item.root().render(ConfigRenderOptions.concise().setJson(true));
    }
}
