package org.lolownia.dev.lapach;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;

@XmlRootElement
public class Rss {

    @XmlElement
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public static class Channel {
        @XmlElement
        private String title;

        @XmlElement(name = "item")
        private ArrayList<Item> items;

        public String getTitle() {
            return title;
        }

        public ArrayList<Item> getItems() {
            return items;
        }
    }

    public static class Item {
        @XmlElement
        private String title;

        @XmlElement
        private String pubDate;

        @XmlElement
        private String guid;

        @XmlElement
        private Enclosure enclosure;

        public String getTitle() {
            return title;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getGuid() {
            return guid;
        }

        public Enclosure getEnclosure() {
            return enclosure;
        }
    }

    public static class Enclosure {
        @XmlAttribute
        private String url;

        @XmlAttribute
        private int length;

        @XmlAttribute
        private String type;

        public String getUrl() {
            return url;
        }

        public int getLength() {
            return length;
        }

        public String getType() {
            return type;
        }
    }


}

