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
package org.vns.javafx.dock.api.view;

import javafx.scene.Cursor;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
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

/*    @Override
    public void resize(double x, double y) {
        if ( true ) {
            resizePopup(x, y);
        }
        PopupControl pc = (PopupControl) getWindow();
        Region root = (Region) pc.getScene().getRoot();
//        double resizeMinWidth = getMinWidth();
//        double resizeMinHeight = getMinHeight();
        //if (getWindowView() != null) {
        //    resizeMinWidth = dockable.dockableController().getTargetController().getResizeMinWidth();
        //    resizeMinHeight = dockable.dockableController().getTargetController().getResizeMinHeight();
        double resizeMinWidth = getWindowView().getMinWidth();
        double resizeMinHeight = getWindowView().getMinHeight();
        System.err.println("   --- root minWidth =" + resizeMinWidth);
        //}
        double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;
//        System.err.println("   --- root minWidth =" + getMinHeight());                
        double curX = getMouseX();
        double curY = getMouseY();
//        System.err.println("   ---           curX=" + curX );        
//        System.err.println("----------------------------------------");                
        if (getCursor() == Cursor.S_RESIZE) {
            hDelta = y - getMouseY();
            curY = y;
        } else if (getCursor() == Cursor.E_RESIZE) {
            wDelta = x - this.getMouseX();
            curX = x;
        } else if (getCursor() == Cursor.N_RESIZE) {
            hDelta = getMouseY() - y;
            yDelta = -hDelta;
            curY = y;
        } else if (getCursor() == Cursor.W_RESIZE) {

            wDelta = this.getMouseX() - x;
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

        if ((xDelta != 0 || wDelta != 0) && wDelta + getWindow().getWidth() >= resizeMinWidth) {
            root.setPrefWidth(wDelta + root.getPrefWidth());
            pc.setAnchorX(xDelta + pc.getAnchorX());
            //pc.setX(xDelta + pc.getX());
            setMouseX(curX);
        }

        if (hDelta + getWindow().getHeight() >= resizeMinHeight) {
            root.setPrefHeight(hDelta + root.getPrefHeight());
            pc.setAnchorY(yDelta + pc.getAnchorY());
            setMouseY(curY);
        }
    }
    */
    @Override
    public void resize(double x, double y) {
        PopupControl pc = (PopupControl) getWindow();
        Region root = (Region) pc.getScene().getRoot();
        //double resizeMinWidth = getMinWidth();
        //double resizeMinHeight = getMinHeight();
        //if (getWindowBuilder() != null) {
            //    resizeMinWidth = dockable.dockableController().getTargetController().getResizeMinWidth();
            //    resizeMinHeight = dockable.dockableController().getTargetController().getResizeMinHeight();
        double resizeMinWidth = getWindowView().getMinWidth();
        double resizeMinHeight = getWindowView().getMinHeight();
       System.err.println("   ++++ --- +++ !!! root minWidth =" + resizeMinWidth);
       System.err.println("   ++++ --- +++ !!! root minWidth =" + resizeMinHeight);
       System.err.println("   ++++ --- +++ !!! getWindow().width =" + getWindow().getWidth());
       System.err.println("   ++++ --- +++ !!! root.width =" + getWindowView().getRootPane().getWidth());
       System.err.println("   ++++ --- +++ !!! root.frefWidth =" + getWindowView().getRootPane().getPrefWidth());
       System.err.println("   ++++++++++ !!! getCursor() =" + getCursor());
       
        //}
        double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;
//        System.err.println("   --- root minWidth =" + getMinHeight());                
/*        System.err.println("START RESIZE x=" + x);
        System.err.println("   --- root minWidth =" + getMinWidth());        
        System.err.println("   --- root prefWidth=" + root.getPrefWidth());        
        System.err.println("   --- mouseX & curX =" + mouseX);                
         */
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
            /*            System.err.println("CALC DELTA:");            
            System.err.println("RESIZE xDelta =" + xDelta );
            System.err.println("RESIZE wDelta =" + wDelta );
            System.err.println("RESIZE curX   =" + curX );
            System.err.println("===========================================");            
             */
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
        //pc.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
        double w = -1;
        double h = -1;

//        if ((xDelta != 0 || wDelta != 0) && wDelta + getWindow().getWidth() > resizeMinWidth) {
        if ((xDelta != 0 || wDelta != 0) && wDelta + getWindow().getWidth() > ((PopupControl)getWindow()).getMinWidth()) {

            System.err.println("RESIZE getWindow().getWidth() =" + getWindow().getWidth() );
            System.err.println("   --- RESIZE wDelta =" + wDelta );
            System.err.println("   --- RESIZE resizeMinWidth   =" + resizeMinWidth );
            System.err.println("   --- RESIZE rootPrefWidth   =" + root.getPrefWidth() );
            System.err.println("     --- RESIZE new rootPrefWidth   =" + ( wDelta + root.getPrefWidth() ) );
            
            root.setPrefWidth(wDelta + root.getPrefWidth());
             //((PopupControl)getWindow()).setPrefWidth(wDelta + ((PopupControl)getWindow()).getWidth());
            
            pc.setAnchorX(xDelta + pc.getAnchorX());
            //pc.setX(xDelta + pc.getX());
            setMouseX(curX);
        }

        if (hDelta + getWindow().getHeight() > ((PopupControl)getWindow()).getMinHeight()) {
            root.setPrefHeight(hDelta + root.getPrefHeight());
            pc.setAnchorY(yDelta + pc.getAnchorY());
            setMouseY(curY);
        }
    }
    @Override
    public void start(MouseEvent ev, Window stage, Cursor cursor, Cursor... supportedCursors) {
        System.err.println("START POPUP RESIZER ");
        super.start(ev, stage, cursor, supportedCursors);
        System.err.println("   ---  START POPUP RESIZER SUPPORTRD CURSORS = " + supportedCursors.length);
        System.err.println("   ---  START POPUP RESIZER CURSOR = " + cursor);
    }
}
