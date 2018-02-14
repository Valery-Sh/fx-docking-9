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

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Window;
import org.vns.javafx.dock.api.dragging.view.FloatView;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty("value")
public class DragContainer { //extends Control implements Dockable{

    //private Dockable owner;

    private final ObjectProperty value = new SimpleObjectProperty();

    private Node placeholder;

/*    public DragContainer(Dockable owner, Object value) {
        
        this(owner, value, true);
    }
*/
    public DragContainer(Node placeholder, Object value) {
        this.value.set(value);
        this.placeholder = placeholder;
        
        makePlaceholderDockable();
        makePlaceholderContainer();
    }
    
    protected DragContainer(Object value) {
        this.value.set(value);
    }

    public ObjectProperty valueProperty() {
        return value;
    }

    public Object getValue() {
        return value.get();
    }

    public void setValue(Object obj) {
        this.value.set(obj);
    }

    public boolean isValueDockable() {
        return value != null && Dockable.of(value.get()) != null;
    }

    public Window getFloatingWindow(Dockable dockable) {
        
        if ( getPlaceholder() == null ) {
            return dockable.node().getScene().getWindow();
        }
        if (getPlaceholder().getScene() == null || getPlaceholder().getScene().getWindow() == null) {
            return null;
        }
        if (!FloatView.isFloating(getPlaceholder())) {
            return null;
        }
        return getPlaceholder().getScene().getWindow();
    }
    
    /**
     * Creates default placeholder.
     * @param value the value used as a source to create placeholder
     * @return new placeholder node
     */
    public static Node placeholderOf(Object value) {
        Node placeholder;
        if (Dockable.of(value) != null) {
            placeholder = Dockable.of(value).node();
        } else if ((value instanceof Node)) {
            Pane p = new Pane();
            p.getChildren().add((Node)value);
            Scene sc = new Scene(p);
            ImageView im = new ImageView(((Node)value).snapshot(null, null));
            placeholder = im;
        } else {
            placeholder = new Rectangle(75, 25);
            placeholder.setOpacity(0.3);
            ((Shape) placeholder).setFill(Color.YELLOW);
            ((Shape) placeholder).setStroke(Color.BLACK);
            ((Shape) placeholder).setStrokeWidth(1);
            ((Shape) placeholder).getStrokeDashArray().addAll(2.0, 2.0, 2.0, 2.0);
            ((Shape) placeholder).setStrokeDashOffset(1.0);
        }
        return placeholder;
    }

    private void makePlaceholderDockable() {
        if (Dockable.of(placeholder) != null) {
            return;
        }
        DockRegistry.makeDockable(placeholder);
    }

    private void makePlaceholderContainer() {
        Dockable d = Dockable.of(getPlaceholder());
        DragContainer dc = d.getContext().getDragContainer();
        if (dc == null) {
            dc = new DragContainer(getValue());
            d.getContext().setDragContainer(dc);
        }
    }

    public Node getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(Node placeholder) {
        this.placeholder = placeholder;
        if (getPlaceholder() == null) {
            return;
        }
        if (Dockable.of(getPlaceholder()) == null) {
            makePlaceholderDockable();
            makePlaceholderContainer();
        } else if (Dockable.of(getPlaceholder()).getContext().getDragContainer() == null) {
            makePlaceholderContainer();
        }
    }

/*    public Dockable getOwner() {
        return owner;
    }

    public void setOwner(Dockable owner) {
        this.owner = owner;
    }
*/
}
