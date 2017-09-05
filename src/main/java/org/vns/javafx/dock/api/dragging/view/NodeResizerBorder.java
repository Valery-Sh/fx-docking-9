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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderImage;
import javafx.scene.layout.BorderRepeat;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class NodeResizerBorder {

    private Border border;

    public NodeResizerBorder() {
        init();
    }

    private void init() {
        URL imageURL = Dockable.class.getResource("resources/border_image.jpg");
        System.err.println("IMAGE URL=" + imageURL);
        String imageURLString = imageURL.toExternalForm();

        List<Double> dashArray = new ArrayList<>();
        dashArray.add(2.0);
        dashArray.add(1.4);

        BorderWidths regionWidths = new BorderWidths(4);
        BorderWidths sliceWidthы = new BorderWidths(4);
        boolean filled = false;
        //BorderRepeat repeatX = BorderRepeat.STRETCH;
        //BorderRepeat repeatY = BorderRepeat.STRETCH;
        BorderRepeat repeatX = BorderRepeat.STRETCH;
        BorderRepeat repeatY = BorderRepeat.STRETCH;
        
        BorderImage borderImage = new BorderImage(new Image(imageURLString),
                regionWidths,
                new Insets(-1),
                sliceWidthы,
                filled,
                repeatX,
                repeatY);

        BorderStrokeStyle strokeStyle
                = new BorderStrokeStyle(StrokeType.CENTERED,
                        //StrokeLineJoin.MITER,
                        StrokeLineJoin.ROUND,
                        StrokeLineCap.BUTT,
                        //StrokeLineCap.ROUND,
                        //StrokeLineCap.SQUARE,
                        10,
                        0,
                        dashArray);

        BorderStroke stroke = new BorderStroke(Color.rgb(255, 148, 40),
                strokeStyle,
                CornerRadii.EMPTY,
                new BorderWidths(4),
                new Insets(1));

        BorderStroke[] strokes = new BorderStroke[]{stroke};
        BorderImage[] images = new BorderImage[]{borderImage};
        border = new Border(images);
        //border = new Border(stroke);

    }

    public Border getBorder() {
        return border;
    }

}
