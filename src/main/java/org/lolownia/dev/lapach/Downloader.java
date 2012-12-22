package org.lolownia.dev.lapach;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Downloader {
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
        System.out.println("Connecting...");
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

        InputStream input = null;

        try {
            input = response.getEntity().getContent();
            FeedParser feedParser = new FeedParser();
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
    }
}
