package org.vns.javafx.dock.api.editor;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
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
    
    public static Bounds screenArrowBounds(TreeItem<ItemValue> item) {
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

    public static TreeCell getCell(TreeItem<ItemValue> item) {
        AnchorPane ap = (AnchorPane) item.getValue().getCellGraphic();
        return (TreeCell) ap.getParent();
    }

    public static Bounds screenValueBounds(TreeItem<ItemValue> item) {
        AnchorPane ap = (AnchorPane) item.getValue().getCellGraphic();
        
//        System.err.println("screenValueBounds scene=" + ap.getScene());
//        System.err.println("screenValueBounds item.getId=" + ((Node)item.getValue().getTreeItemObject()));
//        System.err.println("screenValueBounds 1 ap=" + ap + "; ap.getBoundsInLocal=" + ap.getBoundsInLocal());        
        Bounds apBounds = ap.localToScreen(ap.getBoundsInLocal());
        if ( apBounds == null ) {
            return null;
        }
//System.err.println("screenValueBounds 3 apBounds=" + apBounds + "; item=" + item.getValue().getTreeItemObject());        
//System.err.println("screenValueBounds 3.1  ap.getLayoutBounds=" + ap.getLayoutBounds());        
        
        Bounds cellBounds = screenTreeItemBounds(item);
        if ( cellBounds == null ) {
            return null;
        }
//System.err.println("screenValueBounds 4 cellBounds=" + cellBounds);        
        
        double height = cellBounds.getHeight();
        double width  = cellBounds.getMinX() + cellBounds.getWidth() - apBounds.getMinX();
        
        return new BoundingBox(apBounds.getMinX(), cellBounds.getMinY(), width , height);
    }
    
    public static Bounds screenValueBounds(TreeItem<ItemValue> item, TreeItem<ItemValue> sample) {
        AnchorPane ap = (AnchorPane) sample.getValue().getCellGraphic();
        if ( ap.getScene() == null ) {
            return null;
        }
        TreeCell sampleCell = (TreeCell) ap.getParent();
        
//        System.err.println("screenValueBounds scene=" + ap.getScene());
//        System.err.println("screenValueBounds item.getId=" + ((Node)item.getValue().getTreeItemObject()));
//        System.err.println("screenValueBounds 1 ap=" + ap + "; ap.getBoundsInLocal=" + ap.getBoundsInLocal());        
        Bounds apBounds = ap.localToScreen(ap.getBoundsInLocal());
//System.err.println("screenValueBounds 3 apBounds=" + apBounds + "; item=" + item.getValue().getTreeItemObject());        
//System.err.println("screenValueBounds 3.1  ap.getLayoutBounds=" + ap.getLayoutBounds());        
        
        Bounds cellBounds = screenTreeItemBounds(sample);
//System.err.println("screenValueBounds 4 cellBounds=" + cellBounds);        
        
        double height = cellBounds.getHeight();
        double width  = cellBounds.getMinX() + cellBounds.getWidth() - apBounds.getMinX();
        
        return new BoundingBox(apBounds.getMinX(), cellBounds.getMinY(), width , height);
    }

    public static Bounds screenNonValueBounds(TreeItem<ItemValue> item) {
//        System.err.println("screenNonValueBounds 1 item=" + item.getValue().getTreeItemObject());
        Bounds vBnd = screenValueBounds(item);
//        System.err.println("screenNonValueBounds 2 item=" + item);        
        Bounds itBnd = screenTreeItemBounds(item);
        if ( itBnd == null ) {
            return null;
        }
//        System.err.println("screenNonValueBounds 3 itBnd=" + itBnd);        
        return new BoundingBox(itBnd.getMinX(), itBnd.getMinY(), itBnd.getWidth() - vBnd.getWidth(), itBnd.getHeight());
    }
    public static Bounds screenNonValueBounds(TreeItem<ItemValue> item, TreeItem<ItemValue> sample) {
//        System.err.println("screenNonValueBounds 1 item=" + item.getValue().getTreeItemObject());
        Bounds vBnd = screenValueBounds(item);
//        System.err.println("screenNonValueBounds 2 item=" + item);        
        Bounds itBnd = screenTreeItemBounds(item);
        if ( itBnd == null ) {
            return null;
        }
//        System.err.println("screenNonValueBounds 3 itBnd=" + itBnd);        
        return new BoundingBox(itBnd.getMinX(), itBnd.getMinY(), itBnd.getWidth() - vBnd.getWidth(), itBnd.getHeight());
    }
    
    
    public static double nonValueWidth(TreeItem<ItemValue> item) {
//        System.err.println("screenNonValueBounds 1 item=" + item.getValue().getTreeItemObject());
        Bounds vBnd = screenValueBounds(item);
//        System.err.println("screenNonValueBounds 2 item=" + item);        
        Bounds itBnd = screenTreeItemBounds(item);
//        System.err.println("screenNonValueBounds 3 itBnd=" + itBnd);        
        return 0;
        //return new BoundingBox(itBnd.getMinX(), itBnd.getMinY(), itBnd.getWidth() - vBnd.getWidth(), itBnd.getHeight());
    }
    
    public static Bounds screenNonValueBounds(TreeView treeView, TreeItem<ItemValue> item) {
        Bounds vBnd = screenValueBounds(item);
        Bounds tvBnd = screenTreeViewBounds(treeView);
        return new BoundingBox(tvBnd.getMinX(), vBnd.getMinY(), tvBnd.getWidth() - vBnd.getWidth(), vBnd.getHeight());
    }


    public static Bounds screenNonValueLevelBounds(TreeView treeView, TreeItem<ItemValue> item) {
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
    public static Bounds screenTreeItemBounds(TreeItem<ItemValue> treeItem) {
        //System.err.println("screenTreeItemBounds=" + treeItem.getValue().getCellGraphic().getParent());
        if ( treeItem.getValue().getCellGraphic().getScene() == null ) {
            //System.err.println("screenTreeItemBounds item.getId=" + ((Node)treeItem.getValue().getTreeItemObject()));            
            return null;
        }
        return treeItem.getValue().getCellGraphic().getParent().localToScreen(treeItem.getValue().getCellGraphic().getParent().getBoundsInLocal());
    }

    public static double getRootStartGap_OLD(TreeView<ItemValue> treeView) {
        double gap = 0;
        if (treeView.getExpandedItemCount() > 1) {
            double rootX = screenValueBounds(treeView.getRoot()).getMinX();
            double itemX = screenValueBounds(treeView.getTreeItem(1)).getMinX();
            gap = itemX - rootX;
        }
        return gap;
    }
    public static double getRootStartGap(TreeView<ItemValue> treeView) {
        double gap = 0;
        if (treeView.getExpandedItemCount() > 1) {
            double rootX = treeView.getRoot().getValue().getCellGraphic().getBoundsInParent().getWidth();
            double itemX = treeView.getTreeItem(1).getValue().getCellGraphic().getBoundsInParent().getWidth();
            gap = rootX - itemX;
        }
        return gap;
    }
    
}
