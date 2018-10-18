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

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public class ContentComboBoxSkin extends ComboBoxPopupControl<ContentComboBox> {

    private Node displayNode;

    public ContentComboBoxSkin(ContentComboBox control) {
        super(control, new ComboBoxBaseBehavior(control, new ArrayList<KeyBinding>()));
    }
    
    protected ContentComboBox getComboBox() {
        return (ContentComboBox)getSkinnable();
    }
    
    @Override
    public Node getPopupContent() {
        return getComboBox().getContent();
    }

    @Override
    protected double computeMinWidth(double height,
            double topInset, double rightInset,
            double bottomInset, double leftInset) {
        return 50; //comboBoxContent.getLayoutBounds().getWidth();
    }

    @Override
    protected void focusLost() {
        // do nothing
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    protected void handleControlPropertyChanged(String p) {

        if ("SHOWING".equals(p)) {
            if (getComboBox().isShowing()) {
                show();
            } else {
                hide();
            }
        } else if ("SHOW_WEEK_NUMBERS".equals(p)) {
//            if (datePickerContent != null) {
//                datePickerContent.updateGrid();
//                datePickerContent.updateWeeknumberDateCells();
        } else if ("VALUE".equals(p)) {
            updateDisplayNode();
//            if (datePickerContent != null) {
//                LocalDate date = comboBox.getValue();
//                datePickerContent.displayedYearMonthProperty().set((date != null) ? YearMonth.from(date) : YearMonth.now());
//                datePickerContent.updateValues();
//            }
            getComboBox().fireEvent(new ActionEvent());
        } else {
            super.handleControlPropertyChanged(p);
        }
    }

    @Override
    protected TextField getEditor() {
        if (!getSkinnable().isEditable()) {
            return null;
        }

        TextField tf = new FakeFocusTextField();
        return tf;
    }

    @Override
    protected StringConverter getConverter() {
        return null;
    }
    

    @Override
    public Node getDisplayNode() {

        if (displayNode == null) {
            if (getSkinnable().isEditable()) {
                displayNode = getEditableInputNode();
            } else {
                displayNode = getComboBox().getDisplayNode();
            }
            displayNode.getStyleClass().add("content-combo-box-display-node");
            if (getSkinnable().isEditable()) {
                updateDisplayNode();
            }
        }
        return displayNode;
    }

    public void syncWithAutoUpdate() {
        if (!getPopup().isShowing() && getComboBox().isShowing()) {
            getComboBox().hide();
        }
    }
}
