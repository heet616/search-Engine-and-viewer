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
    public TextElement(String title, String link) {
        this(title, null, null, null, null, link, false);
    }

    @Override
    public String toString() {
        return "TextElement{" +
                "title='" + title + '\'' +
                ", authors=" + authors +
                ", summary='" + summary + '\'' +
                ", number='" + number + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", id='" + id + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
