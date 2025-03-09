package heet.wikipediaviewer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

    MainView view;

    public void setView(final MainView view) {
        this.view = view;
    }

    public ObservableList<String> getRecentPages() {
        ObservableList<String> recents = FXCollections.observableArrayList();
        return recents;
    }

    public ObservableList<String> getBookmarkPages() {
        ObservableList<String> bookmarks = FXCollections.observableArrayList();
        return bookmarks;
    }

    private void addPageTab(){
    }



}