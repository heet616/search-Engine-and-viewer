package heet.wikipediaviewer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class MainView {
    private final ObservableList<String> recentPagesList;
    private final ObservableList<String> bookmarksList;
    TabPane currentPagesTabs;

    StringProperty searchboxproperty;

    private final ObservableList<Tab> currentPagesList = FXCollections.observableArrayList();

    private final HelloController controller;

    public MainView(HelloController controller, final ObservableList<String> recentPagesList, ObservableList<String> bookmarksList) {
        this.controller = controller;
        this.recentPagesList = recentPagesList;
        this.bookmarksList = bookmarksList;
    }

    private void addCurrentPagesTab(ListChangeListener.Change<? extends Tab> change) {
        currentPagesTabs.getTabs().addAll(change.getAddedSubList());
    }

    public Scene initialize() {
        BorderPane window = new BorderPane();
        window.setTop(buildMenubar());
        window.setLeft((buildFiltersLinksSection()));
        window.setCenter(buildCurrentPagesTabs());
        window.setRight(buildBookmarkRecentBuilder());
        Scene scene = new Scene(window);
        return scene;
    }

    private Node buildMenubar() {
        MenuBar menu = new MenuBar();
        Menu pagesMenu = new Menu("pages");
        MenuItem addPage = new MenuItem("add Page");
        addPage.setOnAction(event -> this.currentPagesTabs.getSelectionModel().selectLast());
        pagesMenu.getItems().add(addPage);
        menu.getMenus().add(pagesMenu);

        return menu;
    }


    public Node buildFiltersLinksSection() {
        VBox container = new VBox();
        container.setSpacing(10); // Add spacing
        container.getChildren().add(createFilterOptions());

        container.getChildren().add(getLabel("Structure and Links"));

        ListView<String> links = new ListView<>();
        links.setItems(FXCollections.observableArrayList(
                "arXiv Papers", "Semantic Scholar", "IEEE Xplore", "DOAJ", "Springer"
        ));
        container.getChildren().add(links);

        return container;
    }

    private Node createFilterOptions() {
        ArrayList<Pair<String, BooleanProperty>> filterOptions = new ArrayList<>();
        TitledPane filtersPane = new TitledPane("Filters", new VBox(
                createFilterSection("Documentation", filterOptions, new CheckBox("DevDocs"), new CheckBox("MDN Docs")),
                createFilterSection("Research Papers", filterOptions, new CheckBox("arXiv"), new CheckBox("PubMed")),
                this.createFilterSection("Datasets", filterOptions, new CheckBox("PapersWithCode"), new CheckBox("Hugging Face"))
        ));
        return filtersPane;
    }

    private TitledPane createFilterSection(String title, ArrayList<Pair<String, BooleanProperty>> filterOptions, CheckBox... options) {
        for(CheckBox box: options){
            filterOptions.add(new Pair<>(box.getText(), box.selectedProperty()));
        }
        VBox container = new VBox(options);
        container.setSpacing(5);
        return new TitledPane(title, container);
    }

    private Node buildCurrentPagesTabs() {
        VBox container = new VBox();

        currentPagesTabs = new TabPane();
        VBox.setVgrow(currentPagesTabs, Priority.ALWAYS);
        container.getChildren().add(currentPagesTabs);
        Tab add = new Tab("+");
        add.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                int i = currentPagesTabs.getTabs().size() - 1;
                Tab s = createTab("new Tab");
                currentPagesTabs.getTabs().add(currentPagesTabs.getTabs().size() - 1, s);
                currentPagesTabs.getSelectionModel().select(i);
            }
        });
        currentPagesTabs.getTabs().add(add);

        return container;
    }

    private Tab createTab(String head) {
        TabWebpage t = new TabWebpage(head);
        VBox container = new VBox();
        Text results = new Text();
        results.setText("Results to Search will be displayed here");

        container.getChildren().add(createSearchBox());
        container.getChildren().add(results);

        t.setContent(container);
        return t;
    }

    Node createSearchBox(){
        TextField searchBox = new TextField();
        searchBox.setPromptText("Search Wikipedia");
        searchboxproperty = searchBox.textProperty();
        HBox searchContainer = new HBox();

        searchContainer.getChildren().add(searchBox);
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(40);
        searchContainer.getChildren().add(searchButton);
        TitledPane searchPane = new TitledPane("Search", searchContainer);
        Accordion accontainer = new Accordion(searchPane);
        accontainer.setExpandedPane(searchPane);
        return accontainer;
    }

    private Node buildBookmarkRecentBuilder() {
        VBox container = new VBox();
        container.getChildren().add(getLabel("Recent"));
        //TODO: make String to Page
        ListView<String> recentListView = new ListView<>();
        recentListView.setItems(recentPagesList);
        container.getChildren().add(recentListView);
        container.getChildren().add(getLabel("Bookmarks"));
        ListView<String> bookmarksView = new ListView<>();
        bookmarksView.setItems(bookmarksList);
        container.getChildren().add(bookmarksView);
        return container;
    }

    private Label getLabel(String text) {
        return new Label(text);
    }
}
