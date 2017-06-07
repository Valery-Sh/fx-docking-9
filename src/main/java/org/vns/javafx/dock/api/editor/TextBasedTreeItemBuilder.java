/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api.editor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.editor.bean.ReflectHelper;

/**
 *
 * @author Valery
 */
public class TextBasedTreeItemBuilder extends AbstractContentBasedTreeItemBuilder<String> {

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval;
        retval = createItem(obj);
        return retval;
    }

    @Override
    public boolean isAcceptable(Object obj) {
        return (obj == null) || (obj instanceof String);
    }

    @Override
    protected Node createDefaultContent(Object obj, Object... others) {
        String text = "";
        if (obj instanceof TextInputControl) {
            text = ((TextInputControl) obj).getText();
        }
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }

    protected String getText(Object obj) {
        String retval = null;
        try {
            Method m = ReflectHelper.MethodUtil.getMethod(obj.getClass(), "getText", new Class[0]);
            retval = (String) m.invoke(obj, new Object[0]);
        } catch (NoSuchMethodException ex) {
            System.err.println("NoSuchMethodException. " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.err.println("IllegalAccessException. " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("IllegalArgumentException. " + ex.getMessage());
        } catch (InvocationTargetException ex) {
            System.err.println("InvocationTargetException. " + ex.getMessage());
        }
        return retval;
    }

    @Override
    protected String getContent(Object obj) {
        return getText(obj);
    }

    @Override
    protected void setContent(Object obj, String content) {
        try {
            Method m = ReflectHelper.MethodUtil.getMethod(obj.getClass(), "setText", new Class[]{String.class});
            m.invoke(obj, new Object[]{content});
        } catch (NoSuchMethodException ex) {
            System.err.println("NoSuchMethodException. " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.err.println("IllegalAccessException. " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("IllegalArgumentException. " + ex.getMessage());
        } catch (InvocationTargetException ex) {
            System.err.println("InvocationTargetException. " + ex.getMessage());
        }
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
        TreeItemEx retval = null;

        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null) {
            return retval;
        }
        //Object value = dg.getGestureSourceObject();
        if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
            TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
            if (treeItem instanceof TreeItemEx) {
                //notifyObjectRemove(treeView, treeItem);
                treeView.removeTreeItemObject(treeItem);
                treeView.removeTreeItem(treeItem);
                //notifyTreeItemRemove(treeView, treeItem);
            }
        } else if (dg.getGestureSourceObject() instanceof String) {
            String text = (String) dg.getGestureSourceObject();
            if (text == null) {
                text = "";
            }

            Object obj = target.getValue().getTreeItemObject();
            setContent(obj, text);
            return (TreeItemEx) target;
        }

        return retval;
    }

}
