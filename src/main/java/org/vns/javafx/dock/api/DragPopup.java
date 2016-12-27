package org.vns.javafx.dock.api;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class DragPopup extends Popup {

    private Pane dockPane;
    private Pane popupPane;
    private Popup dockNodePopup;
    private GridPane dockNodePane;

    private Button btnTop;
    private Button btnBottom;
    private Button btnLeft;
    private Button btnRight;

    private Button nodeBtnTop;
    private Button nodeBtnBottom;
    private Button nodeBtnLeft;
    private Button nodeBtnRight;

    private Node dockable;
    private Rectangle dockPlace;

    private Node dragTarget;
    private Side dockPos;

    public DragPopup() {
        init();
    }

    private void init() {
        popupPane = new BorderPane();
        popupPane.setMouseTransparent(true);

        popupPane.setStyle("-fx-border-width: 4.0; -fx-border-color: blue;");
        getContent().add(popupPane);

        dockPlace = new Rectangle();
        dockPlace.setManaged(false);
        dockPlace.getStyleClass().add("dock-place");

        popupPane.getChildren().add(dockPlace);
        btnTop = new Button();

        btnTop.getStyleClass().add("dock-pos-top");

        ((BorderPane) popupPane).setTop(btnTop);

        btnRight = new Button("");
        btnRight.getStyleClass().add("dock-pos-right");
        ((BorderPane) popupPane).setRight(btnRight);
        btnLeft = new Button("");
        ((BorderPane) popupPane).setLeft(btnLeft);
        btnLeft.getStyleClass().add("dock-pos-left");

        btnBottom = new Button();
        ((BorderPane) popupPane).setBottom(btnBottom);

        btnBottom.getStyleClass().add("dock-pos-bottom");

        //((BorderPane)popupPane).
        BorderPane.setAlignment(btnTop, Pos.CENTER);
        BorderPane.setAlignment(btnLeft, Pos.CENTER);
        BorderPane.setAlignment(btnBottom, Pos.CENTER);
        BorderPane.setAlignment(btnRight, Pos.CENTER);
        //
        // A popup to treat DockNode as dock target
        //
        dockNodePopup = new Popup();

        dockNodePane = new GridPane();
        dockNodePane.getStyleClass().add("dock-target-pos");
        nodeBtnTop = new Button();
        nodeBtnTop.getStyleClass().add("dock-pos-top");
        nodeBtnBottom = new Button();
        nodeBtnBottom.getStyleClass().add("dock-pos-bottom");
        nodeBtnLeft = new Button();
        nodeBtnLeft.getStyleClass().add("dock-pos-left");

        nodeBtnRight = new Button();
        nodeBtnRight.getStyleClass().add("dock-pos-right");

        dockNodePane.add(nodeBtnTop, 1, 0);
        dockNodePane.add(nodeBtnBottom, 1, 2);
        dockNodePane.add(nodeBtnLeft, 0, 1);
        dockNodePane.add(nodeBtnRight, 2, 1);
        dockNodePopup.getContent().add(dockNodePane);
    }

    public Pane getRoot() {
        return popupPane;
    }

    public Node getDragTarget() {
        return dragTarget;
    }

    public Side getDockPos() {
        return dockPos;
    }

    public void show(Pane dockPane, Node dockable) {
        setAutoFix(false);
        this.dockPane = dockPane;
        this.dockable = dockable;

        popupPane.prefHeightProperty().bind(dockPane.heightProperty());
        popupPane.prefWidthProperty().bind(dockPane.widthProperty());
        //popupPane.minWidthProperty().bind(dockPane.minWidthProperty());
        //popupPane.minHeightProperty().bind(dockPane.minHeightProperty());
        popupPane.minHeightProperty().bind(dockPane.heightProperty());
        popupPane.minWidthProperty().bind(dockPane.widthProperty());

        Point2D pos = dockPane.localToScreen(0, 0);

        this.show(dockPane, pos.getX(), pos.getY());

    }

    public boolean hideWhenOut(double x, double y) {
        if (!isShowing()) {
            return true;
        }
        boolean retval = false;
        if (contains(x, y)) {
            retval = true;
        } else {
            hide();
        }
        return retval;
    }

    public boolean contains(double x, double y) {
        Point2D p = popupPane.localToScreen(0, 0);
        return !((x < p.getX() || x > p.getX() + popupPane.getWidth()
                || y < p.getY() || y > p.getY() + popupPane.getHeight()));

    }

    public boolean contains(Button b, double x, double y) {
        Point2D p = b.localToScreen(0, 0);
        return !((x < p.getX() || x > p.getX() + b.getWidth()
                || y < p.getY() || y > p.getY() + b.getHeight()));

    }

    public void handle(double screenX, double screenY) {
        //
        // Try to find a Dockable objects which contains the specifired 
        // (x,y) coordinates
        // The result may be null
        //
        System.err.println("DOCKPANE !!!!!!!!!! " + dockPane);
        Region d = (Region) DockUtil.findDockable(dockPane, screenX, screenY);
        if (d != null) {
            Point2D p = d.localToScreen((d.getWidth() - dockNodePane.getWidth()) / 2, (d.getHeight() - dockNodePane.getHeight()) / 2);
            dockNodePopup.show(this, p.getX(), p.getY());
        }
        
//        if ( (dockPane instanceof DockPaneTarget ) {
            
//        }
        //System.err.println("handle DOCKABLE " + d);
        dockPlace.setVisible(false);
        dragTarget = null;

        if (contains(nodeBtnTop, screenX, screenY)) {
            showDockPlace(d, Side.TOP);
        } else if (contains(nodeBtnLeft, screenX, screenY)) {
            showDockPlace(d, Side.LEFT);
        } else if (contains(nodeBtnRight, screenX, screenY)) {
            showDockPlace(d, Side.RIGHT);
        } else if (contains(nodeBtnBottom, screenX, screenY)) {
            showDockPlace(d, Side.BOTTOM);
        }

        if (dragTarget != null) {
            return;
        }
        
        if (contains(btnTop, screenX, screenY)) {
            showDockPlace(Side.TOP);
        } else if (contains(btnLeft, screenX, screenY)) {
            showDockPlace(Side.LEFT);
        } else if (contains(btnRight, screenX, screenY)) {
            showDockPlace(Side.RIGHT);
        } else if (contains(btnBottom, screenX, screenY)) {
            showDockPlace(Side.BOTTOM);
        } else {
            dockPlace.setVisible(false);
            dragTarget = null;
        }
    }

    public void showDockPlace(Side side) {
        dockPos = side;
        dragTarget = dockPane;
        //System.err.println("showDockPlace dockTarget " + dragTarget);
        switch (side) {
            case TOP:
                dockPlace.setWidth(dockPane.getWidth());
                dockPlace.setHeight(dockPane.getHeight() / 2);
                Point2D p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case BOTTOM:
                dockPlace.setWidth(dockPane.getWidth());
                dockPlace.setHeight(dockPane.getHeight() / 2);
                p = dockPlace.localToParent(0, dockPane.getHeight() - dockPlace.getHeight());
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case LEFT:
                dockPlace.setWidth(dockPane.getWidth() / 2);
                dockPlace.setHeight(dockPane.getHeight());
                p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case RIGHT:
                dockPlace.setWidth(dockPane.getWidth() / 2);
                dockPlace.setHeight(dockPane.getHeight());
                p = dockPlace.localToParent(dockPane.getWidth() - dockPlace.getWidth(), 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
        }
        dockPlace.setVisible(true);
    }

    public void showDockPlace(Region target, Side side) {
        if ( target == null ) {
            return;
        }
        dockPos = side;
        dragTarget = target;
        
        Point2D p = target.localToScreen(0, 0).subtract(dockPane.localToScreen(0, 0));
        dockPlace.setX(p.getX());
        dockPlace.setY(p.getY());

        switch (side) {
            case TOP:
                dockPlace.setWidth(target.getWidth());
                dockPlace.setHeight(target.getHeight() / 2);
                break;
            case BOTTOM:
                dockPlace.setWidth(target.getWidth());
                dockPlace.setHeight(target.getHeight() / 2);
                dockPlace.setY(p.getY() + dockPlace.getHeight());
                break;
            case LEFT:
                dockPlace.setWidth(target.getWidth() / 2);
                dockPlace.setHeight(target.getHeight());
                break;
            case RIGHT:
                dockPlace.setWidth(target.getWidth() / 2);
                dockPlace.setHeight(target.getHeight());
                dockPlace.setX(p.getX() + dockPlace.getWidth());
                break;
        }
        dockPlace.setVisible(true);
    }

    public Pane getDockPane() {
        return dockPane;
    }

}
