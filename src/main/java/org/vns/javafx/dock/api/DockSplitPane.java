package org.vns.javafx.dock.api;

import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class DockSplitPane extends SplitPane implements ListChangeListener {

    private EventHandler<ActionEvent> root;
    private ListChangeListener itemsChangeListener;

    public DockSplitPane() {
        init();
    }

    public DockSplitPane(Node... items) {
        super(items);
        init();
    }

    public EventHandler<ActionEvent> getRoot() {
        return root;
    }

    public void setRoot(EventHandler<ActionEvent> root) {
        this.root = root;
    }

    private void init() {
        DockTarget dpt = DockUtil.getParentDockPane(this);
        if (dpt != null && getItems().size() > 0) {
            getItems().forEach(it -> {
                if (DockRegistry.instanceOfDockable(it)) {
                    DockRegistry.dockable(it).getDockableContext().setTargetContext(dpt.getTargetContext());
                }
            });
        }
        if (dpt != null) {
            update();
        }
        getItems().addListener(this);
    }

    @Override
    public void onChanged(ListChangeListener.Change change) {
        itemsChanged(change);
    }

    protected void itemsChanged(ListChangeListener.Change<? extends Node> change) {
        DockTarget dpt = null;
        while (change.next()) {
            if (change.wasRemoved()) {
                List<? extends Node> list = change.getRemoved();
                if (!list.isEmpty() && dpt == null) {
                    dpt = DockUtil.getParentDockPane(list.get(0));
                }
                for (Node node : list) {
                    if (dpt != null && DockRegistry.instanceOfDockable(node)) {
                    } else if (dpt != null && node instanceof DockSplitPane) {
                        splitPaneRemoved((SplitPane) node, dpt);
                    }
                }

            }
            if (change.wasAdded()) {
                List<? extends Node> list = change.getAddedSubList();
                if (!list.isEmpty() && dpt == null) {
                    dpt = DockUtil.getParentDockPane(list.get(0));
                }
                for (Node node : list) {
                    if (dpt != null && DockRegistry.instanceOfDockable(node)) {
                        DockRegistry.dockable(node).getDockableContext().setTargetContext(dpt.getTargetContext());
                    } else if (dpt != null && node instanceof DockSplitPane) {
                        splitPaneAdded((SplitPane) node, dpt);
                    } else if (node instanceof DockSplitPane) {
                        ((DockSplitPane) node).setRoot(getRoot());
                    }
                }
            }
        }//while
        update();
    }

    protected void update(DockSplitPane split, TargetContext ph) {
        for (int i = 0; i < split.getItems().size(); i++) {
            Node node = split.getItems().get(i);
            if (DockRegistry.instanceOfDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.getDockableContext().setTargetContext(ph);
                /*                if (i < split.getDividers().size() && d.getDockableContext().getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(d.getDockableContext().getDividerPos());
                }
                 */
            } else if (node instanceof DockSplitPane) {
                ((DockSplitPane) node).setRoot(getRoot());
                DockSplitPane sp = (DockSplitPane) node;
                /*                if (i < split.getDividers().size() && sp.getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(sp.getDividerPos());
                }
                 */
                update(sp, ph);
            }
        }
    }

    public void update() {
        if (getRoot() != null) {
            getRoot().handle(new ActionEvent());
        }
    }

    protected void splitPaneAdded(SplitPane sp, DockTarget dpt) {
        for (int di = 0; di < sp.getDividerPositions().length; di++) {
            sp.setDividerPosition(di, sp.getDividerPositions()[di] + 0.01);
        }

        sp.getItems().forEach((node) -> {
            if (DockRegistry.instanceOfDockable(node)) {
                DockRegistry.dockable(node).getDockableContext().setTargetContext(dpt.getTargetContext());
            } else if (node instanceof SplitPane) {
                splitPaneAdded(((SplitPane) node), dpt);
            }
        });
    }

    protected void splitPaneRemoved(SplitPane sp, DockTarget dpt) {
        sp.getItems().forEach((node) -> {
            if (DockRegistry.instanceOfDockable(node)) {
            } else if (node instanceof SplitPane) {
                splitPaneRemoved(((SplitPane) node), dpt);
            }
        });
    }

    /*    public DoubleProperty dividerPosProperty() {
        return dividerPosProperty;
    }

    public double getDividerPos() {
        return dividerPosProperty.get();
    }

    public void setDividerPos(double dividerPos) {
        this.dividerPosProperty.set(dividerPos);
    }
     */
    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    /*    public void setDividerPosition(Node node, Side dockPos) {
        setDividerPosition(node, this, dockPos);
    }

    public static void setDividerPosition(Node node, DockSplitPane split, Side dockPos) {
        if (split.getItems().size() <= 1) {
            return;
        }

        int idx = split.getItems().indexOf(node);
        Dockable d = DockRegistry.dockable(node);

        double sizeSum = 0;
        for (int i = 0; i < split.getItems().size(); i++) {
            if (i == idx) {
                continue;
            }
            if (split.getOrientation() == Orientation.HORIZONTAL) {
                sizeSum += split.getItems().get(i).prefWidth(0);
            } else {
                sizeSum += split.getItems().get(i).prefHeight(0);
            }
        }
        if (dockPos == Side.TOP || dockPos == Side.LEFT) {
            if (split.getOrientation() == Orientation.HORIZONTAL) {
                split.setDividerPosition(idx,
                        node.prefWidth(0) / (sizeSum + node.prefWidth(0)));
            } else {
                split.setDividerPosition(idx,
                        node.prefHeight(0) / (sizeSum + node.prefHeight(0)));

            }
        } else {
            if (split.getOrientation() == Orientation.HORIZONTAL) {
                split.setDividerPosition(idx,
                        1 - node.prefWidth(0) / (sizeSum + node.prefWidth(0)));
            } else {
                split.setDividerPosition(idx,
                        1 - node.prefHeight(0) / (sizeSum + node.prefHeight(0)));
            }
        }
    }
     */
}
