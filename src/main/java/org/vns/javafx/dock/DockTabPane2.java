package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
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
import org.vns.javafx.dock.api.DragPopup;
import org.vns.javafx.dock.api.PaneHandler;

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
        //System.err.println("addTab idx=" + idx);
        tabArea.addTab(idx, tab);

        ((Region) content).prefHeightProperty().bind(heightProperty());
        ((Region) content).prefWidthProperty().bind(widthProperty());

        ((StackPane) rootPane.getChildren().get(1)).getChildren().add(content);

        return tab;
    }

    public List<Tab> getTabs() {
        return tabArea.getTabs();
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
        tabArea.getTitleBars().forEach(tab -> {
            Dockable d = ((TitleBar) tab).getOwner();
            if (newValue && isFocused((Region) tab)) {
                tabArea.select(d.node());
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

        private Separator separator;

        public TitleBar(Dockable dockNode, DockTabPane2 tabPane) {
            super(dockNode);
            this.tabPane = tabPane;
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
                System.err.println("DOCK TITLE PRESSED");
                getStateButton().requestFocus();
                if (getOwner() != null && getOwner().node() != null) {
                    System.err.println("DOCK TITLE PRESSED -> SELECT");
                    tabPane.tabArea.select(getOwner().node());
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
            setSidePointerModifier(this::modifyNodeSidePointer);
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
            //DockTabPane2 dockPane = (DockTabPane2) getDockPane();

            Tab tab = getDockPane().addTab(mousePos, node);

            DockNodeHandler nodeHandler = DockRegistry.dockable(node).nodeHandler();
            nodeHandler.setDragNode(tab.getTitleBar());
            if (nodeHandler.getPaneHandler() == null || nodeHandler.getPaneHandler() != this) {
                nodeHandler.setPaneHandler(this);
            }
        }

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
            titleBar = new TitleBar(DockRegistry.dockable(content), tabPane);
            titleBar.setId("TitleBar:" + content.getId());
            saveContentTitleBar();
            System.err.println("TAB INIT after save " + this);
            hideContentTitleBar();

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

        public void saveContentTitleBar() {
            Region tb = DockRegistry.dockable(content).nodeHandler().getTitleBar();
            titleBarVisible = tb.isVisible();
            titleBarMinHeight = tb.getMinHeight();
            titleBarPrefHeight = tb.getPrefHeight();

        }

        public void hideContentTitleBar() {
            Region tb = DockRegistry.dockable(content).nodeHandler().getTitleBar();
            tb.setVisible(false);
            tb.setMinHeight(0);
            tb.setPrefHeight(0);
        }

        public void showContentTitleBar() {
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

        private final ScrollPane tabScrollPane = new ScrollPane();

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
            pane.getChildren().addAll(dragPane, tabScrollPane, fillPane, leftScrollButton, rightScrollButton, menuButton);
            pane.getStyleClass().add("tab-area");
            tabs.addListener(this::tabsChanged);

        }
        public void remove(Tab tab) {
            //titleBarPane.getChildren().remove(tab.getTitleBar());
            //titleBarPane.getChildren().remove(tab.getTitleBar().getSeparator());
            tabs.remove(tab);
/*            int idx = tabs.indexOf(tab);
            if (tabs.size() > 1) {
                if (idx - 1 >= 0) {
                    idx--;
                }
            } else {
                idx = -1;
            }
            tabs.remove(tab);
            if (idx >= 0) {
                //select(tabs.get(idx).getContent());
            }
*/
        }

        protected void tabsChanged(ListChangeListener.Change<? extends Tab> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends Tab> list = change.getRemoved();
                    for (Tab t : list) {
                        titleBarPane.getChildren().remove(t.getTitleBar());
                        titleBarPane.getChildren().remove(t.getTitleBar().getSeparator());
                    
                        int idx = change.getFrom();
                        if ( ! change.getList().isEmpty() ) {
                            if (idx - 1 >= 0) {
                                idx--;
                            }
                            select(change.getList().get(idx).getContent());
                        }
                        
                        System.err.println("tabsChanged removed from=" + change.getFrom());
                        System.err.println("tabsChanged removed to=" + change.getTo());
//                        t.showContentTitleBar();
//                        titleBarPane.getChildren().remove(t.getTitleBar());
//                        titleBarPane.getChildren().remove(t.getTitleBar().getSeparator());
                        
                        
                    }

                } else if (change.wasAdded()) {
                    //
                    // For the current implementation only one element may be aded
                    //
                    List<? extends Tab> list = change.getAddedSubList();
                    for (Tab t : list) {
                        int idx = change.getList().indexOf(t);
                        System.err.println("tabsChanged idx=" + idx);
                        titleBarPane.getChildren().add(idx, t.getTitleBar());
                        titleBarPane.getChildren().add(idx + 1, t.getTitleBar().getSeparator());
                        select(t.getContent());
                    }
                }
            }
        }

        protected void select(Node selected) {
            getTitleBars().forEach(tab -> {
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
            menuButton.getStyleClass().add("menu-button");
            menuButton.setTooltip(new Tooltip("List items"));
            menuButton.setContextMenu(new ContextMenu());
            menuButton.setOnAction(ev -> {
                menuButton.getContextMenu().show(menuButton, Side.BOTTOM, 0, 0);
            });
        }

        protected void initDragPane() {
            dragPane = new StackPane();
            dragPane.setMinWidth(16);
            dragPane.getStyleClass().add("drag-pane");
            dragButton = new Button();
            dragButton.setTooltip(new Tooltip("Drag Tab Pane"));
            dragPane.getChildren().add(dragButton);
            dragButton.getStyleClass().add("drag-button");
            dragButton.setFocusTraversable(false);
            StackPane.setAlignment(dragButton, Pos.CENTER);

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
            // We set the hmax property basing on the item count. We take into account
            // that ther is a separator after each TitleBar instance.  
            //
            tabScrollPane.hmaxProperty().bind(Bindings.divide(Bindings.size(titleBarPane.getChildren()), 2));

            leftScrollButton.setOnAction(a -> {
                if (tabScrollPane.getHvalue() != tabScrollPane.getHmin()) {
                    tabScrollPane.setHvalue(tabScrollPane.getHvalue() - 1);
                }
            });

            leftScrollButton.setFocusTraversable(false);
            leftScrollButton.borderProperty().set(Border.EMPTY);
            leftScrollButton.getStyleClass().addAll("hscroll-button", "left-scroll");
            leftScrollButton.setTooltip(new Tooltip("Scroll Left"));

            rightScrollButton.setFocusTraversable(false);
            rightScrollButton.borderProperty().set(Border.EMPTY);
            rightScrollButton.getStyleClass().addAll("hscroll-button", "right-scroll");
            rightScrollButton.setTooltip(new Tooltip("Scroll Right"));

            rightScrollButton.setOnAction(a -> {
                if (tabScrollPane.getHvalue() != tabScrollPane.getHmax()) {
                    tabScrollPane.setHvalue(tabScrollPane.getHvalue() + 1);
                }
            });

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
}//DockTabPane
