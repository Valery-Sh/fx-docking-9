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

import java.lang.reflect.TypeVariable;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.EnumPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.InsetsPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.SliderPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.model.ModelProperty;
import org.vns.javafx.dock.api.designer.bean.model.PropertyPaneModel;
import org.vns.javafx.dock.api.designer.bean.model.PropertyPaneModelProvider;

/**
 *
 * @author Valery
 */
public class TestPropertyPaneModelProvider extends Application {

    Stage stage;
    Scene scene;
    Button saveBtn;

    enum Foo {
        BLAT,
        BLARG
    };

    @Override
    public void start(Stage stage) throws ClassNotFoundException {
        ObservableList<String> olist = FXCollections.observableArrayList();
        
        TypeVariable[] tv = olist.getClass().getTypeParameters();
        if (tv.length != 0) {
            //System.err.println("pd.getClass=" + pd.getPropertyType().getSimpleName() + "; pd.getName=" + pd.getName() + "; typeVar = " + tv[0]);
            
            System.err.println("  --- gendecl = " + tv[0]);
        }

        System.err.println(Class.forName(Foo.class.getName()).getName());
        System.err.println(Enum.valueOf((Class<? extends Enum>) Class.forName(Foo.class.getName()), "BLARG"));
        long tstart = System.currentTimeMillis();
        PropertyPaneModel model = PropertyPaneModelProvider.getInstance().getModel(Node.class);
        ObservableList<ModelProperty> props = model.getModelProperties();
        Node n;

        for (int i = 0; i < props.size(); i++) {
            System.err.println(i + "). " + props.get(i).getName() + "; editor = " + props.get(i).getEditorClass());
        }
        long tend = System.currentTimeMillis();
        System.err.println("TIME = " + (tend - tstart));
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
