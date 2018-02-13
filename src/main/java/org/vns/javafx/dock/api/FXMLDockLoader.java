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
package org.vns.javafx.dock.api;

import org.vns.javafx.dock.api.save.DockStateLoader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.vns.javafx.dock.incubator.designer.bean.ReflectHelper;

/**
 *
 * @author Valery Shysshkin
 */
public class FXMLDockLoader extends DockStateLoader {

    private Parent fxmlRoot;
    private Object controller;

    public FXMLDockLoader(String prefEntry, Object controller) {
        super(prefEntry);
        this.controller = controller;
        init();
    }

    public FXMLDockLoader(Class clazz, Object controller) {
        super(clazz);
        this.controller = controller;
        init();
    }

    /*    public FXMLDockLoader(String prefEntry, Parent fxmlRoot) {
        super(prefEntry);
        this.fxmlRoot = fxmlRoot;
        init();
    }

    public FXMLDockLoader(Class clazz, Parent fxmlRoot) {
        this(clazz.getName().replace(".", "/"), fxmlRoot);

    }
     */
    public Object getController() {
        return controller;
    }

    @Override
    public Node register(String entry, Class clazz) {
        return super.register(entry, clazz);
    }

    private void init() {
        Field[] fields = ReflectHelper.getDeclaredFields(getController().getClass());

        for (Field field : fields) {
            if (field.getAnnotation(FXML.class) != null) {

                try {
                    field.setAccessible(true);
                    if (field.getAnnotation(FXML.class) == null) {
                        continue;
                    }
                    if (field.getAnnotation(FXMLDockLoader.Save.class) == null) {
                        continue;
                    }
                    
                    if (field.get(controller) instanceof Node) {
                        register(field.getName(), (Node) field.get(controller));
                    }

                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(FXMLDockLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    protected boolean isValidId(String id) {
        return id != null && !id.trim().isEmpty();
    }

    protected void registerAll(Parent node) {
        //!!!08
        if (isValidId(node.getId()) && (DockRegistry.instanceOfDockTarget(node) || DockRegistry.isDockable(node))) {
            register(node.getId(), node);
        }
        for (Node n : ((Parent) node).getChildrenUnmodifiable()) {
            //!!!08
            if (isValidId(n.getId()) && (DockRegistry.instanceOfDockTarget(n) || DockRegistry.isDockable(n))) {
                //register(n.getId(),n);
            }
            if (!(n instanceof Parent)) {
                continue;
            }
            registerAll((Parent) n);
        }

    }
    /**
     * Annotation that tags a class or member as accessible to markup.
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface Save {
    }

}
