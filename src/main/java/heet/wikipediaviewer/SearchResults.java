package heet.wikipediaviewer;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;

public class SearchResults {
    StringProperty title;
    ListProperty<String> author;
    StringProperty summary;

    StringProperty pages;

    StringProperty publishedDate;

    StringProperty url;
    List<String> tags;

    boolean isRead;

    public SearchResults(final String title, final ObservableList<String> author, final String summary, final String pages, String publishedDate, final String url) {
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleListProperty<String>(author);
        this.summary = new SimpleStringProperty(summary);
        this.pages = new SimpleStringProperty(title);
        this.publishedDate = new SimpleStringProperty(publishedDate);
        this.url = new SimpleStringProperty(url);
    }

    public String getTitle() {
        return this.title.get();
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title.set(title);
    }

    public List<String> getAuthor() {
        return this.author.get();
    }

    public List<String> authorProperty() {
        return this.author;
    }

    public void setAuthor(final ObservableList<String> author) {
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

