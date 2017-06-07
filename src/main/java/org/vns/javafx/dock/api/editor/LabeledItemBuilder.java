package org.vns.javafx.dock.api.editor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;

/**
 *
 * @author Valery
 */
public class LabeledItemBuilder extends DefaultTreeItemBuilder {

    private final LabelPlaceholderBuilder placeholderBuilder;

    public LabeledItemBuilder() {
        this.placeholderBuilder = new LabelPlaceholderBuilder();
    }

    private void init() {

    }

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
        if (obj instanceof Labeled) {
            Labeled node = (Labeled) obj;
            retval = createItem((Node) obj);

            if (node.getGraphic() != null) {
                TreeItem placeholder = createPlaceHolders(node.getGraphic())[0];
                retval.getChildren().add(placeholder);
            }
        }
        return retval;
    }

    @Override
    protected Node createDefaultContent(Object obj, Object... others) {
        String text = ((Labeled) obj).getText();
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }

    /**
     * Checks whether the specified object can be used as a value of the graphic
     * property. May accepts only objects of type {@literal Node} witch becomes
     * a value of the graphic property. of the
     *
     * @param obj an object to be checked
     * @return true if the parameter value is not null and is an instance of
     * Node/
     */
    @Override
    public boolean isAcceptable(Object obj) {
        return obj != null && ((obj instanceof Node) || (obj instanceof String));
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
        Labeled lb = (Labeled) place.getValue().getTreeItemObject();
        if (place == target && lb.getGraphic() != null) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether the specified object is not null and is an instance of
     * Node and the specified target doesn't have children. The method returns {@literal false
     * } if one of the following conditions is not satisfied:
     * <ul>
     * <li>The method {@link #isAcceptable(java.lang.Object)} returns
     * {@literal false} }
     * </li>
     * <li>The specified {@literal target} has children. This means that the
     * {@literal Labeled} node has already it's {@literal  graphic} value set to
     * not null value
     * </li>
     * </ul>
     *
     * @param treeView ???
     * @param target the TreeItem object witch corresponds to the
     * {@literal  Labeled node}.
     * @param gestureSource an object to be checked
     * @param place ???
     * @return true if the parameter value is not null and is an instance of
     * Node and the specified target doesn't have children
     */
    @Override
    public TreeItemEx accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        int r = treeView.getRow(place); 

        TreeItemEx retval = null;
            //System.err.println("1 labeled place.obj= " + place.getValue().getTreeItemObject());            
            //System.err.println("   --- labeled row = " + r + "; isPlaceholder=" + place.getValue().isPlaceholder());

        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null) {
            return retval;
        }
        Object value = dg.getGestureSourceObject();
        if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
            TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
            if (treeItem instanceof TreeItemEx) {
                //(notifyObjectRemove(treeView, treeItem);
                treeView.removeTreeItemObject(treeItem);
                //treeView.removeTreeItem(treeItem);

                //notifyTreeItemRemove(treeView, treeItem);
            }
        } else if (dg.getGestureSourceObject() instanceof String) {
            String text = (String) dg.getGestureSourceObject();
            if (text == null) {
                text = "";
            }
            ((Labeled) target.getValue().getTreeItemObject()).setText(text);
            Object obj = target.getValue().getTreeItemObject();
            Node node = getItemContentPane(target).getChildren().get(0);
            if (node instanceof Labeled) {
                ((Labeled) node).setText(obj.getClass().getSimpleName() + " " + text);
            }
            return (TreeItemEx) target;
        }
        r = treeView.getRow(place); 
            //System.err.println("2 labeled place.obj= " + place.getValue().getTreeItemObject());            
            //System.err.println("   --- labeled row = " + r + "; isPlaceholder=" + place.getValue().isPlaceholder());

        ItemValue v = target.getValue();

        //retval = (TreeItemEx) createPlaceHolders(value)[0];
        //target.getChildren().add(retval);
        ((Labeled) v.getTreeItemObject()).setGraphic((Node) value);
        //}
            //System.err.println("3 labeled place.obj= " + place.getValue().getTreeItemObject());            
            //System.err.println("   --- labeled row = " + r + "; isPlaceholder=" + place.getValue().isPlaceholder());
        
        return retval;
    }

    @Override
    public void removeChildObject(Object parent, Object toRemove) {
        if (parent instanceof Labeled) {
            ((Labeled) parent).setGraphic(null);
        }
    }

    @Override
    public void removeChildTreeItem(TreeItemEx parent, TreeItemEx toRemove) {
        parent.getChildren().remove(toRemove);
    }

    protected TreeItem[] createPlaceHolders(Object obj) {
        return new TreeItem[]{placeholderBuilder.build(obj)};
    }

    @Override
    public TreeItemBuilder getPlaceHolderBuilder(TreeItemEx placeHolder) {
        return placeholderBuilder;
    }

    @Override
    public void registerChangeHandler(TreeItemEx item) {
        //System.err.println("item.getValue().getTreeItemObject() = " + item.getValue().getTreeItemObject());
        Labeled node = (Labeled) item.getValue().getTreeItemObject();
        GraphicChangeListener l = new GraphicChangeListener(item);
        node.graphicProperty().addListener(l);
        item.getValue().setChangeListener(l);
    }

    @Override
    public void unregisterChangeHandler(TreeItemEx item) {
        Labeled node = (Labeled) item.getValue().getTreeItemObject();
        if (item.getValue().getChangeListener() == null) {
            return;
        }
        node.graphicProperty().removeListener((ChangeListener) item.getValue().getChangeListener());
        item.getValue().setChangeListener(null);
    }

    public static class LabelPlaceholderBuilder extends DefaultTreeItemBuilder {

        @Override
        public TreeItem build(Object obj) {
            TreeItem retval = createItem(obj);
            return retval;
        }

        @Override
        protected Node createDefaultContent(Object obj, Object... others) {
            //HBox hb = new HBox();
            Label label = new Label();
            HBox hb = new HBox(label);
            label.getStyleClass().add("labeled-insert-graphic");
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

        @Override
        public boolean isAcceptable(Object obj) {
            return obj != null && (obj instanceof Node);
        }

        @Override
        public TreeItem accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {

            TreeItem retval = null;
            DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
            if (dg == null) {
                return retval;
            }
            Object obj = dg.getGestureSourceObject();
            retval = TreeItemBuilderRegistry.getInstance().getBuilder(obj).build(obj);
            ((ItemValue) retval.getValue()).setPlaceholder(true);

            target.getChildren().add(retval);
            ItemValue v = (ItemValue) target.getValue();
            ((Labeled) v.getTreeItemObject()).setGraphic((Node) obj);

            //}
            return retval;
        }

        @Override
        public void registerChangeHandler(TreeItemEx item) {
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
/*            if (item.getValue().getChangeListener() == null) {
                return;
            }
            item.getValue().treeItemObjectProperty().removeListener((ChangeListener) item.getValue().getChangeListener());
            item.getValue().setChangeListener(null);
*/            
        }

        public class TreeItemObjectChangeListener implements ChangeListener<Node> {

            private final TreeItem<ItemValue> treeItem;

            public TreeItemObjectChangeListener(TreeItem<ItemValue> treeItem) {
                this.treeItem = treeItem;
            }

            @Override
            public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                if (oldValue != null && newValue != null) {
                    //System.err.println("1 Labeled PlaceHilder oldValue=" + oldValue + "; newValue = " + newValue);
                }
                if (oldValue != null && newValue == null) {
                    //System.err.println("2 Labeled PlaceHilder oldValue=" + oldValue + "; newValue = " + newValue);                    
                    //treeItem.getChildren().remove(0);
                } else if (oldValue == null && newValue != null) {
                    //System.err.println("3 Labeled PlaceHilder oldValue=" + oldValue + "; newValue = " + newValue);                    
                    
                    TreeItem<ItemValue> oldItem = EditorUtil.findChildTreeItem(EditorUtil.findRootTreeItem(treeItem), newValue);
                    if (oldItem != null) {
                        //TreeViewEx.removeTreeItemObject(oldItem);
                    }
//                    TreeItem<ItemValue> ph = (TreeItemEx) createPlaceHolders(newValue)[0];

//                    treeItem.getChildren().add(ph);
                    //TreeItemBuilderRegistry.getInstance().getBuilder(newValue).removeChildObject(ph, treeItem);
//                ((Labeled)  treeItem.getValue().getTreeItemObject()).setGraphic(newValue);
                }
            }

        }

    }//LabeledPlaceholder

    public class GraphicChangeListener implements ChangeListener<Node> {

        private final TreeItemEx treeItem;

        public GraphicChangeListener(TreeItemEx treeItem) {
            this.treeItem = treeItem;
        }

        @Override
        public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
            //System.err.println("4 CHANGED place.obj= " + treeItem.getValue().getTreeItemObject());            
            //System.err.println("   --- changed.isPlaceHolder  " + treeItem.getValue().isPlaceholder());
            
            if (oldValue != null && newValue == null) {
                treeItem.getChildren().remove(0);
            } else if (oldValue == null && newValue != null) {
                TreeItemEx t = treeItem.treeItemOf(newValue);
                //System.err.println("treeItem == t = " + (treeItem == t));
                if ( t != null  ) {
                    //System.err.println("LABELED remove " + newValue);
                    //TreeViewEx.removeTreeItemObject(t);
                }
                
/*                TreeItem<ItemValue> it = EditorUtil.findRootTreeItem(treeItem);
                if ( it != null ) {
                    it = EditorUtil.findChildTreeItem(it, newValue);
                    if ( it != null ) {
                        TreeViewEx.removeTreeItemObject(it);
                    }
                }
*/                
                TreeItemEx ph = (TreeItemEx) createPlaceHolders(newValue)[0];
                Object ooo = treeItem.getValue().getTreeItemObject();
                boolean b = treeItem.getValue().isPlaceholder();
                b = ph.getValue().isPlaceholder();
                treeItem.getChildren().add(ph);
                ((Labeled)  treeItem.getObject()).setGraphic(newValue);
                Object ooo1 = treeItem.getValue().getTreeItemObject();
                b = treeItem.getValue().isPlaceholder();
            }  else if (oldValue != null && newValue != null) {
                TreeItemEx t = treeItem.treeItemOf(newValue);
                if ( t != null  ) {
                    TreeViewEx.removeTreeItemObject(t);
                }
                
/*                TreeItem<ItemValue> it = EditorUtil.findRootTreeItem(treeItem);
                if ( it != null ) {
                    it = EditorUtil.findChildTreeItem(it, oldValue);
                    if ( it != null ) {
                        TreeViewEx.removeTreeItemObject(it);
                    }
                }
*/
                ((Labeled)  treeItem.getObject()).setGraphic(newValue);
            }
            //System.err.println("5 CHANGED place.obj= " + treeItem.getValue().getTreeItemObject());            
            //System.err.println("   --- changed  " + treeItem.getValue().isPlaceholder());
            Node tt =  treeItem.getValue().getCellGraphic().getParent();
            //System.err.println("   ---   " + tt.getRow(treeItem));
            //System.err.println("   ---   cellGraphic = " + tt);
        }

    }

}
