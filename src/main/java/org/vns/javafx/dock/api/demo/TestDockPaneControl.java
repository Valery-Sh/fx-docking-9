package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.api.DockLoader;

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

        StackPane root = new StackPane();
        //DockPane cc = new DockPane();

        //DockNode dnc1 = new DockNode("DockNodeControl dnc1");
        DockLoader loader = DockLoader.create(TestDockPaneControl.class);
        DockPane cc = (DockPane) loader.registerDockTarget("dockPane1", DockPane.class);
        DockNode dnc1 = (DockNode) loader.registerDockable("dnc1", DockNode.class);
        DockNode dnc2 = (DockNode) loader.registerDockable("dnc2", DockNode.class);
        DockNode dnc3 = (DockNode) loader.registerDockable("dnc3", DockNode.class);

        loader.saveStore();
        //DockNode dnc2 = new DockNode("DockNodeControl dnc2");
        dnc3.setTitle("DockNodeControl dnc3");
        loader.load();

        DockNode dnc4 = new DockNode("DockNodeControl dnc4");

        dnc1.setId("dnc1");
        dnc2.setId("dnc2");
        dnc3.setId("dnc3");
        dnc4.setId("dnc4");

        VPane vs1 = new VPane();
        vs1.setId("vs1");
        cc.getItems().add(vs1);

        HPane hs1 = new HPane(dnc1, dnc2);
        hs1.setId("hs1");
        vs1.getItems().addAll(hs1, dnc3);
        //TreeItem<PreferencesItem> items = cc.targetController().getPreferencesBuilder().build(cc);
        String s = loader.toString(cc);
        System.err.print(s);
        System.err.println("===================================");
        loader.reset();
        loader.save(cc);
        System.err.println(loader.namespaceStringValue(cc));
        System.err.println("===================================");

        //vs1.getItems().add(hs1);
        //vs1.getItems().add(dnc3);
        Button b1 = new Button("Items Count");
        Button b2 = new Button("add dnc4");
        Button b3 = new Button("remove dnc4");
        Button b4 = new Button("change dnc1 DividerPos");
        Button b5 = new Button("print ");

        VBox content = new VBox(b1, b2, b3, b4, b5);
        dnc1.setContent(content);
        Label contentLabel = new Label("CONTENT LABEL");
        dnc2.setContent(contentLabel);
        Label childLabel = new Label("dnc3 label");
        ((StackPane) dnc3.getContent()).getChildren().add(childLabel);

        b1.setOnAction(a -> {
            System.err.println("hs1.sz=" + hs1.getItems().size());
        });
        b2.setOnAction(a -> {
            System.err.println("(b2)hs1.sz=" + hs1.getItems().size());
            hs1.getItems().add(dnc4);
        });
        b3.setOnAction(a -> {
            System.err.println("(b3)hs1.sz=" + hs1.getItems().size());
            hs1.getItems().remove(dnc4);
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
            System.err.println("dnc2 content " + dnc2.getContent());
            System.err.println("dnc2 content.size= " + ((Label) dnc2.getContent()).getHeight());
            System.err.println("dnc2 control.size= " + dnc2.getHeight());
            System.err.println("dnc3 size= " + dnc3.getHeight());

        });

        //cc.setRoot(vs1);
        //System.err.println("dn1 isDocked()=" + dn1.dockableController().isDocked());
        root.getChildren().add(cc);

        scene = new Scene(root, 250, 250);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
        System.err.println("SKIN = " + cc.getSkin());
        System.err.println("SKIN.node = " + cc.getSkin().getNode());
        // SplitDelegate.DockSplitPane dsp = SplitDelegate.DockSplitPane.getParentSplitPane(dn3);

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
