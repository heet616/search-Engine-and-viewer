package heet.researchSearchEngine.Repository;

import heet.researchSearchEngine.Models.PageElement;

import java.util.HashMap;
import java.util.List;

public enum ResultsCache {
    ;
    static HashMap<SearchSpecification, List<PageElement>> results = new HashMap<>();

    public static List<PageElement> getIfPresent(String search, List<String> websites) {
        var spec = new SearchSpecification(websites, search);
        if (results.containsKey(spec)) {
            return results.get(spec);
        }
        return null;
    }

    public static void add(String search, List<String> websites, List<PageElement> node) {
        results.put(new SearchSpecification(websites, search), node);
    }

    public static HashMap<SearchSpecification, List<PageElement>> getResults() {
        return results;
    }
}

