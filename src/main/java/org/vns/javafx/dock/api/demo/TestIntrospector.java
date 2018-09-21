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
package org.vns.javafx.dock.api.demo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.dock.api.designer.bean.editor.IntegerListPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestIntrospector extends Application {

    boolean updating;

    @Override
    public void start(Stage stage) throws ClassNotFoundException {

        Button btn1 = new Button("Print ObservableList");
        btn1.setId("olga");
        Button btn2 = new Button("Button btn2");
        Button btn3 = new Button("Empty List");
        Button btn4 = new Button("add Empty String");

        ListProperty lp = new SimpleListProperty(btn1, "styleClass", btn1.getStyleClass());
        ObservableList ol = FXCollections.observableArrayList();
        System.err.println("isInstance = " + (ObservableList.class.isAssignableFrom(btn1.getStyleClass().getClass())));
        if (true) {
            //return;
        }
        //System.err.println("lp." + lp.getName());
        System.err.println("lp.name = " + lp.getName() + "; lb.getBean().class = " + lp.getBean().getClass().getSimpleName());

        long start = System.currentTimeMillis();
        List<String> excludeProps = FXCollections.observableArrayList("properties", "pseudoClassStates", "scene","parent","skin",
                "graphic", "contentMenu", "clip");

        Map<String, MethodDescriptor> methodDescr = new HashMap<>();
        Map<String, PropertyDescriptor> propDescrs = new HashMap<>();
        Map<String, FXPropertyDescriptor> fxPropertyDescriptors = new HashMap<>();
        
        MyBean mb = new MyBean();
        try {
            BeanInfo info = Introspector.getBeanInfo(btn1.getClass());
            MethodDescriptor[] mds = info.getMethodDescriptors();
            for (MethodDescriptor md : mds) {
//                if ( ! md.readMethod().getName().startsWith("get") && ! md.readMethod().getName().startsWith("set")) {
//                    System.err.println("not get not set" + md.readMethod().getName());
//                }
/*                if ( md.getName().equals("getId")) {
                    Method idGet = md.getMethod();
                    String id = (String) idGet.invoke(btn1, new Object[0]);
                    Type tp = idGet.getGenericReturnType();
                    System.err.println("btn1.getId = " + id + "; getGenericName = " + tp.getTypeName());
                }
*/
/*                if ( md.getName().equals("getStyleClass")) {
                    Method idGet = md.getMethod();
                    ObservableList<String> olist = (ObservableList<String>) idGet.invoke(btn1, new Object[0]);
                    Type tp = idGet.getGenericReturnType();
                    System.err.println("olist getGenericName = " + tp.getTypeName());
                    Class c  = BeanAdapter.getListItemType(tp);
                    System.err.println("olist generic class = " + c.getName());
                }                
*/
                if (md.getName().endsWith("Property")) {
                    methodDescr.put(md.getName(), md);
                }
            }
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if ( excludeProps.contains(pd.getName()) ) {
                    continue;
                }
                
                FXPropertyDescriptor fxDescr = new FXPropertyDescriptor(pd.getName());
                fxPropertyDescriptors.put(pd.getName(), fxDescr);
                if (methodDescr.containsKey(pd.getName() + "Property")) {
                    fxDescr.setPropertyMethod(methodDescr.get(pd.getName() + "Property").getMethod());
                }
                if (pd.getReadMethod() != null) {
                    fxDescr.setReadMethod(pd.getReadMethod());
                }
                if (pd.getWriteMethod() != null) {
                    fxDescr.setWritetMethod(pd.getWriteMethod());
                }
                fxDescr.setPropertyType(pd.getPropertyType());
                
/* DO NOT DELETE !!!!
                if (methodDescr.containsKey(pd.getName() + "Property")) {
                    propDescrs.put(pd.getName(), pd);
                } else if (pd.getReadMethod() != null) {
                    Class<?> clazz = pd.getReadMethod().getReturnType();
                    if (ObservableList.class.isAssignableFrom(clazz) || ObservableMap.class.isAssignableFrom(clazz)  || ObservableSet.class.isAssignableFrom(clazz)) {
                        System.err.println("MMM = " + pd.getReadMethod().getName());
                        propDescrs.put(pd.getName(), pd);
                    }
                }
*/                
            }
        long end = System.currentTimeMillis();
        System.err.println("1) Interval = " + (end - start));
        
            System.err.println("=====================================================================");
            fxPropertyDescriptors.forEach((k,v) -> {
                System.err.println("name = " + k);
                System.err.println("  --- getPropertyMethod = " + v.getPropertyMethod());
                System.err.println("  --- getReadMethod = " + v.getReadMethod());
                System.err.println("  --- getWriteMethod = " + v.getWritetMethod());
                System.err.println("  --- getPropertyType = " + v.getPropertyType().getSimpleName());
                System.err.println("----------------------------------------------------------------");
                
            });
            System.err.println("FXPropertyDescriptors size = " + fxPropertyDescriptors.size());
            System.err.println("=====================================================================");
            
            /*            for ( MethodDescriptor md : mds) {
                System.err.println("MethodName = " + md.readMethod().getName());
                System.err.println("returnType = " + md.readMethod().getReturnType().getSimpleName());
                System.err.println("toGeneric = " + md.readMethod().toGenericString());
            }
             */
            //PropertyDescriptor pd1 = new PropertyDescriptor("styleClass", btn1.getClass(),"getStyleClass", null);
            //System.err.println("readMethod Name = " + pd1.getReadMethod().getName() + "; name = " + pd1.getName());
            //System.err.println("writeMethod Name = " + pd.getWriteMethod().getName());
        } catch (IntrospectionException ex) {
            System.err.println("INROSPECTION Exception ex = " + ex.getMessage());
        }
        long end = System.currentTimeMillis();
        System.err.println("2) Interval = " + (end - start));
        System.err.println("propDescr.size = " + propDescrs.size());

        //btn4.getStyleClass().add(null);
        System.err.println("btn4.getStyleClass() size=" + btn4.getStyleClass().size());
        GridPane grid = new GridPane();
        grid.setHgap(10);

        StackPane root = new StackPane(grid);
        Label lb1 = new Label("Text Alignment");
        ObservableList<Integer> list = FXCollections.observableArrayList();

        //list.add(null);
        //System.err.println("list.size=" + list.size() + "; item = " + list.get(0));
        IntegerListPropertyEditor tf1 = new IntegerListPropertyEditor();
        tf1.setOnKeyPressed(e -> {
            if (e.getEventType() == KeyEvent.KEY_PRESSED && e.isControlDown()) {
                System.err.println("isControlDown = " + e.isControlDown());
                System.err.println("getText() = '" + e.getText() + "'");
                System.err.println("getKeCode() = '" + e.getCode() + "'");

            }
        });

        System.err.println("tf1.getNullSubstitution = " + tf1.getNullSubstitution());
        System.err.println("tf1.separator = " + tf1.getSeparator());

//            retval = new ObservableListPropertyEditor<String>();
        //tf1.setStringConverter(new ObservableListItemStringConverter(tf1,String.class));  
        //tf1.setNullSubstitution("<NULL>");
        //tf1.setSeparator(",", "\\s*,\\s*");
        tf1.bindBidirectional(list);
        list.add(null);

        btn1.setOnAction(e -> {
            list.forEach(s -> {
                System.err.println("list item = " + s);
            });
        });

        Label lb2 = new Label("111111lable 1");
        lb2.setFont(new Font(13));
//        SimpleStringPropertyEditor tf2 = new SimpleStringPropertyEditor("1234");

//        tf2.setFont(new Font(13));
        btn2.setOnAction(e -> {
            btn2.setPrefWidth(200.56);
        });

        btn3.setOnAction(e -> {
            list.clear();
        });
        btn4.setOnAction(e -> {
            list.add(15);
        });

        Label elb = new Label("errors");
        HBox ehb = new HBox();
        ehb.setStyle("-fx-background-color: aqua");
        Circle shape = new Circle(2, Color.RED);
        shape.setManaged(false);
        ehb.getChildren().add(shape);

        grid.add(lb1, 0, 0);
        grid.add(tf1, 1, 0);
        grid.add(elb, 0, 1);
        grid.add(ehb, 1, 1);
        grid.add(lb2, 0, 2);
        grid.add(btn3, 0, 3);
        grid.add(btn4, 0, 4);

//        grid.add(tf2, 1, 2);
        ColumnConstraints cc0 = new ColumnConstraints();
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc20 = new ColumnConstraints();

        cc0.setPercentWidth(35);
        cc1.setPercentWidth(65);

        grid.getColumnConstraints().addAll(cc0, cc1);
        root.setPrefSize(500, 200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        VBox vbox = new VBox(btn1, btn2);
        VBox propPane = new VBox();
        TilePane tilePane = new TilePane();
        propPane.setStyle("-fx-border-width: 2; -fx-border-color: green");
        vbox.getChildren().add(propPane);
        propPane.getChildren().add(tilePane);

        StackPane contentPane = new StackPane();
        propPane.getChildren().add(contentPane);
        contentPane.setStyle("-fx-border-width: 2; -fx-border-color: blue");
        Button propBtn = new Button("Properties");
        Button layoutBtn = new Button("Layout");
        Button codeBtn = new Button("Code");
        tilePane.getChildren().addAll(propBtn, layoutBtn, codeBtn);
        //
        // Properties Category
        //
        TitledPane propTitledPane1 = new TitledPane();
        propTitledPane1.setText("Node");

        TitledPane propTitledPane2 = new TitledPane();
        propTitledPane2.setText("JavaFx CSS");
        TitledPane propTitledPane3 = new TitledPane();
        propTitledPane3.setText("Extras");
        VBox propSecBox = new VBox(propTitledPane1, propTitledPane2, propTitledPane3);
        contentPane.getChildren().add(propSecBox);

        TitledPane layoutTitledPane1 = new TitledPane();
        layoutTitledPane1.setText("Content");
        TitledPane layoutTitledPane2 = new TitledPane();
        layoutTitledPane2.setText("Internals");
        VBox layoutSecBox = new VBox(layoutTitledPane1, layoutTitledPane2);
        contentPane.getChildren().add(layoutSecBox);
        layoutSecBox.setVisible(false);

        TitledPane codeTitledPane1 = new TitledPane();
        codeTitledPane1.setText("onAction");
        VBox codeSecBox = new VBox(codeTitledPane1);
        contentPane.getChildren().add(codeSecBox);
        codeSecBox.setVisible(false);

        propBtn.setDisable(true);

        propBtn.setOnAction(e -> {
            propBtn.setDisable(true);
            propSecBox.setVisible(true);
            layoutBtn.setDisable(false);
            layoutSecBox.setVisible(false);
            codeBtn.setDisable(false);
            codeSecBox.setVisible(false);
        });
        layoutBtn.setOnAction(e -> {
            layoutBtn.setDisable(true);
            layoutSecBox.setVisible(true);
            propBtn.setDisable(false);
            propSecBox.setVisible(false);
            codeBtn.setDisable(false);
            codeSecBox.setVisible(false);
        });
        codeBtn.setOnAction(e -> {
            codeBtn.setDisable(true);
            codeSecBox.setVisible(true);
            propBtn.setDisable(false);
            propSecBox.setVisible(false);
            layoutBtn.setDisable(false);
            layoutSecBox.setVisible(false);
        });

        VBox vbox2 = new VBox(btn2);

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);
        System.err.println("R = " + getClass().getResource("resources/demo-styles.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("resources/demo-styles.css").toExternalForm());

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static class MyBean {
        private StringProperty id = new SimpleStringProperty("valery");
        
        public StringProperty idProperty() {
            return id;
        }
        public String getId() {
            return id.get();
        }

        public void setId(String id) {
            this.id.set(id);
        }
        
    }
    
    public static class FXPropertyDescriptor {
        private String name;
        private Method propertyMethod;
        private Method readMethod;
        private Method writetMethod;        
        private Class<?> propertyType;

        public FXPropertyDescriptor() {
        }

        public FXPropertyDescriptor(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Method getPropertyMethod() {
            return propertyMethod;
        }

        public void setPropertyMethod(Method propertyMethod) {
            this.propertyMethod = propertyMethod;
        }

        public Method getReadMethod() {
            return readMethod;
        }

        public void setReadMethod(Method readMethod) {
            this.readMethod = readMethod;
        }

        public Method getWritetMethod() {
            return writetMethod;
        }

        public void setWritetMethod(Method writetMethod) {
            this.writetMethod = writetMethod;
        }

        public Class<?> getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(Class<?> propertyType) {
            this.propertyType = propertyType;
        }

    }
}
