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
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.vns.javafx.dock.api.editor.bean.ReflectHelper;

/**
 *
 * @author Valery
 * @param <T> ???
 */
public abstract class AbstractContentBasedTreeItemBuilder<T> extends AbstractTreeItemBuilder {

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval;
        retval = (TreeItemEx) createItem(obj);
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

    protected ObjectProperty<T> getContentProperty(Object obj) {
        ObjectProperty<T> retval = null;
        try {
            Method m = ReflectHelper.MethodUtil.getMethod(obj.getClass(), "contentProperty", new Class[0]);
            retval = (ObjectProperty<T>) m.invoke(obj, new Object[0]);
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


    @SuppressWarnings("unchecked")
    public Class<T> getTypeParameterClass() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
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

        return !(place == target && getContent(place.getObject()) != null);
    }

    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        setContent(target.getObject(), (T) sourceObject);
    }

    @Override
    public void updateOnMove(TreeItemEx item) {
        TreeItemEx parent = (TreeItemEx) item.getParent();
        setContent(parent.getObject(), null);
    }
    protected Object createAndAddListener(TreeItemEx item) {
        ObjectProperty<T> contentProperty = getContentProperty(item.getObject());
        ContentPropertyChangeListener listener = new ContentPropertyChangeListener(item);
        contentProperty.addListener(listener);
        return listener;
        
    }
    protected void removeListener(TreeItemEx item, Object listener) {
         getContentProperty(item.getObject()).removeListener((ChangeListener) listener);
    }
/*    @Override
    public void registerChangeHandler(TreeItemEx item) {
        if (!(item.getObject() != null && (item.getObject() instanceof Node))) {
            return;
        }

        unregisterChangeHandler(item);
        
        ObjectProperty<T> contentProperty = getContentProperty(item.getObject());
        ContentPropertyChangeListener listener = new ContentPropertyChangeListener(item);
        contentProperty.addListener(listener);
//            node.getProperties().put(EditorUtil.CHANGE_LISTENER, listener);
        item.getValue().setChangeListener(listener);
        System.err.println("REGISTER CHANGE " + listener);
    }
*/
/*    @Override
    public void unregisterObjectChangeHandler(TreeItemEx item) {
        //Node node = (Node) item.getObject();
        //ContentPropertyChangeListener listener = (ContentPropertyChangeListener) node.getProperties().get(EditorUtil.CHANGE_LISTENER);
        ContentPropertyChangeListener listener = (ContentPropertyChangeListener) item.getValue().getChangeListener();
        if (listener == null) {
            return;
        }
        getContentProperty(item.getObject()).removeListener(listener);
        //node.getProperties().remove(EditorUtil.CHANGE_LISTENER);

        item.getValue().setChangeListener(null);

    }
*/
    public class ContentPropertyChangeListener implements ChangeListener<T> {

        private final TreeItemEx treeItem;

        public ContentPropertyChangeListener(TreeItemEx treeItem) {
            this.treeItem = treeItem;
        }

        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
            if (oldValue != null && newValue == null) {
                treeItem.getChildren().clear();
            } else if (oldValue == null && newValue != null) {
                TreeItemEx contentItem = TreeItemBuilderRegistry.getInstance().getBuilder(newValue).build(newValue);
                treeItem.getChildren().add(contentItem);
            } else if (oldValue != null && newValue != null) {
                TreeItemEx item = treeItem.treeItemOf(oldValue);
                if (item != null) {
                    TreeViewEx.updateOnMove(item);
                }
                TreeItemEx contentItem = TreeItemBuilderRegistry.getInstance().getBuilder(newValue).build(newValue);
                treeItem.getChildren().add(contentItem);
            }
        }

    }

    public static class NodeContentBasedItemBuilder extends AbstractContentBasedTreeItemBuilder<Node> {

        @Override
        public boolean isAcceptable(Object target, Object accepting) {
            return accepting instanceof Node;
        }

    }
}
