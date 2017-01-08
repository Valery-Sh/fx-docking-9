package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragPopup;
import org.vns.javafx.dock.api.PaneHandler;
import org.vns.javafx.dock.api.properties.TitleBarProperty;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPane extends TabPane implements Dockable, DockPaneTarget {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private final StringProperty titleProperty = new SimpleStringProperty();

    private final DockNodeHandler nodeHandler = new DockNodeHandler(this);

    private Button dragButton;
    private Tab dragTab;

    private final Map<Dockable, Object> listeners = new HashMap<>();

    private TabPaneHandler paneHandler;

    public DockTabPane() {
        init();
    }
    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }
    private void init() {

        getStyleClass().add("dock-tab-pane");
        getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        
        paneHandler = new TabPaneHandler(this);

        initDragPane();

        nodeHandler().setTitleBar(new DockTitleBar(this));
        nodeHandler.setDragSource(dragButton);

    }

    protected void initDragPane() {
        dragButton = new Button();
        dragButton.getStyleClass().add("drag-button");
        dragButton.setFocusTraversable(false);
        dragTab = new Tab();
        dragTab.setGraphic(dragButton);
        getTabs().add(dragTab);
        Label lb = new Label("drag content");
        dragTab.setContent(lb);
    }

    public Button getDragButton() {
        return dragButton;
    }

    public Tab getDragTab() {
        return dragTab;
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

    /*    protected void choose(Dockable choosed) {
        getTabList().forEach(tabGraphic -> {
            Dockable d = ((TitleBarSafe) tabGraphic).getOwner();
            TitleBarSafe tb = (TitleBarSafe) tabGraphic;
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
        getTabList().forEach(tabGraphic -> {
            Dockable d = ((TitleBarSafe) tabGraphic).getOwner();
            if (newValue && isFocused((Region) tabGraphic)) {
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
     */
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

    public static class TabGraphic extends DockTitleBar {

        //private final DockTabPane tabPane;
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
            String txt = d.nodeHandler().getTitle();
            if (d.nodeHandler().getProperties().getProperty("user-title") != null) {
                txt = d.nodeHandler().getProperties().getProperty("user-title");
            } else if (d.nodeHandler().getProperties().getProperty("short-title") != null) {
                txt = d.nodeHandler().getProperties().getProperty("short-title");
            } else if (d.node().getId() != null && d.node().getId().isEmpty()) {
                txt = d.node().getId();
            }
            if (txt == null || txt.trim().isEmpty()) {
                txt = "";
            }
            return txt;
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

    public static class TabPaneHandler extends PaneHandler {

        public TabPaneHandler(DockTabPane tabPane) {
            super((Region) tabPane);
            init();
        }

        private void init() {
            setSidePointerModifier(this::modifyNodeSidePointer);
        }

        @Override
        public TabPane getDockPane() {
            return (TabPane) super.getDockPane();
        }

        @Override
        protected void initSplitDelegate() {
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
        public Dockable dock(Dockable node, Side dockPos, Dockable target) {
            return null;
        }

        @Override
        protected void doDock(Point2D mousePos, Node node, Side dockPos) {
            Dockable dockable = DockRegistry.dockable(node);
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            DockTabPane tabPane = (DockTabPane) getDockPane();
            //TabGraphic tabGraphic = new TabGraphic(dockable, tabPane);
            int idx = -1;
            if (mousePos != null) {
                System.err.println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
/*                List<Node> list = new ArrayList<>();
                tabPane.getTabs().forEach(tb -> {
                    if (tb.getGraphic() != null) {
                        list.add(tb.getGraphic());
                    } else {
                        list.add(new Button());
                    }
                });
                Node tb = DockUtil.findNode(list, mousePos.getX(), mousePos.getY());
*/                
                idx = tabPane.indexOf(mousePos.getX(), mousePos.getY());
                if ( idx == 0 ) {
                    if ( tabPane.getTabs().get(0).getGraphic() == tabPane.getDragButton() ) {
                        idx++;
                    }
                }
                
                System.err.println("moX = " + mousePos.getX() + "; moY = " + mousePos.getY());
                System.err.println("idx = " + idx);
                for ( double d : tabPane.getTabBoundaries()) {
                    System.err.println("idx = " + idx + "; d = " + d);
                }
                
                
            }
            String txt = getButtonText(dockable);
            if ( txt.isEmpty() ) {
                txt = " ... ";
            }
            Rectangle r = new Rectangle();
            r.setWidth(1);
            
            Tab newTab = new Tab(txt);
            newTab.setGraphic(r);
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

//            tabPane.getContents().add(node);
            if (DockRegistry.isDockable(node)) {
                DockNodeHandler nodeHandler = ((Dockable) node).nodeHandler();
                nodeHandler.setDragSource(newTab.getGraphic());
                if (nodeHandler.getPaneHandler() == null || nodeHandler.getPaneHandler() != this) {
                    nodeHandler.setPaneHandler(this);
                }
            }
        }
        protected String getButtonText(Dockable d) {
            String txt = d.nodeHandler().getTitle();
            if (d.nodeHandler().getProperties().getProperty("user-title") != null) {
                txt = d.nodeHandler().getProperties().getProperty("user-title");
            } else if (d.nodeHandler().getProperties().getProperty("short-title") != null) {
                txt = d.nodeHandler().getProperties().getProperty("short-title");
            } else if (d.node().getId() != null && d.node().getId().isEmpty()) {
                txt = d.node().getId();
            }
            if (txt == null || txt.trim().isEmpty()) {
                txt = "";
            }
            return txt;
        }

        public void saveContentTitleBar(Dockable dockable) {
            Region tb = dockable.nodeHandler().getTitleBar();
            if ( tb == null ) {
                return;
            }
            tb.getProperties().put("titleBarVisible", tb.isVisible());
            tb.getProperties().put("titleBarMinHeight", tb.getMinHeight());
            tb.getProperties().put("titleBarPrefHeight", tb.getPrefHeight());            
        }

        protected void hideContentTitleBar(Dockable dockable) {
            Region tb = dockable.nodeHandler().getTitleBar();
            if ( tb == null ) {
                return;
            }
            tb.setVisible(false);
            tb.setMinHeight(0);
            tb.setPrefHeight(0);
        }

        public void showContentTitleBar(Dockable dockable) {
            Region tb = dockable.nodeHandler().getTitleBar();
            if ( tb == null) {
                return;
            }
            tb.setVisible((boolean) tb.getProperties().get("titleBarVisible"));
            tb.setMinHeight((double) tb.getProperties().get("titleBarMinHeight"));
            tb.setPrefHeight((double) tb.getProperties().get("titleBarPrefHeight"));
        }

        @Override
        public Point2D modifyNodeSidePointer(DragPopup popup, Dockable target, double mouseX, double mouseY) {
            if (popup.getSidePointerGrid().getChildren().contains(popup.getNodeSideButtons(Side.BOTTOM))) {
                //System.err.println("2 modifyNodeSidePointer !!!!!!!!!!!!!!!!!!!!! target=" + target);                            
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
            //System.err.println("1 modifyNodeSidePointer !!!!!!!!!!!!!!!!!!!!! target=" + target + "; tabPane=" + p);            

            DockTabPane tabPane;// = null;
            TabPaneHandler paneHandler;// = null; 
            Point2D retval = null;
            if (p instanceof DockTabPane) {
                paneHandler = (TabPaneHandler) ((DockTabPane) p).paneHandler();
                tabPane = (DockTabPane) p;
                List<Node> list = new ArrayList<>();
                tabPane.getTabs().forEach(tb -> {
                    if (tb.getGraphic() != null) {
                        list.add(tb.getGraphic());
                    } else {
                        list.add(new Button());
                    }
                });
                Node node = DockUtil.findNode(list, mouseX, mouseY);
                int idx = tabPane.indexOf(mouseX, mouseY);
                System.err.println("MMMMMMMMMMMMMMMMMMMMM idx=" + idx);                
                if (idx >= 0 ) {
                    retval = new Point2D(mouseX - 5, mouseY - 5);
                } else {
                    retval = tabPane.localToScreen(
                            (tabPane.getWidth() - popup.getSidePointerGrid().getWidth()) / 2,
                            (tabPane.getHeight() - popup.getSidePointerGrid().getHeight()) / 2);
                }

            }

            return retval;
        }
        
      
        public Point2D modifyNodeSidePointer__OLD(DragPopup popup, Dockable target, double mouseX, double mouseY) {
            if (popup.getSidePointerGrid().getChildren().contains(popup.getNodeSideButtons(Side.BOTTOM))) {
                //System.err.println("2 modifyNodeSidePointer !!!!!!!!!!!!!!!!!!!!! target=" + target);                            
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
            //System.err.println("1 modifyNodeSidePointer !!!!!!!!!!!!!!!!!!!!! target=" + target + "; tabPane=" + p);            

            DockTabPane tabPane;// = null;
            TabPaneHandler paneHandler;// = null; 
            Point2D retval = null;
            if (p instanceof DockTabPane) {
                paneHandler = (TabPaneHandler) ((DockTabPane) p).paneHandler();
                tabPane = (DockTabPane) p;
                List<Node> list = new ArrayList<>();
                tabPane.getTabs().forEach(tb -> {
                    if (tb.getGraphic() != null) {
                        list.add(tb.getGraphic());
                    } else {
                        list.add(new Button());
                    }
                });
                Node node = DockUtil.findNode(list, mouseX, mouseY);
                if (node != null) {
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
            Tab tab = null;
            for (Tab tb : getDockPane().getTabs()) {
                if (tb.getGraphic() != null && (tb.getGraphic() instanceof TabGraphic)) {
                    if (((TabGraphic) tb.getGraphic()).getOwner() == dockNode) {
                        tab = tb;
                        break;
                    }
                }
            }

            if (tab != null) {
                showContentTitleBar(DockRegistry.dockable(dockNode));
                getDockPane().getTabs().remove(tab);
            }

        }
    }//class TabPaneHandler
    
    protected int indexOf(double x, double y) {
        List<Double> list = getTabBoundaries();
        int retval = -1;
        if ( list.isEmpty() ) {
            return retval;
        }
        Tab tab = getTabs().get(0);
        double h = ((Region)tab.getGraphic()).getHeight();
        double upper = localToScreen(0, 0).getY();
        double lower = getTabs().get(0).getContent().localToScreen(0, 0).getY();
        System.err.println("upper=" + upper + "; lower=" + lower);
        
        for ( int i=0; i < list.size() - 1; i++) {
            if ( x >= list.get(i) && x < list.get(i+1)) {
                if ( y >= upper && y <= lower) {
                    retval = i;
                    break;
                }
            }
        }
        return retval;
    }
    protected List<Double> getTabBoundaries() {
        List<Double> list = new ArrayList<>();
        for ( int  i= 0; i < getTabs().size(); i++) {
            Node n = getTabs().get(i).getGraphic();
            double x = n.localToScreen(0,0).getX();
            list.add(x);
            if ( i == getTabs().size()-1) {
                Node g = getTabs().get(i).getGraphic();
                double w = 0;
                if ( g instanceof Region ) {
                    w = ((Region)g).getWidth();
                } else  if (g instanceof Rectangle) {
                    w = ((Rectangle)g).getWidth();
                }
                list.add(x + w);
            }
        }
        return list;
    }
}//DockTabPane
