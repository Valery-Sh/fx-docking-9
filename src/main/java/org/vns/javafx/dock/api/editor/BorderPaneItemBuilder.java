package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
    public void removeItem(TreeItem<ItemValue> parent, TreeItem<ItemValue> toRemove) {
//        System.err.println("BorderPane removeItem ");
        Object obj = toRemove.getValue().getTreeItemObject();
        BorderPane bp = (BorderPane) parent.getValue().getTreeItemObject();
        TreeItemBuilder builder;
        if (obj == null) {
            //
            // PlaceHolderBuilder will be returned
            //
            builder = toRemove.getValue().getBuilder();
        } else {
            builder = parent.getValue().getBuilder().getPlaceHolderBuilder(toRemove);
        }
        ((BorderPanePlaceholderBuilder) builder).setContent(toRemove, null);
    }

    @Override
    public void removeObject(Object parent, Object toRemove) {
//        System.err.println("BorderPane remove ");
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
    public TreeItemBuilder getPlaceHolderBuilder(TreeItem placeHolder) {
        return placeholderBuilder;
    }

    @Override
    public boolean isAdmissiblePosition(TreeView treeView, TreeItem<ItemValue> target,
            TreeItem<ItemValue> place,
            Object dragObject) {
        return false;
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

        protected void setContent(TreeItem<ItemValue> item, Object obj) {
            HBox c = getItemContentPane(item);
            HBox hb = (HBox) c.getChildren().get(0);
            if (obj != null) {
                Label glb = new Label(obj.getClass().getSimpleName());

                glb.getStyleClass().add("tree-item-node-" + obj.getClass().getSimpleName().toLowerCase());
                hb.getChildren().add(glb);
                ((Labeled) hb.getChildren().get(0)).setText("");
                if (obj instanceof Labeled) {
                    glb.setText(glb.getText() + " " + ((Labeled) obj).getText());
                }
            } else {
                hb.getChildren().remove(1);
                Label lb = (Label) hb.getChildren().get(0);
                lb.setText(item.getValue().getTitle());
                item.getValue().setTreeItemObject(null);
            }

        }

        @Override
        public boolean isAdmissiblePosition(TreeView treeView, TreeItem<ItemValue> target,
                TreeItem<ItemValue> place,
                Object dragObject) {
            boolean retval = super.isAdmissiblePosition(treeView, target, place, dragObject);
            if ( ! retval ) {
                return false;
            }
            if ( place.getParent() == target ) {
                return false;
            }
            return true;
        }

        @Override
        public boolean isAcceptable(Object obj) {
            return obj != null && (obj instanceof Node);
        }

        @Override
        public TreeItemEx accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
//            System.err.println("BorderPane Placeholder accept");
            int idx = place.getParent().getChildren().indexOf(place);
            BuildPos pos = BuildPos.values()[idx];

            TreeItemEx retval = null;
            DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
            if (dg == null) {
                return retval;
            }
            Object value = dg.getGestureSourceObject();
            //if (isAcceptable(target, value)) {
                if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                    TreeCell cell = (TreeCell) dg.getGestureSource();
                    if (cell.getTreeItem() instanceof TreeItemEx) {
//                        System.err.println("Accept Node before Notify");
                        notifyObjectRemove(treeView, cell.getTreeItem());
                        notifyTreeItemRemove(treeView, cell.getTreeItem());
                    }
                }

                ItemValue v = (ItemValue) place.getValue();
                v.setTreeItemObject(value);

                setContent(place, value);

/*                if (isAcceptable(target, value)) {
                    switch (pos) {
                        case TOP:
                            break;
                        case RIGHT:
                            break;
                        case BOTTOM:
                            break;
                        case LEFT:
                            break;
                        case CENTER:
                            break;
                    }

                }
*/
            //}
//            System.err.println("(ItemValue) place.getValue().getTreeItemObject=" + (ItemValue) place.getValue().getTreeItemObject());
            return retval;

        }


        @Override
        public TreeItemBuilder getPlaceHolderBuilder(TreeItem placeHolder) {
            return null;
        }
    }
}
