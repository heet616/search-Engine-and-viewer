package heet.researchSearchEngine.Models;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ResultBox extends VBox {
    public ResultBox() {
    }

    public ResultBox(final double v) {
        super(v);
    }

    public ResultBox(final Node... nodes) {
        super(nodes);
    }

    public ResultBox(final double v, final Node... nodes) {
        super(v, nodes);
    }
}
