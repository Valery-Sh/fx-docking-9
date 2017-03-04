package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.dock.api.editor.SceneGraphEditor.ANCHOR_OFFSET;

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

    public boolean isAcceptable(TreeItem<ItemValue> target, Object obj) {
        return isAcceptable(obj);
    }

    public boolean isDragTarget() {
        return false;
    }

    public TreeItemEx accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        return null;
    }

    public void childrenTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
    }

    protected void notifyTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        TreeItem<ItemValue> parent = toRemove.getParent();
        if (parent != null) {
            Object obj = ((ItemValue) parent.getValue()).getTreeItemObject();
            TreeItemRegistry.getInstance().getBuilder(obj).childrenTreeItemRemove(treeView, toRemove);
        }
    }

    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof Node) {
            retval = createItem(obj);
        }
        return retval;
    }

    protected TreeItemEx createItem(Object obj) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        //anchorPane.setStyle(getStyle());
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        TreeItemEx item = new TreeItemEx();
        ItemValue itv = new ItemValue(item);

        itv.setTreeItemObject(obj);
        itv.setCellGraphic(anchorPane);
        
        item.setValue(itv);
        box.getChildren().add(createItemContent(obj));
        
        return item;
    }

    public static TreeItem<ItemValue> findTreeItem(TreeItem<ItemValue> item, Object obj) {
        TreeItem<ItemValue> retval = null;
        TreeItem<ItemValue> root = null;
        TreeItem<ItemValue> parent = item;

        while (parent != null) {
            root = parent;
            parent = parent.getParent();
        }
        /*        while (false) {
            for (int i = 0; i < root.getChildren().size(); i++) {
                TreeItem<ItemValue> it = root.getChildren().get(i);
                if (it.getValue().getTreeItemObject() == obj) {
                    retval = it;
                    break;
                }
            }
            
        }
         */
        return retval;
    }

    public String getStyle() {
        return "-fx-backGround-color: aqua";
    }

    protected Node createItemContent(Object obj) {
        return createDefaultContent(obj);
    }


    protected Node createDefaultContent(Object obj) {
        String text = "";
        if (obj != null && (obj instanceof Labeled)) {
            text = ((Labeled) obj).getText();
        }
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }

    public String getText() {
        return "";
    }

    public Node getGraphic() {
        return null;
    }

    public TreeItem[] createPlaceHolders(Object obj) {
        return new TreeItem[0];
    }

    public boolean hasPlaceHolders() {
        return false;
    }

    public TreeItemBuilder getPlaceHolderBuilder(TreeItem placeHolder) {
        return null;
    }
}// TreeItemBuilder
