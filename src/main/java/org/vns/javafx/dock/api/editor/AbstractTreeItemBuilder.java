package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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

    protected abstract void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject);

    @Override
    public TreeItem accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        Object value = dg.getGestureSourceObject();

        if (target != null && place != null && value != null) {

            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    TreeViewEx.updateOnMove((TreeItemEx) treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItemEx sourceTreeItem = EditorUtil.findTreeItemByObject(treeView, value);
                if (sourceTreeItem != null) {
                    TreeViewEx.updateOnMove(sourceTreeItem);
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
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        retval = createItem(obj);
        return retval;
    }

    @Override
    public final TreeItemEx createItem(Object obj) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        anchorPane.setStyle("-fx-background-color: yellow");
        TreeItemEx item = new TreeItemEx();
        ItemValue itv = new ItemValue(item);
        item.setValue(itv);
        /*        if ( obj == null ) {
            item.setPlaceholderBuilder(this);
        }
         */
        //box.getChildren().add(new HBox()); //for placeholder data
        box.getChildren().add(createItemContent(obj));
        itv.setCellGraphic(anchorPane);
        itv.setTreeItemObject(obj);

        //box.getChildren().add(createItemContent(obj));
        return item;
    }

    public final TreeItemEx createPlaceholder(Object obj) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        anchorPane.setStyle("-fx-background-color: yellow");
        TreeItemEx item = new TreeItemEx();
        ItemValue itv = new ItemValue(item);
        item.setValue(itv);
        item.setPlaceholderBuilder(this);
        //box.getChildren().add(new HBox()); //for placeholder data
        box.getChildren().add(createItemContent(null));
        itv.setCellGraphic(anchorPane);
        itv.setTreeItemObject(obj);

        //box.getChildren().add(createItemContent(obj));
        return item;
    }

    public TreeItemEx buildPlaceholder(Object obj) {
        assert obj != null;
        TreeItemEx item = TreeItemBuilderRegistry.getInstance().getBuilder(obj).build(obj);
        TreeItemEx placeholder = createPlaceholder(obj);

        placeholder.getChildren().addAll(item.getChildren());
        return placeholder;
    }
    public void buildPlaceholder(TreeItemEx placeholder,Object obj) {
        if ( obj == null ) {
            placeholder.getChildren().clear();
            placeholder.getValue().setTreeItemObject(null);
            return;
        }
        placeholder.getValue().setTreeItemObject(obj);
        TreeItemEx item = TreeItemBuilderRegistry.getInstance().getBuilder(obj).build(obj);
        placeholder.getChildren().addAll(item.getChildren());
    }

    @Override
    public void updateTreeItemContent(TreeItemEx item, Object oldValue, Object newValue) {
        HBox c = getItemContentPane(item);
        c.getChildren().remove(0);
        //c = (HBox) c.getChildren().get(0);
        //c.getChildren().remove(1, c.getChildren().size());
        c.getChildren().add(0, createItemContent(newValue));
        //HBox hb = (HBox) c.getChildren().get(0);

        //hb.setMouseTransparent(true);
/*        if (newValue != null) {
            Label glb = new Label(newValue.getClass().getSimpleName());
            glb.getStyleClass().add("tree-item-node-" + newValue.getClass().getSimpleName().toLowerCase());
            hb.getChildren().add(glb);
            ((Labeled) hb.getChildren().get(0)).setText("");
            if (newValue instanceof Labeled) {
                glb.setText(glb.getText() + " " + ((Labeled) newValue).getText());
            }
            //glb.setMouseTransparent(true);
//            TreeItemEx objItem = (TreeItemEx) TreeItemBuilderRegistry.getInstance().getBuilder(obj).build(obj);
//            item.getChildren().addAll(objItem.getChildren());
//            item.getValue().setTreeItemObject(obj);

        } else {
            hb.getChildren().remove(1);
            Label lb = (Label) hb.getChildren().get(0);
            lb.setText(item.getValue().getTitle());
            item.getValue().setTreeItemObject(null);
            //lb.setMouseTransparent(true);
            item.getChildren().clear();

        }
         */
    }

    protected HBox getItemContentPane(TreeItemEx item) {
        return (HBox) ((AnchorPane) item.getValue().getCellGraphic()).getChildren().get(0);
    }

    @Override
    public final Node createItemContent(Object obj) {
        return createDefaultContent(obj);
    }

    protected HBox createDefaultContent(Object obj) {

        String text = "";
        if (obj != null && (obj instanceof Labeled)) {
            text = ((Labeled) obj).getText();
        }
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return new HBox(label);
    }

    @Override
    public void unregisterChangeHandler(TreeItemEx item) {
        if (item.getObject() == null) {
            return;
        }
        TreeItemBuilder b = TreeItemBuilderRegistry.getInstance().getBuilder(item.getObject());
        b.unregisterObjectChangeHandler(item);
        for (TreeItem it : item.getChildren()) {
            unregisterChangeHandler((TreeItemEx) it);
        }
    }

    protected Object createAndAddListener(TreeItemEx item) {
        return null;
    }

    protected void removelistener(TreeItemEx item, Object listener) {
    }

    @Override
    public void registerChangeHandler(TreeItemEx item) {

        if (item.getObject() == null) {
            return;
        }
        unregisterChangeHandler(item);
        Object listener = createAndAddListener(item);
        if (item.getObject() instanceof Node) {
            Node n = (Node) item.getObject();
            n.getProperties().put("lll", listener);
            if ("graphicLb".equals(n.getId())) {
                System.err.println("REGICTER graphicLb listener = " + listener);
            }
        }

        item.getValue().setChangeListener(listener);
    }

    @Override
    public void unregisterObjectChangeHandler(TreeItemEx item) {

        Object listener = item.getValue().getChangeListener();
        if (item.getObject() instanceof Node) {
            Node n = (Node) item.getObject();
            listener = n.getProperties().get("lll");
            if (listener != null) {

                if (n instanceof Labeled) {
                    //((Labeled))
                }
            }
            if ("graphicLb".equals(n.getId())) {
                System.err.println("UNREGICTER graphicLb listener = " + listener);
                listener = n.getProperties().get("lll");
                System.err.println("   --- 1 UNREGICTER graphicLb listener = " + n.getProperties().get("lll"));

                if (((Labeled) n).getGraphic() != null) {
                    System.err.println("   --- 2 UNREGICTER graphicLb listener = " + ((Labeled) n).getGraphic().getProperties().get("lll"));
                }

            }
        }

        if (listener == null) {
            return;
        }
        if (item.getObject() instanceof Node) {
            Node n = (Node) item.getObject();
            if ("graphicLb".equals(n.getId())) {
                System.err.println("UNREGICTER graphicLb");
            }
        }

        removelistener(item, listener);
        if (item.getObject() instanceof Node) {
            Node n = (Node) item.getObject();
            n.getProperties().remove("lll");
        }
        item.getValue().setChangeListener(null);
    }

}// AbstractTreeItemBuilder
