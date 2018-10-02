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

import javafx.scene.control.ButtonType;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public class ButtonTypeStringConverter extends StringConverter<ButtonType> {

    @Override
    public String toString(ButtonType buttonType) {
        if (buttonType == null) {
            return "";
        }
        return buttonType.getText();
    }

    @Override
    public ButtonType fromString(String string) {
        ButtonType retval = null;
        switch (string) {
            case "APPLY":
                retval = ButtonType.APPLY;
                break;
            case "CANCEL":
                retval = ButtonType.CANCEL;
                break;
            case "CLOSE":
                retval = ButtonType.CLOSE;
                break;
            case "FINISH":
                retval = ButtonType.FINISH;
                break;
            case "NEXT":
                retval = ButtonType.NEXT;
                break;
            case "NO":
                retval = ButtonType.NO;
                break;
            case "OK":
                retval = ButtonType.OK;
                break;
            case "PREVIOUS":
                retval = ButtonType.PREVIOUS;
                break;
            case "YES":
                retval = ButtonType.YES;
                break;
        }
        return retval;

    }

}
