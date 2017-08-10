package org.vns.javafx.dock.api;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.PopupWindow;

/**
 *
 * @author Valery
 */
public class StageBuilder extends FloatWindowBuilder {

    public StageBuilder(DockableController nodeController) {
        super(nodeController);
    }

    /*    @Override
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
     */
    @Override
    public PopupControl createPopupControl(Dockable dockable) {

        Region node = dockable.node();
        /*        if (node.getScene() == null || node.getScene().getWindow() == null) {
            return null;
        }
         */
        //Window owner = node.getScene().getWindow();

        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
        }
        Node titleBar = dockable.dockableController().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        /*        if (dockable.dockableController().isDocked() && dockable.dockableController().getTargetController().getTargetNode() != null) {
            Window w = dockable.dockableController().getTargetController().getTargetNode().getScene().getWindow();
            if (dockable.node().getScene().getWindow() != w) {
                //??? rootPane = (Pane) dockable.node().getScene().getRoot();
                setFloatingWindow((Stage) dockable.node().getScene().getWindow());
                addResizer((Stage) dockable.node().getScene().getWindow(), dockable);
                dockable.dockableController().getTargetController().undock(dockable.node());
                return getFloatingWindow();
            }
        }

        if (dockable.dockableController().isDocked()) {
            dockable.dockableController().getTargetController().undock(dockable.node());
        }
         */
        final PopupControl floatPopup = new PopupControl();

        setFloatingWindow(floatPopup);

        Point2D stagePosition = screenPoint;

        BorderPane borderPane = new BorderPane();
        // ??? this.rootPane = borderPane;

        //DockPane dockPane = new DockPane();

        /*        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (floatPopup != null) {
                    floatPopup.hide();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };
         */
        //
        // Prohibit to use as a dock target
        //
        //dockPane.setUsedAsDockTarget(false);
        //dockPane.getItems().add(dockable.node());
        //borderPane.getStyleClass().add("dock-node-border");
        borderPane.setCenter(dockable.node());
        setRootPane(borderPane); // needs by resizer

        //setDefaultCursors();
        //??? floatingProperty.set(true);
        node.applyCss();
        borderPane.applyCss();

        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
        //floatPopup.setAutoFix(false);
        Platform.runLater(() -> {
//            floatPopup.setX(stagePosition.getX() - insetsDelta.getLeft());
//            floatPopup.setY(stagePosition.getY() - insetsDelta.getTop());

//            floatPopup.setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
//            floatPopup.setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);
            borderPane.setPrefSize(node.getWidth() + insetsWidth, node.getHeight() + insetsHeight);
        });
        floatPopup.setMinWidth(50);
        floatPopup.setMaxWidth(500);
        
        borderPane.setPrefWidth(51);
        borderPane.setMaxWidth(500);

        floatPopup.getScene().setRoot(borderPane);
        
        borderPane.setStyle("-fx-background-color: aqua");
        //dockPane.setStyle("-fx-background-color: blue");
        node.setStyle("-fx-background-color: green");
        floatPopup.setOnShown(e -> {
            DockRegistry.register(floatPopup);
        });
        floatPopup.setOnHidden(e -> {
            DockRegistry.unregister(floatPopup);
        });
        floatPopup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
        //floatPopup.show(owner);
        //dockable.node().parentProperty().addListener(pcl);
        System.err.println("DOCABLE isFloating = " + dockable.dockableController().isFloating());
        addResizer(floatPopup, dockable);

        return floatPopup;
    }//makeFloatingPopupControl

}//class StageBuilder
