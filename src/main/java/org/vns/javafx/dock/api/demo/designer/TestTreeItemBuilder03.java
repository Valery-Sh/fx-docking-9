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
package org.vns.javafx.dock.api.demo.designer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.stage.Stage;
import org.vns.javafx.designer.SceneGraphView;
import org.vns.javafx.designer.TreeItemBuilder;
import org.vns.javafx.designer.TreeItemEx;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.editor.bean.BeanAdapter;
import org.vns.javafx.dock.api.editor.bean.ReflectHelper.MethodUtil;

public class TestTreeItemBuilder03 extends Application {

    private ObservableList<VBox> nodeList = FXCollections.observableArrayList();

    public ObservableList<VBox> getNodeList() {
        return nodeList;
    }

    Label testLb = new Label("testLb");
    Label gLb = new Label("glb");

    @Override
    public void start(Stage stage) throws Exception {
        testLb.setGraphic(gLb);
        Method m1 = MethodUtil.getMethod(testLb.getClass(), "graphicProperty", new Class[0]);
        Object obj = MethodUtil.invoke(m1, testLb, new Object[0]);
        System.err.println("Method m1=" + m1);
        System.err.println("Method m1 invoke = " + obj);
        System.err.println("Method obj class = " + obj);

        TreeItemEx titem = new TreeItemBuilder().build(gLb);
        titem.setValue(gLb);
        Method m2 = MethodUtil.getMethod(ObservableValue.class, "addListener", new Class[]{ChangeListener.class});
        //MethodUtil.invoke(m2, obj, new Object[] {new TreeItemObjectChangeListener(titem,"graphicProperty")});        
        testLb.setGraphic(null);
        //
        Method m3 = MethodUtil.getMethod(ObservableList.class, "addListener", new Class[]{ListChangeListener.class});
        //MethodUtil.invoke(m2, obj, new Object[] {new TreeItemObjectChangeListener(titem)});        
        System.err.println("Method m3=" + m3);

        /*        BeanAdapter ba = new BeanAdapter(this);
        Class cl = ba.getType("nodeList");
        
        System.err.println("isAssignable = " + cl.isAssignableFrom(VBox.class));
        Type tp = ba.getGenericType("nodeList");
        Type tpl = BeanAdapter.getGenericListItemType(tp);
        System.err.println("List Item Type name= " + tpl.getTypeName());
        //System.err.println("List Item Type = " + BeanAdapter.getGenericListItemType(tp));
        //System.err.println("List Item Type = " + BeanAdapter.getListItemType(tp).getName());
        System.err.println("cl = " + cl.getName());
         */
        VBox root = new VBox();
//        Pane p;
//        nodeList.add(root);
        Label btn1Graphic = new Label("btn1Graphic");
        Label lbGraphic = new Label("lbGraphic");
        btn1Graphic.setGraphic(lbGraphic);

        root.setId("ROOT");
        Button btn1 = new Button("btn1");
        btn1.setGraphic(btn1Graphic);

        root.getChildren().add(btn1);
        BorderPane borderPane1 = new BorderPane();
        Label lb1 = new Label("lb1");
        HBox hbox1 = new HBox(lb1);
        borderPane1.setRight(hbox1);

        root.getChildren().add(borderPane1);
        TitledPane titledPane1 = new TitledPane();
        root.getChildren().add(titledPane1);
        TabPane tabPane1 = new TabPane();
        root.getChildren().add(tabPane1);
        Tab tab1 = new Tab("Tab01");
        tabPane1.getTabs().add(tab1);
        Arc arc1 = new Arc();
        root.getChildren().add(arc1);
        Label lb2 = new Label("lb2");
        lb2.setGraphic(btn1Graphic);
        Label lb3 = new Label("lb3");
        Label lb4 = new Label("lb4");        
        Label lb5 = new Label("lb5");        
        
        HBox hbox2 = new HBox(lb2,lb3,lb4,lb5);
        root.getChildren().add(hbox2);
        
        
        //VBox vbox2 = new VBox(lb3);
        //hbox2.getChildren().add(vbox2);

        //TreeItemBuilder builder = new TreeItemBuilder();
        //TreeItem rootItem = builder.build(root);
        //TreeView treeView = new TreeView(rootItem);
        SceneGraphView sceneGraphView = new SceneGraphView(root);
        Scene tvScene = new Scene(sceneGraphView);
        Stage tvStage = new Stage();
        tvStage.setHeight(300);
        tvStage.setWidth(300);

        tvStage.setScene(tvScene);
        //rootItem.setExpanded(true);
        //rootItem.setValue(itemLabel);        
        //root.getChildren().add(treeView);
        //customizeCell(treeView);
        Label lbl1 = new Label("lbl1");
        //btn1.setGraphic(lbl1);
        stage.sizeToScene();
        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setTitle("Stage TestTreeItemBuilder01");
        //NodeDescriptor labeledNd = new NodeDescriptor(btn1);
        stage.setScene(scene);
        ObservableList<Node> ol = FXCollections.observableArrayList();
        stage.setOnShown(s -> {
            System.err.println("p1 = " + btn1Graphic.getParent());
            BeanAdapter pba = new BeanAdapter(btn1Graphic.getParent());
            ObservableList l = (ObservableList) pba.get("children");
            //l.remove(btn1Graphic);
            Method method;
            Node nd = btn1Graphic.getParent();
            Class cl = nd.getClass();
            try {
                while ( ! Parent.class.equals(cl)) {
                    cl = cl.getSuperclass();    
                }
                
                method = cl.getDeclaredMethod("getChildren");
                method.setAccessible(true);
                ObservableList list = (ObservableList) method.invoke(nd);
                System.err.println("List size() = " + list.size());
                list.forEach(el -> {
                    System.err.println("  --- List el = " + el);
                });
                
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(TestTreeItemBuilder03.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(TestTreeItemBuilder03.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TestTreeItemBuilder03.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TestTreeItemBuilder03.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TestTreeItemBuilder03.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.err.println("p2 = " + btn1Graphic.getParent().getParent());
            System.err.println("p3 = " + btn1Graphic.getParent().getParent().getParent());
            System.err.println("hbox2 children.size()=" + hbox2.getChildren().size());
            /*            System.err.println("titleProperty = " + labeledNd.getTitleProperty()); 
            System.err.println("name = " + child1.getName()); 
            System.err.println("isPlaceholder  = " + child1.isPlaceholder()); 
            System.err.println("placeholder isHideIfNull = " + child1.isHideIfNull()); 
            System.err.println("placeholder title = " + child1.getTitle()); 
            
            System.err.println("placeholderStyleClass  = " + child1.getStyleClass()); 
             */
            System.err.println("isAssignable " + Set.class.isAssignableFrom(ol.getClass()));

        });
        stage.setMinHeight(100);
        root.setMinHeight(100);
        stage.setMinWidth(400);
        root.setMinWidth(400);

        stage.show();
        tvStage.show();
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

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

    /*   protected void customizeCell(TreeView treeView) {
        TreeViewEx t = treeView;
        t.setCellFactory((TreeView<ItemValue> tv) -> {
            TreeCell<ItemValue> cell = new TreeCell<ItemValue>() {
                @Override
                public void updateItem(ItemValue value, boolean empty) {
                    super.updateItem(value, empty);

                    if (empty || value == null) {
                        setText(null);
                        setGraphic(null);
                        if (this.getUserData() != null) {
                            Object[] o = (Object[]) this.getUserData();
                            if (o[0] != null) {
                                this.removeEventHandler(DragEvent.DRAG_OVER, (SceneGraphView.TreeItemCellDragEventHandler) o[0]);
                            }
                        }
                        this.setOnDragDetected(null);
                        this.setOnDragDropped(null);
                        this.setOnDragDone(null);
                    } else {
                        this.setGraphic(value.getCellGraphic());
                        if (value.getTreeItemObject() instanceof Node) {
                            setId(((Node) value.getTreeItemObject()).getId());
                        }
                    }
                }
            };
            return cell;
        });
    }
     */
}
