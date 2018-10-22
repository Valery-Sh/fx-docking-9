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

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Skin;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.util.StringConverter;
import static org.vns.javafx.dock.api.designer.bean.editor.ComboButton.createTriangle;

/**
 *
 * @author Valery
 */
public class TreePaneItem<E> extends BaseEditor { //AbstractPropertyEditor<E> implements StaticConstraintPropertyEditor {

    private static final PseudoClass LEAFITEM_PSEUDO_CLASS = PseudoClass.getPseudoClass("leafitem");
    private static final PseudoClass ITEMEXPANDED_PSEUDO_CLASS = PseudoClass.getPseudoClass("itemexpanded");

    private final ReadOnlyObjectWrapper<ButtonBase> textButtonWrapper = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Node> expandNodeWrapper = new ReadOnlyObjectWrapper<>();

    private final ObservableList<ButtonBase> buttons = FXCollections.observableArrayList();

    private TreePaneItem<E> parentItem;

    private boolean realTimeBinding;

    private StringConverter<E> stringConverter;

    private HiddenTitledPane titledPane;

    //private ObservableList<Button> childItems;
    public TreePaneItem() {
        this(null);
    }

    public TreePaneItem(String name) {
        super(name);
        ToggleButton b = new ToggleButton();
        b.setMaxWidth(1000);
        b.setAlignment(Pos.BASELINE_LEFT);
        textButtonWrapper.setValue(b);
        titledPane = new HiddenTitledPane();
//        togleGroup = new ToggleGroup();
        init();
    }

    private void init() {
        getStyleClass().add("tree-pane-item");
        setMenuButton(new Button());

        stringConverter = createBindingStringConverter();
        pseudoClassStateChanged(LEAFITEM_PSEUDO_CLASS, true);
        editableProperty().addListener((v, ov, nv) -> {
            this.setEditable(nv);
            this.setDisable(!isEditable());
        });
        Button eb = new Button();
        eb.getStyleClass().clear();
        eb.getStyleClass().add("expand-button");
        setExpandNode(eb);
        getTitledPane().expandedProperty().addListener((v, ov, nv) -> {
            pseudoClassStateChanged(ITEMEXPANDED_PSEUDO_CLASS, nv);
        });

        eb.setOnAction(e -> {
            getTitledPane().setExpanded(!isExpanded());
        });
        getChildItems().addListener(this::childItemsChanged);
    }

    public TreePane<TreePaneItem<E>> getTreePane() {
        TreePaneItem<E> p = getParentItem();
        TreePaneItem<E> root = null;

        while (p != null) {
            root = p;
            p = p.getParentItem();
        }
        TreePane<TreePaneItem<E>> pane = null;
        if (root != null && (root instanceof TreePane)) {
            pane = (TreePane<TreePaneItem<E>>) root;
            /*            Parent parent = root.getParent().getParent();
            if ((TreePane<TreePaneItem<E>>) root.getParent().getParent() instanceof TreePane) {
                pane = (TreePane<TreePaneItem<E>>) root.getParent().getParent();
            }
             */
        }
        return pane;
    }

    protected void updateMenuButton() {
        if (getParentItem() != null) {
            getMenuButton().setGraphic(null);
            getMenuButton().getStyleClass().clear();
            AnchorPane.setRightAnchor(getEditorNode(), 0d);
        }

    }

    private void childItemsChanged(ListChangeListener.Change<? extends TreePaneItem> change) {
        if (change.getList().isEmpty()) {
            pseudoClassStateChanged(LEAFITEM_PSEUDO_CLASS, true);
        } else {
            pseudoClassStateChanged(LEAFITEM_PSEUDO_CLASS, false);
        }
        while (change.next()) {
            if (change.wasPermutated()) {
            } else if (change.wasUpdated()) {
                List<TreePaneItem> list = (List<TreePaneItem>) change.getList().subList(change.getFrom(), change.getTo());
                list.forEach(it -> {
                    it.setParentItem(this);
                });

            } else if (change.wasReplaced()) {
                change.getRemoved().forEach(it -> {
                    TreePane tp = it.getTreePane();
                    if (tp != null) {
                        tp.getTogleGroup().getToggles().remove(it.getTextButton());
                    }
                    it.setParentItem(null);
                });
                change.getAddedSubList().forEach(it -> {
                    it.setParentItem(this);
                    updateMenuButton();
                    int level = it.getLevel();
                    Insets ins = it.getPadding();
                    //it.setPadding(new Insets(ins.getTop(), ins.getRight(), ins.getBottom(), 10));
                    TreePane tp = it.getTreePane();
                    if (tp != null) {
                        tp.getTogleGroup().getToggles().add((Toggle) it.getTextButton());
                    }

                });
            } else {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(it -> {
                        it.setParentItem(null);
                        TreePane tp = it.getTreePane();
                        if (tp != null) {
                            tp.getTogleGroup().getToggles().remove((Toggle) it.getTextButton());
                        }
                    });

                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(it -> {
                        it.setParentItem(this);
                        updateMenuButton();

                        Insets ins = it.getPadding();
                        //it.setPadding(new Insets(ins.getTop(), ins.getRight(), ins.getBottom(), 10));
                        TreePane tp = it.getTreePane();
                        if (tp != null) {
                            tp.getTogleGroup().getToggles().add((Toggle) it.getTextButton());
                        }

                    });
                }
            }
        }
    }

    public void setParentItem(TreePaneItem<E> item) {
        this.parentItem = item;
    }

    public TreePaneItem<E> getParentItem() {
        return parentItem;
        /*        if ( getParent() == null ) {
            return null;
        }
        Node parent = getParent();
        while ( ! (parent instanceof TreePaneItem) ) {
            parent = parent.getParent();
            if ( parent == null ) {
                break;
            }
        }
        return (TreePaneItem<E>) parent;
         */
    }

    public ObservableList<TreePaneItem> getChildItems() {
        VBox content = (VBox) getTitledPane().getContent();
        ObservableList contentVBox = content.getChildren();
        ObservableList<TreePaneItem> retval;
        retval = contentVBox;
        return retval;
    }

    public List<TreePaneItem<E>> getAllItems() {
        List list = new ArrayList<>();
        list.add(this);
        for (TreePaneItem it : (List<TreePaneItem>) getChildItems()) {
            list.add(it);
            addToList(list, it);
        }

        return list;
    }

    private void addToList(List list, TreePaneItem item) {
        for (TreePaneItem it : (List<TreePaneItem>) item.getChildItems()) {
            list.add(it);
            addToList(list, it);
        }
    }

    public int getLevel() {
        TreePaneItem p = getParentItem();
        if (p == null) {
            return 0;
        }
        return p.getLevel() + 1;
    }

    public boolean isExpanded() {
        return getTitledPane().isExpanded();
    }

    public boolean isLeaf() {
        return getChildItems().isEmpty();
    }

    protected HiddenTitledPane getTitledPane() {
        return (HiddenTitledPane) titledPane;
    }

    protected void setTitledPane(HiddenTitledPane titledPane) {
        this.titledPane = titledPane;
    }

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
    public ReadOnlyObjectProperty<ButtonBase> textFieldProperty() {
        return textButtonWrapper.getReadOnlyProperty();
    }

    public ButtonBase getTextButton() {
        return textButtonWrapper.get();
    }

    public ReadOnlyObjectProperty<Node> expandNodeProperty() {
        return expandNodeWrapper.getReadOnlyProperty();
    }

    public Node getExpandNode() {
        return expandNodeWrapper.get();
    }

    protected void setExpandNode(Node expandNode) {
        expandNodeWrapper.setValue(expandNode);
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

    public StringConverter<E> getStringConverter() {
        return stringConverter;
    }

/*    @Override
    public void bind(ReadOnlyProperty property) {

        unbind();
        //setEditable(true);
        setBoundProperty(property);
        setEditable(false);

        //   this.boundProperty = property;
        StringProperty sp = getTextButton().textProperty();
        if (property instanceof StringExpression) {
            sp.bind(property);
        } else {
//            sp.bind(asString(property));
        }
        createContextMenu(property);
    }

    @Override
    public void bindBidirectional(Property property) {
        unbind();
        setEditable(true);
        setBoundProperty(property);

        if (isRealTimeBinding()) {
            getTextButton().textProperty().bindBidirectional(property, stringConverter);
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
            Method m = node.getParent().getClass().getMethod(getname, new Class[]{Node.class});
            if (m != null) {
                E value = (E) m.invoke(node.getParent(), new Object[]{node});
                property.setValue(value);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }

        property.addListener((v, ov, nv) -> {
            setConstraint(node, nv);
        });
        getTextButton().textProperty().bindBidirectional(property, stringConverter);
        //createContextMenu(property);
    }

    protected void setConstraint(Parent node, E value) {
        try {

            String setname = "set" + getName().substring(0, 1).toUpperCase() + getName().substring(1);
            Method m = node.getParent().getClass().getMethod(setname, new Class[]{Node.class, value.getClass()});
            if (m != null) {
                m.invoke(node.getParent(), new Object[]{node, value});
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }

    }
*/    
//    public abstract E valueOf(String txt);

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
/*
    @Override
    public void unbind() {

        getTextButton().textProperty().unbind();
        if (getBoundProperty() != null && (getBoundProperty() instanceof Property)) {
            getTextButton().textProperty().unbindBidirectional(getBoundProperty());
        }
        setBoundProperty(null);
    }

    @Override
    public boolean isBound() {
        return getTextButton().textProperty().isBound() || getBoundProperty() != null;
    }
*/
    @Override
    public Skin<?> createDefaultSkin() {
        return new TreePaneItemSkin(this);
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

        private final TreePaneItem editor;

        public BindingStringConverter(TreePaneItem textField) {
            this.editor = textField;
        }

        protected T getBoundValue() {
            return (T) getEditor().getBoundProperty().getValue();
        }

        public TreePaneItem getEditor() {
            return editor;
        }

        @Override
        public String toString(T object) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public T fromString(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }//class BindingStringConverter

    public static class TreePaneItemSkin extends BaseEditorSkin {

        private final GridPane grid;
        private final TreePaneItem control;
        private VBox vbox;

        public TreePaneItemSkin(TreePaneItem control) {
            super(control);
            this.control = control;

            control.updateMenuButton();
            grid = (GridPane) control.getEditorNode();
  
            HBox btnBox = new HBox();
            btnBox.setSpacing(1);
            btnBox.getStyleClass().add("button-box");
            grid.getStyleClass().add("control-pane");
//            editorNode.getChildren().get(1).getStyleClass().add("value-pane");
            btnBox.getChildren().addAll(control.getButtons());
            ColumnConstraints column0 = new ColumnConstraints();
            column0.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(column0);

            vbox = new VBox();

            GridPane gridBox = new GridPane();
            column0 = new ColumnConstraints();
            column0.setHgrow(Priority.NEVER);

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setHgrow(Priority.ALWAYS);
            gridBox.getColumnConstraints().addAll(column0, column1);

            gridBox.add(control.getExpandNode(), 0, 0);
            gridBox.add(control.getTextButton(), 1, 0);

            vbox.getChildren().add(gridBox);

            grid.add(vbox, 0, 0);
            grid.add(btnBox, 1, 0);
            vbox.getChildren().add(control.getTitledPane());
            control.getTitledPane().setExpanded(false);

            for (Object b : control.getButtons()) {
                if (b instanceof ComboButton) {
                    injectComboButton((ComboButton) b);
                }
            }
            control.getButtons().addListener((ListChangeListener.Change change) -> {

                btnBox.getChildren().clear();
                List<Node> list = new ArrayList<>();
                list.addAll(vbox.getChildren());
                for (Node node : list) {
                    if (node instanceof ComboButton) {
                        vbox.getChildren().remove(((ComboButton) node).getComboBox());
                    }
                }

                for (Object b : control.getButtons()) {
                    if (!btnBox.getChildren().contains(b)) {
                        btnBox.getChildren().add((Node) b);
                        if ((b instanceof ComboButton)) {
                            vbox.getChildren().add(((ComboButton) b).getComboBox());
                            injectComboButton(((ComboButton) b));
                        }
                    }
                }

            });

            //getChildren().add(grid);
        }

        private void injectComboButton(ComboButton comboButton) {
            ComboBox comboBox = comboButton.getComboBox();

            comboBox.setMaxWidth(1000);
            comboBox.setVisible(false);
            vbox.getChildren().add(comboBox);

            comboBox.setOnHidden(ev -> {
                if (comboBox.getValue() == null) {
                    return;
                }
                String text = control.getTextButton().getText();
                if (text.isEmpty()) {
                    control.getTextButton().setText(comboButton.getSelectedText());
                } else {
                    control.getTextButton().setText(comboButton.getSelectedText());
                }
                comboBox.getSelectionModel().clearSelection();
            });
        }

        /*        private void injectTitledPane(TitledPane pane) {
//            pane.setVisible(false);
            pane.toBack();
            vbox.getChildren().add(pane);
            
            control.setTitledPane(pane);
            pane.setExpanded(false);
        }
         */
    }//TextFieldPropertyEditorSkin

}//class TextFieldPropertyEditor
