package org.vns.javafx.dock.api.editor;

import com.sun.javafx.stage.StageHelper;
import java.util.List;
import java.util.Set;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
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
    public static TreeItem<ItemValue> findTreeItemByObject(TreeView treeView,Object sourceGesture) {
        return findTreeItem(treeView.getRoot(), sourceGesture);
    }

    protected static TreeItem<ItemValue> findTreeItem(TreeItem<ItemValue> item, Object sourceGesture) {
        TreeItem retval = null;
        for (TreeItem<ItemValue> it : item.getChildren()) {
            if (it.getValue().getTreeItemObject() == sourceGesture) {
                retval = it;
                break;
            }
            retval = findTreeItem(it, sourceGesture);
            if (retval != null) {
                break;
            }
        }
        return retval;
    }
    public static TreeItem<ItemValue> findTreeItem(TreeView<ItemValue> treeView,double x, double y) {
        TreeItem<ItemValue> retval = null;
        int count = treeView.getExpandedItemCount();
        for ( int i=0; i < count; i++) {
            //System.err.println("COUNT = " + count + "; i=" + i );
            TreeCell cell = (TreeCell) treeView.getTreeItem(i).getValue().getCellGraphic().getParent();
            if ( cell == null ) {
                //System.err.println("cell == NULL obj = " + treeView.getTreeItem(i).getValue().getTreeItemObject());
                continue;
            }
            if ( cell.contains(cell.screenToLocal(x, y))) {
                retval = treeView.getTreeItem(i);
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
        //System.err.println("   --- getTargetTreeView stages.size=" + allStages.size());  
        for ( Stage s : allStages ) {
            if ( s.getScene() == null || s.getScene().getRoot() == null ) {
                break;
            }
            Bounds b = s.getScene().getRoot().localToScreen(s.getScene().getRoot().getBoundsInLocal());
            if ( ! b.contains(x, y)) {
                continue;
            }
            //System.err.println("   --- getTargetTreeView 1 " + s.getScene().getRoot());              
            Node n =  s.getScene().lookup("." + TreeViewEx.LOOKUP_SELECTOR);
            //System.err.println("   --- getTargetTreeView 2 " + n);              
            
            if ( n != null && (n instanceof TreeViewEx) ) {
                retval = (TreeViewEx) n;
                Set<Node> set = s.getScene().getRoot().lookupAll("." + TreeViewEx.LOOKUP_SELECTOR);
                retval = (TreeViewEx) TopNodeHelper.getTopNode(set);
            }
        }
        
        return retval;
    }
    
/*    public static Stage getTarget(double x, double y, Stage excl) {
        Stage retval = null;
        List<Stage> allStages = getStages(x, y, excl);
        if (allStages.isEmpty()) {
            return null;
        }
        List<Stage> targetStages = new ArrayList<>();
        allStages.forEach(s -> {
            Node topNode = TopNodeHelper.getTopNode(s, x, y, n -> {
                //12.05return (n instanceof DockTarget);
                return isDockPaneTarget(n);
            });
            if (topNode != null) {
                targetStages.add(s);
            }
        });
        for (Stage s1 : targetStages) {
            retval = s1;
            for (Stage s2 : allStages) {
                if (s1 == s2) {
                    continue;
                }
                if (s1 != getTarget(s1, s2)) {
                    retval = null;
                    break;
                }
            }
            if (retval != null) {
                break;
            }
        }
        return retval;
    }

    public static Stage getTarget(Stage s1, Stage s2) {
        Stage retval = null;
        Stage s = s1;

        boolean b1 = s1.isAlwaysOnTop();
        boolean b2 = s2.isAlwaysOnTop();
        if (isChild(s1, s2)) {
            //retval must be null s2 is a child window of s1

        } else if (isChild(s2, s1)) {
            retval = s1;
        } else if (zorder(s1) < zorder(s2) && !b1 && !b2) {
            retval = s1;
        } else if (zorder(s1) < zorder(s2) && b1 && b2) {
            retval = s1;
        } else if (b1 && !b2) {
            retval = s1;
        } else if (!b1 && b2) {
        }
        String t = null;
        if (retval != null) {
            t = retval.getTitle();
        }

        return retval;
    }
*/
}
