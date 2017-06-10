package org.vns.javafx.dock.api.editor;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.dock.api.editor.SceneGraphView.ANCHOR_OFFSET;

/**
 *
 * @author Valery
 */
public abstract class AbstractTreeItemBuilder implements TreeItemBuilder {

    public AbstractTreeItemBuilder() {
        init();
    }

    private void init() {
    }

    @Override
    public TreeItem accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        Object value = dg.getGestureSourceObject();
        //TreeItemBuilder targetBuilder = target.getValue().getBuilder();

        if (target != null && place != null && value != null) {

            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    TreeViewEx.updateSourceSceneGraph((TreeItemEx) treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItemEx sourceTreeItem = EditorUtil.findTreeItemByObject(treeView, value);
                if (sourceTreeItem != null) {
                    TreeViewEx.updateSourceSceneGraph(sourceTreeItem);
                } else {
                    DragManager.ChildrenRemover r = (DragManager.ChildrenRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        r.remove();
                    }
                }
            }
        }
        update(treeView, target, place, value);
        return retval;
    }

    @Override
    public abstract boolean isAcceptable(Object obj);

    protected abstract void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject);

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        retval = createItem(obj);
        return retval;
    }

    /*    @Override
    public TreeItem buildRoot(Object obj) {
        TreeItem<ItemValue> retval = null;
        retval = createItem(obj);
        retval.addEventHandler(TreeItem.<ItemValue>childrenModificationEvent(),
                this::childrenModification);        
        return retval;
    }
     */
    public final TreeItemEx createItem(Object obj, Object... others) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        anchorPane.setStyle("-fx-background-color: yellow");
        TreeItemEx item = new TreeItemEx();
        ItemValue itv = new ItemValue(item);
        item.setValue(itv);
        itv.setTreeItemObject(obj);
        itv.setCellGraphic(anchorPane);

        box.getChildren().add(createItemContent(obj, others));
        return item;
    }

    protected HBox getItemContentPane(TreeItemEx item) {
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

/*    protected void updateSourceSceneGraph(TreeItemEx sourceTreeItem) {
        TreeItemEx parentItem = (TreeItemEx) sourceTreeItem.getParent();
        if (parentItem != null && sourceTreeItem != null) {
            //Object parent = parentItem.getValue().getTreeItemObject();
            //Object sourceObject = sourceTreeItem.getValue().getTreeItemObject();
            TreeItemBuilderRegistry.getInstance().getBuilder(parentItem.getObject()).updateSourceSceneGraph(parentItem, sourceTreeItem);
        }
    }
*/
    /*    protected boolean updateSourceSceneGraph(TreeViewEx treeView, Object sourceObject) {
        boolean retval = false;
        TreeItemEx sourceTreeItem = EditorUtil.findTreeItemByObject(treeView, sourceObject);
        if (sourceTreeItem != null) {
            updateSourceSceneGraph(sourceTreeItem);
            retval = true;
        }
        return retval;
    }
     */
/*    @Override
    public void removeChildTreeItem(TreeItemEx parent, TreeItemEx toRemove) {
        parent.getChildren().remove(toRemove);
    }
*/
    public void unregisterChangeHandler_OLD(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            Object o = node.getProperties().remove(EditorUtil.CHANGE_LISTENER);
            if (o != null && (node instanceof Labeled)) {
                ((Labeled) node).graphicProperty().removeListener((ChangeListener<Node>) o);
            }
        }
    }

    public void unregisterChangeHandler(TreeItemEx item) {
        if (item.getObject() == null) {
            return;
        }
        TreeItemBuilder b  = TreeItemBuilderRegistry.getInstance().getBuilder(item.getObject());
        b.unregisterObjectChangeHandler(item.getObject());
        for (TreeItem it : item.getChildren()) {
            unregisterChangeHandler((TreeItemEx) it);
        }
    }


    /*    protected void unregisterSingleChangeHandler(Object obj) {
        if (obj == null || !(obj instanceof Node)) {
            return;
        }
        Node node = (Node) obj;
        Object o = node.getProperties().remove(EditorUtil.CHANGE_LISTENER);
    }
     */
//    public void unregisterSingleChangeHandler(Object obj) {
//    }    
/*    private void toList(TreeItemEx item, List list) {
        list.add(item.getObject());
        for (TreeItem it : item.getChildren()) {
            toList((TreeItemEx) it, list);
        }
/
    }
     */
}// DefaultTreeItemBuilder
