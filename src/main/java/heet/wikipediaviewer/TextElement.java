package heet.wikipediaviewer;

import java.util.List;

public record TextElement(
        String title,
        List<String> authors,
        String summary,
        String number,
        String publishedDate,
        String id
) implements PageElement {
}
