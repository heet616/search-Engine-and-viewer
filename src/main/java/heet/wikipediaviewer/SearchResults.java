package heet.wikipediaviewer;

import javafx.beans.property.StringProperty;

import java.util.List;

public class SearchResults {
    StringProperty title;
    StringProperty author;
    StringProperty summary;

    StringProperty pages;

    StringProperty url;
    List<String> tags;

    boolean isRead;

    public String getTitle() {
        return this.title.get();
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title.set(title);
    }

    public String getAuthor() {
        return this.author.get();
    }

    public StringProperty authorProperty() {
        return this.author;
    }

    public void setAuthor(final String author) {
        this.author.set(author);
    }

    public String getSummary() {
        return this.summary.get();
    }

    public StringProperty summaryProperty() {
        return this.summary;
    }

    public void setSummary(final String summary) {
        this.summary.set(summary);
    }

    public String getPages() {
        return this.pages.get();
    }

    public StringProperty pagesProperty() {
        return this.pages;
    }

    public void setPages(final String pages) {
        this.pages.set(pages);
    }

    public String getUrl() {
        return this.url.get();
    }

    public StringProperty urlProperty() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url.set(url);
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    public boolean isRead() {
        return this.isRead;
    }

    public void setRead(final boolean read) {
        this.isRead = read;
    }

}
