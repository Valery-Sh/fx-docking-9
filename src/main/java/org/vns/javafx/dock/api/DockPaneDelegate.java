package org.vns.javafx.dock.api;

import org.vns.javafx.dock.DockUtil;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.vns.javafx.dock.api.DockSplitDelegate.DockSplitPane;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 * @param <T>
 */
public class DockPaneDelegate<T extends Pane> {

    private final ObjectProperty<T> dockPaneProperty = new SimpleObjectProperty<>();
    private DockSplitDelegate splitDelegate;
    private DockSplitPane rootSplitPane;
    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();

    public DockPaneDelegate(T dockPane) {
        dockPaneProperty.set(dockPane);
        init();
    }

    private void init() {

        rootSplitPane = new DockSplitPane();
        getDockPane().getChildren().add(rootSplitPane);

        splitDelegate = new DockSplitDelegate(rootSplitPane);

        getDockPane().sceneProperty().addListener((Observable observable) -> {
            focusedDockNode.bind(getDockPane().getScene().focusOwnerProperty());
        });
        
        focusedDockNode.addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            //Node newNode = DockUtil.getDockableImmediateParent(newValue);
            Node newNode = DockUtil.getImmediateParent(newValue, (p) -> {return p instanceof Dockable;} );
            if (newNode != null) {
                Dockable n = ((Dockable) newNode).stateProperty().getImmediateParent(newValue);
                if (n != null && n != newNode) {
                    newNode = (Node) n;
                }
                ((Dockable) newNode).stateProperty().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
            //Dockable oldNode = (Dockable) DockUtil.getDockableImmediateParent(oldValue);
            Dockable oldNode =  (Dockable) DockUtil.getImmediateParent(oldValue, (p) -> {return p instanceof Dockable;} );
            if (oldNode != null) {
                Dockable n = ((Dockable) oldNode).stateProperty().getImmediateParent(oldValue);
                if (n != null && n != oldNode) {
                    oldNode = n;
                }
            }
            if (oldNode != null && oldNode != newNode) {
                oldNode.stateProperty().titleBarProperty().setActiveChoosedPseudoClass(false);
            } else if (oldNode != null && !oldNode.stateProperty().titleBarProperty().isActiveChoosedPseudoClass()) {
                oldNode.stateProperty().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        });

    }

    public DockSplitPane parentSplitPane(Node node) {
        return DockUtil.getParentSplitPane(rootSplitPane, node);
    }

    public ObjectProperty<T> dockPaneProperty() {
        return dockPaneProperty;
    }

    public T getDockPane() {
        return this.dockPaneProperty.get();
    }

    public void setDockPane(T dockPane) {
        this.dockPaneProperty.set(dockPane);
    }

    protected boolean isDocked(Node node) {
        return DockUtil.getParentSplitPane(rootSplitPane, node) != null;
    }

    public void undock(Node node) {
        if (!isDocked(node)) {
            return;
        }
        if (node instanceof Dockable) {
            ((Dockable) node).stateProperty().undock();
        }
    }

    public void dock(Node node, Side dockPos) {
        if (isDocked(node)) {
            return;
        }
        splitDelegate.dock(node, dockPos);
        DockSplitPane save = rootSplitPane;
        if (rootSplitPane != splitDelegate.getRoot()) {
            rootSplitPane = splitDelegate.getRoot();
        }
        int idx = getDockPane().getChildren().indexOf(save);
        getDockPane().getChildren().set(idx, rootSplitPane);
        if (node instanceof Dockable) {
            StateProperty state = ((Dockable) node).stateProperty();
            if (state.getParent() == null || state.getParent() != this) {
                state.setParent(this);
            }
            state.setDocked(true, null);
        }
    }

    public void dock(Node node, Side dockPos, DockTarget target) {
        if (isDocked(node)) {
            return;
        }
        if (target == null) {
            dock(node, dockPos);
        } else {
            splitDelegate.dock(node, dockPos, target);
        }
        if (node instanceof Dockable) {
            StateProperty state = ((Dockable) node).stateProperty();
            if (state.getParent() == null || state.getParent() != this) {
                state.setParent(this);
            }
            state.setDocked(true, null);
        }

    }

    public void remove(Node dockNode) {
        DockSplitPane dsp = getParentSplitPane(rootSplitPane, dockNode);
        if (dsp != null) {
            dsp.getItems().remove(dockNode);
            clearEmptySplitPanes(rootSplitPane, dsp);
        }
    }

    protected DockSplitPane getRootSplitPane() {
        return rootSplitPane;
    }

    protected void setRootSplitPane(DockSplitPane rootSplitPane) {
        this.rootSplitPane = rootSplitPane;
    }

    /*    public static Dockable findFocusedDockNode(Node focusedNode) {
        if (focusedNode == null) {
            return null;
        }
        Dockable retval = null;

        Node p = focusedNode.getParent();

        while (p != null) {

            if ((p instanceof DockableOwner) && ((DockableOwner) p).getOwner() != null) {
                retval = ((DockableOwner) p).getOwner();
                break;
            }
            if (p instanceof Dockable) {
                retval = (Dockable) p;
                break;
            }
            p = p.getParent();
        }
        return retval;
    }
     */
}
