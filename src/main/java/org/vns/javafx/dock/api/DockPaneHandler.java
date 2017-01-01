/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.vns.javafx.dock.DockTabPane.TABOVER_PSEUDO_CLASS;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;

/**
 *
 * @author Valery
 */
public class DockPaneHandler {

    private final ObjectProperty<Pane> dockPaneProperty = new SimpleObjectProperty<>();
    private SplitDelegate splitDelegate;
    private SplitDelegate.DockSplitPane rootSplitPane;
    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();
    private int zorder = 0;
    private boolean usedAsDockTarget = true;

    private DragPopup dragPopup;

    private ObservableMap<Node, Dockable> notDockableItemsProperty = FXCollections.observableHashMap();

    private SidePointerModifier sidePointerModifier;

    public DockPaneHandler(Pane dockPane) {
        dockPaneProperty.set(dockPane);
        init();
    }

    private void init() {
        setSidePointerModifier(this::modifyNodeSidePointer);
        dragPopup = new DragPopup();
        inititialize();
    }

    public DragPopup getDragPopup() {
        return dragPopup;
    }

    protected void initSplitDelegate() {
        rootSplitPane = new SplitDelegate.DockSplitPane();
        getDockPane().getChildren().add(rootSplitPane);

        splitDelegate = new SplitDelegate(rootSplitPane);
    }

    protected void inititialize() {
        DockRegistry.start();
        initSplitDelegate();
        getDockPane().sceneProperty().addListener((Observable observable) -> {
            if (getDockPane().getScene() != null) {
                focusedDockNode.bind(getDockPane().getScene().focusOwnerProperty());
            }
        });

        focusedDockNode.addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            Node newNode = DockUtil.getImmediateParent(newValue, (p) -> {
                //return p instanceof Dockable;
                return DockRegistry.isDockable(p);
            });

            if (newNode != null) {
                //31.12 Dockable n = ((Dockable) newNode).nodeHandler().getImmediateParent(newValue);
                Dockable n = DockRegistry.dockable(newNode).nodeHandler().getImmediateParent(newValue);
                if (n != null && n != newNode) {
                    newNode = (Node) n;
                }
                //31.12 ((Dockable) newNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
                DockRegistry.dockable(newNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
            /* 31.12
            Dockable oldNode = (Dockable) DockUtil.getImmediateParent(oldValue, (p) -> {
                return p instanceof Dockable;
            });
            if (oldNode != null) {
                Dockable n = ((Dockable) oldNode).nodeHandler().getImmediateParent(oldValue);
                if (n != null && n != oldNode) {
                    oldNode = n;
                }
            }
            
             */
            Node oldNode = DockUtil.getImmediateParent(oldValue, (p) -> {
                return DockRegistry.isDockable(p);
            });

            if (oldNode != null && oldNode != newNode) {
                DockRegistry.dockable(oldNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(false);
            } else if (oldNode != null && !DockRegistry.dockable(oldNode).nodeHandler().titleBarProperty().isActiveChoosedPseudoClass()) {
                DockRegistry.dockable(oldNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        });

    }

    protected ObservableMap<Node, Dockable> getNotDockableItems() {
        return this.notDockableItemsProperty;
    }

    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

    public Point2D modifyNodeSidePointer(DragPopup popup, Dockable target, double mouseX, double mouseY) {
        //popup.initSidePointerGrid();
        return null;
    }

    /*    public DockSplitPane parentSplitPane(Node node) {
        return DockUtil.getParentSplitPane(rootSplitPane, node);
    }
     */
    protected ObjectProperty<Pane> dockPaneProperty() {
        return dockPaneProperty;
    }

    public SidePointerModifier getSidePointerModifier() {
        return sidePointerModifier;
    }

    public void setSidePointerModifier(SidePointerModifier sidePointerModifier) {
        this.sidePointerModifier = sidePointerModifier;
    }

    public Pane getDockPane() {
        return this.dockPaneProperty.get();
    }

    public void setDockPane(Pane dockPane) {
        this.dockPaneProperty.set(dockPane);
    }

    protected boolean isDocked(Node node) {
        boolean retval;
//31.12        if (node instanceof Dockable) {
        if (DockRegistry.isDockable(node)) {
            retval = DockUtil.getParentSplitPane(rootSplitPane, node) != null;
        } else {
            retval = notDockableItemsProperty.get(node) != null;
        }
        return retval;
    }

    public void undock(Node node) {
        if (!isDocked(node)) {
            return;
        }
        /*
        if (node instanceof Dockable) {
            ((Dockable) node).nodeHandler().undock();
        }
         */
        if (DockRegistry.isDockable(node)) {
            DockRegistry.dockable(node).nodeHandler().undock();
        }
    }

    protected Dockable dock(Point2D mousePos, Node node, Side dockPos, Dockable target) {
        //31.12return dock(node, dockPos, target);
        return dock(DockRegistry.dockable(node), dockPos, target);
    }

    protected Dockable dock(Point2D mousePos, Node node, Side dockPos) {
        Dockable d;
        if (isDocked(node)) {
//31.12            if (node instanceof Dockable) {
//31.12            if (DockRegistry.isDockable(node)) {
//31.12                d = (Dockable) node;
            d = DockRegistry.dockable(node);
            return d;
        }

//31.12        if (node instanceof Dockable) {
//31.12        if (DockRegistry.isDockable(node)) {
//31.12            d = (Dockable) node;
        d = DockRegistry.dockable(node);
        d.nodeHandler().setFloating(false);
        d = convert(d, DockConverter.BEFORE_DOCK);

        doDock(mousePos, d.node(), dockPos);
        return d;
    }
    protected Dockable convert(Dockable source, int when) {
        Dockable retval = source;
        if (source instanceof DockConverter) {
            retval = ((DockConverter) source).convert(source, when);
        }
        return retval;
    }

    public Dockable dock(Dockable dockable, Side dockPos) {
        return dock(null, dockable.node(), dockPos);
    }

    public Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
        
        if (isDocked(dockable.node())) {
//31.12            if (dockable instanceof Dockable) {
            return dockable;
        }

//31.12        if (dockable instanceof Dockable) {
//31.12        if (DockRegistry.isDockable(dockable)) {
//31.12            d = (Dockable) dockable;
        
        if ( ! (dockable instanceof Node ) && ! DockRegistry.getDockables().containsKey(dockable.node())) {
            DockRegistry.getDockables().put(dockable.node(), dockable);
        }
        dockable.nodeHandler().setFloating(false);

        doDock(dockable.node(), dockPos, target);
        return dockable;
    }

    protected void doDock(Point2D mousePos, Node node, Side dockPos) {

        if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
            ((Stage) node.getScene().getWindow()).close();
        }
        //31.12splitDelegate.dock((Dockable) node, dockPos);
        splitDelegate.dock(DockRegistry.dockable(node), dockPos);

        SplitDelegate.DockSplitPane save = rootSplitPane;
        if (rootSplitPane != splitDelegate.getRoot()) {
            rootSplitPane = splitDelegate.getRoot();
        }

        int idx = getDockPane().getChildren().indexOf(save);
        getDockPane().getChildren().set(idx, rootSplitPane);

//31.12        if (node instanceof Dockable) {
        if (DockRegistry.isDockable(node)) {
//31.12            DockNodeHandler state = ((Dockable) node).nodeHandler();
            DockNodeHandler state = DockRegistry.dockable(node).nodeHandler();
            if (state.getPaneHandler() == null || state.getPaneHandler() != this) {
                state.setPaneHandler(this);
            }
            state.setDocked(true);
        }
    }

    private void doDock(Node node, Side dockPos, Dockable targetDockable) {
        if (isDocked(node)) {
            return;
        }
        if (targetDockable == null) {
            dock(DockRegistry.dockable(node), dockPos);
        } else {
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
//31/12            if (node instanceof Dockable) {
            if (DockRegistry.isDockable(node)) {
//31.12                ((Dockable) node).nodeHandler().setFloating(false);
                DockRegistry.dockable(node).nodeHandler().setFloating(false);
            }
            if (targetDockable instanceof DockTarget) {
                ((DockTarget) targetDockable).dock(node, dockPos);
            } else {
                splitDelegate.dock(node, dockPos, targetDockable);
            }
        }
//31.12        if (node instanceof Dockable) {
        if (DockRegistry.isDockable(node)) {
//31.12            DockNodeHandler state = ((Dockable) node).nodeHandler();
            DockNodeHandler state = DockRegistry.dockable(node).nodeHandler();
            if (state.getPaneHandler() == null || state.getPaneHandler() != this) {
                state.setPaneHandler(this);
            }
            state.setDocked(true);
        }

    }

    public void remove(Node dockNode) {
        SplitDelegate.DockSplitPane dsp = getParentSplitPane(rootSplitPane, dockNode);
        if (dsp != null) {
            dsp.getItems().remove(dockNode);
            clearEmptySplitPanes(rootSplitPane, dsp);
        }
    }

    public int zorder() {
        return zorder;
    }

    public void setZorder(int zorder) {
        this.zorder = zorder;

    }

    @FunctionalInterface
    public interface SidePointerModifier {

        /**
         *
         * @param mouseX
         * @param mouseY
         * @param target
         * @return null than a default position of node indicator is used or a
         * new position of node indicator
         */
        Point2D modify(DragPopup popup, Dockable target, double mouseX, double mouseY);
    }

    public interface DockConverter {

        public static final int BEFORE_DOCK = 0;
        public static final int AFTER_DOCK = 0;

        Dockable convert(Dockable source, int when);
    }//interface DockConverter

}//class
