package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.ObservableListStringBinding;
import org.vns.javafx.dock.api.designer.bean.editor.ListContentStringBinding;
import org.vns.javafx.dock.api.designer.bean.editor.StringTextField;

/**
 *
 * @author Valery
 */
public class TestListContentStringBinding extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        Button b01 = new Button("Change styleClass");
        Button b02 = new Button("Unbind");
        StringProperty strProp = new SimpleStringProperty();
        b01.getStyleClass().add("btn01");
        ListContentStringBinding<String> olb = new ListContentStringBinding<>(strProp,b01.getStyleClass(),",", new DefaultStringConverter());
        olb.bindBidirectional();
        VBox vbox = new VBox(b01, b02);
        stage.setTitle("Test DockSideBar");
        //b01.fireEvent();
        //((Region)borderPane.getRight()).setMaxWidth(0);
        Scene scene = new Scene(vbox);
        b01.setOnAction(e->{
            System.err.println("1 strProp = " + strProp.get());
            if ( b01.getStyleClass().contains("button") ) {
                b01.getStyleClass().remove("button");
            } else {
                b01.getStyleClass().add("button");
            }
            System.err.println("2 strProp = " + strProp.get());
        });
        b02.setOnAction(e->{
            //strProp.unbind();
            strProp.set("button, button1,button2");
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

