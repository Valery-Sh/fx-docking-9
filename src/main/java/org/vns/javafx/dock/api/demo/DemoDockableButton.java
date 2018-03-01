package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableContext;

/**
 *
 * @author Valery
 */
public class DemoDockableButton extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        MyClass mc = new MyClass();
        mc.print();
        
        stage.setTitle("PRIMARY");
        VBox root = new VBox();
        scene = new Scene(root, 200, 200);
        Button dockButton = new Button("To be docked 1");
        
        Dockable dockableButton = DockRegistry.getInstance().getDefaultDockable(dockButton);
        dockableButton.getContext().setDragNode(dockButton);
        
        Button dockButton1 = new Button("To be docked 2");        
        Dockable dockableButton1 = DockRegistry.getInstance().getDefaultDockable(dockButton1);
        dockableButton1.getContext().setDragNode(dockButton1);
        //
        // Dynamically created Dockable Button
        //
        Button dockButton2 = new Button("To be docked 3");
        Dockable dockableButton2 = new Dockable() {
            private DockableContext c = new DockableContext(this);
            @Override
            public Region node() {
                return dockButton2;
            }

            @Override
            public DockableContext getContext() {
                return c;
            }
            
        };
        dockableButton2.getContext().setDragNode(dockButton2);
        DockRegistry.getInstance().register(dockableButton2);
        //
        //
        //
        root.getChildren().addAll(dockButton, dockButton1, dockButton2);
        
/*        if ( dockButton2.getParent() != null ) {
            //09.02d.getContext().getLayoutContext().changeDockedState(d, true);
            dockableButton2.getContext().getLayoutContext().setTargetNode((Region)dockableButton2.node().getParent());
        }        
*/
        Stage stage1 = new Stage();
        DockPane dockPane = new DockPane();
        dockPane.setStyle("-fx-border-width: 2px; -fx-border-color: red");
        stage1.setX(100);
        stage1.setY(100);
        
        StackPane rootPane = new StackPane(dockPane);
        Scene scene1 = new Scene(rootPane, 200, 200);
        stage1.setScene(scene1);

        stage.setScene(scene);
        stage.show();
        stage1.show();
        
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
