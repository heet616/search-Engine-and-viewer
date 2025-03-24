package heet.wikipediaviewer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class TabWebpage extends Tab {

    StringProperty heading;

    List<PageElement> results;

    StringProperty searchQuery;
    Node container;

    Stack<Node> backward;

    Queue<Node> forward;

    BooleanProperty backwardProperty;
    BooleanProperty forwardProperty;

    public TabWebpage(String s, final Node container) {
        super(s);
//        historyCountProperty.bind(backward.size());
        this.backwardProperty = new SimpleBooleanProperty();
        this.backwardProperty.setValue(true);
        this.forwardProperty = new SimpleBooleanProperty();
        this.forwardProperty.setValue(true);
        heading = new SimpleStringProperty(s);
        this.searchQuery = new SimpleStringProperty("");
        this.container = container;
        this.backward = new Stack<>();
        this.forward = new ArrayDeque<>();
    }

    public Node goBack(final Node curNode) {
        final var x = this.backward.pop();
        this.forward.add(curNode);
        this.forwardProperty.set(false);
        if (0 == backward.size()) this.backwardProperty.set(true);
        return x;
    }

    public Node goForward(final Node curNode) {
        final var x = this.forward.remove();
        this.backward.add(curNode);
        this.backwardProperty.set(false);
        if (0 == forward.size()) this.forwardProperty.set(true);
        return x;
    }

    public void addBackwardNode(final Node curNdoe) {
        this.backwardProperty.set(false);
        this.backward.add(curNdoe);
    }

    public void addForwardNode(final Node curNdoe) {
        this.forwardProperty.set(false);
        this.forward.add(curNdoe);
    }

//    public SearchResults getResults() {
//    }

}
