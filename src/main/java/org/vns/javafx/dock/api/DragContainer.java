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
import javafx.scene.control.Control;

/**
 *
 * @author Valery
 */
@DefaultProperty("value")
public class DragContainer extends Control implements Dockable{
    
    private DockableContext context;
    
    private ObjectProperty value = new SimpleObjectProperty();
    
    @Override
    public Node node() {
        return this;
    }

    @Override
    public DockableContext getDockableContext() {
        if ( context == null ) {
            context = new DockableContext(this);
        }
        return context;
    }
    
    public ObjectProperty valueProperty() {
        return value;
    }
    public Object getValue() {
        return value.get();
    }

    public void setValue(Object obj) {
        if ( obj != null && DockRegistry.isDockable(obj) ) {
            Dockable.of(obj).getDockableContext().setFloating(true);
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
}
