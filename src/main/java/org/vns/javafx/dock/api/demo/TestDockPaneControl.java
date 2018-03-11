package org.vns.javafx.dock.api.demo;

import java.util.UUID;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.DockTabPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockPaneContext;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.ScenePaneContext;

/**
 *
 * @author Valery
 */
public class TestDockPaneControl extends Application {

    Stage stage;
    Scene scene;
    Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("PRIMARY");
        System.err.println("UUID = " + UUID.randomUUID());

        StackPane stackPane = new StackPane();
        HBox root = new HBox();
        root.getChildren().add(stackPane);
        //StackPane root = new StackPane();
        //DockPane dockPane1 = new DockPane();
        //DockNode dnc1 = new DockNode("DockNodeControl dnc1");

        //loader.setSaveOnClose(true);
        DockPane dockPane2 = new DockPane();
        //loader.register("dockPane2", dockPane2);
        //dockPane2.getItems().add(new DockSplitPane());
        dockPane2.setId("dockPane2");
        DockNode dnc1_1 = new DockNode();
        StackPane formPane = new StackPane();
        formPane.setId("sp1");
        dnc1_1.setContent(formPane);
        DockNode dnc2_1 = new DockNode();
        dnc1_1.setId("dnc1_1");
        dnc1_1.setTitle("dnc1_1");
        dnc2_1.setId("dnc2_1");
        dnc2_1.setTitle("dnc2_1");

        Button b1_1 = new Button("b1_1");
        b1_1.setOnAction(a -> {
            HBox h = new HBox(new Button("Title Bar"));
            h.setMaxHeight(Region.USE_PREF_SIZE);
            dockPane2.setTitleBar(h);
        });
        Button b1_2 = new Button("b1_2");
        //dnc1_1.setContent(b1_1);
        dnc2_1.setContent(b1_2);
        Stage popup = new Stage();
        popup.initOwner(stage);
        VBox vbox = new VBox();
        Scene popupScene = new Scene(vbox);
        popup.setScene(popupScene);
        Button popupBtn = new Button("Popup Button");
        vbox.getChildren().add(popupBtn);
        popupBtn.setOnAction(a -> {
            if (dnc1_1.getContent().getId() == "sp1") {
                dnc1_1.setContent(new StackPane());
            } else {
                ((StackPane) dnc1_1.getContent()).getChildren().add(new Button("New Button"));
            }

        });

        b1_2.setOnAction(a -> {
            popup.show();
        });

        VPane vp1_1 = new VPane();
        vp1_1.getItems().addAll(dnc1_1, dnc2_1);
//        System.err.println("");

        vp1_1.setId("vp1_1");
        dockPane2.getItems().add(vp1_1);
        System.err.println("0000 dnc1_1 getTargetContext() = " + dnc1_1.getContext().getLayoutContext());

        DockNode dnc1 = new DockNode();
        dnc1.setTitle("dnc1");
        DockNode dnc2 = new DockNode();
        dnc2.setTitle("dnc2");
        DockNode dnc3 = new DockNode();
        dnc3.setTitle("DockNodeControl dnc3");

        DockNode dnc4 = new DockNode("DockNodeControl dnc4");
        //loader.register("dnc4", dnc4);
        //loader.register("dnc2", dnc2);

        dnc1.setId("dnc1");
        dnc2.setId("dnc2");
        dnc3.setId("dnc3");
        dnc4.setId("dnc4");

        VPane vs1 = new VPane();

        //VBox vs1 = new VBox();
        vs1.setId("vs1");
        //dockPane2.getItems().add(vs1);
        HPane hs1 = new HPane(dnc1, dnc2);
        hs1.setId("hs1");
        ////////// --------------------------
        vs1.getItems().addAll(hs1);
        vs1.getItems().addAll(dnc3);
        System.err.println("TARGET CONTEXT: " + Dockable.of(dnc3).getContext().getLayoutContext());
        ////////// --------------------------
        dockPane2.getItems().add(vs1);

        stackPane.getChildren().add(dockPane2);

        DockTabPane dockTabPane1 = new DockTabPane();
        //loader.register("dockTabPane1", dockTabPane1);
        Button tabButton1 = new Button("Tab Button1");
        DockNode tabDnc1 = new DockNode(" tan Dnc1");
        tabDnc1.setId("tabDnc1");
        DockNode tabDnc2 = new DockNode(" tab Dnc2");
        tabDnc2.setId("tabDnc3");
        Button tabDnc1Btn1 = new Button("Tab Dnc1 Btn");
        Button tabDnc2Btn1 = new Button("Tab Dnc2 Btn");
        tabDnc1.setContent(tabDnc1Btn1);
        tabDnc2.setContent(tabDnc2Btn1);
        DockNode tabDnc3 = new DockNode(" tab Dnc3");
        tabDnc3.setId("tabDnc2");

        dockTabPane1.dockNode(tabDnc1);
        dockTabPane1.dockNode(tabDnc2);
        dockTabPane1.dockNode(tabDnc3);
        Tab tab = new Tab("Not dock Tab", tabButton1);

        System.err.println("TAB: " + System.identityHashCode(tab));
        dockTabPane1.getTabs().add(tab);
        root.getChildren().add(0, dockTabPane1);

        DockableContext dc = DockRegistry.dockable(dnc3).getContext();
        LayoutContext dtc = dc.getLayoutContext();

        Button b1 = new Button("remove dnc1.titleBar");
        Button b2 = new Button("add dnc4");
        Button b3 = new Button("remove dnc4");
        Button b4 = new Button("change dnc1 DividerPos");
        Button b5 = new Button("reset");
        Button b6 = new Button("print");

        VBox content = new VBox(b1, b2, b3, b4, b5, b6);
        dnc1.setContent(content);
        Label contentLabel = new Label("CONTENT LABEL");
        dnc2.setContent(contentLabel);
        contentLabel.setOnMouseClicked(e -> {
            ((DockPaneContext) ((ScenePaneContext) dnc2.getContext().getLayoutContext()).getRestoreContext()).restore(Dockable.of(dnc2));
        });
        Label childLabel = new Label("dnc3 label");
        dnc3.setContent(new StackPane());
        ((StackPane) dnc3.getContent()).getChildren().add(childLabel);

        b1.setOnAction(a -> {
            //System.err.println("----------  hs1.sz=" + hs1.getItems().size());
            //loader.save();
            if (dnc1.getTitleBar() == null) {
                dnc1.setTitleBar(Dockable.of(dnc1).getContext().createDefaultTitleBar("dnc1 () new titleBar"));
            } else {
                dnc1.setTitleBar(null);
            }
            //loader.save(dockPane1);
            //loader.reload();
            //System.err.println(loader.toString(dockPane1));
        });
        b2.setOnAction(a -> {
            //System.err.println("before (b2)hs1.sz=" + hs1.getItems().size());
            hs1.getItems().add(dnc4);
            //System.err.println("after (b2)hs1.sz=" + hs1.getItems().size());

        });
        b3.setOnAction(a -> {
            //System.err.println("Tabs size = " + dockTabPane1.getTabs().size());
            //System.err.println("before (b3)hs1.sz=" + hs1.getItems().size());
            hs1.getItems().remove(dnc4);
            //System.err.println("after (b3)hs1.sz=" + hs1.getItems().size());
        });
        b4.setOnAction(a -> {

            System.err.println("dockPane2.getRoot().getItems() " + dockPane2.getItems().size());
            dockPane2.getItems().forEach(it -> {
                System.err.println("   --- item = " + it);
            });
        });


        /*        b6.setOnAction(a -> {
            System.err.println("dnc1.dragNose = " + Dockable.of(dnc1).getContext().getDragNode());            
            b6.getScene().getWindow().setWidth(b6.getScene().getWindow().getWidth() + 20);
                        System.err.println("================================");
            Node n = dockPane1.getItems().get(0);
            System.err.println(n);
            //System.err.println(loader.toString(dockPane1));
            System.err.println("================================");
            System.err.println("dockPane2 dnc3 " + ((SplitPane)dockPane2.getItems().get(0)).getItems().size());
            System.err.println("================================");
            
        });
         */
        b6.setOnMousePressed(e -> {
            System.err.println("b6 pressed");
        });
        b6.setOnMouseReleased(e -> {
            System.err.println("b6 released");
        });

        //root.getChildren().add(dockPane1);
        scene = new Scene(root, 550, 550);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
        //popup.show();
        Stage stage1 = new Stage();
        stage1.setTitle("Node Node Dockables");
        VBox root1 = new VBox();
        Scene scene1 = new Scene(root1);
        stage1.setScene(scene1);
        Button ndBtn1 = new Button("ndBtn1");
        DockRegistry.makeDockable(ndBtn1);

        TabNode tab1 = new TabNode("Tab1 of TabNode");
        DockRegistry.getInstance().register(tab1);
        tab1.getContext().setDragNode(tab1.node());
        root1.getChildren().add(tab1.node());

        TabNode tab2 = new TabNode("Tab2");
        DockRegistry.getInstance().register(tab2);
        tab2.getContext().setDragNode(tab2.node());
        Node node = new Label("Tab2 Label");
        tab2.getContext().setDragContainer(new DragContainer(DragContainer.placeholderOf(node), node));
        //tab2.getContext().getDragContainer().setCarrier(Dockable.of(tab2));        
        root1.getChildren().add(tab2.node());

        TabNode tab3 = new TabNode("Tab3");
        DockRegistry.getInstance().register(tab3);
        tab3.getContext().setDragNode(tab3.node());
        tab3.getContext().setDragContainer(new DragContainer(DragContainer.placeholderOf("Shyshkin"), "Shyshkin"));
        //tab2.getContext().getDragContainer().setCarrier(Dockable.of(tab2));        
        root1.getChildren().add(tab3.node());

        Button dockableBtn1 = new Button("dockableBtn1");

        //DockRegistry.makeDockable(dockableBtn1);
        System.err.println("dockableBtn1.isResizable() = " + dockableBtn1.isResizable());

        tab1.getContext().setDragContainer(new DragContainer(dockableBtn1, dockableBtn1));
        //tab1.getContext().getDragContainer().setPlaceholder(dockableBtn1);
        //tab1.getContext().getDragContainer().setCarrier(Dockable.of(tab1));
        //Node graphic = tab1.getContext().getDragContainer().getGraphic();
        stage.setHeight(350);
        stage.setWidth(350);
        stage1.setAlwaysOnTop(true);
        //stage1.show();
        Stage sideBarStage = getSideBarStage(dnc1_1);
        sideBarStage.show();
        Stage stage2 = new Stage();
        BorderPane rootBorderPane = new BorderPane();
        Scene scene2 = new Scene(rootBorderPane);
        stage2.setScene(scene2);
        Button btnBP = new Button("BorderPaneButton");
        rootBorderPane.setCenter(btnBP);
        stage2.sizeToScene();
        stage2.setAlwaysOnTop(true);        
 
        stage2.setMinWidth(rootBorderPane.minWidth(DockUtil.heightOf(btnBP)));
        stage2.setMinHeight(rootBorderPane.minHeight(DockUtil.widthOf(btnBP)));
        
        //setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        //setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);
        double prefWidth = rootBorderPane.prefWidth(DockUtil.heightOf(btnBP));
        double prefHeight = rootBorderPane.prefHeight(DockUtil.widthOf(btnBP));

        rootBorderPane.setPrefWidth(prefWidth);
        rootBorderPane.setPrefHeight(prefHeight);
        rootBorderPane.setStyle("-fx-background-color: red");
        stage2.show();

        System.err.println("VPAne getParent() = " + vp1_1.getParent() + "; getParent.getParent = " + vp1_1.getParent().getParent());
        System.err.println("2 getParent() = " + vp1_1.getParent().getParent().getParent().getParent());
        System.err.println("3 dnc1_1 getTargetContext() = " + dnc1_1.getContext().getLayoutContext());
        //dnc1_1
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public Stage getSideBarStage(DockNode dn) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        StackPane root = new StackPane();

        stage.setTitle("Test DockSideBar");
        SplitPane p = new SplitPane();
        p.setId("Pane p");
        Button pBtn = new Button("Pane Btn ");
        p.getItems().add(pBtn);

        DockSideBar sideBar01 = new DockSideBar();
        sideBar01.setOrientation(Orientation.VERTICAL);
        sideBar01.setRotation(DockSideBar.Rotation.UP_DOWN);
        sideBar01.setSide(Side.RIGHT);

        //sideBar01.setHideOnExit(true);
        //borderPane.getChildren().add(sideBar01);
        root.setPrefHeight(300);
        root.setPrefWidth(300);

        Button b01 = new Button("Change Rotate Angle");

        Scene scene = new Scene(root);
        //scene.getRoot().setStyle("-fx-background-color: yellow");
        root.getChildren().add(p);

        System.err.println("SP PARENT = " + pBtn.getParent());
        root.getChildren().remove(p);

        DockNode dn01 = new DockNode();
        dn01.setPrefHeight(100);
        dn01.setTitle("DockNode: dn01");
        b01.setOnAction(a -> {
            if (null != sideBar01.getRotation()) {
                switch (sideBar01.getRotation()) {
                    case DEFAULT:
                        sideBar01.setRotation(DockSideBar.Rotation.UP_DOWN);
                        break;
                    case UP_DOWN:
                        sideBar01.setRotation(DockSideBar.Rotation.DOWN_UP);
                        break;
                    case DOWN_UP:
                        sideBar01.setRotation(DockSideBar.Rotation.DEFAULT);
                        break;
                    default:
                        break;
                }
            }

        });

        Button b02 = new Button("Change Orientation");
        Button b03 = new Button("Change Side");
        b03.setOnAction(a -> {

            if (null != sideBar01.getSide()) {
                switch (sideBar01.getSide()) {
                    case RIGHT:
                        sideBar01.setSide(Side.LEFT);
                        break;
                    case LEFT:
                        sideBar01.setSide(Side.TOP);
                        break;
                    case TOP:
                        sideBar01.setSide(Side.BOTTOM);
                        break;
                    case BOTTOM:
                        sideBar01.setSide(Side.RIGHT);
                        break;
                    default:
                        break;
                }
            }

        });
        Button b04 = new Button("set dragNode");
        Button dragButton1 = new Button();
        dragButton1.getStyleClass().add("drag-icon");
        Button dragButton2 = new Button();
        ImageView iv = new ImageView("/org/vns/javafx/dock/api/resources/drag-hand-2-16x16.png");
        //ImageView iv = new ImageView("/org/vns/javafx/dock/api/resources/drag-12x12.png");
        dragButton2.setGraphic(iv);
        //dragButton2.setStyle("-fx-padding: 0");

        b04.setOnAction(a -> {
            if (dn.getContent().getId() == "sp1") {
                dn.setContent(new StackPane());
            } else {
                ((StackPane) dn.getContent()).getChildren().add(new Button("New Button"));
            }

            if (sideBar01.getDragNode() == null || sideBar01.getDragNode() == dragButton2 || sideBar01.getDragNode() == iv) {
                //sideBar01.setDragNode(dragButton1);
            } else {
                //sideBar01.setDragNode(dragButton2);
                //sideBar01.setDragNode(iv);
                //iv.setMouseTransparent(true);
            }

        });
        Button b05 = new Button("Info");
        b05.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent a) {

                System.err.println(";sideBar01.bounds=" + sideBar01.getLayoutBounds());
                System.err.println("sideBar width=" + sideBar01.getWidth());
                System.err.println("sideBar height=" + sideBar01.getHeight());
                System.err.println("");
                //sideBar01.getScene().getWindow().setWidth(sideBar01.getScene().getWindow().getWidth() - 20);
                //sideBar01.setPrefWidth(sideBar01.getWidth() - 20);
                //sideBar01.getScene().getWindow().sizeToScene();
            }
        });

        VBox vb = new VBox();
        vb.getChildren().addAll(b01, b02, b03, b04, b05);
        root.getChildren().add(vb);
        //borderPane.getChildren().add(b02);
        //StackPane.setAlignment(vb,Pos.CENTER_LEFT);

        root.getChildren().add(sideBar01);
        StackPane.setAlignment(sideBar01, Pos.CENTER_RIGHT);

        b02.setOnAction(a -> {
            if (sideBar01.getOrientation() == Orientation.VERTICAL) {
                sideBar01.setOrientation(Orientation.HORIZONTAL);
            } else if (sideBar01.getOrientation() == Orientation.HORIZONTAL) {
                sideBar01.setOrientation(Orientation.VERTICAL);
            }

        });

        //sideBar01.dock(dn01);
        DockNode dn02 = new DockNode();
        //dn02.setPrefSize(150,100);
        //dn02.setMinSize(100,50);

        dn02.setId("dn02");
        VBox vb2 = new VBox();
        Button dn02Btn = new Button("------------- dn02 button ---------------");
        dn02.setContent(dn02Btn);
        dn02Btn.setOnAction(a -> {
            System.err.println("SFFFFFFFFFF" + dn02.getContext().isFloating());
            System.err.println(" === " + dn02.getScene().getWindow());
            // ((SidePaneController)sideBar01.getLayoutContext()).cont.changeSize();
        });
        //dn02.setContent(vb2);
        //vb2.getChildren().add(new Button("dn02 button"));
        //dn02.setPrefHeight(100);
        dn02.getContext().setTitle("DockNode: dn02");
        dn02.setId("dn02");
        //sideBar01.dock(dn02);
        //scene.getRoot().setStyle("-fx-background-color: yellow");
        //sideBar01.getToolBar().setStyle("-fx-padding: 0;");
        //sideBar01.setStyle("-fx-padding: 0; -fx-border-width: 0; -fx-border-insets: 0,0,0,0; -fx-border-color: transparent");
        //sideBar01.getToolBar().setStyle("-fx-padding: 0; -fx-border-width: 0;  -fx-border-insets: 0,0,0,0;-fx-border-color: transparent");
        DockNode dn03 = new DockNode();

        //dn03.setPrefHeight(100);
        dn03.getContext().setTitle("DockNode: dn03");
        DockNode dn04 = new DockNode();
        dn04.getContext().setTitle("DockNode: dn04");

//        sideBar01.getItems().add(dn03);
        //sideBar01.dock(dn02);
        //sideBar01.dock(dn03);     
        sideBar01.addItems(dn02, dn03, dn04);
//        sideBar01.setMaxSize(sideBar01.getToolBar().getMaxWidth(), sideBar01.getToolBar().getMaxHeight());
//        sideBar01.setMinSize(sideBar01.getToolBar().getMinWidth(), sideBar01.getToolBar().getMinHeight());        
        //stage.setTitle("Main Dockable and Toolbar");
        stage.setScene(scene);
        System.err.println("uuid-restore-key-" + UUID.randomUUID());
        //System.err.println("uuid-restore-data-" + UUID.randomUUID() );
        stage.setOnShown(e -> {
            //sideBar01.setHideOnExit(true);
//        sideBar01.setPrefSize(sideBar01.getToolBar().getWidth(), sideBar01.getToolBar().getHeight());
            //sideBar01.setMinSize(sideBar01.getToolBar().getMinWidth(), sideBar01.getToolBar().getMinHeight());        
            //System.err.println("sideBar01.getWidth()=" + sideBar01.getWidth());
//            System.err.println("sideBar01.toolBar.getWidth()=" + sideBar01.getToolBar().getWidth());
        });
        return stage;
    }

    public static class CustomButton extends Button {

        public CustomButton() {
        }

        public CustomButton(String text) {
            super(text);
        }

        public CustomButton(String text, Node graphic) {
            super(text, graphic);
        }

        @Override
        public ObservableList<Node> getChildren() {
            return super.getChildren();
        }
    }

    public static class TabNode extends Tab implements Dockable {

        private Label node = new Label();
        private DockableContext context;

        public TabNode() {
        }

        public TabNode(String text) {
            super(text);
        }

        public TabNode(String text, Node content) {
            super(text, content);
        }

        @Override
        public Node node() {
            node.setText(getText());
            return node;
        }

        @Override
        public DockableContext getContext() {
            if (context == null) {
                context = new DockableContext(this);
                node.setText(getText());
            }
            return context;
        }
    }
}
