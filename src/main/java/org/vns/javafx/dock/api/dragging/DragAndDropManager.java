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

import java.util.List;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.TopNodeHelper;

/**
 *
 * @author Valery
 */
public class DragAndDropManager implements DragManager, EventHandler<DragEvent> {

    private Object docablePositionSave;

    PopupControl PC;
    Point2D dragOverPos;
    private final Dockable dockable;

    private final TargetContext targetContext;
    /**
     * The target dock target
     */
    private Parent targetDockPane;

    private Node dragSource;
    /**
     * Pop up window which provides indicators to choose a place of the target
     * object
     */
    private IndicatorPopup popup;

    //private Popup popupDelegate;
    /**
     * The target dock target
     */
    /**
     * The floatWindow that contains the target dock target
     */
    private Window resultStage;
    /**
     * The mouse screen coordinates assigned by the mousePressed method.
     */
    private Point2D startMousePos;

    /**
     * The property that defines a node that can be used to start dragging.
     *
     * @param dockable the object
     */
    public DragAndDropManager(Dockable dockable) {
        this.dockable = dockable;
        this.targetContext = dockable.getDockableContext().getTargetContext();
    }

    @Override
    public DragType getDragType() {
        return DragType.DRAG_AND_DROP;
    }

    @Override
    public Dockable getDockable() {
        return dockable;
    }

    /*    @Override
    public void dragDetected(MouseEvent ev, Point2D startMousePos) {
        System.err.println("DRAG MANGER " + this);
        System.err.println("dragDetected getTargetContext " + dockable.getDockableContext().getTargetContext());
        dragSource = (Node) ev.getSource();
        this.startMousePos = startMousePos;

        Node root = dragSource.getScene().getRoot();
        root.addEventHandler(DragEvent.ANY, new DragAndDropHandler(this));
        System.err.println("   --- DRAG MANGER dragSource=" + dragSource);
        //Dragboard dragboard = dragSource.startDragAndDrop(TransferMode.COPY_OR_MOVE);
        Dragboard dragboard = root.startDragAndDrop(TransferMode.COPY_OR_MOVE);
        DragGesture dg = new DragNodeGesture(dockable.node());
//        treeView.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
        ClipboardContent content = new ClipboardContent();
        content.putUrl("fxdocking//test");
        dragboard.setContent(content);

        if (!dockable.getDockableContext().isFloating()) {
            System.err.println("getDockableContext().isFloating() = " + dockable.getDockableContext().isFloating());
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            //FloatView view = FloatViewFactory.getInstance().getFloatView(this);
            //view.make(dockable);
            targetDockPane.addEventFilter(DragEvent.DRAG_OVER, this);
            targetDockPane.addEventHandler(DragEvent.DRAG_DROPPED, this);
            targetDockPane.addEventFilter(DragEvent.DRAG_DONE, this);
            targetDockPane.addEventFilter(DragEvent.DRAG_EXITED, this);
            
            docablePositionSave = dockable.getDockableContext().getTargetContext().getRestorePosition(dockable);
            dockable.getDockableContext().getTargetContext().undock(dockable.node());
            dockable.getDockableContext().setFloating(true);
        }

        ev.consume();
    }
     */
    @Override
    public void mouseDragDetected(MouseEvent ev, Point2D startMousePos) {
        System.err.println("DRAG MANGER " + this);
        System.err.println("dragDetected targetController " + dockable.getDockableContext().getTargetContext());
        dragSource = (Node) ev.getSource();
        this.startMousePos = startMousePos;

        Node root = dragSource.getScene().getRoot();
        //root.addEventHandler(DragEvent.ANY, new DragAndDropHandler(this));
        //System.err.println("   --- DRAG MANGER dragSource=" + dragSource);
        //Dragboard dragboard = dragSource.startDragAndDrop(TransferMode.COPY_OR_MOVE);
        Dragboard dragboard = root.startDragAndDrop(TransferMode.COPY_OR_MOVE);
        //DragGesture dg = new DragNodeGesture(dockable.node());
//        treeView.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
        ClipboardContent content = new ClipboardContent();
        content.putUrl("fxdocking://DragAndDropManager");
        dragboard.setContent(content);

        if (!dockable.getDockableContext().isFloating()) {
            System.err.println("dockableController().isFloating() = " + dockable.getDockableContext().isFloating());
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            if ( PC == null ) {
                PC = new PopupControl();
            }
            Rectangle cbtn = new Rectangle(10, 10);
            StackPane sp = new StackPane(cbtn);
            cbtn.setStyle("-fx-border-color: red");
            PC.getScene().setRoot(sp);
            PC.show(targetDockPane, 10, 15);

            //FloatView view = FloatViewFactory.getInstance().getFloatView(this);
            //view.make(dockable);
            targetDockPane.addEventFilter(DragEvent.DRAG_OVER, this);
            targetDockPane.addEventHandler(DragEvent.DRAG_DROPPED, this);
            targetDockPane.addEventFilter(DragEvent.DRAG_DONE, this);
            targetDockPane.addEventFilter(DragEvent.DRAG_EXITED, this);

            docablePositionSave = dockable.getDockableContext().getTargetContext().getRestorePosition(dockable);
            dockable.getDockableContext().getTargetContext().undock(dockable.node());
            dockable.getDockableContext().setFloating(true);
        }

        ev.consume();
    }

    @Override
    public void handle(DragEvent ev) {
        if (ev.getEventType() == DragEvent.DRAG_OVER) {
            //if (isAdmissiblePosition(ev)) {
            ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            //System.err.println("DRAG OVER");
            //    drawIndicator(ev);
            //}
            dragOver(ev);
            ev.consume();
        } else if (ev.getEventType() == DragEvent.DRAG_DROPPED) {
            //System.err.println("DRAG DROPPED");
            //
            // Transfer the data to the place
            //
//            if (isAdmissiblePosition(ev)) {
            dragDropped(ev);
            ev.setDropCompleted(true);

//            } else {
//                ev.setDropCompleted(false);
//            }
        } else if (ev.getEventType() == DragEvent.DRAG_EXITED) {
            System.err.println("DRAG EXITED");
            dragExited(ev);
            //getEditor().getDragIndicator().hideDrawShapes();
        } else if (ev.getEventType() == DragEvent.DRAG_DONE) {
            //System.err.println("DRAG DONE");
            dragDone(ev);
            //getEditor().getDragIndicator().hideDrawShapes();
        }
        ev.consume();
    }

    protected void dragOver(DragEvent ev) {
        //System.err.println("DragManager START MOUSE POS " + startMousePos);
        //System.err.println("DragManager = " + this);
        //System.err.println("popup.getScene().getCursor() = " + popup.getScene().getCursor());
        System.err.println("targetDockPane.getScene().getCursor() = " + targetDockPane.getScene().getCursor());

        double X = ev.getScreenX();
        double Y = ev.getScreenY();
        dragOverPos = new Point2D(X, Y);
        PC.setX(X+5);
        PC.setY(Y + 15);

        if (!dockable.getDockableContext().isFloating()) {
            if (popup != null && popup.isShowing()) {
                popup.hide();
            }
            return;
        }
        //
        // The floatWindow where the floating dockable resides may have a root node as a Borderpane
        //

        /*        Window floatWindow = (Window) dockable.node().getScene().getWindow();
        double leftDelta = 0;
        double topDelta = 0;

        if (getFloatingWindowRoot() instanceof BorderPane) {
            Insets insets = ((BorderPane) getFloatingWindowRoot()).getInsets();

            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }
        if (floatWindow instanceof PopupControl) {
            ((PopupControl) floatWindow).setAnchorX(ev.getScreenX() - leftDelta - startMousePos.getX());
            ((PopupControl) floatWindow).setAnchorY(ev.getScreenY() - topDelta - startMousePos.getY());

        } else {
            floatWindow.setX(ev.getScreenX() - leftDelta - startMousePos.getX());
            floatWindow.setY(ev.getScreenY() - topDelta - startMousePos.getY());
        }
         */
 /*System.err.println("=================================");
        System.err.println("   --- startPosition x=" + startMousePos.getX() + "; y=" + startMousePos.getY());
        System.err.println("   --- mousePos      x=" + ev.getScreenX() + "; y=" + ev.getScreenY());
        System.err.println("   --- windowPos     x=" + floatWindow.getX() + "; y=" + floatWindow.getY());
        System.err.println("   --- leftDelta = " + leftDelta + "; topDelta=" + topDelta);
        System.err.println("=================================");
         */
        if (popup != null && popup.isShowing()) {
            popup.hideWhenOut(ev.getScreenX(), ev.getScreenY());
        }

        if ((popup == null || !popup.isShowing())) {
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), null);
        }

        if (resultStage == null) {
            //System.err.println("DRAGOVER 1");
            return;
        }

        Node root = resultStage.getScene().getRoot();
        if (root == null || !(root instanceof Pane) && !(DockRegistry.instanceOfDockTarget(root))) {
            return;
        }
        //System.err.println("DRAGOVER 2");
        Node topPane = TopNodeHelper.getTopNode(resultStage, ev.getScreenX(), ev.getScreenY(), (n) -> {
            return DockRegistry.instanceOfDockTarget(n);
        });
        //System.err.println("DRAGOVER 3");
        if (topPane != null) {
            root = topPane;
        } else if (!DockRegistry.instanceOfDockTarget(root)) {
            return;
        }
        if (!DockRegistry.dockTarget(root).getTargetContext().isAcceptable(dockable.node())) {
            return;
        }
        if (!DockRegistry.dockTarget(root).getTargetContext().isUsedAsDockTarget()) {
            return;
        }
        //System.err.println("DRAGOVER 4");
        //IndicatorPopup newPopup = DockRegistry.dockTarget(root).getTargetContext().getIndicatorPopup();
        IndicatorPopup newPopup =  DockRegistry.dockTarget(root).getTargetContext().getLookup().lookup(IndicatorPopup.class);        
        newPopup.getProperties().put("POPUP", "newPopup");
        //newPopup.getScene().setFill(Color.AQUA);
        //newPopup.getScene().getRoot().setOpacity(0.1);
        //System.err.println("IndicatorPopup = " + newPopup);
        //System.err.println("DRAGOVER 4.0");

        if (popup != newPopup && popup != null) {
            //System.err.println("DRAGOVER 4.1");
            popup.hide();
        }
        //System.err.println("DRAGOVER 4.2");

        if (newPopup == null) {
            //System.err.println("DRAGOVER 4.3");
            return;
        }
        //System.err.println("DRAGOVER 4.4");

        popup = newPopup;
        //setOnDragEvent(popup);
        if (!popup.isShowing()) {
            popup.showPopup();
//            setOnDragEvent(popup);
            //System.err.println("DRAGOVER 5");
        }
        setOnDragEvent(popup);
        //System.err.println("DRAGOVER 6");
        if (popup == null) {
            return;
        }
        //popup.getScene().getRoot().setMouseTransparent(true);
        //popup.getScene().getRoot().addEventFilter(DragEvent.DRAG_OVER, this);
        //System.err.println("DRAGOVER 7");
        //System.err.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
        popup.handle(ev.getScreenX(), ev.getScreenY());
    }

    protected void setOnDragEvent(IndicatorPopup popup) {
        List<IndicatorPopup> list = popup.getAllChildIndicatorPopup();
        //List<IndicatorPopup> list = popup.getChildWindows();
        //System.err.println("clist.size=" + list.size());
        list.add(0, popup);
        list.forEach(w -> {
//            System.err.println("INDICATOR POPUP w=" + w);
        });
        list.forEach(p -> {
            if (p.isShowing()) {
                p.getScene().getRoot().setOnDragOver(evo -> {
                    System.err.println("POPUP DRAG OVER popup = " + popup.getProperties().get("POPUP"));
                    //
                    // The next line of code makes correct mouce cursor 
                    //
                    evo.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    DragEvent e1 = createDragEvent(evo, targetDockPane);
                    DragEvent.fireEvent(targetDockPane, e1);
                    evo.consume();
                });
                popup.getScene().getRoot().setOnDragExited(evo -> {
                    System.err.println("POPUP DRAG Exited " + popup.getProperties().get("POPUP"));
                    //evo.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    //DragEvent e1 = createDragEvent(evo, targetDockPane);
                    //DragEvent.fireEvent(targetDockPane, e1);
                    if (popup.isShowing()) {
                        popup.hide();
                    }
                    evo.consume();
                });

                p.getScene().getRoot().setOnDragDropped(evd -> {
                    System.err.println("POPUP DRAG DROPPED");
                    //evd.setDropCompleted(true);
                    //evd.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    DragEvent e1 = createDragEvent(evd, targetDockPane);
                    DragEvent.fireEvent(targetDockPane, e1);
                    //DragEvent.fireEvent( createDragEvent(evd, ((Node)dragSource).getScene().getRoot()));
                    //((Node)dragSource).getScene().getRoot().fireEvent( createDragEvent(evd, ((Node)dragSource).getScene().getRoot()));
                    evd.consume();
                });
            }
        });
    }

    protected void dragDone(DragEvent ev) {
        System.err.println("dragDone targetController " + dockable.getDockableContext().getTargetContext());
        System.err.println("dragDone isFloating =  " + dockable.getDockableContext().isFloating());
        if (dockable.getDockableContext().isFloating() && docablePositionSave != null && targetContext != null) {
            targetContext.restore(dockable, docablePositionSave);
            //dockable.getDockableContext().setFloating(false);

            //DockPaneController dpc = (DockPaneContext) getTargetContext;
            //dpc.dock(dockable, Side.LEFT);
            //dockable.getDockableContext().setFloating(false);
        }
        if (popup != null && popup.isShowing()) {
            popup.hide();
            popup = null;
        }
        if (dragSource != null && dragSource.getScene() != null ) {
            Node root = dragSource.getScene().getRoot();
            root.removeEventHandler(DragEvent.ANY, new DragAndDropHandler(this));
            root.removeEventFilter(DragEvent.ANY, new DragAndDropHandler(this));
        }

        if (targetDockPane != null) {
            //targetDockPane.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
            targetDockPane.removeEventFilter(DragEvent.DRAG_OVER, this);
            targetDockPane.removeEventFilter(DragEvent.DRAG_DROPPED, this);
            targetDockPane.removeEventFilter(DragEvent.DRAG_DONE, this);
            targetDockPane.removeEventFilter(DragEvent.DRAG_EXITED, this);

            targetDockPane.removeEventHandler(DragEvent.DRAG_DROPPED, this);
            targetDockPane.removeEventHandler(DragEvent.DRAG_DROPPED, this);
            targetDockPane.removeEventHandler(DragEvent.DRAG_DONE, this);
            targetDockPane.removeEventHandler(DragEvent.DRAG_EXITED, this);

        }
    }

    protected void dragExited(DragEvent ev) {

        if (popup != null && popup.isShowing()) {
            //Node r = popup.getScene().getRoot(); 
/*            Node r = targetDockPane;
            Point2D p = r.screenToLocal(ev.getScreenX(), ev.getScreenY());
            p = new Point2D(ev.getScreenX(), ev.getScreenY());
            p = new Point2D(ev.getX(), ev.getY());

            //System.err.println("RRRRR p = " + p);
            Bounds b = r.localToScreen(r.getBoundsInParent());
            System.err.println("RRRRR r.bounds = " + r.localToScreen(r.getBoundsInParent()));

            if (b.contains(p)) {
                //System.err.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
                return;
            }
             */
            Node r = targetDockPane;//.getScene().getRoot();//popup.getScene().getRoot();
            Platform.runLater(() -> {
                Bounds pb = r.localToScreen(r.getBoundsInLocal());
                Bounds pb1 = new BoundingBox(pb.getMinX() + 5, pb.getMinY() + 5, pb.getWidth() - 10, pb.getHeight() - 10);
                if (!pb1.contains(dragOverPos)) {
                    popup.hide();
                }
                System.err.println("dragOverPos" + dragOverPos);
                System.err.println("BOUNDS = " + pb1);
            });
//            Bounds pb = r.localToScreen(r.getBoundsInLocal());
//            Bounds pb1 = new BoundingBox(pb.getMinX() + 5, pb.getMinY() + 5, pb.getWidth() - 10, pb.getHeight() - 10);
            //if ( ! pb1.contains(PC.getX(),PC.getY())) {
//            System.err.println("popup.getScene().getCursor() = " + popup.getScene().getCursor());
//            System.err.println("targetDockPane.getScene().getCursor() = " + targetDockPane.getScene().getCursor());

            //popup = null;
        }
    }

    protected void dragDropped(DragEvent ev) {
        System.err.println("dragDropped 1");
        if (popup != null && popup.isShowing()) {
            System.err.println("dragDropped 2");
            popup.handle(ev.getScreenX(), ev.getScreenY());
        }

        /*        if (targetDockPane != null) {
            //targetDockPane.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            targetDockPane.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);

        }
         */
        if (dragSource != null) {
            //targetDockPane.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
/*            dragSource.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            dragSource.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            dragSource.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            dragSource.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
             */
        }
        System.err.println("dragDropped 3");
        Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());
        if (popup != null && popup.isShowing()) {
            System.err.println("dragDropped 4");
            popup.getTargetContext().dock(pt, dockable);
        } else if (popup != null && popup.getPositionIndicator() == null) {
            //
            // We use default indicatorPopup without position indicator
            //
            System.err.println("dragDropped 5");
            popup.getTargetContext().dock(pt, dockable);
        }
        System.err.println("dragDropped 6");
        if (popup != null && popup.isShowing()) {
            System.err.println("dragDropped 7");
            popup.hide();
        }
        ev.setDropCompleted(true);
        ev.consume();
    }

    protected DragEvent createDragEvent(DragEvent ev, Node target) {
        Object gestureSource = targetDockPane;
        Object gestureTarget = target;
//        System.err.println("gesttureSource = " + gestureSource);
//        System.err.println("gestturetarget = " + gestureTarget);
        return createDragEvent(ev, ev.getEventType(), gestureSource, gestureTarget);
    }

    protected DragEvent createDragEvent(DragEvent ev, EventType eventType, Object gestureSource, Object gestureTarget) {
        DragEvent retval = new DragEvent(
                eventType,
                null,
                ev.getSceneX(),
                ev.getSceneY(),
                ev.getScreenX(),
                ev.getScreenY(),
                TransferMode.MOVE,
                gestureSource,
                gestureTarget,
                null);
        return retval;

    }

}
