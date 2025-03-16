package heet.wikipediaviewer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public enum Parser {
    ;

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<PageElement> parseApiResponse(final String apiName, final String jsonResponse) {
        return switch (apiName) {
            case "arXiv" -> Parser.parseArxiv(jsonResponse);
            case "CORE" -> Parser.parseCore(jsonResponse);
            case "PLOS" -> Parser.parsePlos(jsonResponse);
            case "Semantic Scholar" -> Parser.parseSemanticScholar(jsonResponse);
            case "PubMed" -> Parser.parsePubMed(jsonResponse);
            case "OpenAlex" -> Parser.parseOpenAlex(jsonResponse);
            case "CrossRef" -> Parser.parseCrossRef(jsonResponse);
            case "IEEE" -> Parser.parseIEEE(jsonResponse);
            case "Springer" -> Parser.parseSpringer(jsonResponse);
            case "Scopus" -> Parser.parseScopus(jsonResponse);
            case "Web of Science" -> Parser.parseWebOfScience(jsonResponse);
            default -> new ArrayList<>();
        };
    }

    private static List<PageElement> parseArxiv(final String xmlResponse) {
        final List<PageElement> results = new ArrayList<>();
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8)));

            final NodeList entries = doc.getElementsByTagName("entry");
            System.out.println("asd" + entries.getLength());
            for (int i = 0; i < entries.getLength(); i++) {
                final Element entry = (Element) entries.item(i);

                // Extract Title
                final String title = entry.getElementsByTagName("title").item(0).getTextContent().trim();

                // Extract Authors (Multiple <author><name> nodes)
                final NodeList authorNodes = entry.getElementsByTagName("author");
                final List<String> authors = new ArrayList<>();
                for (int j = 0; j < authorNodes.getLength(); j++) {
                    final Element authorElement = (Element) authorNodes.item(j);
                    final String authorName = authorElement.getElementsByTagName("name").item(0).getTextContent().trim();
                    authors.add(authorName);
                }

                // Extract Summary
                final String summary = entry.getElementsByTagName("summary").item(0).getTextContent().trim();

                // Extract Published Date
                final String publishedDate = entry.getElementsByTagName("published").item(0).getTextContent().trim();

                // Extract ID (which is the full paper link)
                final String id = entry.getElementsByTagName("id").item(0).getTextContent().trim();

                // Extract arXiv Number (e.g., "2403.12345v1" from id)
                final String number = id.substring(id.lastIndexOf('/') + 1);

                // Create a TextElement with full details
                final var x = new TextElement(title, authors, summary, number, publishedDate, id, false);
                results.add(new TextElement(title, authors, summary, number, publishedDate, id, false));
                System.out.println(x);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return results;
    }


    private static List<PageElement> parseCore(final String jsonResponse) {
        final List<PageElement> results = new ArrayList<>();
        try {
            final JsonNode root = Parser.mapper.readTree(jsonResponse);
            final JsonNode articles = root.path("results");

            for (final JsonNode article : articles) {
                final String title = article.path("title").asText();
                final String id = article.path("id").asText();
                final String summary = article.path("abstract").asText(null);
                final String publishedDate = article.path("publishedDate").asText(null);
                final List<String> authors = Parser.extractAuthors(article, "authors");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parsePlos(final String jsonResponse) {
        final List<PageElement> results = new ArrayList<>();
        try {
            final JsonNode root = Parser.mapper.readTree(jsonResponse);
            final JsonNode articles = root.path("response").path("docs");

            for (final JsonNode article : articles) {
                final String title = article.path("title_display").asText();
                final String id = article.path("id").asText();
                final String summary = article.path("abstract").asText(null);
                final String publishedDate = article.path("publication_date").asText(null);
                final List<String> authors = Parser.extractAuthors(article, "author_display");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parseSemanticScholar(final String jsonResponse) {
        final List<PageElement> results = new ArrayList<>();
        try {
            final JsonNode root = Parser.mapper.readTree(jsonResponse);
            final JsonNode articles = root.path("data");

            for (final JsonNode article : articles) {
                final String title = article.path("title").asText();
                final String id = article.path("paperId").asText();
                final String summary = article.path("abstract").asText(null);
                final String publishedDate = article.path("year").asText(null);
                final List<String> authors = Parser.extractAuthors(article, "authors", "name");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parsePubMed(final String jsonResponse) {
        final List<PageElement> results = new ArrayList<>();
        try {
            final JsonNode root = Parser.mapper.readTree(jsonResponse);
            final JsonNode articles = root.path("records");

            for (final JsonNode article : articles) {
                final String title = article.path("title").asText();
                final String id = article.path("uid").asText();
                final String summary = article.path("abstract").asText(null);
                final String publishedDate = article.path("pubdate").asText(null);
                final List<String> authors = Parser.extractAuthors(article, "authors", "name");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parseOpenAlex(final String jsonResponse) {
        final List<PageElement> results = new ArrayList<>();
        try {
            final JsonNode root = Parser.mapper.readTree(jsonResponse);
            final JsonNode articles = root.path("results");

            for (final JsonNode article : articles) {
                final String title = article.path("display_name").asText();
                final String id = article.path("id").asText();
                final String summary = article.path("abstract").asText(null);
                final String publishedDate = article.path("publication_date").asText(null);
                final List<String> authors = Parser.extractAuthors(article, "authors", "display_name");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parseCrossRef(final String jsonResponse) {
        final List<PageElement> results = new ArrayList<>();
        try {
            final JsonNode root = Parser.mapper.readTree(jsonResponse);
            final JsonNode articles = root.path("message").path("items");

            for (final JsonNode article : articles) {
                final String title = article.path("title").get(0).asText();
                final String id = article.path("DOI").asText();
                final String publishedDate = article.path("published-print").path("date-parts").toString();
                final List<String> authors = Parser.extractAuthors(article, "author", "family");

                results.add(new TextElement(title, authors, null, null, publishedDate, id, false));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parseIEEE(final String jsonResponse) {
        return Parser.parseGenericAPI(jsonResponse, "articles");
    }

    private static List<PageElement> parseSpringer(final String jsonResponse) {
        return Parser.parseGenericAPI(jsonResponse, "records");
    }

    private static List<PageElement> parseScopus(final String jsonResponse) {
        return Parser.parseGenericAPI(jsonResponse, "search-results.entry");
    }

    private static List<PageElement> parseWebOfScience(final String jsonResponse) {
        return Parser.parseGenericAPI(jsonResponse, "data");
    }

    private static List<PageElement> parseGenericAPI(final String jsonResponse, final String key) {
        final List<PageElement> results = new ArrayList<>();
        try {
            final JsonNode root = Parser.mapper.readTree(jsonResponse);
            final JsonNode articles = root.path(key);

            for (final JsonNode article : articles) {
                final String title = article.path("title").asText();
                final String id = article.path("id").asText();
                final String summary = article.path("abstract").asText(null);
                final String publishedDate = article.path("publication_date").asText(null);
                final List<String> authors = Parser.extractAuthors(article, "authors", "name");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<String> extractAuthors(final JsonNode node, final String key) {
        final List<String> authors = new ArrayList<>();
        if (node.has(key) && node.get(key).isArray()) {
            for (final JsonNode author : node.get(key)) {
                authors.add(author.asText());
            }
        }
        return authors;
    }

    private static List<String> extractAuthors(final JsonNode node, final String key, final String subKey) {
        final List<String> authors = new ArrayList<>();
        if (node.has(key) && node.get(key).isArray()) {
            for (final JsonNode author : node.get(key)) {
                authors.add(author.path(subKey).asText());
            }
        }
        return authors;
    }
}
 