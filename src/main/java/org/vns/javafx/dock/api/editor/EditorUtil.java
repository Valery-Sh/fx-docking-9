package org.vns.javafx.dock.api.editor;

import com.sun.javafx.stage.StageHelper;
import java.util.List;
import java.util.Set;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.TopNodeHelper;

/**
 *
 * @author Valery
 */
public class EditorUtil {

    public static final String GESTURE_SOURCE_KEY = "drag-gesture-source-key";
    public static final String DRAGBOARD_KEY = "dragboard-url-key";
    public static final String REMOVER_KEY = "remove-children-node-key";
    public static final String MOUSE_EVENT_NOTIFIER_KEY = "mouse-event-notifier-key";
    public static final String CHANGE_LISTENER = "object-change-handler-listener-key";

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

    public static TreeCell getCell(TreeItemEx item) {
        return (TreeCell) ((AnchorPane) item.getValue().getCellGraphic()).getParent();
    }

    /*    public static Bounds screenTreeViewBounds(TreeView treeView) {
        return treeView.localToScreen(treeView.getBoundsInLocal());
    }
     */
    public static Bounds screenTreeItemBounds(TreeItemEx treeItem) {
        Node node = treeItem.getValue().getCellGraphic().getParent();
        //Bounds b1 = node.localToScreen(node.getBoundsInLocal());
        //System.err.println("=============== b1=" + b1);
        return node.localToScreen(node.getBoundsInLocal());
    }

    /**
     * Returns visible bounds of a cell which the given {@code treeItem}
     * represents. When calculate the bounds the method takes into account
     * whether there is a horizontal scroll bar. The resulting bounds differ
     * from the full bounds of the cell only with a {@code width } value.
     *
     * @param treeView the tree view of the specified {@code treeItem}
     * @param treeItem ???
     * @return ???
     */
    public static Bounds screenHorVisibleBounds(TreeViewEx treeView, TreeItemEx treeItem) {
        Node node = treeItem.getValue().getCellGraphic().getParent();
        Bounds retval = node.localToScreen(node.getBoundsInLocal());
        if (treeView.getHScrollBar().isVisible()) {
            double vertSrollBarWidth = treeView.getVScrollBar().getWidth();
            Bounds b = node.getBoundsInParent();
            Bounds tvb = treeView.getBoundsInLocal();
            double w = tvb.getWidth() - treeView.getInsets().getRight() - treeView.getInsets().getLeft() - vertSrollBarWidth;
            retval = new BoundingBox(retval.getMinX(), retval.getMinY(), w, retval.getHeight());
        }
        //Bounds b1 = node.localToScreen(node.getBoundsInLocal());
        //System.err.println("=============== b1=" + b1);
        return retval;
    }

/*    public static Bounds screenTopVisibleBounds(TreeViewEx treeView, TreeItem<ItemValue> treeItem) {
        Node node = treeItem.getValue().getCellGraphic().getParent();
        Bounds retval = node.localToScreen(node.getBoundsInLocal());
        if (treeView.getVScrollBar().isVisible()) {
            Bounds noInserts = EditorUtil.screenInsetsFreeBounds(treeView);
        }
        return retval;
    }
*/
    public static Bounds getIntersection(Bounds b1, Bounds b2) {
        
        if ( ! b1.intersects(b2)) {
            return null;
        }
        
        double x, y, w, h;
        Bounds ib; // internal
        Bounds eb; // external
        if (b1.getMinX() >= b2.getMinX()) {
            x = b1.getMinX();
            ib = b1;
            eb = b2;
        } else {
            ib = b2;
            eb = b1;
            x = b2.getMinX();
        }

        double iCoord = ib.getMinX();
        double iDimention = ib.getWidth();
        double eCoord = eb.getMinX();
        double eDimention = eb.getWidth();

        double iEndDimention = iCoord + iDimention - 1;
        double eEndDimention = eCoord + eDimention - 1;

        if (iEndDimention <= eEndDimention) {
            w = iDimention;
        } else {
            w = eEndDimention - iCoord + 1;
        }
        //
        // y and h
        //
        if (b1.getMinY() >= b2.getMinY()) {
            y = b1.getMinY();
            ib = b1;
            eb = b2;
        } else {
            ib = b2;
            eb = b1;
            y = b2.getMinY();
        }
        
        iCoord = ib.getMinY();
        iDimention = ib.getHeight();
        eCoord = eb.getMinY();
        eDimention = eb.getHeight();

        iEndDimention = iCoord + iDimention - 1;
        eEndDimention = eCoord + eDimention - 1;

        if (iEndDimention <= eEndDimention) {
            h = iDimention;
        } else {
            h = eEndDimention - iCoord + 1;
        }

        return new BoundingBox(x, y, w, h);

    }

    public static Bounds translate(Bounds b1, double x, double y) {
        return Transform.translate(x, y).transform(b1);
    }
    
    public static Bounds screenInsetsFreeBounds(Region node) {
        Bounds b = node.localToScreen(node.getBoundsInLocal());
        Insets ins = node.getInsets();
        return new BoundingBox(b.getMinX() + ins.getLeft(),
                b.getMinY() + ins.getTop(),
                b.getWidth() - ins.getLeft() - ins.getRight(),
                b.getHeight() - ins.getTop() - ins.getBottom()
        );
    }

    /*    public Bounds getParentInsetsFreeBounds(Region node) {
        Bounds b = node.getBoundsInParent();
        return new BoundingBox(b.getMinX(), b.getMinY(), b.getWidth() 
                    - node.getInsets().getLeft() - node.getInsets().getRight(), 
                    b.getHeight()
                    - node.getInsets().getTop() - node.getInsets().getBottom() 
        );
    }
     */
    public static TreeItemEx findTreeItemByObject(TreeView treeView, Object sourceGesture) {
        return (TreeItemEx) findChildTreeItem((TreeItemEx) treeView.getRoot(), sourceGesture);
    }

    protected static TreeItemEx findChildTreeItem(TreeItemEx item, Object sourceGesture) {
        TreeItemEx retval = null;
        if ( item.getChildren() == null ) {
            return null;
        }
        for (TreeItem<ItemValue> it : item.getChildren()) {
            if (it.getValue().getTreeItemObject() == sourceGesture) {
                retval = (TreeItemEx) it;
                break;
            }
            retval = findChildTreeItem((TreeItemEx) it, sourceGesture);
            if (retval != null) {
                break;
            }
        }
        return retval;
    }
    protected static TreeItemEx findByTreeItemObject(TreeItemEx item) {
        if ( item.getValue().getTreeItemObject() == null ) {
            return null;
        }
        TreeItemEx  root = findRootTreeItem(item);
        return findChildTreeItem(root, item.getValue().getTreeItemObject());
    }    
    protected static TreeItemEx findRootTreeItem(TreeItemEx item) {
        TreeItemEx  root = item;
        TreeItemEx  retval = null;
        while ( root != null  ) {
            retval = root;
            root = (TreeItemEx) root.getParent();
            
        }
        return retval;
    }
    public static TreeItemEx findTreeItem(TreeView<ItemValue> treeView, double x, double y) {
        TreeItemEx retval = null;
        int count = treeView.getExpandedItemCount();
        for (int i = 0; i < count; i++) {
            TreeCell cell = (TreeCell) treeView.getTreeItem(i).getValue().getCellGraphic().getParent();
            if (cell == null) {
                continue;
            }
            if (cell.contains(cell.screenToLocal(x, y))) {
                retval = (TreeItemEx) treeView.getTreeItem(i);
                break;
            }
        }
        return retval;
    }

    /*    public Stage findStage(double x, double y, Stage excl) {
        return DockRegistry.getInstance().getTarget(x, y, excl, (n) -> {
            return (n instanceof TreeViewEx);
        });
    }
    public static TreeViewEx findTreeView(Stage stage, double x, double y ) {
        return  (TreeViewEx) TopNodeHelper.getTopNode(stage, x, y, (n) -> {
            return (n instanceof TreeViewEx);
        });
    }
     */
    public static TreeViewEx getTargetTreeView(double x, double y) {
        TreeViewEx retval = null;
        List<Stage> allStages = StageHelper.getStages();
        if (allStages.isEmpty()) {
            return null;
        }
        for (Stage s : allStages) {
            if (s.getScene() == null || s.getScene().getRoot() == null) {
                break;
            }
            Bounds b = s.getScene().getRoot().localToScreen(s.getScene().getRoot().getBoundsInLocal());
            if (!b.contains(x, y)) {
                continue;
            }
            Node n = s.getScene().lookup("." + TreeViewEx.LOOKUP_SELECTOR);

            if (n != null && (n instanceof TreeViewEx)) {
                retval = (TreeViewEx) n;
                Set<Node> set = s.getScene().getRoot().lookupAll("." + TreeViewEx.LOOKUP_SELECTOR);
                retval = (TreeViewEx) TopNodeHelper.getTopNode(set);
            }
        }

        return retval;
    }
}
