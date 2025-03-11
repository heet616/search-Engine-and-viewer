package heet.wikipediaviewer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;

import java.util.HashMap;
import java.util.List;

public class TabWebpage extends Tab {

    StringProperty heading;

    List<PageElement> results;

    HashMap<String, BooleanProperty> filtersOptions;
    StringProperty searchQuery;
    Node container;

    public TabWebpage(String s, final Node container) {
        super(s);
        heading = new SimpleStringProperty(s);
        this.searchQuery = new SimpleStringProperty("");
        this.container = container;
    }

//    public SearchResults getResults() {
//
//    }
}
