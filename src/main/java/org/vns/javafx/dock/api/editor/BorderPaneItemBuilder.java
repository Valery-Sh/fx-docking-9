package org.vns.javafx.dock.api.editor;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.vns.javafx.dock.api.editor.DragManager.ChildrenRemover;

/**
 *
 * @author Valery
 */
public class BorderPaneItemBuilder extends AbstractTreeItemBuilder {

    private final BorderPanePlaceholderBuilder placeholderBuilder;

    public BorderPaneItemBuilder() {
        this.placeholderBuilder = new BorderPanePlaceholderBuilder();
    }

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof BorderPane) {
            //BorderPane pane = (BorderPane) obj;
            retval = createItem((BorderPane) obj);
            //
            // Create five placeholders
            //
            TreeItem[] placeholder = createPlaceHolders(obj);
            retval.getChildren().add(placeholder[0]);
            retval.getChildren().add(placeholder[1]);
            retval.getChildren().add(placeholder[2]);
            retval.getChildren().add(placeholder[3]);
            retval.getChildren().add(placeholder[4]);

        }
        return retval;
    }

    protected TreeItem[] createPlaceHolders(Object obj) {
        BorderPane bp = (BorderPane) obj;
        TreeItem top;
        TreeItem right;
        TreeItem bottom;
        TreeItem left;
        TreeItem center;

        if (bp.getTop() != null) {
            top = placeholderBuilder.buildTop(bp.getTop());
        } else {
            top = placeholderBuilder.buildTop(null);
        }
        if (bp.getRight() != null) {
            right = placeholderBuilder.buildRight(bp.getRight());
        } else {
            right = placeholderBuilder.buildRight(null);
        }
        if (bp.getBottom() != null) {
            bottom = placeholderBuilder.buildBottom(bp.getBottom());
        } else {
            bottom = placeholderBuilder.buildBottom(null);
        }

        if (bp.getLeft() != null) {
            left = placeholderBuilder.buildLeft(bp.getLeft());
        } else {
            left = placeholderBuilder.buildLeft(null);
        }
        if (bp.getCenter() != null) {
            center = placeholderBuilder.buildCenter(bp.getCenter());
        } else {
            center = placeholderBuilder.buildCenter(null);
        }

        TreeItem[] items = new TreeItem[]{
            top, right, bottom, left, center
        };
        return items;
    }

    /**
     *
     * @param parent an object of type BorderPane
     * @param toRemove a place holder TreeItem (one of five)
     */
    /*    @Override
    public void removeChildTreeItem(TreeItemEx parent, TreeItemEx toRemove) {
        //Object obj = toRemove.getValue().getTreeItemObject();
        BorderPane bp = (BorderPane) parent.getValue().getTreeItemObject();
        TreeItemBuilder builder;
        builder = toRemove.getBuilder();
        ((BorderPanePlaceholderBuilder) builder).setContent(toRemove, null);
    }
     */
    @Override
    public void updateOnMove(TreeItemEx child) {
        TreeItemEx parent = (TreeItemEx) child.getParent();
        BorderPane bp = (BorderPane) parent.getObject();
        if (bp.getTop() == child.getObject()) {
            TreeItemBuilderRegistry.getInstance().getBuilder(child).unregisterChangeHandler(child);
            bp.setTop(null);
        } else if (bp.getRight() == child.getObject()) {
            TreeItemBuilderRegistry.getInstance().getBuilder(child).unregisterChangeHandler(child);
            bp.setRight(null);
        } else if (bp.getBottom() == child.getObject()) {
            TreeItemBuilderRegistry.getInstance().getBuilder(child).unregisterChangeHandler(child);
            bp.setBottom(null);
        } else if (bp.getLeft() == child.getObject()) {
            TreeItemBuilderRegistry.getInstance().getBuilder(child).unregisterChangeHandler(child);
            bp.setLeft(null);
        } else if (bp.getCenter() == child.getObject()) {
            TreeItemBuilderRegistry.getInstance().getBuilder(child).unregisterChangeHandler(child);
            bp.setCenter(null);
        }
    }

    @Override
    public TreeItemBuilder getPlaceHolderBuilder(TreeItemEx placeHolder) {
        return placeholderBuilder;
    }

    @Override
    public boolean isAdmissiblePosition(TreeView treeView, TreeItemEx target,
            TreeItemEx place,
            Object dragObject) {
        return false;
    }

    @Override
    public void registerChangeHandler(TreeItemEx item) {
        if (!(item.getValue().getTreeItemObject() != null && (item.getValue().getTreeItemObject() instanceof BorderPane))) {
            return;
        }
//        unregisterChangeHandler((Node) item.getValue().getTreeItemObject());
        final BorderPane pane = (BorderPane) item.getObject();
        BorderPaneChangeHandler listener = new BorderPaneChangeHandler(item);
        //pane.getProperties().put(EditorUtil.CHANGE_LISTENER, listener);
        item.getValue().setChangeListener(listener);

        pane.topProperty().addListener(listener.topListener);
        pane.rightProperty().addListener(listener.rightListener);
        pane.bottomProperty().addListener(listener.bottomListener);
        pane.leftProperty().addListener(listener.leftListener);
        pane.centerProperty().addListener(listener.centerListener);
    }

    @Override
    public boolean isAcceptable(Object target, Object accepting) {
        return false;
    }

    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {

    }

    @Override
    public void unregisterObjectChangeHandler(TreeItemEx item) {
        BorderPane l = (BorderPane) item.getObject();
        //BorderPaneChangeHandler listener = (BorderPaneChangeHandler) l.getProperties().get(EditorUtil.CHANGE_LISTENER);
        BorderPaneChangeHandler listener = (BorderPaneChangeHandler) item.getValue().getChangeListener();
        if (listener == null) {
            return;
        }
        //l.getProperties().remove(EditorUtil.CHANGE_LISTENER);
        item.getValue().setChangeListener(null);

    }

    ChangeListener<? super Node> getListener(TreeItemEx borderPaneItem, int idx) {
        ChangeListener<? super Node> listener = (observable, oldValue, newValue) -> {

            if (oldValue == null && newValue != null) {
                BorderPanePlaceholderBuilder builder = (BorderPanePlaceholderBuilder) borderPaneItem.getBuilder().getPlaceHolderBuilder(null);
                builder.setContent((TreeItemEx) borderPaneItem.getChildren().get(idx), newValue);

            } else if (oldValue != null && newValue == null) {
                BorderPanePlaceholderBuilder builder = (BorderPanePlaceholderBuilder) borderPaneItem.getBuilder().getPlaceHolderBuilder(null);
                builder.setContent((TreeItemEx) borderPaneItem.getChildren().get(idx), null);
            } else if (oldValue != null && newValue != null) {

            }
        };
        return listener;
    }

    public static class BorderPaneChangeHandler {

        final TreeItemEx borderPaneItem;
        final BorderPaneItemBuilder builder;
        ChangeListener<? super Node> topListener;// = builder.getListener((TreeItemEx) borderPaneItem.getChildren().get(0), 0 );
        ChangeListener<? super Node> rightListener;// = builder.getListener((TreeItemEx) borderPaneItem.getChildren().get(1), 1 );        
        ChangeListener<? super Node> bottomListener;// = builder.getListener((TreeItemEx) borderPaneItem.getChildren().get(2), 2 );                
        ChangeListener<? super Node> leftListener;// = builder.getListener((TreeItemEx) borderPaneItem.getChildren().get(3), 3 );
        ChangeListener<? super Node> centerListener;// = builder.getListener((TreeItemEx) borderPaneItem.getChildren().get(4), 4 );                                

        public BorderPaneChangeHandler(TreeItemEx item) {
            this.borderPaneItem = item;
            this.builder = (BorderPaneItemBuilder) borderPaneItem.getBuilder();
            topListener = builder.getListener(borderPaneItem, 0);
            rightListener = builder.getListener(borderPaneItem, 1);
            bottomListener = builder.getListener(borderPaneItem, 2);
            leftListener = builder.getListener(borderPaneItem, 3);
            centerListener = builder.getListener(borderPaneItem, 4);
        }
    }

    public static class BorderPanePlaceholderBuilder extends DefaultTreeItemBuilder {

        public static enum BuildPos {
            TOP, RIGHT, BOTTOM, LEFT, CENTER
        }

        @Override
        public TreeItemEx build(Object obj) {
            TreeItemEx retval = buildCenter(obj);
            return retval;
        }

        public TreeItemEx buildTop(Object obj) {
            TreeItemEx retval = createItem(obj, BuildPos.TOP);
            retval.getValue().setPlaceholder(true);
            retval.getValue().setTitle("insert TOP");
            return retval;
        }

        public TreeItemEx buildRight(Object obj) {
            TreeItemEx retval = createItem(obj, BuildPos.RIGHT);
            retval.getValue().setPlaceholder(true);
            retval.getValue().setTitle("insert RIGHT");
            return retval;
        }

        public TreeItemEx buildBottom(Object obj) {
            TreeItemEx retval = createItem(obj, BuildPos.BOTTOM);
            retval.getValue().setPlaceholder(true);
            retval.getValue().setTitle("insert BOTTOM");
            return retval;
        }

        public TreeItemEx buildLeft(Object obj) {
            TreeItemEx retval = createItem(obj, BuildPos.LEFT);
            retval.getValue().setPlaceholder(true);
            retval.getValue().setTitle("insert LEFT");
            return retval;
        }

        public TreeItemEx buildCenter(Object obj) {
            TreeItemEx retval = createItem(obj, BuildPos.CENTER);
            retval.getValue().setPlaceholder(true);
            retval.getValue().setTitle("insert CENTER");
            return retval;
        }

        @Override
        protected Node createDefaultContent(Object obj, Object... others) {
            Label label = new Label();
            HBox hb = new HBox(label);
            switch ((BuildPos) others[0]) {
                case TOP:
                    if (obj == null) {
                        label.setText("insert TOP");
                    }
                    label.getStyleClass().add("borderpane-insert-top");
                    break;
                case RIGHT:
                    if (obj == null) {
                        label.setText("insert RIGHT");
                    }
                    label.getStyleClass().add("borderpane-insert-right");
                    break;
                case BOTTOM:
                    if (obj == null) {
                        label.setText("insert BOTTOM");
                    }
                    label.getStyleClass().add("borderpane-insert-bottom");
                    break;
                case LEFT:
                    if (obj == null) {
                        label.setText("insert LEFT");
                    }
                    label.getStyleClass().add("borderpane-insert-left");
                    break;
                case CENTER:
                    if (obj == null) {
                        label.setText("insert CENTER");
                    }
                    label.getStyleClass().add("borderpane-insert-center");
                    break;
            }
            if (obj != null) {
                Label glb = new Label(obj.getClass().getSimpleName());

                glb.getStyleClass().add("tree-item-node-" + obj.getClass().getSimpleName().toLowerCase());
                hb.getChildren().add(glb);
                if (obj instanceof Labeled) {
                    glb.setText(glb.getText() + " " + ((Labeled) obj).getText());
                }
            }

            return hb;
        }

        protected void setContent(TreeItemEx item, Object obj) {
            System.err.println("setContent = itemGetObject=" + item.getObject() + "; obj = " + obj);
            HBox c = getItemContentPane(item);
            HBox hb = (HBox) c.getChildren().get(0);

            hb.setMouseTransparent(true);
            if (obj != null) {
                Label glb = new Label(obj.getClass().getSimpleName());
                glb.getStyleClass().add("tree-item-node-" + obj.getClass().getSimpleName().toLowerCase());
                hb.getChildren().add(glb);
                ((Labeled) hb.getChildren().get(0)).setText("");
                if (obj instanceof Labeled) {
                    glb.setText(glb.getText() + " " + ((Labeled) obj).getText());
                }
                //glb.setMouseTransparent(true);
                TreeItemEx objItem = (TreeItemEx) TreeItemBuilderRegistry.getInstance().getBuilder(obj).build(obj);
                item.getChildren().addAll(objItem.getChildren());
                item.getValue().setTreeItemObject(obj);
            } else {
                hb.getChildren().remove(1);
                Label lb = (Label) hb.getChildren().get(0);
                lb.setText(item.getValue().getTitle());
                item.getValue().setTreeItemObject(null);
                //lb.setMouseTransparent(true);
                item.getChildren().clear();

            }

        }

        @Override
        public boolean isAdmissiblePosition(TreeView treeView, TreeItemEx target,
                TreeItemEx place,
                Object dragObject) {
            boolean retval = super.isAdmissiblePosition(treeView, target, place, dragObject);
            if (!retval) {
                return false;
            }
            if (place.getParent() == target) {
                return false;
            }
            return true;
        }

        @Override
        public boolean isAcceptable(Object target,Object accepting) {
            return accepting != null && (accepting instanceof Node);
        }

        @Override
        public TreeItemEx accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
            TreeItemEx retval = null;
            DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
            if (dg == null) {
                return retval;
            }
            Object value = dg.getGestureSourceObject();
            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    treeView.updateOnMove((TreeItemEx) treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItem item;
                item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                if (item != null) {
                    treeView.updateOnMove((TreeItemEx) item);
                } else {
                    ChildrenRemover r = (ChildrenRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        r.remove();
                    }
                }
            }
            setNode(place, (Node) value);
            place.getValue().setTreeItemObject(value);

            boolean b = place.getValue().isPlaceholder();
            int r = treeView.getRow(place);
            return retval;

        }

        private void setNode(TreeItemEx place, Node value) {
            BorderPane bp = (BorderPane) place.getParent().getValue().getTreeItemObject();
            if (place == place.getParent().getChildren().get(0)) {
                bp.setTop(value);
            } else if (place == place.getParent().getChildren().get(1)) {
                bp.setRight(value);
            } else if (place == place.getParent().getChildren().get(2)) {
                bp.setBottom(value);
            } else if (place == place.getParent().getChildren().get(3)) {
                bp.setLeft(value);
            } else if (place == place.getParent().getChildren().get(4)) {
                bp.setCenter(value);
            }
        }

        @Override
        public TreeItemBuilder getPlaceHolderBuilder(TreeItemEx placeHolder) {
            return null;
        }
    }
}
