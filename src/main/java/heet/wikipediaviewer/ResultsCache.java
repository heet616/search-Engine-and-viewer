package heet.wikipediaviewer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public enum ResultsCache {
    ;
    static HashMap<SearchSpecification, List<PageElement>> results = new HashMap<>();

    static List<PageElement> getIfPresent(final String search, final List<String> websites) {
        final var spec = new SearchSpecification(websites, search);
        if (ResultsCache.results.containsKey(spec)) {
            return ResultsCache.results.get(spec);
        }
        return null;
    }

    static void add(final String search, final List<String> websites, final List<PageElement> node) {
        ResultsCache.results.put(new SearchSpecification(websites, search), node);
    }
}

class SearchSpecification {
    List<String> websites;
    String search;

    public SearchSpecification(final List<String> websites, final String search) {
        this.search = search;
        this.websites = websites;
        websites.sort(Comparator.naturalOrder());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final SearchSpecification that)) return false;
        return this.websites.equals(that.websites) && Objects.equals(search, that.search);
    }

    @Override
    public int hashCode() {
        return Objects.hash(websites, search);
    }
}

