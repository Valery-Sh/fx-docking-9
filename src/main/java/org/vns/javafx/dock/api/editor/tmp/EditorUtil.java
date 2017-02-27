package org.vns.javafx.dock.api.editor.tmp;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery
 */
public class EditorUtil {

    public static Bounds screenArrowBounds(TreeItem<AnchorPane> item) {
        TreeCell cell = getCell(item);
        Bounds retval = new BoundingBox(0, 0, 0, 0);
        if (!(cell.getDisclosureNode() instanceof Pane)) {
            return retval;
        }

        if (((Pane) cell.getDisclosureNode()).getChildren().isEmpty()) {
            return retval;
        }
        Node arrow = ((Pane) cell.getDisclosureNode()).getChildren().get(0);
        Bounds b = ((Pane) cell.getDisclosureNode()).getChildren().get(0).getBoundsInLocal();
        if (arrow.localToScreen(b) == null) {
            return retval;
        }
        return arrow.localToScreen(b);
    }

    public static TreeCell getCell(TreeItem<AnchorPane> item) {
        AnchorPane ap = item.getValue();
        return (TreeCell) item.getValue().getParent();
    }

    public static Bounds screenValueBounds(TreeItem<AnchorPane> item) {
        AnchorPane ap = item.getValue();
        Bounds apBounds = ap.localToScreen(ap.getBoundsInLocal());
        Bounds cellBounds = screenTreeItemBounds(item);
        double height = cellBounds.getHeight();
        double width  = cellBounds.getMinX() + cellBounds.getWidth() - apBounds.getMinX();
        
        return new BoundingBox(apBounds.getMinX(), cellBounds.getMinY(), width , height);
    }

    public static Bounds screenNonValueBounds(TreeItem<AnchorPane> item) {
        Bounds vBnd = screenValueBounds(item);
        Bounds itBnd = screenTreeItemBounds(item);
        
        return new BoundingBox(itBnd.getMinX(), itBnd.getMinY(), itBnd.getWidth() - vBnd.getWidth(), itBnd.getHeight());
    }
    
    public static Bounds screenNonValueBounds(TreeView treeView, TreeItem<AnchorPane> item) {
        Bounds vBnd = screenValueBounds(item);
        Bounds tvBnd = screenTreeViewBounds(treeView);
        return new BoundingBox(tvBnd.getMinX(), vBnd.getMinY(), tvBnd.getWidth() - vBnd.getWidth(), vBnd.getHeight());
    }

    public static Bounds screenNonValueLevelBounds1(TreeView treeView, TreeItem<AnchorPane> item) {
        Bounds itemBnd = screenValueBounds(item);
        Bounds treeBnd = screenTreeViewBounds(treeView);

        Insets ins = ((TreeCell) item.getValue().getUserData()).getInsets();
        double wdelta = 0;
        double hdelta = 0;

        if (ins != null) {
            wdelta = ins.getLeft() + ins.getRight();
        }
        int level = treeView.getTreeItemLevel(item);
        double w = ((treeBnd.getWidth() - itemBnd.getWidth() - wdelta) / (level + 1));
        System.err.println("w = " + w);
        Bounds nvBnd = new BoundingBox(treeBnd.getMinX() + w * level, itemBnd.getMinY(), w, itemBnd.getHeight());
        return nvBnd;
    }

    public static Bounds screenNonValueLevelBounds(TreeView treeView, TreeItem<AnchorPane> item) {
        Bounds itemBnd = screenValueBounds(item);
        //Bounds treeBnd = screenTreeViewBounds(treeView);
        Bounds rootBnd = null;

        int level = treeView.getTreeItemLevel(item);

        //Bounds rootBnd = 
        //Insets ins = ((TreeCell)item.getValue().getParent()).getInsets();
        //double wdelta = 0;
        //double hdelta = 0;
        double gap = getRootStartGap(treeView);
        if (level > 0) {

        }
        //if (ins != null) {
            //wdelta = ins.getLeft() + ins.getRight();
        //}
        double w;// = ((treeBnd.getWidth() - itemBnd.getWidth() - wdelta) / (level + 1));
        double xOffset;// = 0;

        rootBnd = screenNonValueBounds(treeView, treeView.getRoot());
        if (level > 0) {
            xOffset = rootBnd.getWidth() / 2 + gap * level;
            w = gap;
        } else {
            w = rootBnd.getWidth() / 2;
            xOffset = 0;
        }
        Bounds nvBnd = new BoundingBox(rootBnd.getMinX() + xOffset, itemBnd.getMinY(), w, itemBnd.getHeight());
        return nvBnd;
    }

    public static Bounds screenTreeViewBounds(TreeView treeView) {
        return treeView.localToScreen(treeView.getBoundsInLocal());
    }
    public static Bounds screenTreeItemBounds(TreeItem treeItem) {
        return ((Node)treeItem.getValue()).getParent().localToScreen(((Node)treeItem.getValue()).getParent().getBoundsInLocal());
    }

/*    public static Bounds screenTreeItemBounds(TreeView treeView, TreeItem<AnchorPane> item) {
        AnchorPane ap = item.getValue();
        Bounds apBnd = screenValueBounds(item);
        Bounds itBnd = screenTreeViewBounds(treeView);
        return new BoundingBox(itBnd.getMinX(), apBnd.getMinY(), itBnd.getWidth(), apBnd.getHeight());
    }
*/
    public static double getRootStartGap(TreeView<AnchorPane> treeView) {
        double gap = 0;
        if (treeView.getExpandedItemCount() > 1) {
            double rootX = screenValueBounds(treeView.getRoot()).getMinX();
            double itemX = screenValueBounds(treeView.getTreeItem(1)).getMinX();
            gap = itemX - rootX;
        }
        return gap;
    }
}
