package org.lolownia.dev.lapach;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class OneItemDownloader {

    private static Logger logger = LoggerFactory.getLogger(OneItemDownloader.class);

    private Rss.Item item;
    private File podcastDir;
    OObjectDatabaseTx db;

    public OneItemDownloader(Rss rss, Rss.Item item, File podcastDir, OObjectDatabaseTx db) {
        this.item = item;
        this.podcastDir = podcastDir;
        this.db = db;
    }

    public void download() {
        OSQLSynchQuery<DownloadInfo> query =
                new OSQLSynchQuery<>("select * from DownloadInfo where guid = ?");
        List<DownloadInfo> result = db.query(query, item.getGuid());

        boolean downloadNeeded = false;
        if (result.size() == 0) {

            DownloadInfo downloadInfo = db.newInstance(DownloadInfo.class);
            downloadInfo.setGuid(item.getGuid());
            db.save(downloadInfo);
        } else if (result.size() == 1) {
            DownloadInfo downloadInfo = result.get(0);
            System.out.println(downloadInfo.getGuid());
        } else {
           throw new RuntimeException("Duplicate GUIDS for download items");
        }


    }

    private static String getSavedFileName(String originalFileName, String title) {

    }

}
