package heet.wikipediaviewer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;

import java.util.List;

public class TabWebpage extends Tab {

    StringProperty heading;

    List<PageElement> results;

    StringProperty searchQuery;
    Node container;

    public TabWebpage(final String s, Node container) {
        super(s);
        this.heading = new SimpleStringProperty(s);
        searchQuery = new SimpleStringProperty("");
        this.container = container;
    }

//    public SearchResults getResults() {
//    }

}
