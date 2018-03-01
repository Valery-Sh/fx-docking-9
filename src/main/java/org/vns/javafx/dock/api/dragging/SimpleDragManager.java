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

import com.sun.javafx.stage.WindowHelper;
import javafx.application.Platform;
import org.vns.javafx.dock.api.dragging.view.FloatViewFactory;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.TopNodeHelper;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.ALL;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.CARRIER;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.CARRIERED;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.NONE;
import org.vns.javafx.dock.api.dragging.view.FloatView;
import org.vns.javafx.dock.api.indicator.IndicatorManager;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;

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
     * Pop up window which provides indicators to choose a place of the layoutNode
 object
     */
    private IndicatorManager indicatorManager;

    private HideOption hideOption = NONE;
    /**
     * The layoutNode dock layoutNode
     */
    private Parent targetDockPane;
    /**
     * The floatingWindow that contains the layoutNode dock layoutNode
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
        setStartMousePos(startMousePos);
        this.dragSource = (Node) ev.getSource();
        if (!dockable.getContext().isFloating()) {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            FloatViewFactory f = null;
            if (getTargetContext(getDockable()) != null) {
                f = getTargetContext(getDockable()).getLookup().lookup(FloatViewFactory.class);
            }
            if (f == null) {
                f = getDockable().getContext().getLookup().lookup(FloatViewFactory.class);
            }
            FloatView view = f.getFloatView(this);
            floatingWindow = (Window) view.make(dockable);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        } else {
            if (floatingWindow == null) {
                //
                // floatingWindow is null if the dragMaager changed
                //
                floatingWindow = dockable.node().getScene().getWindow();
            }
            //
            // If floating window contains snapshot and not the dockable then
            // the folowing two operator must be skipped
            //
            ((Node) ev.getSource()).addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            ((Node) ev.getSource()).addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }

    }

    protected LayoutContext getTargetContext(Dockable d) {
        return d.getContext().getLayoutContext();
    }

    /**
     * The method is called when the user moves the mouse and the primary mouse
     * button is pressed. The method checks whether the {@literal  dockable} node
     * is in the {@code floating} state and if not the method returns.<P>
     * If the method encounters a {@literal dockable} node or a
     * {@code dock layoutNode layoutNode} then it shows a pop up window which contains
 indicators to select a dock place on the layoutNode dock node or layoutNode.
 <p>
     * The method checks whether the {@code control key} of the keyboard is
 pressed and if so then it shows a special indicator window which allows
 to select a dock layoutNode or one of it's parents.
     *
     * @param ev the event that describes the mouse events
     */
    protected void mouseDragged(MouseEvent ev) {
        
        if (indicatorManager != null && !(indicatorManager instanceof Window)) {
            indicatorManager.hide();
        }
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!dockable.getContext().isFloating()) {
            return;
        }
        //
        // The floatingWindow where the floating dockable resides may have a root node as a Borderpane
        //
        double leftDelta = 0;
        double topDelta = 0;

        if (getFloatingWindowRoot() instanceof Pane) {
            Insets insets = ((Pane) getFloatingWindowRoot()).getInsets();

            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }
        if (floatingWindow instanceof PopupControl) {
            ((PopupControl) floatingWindow).setAnchorX(ev.getScreenX() - leftDelta - getStartMousePos().getX());
            ((PopupControl) floatingWindow).setAnchorY(ev.getScreenY() - topDelta - getStartMousePos().getY());
        } else {
            floatingWindow.setX(ev.getScreenX() - leftDelta - getStartMousePos().getX());
            floatingWindow.setY(ev.getScreenY() - topDelta - getStartMousePos().getY());
        }

        if (indicatorManager != null && indicatorManager.isShowing()) {
            indicatorManager.hideWhenOut(ev.getScreenX(), ev.getScreenY());
        }

        if ((indicatorManager == null || !indicatorManager.isShowing())) {
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), floatingWindow);
        }
        if (resultStage == null) {
            return;
        }
        Node root = resultStage.getScene().getRoot();
        if (root == null || !(root instanceof Pane) && !(DockRegistry.instanceOfDockLayout(root))) {
            return;
        }
        Node topPane = TopNodeHelper.getTopNode(resultStage, ev.getScreenX(), ev.getScreenY(), (n) -> {
            return DockRegistry.instanceOfDockLayout(n);
        });

        if (topPane != null) {
            root = topPane;
        } else if (!DockRegistry.instanceOfDockLayout(root)) {
            return;
        }
        LayoutContext tc = DockRegistry.dockLayout(root).getLayoutContext();
        
        tc.mouseDragged(dockable, ev);
        Object o = getDockable().getContext().getDragValue();
        Node node = null;
        if (DockRegistry.isDockable(o)) {
            node = Dockable.of(o).node();
        }
        boolean accept = node != tc.getLayoutNode();
        if (accept && !DockRegistry.dockLayout(root).getLayoutContext().isAcceptable(getDockable())) {
            return;
        }
        
        if ( !DockRegistry.dockLayout(root).getLayoutContext().isAdmissiblePosition(dockable,new Point2D(ev.getScreenX(), ev.getScreenY())) ) {
            return;
        }
        
        if (!DockRegistry.dockLayout(root).getLayoutContext().isUsedAsDockLayout()) {
            return;
        }
        
        //
        // Start use of IndicatorPopup
        //
        IndicatorManager newPopup = DockRegistry.dockLayout(root).getLayoutContext().getLookup().lookup(IndicatorManager.class);
        if (newPopup == null) {
            DockRegistry.dockLayout(root).getLayoutContext().getLookup().lookup(IndicatorManager.class);
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
        }
        if (indicatorManager == null) {
            return;
        }

        indicatorManager.handle(ev.getScreenX(), ev.getScreenY());
    }

    /**
     * The method is called when a user releases the mouse button.
     *
     * Depending on whether or not the layoutNode object is detected during dragging
 the method initiates a dock operation or just returns.
     *
     * @param ev the event that describes the mouse events.
     */
    protected void mouseReleased(MouseEvent ev) {
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
            LayoutContext tc = indicatorManager.getTargetContext();

            //
            // NEW!!! Dragged value cannot be the same as targetNode 
            //
            Object o = dockable.getContext().getDragValue();
            Node node = null;
            if (DockRegistry.isDockable(o)) {
                node = Dockable.of(o).node();
            }
            boolean accept = node != tc.getLayoutNode() &&  tc.isAdmissiblePosition(getDockable(), pt) ;
            if (accept && (indicatorManager.isShowing() || indicatorManager.getPositionIndicator() == null)) {
                tc.dock(pt, dockable);
                boolean isDocked = LayoutContext.isDocked(tc, dockable);
                if (isDocked && floatingWindow != null && floatingWindow.isShowing()) {
                    hideFloatingWindow();
                }
            }
            if (indicatorManager != null && indicatorManager.isShowing()) {
                indicatorManager.hide();
            }
        }
        if ((getHideOption() == ALL || getHideOption() == CARRIERED) && getContainerValue() != null && floatingWindow != null) {
            hideFloatingWindow();
        }
        if ((getHideOption() == ALL || getHideOption() == CARRIER) && getContainerValue() == null && floatingWindow != null) {
            hideFloatingWindow();
        }

        DragContainer dc = getDockable().getContext().getDragContainer();

        if (dc != null && dc.getPlaceholder() != null) {
            getDockable().getContext().setDragContainer(null);
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
        DragContainer dc = dockable.getContext().getDragContainer();
        Object v = dc.getValue();

        if (v != null && (dc.isValueDockable())) {
            retval = Dockable.of(v);
        }
        return retval;
    }

    protected Object getContainerValue() {
        DragContainer dc = dockable.getContext().getDragContainer();
        Object retval = dc == null ? null : dc.getValue();
        return retval;
    }

    @Override
    public Dockable getDockable() {
        return dockable;
    }

    protected Node getFloatingWindowRoot() {
        return floatingWindow.getScene().getRoot();
    }

    @Override
    public void handle(MouseEvent ev) {
        ev.consume();
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
