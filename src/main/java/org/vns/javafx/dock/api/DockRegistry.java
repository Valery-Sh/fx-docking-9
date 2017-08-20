package org.vns.javafx.dock.api;

import com.sun.javafx.stage.StageHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The class contains methods to manage all windows
 *
 * @author Valery Shyshkin
 */
public class DockRegistry {

    private final ObservableList<Window> windows = FXCollections.observableArrayList();
    //private final ObservableList<Window> windows = FXCollections.observableArrayList();

    private final Map<Window, Window> owners = new HashMap<>();
    private final ObservableMap<Node, Dockable> dockables = FXCollections.observableHashMap();
    private final ObservableMap<Node, DockTarget> dockTargets = FXCollections.observableHashMap();

    private boolean registerDone;

    private DockRegistry() {
    }

    public static DockRegistry getInstance() {
        return SingletonInstance.instance;
    }

    public static void register(Window window) {

        getInstance().doRegister(window);
        if (!(window instanceof Stage)) {
            getInstance().getWindows().add(window);
            if (window instanceof Popup) {
                Popup p = (Popup) window;
                if (p.getOwnerWindow() != null) {
                    getInstance().owners.put(window, p.getOwnerWindow());
                }
            }
            Platform.runLater(() -> {
                getInstance().updateRegistry();
            });

        }
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
                getInstance().windows.add(0, s);

            });
            StageHelper.getStages().addListener(getInstance()::onChangeStages);
            //getWindows().addListener(getInstance()::onChangeWindows);
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
                        getInstance().windows.remove(s);
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

    public static void unregister(Window window) {
        if (window instanceof Stage) {
            return;
        }
        getInstance().windows.remove(window);
        getInstance().owners.remove(window);
        Platform.runLater(() -> {
            getInstance().updateRegistry();
        });

    }

    protected void onChangeWindows(ListChangeListener.Change<? extends Window> change) {
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
        for (Window w : windows) {
            if (w instanceof Stage) {
                continue;
            }
            if (!(w instanceof Popup)) {
                continue;
            }
            Popup p = (Popup) w;
            if (p.getOwnerWindow() != null) {
                int idx1 = windows.indexOf(w);
                int idx2 = windows.indexOf(p.getOwnerWindow());
                if (idx1 < idx2) {
                    owners.remove(w);
                }
            }

        }
    }

    public static ObservableList<Window> getWindows() {
        return getInstance().windows;
    }

    private void doRegister(Window window) {
        window.focusedProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue) {
                windows.remove(window);
                windows.add(0, window);
            }
        });
    }

    private boolean isChild(Window parent, Window child) {
        boolean retval = false;
        Window win = child;
        while (true) {
            Window w = owners.get(win);
            if (w == null) {
                break;
            }
            if (w == parent) {
                retval = true;
                break;
            }

        }
        return retval;
    }

    public int zorder(Window window) {
        if (!windows.contains(window)) {
            register(window);
        }
        return windows.indexOf(window);
    }

    public Window getTargetWindow(double x, double y, Window excl) {
        Window retval = null;
        for (Window w : windows) {
            Bounds b = new BoundingBox(w.getX(), w.getY(), w.getWidth(), w.getHeight());
            if (b.contains(x, y)) {
                retval = w;
            }
        }
        return retval;
    }

    public Window getTarget(double x, double y, Window excl) {
        Window retval = null;
        List<Window> allStages = getWindows(x, y, excl);
        if (allStages.isEmpty()) {
            return null;
        }
        List<Window> targetStages = new ArrayList<>();
        allStages.forEach(s -> {
            Node topNode = TopNodeHelper.getTopNode(s, x, y, n -> {
                return instanceOfDockTarget(n);
            });
            if (topNode != null) {
                targetStages.add(s);
            }
        });
        for (Window s1 : targetStages) {
            retval = s1;
            for (Window s2 : allStages) {
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

    public Window getTarget(double x, double y, Window excl, Predicate<Node> predicate) {
        Window retval = null;
        List<Window> allStages = getWindows(x, y, excl);
        if (allStages.isEmpty()) {
            return null;
        }
        List<Window> targetStages = new ArrayList<>();
        allStages.forEach(s -> {
            Node topNode = TopNodeHelper.getTopNode(s, x, y, n -> {
                return predicate.test(n);
            });
            if (topNode != null) {
                targetStages.add(s);
            }
        });
        for (Window s1 : targetStages) {
            retval = s1;
            for (Window s2 : allStages) {
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

    /*    public Stage getTarget(Stage s1, Stage s2) {
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
        return retval;
    }
     */
    public Window getTarget(Window s1, Window s2) {
        Window retval = null;
        Window s = s1;
        boolean b1 = false;
        boolean b2 = false;

        if (s1 instanceof Popup) {
            b1 = true;
        } else if ((s1 instanceof Stage)) {
            b1 = ((Stage) s1).isAlwaysOnTop();
        }
        if (s2 instanceof Popup) {
            b2 = true;
        } else if ((s2 instanceof Stage)) {
            b2 = ((Stage) s2).isAlwaysOnTop();
        }

        if (isChild(s1, s2)) {
            //
            //retval must be null s2 is a child window of s1
            //
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
        /*  String t = null;
        if (retval != null) {
            t = retval.getTitle();
        }
         */
        return retval;
    }

    public static List<Window> getWindows(double x, double y, Window excl) {
        List<Window> retlist = new ArrayList<>();
        StageHelper.getStages().forEach(s -> {
            if (!((x < s.getX() || x > s.getX() + s.getWidth()
                    || y < s.getY() || y > s.getY() + s.getHeight()))) {
                if (s != excl) {
                    retlist.add(s);
                }
            }
        });
        getInstance().windows.forEach(s -> {
            if ( ! ( s instanceof Stage) ) {
            if (!((x < s.getX() || x > s.getX() + s.getWidth()
                    || y < s.getY() || y > s.getY() + s.getHeight()))) {
                if (s != excl) {
                    retlist.add(s);
                }
            }
                
            }
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
            dockable.getDockableContext().getTargetContext().setTargetNode((Region) dockable.node().getParent());
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
            //dockable.getDockableContext().getTargetContext().setTargetNode((Region) dockTarget.node().getParent());
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
            d.getDockableContext().getTargetContext().setTargetNode((Region) d.node().getParent());
        }
        return d;
    }

    public static boolean instanceOfDockable(Node node) {
        return getInstance().isNodeDockable(node);
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

    public static boolean instanceOfDockTarget(Node node) {
        return getInstance().isNodeDockTarget(node);
    }
    public static boolean isDockTarget(Node node) {
        return getInstance().isNodeDockTarget(node);
    }

    protected boolean isNodeDockTarget(Node node) {
        boolean retval = node instanceof DockTarget;
        if (!retval && dockTargets.get(node) != null) {
            retval = true;
        }
        return retval;
    }

    public static DockTarget dockTarget(Node node) {
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
        private DockableContext nodeHandler;

        public DefaultDockable(Region node) {
            this.node = node;
            init();
        }

        private void init() {
            nodeHandler = new DockableContext(this);
        }

        @Override
        public Region node() {
            return node;
        }

        @Override
        public DockableContext getDockableContext() {
            return nodeHandler;
        }

    }
}
