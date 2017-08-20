/*
 * Copyright 2017 Your Organisation.
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

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockTabPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.save.DockStateLoader;

/**
 *
 * @author Valery
 */
public class TestDockPane1NEW extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");

        StackPane stackPane = new StackPane();
        HBox root = new HBox();
        root.getChildren().add(stackPane);
        
        //DockPane dockPane1 = new DockPane();

        //DockNode dnc1 = new DockNode("DockNodeControl dnc1");
        DockStateLoader loader = new DockStateLoader(TestDockPaneControl.class);
        //DockPane1 dockPane1 = (DockPane) loader.register("dockPane1", DockPane.class);
        DockPane dockPane1 = new DockPane();
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
        
        dnc1.setId("dnc1");
        dnc2.setId("dnc2");
        dnc3.setId("dnc3");
        dnc4.setId("dnc4");

        VPane vs1 = new VPane();
        vs1.setId("vs1");
        dockPane1.getItems().add(vs1);

        HPane hs1 = new HPane(dnc1, dnc2);
        hs1.setId("hs1");
        vs1.getItems().addAll(hs1, dnc3);
        //TreeItem<PreferencesItem> items = dockPane1.getTargetContext().getPreferencesBuilder().build(dockPane1);
//        String s = loader.toString(dockPane1);
//        System.err.print(s);
        
        DockTabPane dockTabPane1 = new DockTabPane();
        loader.register("dockTabPane1", dockTabPane1);
        Button tabButton1 = new Button("Tab Button1");
        DockNode tabDnc1 = new DockNode(" tan Dnc1");
        tabDnc1.setId("tabDnc1");
        DockNode tabDnc2 = new DockNode(" tab Dnc2");
        tabDnc2.setId("tabDnc2");
        
        dockTabPane1.dock(tabDnc1);
        dockTabPane1.dock(tabDnc2);
        Tab tab = new Tab("Not dock Tab",tabButton1);
        //dockTabPane1.getTabs().add(tab);
        root.getChildren().add(0,dockTabPane1);
        //loader.resetPreferences();
        //loader.load();
        //System.err.println(" TEST ===================================");
        //loader.resetPreferences();
        //loader.save(dockPane1);
        //System.err.println(loader.preferencesStringValue(dockPane1));
        //System.err.println("TEST ===================================");

        //vs1.getItems().add(hs1);
        //vs1.getItems().add(dnc3);
        Button b1 = new Button("reset");
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
            System.err.println("----------  hs1.sz=" + hs1.getItems().size());
            //loader.save(dockPane1);
            //loader.save(dockPane1);
            //loader.reload();
            //System.err.println(loader.toString(dockPane1));
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
        //System.err.println("dn1 isDocked()=" + dn1.getDockableContext().isDocked());
        root.getChildren().add(dockPane1);

        scene = new Scene(root, 250, 250);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
        System.err.println("SKIN = " + dockPane1.getSkin());
        System.err.println("SKIN.node = " + dockPane1.getSkin().getNode());
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
