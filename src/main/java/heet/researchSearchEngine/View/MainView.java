package heet.researchSearchEngine.View;

import heet.researchSearchEngine.Controller.MainController;
import heet.researchSearchEngine.Models.PageElement;
import heet.researchSearchEngine.Models.ResultBox;
import heet.researchSearchEngine.Models.TabWebpage;
import heet.researchSearchEngine.Models.TextElement;
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

    public ObservableList<String> getRecentsList() {
        return recentsList;
    }

    public ObservableList<String> getBookmarksList() {
        return bookmarksList;
    }

    public List<String> getWebsiteNames() {
        return websiteNames;
    }

    public HashMap<String, BooleanProperty> getWebsiteCheckBoxes() {
        return websiteCheckBoxes;
    }

    private final MainController controller;
    List<String> websiteNames;
    HashMap<String, BooleanProperty> websiteCheckBoxes;
    private TabPane currentPagesTabs;

    public MainView(MainController controller, ObservableList<String> recentsList, ObservableList<String> bookmarksList) {
        this.controller = controller;
        this.recentsList = FXCollections.observableArrayList(recentsList);
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
        window.setRight(bookmarkRecentBlockBuilder());

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
        VBox searchResults = new VBox();
        TabWebpage tab = new TabWebpage(head, searchResults);
        ScrollPane scrollPane = new ScrollPane();

        VBox tabContainer = new VBox(10);
        tabContainer.setPadding(new Insets(10));
        scrollPane.setContent(tabContainer);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        tabContainer.setFillWidth(true);

        searchResults.getChildren().add(getStyledLabel("Results to Search will be displayed here"));
        tabContainer.getChildren().add(createSearchBox(tab, searchResults));
        tabContainer.getChildren().add(searchResults);

//        tab.setOnClosed({ta});

        tab.setContent(scrollPane);
        return tab;
    }

    private Node createSearchBox(TabWebpage page, final VBox searchResults) {

        Button backButton = new Button("<-");
        Button forwardButton = new Button("->");

        HBox controlsBox = new HBox(backButton, forwardButton);

        TextField searchBox = new TextField();
        searchBox.setPromptText("Enter Text...");
        searchBox.setPrefHeight(35);
        searchBox.setStyle("-fx-font-size: 14px;");
        page.textProperty().bind(searchBox.textProperty());

        backButton.setOnAction((e) -> {
            var cur = searchResults.getChildren().getFirst();
            searchResults.getChildren().clear();
            final var newNode = page.goBack(cur);
            searchResults.getChildren().add(newNode);
            searchBox.setText((String) newNode.getUserData());
        });

        forwardButton.setOnAction((e) -> {
            var cur = searchResults.getChildren().getFirst();
            searchResults.getChildren().clear();
            final var newNode = page.goForward(cur);
            searchResults.getChildren().add(newNode);
            searchBox.setText((String) newNode.getUserData());
        });

        forwardButton.disableProperty().bindBidirectional(page.forwardProperty);
        backButton.disableProperty().bindBidirectional(page.backwardProperty);


        Button searchButton = new Button("Search");
        searchButton.setPrefHeight(35);
        searchButton.setStyle("-fx-background-color: rgb(0,120,215); -fx-text-fill: rgb(255,255,255);");

        searchButton.setOnAction(event -> {
            final String text = searchBox.getText();
            page.results = controller.search(text, page);
            updatePageResults(page, searchBox.getText());
            this.recentsList.add(0, text);
        });

        searchButton.disableProperty().bind(searchBox.textProperty().isEmpty());

        final VBox container = new VBox();

        final HBox controlsContainer = new HBox();
        controlsContainer.getChildren().addAll(backButton, forwardButton);

        HBox searchContainer = new HBox(5, searchBox, searchButton);
        searchContainer.setPadding(new Insets(5));

        container.getChildren().addAll(controlsContainer, searchContainer);
        return container;
    }

    private void updatePageResults(TabWebpage page, final String query) {
        VBox box = (VBox) (page.container);
        page.addBackwardNode(box.getChildren().getFirst());
        box.getChildren().clear();

        VBox resultsContainer = new VBox();
        box.getChildren().add(resultsContainer);
        resultsContainer.setUserData(query);
        for (var r : page.results) {
            var resultElementBox = resultElementBuiler(r, box.maxWidthProperty());
            resultElementBox.setOnMouseClicked((e) -> {
                page.addBackwardNode(box.getChildren().getFirst());
                box.getChildren().clear();
                VBox detailsBox = new VBox();
                detailsBox.getChildren().clear();
                detailsBox.getChildren().add(0, ((TextElement) (resultElementBox.getUserData())).display());
                detailsBox.setUserData(query);
                box.getChildren().add(detailsBox);
            });
            resultsContainer.getChildren().add(resultElementBox);
        }

    }

    private Node resultElementBuiler(PageElement r, DoubleProperty widthProp) {
        ResultBox box = new ResultBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgb(244,244,244); -fx-border-color: rgb(204,204,204);");

        if (r instanceof final TextElement t) {
            var x = getStyledLabel(t.title());
            x.setWrapText(true);
            x.maxWidthProperty().bind(widthProp);
            box.getChildren().add(x);
            box.getChildren().add(getStyledLabel(t.summary()));
            box.getChildren().add(getStyledLabel(t.publishedDate()));
            box.setUserData(t);
        }

        Scale scale = new Scale(1.0, 1.0);
        box.getTransforms().add(scale);
        box.setOnMouseEntered(event -> scale.setX(1.05));
        box.setOnMouseExited(event -> scale.setX(1.0));
        return box;
    }

    private Node bookmarkRecentBlockBuilder() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getChildren().add(getStyledLabel("Recent"));
        final var recentsListView = listViewBuilder(recentsList);
        final List<Integer> lis = recentsListView.getSelectionModel().getSelectedIndices();
//        lis.get(0)
        container.getChildren().add(recentsListView);
        container.getChildren().add(getStyledLabel("Bookmarks"));
        container.getChildren().add(listViewBuilder(bookmarksList));
        return container;
    }

    private ListView<String> listViewBuilder(ObservableList<String> list) {
        return new ListView<String>(list);
    }

    private Label getStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(16));
        label.setTextFill(Color.DARKBLUE);
        return label;
    }
}
