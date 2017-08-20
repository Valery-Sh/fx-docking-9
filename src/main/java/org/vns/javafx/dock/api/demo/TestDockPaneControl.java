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
import org.vns.javafx.dock.api.DockStateLoader;

/**
 *
 * @author Valery
 */
public class TestDockPaneControl extends Application {

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
        DockStateLoader loader = new DockStateLoader(TestDockPaneControl.class);
        
        //loader.setSaveOnClose(true);
        DockPane dockPane2 = new DockPane();
        DockPane dockPane1 = (DockPane) loader.register("dockPane1", DockPane.class);
        loader.register("dockPane2", dockPane2);
        
        dockPane2.setId("dockPane2");
        DockNode dnc1_1 = new DockNode();
        DockNode dnc2_1 = new DockNode();
        dnc1_1.setId("dnc1_1");        
        dnc2_1.setId("dnc2_1");
        Button b1_1 = new Button("b1_1");
        Button b1_2 = new Button("b1_2");
        dnc1_1.setContent(b1_1);
        dnc2_1.setContent(b1_2);
        VPane vp1_1 = new VPane();
        vp1_1.setId("vp1_1");
        dockPane2.getItems().add(vp1_1);
        vp1_1.getItems().addAll(dnc1_1,dnc2_1);
        
        //DockPane dockPane1 = new DockPane();

        //DockPane1 dockPane1 = (DockPane) loader.register("dockPane1", DockPane.class);        
        dockPane1.setId("dockPane1");
        //DockNode dnc1 = (DockNode) loader.register("dnc1", DockNode.class);
        //DockNode dnc2 = (DockNode) loader.register("dnc2", DockNode.class);
        //DockNode dnc3 = (DockNode) loader.register("dnc3", DockNode.class);
        DockNode dnc1 = new DockNode();
        DockNode dnc2 = new DockNode();
        DockNode dnc3 = new DockNode();
        //loader.resetPreferences();
        //DockNode dnc2 = new DockNode("DockNodeControl dnc2");
        dnc3.setTitle("DockNodeControl dnc3");

        DockNode dnc4 = new DockNode("DockNodeControl dnc4");
        loader.register("dnc4", dnc4);
        loader.register("dnc2", dnc2);

        dnc1.setId("dnc1");
        dnc2.setId("dnc2");
        dnc3.setId("dnc3");
        dnc4.setId("dnc4");

        VPane vs1 = new VPane();
        vs1.setId("vs1");
        dockPane1.getItems().add(vs1);
        HPane hs1 = new HPane(dnc1, dnc2);
        hs1.setId("hs1");
        vs1.getItems().addAll(dockPane2,hs1, dnc3);
        //vs1.getItems().addAll(hs1, dnc3);
        //vs1.getItems().addAll(hs1);
        //dockPane1.getRoot().getItems().addAll(dnc1, dnc2,dnc3);
        
        
        //TreeItem<PreferencesItem> items = dockPane1.getTargetContext().getPreferencesBuilder().build(dockPane1);
//        String s = loader.toString(dockPane1);
//        System.err.print(s);

        DockTabPane dockTabPane1 = new DockTabPane();
        loader.register("dockTabPane1", dockTabPane1);
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
        //loader.resetPreferences();
/////// LOAD /////////////        
        loader.load();
        
        //TreeItem ti = dockPane1.getTargetContext().getPreferencesBuilder().build("dockPane1");
        //TreeItemStringConverter tc = new TreeItemStringConverter();
        //System.err.println("TC: ");
        //System.err.println(tc.toString(ti));

        //System.err.println(" TEST ===================================");
        //loader.resetPreferences();
        //loader.save(dockPane1);
        //System.err.println(loader.preferencesStringValue(dockPane1));
        //System.err.println("TEST ===================================");
        //vs1.getItems().add(hs1);
        //vs1.getItems().add(dnc3);
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

        b1.setOnAction(a -> {
            System.err.println("----------  hs1.sz=" + hs1.getItems().size());
            loader.save();
            //loader.save(dockPane1);
            //loader.reload();
            //System.err.println(loader.toString(dockPane1));
        });
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
        b4.setOnAction(a -> {
            /*          if ( dnc1.getDividerPos() > 0.6 )  {
              dnc1.setDividerPos(0.346);
          } else {
              dnc1.setDividerPos(0.48);
          }
             */
        });

        b5.setOnAction(a -> {
            loader.reset();
        });
        b6.setOnAction(a -> {
            System.err.println("================================");
            Node n = dockPane1.getItems().get(0);
            System.err.println(n);
            //System.err.println(loader.toString(dockPane1));
            System.err.println("================================");
            System.err.println("dockPane2 dnc3 " + ((SplitPane)dockPane2.getItems().get(0)).getItems().size());
            System.err.println("================================");
        });

        //cc.setRoot(vs1);
        //System.err.println("dn1 isDocked()=" + dn1.getDockableContext().isDocked());
        root.getChildren().add(dockPane1);

        scene = new Scene(root, 550, 550);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
        //Node p = vs1.getParent();
        //String id = p.getId();
        //String s = p.getClass().getName();
        List<Node> l = dockPane1.getItems();
        Node n1 = l.get(0);
        //Node n2 = l.get(1);
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
}
