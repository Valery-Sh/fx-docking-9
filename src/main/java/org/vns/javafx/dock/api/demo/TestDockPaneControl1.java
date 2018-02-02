package org.vns.javafx.dock.api.demo;

import java.util.List;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockTabPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.save.DockStateLoader;

/**
 *
 * @author Valery
 */
public class TestDockPaneControl1 extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");

        StackPane stackPane = new StackPane();
        HBox root = new HBox();
        root.getChildren().add(stackPane);
        //StackPane root = new StackPane();
        //DockPane dockPane1 = new DockPane();
        //DockNode dnc1 = new DockNode("DockNodeControl dnc1");
        //DockStateLoader loader = new DockStateLoader(TestDockPaneControl1.class);
        
        //loader.setSaveOnClose(true);
        DockPane dockPane2 = new DockPane();
        //DockPane dockPane1 = (DockPane) loader.register("dockPane1", DockPane.class);
        //loader.register("dockPane2", dockPane2);
        
        dockPane2.setId("dockPane2");
        DockNode dnc1_1 = new DockNode();
        DockNode dnc2_1 = new DockNode();
        DockNode dnc3_1 = new DockNode();
        
        dnc1_1.setId("dnc1_1");        
        dnc2_1.setId("dnc2_1");
        dnc3_1.setId("dnc3_1");
        Button b1_1 = new Button("b1_1");
        Button b1_2 = new Button("b1_2");
        Button b1_3 = new Button("b1_3");
        dnc1_1.setContent(b1_1);
        dnc2_1.setContent(b1_2);
        dnc3_1.setContent(b1_3);
        
        VPane vp1_1 = new VPane();
        vp1_1.setId("vp1_1");
        
        dockPane2.getItems().add(vp1_1);
        vp1_1.getItems().addAll(dnc1_1,dnc2_1, dnc3_1);
        stackPane.getChildren().add(dockPane2);
        
        //DockPane dockPane1 = new DockPane();


        DockNode dnc1 = new DockNode();
        DockNode dnc2 = new DockNode();
        DockNode dnc3 = new DockNode();
        dnc3.setTitle("DockNodeControl dnc3");

        DockNode dnc4 = new DockNode("DockNodeControl dnc4");

        dnc1.setId("dnc1");
        dnc2.setId("dnc2");
        dnc3.setId("dnc3");
        dnc4.setId("dnc4");

        VPane vs1 = new VPane();
        vs1.setId("vs1");
        HPane hs1 = new HPane(dnc1, dnc2);
        hs1.setId("hs1");
       // vs1.getItems().addAll(dockPane2,hs1, dnc3);

/*        DockTabPane dockTabPane1 = new DockTabPane();
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

        dockTabPane1.dock(tabDnc1);
        dockTabPane1.dock(tabDnc2);
        dockTabPane1.dock(tabDnc3);
        Tab tab = new Tab("Not dock Tab", tabButton1);

        System.err.println("TAB: " + System.identityHashCode(tab));
        dockTabPane1.getTabs().add(tab);
        root.getChildren().add(0,dockTabPane1);

        DockableContext dc = DockRegistry.dockable(dnc3).getDockableContext();
        TargetContext dtc = dc.getTargetContext();
        Button b1 = new Button("save");
        Button b2 = new Button("add dnc4");
        Button b3 = new Button("remove dnc4");
        Button b4 = new Button("change dnc1 DividerPos");
        Button b5 = new Button("reset");
        Button b6 = new Button("print");

        VBox content = new VBox(b1, b2, b3, b4, b5, b6);
        dnc1.setContent(content);
        Label contentLabel = new Label("CONTENT LABEL");
        dnc2.setContent(contentLabel);
        Label childLabel = new Label("dnc3 label");
        ((StackPane) dnc3.getContent()).getChildren().add(childLabel);

        b2.setOnAction(a -> {
            System.err.println("before (b2)hs1.sz=" + hs1.getItems().size());
            hs1.getItems().add(dnc4);
            System.err.println("after (b2)hs1.sz=" + hs1.getItems().size());

        });
        b3.setOnAction(a -> {
            System.err.println("before (b3)hs1.sz=" + hs1.getItems().size());
            hs1.getItems().remove(dnc4);
            System.err.println("after (b3)hs1.sz=" + hs1.getItems().size());
        });

*/
        scene = new Scene(root, 550, 550);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
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
        public DockableContext getDockableContext() {
            if ( context == null ) {
                context = new DockableContext(this);
                node.setText(getText());
            }
            return context;
        }
    }
}
