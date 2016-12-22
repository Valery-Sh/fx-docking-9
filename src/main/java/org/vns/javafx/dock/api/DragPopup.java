package org.vns.javafx.dock.api;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
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

    public DragPopup() {
        init();
    }

    private void init() {
        System.err.println("##############################");
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
        Button btnCenter = new Button("Center button");
        ((BorderPane) popupPane).setCenter(btnCenter);
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
        
        nodeBtnTop = new Button();
        nodeBtnTop.getStyleClass().add("dock-pos-top");
        nodeBtnBottom = new Button();
        nodeBtnBottom.getStyleClass().add("dock-pos-bottom");
        nodeBtnLeft = new Button();
        nodeBtnLeft.getStyleClass().add("dock-pos-left");
        
        nodeBtnRight = new Button();
        nodeBtnRight.getStyleClass().add("dock-pos-right");
        
        
        dockNodePane.add(nodeBtnTop,1,0);
        dockNodePane.add(nodeBtnBottom,1,2);
        dockNodePane.add(nodeBtnLeft,0,1);
        dockNodePane.add(nodeBtnRight,2,1);
        dockNodePopup.getContent().add(dockNodePane);

    }

    public Pane getRoot() {
        return popupPane;
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
    
    public void showGrid(double x, double y) {
        
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
        Region d = (Region) DockUtil.findNode(dockPane, screenX, screenY);
        if ( d != null ) {
            Point2D p = d.localToScreen( (d.getWidth() - dockNodePane.getWidth() )/ 2,(getHeight() - dockNodePane.getHeight() )/ 2);
            dockNodePopup.show(this , p.getX(), p.getY());
        }
        System.err.println("DOCKABLE " + d);        
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
        }

    }

    public void showDockPlace(Side side) {
        switch (side) {
            case TOP:
                dockPlace.setWidth(dockPane.getWidth());
                dockPlace.setHeight(((Region)dockable).getHeight());
                Point2D p =  dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case BOTTOM:
                dockPlace.setWidth(dockPane.getWidth());
                dockPlace.setHeight(((Region)dockable).getHeight());
                p =  dockPlace.localToParent(0,dockPane.getHeight() - dockPlace.getHeight());
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                
                break;
            case LEFT:
                dockPlace.setWidth(((Region) dockable).getWidth());
                dockPlace.setHeight(dockPane.getHeight());
                p =  dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                
                break;
            case RIGHT:
                dockPlace.setWidth(((Region) dockable).getWidth());
                dockPlace.setHeight(dockPane.getHeight());
                p = dockPlace.localToParent(dockPane.getWidth() - dockPlace.getWidth(), 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());

                break;
        }
        dockPlace.setVisible(true);
    }


}
