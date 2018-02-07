
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
package org.vns.javafx.dock.api.dragging;

import org.vns.javafx.dock.api.dragging.view.FloatViewFactory;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.TopNodeHelper;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.ALL;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.CARRIER;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.CARRIERED;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.NONE;
import org.vns.javafx.dock.api.dragging.view.FloatView;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

/**
 * The class manages the process of dragging of the object of type
 * {@link Dockable}} from the moment you press the mouse button and ending by
 * initiation docking operations.
 *
 * The objects of typo {@code Dockable} can have a title bar. It is an object of
 * type {@code Region}, which is assigned by calling the method
 * DockableContext.setTitleBar(javafx.scene.layout.Region) or by applying the
 * method DockableContext.createDefaultTitleBar(java.lang.String). The title bar
 * object automatically becomes a listener of mouse events by executing the code
 * below:
 * <pre>
 *   titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED,  this);
 *   titleBar.addEventHandler(MouseEvent.DRAG_DETECTED,  this);
 *   titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED,  this);
 *   titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
 * </pre> Thus, if the object of type {@code Dockable} has a title bar and it is
 * visible on screen, then it can be used to perform mouse dragging.
 * <p>
 * The object of type {@code Dockable} has a method
 * DockableContext#setDragNode(javafx.scene.Node) . The {@code Node } which has
 * been set by the method may be used to drag the {@literal dockable} in the
 * same manner as the title bar is used. Thus, both objects, such as a title bar
 * and a drag node can be used to perform dragging.
 *
 * </p>
 *
 * @author Valery Shyshkin
 */
public class SimpleDragManager implements DragManager, EventHandler<MouseEvent> {

    private Window floatingWindow;
    /**
     * The object to be dragged
     */
    private final Dockable dockable;

    private Node dragSource;
    /**
     * Pop up window which provides indicators to choose a place of the target
     * object
     */
    private IndicatorManager indicatorManager;

    private HideOption hideOption = NONE;
    /**
     * The target dock target
     */
    private Parent targetDockPane;
    /**
     * The floatingWindow that contains the target dock target
     */
    private Window resultStage;
    /**
     * The mouse screen coordinates assigned by the mousePressed method.
     */
    private Point2D startMousePos;

    /**
     * Create a new instance for the given dock node.
     *
     * @param dockNode the object to be dragged
     */
    public SimpleDragManager(Dockable dockNode) {
        this.dockable = dockNode;
    }

    protected Window getFloatingWindow() {
        return floatingWindow;
    }

/*    protected void setFloatingWindow(Window floatingWindow) {
        this.floatingWindow = floatingWindow;
    }
*/
    @Override
    public DragType getDragType() {
        return DragType.SIMPLE;
    }

    public HideOption getHideOption() {
        return hideOption;
    }

    public void setHideOption(HideOption hideOption) {
        this.hideOption = hideOption;
    }

    @Override
    public void mouseDragDetected(MouseEvent ev, Point2D startMousePos) {
        this.startMousePos = startMousePos;
        this.dragSource = (Node) ev.getSource();

        if (!dockable.getDockableContext().isFloating()) {
            System.err.println("drag detected not floating");
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
//            System.err.println("=== SCENE " + dockable.node().getScene());
//            if (dockable.node().getScene().getWindow() != null ) 
//                System.err.println("=== SCENE.WINDOW " + dockable.node().getScene().getWindow());
            FloatViewFactory f = null;
            if (getTargetContext(getDockable()) != null) {
                f = getTargetContext(getDockable()).getLookup().lookup(FloatViewFactory.class);
            }
            if (f == null) {
                f = getDockable().getDockableContext().getLookup().lookup(FloatViewFactory.class);
            }
            FloatView view = f.getFloatView(this);
            floatingWindow = (Window) view.make(dockable);
//            System.err.println("----------- FLOATING WINDOW = " + floatingWindow);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);

            //dockable.getDockableContext().setFloating(true);
            Dockable cd = getContainerDockable();
            if (cd != null) {
                //cd.getDockableContext().setFloating(true);
            }

        } else {
            System.err.println("drag detected if floating");

            //
            // If floating window contains snapshot and not the dockable then
            // the folowing two operator must be skipped
            //
            ((Node) ev.getSource()).addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            ((Node) ev.getSource()).addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }

    }
    protected TargetContext getTargetContext(Dockable d) {
        return d.getDockableContext().getTargetContext();
    }

    /**
     * The method is called when the user moves the mouse and the primary mouse
     * button is pressed. The method checks whether the {@literal  dockable} node
     * is in the {@code floating} state and if not the method returns.<P>
     * If the method encounters a {@literal dockable} node or a
     * {@code dock target target} then it shows a pop up window which contains
     * indicators to select a dock place on the target dock node or target.
     * <p>
     * The method checks whether the {@code control key} of the keyboard is
     * pressed and if so then it shows a special indicator window which allows
     * to select a dock target or one of it's parents.
     *
     * @param ev the event that describes the mouse events
     */
    protected void mouseDragged(MouseEvent ev) {
        //System.err.println("DRAGGED");
        if (indicatorManager != null && !(indicatorManager instanceof Window)) {
            indicatorManager.hide();
        }
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!dockable.getDockableContext().isFloating()) {
            return;
        }
        //
        // The floatingWindow where the floating dockable resides may have a root node as a Borderpane
        //
        double leftDelta = 0;
        double topDelta = 0;

        if (getFloatingWindowRoot() instanceof BorderPane) {
            Insets insets = ((BorderPane) getFloatingWindowRoot()).getInsets();

            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }
//        System.err.println("!!! floatingWindow = " + floatingWindow);
//        System.err.println("!!! startMousePos = " + startMousePos);
        if (floatingWindow instanceof PopupControl) {
            ((PopupControl) floatingWindow).setAnchorX(ev.getScreenX() - leftDelta - startMousePos.getX());
            ((PopupControl) floatingWindow).setAnchorY(ev.getScreenY() - topDelta - startMousePos.getY());
            //((PopupControl) floatingWindow).setAnchorX(ev.getScreenX() - leftDelta);
            //((PopupControl) floatingWindow).setAnchorY(ev.getScreenY() - topDelta);

        } else {
            floatingWindow.setX(ev.getScreenX() - leftDelta - startMousePos.getX());
            floatingWindow.setY(ev.getScreenY() - topDelta - startMousePos.getY());
            //floatingWindow.setX(ev.getScreenX() - leftDelta);
            //floatingWindow.setY(ev.getScreenY() - topDelta);
            
        }

        if (indicatorManager != null && indicatorManager.isShowing()) {
            indicatorManager.hideWhenOut(ev.getScreenX(), ev.getScreenY());
        }

        if ((indicatorManager == null || !indicatorManager.isShowing())) {
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), floatingWindow);
        }
//System.err.println("******** SimpleDragManager 1 resultStage=" + resultStage);            
        if (resultStage == null) {
            return;
        }
        Node root = resultStage.getScene().getRoot();
        if (root == null || !(root instanceof Pane) && !(DockRegistry.instanceOfDockTarget(root))) {
            return;
        }
//System.err.println("******** SimpleDragManager 2");            
        Node topPane = TopNodeHelper.getTopNode(resultStage, ev.getScreenX(), ev.getScreenY(), (n) -> {
            return DockRegistry.instanceOfDockTarget(n);
        });
//        System.err.println("******** SimpleDragManager 3 + topPane=" + topPane);
        if (topPane != null) {
            root = topPane;
        } else if (!DockRegistry.instanceOfDockTarget(root)) {
//System.err.println("******** SimpleDragManager 4");            
            return;
        }
        if (!DockRegistry.dockTarget(root).getTargetContext().isAcceptable(getDockable())) {
            return;
        }

        if (!DockRegistry.dockTarget(root).getTargetContext().isUsedAsDockTarget()) {
            return;
        }
        //
        // Start use of IndicatorPopup
        //
        IndicatorManager newPopup = DockRegistry.dockTarget(root).getTargetContext().getLookup().lookup(IndicatorManager.class);
        if (newPopup == null) {
            DockRegistry.dockTarget(root).getTargetContext().getLookup().lookup(IndicatorManager.class);
        }
        if (newPopup == null) {
            return;
        }

        newPopup.setDraggedNode(getDockable().node());

        if (indicatorManager != newPopup && indicatorManager != null) {
            indicatorManager.hide();
        }
        indicatorManager = newPopup;

        if (!indicatorManager.isShowing()) {
            indicatorManager.showIndicator();
            indicatorManager.showIndicator(ev.getScreenX(), ev.getScreenY());
        }
        if (indicatorManager == null) {
            return;
        }
        indicatorManager.handle(ev.getScreenX(), ev.getScreenY());
    }

    /**
     * The method is called when a user releases the mouse button.
     *
     * Depending on whether or not the target object is detected during dragging
     * the method initiates a dock operation or just returns.
     *
     * @param ev the event that describes the mouse events.
     */
    protected void mouseReleased(MouseEvent ev) {
        System.err.println("MOUSE RELEASED ");
        if (indicatorManager != null && indicatorManager.isShowing()) {
            indicatorManager.handle(ev.getScreenX(), ev.getScreenY());
        }

        if (targetDockPane != null) {
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            targetDockPane.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);

        }
        if (dragSource != null) {
            dragSource.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            dragSource.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            dragSource.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            dragSource.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
        }
        if (indicatorManager != null) {
            Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());
            TargetContext tc = indicatorManager.getTargetContext();
            if (indicatorManager.isShowing() || indicatorManager.getPositionIndicator() == null) {
                System.err.println(" ----  dock() 1");
                tc.dock(pt, dockable);
                boolean isDocked = TargetContext.isDocked(tc, dockable);
                if (isDocked && floatingWindow != null && floatingWindow.isShowing()) {
                    hideFloatingWindow();
                }
                if (isDocked) {
                    System.err.println("2 MouseReleased hideFloatingWindow dockable=" + dockable.node());                    
                    //dockable.getDockableContext().setFloating(false);
                    if (getContainerValue() != null && floatingWindow != null && getContainerDockable() != null) {
                        //getContainerDockable().getDockableContext().setFloating(false);
                    }
                }
            }
            if (indicatorManager != null && indicatorManager.isShowing()) {
                indicatorManager.hide();
            }
        }
        if ( (getHideOption() == ALL || getHideOption() == CARRIERED ) && getContainerValue() != null && floatingWindow != null) {
           hideFloatingWindow();
           //dockable.getDockableContext().setFloating(false);
        }
        if ( (getHideOption() == ALL || getHideOption() == CARRIER ) && getContainerValue() == null && floatingWindow != null) {
           hideFloatingWindow();
           //dockable.getDockableContext().setFloating(false);
        }
        
    }

    protected void hideFloatingWindow() {
        if (floatingWindow != null && (floatingWindow instanceof Stage)) {
            ((Stage) floatingWindow).close();
        } else {
            floatingWindow.hide();
        }
    }

    protected Dockable getContainerDockable() {
        Dockable retval = null;
        DragContainer dc = dockable.getDockableContext().getDragContainer();
        Object v = dc.getValue();

        if (v != null && (dc.isValueDockable())) {
            retval = Dockable.of(v);
        }
        return retval;
    }

    protected Object getContainerValue() {
        Object retval = null;
        DragContainer dc = dockable.getDockableContext().getDragContainer();
        Object v = dc.getValue();

        if (v != null) {
            retval = v;
        }
        return retval;
    }

    @Override
    public Dockable getDockable() {
        return dockable;
    }

    protected Node getFloatingWindowRoot() {
        //System.err.println("++++ FLOATING WINDOW = " + floatingWindow);
        return floatingWindow.getScene().getRoot();
    }

    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            mouseDragged(ev);
        } else if (ev.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            mouseReleased(ev);
        }
    }

    public Node getDragSource() {
        return dragSource;
    }

    public IndicatorManager getIndicatorManager() {
        return indicatorManager;
    }

    public Parent getTargetDockPane() {
        return targetDockPane;
    }

    public Window getResultStage() {
        return resultStage;
    }

    public Point2D getStartMousePos() {
        return startMousePos;
    }

    public void setTargetDockPane(Parent targetDockPane) {
        this.targetDockPane = targetDockPane;
    }

    public void setResultStage(Window resultStage) {
        this.resultStage = resultStage;
    }

    public void setStartMousePos(Point2D startMousePos) {
        this.startMousePos = startMousePos;
    }

    public void setIndicatorManager(IndicatorManager indicatorManager) {
        this.indicatorManager = indicatorManager;
    }

    public void setDragSource(Node dragSource) {
        this.dragSource = dragSource;
    }

}
