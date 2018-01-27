/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.designer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.designer.SceneGraphView.ANCHOR_OFFSET;
import static org.vns.javafx.designer.SceneGraphView.FIRST;
import org.vns.javafx.designer.TreeItemEx.ItemType;
import org.vns.javafx.dock.api.editor.bean.BeanAdapter;

/**
 *
 * @author Valery
 */
public class TreeItemBuilder {

    public static final String ACCEPT_TYPES_KEY = "tree-item-builder-accept-types";
    public static final String CELL_UUID = "uuid-29a4b479-0282-41f1-8ac8-21b4923235be";
    public static final String NODE_UUID = "uuid-f53db037-2e33-4c68-8ffa-06044fc10f81";

    //private final Object nodeObject;
    public TreeItemBuilder() {

    }

    public TreeItemEx build(Object obj) {
        return build(obj, null);
    }

    protected TreeItemEx build(Object obj, PropertyElement p) {
        TreeItemEx retval;

        if (p != null && (p instanceof Placeholder)) {
            retval = createPlaceholder(obj, (Placeholder) p);
        } else if (p != null && (p instanceof ListContent)) {
            retval = createListContentItem(obj, (ListContent) p);
        } else if (p != null && (p instanceof Content)) {
            retval = createItem(obj);
        } else {
            retval = createBaseItem(obj);
        }
        if (p != null && (p instanceof Property)) {
            retval.setPropertyName(((Property)p).getName());
        }
        //System.err.println("p=" + p);
        if (p != null && (p instanceof ListContent)) {
            retval.setValue(obj);
            ObservableList ol = (ObservableList) obj;
            System.err.println("ol.size()=" + ol.size());
            for (int i = 0; i < ol.size(); i++) {
                retval.getChildren().add(build(ol.get(i)));
            }
            return retval;
        }
        if (obj == null && !(p instanceof ListContent)) {
            return retval;
        }

        NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(obj);

        BeanAdapter adapter = new BeanAdapter(obj);

        nc.getProperties().forEach(cp -> {
            int cpIdx = nc.getProperties().indexOf(cp);
            Object cpObj = adapter.get(cp.getName());
            boolean isplaceholder = false;
            boolean hideIfNull = false;
            if (cp instanceof Placeholder) {
                isplaceholder = true;
                hideIfNull = ((Placeholder) cp).isHideNull();
            }

            if ((cp instanceof ListContent)) {
                if (nc.getProperties().size() == 1 && !((ListContent) cp).isAlwaysVisible()) {
                    //
                    // Omit TreeItem for ListItem
                    //
                    ObservableList ol = (ObservableList) cpObj;
                    for (int i = 0; i < ol.size(); i++) {
                        retval.getChildren().add(build(ol.get(i)));
                    }
                } else {
                    TreeItemEx listItem = build(cpObj, (ListContent) cp);
                    retval.getChildren().add(listItem);
                    listItem.setPropertyName(cp.getName());

                }

            } else if (!isplaceholder && cpObj != null) {
                //
                // This is a Content ItemType
                //
/*                if (List.class.isAssignableFrom(cpObj.getClass())) {
                    List ls = (List) cpObj;
                    for (int i = 0; i < ls.size(); i++) {
                        TreeItemEx item = build(ls.get(i));
                        item.setPropertyName(cp.getName());
                        retval.getChildren().add(item);
                    }
                } else {
                 */
                TreeItemEx item = build(cpObj, (Content) cp);
                item.setPropertyName(cp.getName());
                retval.getChildren().add(item);
//                }
            } else if (isplaceholder && (cpObj != null || !hideIfNull)) {
                TreeItemEx item = build(cpObj, (Placeholder) cp);
                item.setPropertyName(cp.getName());
                retval.getChildren().add(item);
            }
        });
        return retval;
    }

    public final HBox createItemContent(Object obj) {

        HBox box = new HBox(new HBox()); // placeholder 
        NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(obj);
        String tp = nc.getTitleProperty();
        String text = "";
        if (tp != null) {
            BeanAdapter adapter = new BeanAdapter(obj);
            text = (String) adapter.get(tp);
            if (text == null) {
                text = "";
            }
        }
        Label label = new Label((obj.getClass().getSimpleName() + " " + text).trim());
        String styleClass = nc.getStyleClass();
        if (styleClass == null) {
            styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        }
        label.getStyleClass().add(styleClass);
        box.getChildren().add(label);
        return box;
    }

    public final TreeItemEx createItem(Object obj) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        //anchorPane.setStyle("-fx-background-color: yellow");

        TreeItemEx retval = new TreeItemEx();

        retval.setValue(obj);

        box.getChildren().add(createItemContent(obj));

        retval.setCellGraphic(anchorPane);
        retval.setItemType(TreeItemEx.ItemType.CONTENT);
        try {
            retval.registerChangeHandlers();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    public final TreeItemEx createBaseItem(Object obj) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        //anchorPane.setStyle("-fx-background-color: yellow");

        TreeItemEx retval = new TreeItemEx();

        retval.setValue(obj);

        box.getChildren().add(createItemContent(obj));

        retval.setCellGraphic(anchorPane);
        retval.setItemType(TreeItemEx.ItemType.ELEMENT);
        try {
            retval.registerChangeHandlers();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    protected HBox createListContent(Object obj, ListContent h) {
        HBox box = new HBox(new HBox()); // placeholder 
        String title = h.getTitle();

        if (title == null) {
            title = "";
        }

        Label label = new Label(title.trim());
        String styleClass = h.getStyleClass();
        if (styleClass == null) {
            styleClass = "tree-item-list-header";
        }
        label.getStyleClass().add(styleClass);
        box.getChildren().add(label);
        return box;
    }

    public final TreeItemEx createListContentItem(Object obj, ListContent h) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        //anchorPane.setStyle("-fx-background-color: yellow");

        TreeItemEx retval = new TreeItemEx();

        //retval.setValue(obj);
        box.getChildren().add(createListContent(obj, h));

        retval.setCellGraphic(anchorPane);
        retval.setItemType(TreeItemEx.ItemType.LISTCONTENT);

        return retval;
    }

    public final TreeItemEx createPlaceholder(Object obj, Placeholder cp) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        //anchorPane.setStyle("-fx-background-color: yellow");
        TreeItemEx retval = new TreeItemEx();

        retval.setValue(obj);

        box.getChildren().add(createPlaceholderContent(obj, cp));
        retval.setCellGraphic(anchorPane);
        retval.setItemType(TreeItemEx.ItemType.PLACEHOLDER);
        try {
            retval.registerChangeHandlers();
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retval;
    }

    protected HBox createPlaceholderContent(Object obj, Placeholder cp) {
        Label iconLabel = new Label();
        HBox retval = new HBox(iconLabel);

        String style = cp.getStyleClass();
        if (style != null) {
            iconLabel.getStyleClass().add(style);
        }

        String title = cp.getTitle();
        if (title == null) {
            title = "";
        }

        if (obj == null) {
            iconLabel.setText(title.trim());
        } else {

            NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(obj);
            title = "";
            String tp = nc.getTitleProperty();
            if (tp != null) {
                BeanAdapter adapter = new BeanAdapter(obj);
                title = (String) adapter.get(tp);
                if (title == null) {
                    title = "";
                }
            }
            Label glb = new Label(obj.getClass().getSimpleName());

            glb.getStyleClass().add("tree-item-node-" + obj.getClass().getSimpleName().toLowerCase());
            retval.getChildren().add(glb);

            glb.setText(glb.getText() + " " + title.trim());
        }
        return retval;
    }

    /**
     *
     * @param treeView the treeView/ Cannot be null
     * @param target the item which is an actual target item to accept a dragged
     * object
     * @param place the item which is a gesture target during the drag-and-drop
     * operation
     * @param dragObject an object which is an actual object to be accepted by
     * the target item.
     * @return true id the builder evaluates that a specified dragObject can be
     * accepted by the given target tree item
     */
    public boolean isAdmissiblePosition(TreeViewEx treeView, TreeItemEx target,
            TreeItemEx place,
            Object dragObject) {
        //
        // Check if the dragObject equals to targetItemObject 
        //
        if (target.getValue() == dragObject) {
            return false;
        }
        TreeItemEx dragItem = EditorUtil.findTreeItemByObject(treeView, dragObject);
        System.err.println("dragItem = " + dragItem);
        System.err.println("target = " + target);
        System.err.println("place = " + place);
        System.err.println("place.getParent = " + place.getParentSkipHeader());
        System.err.println("dragItem.previousSibling() = " + dragItem.previousSibling());
        System.err.println("-----------------------------------------");

        //
        // We do not want to insert the draggedItem before or after itself
        //
        if (target == place && dragItem != null) {
            if (target.getChildren().indexOf(dragItem) == 0) {
                return false;
            }
        }
        if (target == place.getParentSkipHeader() && dragItem != null) {
            System.err.println(" --- ELSE 3");
            if (dragItem == place || dragItem.previousSibling() == place) {
                System.err.println(" --- ELSE 4");
                return false;
            }
            System.err.println(" --- ELSE 5");

        } else if (treeView.getTreeItemLevel(place) - treeView.getTreeItemLevel(target) > 1 && dragItem != null) {
            System.err.println("ELSE 1");

            int level = treeView.getTreeItemLevel(target) + 1;
            System.err.println("ELSE 2");

            TreeItemEx actualPlace = (TreeItemEx) EditorUtil.parentOfLevel(treeView, place, level);
            System.err.println("  --- level = " + level);
            System.err.println("  --- actualPlace = " + actualPlace);
            System.err.println("  --- dragItem.previousSibling() = " + dragItem.previousSibling());
            if (dragItem == actualPlace || dragItem.previousSibling() == actualPlace) {
                return false;
            }
        }
        NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(target.getValue());
        System.err.println("target.getPropertyName = " + target.getPropertyName());
        //Property cp = target.getProperty(target.getPropertyName());//nc.getProperties().get(target.getIndex());
        //BeanAdapter adapter = new BeanAdapter(target.getValue());
        //retval = adapter.getType(cp.getName()).isAssignableFrom(toAccept.getClass());

        return isAcceptable(target, dragObject);
    }

    public boolean isAdmissiblePosition__(TreeViewEx treeView, TreeItemEx target,
            TreeItemEx place,
            Object dragObject) {
        //
        // Check if the dragObject equals to targetItemObject 
        //
        if (target.getValue() == dragObject) {
            return false;
        }
        TreeItemEx dragItem = EditorUtil.findTreeItemByObject(treeView, dragObject);
        System.err.println("dragItem = " + dragItem);
        System.err.println("target = " + target);
        System.err.println("place = " + place);
        System.err.println("place.getParent = " + place.getParentSkipHeader());
        System.err.println("dragItem.previousSibling() = " + dragItem.previousSibling());
        System.err.println("-----------------------------------------");

        //
        // We do not want to insert the draggedItem before or after itself
        //

        /*        if (target == place && dragItem != null) {
            if (target.getChildren().indexOf(dragItem) == 0) {
                return false;
            }
        }
         */
        if (target == place.getParentSkipHeader() && dragItem != null) {
            System.err.println(" --- ELSE 3");
            if (dragItem == place || dragItem.previousSibling() == place) {
                System.err.println(" --- ELSE 4");
                return false;
            }
            System.err.println(" --- ELSE 5");

        } else if (target == place.getParentSkipHeader() && dragItem == null) {
        } else if (treeView.getTreeItemLevel(place) - treeView.getTreeItemLevel(target) > 1 && dragItem != null) {
            //
            // Now: target != place.getParentSkipHeader()
            //
            System.err.println("ELSE 1");

            int level = treeView.getTreeItemLevel(target) + 1;
            System.err.println("ELSE 2");

            TreeItemEx actualPlace = (TreeItemEx) EditorUtil.parentOfLevel(treeView, place, level);
            System.err.println("  --- level = " + level);
            System.err.println("  --- actualPlace = " + actualPlace);
            System.err.println("  --- dragItem.previousSibling() = " + dragItem.previousSibling());
            if (dragItem == actualPlace || dragItem.previousSibling() == actualPlace) {
                return false;
            }
        }
        //NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(target.getValue());
        //System.err.println("target.getPropertyName = " + target.getPropertyName());
        //Property cp = target.getProperty(target.getPropertyName());//nc.getProperties().get(target.getIndex());
        //BeanAdapter adapter = new BeanAdapter(target.getValue());
        //retval = adapter.getType(cp.getName()).isAssignableFrom(toAccept.getClass());
        System.err.println("BEFORE isAcceptable");
        return isAcceptable(target, dragObject);
    }

    ////////////////////////////////////////////////////////////
    protected boolean isAcceptable(TreeItemEx target, Object toAccept) {
        if (toAccept == null) {
            return false;
        }
        boolean retval = true;
        NodeDescriptor nc;
        if (target.getValue() == null) {
            //
            // This is possible when target is a placeholder
            //
            TreeItemEx parent = (TreeItemEx) target.getParent();
            nc = NodeDescriptorRegistry.getInstance().getDescriptor(parent.getValue());
            Property cp = parent.getProperty(target.getPropertyName());//nc.getProperties().get(target.getIndex());
            BeanAdapter adapter = new BeanAdapter(parent.getValue());
            retval = adapter.getType(cp.getName()).isAssignableFrom(toAccept.getClass());
        } else {
            nc = NodeDescriptorRegistry.getInstance().getDescriptor(target.getValue());
        }
        System.err.println("isAcceptable = " + retval);
        return retval;
    }

    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    public void updateOnMove(TreeItemEx child) {
        TreeItemEx parent = (TreeItemEx) child.getParent();
        if (parent == null) {
            // root item
            return;
        }
        if (parent.getItemType() == ItemType.LISTCONTENT) {
            ((ObservableList) parent.getValue()).remove(child.getValue());
        } else if (parent.getItemType() == ItemType.CONTENT) {
            NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(parent.getValue());
            if (nd.getProperties().size() == 1 && (nd.getProperties().get(0) instanceof ListContent)) {
                BeanAdapter ba = new BeanAdapter(parent.getValue());
                ((ObservableList) ba.get(nd.getProperties().get(0).getName())).remove(child.getValue());
            } else {
                BeanAdapter ba = new BeanAdapter(parent.getValue());
                ba.put(child.getPropertyName(), null);
            }
        }
    }

    public void addTreeItemObjectChangeListener(TreeItemEx item) {

        if (item.getValue() == null) {
            return;
        }
        removeTreeItemObjectChangeListener(item);
        Object listener = null;

        //!!!23.01item.getValue().setChangeListener(listener);
    }

    public void removeTreeItemObjectChangeListener(TreeItemEx item) {
    }

    public void accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        //TreeItem retval = null;
        System.err.println("ACCEPTL target = " + target);
        System.err.println("ACCEPTL place = " + place);
        System.err.println("ACCEPTL gestureSource = " + gestureSource);
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        Object value = dg.getGestureSourceObject();
        System.err.println("ACCEPTL gestureSource.value = " + value);
        System.err.println("==========================================");
        if (target != null && place != null && value != null) {

            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItemEx treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    System.err.println("ACCEPTL before updateOnMove 1");
                    updateOnMove(treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItemEx sourceTreeItem = EditorUtil.findTreeItemByObject(treeView, value);
                if (sourceTreeItem != null) {
                    System.err.println("ACCEPTL before updateOnMove 2");
                    updateOnMove(sourceTreeItem);
                } else {
                    System.err.println("ACCEPTL before remove");

                    DragAndDropManager.ChildrenRemover r = (DragAndDropManager.ChildrenRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        r.remove();
                    }
                }
            }
        }
        System.err.println("BEFORE UPDATE");
        update(treeView, target, place, value);

    }

    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        //setContent(target.getObject(), (T) sourceObject);
        //int idx = getIndex(treeView, target, place);
        //getList(target.getObject()).add(idx, (T)sourceObject);        

        System.err.println("update: target = " + target);
        System.err.println("update: place = " + place);
        System.err.println("update:  place.getPropertyName = " + place.getPropertyName());
        System.err.println("-----------------------------------------");
        int idx = getIndex(treeView, target, place);
        System.err.println("update: idx = " + idx);
        NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(target.getValue());
        BeanAdapter ba = new BeanAdapter(target.getParent().getValue());
        Class propType = ba.getType(place.getPropertyName());

        if (List.class.isAssignableFrom(propType)) {
            List ls = (List) ba.get(place.getPropertyName());
            System.err.println("getIndex = " + getIndex(treeView, target, place));

            ls.add(getIndex(treeView, target, place), sourceObject);
        } else {
            //ba.get(target.getPropertyName());
            ba.put(target.getPropertyName(), sourceObject);
        }

        //ba.put(target.getPropertyName(), sourceObject);
        /*        System.err.println("update obj=" + parent.getValue());
        if (parent.getValue() instanceof BorderPane) {
            System.err.println("borderPane SIZE = " + ((BorderPane)parent.getValue()).getChildren().size());
            for (TreeItem it : parent.getChildren()) {
                System.err.println("   --- BorderPane update propNeme=" + ((TreeItemEx) it).getPropertyName());
            }
        }
         */
        //String nm = target.getPropertyName();
        //Method propMethod = ReflectHelper.MethodUtil.getMethod(parent.getValue().getClass(), nm + "Property", new Class[0]);
        //Object propValue = ReflectHelper.MethodUtil.invoke(propMethod, getValue(), new Object[0]);
        //Method addListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, "addListener", new Class[]{ChangeListener.class});
        //ReflectHelper.MethodUtil.invoke(addListenerMethod, propValue, new Object[]{changeListener});
    }

    /**
     * Tries to to find an object of type {@code TreeItem} in the specified 
     * {@link TreeViewEx } which corresponds to an object specified by the
     * {@code value} parameter.
     *
     * @param treeView the node to search in
     * @param target the target TreeItem where the new TreeItem should be place
     * as a children
     * @param place the object of type TreeItem which represents a drag target
     * TreeCell
     * @param value the object to search
     *
     * @return an index in the collection of children in the target TreeItem
     * used to insert a new TreeItem for the object specified by the value
     * parameter.
     *
     *
     */
    protected int getIndex(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object value) {
        if (target.getValue() == value) {
            return -1;
        }
        TreeItemEx sourceItem = EditorUtil.findTreeItemByObject(treeView, value);
        if (sourceItem == null) {
            return -1;
        }
        int idx = -1;
        NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(target.getValue());
        BeanAdapter ba = new BeanAdapter(target.getValue());
        Class propType = ba.getType(target.getPropertyName());
        List ls = (List) ba.get(target.getPropertyName());
        int valueIdx = ls.indexOf(value);

        if (target == place) {

            if (valueIdx == 0) {
                idx = -1;
            } else {
                idx = 0;
            }
        } else {
            int targetLevel = treeView.getTreeItemLevel(target);
            int placeLevel = treeView.getTreeItemLevel(place);
            TreeItemEx parent = place;
            if (targetLevel - placeLevel != 1) {
                //
                // Occurs when place is the las TreeItemof it's parent
                //
                while (treeView.getTreeItemLevel(parent) - targetLevel > 1) {
                    parent = (TreeItemEx) parent.getParent();
                }
            }
            idx = target.getChildren().indexOf(parent) + 1;
        }
        return idx;
    }

    /**
     * Tries to calculate an index in the children collection of the item
     * specified by the parameter {@code target } where a new item can be
     * inserted.
     *
     * @param treeView the node to search in
     * @param target the target TreeItem where the new TreeItem should be place
     * as a children
     * @param place the object of type TreeItem which represents a drag target
     * TreeCell
     *
     * @return an index in the collection of children in the target TreeItem
     * used to insert a new TreeItem
     */
    protected int getIndex(TreeViewEx treeView, TreeItemEx target, TreeItemEx place) {
        int idx = -1;

        if (target == place) {
            int q = place.getDragDropQualifier();

            if (q == FIRST) {
                idx = 0;
            } else {
                idx = target.getChildren().size();
            }
        } else {
            int targetLevel = treeView.getTreeItemLevel(target);
            int placeLevel = treeView.getTreeItemLevel(place);
            TreeItemEx parent = place;
            if (targetLevel - placeLevel != 1) {
                //
                // Occurs when place is the last TreeItem of it's parent
                //
                while (treeView.getTreeItemLevel(parent) - targetLevel > 1) {
                    parent = (TreeItemEx) parent.getParent();
                }
            }
            idx = target.getChildren().indexOf(parent) + 1;
        }
        return idx;
    }

}
