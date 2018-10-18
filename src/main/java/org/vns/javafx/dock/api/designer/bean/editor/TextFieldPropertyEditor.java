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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Skin;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.util.StringConverter;
import static org.vns.javafx.dock.api.designer.bean.editor.ComboButton.createTriangle;
import static org.vns.javafx.dock.api.designer.bean.editor.EnumPropertyEditor.NULL_VALUE;

/**
 *
 * @author Valery
 */
//public abstract class TextFieldPropertyEditor<E> extends StringTextField implements PropertyEditor<E> {
public abstract class TextFieldPropertyEditor<E> extends AbstractPropertyEditor<E> implements StaticConstraintPropertyEditor {

    private final ReadOnlyObjectWrapper<StringTextField> textFieldWrapper = new ReadOnlyObjectWrapper<>();

    private final ObservableList<ButtonBase> buttons = FXCollections.observableArrayList();

    private boolean realTimeBinding;

    private StringConverter<E> stringConverter;

    public boolean isRealTimeBinding() {
        return realTimeBinding;
    }

    public void setRealTimeBinding(boolean realTimeBinding) {
        this.realTimeBinding = realTimeBinding;
    }

    /*    public TextFieldPropertyEditor(E defaulValue) {
        super(defaulValue.toString());
        init();
    }
     */
    public TextFieldPropertyEditor() {
        this(null);
    }

    public TextFieldPropertyEditor(String name) {
        super(name);
        textFieldWrapper.setValue(new StringTextField());
        init();
    }

    private void init() {
        stringConverter = createBindingStringConverter();
        getTextField().setErrorMarkerBuilder(new ErrorMarkerBuilder(this.getTextField()));
        editableProperty().addListener((v, ov, nv) -> {
            this.setEditable(nv);
            this.setDisable(!isEditable());
        });
        addValidators();
        addFilterValidators();
    }

    public ReadOnlyObjectProperty<StringTextField> textFieldProperty() {
        return textFieldWrapper.getReadOnlyProperty();
    }

    public StringTextField getTextField() {
        return textFieldWrapper.get();
    }

    public ObservableList<ButtonBase> getButtons() {
        return buttons;
    }

    @Override
    protected Node createEditorNode() {
        return new GridPane();
    }
    protected void addValidators() {

    }

    protected void addFilterValidators() {

    }

    protected ObservableList<Predicate<String>> getValidators() {
        return getTextField().getValidators();
    }

    protected ObservableList<Predicate<String>> getFilterValidators() {
        return getTextField().getFilterValidators();
    }

    public StringConverter<E> getStringConverter() {
        return stringConverter;
    }

    @Override
    public void bind(ReadOnlyProperty property) {

        unbind();
        //setEditable(true);
        setBoundProperty(property);
        setEditable(false);

        //   this.boundProperty = property;
        StringProperty sp = isRealTimeBinding() ? getTextField().textProperty() : getTextField().lastValidTextProperty();
        if (property instanceof StringExpression) {
            sp.bind(property);
        } else {
            sp.bind(asString(property));
        }
        createContextMenu(property);
    }

    @Override
    public void bindBidirectional(Property property) {
        unbind();
        setEditable(true);
        setBoundProperty(property);

        if (isRealTimeBinding()) {
            getTextField().textProperty().bindBidirectional(property, stringConverter);
        } else {
            getTextField().lastValidTextProperty().bindBidirectional(property, stringConverter);
        }
        createContextMenu(property);
    }
    
    @Override
    public void bindConstraint(Parent node, Method... setMethods) {
        unbind();
        setEditable(true);
        ObjectProperty<E> property = new SimpleObjectProperty<>();
        setBoundProperty(property);
        try {
            
            String getname = "get" + getName().substring(0, 1).toUpperCase() + getName().substring(1);
            Method m  = node.getParent().getClass().getMethod(getname, new Class[]{Node.class});
            if (m != null) {
                E value = (E) m.invoke(node.getParent(), new Object[]{node});
                property.setValue(value);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }
        
        
        property.addListener((v,ov,nv) -> {
            setConstraint(node, nv);
        });
        getTextField().textProperty().bindBidirectional(property, stringConverter);
        //createContextMenu(property);
    }
    protected void setConstraint(Parent node, E value) {
        try {
            
            String setname = "set" + getName().substring(0, 1).toUpperCase() + getName().substring(1);
            Method m  = node.getParent().getClass().getMethod(setname, new Class[]{Node.class, value.getClass()});
            if (m != null) {
                m.invoke(node.getParent(), new Object[]{node, value});
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }
        
    }
    public abstract E valueOf(String txt);

    /*    @Override
    public ReadOnlyProperty<E> getBoundProperty() {
        return boundProperty;
    }

    protected void setBoundProperty(Property<E> boundProperty) {
        this.boundProperty = boundProperty;
    }
     */
    public StringConverter<E> createBindingStringConverter() {
        return new BindingStringConverter(this);
    }

    protected void createContextMenu(ReadOnlyProperty property) {
    }

    public String stringOf(E value) {
        String retval = value == null ? "" : value.toString();
        if (value == null && getTextField().getNullSubstitution() != null) {
            retval = getTextField().getNullSubstitution();
        }
        return retval;
    }

    protected abstract StringBinding asString(ReadOnlyProperty property);

    @Override
    public void unbind() {

        getTextField().lastValidTextProperty().unbind();
        getTextField().textProperty().unbind();
        if ( getBoundProperty() != null && (getBoundProperty() instanceof Property) ) {
            getTextField().lastValidTextProperty().unbindBidirectional(getBoundProperty());
            getTextField().textProperty().unbindBidirectional(getBoundProperty());
        }
        setBoundProperty(null);
    }

    @Override
    public boolean isBound() {
        return getTextField().lastValidTextProperty().isBound() || getTextField().textProperty().isBound() || getBoundProperty() != null;
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new TextFieldPropertyEditorSkin(this);
    }

    public static void setDefaultButtonGraphic(Button btn) {
        Polygon polygon = (Polygon) createTriangle();
        btn.setGraphic(polygon);
    }

    public static void setDefaultLayout(Button btn) {
        btn.setTextOverrun(OverrunStyle.CLIP);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setAlignment(Pos.CENTER_LEFT);

    }

    public static class BindingStringConverter<T> extends StringConverter<T> {
    
        private final TextFieldPropertyEditor editor;

        public BindingStringConverter(TextFieldPropertyEditor textField) {
            this.editor = textField;
        }

        protected T getBoundValue() {
            return (T) getEditor().getBoundProperty().getValue();
        }

        public TextFieldPropertyEditor getEditor() {
            return editor;
        }

        @Override
        public String toString(T dv) {
            if (dv == null && editor.getTextField().getNullSubstitution() != null) {
                return editor.getTextField().getNullSubstitution();
            }
            return editor.stringOf(dv);
        }

        @Override
        public T fromString(String tx) {
            T retval;
            if (getEditor().getTextField().hasErrorItems()) {
                retval = getBoundValue();
            } else {
                retval = (T) editor.valueOf(tx);
            }
            return retval;
        }
    }//class BindingStringConverter

    public static class TextFieldPropertyEditorSkin extends AbstractPropertyEditorSkin {

        private final GridPane grid;
        private final TextFieldPropertyEditor control;
        private StackPane textFieldParent;
     
        
        public TextFieldPropertyEditorSkin(TextFieldPropertyEditor control) {
            super(control);
            this.control = control;

            grid = (GridPane) control.getEditorNode();
            
            HBox btnBox = new HBox();
            btnBox.setSpacing(1);
            btnBox.getStyleClass().add("button-box");
            grid.getStyleClass().add("control-pane");

            btnBox.getChildren().addAll(control.getButtons());
            ColumnConstraints column0 = new ColumnConstraints();
            column0.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(column0);
            textFieldParent = new StackPane(control.getTextField());
            
            grid.add(textFieldParent, 0, 0);
            grid.add(btnBox, 1, 0);
            
            for ( Object b : control.getButtons()) {
                if ( b instanceof ComboButton ) {
                    injectComboButton((ComboButton) b);
                }
            }
            control.getButtons().addListener((ListChangeListener.Change change) -> {
                
                btnBox.getChildren().clear();
                List<Node> list = new ArrayList<>();
                list.addAll(textFieldParent.getChildren());
                for ( Node node : list) {
                    if ( node instanceof ComboButton ) {
                        textFieldParent.getChildren().remove(((ComboButton)node).getComboBox());
                    }
                }
                
                for (Object b : control.getButtons()) {
                    if (!btnBox.getChildren().contains(b)) {
                        btnBox.getChildren().add((Node) b);
                        if ( ( b instanceof ComboButton)  ) {
                           textFieldParent.getChildren().add(((ComboButton)b).getComboBox());
                           injectComboButton(((ComboButton) b));
                        }
                    }
                }
                
            });

            //getChildren().add(grid);
        }
      
        private void injectComboButton(ComboButton comboButton) {
            String separator = control.getTextField().getSeparator();

            ComboBox comboBox = comboButton.getComboBox();
            
            comboBox.setMaxWidth(1000);
            comboBox.setVisible(false);
            textFieldParent.getChildren().add(comboBox);

            comboBox.setOnHidden(ev -> {
                if (comboBox.getValue() == null) {
                    return;
                }
                String text = control.getTextField().getText();
                if (text.isEmpty()) {
                    control.getTextField().setText(comboButton.getSelectedText());
                } else if ( separator != null ){
                    control.getTextField().setText(text + separator + comboButton.getSelectedText());
                } else {
                    control.getTextField().setText(comboButton.getSelectedText());
                }
                comboBox.getSelectionModel().clearSelection();
            });
          
/*            comboBox.setCellFactory(listView -> new ListCell<Label>() {
                @Override
                public void updateItem(Label item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getText());
                    }
                }
            });
  */          
        }

    }//TextFieldPropertyEditorSkin

}//class TextFieldPropertyEditor
