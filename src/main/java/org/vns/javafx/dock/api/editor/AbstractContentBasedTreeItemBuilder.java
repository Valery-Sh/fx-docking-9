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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.editor.DragManager.ChildrenRemover;
import org.vns.javafx.dock.api.editor.bean.ReflectHelper;

/**
 *
 * @author Valery
 * @param <T> ???
 */
public abstract class AbstractContentBasedTreeItemBuilder<T> extends DefaultTreeItemBuilder {

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval;
        retval = createItem(obj);
        T content = getContent(obj);
        if (content != null) {
            TreeItem item = TreeItemBuilderRegistry.getInstance().getBuilder(content).build(content);
            retval.getChildren().add(item);
        }
        return retval;
    }

    protected T getContent(Object obj) {
        T retval = null;
        try {
            Method m = ReflectHelper.MethodUtil.getMethod(obj.getClass(), "getContent", new Class[0]);
            retval = (T) m.invoke(obj, new Object[0]);
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

    protected void setContent(Object obj, T content) {
        try {
            Method m = ReflectHelper.MethodUtil.getMethod(obj.getClass(), "setContent", new Class[]{getTypeParameterClass()});
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

    @Override
    public abstract boolean isAcceptable(Object obj);

    @Override
    public TreeItem accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        T value = (T) dg.getGestureSourceObject();
        TreeItemBuilder targetBuilder = target.getValue().getBuilder();

        if (target != null && place != null && value != null) {
            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    //targetBuilder.notifyObjectRemove(treeView, treeItem);
                    treeView.removeTreeItemObject(treeItem);
                    treeView.removeTreeItem(treeItem);

                    //targetBuilder.notifyTreeItemRemove(treeView, treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItem item;
                item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                if (item != null) {
                    //targetBuilder.notifyObjectRemove(treeView, item);
                    treeView.removeTreeItemObject(item);
                    treeView.removeTreeItem(item);
                    //targetBuilder.notifyTreeItemRemove(treeView, item);

                } else {
                    ChildrenRemover r = (ChildrenRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        //r.remove(dg.getGestureSource());
                        r.remove();
                    }
                }
            }

            retval = TreeItemBuilderRegistry.getInstance().getBuilder(value).build(value);
            Object obj = target.getValue().getTreeItemObject();
            setContent(obj, (T) dg.getGestureSourceObject());

            target.getChildren().clear();
            target.getChildren().add(0, retval);

        }
        return retval;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getTypeParameterClass() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
    }

    public static class NodeContentBasedItemBuilder extends AbstractContentBasedTreeItemBuilder<Node> {

        @Override
        public boolean isAcceptable(Object obj) {
            return obj instanceof Node;
        }
    }
}
