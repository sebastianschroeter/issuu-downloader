/*
 * Copyright (C) 2013 by www.scseba.de, Germany. All Rights Reserved.
 */
package de.scseba.issuudownloader;

import java.io.IOException;

/**
 * Date: 29.06.13
 */
public final class Main {

    private Main() {
    }

    public static void main(final String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar issuuDownloader issuuDocumentURL");
            System.exit(-1);
        }

        IssuuDownloader issuuDownloader = new IssuuDownloader(args[0]);
        try {
            issuuDownloader.download();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

}
