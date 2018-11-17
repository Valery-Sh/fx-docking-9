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

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Selection;

/**
 *
 * @author Olga
 */
public class RectangularFraming extends AbstractNodeFraming {
    
    public static final String RECT_ID = "DEFAULT-RECT-" + RectangleFrame.ID;
    
    private RectangleFrame rectangleFrame;

    private boolean applyCss;
    private String id;
    
    @Override
    protected void finalizeOnHide(Node node) {
        //super.finalizeOnHide(node);
        //rectangleFrame.setVisible(false);
        //finalizeNode();
        rectangleFrame.hide();
    }

    public RectangularFraming() {
        this(RECT_ID,false);
       
    }

    public RectangularFraming(boolean applyCss) {
        this(RECT_ID,applyCss);
    }

    public RectangularFraming(String rectId) {
        this(rectId,false);
       
    }

    public RectangularFraming(String rectId,boolean applyCss) {
        this.applyCss = applyCss;
        id = rectId;
    }

    public void setDefaultStyle() {
        setStyle("-fx-stroke-type: outside; -fx-stroke: rgb(255, 148, 40); -fx-stroke-width: 1; -fx-fill: white");
    }

    public RectangleFrame getRectangleFrame() {
        return rectangleFrame;
    }

    //protected void initializeOnShow(Node node) {
        
    //}
    @Override
    protected void initializeOnShow(Node node) {
        RectangleFrame shape = lookupShapeFraming();
        if (rectangleFrame != null && shape == null) {
            if ( ! (rectangleFrame instanceof PopupRectangleFrame) ) {
                ((Pane) node.getScene().getRoot()).getChildren().add(rectangleFrame);
            }
        } else if (rectangleFrame == null && shape != null) {
            rectangleFrame = shape;
        } else if (rectangleFrame == null && shape == null) {
            
            rectangleFrame = new RectangleFrame();    
            
            rectangleFrame.setId(id);
            ((Pane) node.getScene().getRoot()).getChildren().add(rectangleFrame);
            if (!applyCss) {
                if (getStyle() != null) {
                    rectangleFrame.setStyle(getStyle());
                } else {
                    rectangleFrame.setDefaultStyles();
                }
            } else {
                if (getStyle() != null) {
                    rectangleFrame.setStyle(getStyle());
                }
                if (!getStyleClass().isEmpty()) {
                    getStyleClass().forEach(s -> {
                        rectangleFrame.getStyleClass().add(s);
                    });
                }
            }
        }

        rectangleFrame.bind(node);
        rectangleFrame.show();

        Selection sel = DockRegistry.lookup(Selection.class);
        if (sel != null) {
            sel.notifySelected(node);
        }

    }

    protected RectangleFrame lookupShapeFraming() {
        return (RectangleFrame) getNode().getScene().getRoot().lookup("#"+ getId());
        //return (RectangleFrame) getNode().getScene().getRoot().lookup("." + RectangleFrame.ID);
    }

    public boolean isApplyCss() {
        return applyCss;
    }
    
    protected void setId(String id) {
        this.id = id;
    }
  

    protected String getId() {
        return id;
    }

}
