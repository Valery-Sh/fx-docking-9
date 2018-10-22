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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.BoundsPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestBoundsPropertyEditor extends Application {

    public enum BoundsValue {
        minX, minY, minZ,
        maxX, maxY, maxZ,
        width, height, depth
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();

        Button btn1 = new Button("Btn1");
        Button btn2 = new Button("Btn2");
        
        root.getChildren().add(btn1);
        BoundsPropertyEditor boundsEditor = new BoundsPropertyEditor();
        
        boundsEditor.setPrefWidth(350);
       
        root.getChildren().add(boundsEditor);
        ComboBox cb = new ComboBox();
        cb.setStyle("-fx-border-color: red; -fx-border-width: 1");
        //boundsEditor.bind(cb.layoutBoundsProperty());
        ObjectProperty<Bounds> bp = new SimpleObjectProperty<>();
        boundsEditor.bindBidirectional(bp);
        //boundsEditor.setEditable(true);
        btn1.setOnAction(e -> {
            System.err.println("cb.pw = " + cb.getLayoutBounds().getMaxX() + "; cb.ph = " + cb.getPrefHeight());
            Bounds b = new BoundingBox(1, 2, 3, 4);
            if ( bp.get() != null ) {
                b = new BoundingBox(bp.get().getMinX()+ 10, bp.get().getMinY() + 5, bp.get().getWidth()+ 2, bp.get().getHeight()+ 1) ;
            }
            bp.set(b);
            //cb.setPrefSize(cb.getPrefWidth() + 10, cb.getPrefHeight() + 2);
        });
        
        root.getChildren().add(cb);
        
        root.setPrefSize(500, 100);
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


}
