package heet.wikipediaviewer;

import java.util.List;

public record TextElement(
        String title,
        List<String> authors,
        String summary,
        String number,
        String publishedDate,
        String id,

        boolean isRead
) implements PageElement {
    public TextElement(final String title, final String link) {
        this(title, null, null, null, null, link, false);
    }

    @Override
    public String toString() {
        return "TextElement{" +
                "title='" + this.title + '\'' +
                ", authors=" + this.authors +
                ", summary='" + this.summary + '\'' +
                ", number='" + this.number + '\'' +
                ", publishedDate='" + this.publishedDate + '\'' +
                ", id='" + this.id + '\'' +
                ", isRead=" + this.isRead +
                '}';
    }
}
