package org.vns.javafx.dock.api;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author Valery
 */
public interface DockableTitleBar {
    Label getLabel();
    Button getCloseButton();
    Button getStateButton();
    Button getPinButton();
}
