package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.ObservableListStringBinding;
import org.vns.javafx.dock.api.designer.bean.editor.ListContentPropertyEditors;
import org.vns.javafx.dock.api.designer.bean.editor.ObservableListPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestObservableListPropertyEditor extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        Button b01 = new Button("Change styleClass");
        Button b02 = new Button("Unbind");
        StringProperty strProp = new SimpleStringProperty();
        b01.getStyleClass().add("btn01");
        
        ObservableListPropertyEditor<String> olb = ListContentPropertyEditors.getEditor(String.class);
       olb.getValidators().add( item -> {
            System.err.println("******** VALIDATOR item = '" + item + "'");
            if ( olb.isSameAsNull(item) ) {
                System.err.println("&&&&&&&&&&& IS SAME AS NULL &&&&&&&&&&&&&&& ");
                return false;
            }
            return true;
        });
        
        olb.bindBidirectional(b01.getStyleClass());
        
        VBox vbox = new VBox(olb,b01, b02);
        
        stage.setTitle("Test DockSideBar");
        //b01.fireEvent();
        //((Region)borderPane.getRight()).setMaxWidth(0);
        Scene scene = new Scene(vbox);
        stage.setWidth(200);
        b01.setOnAction(e->{
            System.err.println("1 lastValid = '" + olb.getLastValidText() + "'" );
            for ( String s : b01.getStyleClass()) {
                System.err.println("1 b01.styleClass = '" + s + "'" );
            }
            
            if ( b01.getStyleClass().contains("button") ) {
                b01.getStyleClass().remove("button");
            } else {
                b01.getStyleClass().add("button");
            }
            System.err.println("2 lastValid = '" + olb.getLastValidText() + "'" );
            for ( String s : b01.getStyleClass()) {
                System.err.println("2 b01.styleClass = '" + s + "'" );
            }

        });
        b02.setOnAction(e->{
            olb.setText(null);
        });        
        stage.setScene(scene);
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

    private void initSceneDragAndDrop(Scene scene) {
        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() || db.hasUrl()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });
        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            String url = null;
            if (db.hasFiles()) {
                url = db.getFiles().get(0).toURI().toString();
            } else if (db.hasUrl()) {
                url = db.getUrl();
            }
            if (url != null) {
                //songModel.setURL(url);
                //songModel.getMediaPlayer().play();
            }
            System.err.println("DROPPED");
            event.setDropCompleted(url != null);
            event.consume();
        });
    }
}

