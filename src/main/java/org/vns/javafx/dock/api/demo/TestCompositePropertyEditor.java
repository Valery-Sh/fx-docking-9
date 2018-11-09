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
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.scene.control.editors.EffectPropertyHelper;
import org.vns.javafx.scene.control.editors.TreePane;
import org.vns.javafx.scene.control.editors.TreePaneItem;

/**
 *
 * @author Valery
 */
public class TestCompositePropertyEditor extends Application {
    private Pane colorBar;
    @Override
    public void start(Stage stage) throws ClassNotFoundException {

        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");
        btn1.setStyle("-fx-font-weight: bold; -fx-font-size: 24");
        btn2.setStyle("-fx-font-weight: bold; -fx-font-size: 24");
        Button btn3 = new Button("ExpandTitledPane");
        TextField tf;
        
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.getStylesheets().add( getClass().getResource("resources/color.css").toExternalForm());
        
        //colorPicker.setStyle("-fx-color-label-visible: false");
        colorPicker.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
        TreePane effectTreePane = EffectPropertyHelper.createTreePane("effect");
        TreePaneItem treeItemEditor = EffectPropertyHelper.createTreePaneItem("effect1");
        TreePaneItem treeItemEditor2 = EffectPropertyHelper.createTreePaneItem("effect2");
        effectTreePane.getChildItems().add(treeItemEditor);
        effectTreePane.getChildItems().add(treeItemEditor2);
        
        treeItemEditor.bindBidirectional(btn1.effectProperty());
        effectTreePane.bindBidirectional(btn2.effectProperty());
        colorBar = new Pane();
        colorBar.setPrefSize(100, 30);
        colorBar.setBackground(new Background(new BackgroundFill(createHueGradient(),
                CornerRadii.EMPTY, Insets.EMPTY)));        
        VBox vbox = new VBox(colorBar,colorPicker,btn1, btn2,  btn3, effectTreePane );

        StackPane root = new StackPane(vbox);
        btn1.setOnAction(e -> {
            //treeItemEditor.getTextButton().setText("Bloom");
            System.err.println("btn1 textFill = " + btn1.getTextFill());
            //btn2.setTextFill(Color.valueOf("#333333ff"));
            btn1.setTextFill(Color.BLACK);
            System.err.println("Color.BLACK = " + Color.BLACK);
        });
        btn2.setOnAction(e -> {
            ColorPicker colorPicker1 = new ColorPicker();
            
        });
        btn3.setOnAction(e -> {
        });
        root.setPrefSize(500, 200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
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
   private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int x = 0; x < 255; x++) {
            offset = (double) ((1.0 / 255) * x);
            int h = (int) ((x / 255.0) * 360);
            stops[x] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 0f, 1f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }
    public static class TitledPaneEx extends TitledPane {
//        StackPane p = (StackPane) titledPane.lookup(".title");
        //p.setManaged(false);
        //p.setMaxHeight(0);
        //p.setMinHeight(0);
//        p.setPrefHeight(0);
//        p.getChildren().clear();

        public TitledPaneEx() {
        }

        public TitledPaneEx(String title, Node content) {
            super(title, content);
            this.setBorder(Border.EMPTY);
            this.setLineSpacing(0);
            //this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        }

        @Override
        public ObservableList<Node> getChildren() {
            return super.getChildren();
        }
        private boolean layoutDone = false;

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            StackPane p = (StackPane) lookup(".title");
            if (p != null && !layoutDone) {
                System.err.println("LAYOUT");
                p.setPrefHeight(0);
                p.getChildren().clear();
                layoutDone = true;
            }
        }
    }
}
