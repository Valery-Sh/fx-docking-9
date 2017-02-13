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
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockNodeController;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.DragPopup;
import org.vns.javafx.dock.api.IndicatorPopup;
import org.vns.javafx.dock.api.SideIndicator;
import org.vns.javafx.dock.api.SideIndicatorTransformer.NodeIndicatorTransformer;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPaneFX extends TabPane implements Dockable, DockPaneTarget {

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

    public DockTabPaneFX() {
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

    /*    protected void choose(Dockable choosed) {
        getTabList().forEach(tabGraphic -> {
            Dockable d = ((TitleBarSafe) tabGraphic).getOwner();
            TitleBarSafe tb = (TitleBarSafe) tabGraphic;
            if (d != choosed) {
                tb.setSelectedPseudoClass(false);
                d.node().toBack();
            } else {
                tb.setSelectedPseudoClass(true);
                d.node().toFront();
            }
        });
    }

    protected void focusChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        getTabList().forEach(tabGraphic -> {
            Dockable d = ((TitleBarSafe) tabGraphic).getOwner();
            if (newValue && isFocused((Region) tabGraphic)) {
                choose(d);
            } else if (!newValue) {
                d.nodeController().titleBarProperty().setSelectedPseudoClass(false);
            }
        });
    }

    protected boolean isFocused(Region titleBar) {
        boolean retval = false;
        if (titleBar.isFocused()) {
            retval = true;
        } else if (titleBar instanceof DockTitleBar) {
            DockTitleBar tb = (DockTitleBar) titleBar;
            if (tb.getCloseButton().isFocused() || tb.getPinButton().isFocused() || tb.getStateButton().isFocused()) {
                retval = true;
            }
        }
        return retval;
    }
     */
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

    /*    public static class TabGraphic extends DockTitleBar {

        private double titleBarMinHeight;
        private double titleBarPrefHeight;
        private boolean titleBarVisible;

        public TabGraphic(Dockable dockNode, DockTabPane tabPane) {
            super(dockNode);
            //this.tabPane = tabPane;
            init();
        }

        private void init() {
            saveContentTitleBar();
            getStateButton().setMouseTransparent(true);
            removeButtons(getCloseButton(), getPinButton(), getStateButton());

            String txt = getButtonText(getOwner());
            if (txt.trim().isEmpty()) {
                txt = " ... ";
            }

            getChildren().remove(getLabel());
            Label newLabel = new Label(txt);

            setLabel(newLabel);
            getChildren().add(0, getLabel());

            setOnMouseClicked(ev -> {
                getStateButton().requestFocus();
//                tabPane.choose(getOwner());
                ev.consume();
            });
            getStyleClass().add("dock-tab-graphic");

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

        public void saveContentTitleBar() {
            Region tb = getOwner().nodeController().getTitleBar();
            titleBarVisible = tb.isVisible();
            titleBarMinHeight = tb.getMinHeight();
            titleBarPrefHeight = tb.getPrefHeight();

        }

        public void hideContentTitleBar() {
            Region tb = getOwner().nodeController().getTitleBar();
            tb.setVisible(false);
            tb.setMinHeight(0);
            tb.setPrefHeight(0);
        }

        public void showContentTitleBar() {
            Region tb = getOwner().nodeController().getTitleBar();
            tb.setVisible(titleBarVisible);
            tb.setMinHeight(titleBarMinHeight);
            tb.setPrefHeight(titleBarPrefHeight);
        }

    }
     */
    public static class TabPaneController extends DockTargetController {

        public TabPaneController(DockTabPaneFX tabPane) {
            super((Region) tabPane);
            init();

        }

        private void init() {
            //setSidePointerModifier(this::modifyNodeSidePointer);
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
        protected void dock(Point2D mousePos, Node node, IndicatorPopup popup) {
            if (!(popup instanceof DragPopup)) {
                return;
            }
            DragPopup dp = (DragPopup) popup; 
            Dockable d = DockRegistry.dockable(node);
            if (d.nodeController().isFloating() && dp != null && (dp.getTargetNodeSidePos() != null || dp.getTargetPaneSidePos() != null) && dp.getDragTarget() != null) {
                dock(mousePos, node, dp.getTargetNodeSidePos(), dp.getTargetPaneSidePos(), dp.getDragTarget());
            }

        }

        @Override
        public Dockable dock(Dockable node, Side dockPos, Dockable target) {
            return null;
        }

        @Override
        protected boolean doDock(Point2D mousePos, Node node, Side dockPos) {
            Dockable dockable = DockRegistry.dockable(node);
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            DockTabPaneFX tabPane = (DockTabPaneFX) getDockPane();
            //TabGraphic tabGraphic = new TabGraphic(dockable, tabPane);
            int idx = -1;
            if (mousePos != null) {
                idx = tabPane.indexOf(mousePos.getX(), mousePos.getY());
                System.err.println("DOCK: idx=" + idx);
                if (idx == 0) {
                    if (tabPane.getTabs().get(0).getGraphic() == tabPane.getDragLabel()) {
                        idx++;
                    }
                }
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
            System.err.println("save titlebar");
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
        protected NodeIndicatorTransformer createNodeIndicatorTransformer() {
            return new TabSideIndicatorTransformer();
        }

        public static class TabSideIndicatorTransformer extends NodeIndicatorTransformer {

            public TabSideIndicatorTransformer() {
            }

            /**
             * The method does nothing. It overrides the method of the subclass
             * to escape scaling of an indicator pane.
             */
            @Override
            public void notifyPopupShown() {
            }

            @Override
            public Point2D getIndicatorPosition() {

                Point2D retval = null;// = super.mousePos();

                SideIndicator paneIndicator = ((DragPopup)getTargetPaneController().getDragPopup()).getPaneIndicator();
                Pane topBtns;//= null;
                Button topPaneButton = null;
                if (paneIndicator != null) {
   
                    topBtns = paneIndicator.getTopButtons();
                    topPaneButton = (Button) topBtns.getChildren().get(0);
                }

                if (getIndicator().getIndicatorPane().getChildren().contains(getBottomButtons())) {
                    getIndicator().getIndicatorPane().getChildren().clear();
                    if (paneIndicator != null) {
                        paneIndicator.getIndicatorPane().getChildren().remove(paneIndicator.getTopButtons());
                        paneIndicator.getIndicatorPane().getChildren().remove(paneIndicator.getRightButtons());
                        paneIndicator.getIndicatorPane().getChildren().remove(paneIndicator.getBottomButtons());
                        paneIndicator.getIndicatorPane().getChildren().remove(paneIndicator.getLeftButtons());
                        topBtns = paneIndicator.getTopButtons();
                        ((GridPane) getIndicator().getIndicatorPane()).add(topBtns, 0, 1);
                    }
                }

                DockTabPaneFX tabPane = (DockTabPaneFX) getTargetPaneController().getDockPane();

                double mouseX = getMousePos().getX();
                double mouseY = getMousePos().getY();

                if (tabPane.getTabs().isEmpty() && DockUtil.contains(tabPane, mouseX, mouseY)) {
                    retval = centerPosOf(tabPane, topPaneButton);
                    topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, false);
                } else {
                    int idx = tabPane.indexOf(mouseX, mouseY);
                    if (idx == 0) {
                        if (tabPane.getTabs().get(0).getGraphic() == tabPane.getDragLabel()) {
                        }
                    }
                    if (idx >= 0) {
                        retval = new Point2D(mouseX - 5, mouseY - 5);
                        topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
                    } else {
                        topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, false);
                        retval = centerPosOf(tabPane, topPaneButton);
                    }
                }
                return retval;
            }

            private Point2D centerPosOf(DockTabPaneFX tabPane, Region r) {
                double x = tabPane.localToScreen(tabPane.getBoundsInLocal()).getMinX();
                double y = tabPane.localToScreen(tabPane.getBoundsInLocal()).getMinY();
                return new Point2D(x + (tabPane.getWidth() - r.getWidth()) / 2, y + (tabPane.getHeight() - r.getHeight()) / 2);
            }

        }//TabSideIndicatorTransformer

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
                /*                System.err.println("x=" + x + "; y=" + y);
                System.err.println("get(i).w=" + ((Region)list.get(i)).getWidth() + "; get(i).h=" + ((Region)list.get(i)).getHeight());
                System.err.println("locale.x,y=" + list.get(i).localToScreen(0, 0));                
                 */
                boolean b = list.get(i).localToScreen(list.get(i).getBoundsInLocal()).contains(x, y);
                if (b) {
                    retval = i;
                    break;
                }
            }
        }
        System.err.println("1. indexOf retval=" + retval);
        if (retval < 0
                && localToScreen(getBoundsInLocal()).contains(x, y)
                && contentIndexOf(x, y) < 0) {
            System.err.println("2. indexOf retval=" + retval);
            retval = getTabs().size();
        }
        System.err.println("3. indexOf retval=" + retval);
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
            Node n = getTabs().get(i).getGraphic();
            if (n == null) {
                list.add(new Label());
            } else {
                list.add((Region) n);
            }
        }
        return list;
    }
}//DockTabPane
