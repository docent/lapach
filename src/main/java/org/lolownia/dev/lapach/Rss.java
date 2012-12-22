package org.lolownia.dev.lapach;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class Rss {

    @XmlElementWrapper
    private ArrayList<Channel> channels;

    private class Channel {
        @XmlAttribute
        private String title;
    }

}

