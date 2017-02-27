package org.vns.javafx.dock.api.editor.tmp;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.dock.api.editor.tmp.EditorTreeView.ANCHOR_OFFSET;

/**
 *
 * @author Valery
 */
public class TreeItemBuilder {
    
    public static final String CELL_UUID = "uuid-29a4b479-0282-41f1-8ac8-21b4923235be";
    public static final String NODE_UUID = "uuid-f53db037-2e33-4c68-8ffa-06044fc10f81";
    
    public TreeItemBuilder() {
        init();
    }

    private void init() {
    }

    public boolean isAcceptable(Object obj) {
        return false;
    }
    public boolean isDragTarget() {
        return false;
    }

    public TreeItemEx accept(TreeItemEx parent, Object obj) {
        return null;
    }

    public TreeItemEx release(TreeItemEx parent, Object obj) {
        return null;
    }

    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof Node) {
            Node node = (Node) obj;
            retval = createItem((Node) obj);
        }
        return retval;
    }

    protected TreeItemEx<AnchorPane> createItem(Node node) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        anchorPane.setStyle(getStyle());
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        TreeItemEx item = new TreeItemEx(anchorPane);
        item.setNode(node);
        box.getChildren().add(createItemContent(node));
        return item;
    }


    public String getStyle() {
        return "-fx-backGround-color: aqua";
    }

    protected Node createItemContent(Node node) {
        return createDefaultContent(node);
    }

    protected Node createDefaultContent(Node node) {
        Label label = new Label(node.getClass().getSimpleName() + " " + getText());
        if ( getGraphic() != null  ) {
            label.setGraphic(getGraphic());
        }
        label.setStyle(getStyle());        
        return label;
    }

    public String getText() {
        return "";
    }

    public Node getGraphic() {
        return null;
    }
    
}// TreeItemBuilder
