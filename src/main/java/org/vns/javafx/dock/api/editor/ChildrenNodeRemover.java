package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;

/**
 *
 * @author Valery
 */
@FunctionalInterface
public interface ChildrenNodeRemover {
    boolean remove(Node node);
}
