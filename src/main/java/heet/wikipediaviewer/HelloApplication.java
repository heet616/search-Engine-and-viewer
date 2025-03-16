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
    public void start(final Stage stage) throws IOException {
        final HelloController controller = new HelloController();
        final MainView view = new MainView(controller, controller.getRecentPages(), controller.getBookmarkPages());
        controller.setView(view);
        stage.setScene(view.initialize());
        stage.setHeight(600);
        stage.setWidth(1000);
        stage.show();
    }

    public static void main(final String[] args) {
        Application.launch();
    }
}