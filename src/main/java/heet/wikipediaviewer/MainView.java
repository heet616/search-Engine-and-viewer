package heet.wikipediaviewer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainView {
    private final ObservableList<String> recentsList;
    private final ObservableList<String> bookmarksList;
    private final MainController controller;
    List<String> websiteNames;
    HashMap<String, BooleanProperty> websiteCheckBoxes;
    private TabPane currentPagesTabs;

    public MainView(final MainController controller, final ObservableList<String> recentsList, final ObservableList<String> bookmarksList) {
        this.controller = controller;
        this.recentsList = FXCollections.observableArrayList(recentsList);
        this.bookmarksList = FXCollections.observableArrayList(bookmarksList);
        this.websiteNames = List.of(
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

        this.websiteCheckBoxes = new HashMap<>(websiteNames.size());
    }

    public Scene initialize() {
        final BorderPane window = new BorderPane();
        window.setTop(this.buildMenubar());
        window.setLeft(this.buildFiltersLinksSection());
        window.setCenter(this.buildCurrentPagesTabs());
        window.setRight(this.bookmarkRecentBlockBuilder());

        final Scene scene = new Scene(window, 1000, 600);
//        scene.getStylesheets().add("style.css"); // Attach external CSS
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
        CheckBox check;
        var iterator = websiteNames.iterator();
        var checkBoxes = new ArrayList<CheckBox>();
        while (iterator.hasNext()) {
            String name = iterator.next();
            check = new CheckBox(name);
            checkBoxes.add(check);
            websiteCheckBoxes.put(name, check.selectedProperty());
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
        final VBox searchResults = new VBox();
        final TabWebpage tab = new TabWebpage(head, searchResults);
        final ScrollPane scrollPane = new ScrollPane();

        final Button backButton = new Button("<-");
        final Button forwardButton = new Button("->");

//        forwardButton.setBorder(Border);

        final HBox controlsBox = new HBox(backButton, forwardButton);

        final VBox tabContainer = new VBox(10);
        tabContainer.setPadding(new Insets(10));
        scrollPane.setContent(tabContainer);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tabContainer.setFillWidth(true);

        backButton.setOnAction((e) -> {
            final var cur = searchResults.getChildren().getFirst();
            searchResults.getChildren().clear();
            searchResults.getChildren().add(tab.goBack(cur));
        });

        forwardButton.setOnAction((e) -> {
            final var cur = searchResults.getChildren().getFirst();
            searchResults.getChildren().clear();
            searchResults.getChildren().add(tab.goForward(cur));
        });

//        Bindings.createIntegerBinding(tab.backward.size());

//        backButton.disableProperty().bind();
//        backButton.disableProperty().bind(Bindings.isEmpty(tab.backward));

        forwardButton.disableProperty().bindBidirectional(tab.forwardProperty);
        backButton.disableProperty().bindBidirectional(tab.backwardProperty);

        searchResults.getChildren().add(this.getStyledLabel("Results to Search will be displayed here"));
        tabContainer.getChildren().add(controlsBox);
        tabContainer.getChildren().add(this.createSearchBox(tab));
        tabContainer.getChildren().add(searchResults);


//        tab.setOnClosed({ta});

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
        searchButton.setStyle("-fx-background-color: rgb(0,120,215); -fx-text-fill: rgb(255,255,255);");

        searchButton.setOnAction(event -> {
            String text = searchBox.getText();
            page.results = this.controller.search(text, page);
            page.setText(text);
            this.updatePageResults(page);
            recentsList.add(0, text);
        });

        searchButton.disableProperty().bind(searchBox.textProperty().isEmpty());

        final HBox searchContainer = new HBox(5, searchBox, searchButton);
        searchContainer.setPadding(new Insets(5));
        return searchContainer;
    }

    private void updatePageResults(final TabWebpage page) {
        final VBox box = (VBox) (page.container);
        page.addBackwardNode(box.getChildren().getFirst());
        box.getChildren().clear();

        final VBox resultsContainer = new VBox();
        box.getChildren().add(resultsContainer);
        for (final var r : page.results) {
            final var resultElementBox = this.resultElementBuiler(r, box.maxWidthProperty());
            resultElementBox.setOnMouseClicked((e) -> {
                page.addBackwardNode(box.getChildren().getFirst());
                box.getChildren().clear();
                final VBox detailsBox = new VBox();
                detailsBox.getChildren().clear();
                detailsBox.getChildren().add(0, this.getStyledLabel(((TextElement) (resultElementBox.getUserData())).toString()));
                box.getChildren().add(detailsBox);
            });
            resultsContainer.getChildren().add(resultElementBox);
        }

    }

    private Node resultElementBuiler(final PageElement r, final DoubleProperty widthProp) {
        final ResultBox box = new ResultBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgb(244,244,244); -fx-border-color: rgb(204,204,204);");

        if (r instanceof TextElement t) {
            final var x = this.getStyledLabel(t.title());
            x.setWrapText(true);
            x.maxWidthProperty().bind(widthProp);
            box.getChildren().add(x);
            box.getChildren().add(this.getStyledLabel(t.summary()));
            box.getChildren().add(this.getStyledLabel(t.publishedDate()));
            box.setUserData(t);
        }

        final Scale scale = new Scale(1.0, 1.0);
        box.getTransforms().add(scale);
        box.setOnMouseEntered(event -> scale.setX(1.05));
        box.setOnMouseExited(event -> scale.setX(1.0));
        return box;
    }

    private Node bookmarkRecentBlockBuilder() {
        final VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getChildren().add(this.getStyledLabel("Recent"));
        container.getChildren().add(this.listViewBuilder(this.recentsList));
        container.getChildren().add(this.getStyledLabel("Bookmarks"));
        container.getChildren().add(this.listViewBuilder(this.bookmarksList));
        return container;
    }

    private Node listViewBuilder(final ObservableList<String> list) {
        return new ListView<String>(list);
    }

    private Label getStyledLabel(final String text) {
        final Label label = new Label(text);
        label.setFont(Font.font(16));
        label.setTextFill(Color.DARKBLUE);
        return label;
    }
}
