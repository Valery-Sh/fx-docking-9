/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockPane;

/**
 *
 * @author Valery
 */
public class StageBuilder extends FloatStageBuilder {

    public StageBuilder(DockNodeHandler nodeHandler) {
        super(nodeHandler);
    }

    @Override
    protected void makeFloating(Dockable dockable) {

        Node titleBar = dockable.nodeHandler().getTitleBar();
        
        titleBar.setVisible(true);
        titleBar.setManaged(true);
        
        double nodeHeight = dockable.node().getHeight();
        

        //setRootPane((Pane) dockable.node().getScene().getRoot());
        //stageProperty().set((Stage) dockable.node().getScene().getWindow());
        setDefaultCursors();
        
        //addResizer((Stage) dockable.node().getScene().getWindow(), dockable);
        dockable.nodeHandler().undock();
        BorderPane borderPane = (BorderPane) dockable.node().getScene().getRoot();
        
        borderPane.getStyleClass().add("dock-node-border");    
        dockable.node().setPrefHeight(nodeHeight + 50);
/*        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        dockable.node().applyCss();
        borderPane.applyCss();
*/
        //getStage().setX(stagePosition.getX() - insetsDelta.getLeft());
        //newStage.setY(stagePosition.getY() - insetsDelta.getTop());

//        getStage().setMinWidth(borderPane.minWidth(dockable.node().getHeight()) + insetsWidth);
//        getStage().setMinHeight(borderPane.minHeight(dockable.node().getWidth()) + insetsHeight);
        //borderPane.setMinWidth(borderPane.minWidth(dockable.node().getHeight()) + insetsWidth);
        //borderPane.setMinHeight(100);

        //borderPane.setPrefSize(dockable.node().getWidth() + insetsWidth, nodeHeight + insetsHeight);
        //borderPane.setPrefSize(100,100);
        
/*        if (getStageStyle() == StageStyle.TRANSPARENT) {
            dockable.node().getScene().setFill(null);
        }
*/        
        
        addResizer((Stage) dockable.node().getScene().getWindow(), dockable);
    }
    @Override
    protected void addListeners(Stage stage) {
        stage.addEventHandler(MouseEvent.MOUSE_PRESSED, getMouseResizeHanler());
        stage.addEventFilter(MouseEvent.MOUSE_MOVED, getMouseResizeHanler());
        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, getMouseResizeHanler());
    }

    public Stage createStage(Dockable dockable) {

        Region node = dockable.node();
        Node titleBar = dockable.nodeHandler().getTitleBar();
        titleBar.setVisible(true);
        titleBar.setManaged(true);

        Stage newStage = new Stage();
        DockRegistry.register(newStage);
        stageProperty().set(newStage);

        //newStage.titleProperty().bind(dockable.titleProperty());
        newStage.setTitle("NEW STAGE");
        Pane lastDockPane = dockable.nodeHandler().getLastDockPane();
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            newStage.initOwner(lastDockPane.getScene().getWindow());
        }
        newStage.initStyle(getStageStyle());

        setRootPane(new BorderPane());

        Pane dockPane = new DockPane();
        dockPane.getChildren().add(dockable.node()); // we do not apply dock() 

        ((BorderPane) getRootPane()).setCenter(dockPane);

        Scene scene = new Scene(getRootPane());

        node.applyCss();
        getRootPane().applyCss();

        newStage.setResizable(true);
        newStage.setScene(scene);
        if (getStageStyle() == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer(newStage, dockable);
        newStage.sizeToScene();

        return newStage;
    }

}//class StageBuilder
