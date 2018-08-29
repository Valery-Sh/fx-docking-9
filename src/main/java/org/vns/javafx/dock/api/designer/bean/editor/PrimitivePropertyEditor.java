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
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public abstract class PrimitivePropertyEditor<T> extends AbstractPropertyEditor<T> {

    public PrimitivePropertyEditor() {
        init();
    }

    private void init() {
        setValueIfBlank("0");
    }

    @Override
    public StringConverter<T> createStringConverter() {
        return new PrimitiveStringConverter<>(this);
    }

    @Override
    protected StringBinding asString(Property property) {
        return ((NumberExpression) property).asString();
    }

    public static class ShortPropertyEditor extends PrimitivePropertyEditor<Short> {

        @Override
        protected void addValidators() {
            getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getFilterValidators().add(item -> {

                String regExp = "0|-?([1-9][0-9]*)?";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                    if (retval && !item.equals("-")) {
                        long l = Long.valueOf(item.trim());
                        retval = (l >= Short.MIN_VALUE && l <= Short.MAX_VALUE);
                    }
                }

                return retval;
            });
        }

        @Override
        public void setBoundValue(Short boundValue) {
            ((ObjectProperty) boundValueProperty()).set(boundValue);
        }

    }//class ShortPropertyEditor


    public static class LongPropertyEditor extends PrimitivePropertyEditor<Long> {
        @Override
        protected void addValidators() {
            getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getFilterValidators().add(item -> {
                //item = item.trim();
                String regExp = "0|-?([1-9][0-9]*)?";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                    if (retval && !item.equals("-")) {
                        String min = String.valueOf(Long.MIN_VALUE);
                        String max = String.valueOf(Long.MAX_VALUE);
                        if (item.startsWith("-")) {
                            if (item.length() > min.length()) {
                                retval = false;
                            } else if (item.length() == min.length() && item.compareTo(min) > 0) {
                                retval = false;
                            }
                        } else {
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

        @Override
        public void setBoundValue(Long boundValue) {
            ((LongProperty) boundValueProperty()).set(boundValue);
        }

    }//class LongPropertyEditor


    public static class DoublePropertyEditor extends PrimitivePropertyEditor<Double> {

        @Override
        protected void addValidators() {
            System.err.println("Add Validators");
            getValidators().add(item -> {
                return Pattern.matches("[+-]?\\d+\\.?(\\d+)?", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getFilterValidators().add(item -> {
                item = item.trim();
                String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d+)?)";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                }

                return retval;
            });

        }

        @Override
        public void setBoundValue(Double boundValue) {
            ((DoubleProperty) boundValueProperty()).set(boundValue);
        }

    }//class DoublePropertyEditor

    public static class FloatPropertyEditor extends PrimitivePropertyEditor<Float> {

        @Override
        protected void addValidators() {
            System.err.println("Add Validators");
            getValidators().add(item -> {
                boolean retval =  Pattern.matches("[+-]?\\d+\\.?(\\d+)?", item.trim());
                Double dv = Double.valueOf(item);
                retval = dv >= -Float.MAX_VALUE && dv <= Float.MAX_VALUE;
                return retval;
            });
        }

        @Override
        protected void addFilterValidators() {
            getFilterValidators().add(item -> {
                item = item.trim();
                String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d+)?)";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                }

                return retval;
            });

        }

        @Override
        public void setBoundValue(Float boundValue) {
            ((FloatProperty) boundValueProperty()).set(boundValue);
        }
    }//class FloatPropertyEditor

    public static class IntegerPropertyEditor extends PrimitivePropertyEditor<Integer> {

        @Override
        protected void addValidators() {
            getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getFilterValidators().add(item -> {
                //item = item.trim();
                String regExp = "0|-?([1-9][0-9]*)?";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                    if (retval && !item.equals("-")) {
                        long l = Long.valueOf(item.trim());
                        retval = (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE);
                    }
                }
                return retval;
            });
        }

        @Override
        public void setBoundValue(Integer boundValue) {
            ((IntegerProperty) boundValueProperty()).set(boundValue);
        }

    }//class IntegerPropertyEditor

    public static class BytePropertyEditor extends PrimitivePropertyEditor<Byte> {

        @Override
        protected void addValidators() {
            getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]{0,3})+", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getFilterValidators().add(item -> {
                //item = item.trim();
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

        @Override
        public void setBoundValue(Byte boundValue) {
            ((ObjectProperty) boundValueProperty()).set(boundValue);
        }

    }//class IntegerPropertyEditor

    public static class CharPropertyEditor extends PrimitivePropertyEditor<Character> {

        public CharPropertyEditor() {
            init();
        }

        private void init() {
            setValueIfBlank(" ");
        }

        @Override
        protected void addValidators() {
            getValidators().add(item -> {
                return Pattern.matches(".{1}", item);
            });
        }

        @Override
        protected void addFilterValidators() {
            getFilterValidators().add(item -> {
                item = item.trim();
                String regExp = ".{1}";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                }

                return retval;
            });
        }

        @Override
        public void setBoundValue(Character boundValue) {
            ((ObjectProperty) boundValueProperty()).set(boundValue);
        }

    }//class CharPropertyEditor
    
    public static class PrimitiveStringConverter<T> extends AbstractPropertyEditor.Converter<T> {

        public PrimitiveStringConverter(AbstractPropertyEditor textField) {
            super(textField);
        }

        @Override
        protected T valueOf(String txt) {

            T retval = null;

            T o = (T) getTextField().getBoundValue();
            if (o instanceof Double) {
                retval = (T) Double.valueOf(getTextField().stringOf(Double.valueOf(txt)));
            } else if (o instanceof Float) {
                retval = (T) Float.valueOf(getTextField().stringOf(Float.valueOf(txt)));
            } else if (o instanceof Integer) {
                retval = (T) Integer.valueOf(getTextField().stringOf(Integer.valueOf(txt)));
            } else if (o instanceof Short) {
                retval = (T) Short.valueOf(getTextField().stringOf(Short.valueOf(txt)));
            } else if (o instanceof Byte) {
                retval = (T) Byte.valueOf(getTextField().stringOf(Byte.valueOf(txt)));
            } else if (o instanceof Character) {
                retval = (T) new Character(txt.charAt(0));
            }

            return retval;
        }
    }//class PrimitiveStringConverter


}
