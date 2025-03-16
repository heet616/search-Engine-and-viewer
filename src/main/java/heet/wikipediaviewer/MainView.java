package heet.wikipediaviewer;

import javafx.beans.property.BooleanProperty;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainView {
    private final ObservableList<String> recentPagesList;
    private final ObservableList<String> bookmarksList;
    private TabPane currentPagesTabs;
    private final HelloController controller;


    List<String> websiteNames;
    HashMap<String, BooleanProperty> websiteCheckBoxes;

    public MainView(HelloController controller, ObservableList<String> recentPagesList, ObservableList<String> bookmarksList) {
        this.controller = controller;
        this.recentPagesList = FXCollections.observableArrayList(recentPagesList);
        this.bookmarksList = FXCollections.observableArrayList(bookmarksList);
        websiteNames = List.of(
                "arXiv",
                "PLOS",
                "CORE",
                "PubMed",
                "Semantic Scholar",
                "OpenAlex",
                "CrossRef",
                "IEEE Xplore",
                "Springer",
                "Scopus",
                "Web of Science"
        );

        websiteCheckBoxes = new HashMap<>(this.websiteNames.size());
    }

    public Scene initialize() {
        BorderPane window = new BorderPane();
        window.setTop(buildMenubar());
        window.setLeft(buildFiltersLinksSection());
        window.setCenter(buildCurrentPagesTabs());
        window.setRight(buildBookmarkRecentBuilder());

        Scene scene = new Scene(window, 1000, 600);
//        scene.getStylesheets().add("style.css"); // Attach external CSS
        return scene;
    }

    private Node buildMenubar() {
        MenuBar menu = new MenuBar();
        Menu pagesMenu = new Menu("Pages");
        MenuItem addPage = new MenuItem("Add Page");
        addPage.setOnAction(event -> currentPagesTabs.getSelectionModel().selectLast());
        pagesMenu.getItems().add(addPage);
        menu.getMenus().add(pagesMenu);
        return menu;
    }

    private Node buildFiltersLinksSection() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getStyleClass().add("sidebar");
        container.getChildren().add(createFilterOptions());
        container.getChildren().add(getStyledLabel("Structure and Links"));

        ListView<String> links = new ListView<>();
        links.setItems(FXCollections.observableArrayList("arXiv Papers", "Semantic Scholar", "IEEE Xplore", "DOAJ", "Springer"));
        container.getChildren().add(links);
        return container;
    }

    private Node createFilterOptions() {
        VBox container = new VBox(5);
        CheckBox check;
        final var iterator = this.websiteNames.iterator();
        final var checkBoxes = new ArrayList<CheckBox>();
        while (iterator.hasNext()) {
            final String name = iterator.next();
            check = new CheckBox(name);
            checkBoxes.add(check);
            this.websiteCheckBoxes.put(name, check.selectedProperty());
        }
        container.getChildren().addAll(checkBoxes);
//        container.getChildren().add(new CheckBox("DevDocs"));
//        container.getChildren().add(new CheckBox("MDN Docs"));
//        container.getChildren().add(new CheckBox("arXiv"));
//        container.getChildren().add(new CheckBox("PubMed"));
//        container.getChildren().add(new CheckBox("PapersWithCode"));
//        container.getChildren().add(new CheckBox("Hugging Face"));
        return new TitledPane("Filters", container);
    }

    private Node buildCurrentPagesTabs() {
        VBox container = new VBox();
        currentPagesTabs = new TabPane();
        VBox.setVgrow(currentPagesTabs, Priority.ALWAYS);
        container.getChildren().add(currentPagesTabs);
        Tab add = new Tab("+");
        add.setOnSelectionChanged(event -> {
            Tab newTab = createTab("New Tab");
            currentPagesTabs.getTabs().add(currentPagesTabs.getTabs().size() - 1, newTab);
            currentPagesTabs.getSelectionModel().select(newTab);
        });
        currentPagesTabs.getTabs().add(add);
        return container;
    }

    private Tab createTab(String head) {
        VBox results = new VBox();
        TabWebpage tab = new TabWebpage(head, results);
        ScrollPane scrollPane = new ScrollPane();
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        scrollPane.setContent(container);
        container.setFillWidth(true);

        Text noSearchText = new Text("Results to Search will be displayed here");
        noSearchText.setFont(Font.font(14));
        container.getChildren().add(createSearchBox(tab));
        results.getChildren().add(noSearchText);
        container.getChildren().add(results);

//        tab.setOnClosed({ta});

        tab.setContent(scrollPane);
        return tab;
    }

    private Node createSearchBox(TabWebpage page) {
        TextField searchBox = new TextField();
        searchBox.setPromptText("Enter Text...");
        searchBox.setPrefHeight(35);
        searchBox.setStyle("-fx-font-size: 14px;");

        Button searchButton = new Button("Search");
        searchButton.setPrefHeight(35);
        searchButton.setStyle("-fx-background-color: rgb(0,120,215); -fx-text-fill: rgb(255,255,255);");

        searchButton.setOnAction(event -> {
            final String text = searchBox.getText();
            page.results = controller.search(text, page);
            page.setText(text);
            updatePageResults(page);
            this.recentPagesList.add(0, text);
        });

        searchButton.disableProperty().bind(searchBox.textProperty().isEmpty());

        HBox searchContainer = new HBox(5, searchBox, searchButton);
        searchContainer.setPadding(new Insets(5));
        return searchContainer;
    }

    private void updatePageResults(TabWebpage page) {
        Pane box = ((Pane) (page.container));
        box.getChildren().clear();
        for (var r : page.results) {
            box.getChildren().add(buildResultBox(r));
        }
    }

    private Node buildResultBox(PageElement r) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgb(244,244,244); -fx-border-color: rgb(204,204,204);");

        if (r instanceof TextElement t) {
            box.getChildren().add(getStyledLabel(t.title()));
            box.getChildren().add(getStyledLabel(t.summary()));
            box.getChildren().add(getStyledLabel(t.publishedDate()));
        }

        Scale scale = new Scale(1.0, 1.0);
        box.getTransforms().add(scale);
        box.setOnMouseEntered(event -> scale.setX(1.05));
        box.setOnMouseExited(event -> scale.setX(1.0));
        return box;
    }

    private Node buildBookmarkRecentBuilder() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getChildren().add(getStyledLabel("Recent"));
        ListView<String> recentListView = new ListView<>(recentPagesList);
        container.getChildren().add(recentListView);
        container.getChildren().add(getStyledLabel("Bookmarks"));
        ListView<String> bookmarksView = new ListView<>(bookmarksList);
        container.getChildren().add(bookmarksView);
        return container;
    }

    private Label getStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(16));
        label.setTextFill(Color.DARKBLUE);
        return label;
    }
}
