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

import java.util.regex.Pattern;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.Property;
import javafx.util.StringConverter;
import javafx.util.converter.ByteStringConverter;
import javafx.util.converter.CharacterStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;
import javafx.util.converter.ShortStringConverter;

/**
 *
 * @author Valery
 */
public class PrimitivePropertyEditor<E> extends StringTextField implements PropertyEditor<E> {

    protected StringConverter<E> stringConverter;
    protected boolean isPropertyReadOnly;
            
    public PrimitivePropertyEditor() {
        init();
    }
    private void init() {
        setToStringTransformer((source) -> {return source.trim();});
        setFromStringTransformer((source) -> {return source.trim();});
    }
    @Override
    public void bind(Property property) {
        rightValueProperty().removeListener(getRightValueChangeListener());
        this.setEditable(false);
        this.setFocusTraversable(false);
        if (property instanceof NumberExpression) {
            rightValueProperty().bind(((NumberExpression) property).asString());
        } else if (property instanceof BooleanExpression) {
            rightValueProperty().bind(((BooleanExpression) property).asString());
        } else if (property instanceof ObjectExpression) {
            rightValueProperty().bind(((ObjectExpression<E>) property).asString());
        }
    }

    public void bind(Property property, String formatter) {
        rightValueProperty().removeListener(getRightValueChangeListener());
        this.setEditable(false);
        this.setFocusTraversable(false);
        if (property instanceof NumberExpression) {
            rightValueProperty().bind(((NumberExpression) property).asString(formatter));
        } else if (property instanceof BooleanExpression) {
            rightValueProperty().bind(((BooleanExpression) property).asString());
        } else if (property instanceof ObjectExpression) {
            rightValueProperty().bind(((ObjectExpression<E>) property).asString(formatter));
        }
    }

    @Override
    public void bindBidirectional(Property property) {
        this.setEditable(true);
        this.setFocusTraversable(true);
        rightValueProperty().removeListener(getRightValueChangeListener());
        rightValueProperty().addListener(getRightValueChangeListener());
        
        rightValueProperty().bindBidirectional(property, stringConverter);
    }

    @Override
    public void unbind() {
        rightValueProperty().unbind();
    }

    @Override
    public boolean isBound() {
        return rightValueProperty().isBound();

    }

    public static class IntegerPropertyEditor extends PrimitivePropertyEditor<Integer> {

        public IntegerPropertyEditor() {
            init();
        }

        private void init() {
            stringConverter = new IntegerStringConverter();
            setValueIfBlank("0");

            setErrorMarkerBuilder(new ErrorMarkerBuilder(this));

            getValidators().add(item -> {
                boolean retval = Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());

                System.err.println("validator ITEM = " + item + "; matches=" + retval);
                return retval;
            });
            getFilterValidators().add(item -> {
                item = item.trim();
                String regExp = "0|-?([1-9][0-9]*)?";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                    if (retval && ! item.equals("-")) {
                        long l = Long.valueOf(item.trim());
                        retval = (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE);
                    }
                }

                return retval;
            });
        }
    }

    public static class ShortPropertyEditor extends PrimitivePropertyEditor<Short> {

        public ShortPropertyEditor() {
            init();
        }

        private void init() {
            stringConverter = new ShortStringConverter();
            setValueIfBlank("0");

            setErrorMarkerBuilder(new ErrorMarkerBuilder(this));

            getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());
            });
            getFilterValidators().add(item -> {

                String regExp = "0|-?([1-9][0-9]*)?";
                boolean retval = item.trim().isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item.trim());
                    if (retval && !item.equals("-")) {
                        long l = Long.valueOf(item.trim());
                        retval = (l >= Short.MIN_VALUE && l <= Short.MAX_VALUE);
                    }
                }

                return retval;
            });
        }
    }

    public static class BytePropertyEditor extends PrimitivePropertyEditor<Byte> {

        public BytePropertyEditor() {
            init();
        }

        private void init() {
            stringConverter = new ByteStringConverter();
            setValueIfBlank("0");

            setErrorMarkerBuilder(new ErrorMarkerBuilder(this));

            getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]{0,3})+", item.trim());
            });
            getFilterValidators().add(item -> {
                item = item.trim();
                String regExp = "0|-?([1-9][0-9]{0,3})?";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                    if (retval && !item.equals("-")) {
                        long l = Long.valueOf(item);
                        retval = (l >= Byte.MIN_VALUE && l <= Byte.MAX_VALUE);
                    }
                }

                return retval;
            });
        }
    }

    public static class LongPropertyEditor extends PrimitivePropertyEditor<Long> {

        public LongPropertyEditor() {
            init();
        }

        private void init() {
            stringConverter = new LongStringConverter();
            setValueIfBlank("0");

            setErrorMarkerBuilder(new ErrorMarkerBuilder(this));

            getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());
            });

            getFilterValidators().add(item -> {
                item = item.trim();
                String regExp = "0|-?([1-9][0-9]*)?";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                    if (retval && ! item.equals("-")) {
                        String min = String.valueOf(Long.MIN_VALUE);
                        String max = String.valueOf(Long.MAX_VALUE);
                        if (item.startsWith("-")) {
                            if (item.length() > min.length()) {
                                retval = false;
                            } else if (item.length() == min.length() && item.compareTo(min) > 0) {
                                retval = false;
                            }
                        } else {
                            System.err.println("MIN = " + min + "item.trim().compareTo(min) = " + item.trim().compareTo(min));
                            System.err.println("MAX = " + max + "item.trim().compareTo(max) = " + item.trim().compareTo(max));
                            if (item.length() > max.length()) {
                                retval = false;
                            } else if (item.length() == max.length() && item.compareTo(max) > 0) {
                                retval = false;
                            }

                        }
                    }
                }

                return retval;
            });
        }
    }
    public static class CharPropertyEditor extends PrimitivePropertyEditor<Character> {

        public CharPropertyEditor() {
            init();
        }

        private void init() {
            stringConverter = new CharacterStringConverter();
            setValueIfBlank(" ");

            setErrorMarkerBuilder(new ErrorMarkerBuilder(this));

            getValidators().add(item -> {
                return Pattern.matches(".{1}", item.trim());
            });

            getFilterValidators().add(item -> {
                item = item.trim();
                String regExp = ".{1}";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
/*                    if (retval && ! item.equals("-")) {
                        String min = String.valueOf(Long.MIN_VALUE);
                        String max = String.valueOf(Long.MAX_VALUE);
                        if (item.startsWith("-")) {
                            if (item.length() > min.length()) {
                                retval = false;
                            } else if (item.length() == min.length() && item.compareTo(min) > 0) {
                                retval = false;
                            }
                        } else {
                            System.err.println("MIN = " + min + "item.trim().compareTo(min) = " + item.trim().compareTo(min));
                            System.err.println("MAX = " + max + "item.trim().compareTo(max) = " + item.trim().compareTo(max));
                            if (item.length() > max.length()) {
                                retval = false;
                            } else if (item.length() == max.length() && item.compareTo(max) > 0) {
                                retval = false;
                            }

                        }

                    }
*/
                }

                return retval;
            });
        }
    }

    public static class DoublePropertyEditor extends PrimitivePropertyEditor<Double> {

        public DoublePropertyEditor() {
            init();
        }

        private void init() {
            stringConverter = new DoubleStringConverter();
            setValueIfBlank("0.0");
            setErrorMarkerBuilder(new ErrorMarkerBuilder(this));

            getValidators().add(item -> {
                boolean retval = Pattern.matches("[+-]?\\d+\\.?(\\d+)?", item.trim());
                return retval;
            });
            getFilterValidators().add(item -> {
                item = item.trim();
                String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d+)?)";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);

                    if (retval) {
                        try {
                            stringConverter.fromString(item);
                        } catch (Exception ex) {
                            
                        }
                    }
                }

                return retval;
            });
        }
    }

}
