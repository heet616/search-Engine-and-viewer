package heet.wikipediaviewer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HelloController {

    MainView view;

    public void setView(MainView view) {
        this.view = view;
    }

    public ObservableList<String> getRecentPages() {
        final ObservableList<String> recents = FXCollections.observableArrayList();
        return recents;
    }

    public ObservableList<String> getBookmarkPages() {
        final ObservableList<String> bookmarks = FXCollections.observableArrayList();
        return bookmarks;
    }

    private void addPageTab(){
    }


    private static List<PageElement> parseQuery(final String url) {
        final List<PageElement> results;
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            final StringBuilder response = HelloController.getResponse(conn);

            conn.getResponseCode();
            conn.disconnect();

            results = HelloController.parseXML(response);
            HelloController.parseXML(response);

        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    private static StringBuilder getResponse(final HttpURLConnection conn) throws IOException {
        final Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
        final var response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        System.out.println("response = " + response);
        return response;
    }

    private static List<PageElement> parseXML(final StringBuilder response) throws IOException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        final List<PageElement> results = new ArrayList<>();
        try {
            db = dbf.newDocumentBuilder();
            final InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response.toString()));
            final Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            final NodeList entryNodes = doc.getElementsByTagName("entry");
            for (int i = 0; i < entryNodes.getLength(); i++) {
                final Element entry = (Element) entryNodes.item(i);

                final String title = HelloController.getTextContent(entry, "title");
                final String summary = HelloController.getTextContent(entry, "summary");
                final String id = HelloController.getTextContent(entry, "id"); // The paper's URL
                final String publishedDate = HelloController.getTextContent(entry, "published");
                final List<String> authors = HelloController.parseAuthors(entry);

                System.out.println("\n📄 Paper " + (i + 1));
                System.out.println("🔹 Title: " + title);
                System.out.println("🔹 Summary: " + summary);
                System.out.println("🔹 Link: " + id);
                System.out.println("🔹 Published: " + publishedDate);
                System.out.println("🔹 Authors: ");
                authors.stream().forEach((String authorName) -> System.out.println(authorName));
                results.add(new TextElement(title, authors, summary, "0", publishedDate, id));
            }

        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (final SAXException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    private static List<String> parseAuthors(final Element entry) {
        final NodeList nodes = entry.getElementsByTagName("author");
        final int l = nodes.getLength();
        final ArrayList<String> authors = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            final Element node = (Element) nodes.item(i);
            authors.add(HelloController.getTextContent(node, "name"));
        }
        return authors;
    }

    private static String getTextContent(final Element element, final String tagName) {
        final NodeList nodeList = element.getElementsByTagName(tagName);
        return (0 < nodeList.getLength()) ? nodeList.item(0).getTextContent().trim() : "N/A";
    }

    public List<PageElement> search(final String text) {
        final String url = "http://export.arxiv.org/api/query?search_query=all:" + text + "&max_results=5";
        return HelloController.parseQuery(url);
    }
}