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
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.editor.bean.ReflectHelper;

/**
 *
 * @author Valery
 */
public class TreeViewItemBuilder extends AbstractContentBasedTreeItemBuilder<TreeItem>{

    @Override
    public boolean isAcceptable(Object target,Object accepting) {
        String[] types = getAcceptTypes(target);
        if ( types == null || types.length == 0  ) {
            return false;
        } 
        if ( accepting == null ) {
            return false;
        }
        if ( types[0] != null && ("*".equals(types[0]) || "all".equals(types[0].toLowerCase()) ) )   {
            return true;
        }
        boolean retval = false;
        for ( String clazz : types ) {
            if ( accepting.getClass().getName().equals(clazz) ) {
                retval = true;
                break;
            }
        }
        return retval;
    }
    
    protected String[] getAcceptTypes(Object target) {
        if ( target instanceof Node ) {
            String str = (String) ((Node)target).getProperties().get(TreeItemBuilder.ACCEPT_TYPES_KEY);
            if ( str == null || str.trim().isEmpty() ) {
                return null;
            }
            return str.split(",");
            
        }
        return null;
    }
    
    protected TreeItem getRoot(Object obj) {
        TreeItem retval = null;
        try {
            Method m = ReflectHelper.MethodUtil.getMethod(obj.getClass(), "getRoot", new Class[0]);
            retval = (TreeItem) m.invoke(obj, new Object[0]);
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
    protected TreeItem getContent(Object obj) {
        return getRoot(obj);
    }

    @Override
    protected void setContent(Object obj, TreeItem content) {
        try {
            Method m = ReflectHelper.MethodUtil.getMethod(obj.getClass(), "setRoot", new Class[]{String.class});
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
    
}
