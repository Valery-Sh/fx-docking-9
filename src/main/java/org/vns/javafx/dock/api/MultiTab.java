package org.vns.javafx.dock.api;

import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 *
 * @author Valery
 */
public interface MultiTab {
    ObservableList<Node> getTitleBars();
    ObservableList<Node> getContents();
    void remove(Dockable child);
    
}
