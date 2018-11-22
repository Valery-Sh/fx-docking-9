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

import java.util.function.BiPredicate;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.dragging.DragManager;

public class SceneEventDispatcher implements PalettePane.PaletteEventDispatcher {

    private EventDispatcher initial;
    private Node target;
    private Node node;
    private BiPredicate<Event, Node> preventCondition;
    private Scene scene;

    boolean pressed;
    boolean dragDetected;
    boolean dragged;
    boolean released;

    public SceneEventDispatcher() {
        this(null);
    }

    public SceneEventDispatcher(BiPredicate<Event, Node> cond) {
        this.scene = scene;
        preventCondition = cond;
        init();
    }

    private void init() {
        released = true;
    }

    @Override
    public void start(Node node) {
        this.target = node;
        initial = node.getEventDispatcher();
        node.setEventDispatcher(this);
    }

    public void start(Scene scene) {
        this.scene = scene;
        initial = scene.getEventDispatcher();
        scene.setEventDispatcher(this);
    }

    @Override
    public BiPredicate<Event, Node> getPreventCondition() {
        return preventCondition;
    }

    @Override
    public void setPreventCondition(BiPredicate<Event, Node> preventCondition) {
        this.preventCondition = preventCondition;
    }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        //if ( EventType. )
        if (!(event instanceof MouseEvent)) {
            return null;
            //return initial.dispatchEvent(event, tail);
        }
        //
        // not primary button
        //

        MouseEvent mouseEvent = (MouseEvent) event;
        //System.err.println("1 Scene dispatch ev " + mouseEvent.getEventType());
        if (mouseEvent.getEventType() != MouseEvent.MOUSE_RELEASED && !mouseEvent.isPrimaryButtonDown()) {
            return initial.dispatchEvent(event, tail);
        }
        if ( SceneView.isFrameShape(event.getTarget()) ) {
            return initial.dispatchEvent(event, tail);
        }
        //System.err.println("2 Scene dispatch ev " + mouseEvent.getEventType());

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
            return pressed(event, tail);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
            return released(event, tail);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            return clicked(event, tail);
        } else if (mouseEvent.getEventType() == MouseEvent.DRAG_DETECTED) {
            return dragDetected(event, tail);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            return dragged(event, tail);
        } else {
            return null;
        }

        //return initial.dispatchEvent(event, tail);
    }

    protected boolean acceptable(Event event) {
        boolean retval = false;
        // if ((event instanceof MouseEvent) && event.getSource() == node) {
        //     retval = true;
        // }
        Node parent = null;
        if (event.getTarget() == target) {
            parent = target;
        } else if (Dockable.of(event.getTarget()) == null) {
            parent = getDockableParent((Node) event.getTarget());
        }
        if (parent == target) {
            retval = true;
        }
        return retval;
    }

    public Node getDockableParent(Node startNode) {
        //System.err.println("getDockableParent tar = " + startNode);
        if (Dockable.of(startNode) != null) {
            return startNode;
        }
        Parent p = startNode.getParent();
        //System.err.println("getDockableParent p = " + p);
        while (p != null && p != this.target && Dockable.of(p) == null) {
            p = p.getParent();
        }
        //System.err.println("getDockableParent retval = " + p);
        return p;
    }

    protected Event pressed(Event event, EventDispatchChain tail) {
//        System.err.println("SCENE PRESSED TARGET = " + event.getTarget());
//        System.err.println("Scene pressed node = " + node);
/*        System.err.println("Scene pressed target = " + target);
        System.err.println("Scene pressed ev.source = " + event.getSource());
        System.err.println("Scene pressed ev.target = " + event.getTarget());
         */
//        System.err.println("DRAGGED event WINDOW " + ((Node) event.getTarget()).getScene().getWindow());
//        if (target != null) {
//            System.err.println("DRAGGED  WINDOW " + target.getScene().getWindow());
//        }
        if (target != null) {
            return null;
        }

        target = (Node) event.getTarget();
        node = getDockableParent(target);
//        System.err.println("PRESSED node = " + node);
        Selection.SelectionListener l = DockRegistry.lookup(Selection.SelectionListener.class);
        if (l != null) { //&& (event.getTarget() instanceof Node)) {
            //l.handle((MouseEvent) event, (Node) event.getTarget());
            l.handle((MouseEvent) event, node);
        }
        if (Dockable.of(node) != null) {
            
//            System.err.println("SceneDispatcher mousePressed node " + node);
//            System.err.println("SceneDispatcher mousePressed source " + event.getSource());
//        System.err.println("DefaultDragHandler mousePressed target " + ev.getTarget());
//            System.err.println("SceneDispatcher mousePressed x = " + ((MouseEvent)event).getX() + "; y = " + ((MouseEvent)event).getY());
            MouseEvent copy = (MouseEvent) event.copyFor(node, node);
            Dockable.of(node).getContext().getDragDetector().handle(copy);
//             System.err.println("SceneDispatcher copy mousePressed x = " + copy.getX() + "; y = " + copy.getY());
            double x = node.getBoundsInParent().getMinX();
            double y = node.getBoundsInParent().getMinY();
            //Point2D point = new Point2D( ((MouseEvent)event).getX(), ((MouseEvent)event).getY());
            Point2D point = new Point2D(x, y);
//            System.err.println("SceneDispatcher mousePressed Point2D " + point);
            //Dockable.of(node).getContext().getDragDetector().getDragHandler().setStartMousePos(point);
            //event.consume();

        }
        released = false;
        pressed = true;
        return null;

        //return initial.dispatchEvent(event, tail);
    }

    protected Event released(Event event, EventDispatchChain tail) {
//        System.err.println("dragget Scene RELEASED ");
        if (Dockable.of(node) != null) {
            DragManager dm = Dockable.of(node).getContext().getDragDetector().getDragHandler().getDragManager();
//            System.err.println("dragget Scene released  1 ");
            if (dm != null || (dm instanceof EventHandler)) {
                ((javafx.event.EventHandler) dm).handle(event);
            }
            Dockable.of(node).getContext().getDragDetector().getDragHandler().handle((MouseEvent) event);
        }

        node = null;
        target = null;
        pressed = false;
        released = true;
        dragged = false;
        dragDetected = false;
        return null;

    }

    protected Event clicked(Event event, EventDispatchChain tail) {
        /*        System.err.println("CLICKED");
        System.err.println("Scene CLICKED node = " + node);
        System.err.println("Scene CLICKED target = " + target);
        System.err.println("Scene CLICKED ev.source = " + event.getSource());
        System.err.println("Scene CLICKED ev.target = " + event.getTarget());
         */
        return pressed(event, tail);
    }

    protected Event dragDetected(Event event, EventDispatchChain tail) {
//        System.err.println("dragDetected dispatch target " + event.getTarget());
        /*            System.err.println("dragDetected dispatch node " + node);
            System.err.println("dragDetected dispatch source " + event.getSource());
            System.err.println("dragDetected dispatch target " + event.getTarget());
            System.err.println("dragDetected dispatch isConsumed " + event.isConsumed());
            System.err.println("-----------");
         */
        if (dragDetected) {
            return null;
        }
        if (target != event.getTarget()) {
            return null;
        }

        /*        System.err.println("Scene dragDetected node = " + node);
        System.err.println("Scene dragDetected target = " + target);
        System.err.println("Scene dragDetected ev.source = " + event.getSource());
        System.err.println("Scene dragDetected ev.target = " + event.getTarget());
         */
        if (Dockable.of(node) != null) {
            MouseEvent copy = (MouseEvent) event.copyFor(node, node);
            Dockable.of(node).getContext().getDragDetector().handle(copy);
        }
        dragDetected = true;
        return null;
    }

    protected Event dragged(Event event, EventDispatchChain tail) {
//        System.err.println("Scene dragged ev.target = " + event.getTarget());
//        System.err.println("DRAGGED WINDOW " + target.getScene().getWindow());
//        System.err.println("DRAGGED event WINDOW " + ((Node) event.getTarget()).getScene().getWindow());
        /*        System.err.println("Scene dragged node = " + node);
        System.err.println("Scene dragged target = " + target);
        System.err.println("Scene dragged ev.source = " + event.getSource());
        System.err.println("Scene dragged ev.target = " + event.getTarget());
         */
        if (true) {
//            return initial.dispatchEvent(event, tail);            
        }
        if (target == null || !dragDetected) {
            return null;
        }
//        System.err.println("dragget before redirect dragManager 1");
        if (Dockable.of(node) != null) {
            if (!Dockable.of(node).getContext().isFloating()) {
                return null;
            }
//            System.err.println("dragget before redirect dragManager 2");
            DragManager dm = Dockable.of(node).getContext().getDragDetector().getDragHandler().getDragManager();
//            System.err.println("dragget before redirect dragManager 3 dm = " + dm);
            if (dm instanceof EventHandler) {
//                System.err.println("dragget before redirect dragManager 4");
                MouseEvent copy = (MouseEvent) event.copyFor(node, node);
                //((javafx.event.EventHandler)dm).handle(copy);    
//                System.err.println("dragget redirect dragManager");
                ((javafx.event.EventHandler) dm).handle(copy);
            }

        }
//        System.err.println("Scene dragged source = " + event.getSource());
//        System.err.println("Scene dragged target = " + event.getTarget());

        return null;
    }

    @Override
    public void finish(Node node) {
        node.setEventDispatcher(initial);
    }

    public void finish(Scene scene) {
        scene.setEventDispatcher(initial);
    }

}
