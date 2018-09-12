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
import javafx.application.Platform;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;

/**
 *
 * @author Valery
 */
public abstract class PrimitivePropertyEditor<T> extends AbstractPropertyEditor<T> {

    public PrimitivePropertyEditor() {
        init();
    }

    private void init() {
//        setValueIfBlank("0");
    }


    @Override
    protected StringBinding asString(Property property) {
        StringBinding retval = null;
        if ( property instanceof ObjectExpression) {
            retval = ((ObjectExpression) property).asString();            
        } else if ( property instanceof NumberExpression) {
            retval = ((NumberExpression) property).asString();            
        } else if ( property instanceof BooleanExpression) {
            retval = ((BooleanExpression) property).asString();
        }
        return retval;
    }

    @Override
    public T valueOf(String txt) {

        T retval = null;
        if (isNullString(txt)) {
            return null;
        }

        T o = (T) getBoundProperty().getValue();

        if (o instanceof Double) {
            retval = (T) Double.valueOf(txt);
        } else if (o instanceof Float) {
            retval = (T) Float.valueOf(txt);
        } else if (o instanceof Integer) {
            retval = (T) Integer.valueOf(txt);
        }  else if (o instanceof Boolean) {
            retval = (T) Boolean.valueOf(txt);
        }
        return retval;

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

/*0909        @Override
        public void setBoundValue(Short boundValue) {
            ((ObjectProperty) (ObservableValue) boundPropertyProperty().get()).set(boundValue);
        }
*/
        @Override
        protected StringBinding asString(Property property) {
            return ((ObjectExpression) property).asString();
        }

        @Override
        public Short valueOf(String txt) {
            System.err.println("VALUE OF txt = '" + txt + "'");
            if (isNullString(txt)) {
                return null;
            }
            Short retval;
            if (txt.isEmpty()) {
                retval = 0;
            } else {
                retval = Short.valueOf(txt);
            }

            System.err.println("VALUE OF  retval = '" + retval + "'");
            System.err.println("===============================================");
            return retval;
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

/*0909        @Override
        public void setBoundValue(Long boundValue) {
            ((LongProperty) (ObservableValue) boundPropertyProperty().get()).set(boundValue);
        }
*/

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

/*0909        @Override
        public void setBoundValue(Double boundValue) {
            ((DoubleProperty) (ObservableValue) boundPropertyProperty().get()).set(boundValue);
        }
*/
    }//class DoublePropertyEditor

    public static class FloatPropertyEditor extends PrimitivePropertyEditor<Float> {

        @Override
        protected void addValidators() {
            System.err.println("Add Validators");
            getValidators().add(item -> {
                boolean retval = Pattern.matches("[+-]?\\d+\\.?(\\d+)?", item.trim());
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

/*0909        @Override
        public void setBoundValue(Float boundValue) {
            ((FloatProperty) (ObservableValue) boundPropertyProperty().get()).set(boundValue);
        }
*/
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

/*0909        @Override
        public void setBoundValue(Integer boundValue) {
            ((IntegerProperty) (ObservableValue) boundPropertyProperty().get()).set(boundValue);
        }
*/
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

/*0909        @Override
        public void setBoundValue(Byte boundValue) {
            ((ObjectProperty) (ObservableValue) boundPropertyProperty().get()).set(boundValue);
        }
*/
        @Override
        protected StringBinding asString(Property property) {
            return ((ObjectExpression) property).asString();
        }

        @Override
        public Byte valueOf(String txt) {
            System.err.println("VALUE OF txt = '" + txt + "'");
            if (isNullString(txt)) {
                return null;
            }
            Byte retval;
            if (txt.isEmpty()) {
                retval = 0;
            } else {
                retval = Byte.valueOf(txt);
            }

            System.err.println("VALUE OF  retval = '" + retval + "'");
            System.err.println("===============================================");
            return retval;
        }

    }//class IntegerPropertyEditor

    public static class CharacterPropertyEditor extends PrimitivePropertyEditor<Character> {

        public CharacterPropertyEditor() {
            init();
        }

        private void init() {
//            setValueIfBlank(null);
            //setNullString("<NULL>");
            setNullSubstitution("");
        }

        @Override
        protected void addValidators() {
            getValidators().add(item -> {

                if (getNullSubstitution() != null) {
                    String it = item;
                    if (!getNullSubstitution().isEmpty()) {
                        it = it.trim();
                    }
                    if (it.equals((getNullSubstitution()))) {
                        return true;
                    }
                }
                return Pattern.matches(".{0,1}", item);
            });

        }

        @Override
        protected void addFilterValidators() {
            getFilterValidators().add(item -> {
                if (getNullSubstitution() != null) {
                    String it = item;
                    if (!getNullSubstitution().isEmpty()) {
                        it = it.trim();
                    }
                    //System.err.println("======= it = '" + it + "'; getNullString()='" + getNullString() + "'");
                    if (it.equals(getNullSubstitution())) {
                        if (!getNullSubstitution().isEmpty()) {
                            System.err.println("======= it = '" + it + "'; getNullString()='" + getNullSubstitution() + "'");
                            Platform.runLater(() -> {
                                selectAll();
                            });
                        }
                        return true;
                    }
                }
                //item = item.trim();
                String regExp = ".{1}";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                }

                return retval;
            });
        }

/*0909        @Override
        public void setBoundValue(Character boundValue
        ) {
            ((ObjectProperty) (ObservableValue) boundPropertyProperty().get()).set(boundValue);
        }
*/
        @Override
        protected StringBinding asString(Property property) {
            return ((ObjectExpression) property).asString();
        }

        @Override
        public Character valueOf(String txt) {
            System.err.println("VALUE OF txt = '" + txt + "'");
            if (isNullString(txt)) {
                return null;
            }
            Character retval;
            if (txt.isEmpty()) {
                retval = new Character(' ');
            } else {
                retval = new Character(txt.charAt(0));
            }

            System.err.println("VALUE OF  retval = '" + retval + "'");
            System.err.println("===============================================");
            return retval;
        }

    }//class CharacterPropertyEditor

/*    public static class PrimitiveStringConverter<T> extends AbstractPropertyEditor.Converter<T> {

        public PrimitiveStringConverter(AbstractPropertyEditor textField) {
            super(textField);
        }

    }//class PrimitiveStringConverter
*/
    public static class StringPropertyEditor extends PrimitivePropertyEditor<String> {

        public StringPropertyEditor() {
            init();
        }

        private void init() {
//            setValueIfBlank(null);
        }


/*0909        @Override
        public void setBoundValue(String boundValue) {
            ((StringProperty) (ObservableValue) boundPropertyProperty().get()).set(boundValue);
        }
*/
        @Override
        public String valueOf(String txt) {
            System.err.println("VALUE OF txt = '" + txt + "'");
            if (isNullString(txt)) {
                return null;
            }
            return txt;
        }

    }//class StringPropertyEditor

}
