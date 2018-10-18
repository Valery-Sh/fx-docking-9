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
package org.vns.javafx.dock.api.designer;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static javafx.css.StyleOrigin.AUTHOR;
import static javafx.css.StyleOrigin.INLINE;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.designer.bean.BeanModel;
import org.vns.javafx.dock.api.designer.bean.Category;
import org.vns.javafx.dock.api.designer.bean.BeanProperty;
import org.vns.javafx.dock.api.designer.bean.PropertyPaneModelRegistry;
import org.vns.javafx.dock.api.designer.bean.PropertyPaneModelRegistry.Introspection;
import org.vns.javafx.dock.api.designer.bean.Section;
import org.vns.javafx.dock.api.designer.bean.editor.HyperlinkTitle;
import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditorFactory;

/**
 *
 * @author Valery
 */
@DefaultProperty("bean")
public class PropertyEditorPane1 extends Control {

    private final ObjectProperty bean = new SimpleObjectProperty<>();
    private final ObjectProperty<ScrollPane.ScrollBarPolicy> scrollVBarPolicy = new SimpleObjectProperty<>(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    public PropertyEditorPane1() {
    }

    public ObjectProperty beanProperty() {
        return bean;
    }

    public Object getBean() {
        return bean.get();
    }

    public void setBean(Object bean) {
        this.bean.set(bean);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new PropertyEditorPaneSkin(this);
    }

    /**
     * Sets the policy for showing the vertical scroll bar.
     *
     * @param value the value of the scroll bar policy
     */
    public void setScrollPaneVbarPolicy(ScrollPane.ScrollBarPolicy value) {
        this.scrollVBarPolicy.set(value);
    }

    /**
     * Gets the value of the property vbarPolicy of the vertical scroll bar.
     *
     * @return the value of the property vbarPolicy of the vertical scroll bar.
     */
    public ScrollPane.ScrollBarPolicy getScrollPaneVbarPolicy() {
        return scrollVBarPolicy.get();
    }

    /**
     * Specifies the policy for showing the vertical scroll bar.
     *
     * @return value the value of the scroll bar policy
     */
    public ObjectProperty<ScrollPane.ScrollBarPolicy> scrollPaneVbarPolicy() {
        return scrollVBarPolicy;
    }

    public class PropertyEditorPaneSkin extends SkinBase<PropertyEditorPane1> {

        private final VBox layout;
        private VBox beanPane;
        private Introspection introspection;
        //private List<CssMetaData<? extends Styleable, ?>> cssMetaData;
        //private final Map<String, StyleableProperty> styleableMap = FXCollections.observableHashMap();
        private final Map<Object, Map<StyleableProperty, ChangeListener>> listenerMap = FXCollections.observableHashMap();

        public PropertyEditorPaneSkin(PropertyEditorPane1 control) {
            super(control);

            beanPane = new VBox();

            layout = new VBox(getToolBar(), getStatusBar(), beanPane);
            layout.setSpacing(2);
            getSkinnable().beanProperty().addListener(this::beanChanged);
            getChildren().add(layout);
            show();
        }
        int counter = 0;
        long start = 0;
        long start1 = 0;
        long inter1 = 0;
        long inter2 = 0;
        long inter3 = 0;
        long inter4 = 0;

        protected void beanChanged(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            if (oldValue != null) {
                Set<Node> editors = beanPane.lookupAll(".it_is_pe");
                System.err.println("EDITORS SIZE = " + editors.size());
                editors.forEach(node -> {
                    if (((PropertyEditor) node).isBound()) {
                        counter++;
//                        System.err.println("isBound name = " + ((PropertyEditor)node).getName());
                    } else {
                        System.err.println("not Bound name = " + ((PropertyEditor) node).getName());

                    }

                    ((PropertyEditor) node).unbind();
                });
                System.err.println("   --- bound counter = " + counter);
                counter = 0;
            }
            listenerMap.clear();
            beanPane.getChildren().clear();
            beanPane = new VBox();
            layout.getChildren().set(2, beanPane);

            if (newValue != null) {
                //layout.getChildren().add(newValue);
                layout.getChildren().set(0, getToolBar());
                layout.getChildren().set(1, getStatusBar());
                show();
            }
        }

        private Node getStatusBar() {
            Label lb = getSkinnable().getBean() == null ? new Label()
                    : new Label("Properties: " + getSkinnable().getBean().getClass().getSimpleName());
            lb.setPadding(new Insets(4, 4, 4, 4));

            HBox hb = new HBox(lb);
            hb.setStyle("-fx-border-width: 1; -fx-border-color: lightgrey");
            return hb;
        }

        private Node getToolBar() {
            ToolBar tb = new ToolBar();
            tb.setVisible(false);
            return tb;
        }

        public void show() {
            if (getSkinnable().getBean() == null) {
                return;
            }
            start = System.currentTimeMillis();
            Map<StyleableProperty, ChangeListener> stylePropMap = listenerMap.get(getSkinnable().getBean());
            if (stylePropMap == null) {
                stylePropMap = FXCollections.observableHashMap();
                listenerMap.put(getSkinnable().getBean(), stylePropMap);
            }
            introspection = PropertyPaneModelRegistry.getInstance().introspect(getBean().getClass());
            //styleableMap.clear();
            inter1 = System.currentTimeMillis() - start;
            //cssMetaData = null;
            BeanModel beanModel = PropertyPaneModelRegistry.getInstance().getBeanModel(getBean(), introspection);

            inter2 = System.currentTimeMillis() - start;

            start = System.currentTimeMillis();

            if ((getSkinnable().getBean() instanceof Styleable)) {
                //cssMetaData = ((Styleable) getSkinnable().getBean()).getCssMetaData();
                //for (CssMetaData c : cssMetaData) {
                //    StyleableProperty sp = c.getStyleableProperty((Styleable) getSkinnable().getBean());
                //styleableMap.put(((ReadOnlyProperty) sp).getName(), sp);
                //}
            }

            VBox catVBox = new VBox();
            beanPane.getChildren().add(catVBox);

            TilePane tilePane = new TilePane();

            catVBox.getChildren().add(tilePane);
            StackPane contentPane = new StackPane();
            catVBox.getChildren().add(contentPane);

            ToggleGroup toggleGroup = new ToggleGroup();
            toggleGroup.selectedToggleProperty().addListener((v, ov, nv) -> {
                if (ov != null) {
                    String id = "#c-" + ((ToggleButton) ov).getId().substring(3);
                    contentPane.lookup(id).setVisible(false);
                }
                if (nv != null) {
                    String id = "#c-" + ((ToggleButton) nv).getId().substring(3);
                    contentPane.lookup(id).setVisible(true);
                }

            });
            for (Category c : beanModel.getItems()) {
                ToggleButton catBtn = new ToggleButton(c.getDisplayName());
                catBtn.setId("tb-" + c.getName());
                toggleGroup.getToggles().add(catBtn);

                tilePane.getChildren().add(catBtn);
                VBox cvb = new VBox();
                ScrollPane scrollPane = new ScrollPane(cvb);
                scrollPane.setId("c-" + c.getName());
                scrollPane.setVbarPolicy(getScrollPaneVbarPolicy());
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setFitToWidth(true);
                //scrollPane.setFitToHeight(true);
                contentPane.getChildren().add(scrollPane);

                for (Section s : c.getItems()) {
                    TitledPane tp = new TitledPane();
                    tp.setId("s-" + s.getName());
                    tp.setText(s.getDisplayName());
                    cvb.getChildren().add(tp);

                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(5);
                    StackPane stackPane = new StackPane(grid);
                    ColumnConstraints cc0 = new ColumnConstraints();
                    ColumnConstraints cc1 = new ColumnConstraints();

                    cc0.setPercentWidth(35);
                    cc1.setPercentWidth(65);
                    grid.getColumnConstraints().addAll(cc0, cc1);
                    int i = 0;
                    for (BeanProperty propItem : s.getItems()) {
                        Class[] propTypes = introspection.getPropTypes(propItem.getName());
                        if (propTypes == null) {
                            continue;
                        }
                        PropertyEditor editor = PropertyEditorFactory.getDefault().getEditor(propItem.getName(), propTypes);

                        if (editor != null) {
                            ((Node) editor).getStyleClass().add("it_is_pe");
                            AnchorPane ap = new AnchorPane();
                            HyperlinkTitle title = editor.getTitle();
                            Object value = null;

/*                            if ((editor instanceof ListPropertyEditor) && isObservableList(propItem.getName())) {
                                PropertyDescriptor pd = introspection.getPropertyDescriptors().get(propItem.getName());
                                try {
                                    value = pd.getReadMethod().invoke(getBean(), new Object[0]);
                                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                    Logger.getLogger(PropertyEditorPane.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            } else {
                                MethodDescriptor md = introspection.getMethodDescriptors().get(propItem.getName() + "Property");
                                try {
                                    value = md.getMethod().invoke(getBean(), new Object[0]);
                                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                    Logger.getLogger(PropertyEditorPane.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            if (value != null && isEditable(propItem)) {
                                if (value instanceof ObservableList) {
                                    ListProperty lp = new SimpleListProperty(getBean(), propItem.getName(), (ObservableList) value);
                                    //editor.bindBidirectional(lp);
                                } else {
                                    //editor.bindBidirectional((Property) value);
                                    modifyByStyleOrigin(propItem, (Property) value, editor, title);
                                }
                            } else if (value != null) {
                                if (value instanceof ObservableList) {
                                    ListProperty lp = new SimpleListProperty(getBean(), propItem.getName(), (ObservableList) value);
                                    //editor.bind(lp);
                                } else {
                                    //editor.bind((ReadOnlyProperty) value);
                                }

                            }
*/
                            //grid.add(title, 0, i);
                            //grid.add(new Label("lb " + i), 0, i);
                            //grid.add((Node) editor, 1, i);

                            tp.setContent(stackPane);
                        }
                        i++;
                    }
                }
            }

            contentPane.getChildren().forEach(node -> node.setVisible(false));

            if (toggleGroup.getToggles().size() > 0) {
                toggleGroup.getToggles().get(0).setSelected(true);
            }
            long end = System.currentTimeMillis();
            System.err.println("first INTERVAL: " + (inter1));
            System.err.println("first INTERVAL: " + (inter2));
            System.err.println("SECOND INTERVAL: " + (end - start));
            beanModel.setBean(null);
        }


        /*        private boolean isStyleableProperty(String propName) {
            return styleableMap.containsKey(propName);
        }
         */
        private boolean isObservableList(String propName) {
            Class[] types = introspection.getPropTypes(propName);
            return types.length == 2 && ObservableList.class.isAssignableFrom(types[0]);
        }

        private boolean isEditable(BeanProperty pi) {
            boolean retval = pi.isModifiable();
            if (retval) {
                PropertyDescriptor pd = introspection.getPropertyDescriptors().get(pi.getName());
                if (pd.getWriteMethod() == null && introspection.getPropTypes(pi.getName()).length == 1) {
                    retval = false;
                }
            }

            return retval;
        }

        private void modifyByStyleOrigin(BeanProperty propItem, Property prop, PropertyEditor editor, HyperlinkTitle title) {
            if (prop instanceof StyleableProperty) {

                StyleableProperty sp = (StyleableProperty) prop;
                if (sp.getStyleOrigin() != null && (sp.getStyleOrigin().equals(INLINE) || sp.getStyleOrigin().equals(AUTHOR))) {
                    editor.setEditable(false);
                    title.setCssValue(true);
                }
                Map<StyleableProperty, ChangeListener> map = listenerMap.get(getSkinnable().getBean());
                ChangeListener l = map.get(sp);

                if (l == null) {
                    l = (v, ov, nv) -> {
                        Platform.runLater(() -> {
                            if (sp.getStyleOrigin() != null && (sp.getStyleOrigin().equals(INLINE) || sp.getStyleOrigin().equals(AUTHOR))) {
                                editor.setEditable(false);
                                title.setCssValue(true);
                            } else {
                                if (isEditable(propItem)) {
                                    editor.setEditable(true);
                                    title.setCssValue(false);
                                }
                            }
                        });
                    };
                    map.put(sp, l);
                    prop.addListener(l);
                }

            }

        }

        /*        private void showInBrowser(String propName, Introspection isp) {
            String rdmethod = isp.getPropertyDescriptors().get(propName).getReadMethod().getName();
            String origin = isp.getBeanClass().getName();
            Class objClass = isp.getBeanClass();
            while (!Object.class.equals(objClass)) {
                try {
                    Method m = objClass.getMethod(rdmethod, new Class[0]);
                    if (Modifier.isPublic(m.getModifiers())) {
                        origin = objClass.getName();
                    }
                    objClass = objClass.getSuperclass();
                } catch (NoSuchMethodException ex) {
                    break;
                    //Logger.getLogger(PropertyEditorPane.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    break;
                    //Logger.getLogger(PropertyEditorPane.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            origin = origin.replace('.', '/');
            BrowserService.getInstance().showDocument(PropertyEditor.HYPERLINK + origin + ".html#" + rdmethod + "--");
        }
         */
    }// Skin
}//class PropertyEditorPane
