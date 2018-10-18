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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.PropertyPaneModelRegistry;
import org.vns.javafx.dock.api.designer.bean.editor.InsetsPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.MarginBinding;

/**
 *
 * @author Valery
 */
public class TestInsetsPropertyEditor extends Application {

    public enum BoundsValue {
        minX, minY, minZ,
        maxX, maxY, maxZ,
        width, height, depth
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        PropertyPaneModelRegistry r = PropertyPaneModelRegistry.getInstance();
        r.introspect(VBox.class);
        
        Button btn1 = new Button("Btn1");
        Button btn2 = new Button("Btn2");
        
        
        InsetsPropertyEditor insetsEditor = new InsetsPropertyEditor();
        
        insetsEditor.setPrefWidth(350);
       
        root.getChildren().add(insetsEditor);
        ComboBox cb = new ComboBox();
        cb.setStyle("-fx-border-color: red; -fx-border-width: 1");
        //boundsEditor.bind(cb.layoutBoundsProperty());
       // ObjectProperty<Bounds> bp = new SimpleObjectProperty<>();
        System.err.println("isSupported(VBox) = " + MarginBinding.isSupported(VBox.class));
        insetsEditor.bindConstraint(btn1);
        //boundsEditor.setEditable(true);
        root.getChildren().add(btn1);
        btn1.setOnAction(e -> {
            System.err.println("margin = " + VBox.getMargin(btn1));
/*            System.err.println("cb.pw = " + cb.getLayoutBounds().getMaxX() + "; cb.ph = " + cb.getPrefHeight());
            Insets ins = new Insets(1, 2, 3, 4);
            insetsEditor.setEditorInsets(ins);
*/            
            //btn1.setPadding(ins);
            
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
        System.err.println("MAFGIN " + Insets.EMPTY);
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
