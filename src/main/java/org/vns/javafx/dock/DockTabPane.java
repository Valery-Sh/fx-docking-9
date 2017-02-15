package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
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
import org.vns.javafx.dock.api.DockIndicator;
import org.vns.javafx.dock.api.DockNodeController;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.IndicatorPopup;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPane extends TabPane implements Dockable, DockPaneTarget {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private final StringProperty title = new SimpleStringProperty();

    private final DockNodeController nodeController = new DockNodeController(this);

    private Label dragLabel;
    private Button dragButton;
    //private ImageView dragImageView;
    private Node dragShape;

    private Tab dragTab;

    private final Map<Dockable, Object> listeners = new HashMap<>();

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
        nodeController.setDragNode(dragShape);
        dragShape.setLayoutX(4);
        dragShape.setLayoutY(4);

        Platform.runLater(() -> {
            dragShape.toFront();
        });

        getStyleClass().add("dock-tab-pane");
        getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

        paneController = new TabPaneController(this);

        nodeController().setTitleBar(new DockTitleBar(this));

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
        nodeController.setDragNode(dragNode);
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
        return nodeController().titleProperty();
    }

    public DockNodeController getNodeController() {
        return nodeController;
    }

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockNodeController nodeController() {
        return this.nodeController;
    }

    @Override
    public Region pane() {
        return this;
    }

    @Override
    public DockTargetController paneController() {
        return paneController;
    }

    public void dock(Dockable dockable) {
        paneController.dock(dockable);
    }

    public static class TabPaneController extends DockTargetController {

        private DockIndicator dockIndicator;

        public TabPaneController(DockTabPane tabPane) {
            super((Region) tabPane);
            init();

        }

        private void init() {
            setDragPopup(new IndicatorPopup(this));
        }

        @Override
        public TabPane getDockPane() {
            return (TabPane) super.getDockPane();
        }

        @Override
        protected boolean isDocked(Node node) {
            boolean retval = false;
            for (Tab tb : getDockPane().getTabs()) {
                if (tb.getContent() == node) {
                    retval = true;
                    break;
                }
            }

            return retval;
        }

        @Override
        protected void dock(Point2D mousePos, Dockable dockable) {
            if (dockable.nodeController().isFloating()) {
                if (doDock(mousePos, dockable.node())) {
                    dockable.nodeController().setFloating(false);
                }
            }
        }

        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            Stage stage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                stage = (Stage) node.getScene().getWindow();
            }

            Dockable dockable = DockRegistry.dockable(node);
            DockTabPane tabPane = (DockTabPane) getDockPane();
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
                DockNodeController nodeHandler = DockRegistry.dockable(node).nodeController();
                nodeHandler.setDragNode(newTab.getGraphic());
                if (nodeHandler.getPaneController() == null || nodeHandler.getPaneController() != this) {
                    nodeHandler.setPaneController(this);
                }
            }
            return true;
        }

        public void dock(Dockable dockable) {
            if (doDock(null, dockable.node())) {
                dockable.nodeController().setFloating(false);
            }
        }

        @Override
        public DockIndicator getDockIndicator() {
            if (dockIndicator == null) {
                createDockIndicator();
            }
            return dockIndicator;
        }

        public DockIndicator createDockIndicator() {
            dockIndicator = new DockIndicator(this) {
                private Rectangle tabDockPlace;

                @Override
                public void showIndicator(double screenX, double screenY, Region targetNode) {
                    getDragPopup().show(getPaneController().getDockPane(), screenX, screenY);
                }

                @Override
                protected Pane createIndicatorPane() {
                    Pane p = new Pane();
                    p.getStyleClass().add("drag-pane-indicator");
                    return p;
                }

                @Override
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
                    DockTabPane pane = (DockTabPane) getDockPane();
                    int idx = pane.indexOf(x, y);
                    if (idx < 0 && !pane.localToScreen(pane.getBoundsInLocal()).contains(x, y)) {
                        return;
                    }

                    double tabsHeight = pane.getTabAreaHeight(x, y);

                    Rectangle dockPlace = (Rectangle) getDockPlace();
                    dockPlace.setWidth(pane.getWidth());
                    dockPlace.setHeight(pane.getHeight() / 2);
                    Point2D p = dockPlace.localToParent(0, 0);

                    dockPlace.setX(p.getX());
                    dockPlace.setY(p.getY() + tabsHeight);

                    dockPlace.setVisible(true);
                    dockPlace.toFront();

                    Rectangle tabPlace = (Rectangle) getTabDockPlace();
                    tabPlace.setWidth(75);
                    tabPlace.setHeight(tabsHeight);
                    p = tabPlace.localToParent(0, 0);

                    Point2D pt = tabPlace.screenToLocal(x, y);

                    double tabPlaceX = 0;

                    Node node = null;
                    //
                    // idx may be equal to size => the mouse is after last tab
                    //
                    List<Node> tg = pane.getTabGraphics();
                    if (idx >= 0 && idx < tg.size()) {
                        node = tg.get(idx);
                    }
                    int tabsSize = tg.size();
                    if (node != null) {
                        Point2D tabPt = node.localToParent(0, 0);
                        tabPlaceX = tabPt.getX();
                    } else if (tabsSize > 0) {
                        node = tg.get(tabsSize - 1);
                        Point2D tabPt = node.localToParent(node.getBoundsInParent().getWidth(), 0);
                        tabPlaceX = tabPt.getX();
                    }
                    tabPlace.setX(tabPlaceX);
                    tabPlace.setY(p.getY());

                    tabPlace.setVisible(true);
                    tabPlace.toFront();

                }

            };
            return dockIndicator;
        }

        protected String getButtonText(Dockable d) {
            String txt = d.nodeController().getTitle();
            if (d.nodeController().getProperties().getProperty("user-title") != null) {
                txt = d.nodeController().getProperties().getProperty("user-title");
            } else if (d.nodeController().getProperties().getProperty("short-title") != null) {
                txt = d.nodeController().getProperties().getProperty("short-title");
            } else if (d.node().getId() != null && d.node().getId().isEmpty()) {
                txt = d.node().getId();
            }
            if (txt == null || txt.trim().isEmpty()) {
                txt = "";
            }
            return txt;
        }

        public void saveContentTitleBar(Dockable dockable) {
            Region tb = dockable.nodeController().getTitleBar();
            if (tb == null) {
                return;
            }
            tb.getProperties().put("titleBarVisible", tb.isVisible());
            tb.getProperties().put("titleBarMinHeight", tb.getMinHeight());
            tb.getProperties().put("titleBarPrefHeight", tb.getPrefHeight());
        }

        protected void hideContentTitleBar(Dockable dockable) {
            Region tb = dockable.nodeController().getTitleBar();
            if (tb == null) {
                return;
            }
            saveContentTitleBar(dockable);
            tb.setVisible(false);
            tb.setMinHeight(0);
            tb.setPrefHeight(0);
        }

        public void showContentTitleBar(Dockable dockable) {
            Region tb = dockable.nodeController().getTitleBar();
            if (tb == null) {
                return;
            }
            tb.setVisible((boolean) tb.getProperties().get("titleBarVisible"));
            tb.setMinHeight((double) tb.getProperties().get("titleBarMinHeight"));
            tb.setPrefHeight((double) tb.getProperties().get("titleBarPrefHeight"));
        }

        @Override
        public void remove(Node dockNode
        ) {
            Tab tab = null;
            for (Tab tb : getDockPane().getTabs()) {
                if (tb.getContent() == dockNode) {
                    tab = tb;
                    break;
                }
            }
            if (tab != null) {
                showContentTitleBar(DockRegistry.dockable(dockNode));
                getDockPane().getTabs().remove(tab);
            }
        }
    }//class TabPaneController

    protected int indexOf(double x, double y) {
        List<Node> list = getTabGraphics();
        int retval = -1;
        if (list.isEmpty()) {
            return retval;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Region) {
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

    protected double getTabAreaHeight(double x, double y) {
        double retval = 24;
        List<Node> list = getTabGraphics();
        int idx = -1;
        if (list.isEmpty()) {
            return idx;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Region) {
                boolean b = list.get(i).localToScreen(list.get(i).getBoundsInLocal()).contains(x, y);
                if (b) {
                    idx = i;
                    break;
                }
            }
        }

        if (idx < 0
                && localToScreen(getBoundsInLocal()).contains(x, y)
                && contentIndexOf(x, y) < 0) {
            idx = getTabs().size();
        }
        if (idx < 0 && getTabs().size() == 0 && localToScreen(getBoundsInLocal()).contains(x, y)) {

        } else if (idx >= 0) {
            double ch = getContentHeight();
            if (ch > 0) {
                retval = getHeight() - ch;
            }
        }
        return retval;

    }

    protected double getContentHeight() {
        double retval = 0;
        if (getTabs().size() > 0) {
            for (int i = 0; i < getTabs().size(); i++) {
                Node node = getTabs().get(i).getContent();
                if (node instanceof Region) {
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

    /*    public int getItemIndex(double mouseX, double mouseY) {

        int idx = -1;

        if (getTabs().isEmpty() && DockUtil.contains(this,mouseX, mouseY)) {
        
        } else {
            int idx = indexOf(mouseX, mouseY);
            if (idx == 0) {
                if (getTabs().get(0).getGraphic() == getDragLabel()) {
                }
            }
            if (idx >= 0) {
            } else {
            }
        }
        return idx;
    }
     */
    protected List<Node> getTabGraphics() {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < getTabs().size(); i++) {
            Node n = getTabs().get(i).getGraphic();
            if (n == null) {
                list.add(new Label());
            } else {
                list.add((Region) n);
            }
        }
        return list;
    }
    /*    public static class TabPaneDockExecutor {
        private TabPaneController  paneController;
        public TabPaneDockExecutor(TabPaneController paneController) {
            this.paneController = paneController;
        }
        
        protected void dock(Point2D mousePos, Dockable dockable) {
            Node node = dockable.node();
            if (((DockTabPane)paneController.getDockPane()).getItemIndex(mousePos.getX(), mousePos.getY()) < 0) {
                return;
            }
            Dockable d = DockRegistry.dockable(node);
            if (d.nodeController().isFloating()) {
                //doDock(mousePos, DockRegistry.dockable(node));
                if ( doDock(mousePos, node) ) {
                    dockable.nodeController().setFloating(false);
                }
            }
        }
        
        protected void dock(int idx, Dockable d) {
            if (d.node().getScene() != null && d.node().getScene().getWindow() != null && (d.node().getScene().getWindow() instanceof Stage)) {
                ((Stage) d.node().getScene().getWindow()).close();
            }

            Tab tab = ((DockTabPane)paneController.getDockPane()).addTab(idx, d.node());

            DockNodeController nodeController = d.nodeController();
            nodeController.setDragNode(tab.getTitleBar());
            if (nodeController.getPaneController() == null || nodeController.getPaneController() != getPaneController()) {
                nodeController.setPaneController(getPaneController());
            }
            if ( nodeController.isFloating()) {
                nodeController.setFloating(false);
            }

        }
        @Override
        protected void dock(Dockable dockable, Object pos) {
            if ( pos instanceof Side) {
                int idx = ((DockTabPane3)getPaneController().getDockPane()).getTabs().size();
                dock(idx,dockable);
            }
        }
        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            if (!DockRegistry.isDockable(node)) {
                return false;
            }
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }

            Tab tab = ((DockTabPane3)getPaneController().getDockPane()).addTab(mousePos, node);

            DockNodeController nodeController = DockRegistry.dockable(node).nodeController();
            nodeController.setDragNode(tab.getTitleBar());
            if (nodeController.getPaneController() == null || nodeController.getPaneController() != getPaneController()) {
                nodeController.setPaneController(getPaneController());
            }
            return true;
        }
  }    
     */
}//DockTabPane
