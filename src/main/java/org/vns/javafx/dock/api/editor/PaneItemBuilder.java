package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery
 */
public class PaneItemBuilder extends TreeItemBuilder {

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof Pane) {
            Pane pane = (Pane) obj;
            retval = createItem((Pane) obj);
            for (Node node : pane.getChildren()) {
                TreeItemBuilder gb = TreeItemRegistry.getInstance().getBuilder(node);
                retval.getChildren().add(gb.build(node));
            }
        }
        return retval;
    }

    @Override
    public boolean isAcceptable(Object obj) {
        return obj instanceof Node;
    }

    @Override
    public boolean isAcceptable(TreeItem<ItemValue> target, Object obj) {
        return isAcceptable(obj);
    }

    @Override
    //public TreeItem<ItemValue> accept(TreeItem<ItemValue> parent, TreeItem<ItemValue> target,Object obj) {
    public TreeItemEx accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {

        TreeItemEx retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null || !(dg.getGestureSourceObject() instanceof Node)) {
            return retval;
        }
        Node value = (Node) dg.getGestureSourceObject();

        if (isAcceptable(target, value) && target != null && place != null) {
            int idx = getIndex(target, place);
            if (idx < 0) {
                return null;
            }
            Pane p = (Pane) ((ItemValue) target.getValue()).getTreeItemObject();
            System.err.println("1 p=" + p);
            p.getChildren().forEach(c -> {
                System.err.println(" ---- c=" + c);
            });
            if (!p.getChildren().contains(value)) {
                if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                    TreeCell cell = (TreeCell) dg.getGestureSource();
                    if (cell.getTreeItem() instanceof TreeItemEx) {
                        notifyTreeItemRemove(treeView, cell.getTreeItem());
                        cell.getTreeItem().getParent().getChildren().remove(cell.getTreeItem());
                    }
                }

                retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
                target.getChildren().add(idx, retval);
                p.getChildren().add(idx, value);
            } else {
                int idxSource = p.getChildren().indexOf(value);
                if (idx != idxSource) {
                    if (idxSource > idx) {
                        p.getChildren().remove(idxSource);
                    } else {
                        p.getChildren().remove(idxSource);
                        idx--;
                    }
                    if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                        TreeCell cell = (TreeCell) dg.getGestureSource();
                        if (cell.getTreeItem() instanceof TreeItemEx) {
                            notifyTreeItemRemove(treeView, cell.getTreeItem());
                            cell.getTreeItem().getParent().getChildren().remove(cell.getTreeItem());
                        }
                    }

                    retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
                    target.getChildren().add(idx, retval);

                    p.getChildren().add(idx, value);

                }
            }
        }
        return retval;
    }

    protected int getIndex(TreeItem<ItemValue> parent, TreeItem<ItemValue> target) {
        int idx = -1;
        Pane p = (Pane) parent.getValue().getTreeItemObject();
        if (parent == target) {
            idx = p.getChildren().size();
        } else {
            if (parent.getValue().getTreeItemObject() instanceof Node) {
                Node node = (Node) target.getValue().getTreeItemObject();
                idx = p.getChildren().indexOf(node) + 1;
            }
        }
        return idx;
    }

    @Override
    public boolean isDragTarget() {
        return true;
    }

}//PaneItemBuilder

