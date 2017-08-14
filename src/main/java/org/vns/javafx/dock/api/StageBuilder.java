package org.vns.javafx.dock.api;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class StageBuilder extends FloatWindowBuilder {

    public StageBuilder(Dockable dockable) {
        super(dockable);
    }

    public Window createPopupControl(Dockable dockable, Window owner) {

        Region node = dockable.node();

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
                rootPane = (Pane) dockable.node().getScene().getRoot();
                markFloating((Stage) dockable.node().getScene().getWindow());
                addResizer((Stage) dockable.node().getScene().getWindow(), dockable);
                dockable.dockableController().getTargetController().undock(dockable.node());
                return getFloatingWindow();
            }
        }
         */
        final PopupControl floatPopup = new PopupControl();

        markFloating(floatPopup);

        Point2D stagePosition = screenPoint;

        BorderPane borderPane = new BorderPane();
        setRootPane(borderPane);

        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (floatPopup != null) {
                    floatPopup.hide();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        borderPane.setCenter(dockable.node());

        node.applyCss();
        borderPane.applyCss();

        Insets insetsDelta = borderPane.getInsets();
        //
        // The folloing lines may be usefull only when a Dockable has alredy
        // been shown in Scene Graph. 
        //
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        floatPopup.setX(stagePosition.getX() - insetsDelta.getLeft());
        floatPopup.setY(stagePosition.getY() - insetsDelta.getTop());

//        floatPopup.setMinWidth(borderPane.minHeight(node.getHeight()) + insetsWidth);
//        floatPopup.setMinHeight(borderPane.minWidth(node.getWidth()) + insetsHeight);
        borderPane.setPrefSize(node.getWidth() + insetsWidth, node.getHeight() + insetsHeight);
        //final boolean hasPrefSize = (dockable.node().getPrefHeight() > 0 && dockable.node().getPrefWidth() > 0  ) ? true : false;
        double minWidth = dockable.node().getMinWidth();

        double minHeight = dockable.node().getMinHeight();
        double prefWidth = dockable.node().getPrefWidth();
        double prefHeight = dockable.node().getPrefHeight();

        if (minWidth > 0 && minHeight > 0) {
            //borderPane.setMinSize(minWidth, minHeight);
        }
        floatPopup.getScene().setRoot(borderPane);

        borderPane.setStyle("-fx-background-color: aqua");
        node.setStyle("-fx-background-color: green");
        // 
        // !!! do not set floatPopup.sizeToScene();
        //

        //
        // We must use setOnShowing and setOnShown
        //
        IntegerProperty shownJob = new SimpleIntegerProperty(-1);

        floatPopup.setOnShowing(e -> {

            DockRegistry.register(floatPopup);

//            if ( borderPane.getWidth() <= 0 || borderPane.getWidth()  )
            double x = floatPopup.getAnchorX();
            double sz = borderPane.getWidth();
            double delta = 0;
            System.err.println("prefW=" + prefWidth + "; borderPane.getWidth()=" + borderPane.getWidth());
            if (shownJob.get() < 0) {
                if (borderPane.getWidth() <= 0 && borderPane.getHeight() <= 0) {
                    shownJob.set(2); // onShown must process width and height
                    return;
                } else if (borderPane.getWidth() <= 0) {
                    shownJob.set(0); // onShown must process only width
                    return;
                }
                if (borderPane.getHeight() <= 0) {
                    shownJob.set(1); // onShown must process only height
                    return;
                }
            } else {
                floatPopup.setOnShown(null);
                floatPopup.setOnShowing(null);

/*                System.err.println("popup width = " + floatPopup.getWidth());
                System.err.println("popup getMinWidth = " + floatPopup.getMinWidth());
                System.err.println("popup prefWidth = " + floatPopup.getPrefWidth());
                System.err.println("popup minMidth = " + borderPane.minWidth(node.getHeight()));
*/                
//                dockable.dockableController().setResizeMinWidth(borderPane.minWidth(node.getHeight()));
//                dockable.dockableController().setResizeMinHeight(borderPane.minWidth(node.getWidth()));
                setMinWidth(borderPane.minWidth(node.getHeight()));
                setMinHeight(borderPane.minWidth(node.getWidth()));

            }

            if (shownJob.get() <= 0 || shownJob.get() == 2) { // width or both
                if (prefWidth > borderPane.getWidth()) {
                    delta = sz - prefWidth;
                    sz = prefWidth;
                }

                borderPane.setPrefWidth(sz);

                if (delta < 0) {
                    floatPopup.setAnchorX(x + delta);
                }
                if (minWidth <= 0) {
                    //borderPane.setMinWidth(sz);
                }
                //dockable.dockableController().setResizeMinWidth(sz);

            }
            if (shownJob.get() <= 0 || shownJob.get() >= 1) { // height or both

                delta = 0;
                double y = floatPopup.getAnchorY();
                sz = borderPane.getHeight();

                if (prefHeight > borderPane.getHeight()) {
                    delta = sz - prefHeight;
                    sz = prefHeight;
                }
                borderPane.setPrefHeight(sz);
                if (delta < 0) {
                    floatPopup.setAnchorY(y + delta);
                }
                //dockable.dockableController().setResizeMinHeight(sz);

            }
        });

        floatPopup.setOnShown(floatPopup.getOnShowing());

        /*        floatPopup.setOnShown(e -> {
            System.err.println("popup width = " + floatPopup.getWidth());
            System.err.println("popup getMinWidth = " + floatPopup.getMinWidth());
            System.err.println("popup prefWidth = " + floatPopup.getPrefWidth());
            System.err.println("popup minMidth = " + borderPane.minWidth(node.getHeight()));
            dockable.dockableController().setResizeMinWidth( borderPane.minWidth(node.getHeight()));
            dockable.dockableController().setResizeMinHeight( borderPane.minWidth(node.getWidth()));
        });
         */
        floatPopup.setOnHidden(e -> {
            DockRegistry.unregister(floatPopup);
        });
        floatPopup.setId("FLOAT_POPUP");
        System.err.println("************** dockable.window=" + dockable.node().getScene().getWindow());


        //addResizer(floatPopup, dockable);
        addResizer();        
//        System.err.println("************** +++++++++++ *************");
        return floatPopup;
    }//makeFloatingPopupControl

}//class StageBuilder
