package org.vns.javafx.dock.api;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author Valery
 */
public interface DockableTitleBar extends DockableOwner {
    //ObjectProperty<Node> ownerProperty();
    Label getLabel();
    Button getCloseButton();
    Button getStateButton();
    Button getPinButton();
}
