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
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.designer.SceneGraphView.ANCHOR_OFFSET;
import org.vns.javafx.dock.api.editor.bean.BeanAdapter;
import org.vns.javafx.dock.api.editor.bean.ReflectHelper;

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

    protected TreeItemEx build(Object obj, Property p) {
        TreeItemEx retval;

        if (obj instanceof BorderPane) {
            System.err.println("!!!!!!");
        }

        if (p != null && (p instanceof Placeholder)) {
            retval = createPlaceholder(obj, (Placeholder) p);
        } else if (p != null && (p instanceof Header)) {
            retval = createHeaderItem(obj, (Header) p);
        } else {
            retval = createItem(obj);
        }
        if (p != null) {
            retval.setPropertyName(p.getName());
        }
        if (obj == null && !(p instanceof Header)) {
            return retval;
        }

        System.err.println("retval propName = " + retval.getPropertyName());
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

            if (cp instanceof Header) {
                TreeItemEx headerItem = build(cpObj, (Header) cp);
                headerItem.setPropertyName(cp.getName());
                retval.getChildren().add(headerItem);
                retval.setPropertyName(cp.getName());
                if (List.class.isAssignableFrom(cpObj.getClass())) {

                    List ls = (List) cpObj;

                    for (int i = 0; i < ls.size(); i++) {
                        TreeItemEx item = build(ls.get(i));
                        item.setPropertyName(cp.getName());
                        headerItem.getChildren().add(item);
                    }
                } else {
                    TreeItemEx item = build(cpObj);
                    item.setPropertyName(cp.getName());
                    retval.getChildren().add(item);
                }
            } else if (!isplaceholder && cpObj != null) {
                if (List.class.isAssignableFrom(cpObj.getClass())) {
                    List ls = (List) cpObj;
                    for (int i = 0; i < ls.size(); i++) {
                        TreeItemEx item = build(ls.get(i));
                        item.setPropertyName(cp.getName());
                        retval.getChildren().add(item);
                    }
                } else {
                    TreeItemEx item = build(cpObj);
                    item.setPropertyName(cp.getName());
                    retval.getChildren().add(item);
                }
            } else if (isplaceholder && (cpObj != null || !hideIfNull)) {
                TreeItemEx item = build(cpObj, (Placeholder) cp);
                item.setPropertyName(cp.getName());
                System.err.println(" --- item propName = " + item.getPropertyName());
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
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    protected HBox createHeaderContent(Object obj, Header h) {
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

    public final TreeItemEx createHeaderItem(Object obj, Header h) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        //anchorPane.setStyle("-fx-background-color: yellow");

        TreeItemEx retval = new TreeItemEx();

        //retval.setValue(obj);
        box.getChildren().add(createHeaderContent(obj, h));

        retval.setCellGraphic(anchorPane);
        retval.setItemType(TreeItemEx.ItemType.HEADER);

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

        return retval;
    }

    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    public void updateOnMove(TreeItemEx child) {
        TreeItemEx parent = child.getParentSkipHeader();
        Property prop = parent.getProperty(child.getPropertyName());
        Object obj = parent.getValue();
        //NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(obj);
        //Property prop = nd.getProperties().get( child.getIndex());
        BeanAdapter ba = new BeanAdapter(obj);
        Class propType = ba.getType(prop.getName());
        if (List.class.isAssignableFrom(propType)) {
            List ls = (List) ba.get(prop.getName());
            ls.remove(child.getValue());
        } else {
            System.err.println("0 updateOnMove obj=" + obj);
            if (obj instanceof BorderPane) {
                for (TreeItem it : parent.getChildren()) {
                    System.err.println("   0 --- BorderPane updateOnMove propNeme=" + ((TreeItemEx) it).getPropertyName());
                }
            }

            ba.put(prop.getName(), null);
            System.err.println("updateOnMove obj=" + obj);
            if (obj instanceof BorderPane) {
                for (TreeItem it : parent.getChildren()) {
                    System.err.println("   --- BorderPane updateOnMove propNeme=" + ((TreeItemEx) it).getPropertyName());
                }
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
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        Object value = dg.getGestureSourceObject();

        if (target != null && place != null && value != null) {

            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItemEx treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    updateOnMove(treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItemEx sourceTreeItem = EditorUtil.findTreeItemByObject(treeView, value);
                if (sourceTreeItem != null) {
                    updateOnMove(sourceTreeItem);
                } else {
                    DragAndDropManager.ChildrenRemover r = (DragAndDropManager.ChildrenRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        r.remove();
                    }
                }
            }
        }
        update(treeView, target, place, value);

    }

    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        // setContent(target.getObject(), (T) sourceObject);
        TreeItemEx parent = target.getParentSkipHeader();
        BeanAdapter ba = new BeanAdapter(parent.getValue());
        ba.put(target.getPropertyName(), sourceObject);
        System.err.println("update obj=" + parent.getValue());
        if (parent.getValue() instanceof BorderPane) {
            for (TreeItem it : parent.getChildren()) {
                System.err.println("   --- BorderPane update propNeme=" + ((TreeItemEx) it).getPropertyName());
            }
        }

        //String nm = target.getPropertyName();
        //Method propMethod = ReflectHelper.MethodUtil.getMethod(parent.getValue().getClass(), nm + "Property", new Class[0]);
        //Object propValue = ReflectHelper.MethodUtil.invoke(propMethod, getValue(), new Object[0]);
        //Method addListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, "addListener", new Class[]{ChangeListener.class});
        //ReflectHelper.MethodUtil.invoke(addListenerMethod, propValue, new Object[]{changeListener});
    }

}
