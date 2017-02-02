package org.vns.javafx.dock.api;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        //double nodeHeight = dockable.node().getHeight();
        setDefaultCursors();

        dockable.nodeHandler().getPaneHandler().undock(dockable.node());

        BorderPane borderPane = (BorderPane) dockable.node().getScene().getRoot();

        borderPane.getStyleClass().add("dock-node-border");
        borderPane.applyCss();
        //dockable.node().setPrefHeight(nodeHeight + 50);

        addResizer((Stage) dockable.node().getScene().getWindow(), dockable);

        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
        Stage st = (Stage) dockable.node().getScene().getWindow();

        st.setMinHeight(borderPane.minHeight(dockable.node().getWidth()) + insetsHeight);
        st.setMinWidth(borderPane.minWidth(dockable.node().getHeight()) + insetsWidth);
    }

    public Stage createStage(Dockable dockable) {

        Region node = dockable.node();
        Node titleBar = dockable.nodeHandler().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        Stage newStage = new Stage();

        DockRegistry.register(newStage);
        stageProperty().set(newStage);

        newStage.setTitle("NEW STAGE");
        Region lastDockPane = dockable.nodeHandler().getPaneHandler().getDockPane();
        System.err.println("LAST DOCK PANE");
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            System.err.println("INIT OWNER");
            newStage.initOwner(lastDockPane.getScene().getWindow());
        }
        newStage.initStyle(getStageStyle());

        setRootPane(new BorderPane());

        Pane dockPane = new DockPaneBox();
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
