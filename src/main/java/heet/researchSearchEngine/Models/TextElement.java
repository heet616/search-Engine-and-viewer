package heet.researchSearchEngine.Models;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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

    public Node display() {
        final VBox detailsContainer = new VBox(5); // 5px spacing between elements
        detailsContainer.setPadding(new Insets(10));

        final Label titleLabel = new Label(this.title());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        final Label authorLabel = new Label("Authors: " + (null == authors() ? "Unknown" : String.join(", ", this.authors())));
        authorLabel.setFont(Font.font("Arial", 12));

        final Label summaryLabel = new Label("Summary: " + (null == summary() ? "No summary available." : this.summary()));
        summaryLabel.setWrapText(true);

        final Label dateLabel = new Label("Published: " + (null == publishedDate() ? "N/A" : this.publishedDate()));

        detailsContainer.getChildren().addAll(titleLabel, authorLabel, summaryLabel, dateLabel);

        return detailsContainer;
    }
}
