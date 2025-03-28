package heet.researchSearchEngine.Models;

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

    public List<PageElement> results;

    StringProperty searchQuery;
    public Node container;

    Stack<Node> backward;

    Queue<Node> forward;

    public BooleanProperty backwardProperty;
    public BooleanProperty forwardProperty;

    public TabWebpage(final String s, Node container) {
        super(s);
//        historyCountProperty.bind(backward.size());
        backwardProperty = new SimpleBooleanProperty();
        backwardProperty.setValue(true);
        forwardProperty = new SimpleBooleanProperty();
        forwardProperty.setValue(true);
        this.heading = new SimpleStringProperty(s);
        searchQuery = new SimpleStringProperty("");
        this.container = container;
        backward = new Stack<>();
        forward = new ArrayDeque<>();
    }

    public Node goBack(Node curNode) {
        var x = backward.pop();
        forward.add(curNode);
        forwardProperty.set(false);
        if (0 == this.backward.size()) backwardProperty.set(true);
        return x;
    }

    public Node goForward(Node curNode) {
        var x = forward.remove();
        backward.add(curNode);
        backwardProperty.set(false);
        if (0 == this.forward.size()) forwardProperty.set(true);
        return x;
    }

    public void addBackwardNode(Node curNdoe) {
        backwardProperty.set(false);
        backward.add(curNdoe);
        forward.clear();
        forwardProperty.set(true);
    }

    public void addForwardNode(Node curNdoe) {
        forwardProperty.set(false);
        forward.add(curNdoe);
        backward.clear();
        backwardProperty.set(true);
    }

//    public SearchResults getResults() {
//    }

}
