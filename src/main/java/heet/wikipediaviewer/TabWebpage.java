package heet.wikipediaviewer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Tab;
import java.util.HashMap;

public class TabWebpage extends Tab {

    StringProperty heading;

    public TabWebpage(final String s) {
        super(s);
        this.heading = new SimpleStringProperty(s);
    }

    HashMap<String, BooleanProperty> filtersOptions;
    StringProperty searchQuery;
    SearchResults results;

//    public SearchResults getResults() {
//
//    }
}
