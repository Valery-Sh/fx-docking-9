package org.vns.javafx.dock.api;

import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Valery
 */
public class StageBuilder extends FloatStageBuilder {

    public StageBuilder(DockableController nodeController) {
        super(nodeController);
    }

    @Override
    protected void makeFloating(Dockable dockable) {

        Node titleBar = dockable.dockableController().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        //double nodeHeight = dockable.node().getHeight();
        setDefaultCursors();

        dockable.dockableController().getTargetController().undock(dockable.node());

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
        return this.createStage(dockable, null);
    }

    public Stage createStage(Dockable dockable, Region parentPane) {

        Region node = dockable.node();
        Node titleBar = dockable.dockableController().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        Stage newStage = new Stage();

        DockRegistry.register(newStage);
        stageProperty().set(newStage);

        newStage.setTitle("NEW STAGE");
        Region lastDockPane = dockable.dockableController().getTargetController().getTargetNode();
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            newStage.initOwner(lastDockPane.getScene().getWindow());
        }
        newStage.initStyle(getStageStyle());

        setRootPane(new BorderPane());
        Region pane = parentPane;
        if (parentPane == null) {
            pane = new StackPane();
        }

        pane.setStyle("-fx-background-color: aqua");
        //dockPane.getChildren().add(dockable.node()); // we do not apply dock() 
        //PaneHandler ph = dockable.dockableController().getTargetController();
        //ph.dock(dockable, Side.TOP);
        if (pane instanceof Pane) {
            ((Pane) pane).getChildren().add(dockable.node());
        } else if (pane instanceof SplitPane) {
            ((SplitPane) pane).getItems().add(dockable.node());
        }
        

        ((BorderPane) getRootPane()).setCenter(pane);

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
