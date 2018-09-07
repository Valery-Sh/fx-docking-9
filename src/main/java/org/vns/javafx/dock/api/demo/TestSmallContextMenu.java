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

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.SmallContextMenu;

/**
 *
 * @author Valery
 */
public class TestSmallContextMenu extends Application {

    @Override
    public void start(Stage stage) throws ClassNotFoundException {

        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");
        TextField textField = new TextField();
        VBox vb = new VBox(btn1, btn2, textField);
        String[] itemLabels = new String[500];
        MenuItem[] its = new MenuItem[500];
        for (int i = 0; i < itemLabels.length; i++) {
            its[i] = new MenuItem("Menu Item " + i);
        }

        SmallContextMenu menu = new SmallContextMenu(its);
        btn1.setOnAction(a -> {
            if ( menu.getContentHeight() == 300 ) {
                menu.setContentHeight(1000d);
            } else {
                menu.setContentHeight(200d);
            }
        });
        
        menu.setContentHeight(300d);
        menu.getStyleClass().add("vns-style");
        textField.setContextMenu(menu);

        Scene scene = new Scene(vb);
        Node n;
//        System.err.println("Class = " + menu.getSkin().getNode().getClass().getName());
        stage.setScene(scene);
        stage.show();
        System.err.println("Class = " + menu.getScene().getRoot().getClass().getName());
/*        menu.setOnShowing(e -> {
            Parent root = menu.getScene().getRoot();
            for (Node node : root.getChildrenUnmodifiable()) {
                System.err.println("child = " + node.getClass().getName());
                Parent p = (Parent) node;
                for (Node node1 : p.getChildrenUnmodifiable()) {
                    System.err.println("   --- child = " + node1.getClass().getName());
                }
            }
            Set<Node> nodes = root.lookupAll(".vns-style");
            nodes.forEach(node -> {
                System.err.println("node = " + node);
                if (node instanceof ContextMenuContent) {
                    System.err.println("YES");
                    ((Region)node).setMaxHeight(200);
                }
            });

        });
*/
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

}
