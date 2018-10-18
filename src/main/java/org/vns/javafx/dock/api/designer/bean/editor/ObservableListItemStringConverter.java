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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import javafx.scene.control.ButtonType;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.BigIntegerStringConverter;
import javafx.util.converter.ByteStringConverter;
import javafx.util.converter.CharacterStringConverter;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.LongStringConverter;
import javafx.util.converter.ShortStringConverter;

/**
 *
 * @author Valery
 */
public class ObservableListItemStringConverter<T> extends StringConverter<T> implements SubstitutionConverter<T> {

    private final ObservableListEditor<T> textField;
    private final ObservableListPropertyEditor<T> editor;
    private final Class<?> itemClass;
    private StringConverter converter;

    public ObservableListItemStringConverter(ObservableListPropertyEditor<T> editor, Class<?> itemClass) {
        this.textField = (ObservableListEditor<T>) editor.getTextField();
        this.editor = editor;
        this.itemClass = itemClass;
        init();
    }

    private void init() {
        if (itemClass.equals(String.class)) {
            converter = new DefaultStringConverter();
        } else if (itemClass.equals(Integer.class)) {
            converter = new IntegerStringConverter();
        } else if (itemClass.equals(Double.class)) {
            converter = new DoubleStringConverter();
        } else if (itemClass.equals(Float.class)) {
            converter = new FloatStringConverter();
        } else if (itemClass.equals(Short.class)) {
            converter = new ShortStringConverter();
        } else if (itemClass.equals(Byte.class)) {
            converter = new ByteStringConverter();
        } else if (itemClass.equals(Character.class)) {
            converter = new CharacterStringConverter();
        } else if (itemClass.equals(Long.class)) {
            converter = new LongStringConverter();
        } else if (itemClass.equals(BigInteger.class)) {
            converter = new BigIntegerStringConverter();
        } else if (itemClass.equals(BigDecimal.class)) {
            converter = new BigDecimalStringConverter();
        } else if (itemClass.equals(Date.class)) {
            converter = new DateTimeStringConverter();
        } else if (itemClass.equals(LocalDate.class)) {
            converter = new LocalDateStringConverter();
        } else if (itemClass.equals(LocalDateTime.class)) {
            converter = new LocalDateTimeStringConverter();
        } else if (itemClass.equals(LocalTime.class)) {
            converter = new LocalTimeStringConverter();
        } else if (itemClass.equals(ButtonType.class)) {
            converter = new ButtonTypeStringConverter();
        }
    }

    public Class<?> getItemClass() {
        return itemClass;
    }

    @Override
    public String toString(T obj) {
        String retval = toSubstitution(obj);
        if ( toSubstitution(obj) == null ) {
            retval = converter.toString(obj);
        }
        return retval;
    }

    @Override
    public T fromString(String txt) {
        return (T) converter.fromString(txt);
    }

    @Override
    public ObservableListPropertyEditor<T> getEditor() {
        return editor;
    }


}
