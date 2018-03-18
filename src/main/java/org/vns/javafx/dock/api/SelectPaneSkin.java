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

import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author Valery
 */
public class SelectPaneSkin extends SkinBase {

    private Pane pane;
    private Rectangle rect;
    double rectStrokeWidth = 3;
    Paint rectFill = Color.YELLOW;
    Paint rectStroke = Color.RED;

    public SelectPaneSkin(Control control) {
        super(control);
    }

/*    public SelectPaneSkin(Control control) {
        
        init();
        
        getChildren().add(pane);

    }
*/
    private void init() {
        
        rect = new Rectangle(10, 10, 70, 30);
        rect.setFill(rectFill);
        rect.setStroke(rectStroke);
        rect.setStrokeType(StrokeType.INSIDE);
        rect.setStrokeWidth(rectStrokeWidth);

        pane.getChildren().add(rect);
    }

    protected void initPane() {
/*        pane = new Pane() {
            SelectPane sp;
            { sp = getSkinnable();}
             
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                System.err.println("SelectPaneSkin layoutChildren: ");
            }

            @Override
            protected double computeMinWidth(double height) {
                double retval = sp.getMinWidth();
                System.err.println("SelectPaneSkin: computeMinWidth = " + retval);
                
                return retval;
            }

            @Override
            protected double computeMinHeight(double width) {
                double retval = sp.getMinHeight();
                System.err.println("SelectPaneSkin: computeMinHeight = " + retval);
                return retval;                
            }

            @Override
            protected double computePrefWidth(double height) {
                double retval = sp.getPrefWidth();
                System.err.println("SelectPaneSkin: computePrefWidth = " + retval);
                return retval;                
            }

            @Override
            protected double computePrefHeight(double width) {
                
                double retval = sp.getPrefHeight();
                System.err.println("SelectPaneSkin: computePrefHeight = " + retval);
                //retval = 70;
                
                return retval;                
            }

            @Override
            protected double computeMaxWidth(double height) {
                double retval = sp.getMaxWidth();
                //retval = 150;
                System.err.println("SelectPaneSkin: computeMaxWidth = " + retval);
                return retval;                
            }

            @Override
            protected double computeMaxHeight(double width) {
                double retval = sp.getMaxHeight();
                //retval = 150;
                System.err.println("SelectPaneSkin: computeMaxHeight = " + retval);
                return retval;                
            }

        };
        //pane.setMinWidth(70);
        //pane.setMinHeight(70);
        //pane.setManaged(false);
        pane.setStyle("-fx-background-color: aqua; -fx-border-width: 2; -fx-border-color: blue");
*/        

    }
    
  /**
     * Invoked during the layout pass to layout the children in this
     * {@code Parent}. By default it will only set the size of managed,
     * resizable content to their preferred sizes and does not do any node
     * positioning.
     * <p>
     * Subclasses should override this function to layout content as needed.
     */
/*    protected void layoutChildren() {
        for (int i=0, max=children.size(); i<max; i++) {
            final Node node = children.get(i);
            if (node.isResizable() && node.isManaged()) {
                node.autosize();
            }
        }
    }    
*/
}
