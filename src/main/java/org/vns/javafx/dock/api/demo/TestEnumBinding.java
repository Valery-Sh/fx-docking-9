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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.EnumPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestEnumBinding extends Application {
    
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
        //VBox.setVgrow(btn1, Priority.ALWAYS);
        ObjectProperty<Priority> editorEnum = new SimpleObjectProperty<>();
        editorEnum.set(Priority.SOMETIMES);
/*        EnumBinding binding = new EnumBinding("vgrow", btn1, editorEnum, Priority.class);
        binding.bind();
*/
        VBox.setVgrow(btn1,Priority.SOMETIMES);
        
        EnumPropertyEditor editor = new EnumPropertyEditor("vgrow", Priority.class);
        editor.bindConstraint(btn1);
        root.getChildren().add(0,editor);
        btn1.setOnAction(e -> {
            System.err.println(" VBox.getVgrow(btn1) = " + VBox.getVgrow(btn1));
        });
        
        
        //EnumPropertyEditor editor = new EnumPropertyEditor(Priority.class);
        
        root.setPrefSize(500, 100);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();
        System.err.println("btn1.paddingProperty() = " + btn1.getPadding());
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
