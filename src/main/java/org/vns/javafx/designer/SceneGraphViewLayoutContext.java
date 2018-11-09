/*
 * Copyright 2018 Your Organisation.
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
package org.vns.javafx.designer;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.ContextLookup;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

/**
 *
 * @author Valery Shyshkin
 */
public class SceneGraphViewLayoutContext extends LayoutContext {

    private final ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();
    private final ObjectProperty acceptedValue = new SimpleObjectProperty<>();

    public SceneGraphViewLayoutContext(Node targetNode) {
        super(targetNode);
    }

    /*    public SceneGraphViewTargetContext(Dockable dockable) {
        super(dockable);
    }
     */
    @Override
    protected void initLookup(ContextLookup lookup) {
        //lookup.putUnique(DragManagerFactory.class, new TreeItemDragManagerFactory());
        //lookup.putUnique(FloatViewFactory.class, new TreeItemFloatViewFactory());

    }

    public ObjectProperty acceptedValue() {
        return acceptedValue;
    }

    public Object getAcceptedValue() {
        return acceptedValue.get();
    }

    public void setAcceptedValue(Object obj) {
        acceptedValue.set(obj);
    }

    public ObjectProperty<Point2D> mousePositionProperty() {
        return mousePosition;
    }

    public Point2D getMousePosition() {
        return mousePosition.get();
    }

    public void setMousePosition(Point2D pos) {
        this.mousePosition.set(pos);
    }

    @Override
    public void remove(Object obj) {
        System.err.println("1. SceneGraphViewLayoutContext: remove obj = " + obj);
        TreeItemEx item = EditorUtil.findTreeItemByObject(getTreeView(), obj);
        if (item != null) {
            System.err.println("2. SceneGraphViewLayoutContext: remove obj = " + obj);
            new TreeItemBuilder().updateOnMove(item);
        }
    }

    /**
     * To prevent setting of this LayoutContext to object dockable context.
     *
     * @param obj the docked object
     */
    @Override
    public void commitDock(Object obj) {

    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
//        System.err.println("DOCK: dockable.node() = " + dockable.node());
        Dockable d = dockable;
        Window window = null;
//        System.err.println("1. dock() dockable = " + dockable.node());
        DragContainer dc = dockable.getContext().getDragContainer();
//        System.err.println("2. dock() dc = " + dc);

        if (dc != null && dc.getValue() != null) {
            window = dc.getFloatingWindow(dockable);
            d = Dockable.of(dc.getValue());
        }
//        System.err.println("3. dock() dockable = " + dockable.node());
        Node node = null;
        if (d != null) {
            node = d.node();
        }

        if (window == null && node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
            window = node.getScene().getWindow();
        }
        if (window == null && dockable.node() != node) {
            node = dockable.node();
            if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
                window = node.getScene().getWindow();
            }
        }
        Object value = getValue(dockable);
        Object toAccept = value;
        if (value instanceof Dockable) {
            toAccept = ((Dockable) value).node();
        }
//        System.err.println("4. dock() dockable = " + dockable.node());
        boolean accepted = acceptValue(mousePos, toAccept);
//        System.err.println("accepted = " + accepted);
        //SaveRestore sr = DockRegistry.lookup(SaveRestore.class);

        //if (accepted && sr != null) {
        //sr.restoreExpanded(toAccept);
        //}
        if (accepted && window != null) {
            if ((window instanceof Stage)) {
                ((Stage) window).close();
            } else {
                window.hide();
            }
        }
        NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
        if (nf != null) {

            //
            // We apply Platform.runLater because a list do not 
            // has to be a children but for instance for SplitPane it
            // is an items and an added node may be not set into scene graph
            // immeduately
            //
            if (!(toAccept instanceof Node)) {
                return;
            }
            final Node av = (Node) toAccept;
            Platform.runLater(() -> {
                //nf.show(av);
            });
        }
    }

    /**
     * The method is called by the object {@code DragManager } when the mouse
     * event of type {@code MOUSE_DRAGGED} is handled. The class uses the method
     * when implements animation for the tree view scroll bar. Sets the new
     * mouse position of the (@code mousePosition } property.
     *
     * @param dockable the dragged object
     * @param ev the object of type {@code MouseEvent }
     * @see #mousePositionProperty()
     */
    @Override
    public void mouseDragged(Dockable dockable, MouseEvent ev) {
        setMousePosition(new Point2D(ev.getScreenX(), ev.getScreenY()));
    }

    /**
     * Checks whether the given {@code dockable} can be accepted by this
     * context.
     *
     * @param dockable the object to be checked
     * @param mousePos the current mouse position
     * @return true if the {@code dockable} can be accepted
     */
    @Override
    public boolean isAdmissiblePosition(Dockable dockable, Point2D mousePos) {

        SceneGraphView gv = (SceneGraphView) getLayoutNode();
        if (gv.getTreeView(mousePos.getX(), mousePos.getY()) != null) {
            if (gv.getTreeView().getRoot() == null) {
                return true;
            }
        }
        TreeItemEx place = gv.getTreeItem(mousePos);

        if (place == null) {
            return false;
        }

        TreeItemEx target = getDragIndicator().getTargetTreeItem(mousePos.getX(), mousePos.getY(), place);

        if (target == null) {
            return false;
        }

        Object value = getValue(dockable);

        if (value == null) {
            return false;
        }

        if (value instanceof Dockable) {
            value = ((Dockable) value).node();
        }
//        System.err.println("SceneGraphViewTargetContext target = " + target + "; value = " + value);
        return new TreeItemBuilder().isAdmissiblePosition(gv.getTreeView(), target, place, value);

    }

    protected TreeViewEx getTreeView() {
        return ((SceneGraphView) getLayoutNode()).getTreeView();
    }

    protected DragIndicatorManager getDragIndicatorManager() {
        return (DragIndicatorManager) getLookup().lookup(IndicatorManager.class);
    }

    protected DragIndicator getDragIndicator() {
        return getDragIndicatorManager().getDragIndicator();
    }

    @Override
    public boolean isAcceptable(Dockable dockable) {
        return true;
    }

    protected boolean acceptValue(Point2D mousePos, Object value) {
        System.err.println("this.getClass = " + this.getClass());
        SceneGraphView gv = (SceneGraphView) getLayoutNode();
        boolean retval = false;
        if (gv.getTreeView().getRoot() == null) {
            gv.setRoot((Node) value);
            return true;
        }
        TreeItemEx place = gv.getTreeItem(mousePos);
        System.err.println("PLACE mousePos = " + mousePos);
        if (place != null) {
            TreeItemEx target = getDragIndicator().getTargetTreeItem(mousePos.getX(), mousePos.getY(), place);
            if (target != null) {
                new TreeItemBuilder().accept(gv.getTreeView(), target, place, value);
                retval = true;
            }
        }
        return retval;
    }

    @Override
    public boolean contains(Object obj) {
        System.err.println("contains");
        SceneGraphView gv = (SceneGraphView) getLayoutNode();        
        if ( gv.getTreeView().getRoot() != null && gv.getTreeView().getRoot().getValue() == obj  ) {
            return true;
        }
        return false;
    }

}//SceneGraphViewLayoutContext
