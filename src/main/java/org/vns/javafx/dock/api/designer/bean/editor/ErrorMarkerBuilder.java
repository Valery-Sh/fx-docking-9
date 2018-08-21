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
package org.vns.javafx.dock.api.designer.bean.editor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

/**
 *
 * @author Valery Shyshkin
 */
public class ErrorMarkerBuilder {

    private final StringTextField textField;

    private Node[] errorMarkers;

    private Class<?> defaultErrorMarkerClass;

    public ErrorMarkerBuilder(StringTextField textField) {
        this.textField = textField;
        init();
    }

    private void init() {
        setDefaultErrorMarkerClass(Circle.class);
    }

    public Class<?> getDefaultErrorMarkerClass() {
        return defaultErrorMarkerClass;
    }

    public void setDefaultErrorMarkerClass(Class<?> defaultErrorMarkerClass) {
        this.defaultErrorMarkerClass = defaultErrorMarkerClass;
    }

    public Node[] getErrorMarkers() {
        return errorMarkers;
    }

    public void setErrorMarkers(Node... errorMarkers) {
        this.errorMarkers = errorMarkers;
    }

    private String getSeparator() {
        return textField.getSeparator();
    }

    protected void showErrorMarkers(String[] items, Integer[][] itemPos, Integer... errorIndexes) {

        if (errorMarkers != null && errorMarkers.length > 0) {
            textField.getChildren().removeAll(errorMarkers);
        }
        errorMarkers = getDefaultErrorMarkers(errorIndexes.length);
        textField.getChildren().addAll(errorMarkers);

        for (int i = 0; i < errorIndexes.length; i++) {
            int itemIndex = errorIndexes[i];
            int start = itemPos[itemIndex][0];
            int end = itemPos[itemIndex][1];
            int charPos = start + (end - start) / 2;
            double pos = getWidth(textField.getText(0,charPos));
            if ( (end - start) % 2 != 0 ) {
                //
                // An odd integer value. 
                // We add delta whick equals to half length of a character
                //
                pos += getWidth(textField.getText(charPos,charPos+1)) / 2;
            }
            errorMarkers[i].setLayoutX(textField.getInsets().getLeft() + pos);
            errorMarkers[i].setLayoutY(textField.getInsets().getTop() - 2);
        }

    }
    protected void showErrorMarker() { 
        
    }
    public void showErrorMarkers(Integer... errorIndexes) {
        if (textField.getText().trim().isEmpty()) {
            return;
        }
        if ( textField.getSeparator() == null ) {
            showErrorMarker();
            return;
        }
        //String[] items = ((ObservableListPropertyEditor) textField).split(textField.getText());
        String[] items = textField.getText().split(textField.getSeparatorRegExp());

        String regExp = textField.getSeparatorRegExp();
        
        Pattern ptn = Pattern.compile(regExp);
        Matcher m = ptn.matcher(textField.getText());
        
        Integer[][] itemPos = new Integer[items.length][2];
        int start = 0;
        for (int i = 0; i < items.length; i++) {

            boolean found = m.find();
            if (!found && i == items.length - 1) {
                itemPos[i][0] = start + 1;
                itemPos[i][1] = start + 1 + items[items.length - 1].length();
                break;
            }
            start = m.start();
            itemPos[i][0] = start - items[i].length();
            itemPos[i][1] = start;
        }

        showErrorMarkers(items, itemPos,errorIndexes);
    }


    protected double getWidth(String text) {
        Text t = new Text(text);
        t.setFont(textField.getFont());
        return t.getLayoutBounds().getWidth();
    }

    protected Node[] getDefaultErrorMarkers(int length) {
        Circle[] nodes = new Circle[length];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Circle(2, Color.RED);
            nodes[i].setManaged(false);
        }
        return nodes;
    }

    protected Node[] getTriangleMarkers(int length) {

        Polygon[] nodes = new Polygon[length];
        for (int i = 0; i < nodes.length; i++) {
            //nodes[i] = new Polygon(4.0, 0.0,  0.0, 4.0,8.0, 4.0);
            //nodes[i] = new Polygon(4.0,4.0, 0.0,0.0, 8.0,0.0);
            nodes[i] = new Polygon(0.0, 3.0, 3.0, 0.0, 1.5, 1.5, 0.0, 0.0, 3.0, 3.0);
            nodes[i].setFill(Color.RED);
            nodes[i].setManaged(false);
        }
        return nodes;
    }
}//class ErrorMarkerBuilder
