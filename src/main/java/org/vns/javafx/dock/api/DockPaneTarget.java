package org.vns.javafx.dock.api;

import javafx.scene.layout.Pane;

/**
 *
 * @author Valery Shyshkin
 */
public interface DockPaneTarget {//extends DockTarget{
    Pane pane();
    DockPaneHandler paneHandler();
}
