package org.vns.javafx.dock.api;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 *
 * @author Valery Shyshkin
 */
public interface DockPaneTarget {//extends DockTarget{
    Region pane();
    PaneHandler paneHandler();
}
