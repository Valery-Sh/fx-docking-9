package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.DefaultProperty;
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
import javafx.scene.control.Control;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "items")
public class DockTabPane2 extends Control implements Dockable, DockTarget, ListChangeListener {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private final ObservableList<Dockable> items = FXCollections.observableArrayList();

    private final DockableContext dockableContext = new DockableContext(this);

    private final VBox rootPane = new VBox();

    private final TabArea tabArea = new TabArea();

    private TabPaneContext paneContext;

    private final CustomStackPane delegate = new CustomStackPane(rootPane);

    public DockTabPane2() {
        init();
    }

    private void init() {

        getStyleClass().add("dock-tab-pane2");

        paneContext = new TabPaneContext(this);

        StackPane stackPane = new StackPane();
        stackPane.setManaged(true);

        //getChildren().add(rootPane);
        rootPane.getChildren().addAll(tabArea.getPane(), stackPane);
        stackPane.setStyle("-fx-background-color: white");
        stackPane.setPrefHeight(getPrefHeight());
        stackPane.setMaxHeight(Double.MAX_VALUE);
        dockableContext.titleBarProperty().activeChoosedPseudoClassProperty().addListener(this::focusChanged);

        getDockableContext().setTitleBar(new DockTitleBar(this));
        dockableContext.setDragNode(tabArea.getDragButton());
        items.addListener(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    protected StackPane getDelegate() {
        return delegate;
    }

    @Override
    public void onChanged(ListChangeListener.Change change) {
        itemsChanged(change);

    }

    protected void itemsChanged(ListChangeListener.Change<? extends Dockable> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List<? extends Dockable> list = change.getRemoved();
                for (Dockable d : list) {
                    paneContext.undock(d.node());
                }

            }
            if (change.wasAdded()) {
                for (int i = change.getFrom(); i < change.getTo(); i++) {
                    ((TabPaneContext) getTargetContext()).getDockExecutor().dock(i, change.getList().get(i));
                }
            }
        }//while
    }

    public ObservableList<Dockable> getItems() {
        return items;
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

    public int getItemIndex(double x, double y) {
        int retval = -1;

        Node node = DockUtil.findNode(tabArea.getTitleBars(), x, y);
        if (node != null) {
            retval = tabArea.getTitleBars().indexOf(node);
        } else if (DockUtil.contains(tabArea.getPane(), x, y)) {
            retval = tabArea.getTitleBars().size();
        }
        return retval;
    }

    protected Tab addTab(int idx, Node content) {
        Tab tab = new Tab(this, content);
        tabArea.addTab(idx, tab);

        ((Region) content).prefHeightProperty().bind(heightProperty());
        ((Region) content).prefWidthProperty().bind(widthProperty());

        ((StackPane) rootPane.getChildren().get(1)).getChildren().add(content);

        return tab;
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
        return getDockableContext().titleProperty();
    }

    public DockableContext getNodeContext() {
        return dockableContext;
    }

    protected void focusChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        getTabs().forEach(tab -> {
            Dockable d = DockRegistry.dockable(tab.getContent());
            if (newValue && isFocused(tab.getTitleBar())) {
                //tabArea.select(d.node());
                tab.setSelected(true);
            } else if (!newValue) {
                d.getDockableContext().titleBarProperty().setActiveChoosedPseudoClass(false);
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

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockTabPaneSkin(this);
    }

    public static class DockTabPaneSkin extends SkinBase<DockTabPane2> {

        public DockTabPaneSkin(DockTabPane2 control) {
            super(control);
            getChildren().add(control.getDelegate());
        }
    }

    @Override
    protected double computePrefHeight(double h) {
        return delegate.computePrefHeight(h);
    }

    @Override
    protected double computePrefWidth(double w) {
        return delegate.computePrefWidth(w);
    }

    @Override
    protected double computeMinHeight(double h) {
        return delegate.computeMinHeight(h);
    }

    @Override
    protected double computeMinWidth(double w) {
        return delegate.computeMinWidth(w);
    }

    @Override
    protected double computeMaxHeight(double h) {
        return delegate.computeMaxHeight(h);
    }

    @Override
    protected double computeMaxWidth(double w) {
        return delegate.computeMaxWidth(w);
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

    public static class TabPaneContext extends TargetContext {

        private PositionIndicator positionIndicator;

        public TabPaneContext(Region dockPane) {
            super(dockPane);
            init();
        }

        private void init() {
            //12.05setIndicatorPopup(new IndicatorPopup(this));
        }

        @Override
        public DockTabPane2 getTargetNode() {
            return (DockTabPane2) super.getTargetNode();
        }

        private StackPane getContents() {
            return ((StackPane) getTargetNode().rootPane.getChildren().get(1));
        }
        public ObservableList<Dockable> getDockables() {
            return ((DockTabPane2)getTargetNode()).getItems();
        }

        @Override
        protected boolean isDocked(Node node) {
            boolean retval = getContents().getChildren().contains(node);
            return retval;
        }

        @Override
        public void dock(Point2D mousePos, Dockable dockable) {
            getDockExecutor().dock(mousePos, dockable);
        }

        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            return getDockExecutor().doDock(mousePos, node);
        }

        @Override
        public PositionIndicator getPositionIndicator() {
            if (positionIndicator == null) {
                createPositionIndicator();
            }
            return positionIndicator;
        }
        private DockExecutor dockExecutor;

        protected DockExecutor getDockExecutor() {
            if (dockExecutor == null) {
                dockExecutor = new DockExecutor(this);
            }
            return dockExecutor;
        }

        @Override
        public PositionIndicator createPositionIndicator() {
            positionIndicator = new PositionIndicator(this) {
                private Rectangle tabDockPlace;

                @Override
                public void showIndicator(double screenX, double screenY) {
                    getIndicatorPopup().show(getTargetContext().getTargetNode(), screenX, screenY);
                }

                @Override
                protected Pane createIndicatorPane() {
                    Pane p = new Pane();
                    p.getStyleClass().add("drag-pane-indicator");
                    return p;
                }

               // @Override
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
                    int idx = getTargetNode().getItemIndex(x, y);
                    if (idx < 0) {
                        return;
                    }

                    DockTabPane2 pane = (DockTabPane2) getTargetNode();
                    double tabsHeight = pane.tabArea.getPane().getHeight();

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
                    if (idx < pane.tabArea.getTitleBars().size()) {
                        node = pane.tabArea.getTitleBars().get(idx);
                    }
                    int tabsSize = pane.tabArea.getTitleBars().size();
                    if (node != null) {
                        Point2D tabPt = node.localToParent(0, 0);
                        tabPlaceX = tabPt.getX();
                    } else if (tabsSize > 0) {
                        node = pane.tabArea.getTitleBars().get(tabsSize - 1);
                        Point2D tabPt = node.localToParent(node.getBoundsInParent().getWidth(), 0);
                        tabPlaceX = tabPt.getX();
                    }
                    tabPlace.setX(tabPlaceX);
                    tabPlace.setY(p.getY());

                    tabPlace.setVisible(true);
                    tabPlace.toFront();

                }

            };
            return positionIndicator;
        }

        @Override
        public void remove(Node dockNode) {
            Tab tab = getTargetNode().tabArea.tabByContent(dockNode);
            if (tab != null) {
                tab.showContentTitleBar();
                getTargetNode().tabArea.remove(tab);
            }

            getContents().getChildren().remove(dockNode);
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

    public static class Tab {

        private DockTabPane2 tabPane;
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

        public void setContent(Node content) {
            this.content = content;
        }

        /*        public void setContent(Node content) {
            this.content = content;
            ((StackPane) tabPane.rootPane.getChildren().get(1)).getChildren().add(content);
        }
         */
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
            Region tb = DockRegistry.dockable(content).getDockableContext().getTitleBar();
            titleBarVisible = tb.isVisible();
            titleBarMinHeight = tb.getMinHeight();
            titleBarPrefHeight = tb.getPrefHeight();

        }

        protected void hideContentTitleBar() {
            Region tb = DockRegistry.dockable(content).getDockableContext().getTitleBar();
            tb.setVisible(false);
            tb.setMinHeight(0);
            tb.setPrefHeight(0);
        }

        protected void showContentTitleBar() {
            Region tb = DockRegistry.dockable(content).getDockableContext().getTitleBar();
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

    public static class CustomStackPane extends StackPane {

        public CustomStackPane() {

        }

        public CustomStackPane(Node... items) {
            super(items);
        }

        @Override
        protected double computePrefHeight(double h) {
            return super.computePrefHeight(h);
        }

        @Override
        protected double computePrefWidth(double w) {
            return super.computePrefWidth(w);
        }

        @Override
        protected double computeMinHeight(double h) {
            return super.computeMinHeight(h);
        }

        @Override
        protected double computeMinWidth(double w) {
            return super.computeMinWidth(w);
        }

        @Override
        protected double computeMaxHeight(double h) {
            return super.computeMaxHeight(h);
        }

        @Override
        protected double computeMaxWidth(double w) {
            return super.computeMaxWidth(w);
        }
    }

    public static class DockExecutor {

        private TabPaneContext paneContext;

        public DockExecutor(TabPaneContext paneContext) {
            this.paneContext = paneContext;
        }

        protected void dock(Point2D mousePos, Dockable dockable) {
            Node node = dockable.node();
            if (((DockTabPane2) paneContext.getTargetNode()).getItemIndex(mousePos.getX(), mousePos.getY()) < 0) {
                return;
            }
            Dockable d = DockRegistry.dockable(node);
            if (d.getDockableContext().isFloating()) {
                if (doDock(mousePos, node)) {
                    dockable.getDockableContext().setFloating(false);
                }
            }
        }

        protected void dock(int idx, Dockable d) {
            if (d.node().getScene() != null && d.node().getScene().getWindow() != null && (d.node().getScene().getWindow() instanceof Stage)) {
                ((Stage) d.node().getScene().getWindow()).close();
            }

            Tab tab = ((DockTabPane2) paneContext.getTargetNode()).addTab(idx, d.node());

            DockableContext nodeContext = d.getDockableContext();
            nodeContext.setDragNode(tab.getTitleBar());
            if (nodeContext.getTargetContext() == null || nodeContext.getTargetContext() != paneContext) {
                nodeContext.setTargetContext(paneContext);
            }
            if (nodeContext.isFloating()) {
                nodeContext.setFloating(false);
            }

        }

        /*07.05        protected void dock(Dockable dockable, Object pos) {
            if (pos instanceof Side) {
                int idx = ((DockTabPane2) paneContext.getTargetNode()).getTabs().size();
                dock(idx, dockable);
            }
        }
         */
        protected boolean doDock(Point2D mousePos, Node node) {
            if (!DockRegistry.instanceOfDockable(node)) {
                return false;
            }
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }

            Tab tab = ((DockTabPane2) paneContext.getTargetNode()).addTab(mousePos, node);

            DockableContext nodeContext = DockRegistry.dockable(node).getDockableContext();
            nodeContext.setDragNode(tab.getTitleBar());
            if (nodeContext.getTargetContext() == null || nodeContext.getTargetContext() != paneContext) {
                nodeContext.setTargetContext(paneContext);
            }
            return true;
        }

    }
}//DockTabPane

