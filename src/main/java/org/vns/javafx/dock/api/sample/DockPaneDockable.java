package org.vns.javafx.dock.api.sample;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.demo.DockPaneControllerOld;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery
 */
public class DockPaneDockable extends VBox implements DockTarget{

    private DockPaneControllerOld paneHandler;
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
        paneHandler = new DockPaneControllerOld(this.dockRootPane);
        Platform.runLater(() -> dockRootPane.prefHeightProperty().bind(heightProperty()));
    }

    public Dockable dock(Dockable node, Side dockPos) {
        return null;
        //return paneHandler.dock(node, dockPos);
    }

    @Override
    public Pane target() {
        return dockRootPane;
    }

    @Override
    public DockTargetController targetController() {
        return paneHandler;
    }

}
