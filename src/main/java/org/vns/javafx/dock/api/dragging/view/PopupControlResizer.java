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

import javafx.scene.Cursor;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class PopupControlResizer extends StageResizer {

    public PopupControlResizer(FloatWindowView windowView) {
        super(windowView);
    }

    @Override
    public void resize(double x, double y) {
        PopupControl pc = (PopupControl) getWindow();
        Region root = (Region) pc.getScene().getRoot();

        double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;

        double curX = getMouseX();
        double curY = getMouseY();

        if (getCursor() == Cursor.S_RESIZE) {
            hDelta = y - this.getMouseY();
            curY = y;
        } else if (getCursor() == Cursor.E_RESIZE) {
            wDelta = x - this.getMouseX();
            curX = x;
        } else if (getCursor() == Cursor.N_RESIZE) {
            hDelta = getMouseY() - y;
            yDelta = -hDelta;
            curY = y;
        } else if (getCursor() == Cursor.W_RESIZE) {

            wDelta = getMouseX() - x;
            xDelta = -wDelta;
            curX = x;
        } else if (getCursor() == Cursor.SE_RESIZE) {
            hDelta = y - getMouseY();
            curY = y;
            wDelta = x - getMouseX();
            curX = x;

        } else if (getCursor() == Cursor.NE_RESIZE) {
            hDelta = getMouseY() - y;
            wDelta = x - getMouseX();
            yDelta = -hDelta;
            curX = x;
            curY = y;
        } else if (getCursor() == Cursor.SW_RESIZE) {
            hDelta = y - getMouseY();
            wDelta = getMouseX() - x;
            xDelta = -wDelta;
            curX = x;
            curY = y;
        } else if (getCursor() == Cursor.NW_RESIZE) {
            hDelta = getMouseY() - y;
            wDelta = getMouseX() - x;
            xDelta = -wDelta;
            yDelta = -hDelta;
            curX = x;
            curY = y;
        }
        double w = -1;
        double h = -1;
//        if (wDelta + pc.getWidth() > ((PopupControl) getWindow()).getWidth()) {

        if ((xDelta != 0 || wDelta != 0) && wDelta + getWindow().getWidth() != ((PopupControl) getWindow()).getWidth()) {

            Region child = (Region) ((Pane) root).getChildren().get(0);
            double childMin = child.minWidth(-1);
//            System.err.println("childMin = " + childMin);         
//            System.err.println("childWidth = " + child.getWidth());         
//            System.err.println("wDelta = " + wDelta);         
            
            if (child.getWidth() > childMin || wDelta > 0 && child.getWidth() == childMin) {
                pc.setAnchorX(xDelta + pc.getAnchorX());
                root.setPrefWidth(wDelta + root.getWidth());
                root.setMinWidth(wDelta + root.getWidth());
                setMouseX(curX);
                pc.sizeToScene();
            }
        }

        if ((yDelta != 0 || hDelta != 0) && hDelta + getWindow().getHeight() != ((PopupControl) getWindow()).getHeight()) {

            Region child = (Region) ((Pane) root).getChildren().get(0);
            double childMin = child.minHeight(-1);
            
            if (child.getHeight() > childMin || hDelta > 0 && child.getHeight() == childMin) {
                pc.setAnchorY(yDelta + pc.getAnchorY());
                root.setPrefHeight(hDelta + root.getHeight());
                root.setMinHeight(hDelta + root.getHeight());
                setMouseY(curY);
                pc.sizeToScene();
            }

        }
    }

    @Override
    public void start(MouseEvent ev, Window stage,
             Cursor cursor, Cursor... supportedCursors
    ) {
        super.start(ev, stage, cursor, supportedCursors);
    }
}
