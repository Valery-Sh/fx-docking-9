package org.vns.javafx.dock.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class FloatWindowResizer {

    private final DoubleProperty mouseX = new SimpleDoubleProperty();
    private final DoubleProperty mouseY = new SimpleDoubleProperty();

    private Cursor cursor;
    private Window window;
    
    private final Set<Cursor> cursorTypes = new HashSet<>();
    
    private FloatWindowBuilder windowBuilder;
            
    protected FloatWindowResizer() {
        Collections.addAll(cursorTypes,
        Cursor.S_RESIZE,Cursor.E_RESIZE,Cursor.N_RESIZE,Cursor.W_RESIZE,
        Cursor.SE_RESIZE,Cursor.NE_RESIZE,Cursor.SW_RESIZE,Cursor.NW_RESIZE);
    }
    public FloatWindowResizer(FloatWindowBuilder windowBuilder) {
        this();
        this.windowBuilder = windowBuilder;
    }
    public FloatWindowBuilder getWindowBuilder() {
        return windowBuilder;
    }

    public void setWindowBuilder(FloatWindowBuilder windowBuilder) {
        this.windowBuilder = windowBuilder;
    }
    
    private void setCursorTypes(Cursor... cursors) {
        cursorTypes.clear();
        Collections.addAll(this.cursorTypes, cursors);
    }
    
    public void resize(double x, double y) {
        if ( window instanceof PopupControl ) {
            resizePopup(x, y);
            return;
        }
        if ( ! cursorTypes.contains(cursor) ) {
            //return;
        }
        double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;

        double curX = mouseX.get();
        double curY = mouseY.get();
        if (cursor == Cursor.S_RESIZE) {
            hDelta = y - this.mouseY.get();
            curY = y;
        } else if (cursor == Cursor.E_RESIZE) {
            wDelta = x - this.mouseX.get();
            curX = x;
        } else if (cursor == Cursor.N_RESIZE) {
            hDelta = this.mouseY.get() - y;
            yDelta = -hDelta;
            curY = y;
        } else if (cursor == Cursor.W_RESIZE) {
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            curX = x;
        } else if (cursor == Cursor.SE_RESIZE) {
            hDelta = y - this.mouseY.get();
            curY = y;
            wDelta = x - this.mouseX.get();
            curX = x;

        } else if (cursor == Cursor.NE_RESIZE) {
            hDelta = this.mouseY.get() - y;
            wDelta = x - this.mouseX.get();
            yDelta = -hDelta;
            curX = x;
            curY = y;
        } else if (cursor == Cursor.SW_RESIZE) {
            hDelta = y - this.mouseY.get();
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            curX = x;
            curY = y;
        } else if (cursor == Cursor.NW_RESIZE) {
            hDelta = this.mouseY.get() - y;
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            yDelta = -hDelta;
            curX = x;
            curY = y;
        }

        if (wDelta + window.getWidth() >= getMinWidth()) {
            window.setX(xDelta + window.getX());
            window.setWidth(wDelta + window.getWidth());
            mouseX.set(curX);
        }
        if (hDelta + window.getHeight() >= getMinHeight()) {
            window.setY(yDelta + window.getY());
            window.setHeight(hDelta + window.getHeight());
            mouseY.set(curY);
        }
    }
    public void resizePopup(double x, double y) {
        PopupControl pc = (PopupControl) window;
        Region r = (Region) pc.getScene().getRoot();        
        double resizeMinWidth = getMinWidth();
        double resizeMinHeight = getMinHeight();
        if ( getWindowBuilder() != null) {
        //    resizeMinWidth = dockable.dockableController().getTargetController().getResizeMinWidth();
        //    resizeMinHeight = dockable.dockableController().getTargetController().getResizeMinHeight();
            resizeMinWidth = windowBuilder.getMinWidth();
            resizeMinHeight = windowBuilder.getMinHeight();
            System.err.println("   --- root minWidth =" + resizeMinWidth);                            
        }
        double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;
//        System.err.println("   --- root minWidth =" + getMinHeight());                
/*        System.err.println("START RESIZE x=" + x);
        System.err.println("   --- root minWidth =" + getMinWidth());        
        System.err.println("   --- root prefWidth=" + r.getPrefWidth());        
        System.err.println("   --- mouseX & curX =" + mouseX);                
*/
        double curX = mouseX.get();
        double curY = mouseY.get();
//        System.err.println("   ---           curX=" + curX );        
//        System.err.println("----------------------------------------");                
        if (cursor == Cursor.S_RESIZE) {
            hDelta = y - this.mouseY.get();
            curY = y;
        } else if (cursor == Cursor.E_RESIZE) {
            wDelta = x - this.mouseX.get();
            curX = x;
        } else if (cursor == Cursor.N_RESIZE) {
            hDelta = this.mouseY.get() - y;
            yDelta = -hDelta;
            curY = y;
        } else if (cursor == Cursor.W_RESIZE) {
            
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            curX = x;
/*            System.err.println("CALC DELTA:");            
            System.err.println("RESIZE xDelta =" + xDelta );
            System.err.println("RESIZE wDelta =" + wDelta );
            System.err.println("RESIZE curX   =" + curX );
            System.err.println("===========================================");            
*/            
        } else if (cursor == Cursor.SE_RESIZE) {
            hDelta = y - this.mouseY.get();
            curY = y;
            wDelta = x - this.mouseX.get();
            curX = x;

        } else if (cursor == Cursor.NE_RESIZE) {
            hDelta = this.mouseY.get() - y;
            wDelta = x - this.mouseX.get();
            yDelta = -hDelta;
            curX = x;
            curY = y;
        } else if (cursor == Cursor.SW_RESIZE) {
            hDelta = y - this.mouseY.get();
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            curX = x;
            curY = y;
        } else if (cursor == Cursor.NW_RESIZE) {
            hDelta = this.mouseY.get() - y;
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            yDelta = -hDelta;
            curX = x;
            curY = y;
        }
        
        double w = -1;
        double h = -1;
//        System.err.println("CURSOR = " + cursor);
        //System.err.println("   --- ROOT = " + r + "; minWidth=" + getMinWidth() + "; maxWidth=" + r.getMaxWidth());
        if ( (xDelta != 0 || wDelta != 0 ) &&  wDelta + window.getWidth() >= resizeMinWidth) {
        //if (wDelta + r.getWidth() >= getMinWidth()) {
            //System.err.println("   --- wDelta= " + wDelta + "; xDelta" + xDelta );
            pc.setAnchorX(xDelta + pc.getAnchorX());
            //System.err.println("   --- oldW = " + r.getPrefWidth());
            r.setPrefWidth(wDelta + r.getPrefWidth());
            //System.err.println("   --- newW = " + r.getPrefWidth());            
            //System.err.println("   --- win Width = " + pc.getScene().getWindow().getWidth());            
            mouseX.set(curX);
            
        } 
        
        if (hDelta + window.getHeight() >= resizeMinHeight) {
//            System.err.println("   --- hDelta= " + hDelta + "; yDelta" + yDelta );
            pc.setAnchorY(yDelta + pc.getAnchorY());
            r.setPrefHeight(hDelta + r.getPrefHeight());
            mouseY.set(curY);
        }
        
        
    }
    
    protected double getMinWidth() {
        double retval = 50.0;
        if ( window instanceof Stage  ) {
            retval = ((Stage)window).getMinWidth();
        } else if ( window instanceof PopupControl  ) {
            //retval = ((PopupControl)window).getMinWidth();
            Node root = ((PopupControl)window).getScene().getRoot();
            if ( root instanceof Region ) {
                retval = ((Region)root).getMinWidth();
            }
        }
        return retval;
    }
    protected double getMinHeight() {
        double retval = 50.0;
        if ( window instanceof Stage ) {
            retval = ((Stage)window).getMinHeight();
        } else if ( window instanceof PopupControl  ) {
            retval = ((PopupControl)window).getMinHeight();
        }
        return retval;
    }    
    public void resize(MouseEvent ev) {
        resize(ev.getScreenX(), ev.getScreenY());
    }

    public Window getStage() {
        return window;
    }

    public void start(MouseEvent ev, Window stage, Cursor cursor, Cursor... supportedCursors) {
        setCursorTypes(supportedCursors);
        this.mouseX.set(ev.getScreenX());
        this.mouseY.set(ev.getScreenY());

        this.cursor = cursor;
        this.window = stage;
    }

    public static Cursor cursorBy(double nodeX, double nodeY, double width, double height, double left, double right, double top, double bottom, Cursor... supported) {
        boolean e, w, n, s;
        Cursor cursor = Cursor.DEFAULT;
        w = nodeX < left;
        e = nodeX > width - right;
        n = nodeY < top;
        s = nodeY > height - bottom;

        if (w) {
            if (n) {
                cursor = Cursor.NW_RESIZE;
            } else if (s) {
                cursor = Cursor.SW_RESIZE;
            } else {
                cursor = Cursor.W_RESIZE;
            }
        } else if (e) {
            if (n) {
                cursor = Cursor.NE_RESIZE;
            } else if (s) {
                cursor = Cursor.SE_RESIZE;
            } else {
                cursor = Cursor.E_RESIZE;
            }
        } else if (n) {
            cursor = Cursor.N_RESIZE;
        } else if (s) {
            cursor = Cursor.S_RESIZE;
        }
        Cursor retval = Cursor.DEFAULT;
        for ( Cursor c : supported) {
            if ( c.equals(cursor) ) {
                retval = cursor;
                break;
            }
        }
        return cursor;
    }

    public static Cursor cursorBy(MouseEvent ev, double width, double height, double left, double right, double top, double bottom) {
        return cursorBy(ev.getX(), ev.getY(), width, height, left, right, top, bottom);
    }

    public static Cursor cursorBy(MouseEvent ev, Region r) {
        double x, y, w, h;
        
        Insets ins = r.getPadding();
        if (ins == Insets.EMPTY) {
            return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5, 5, 5, 5);            
        }
        return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
    }
}
