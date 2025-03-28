package heet.researchSearchEngine.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import heet.researchSearchEngine.Models.PageElement;
import heet.researchSearchEngine.Models.TextElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Parser {
    ;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<PageElement> parseApiResponse(String apiName, String jsonResponse) {
        return switch (apiName) {
            case "arXiv" -> parseArxiv(jsonResponse);
            case "CORE" -> parseCore(jsonResponse);
            case "PLOS" -> parsePlos(jsonResponse);
            case "Semantic Scholar" -> parseSemanticScholar(jsonResponse);
            case "PubMed" -> parsePubMed(jsonResponse);
            case "OpenAlex" -> parseOpenAlex(jsonResponse);
            case "CrossRef" -> parseCrossRef(jsonResponse);
            case "IEEE" -> parseIEEE(jsonResponse);
            case "Springer" -> parseSpringer(jsonResponse);
            case "Scopus" -> parseScopus(jsonResponse);
            case "Web of Science" -> parseWebOfScience(jsonResponse);
            default -> new ArrayList<>();
        };
    }

    private static List<PageElement> parseArxiv(String xmlResponse) {
        List<PageElement> results = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8)));

            NodeList entries = doc.getElementsByTagName("entry");
            System.out.println("asd" + entries.getLength());
            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element) entries.item(i);

                // Extract Title
                String title = entry.getElementsByTagName("title").item(0).getTextContent().trim();

                // Extract Authors (Multiple <author><name> nodes)
                NodeList authorNodes = entry.getElementsByTagName("author");
                List<String> authors = new ArrayList<>();
                for (int j = 0; j < authorNodes.getLength(); j++) {
                    Element authorElement = (Element) authorNodes.item(j);
                    String authorName = authorElement.getElementsByTagName("name").item(0).getTextContent().trim();
                    authors.add(authorName);
                }

                // Extract Summary
                String summary = entry.getElementsByTagName("summary").item(0).getTextContent().trim();

                // Extract Published Date
                String publishedDate = entry.getElementsByTagName("published").item(0).getTextContent().trim();

                // Extract ID (which is the full paper link)
                String id = entry.getElementsByTagName("id").item(0).getTextContent().trim();

                // Extract arXiv Number (e.g., "2403.12345v1" from id)
                String number = id.substring(id.lastIndexOf('/') + 1);

                // Create a TextElement with full details
                var x = new TextElement(title, authors, summary, number, publishedDate, id, false);
                results.add(new TextElement(title, authors, summary, number, publishedDate, id, false));
                System.out.println(x);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }


    private static List<PageElement> parseCore(String jsonResponse) {
        List<PageElement> results = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode articles = root.path("results");

            for (JsonNode article : articles) {
                String title = article.path("title").asText();
                String id = article.path("id").asText();
                String summary = article.path("abstract").asText(null);
                String publishedDate = article.path("publishedDate").asText(null);
                List<String> authors = extractAuthors(article, "authors");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parsePlos(String jsonResponse) {
        List<PageElement> results = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode articles = root.path("response").path("docs");

            for (JsonNode article : articles) {
                String title = article.path("title_display").asText();
                String id = article.path("id").asText();
                String summary = article.path("abstract").asText(null);
                String publishedDate = article.path("publication_date").asText(null);
                List<String> authors = extractAuthors(article, "author_display");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parseSemanticScholar(final String jsonResponse) {
        List<PageElement> results = new ArrayList<>();
        final List<String> ids = new ArrayList<>();

        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode articles = root.path("data");

            for (JsonNode article : articles) {
                ids.add(article.path("paperId").asText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ids.isEmpty()) {
            return results;
        }

        try {
            // API Call for Batch Paper Data
            final String fullUrl = "https://api.semanticscholar.org/graph/v1/paper/batch";
            final ObjectMapper mapper = new ObjectMapper();
            HashMap<String, List<String>> jsonMap = new HashMap<>();
            jsonMap.put("ids", ids);
            final String jsonInputString = mapper.writeValueAsString(jsonMap);

            System.out.println("Request: " + jsonInputString);

            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (final OutputStream os = conn.getOutputStream()) {
                final byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            final JsonNode root = mapper.readTree(conn.getInputStream());
            return root.isArray() ? Parser.parseSemPostResponse(root) : List.of();
        } catch (final Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }

    private static List<PageElement> parseSemPostResponse(final JsonNode root) {
        final List<PageElement> results = new ArrayList<>();

        for (final JsonNode paper : root) {  // Iterate over each JSON object
            final String id = paper.path("paperId").asText();
            final String title = paper.path("title").asText();
            final String summary = paper.path("abstract").asText(null);
            final String publishedDate = paper.path("year").asText(null);
            final String link = paper.path("url").asText(null);

            // Get Open Access PDF link
            String downloadLink = null;
            if (paper.has("openAccessPdf")) {
                downloadLink = paper.path("openAccessPdf").path("url").asText(null);
            }

            // If no direct PDF, check DOI
            if (null == downloadLink && paper.has("externalIds")) {
                final String doi = paper.path("externalIds").path("DOI").asText(null);
                if (null != doi) {
                    downloadLink = "https://doi.org/" + doi;
                }
            }

            final List<String> authors = extractAuthors(paper, "authors", "name");
            var t = new TextElement(title, authors, summary, id, publishedDate, id, false);
            System.out.println(t);
            results.add(t);
        }

        return results;
    }


    private static List<PageElement> parsePubMed(String jsonResponse) {
        List<PageElement> results = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode articles = root.path("records");

            for (JsonNode article : articles) {
                String title = article.path("title").asText();
                String id = article.path("uid").asText();
                String summary = article.path("abstract").asText(null);
                String publishedDate = article.path("pubdate").asText(null);
                List<String> authors = extractAuthors(article, "authors", "name");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parseOpenAlex(String jsonResponse) {
        List<PageElement> results = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode articles = root.path("results");

            for (JsonNode article : articles) {
                String title = article.path("display_name").asText();
                String id = article.path("id").asText();
                String summary = article.path("abstract").asText(null);
                String publishedDate = article.path("publication_date").asText(null);
                List<String> authors = extractAuthors(article, "authors", "display_name");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parseCrossRef(String jsonResponse) {
        List<PageElement> results = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode articles = root.path("message").path("items");

            for (JsonNode article : articles) {
                String title = article.path("title").get(0).asText();
                String id = article.path("DOI").asText();
                String publishedDate = article.path("published-print").path("date-parts").toString();
                List<String> authors = extractAuthors(article, "author", "family");

                results.add(new TextElement(title, authors, null, null, publishedDate, id, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<PageElement> parseIEEE(String jsonResponse) {
        return parseGenericAPI(jsonResponse, "articles");
    }

    private static List<PageElement> parseSpringer(String jsonResponse) {
        return parseGenericAPI(jsonResponse, "records");
    }

    private static List<PageElement> parseScopus(String jsonResponse) {
        return parseGenericAPI(jsonResponse, "search-results.entry");
    }

    private static List<PageElement> parseWebOfScience(String jsonResponse) {
        return parseGenericAPI(jsonResponse, "data");
    }

    private static List<PageElement> parseGenericAPI(String jsonResponse, String key) {
        List<PageElement> results = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode articles = root.path(key);

            for (JsonNode article : articles) {
                String title = article.path("title").asText();
                String id = article.path("id").asText();
                String summary = article.path("abstract").asText(null);
                String publishedDate = article.path("publication_date").asText(null);
                List<String> authors = extractAuthors(article, "authors", "name");

                results.add(new TextElement(title, authors, summary, null, publishedDate, id, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<String> extractAuthors(JsonNode node, String key) {
        List<String> authors = new ArrayList<>();
        if (node.has(key) && node.get(key).isArray()) {
            for (JsonNode author : node.get(key)) {
                authors.add(author.asText());
            }
        }
        return authors;
    }

    private static List<String> extractAuthors(JsonNode node, String key, String subKey) {
        List<String> authors = new ArrayList<>();
        if (node.has(key) && node.get(key).isArray()) {
            for (JsonNode author : node.get(key)) {
                authors.add(author.path(subKey).asText());
            }
        }
        return authors;
    }
}
 