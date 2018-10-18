package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestConstraint extends Application {
    private ObjectProperty<Double> constr = new SimpleObjectProperty();
    StringConverter<Double> converter = new ObjectDoubleConverter();
    Button button;
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        VBox root = new VBox();
        Button btn1 = new Button("Btn1");
        //Button btn1 = new Button("Btn1");
        btn1.textProperty().bindBidirectional(constr, converter);
        btn1.setText("100");
        //System.err.println("getConstr() = " + getConstr());
        AnchorPane anchorPane = new AnchorPane();
        root.getChildren().addAll(btn1, anchorPane);
        
        anchorPane.setStyle("-fx-background-color: aqua");
        button = new Button("Add");
        AnchorPane.setLeftAnchor(button, 10.0);
        System.err.println("getConstr() = " + getConstr());
        
        AnchorPane.setRightAnchor(button, 10.0);
        //setConstr(50);
        
        anchorPane.getChildren().addAll(button);
        Scene primaryScene = new Scene(root);

        primaryStage.setTitle("JavaFX and Maven");
        primaryStage.setScene(primaryScene);

        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

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
    public ObjectProperty<Double> constrProperty() {
        return constr;
    }
    public double getConstr() {
        return AnchorPane.getLeftAnchor(button);
    }
    public void setConstr(double c) {
        AnchorPane.setLeftAnchor(button,c);
    }    
    public static class ObjectDoubleConverter extends StringConverter<Double> {

        @Override
        public String toString(Double object) {
            if ( object == null) {
                return "";
            }
            return object.toString();
        }

        @Override
        public Double fromString(String string) {
            return Double.valueOf(string);
        }
        
    } 
}
