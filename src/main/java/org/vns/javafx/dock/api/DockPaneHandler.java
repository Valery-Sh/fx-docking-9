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
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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

    private ObservableMap<Node,Dockable> notDockableItems = FXCollections.observableHashMap();
            
    public DockPaneHandler(Pane dockPane) {
        dockPaneProperty.set(dockPane);
        init();
    }
    private void init() {
        inititialize();
    }
    protected void inititialize() {
        StageRegistry.start();
        rootSplitPane = new SplitDelegate.DockSplitPane();
        getDockPane().getChildren().add(rootSplitPane);

        splitDelegate = new SplitDelegate(rootSplitPane);

        getDockPane().sceneProperty().addListener((Observable observable) -> {
            focusedDockNode.bind(getDockPane().getScene().focusOwnerProperty());
        });

        focusedDockNode.addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            Node newNode = DockUtil.getImmediateParent(newValue, (p) -> {
                return p instanceof Dockable;
            });
            if (newNode != null) {
                Dockable n = ((Dockable) newNode).nodeHandler().getImmediateParent(newValue);
                if (n != null && n != newNode) {
                    newNode = (Node) n;
                }
                ((Dockable) newNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
            //Dockable oldNode = (Dockable) DockUtil.getDockableImmediateParent(oldValue);
            Dockable oldNode = (Dockable) DockUtil.getImmediateParent(oldValue, (p) -> {
                return p instanceof Dockable;
            });
            if (oldNode != null) {
                Dockable n = ((Dockable) oldNode).nodeHandler().getImmediateParent(oldValue);
                if (n != null && n != oldNode) {
                    oldNode = n;
                }
            }
            if (oldNode != null && oldNode != newNode) {
                oldNode.nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(false);
            } else if (oldNode != null && !oldNode.nodeHandler().titleBarProperty().isActiveChoosedPseudoClass()) {
                oldNode.nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        });

    }

    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

/*    public DockSplitPane parentSplitPane(Node node) {
        return DockUtil.getParentSplitPane(rootSplitPane, node);
    }
*/
    public ObjectProperty<Pane> dockPaneProperty() {
        return dockPaneProperty;
    }

    public Pane getDockPane() {
        return this.dockPaneProperty.get();
    }

    public void setDockPane(Pane dockPane) {
        this.dockPaneProperty.set(dockPane);
    }

    protected boolean isDocked(Node node) {
        boolean retval;
        if ( node instanceof Dockable ) {
            retval = DockUtil.getParentSplitPane(rootSplitPane, node) != null;
        } else {
            retval = notDockableItems.get(node) != null ;
        }
        return retval;
    }

    public void undock(Node node) {
        if (!isDocked(node)) {
            return;
        }
        if (node instanceof Dockable) {
            ((Dockable) node).nodeHandler().undock();
        }
    }
    
    public Dockable dock(Dockable dockable, Side dockPos) {
        return dock(dockable.node(), dockPos);
    }
    public Dockable dock(Node node, Side dockPos) {
        Dockable d = null; 
        if (isDocked(node)) {
            if ( node instanceof Dockable ) {
                d = (Dockable) node;
            } else {
                d = notDockableItems.get(node);
            }
            return d;
        }
        
        if (node instanceof Dockable) {
            d = (Dockable) node;
            d.nodeHandler().setFloating(false);
        }  else {
            d = new DefaultDockable(node);
            notDockableItems.put(node, d);
        }
        doDock(d.node(), dockPos);
        return d;
        
    }    
    private void doDock(Node node, Side dockPos) {
        
        if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
            ((Stage) node.getScene().getWindow()).close();
        }
        splitDelegate.dock((Dockable)node, dockPos);

        SplitDelegate.DockSplitPane save = rootSplitPane;
        if (rootSplitPane != splitDelegate.getRoot()) {
            rootSplitPane = splitDelegate.getRoot();
        }

        int idx = getDockPane().getChildren().indexOf(save);
        getDockPane().getChildren().set(idx, rootSplitPane);

        if (node instanceof Dockable) {
            DockNodeHandler state = ((Dockable) node).nodeHandler();
            if (state.getPaneHandler() == null || state.getPaneHandler() != this) {
                state.setPaneHandler(this);
            }
            state.setDocked(true);
        }
    }

    public Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
        return this.dock(dockable.node(), dockPos, target);
    }    
    
    public Dockable dock(Node node, Side dockPos, Dockable target) {
        Dockable d = null; 
        if (isDocked(node)) {
            if ( node instanceof Dockable ) {
                d = (Dockable) node;
            } else {
                d = notDockableItems.get(node);
            }
            return d;
        }

        if (node instanceof Dockable) {
            d = (Dockable) node;
            d.nodeHandler().setFloating(false);
        }  else {
            d = new DefaultDockable(node);
            notDockableItems.put(node, d);
        }
        doDock(d.node(), dockPos, target);
        return d;
    }    
    
    private void doDock(Node node, Side dockPos, Dockable target) {
        if (isDocked(node)) {
            return;
        }
        if (target == null) {
            dock(node, dockPos);
        } else {
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            if (node instanceof Dockable) {
                ((Dockable) node).nodeHandler().setFloating(false);
            }
            if ( target instanceof DockTarget ) {
                ((DockTarget)target).dock(node, dockPos);
            } else {
                splitDelegate.dock(node, dockPos, target);
            }
        }
        if (node instanceof Dockable) {
            DockNodeHandler state = ((Dockable) node).nodeHandler();
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

/*    protected DockSplitPane getRootSplitPane() {
        return rootSplitPane;
    }
*/
/*    protected void setRootSplitPane(DockSplitPane rootSplitPane) {
        this.rootSplitPane = rootSplitPane;
    }
*/
    public int zorder() {
        return zorder;
    }

    public void setZorder(int zorder) {
        this.zorder = zorder;
    }
}
