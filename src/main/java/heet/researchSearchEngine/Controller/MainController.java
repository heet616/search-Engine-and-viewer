package heet.researchSearchEngine.Controller;

import heet.researchSearchEngine.Models.PageElement;
import heet.researchSearchEngine.Models.TabWebpage;
import heet.researchSearchEngine.Repository.ResultsCache;
import heet.researchSearchEngine.Repository.SearchSpecification;
import heet.researchSearchEngine.Utils.Parser;
import heet.researchSearchEngine.View.MainView;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    private static final Map<String, String> API_BASE_URLS = new HashMap<>();

    static {
        MainController.API_BASE_URLS.put("arXiv", "https://export.arxiv.org/api/query");
        MainController.API_BASE_URLS.put("CORE", "https://api.core.ac.uk/v3/search");
        MainController.API_BASE_URLS.put("PLOS", "https://api.plos.org/search?q=");

        // APIs requiring metadata parsing & external links
        MainController.API_BASE_URLS.put("Semantic Scholar", "https://api.semanticscholar.org/graph/v1/paper/search");
        MainController.API_BASE_URLS.put("PubMed", "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi");
        MainController.API_BASE_URLS.put("OpenAlex", "https://api.openalex.org/works");
        MainController.API_BASE_URLS.put("CrossRef", "https://api.crossref.org/works");

        // Commercial research APIs (May require API key)
        MainController.API_BASE_URLS.put("IEEE", "https://api.ieee.org/search/articles");
        MainController.API_BASE_URLS.put("Springer", "https://api.springernature.com/meta/v2/json");
        MainController.API_BASE_URLS.put("Scopus", "https://api.elsevier.com/content/search/scopus");
        MainController.API_BASE_URLS.put("Web of Science", "https://wos-api.clarivate.com/api/wos");
    }

    MainView view;
    HashMap<BooleanProperty, String> links = new HashMap<>();

    private static String formatQuery(final String apiName, final String query) {
        final String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        switch (apiName) {
            case "arXiv":
                return "?search_query=" + encodedQuery + "&start=0&max_results=10";
            case "CORE":
                return "?q=" + encodedQuery;
            case "PLOS":
                return "?q=" + encodedQuery;
            case "Semantic Scholar":
                return "?query=" + encodedQuery;
            case "PubMed":
                return "?db=pubmed&term=" + encodedQuery + "&retmode=json";
            case "OpenAlex":
                return "?filter=display_name.search:" + encodedQuery;
            case "CrossRef":
                return "?query=" + encodedQuery;
            case "IEEE":
                return "?querytext=" + encodedQuery;
            case "Springer":
                return "?q=" + encodedQuery + "&api_key=" + System.getenv("SPRINGER_API_KEY");
            case "Scopus":
                return "?query=" + encodedQuery + "&apiKey=" + System.getenv("SCOPUS_API_KEY");
            case "Web of Science":
                return "?query=" + encodedQuery + "&apiKey=" + System.getenv("WOS_API_KEY");
            default:
                return "?q=" + encodedQuery;
        }
    }

    public static List<PageElement> parseQuery(final String apiName, final String fullUrl) {
        final List<PageElement> results = new ArrayList<>();
        try {
            final URL url = new URL(fullUrl);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            final StringBuilder response = new StringBuilder();
            String line;
            while (null != (line = br.readLine())) {
                response.append(line);
            }
            br.close();

            final String jsonResponse = response.toString();
//            System.out.println(jsonResponse);
            results.addAll(Parser.parseApiResponse(apiName, jsonResponse));
            System.out.println("size " + results.size());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public void setView(final MainView view) {
        this.view = view;
    }

    public ObservableList<String> getRecentPages() {
        return FXCollections.observableArrayList();
    }

    public ObservableList<String> getBookmarkPages() {
        return FXCollections.observableArrayList();
    }

    public List<PageElement> search(String text, final TabWebpage page) {
//        url = "http://export.arxiv.org/api/query?search_query=all:" + text + "&max_results=10";
        final List<PageElement> results = new ArrayList<>();
        List<String> websites = new ArrayList<>();
        this.view.getWebsiteCheckBoxes().forEach(((s, booleanProperty) -> {
            if (booleanProperty.get())
                websites.add(s);
        }));
        System.out.println("cache size: " + ResultsCache.getResults().size());
        for (SearchSpecification s : ResultsCache.getResults().keySet()) {
            System.out.println(s.toString() + " " + ResultsCache.getResults().get(s));
        }
        if (ResultsCache.getIfPresent(text, websites) instanceof List<PageElement> list) {
            System.out.println("YESSSS");
            return list;
        }

        for (String name : websites) {
            BooleanProperty value = this.view.getWebsiteCheckBoxes().get(name);
            if (value.get()) {
                final String apiUrl = MainController.API_BASE_URLS.get(name);
                if (null != apiUrl) {
                    final String formattedQuery = apiUrl + MainController.formatQuery(name, text);
                    System.out.println(formattedQuery);
                    results.addAll(MainController.parseQuery(name, formattedQuery));
                } else {
                    System.err.println("API URL not found for: " + name);
                }
            }
        }
        ResultsCache.add(text, websites, results);
        return results;
    }
}