package org.lolownia.dev.lapach;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class OneItemDownloader {

    private static Logger logger = LoggerFactory.getLogger(OneItemDownloader.class);

    private Rss.Item item;
    private File podcastDir;
    OObjectDatabaseTx db;
    private final HttpClient httpClient;

    public OneItemDownloader(HttpClient httpClient, Rss rss, Rss.Item item,
                             File podcastDir, OObjectDatabaseTx db) {
        this.httpClient = httpClient;
        this.item = item;
        this.podcastDir = podcastDir;
        this.db = db;
    }

    public void download() throws IOException {
        OSQLSynchQuery<DownloadInfo> query =
                new OSQLSynchQuery<>("select * from DownloadInfo where guid = ?");
        List<DownloadInfo> result = db.query(query, item.getGuid());

        DownloadInfo downloadInfo;
        boolean downloadNeeded = true;
        if (result.size() == 0) {

            downloadInfo = db.newInstance(DownloadInfo.class);
            downloadInfo.setGuid(item.getGuid());
            downloadInfo = db.save(downloadInfo);
        } else if (result.size() == 1) {
            downloadInfo = result.get(0);
            if (downloadInfo.isDownloaded()) {
                // Check if exist and file size correct
                File downloadedFile = new File(podcastDir, downloadInfo.getSavedFileName());
                if (!downloadedFile.exists()) {
                    logger.info("Previously downloaded file does not exist, downloading again");
                } else if (new File(podcastDir, downloadInfo.getSavedFileName()).length() !=
                        item.getEnclosure().getLength()) {
                    logger.info("Previously downloaded file size different from expected, " +
                            "downloading again");
                } else {
                    downloadNeeded = false;
                }
            }
        } else {
           throw new RuntimeException("Duplicate GUIDS for download items");
        }

        if (downloadNeeded) {
            logger.info("Downloading \"" + item.getTitle() + "\"");
            logger.info("Download URL: " + item.getEnclosure().getUrl());
            URL url = new URL(item.getEnclosure().getUrl());
            String originalFileName = url.getFile();
            String savedFileName = getSavedFileName(originalFileName, item.getTitle());


            HttpGet get = new HttpGet(item.getEnclosure().getUrl());
            HttpResponse response = httpClient.execute(get);

            byte[] buffer = new byte[1024];

            InputStream input = null;
            OutputStream output = null;
            try {
                input = response.getEntity().getContent();

                output = new FileOutputStream(new File(podcastDir, savedFileName));
                for (int length; (length = input.read(buffer)) > 0;) {
                    output.write(buffer, 0, length);
                }
                logger.info("Download successful");
            } finally {
                if (output != null) {
                    try { output.close();
                    } catch (IOException ignored) {}
                }
                if (input != null) try {
                    input.close();
                } catch (IOException ignored) {}
            }
            downloadInfo.setDownloaded(true);
            downloadInfo.setSavedFileName(savedFileName);
            db.save(downloadInfo);
        } else {
            logger.debug("Skipping \"" + item.getTitle() + "\"");
        }
    }

    private static String getSavedFileName(String originalFileName, String title) {
        String originalFileExt = FilenameUtils.getExtension(originalFileName);
        String savedFileName = title.trim() + "." + originalFileExt;
        return LapachUtil.getValidFileName(savedFileName);
    }
}
