package org.vns.javafx.dock.api;

import javafx.scene.Node;

/**
 *
 * @author Valery
 */
public class DefaultDockable extends DockNodeBase {
    private Node node;
    public DefaultDockable(Node node) {
        super();
    }

    public Node getNode() {
        return node;
    }
    
}
