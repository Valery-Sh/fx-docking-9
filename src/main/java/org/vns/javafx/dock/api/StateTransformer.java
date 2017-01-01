package org.vns.javafx.dock.api;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.vns.javafx.dock.DockPane;

/**
 *
 * @author Valery
 */
public class StateTransformer {
    private StageStyle stageStyle = StageStyle.TRANSPARENT;

    private ObjectProperty<Stage> stageProperty = new SimpleObjectProperty<>();
    private BorderPane borderPane;

    private final DockNodeHandler dockState;   

    private ResizeTransformer resizer;
    
    
    public StateTransformer(DockNodeHandler dockState) {
        this.dockState = dockState;
    }

    public ObjectProperty<Stage> stageProperty() {
        return this.stageProperty;
    }

    public Stage getStage() {
        return stageProperty.get();
    }
    private Region node() {
        return dockState.node();
    }
    private Dockable dockable() {
        return dockState.dockable();
    }
    
    //==========================
    //
    //==========================
    public void makeFloating() {
        if (node() == null) {
            return;
        }
        makeFloating(dockable());
        
/*        if (node() instanceof MultiTab) {
            makeFloating((MultiTab) node());
        } else  {
            makeFloating(dockable());
        }
*/
    }

    public final boolean isDecorated() {
        return stageStyle != StageStyle.TRANSPARENT && stageStyle != StageStyle.UNDECORATED;
    }

    protected void makeFloating(Dockable dockable) {

        //31.12Region node = (Region)dockable;
        Region node = dockable.node();
        Point2D screenPoint = node.localToScreen(0, 0);
        Node titleBar = dockable.nodeHandler().getTitleBar();
        titleBar.setVisible(true);
        titleBar.setManaged(true);

        if (dockable.nodeHandler().isDocked()) {
            dockable.nodeHandler().undock();
        }

        Stage newStage = new Stage();
        DockRegistry.register(newStage);                        
        stageProperty.set(newStage);

        //newStage.titleProperty().bind(dockable.titleProperty());
        newStage.setTitle("FLOATING STAGE");
        Pane dockPane = dockable.nodeHandler().getPaneHandler().getDockPane();
        
        if (dockPane != null && dockPane.getScene() != null
                && dockPane.getScene().getWindow() != null) {
            newStage.initOwner(dockPane.getScene().getWindow());
        }
        
        newStage.initStyle(stageStyle);

        // offset the new stage to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        Point2D stagePosition = screenPoint;
        /*            if (isDecorated()) {
                Window owner = stage.getOwner();
                stagePosition = scenePoint.add(new Point2D(owner.getX(), owner.getY()));
            } else {
                stagePosition = screenPoint;
            }
            if (translation != null) {
                stagePosition = stagePosition.add(translation);
            }
         */
        DockPaneHandler  pn01 = dockState.getPaneHandler();
        
        borderPane = new BorderPane();
        dockPane = new DockPane();
        //
        // Prohibit to use as a dock target
        //
        ((DockPaneTarget)dockPane).paneHandler().setUsedAsDockTarget(false);
        
        //31.12((DockPane)dockPane).dock((Node)dockable, Side.TOP);
        ((DockPane)dockPane).dock(dockable, Side.TOP);        
        
        borderPane.getStyleClass().add("dock-node-border");
        
        dock(dockPane, dockable);
        borderPane.setCenter(dockPane);

        Scene scene = new Scene(borderPane);
        DockPaneHandler  pn02 = dockState.getPaneHandler();
        boolean b = pn01 == pn02;
        
        
        floatingProperty.set(true);
        
        node.applyCss();
        borderPane.applyCss();
        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        newStage.setX(stagePosition.getX() - insetsDelta.getLeft());
        newStage.setY(stagePosition.getY() - insetsDelta.getTop());

        newStage.setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        newStage.setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);

        borderPane.setPrefSize(node.getWidth() + insetsWidth, node.getHeight() + insetsHeight);
        newStage.setResizable(true);
        newStage.setScene(scene);
        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        newStage.setResizable(dockable.nodeHandler().isResizable());
        if (newStage.isResizable()) {
            newStage.addEventFilter(MouseEvent.MOUSE_PRESSED, this::pressedHandle);
            newStage.addEventFilter(MouseEvent.MOUSE_MOVED, this::movedHandle);
            newStage.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::draggedHandle);
        }
        
        newStage.sizeToScene();
        resizer = new ResizeTransformer();
        newStage.show();

    }
    public void pressedHandle(MouseEvent ev) {
        resizer.start(ev, getStage(), getStage().sceneProperty().get().getCursor());
    }

    public void movedHandle(MouseEvent ev) {
        Cursor c = ResizeTransformer.cursorBy(ev, borderPane);
        getStage().sceneProperty().get().setCursor(c);

        if (!c.equals(Cursor.DEFAULT)) {
            ev.consume();
        }
    }

    public void draggedHandle(MouseEvent ev) {
        resizer.resize(ev);
    }

    protected void dock(Pane dockPane, Dockable dockable) {
        Node node = (Node) dockable;
        SplitDelegate.DockSplitPane rootSplitPane = null;
        rootSplitPane = new SplitDelegate.DockSplitPane();
        
        //!!!!!! old dockPane.setRootSplitPane(rootSplitPane);
        
        rootSplitPane.getItems().add(node);

        dockPane.getChildren().add(rootSplitPane);
        //
        // Actually it's docken/ Return later. May be implement 
        // docked state extended
        //
        // >>> node.getDockableState().setDocked(true);
    }

  
    private BooleanProperty floatingProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            Node node = node();
            node.pseudoClassStateChanged(PseudoClass.getPseudoClass("floating"), get());
            if (borderPane != null) {
                borderPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("floating"), get());
            }
        }

        @Override
        public String getName() {
            return "floating";
        }
    };

}
