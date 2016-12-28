package org.vns.javafx.dock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DefaultDockable;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragPopup;
import org.vns.javafx.dock.api.properties.TitleBarProperty;

/**
 *
 * @author Valery
 */
public class DockTabPane extends VBox implements Dockable, DockPaneTarget {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private final StringProperty titleProperty = new SimpleStringProperty();

    private final DockNodeHandler nodeHandler = new DockNodeHandler(this);

    private Button menuButton;

    private final HBox titleBarBox = new HBox();
    StackPane stackPane;
    private Map<Dockable, Object> listeners = new HashMap<>();

    private TabPaneHandler paneHandler;

    public DockTabPane() {
        init();
    }

    private void init() {
        //nodeHandler.setImmediateParentFunction(this::getImmediateParent);
        paneHandler = new TabPaneHandler(this);

        stackPane = new StackPane();
        stackPane.setManaged(true);
        
        menuButton = new Button();
        menuButton.focusTraversableProperty().set(false);
        menuButton.borderProperty().set(Border.EMPTY);
        menuButton.getStyleClass().add(DockTitleBar.StyleClasses.MENU_BUTTON.cssClass());
        menuButton.setTooltip(new Tooltip("List items"));
        menuButton.setContextMenu(new ContextMenu());
        menuButton.setOnAction(ev -> {
            menuButton.getContextMenu().show(menuButton, Side.BOTTOM, 0, 0);
        });
        Pane fillPane = new Pane();
        HBox.setHgrow(fillPane, Priority.ALWAYS);
        HBox full = new HBox(titleBarBox, fillPane, menuButton);
        getChildren().addAll(full, stackPane);

        stackPane.setStyle("-fx-background-color: white");
        stackPane.setPrefHeight(getPrefHeight());
        stackPane.setMaxHeight(Double.MAX_VALUE);
        
        //DockTabPane.this.dockPaneProperty.set(dockPane);

        getTitleBars().addListener(DockTabPane.this::onChangeTitleBars);

        nodeHandler.titleBarProperty().activeChoosedPseudoClassProperty().addListener(this::focusChanged);

        nodeHandler().setTitleBar(new DockTitleBar(this));
    }

    public void onChangeTitleBars(ListChangeListener.Change<? extends Node> c) {
        while (c.next()) {
            if (c.wasUpdated()) {

            } else if (c.wasReplaced()) {
                List<? extends Node> rList = c.getList().subList(c.getFrom(), c.getTo());

            } else {

                if (c.wasRemoved()) {

                } else if (c.wasAdded()) {

                }
            }
        }
    }

    private void add(Dockable dockable) {
        MenuItem mi = new MenuItem();
        menuButton.getContextMenu().getItems().add(mi);
        getContents().add((Node) dockable);
    }

    protected boolean addDockNode(Dockable dockable) {
        dockable.nodeHandler().setPaneHandler(nodeHandler().getPaneHandler());
        if (getChildren().isEmpty()) {
            add(dockable);
            return true;
        }
        return addDockNode(getTitleBars().size(), dockable);
    }

    protected boolean addDockNode(int tabPos, Dockable dockable) {
        if (tabPos < 0 || tabPos > getTitleBars().size()) {
            return false;
        }
        if (getChildren().isEmpty()) {
            add(dockable);
            //dockable.getDockableState().setDocked(this, true);
            return true;
        }
        getTitleBars().add(tabPos, dockable.nodeHandler().getTitleBar());
        getContents().add(tabPos, (Node) dockable);
        //dockable.getDockableState().setDocked(this, true);
        MenuItem mi = new MenuItem();
        mi.setText(dockable.nodeHandler().getTitle());
        menuButton.getContextMenu().getItems().add(mi);
//        addDockedListener(dockable);
        return true;
    }

    public ObservableList<Node> getTitleBars() {
        Node n = null;
        if (!titleBarBox.getChildren().isEmpty()) {
            n = titleBarBox.getChildren().get(0);
        }
        return titleBarBox.getChildren();
    }

    public ObservableList<Node> getContents() {
        return ((StackPane) getChildren().get(1)).getChildren();
    }

    public TitleBarProperty titleBarProperty() {
        return null;
    }

    public StringProperty titleProperty() {
        return nodeHandler().titleProperty();
    }

    public DockNodeHandler getDockNodeHandler() {
        return nodeHandler;
    }

    protected void choose(Dockable choosed) {
        getTitleBars().forEach(tab -> {
            Dockable d = ((TabTitleBar) tab).getOwner();
            TabTitleBar tb = (TabTitleBar) tab;
            if (d != choosed) {
                tb.setActiveChoosedPseudoClass(false);
                d.node().toBack();
            } else {
                tb.setActiveChoosedPseudoClass(true);
                d.node().toFront();
            }
        });
    }

    protected void focusChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        getTitleBars().forEach(tab -> {
            Dockable d = ((TabTitleBar) tab).getOwner();
            if (newValue && isFocused((Region) tab)) {
                choose(d);
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

    public void dock(int pos, Dockable node) {

        if (this.addDockNode(pos, node)) {
            ((Dockable) node).nodeHandler().setDocked(true);
            ((Dockable) node).nodeHandler().setPaneHandler(this.nodeHandler.getPaneHandler());
        }

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
    public Pane pane() {
        return this;
    }

    @Override
    public DockPaneHandler paneHandler() {
        return paneHandler;
    }

    public static class TabTitleBar extends DockTitleBar {

        private final DockTabPane tabPane;

        private double titleBarMinHeight;
        private double titleBarPrefHeight;
        private boolean titleBarVisible;

        public TabTitleBar(Dockable dockNode, DockTabPane tabPane) {
            super(dockNode);
            this.tabPane = tabPane;
            init();
        }

        private void init() {
            saveContentTitleBar();
            getStateButton().setMouseTransparent(true);
            getCloseButton().setMouseTransparent(true);
            getPinButton().setMouseTransparent(true);

            setOnMouseClicked(ev -> {
                //System.err.println("Mouse Clicked ev.getSoource=" + ev.getSource());
                getCloseButton().requestFocus();
                tabPane.choose(getOwner());
                ev.consume();
                //dockNode.nodeHandler().node().toFront();
            });

        }

        public void saveContentTitleBar() {
            Region tb = getOwner().nodeHandler().getTitleBar();
            titleBarVisible = tb.isVisible();
            titleBarMinHeight = tb.getMinHeight();
            titleBarPrefHeight = tb.getPrefHeight();

        }

        public void hideContentTitleBar() {
            Region tb = getOwner().nodeHandler().getTitleBar();
            tb.setVisible(false);
            tb.setMinHeight(0);
            tb.setPrefHeight(0);
        }

        public void showContentTitleBar() {
            Region tb = getOwner().nodeHandler().getTitleBar();
            tb.setVisible(titleBarVisible);
            tb.setMinHeight(titleBarMinHeight);
            tb.setPrefHeight(titleBarPrefHeight);
        }

    }

    public static class TabPaneHandler extends DockPaneHandler {

        public TabPaneHandler(DockTabPane dockPane) {
            super(dockPane);
            init();
        }
        private void init() {
            //setSidePointerModifier(this::modifyNodeSidePointer);
        }
        @Override
        protected void initSplitDelegate() {
        }

        /*        public Dockable dock(Node node, Side dockPos) {
            Dockable d = null;
            if (isDocked(node)) {
                if (node instanceof Dockable) {
                    d = (Dockable) node;
                } else {
                    d = getNotDockableItems().get(node);
                }
                return d;
            }

            if (node instanceof Dockable) {

                d = (Dockable) node;
                d.nodeHandler().setFloating(false);
                d = convert(d, DockConverter.BEFORE_DOCK);
            } else {
                d = new DefaultDockable(node);
                getNotDockableItems().put(node, d);
            }
            doDock(d.node(), dockPos);
            return d;

        }
         */
        @Override
        protected boolean isDocked(Node node) {
            boolean retval;
            retval = getNotDockableItems().get(node) != null;

            return retval;
        }

        @Override
        public Dockable dock(Node node, Side dockPos, Dockable target) {
            if (true) {
                return null;
            }
            Dockable d;
            if (isDocked(node)) {
                if (node instanceof Dockable) {
                    d = (Dockable) node;
                } else {
                    d = getNotDockableItems().get(node);
                }
                return d;
            }

            if (node instanceof Dockable) {
                d = (Dockable) node;
                d.nodeHandler().setFloating(false);
            } else {
                d = new DefaultDockable(node);
                getNotDockableItems().put(node, d);
            }
            doDock(d.node(), dockPos, target);
            return d;
        }

        @Override
        protected void doDock(Point2D mousePos, Node node, Side dockPos) {

            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            DockTabPane dockPane = (DockTabPane) getDockPane();
            TabTitleBar tab = new TabTitleBar((Dockable) node, dockPane);
            tab.setId("TabTitleBar:" + node.getId());

            dockPane.getTitleBars().add(tab);
            
            //node.setManaged(true);
            //StackPane p = new StackPane();
            ((Region)node).prefHeightProperty().bind(dockPane.heightProperty());
            ((Region)node).prefWidthProperty().bind(dockPane.widthProperty());
            
                    
            
            dockPane.getContents().add(node);
            
            //StackPane.setAlignment(node, Pos.TOP_CENTER);
            if (node instanceof Dockable) {
                DockNodeHandler state = ((Dockable) node).nodeHandler();
                tab.hideContentTitleBar();
                state.getDragTransformer().setDragSource(tab);
                if (state.getPaneHandler() == null || state.getPaneHandler() != this) {
                    state.setPaneHandler(this);
                }
                state.setDocked(true);
            }
        }

        public Point2D modifyNodeSidePointer(DragPopup popup, Dockable target, double mouseX, double mouseY) {
            //System.err.println("1 modifyNodeSidePointer !!!!!!!!!!!!!!!!!!!!!");            
/*            popup.getSidePointerGrid().getChildren().remove(popup.getNodeSideButton(Side.BOTTOM));
            popup.getSidePointerGrid().getChildren().remove(popup.getNodeSideButton(Side.LEFT));
            popup.getSidePointerGrid().getChildren().remove(popup.getNodeSideButton(Side.RIGHT));
           
            Button btnTop = popup.getNodeSideButton(Side.TOP);
            //btnTop.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);
*/            
            return null;
        }

        private void doDock(Node node, Side dockPos, Dockable targetDockable) {
            //System.err.println("1 doDock() !!!!!!!!!!!!!!!!!!!!!");
            if (isDocked(node)) {
                return;
            }
            if (targetDockable == null) {
                dock(node, dockPos);
            } else {
                if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                    ((Stage) node.getScene().getWindow()).close();
                }
                if (node instanceof Dockable) {
                    ((Dockable) node).nodeHandler().setFloating(false);
                }
                if (targetDockable instanceof DockTarget) {
                    //((DockTarget)targetDockable).dock(node, dockPos);
                } else {
                    //splitDelegate.dock(node, dockPos, targetDockable);
                }
                ((DockTarget) targetDockable).dock(node, dockPos);
            }
            if (node instanceof Dockable) {
                DockNodeHandler state = ((Dockable) node).nodeHandler();
                if (state.getPaneHandler() == null || state.getPaneHandler() != this) {
                    state.setPaneHandler(this);
                }
                state.setDocked(true);
            }

        }

        @Override
        public void remove(Node dockNode) {
            TabTitleBar tb = null;
            for (Node tbNode : ((DockTabPane) getDockPane()).getTitleBars()) {
                if (((TabTitleBar) tbNode).getOwner() == dockNode) {
                    tb = (TabTitleBar) tbNode;
                    break;
                }
            }
            if (tb != null) {
                tb.showContentTitleBar();
                ((DockTabPane) getDockPane()).getTitleBars().remove(tb);

            }
            ((DockTabPane) getDockPane()).getContents().remove(dockNode);
        }

    }//class TabPaneHandler
}//DockTabPane
