/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.controller;

import com.sun.javafx.css.StyleManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DockPaneFXMLController implements Initializable {

    //@FXML
    //private Label label;
    @FXML
    private DockPane dockPane;
    
    
    
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        System.err.println("########################");
        String strUrl = Dockable.class.getResource("resources/default.css").toExternalForm();
        System.err.println("STRURL=" + strUrl);
        StyleManager.getInstance()
                .addUserAgentStylesheet(Dockable.class.getResource("resources/default.css").toExternalForm());

    }
}
