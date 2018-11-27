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
package org.vns.javafx.designer.demo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 *
 * @author Valery
 */
public class MyVBox extends VBox {
    private ObjectProperty<Node> title = new SimpleObjectProperty<>();

    public MyVBox() {
    }

    public MyVBox(double spacing) {
        super(spacing);
    }

    public MyVBox(Node... children) {
        super(children);
    }

    public MyVBox(double spacing, Node... children) {
        super(spacing, children);
    }
    public ObjectProperty<Node> titleProperty() {
        return title;
    }
    public Node getTitle() {
        return title.get();
    }

    public void setTitle(Node title) {
        this.title.set(title);
    }
    
}
