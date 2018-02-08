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

import com.sun.istack.internal.NotNull;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
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
 * @author Valery
 */
@DefaultProperty("value")
public class DragContainer { //extends Control implements Dockable{

    private boolean valueDockable;
    
    private Dockable carrier;
    
    private final ObjectProperty value = new SimpleObjectProperty();

    private Node graphic;

    public DragContainer(Object value) {
         setValue(value);
    }


    
    public ObjectProperty valueProperty() {
        return value;
    }

    public Object getValue() {
        return value.get();
    }

    public void setValue(Object obj) {
        if (obj == null || ! DockRegistry.isDockable(obj)) {
            valueDockable = false;
        } else if (obj != null && ! DockRegistry.isDockable(obj)) {
            valueDockable = false;
        } else {
            valueDockable = true;
        }
        System.err.println("VALUE DOCKABLE = " + valueDockable);        
        this.value.set(obj);
    }

    public boolean isValueDockable() {
        return valueDockable;
    }

    public Window getFloatingWindow() {
        if (getValue() == null) {
            return null;
        }
        if (getGraphic().getScene() == null || getGraphic().getScene().getWindow() == null) {
            return null;
        }
        if (!FloatView.isFloating(getGraphic())) {
            return null;
        }
        return getGraphic().getScene().getWindow();
    }

    public Node getGraphic() {
        if (graphic == null && getValue() != null && Dockable.of(getValue()) != null) {
            graphic = Dockable.of(getValue()).node();
            return graphic;
        }
        if (graphic == null && getValue() != null && (getValue() instanceof Node)) {
            Pane p = new Pane();
            p.getChildren().add((Node)getValue());
            Scene sc = new Scene(p);
            ImageView im = new ImageView(((Node) getValue()).snapshot(null,null));
            graphic = im;
            //graphic = (Node) getValue();
        } else if (graphic == null) {
            graphic = new Rectangle(75, 25);
            graphic.setOpacity(0.3);
            ((Shape) graphic).setFill(Color.YELLOW);
            ((Shape) graphic).setStroke(Color.BLACK);
            ((Shape) graphic).setStrokeWidth(1);
            ((Shape) graphic).getStrokeDashArray().addAll(2.0, 2.0, 2.0, 2.0);
            ((Shape) graphic).setStrokeDashOffset(1.0);
        }
        Dockable d = Dockable.of(graphic);
        if (d == null) {
            d = DockRegistry.getInstance().registerDefault(graphic);
        }
        DragContainer dc = d.getDockableContext().getDragContainer();
        if (dc == null) {
            dc = new DragContainer(getValue());
            d.getDockableContext().setDragContainer(dc);
        }
        
        dc.setCarrier(d);
        dc.setGraphic(graphic);
        
        return graphic;
    }

    public void setGraphic(Node graphic) {
        this.graphic = graphic;
    }

    public Dockable getCarrier() {
        return carrier;
    }

    public void setCarrier(Dockable carrier) {
        this.carrier = carrier;
    }

}
