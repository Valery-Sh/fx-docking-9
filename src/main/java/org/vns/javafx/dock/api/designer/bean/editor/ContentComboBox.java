/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.designer.bean.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery
 */
public class ContentComboBox extends ComboBoxBase {

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();
    
    private final ReadOnlyObjectWrapper<Node> displayNode = new ReadOnlyObjectWrapper<>();
    
    public ContentComboBox() {
        getStyleClass().addAll("content-combo-box");
        init();
    }
    
    private void init() {
        setDisplayNode(new Label());
        getDisplayNode().getStyleClass().add("display-node");
    }
    
    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }
    
    public ReadOnlyObjectProperty<Node> displayNodeProperty() {
        return displayNode.getReadOnlyProperty();
    }
    public Node getDisplayNode() {
        return displayNode.getValue();
    }
    protected void setDisplayNode(Node displayNode) {
        this.displayNode.setValue(displayNode);
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new ContentComboBoxSkin(this);
    }

    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    public Node getContent() {
        return content.get();
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

}
