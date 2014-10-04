/*
 * Copyright (C) 2013 by www.scseba.de, Germany. All Rights Reserved.
 */
package de.scseba.issuudownloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Date: 29.06.13
 *
 * This class works as download-helper.
 */
public class FileDownload {

    private DefaultHttpClient httpClient;

    public FileDownload() {
        httpClient = createThreadSafeHttpClient();
        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
    }

    DefaultHttpClient createThreadSafeHttpClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager connectionManager = client.getConnectionManager();
        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(connectionManager.getSchemeRegistry()), params);
        return client;
    }

    public void downloadSingleFile(final String fileUrl, final File file) throws IOException {
        HttpGet httpGet = new HttpGet(fileUrl);
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200 && entity != null) {
            try (InputStream in = entity.getContent();
                 FileOutputStream out = new FileOutputStream(file)) {

                byte[] buf = new byte[4096];
                int n;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
            }
        } else {
            throw new IOException("StatusCode [" + statusCode + "] Could not download [" + fileUrl + "]");
        }
    }

}
