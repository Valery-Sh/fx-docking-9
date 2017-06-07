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
public class BorderPaneItemBuilder extends DefaultTreeItemBuilder {

    private final BorderPanePlaceholderBuilder placeholderBuilder;

    public BorderPaneItemBuilder() {
        this.placeholderBuilder = new BorderPanePlaceholderBuilder();
    }

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
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
    @Override
    public void removeChildTreeItem(TreeItemEx parent, TreeItemEx toRemove) {
        Object obj = toRemove.getValue().getTreeItemObject();
        BorderPane bp = (BorderPane) parent.getValue().getTreeItemObject();
        TreeItemBuilder builder;
/*        if (obj == null) {
            //
            // PlaceHolderBuilder will be returned
            //
            builder = toRemove.getValue().getBuilder();
        } else {
            builder = parent.getValue().getBuilder().getPlaceHolderBuilder(toRemove);
        }
*/
        builder = toRemove.getBuilder();
        ((BorderPanePlaceholderBuilder) builder).setContent(toRemove, null);
    }

    @Override
    public void removeChildObject(Object parent, Object toRemove) {
        BorderPane bp = (BorderPane) parent;
        if (bp.getTop() == toRemove) {
            bp.setTop(null);
        } else if (bp.getRight() == toRemove) {
            bp.setRight(null);
        } else if (bp.getBottom() == toRemove) {
            bp.setBottom(null);
        } else if (bp.getLeft() == toRemove) {
            bp.setLeft(null);
        } else if (bp.getCenter() == toRemove) {
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
        final BorderPane pane = (BorderPane) item.getObject();
        pane.topProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) {
                TreeItem<ItemValue> it = EditorUtil.findRootTreeItem(item);
                BorderPanePlaceholderBuilder builder = (BorderPanePlaceholderBuilder) item.getBuilder().getPlaceHolderBuilder(null);                
                builder.setContent((TreeItemEx)item.getChildren().get(0), newValue);
            } else if (oldValue != null && newValue == null) {
                BorderPanePlaceholderBuilder builder = (BorderPanePlaceholderBuilder) item.getBuilder().getPlaceHolderBuilder(null);                
                builder.setContent((TreeItemEx)item.getChildren().get(0), null);
            } else if (oldValue != null && newValue != null) {

            }
        });
        //parentProperty().addListener( (observable, oldValue, newValue) -> {            
//            graphic.parentProperty().addListener( (observable, oldValue, newValue) -> {
/*            Node graphic = (Node) item.getValue().getTreeItemObject();  
            System.err.println("LABELED placeholder registerchangeHandler graphic = " + graphic);
            graphic.parentProperty().addListener( (observable, oldValue, newValue) -> {
                System.err.println("NULL Graphic oldValue= " + oldValue + "; newValue=" + newValue);
                if (oldValue != null) {
                    //TreeItemBuilder oldParentBuilder = TreeItemBuilderRegistry.getInstance().getBuilder(oldValue);
                    removeChildObject(oldValue,graphic);
                    //TreeViewEx.removeTreeItemObject(item);
                   //((Labeled)oldValue).setGraphic(null);
                } else if ( newValue != null) {
                    //((Labeled)oldValue).setGraphic(newValue);
                }
            });
            System.err.println("Labaeld Palceholder registerChangeHandler " + item.getValue().getTreeItemObject());
         */
    }

    @Override
    public void unregisterChangeHandler(TreeItemEx item) {
        //Object treeItemObject = (Labeled) item.getValue().getTreeItemObject();
        if (item.getValue().getChangeListener() == null) {
            return;
        }
        item.getValue().treeItemObjectProperty().removeListener((ChangeListener) item.getValue().getChangeListener());
        item.getValue().setChangeListener(null);
    }

    public static class BorderPanePlaceholderBuilder extends DefaultTreeItemBuilder {

        public static enum BuildPos {
            TOP, RIGHT, BOTTOM, LEFT, CENTER
        }

        @Override
        public TreeItem build(Object obj) {
            TreeItem retval = buildCenter(obj);
            return retval;
        }

        public TreeItem buildTop(Object obj) {
            TreeItem<ItemValue> retval = createItem(obj, BuildPos.TOP);
            retval.getValue().setPlaceholder(true);
            retval.getValue().setTitle("insert TOP");
            return retval;
        }

        public TreeItem buildRight(Object obj) {
            TreeItem<ItemValue> retval = createItem(obj, BuildPos.RIGHT);
            retval.getValue().setPlaceholder(true);
            retval.getValue().setTitle("insert RIGHT");
            return retval;
        }

        public TreeItem buildBottom(Object obj) {
            TreeItem<ItemValue> retval = createItem(obj, BuildPos.BOTTOM);
            retval.getValue().setPlaceholder(true);
            retval.getValue().setTitle("insert BOTTOM");
            return retval;
        }

        public TreeItem buildLeft(Object obj) {
            TreeItem<ItemValue> retval = createItem(obj, BuildPos.LEFT);
            retval.getValue().setPlaceholder(true);
            retval.getValue().setTitle("insert LEFT");
            return retval;
        }

        public TreeItem buildCenter(Object obj) {
            TreeItem<ItemValue> retval = createItem(obj, BuildPos.CENTER);
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
                glb.setMouseTransparent(true);
            } else {
                hb.getChildren().remove(1);
                Label lb = (Label) hb.getChildren().get(0);
                lb.setText(item.getValue().getTitle());
                item.getValue().setTreeItemObject(null);
                lb.setMouseTransparent(true);
                
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
        public boolean isAcceptable(Object obj) {
            return obj != null && (obj instanceof Node);
        }

        @Override
        public TreeItemEx accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
            //int idx = place.getParent().getChildren().indexOf(place);

            TreeItemEx retval = null;
            DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
            if (dg == null) {
                return retval;
            }
            Object value = dg.getGestureSourceObject();
            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    //notifyObjectRemove(treeView, treeItem);
                    treeView.removeTreeItemObject(treeItem);
                    //treeView.removeTreeItem(treeItem);

                    //notifyTreeItemRemove(treeView, treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItem item;
                item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                if (item != null) {
                    treeView.removeTreeItemObject(item);
                    //treeView.removeTreeItem(item);
                } else {
                    ChildrenRemover r = (ChildrenRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        //r.remove(dg.getGestureSource());
                        r.remove();
                    }
                }
            }
            setNode(place, (Node) value);
            place.getValue().setTreeItemObject(value);
            boolean b = place.getValue().isPlaceholder();
            int r = treeView.getRow(place); 
            //System.err.println("BorderPane place.obj= " + place.getValue().getTreeItemObject());            
            //System.err.println("BorderPane row = " + r + "; isPlaceholder=" + b);
            return retval;

        }
        private void setNode(TreeItemEx place, Node value) {
            BorderPane bp = (BorderPane) place.getParent().getValue().getTreeItemObject();
            if (place == place.getParent().getChildren().get(0) ) {
                bp.setTop(value);
            } else if (place == place.getParent().getChildren().get(1) ) {
                bp.setRight(value);
            } else if (place == place.getParent().getChildren().get(2) ) {
                bp.setBottom(value);
            } else if (place == place.getParent().getChildren().get(3) ) {
                bp.setLeft(value);
            } else if (place == place.getParent().getChildren().get(4) ) {
                bp.setCenter(value);
            }            
        }  
        @Override
        public TreeItemBuilder getPlaceHolderBuilder(TreeItemEx placeHolder) {
            return null;
        }
    }
}
