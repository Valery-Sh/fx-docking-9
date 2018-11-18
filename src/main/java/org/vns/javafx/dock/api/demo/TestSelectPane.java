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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

import org.vns.javafx.dock.api.dragging.view.NodeFraming;
import org.vns.javafx.dock.api.dragging.view.StageNodeFraming;
import org.vns.javafx.dock.api.dragging.view.WindowNodeFraming;

/**
 *
 * @author Valery
 */
public class TestSelectPane extends Application {
    
    
    int counter = 0;
    int click = 0;
    Node last;
    Node lastLabel;
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
        System.err.println(java.util.UUID.randomUUID());
        DockRegistry.getInstance().getLookup().putUnique(WindowNodeFraming.class, StageNodeFraming.getInstance());
        stage.setAlwaysOnTop(true);

        Circle rect = new Circle(5);
        rect.getStyleClass().add("circle");
        //rect.setFill(Color.AQUA);

        Button createLabelButton = new Button("create Label");
        Button addLabelButton = new Button("add Label");
        Button applyCssButton = new Button("applyCss");        
        
        Button nullShapeClassButton = new Button("Null Shape Class");
        Button configShapeClassButton = new Button("Config Side Shapes");

        Button addButton = new Button("add new Node");
        Button doTransformButton = new Button("Show layout");
        Button rectButton = new Button("Test KeyStroke");
        Button infoButton = new Button("print info");

        Button addSelectPaneButton = new Button("add SelectPane");

        VBox root = new VBox(createLabelButton, addLabelButton, addSelectPaneButton, applyCssButton, configShapeClassButton, nullShapeClassButton,addButton, doTransformButton, rectButton, infoButton);

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
        //rightPaneRoot.setStyle("-fx-background-color: SIENNA; -fx-padding: 10 10 10 10");
        rightPaneRoot.setStyle("-fx-background-color: aqua; -fx-padding: 30 30 30 30");

        rightStage.setScene(rightScene);

        ObjectProperty<Bounds> oBounds = new SimpleObjectProperty();
        //oBounds.show(last.boundsInParentProperty());
        BorderPane bp = new BorderPane();
        createLabelButton.setOnAction(a -> {
            if ( lastLabel == null) {
                lastLabel = new Label("Label to Bind");
            } else {
                lastLabel = new ComboBox();
            }

        });
/*        addLabelButton.setOnAction(a -> {
            //rightPane.getChildren().add(lastLabel);
            if (selPane == null) {
                selPane = new SelectPane();
                leftPane.getChildren().add(selPane);
                //selPane.setPrefWidth(-1);
                //selPane.setMinHeight(80);
                //selPane.setLayoutX(-1);
                //selPane.setLayoutY(-1);
                // selPane.setBoundNode(last);
                rightPane.getChildren().add(lastLabel);
                selPane.show(lastLabel);
                selPane.setDefaultStyles();                
                selPane.getIndicator().getStyleClass().add("indicator");
                SideCircles sc = new SideCircles();
                selPane.setSideShapes(sc);

                sc.setRadius(1.5);
                sc.setDefaultStyles();

                
            } else {
                leftPane.getChildren().add(lastLabel);
            }
        });
*/        
        addLabelButton.setOnAction(a -> {
            
            rightPane.getChildren().add(lastLabel);
        });
        
        
        applyCssButton.setOnAction(a -> {
        });        
        
        nullShapeClassButton.setOnAction(a -> {
/*            if ( selPane.getSideShapes() != null ) {
                selPane.setSideShapes(null);
            } else {
                selPane.setSideShapes(new SideCircles());
                selPane.getSideShapes().bind(selPane);
                
            }
*/            
        });
        
        configShapeClassButton.setOnAction(a -> {
/*            CircleConfig  cfg = (CircleConfig) selPane.getSideShapes().getConfig();
            double radius = cfg.getShape().getRadius();
            
            if ( radius < 3  ) {
                cfg.getShape().setRadius(radius + 1);
            } else if (radius == 1)  {
                cfg.getShape().setRadius(2);
            } else {
                cfg.getShape().setRadius(radius - 1);
            }
            cfg.apply();
            
            selPane.getSideShapes().applyCss();
  */          
        });        
        
        addButton.setOnAction(a -> {

            last = new Button("Button" + counter++);
            Button btn = (Button) last;
            //btn.arm();
            btn.setStyle("-fx-border-width: 5;-fx-border-color: aqua; ");
            btn.setFocusTraversable(false);
            btn.setTranslateX(12);
            btn.setTranslateY(12);
            //last = new CheckBox("Button" + counter++);
            //last.setFocusTraversable(false);
            //Button btn = (Button) last;
            //btn.arm();
            //btn.setStyle("-fx-border-color: aqua; ");
            //btn.setFocusTraversable(false);

            //btn.setStyle("-fx-background-insets: 0 0 0 0, 0, 1, 2;");
            //btn.setStyle("-fx-border-width: 10; -fx-border-color: aqua; ");
            rightPane.getChildren().add(last);

        });

        addSelectPaneButton.setOnAction(e -> {
/*            if (selPane == null) {
                selPane = new SelectPane();
                leftPane.getChildren().add(selPane);
                //selPane.setPrefWidth(-1);
                //selPane.setMinHeight(80);
                //selPane.setLayoutX(-1);
                //selPane.setLayoutY(-1);
                // selPane.setBoundNode(last);

            }
            selPane.bind(last);
            selPane.setVisible(true);
*/
            //selPane.setMinWidth(80);
        });

        rectButton.setOnAction(a -> {
            System.err.println("rect StrokeType = " + rect.getStrokeType() + "; getStroke()=" + rect.getStroke());
            //rect.setFill(Color.BLUE);
            if (rect.getStroke() == null) {
                rect.setStroke(Color.BLUE);
                rect.setStrokeWidth(5);
                rect.setStrokeType(StrokeType.OUTSIDE);
            } else {
                rect.setStroke(null);

            }
        });
        infoButton.setOnAction(a -> {

            System.err.println("rect bounds = " + rect.getLayoutBounds());
//            System.err.println("   --- rect width = " + rect.getWidth());
//            System.err.println("   --- rect height = " + rect.getHeight());
        });

        doTransformButton.setOnAction(a -> {

            //rightPane.getChildren().add(0, new Label("VALERY"));
/*            ((Button) last).getInsets();
            System.err.println("LAYOUT Y = " + last.getLayoutY());
            System.err.println("   -- last layoutBounds  = " + last.getLayoutBounds());
            System.err.println("   -- last Insets  = " + ((Button) last).getInsets());
             */
            last.setTranslateY(last.getLayoutY() + 10 * (++click));
            last.setScaleX(2);
            /*            System.err.println("=== " + last + " ====================================");
            System.err.println("oBounds      = " + oBounds.get());
            System.err.println("localBounds  = " + last.getLayoutBounds());
            System.err.println("parentBounds = " + last.localToParent(last.getLayoutBounds()));
            System.err.println("last.height = " + ((Region) last).getHeight());
            System.err.println("--------------------------------------------------");

            Insets insetsDelta = ((Region) last).getInsets();
            double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
            double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
            Bounds sceneBounds = last.localToScene(last.getLayoutBounds());
            System.err.println("node width  = " + sceneBounds.getWidth());
            System.err.println("node height = " + sceneBounds.getHeight());
            System.err.println("   --- insetsWidth  = " + insetsWidth);
            System.err.println("   --- insetsHeight = " + insetsHeight);
             */
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
                if (counter != 1) {
                    //if (resizer != null && resizer.isShowing()) {
                    //    resizer.hide();
                }
                //last.setTranslateY(60);
                if ( list.get(0) == last ) {
                    //resizer.show(last);
                } else if ( list.get(0) == lastLabel ) {
                    //resizer.show(lastLabel);
                }

                //last.setTranslateY(60);
                //Platform.runLater(() -> {

//                System.err.println("getTransforms = " + last.getTransforms());
                //});

                //resizer.getIndicator().setVisible(false);
                //last.setOpacity(0.5);
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
