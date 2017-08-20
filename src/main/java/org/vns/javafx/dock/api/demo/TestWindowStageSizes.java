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
import static javafx.application.Application.launch;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestWindowStageSizes extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        root.setId("ROOT");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            System.err.println("==============================================");
            System.err.println("Insets " + root.getInsets());
            System.err.println("----------------------------------------------");
            Bounds bnd = root.localToScreen(root.getBoundsInLocal());
            System.err.println("ev.getX()=" + e.getX() + "; ev.getY=" + e.getY());
            System.err.println("ev.getScreenX()=" + e.getScreenX() + "; ev.getScreenY=" + e.getScreenY());
            System.err.println("stage.X=" + stage.getX() + "; stageY=" + stage.getY());
            System.err.println("root.X=" + bnd.getMinX() + "; rootY=" + bnd.getMinY());

            System.err.println("Stage width = " + stage.getWidth() + "; height=" + stage.getHeight());
            System.err.println("root  width = " + root.getWidth() + "; height=" + root.getHeight());
            System.err.println("root bounds width = " + bnd.getWidth() + "; height=" + bnd.getHeight());
            //System.err.println("Scene width = " + scene.getWidth() + "; height=" + scene.getHeight());
            System.err.println("layoutBounds = " + root.getLayoutBounds());

        });
        stage.sizeToScene();
        //root.setStyle("-fx-padding: 10");

        //root.setStyle("-fx-background-color: green");
        //root.setStyle("-fx-background-color: green");
        //root.getStyleClass().clear();
        //root.setStyle("-fx-padding: 20; -fx-background-color: green");
        //root.setStyle("-fx-background-color: blue");
//        root.setStyle("-fx-background-color: green");
        //1234567890abcde;pbrkvyprstefx
        root.getStyleClass().add("my-test-css");
        Button b1 = new Button("Show Dimentions");
        Button b2 = new Button("b2r");
        //System.err.println("button maxheight = " + b1.getMaxWidth());
        //System.err.println("is button resizable = " + b1.isResizable());
        //b1.setMaxWidth(400);
        root.getChildren().add(b1);
        b1.setGraphic(b2);
        root.applyCss();
        Scene scene = new Scene(root);
        b1.setOnAction(a -> {
            Bounds wBtn = root.getBoundsInParent();
            double rightPos = wBtn.getWidth() - root.getInsets().getRight();
            Bounds b = root.getBoundsInParent();
            System.err.println("   --- cals SceneBounds RIGHT POS=" + rightPos);
//            System.err.println("MIN WIDTH = " + stage.getScene().minWidth(root.getMinHeight()));
/*            System.err.println("   --- root.getBoundsInParent()=" + b);
            b = root.getBoundsInLocal();
            System.err.println("   --- root.getBoundsInLocal()=" + b);
            b = root.localToScreen(root.getLayoutBounds());
            System.err.println("   --- root.getlayoutBoundas.toScreen()=" + b);
            b = root.localToScreen(root.getBoundsInLocal());
            System.err.println("   --- root.localToScreen=" + b);
            root.getStyleClass().forEach(s -> {
                System.err.println("styleClass = " + s);

            });
            System.err.println("EFFECT " + root.getEffect());
            if (root.getEffect() != null) {
                root.setEffect(null);
            }
            //System.err.println("EFFECT " + root.getEffect().);
            System.err.println("Padding " + root.getPadding());
            System.err.println("Insets " + root.getInsets());
            //System.err.println("Background " + root.getBackground().getOutsets());
            Bounds bnd = root.localToScreen(root.getBoundsInLocal());
            System.err.println("Stage width = " + stage.getWidth() + "; height=" + stage.getHeight());
            System.err.println("root  width = " + root.getWidth() + "; height=" + root.getHeight());
            System.err.println("root bounds width = " + bnd.getWidth() + "; height=" + bnd.getHeight());
            System.err.println("Scene width = " + scene.getWidth() + "; height=" + scene.getHeight());
            System.err.println("layoutBounds = " + root.getLayoutBounds());
*/
        });

///////////////////////////////////////////////////////


///////////////////////////////////////////////////////
        // 1. setStyle("-fx-padding: 10")
        //dockPane.dock(p1, Side.TOP).getDockableContext().setTitle("Pane p1");
        //scene.setFill(Color.rgb(0, 0, 0, 1d / 255d));
        scene.setFill(null);
        stage.setTitle("Stage Dimentions");

        stage.setScene(scene);

        stage.setOnShown(s -> {
            //((Pane)custom.getContent()).getChildren().forEach(n -> {System.err.println("custom node=" + n);});
            //System.err.println("tp.lookup(arrowRegion)" + tp.);
            //DockUtil.print(b1);
        });
        stage.setMinHeight(100);
        root.setMinHeight(100);
        stage.setMinWidth(400);
        root.setMinWidth(400);

        stage.show();

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

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }

    public Bounds getSceneBounds(Region node, Scene scene) {
        Bounds bnd = null;
        Bounds b = node.getBoundsInParent();

        double leftPos = b.getWidth() - node.getInsets().getRight();
        return bnd;
    }
}
