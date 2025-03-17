package heet.wikipediaviewer;

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
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        HelloController controller = new HelloController();
        MainView view = new MainView(controller, controller.getRecentPages(), controller.getBookmarkPages());
        controller.setView(view);
        stage.setScene(view.initialize());
        stage.getScene().getStylesheets().add("heet/wikipediaviewer/style.css");
        stage.setHeight(600);
        stage.setWidth(1000);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}