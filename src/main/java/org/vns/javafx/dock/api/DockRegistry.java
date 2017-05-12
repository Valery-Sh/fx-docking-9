package org.vns.javafx.dock.api;

import com.sun.javafx.stage.StageHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The class contains methods to manage all stages
 *
 * @author Valery Shyshkin
 */
public class DockRegistry {

    private final ObservableList<Stage> stages = FXCollections.observableArrayList();
    private final Map<Stage, Window> owners = new HashMap<>();
    private final ObservableMap<Node, Dockable> dockables = FXCollections.observableHashMap();
    private final ObservableMap<Node, DockTarget> dockTargets = FXCollections.observableHashMap();

    private boolean registerDone;

    private DockRegistry() {
    }

    public static DockRegistry getInstance() {
        return SingletonInstance.instance;
    }

    public static void register(Stage stage) {
        getInstance().doRegister(stage);
    }

    public static ObservableMap<Node, Dockable> getDockables() {
        return getInstance().dockables;
    }

    public static void start() {
        if (!getInstance().registerDone) {
            getInstance().registerDone = true;
            javafx.application.Platform.runLater(() -> {
                getInstance().updateRegistry();
            });

            StageHelper.getStages().forEach(s -> {
                //
                // Add in reverse order
                //
                getInstance().stages.add(0, s);

            });
            StageHelper.getStages().addListener(getInstance()::onChangeStages);
        }
    }

    protected void onChangeStages(ListChangeListener.Change<? extends Stage> change) {
        while (change.next()) {
            if (change.wasPermutated()) {

            } else if (change.wasUpdated()) {

            } else if (change.wasReplaced()) {
            } else {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(s -> {
                        getInstance().stages.remove(s);
                        getInstance().owners.remove(s);
                    });
                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(s -> {
                        register(s);
                        if (s.getOwner() != null) {
                            getInstance().owners.put(s, s.getOwner());
                        }
                    });

                    Platform.runLater(() -> {
                        updateRegistry();
                    });

                }
            }
        }

    }

    protected void updateRegistry() {

        StageHelper.getStages().forEach(s -> {
            if (s.getOwner() != null) {
                int idx1 = StageHelper.getStages().indexOf(s);
                int idx2 = StageHelper.getStages().indexOf(s.getOwner());
                if (idx1 < idx2) {
                    owners.remove(s);
                }
            }
        });
    }

    public static ObservableList<Stage> getStages() {
        return getInstance().stages;
    }

    private void doRegister(Stage stage) {
        stage.focusedProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue) {
                stages.remove(stage);
                stages.add(0, stage);
            }
        });
    }

    public static Window getOwner(Stage stage) {
        return getInstance().owners.get(stage);
    }

    private boolean isChild(Stage parent, Stage child) {
        boolean retval = false;
        Stage win = child;
        while (true) {
            Window w = owners.get(win);
            if (w == null) {
                break;
            }
            if (w == parent) {
                retval = true;
                break;
            }
            if (w instanceof Stage) {
                win = (Stage) w;
            }

        }
        return retval;
    }

    public int zorder(Stage stage) {
        if (!stages.contains(stage)) {
            register(stage);
        }
        return stages.indexOf(stage);
    }

    public Stage getTarget(double x, double y, Stage excl) {
        Stage retval = null;
        List<Stage> allStages = getStages(x, y, excl);
        if (allStages.isEmpty()) {
            return null;
        }
        List<Stage> targetStages = new ArrayList<>();
        allStages.forEach(s -> {
            Node topNode = TopNodeHelper.getTopNode(s, x, y, n -> {
                //12.05return (n instanceof DockTarget);
                return isDockPaneTarget(n);
            });
            /* 11.01            List<Node> ls = DockUtil.findNodes(s.getScene().getRoot(), (node) -> {
                Point2D p = node.screenToLocal(x, y);
                return node.contains(p) && (node instanceof DockTarget)
                        && ((DockTarget) node).paneHandler().zorder() == 0;
            });
            
            Node node = s.getScene().getRoot();
            Point2D p = node.screenToLocal(x, y);
            if (node.contains(p) && (node instanceof DockTarget && ((DockTarget) node).paneHandler().zorder() == 0)) {
                ls.add(0, node);
            }
            if (!ls.isEmpty()) {
                targetStages.add(s);
            }
             */
            if (topNode != null) {
                targetStages.add(s);
            }
        });
        for (Stage s1 : targetStages) {
            retval = s1;
            for (Stage s2 : allStages) {
                if (s1 == s2) {
                    continue;
                }
                if (s1 != getTarget(s1, s2)) {
                    retval = null;
                    break;
                }
            }
            if (retval != null) {
                break;
            }
        }
        return retval;
    }

    public Stage getTarget(Stage s1, Stage s2) {
        Stage retval = null;
        Stage s = s1;

        boolean b1 = s1.isAlwaysOnTop();
        boolean b2 = s2.isAlwaysOnTop();
        if (isChild(s1, s2)) {
            //retval must be null s2 is a child window of s1

        } else if (isChild(s2, s1)) {
            retval = s1;
        } else if (zorder(s1) < zorder(s2) && !b1 && !b2) {
            retval = s1;
        } else if (zorder(s1) < zorder(s2) && b1 && b2) {
            retval = s1;
        } else if (b1 && !b2) {
            retval = s1;
        } else if (!b1 && b2) {
        }
        String t = null;
        if (retval != null) {
            t = retval.getTitle();
        }

        return retval;
    }

    public static List<Stage> getStages(double x, double y, Stage excl) {
        List<Stage> retlist = new ArrayList<>();
        StageHelper.getStages().forEach(s -> {
            if (!((x < s.getX() || x > s.getX() + s.getWidth()
                    || y < s.getY() || y > s.getY() + s.getHeight()))) {
                if (s != excl) {
                    retlist.add(s);
                }
            }
            //s.getScene().getRoot().setStyle("-fx-background-color: lightgray");
            //s.getScene().getRoot().setStyle("-fx-background-color: lightgray");
        });
        return retlist;
    }

    protected boolean isNodeDockable(Node node) {
        boolean retval = node instanceof Dockable;
        if (!retval && dockables.get(node) != null) {
            retval = true;
        }
        return retval;
    }

    public void register(Dockable dockable) {
        if (dockable.node() instanceof Dockable) {
            return;
        }
        if (dockables.get(dockable.node()) != null) {
            return;
        }
        dockables.put(dockable.node(), dockable);
        if (dockable.node().getParent() != null) {
            dockable.dockableController().getTargetController().setTargetNode((Region) dockable.node().getParent());
        }
        
    }
    public void register(DockTarget dockTarget) {
        if (dockTarget.target() instanceof DockTarget) {
            return;
        }
        if (dockTargets.get(dockTarget.target()) != null) {
            return;
        }
        dockTargets.put(dockTarget.target(), dockTarget);
        if (dockTarget.target().getParent() != null) {
            //dockable.dockableController().getTargetController().setTargetNode((Region) dockTarget.node().getParent());
        }
        
    }

    public Dockable getDefaultDockable(Node node) {
        if (node instanceof Dockable) {
            return (Dockable) node;
        }
        if (dockables.get(node) != null) {
            return dockables.get(node);
        }
        Dockable d = new DefaultDockable((Region) node);
        dockables.put(node, d);
        if (d.node().getParent() != null) {
            d.dockableController().getTargetController().setTargetNode((Region) d.node().getParent());
        }
        return d;
    }

    public static boolean isDockable(Node node) {
        return getInstance().isNodeDockable(node);
    }

    public static Dockable dockable(Node node) {
        if (node instanceof Dockable) {
            return (Dockable) node;
        }
        return getInstance().dockables.get(node);
    }

    public static boolean isDockPaneTarget(Node node) {
        return getInstance().isNodeDockPaneTarget(node);
    }

    protected boolean isNodeDockPaneTarget(Node node) {
        boolean retval = node instanceof DockTarget;
        if (!retval && dockTargets.get(node) != null) {
            retval = true;
        }
        return retval;
    }

    public static DockTarget dockPaneTarget(Node node) {
        if (node instanceof DockTarget) {
            return (DockTarget) node;
        }
        return getInstance().dockTargets.get(node);
    }

    private static class SingletonInstance {

        private static final DockRegistry instance = new DockRegistry();
    }

    public static class DefaultDockable implements Dockable {

        private final Region node;
        private DockableController nodeHandler;

        public DefaultDockable(Region node) {
            this.node = node;
            init();
        }

        private void init() {
            nodeHandler = new DockableController(this);
        }

        @Override
        public Region node() {
            return node;
        }

        @Override
        public DockableController dockableController() {
            return nodeHandler;
        }

    }
}
