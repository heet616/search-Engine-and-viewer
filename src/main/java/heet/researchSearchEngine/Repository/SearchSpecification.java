package heet.researchSearchEngine.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SearchSpecification {
    List<String> websites;
    String search;

    public SearchSpecification(List<String> websites, String search) {
        this.search = search;
        this.websites = websites;
        websites.sort(Comparator.naturalOrder());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchSpecification that)) return false;
        return websites.equals(that.websites) && Objects.equals(this.search, that.search);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.websites, this.search);
    }
}
