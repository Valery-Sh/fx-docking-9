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

import com.sun.javafx.sg.prism.NGCanvas;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import org.vns.javafx.dock.DockTitleBar;

/**
 *
 * @author Valery
 */
public class StyleUtil {
    
    private static final String FX_BACKGROUND  = "#f4f4f4ff";
    private static final String FX_OUTER_BORDER  = "#b6b6b6ff";
    private static final String DOCK_PLACE_COLOR  = "#0093ff";
    
    /**
     *
     * @return the Color which corresponds to modena style
     * {@code -fx-background}
     */
    public static Color getFxBackground() {
        return Color.valueOf(FX_BACKGROUND);
    }
    public static Color getFxOuterBorder() {
       return Color.valueOf(FX_OUTER_BORDER); 
    }
     public static Color getDockPlaceColor() {
       return Color.valueOf(DOCK_PLACE_COLOR); 
    }      
    public static void setFxBackground(Region node) {
        node.setBackground(new Background(new BackgroundFill(getFxBackground(), null, null)));
    }

    public static void setBackground(Region node, Color c) {
        node.setBackground(new Background(new BackgroundFill(c, null, null)));
    }

    /**
     * Sets background and border which correspond to the following
     * {@literal css}.
     * <pre>
     * .float-window-root {
     *   -fx-background-color: -fx-background;
     *   -fx-border-width: 1 1 1 1;
     *   -fx-border-color: -fx-outer-border;
     *  }
     * </pre>
     *
     * @param node the node to be decorated
     */
    public static void styleFloatWindowRoot(Region node) {
        setFxBackground(node);
        BorderStroke stroke = new BorderStroke(getFxOuterBorder(), BorderStrokeStyle.SOLID, null, new BorderWidths(1, 1, 1, 1));
        Border border = new Border(stroke);
        node.setBorder(border);
    }
  /**
     * Sets background and border which correspond to the following
     * {@literal css}.
     * <pre>
     * .sidebar-popup-root {
     *   -fx-background-color: -fx-background;
     *   -fx-border-width: 1 1 1 1;
     *   -fx-border-color: -fx-outer-border;
     *  }
     * </pre>
     *
     * @param node the node to be decorated
     */
    public static void styleSideBarPopupRoot(Region node) {
        setFxBackground(node);
        BorderStroke stroke = new BorderStroke(getFxOuterBorder(), BorderStrokeStyle.SOLID, null, new BorderWidths(1, 1, 1, 1));
        Border border = new Border(stroke);
        node.setBorder(border);
    }    
    /**
     * Sets padding and background color.
     * <pre>
     * .dock-node {
     *      -fx-background-color: -fx-background;
     *      -fx-padding: 2 2 2 2;
     * }
     * </pre>
     * @param node the object of type {@code DockNode}
     */
    public static void styleDockNode(Region node) {
        setFxBackground(node);
        node.setPadding(new Insets(2,2,2,2));
    }
/**
     * Sets background and border which correspond to the following
     * {@literal css}.
     * <pre>
     * .sidebar-popup-root {
     *   -fx-background-color: -fx-background;
     *   -fx-border-width: 1 1 1 1;
     *   -fx-border-color: -fx-outer-border;
     *  }
     * </pre>
     *
     * @param node the node to be decorated
     */
    public static void styleTabPlace(Rectangle node) {
        node.setFill(Color.TRANSPARENT);
        node.setOpacity(0.5);
        node.setStroke(getDockPlaceColor());
        node.setStrokeWidth(3);
        node.setStrokeType(StrokeType.INSIDE);
        node.setStrokeLineCap(StrokeLineCap.BUTT);
        node.getStrokeDashArray().addAll(8d,2d);
    }        
   /**
     * Sets properties which correspond to the following
     * {@literal css}.
     * <pre>
     * .dock-title-bar {
     *      -fx-padding: 2;
     *      -fx-spacing: 3;
     *      -fx-border-width: 1;
     *      -fx-border-color: -fx-outer-border;
     * }
     * </pre>
     *
     * @param node the node to be decorated
     */
/*    public static void styleDockTitleBar(DockTitleBar node) {
        
        node.setPadding(new Insets(2,2,2,2));
        node.setSpacing(3);
        
        setFxBackground(node);
        BorderStroke stroke = new BorderStroke(getFxOuterBorder(), BorderStrokeStyle.SOLID, null, new BorderWidths(1, 1, 1, 1));
        Border border = new Border(stroke);
        node.setBorder(border);
    }    
*/    
    
}
