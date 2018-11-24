package org.vns.javafx.dock.api;

import org.vns.javafx.ContextLookup;
import com.sun.javafx.stage.StageHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.BaseContextLookup;

/**
 * The class contains methods to manage all windows
 *
 * @author Valery Shyshkin
 */
public class DockRegistry {

    public final ContextLookup lookup;

    private final ObservableList<Window> windows = FXCollections.observableArrayList();

//    private final Map<Window, Window> owners = new HashMap<>();
    private final ObservableList<Window> excluded = FXCollections.observableArrayList();

    private final ObservableMap<Node, Dockable> dockables = FXCollections.observableHashMap();
    private final ObservableMap<Node, DockLayout> dockLayouts = FXCollections.observableHashMap();

    private BeanRemover beanRemover;

    private boolean registerDone;

    private DockRegistry() {
        beanRemover = new DefaultNodeRemover();
        lookup = new BaseContextLookup();
        init();
    }

    public ContextLookup getLookup() {
        return lookup;
    }

    private void init() {
        windows.addListener(this::windowsChanged);
        lookup.putUnique(ScopeEvaluator.class, new LayoutContext.DefaultScopeEvaluator());
    }

    public static <T> T lookup(Class<T> clazz) {
        return getInstance().lookup.lookup(clazz);
    }

    public void windowsChanged(ListChangeListener.Change<? extends Window> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List<? extends Window> list = change.getRemoved();
                for (Window win : list) {

                }

            }
            if (change.wasAdded()) {
                List<? extends Window> list = change.getAddedSubList();
            }
        }//while
    }

    public Window getFocusedWindow() {
        Window retval = null;
        for (Window win : windows) {
            if (win.isFocused()) {
                retval = win;
                break;
            }
        }
        return retval;
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

    public static void register(Window window, boolean excluded) {
        register(window);
        if (excluded && !getInstance().getExcluded().contains(window)) {
            getInstance().getExcluded().add(window);
        }
    }

    protected ObservableList<Window> getExcluded() {
        return excluded;
    }

    public static void register(Window window) {
        getInstance().doRegister(window);
        if (!(window instanceof Stage)) {
            getInstance().getWindows().add(window);
        }
    }

    public static ObservableMap<Node, Dockable> getDockables() {
        return getInstance().dockables;
    }

    public static void start() {
        if (!getInstance().registerDone) {
            getInstance().registerDone = true;
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
                        //getInstance().owners.clear(s);
                    });
                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(s -> {
                        register(s);
                        if (s.getOwner() != null) {
                            //getInstance().owners.put(s, s.getOwner());
                        }
                    });

                    Platform.runLater(() -> {
                        //updateRegistry();
                    });
                }
            }
        }

    }

    public static void unregister(Window window) {
        if (getInstance().getExcluded().contains(window)) {
            getInstance().getExcluded().remove(window);
        }

        if (window instanceof Stage) {
            return;
        }
        getInstance().windows.remove(window);
        //getInstance().owners.clear(window);
        Platform.runLater(() -> {
            // getInstance().updateRegistry();
        });
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
        while (win != null) {
            if (!(win instanceof PopupWindow) && !(win instanceof Stage)) {
                break;
            } else if ((win instanceof Stage) && ((Stage) win).getOwner() == parent) {
                retval = true;
                break;
            }

            if ((win instanceof PopupWindow) && ((PopupWindow) win).getOwnerWindow() == parent) {
                retval = true;
                break;
            } else if ((win instanceof Stage) && ((Stage) win).getOwner() == parent) {
                retval = true;
                break;
            }

            if ((win instanceof PopupWindow) && ((PopupWindow) win).getOwnerWindow() == parent) {
                win = ((PopupWindow) win).getOwnerWindow();
            } else if ((win instanceof Stage)) {
                win = ((Stage) win).getOwner();
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

    public Window getTopWindow(double x, double y, Window excl) {
        Window retval = null;
        List<Window> allWindows = getWindows(x, y, excl);
        if (allWindows.isEmpty()) {
            return null;
        }
        List<Window> targetStages = new ArrayList<>();
        allWindows.forEach(w -> {
            Node topNode = TopNodeHelper.getTop(w, x, y, n -> {
                return (n instanceof Node);
            });
            if (topNode != null) {
                targetStages.add(w);
            }
        });
        for (Window w : targetStages) {
            retval = w;
            for (Window w2 : allWindows) {
                if (w == w2) {
                    continue;
                }
                if (w != DockRegistry.this.getTarget(w, w2)) {
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

    public Window getTarget(double x, double y, Window excl) {
        Window retval = null;
        List<Window> allWindows = getWindows(x, y, excl);
        if (allWindows.isEmpty()) {
            return null;
        }
        List<Window> targetStages = new ArrayList<>();
        allWindows.forEach(w -> {
            Node topNode = TopNodeHelper.getTop(w, x, y, n -> {
                return isDockLayout(n);
            });
            if (topNode != null) {
                targetStages.add(w);
            }
        });

        for (Window w : targetStages) {
            retval = w;
            for (Window w2 : allWindows) {
                if (w == w2) {
                    continue;
                }
                if (w != DockRegistry.this.getTarget(w, w2)) {
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
            Node topNode = TopNodeHelper.getTop(s, x, y, n -> {
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

    public Window getTarget(Window w1, Window w2) {
        Window retval = null;

        //Window s = w1;
        boolean b1 = false;
        boolean b2 = false;

        if (w1 instanceof PopupWindow) {
            b1 = true;
        } else if ((w1 instanceof Stage)) {
            b1 = ((Stage) w1).isAlwaysOnTop();
        }
        if (w2 instanceof PopupWindow) {
            b2 = true;
        } else if ((w2 instanceof Stage)) {
            b2 = ((Stage) w2).isAlwaysOnTop();
        }

        if (isChild(w1, w2)) {
            //
            //retval must be null w2 is a child window of w1
            //

        } else if (isChild(w2, w1)) {
            retval = w1;
        } else if (zorder(w1) < zorder(w2) && !b1 && !b2) {
            retval = w1;
        } else if (zorder(w1) < zorder(w2) && b1 && b2) {
            retval = w1;
        } else if (b1 && !b2) {
            retval = w1;
        } else if (!b1 && b2) {
        }
        return retval;
    }

    public static List<Window> getWindows(double x, double y, Window excl) {
        List<Window> retlist = new ArrayList<>();
        StageHelper.getStages().forEach(s -> {
            if (!((x < s.getX() || x > s.getX() + s.getWidth()
                    || y < s.getY() || y > s.getY() + s.getHeight()))) {
                if (s != excl && !getInstance().getExcluded().contains(s)) {
                    retlist.add(s);
                }
            }
        });
        getInstance().windows.forEach(s -> {
            if (!(s instanceof Stage)) {
                if (!((x < s.getX() || x > s.getX() + s.getWidth()
                        || y < s.getY() || y > s.getY() + s.getHeight()))) {
                    if (s != excl && !getInstance().getExcluded().contains(s)) {
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
        if (isDockable(node)) {
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

//    public void unregisterDockable(Node node) {
//        node.getProperties().remove(Dockable.DOCKABLE_KEY);
//    }
    public static void unregisterDockable(Object obj) {
        if ( (obj instanceof Dockable) || Dockable.of(obj) == null ) {
            return;
        }
//        Node dn = Dockable.of(obj).getContext().getDragNode();
        //Dockable.of(obj).getContext().setDragNode(null);
        
        Dockable.of(obj).getContext().reset();
//        Dockable.of(obj).getContext().setLayoutContext(null);
//        Dockable.of(obj).getContext().setDragNode(dn);
        
        Dockable.of(obj).node().getProperties().remove(Dockable.DOCKABLE_KEY);
    }
    public static void unregisterDockLayout(Object obj) {
        if ( (obj instanceof DockLayout) || DockLayout.of(obj) == null ) {
            return;
        }
        DockLayout.of(obj).getLayoutContext().reset();
        DockLayout.of(obj).layoutNode().getProperties().remove(DockLayout.DOCKLAYOUTS_KEY);
        
        
    }
    
    public static DockLayout makeDockLayout(Node node, LayoutContext layoutContext) {

        if (node instanceof DockLayout) {
            return (DockLayout) node;
        }
        if (getInstance().dockLayouts.get(node) != null) {
            return getInstance().dockLayouts.get(node);
        }
        DockLayout d = new DefaultDockLayout(node, layoutContext);
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

    public DockLayout makeDockLayout(Node node) {
        if (isDockLayout(node)) {
            return dockLayout(node);
        }
        LayoutContextFactory f = new LayoutContextFactory();
        LayoutContext c = f.getContext(node);
        if (c == null) {
            return null;
        }
        DockLayout dt = new DefaultDockLayout(node, c);
        register(dt);
        return dt;
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
        return d;
    }

    public static boolean isDockable(Object obj) {
        if (obj instanceof Dockable) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        return getInstance().isNodeDockable((Node) obj);
    }

    public static Dockable dockable(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Dockable) {
            return (Dockable) obj;
        }
        if (!(obj instanceof Node)) {
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

/*    public static boolean instanceOfDockLayout(Node node) {
        return getInstance().isNodeDockLayout(node);
    }
*/
    public static boolean isDockLayout(Object obj) {
        if (obj instanceof DockLayout) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        return getInstance().isNodeDockLayout((Node) obj);
    }

    private boolean isNodeDockLayout(Node node) {
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

    public static DockLayout dockLayout(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof DockLayout) {
            return (DockLayout) obj;
        }
        DockLayout retval = getInstance().dockLayouts.get(obj);
        if (retval == null && (obj instanceof Node)) {
            Object d = ((Node) obj).getProperties().get(DockLayout.DOCKLAYOUTS_KEY);
            if (d != null && (d instanceof DockLayout) && ((DockLayout) d).layoutNode() == obj) {
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
