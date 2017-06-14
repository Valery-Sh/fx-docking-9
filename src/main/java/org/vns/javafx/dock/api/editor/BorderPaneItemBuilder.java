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
public class BorderPaneItemBuilder extends AbstractTreeItemBuilder implements TreeItemBuilder.PlaceholderBuilderFactory {

//    private final BorderPanePlaceholderBuilder placeholderBuilder;
    private final BorderPanePlaceholderBuilder topPlaceholderBuilder;
    private final BorderPanePlaceholderBuilder rightPlaceholderBuilder;
    private final BorderPanePlaceholderBuilder bottomPlaceholderBuilder;
    private final BorderPanePlaceholderBuilder leftPlaceholderBuilder;
    private final BorderPanePlaceholderBuilder centerPlaceholderBuilder;

    public BorderPaneItemBuilder() {
        //this.placeholderBuilder = new BorderPanePlaceholderBuilder();
        this.topPlaceholderBuilder = new BorderPanePlaceholderBuilder(0);
        this.rightPlaceholderBuilder = new BorderPanePlaceholderBuilder(1);
        this.bottomPlaceholderBuilder = new BorderPanePlaceholderBuilder(2);
        this.leftPlaceholderBuilder = new BorderPanePlaceholderBuilder(3);
        this.centerPlaceholderBuilder = new BorderPanePlaceholderBuilder(4);

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

    public TreeItemEx createPlaceholder(int placeHolderId, Object obj) {
        TreeItemEx retval = null;
        switch (placeHolderId) {
            case 0:
                retval = topPlaceholderBuilder.buildTop(obj);
                //retval.setPlaceholderBuilder(topPlaceholderBuilder);
                break;
            case 1:
                retval = rightPlaceholderBuilder.buildRight(obj);
                //retval.setPlaceholderBuilder(rightPlaceholderBuilder);
                break;
            case 2:
                retval = bottomPlaceholderBuilder.buildBottom(obj);
                //retval.setPlaceholderBuilder(bottomPlaceholderBuilder);                
                break;
            case 3:
                retval = leftPlaceholderBuilder.buildLeft(obj);
                //retval.setPlaceholderBuilder(leftPlaceholderBuilder);                
                break;
            case 4:
                retval = centerPlaceholderBuilder.buildCenter(obj);
                //retval.setPlaceholderBuilder(centerPlaceholderBuilder);
                break;
            case 5:
                break;
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

        top = createPlaceholder(0, bp.getTop());
        right = createPlaceholder(1, bp.getRight());
        bottom = createPlaceholder(2, bp.getBottom());
        left = createPlaceholder(3, bp.getLeft());
        center = createPlaceholder(4, bp.getCenter());
        TreeItem[] items = new TreeItem[]{
            top, right, bottom, left, center
        };
        return items;
    }

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
                TreeItemEx item = (TreeItemEx) borderPaneItem.getChildren().get(idx);
                //BorderPanePlaceholderBuilder builder = (BorderPanePlaceholderBuilder) borderPaneItem.getBuilder().getPlaceHolderBuilder(null);
                BorderPanePlaceholderBuilder builder = (BorderPanePlaceholderBuilder) item.getPlaceholderBuilder();
                //builder.setContent(item, newValue);
                builder.buildPlaceholder(item, newValue);


            } else if (oldValue != null && newValue == null) {
                //BorderPanePlaceholderBuilder builder = (BorderPanePlaceholderBuilder) borderPaneItem.getBuilder().getPlaceHolderBuilder(null);
                //builder.setContent((TreeItemEx) borderPaneItem.getChildren().get(idx), null);
                TreeItemEx item = (TreeItemEx) borderPaneItem.getChildren().get(idx);
                BorderPanePlaceholderBuilder builder = (BorderPanePlaceholderBuilder) item.getPlaceholderBuilder();
                //builder.setContent(item, null);
                builder.buildPlaceholder(item, null);

            } else if (oldValue != null && newValue != null) {

            }
        };
        return listener;

    }

    public TreeItemEx buildPlaceholder(Object obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlaceholderBuilder getPlaceholderBuilder(int placeholderId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public static class BorderPanePlaceholderBuilder extends DefaultTreeItemBuilder implements PlaceholderBuilder {

        private final int placeHolderId;

        public BorderPanePlaceholderBuilder() {
            this(0);
        }

        public BorderPanePlaceholderBuilder(int placeHolderId) {
            this.placeHolderId = placeHolderId;
        }

        public static final int TOP = 0;
        public static final int RIGHT = 1;
        public static final int BOTTOM = 2;
        public static final int LEFT = 3;
        public static final int CENTER = 4;

        /*        public static enum BuildPos {
            TOP, RIGHT, BOTTOM, LEFT, CENTER
        }
         */
        @Override
        public TreeItemEx build(Object obj) {
            TreeItemEx retval = buildCenter(obj);
            return retval;
        }

        public TreeItemEx buildTop(Object obj) {
            TreeItemEx retval = createPlaceholder(obj);

//            TreeItemEx retval = createItem(obj, BuildPos.TOP);
//            TreeItemEx retval = createItem(obj);
//            retval.setPlaceholderBuilder(this);
            retval.getValue().setTitle("insert TOP");
            return retval;
        }

        public TreeItemEx buildRight(Object obj) {
            TreeItemEx retval = createPlaceholder(obj);
            //TreeItemEx retval = createItem(obj);
            //retval.setPlaceholderBuilder(this);
            retval.getValue().setTitle("insert RIGHT");
            return retval;
        }

        public TreeItemEx buildBottom(Object obj) {
            TreeItemEx retval = createPlaceholder(obj);

            //TreeItemEx retval = createItem(obj);
            //retval.setPlaceholderBuilder(this);
            retval.getValue().setTitle("insert BOTTOM");
            return retval;
        }

        public TreeItemEx buildLeft(Object obj) {
            TreeItemEx retval = createPlaceholder(obj);

//            TreeItemEx retval = createItem(obj);
//            retval.setPlaceholderBuilder(this);
            retval.getValue().setTitle("insert LEFT");
            return retval;
        }

        public TreeItemEx buildCenter(Object obj) {
            TreeItemEx retval = createPlaceholder(obj);
            //retval.setPlaceholderBuilder(this);
            retval.getValue().setTitle("insert CENTER");
            //retval.getValue().setTreeItemObject(obj);
            return retval;
        }

        @Override
        protected HBox createDefaultContent(Object obj) {
            Label label = new Label();
            HBox hb = new HBox(label);
            switch (placeHolderId) {
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

//        protected void setContent(TreeItemEx item, Object obj) {
//            System.err.println("setContent = itemGetObject=" + item.getObject() + "; obj = " + obj);
            //HBox c = getItemContentPane(item);
            //HBox hb = (HBox) c.getChildren().get(0);
            //hb.setMouseTransparent(true);
//            if (obj != null) {
                /*                Label glb = new Label(obj.getClass().getSimpleName());
                glb.getStyleClass().add("tree-item-node-" + obj.getClass().getSimpleName().toLowerCase());
                hb.getChildren().add(glb);
                ((Labeled) hb.getChildren().get(0)).setText("");
                if (obj instanceof Labeled) {
                    glb.setText(glb.getText() + " " + ((Labeled) obj).getText());
                }
                 */
                //glb.setMouseTransparent(true);
                
                //TreeItemEx objItem = (TreeItemEx) TreeItemBuilderRegistry.getInstance().getBuilder(obj).build(obj);
                //item.getChildren().addAll(objItem.getChildren());
                //item.getValue().setTreeItemObject(obj);
//                buildPlaceholder(item,obj);
//            } else {
//                item.getValue().setTreeItemObject(null);
//                item.getChildren().clear();
//            }

//        }

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
        public boolean isAcceptable(Object target, Object accepting) {
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
            place.getValue().setTreeItemObject(value);

        }
    }
}
