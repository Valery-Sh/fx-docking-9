package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
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
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.vns.javafx.dock.api.AbstractDockTreeItemBuilder;
import org.vns.javafx.dock.api.PositionIndicator;
import org.vns.javafx.dock.api.DockableController;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.DockTreeItemBuilder;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPane extends TabPane implements Dockable, DockTarget {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    //private final StringProperty title = new SimpleStringProperty();
    private final DockableController dockableController = new DockableController(this);

    private Label dragLabel;
    private Button dragButton;
    private Node dragShape;

    private Tab dragTab;

    //private final Map<Dockable, Object> listeners = new HashMap<>();
    private TabPaneController paneController;

    public DockTabPane() {
        init();
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    private void init() {
        dragShape = createDefaultDragNode();
        getChildren().add(dragShape);
        dockableController.setDragNode(dragShape);
        dragShape.setLayoutX(4);
        dragShape.setLayoutY(4);

        Platform.runLater(() -> {
            dragShape.toFront();
        });

        getStyleClass().add("dock-tab-pane");
        getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

        paneController = new TabPaneController(this);

        dockableController().setTitleBar(new DockTitleBar(this));

        setRotateGraphic(true);

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

    public void closeDragTag() {
        if (dragTab != null) {
            getTabs().remove(dragTab);
        }
    }

    public void setDragNode(Node dragNode) {
        dockableController.setDragNode(dragNode);
    }

    public void openDragTag() {
        openDragTag(0);
    }

    public void openDragTag(int idx) {
        dragLabel = new Label();
        dragLabel.getStyleClass().add("drag-label");
        dragLabel.setPrefSize(12, 12);
        dragButton = new Button();
        dragLabel.setGraphic(dragButton);
        dragButton.getStyleClass().add("drag-button");
        dragButton.setFocusTraversable(false);
        dragTab = new Tab();
        dragTab.setGraphic(dragLabel);
        getTabs().add(idx, dragTab);
        dragTab.setClosable(false);

        dragTab.setOnSelectionChanged(e -> {
            dragLabel.setGraphic(null);
            dragLabel.setGraphic(dragButton);
        });
        setDragNode(dragButton);
    }

    protected Label getDragLabel() {
        return dragLabel;
    }

    public Tab getDragTab() {
        return dragTab;
    }

    public StringProperty titleProperty() {
        return dockableController().titleProperty();
    }

    public DockableController getNodeController() {
        return dockableController;
    }

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableController dockableController() {
        return this.dockableController;
    }

    @Override
    public Region target() {
        return this;
    }

    @Override
    public DockTargetController targetController() {
        return paneController;
    }

    public void dock(Dockable dockable) {
        paneController.doDock(0, dockable.node());
    }

    public void dock(int idx, Dockable dockable) {
        if (!targetController().isAcceptable(dockable.node())) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }

        if (dockable.dockableController().getTargetController() != null) {
            dockable.dockableController().getTargetController().undock(dockable.node());
        }
        paneController.doDock(idx, dockable.node());
    }

    public static class TabPaneController extends DockTargetController {

        private PositionIndicator positionIndicator;

        public TabPaneController(DockTabPane tabPane) {
            super((Region) tabPane);
            init();

        }

        private void init() {
            //12.05setIndicatorPopup(new IndicatorPopup(this));
        }

        @Override
        public TabPane getTargetNode() {
            return (TabPane) super.getTargetNode();
        }

        @Override
        public DockTreeItemBuilder getDockTreeTemBuilder() {
            return new DockTabPaneTreeItemBuilder((DockTabPane) getTargetNode());
        }

        @Override
        public ObservableList<Dockable> getDockables() {
            List<Dockable> list = FXCollections.observableArrayList();
            getTargetNode().getTabs().forEach(tab -> {
                if (tab.getContent() != null && DockRegistry.isDockable(tab.getContent())) {
                    list.add(DockRegistry.dockable(tab.getContent()));
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

        /*07.05        @Override
        protected void dock(Point2D mousePos, Dockable dockable) {
            if (dockable.dockableController().isFloating()) {
                if (doDock(mousePos, dockable.tab())) {
                    dockable.dockableController().setFloating(false);
                }
            }
        }
         */
        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            Stage stage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                stage = (Stage) node.getScene().getWindow();
            }

            Dockable dockable = DockRegistry.dockable(node);
            DockTabPane tabPane = (DockTabPane) getTargetNode();
            //TabGraphic tabGraphic = new TabGraphic(dockable, tabPane);
            int idx = -1;
            if (mousePos != null) {
                idx = tabPane.indexOf(mousePos.getX(), mousePos.getY());
                if (idx == 0) {
                    if (tabPane.getTabs().get(0).getGraphic() == tabPane.getDragLabel()) {
                        idx++;
                    }
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
                stage.close();
            }

            hideContentTitleBar(dockable);
            tabPane.getSelectionModel().select(newTab);
            ((Region) node).prefHeightProperty().bind(tabPane.heightProperty());
            ((Region) node).prefWidthProperty().bind(tabPane.widthProperty());

            if (DockRegistry.isDockable(node)) {
                DockableController nodeHandler = DockRegistry.dockable(node).dockableController();
                nodeHandler.setDragNode(newTab.getGraphic());
                if (nodeHandler.getTargetController() == null || nodeHandler.getTargetController() != this) {
                    nodeHandler.setTargetController(this);
                }
            }
            return true;
        }

        protected boolean doDock(int idx, Node node) {
            if (idx < 0) {
                return false;
            }
            Stage stage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                stage = (Stage) node.getScene().getWindow();
            }

            Dockable dockable = DockRegistry.dockable(node);
            DockTabPane tabPane = (DockTabPane) getTargetNode();

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
            ((Region) node).prefHeightProperty().bind(tabPane.heightProperty());
            ((Region) node).prefWidthProperty().bind(tabPane.widthProperty());

            if (DockRegistry.isDockable(node)) {
                DockableController nodeHandler = DockRegistry.dockable(node).dockableController();
                nodeHandler.setDragNode(newTab.getGraphic());
                if (nodeHandler.getTargetController() == null || nodeHandler.getTargetController() != this) {
                    nodeHandler.setTargetController(this);
                }
            }
            return true;
        }

        public void dock(Dockable dockable) {
            if (doDock(null, dockable.node())) {
                dockable.dockableController().setFloating(false);
            }
        }

        @Override
        public PositionIndicator getPositionIndicator() {
            if (positionIndicator == null) {
                createPositionIndicator();
            }
            return positionIndicator;
        }

        public PositionIndicator createPositionIndicator() {
            positionIndicator = new PositionIndicator(this) {
                private Rectangle tabDockPlace;

                @Override
                public void showIndicator(double screenX, double screenY) {
                    getIndicatorPopup().show(getTargetController().getTargetNode(), screenX, screenY);
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
                    DockTabPane pane = (DockTabPane) getTargetNode();
                    Bounds tabBounds = pane.screenBounds(x, y);
                    if (tabBounds == null) {
                        ((Rectangle) getDockPlace()).setVisible(false);
                        ((Rectangle) getTabDockPlace()).setVisible(false);
                        return;
                    }

                    double tabsHeight = pane.getTabAreaHeight();

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

            };

            return positionIndicator;
        }

        protected String getButtonText(Dockable d) {
            String txt = d.dockableController().getTitle();
            if (d.dockableController().getProperties().getProperty("user-title") != null) {
                txt = d.dockableController().getProperties().getProperty("user-title");
            } else if (d.dockableController().getProperties().getProperty("short-title") != null) {
                txt = d.dockableController().getProperties().getProperty("short-title");
            } else if (d.node().getId() != null && d.node().getId().isEmpty()) {
                txt = d.node().getId();
            }
            if (txt == null || txt.trim().isEmpty()) {
                txt = "";
            }
            return txt;
        }

        public void saveContentTitleBar(Dockable dockable) {
            Region tb = dockable.dockableController().getTitleBar();
            if (tb == null) {
                return;
            }
            tb.getProperties().put("titleBarVisible", tb.isVisible());
            tb.getProperties().put("titleBarMinHeight", tb.getMinHeight());
            tb.getProperties().put("titleBarPrefHeight", tb.getPrefHeight());
            dockable.node().getProperties().put("titleBar", tb);
            dockable.dockableController().setTitleBar(null);
        }

        protected void hideContentTitleBar(Dockable dockable) {
            Region tb = dockable.dockableController().getTitleBar();
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
            dockable.dockableController().setTitleBar(tb);
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
                showContentTitleBar(DockRegistry.dockable(dockNode));
                getTargetNode().getTabs().remove(tab);
            }
        }
    }//class TabPaneController

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
        H = localToScreen(0, H).getY() - Y;

        for (int i = 0; i < grList.size(); i++) {
            Bounds b = grList.get(i).localToScreen(grList.get(i).getBoundsInLocal());
            b = new BoundingBox(b.getMinX(), Y, b.getWidth(), H);
//            System.err.println("bbbbbbbbbb i = " + i + ") b=" + b);
            list.add(b);
        }

        for (int i = 0; i < list.size(); i++) {
            Bounds b = list.get(i);
            boolean cond = ID.equals(getTabs().get(i).getGraphic().getId());
            if (cond && i == 0 && i + 1 < list.size()) {

                b = new BoundingBox(b.getMinX(), Y, list.get(i + 1).getMinX() -  b.getMinX(), H);
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

        Bounds tabPaneBounds = localToScreen(getBoundsInLocal());
        if (!(localToScreen(getBoundsInLocal()).contains(x, y)
                && contentIndexOf(x, y) < 0)) {
            return null;
        }

        List<Bounds> list = screenBounds();

        if (list == null || list.isEmpty()) {
            return new BoundingBox(tabPaneBounds.getMinX(), tabPaneBounds.getMinY(), 75, H);
        }
        String ID = "abc-123-cba-321";

        Y = grList.get(0).localToScreen(grList.get(0).getBoundsInLocal()).getMinY() - 5;
        H = localToScreen(0, H).getY() - Y;

        Bounds retval = null;
        for (int i = 0; i < list.size(); i++) {
            System.err.println("bbbbbbbbbb i = " + i + ") b=" + list.get(i) + "; x=" + x + "; y=" + y);
            
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

    protected int indexOf(double x, double y) {

        if (!(localToScreen(getBoundsInLocal()).contains(x, y)
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
            idx = getTabs().size();
        }
        return idx;

    }

    protected int indexOf_old(double x, double y) {
        List<Node> list = getTabGraphics();
        int retval = -1;
        if (list.isEmpty()) {
            return retval;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Region) {
                if (list.get(i).localToScreen(list.get(i).getBoundsInLocal()) == null) {
                    continue;
                }
                boolean b = list.get(i).localToScreen(list.get(i).getBoundsInLocal()).contains(x, y);
                if (b) {
                    retval = i;
                    break;
                }
            }
        }
        if (retval < 0
                && localToScreen(getBoundsInLocal()).contains(x, y)
                && contentIndexOf(x, y) < 0) {
            retval = getTabs().size();
        }
        return retval;
    }

    protected double getTabAreaHeight() {

        double retval = 0;
        List<Node> list = getTabGraphics();
        if (list.isEmpty()) {
            return retval;
        }
        retval = getHeight() - getContentHeight();
        return retval;

    }

    protected double getContentHeight() {
        double retval = 0;
        if (getTabs().size() > 0) {
            for (int i = 0; i < getTabs().size(); i++) {
                Node g = getTabs().get(i).getGraphic();
                if (g == null || g.localToScreen(g.getBoundsInLocal()) == null) {
                    //System.err.println("GGGGGGGGGGGGGGGGGGGGGGGGG NUL");                    
                    continue;
                }
                Node node = getTabs().get(i).getContent();
                if ((node instanceof Region) && ((Region) node).getHeight() > retval) {
                    retval = ((Region) node).getHeight();
                }
            }
        }
        return retval;
    }

    protected int contentIndexOf(double x, double y) {
        int retval = -1;
        if (getTabs().isEmpty()) {
            return retval;
        }
        for (int i = 0; i < getTabs().size(); i++) {
            if (getTabs().get(i).getContent() == null) {
                continue;
            }
            if (getTabs().get(i).getContent().localToScreen(getTabs().get(i).getContent().getBoundsInLocal()).contains(x, y)) {
                retval = i;
                break;
            }
        }
        return retval;
    }

    protected List<Node> getTabGraphics() {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < getTabs().size(); i++) {
            Node node = getTabs().get(i).getGraphic();
            if (node == null || node.localToScreen(node.getBoundsInLocal()) == null) {
                //list.add(null);
                String ID = "abc-123-cba-321";

                Label lb = new Label();
                lb.setId(ID);
                getTabs().get(i).setGraphic(lb);
                list.add(getTabs().get(i).getGraphic());

            } else {
                list.add(node);
            }
        }
        return list;

    }

    public static class DockTabPaneTreeItemBuilder extends AbstractDockTreeItemBuilder {

        public DockTabPaneTreeItemBuilder(DockTabPane tabPane) {
            super(tabPane);
        }

        @Override
        public Node restore(TreeItem<Properties> targetRoot) {
            Properties props = targetRoot.getValue();
            if (!(props.get(OBJECT_ATTR) instanceof DockTabPane)) {
                return null;
            }
            DockTabPane pane = (DockTabPane) props.get(OBJECT_ATTR);
            targetRoot.setExpanded(true);

            pane.getTabs().clear();
            for ( int i=0; i < targetRoot.getChildren().size(); i++ ) {
                
                TreeItem<Properties> item = targetRoot.getChildren().get(i);
            //for (TreeItem<Properties> item : targetRoot.getChildren()) {
                Tab tab = (Tab) item.getValue().get(OBJECT_ATTR);
                
                if (tab == null) {
                    tab = buildTab(item);
                    //pane.getTabs().add(tab);
                } 
                
                if ( tab.getContent() == null ) {
                    pane.getTabs().add(i,tab);
                    return pane;
                }
                
                if (DockRegistry.isDockTarget(tab.getContent())) {
                    tab.setContent(restore(item));
                    pane.getTabs().add(i,tab);
                } else if (DockRegistry.isDockable(tab.getContent())) {
                    if (DockRegistry.dockable(tab.getContent()).dockableController().getTargetController() != null) {
                        DockTargetController c = DockRegistry.dockable(tab.getContent()).dockableController().getTargetController();
                        if (c != getDockTarget().targetController()) {
                            c.undock(tab.getContent());
                        }
                    }
                    String fnm = item.getValue().getProperty(FIELD_NAME_ATTR);
                    pane.dock(i, DockRegistry.dockable(tab.getContent()));
                    //pane.getTabs().add(tab);
                } else {
                    pane.getTabs().add(i,tab);
                }
            }
            return pane;
        }

        protected Tab buildTab(TreeItem<Properties> tabItem) {
            Tab tab = (Tab) tabItem.getValue().get(OBJECT_ATTR);
            if (tab == null) {
                tab = buildTabInstance(tabItem);
                tabItem.getValue().put(OBJECT_ATTR, tab);
            }
            if (tabItem.getChildren().isEmpty()) {
                return tab;
            }
            TreeItem<Properties> item = tabItem.getChildren().get(0); 
            item.setExpanded(true);
            Node node = (Node) item.getValue().get(OBJECT_ATTR);
            if (node == null) {
                return tab;
            } 
            if (DockRegistry.isDockTarget(node)) {
                node = restore(item);
                tab.setContent(node);
            } else if (DockRegistry.isDockable(node)) {
                tab.setContent(node);
            }
            return tab;
        }

        protected Tab buildTabInstance(TreeItem<Properties> sourceItem) {
            sourceItem.setExpanded(true);
            Tab tab = new Tab();
            tab.setId(sourceItem.getValue().getProperty("id"));
            return tab;
        }

        /*
        private void setProperties(PreferencesItem it) {
            DockTabPane tabPane = (DockTabPane) it.getItemObject();
            //it.getProperties().put("tabPane-pref-width", String.valueOf(tabPane.getWidth()));
            //it.getProperties().put("tabPane-pref-height", String.valueOf(tabPane.getHeight()));
            it.getProperties().put("tabPane-min-width", String.valueOf(tabPane.getMinWidth()));
            it.getProperties().put("tabPane-max-width", String.valueOf(tabPane.getMaxWidth()));
        }

        public Map<String, String> getProperties(Object node) {
            Map<String, String> props = FXCollections.observableHashMap();
            if (node instanceof DockTabPane) {
                //props.put("tabPane-pref-width", String.valueOf(((DockTabPane) tab).getWidth()));
                //props.put("tabPane-pref-height", String.valueOf(((DockTabPane) tab).getHeight()));
                props.put("tabPane-min-width", String.valueOf(((DockTabPane) node).getMinWidth()));
                props.put("tabPane-max-width", String.valueOf(((DockTabPane) node).getMaxWidth()));
            }
            return props;
        }
         */
        @Override
        public TreeItem<Properties> build(String fieldName) {
            Node node = getDockTarget().target();
            TreeItem<Properties> retval = DockTreeItemBuilder.build(fieldName, node);
            retval.setExpanded(true);
            int sz = retval.getChildren().size();
            //setObjectProperties(retval.getValue());        
            buildChildren(retval);
            return retval;

        }

        protected void buildChildren(TreeItem<Properties> root) {
            //DockPane tab = (DockPane) getTargetController().getTargetNode();
            DockTabPane pane = (DockTabPane) getDockTarget().target();
            for (int i = 0; i < pane.getTabs().size(); i++) {
                TreeItem ti = DockTreeItemBuilder.build(pane.getTabs().get(i));
                root.getChildren().add(ti);
                ti.setExpanded(true);

                notifyOnBuidItem(ti);

                if (DockRegistry.isDockTarget(pane.getTabs().get(i).getContent())) {
                    TreeItem contentItem = DockRegistry.dockTarget(pane.getTabs().get(i).getContent())
                            .targetController()
                            .getDockTreeTemBuilder().build();
                    ti.getChildren().add(contentItem);
                } else if (DockRegistry.isDockable(pane.getTabs().get(i).getContent())) {
                    TreeItem contentItem = DockTreeItemBuilder.build(pane.getTabs().get(i).getContent());
                    ti.getChildren().add(contentItem);
                }
            }
        }

    }
}//DockTabPane
