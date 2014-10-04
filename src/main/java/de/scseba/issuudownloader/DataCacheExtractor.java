/*
 * Copyright (C) 2013 by www.scseba.de, Germany. All Rights Reserved.
 */
package de.scseba.issuudownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DataCacheExtractor {

    private final String issuuDocumentUrl;
    private String[] dataCache;
    private Logger logger = LoggerFactory.getLogger(DataCacheExtractor.class);

    public DataCacheExtractor(final String issuuDocumentUrl) {
        this.issuuDocumentUrl = issuuDocumentUrl;
    }

    public boolean download() {
        try {
            Document htmlDocument = Jsoup.connect(issuuDocumentUrl).get();
            Elements scripts = htmlDocument.getElementsByTag("script");
            for (Element script : scripts) {
                if (script.data().contains("window.issuuDataCache")) {
                    dataCache = script.data().replaceAll("\\\"", "").replaceAll(",", ":").split("\\{");
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error("Exception while download [" + issuuDocumentUrl + "]", e);
        }
        return false;
    }

    public int getPageCount() {
        return extractInteger("pageCount");
    }

    public String getOrgDocName() {
        return extractString("orgDocName");
    }

    public String getDocumentId() {
        return extractString("documentId");
    }

    public String getDocName() {
        return extractString("docname");
    }

    private int extractInteger(final String valueName) {
        for (String entry : dataCache) {
            if (entry.contains(valueName)) {
                List<String> subEntries = Arrays.asList(entry.split(":"));
                return Integer.valueOf(subEntries.get(subEntries.indexOf(valueName) + 1));
            }
        }
        return 0;
    }

    private String extractString(final String valueName) {
        for (String entry : dataCache) {
            if (entry.contains(valueName)) {
                List<String> subEntries = Arrays.asList(entry.split(":"));
                return subEntries.get(subEntries.indexOf(valueName) + 1);
            }
        }
        return "";
    }

    public void save(final File file) {
        if (dataCache == null || dataCache.length == 0) {
            logger.debug("No DataCache found to save");
            return;
        }
        try (final FileOutputStream fos = new FileOutputStream(file)) {
            for (String line : dataCache) {
                fos.write(line.getBytes());
                fos.write("\n".getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            logger.error("Could not find file [" + file.getAbsolutePath() + "]", e);
        } catch (IOException e) {
            logger.error("Error while save DataCache to file [" + file.getAbsolutePath() + "]", e);
        }
    }

}
