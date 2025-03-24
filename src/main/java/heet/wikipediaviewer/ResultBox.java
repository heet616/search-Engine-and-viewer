package heet.wikipediaviewer;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ResultBox extends VBox {
    public ResultBox() {
    }

    public ResultBox(double v) {
        super(v);
    }

    public ResultBox(Node... nodes) {
        super(nodes);
    }

    public ResultBox(double v, Node... nodes) {
        super(v, nodes);
    }
}
