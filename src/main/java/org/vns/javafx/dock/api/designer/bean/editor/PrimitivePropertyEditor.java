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
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.text.Font;

/**
 *
 * @author Valery
 */
public abstract class PrimitivePropertyEditor<T> extends TextFieldPropertyEditor<T> {

    public PrimitivePropertyEditor() {
        this(null);
    }

    public PrimitivePropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
    }

    @Override
    protected StringBinding asString(ReadOnlyProperty property) {
        StringBinding retval = null;
        if (property instanceof ObjectExpression) {
            retval = ((ObjectExpression) property).asString();
        } else if (property instanceof NumberExpression) {
            retval = ((NumberExpression) property).asString();
        } else if (property instanceof BooleanExpression) {
            retval = ((BooleanExpression) property).asString();
        }
        return retval;
    }

    @Override
    public T valueOf(String txt) {

        T retval = null;
        if (getTextField().isNullString(txt)) {
            return null;
        }

        T o = (T) getBoundProperty().getValue();

        if (o instanceof Double) {
            retval = (T) Double.valueOf(txt);
        } else if (o instanceof Float) {
            retval = (T) Float.valueOf(txt);
        } else if (o instanceof Integer) {
            retval = (T) Integer.valueOf(txt);
        } else if (o instanceof Boolean) {
            retval = (T) Boolean.valueOf(txt);
        }
        return retval;

    }

    public static class ShortPropertyEditor extends PrimitivePropertyEditor<Short> {

        public ShortPropertyEditor() {
        }

        public ShortPropertyEditor(String name) {
            super(name);
            getTextField().setEmptySubstitution("0");
        }

        @Override
        protected void addValidators() {
            getTextField().getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getTextField().getFilterValidators().add(item -> {

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
        protected StringBinding asString(ReadOnlyProperty property) {
            return ((ObjectExpression) property).asString();
        }

        @Override
        public Short valueOf(String txt) {
            if (getTextField().isNullString(txt)) {
                return null;
            }
            Short retval;
            if (txt.isEmpty()) {
                retval = 0;
            } else {
                retval = Short.valueOf(txt);
            }
            return retval;
        }

    }//class ShortPropertyEditor

    public static class LongPropertyEditor extends PrimitivePropertyEditor<Long> {

        public LongPropertyEditor() {
        }

        public LongPropertyEditor(String name) {
            super(name);
            getTextField().setEmptySubstitution("0");
        }

        @Override
        protected void addValidators() {
            getTextField().getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getTextField().getFilterValidators().add(item -> {
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

        public DoublePropertyEditor() {
            this(null);
        }

        public DoublePropertyEditor(String name) {
            super(name);
            getTextField().setEmptySubstitution("0");
        }

        @Override
        protected void addValidators() {
            getTextField().getValidators().add(item -> {
                return Pattern.matches("[+-]?\\d+\\.?(\\d+)?", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getTextField().getFilterValidators().add(item -> {
                item = item.trim();
                String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d+)?)";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                }

                return retval;
            });

        }

    }//class DoublePropertyEditor

    public static class FloatPropertyEditor extends PrimitivePropertyEditor<Float> {

        public FloatPropertyEditor() {
        }

        public FloatPropertyEditor(String name) {
            super(name);
            getTextField().setEmptySubstitution("0");
        }

        @Override
        protected void addValidators() {
            getTextField().getValidators().add(item -> {
                boolean retval = Pattern.matches("[+-]?\\d+\\.?(\\d+)?", item.trim());
                Double dv = Double.valueOf(item);
                retval = dv >= -Float.MAX_VALUE && dv <= Float.MAX_VALUE;
                return retval;
            });
        }

        @Override
        protected void addFilterValidators() {
            getTextField().getFilterValidators().add(item -> {
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

        public IntegerPropertyEditor() {
        }

        public IntegerPropertyEditor(String name) {
            super(name);
            getTextField().setEmptySubstitution("0");
        }

        @Override
        protected void addValidators() {
            getTextField().getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getTextField().getFilterValidators().add(item -> {
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

    }//class IntegerPropertyEditor

    public static class BytePropertyEditor extends PrimitivePropertyEditor<Byte> {

        public BytePropertyEditor() {
        }

        public BytePropertyEditor(String name) {
            super(name);
        }

        @Override
        protected void addValidators() {
            getTextField().getValidators().add(item -> {
                return Pattern.matches("0|-?([1-9][0-9]{0,3})+", item.trim());
            });
        }

        @Override
        protected void addFilterValidators() {
            getTextField().getFilterValidators().add(item -> {
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
        protected StringBinding asString(ReadOnlyProperty property) {
            return ((ObjectExpression) property).asString();
        }

        @Override
        public Byte valueOf(String txt) {
            if (getTextField().isNullString(txt)) {
                return null;
            }
            Byte retval;
            if (txt.isEmpty()) {
                retval = 0;
            } else {
                retval = Byte.valueOf(txt);
            }

            return retval;
        }

    }//class IntegerPropertyEditor

    public static class CharacterPropertyEditor extends PrimitivePropertyEditor<Character> {

        public CharacterPropertyEditor() {
        }

        public CharacterPropertyEditor(String name) {
            super(name);
            init();
        }

        private void init() {
//            setValueIfBlank(null);
            //setNullString("<NULL>");
            getTextField().setNullSubstitution("");
        }

        @Override
        protected void addValidators() {
            getTextField().getValidators().add(item -> {

                if (getTextField().getNullSubstitution() != null) {
                    String it = item;
                    if (!getTextField().getNullSubstitution().isEmpty()) {
                        it = it.trim();
                    }
                    if (it.equals((getTextField().getNullSubstitution()))) {
                        return true;
                    }
                }
                return Pattern.matches(".{0,1}", item);
            });

        }

        @Override
        protected void addFilterValidators() {
            getTextField().getFilterValidators().add(item -> {
                if (getTextField().getNullSubstitution() != null) {
                    String it = item;
                    if (!getTextField().getNullSubstitution().isEmpty()) {
                        it = it.trim();
                    }
                    if (it.equals(getTextField().getNullSubstitution())) {
                        if (!getTextField().getNullSubstitution().isEmpty()) {
                            Platform.runLater(() -> {
                                getTextField().selectAll();
                            });
                        }
                        return true;
                    }
                }
                String regExp = ".{1}";
                boolean retval = item.isEmpty();
                if (!retval) {
                    retval = Pattern.matches(regExp, item);
                }

                return retval;
            });
        }

        @Override
        protected StringBinding asString(ReadOnlyProperty property) {
            return ((ObjectExpression) property).asString();
        }

        @Override
        public Character valueOf(String txt) {
            if (getTextField().isNullString(txt)) {
                return null;
            }
            Character retval;
            if (txt.isEmpty()) {
                retval = new Character(' ');
            } else {
                retval = new Character(txt.charAt(0));
            }
            return retval;
        }

    }//class CharacterPropertyEditor

    public static class StringPropertyEditor extends PrimitivePropertyEditor<String> {

        public StringPropertyEditor() {
            this(null);
        }

        public StringPropertyEditor(String name) {
            super(name);
        }
        @Override
        public String valueOf(String txt) {
            if (getTextField().isNullString(txt)) {
                return null;
            }
            return txt;
        }

    }//class StringPropertyEditor

}
