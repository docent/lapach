package org.lolownia.dev.lapach;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Downloader {

    private static Logger log = LoggerFactory.getLogger(Downloader.class);

    private String feedUrl;
    private String username;
    private String password;

    public Downloader(String feedUrl) {
        this(feedUrl, null, null);
    }

    public Downloader(String feedUrl, String username, String password) {
        this.feedUrl = feedUrl;
        this.username = username;
        this.password = password;
    }

    public void download() {
        FeedParser parser = new FeedParser();
        Rss rss = parser.parse(null);

        // Create a directory for all podcasts, if not exists
        File allPodcastsDir = new File("Podcasts");
        if (allPodcastsDir.exists()) {
            if (!allPodcastsDir.isDirectory()) {
                throw new RuntimeException(allPodcastsDir.getAbsolutePath() +
                        " exists and is not a directory");
            }
        } else {
            if (!allPodcastsDir.mkdir()) {
                throw new RuntimeException("Could not create directory " +
                    allPodcastsDir.getAbsolutePath());
            }
        }

        // Create a directory for the currently downloaded podcast, if not
        // exists
        String podcastDirName = getValidFileName(rss.getChannel().getTitle());

        if (podcastDirName == null) {
            throw new RuntimeException("Generated podcast dir is empty");
        }

        File podcastDir = new File (allPodcastsDir, podcastDirName);

        if (podcastDir.exists()) {
            if (!podcastDir.isDirectory()) {
                throw new RuntimeException(podcastDir.getAbsolutePath() +
                        " exists and is not a directory");
            }
        } else {
            if (!podcastDir.mkdir()) {
                throw new RuntimeException("Could not create directory " +
                    podcastDir.getAbsolutePath());
            }
        }

        // Create podcast db if not exists
        String dbUrl = "local:" + allPodcastsDir.getAbsolutePath() + "/podcast.db";
        OObjectDatabaseTx db = new OObjectDatabaseTx(dbUrl);
        if (!db.exists()) {
            db.create();
        } else {
            db.open("admin", "admin");
        }

        db.getEntityManager().registerEntityClass(DownloadInfo.class);

//        for (Rss.Item item : rss.getChannel().getItems()) {
//            OneItemDownloader oneItemDownloader = new OneItemDownloader(
//                    rss, item, podcastDir, db);
//            oneItemDownloader.download();
//        }
        OneItemDownloader oneItemDownloader = new OneItemDownloader(
                rss, rss.getChannel().getItems().get(0), podcastDir, db
        );
        oneItemDownloader.download();

    }

    private Rss getRss() {
        DefaultHttpClient client = new DefaultHttpClient();

        if (username != null) {
            client.getCredentialsProvider().setCredentials(
                    new AuthScope(null, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(username, password));
        }

        HttpGet get = new HttpGet(feedUrl);
        HttpResponse response;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FeedParser feedParser = new FeedParser();

        Rss rss = feedParser.parse(null);

        InputStream input = null;

        try {
            input = response.getEntity().getContent();
            feedParser = new FeedParser();
            feedParser.parse(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {}
            }
        }
        return rss;
    }

    public static String getValidFileName(String fileName) {
        String newFileName =  fileName.replaceAll("[:\\\\/*?|<>]", "_");
        if (newFileName.length() == 0) {
            return null;
        }
        return newFileName;
    }
}
