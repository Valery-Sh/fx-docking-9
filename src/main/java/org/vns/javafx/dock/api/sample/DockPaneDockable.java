package org.vns.javafx.dock.api.sample;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.DockPaneController;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTargetController;

/**
 *
 * @author Valery
 */
public class DockPaneDockable extends VBox implements DockPaneTarget{

    private DockPaneController paneHandler;
    private HBox headerPane;
    private StackPane dockRootPane;

    public DockPaneDockable() {
        init();
    }

    private void init() {
        headerPane = new HBox();
        //headerPane.setMaxHeight(30);
        headerPane.getChildren().add(new Button("Test Button"));
        dockRootPane = new StackPane();
        dockRootPane.setStyle("-fx-border-width: 2; -fx-border-color: red");
        getChildren().addAll(headerPane, dockRootPane);
        this.autosize();
        paneHandler = new DockPaneController(this.dockRootPane);
        Platform.runLater(() -> dockRootPane.prefHeightProperty().bind(heightProperty()));
    }

    public Dockable dock(Dockable node, Side dockPos) {
        return paneHandler.dock(node, dockPos);
    }

    @Override
    public Pane pane() {
        return dockRootPane;
    }

    @Override
    public DockTargetController paneController() {
        return paneHandler;
    }

}
