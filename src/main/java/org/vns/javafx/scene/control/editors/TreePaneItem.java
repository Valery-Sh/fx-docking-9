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
package org.vns.javafx.scene.control.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Skin;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.util.StringConverter;
import static org.vns.javafx.scene.control.editors.PropertyEditorPane.STATUSBAR_ID;
import static org.vns.javafx.scene.control.editors.PropertyEditorPane.STATUSBAR_LABEL_ID;
import static org.vns.javafx.scene.control.editors.ComboButton.createTriangle;
import org.vns.javafx.scene.control.editors.skin.BaseEditorSkin;

/**
 *
 * @author Valery
 */
public class TreePaneItem<E> extends AbstractPropertyEditor implements CompositePropertyEditor { //AbstractPropertyEditor<E> implements StaticConstraintPropertyEditor {

    public static final PseudoClass NOTNULL_PSEUDO_CLASS = PseudoClass.getPseudoClass("notnull");

    private static final PseudoClass LEAFITEM_PSEUDO_CLASS = PseudoClass.getPseudoClass("leafitem");
    private static final PseudoClass ITEMEXPANDED_PSEUDO_CLASS = PseudoClass.getPseudoClass("itemexpanded");

    private final ReadOnlyObjectWrapper<ButtonBase> textButtonWrapper = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Node> expandNodeWrapper = new ReadOnlyObjectWrapper<>();

    private final ObservableList<ButtonBase> buttons = FXCollections.observableArrayList();

    private TreePaneItem<E> parentItem;

    private boolean realTimeBinding;

    private StringConverter stringConverter;

    private HiddenTitledPane titledPane;

    private final ObjectProperty<E> value = new SimpleObjectProperty<>();

    private TreePaneItem delegateItem;

    public TreePaneItem() {
        this(null);

    }

    public TreePaneItem(String name) {
        super(name);
        ToggleButton b = new ToggleButton();
        b.setMaxWidth(1000);
        b.setAlignment(Pos.BASELINE_LEFT);
        b.getStyleClass().add("text-button");

        textButtonWrapper.setValue(b);
        titledPane = new HiddenTitledPane();
        init();
    }

    private void init() {

        getStyleClass().add("tree-pane-item");
        setMenuButton(new Button());
        Tooltip tt = new Tooltip();
        tt.setOnShown(e -> tt.setText("Property name=" + getName()));

        getTextButton().setTooltip(tt);
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

    public ObjectProperty<E> valueProperty() {
        return value;
    }

    public E getValue() {
        return value.get();
    }

    public void setValue(E value) {
        this.value.set(value);
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

    /*    private  ChangeListener<String> textReplaceHandler = ( value,  oldValue, newValue) -> {
        
    };

    public ChangeListener<String> getTextReplaceHandler() {
        return textReplaceHandler;
    }

    public void setTextReplaceHandler(ChangeListener<String> textReplaceHandler) {
        this.textReplaceHandler = textReplaceHandler;
    }
     */
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
                        tp.getToggleGroup().getToggles().remove(it.getTextButton());
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
                        tp.getToggleGroup().getToggles().add((Toggle) it.getTextButton());
                    }

                });
            } else {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(it -> {
                        it.setParentItem(null);
                        TreePane tp = it.getTreePane();
                        if (tp != null) {
                            tp.getToggleGroup().getToggles().remove((Toggle) it.getTextButton());
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
                            tp.getToggleGroup().getToggles().add((Toggle) it.getTextButton());
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
        //return new GridPane();
        return new VBox();
    }

    protected void addValidators() {

    }

    public StringConverter getStringConverter() {
        return stringConverter;
    }

    public void setStringConverter(StringConverter<E> stringConverter) {
        this.stringConverter = stringConverter;
    }

    protected void createContextMenu(ReadOnlyProperty property) {
    }

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

    @Override
    public void bind(ReadOnlyProperty property) {
        unbind();

        setBoundProperty(property);
        setEditable(false);
        boundPropertyChanged(null, property.getValue());
        property.addListener(boundPropertyChangeListener);
    }
    protected ChangeListener boundPropertyChangeListener = (ObservableValue observable, Object oldValue, Object newValue) -> {
        if (newValue != null && stringConverter != null) {
            getTextButton().setText(stringConverter.toString(newValue));
        } else if (newValue != null) {
            getTextButton().setText(newValue.getClass().getSimpleName());
        } else {
            getTextButton().setText(null);
        }

    };

    private void boundPropertyChanged(Object oldValue, Object newValue) {
        if (newValue == null) {
            setValue(null);
            if (delegateItem != null) {
                delegateItem.removeChilds();
            } else if (this instanceof TreePane) {
                removeChilds();
            }

        } else {

            if (oldValue != null) {
                if (delegateItem != null) {
                    delegateItem.removeChilds();
                } else if (this instanceof TreePane) {
                    removeChilds();
                }
                //
                // modify newValue by oldValue
                //
                modifyOnReplace(oldValue, newValue);
            }
            List<TreePaneItem> oldChilds = getChildItems();

            PropertyEditorPane ppe = null;
            if (delegateItem == null) {
                ppe = new PropertyEditorPane(this);
                ppe.setBean(newValue);
                setValue((E) ppe);

            } else {
                ppe = new PropertyEditorPane(delegateItem);
                ppe.setBean(newValue);
                delegateItem.setValue((E) ppe);
            }
            ppe.setStatusBar(createDefaultStatusBar(newValue));
            List<TreePaneItem> newChilds = getChildItems();
        }
    }

    private HBox createDefaultStatusBar(Object bean) {
        Labeled lb = bean == null ? new Label()
                : new Label("Property: " + getName() + ". Type: " + bean.getClass().getSimpleName());
        lb.setId(STATUSBAR_LABEL_ID);

        lb.setPadding(new Insets(4, 4, 4, 4));

        HBox hb = new HBox(lb);
        hb.setId(STATUSBAR_ID);
        hb.getStyleClass().add("status-bar");
        return hb;
    }

    public BiConsumer<Object, Object> replaceBoundPropertyHandler = (oldValue, newValue) -> {
    };

    protected void modifyOnReplace(Object oldValue, Object newValue) {
        if (replaceBoundPropertyHandler != null) {
            replaceBoundPropertyHandler.accept(oldValue, newValue);
        }
    }

    public BiConsumer<Object, Object> getReplaceBoundPropertyHandler() {
        return replaceBoundPropertyHandler;
    }

    public void setReplaceBoundPropertyHandler(BiConsumer<Object, Object> replaceBoundPropertyHandler) {
        this.replaceBoundPropertyHandler = replaceBoundPropertyHandler;
    }

    protected ChangeListener boundBidirectionalChangeListener = (ObservableValue observable, Object oldValue, Object newValue) -> {
        boundPropertyChanged(oldValue, newValue);
    };

    public void setSelected(boolean selected) {
        ((ToggleButton) getTextButton()).setSelected(selected);
    }

    public void removeChilds() {
        getAllItems().forEach(item -> {
            item.setSelected(false);
        });
        setSelected(false);
//        if (delegateItem != null) {
//            delegateItem.removeChilds();
//        } else {
        getChildItems().clear();
//        }
    }

    @Override
    public void bindBidirectional(Property property) {
        unbind();
        setEditable(true);

        setBoundProperty(property);
        boundPropertyChanged(null, property.getValue());
        getTextButton().textProperty().bindBidirectional(property, stringConverter);
        property.addListener(boundBidirectionalChangeListener);
    }

    public void bindItems(TreePaneItem newDelegate) {
        delegateItem = newDelegate;
        getTextButton().textProperty().bindBidirectional(newDelegate.getTextButton().textProperty());
        newDelegate.valueProperty().bindBidirectional(valueProperty());
    }

    @Override
    public void unbind() {

        getTextButton().textProperty().unbind();
        if (getBoundProperty() != null && (getBoundProperty() instanceof Property)) {
            getTextButton().textProperty().unbindBidirectional(getBoundProperty());
            if (delegateItem != null) {
                getTextButton().textProperty().unbindBidirectional(delegateItem.getTextButton().textProperty());
            }
            getBoundProperty().removeListener(boundPropertyChangeListener);
            getBoundProperty().removeListener(boundBidirectionalChangeListener);
        }
        setBoundProperty(null);
    }

    @Override
    public boolean isBound() {
        return getTextButton().textProperty().isBound() || getBoundProperty() != null;
    }

    public static class TreePaneItemSkin extends BaseEditorSkin {

        private final GridPane grid;
        private final VBox editorNode;
        private final TreePaneItem control;
        private VBox vbox;

        public TreePaneItemSkin(TreePaneItem control) {
            super(control);
            this.control = control;

            control.updateMenuButton();
            editorNode = (VBox) control.getEditorNode();
            grid = new GridPane();
            editorNode.getChildren().add(grid);

            HBox btnBox = new HBox();
            btnBox.setSpacing(1);
            btnBox.getStyleClass().add("button-box");
            grid.getStyleClass().add("control-pane");

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

            grid.add(gridBox, 0, 0);
            grid.add(btnBox, 1, 0);

            editorNode.getChildren().add(control.getTitledPane());
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
    }//TextFieldPropertyEditorSkin

}//class TextFieldPropertyEditor
