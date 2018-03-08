package org.vns.javafx.dock.api;

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
        lookup = new DefaultContextLookup();
        init();
    }

    public ContextLookup getLookup() {
        return lookup;
    }

    private void init() {
        windows.addListener(this::windowsChanged);
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
                //for (int i = change.getFrom(); i < change.getTo(); i++) {
/*                for (int i = 0; i  < list.size() ;  i++) {
                    final Window w = list.get(i);
                    list.get(i).focusedProperty().addListener((o, ov,nv) -> {
                        //System.err.println("DockRegistry FOCUSED WIN = " + w + "; id=" + w.getScene().getRoot().getId());
                    });
                }
                 */
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
            if (window instanceof PopupWindow) {
                PopupWindow p = (PopupWindow) window;
                if (p.getOwnerWindow() != null) {
                    ///System.err.println("DockRegistry: key=" + window + "; owner=" + p.getOwnerWindow());
                    //getInstance().owners.put(window, p.getOwnerWindow());
                }
            }
            Platform.runLater(() -> {
                //getInstance().updateRegistry();
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
                //getInstance().updateRegistry();
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

    protected void updateRegistry() {

        StageHelper.getStages().forEach(s -> {
            if (s.getOwner() != null) {
                int idx1 = StageHelper.getStages().indexOf(s);
                int idx2 = StageHelper.getStages().indexOf(s.getOwner());
                if (idx1 < idx2) {
                    //owners.clear(s);
                }
            }
        });
        for (Window w : windows) {
            if (w instanceof Stage) {
                continue;
            }
            if (!(w instanceof PopupWindow)) {
                continue;
            }
            PopupWindow p = (PopupWindow) w;
            if (p.getOwnerWindow() != null) {
                int idx1 = windows.indexOf(w);
                int idx2 = windows.indexOf(p.getOwnerWindow());
                if (idx1 < idx2) {
                    //System.err.println("DockRegistry: clear " + w);
                    //owners.clear(w);
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
        /*        System.err.println("DockRegistry: isChild 1(parent,child)");
        System.err.println("    --- parent.id  = " + parent.getScene().getRoot().getId());
        System.err.println("    --- child.id = " + child.getScene().getRoot().getId());
         */
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

    public Window getTopWindow(double x, double y, Window excl) {
        Window retval = null;
        List<Window> allWindows = getWindows(x, y, excl);
        if (allWindows.isEmpty()) {
            return null;
        }
        List<Window> targetStages = new ArrayList<>();
        allWindows.forEach(w -> {
            Node topNode = TopNodeHelper.getTopNode(w, x, y, n -> {
                return (n instanceof Node);
            });
            if (topNode != null) {
                targetStages.add(w);
            }
        });
        for (Window w : targetStages) {
//            System.err.println("DockRegistry: w of targetStages = " + w);            
            retval = w;
            for (Window w2 : allWindows) {
//                System.err.println("DockRegistry: w2 of allWindows = " + w2);            
                if (w == w2) {
                    continue;
                }
//                System.err.println("DockRegistry: getTopWindow before getTarget()");
//                System.err.println("    --- w.id  = " + w.getScene().getRoot().getId());
//                System.err.println("    --- w2.id = " + w2.getScene().getRoot().getId());

                if (w != DockRegistry.this.getTarget(w, w2)) {
//                    System.err.println("   --- AFTER getTarget() w2 = " + w2);
                    retval = null;
                    break;
                }
            }
            if (retval != null) {
                break;
            }
        }
//        System.err.println("DockRegistry: getTopWindow retval = " + retval);
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
            Node topNode = TopNodeHelper.getTopNode(w, x, y, n -> {
                return instanceOfDockLayout(n);
            });
            if (topNode != null) {
                targetStages.add(w);
            }
        });

        for (Window w : targetStages) {
//            System.err.println("DockRegistry: w of targetStages = " + w);            
            retval = w;
            for (Window w2 : allWindows) {
//                System.err.println("DockRegistry: w2 of allWindows = " + w2);            
                if (w == w2) {
                    continue;
                }
//                System.err.println("DockRegistry: getTarget(w, w2) = " +  DockRegistry.this.getTarget(w, w2));            

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
//            System.err.println("DockRegistry:getTarget 1 w1 = " + w1 + "; w2=" + w2);
            //
            //retval must be null w2 is a child window of w1
            //

        } else if (isChild(w2, w1)) {
//            System.err.println("DockRegistry:getTarget 2 w1 = " + w1 + "; w2=" + w2);

            retval = w1;
        } else if (zorder(w1) < zorder(w2) && !b1 && !b2) {
//            System.err.println("DockRegistry:getTarget 3 w1 = " + w1 + "; w2=" + w2);
//System.err.println("   --- retval = " + w1);            

            retval = w1;

        } else if (zorder(w1) < zorder(w2) && b1 && b2) {
//            System.err.println("DockRegistry:getTarget 4 w1 = " + w1 + "; w2=" + w2);
//System.err.println("   --- retval = " + w1);            

            retval = w1;
        } else if (b1 && !b2) {
//            System.err.println("DockRegistry:getTarget 5 w1 = " + w1 + "; w2=" + w2);
//System.err.println("   --- retval = " + w1);            

            retval = w1;
        } else if (!b1 && b2) {
        }
//        System.err.println("DockRegistry getTarget 6 retval = " + retval);

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

    public void unregisterDockable(Node node) {
        node.getProperties().remove(Dockable.DOCKABLE_KEY);
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

    public DockLayout toDockLayout(Node node) {
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
//        if (d.node().getParent() != null) {
//            d.getContext().getLayoutContext().setTargetNode(d.node().getParent());
//        }
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

    public static boolean instanceOfDockLayout(Node node) {
        return getInstance().isNodeDockLayout(node);
    }

    public static boolean isDockLayout(Object obj) {
        if (obj instanceof DockLayout) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        return getInstance().isNodeDockLayout((Node) obj);
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
