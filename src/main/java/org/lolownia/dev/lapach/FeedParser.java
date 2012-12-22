package org.lolownia.dev.lapach;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FeedParser {
    public Rss parse(InputStream is) {
        JAXBContext jaxbContext;
        Unmarshaller unmarshaller;
        Rss rss;
        try {
            jaxbContext = JAXBContext.newInstance(Rss.class);
            unmarshaller = jaxbContext.createUnmarshaller();
            rss = (Rss) unmarshaller.unmarshal(new FileInputStream("c:/temp/feed.xml"));
        } catch (JAXBException | IOException e) {
            throw new RuntimeException(e);
        }
        return rss;
    }

    private class Handler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            System.out.println("qName = " + qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
        }
    }
}
