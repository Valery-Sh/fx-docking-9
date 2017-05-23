package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import static org.vns.javafx.dock.api.editor.SceneGraphEditor.FIRST;
import static org.vns.javafx.dock.api.editor.SceneGraphEditor.LAST;

/**
 *
 * @author Valery
 */
public class PaneItemBuilder extends TreeItemBuilder {

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
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
        System.err.println("isAcceptable obj=" + obj);
        return obj instanceof Node;
    }

    @Override
    public void removeObject(Object parent, Object toRemove) {
        if (parent instanceof Pane) {
            ((Pane) parent).getChildren().remove(toRemove);
        }
    }

    @Override
    public void removeItem(TreeItem<ItemValue> parent, TreeItem<ItemValue> toRemove) {
        parent.getChildren().remove(toRemove);
    }

    @Override
    //public TreeItem<ItemValue> accept(TreeItem<ItemValue> parent, TreeItem<ItemValue> target,Object obj) {
    public TreeItem accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null || !(dg.getGestureSourceObject() instanceof Node)) {
            return retval;
        }
        Node value = (Node) dg.getGestureSourceObject();
        if (target != null && place != null && value != null && isAcceptable(target, value)) {
            int idx = getIndex(treeView, target, place, value);
            if (idx < 0) {
                return null;
            }
            Pane p = (Pane) ((ItemValue) target.getValue()).getTreeItemObject();
            System.err.println("      --- 2 pane = " + p + "; pane.getChildren().contains = " + p.getChildren().contains(value));
            if (!p.getChildren().contains(value)) {
                if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                    TreeCell cell = (TreeCell) dg.getGestureSource();
                    if (cell.getTreeItem() instanceof TreeItemEx) {
                        notifyObjectRemove(treeView, cell.getTreeItem());
                        notifyTreeItemRemove(treeView, cell.getTreeItem());
                    }
                } else if (dg.getGestureSource() != null && (dg.getGestureSourceObject() instanceof Node)) {
                    //Node node = (Node) dg.getGestureSourceObject();
                    TreeItem item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                    if (item == null) {
                        return null;
                    }
                    notifyObjectRemove(treeView, item);
                    notifyTreeItemRemove(treeView, item);
                }

                retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
//                idx = 0;///////!!!!!!!!!!!!!!!!!!!
                target.getChildren().add(idx, retval);
                p.getChildren().add(idx, value);
            } else {
                //int idxSource = p.getChildren().indexOf(value);
                if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                    TreeCell cell = (TreeCell) dg.getGestureSource();
                    if (cell.getTreeItem() instanceof TreeItemEx) {
                        notifyObjectRemove(treeView, cell.getTreeItem());
                        notifyTreeItemRemove(treeView, cell.getTreeItem());
                    }
                } else if (dg.getGestureSource() != null && (dg.getGestureSourceObject() instanceof Node)) {
                    TreeItem item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                    System.err.println("ACCEPT item = item");
                    if (item == null) {
                        return null;
                    }
                    notifyObjectRemove(treeView, item);
                    notifyTreeItemRemove(treeView, item);
                }
                idx = getIndex(treeView, target, place);
                System.err.println("ACCEPT idx = " + idx);
  //              idx = 0;///////!!!!!!!!!!!!!!!!!!!
                retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
                System.err.println("revval.value = " + value);
                target.getChildren().add(idx, retval);

                p.getChildren().add(idx, value);
            }
        }
        return retval;
    }

    protected int getIndex(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node value) {
        if (place.getValue().getTreeItemObject() == value) {
            return -1;
        }
        TreeItem<ItemValue> sourceItem = EditorUtil.findTreeItemByObject(treeView, value);
        if (sourceItem == null) {
            return -1;
        }
        int idx = -1;

        Pane p = (Pane) target.getValue().getTreeItemObject();
        int valueIdx = p.getChildren().indexOf(value);

        if (target == place) {
            int q = place.getValue().getDragDropQualifier();

            if (q == FIRST && valueIdx == 0) {
                idx = -1;
            } else if (q == FIRST) {
                idx = 0;
            } else if (valueIdx == p.getChildren().size()) {
                idx = -1;
            } else {
                idx = p.getChildren().size();
            }
        } /*            int level = treeView.getTreeItemLevel(target);
            TreeItem<ItemValue> it = EditorUtil.parentOfLevel(treeView, place, level + 1);
            Node node = (Node) it.getValue().getTreeItemObject();
            idx = p.getChildren().indexOf(node) + 1;
         */ else {
            int targetLevel = treeView.getTreeItemLevel(target);
            int placeLevel = treeView.getTreeItemLevel(place);
            TreeItem<ItemValue> parent = place;
            System.err.println(" -------------- 1 -- place.obj" + place.getValue().getTreeItemObject());
            System.err.println(" -------------- 2 -- target.obj" + target.getValue().getTreeItemObject());            
            System.err.println(" -------------- 3 -- targetLevel = " + targetLevel);            
            System.err.println(" -------------- 4 -- placeLevel = " + placeLevel);                        
            
            if ( targetLevel - placeLevel == 1) {
                
            } else {
                while(treeView.getTreeItemLevel(parent) - targetLevel  >  1) {
                    
                    parent = parent.getParent();
                    System.err.println("   --- parent = " + parent.getValue().getTreeItemObject());
                    System.err.println("   ---  dif = " + (targetLevel - treeView.getTreeItemLevel(parent)));
                }
            }
            System.err.println(" -------------- 2 " + parent.getValue().getTreeItemObject());            
/*            if (target.getChildren().indexOf(place) < target.getChildren().indexOf(sourceItem)
                    && target.getChildren().indexOf(sourceItem) >= 0) {
                idx = target.getChildren().indexOf(place);
            } else {
                idx = target.getChildren().indexOf(place) + 1;
            }
*/           

            idx = target.getChildren().indexOf(parent);
            System.err.println("1 INDEX == " + idx);            
        }
        System.err.println("2 INDEX == " + idx);
        return idx;
    }
    protected int getIndex_OLD(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node value) {
        if (place.getValue().getTreeItemObject() == value) {
            return -1;
        }
        TreeItem<ItemValue> sourceItem = EditorUtil.findTreeItemByObject(treeView, value);
        if (sourceItem == null) {
            return -1;
        }
        int idx = -1;

        Pane p = (Pane) target.getValue().getTreeItemObject();
        int valueIdx = p.getChildren().indexOf(value);

        if (target == place) {
            int q = place.getValue().getDragDropQualifier();

            if (q == FIRST && valueIdx == 0) {
                idx = -1;
            } else if (q == FIRST) {
                idx = 0;
            } else if (valueIdx == p.getChildren().size()) {
                idx = -1;
            } else {
                idx = p.getChildren().size();
            }
        } /*            int level = treeView.getTreeItemLevel(target);
            TreeItem<ItemValue> it = EditorUtil.parentOfLevel(treeView, place, level + 1);
            Node node = (Node) it.getValue().getTreeItemObject();
            idx = p.getChildren().indexOf(node) + 1;
         */ else {
            int targetLevel = treeView.getTreeItemLevel(target);
            int placeLevel = treeView.getTreeItemLevel(place);
            TreeItem<ItemValue> parent = place;
            System.err.println(" -------------- 1 -- place.obj" + place.getValue().getTreeItemObject());
            System.err.println(" -------------- 2 -- target.obj" + target.getValue().getTreeItemObject());            
            System.err.println(" -------------- 3 -- targetLevel = " + targetLevel);            
            System.err.println(" -------------- 4 -- placeLevel = " + placeLevel);                        
            
            if ( targetLevel - placeLevel == 1) {
                
            } else {
                while(treeView.getTreeItemLevel(parent) - targetLevel  >  1) {
                    
                    parent = parent.getParent();
                    System.err.println("   --- parent = " + parent.getValue().getTreeItemObject());
                    System.err.println("   ---  dif = " + (targetLevel - treeView.getTreeItemLevel(parent)));
                }
            }
            System.err.println(" -------------- 2 " + parent.getValue().getTreeItemObject());            
/*            if (target.getChildren().indexOf(place) < target.getChildren().indexOf(sourceItem)
                    && target.getChildren().indexOf(sourceItem) >= 0) {
                idx = target.getChildren().indexOf(place);
            } else {
                idx = target.getChildren().indexOf(place) + 1;
            }
*/           

            idx = target.getChildren().indexOf(parent);
            System.err.println("1 INDEX == " + idx);            
        }
        System.err.println("2 INDEX == " + idx);
        return idx;
    }

    protected int getIndex(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place) {
        int idx = -1;

        Pane p = (Pane) target.getValue().getTreeItemObject();

        if (target == place) {
            int q = place.getValue().getDragDropQualifier();

            if (q == FIRST) {
                idx = 0;
            } else {
                idx = p.getChildren().size();
            }
        } /*            int level = treeView.getTreeItemLevel(target);
            TreeItem<ItemValue> it = EditorUtil.parentOfLevel(treeView, place, level + 1);
            Node node = (Node) it.getValue().getTreeItemObject();
            idx = p.getChildren().indexOf(node) + 1;
         */ else {
            
            int targetLevel = treeView.getTreeItemLevel(target);
            int placeLevel = treeView.getTreeItemLevel(place);
            TreeItem<ItemValue> parent = place;
            System.err.println("@ -------------- 1 -- place.obj" + place.getValue().getTreeItemObject());
            System.err.println("@ -------------- 2 -- target.obj" + target.getValue().getTreeItemObject());            
            System.err.println("@ -------------- 3 -- targetLevel = " + targetLevel);            
            System.err.println("@ -------------- 4 -- placeLevel = " + placeLevel);                        
            
            if ( targetLevel - placeLevel == 1) {
                
            } else {
                while(treeView.getTreeItemLevel(parent) - targetLevel  >  1) {
                    
                    parent = parent.getParent();
                    System.err.println("@   --- parent = " + parent.getValue().getTreeItemObject());
                    System.err.println("@   ---  dif = " + (targetLevel - treeView.getTreeItemLevel(parent)));
                }
            }
            System.err.println("@ -------------- 2 " + parent.getValue().getTreeItemObject());            
/*            if (target.getChildren().indexOf(place) < target.getChildren().indexOf(sourceItem)
                    && target.getChildren().indexOf(sourceItem) >= 0) {
                idx = target.getChildren().indexOf(place);
            } else {
                idx = target.getChildren().indexOf(place) + 1;
            }
*/           

            idx = target.getChildren().indexOf(parent) + 1;
            System.err.println("@ INDEX == " + idx);            
            
            
//            idx = target.getChildren().indexOf(place) + 1;
        }
        System.err.println("@INDEX == " + idx);
        return idx;
    }

}//PaneItemBuilder

