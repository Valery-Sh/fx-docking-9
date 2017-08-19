/*
 * Copyright 2017 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.dragging.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableController;

/**
 *
 * @author Valery
 */
public class FloatStageView implements FloatWindowView{
    
    private StageStyle stageStyle = StageStyle.TRANSPARENT;

    private ObjectProperty<Window> floatingWindow = new SimpleObjectProperty<>();

    private Pane rootPane;

    private final DockableController dockableController;

    private WindowResizer resizer;

    private MouseResizeHandler mouseResizeHanler;

    private BooleanProperty floating = createFloatingProperty();
            
    private double minWidth = -1;
    private double minHeight = -1;
    

    private Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    
        
    public FloatStageView(Dockable dockable) {
        this.dockableController = dockable.dockableController();
        mouseResizeHanler = new MouseResizeHandler(this);
    }
    @Override
    public void initialize() { }
    
/*    @Override
    public double getMinWidth() {
        return minWidth;
    }

    @Override
    public double getMinHeight() {
        return minHeight;
    }

    public void setMinWidth(double minWidth) {
        this.minWidth = minWidth;
    }

    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }
*/
    protected BooleanProperty floatingProperty() {
        return floating;
    }

    public MouseResizeHandler getMouseResizeHanler() {
        return mouseResizeHanler;
    }
    

    public StageStyle getStageStyle() {
        return stageStyle;
    }

    @Override
    public Pane getRootPane() {
        return rootPane;
    }

    protected void setRootPane(Pane rootPane) {
        this.rootPane = rootPane;
    }

    public DockableController getDockableController() {
        return dockableController;
    }

    @Override
    public WindowResizer getResizer() {
        return resizer;
    }

/*    public void setResizer(FloatWindowResizer resizer) {
        this.resizer = resizer;
    }
*/
    public void setStageStyle(StageStyle stageStyle) {
        this.stageStyle = stageStyle;
    }
    
    @Override
    public Cursor[] getSupportedCursors() {
        return supportedCursors;
    }


    @Override
    public void setSupportedCursors(Cursor[] supportedCursors) {
        this.supportedCursors = supportedCursors;
    }

    public ObjectProperty<Window> stageProperty() {
        return this.floatingWindow;
    }
    
    @Override
    public ObjectProperty<Window> floatingWindowProperty() {
        return floatingWindow;
    }
    
    @Override
    public Window getFloatingWindow() {
        return floatingWindow.get();
    }
    protected void markFloating(Window toMark) {
        floatingWindow.set(toMark);
    }

    protected Region node() {
        return dockableController.dockable().node();
    }

    @Override
    public Dockable getDockable() {
        return dockableController.dockable();
    }

    //==========================
    //
    //==========================
    public void makeFloating() {
        if (node() == null) {
            return;
        }
        make(getDockable());
    }



    public final boolean isDecorated() {
        return stageStyle != StageStyle.TRANSPARENT && stageStyle != StageStyle.UNDECORATED;
    }


    @Override
    public Window make(Dockable dockable, boolean show) {
        
        setSupportedCursors(DEFAULT_CURSORS);
//        System.err.println("1 FloatingStageView CONVERT THE SAME");                        
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

        if (dockable.dockableController().isDocked() && dockable.dockableController().getTargetController().getTargetNode() != null) {
            Window w = dockable.dockableController().getTargetController().getTargetNode().getScene().getWindow();
            if (dockable.node().getScene().getWindow() != w) {
                rootPane = (Pane) dockable.node().getScene().getRoot();
                markFloating(dockable.node().getScene().getWindow());
                setSupportedCursors(DEFAULT_CURSORS);

                //addResizer(getDockable.node().getScene().getWindow(), getDockable);
//System.err.println("2 FloatingStageView CONVERT THE SAME");                
                //addResizer();
                dockable.dockableController().getTargetController().undock(dockable.node());
                return dockable.node().getScene().getWindow();
            }
        }
        if (dockable.dockableController().isDocked()) {
            dockable.dockableController().getTargetController().undock(dockable.node());
        }

        Stage newStage = new Stage();
        DockRegistry.register(newStage);
        markFloating(newStage);

        newStage.setTitle("FLOATING STAGE");
        Region lastDockPane = dockable.dockableController().getTargetController().getTargetNode();
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            newStage.initOwner(lastDockPane.getScene().getWindow());
        }

        newStage.initStyle(stageStyle);

        // offset the new floatingWindow to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        Point2D stagePosition = screenPoint;

        BorderPane borderPane = new BorderPane();
        this.rootPane = borderPane;

        //DockPane dockPane = new DockPane();
        //StackPane dockPane = new StackPane();
        //borderPane.setStyle("-fx-background-color: aqua");
        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (newStage != null) {
                    newStage.close();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        //
        // Prohibit to use as a dock target
        //
        //dockPane.setUsedAsDockTarget(false);
        //dockPane.getItems().add(dockable.node());
        //dockPane.getChildren().add(node);
        //borderPane.getStyleClass().clear();
        borderPane.getStyleClass().add("dock-node-border");
        
        borderPane.setCenter(node);
        //borderPane.setStyle("-fx-background-color: aqua");
        Scene scene = new Scene(borderPane);
        scene.setCursor(Cursor.HAND);
        floatingProperty.set(true);
        
        newStage.setScene(scene);

        node.applyCss();
        borderPane.applyCss();
        //dockPane.applyCss();
        Insets insetsDelta = borderPane.getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        newStage.setX(stagePosition.getX() - insetsDelta.getLeft());
        newStage.setY(stagePosition.getY() - insetsDelta.getTop());

        newStage.setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        newStage.setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);
        
        //setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        //setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);
        
        double prefWidth  = borderPane.prefWidth(node.getHeight()) + insetsWidth;
        double prefHeight = borderPane.prefHeight(node.getWidth()) + insetsHeight;        
        
        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);

        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer();
        newStage.sizeToScene();
        newStage.setAlwaysOnTop(true);
        if ( show ) {
            newStage.show();
        }
        dockable.node().parentProperty().addListener(pcl);
        return newStage;
    }
    
    @Override
    public Window make(Dockable dockable) {
        return make(dockable, true);
    }
    public Stage toFloatingStage(Popup popup) {
        if (popup.getContent() == null || popup.getContent().isEmpty()) {
            return null; // ?? TO DO
        }
        if (!(popup.getContent().get(0) instanceof Region)) {
            return null;
        }

        Region node = (Region) popup.getContent().get(0);
        if (!DockRegistry.instanceOfDockable(node)) {
            return null;
        }
        Dockable dockable = DockRegistry.dockable(node);
        make(dockable);
        getFloatingWindow().setX(popup.getX());
        getFloatingWindow().setY(popup.getY());
        return (Stage) getFloatingWindow();
    }


/*    public void addResizer(Window window, Dockable getDockable) {
        if ( getDockable.dockableController().isResizable()) {
            removeListeners(getDockable);
            addListeners(window);
            
        }
        //resizer = new FloatWindowResizer();
        resizer = new FloatWindowResizer(this);
    }
*/
    
    @Override
    public void addResizer() {
        if ( dockableController.isResizable()) {
            removeListeners(dockableController.dockable());
            addListeners(getFloatingWindow());
            
        }
        setResizer(new StageResizer(this));
        
    }
    
    protected void setResizer(WindowResizer resizer) {
        this.resizer = resizer;
    }
/*    protected void addListeners(Window window) {
        window.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        window.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        window.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
    }

    public void removeListeners(Dockable dockable) {
        if ( dockable.node().getScene() == null ||dockable.node().getScene().getWindow() == null ) {
            //return;
        }
        dockable.node().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getRoot().removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        dockable.node().getScene().getRoot().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseResizeHanler);

        dockable.node().getScene().getRoot().removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
        dockable.node().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);

    }    
*/    
    protected void addListeners(Window window) {
        window.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        window.addEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        window.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
    }

    public void removeListeners(Dockable dockable) {
        if ( dockable.node().getScene() == null ||dockable.node().getScene().getWindow() == null ) {
            //return;
        }
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseResizeHanler);

        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);

    }

    /*    public void pressedHandle(MouseEvent ev) {
        resizer.start(ev, getFloatingWindow(), getFloatingWindow().getScene().getCursor(), supportedCursors);
    }

    public void movedHandle(MouseEvent ev) {
        Cursor c = FloatWindowResizer.cursorBy(ev, rootPane,supportedCursors);
        getFloatingWindow().getScene().setCursor(c);

        if (!c.equals(Cursor.DEFAULT)) {
            ev.consume();
        }
    }

    public void draggedHandle(MouseEvent ev) {
        resizer.resize(ev);
    }
     */
/*    protected void dock(Pane dockPane, Dockable getDockable) {
        Node node = getDockable.node();
        DockSplitPane rootSplitPane = null;
        rootSplitPane = new DockSplitPane();
        //-rootSplitPane = new HSplit();
        //!!!!!! old dockPane.setRootSplitPane(rootSplitPane);
        rootSplitPane.getItems().add(node);

        dockPane.getChildren().add(rootSplitPane);
        //
        // Actually it's docken/ Return later. May be implement 
        // docked state extended
        //
        // >>> node.getDockableState().setDocked(true);
    }
*/
    private BooleanProperty floatingProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            Node node = node();
            node.pseudoClassStateChanged(PseudoClass.getPseudoClass("floating"), get());
            if (rootPane != null) {
                rootPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("floating"), get());
            }
        }

        @Override
        public String getName() {
            return "floating";
        }
    };
}//class FloatWindowBuilder
