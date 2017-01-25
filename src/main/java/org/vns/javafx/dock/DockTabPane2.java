package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PaneHandler;
import org.vns.javafx.dock.api.SideIndicator;
import org.vns.javafx.dock.api.SideIndicatorTransformer.NodeIndicatorTransformer;

/**
 *
 * @author Valery
 */
public class DockTabPane2 extends StackPane implements Dockable, DockPaneTarget {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private final DockNodeHandler nodeHandler = new DockNodeHandler(this);

    private final VBox rootPane = new VBox();

    private final TabArea tabArea = new TabArea();
    //private StackPane stackPane;
    private TabPaneHandler paneHandler;

    public DockTabPane2() {
        init();
    }

    private void init() {

        getStyleClass().add("dock-tab-pane2");

        paneHandler = new TabPaneHandler(this);

        StackPane stackPane = new StackPane();
        stackPane.setManaged(true);

        getChildren().add(rootPane);
        rootPane.getChildren().addAll(tabArea.getPane(), stackPane);
        stackPane.setStyle("-fx-background-color: white");
        stackPane.setPrefHeight(getPrefHeight());
        stackPane.setMaxHeight(Double.MAX_VALUE);
        nodeHandler.titleBarProperty().activeChoosedPseudoClassProperty().addListener(this::focusChanged);

        nodeHandler().setTitleBar(new DockTitleBar(this));
        nodeHandler.setDragNode(tabArea.getDragButton());

    }

    public void showScrollBar(boolean show) {
        tabArea.showScrollBar(show);
    }

    public void showMenuButton(boolean show) {
        tabArea.showMenuButton(show);
    }

    //public void setTabScrollBar
    protected Pane getRootPane() {
        return rootPane;
    }

    protected Tab addTab(Point2D mousePos, Node content) {

        Tab tab = new Tab(this, content);

        int idx = -1;
        if (mousePos != null) {
            Node tb = DockUtil.findNode(tabArea.getTitleBars(), mousePos.getX(), mousePos.getY());
            if (tb != null) {
                idx = tabArea.getTitleBars().indexOf(tb);
            }
        }
        tabArea.addTab(idx, tab);

        ((Region) content).prefHeightProperty().bind(heightProperty());
        ((Region) content).prefWidthProperty().bind(widthProperty());

        ((StackPane) rootPane.getChildren().get(1)).getChildren().add(content);

        return tab;
    }

    public List<Tab> getTabs() {
        return tabArea.getTabs();
    }

    protected TabArea getTabArea() {
        return tabArea;
    }

    public StringProperty titleProperty() {
        return nodeHandler().titleProperty();
    }

    public DockNodeHandler getNodeHandler() {
        return nodeHandler;
    }

    /*    protected void select(Dockable selected) {
        tabArea.select(selected.node());
        tabArea.getTitleBars().forEach(tab -> {
            Dockable d = ((TitleBar) tab).getOwner();
            TitleBar tb = (TitleBar) tab;
            if (d != selected) {
                tb.setSelectedPseudoClass(false);
                d.node().toBack();
                d.node().setVisible(false);
            } else {
                tb.setSelectedPseudoClass(true);
                d.node().setVisible(true);
                d.node().toFront();
            }
        });
        
    }
     */
    protected void focusChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        /*        tabArea.getTitleBars().forEach(tab -> {
            Dockable d = ((TitleBar) tab).getOwner();
            if (newValue && isFocused((Region) tab)) {
                tabArea.select(d.node());
            } else if (!newValue) {
                d.nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(false);
            }
        });
         */
        getTabs().forEach(tab -> {
            Dockable d = DockRegistry.dockable(tab.getContent());
            if (newValue && isFocused(tab.getTitleBar())) {
                //tabArea.select(d.node());
                tab.setSelected(true);
            } else if (!newValue) {
                d.nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(false);
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

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockNodeHandler nodeHandler() {
        return this.nodeHandler;
    }

    @Override
    public Region pane() {
        return this;
    }

    @Override
    public PaneHandler paneHandler() {
        return paneHandler;
    }

    public static class TitleBar extends DockTitleBar implements EventHandler<MouseEvent> {

        private final DockTabPane2 tabPane;
        private final Tab tab;

        private Separator separator;

        public TitleBar(Tab tab) {
            super(DockRegistry.dockable(tab.getContent()));
            this.tabPane = tab.getTabPane();
            this.tab = tab;
            init();
        }

        private void init() {
            getStateButton().setMouseTransparent(true);
            removeButtons(getCloseButton(), getPinButton());
            addEventHandler(MouseEvent.MOUSE_PRESSED, this);

            separator = new Separator();
            separator.setOrientation(Orientation.VERTICAL);
        }

        public Separator getSeparator() {
            return separator;
        }

        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                getStateButton().requestFocus();
                if (getOwner() != null && getOwner().node() != null) {
                    tab.setSelected(true);
                }
            }
        }
    }

    public static class TabPaneHandler extends PaneHandler {

        public TabPaneHandler(Region dockPane) {
            super(dockPane);
            init();
        }

        private void init() {
            //setSidePointerModifier(this::modifyNodeSidePointer);
        }

        @Override
        public DockTabPane2 getDockPane() {
            return (DockTabPane2) super.getDockPane();
        }

        @Override
        protected void initSplitDelegate() {
        }

        private StackPane getContents() {
            return ((StackPane) getDockPane().rootPane.getChildren().get(1));
        }

        @Override
        protected boolean isDocked(Node node) {
            boolean retval = getContents().getChildren().contains(node);
            return retval;
        }

        @Override
        public Dockable dock(Dockable node, Side dockPos, Dockable target) {
            return null;
        }

        @Override
        protected void doDock(Point2D mousePos, Node node, Side dockPos) {
            if (!DockRegistry.isDockable(node)) {
                return;
            }
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }

            Tab tab = getDockPane().addTab(mousePos, node);

            DockNodeHandler nodeHandler = DockRegistry.dockable(node).nodeHandler();
            nodeHandler.setDragNode(tab.getTitleBar());
            if (nodeHandler.getPaneHandler() == null || nodeHandler.getPaneHandler() != this) {
                nodeHandler.setPaneHandler(this);
            }
        }

        @Override
        protected NodeIndicatorTransformer createNodeIndicatorTransformer() {
             return new TabSideIndicatorTransformer();
        }

        /*
        @Override
        public Point2D modifyNodeSidePointer(DragPopup popup, Dockable target, double mouseX, double mouseY) {
            if (popup.getSidePointerGrid().getChildren().contains(popup.getNodeSideButtons(Side.BOTTOM))) {
                popup.removeNodeSideButtons(Side.BOTTOM);
                popup.removeNodeSideButtons(Side.TOP);
                popup.removeNodeSideButtons(Side.LEFT);
                popup.removeNodeSideButtons(Side.RIGHT);

                Pane pane = popup.getPaneSideButtons(Side.TOP);
                popup.addNodeSideButtons(popup.getPaneSideButtons(Side.TOP), Side.TOP);
                //pane.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
                popup.getPaneSideButton(Side.TOP).pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
                popup.removePaneSideButtons(Side.LEFT);
                popup.removePaneSideButtons(Side.RIGHT);
                popup.removePaneSideButtons(Side.BOTTOM);
            }
            Region p = popup.getDockPane();

            DockTabPane2 tabPane;// = null;
            TabPaneHandler paneHandler;// = null; 
            Point2D retval = null;
            if (p instanceof DockTabPane2) {
                paneHandler = ((DockTabPane2) p).paneHandler;
                tabPane = (DockTabPane2) p;
                Node node = DockUtil.findNode(tabPane.tabArea.getTitleBars(), mouseX, mouseY);
                if (node != null) {
                    retval = new Point2D(mouseX - 5, mouseY - 5);
                } else if (DockUtil.contains(tabPane.tabArea.getPane(), mouseX, mouseY)) {
                    retval = new Point2D(mouseX - 5, mouseY - 5);
                } else {
                    retval = tabPane.localToScreen(
                            (tabPane.getWidth() - popup.getSidePointerGrid().getWidth()) / 2,
                            (tabPane.getHeight() - popup.getSidePointerGrid().getHeight()) / 2);
                }
            }

            return retval;
        }
         */
//        @Override
/*        public Point2D modifyNodeSidePointer_PLD(DragPopup popup, Dockable target, double mouseX, double mouseY) {
            if (popup.getSidePointerGrid().getChildren().contains(popup.getNodeSideButtons(Side.BOTTOM))) {
                popup.removeNodeSideButtons(Side.BOTTOM);
                popup.removeNodeSideButtons(Side.TOP);
                popup.removeNodeSideButtons(Side.LEFT);
                popup.removeNodeSideButtons(Side.RIGHT);

                Pane pane = popup.getPaneSideButtons(Side.TOP);
                popup.addNodeSideButtons(popup.getPaneSideButtons(Side.TOP), Side.TOP);
                //pane.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
                popup.getPaneSideButton(Side.TOP).pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
                popup.removePaneSideButtons(Side.LEFT);
                popup.removePaneSideButtons(Side.RIGHT);
                popup.removePaneSideButtons(Side.BOTTOM);
            }
            Region p = popup.getDockPane();

            DockTabPane2 tabPane;// = null;
            TabPaneHandler paneHandler;// = null; 
            Point2D retval = null;
            if (p instanceof DockTabPane2) {
                paneHandler = ((DockTabPane2) p).paneHandler;
                tabPane = (DockTabPane2) p;
                Node node = DockUtil.findNode(tabPane.tabArea.getTitleBars(), mouseX, mouseY);
                if (node != null) {
                    retval = new Point2D(mouseX - 5, mouseY - 5);
                } else if (DockUtil.contains(tabPane.tabArea.getPane(), mouseX, mouseY)) {
                    retval = new Point2D(mouseX - 5, mouseY - 5);
                } else {
                    retval = tabPane.localToScreen(
                            (tabPane.getWidth() - popup.getSidePointerGrid().getWidth()) / 2,
                            (tabPane.getHeight() - popup.getSidePointerGrid().getHeight()) / 2);
                }
            }

            return retval;
        }
         */
        @Override
        public void remove(Node dockNode) {
            Tab tab = getDockPane().tabArea.tabByContent(dockNode);
            if (tab != null) {
                tab.showContentTitleBar();
                getDockPane().tabArea.remove(tab);
            }

            getContents().getChildren().remove(dockNode);
        }

    }//class TabPaneHandler

    public static class Tab {

        private final DockTabPane2 tabPane;
        private TitleBar titleBar;
        private Node content;
        private RadioMenuItem menuItem;
        //
        // save content properties
        //
        private double titleBarMinHeight;
        private double titleBarPrefHeight;
        private boolean titleBarVisible;

        public Tab(DockTabPane2 tabPane, Node content) {
            this.tabPane = tabPane;
            this.content = content;
            init();
        }

        private void init() {
            titleBar = new TitleBar(this);
            titleBar.setId("TitleBar:" + content.getId());
            saveContentTitleBar();
            hideContentTitleBar();
            menuItem = new RadioMenuItem(titleBar.getLabel().getText());
            menuItem.setOnAction(a -> {
                //tabPane.getTabArea().select(content);
                setSelected(true);
                int idx = tabPane.getTabs().indexOf(this);
                tabPane.getTabArea().getScrollPane().setHvalue(idx * 2);
            });

        }

        public DockTabPane2 getTabPane() {
            return tabPane;
        }

        protected RadioMenuItem getMenuItem() {
            return menuItem;
        }

        public TitleBar getTitleBar() {
            return titleBar;
        }

        protected void setTitleBar(TitleBar titleBar) {
            this.titleBar = titleBar;
        }

        public Node getContent() {
            return content;
        }

        protected void setContent(Node content) {
            this.content = content;
            ((StackPane) tabPane.rootPane.getChildren().get(1)).getChildren().add(content);
        }

        protected void setSelected(boolean sel) {
            tabPane.getTabs().forEach(tab -> {
                Dockable d = DockRegistry.dockable(tab.getContent());
                TitleBar tb = tab.getTitleBar();
                if (d != content && sel) {
                    tb.setSelectedPseudoClass(false);
                    d.node().toBack();
                    d.node().setVisible(false);
                } else if (d == content) {
                    tb.setSelectedPseudoClass(sel);
                    d.node().setVisible(sel);
                    if (sel) {
                        d.node().toFront();
                    }
                }
            });

        }

        protected void saveContentTitleBar() {
            Region tb = DockRegistry.dockable(content).nodeHandler().getTitleBar();
            titleBarVisible = tb.isVisible();
            titleBarMinHeight = tb.getMinHeight();
            titleBarPrefHeight = tb.getPrefHeight();

        }

        protected void hideContentTitleBar() {
            Region tb = DockRegistry.dockable(content).nodeHandler().getTitleBar();
            tb.setVisible(false);
            tb.setMinHeight(0);
            tb.setPrefHeight(0);
        }

        protected void showContentTitleBar() {
            Region tb = DockRegistry.dockable(content).nodeHandler().getTitleBar();
            tb.setVisible(titleBarVisible);
            tb.setMinHeight(titleBarMinHeight);
            tb.setPrefHeight(titleBarPrefHeight);
        }
    }

    class TabArea {

        private final ObservableList<Tab> tabs = FXCollections.observableArrayList();

        private final HBox pane = new HBox();
        private final HBox titleBarPane = new HBox();
        private Button menuButton;

        ToggleGroup menuToggleGroup = new ToggleGroup();

        private final ScrollPane tabScrollPane = new ScrollPane();

        private Pane scrollButtonsPane;

        private Button leftScrollButton = new Button();
        private Button rightScrollButton = new Button();

        private Pane dragPane;
        private Button dragButton;

        public TabArea() {
            init();
        }

        private void init() {
            initTitleBarMenu();
            initDragPane();
            initScrollPane();
            Pane fillPane = new Pane();
            HBox.setHgrow(fillPane, Priority.ALWAYS);

            pane.getChildren().addAll(dragPane, tabScrollPane, fillPane, scrollButtonsPane, menuButton);
            pane.getStyleClass().add("tab-area");
            tabs.addListener(this::tabsChanged);

        }

        protected void showScrollBar(boolean show) {
            if (show && pane.getChildren().contains(leftScrollButton)) {
                return;
            }
            if (!show) {
                pane.getChildren().removeAll(leftScrollButton, rightScrollButton);
            } else {
                pane.getChildren().add(3, leftScrollButton);
                pane.getChildren().add(4, rightScrollButton);
            }
        }

        protected void showMenuButton(boolean show) {
            if (show && pane.getChildren().contains(menuButton)) {
                return;
            }
            if (!show) {
                pane.getChildren().remove(menuButton);
            } else {
                pane.getChildren().add(menuButton);
            }
        }

        public void remove(Tab tab) {
            tabs.remove(tab);
        }

        protected ScrollPane getScrollPane() {
            return tabScrollPane;
        }

        protected void tabsChanged(ListChangeListener.Change<? extends Tab> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends Tab> list = change.getRemoved();
                    for (Tab t : list) {
                        titleBarPane.getChildren().remove(t.getTitleBar());
                        titleBarPane.getChildren().remove(t.getTitleBar().getSeparator());

                        int idx = change.getFrom();
                        if (!change.getList().isEmpty()) {
                            if (idx - 1 >= 0) {
                                idx--;
                            }
                            change.getList().get(idx).setSelected(true);
                        }
                    }

                } else if (change.wasAdded()) {
                    //
                    // For the current implementation only one element may be aded
                    //
                    List<? extends Tab> list = change.getAddedSubList();
                    for (Tab t : list) {
                        int idx = change.getList().indexOf(t);
                        titleBarPane.getChildren().add(idx, t.getTitleBar());
                        titleBarPane.getChildren().add(idx + 1, t.getTitleBar().getSeparator());
                        t.setSelected(true);
                    }
                }
            }
        }

        protected Tab getSelected() {
            Tab retval = null;
            for (Tab tab : tabs) {
                TitleBar tb = tab.getTitleBar();
                Dockable d = ((TitleBar) tb).getOwner();
                if (tb.isSelectedPseudoClass()) {
                    retval = tab;
                    break;
                }
            }
            return retval;
        }

        public Button getDragButton() {
            return dragButton;
        }

        public HBox getPane() {
            return pane;
        }

        protected void initTitleBarMenu() {
            menuButton = new Button();
            menuButton.focusTraversableProperty().set(false);
            menuButton.borderProperty().set(Border.EMPTY);
            //menuButton.getStyleClass().addAll("hscroll-button", "tmenu-button");
            menuButton.getStyleClass().add("tab-menu-button");
            menuButton.setTooltip(new Tooltip("List items"));
            ContextMenu cmenu = new ContextMenu();

            menuButton.setContextMenu(cmenu);

            menuButton.setOnAction(ev -> {
                fillMenu();
                menuButton.getContextMenu().show(menuButton, Side.BOTTOM, 0, 0);
            });
        }

        protected void fillMenu() {
            ContextMenu menu = menuButton.getContextMenu();
            menu.getItems().clear();
            menuToggleGroup.getToggles().clear();

            tabs.forEach(tab -> {
                menuButton.getContextMenu().getItems().add(tab.getMenuItem());
                menuToggleGroup.getToggles().add(tab.getMenuItem());
                if (getSelected() == tab) {
                    tab.getMenuItem().setSelected(true);
                }
            });

        }

        protected void initDragPane() {
            dragPane = new HBox();
            dragPane.getStyleClass().add("drag-pane");
            dragButton = new Button();
            dragButton.setTooltip(new Tooltip("Drag Tab Pane"));
            dragPane.getChildren().add(dragButton);
            dragButton.getStyleClass().add("drag-button");
            dragButton.setFocusTraversable(false);
        }

        protected void initScrollPane() {
            tabScrollPane.getStyleClass().add("scroll-pane");
            tabScrollPane.getStyleClass().add("edge-to-edge");
            tabScrollPane.setContent(titleBarPane);

            tabScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            tabScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            tabScrollPane.setMinHeight(0);
            tabScrollPane.setMinWidth(0);

            //
            // We initIndicatorPane the hmax property basing on the item count. We take into account
            // that ther is a separator after each TitleBar instance.  
            //
            tabScrollPane.hmaxProperty().bind(Bindings.divide(Bindings.size(titleBarPane.getChildren()), 2));

            leftScrollButton.setOnAction(a -> {
                if (tabScrollPane.getHvalue() != tabScrollPane.getHmin()) {
                    tabScrollPane.setHvalue(tabScrollPane.getHvalue() - 1);
                }
            });

            leftScrollButton.setFocusTraversable(false);
            leftScrollButton.getStyleClass().addAll("hscroll-button", "left-scroll");
            leftScrollButton.setTooltip(new Tooltip("Scroll Left"));

            rightScrollButton.setFocusTraversable(false);
            rightScrollButton.getStyleClass().addAll("hscroll-button", "right-scroll");
            rightScrollButton.setTooltip(new Tooltip("Scroll Right"));

            rightScrollButton.setOnAction(a -> {
                if (tabScrollPane.getHvalue() != tabScrollPane.getHmax()) {
                    tabScrollPane.setHvalue(tabScrollPane.getHvalue() + 1);
                }
            });
            //scrollButtonsPane = new HBox(20);

            scrollButtonsPane = new HBox(leftScrollButton, rightScrollButton);
            scrollButtonsPane.getStyleClass().add("scroll-buttons-pane");
        }

        protected List<Node> getTitleBars() {
            return titleBarPane.getChildren().filtered(n -> {
                return (n instanceof TitleBar);
            });
        }

        public void showTitleMenuButton(boolean show) {
            if (show && pane.getChildren().indexOf(menuButton) < 0) {
                pane.getChildren().add(menuButton);
            } else if (!show && pane.getChildren().indexOf(menuButton) >= 0) {
                pane.getChildren().remove(menuButton);
            }
        }

        protected Button getMenuButton() {
            return menuButton;
        }

        protected void setMenuButton(Button menuButton) {
            this.menuButton = menuButton;
        }

        protected Button getLeftScrollButton() {
            return leftScrollButton;
        }

        protected void setLeftScrollButton(Button leftScrollButton) {
            this.leftScrollButton = leftScrollButton;
        }

        protected Button getRightScrollButton() {
            return rightScrollButton;
        }

        protected void setRightScrollButton(Button rightScrollButton) {
            this.rightScrollButton = rightScrollButton;
        }

        public void showTitleScrollButtons(boolean show) {
            int leftIdx = pane.getChildren().indexOf(leftScrollButton);
            int rightIdx = pane.getChildren().indexOf(rightScrollButton);

            boolean leftOn = leftIdx >= 0;
            boolean rightOn = rightIdx >= 0;

            if (show) {
                if (!leftOn && rightOn) {
                    pane.getChildren().add(rightIdx, leftScrollButton);
                } else if (leftOn && !rightOn) {
                    pane.getChildren().add(rightScrollButton);
                }
                if (!leftOn && !rightOn) {
                    pane.getChildren().add(leftScrollButton);
                    pane.getChildren().add(rightScrollButton);
                }
            } else {
                if (leftOn) {
                    pane.getChildren().remove(leftScrollButton);
                }
                if (rightOn) {
                    pane.getChildren().remove(rightScrollButton);
                }
            }
        }

        protected Tab addTab(int idx, Tab tab) {
            if (idx >= 0) {
                //titleBarPane.getChildren().add(idx, tab.getTitleBar());
                tabs.add(idx, tab);
                //titleBarPane.getChildren().add(idx + 1, tab.getTitleBar().getSeparator());
            } else {
                //titleBarPane.getChildren().add(tab.getTitleBar());
                tabs.add(tab);
                //titleBarPane.getChildren().add(tab.getTitleBar().getSeparator());
            }
            //select(tab.getContent());

            return tab;
        }

        public List<Tab> getTabs() {
            List<Tab> retval = new ArrayList<>();
            retval.addAll(tabs);
            return retval;
        }

        public Tab tabByContent(Node node) {
            Tab retval = null;
            for (Tab tb : tabs) {
                if (tb.getContent() == node) {
                    retval = tb;
                    break;
                }
            }
            return retval;
        }
    }

    public static class TabSideIndicatorTransformer extends NodeIndicatorTransformer {

        public TabSideIndicatorTransformer() {
        }

        /**
         * The method does nothing. It overrides the method of the subclass to
         * escape scaling of an indicator pane.
         *
         * @param node the dockable node
         */
/*        @Override
        public void indicatorOnShown(WindowEvent ev,Region node) {
        }

        @Override
        public Point2D mousePos() {
            Point2D retval;// = super.mousePos();

            SideIndicator paneIndicator = getTargetPaneHandler().getDragPopup().getPaneIndicator();
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
            
            DockTabPane2 tabPane = (DockTabPane2) getTargetPaneHandler().getDockPane();

            double x = getMousePos().getX();
            double y = getMousePos().getY();

            Node node = DockUtil.findNode(tabPane.tabArea.getTitleBars(), x, y);
            if (node != null) {
                retval = new Point2D(x - 5, y - 5);
                topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
            } else if (DockUtil.contains(tabPane.tabArea.getPane(), x, y)) {
                retval = new Point2D(x - 5, y - 5);
                topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
            } else {
                topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, false);
                retval = tabPane.localToScreen(
                        (tabPane.getWidth() - getIndicator().getIndicatorPane().getWidth()) / 2,
                        (tabPane.getHeight() - getIndicator().getIndicatorPane().getHeight()) / 2);
            }

            return retval;
        }
*/
        
        @Override
        public void notifyPopupShown() {
        }

        @Override
        public Point2D getIndicatorPosition() {
            Point2D retval;// = super.mousePos();

            SideIndicator paneIndicator = getTargetPaneHandler().getDragPopup().getPaneIndicator();
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
            
            DockTabPane2 tabPane = (DockTabPane2) getTargetPaneHandler().getDockPane();

            double x = getMousePos().getX();
            double y = getMousePos().getY();

            Node node = DockUtil.findNode(tabPane.tabArea.getTitleBars(), x, y);
            if (node != null) {
                retval = new Point2D(x - 5, y - 5);
                topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
            } else if (DockUtil.contains(tabPane.tabArea.getPane(), x, y)) {
                retval = new Point2D(x - 5, y - 5);
                topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
            } else {
                topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, false);
                retval = tabPane.localToScreen(
                        (tabPane.getWidth() - getIndicator().getIndicatorPane().getWidth()) / 2,
                        (tabPane.getHeight() - getIndicator().getIndicatorPane().getHeight()) / 2);
            }

            return retval;
        }        
    }
  
}//DockTabPane
