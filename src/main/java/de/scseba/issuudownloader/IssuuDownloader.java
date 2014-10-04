/*
 * Copyright (C) 2013 by www.scseba.de, Germany. All Rights Reserved.
 */
package de.scseba.issuudownloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Date: 04.07.13
 */
public class IssuuDownloader {

    private static final String ISSUU_IMAGE_URL = "http://image.issuu.com/";
    private static final Logger LOGGER = LoggerFactory.getLogger(IssuuDownloader.class);

    private String issuuDocumentUrl;

    public IssuuDownloader(final String issuuDocumentUrl) {
        this.issuuDocumentUrl = issuuDocumentUrl;
    }

    public void download() throws IOException {
        DataCacheExtractor dataCacheExtractor = new DataCacheExtractor(issuuDocumentUrl);
        if (!dataCacheExtractor.download()) {
            System.err.println("Cannot find data cache");
            System.exit(-1);
        }

        int pageCount = dataCacheExtractor.getPageCount();
        String orgDocName = dataCacheExtractor.getOrgDocName();
        String documentId = dataCacheExtractor.getDocumentId();
        String docName = dataCacheExtractor.getDocName();
        String issuuImageUrl = ISSUU_IMAGE_URL + documentId + "/jpg/";

        File imageFolder = new File("downloaded" + File.separator + docName + "-images");
        if (!imageFolder.isDirectory() && !imageFolder.mkdirs()) {
            LOGGER.error("Cannot create output directory [" + imageFolder.getAbsolutePath() + "]");
            throw new IOException("Cannot create output directory [" + imageFolder.getAbsolutePath() + "]");
        }

        dataCacheExtractor.save(new File(imageFolder, "dataCache.txt"));

        LOGGER.debug("Download " + pageCount + " pages of " + docName + " from [" + issuuDocumentUrl);

        FileDownload fileDownload = new FileDownload();
        PdfWriter pdfWriter = new PdfWriter();
        String formatPattern = createFormatPattern(pageCount);
        CliProgress progress = new CliProgress(pageCount);

        for (int page = 1; page <= pageCount; page++) {
            String orgFileName = "page_" + page + ".jpg";
            String saveFileName = "page_" + String.format(formatPattern, page) + ".jpg";
            File imageFile = new File(imageFolder, saveFileName);

//            System.out.print("Download page " + page + "/" + pageCount + " ... ");

            String fileUrl = issuuImageUrl + orgFileName;
            try {
                fileDownload.downloadSingleFile(fileUrl, imageFile);
            } catch (IOException e) {
                LOGGER.error("Error while download file [" + fileUrl + "]", e);
                throw e;
            }
//            System.out.println("done");
            pdfWriter.addImagePage(imageFile);
            progress.updateProgress(page);
        }
        pdfWriter.save(new File(imageFolder, orgDocName));
    }

    private String createFormatPattern(final int pageCount) {
        return "%0" + String.valueOf(pageCount).length() + "d";
    }

}
