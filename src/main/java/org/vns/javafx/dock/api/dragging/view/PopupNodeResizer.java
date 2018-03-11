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
package org.vns.javafx.dock.api.dragging.view;

import javafx.geometry.Bounds;
import javafx.scene.control.PopupControl;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class PopupNodeResizer extends WindowNodeResizer {

    protected PopupNodeResizer() {
        super();
        //setWindow(new PopupControl());
    }
    
    public static WindowNodeResizer getInstance() {
        return SingletonInstance.instance;
    }

    @Override
    protected void createWindow() {
        setWindow(new PopupControl());
        //setWindow(new Popup());
    }
    
    @Override
    protected void setWindowSize(Bounds bounds, double borderWidth, double borderHeight) {
        //getWindow().setHeight(bounds.getHeight() + 2 * borderHeight);
        //getWindow().setHeight(bounds.getWidth() + 2 * borderWidth);
    }

    @Override
    protected void initScene() {
        getWindow().getScene().setRoot(getRoot());
    }

    @Override
    protected void doShow(Window owner) {
        ((PopupControl) getWindow()).show(owner);
    }
    
    private static class SingletonInstance {
        private static final PopupNodeResizer instance = new PopupNodeResizer();
    }
}
