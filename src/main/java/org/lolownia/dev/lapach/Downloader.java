package org.lolownia.dev.lapach;

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
import java.net.MalformedURLException;

public class Downloader {

    private static Logger log = LoggerFactory.getLogger(Downloader.class);

    private String feedUrl;
    private String username;
    private String password;

    HttpClient client;

    public Downloader(String feedUrl) {
        this(feedUrl, null, null);
    }

    public Downloader(String feedUrl, String username, String password) {
        this.feedUrl = feedUrl;
        this.username = username;
        this.password = password;

        client = setupHttpClient();
    }

    private HttpClient setupHttpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        if (username != null) {
            httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope(null, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(username, password));
        }
        return httpClient;
    }

    public void download() {
        Rss rss = getRss();

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
        String podcastDirName = LapachUtil.getValidFileName(rss.getChannel().getTitle());

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

        for (Rss.Item item : rss.getChannel().getItems()) {
            OneItemDownloader oneItemDownloader = new OneItemDownloader(client,
                    rss, item, podcastDir, db);
            try {
                oneItemDownloader.download();
            } catch (IOException e) {
                log.error("Failed to download \"" + item.getTitle() + "\"", e);
            }
        }

    }

    private Rss getRss() {
        HttpGet get = new HttpGet(feedUrl);
        HttpResponse response;
        log.info("Downloading RSS feed");
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

}
