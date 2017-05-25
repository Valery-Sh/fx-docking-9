package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author Valery
 */
public class DefaultTreeItemBuilder  implements TreeItemBuilder {

    public DefaultTreeItemBuilder() {
        init();
    }

    private void init() {
    }

    public boolean isAcceptable(Object obj) {
        return false;
    }

    @Override
    public TreeItem build(Object obj) {
        if ( obj instanceof ComboBox ) {
            System.err.println("COMBO BOX BUILD");
        }
        TreeItem retval = null;
        if (obj instanceof Node) {
            retval = createItem(obj);
        }
        return retval;
    }
    protected HBox getItemContentPane(TreeItem<ItemValue> item) {
        return (HBox) ((AnchorPane) item.getValue().getCellGraphic()).getChildren().get(0);
    }

    @Override
    public Node createItemContent(Object obj, Object... others) {
        return createDefaultContent(obj, others);
    }

    protected Node createDefaultContent(Object obj, Object... others) {
        String text = "";
        if (obj != null && (obj instanceof Labeled)) {
            text = ((Labeled) obj).getText();
        }
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }


 
}// DefaultTreeItemBuilder
