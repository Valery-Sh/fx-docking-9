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
package org.vns.javafx.dock.api;

import com.sun.javafx.fxml.BeanAdapter;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.vns.javafx.dock.api.bean.ReflectHelper;

/**
 * The class which is used to remove a specified node from one of it's 
 * parent nodes. Mainly applied to objects which are docked to 
 * the object of type {@link ScenePaneContext }. The instance of the class
 * may be obtained by  applying the code
 * <pre>
 *   DockRegistry.getInstance().getBeanRemover()
 * </pre>
 * 
 * @author Valery Shyshkin
 */
public class DefaultNodeRemover implements BeanRemover {
    /**
     * Removes the specified object from the scene graph.
     * The parameter must be of type {@code Node }.
     * @param obj the object to be removed.
     * @return {@code true} if the remove action was successful {@code false} otherwise
     */
    @Override
    public boolean remove(Object obj) {
        if ( ! (obj instanceof Node) || ((Node)obj).getParent() == null ) {
            return false;
        }
        
        Node node = ((Node)obj).getParent();
        while (node != null) {
            if ( ReflectHelper.isPublic(node.getClass())) {
                break;
            }
            node = node.getParent();
        }
        if ( node == null ) {
            return false;
        }
        return remove(node, obj);
    }

    protected boolean remove(Node parent, Object toRemove) {
        boolean retval = false;
        //
        // try to find DefaultProperty
        //
        Annotation annotation = parent.getClass().getAnnotation(DefaultProperty.class);
        if (annotation != null) {
            String name = ((DefaultProperty) annotation).value();
            try {
                //BeanAdapter ba1 = new BeanAdapter(parent);
                //List list1 = (List) ba1.get(name);
                //Method method = ReflectHelper.MethodUtil.getMethod(parent.getClass(), "get" + name.substring(0, 1).toUpperCase() + name.substring(1), new Class[0]);
                //Class returnType = method.getReturnType();
                Class returnType = ReflectHelper.getGetterReturnType(parent.getClass(), name);
                if (ObservableList.class.equals(returnType)) {
                    BeanAdapter ba = new BeanAdapter(parent);
                    List list = (List) ba.get(name);
                    if (list.contains(toRemove)) {
                        list.remove(toRemove);
                        retval = true;
                    }
                } else if (returnType.isAssignableFrom(toRemove.getClass())) {
                    BeanAdapter ba = new BeanAdapter(parent);
                    Object o = ba.get(name);
                    if (o == toRemove) {
                        ba.put(name, null);
                        retval = true;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(DefaultNodeRemover.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retval;
    }

}
