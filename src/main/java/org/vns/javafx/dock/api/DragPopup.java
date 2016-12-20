/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import com.sun.javafx.stage.ScreenHelper;
import com.sun.javafx.stage.StageHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.SnapshotResult;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class DragPopup extends Popup{
    
    private Pane dockPane;
    private Pane popupPane;
    
    public DragPopup(Pane dockPane) {
        this.dockPane = dockPane;
        init();
    }
    private void init() {
        setAutoFix(false);
        popupPane = new BorderPane();
        
        popupPane.setPadding(new Insets(10, 10, 10, 10));
        popupPane.setStyle("-fx-border-width: 4.0; -fx-border-color: blue;");
        popupPane.setStyle("-fx-border-width: 4.0; -fx-border-color: blue;");        
        getContent().add(popupPane);
        popupPane.prefHeightProperty().bind(dockPane.heightProperty());
        popupPane.prefWidthProperty().bind(dockPane.widthProperty());
        //popupPane.minWidthProperty().bind(dockPane.minWidthProperty());
        //popupPane.minHeightProperty().bind(dockPane.minHeightProperty());
        popupPane.minHeightProperty().bind(dockPane.heightProperty());
        popupPane.minWidthProperty().bind(dockPane.widthProperty());
       
        Button btnTop = new Button("Top button");
        ((BorderPane)popupPane).setTop(btnTop);

        Button btnRight = new Button("Right button");
        ((BorderPane)popupPane).setRight(btnRight);
        Button btnLeft = new Button("Left button");
        ((BorderPane)popupPane).setLeft(btnLeft);
        
        Button btnCenter = new Button("Center button");
        ((BorderPane)popupPane).setCenter(btnCenter);
        Button btnBottom = new Button("Bottom button");
        ((BorderPane)popupPane).setBottom(btnBottom);
        //((BorderPane)popupPane).
        BorderPane.setAlignment(btnTop, Pos.CENTER);
        BorderPane.setAlignment(btnLeft, Pos.CENTER);
        BorderPane.setAlignment(btnBottom, Pos.CENTER);
        BorderPane.setAlignment(btnRight, Pos.CENTER);

        Point2D pos = dockPane.localToScreen(0, 0);
        
        this.show(dockPane,pos.getX(),pos.getY());
//        double w = popupPane.widthProperty().get();
//        double h = popupPane.heightProperty().get();
        //popupPane.getScene().setsetX(dockPane.getScene().getX());
        //popupPane.layoutXProperty().set(dockPane.getLayoutX());
        //popupPane.layoutXProperty().set(dockPane.getLayoutX());
        //WindowHelper.
        //SnapshotResult sr;
        
    }
}
