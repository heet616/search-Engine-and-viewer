package heet.wikipediaviewer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

public class MainView {
    private final ObservableList<String> recentPagesList;
    private final ObservableList<String> bookmarksList;
    private TabPane currentPagesTabs;
    private final HelloController controller;

    public MainView(final HelloController controller, final ObservableList<String> recentPagesList, final ObservableList<String> bookmarksList) {
        this.controller = controller;
        this.recentPagesList = FXCollections.observableArrayList(recentPagesList);
        this.bookmarksList = FXCollections.observableArrayList(bookmarksList);
    }

    public Scene initialize() {
        final BorderPane window = new BorderPane();
        window.setTop(this.buildMenubar());
        window.setLeft(this.buildFiltersLinksSection());
        window.setCenter(this.buildCurrentPagesTabs());
        window.setRight(this.buildBookmarkRecentBuilder());

        final Scene scene = new Scene(window, 1000, 600);
        scene.getStylesheets().add("style.css"); // Attach external CSS
        return scene;
    }

    private Node buildMenubar() {
        final MenuBar menu = new MenuBar();
        final Menu pagesMenu = new Menu("Pages");
        final MenuItem addPage = new MenuItem("Add Page");
        addPage.setOnAction(event -> this.currentPagesTabs.getSelectionModel().selectLast());
        pagesMenu.getItems().add(addPage);
        menu.getMenus().add(pagesMenu);
        return menu;
    }

    private Node buildFiltersLinksSection() {
        final VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getStyleClass().add("sidebar");
        container.getChildren().add(this.createFilterOptions());
        container.getChildren().add(this.getStyledLabel("Structure and Links"));

        final ListView<String> links = new ListView<>();
        links.setItems(FXCollections.observableArrayList("arXiv Papers", "Semantic Scholar", "IEEE Xplore", "DOAJ", "Springer"));
        container.getChildren().add(links);
        return container;
    }

    private Node createFilterOptions() {
        final VBox container = new VBox(5);
        container.getChildren().add(new CheckBox("DevDocs"));
        container.getChildren().add(new CheckBox("MDN Docs"));
        container.getChildren().add(new CheckBox("arXiv"));
        container.getChildren().add(new CheckBox("PubMed"));
        container.getChildren().add(new CheckBox("PapersWithCode"));
        container.getChildren().add(new CheckBox("Hugging Face"));
        return new TitledPane("Filters", container);
    }

    private Node buildCurrentPagesTabs() {
        final VBox container = new VBox();
        this.currentPagesTabs = new TabPane();
        VBox.setVgrow(this.currentPagesTabs, Priority.ALWAYS);
        container.getChildren().add(this.currentPagesTabs);
        final Tab add = new Tab("+");
        add.setOnSelectionChanged(event -> {
            final Tab newTab = this.createTab("New Tab");
            this.currentPagesTabs.getTabs().add(this.currentPagesTabs.getTabs().size() - 1, newTab);
            this.currentPagesTabs.getSelectionModel().select(newTab);
        });
        this.currentPagesTabs.getTabs().add(add);
        return container;
    }

    private Tab createTab(final String head) {
        final VBox results = new VBox();
        final TabWebpage tab = new TabWebpage(head, results);
        final ScrollPane scrollPane = new ScrollPane();
        final VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        scrollPane.setContent(container);
        container.setFillWidth(true);

        final Text noSearchText = new Text("Results to Search will be displayed here");
        noSearchText.setFont(Font.font(14));
        container.getChildren().add(this.createSearchBox(tab));
        results.getChildren().add(noSearchText);
        container.getChildren().add(results);

        tab.setContent(scrollPane);
        return tab;
    }

    private Node createSearchBox(final TabWebpage page) {
        final TextField searchBox = new TextField();
        searchBox.setPromptText("Enter Text...");
        searchBox.setPrefHeight(35);
        searchBox.setStyle("-fx-font-size: 14px;");

        final Button searchButton = new Button("Search");
        searchButton.setPrefHeight(35);
        searchButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white;");

        searchButton.setOnAction(event -> {
            page.results = this.controller.search(searchBox.getText());
            page.setText(searchBox.getText());
            this.updatePageResults(page);
        });

        final HBox searchContainer = new HBox(5, searchBox, searchButton);
        searchContainer.setPadding(new Insets(5));
        return searchContainer;
    }

    private void updatePageResults(final TabWebpage page) {
        final Pane box = ((Pane) (page.container));
        box.getChildren().clear();
        for (final var r : page.results) {
            box.getChildren().add(this.buildResultBox(r));
        }
    }

    private Node buildResultBox(final PageElement r) {
        final VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc;");

        if (r instanceof final TextElement t) {
            box.getChildren().add(this.getStyledLabel(t.title()));
            box.getChildren().add(this.getStyledLabel(t.summary()));
            box.getChildren().add(this.getStyledLabel(t.publishedDate()));
        }

        final Scale scale = new Scale(1.0, 1.0);
        box.getTransforms().add(scale);
        box.setOnMouseEntered(event -> scale.setX(1.05));
        box.setOnMouseExited(event -> scale.setX(1.0));
        return box;
    }

    private Node buildBookmarkRecentBuilder() {
        final VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getChildren().add(this.getStyledLabel("Recent"));
        final ListView<String> recentListView = new ListView<>(this.recentPagesList);
        container.getChildren().add(recentListView);
        container.getChildren().add(this.getStyledLabel("Bookmarks"));
        final ListView<String> bookmarksView = new ListView<>(this.bookmarksList);
        container.getChildren().add(bookmarksView);
        return container;
    }

    private Label getStyledLabel(final String text) {
        final Label label = new Label(text);
        label.setFont(Font.font(16));
        label.setTextFill(Color.DARKBLUE);
        return label;
    }
}
