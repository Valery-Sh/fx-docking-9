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
package org.vns.javafx.designer.descr;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * The class is intended to describe the properties of an object that an 
 * application can use to build  the hierarchical structure of an object.
 * Why not just use the Refection API? Let us take for example the BorderPane node.
 * As we know, it inherits from Pane. On the one hand, as a successor to Pane, 
 * it has an ObservableList property named children. On the other hand, this 
 * property is practically useless, since for normal operation, the properties 
 * top, right, bottom, left and center are required. And we can not determine it
 * using Reflection.
 * <P> 
 * This class is mainly used when constructing a {@link org.vns.javafx.designer.SceneView} object.
 * </p>
 * 
 * @author Valery Shyshkin
 */
@DefaultProperty("properties")
public class NodeDescriptor {

    private String type;
    private String styleClass;
    private String defaultProperty;
    private String annotationDefaultProperty;

    /**
     * Contains a name of the property which value can be used as a title in a
     * TreeItem
     */
    private String titleProperty;

    private final ObservableList<NodeProperty> properties = FXCollections.observableArrayList();
    
    /**
     * Creates an instance of the class.
     */
    public NodeDescriptor() {
        init();
    }
    
    private void init() {
        properties.addListener(this::propertiesChanged);
    }
    /**
     * The convenient method to find the {@code NodeDescriptor}.
     * Just redirects to {@link NodeDescriptorRegistry#getDescriptor(java.lang.Class) }.
     * 
     * @param clazz the class used as a key
     * @return the object of this type or null if the search object doesn't exist.
     */
    public static NodeDescriptor get(Class<?> clazz) {
        return NodeDescriptorRegistry.getInstance().getDescriptor(clazz);
    }

    protected void propertiesChanged(ListChangeListener.Change<? extends NodeProperty> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                List<NodeProperty> list = (List<NodeProperty>) change.getAddedSubList();
                for (NodeProperty elem : list) {
                    elem.setDescriptor(this);
                }
            }
        }//while
    }
    /**
     * Return the collection of properties described by this object.
     * @return the collection of type {@code ObservableList&lt;NodeProperty&gt;} 
     * containing the properties described by this object.
     */
    public ObservableList<NodeProperty> getProperties() {
        return properties;
    }
    /**
     * Returns the fully qualified name of the class of objects this class describes. 
     * @return the fully qualified name of the class of objects this class describes. 
     */
    public String getType() {
        return type;
    }
    /**
     * Sets the fully qualified name of the class of objects this class describes. 
     * @param type the new fully qualified name of the class of objects this class describes. 
     */
    public void setType(String type) {
        this.type = type;
        
        try {
            Class clazz = Class.forName(type);
            DefaultProperty[] dp = (DefaultProperty[]) clazz.getAnnotationsByType(DefaultProperty.class);
            if (dp.length > 0) {
                annotationDefaultProperty = dp[0].value();
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("EXCEPTION. " + ex.getMessage());
            Logger.getLogger(NodeDescriptor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /**
     * Returns the text which can be used as a style class for a {@code styleable}
     * for visual representation of the descriptor.
     * 
     * @return the text that can be used as a style class for a {@code styleable}
     * for visual representation of the descriptor.
     */
    public String getStyleClass() {
        return styleClass;
    }
    /**
     * Sets the string value which can be used as a style class for a {@code styleable}
     * for visual representation of the descriptor.
     * @param styleClass the new styleClass to be set
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    /**
     * Returns the name of the property belonging to the class that this 
     * descriptor defines and which can be used to visually represent this descriptor.
     * For example the {@code Labeled} objects has the property named {@code text} and
     * the value of the property can be used to visually represent this descriptor.
     * 
     * @return the name of the property belonging to the class that this 
     * descriptor defines and which can be used to visually represent this descriptor.
     */
    public String getTitleProperty() {
        return titleProperty;
    }
    /**
     * Sets the name of the property belonging to the class that this 
     * descriptor defines and which can be used to visually represent this descriptor.
     * For example the {@code Labeled} objects has the property named {@code text} and
     * the value of the property can be used to visually represent this descriptor.
     * @param titleProperty the name of titleProperty to be set
     */
    public void setTitleProperty(String titleProperty) {
        this.titleProperty = titleProperty;
    }
    /**
     * Returns the name of the property belonging to the class that this 
     * descriptor defines and which is considered to be default.
     * For example the class {@code TitledPane} annotated with {@code &commat;DefaultProperty(content)}
     * annotation.The instance of this class for the {@code TitledPane} takes into account 
     * the annotation but we can change it to for instance {@code graphic} property.
     * 
     * @return the name of the property belonging to the class that this 
     * descriptor defines and which is considered to be default.
     */
    public String getDefaultProperty() {
        return defaultProperty;
    }

    /**
     * Returns the value of the {@code &commat;DefaultProperty} annotation.
     * 
     * @return the value of the {@code &commat;DefaultProperty} annotation.
     */
    protected String getAnnotationDefaultProperty() {
        return annotationDefaultProperty;
    }
    /**
     * Sets the value of the {@code &commat;DefaultProperty} annotation.
     * @param annotationDefaultProperty the value to be set
     */
    protected void setAnnotationDefaultProperty(String annotationDefaultProperty) {
        this.annotationDefaultProperty = annotationDefaultProperty;
    }
    /**
     * Sets the name of the property belonging to the class that this 
     * descriptor defines and which is considered to be default.
     * For example the class {@code TitledPane} annotated with {@code &commat;DefaultProperty(content)}
     * annotation.The instance of this class for the {@code TitledPane} takes into account 
     * the annotation but we can change it to for instance {@code graphic} property.
     * @param defaultProperty the new value to be set
     */
    public void setDefaultProperty(String defaultProperty) {
        this.defaultProperty = defaultProperty;
    }
    /**
     * Returns the object of type {@link NodeProperty} by the given name.
     * @param propertyName the name of the property to be returned
     * @return the object of type {@link NodeProperty} by the given name.
     */
    public NodeProperty getProperty(String propertyName) {
        NodeProperty retval = null;
        for (NodeProperty p : properties) {
            if (p.getName().equals(propertyName)) {
                retval = p;
                break;
            }
        }
        return retval;
    }
    /**
     * Returns an index of the element of the collection of elements 
     * of type {@link NodeProperty} by the given name.
     * 
     * @param propertyName the name of the property to define the index
     * @return an integer index of the element of the collection of elements 
     * of type NodeProperty by the given name.
     */
    public int indexOf(String propertyName) {
        int retval = -1;
        for (int i = 0; i < properties.size(); i++) {

            if (properties.get(i).getName().equals(propertyName)) {
                retval = i;
                break;
            }
        }
        return retval;
    }
    /**
     * Returns an object of type {@link NodeContent} if such exists and which 
     * is considered to be default property.
     * @return an object of type {@link NodeContent} if such exists and which 
     * is considered to be default property.
     */
    public NodeProperty getDefaultContentProperty() {
        return calculateDefaultContentProperty();
    }
    /**
     * Returns an object of type {@link NodeList} if such exists and which 
     * is considered to be default property.
     * @return an object of type {@link NodeList} if such exists and which 
     * is considered to be default property.
     */    
    public NodeProperty getDefaultListProperty() {
        return calculateDefaultListProperty();
    }

    private NodeProperty calculateDefaultContentProperty() {
        NodeProperty retval = null;
        if (getProperties().size() == 1) {
            if (getProperties().get(0) instanceof NodeContent) {
                retval = getProperties().get(0);
            }
        }
        NodeProperty p = null;
        if (retval == null && getDefaultProperty() != null) {
            p = getProperty(getDefaultProperty());
        } else if (getDefaultProperty() == null) {
            p = getProperty(getAnnotationDefaultProperty());
        }
        if (p != null && (p instanceof NodeContent)) {
            retval = p;
        }

        return retval;
    }
    private NodeProperty calculateDefaultListProperty() {
        NodeProperty retval = null;
        if (getProperties().size() == 1) {
            if (getProperties().get(0) instanceof NodeList) {
                retval = getProperties().get(0);
            }
        }
        NodeProperty p = null;
        if (retval == null && getDefaultProperty() != null) {
            p = getProperty(getDefaultProperty());
        } else if (getDefaultProperty() == null) {
            p = getProperty(getAnnotationDefaultProperty());
        }
        if (p != null && (p instanceof NodeList)) {
            retval = p;
        }

        return retval;
    }


}
