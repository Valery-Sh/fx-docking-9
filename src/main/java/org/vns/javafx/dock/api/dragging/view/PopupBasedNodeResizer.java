/*
 * Copyright 2017 Your Organisation.
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class PopupBasedNodeResizer  implements EventHandler<MouseEvent> {


    private Window nodeWindow;
    private Popup popup;
    
    private boolean applyTranslateXY;
    private boolean hideOnMouseRelease;


    private Region node;
    private Group group;
    
    private Region indicator;
    private Parent indicatorPane;
    
    //private NodeResizer2.NodeLayout nodeLayout;

    public PopupBasedNodeResizer(Region node) {
        this.node = node;
        init();
    }
    private void init() {
        //popup = new Popup();
    }
    public Popup getWindow() {
        return popup;
    } 

    public Window show() {
        Window newOwner = getNode().getScene().getWindow();
        if (getWindow() != null && nodeWindow == newOwner) {
            hide();
        } else if (nodeWindow != newOwner) {
            hide();
            popup = new Popup();
        }
        //indicatorPane = new Group();
        //indicatorPane.setStyle("-fx-background-color: aqua;");
        //group = new Group();
        

        //StackPane sp = new StackPane();
        
        //sp.getChildren().add(pn);
        Platform.runLater(()-> {
//            sp.setManaged(false);            
        });
        indicator = new Pane();
        indicator.setPrefSize(getNode().getWidth() + 10, getNode().getHeight() + 10);
        popup.getContent().add(indicator);
        bindIndicatorDimensions();
        
//        indicator = new Rectangle(0,0, getNode().getWidth(),getNode().getHeight());
        
        //((Group)indicatorPane).getChildren().add(indicator);        

        indicator.setStyle("-fx-border-width: 5; -fx-border-color: red;-fx-fill: transparent;");        
        indicator.applyCss();
        
        
        bindIndicatorDimensions();

        popup.show(newOwner, newOwner.getWidth(), newOwner.getHeight());
//        System.err.println("stroke width = " + r.getStrokeWidth());

        bindWindowPosition(newOwner);
        bindWindowDimensions(newOwner);
        relocateIndicator();
        
        
        
        return popup;
    }
    
    
    public void relocateIndicator() {
        Bounds sb = getNode().localToScreen(getNode().getLayoutBounds());
        //Bounds lb = group.screenToLocal(sb);

        double x = sb.getMinX() - getWindow().getX() - 5;
        double y = sb.getMinY() - getWindow().getY() - 5;
        //System.err.println("node.w = " + sb.getWidth()+ "; r.w=" + ((Rectangle)indicator).getWidth());
        //System.err.println("group.w = " + group.getLayoutBounds().getWidth());        
//        System.err.println("x = " + x + "; y=" + y);
        indicator.relocate(x,y);
        
    }
    public void hide() {
        if (getWindow() != null) {
            ((Window) getWindow()).hide();
        }
    }
/*    protected void bindIndicatorPanePosition() {
        getWindow().xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                //windowBounds(getWindow(), getNode());
                
                getWindow().setX((Double)newValue);
            }
        });
        getWindow().yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                //windowBounds(getWindow(), getNode());
                getWindow().setY((Double)newValue);                
            }
        });

    }
*/    
    protected void bindNodePosition(Window owner) {
        owner.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                //windowBounds(getWindow(), getNode());
                getWindow().setX((Double)newValue);
            }
        });
        owner.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                //windowBounds(getWindow(), getNode());
                getWindow().setY((Double)newValue);                
            }
        });

    }
    
    protected void bindWindowPosition(Window owner) {
        if ( owner.isShowing() ) {
            getWindow().setX(owner.getX());
            getWindow().setY(owner.getY());
        }

        owner.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                //windowBounds(getWindow(), getNode());
                getWindow().setX((Double)newValue);
            }
        });
        owner.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                //windowBounds(getWindow(), getNode());
                getWindow().setY((Double)newValue);                
            }
        });

    }

    protected void bindWindowDimensions(Window owner) {
        if ( owner.isShowing() ) {
            getWindow().setWidth(owner.getWidth());
            getWindow().setHeight(owner.getHeight());
        }
        owner.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //windowBounds(getWindow(), getNode());
                getWindow().setWidth((Double)newValue);
            }
        });
        owner.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //windowBounds(getWindow(), getNode());
                getWindow().setHeight((Double)newValue);
            }
        });

    }
    protected void bindIndicatorDimensions() {
        getNode().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //windowBounds(getWindow(), getNode());
                //getWindow().setWidth((Double)newValue);
                indicator.setPrefWidth((Double)newValue + 10);
            }
        });
        getNode().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //windowBounds(getWindow(), getNode());
                indicator.setPrefHeight((Double)newValue + 10);
            }
        });

    }
    
    @Override
    public void handle(MouseEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isApplyTranslateXY() {
        return applyTranslateXY;
    }

    public void setApplyTranslateXY(boolean applyTranslateXY) {
        this.applyTranslateXY = applyTranslateXY;
    }

    public boolean isHideOnMouseRelease() {
        return hideOnMouseRelease;
    }

    public void setHideOnMouseRelease(boolean hideOnMouseRelease) {
        this.hideOnMouseRelease = hideOnMouseRelease;
    }

    public Region getNode() {
        return node;
    }

    
}
