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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Border;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.vns.javafx.dock.api.designer.bean.BeanModel;
import org.vns.javafx.dock.api.designer.bean.Category;
import org.vns.javafx.dock.api.designer.bean.PropertyItem;
import org.vns.javafx.dock.api.designer.bean.PropertyPaneModelRegistry;
import org.vns.javafx.dock.api.designer.bean.PropertyPaneModelRegistry.Introspection;
import org.vns.javafx.dock.api.designer.bean.Section;
import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditorFactory;

/**
 *
 * @author Valery
 */
@DefaultProperty("bean")
public class PropertyEditorPane extends Control {

    public static final String HYPERLINK = "https://docs.oracle.com/javase/8/javafx/api/";

    private final ObjectProperty bean = new SimpleObjectProperty<>();
    private final ObjectProperty<ScrollPane.ScrollBarPolicy> scrollVBarPolicy = new SimpleObjectProperty<>(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    public PropertyEditorPane() {
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

    public class PropertyEditorPaneSkin extends SkinBase<PropertyEditorPane> {

        private VBox layout;
        private VBox beanPane;

        public PropertyEditorPaneSkin(PropertyEditorPane control) {
            super(control);

            beanPane = new VBox();

            layout = new VBox(getToolBar(), getStatusBar(), beanPane);
            layout.setSpacing(2);
            getSkinnable().beanProperty().addListener(this::beanChanged);
            getChildren().add(layout);
            show();
        }

        protected void beanChanged(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            if (oldValue != null) {
                beanPane.getChildren().clear();

            }
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
            Introspection isp = PropertyPaneModelRegistry.getInstance().introspect(getBean().getClass());
            BeanModel beanModel = PropertyPaneModelRegistry.getInstance().getBeanModel(getBean(), isp);

            VBox catVBox = new VBox();
            beanPane.getChildren().addAll(catVBox);

            TilePane tilePane = new TilePane();
            //catVBox.setStyle("-fx-border-width: 2; -fx-border-color: green");
            //vbox.getChildren().add(propPane);
            catVBox.getChildren().add(tilePane);
            StackPane contentPane = new StackPane();
            catVBox.getChildren().add(contentPane);
            //contentPane.setStyle("-fx-border-width: 2; -fx-border-color: blue");

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
                //catBtn.setId(c.getName());
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
                    for (PropertyItem pi : s.getItems()) {
                        Class[] propTypes = isp.getPropTypes(pi.getName());
                        if (propTypes == null) {
                            continue;
                        }
                        PropertyEditor ed = PropertyEditorFactory.getDefault().getEditor(pi.getName(), propTypes);
                        if (ed != null) {
                            //Label lb = new Label(pi.getDisplayName());
                            Hyperlink lb = new Hyperlink(pi.getDisplayName());
                            lb.setTextFill(Color.BLACK);
                            lb.setOnAction(a -> {
                                lb.setVisited(false);
                                showInBrowser(pi.getName(), isp);
                            });
                            lb.setBorder(Border.EMPTY);

                            grid.add(lb, 0, i);
                            grid.add((Node) ed, 1, i);
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
        }

        private void showInBrowser(String propName, Introspection isp) {
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
            BrowserService.getInstance().showDocument(HYPERLINK + origin + ".html#" + rdmethod + "--");
        }

    }// Skin
}//class PropertyEditorPane
