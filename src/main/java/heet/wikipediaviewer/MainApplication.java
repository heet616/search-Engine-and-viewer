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
public class MainApplication extends Application {
    public static void main(final String[] args) {
        Application.launch();
    }

    @Override
    public void start(final Stage stage) throws IOException {
        final MainController controller = new MainController();
        final MainView view = new MainView(controller, controller.getRecentPages(), controller.getBookmarkPages());
        controller.setView(view);
        stage.setScene(view.initialize());
        stage.getScene().getStylesheets().add("heet/wikipediaviewer/style.css");
        stage.setHeight(600);
        stage.setWidth(1000);
        stage.show();
    }
}