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

    private final Map<Window, Window> owners = new HashMap<>();
    private final ObservableMap<Node, Dockable> dockables = FXCollections.observableHashMap();
    private final ObservableMap<Node, DockLayout> dockLayouts = FXCollections.observableHashMap();
    
    private BeanRemover beanRemover;
    
    private boolean registerDone;

    private DockRegistry() {
        beanRemover = new DefaultNodeRemover();
    }

    public static DockRegistry getInstance() {
        return SingletonInstance.instance;
    }

    public BeanRemover getBeanRemover() {
//        if ( beanRemover == null )
        return beanRemover;
    }

    public void setBeanRemover(BeanRemover beanRemover) {
        this.beanRemover = beanRemover;
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

    public Window getTargettWindow(double x, double y, Window excl) {
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
                return instanceOfDockLayout(n);
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
                if (s1 != DockRegistry.this.getTarget(s1, s2)) {
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
                if (s1 != DockRegistry.this.getTarget(s1, s2)) {
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
            if (!(s instanceof Stage)) {
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
        } else if (!retval) {
            Object d = node.getProperties().get(Dockable.DOCKABLE_KEY);

            if (d != null && (d instanceof Dockable) && ((Dockable) d).node() == node) {
                retval = true;
            }
        }
        return retval;
    }

    public static Dockable makeDockable(Node node) {
        if ( isDockable(node) ) {
            return dockable(node);
        }
        Dockable d = new DefaultDockable(node);
      //  if (d.node().getParent() != null) {
      //      d.getContext().getLayoutContext().setTargetNode(d.node().getParent());
      //  }
        d.getContext().setDragNode(node);
        node.getProperties().put(Dockable.DOCKABLE_KEY, d);
        return d;
    }

    public void unregisterDockable(Node node) {
        node.getProperties().remove(Dockable.DOCKABLE_KEY);
    }

    public static DockLayout makeDockLayout(Node node, LayoutContext layoutContext) {
        
        if (node instanceof DockLayout) {
            return  (DockLayout) node;
        }
        if (getInstance().dockLayouts.get(node) != null) {
            return getInstance().dockLayouts.get(node);
        }
        DockLayout d = new DefaultDockLayout(node,layoutContext);
        node.getProperties().put(DockLayout.DOCKLAYOUTS_KEY, d);
        return d;
    }

    public void register(Dockable dockable) {
        if (dockable.node() instanceof Dockable) {
            return;
        }
        if (dockables.get(dockable.node()) != null) {
            return;
        }
        dockables.put(dockable.node(), dockable);
//        if (dockable.node().getParent() != null) {
//            dockable.getContext().getLayoutContext().setTargetNode((Region) dockable.node().getParent());
//        }
    }

    public void register(DockLayout dockTarget) {
        if (dockTarget.layoutNode() instanceof DockLayout) {
            return;
        }
        if (dockLayouts.get(dockTarget.layoutNode()) != null) {
            return;
        }
        dockLayouts.put(dockTarget.layoutNode(), dockTarget);

    }

    public DockLayout registerAsDockLayout(Node node) {
        if (isDockLayout(node)) {
            return dockLayout(node);
        }
        TargetContextFactory f = new TargetContextFactory();
        LayoutContext c = f.getContext(node);
        if (c == null) {
            return null;
        }
        DockLayout dt = new DefaultDockLayout(node, c);
        register(dt);
        return dt;
    }
    public void registerAsDockLayout(Node node, LayoutContext context) {
        if (isDockLayout(node)) {
            return;
        }
        DockLayout dt = makeDockLayout(node, context);
        node.getProperties().put(DockLayout.DOCKLAYOUTS_KEY, dt);
    }

    public Dockable getDefaultDockable(Node node) {
        if (node instanceof Dockable) {
            return (Dockable) node;
        }
        if (dockables.get(node) != null) {
            return dockables.get(node);
        }
        Dockable d = new DefaultDockable(node);
        dockables.put(node, d);
//        if (d.node().getParent() != null) {
//            d.getContext().getLayoutContext().setTargetNode(d.node().getParent());
//        }
        return d;
    }


    public static boolean isDockable(Object obj) {
        if ( obj instanceof Dockable) {
            return true;
        }
        if ( !(obj instanceof Node ) ) {
            return false;
        }
        return getInstance().isNodeDockable((Node)obj);
    }

    public static Dockable dockable(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Dockable) {
            return (Dockable) obj;
        }
        if ( !(obj instanceof Node) ) {
            return null;
        }
        Node node = (Node) obj;
        
        Dockable retval = getInstance().dockables.get(node);
        if (retval == null) {
            Object d = node.getProperties().get(Dockable.DOCKABLE_KEY);
            if (d != null && (d instanceof Dockable) && ((Dockable) d).node() == node) {
                retval = (Dockable) d;
            }
        }
        return retval;
    }

    public static boolean instanceOfDockLayout(Node node) {
        return getInstance().isNodeDockLayout(node);
    }

    public static boolean isDockLayout(Node node) {
        return getInstance().isNodeDockLayout(node);
    }

    protected boolean isNodeDockLayout(Node node) {
        boolean retval = node instanceof DockLayout;
        if (!retval && dockLayouts.get(node) != null) {
            retval = true;
        } else if (!retval) {
            Object d = node.getProperties().get(DockLayout.DOCKLAYOUTS_KEY);
            if (d != null && (d instanceof DockLayout) && ((DockLayout) d).layoutNode() == node) {
                retval = true;
            }
        }
        return retval;
    }

    public static DockLayout dockLayout(Node node) {
        if (node == null) {
            return null;
        }
        if (node instanceof DockLayout) {
            return (DockLayout) node;
        }
        DockLayout retval = getInstance().dockLayouts.get(node);
        if (retval == null) {
            Object d = node.getProperties().get(DockLayout.DOCKLAYOUTS_KEY);
            if (d != null && (d instanceof DockLayout) && ((DockLayout) d).layoutNode() == node) {
                retval = (DockLayout) d;
            }
        }
        return retval;

    }


    private static class SingletonInstance {

        private static final DockRegistry instance = new DockRegistry();
    }

    public static class DefaultDockable implements Dockable {

        private final Node node;
        private DockableContext context;

        public DefaultDockable(Node node) {
            this.node = node;
            init();
        }

        private void init() {
            context = new DockableContext(this);
        }

        @Override
        public Node node() {
            return node;
        }

        @Override
        public DockableContext getContext() {
            return context;
        }

    }

    public static class DefaultDockLayout implements DockLayout {

        private final Node node;
        private LayoutContext context;

        public DefaultDockLayout(Node node, LayoutContext context) {
            super();
            this.node = node;
            this.context = context;
        }

        @Override
        public Node layoutNode() {
            return node;
        }

        @Override
        public LayoutContext getLayoutContext() {
            return context;
        }

    }

}
