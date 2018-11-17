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

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class PopupRectangleFrame extends RectangleFrame { //implements NodeFraming{

    private Pane popupRoot;
  

    public PopupRectangleFrame() {
        super();
        getStyleClass().add("popup-rectangle-frame");
        init();
    }

    private void init() {
        setPopup(new Popup());
        popupRoot = new StackPane(this);
        getPopup().getScene().setRoot(popupRoot);
        //popupRoot.getChildren().add(this);
    }

    @Override
    public void setDefaultStyles() {
        setStyle("-fx-stroke-type: inside; -fx-stroke: rgb(255, 148, 40); -fx-stroke-width: 2; -fx-fill: transparent");
    }

    @Override
    public void show() {
        if (getBoundNode() != null) {
/*            Platform.runLater(() -> {
                System.err.println("1 getWidth() = " + getWidth());
                System.err.println("getStrokeWidth = " + getStrokeWidth());
                setWidth(getBoundNode().getBoundsInParent().getWidth() + 2 * getStrokeWidth());
                
                System.err.println("boundsInParent().getWidth() = " + getBoundNode().getBoundsInParent().getWidth());
                System.err.println("getBoundNode().getWidth() = " + ((Region) getBoundNode()).getWidth());
                System.err.println("2 getWidth() = " + getWidth());
                System.err.println("getInsets = " + ((Region)getBoundNode()).getInsets());
                System.err.println("popup.width = " + getPopup().getWidth());
                System.err.println("popup.height = " + getPopup().getHeight());
                

                setHeight(getBoundNode().getBoundsInParent().getHeight() + 2 * getStrokeWidth());
                Bounds bnds = getBoundNode().localToScreen(getBoundNode().parentToLocal(getBoundNode().getBoundsInParent()));

                double x = (bnds.getMinX() - getStrokeWidth());
                double y = (bnds.getMinY() - getStrokeWidth());
                
            });
*/            
            //getPopup().show(getBoundNode(),x,y);
        
            getPopup().show(getBoundNode().getScene().getWindow());
            
            adjustBoundsToNode(getBoundNode().getBoundsInParent());
        }
        
    }

    @Override
    public void hide() {
        getPopup().hide();
    }

    @Override
    protected void adjustBoundsToNode(Bounds boundsInParent) {
        Platform.runLater(() -> {
                setWidth(getBoundNode().getBoundsInParent().getWidth());
                
/*                System.err.println("boundsInParent().getWidth() = " + getBoundNode().getBoundsInParent().getWidth());
                System.err.println("getBoundNode().getWidth() = " + ((Region) getBoundNode()).getWidth());
                System.err.println("2 getWidth() = " + getWidth());
                System.err.println("getInsets = " + ((Region)getBoundNode()).getInsets());
                System.err.println("popup.width = " + getPopup().getWidth());
                System.err.println("popup.height = " + getPopup().getHeight());
*/                

                setHeight(getBoundNode().getBoundsInParent().getHeight());
                Bounds bnds = getBoundNode().localToScreen(getBoundNode().parentToLocal(getBoundNode().getBoundsInParent()));

                getPopup().setX(bnds.getMinX());
                getPopup().setY(bnds.getMinY());

        });
    }

    protected void adjustBoundsToNode1(Bounds boundsInParent) {
        Platform.runLater(() -> {
/*            setWidth(getBoundNode().getBoundsInParent().getWidth());
            setHeight(getBoundNode().getBoundsInParent().getHeight());
            Bounds bnds = getBoundNode().localToScreen(getBoundNode().parentToLocal(getBoundNode().getBoundsInParent()));

            getPopup().setX(bnds.getMinX() - getStrokeWidth());
            getPopup().setY(bnds.getMinY() - getStrokeWidth());
*/
                System.err.println("root.Insets = " + ((StackPane)getScene().getRoot()).getPadding());
                
                System.err.println("1 getWidth() = " + getWidth());
                System.err.println("getStrokeWidth = " + getStrokeWidth());
                setWidth(getBoundNode().getBoundsInParent().getWidth() + 2 * getStrokeWidth());
                
                System.err.println("boundsInParent().getWidth() = " + getBoundNode().getBoundsInParent().getWidth());
                System.err.println("getBoundNode().getWidth() = " + ((Region) getBoundNode()).getWidth());
                System.err.println("2 getWidth() = " + getWidth());
                System.err.println("getInsets = " + ((Region)getBoundNode()).getInsets());
                System.err.println("popup.width = " + getPopup().getWidth());
                System.err.println("popup.height = " + getPopup().getHeight());
                

                setHeight(getBoundNode().getBoundsInParent().getHeight() + 2 * getStrokeWidth());
                Bounds bnds = getBoundNode().localToScreen(getBoundNode().parentToLocal(getBoundNode().getBoundsInParent()));

                getPopup().setX(bnds.getMinX() - getStrokeWidth());
                getPopup().setY(bnds.getMinY() - getStrokeWidth());

        });
    }

    /*    public ObjectProperty<SideShapes> sideShapeProperty() {
        return sideShapes;
    }

    public SideShapes getSideShapes() {
        return sideShapes.get();
    }

    public void setSideShapes(SideShapes sideShapes) {
        this.sideShapes.set(sideShapes);
    }
     */
}//class
