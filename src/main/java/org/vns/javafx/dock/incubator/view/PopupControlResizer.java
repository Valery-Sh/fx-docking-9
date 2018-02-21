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
package org.vns.javafx.dock.incubator.view;

import javafx.scene.Cursor;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.vns.javafx.dock.api.dragging.view.FloatWindowView;

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

        if ((xDelta != 0 || wDelta != 0) && wDelta + getWindow().getWidth() > ((PopupControl) getWindow()).getWidth()) {
            // if ((xDelta != 0 || wDelta != 0) ) {
            pc.setAnchorX(xDelta + pc.getAnchorX());
            pc.setMinWidth(wDelta + pc.getWidth());
            //root.setPrefWidth(wDelta + root.getMinWidth());
            root.setMinWidth(wDelta + root.getWidth());
            setMouseX(curX);
            System.err.println("1111111111111111 wDelta = " + wDelta);
            
            //}

            //if (hDelta + getWindow().getHeight() > ((PopupControl) getWindow()).getMinHeight()) {
/*            if (hDelta + getWindow().getHeight() > ((PopupControl) getWindow()).getMinHeight()) {
                //root.setPrefHeight(hDelta + root.getPrefHeight());
                pc.setAnchorY(yDelta + pc.getAnchorY());
                //pc.setHeight(hDelta + pc.getHeight());
                //pc.setHeight(hDelta + pc.getHeight());
                root.setMinHeight(pc.getHeight());

                setMouseY(curY);
                System.err.println("2222222222222222222");
            }
*/
        }

    }

    @Override
   public void start(MouseEvent ev, Window stage, Cursor cursor, Cursor... supportedCursors) {
        super.start(ev, stage, cursor, supportedCursors);
    }
}
