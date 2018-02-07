package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.vns.javafx.dock.api.ContextLookup;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.save.DockTreeItemBuilderFactory;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPane extends TabPane implements Dockable, DockTarget {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    //private final StringProperty title = new SimpleStringProperty();
    private final DockableContext dockableContext = new DockableContext(this);

//    private Label dragLabel;
//    private Button dragButton;
    private Node dragShape;

    //private final Map<Dockable, Object> listeners = new HashMap<>();
    private TabPaneContext paneContext;

    public DockTabPane() {
        init();
    }

    private void init() {
        paneContext = new TabPaneContext(this);
//        paneContext.getLookup().add(new IndicatorPopup(paneContext));
        dragShape = createDefaultDragNode();
        getChildren().add(dragShape);
        dockableContext.setDragNode(dragShape);
        dragShape.setLayoutX(4);
        dragShape.setLayoutY(4);

        Platform.runLater(() -> {
            dragShape.toFront();
        });

        getStyleClass().add("dock-tab-pane");
        getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

        getDockableContext().setTitleBar(new DockTitleBar(this));

        setRotateGraphic(true);

    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    protected Node createDefaultDragNode() {
        DropShadow ds = new DropShadow();
        ds.setOffsetY(1.0);
        ds.setOffsetX(1.0);
        ds.setColor(Color.GRAY);
        Circle c = new Circle();
        c.setEffect(ds);
        c.setCenterX(0);
        c.setCenterY(0);

        //c.setRadius(30.0f);
        c.setRadius(3.0f);
        c.setFill(Color.WHITE);
        return c;
    }

    public void setDragNode(Node dragNode) {
        dockableContext.setDragNode(dragNode);
    }

    /*    public void openDragTag() {
        openDragTag(0);
    }
     */
    public StringProperty titleProperty() {
        return getDockableContext().titleProperty();
    }

    public DockableContext getNodeContext() {
        return dockableContext;
    }

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableContext getDockableContext() {
        return this.dockableContext;
    }

    @Override
    public Region target() {
        return this;
    }

    @Override
    public TargetContext getTargetContext() {
        return paneContext;
    }

    public void dock(Dockable dockable) {
        paneContext.doDock(0, dockable.node());
    }

    public void dock(int idx, Dockable dockable) {
        if (!getTargetContext().isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }

        if (dockable.getDockableContext().getTargetContext() != null) {
            dockable.getDockableContext().getTargetContext().undock(dockable.node());
        }
        paneContext.doDock(idx, dockable.node());
    }

    public static class TabPaneHelper {

        private TabPane tabPane;

        public TabPaneHelper(TabPane tabPane) {
            this.tabPane = tabPane;
        }

        protected int contentIndexOf(double x, double y) {
            int retval = -1;
            if (tabPane.getTabs().isEmpty()) {
                return retval;
            }
            for (int i = 0; i < tabPane.getTabs().size(); i++) {
                if (tabPane.getTabs().get(i).getContent() == null) {
                    continue;
                }
                if (tabPane.getTabs().get(i).getContent().localToScreen(tabPane.getTabs().get(i).getContent().getBoundsInLocal()).contains(x, y)) {
                    retval = i;
                    break;
                }
            }
            return retval;
        }

        protected List<Node> getTabGraphics() {
            List<Node> list = new ArrayList<>();
            for (int i = 0; i < tabPane.getTabs().size(); i++) {
                Node node = tabPane.getTabs().get(i).getGraphic();
                if (node == null || node.localToScreen(node.getBoundsInLocal()) == null) {
                    //list.add(null);
                    String ID = "abc-123-cba-321";

                    Label lb = new Label();
                    lb.setId(ID);
                    tabPane.getTabs().get(i).setGraphic(lb);
                    list.add(tabPane.getTabs().get(i).getGraphic());

                } else {
                    list.add(node);
                }
            }
            return list;

        }

        protected int indexOf(double x, double y) {

            if (!(tabPane.localToScreen(tabPane.getBoundsInLocal()).contains(x, y)
                    && contentIndexOf(x, y) < 0)) {
                return -1;
            }
            //
            // Now we know that a mouse cursor points anywhere inside tab area
            //
            List<Bounds> list = screenBounds();

            int idx = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).contains(x, y)) {
                    idx = i;
                    break;
                }
            }

            if (idx < 0) {
                idx = tabPane.getTabs().size();
            }
            return idx;

        }

        protected double getTabAreaHeight() {

            double retval = 0;
            List<Node> list = getTabGraphics();
            if (list.isEmpty()) {
                return retval;
            }
            retval = tabPane.getHeight() - getContentHeight();
            return retval;

        }

        protected double getContentHeight() {
            double retval = 0;
            if (tabPane.getTabs().size() > 0) {
                for (int i = 0; i < tabPane.getTabs().size(); i++) {
                    Node g = tabPane.getTabs().get(i).getGraphic();
                    if (g == null || g.localToScreen(g.getBoundsInLocal()) == null) {
                        //System.err.println("GGGGGGGGGGGGGGGGGGGGGGGGG NUL");                    
                        continue;
                    }
                    Node node = tabPane.getTabs().get(i).getContent();
                    if ((node instanceof Region) && ((Region) node).getHeight() > retval) {
                        retval = ((Region) node).getHeight();
                    }
                }
            }
            return retval;
        }

        protected List<Bounds> screenBounds() {
            List<Node> grList = getTabGraphics();
            List<Bounds> list = new ArrayList<>();
            double H = getTabAreaHeight();
            double Y = 0;

            if (grList.isEmpty()) {
                return list;
            }
            String ID = "abc-123-cba-321";

            Y = grList.get(0).localToScreen(grList.get(0).getBoundsInLocal()).getMinY() - 5;
            H = tabPane.localToScreen(0, H).getY() - Y;

            for (int i = 0; i < grList.size(); i++) {
                Bounds b = grList.get(i).localToScreen(grList.get(i).getBoundsInLocal());
                b = new BoundingBox(b.getMinX(), Y, b.getWidth(), H);
//            System.err.println("bbbbbbbbbb i = " + i + ") b=" + b);
                list.add(b);
            }

            for (int i = 0; i < list.size(); i++) {
                Bounds b = list.get(i);
                boolean cond = ID.equals(tabPane.getTabs().get(i).getGraphic().getId());
                if (cond && i == 0 && i + 1 < list.size()) {

                    b = new BoundingBox(b.getMinX(), Y, list.get(i + 1).getMinX() - b.getMinX(), H);
                    list.set(i, b);
                } else if (cond && i > 0 && i + 1 < list.size()) {
                    b = new BoundingBox(b.getMinX(), Y, list.get(i + 1).getMinX() - b.getMinX(), H);
                    list.set(i, b);
                } else if (cond && i > 0 && i + 1 == list.size()) {
                    //
                    // last tab and is not Dockable
                    //
                    b = new BoundingBox(b.getMinX(), Y, 75, H);
                    list.set(i, b);
                } else if (cond && i == 0) {
                    b = new BoundingBox(b.getMinX(), Y, list.get(i + 1).getMinX() - b.getMinX(), H);
                    list.set(i, b);
                }
            }
            return list;
        }

        protected Bounds screenBounds(double x, double y) {

            List<Node> grList = getTabGraphics();
            //List<Bounds> list = new ArrayList<>();
            double H = getTabAreaHeight();
            double Y = 0;

            Bounds tabPaneBounds = tabPane.localToScreen(tabPane.getBoundsInLocal());
            if (!(tabPane.localToScreen(tabPane.getBoundsInLocal()).contains(x, y)
                    && contentIndexOf(x, y) < 0)) {
                return null;
            }

            List<Bounds> list = screenBounds();

            if (list == null || list.isEmpty()) {
                return new BoundingBox(tabPaneBounds.getMinX(), tabPaneBounds.getMinY(), 75, H);
            }
            String ID = "abc-123-cba-321";

            Y = grList.get(0).localToScreen(grList.get(0).getBoundsInLocal()).getMinY() - 5;
            H = tabPane.localToScreen(0, H).getY() - Y;

            Bounds retval = null;
            for (int i = 0; i < list.size(); i++) {
//            System.err.println("bbbbbbbbbb i = " + i + ") b=" + list.get(i) + "; x=" + x + "; y=" + y);

                if (list.get(i).contains(x, y)) {
                    retval = list.get(i);
                    //System.err.println("SXEEEN BOUNDS bbbbbbbbbbbbbbbbbb i=" + i);
                    break;
                }
            }
            if (retval == null) {
                retval = new BoundingBox(tabPaneBounds.getMinX() + tabPaneBounds.getWidth() - 75, Y, 75, H);
            }
            return retval;

        }

    }

    public static class TabPaneContext extends TargetContext {

        public static final String SAVE_DRAGNODE_PROP = "UUID-100b8c98-1b22-4f18-959e-66c16aa3a588";
        //private Node saveDragNode;
        //private PositionIndicator positionIndicator;
        private TabPaneHelper helper;

        public TabPaneContext(Node tabPane) {
            super((Region) tabPane);
            init();
        }

        private void init() {
            helper = new TabPaneHelper(getTargetNode());
            getTargetNode().getTabs().addListener(new ListChangeListener<Tab>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Tab> change) {
                    while (change.next()) {
                        if (change.wasRemoved()) {
                            List<? extends Tab> list = change.getRemoved();
                            for (Tab d : list) {
                                if (d.getContent() != null && Dockable.isDockable(d.getContent())) {
                                    undock(d.getContent());
                                }
                            }

                        }
                        if (change.wasAdded()) {
                            for (int i = change.getFrom(); i < change.getTo(); i++) {
                                Node node = change.getList().get(i).getContent();
                                if (node != null && DockRegistry.isDockable(node)) {
                                    commitDock(i, change.getList().get(i));
                                    commitDock(node);
                                }
                            }
                        }
                    }//while
                }

            });

            //getLookup().putUnique(PositionIndicator.class,new TabPanePositonIndicator(this));
            // getLookup().putUnique(IndicatorPopup.class,new IndicatorPopup(this));
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            super.initLookup(lookup);
            lookup.putUnique(PositionIndicator.class, new TabPanePositonIndicator(this));
            lookup.add(new DockTreeItemBuilderFactory());
        }

        public TabPaneHelper getHelper() {
            return helper;
        }

        @Override
        public TabPane getTargetNode() {
            return (TabPane) super.getTargetNode();
        }

        /**
         * For test purpose
         *
         * @return th elis of dockables
         */
        public ObservableList<Dockable> getDockables() {
            List<Dockable> list = FXCollections.observableArrayList();
            getTargetNode().getTabs().forEach(tab -> {
                if (tab.getContent() != null && DockRegistry.instanceOfDockable(tab.getContent())) {
                    list.add(Dockable.of(tab.getContent()));
                }
            });
            return (ObservableList<Dockable>) list;
        }

        @Override
        protected boolean isDocked(Node node) {
            boolean retval = false;
            for (Tab tb : getTargetNode().getTabs()) {
                if (tb.getContent() == node) {
                    retval = true;
                    break;
                }
            }
            return retval;
        }

        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            Window stage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                //stage = (Stage) node.getScene().getWindow();
                stage = node.getScene().getWindow();
            }

            Dockable dockable = Dockable.of(node);
            TabPane tabPane = (TabPane) getTargetNode();
            //TabGraphic tabGraphic = new TabGraphic(dockable, tabPane);
            int idx = -1;
            if (mousePos != null) {
                idx = helper.indexOf(mousePos.getX(), mousePos.getY());
                System.err.println("doDock:idx = " + idx);
// ??? ERROR when                 only if (idx == 0 ) {
                if (idx == 0 && !tabPane.getTabs().isEmpty()) {
//                if (idx == 0) {
                    //if (tabPane.getTabs().get(0).getGraphic() == helper.getDragLabel()) {
                    //idx++;
                    //}

                }
            }
            if (idx < 0 && tabPane.getTabs().size() > 0) {
                return false;
            }
            if (idx < 0 && mousePos != null && !tabPane.localToScreen(tabPane.getBoundsInLocal()).contains(mousePos.getX(), mousePos.getY())) {
                return false;
            }
            String txt = getButtonText(dockable);
            if (txt.isEmpty()) {
                txt = " ... ";
            }

            Tab newTab = new Tab();
            Label tabLabel = new Label(txt);
            newTab.setGraphic(tabLabel);

            if (idx >= 0) {
                tabPane.getTabs().add(idx, newTab);
                tabPane.getTabs().get(idx).setContent(node);
            } else {
                tabPane.getTabs().add(newTab);
                tabPane.getTabs().get(tabPane.getTabs().indexOf(newTab)).setContent(node);
            }
            if (stage != null) {
                if (stage instanceof Stage) {
                    ((Stage) stage).close();
                } else {
                    stage.hide();
                }
            }

            hideContentTitleBar(dockable);
            tabPane.getSelectionModel().select(newTab);
            //03.02((Region) node).prefHeightProperty().bind(tabPane.heightProperty());
            //03.02((Region) node).prefWidthProperty().bind(tabPane.widthProperty());

            if (DockRegistry.instanceOfDockable(node)) {
                DockableContext dockableContext = Dockable.of(node).getDockableContext();
                //node.getProperties().put(SAVE_DRAGNODE_PROP, dockableContext.getDragNode());
                Node saveDragNode = dockableContext.getDragNode();
                
                System.err.println("SAVE DRAGNODE getDragNode()=" + dockableContext.getDragNode() + "; node=" + dockableContext.dockable().node());
                dockableContext.setDragNode(newTab.getGraphic());
                if (dockableContext.getTargetContext() == null || dockableContext.getTargetContext() != this) {
                    dockableContext.setTargetContext(this);
                }
                dockableContext.targetContextProperty().addListener(new TabPaneContextListener(saveDragNode, dockableContext));                  
                //
                // We must restore draggedNode/ We do it on change target context
                //
                

            }
            return true;
        }

        public class TabPaneContextListener implements ChangeListener<TargetContext> {

            private Node saveDragNode;
            private DockableContext dockableContext;

            public TabPaneContextListener(Node saveDragNode, DockableContext dockableContext) {
                this.saveDragNode = saveDragNode;
                this.dockableContext = dockableContext;
            }

            @Override
            public void changed(ObservableValue<? extends TargetContext> observable, TargetContext oldValue, TargetContext newValue) {
                System.err.println("CHANGED dockableContext");                                    
                if (newValue != TabPaneContext.this) {
                System.err.println("CHANGED DRAGNODE getDragNode()=" + dockableContext.getDragNode() + "; saveNode=" + saveDragNode);                    
                    dockableContext.setDragNode(saveDragNode);
                }
                if ( oldValue != null ) {
                    dockableContext.targetContextProperty().removeListener(this);
                }
            }

        }

        public boolean doDock(int idx, Node node) {
            if (idx < 0) {
                return false;
            }
            Stage stage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                stage = (Stage) node.getScene().getWindow();
            }

            Dockable dockable = Dockable.of(node);
            TabPane tabPane = (TabPane) getTargetNode();

            String txt = getButtonText(dockable);
            if (txt.isEmpty()) {
                txt = " ... ";
            }

            Tab newTab = new Tab();
            Label tabLabel = new Label(txt);
            newTab.setGraphic(tabLabel);
            tabPane.getTabs().add(idx, newTab);
            tabPane.getTabs().get(idx).setContent(node);

            if (stage != null) {
                stage.close();
            }

            hideContentTitleBar(dockable);
            tabPane.getSelectionModel().select(newTab);
            //03.02((Region) node).prefHeightProperty().bind(tabPane.heightProperty());
            //03.02((Region) node).prefWidthProperty().bind(tabPane.widthProperty());

            if (DockRegistry.instanceOfDockable(node)) {
                DockableContext nodeHandler = Dockable.of(node).getDockableContext();
                nodeHandler.setDragNode(newTab.getGraphic());
                if (nodeHandler.getTargetContext() == null || nodeHandler.getTargetContext() != this) {
                    nodeHandler.setTargetContext(this);
                }
            }
            return true;
        }

        protected void commitDock(int idx, Tab tab) {
            if (idx < 0) {
                return;
            }
            Node node = null;
            if (tab.getContent() != null && DockRegistry.isDockable(tab.getContent())) {
                node = tab.getContent();
            } else {
                return;
            }
            Stage stage = null;

            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                stage = (Stage) node.getScene().getWindow();
            }

            Dockable dockable = Dockable.of(node);
            TabPane tabPane = (TabPane) getTargetNode();

            String txt = getButtonText(dockable);
            if (txt.isEmpty()) {
                txt = " ... ";
            }

            Label tabLabel = new Label(txt);
            tab.setGraphic(tabLabel);

            if (stage != null) {
                stage.close();
            }

            hideContentTitleBar(dockable);
            tabPane.getSelectionModel().select(tab);
            //02.02((Region) node).prefHeightProperty().bind(tabPane.heightProperty());
            //03.02((Region) node).prefWidthProperty().bind(tabPane.widthProperty());

            if (DockRegistry.instanceOfDockable(node)) {
                DockableContext nodeHandler = Dockable.of(node).getDockableContext();
                nodeHandler.setDragNode(tab.getGraphic());
                if (nodeHandler.getTargetContext() == null || nodeHandler.getTargetContext() != this) {
                    nodeHandler.setTargetContext(this);
                }
            }
        }

        public void dock(Dockable dockable) {
            if (doDock(null, dockable.node())) {
               // dockable.getDockableContext().setFloating(false);
            }
        }

        public void dock(Node node) {
            if (DockRegistry.isDockable(node)) {
                dock(Dockable.of(node));
            }
        }

        /*        @Override
        public PositionIndicator getPositionIndicator() {
            if (positionIndicator == null) {
                createPositionIndicator();
            }
            return positionIndicator;
        }
         */
        protected String getButtonText(Dockable d) {
            String txt = d.getDockableContext().getTitle();
            if (d.getDockableContext().getProperties().getProperty("user-title") != null) {
                txt = d.getDockableContext().getProperties().getProperty("user-title");
            } else if (d.getDockableContext().getProperties().getProperty("short-title") != null) {
                txt = d.getDockableContext().getProperties().getProperty("short-title");
            } else if (d.node().getId() != null && d.node().getId().isEmpty()) {
                txt = d.node().getId();
            }
            if (txt == null || txt.trim().isEmpty()) {
                txt = "";
            }
            return txt;
        }

        public void saveContentTitleBar(Dockable dockable) {
            Region tb = dockable.getDockableContext().getTitleBar();
            if (tb == null) {
                return;
            }
            tb.getProperties().put("titleBarVisible", tb.isVisible());
            tb.getProperties().put("titleBarMinHeight", tb.getMinHeight());
            tb.getProperties().put("titleBarPrefHeight", tb.getPrefHeight());
            dockable.node().getProperties().put("titleBar", tb);
            dockable.getDockableContext().setTitleBar(null);
        }

        protected void hideContentTitleBar(Dockable dockable) {
            Region tb = dockable.getDockableContext().getTitleBar();
            if (tb == null) {
                return;
            }
            saveContentTitleBar(dockable);
        }

        public void showContentTitleBar(Dockable dockable) {
            Region tb = (Region) dockable.node().getProperties().get("titleBar");
            if (tb == null) {
                return;
            }
            dockable.getDockableContext().setTitleBar(tb);
        }

        @Override
        public void remove(Node dockNode) {
            Tab tab = null;
            for (Tab tb : getTargetNode().getTabs()) {
                if (tb.getContent() == dockNode) {
                    tab = tb;
                    break;
                }
            }
            if (tab != null) {
                showContentTitleBar(Dockable.of(dockNode));
                getTargetNode().getTabs().remove(tab);
            }
        }

        @Override
        public Object getRestorePosition(Dockable dockable) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void restore(Dockable dockable, Object restoreposition) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }//class TabPaneContext

    public static class TabPanePositonIndicator extends PositionIndicator {

        private Rectangle tabDockPlace;
        //private TargetContext targetContext;

        public TabPanePositonIndicator(TargetContext context) {
            super(context);
            //this.targetContext = targetContext;
        }

        @Override
        public void showIndicator(double screenX, double screenY) {
            //getIndicatorPopup().show(getTargetContext().getTargetNode(), screenX, screenY);
            getTargetContext().getLookup().lookup(IndicatorPopup.class).show(getTargetContext().getTargetNode(), screenX, screenY);
        }

        @Override
        protected Pane createIndicatorPane() {
            Pane p = new Pane();
            p.getStyleClass().add("drag-pane-indicator");
            return p;
        }

        //@Override
        protected String getStylePrefix() {
            return "dock-indicator";
        }

        protected Rectangle getTabDockPlace() {
            if (tabDockPlace == null) {
                tabDockPlace = new Rectangle();
                tabDockPlace.getStyleClass().add("dock-place");
                getIndicatorPane().getChildren().add(tabDockPlace);
            }
            return tabDockPlace;
        }

        @Override
        public void hideDockPlace() {
            getDockPlace().setVisible(false);
            getTabDockPlace().setVisible(false);

        }

        @Override
        public void showDockPlace(double x, double y) {
            TabPaneContext ctx = ((TabPaneContext) getTargetContext());
            TabPane pane = (TabPane) getTargetContext().getTargetNode();
            Bounds tabBounds = ctx.getHelper().screenBounds(x, y);
            if (tabBounds == null) {
                ((Rectangle) getDockPlace()).setVisible(false);
                ((Rectangle) getTabDockPlace()).setVisible(false);
                return;
            }

            double tabsHeight = ctx.getHelper().getTabAreaHeight();

            Rectangle dockPlace = (Rectangle) getDockPlace();
            Rectangle tabPlace = (Rectangle) getTabDockPlace();

            dockPlace.setWidth(pane.getWidth());
            dockPlace.setHeight(pane.getHeight() / 2);
            Point2D p = dockPlace.localToParent(0, 0);

            dockPlace.setX(p.getX());
            dockPlace.setY(p.getY() + tabsHeight);

            dockPlace.setVisible(true);
            dockPlace.toFront();

            if (!pane.getTabs().isEmpty()) {
                tabPlace.setWidth(75);
                Bounds b = tabPlace.getParent().screenToLocal(tabBounds);
                tabPlace.setHeight(b.getHeight());
                //
                // idx may be equal to size => the mouse is after last tab
                //
                tabPlace.setX(b.getMinX());
                tabPlace.setY(b.getMinY());
                tabPlace.setVisible(true);
                tabPlace.toFront();
            } else {
                tabPlace.setVisible(false);
            }

            tabPlace.strokeDashOffsetProperty().set(0);
            if (tabPlace.isVisible()) {
                Timeline placeTimeline = new Timeline();
                placeTimeline.setCycleCount(Timeline.INDEFINITE);
                KeyValue kv = new KeyValue(tabPlace.strokeDashOffsetProperty(), 12);
                KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
                placeTimeline.getKeyFrames().add(kf);
                placeTimeline.play();
            }
        }
    }
}//DockTabPane
