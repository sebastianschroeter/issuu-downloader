/*
 * Copyright (C) 2013 by www.scseba.de, Germany. All Rights Reserved.
 */
package de.scseba.issuudownloader;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Date: 30.06.13
 */
public class PdfWriter {

    private PDDocument document;
    private Logger logger = LoggerFactory.getLogger(PdfWriter.class);

    public PdfWriter() {
        document = createDocument();
        setDocumentInformation();
    }

    private void setDocumentInformation() {
        PDDocumentInformation info = new PDDocumentInformation();
        info.setAuthor("zap");
        info.setCreationDate(Calendar.getInstance());
        info.setCreator("issuuDownloader");
        document.setDocumentInformation(info);
    }

    private PDDocument createDocument() {
        try {
            document = new PDDocument();
        } catch (IOException e) {
            logger.error("Error while creating PDF document", e);
        }
        return document;
    }

    public void addImagePage(final File imageFile) {
        PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);

        document.addPage(page);

        try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            PDXObjectImage image = new PDJpeg(document, fileInputStream);

            PDPageContentStream content = new PDPageContentStream(document, page, true, true);
            content.drawXObject(image, 0, 0, PDPage.PAGE_SIZE_A4.getWidth(), PDPage.PAGE_SIZE_A4.getHeight());
            content.close();
        } catch (IOException e) {
            logger.error("Error while adding image [" + imageFile.getAbsolutePath() + "] to page", e);
        }
    }

    public void save(final File pdfFile) {
        try {
            document.save(pdfFile);
            document.close();
        } catch (IOException | COSVisitorException e) {
            logger.error("Error while saving PDF file [" + pdfFile.getAbsolutePath() + "]", e);
        }
    }
}
