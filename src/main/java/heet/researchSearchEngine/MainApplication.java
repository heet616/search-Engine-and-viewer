package heet.researchSearchEngine;

import heet.researchSearchEngine.Controller.MainController;
import heet.researchSearchEngine.View.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/*
WORKS:
    arXiv
    Semantic Scholar
    OpenAlex
    CrossRef
 */
public class MainApplication extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        MainController controller = new MainController();
        MainView view = new MainView(controller, controller.getRecentPages(), controller.getBookmarkPages());
        controller.setView(view);
        stage.setScene(view.initialize());
        stage.getScene().getStylesheets().add("heet/researchSearchEngine/style.css");
        stage.setHeight(600);
        stage.setWidth(1000);
        stage.show();
    }
}