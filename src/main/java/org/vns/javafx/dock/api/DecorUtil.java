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

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 *
 * @author Valery
 */
public class DecorUtil {

    /**
     *
     * @return the Color which corresponds to modena style
     * {@code -fx-background}
     */
    public static Color getBackGround() {
        Color c = Color.gray(0.98);
        //Color c = Color.gray(1);
        
        return c;
/*        double h = c.getHue();
        double s = c.getSaturation();
        double b = c.getBrightness();
        return c.deriveColor(h, s, b, 1);
*/
    }

    public static void setBackGround(Region node) {
        node.setBackground(new Background(new BackgroundFill(getBackGround(), null, null)));
    }
    public static void setBackGround(Region node, Color c) {
        node.setBackground(new Background(new BackgroundFill(c, null, null)));
    }
    /**
     * Sets background and border which correspond to the following {@literal css}.
     * <pre>
     * .float-window-root {
     *   -fx-background-color: -fx-background;
     *   -fx-border-width: 2;
     *   -fx-border-color: lightgrey;
     *  }
     * </pre>
     * @param node the node to be decorated
     */
    public static void setFloatWindowRootDecor(Region node) {
        setBackGround(node);
        BorderStroke stroke = new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, null, new BorderWidths(2, 2, 2, 2));
        Border border = new Border(stroke);
        node.setBorder(border);
    }

}
