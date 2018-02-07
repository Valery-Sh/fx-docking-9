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
    
    //private DockableContext context;
    
    private final ObjectProperty value = new SimpleObjectProperty();
    
    private Node graphic;
    
    //private Point2D mousePosition;
    

    public ObjectProperty valueProperty() {
        return value;
    }
    public Object getValue() {
        return value.get();
    }

    public void setValue(Object obj) {
        if ( obj != null && DockRegistry.isDockable(obj) ) {
            //Dockable.of(obj).getDockableContext().setFloating(true);
        }
        this.value.set(obj);
    }
    
    public boolean isValueDockable() {
        if ( value == null ) {
            return false;
        }
        boolean retval = DockRegistry.isDockable(value.get());//(getValue() instanceof Dockable) || ((getValue() instanceof Node ) && DockRegistry.isDockable((Node)getValue()));  
        
        return retval;
    }

    public Window getFloatingWindow() {
        if ( getValue() == null ) {
            return null;
        }
        if ( getGraphic().getScene() == null || getGraphic().getScene().getWindow() == null) {
            return null;
        }
        if ( ! FloatView.isFloating(getGraphic())) {
            return null;
        }
        return getGraphic().getScene().getWindow();
    }


    public Node getGraphic() {
        if ( graphic == null && getValue() != null &&  Dockable.of(getValue()) != null) {
            graphic = Dockable.of(getValue()).node(); 
        } if ( graphic == null && getValue() != null && (getValue() instanceof Node )) {
            graphic = (Node) getValue(); 
        } else if ( graphic == null ) {
            graphic = new Rectangle(75, 25);
            graphic.setOpacity(0.3);
            ((Shape)graphic).setFill(Color.YELLOW);
            ((Shape)graphic).setStroke(Color.BLACK);
            ((Shape)graphic).setStrokeWidth(1);
            ((Shape)graphic).getStrokeDashArray().addAll(2.0, 2.0, 2.0, 2.0);
            ((Shape)graphic).setStrokeDashOffset(1.0);            
        }
        return graphic;
    }

    public void setGraphic(Node graphic) {
        this.graphic = graphic;
    }
    
}
