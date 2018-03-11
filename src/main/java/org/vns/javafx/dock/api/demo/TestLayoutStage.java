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

import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.dragging.view.StageNodeFraming;
import org.vns.javafx.dock.api.dragging.view.WindowNodeFraming;

/**
 *
 * @author Valery
 */
public class TestLayoutStage extends Application {

    WindowNodeFraming resizer = StageNodeFraming.getInstance();
    int counter = 0;

    Node last;

    Rectangle rect = new Rectangle(50, 20);

    VBox rightPane = new VBox();
    StackPane rightPaneRoot = new StackPane(rightPane);
    VBox leftPane = new VBox() {

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
        }
    };

    @Override
    public void start(Stage stage) throws Exception {

        System.err.println("RECT managed: = " + rect.isManaged());
        stage.setAlwaysOnTop(true);
        Button addButton = new Button("add new Node");
        Button infoButton = new Button("Show layout");
        VBox root = new VBox(addButton, infoButton);
        Scene rootScene = new Scene(root);
        stage.setScene(rootScene);

        Scene leftScene = new Scene(leftPane);

        Stage leftStage = new Stage();

        leftStage.setScene(leftScene);
        Stage rightStage = new Stage();
        //rightStage.setAlwaysOnTop(true);
        //rightStage.initOwner(stage);
        System.err.println("IS ALWAYS = " + rightStage.isAlwaysOnTop());
        Scene rightScene = new Scene(rightPaneRoot);

        rightPane.setStyle("-fx-background-color: white;");
        rightPaneRoot.setStyle("-fx-background-color: SIENNA; -fx-padding: 10 10 10 10");

        rightStage.setScene(rightScene);

        ObjectProperty<Bounds> oBounds = new SimpleObjectProperty();
        //oBounds.bind(last.boundsInParentProperty());
        BorderPane bp = new BorderPane();

        addButton.setOnAction(a -> {

            last = new Button("Button" + counter++);
            Button btn = (Button) last;
            if (counter == 1) {
                //btn.setManaged(false);
                //btn.setMinSize(70,35);
                rect.setStrokeType(StrokeType.OUTSIDE);
                rect.setStroke(Color.RED);
                rect.setStrokeWidth(2);
                rect.setX(20);
                rect.setY(50);
                rect.setManaged(false);
                rect.setFill(Color.TRANSPARENT);
                rightPaneRoot.getChildren().add(rect);
                
            }
            //btn.setStyle("-fx-border-width: 5; -fx-border-color: aqua; -fx-padding: 16 16 16 16");
            btn.setStyle("-fx-border-width: 5; -fx-border-color: aqua; ");
//            last.layoutBoundsProperty().addListener((o, ov, nv) -> {
            //System.err.println("last.layoutProperty newValue = " + nv);
//                System.err.println("   --- width = " + nv.getWidth());
//                System.err.println("   --- height = " + nv.getHeight());
//            });
//            last().localToSceneTransformProperty().addListener((o, ov, nv) -> {

//                System.err.println("   --- x = "   + nv.getTx());
/*                System.err.println("   --- Mxx = " + nv.getMxx());                
                System.err.println("   --- Mxy = " + nv.getMxy());                
                System.err.println("   --- Myy = " + nv.getMyy());                
                System.err.println("   --- Myz() = " + nv.getMyz());                
                
                System.err.println("   --- Mzx = " + nv.getMzx());                                
                System.err.println("   --- Mzy = " + nv.getMzy());                                                
                System.err.println("   --- Mzz = " + nv.getMzz());   
                System.err.println("   --- Tx = " + nv.getTx());   
                System.err.println("   --- Ty = " + nv.getTy());                   
             */
//                System.err.println("   --- y = " + nv.getTy());
            //System.err.println("last.localToSceneTransformProperty newValue = " + nv);
//            });
            rightPane.getChildren().add(last);

            //bp.setBottom(last);
        });

        /*        leftPane.addEventHandler(MouseEvent.MOUSE_RELEASED, a -> {
            //last = new Button("Button" + counter++);
            //oBounds.bind(last.boundsInParentProperty());
            leftPopup.show(leftStage, 200, 100);
            leftPopup.setX(100);
            leftPopup.setX(300);
            //rightPane.getChildren().add(last);
        });
         */
        infoButton.setOnAction(a -> {
            System.err.println("=== " + last + " ====================================");
            System.err.println("oBounds      = " + oBounds.get());
            System.err.println("localBounds  = " + last.getLayoutBounds());
            System.err.println("parentBounds = " + last.localToParent(last.getLayoutBounds()));
            System.err.println("--------------------------------------------------");
        });

        rightPane.getChildren().addListener(this::rightPaneChanged);
        leftPane.getChildren().addListener(this::leftPaneChanged);
        leftStage.setX(200);
        leftStage.setY(200);
        leftStage.setWidth(200);
        leftStage.setHeight(200);

        stage.setX(leftStage.getX() + leftStage.getWidth() + 20);
        stage.setY(200);
        stage.setWidth(100);
        leftStage.setHeight(200);

        rightStage.setX(stage.getX() + stage.getWidth() + 50);
        rightStage.setY(200);
        rightStage.setWidth(200);
        rightStage.setHeight(200);

        leftStage.show();
        stage.show();
        rightStage.show();

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
//        System.out.println("Scene MOUSE PRESSED handle ");
    }

    public void rightPaneChanged(ListChangeListener.Change change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List list = change.getRemoved();
                if (!list.isEmpty()) {
                }
                for (Object elem : list) {

                }

            }
            if (change.wasAdded()) {
                List<Node> list = change.getAddedSubList();
                //System.err.println("rightPaneChanged" );
                //System.err.println("rightPaneChanged: lastParentBounds = " + last().localToParent(last().getLayoutBounds()));
                System.err.println("rightPaneChanged: last() = " + last);
                System.err.println("   -- get(0) = " + list.get(0));
                Label lb = new Label("label" + (counter - 1));
                leftPane.getChildren().add(lb);
                if (resizer != null && resizer.isShowing()) {
                    resizer.hide();
                }
                resizer.show(last);

                //System.err.println("lastParentBounds = " + list.get(0).localToParent(list.get(0).getLayoutBounds()));
                //System.err.println("lastParentBounds = " + last.localToParent(last.getLayoutBounds()));
                Platform.runLater(() -> {
                    //System.err.println("rightPaneChanged: RunLater" );
                    //System.err.println("rightPaneChanged: RunLater: lastParentBounds = " + last().localToParent(last().getLayoutBounds()));
                    //System.err.println("   --- Label Bounds = " + lb.localToParent(lb.getLayoutBounds()));

                });
            }
        }//while
    }

    public void leftPaneChanged(ListChangeListener.Change change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List list = change.getRemoved();
                if (!list.isEmpty()) {
                }
                for (Object elem : list) {

                }

            }
            if (change.wasAdded()) {
                //System.err.println("leftPaneChanged" );
                // System.err.println("leftBoundsChanged: lastParentBounds = " + last.localToParent(last.getLayoutBounds()));
                Platform.runLater(() -> {
//                    System.err.println("leftPaneChanged: RunLater" );
//                    System.err.println("leftBoundsChanged: RunLater lastParentBounds = " + last.localToParent(last.getLayoutBounds()));
                });
            }
        }//while
    }

}
