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

    private Dockable owner;

    private final ObjectProperty value = new SimpleObjectProperty();

    private Node placeholder;

    public DragContainer(Dockable owner, Object value) {
        this(owner, value, true);
    }

    protected DragContainer(Dockable owner, Object value, boolean needContainer) {
        this.owner = owner;
        this.value.set(value);
        createDefaultGraphic();
        makeDockable();
        if ( needContainer ) {
            makeContainer();
        }
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

    public Window getFloatingWindow() {
        if (getPlaceholder().getScene() == null || getPlaceholder().getScene().getWindow() == null) {
            return null;
        }
        if (!FloatView.isFloating(getPlaceholder())) {
            return null;
        }
        return getPlaceholder().getScene().getWindow();
    }

    private void createDefaultGraphic() {
        if (Dockable.of(getValue()) != null) {
            placeholder = Dockable.of(getValue()).node();
        } else if ((getValue() instanceof Node)) {
            Pane p = new Pane();
            p.getChildren().add((Node) getValue());
            Scene sc = new Scene(p);
            ImageView im = new ImageView(((Node) getValue()).snapshot(null, null));
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
    }

    private void makeDockable() {
        Dockable d = Dockable.of(placeholder);
        if (d != null) {
            return;
        }
        d = DockRegistry.makeDockable(placeholder);
    }

    private void makeContainer() {
        Dockable d = Dockable.of(getPlaceholder());
        DragContainer dc = d.getContext().getDragContainer();
        if (dc == null) {
            dc = new DragContainer(d, getValue(), false);
            d.getContext().setDragContainer(dc);
            dc.setPlaceholder(getPlaceholder());
        }
    }
    
    public Node getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(Node placeholder) {
        this.placeholder = placeholder;
        if ( getPlaceholder() == null ) {
            return;
        }
        if ( Dockable.of(getPlaceholder()) == null ) {
            makeDockable();
            makeContainer();
        } else if ( Dockable.of(getPlaceholder()).getContext().getDragContainer() == null  ) {
            makeContainer();
        }
    }

    public Dockable getOwner() {
        return owner;
    }

    public void setOwner(Dockable owner) {
        this.owner = owner;
    }

}
