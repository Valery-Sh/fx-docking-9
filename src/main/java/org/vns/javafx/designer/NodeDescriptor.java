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

import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Valery
 */
@DefaultProperty("contentProperties")
public class NodeDescriptor {
    
    private String nodeClass;
    private String styleClass;
            
    /**
     * Contains a name of the property which value can be used as a title 
     * in a TreeItem
     */
    private String titleProperty;
    
    private final ObservableList<Property> contentProperties = FXCollections.observableArrayList();

    public NodeDescriptor() {
    }


    public ObservableList<Property> getContentProperties() {
        return contentProperties;
    }

    public String getNodeClass() {
        return nodeClass;
    }

    public void setNodeClass(String nodeClass) {
        this.nodeClass = nodeClass;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getTitleProperty() {
        return titleProperty;
    }

    public void setTitleProperty(String titleProperty) {
        this.titleProperty = titleProperty;
    }
    
}
