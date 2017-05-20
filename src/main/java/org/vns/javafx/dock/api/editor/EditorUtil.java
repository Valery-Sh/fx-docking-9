package org.vns.javafx.dock.api.editor;

import javafx.geometry.Bounds;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Valery
 */
public class EditorUtil {
    
    public static final String GESTURE_SOURCE_KEY = "drag-gesture-source-key";

    protected static TreeItem parentOfLevel(TreeView treeView, TreeItem item, int level) {
        TreeItem it = item;
        while (it != null) {
            if (treeView.getTreeItemLevel(it) == level) {
                break;
            }
            it = it.getParent();
        }
        return it;
    }
    
    public static TreeCell getCell(TreeItem<ItemValue> item) {
        //AnchorPane ap = (AnchorPane) item.getValue().getCellGraphic();
        return (TreeCell) ((AnchorPane) item.getValue().getCellGraphic()).getParent();
    }

    public static Bounds screenTreeViewBounds(TreeView treeView) {
        return treeView.localToScreen(treeView.getBoundsInLocal());
    }
    
    public static Bounds screenTreeItemBounds(TreeItem<ItemValue> treeItem) {
        if ( treeItem.getValue().getCellGraphic().getScene() == null ) {
            return null;
        }
        return treeItem.getValue().getCellGraphic().getParent().localToScreen(treeItem.getValue().getCellGraphic().getParent().getBoundsInLocal());
    }

}
