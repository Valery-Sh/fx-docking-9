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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Olga
 */
public abstract class BaseEditor<T> extends Control {

    private static final PseudoClass EDITABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("readonly");

    public static enum SidePos {
        LEFT,
        RIGHT,
        NO
    }
    private Button menuButton;
    private final StackPane valuePane;
    
    private final ObjectProperty<SidePos> menuButtonAllignment = new SimpleObjectProperty(SidePos.RIGHT);

    private String name;

    private HyperlinkTitle title;
    private Node editorNode;

    private ReadOnlyProperty boundProperty;
    private final BooleanProperty editable = new SimpleBooleanProperty(true);

    public BaseEditor() {
        this(null);
    }

    public BaseEditor(String name) {
        this.name = name;
        valuePane = new StackPane();
        valuePane.getStyleClass().add("value-pane");
        init();
    }

    private void init() {
        editorNode = createEditorNode();
        if (getName() != null) {
            setId(getName());
        }
        disableProperty().addListener((v, oldValue, newValue) -> {
            pseudoClassStateChanged(EDITABLE_PSEUDO_CLASS, newValue);
        });
        title = new HyperlinkTitle(this,getName() == null ? "" : getName() );
        editable.addListener(this::editableChangeListener);
    }

    public StackPane getValuePane() {
        return valuePane;
    }

    public ObjectProperty<SidePos> menuButtonAllignmentProperty() {
        return menuButtonAllignment;
    }

    public SidePos getMenuButtonAllignment() {
        return menuButtonAllignment.get();
    }

    public void setMenuButtonAllignment(SidePos pos) {
        menuButtonAllignment.set(pos);
    }

    protected void editableChangeListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        setDisable(!newValue);
    }

    protected ReadOnlyProperty getBoundProperty() {
        return boundProperty;
    }

    protected void setBoundProperty(ReadOnlyProperty boundProperty) {
        this.boundProperty = boundProperty;
    }

    public Node getEditorNode() {
        return editorNode;
    }

    protected abstract Node createEditorNode();

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    public String getName() {
        return name;
    }

    /*    @Override
    public void bind(ReadOnlyProperty<Boolean> property) {

        setEditable(false);

        this.setFocusTraversable(false);
        editorNode.selectedProperty().bind(property);
    }

    @Override
    public void bindBidirectional(Property<Boolean> property) {
        setEditable(true);
        this.setFocusTraversable(true);
        editorNode.selectedProperty().bindBidirectional(property);

    }
     */
    public HyperlinkTitle getTitle() {
        return title;
    }

    public BooleanProperty editableProperty() {
        return editable;
    }

    public boolean isEditable() {
        return editable.get();
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }
    public Button getMenuButton() {
        return menuButton;
    }

    public void setMenuButton(Button menuButton) {
        this.menuButton = menuButton;
    }
    
    /*  @Override
    public void unbind() {
        editorNode.selectedProperty().unbind();
    }

    @Override
    public boolean isBound() {
        return editorNode.selectedProperty().isBound();

    }
     */
    @Override
    public Skin<?> createDefaultSkin() {
        return new BaseEditorSkin(this);
    }

/*    private void showInBrowser() {
        if (getBoundProperty() == null || getBoundProperty().getBean() == null) {
            return;
        }
        try {
            BeanInfo info = Introspector.getBeanInfo(getBoundProperty().getBean().getClass());
            Method method = null;
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (getBoundProperty().getName().equals(pd.getName())) {
                    method = pd.getReadMethod();
                    break;
                }
            }
            if (method == null) {
                return;
            }
            String rdmethod = method.getName();
            String origin = getBoundProperty().getBean().getClass().getName();
            Class objClass = getBoundProperty().getBean().getClass();
            while (!Object.class.equals(objClass)) {
                try {
                    Method m = objClass.getMethod(rdmethod, new Class[0]);
                    if (Modifier.isPublic(m.getModifiers())) {
                        origin = objClass.getName();
                    }
                    objClass = objClass.getSuperclass();
                } catch (NoSuchMethodException | SecurityException ex) {
                    break;
                }
            }
            origin = origin.replace('.', '/');
            BrowserService.getInstance().showDocument(PropertyEditor.HYPERLINK + origin + ".html#" + rdmethod + "--");
        } catch (IntrospectionException ex) {
            Logger.getLogger(AbstractPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String toDisplayName(String propName) {

        char[] str = propName.toCharArray();
        if (Character.isDigit(str[0])) {
            return propName;
        }
        StringBuilder sb = new StringBuilder();
        int startPos = 0;

        while (true) {
            int endPos = getFirstWordPos(str, startPos);
            str[startPos] = Character.toUpperCase(str[startPos]);
            for (int i = startPos; i <= endPos; i++) {
                sb.append(str[i]);
            }
            if (endPos == str.length - 1) {
                break;
            }
            sb.append(' ');
            startPos = endPos + 1;
        }
        return sb.toString().trim();
    }

    int getFirstWordPos(char[] str, int startPos) {
        int lastPos = startPos;
        //str[startPos] = Character.toUpperCase(str[startPos]);
        if (startPos == str.length - 1) {
            return lastPos;
        }
        //
        // Check whether first and cecond char are in upper case
        //

        if (Character.isUpperCase(str[startPos]) && Character.isUpperCase(str[startPos + 1])) {

            // try search lower case char
            for (int i = startPos + 1; i < str.length; i++) {
                if (!Character.isUpperCase(str[i])) {
                    lastPos = i - 1;
                    break;
                }
                lastPos = i;
            }
            return lastPos;
        }

        for (int i = startPos + 1; i < str.length; i++) {
            if (Character.isUpperCase(str[i])) {
                lastPos = i - 1;
                break;
            }
            lastPos = i;
        }
        return lastPos;
    }
*/
    public static class BaseEditorSkin<T> extends SkinBase<BaseEditor<T>> {

        private AnchorPane anchor;
        private Button menuButton;
        
        public BaseEditorSkin(BaseEditor<T> control) {
            super(control);
            init();
        }

        private void init() {
            getSkinnable().getStyleClass().add(PropertyEditor.EDITOR_STYLE_CLASS);
            anchor = createAnchorPane();
            VBox vbox = new VBox(anchor, getSkinnable().getValuePane());
            //getChildren().add(anchor);
            getChildren().add(vbox);
        }

        protected AnchorPane createAnchorPane() {
            anchor = new AnchorPane();

            menuButton = getSkinnable().getMenuButton();
            if ( menuButton == null ) {
                menuButton = new Button();
                getSkinnable().setMenuButton(menuButton);
            }
            menuButton.getStyleClass().clear();

            Circle resetShape = new Circle(3.5);
            resetShape.setStroke(Color.DARKGRAY);
            resetShape.setStrokeWidth(2);
            menuButton.setVisible(false);

            resetShape.setFill(Color.TRANSPARENT);

            menuButton.setGraphic(resetShape);

            switch (getSkinnable().getMenuButtonAllignment()) {
                case RIGHT:
                    AnchorPane.setLeftAnchor(getSkinnable().getEditorNode(), 0d);
                    AnchorPane.setRightAnchor(getSkinnable().getEditorNode(), 14d);

                    AnchorPane.setRightAnchor(menuButton, 0d);
                    AnchorPane.setTopAnchor(menuButton, 0d);
                    AnchorPane.setBottomAnchor(menuButton, 0d);
                    menuButton.setId("menu-" + getSkinnable().getName());
                    anchor.getChildren().addAll(getEditorNode(), menuButton);                    
                    break;
                case LEFT:
                    AnchorPane.setLeftAnchor(getSkinnable().getEditorNode(), 14d);
                    AnchorPane.setRightAnchor(getSkinnable().getEditorNode(), 0d);

                    AnchorPane.setLeftAnchor(menuButton, 0d);
                    AnchorPane.setTopAnchor(menuButton, 0d);
                    AnchorPane.setBottomAnchor(menuButton, 0d);
                    menuButton.setId("menu-" + getSkinnable().getName());
                    anchor.getChildren().addAll(menuButton,getEditorNode() );
                    break;
                case NO:
                    AnchorPane.setLeftAnchor(getSkinnable().getEditorNode(), 0d);
                    AnchorPane.setRightAnchor(getSkinnable().getEditorNode(), 0d);
                    anchor.getChildren().addAll(getEditorNode() );
                    break;
            }
        
        
            anchor.setOnMouseEntered(ev -> {
                menuButton.setVisible(true);
            });
            anchor.setOnMouseExited(ev -> {
                menuButton.setVisible(false);
            });
            return anchor;
        }

        protected Node getEditorNode() {
            return getSkinnable().getEditorNode();
        }
    }//EditorSkin

}//BooleaPropertyEditor
