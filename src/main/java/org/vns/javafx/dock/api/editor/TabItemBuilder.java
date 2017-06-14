package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

/**
 *
 * @author Valery
 */
public class TabItemBuilder extends AbstractContentBasedTreeItemBuilder<Node> {

    public TabItemBuilder() {
    }



    @Override
    protected HBox createDefaultContent(Object obj) {
        String text = ((Tab) obj).getText();
        text = text == null ? "" : text;
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return new HBox(label);
    }

    /**
     * Checks whether the specified object can be used as a value of the graphic
     * property. May accepts only objects of type {@literal Node} witch becomes
     * a value of the graphic property. of the
     * @param target ???
     * @param accepting an object to be checked
     * @return true if the parameter value is not null and is an instance of
     * Node/
     */
    @Override
    public boolean isAcceptable(Object target, Object accepting) {
        return accepting != null && (accepting instanceof Node);
    }

}
