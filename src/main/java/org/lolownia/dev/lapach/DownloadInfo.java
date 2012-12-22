package org.lolownia.dev.lapach;

public class DownloadInfo {
    private String guid;
    private String savedFileName;
    boolean downloaded;

    public DownloadInfo() {
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getSavedFileName() {
        return savedFileName;
    }

    public void setSavedFileName(String savedFileName) {
        this.savedFileName = savedFileName;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "guid='" + guid + '\'' +
                ", savedFileName='" + savedFileName + '\'' +
                ", downloaded=" + downloaded +
                '}';
    }
}
