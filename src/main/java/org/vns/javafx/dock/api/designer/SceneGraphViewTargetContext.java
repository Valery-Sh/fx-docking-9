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
package org.vns.javafx.dock.api.designer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.ContextLookup;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.ScenePaneContext;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.dragging.DragManagerFactory;
import org.vns.javafx.dock.api.dragging.view.FloatViewFactory;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

/**
 *
 * @author Valery Shyshkin
 */
public class SceneGraphViewTargetContext extends TargetContext {

    private ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();

    public SceneGraphViewTargetContext(Node targetNode) {
        super(targetNode);
    }

    public SceneGraphViewTargetContext(Dockable dockable) {
        super(dockable);
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        lookup.putUnique(DragManagerFactory.class, new TreeItemDragManagerFactory());
        lookup.putUnique(FloatViewFactory.class, new TreeItemFloatViewFactory());

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
    public boolean isDocked(Node node) {
        return false;
    }

    @Override
    public void removeValue(Dockable dockable) {
        Object value = dockable.getContext().getDragContainer().getValue();
        new TreeItemBuilder().removeByItemValue(getTreeView(), value);
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
        System.err.println("DOCK: dockable.node() = " + dockable.node());
        Dockable d = dockable;
        Window stage = null;
        DragContainer dc = dockable.getContext().getDragContainer();
        if (dc != null && dc.getValue() != null) {
            stage = dc.getFloatingWindow(dockable);
            d = Dockable.of(dc.getValue());
        }

        Node node = null;
        if (d != null) {
            node = d.node();
        }

        if (stage == null && node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }
        if (stage == null && dockable.node() != node) {
            node = dockable.node();
            if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
                stage = node.getScene().getWindow();
            }
        }
        Object value = getValue(dockable);
        Object toAccept = value;
        if (value instanceof Dockable) {
            toAccept = ((Dockable) value).node();
        }
        boolean accepted = acceptValue(mousePos, toAccept);

        if (accepted && d != null) {
            if (d.node().getScene() != null && d.node().getScene().getWindow() != null) {

            }
            //d.getContext().setTargetContext(this);
        }
        if (accepted && stage != null) {
            if ((stage instanceof Stage)) {
                ((Stage) stage).close();
            } else {
                stage.hide();
            }
        }
        if (Dockable.of(toAccept) == null) {
            return;
        }
        System.err.println("toAccept: isDockable = " + toAccept);
        d = Dockable.of(toAccept);

        //
        // Try to find the first Parent which is a DockTarget
        //
        Parent p = d.node().getParent();

        TargetContext tc = null;
        if (p instanceof Pane) {
            tc = new ScenePaneContext(d);
        }
        while (p != null) {
            if (DockTarget.of(p) != null) {
                if (TargetContext.isDocked(DockTarget.of(p).getTargetContext(), d)) {
                    tc = DockTarget.of(p).getTargetContext();
                }
                break;
            }
            p = p.getParent();
        }
        d.getContext().setTargetContext(tc);
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
     * Checks whether the given {@code dockable}  can be accepted by this context.
     * 
     * @param dockable the object to be checked
     * @param mousePos the current mouse position
     * @return true if the {@code dockable} can be accepted
     */
    @Override
    public boolean isAdmissiblePosition(Dockable dockable, Point2D mousePos) {

        SceneGraphView gv = (SceneGraphView) getTargetNode();
        TreeItemEx place = gv.getTreeItem(mousePos);
//        System.err.println("doDock node = " + dockable.node());
//        System.err.println("isAdmiss place = " + place);
//        System.err.println("doDock place.value = " + place.getValue());

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
        //System.err.println("1 isAdmiss value = " + value);
        return new TreeItemBuilder().isAdmissiblePosition(gv.getTreeView(), target, place, value);

    }

    protected TreeViewEx getTreeView() {
        return ((SceneGraphView) getTargetNode()).getTreeView();
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
        SceneGraphView gv = (SceneGraphView) getTargetNode();
//        System.err.println("DO DOCK");
        boolean retval = false;

        TreeItemEx place = gv.getTreeItem(mousePos);
//        System.err.println("doDock node = " + node);
//        System.err.println("doDock place = " + place);
//        System.err.println("doDock place.value = " + place.getValue());

        if (place != null) {
            TreeItemEx target = getDragIndicator().getTargetTreeItem(mousePos.getX(), mousePos.getY(), place);
            if (target != null) {
//                System.err.println("doDock target = " + target);
                new TreeItemBuilder().accept(gv.getTreeView(), target, place, value);

//                System.err.println("accept target = " + target.getValue());
//                System.err.println("accept place = " + place.getValue());
                retval = true;
            }
        }
        return retval;
    }

    @Override
    public Object getRestorePosition(Dockable dockable
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restore(Dockable dockable, Object restoreposition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove(Node dockNode) {
        TreeItemEx item = EditorUtil.findTreeItemByObject(getTreeView(), dockNode);
        if (item != null) {
            new TreeItemBuilder().updateOnMove(item);
        }

    }

    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
