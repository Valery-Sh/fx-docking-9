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
package org.vns.javafx.dock.api.designer.bean.editor;

import java.util.function.Predicate;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Valery Shyshkin
 */
public class ErrorDialog {
    private Predicate<String> validator;
    private Pane root;
    private Pane textFieldPane;
    private Pane messagePane;
    private Pane buttonPane;
    private StringProperty item;
    private TextField itemTextField;
    private TextFlow messageTextFlow;
    private Text[] errorText;
    private Button okButton;
    private Button cancelButton;
    private Stage stage;
    private Scene scene;
    private Window ownerWindow;
    private Result result;
    
    public enum Result {
        OK, CANCEL
    }

    public ErrorDialog() {
        this.item = new SimpleStringProperty();
        init();
    }
    private void init() {
        textFieldPane = new GridPane();
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setPercentWidth(100);
        ((GridPane)textFieldPane).getColumnConstraints().addAll(cc0);    
        
        itemTextField = new SimpleStringPropertyEditor();
        ((GridPane)textFieldPane).add(itemTextField, 0, 0);
        okButton = new Button("Ok");
        cancelButton = new Button("cancel");
        
        okButton.setOnAction(a -> {
            //System.err.println("H = " + stage.getHeight() +  " W = " + stage.getWidth());
            if ( getValidator() != null && ! getValidator().test(itemTextField.getText())) {
                errorText[0].setText("The error not fixed. Try again");
                return;
            }
            itemTextField.commitValue();
            setItem(itemTextField.getText());
            result = Result.OK;
            stage.close();
        });
        cancelButton.setOnAction(a -> {
            System.err.println("ErrorDialog CANCEL item = " + item);
            result = Result.CANCEL;
            stage.close();
        });
   
        messagePane = new VBox();
        messagePane.setPadding(new Insets(20,0,20,0));
        
        buttonPane = new HBox(okButton, cancelButton);
        ((HBox)buttonPane).setSpacing(10);
        ((HBox)buttonPane).setAlignment(Pos.BOTTOM_RIGHT);
        ((HBox)buttonPane).setPadding(new Insets(0,10,10,0));
        root = new VBox(textFieldPane,messagePane,buttonPane);
        root.setStyle("-fx-padding: 10,10,10,10");
        
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
    
    public StringProperty itemProperty() {
        return item;
    }
    public String getItem() {
        return item.get();
    }
    public void setItem(String item) {
        this.item.set(item);
    }

    public Predicate<String> getValidator() {
        return validator;
    }

    public void setValidator(Predicate<String> validator) {
        this.validator = validator;
    }
    
    public void show(Window ownerWindow, String item, String... errorMessages ) {
        
//        if ( this.ownerWindow != null ) {
            System.err.println("!!! ErrorDialog SHOW ");
            //stage = new Stage();
            //stage.sizeToScene();
            //stage.setScene(scene);
            //stage.initOwner(ownerWindow);
//        } else {
//            this.ownerWindow = ownerWindow;
//            stage.initOwner(ownerWindow);
//        }
        //stage.initModality(Modality.APPLICATION_MODAL);
        
        stage.setWidth(140);
        stage.setHeight(265);
        
        setItem(item);
        itemTextField.setText(item);
        if ( errorMessages.length == 0) {
            errorText = new Text[1];
            errorText[0] = new Text();
            errorText[0].setText("Error item found");
            errorText[0].setStyle("-fx-fill: red;");
             
        } else {
            errorText = new Text[errorMessages.length];
            for ( int i=0; i < errorMessages.length; i++ ) {
               errorText[i] = new Text(errorMessages[i]);
               errorText[i].setText(errorMessages[i]);
            }
            errorText[0].setText("Error item found");
            errorText[0].setStyle("-fx-fill: red;");

        }
        messagePane.getChildren().remove(messageTextFlow);
        messageTextFlow = new TextFlow(errorText);        
        messagePane.getChildren().add(messageTextFlow);
        messageTextFlow.setStyle("-fx-text-fill: red; -fx-background: green;");
        stage.showAndWait();
    }
    
    public void hide() {
        stage.close();
    }
    
}
