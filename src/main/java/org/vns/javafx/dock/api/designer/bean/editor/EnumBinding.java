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
package org.vns.javafx.dock.api.designer.bean.editor;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import javafx.beans.WeakListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Valery Shyshkin
 */
public class EnumBinding<T extends Enum<T>> implements WeakListener {

    private final WeakReference<ObjectProperty<T>> editorEnum;
    private Parent node;
    private String propertyName;
    private Class<T> enumType;
    private Method[] setMethods;
    private boolean updating = false;
    private boolean bound = false;
    private final ChangeListener<? super T> editorListener = (v, ov, nv) -> {
        editorEnumChanged(v, ov, nv);
    };

    public EnumBinding(String propName,Parent node, ObjectProperty<T> editorEnum, Class<T> enumType) {
        this(propName,node, editorEnum,enumType, new Method[0]);

    }

    public EnumBinding(String propName,Parent node, ObjectProperty<T> editorEnum, Class<T> enumType, Method... setMethods) {
        this.editorEnum = new WeakReference<>(editorEnum);
        this.node = node;
        this.setMethods = setMethods;
        this.propertyName = propName;
        this.enumType = enumType;
    }

    public void editorEnumChanged(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (!updating) {

            final ObjectProperty<T> edEnum = editorEnum.get();

            if (edEnum == null) {
                return;
            }
            try {
                updating = true;
                updateEnum(newValue);
            } finally {
                updating = false;
            }

        }
    }

    public Parent getNode() {
        return node;
    }

    public void setNode(Parent node) {
        this.node = node;
    }

    @Override
    public boolean wasGarbageCollected() {
        return (editorEnum.get() == null);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.editorEnum.get());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    public void bind() {
        updateEnum(editorEnum.get().get());
        editorEnum.get().addListener(editorListener);
        bound = true;
    }

    protected void updateEnum(T value) {
        if (node.getParent() == null) {
            return;
        }
        Method m;
        
        try {
            String setname = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            m = node.getParent().getClass().getMethod(setname, new Class[]{Node.class, enumType});
            if (m != null) {
                m.invoke(node.getParent(), new Object[]{node, value});
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }

        /*        if (node.getParent() instanceof HBox) {
            HBox.setMargin(node, update);
        } else if (node.getParent() instanceof VBox) {
            VBox.setMargin(node, update);
        } else if (node.getParent() instanceof StackPane) {
            StackPane.setMargin(node, update);
        } else if (node.getParent() instanceof FlowPane) {
            FlowPane.setMargin(node, update);
        } else if (node.getParent() instanceof BorderPane) {
            BorderPane.setMargin(node, update);
        } else if (node.getParent() instanceof GridPane) {
            GridPane.setMargin(node, update);
        } else if (node.getParent() instanceof AnchorPane) {
            updateAnchoPaneMargin(value);
        } else if ( setMethods.length > 0 ) {
            try {
                setMethods[0].invoke(node.getParent(), new Object[] {node,value});
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            }
        } else {
            Method m;
            try {
                m = node.getParent().getClass().getMethod("setMargin", new Class[] {Node.class, Insets.class});
                if ( m != null ) {
                    m.invoke(node.getParent(), new Object[] {node,value});
                }    
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            }
        }
         */
    }

    protected void updateAnchoPaneMargin(Insets insets) {
        Double update = insets.getTop() == 0 ? null : insets.getTop();
        AnchorPane.setTopAnchor(node, update);
        update = insets.getRight() == 0 ? null : insets.getRight();
        AnchorPane.setRightAnchor(node, update);
        update = insets.getBottom() == 0 ? null : insets.getBottom();
        AnchorPane.setBottomAnchor(node, update);
        update = insets.getLeft() == 0 ? null : insets.getLeft();
        AnchorPane.setLeftAnchor(node, update);
    }

    public void unbind() {
        editorEnum.get().removeListener(editorListener);
        setMethods = new Method[0];
        node = null;
        enumType = null;
        bound = false;
    }

    public boolean isBound() {
        return bound;
    }

    public static boolean isSupported(Class<?> clazz) {
        boolean retval = (HBox.class.isAssignableFrom(clazz) || VBox.class.isAssignableFrom(clazz)
                || StackPane.class.isAssignableFrom(clazz) || BorderPane.class.isAssignableFrom(clazz)
                || FlowPane.class.isAssignableFrom(clazz) || GridPane.class.isAssignableFrom(clazz)
                || AnchorPane.class.isAssignableFrom(clazz));
        if (retval) {
            return retval;
        }
        try {
            Method m = clazz.getMethod("setMargin", new Class[]{Node.class, Insets.class});
            if (m != null) {
                retval = true;
            }
        } catch (NoSuchMethodException | SecurityException ex) {

        }
        return retval;
    }
}
