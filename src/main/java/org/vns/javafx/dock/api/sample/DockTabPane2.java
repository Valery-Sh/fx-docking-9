package org.vns.javafx.dock.api.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
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
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragPopup;
import org.vns.javafx.dock.api.PaneHandler;
import org.vns.javafx.dock.api.properties.TitleBarProperty;

/**
 *
 * @author Valery
 */
public class DockTabPane2 extends VBox implements Dockable, DockPaneTarget {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private final StringProperty titleProperty = new SimpleStringProperty();

    private final DockNodeHandler nodeHandler = new DockNodeHandler(this);

    private Button menuButton;

    private final HBox tabListPane = new HBox();
    private final HBox tabHBox = new HBox();    
    
    private Button leftScrollButton = new Button();    
    private Button rightScrollButton = new Button();    
    
    private StackPane stackPane;
    
    private Pane dragPane;
    private Button dragButton;
    
    private final Map<Dockable, Object> listeners = new HashMap<>();

    private TabPaneHandler paneHandler;
    
    private final ScrollPane scrollPane = new ScrollPane();
    
    public DockTabPane2() {
        init();
    }
    private void init() {
        
        getStyleClass().add("dock-tab-pane2");
        
        paneHandler = new TabPaneHandler(this);

        stackPane = new StackPane();
        stackPane.setManaged(true);
        
        initTitleBarMenu();
        initDragPane();
        initScrollPane();
        
        Pane fillPane = new Pane();
        HBox.setHgrow(fillPane, Priority.ALWAYS);
        
        //titleBox.getChildren().addAll(dragPane,tabListPane, fillPane, menuButton);
        tabHBox.getChildren().addAll(dragPane,scrollPane, fillPane,leftScrollButton, rightScrollButton, menuButton);
        //titleBox.getChildren().addAll(scrollPane, fillPane,leftScrollButton, rightScrollButton, menuButton);        
        tabHBox.getStyleClass().add("tab-hbox");
        
        getChildren().addAll(tabHBox, stackPane);

        stackPane.setStyle("-fx-background-color: white");
        stackPane.setPrefHeight(getPrefHeight());
        stackPane.setMaxHeight(Double.MAX_VALUE);
        

        //getTitleBarList().addListener(DockTabPane2.this::onChangeTitleBars);

        nodeHandler.titleBarProperty().activeChoosedPseudoClassProperty().addListener(this::focusChanged);

        nodeHandler().setTitleBar(new DockTitleBar(this));
        
        //nodeHandler.setTitleBar(dragButton);
        //dragButton.setMouseTransparent(true);
        nodeHandler.setDragSource(dragButton);
        
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
        //dragPane.getStyleClass().add("insert-tab-drag-pane");
        dragPane.getStyleClass().add("drag-pane");
        dragButton = new Button();
        dragButton.setTooltip(new Tooltip("Drag Tab Pane"));
        //dragPane.getChildren().add(dragLabel);
        dragPane.getChildren().add(dragButton);
        //dragButton.getStyleClass().add("insert-tab-drag-button");
        dragButton.getStyleClass().add("drag-button");
        dragButton.setFocusTraversable(false);
        StackPane.setAlignment(dragButton, Pos.CENTER);
        
    }
    protected void initScrollPane() {
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.getStyleClass().add("edge-to-edge");
        
        //scrollPane.setBorder(Border.EMPTY);
        scrollPane.setContent(tabListPane);
        
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //scrollPane.setPrefHeight(10);
        //scrollPane.setMaxHeight(10); 
        scrollPane.setMinHeight(0); 
        scrollPane.setMinWidth(0); 
        
/*        scrollPane.minViewportHeightProperty().addListener((observable, oldValue, newValue) -> {
            Separator s = new Separator();
            s.setOrientation(Orientation.VERTICAL);
            s.setMinSize(0,0);
            s.setMaxSize(0,0);
            
            if ( tabListPane.getChildren().size() > 0 && (tabListPane.getChildren().get(0) instanceof Separator) ) {
                //titleBarListPane.getChildren().set(0,s);
            } else {
                //titleBarListPane.getChildren().add(0,s);
            }

        });
  */      

/*        tabListPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if ( ! newValue.equals(0d)) {
                
                scrollPane.minViewportHeightProperty().set((double) newValue);
                if (leftScrollButton != null && leftScrollButton.isFocused() && rightScrollButton != null) {
                    //rightScrollButton.requestFocus();
                } else if ( rightScrollButton != null && ! rightScrollButton.isFocused() ){
                    //rightScrollButton.requestFocus();
                } else if ( ! tabListPane.getChildren().isEmpty() ) {
                    for ( Node node : tabListPane.getChildren() ) {
                        if ( (node instanceof Separator) && ! node.isFocused() ) {
                            //node.requestFocus();
                        }
                    }
                }
            }
        });
*/
        //
        // We set the hmax property basing on the item count. We take into account
        // that ther is a separator after each TabTitleBar instance.  
        //
        scrollPane.hmaxProperty().bind(Bindings.divide(Bindings.size(getTabListPane().getChildren()),2));
        

        leftScrollButton.setOnAction(a -> {
            if ( scrollPane.getHvalue() != scrollPane.getHmin()) {
                scrollPane.setHvalue(scrollPane.getHvalue() - 1);
            }
        });
        
        leftScrollButton.setFocusTraversable(false);
        leftScrollButton.borderProperty().set(Border.EMPTY);
        leftScrollButton.getStyleClass().addAll("hscroll-button","left-scroll");
        leftScrollButton.setTooltip(new Tooltip("Scroll Left"));
        
        rightScrollButton.setFocusTraversable(false);
        rightScrollButton.borderProperty().set(Border.EMPTY);
        rightScrollButton.getStyleClass().addAll("hscroll-button","right-scroll");
        rightScrollButton.setTooltip(new Tooltip("Scroll Right"));
        
        rightScrollButton.setOnAction(a -> {
            if ( scrollPane.getHvalue() != scrollPane.getHmax()) {
                scrollPane.setHvalue(scrollPane.getHvalue() + 1);
            }
        });
        
    }
    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public HBox getTabListPane() {
        return tabListPane;
    }
    
    public void showTitleMenuButton(boolean show) {
        if ( show && tabHBox.getChildren().indexOf(menuButton) < 0 ) {
            tabHBox.getChildren().add(menuButton);
        } else if ( ! show && tabHBox.getChildren().indexOf(menuButton) >= 0) {
            tabHBox.getChildren().remove(menuButton);
        }
    }

    public Button getMenuButton() {
        return menuButton;
    }

    public void setMenuButton(Button menuButton) {
        this.menuButton = menuButton;
    }

    public Button getLeftScrollButton() {
        return leftScrollButton;
    }

    public void setLeftScrollButton(Button leftScrollButton) {
        this.leftScrollButton = leftScrollButton;
    }

    public Button getRightScrollButton() {
        return rightScrollButton;
    }

    public void setRightScrollButton(Button rightScrollButton) {
        this.rightScrollButton = rightScrollButton;
    }


    
    public void showTitleScrollButtons(boolean show) {
        int leftIdx = tabHBox.getChildren().indexOf(leftScrollButton);
        int rightIdx = tabHBox.getChildren().indexOf(rightScrollButton);
        
        boolean leftOn = leftIdx >= 0;
        boolean rightOn = rightIdx >= 0;

        if ( show ) {
            if ( !leftOn && rightOn ) {
                tabHBox.getChildren().add(rightIdx,leftScrollButton); 
            } else if (leftOn && ! rightOn) {
                tabHBox.getChildren().add(rightScrollButton); 
            } if ( !leftOn && !rightOn) {
                tabHBox.getChildren().add(leftScrollButton); 
                tabHBox.getChildren().add(rightScrollButton); 
            }
        } else {
            if ( leftOn ) {
                tabHBox.getChildren().remove(leftScrollButton);
            }
            if ( rightOn ) {
                tabHBox.getChildren().remove(rightScrollButton);
            }
        }
    }

    public List<Node> getTabList() {
        return tabListPane.getChildren().filtered( node -> { return (node instanceof TabTitleBar); } );
    }

    public HBox getTabHBox() {
        return tabHBox;
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
        getTabList().forEach(tab -> {
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
        getTabList().forEach(tab -> {
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
    public PaneHandler paneHandler() {
        return paneHandler;
    }

    public static class TabTitleBar extends DockTitleBar {

        private final DockTabPane2 tabPane;

        private double titleBarMinHeight;
        private double titleBarPrefHeight;
        private boolean titleBarVisible;
        private Separator separator;

        public TabTitleBar(Dockable dockNode, DockTabPane2 tabPane) {
            super(dockNode);
            this.tabPane = tabPane;
            //setMouseTransparent(true);
            init();
        }

        private void init() {
            saveContentTitleBar();
            getStateButton().setMouseTransparent(true);
//            getCloseButton().setMouseTransparent(true);
//            getPinButton().setMouseTransparent(true);
            removeButtons(getCloseButton(),getPinButton());
            setOnMouseClicked(ev -> {
                getStateButton().requestFocus();
                tabPane.choose(getOwner());
                ev.consume();
            });
            //getStyleClass().add("tab-title-bar");
            for ( String s : getStateButton().getStyleClass() ) {
                System.err.println("STYLE: " + s);
            }
            //getLabel().getStyleClass().set(0, "insert-tab-title-label");
            //getStateButton().getStyleClass().remove("button");
            for ( String s : getStateButton().getStyleClass() ) {
                System.err.println("1 STYLE: " + s);
            }
            
            separator = new Separator();
            separator.setOrientation(Orientation.VERTICAL);
            
        }

        public Separator getSeparator() {
            return separator;
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

        public TabPaneHandler(DockTabPane2 dockPane) {
            super((Pane)dockPane);
            init();
        }
        private void init() {
            setSidePointerModifier(this::modifyNodeSidePointer);
        }
        @Override
        protected void initSplitDelegate() {
        }

        @Override
        protected boolean isDocked(Node node) {
            boolean retval = ((DockTabPane2)getDockPane()).getContents().contains(node);
            return retval;
        }

        @Override
        public Dockable dock(Dockable node, Side dockPos, Dockable target) {
           return null;
        }

        @Override
        protected void doDock(Point2D mousePos, Node node, Side dockPos) {

            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            DockTabPane2 dockPane = (DockTabPane2) getDockPane();
            TabTitleBar tab = new TabTitleBar((Dockable) node, dockPane);
            tab.setId("TabTitleBar:" + node.getId());
            int idx = -1;
            if ( mousePos != null ) {
               Node tb = DockUtil.findNode(dockPane.getTabList(), mousePos.getX(), mousePos.getY());
               if ( tb != null ) {
                   idx = dockPane.getTabListPane().getChildren().indexOf(tb);
               }
            }
            if ( idx >= 0 ) {
                dockPane.getTabListPane().getChildren().add(idx,tab);
                dockPane.getTabListPane().getChildren().add(idx+1,tab.getSeparator());
            } else {
                dockPane.getTabListPane().getChildren().add(tab);
                dockPane.getTabListPane().getChildren().add(tab.getSeparator());
                
            }
            
            ((Region)node).prefHeightProperty().bind(dockPane.heightProperty());
            ((Region)node).prefWidthProperty().bind(dockPane.widthProperty());
             
            dockPane.getContents().add(node);
            
            if (DockRegistry.isDockable(node)) {
                DockNodeHandler nodeHandler = ((Dockable) node).nodeHandler();
                tab.hideContentTitleBar();
                nodeHandler.setDragSource(tab);
                if (nodeHandler.getPaneHandler() == null || nodeHandler.getPaneHandler() != this) {
                    nodeHandler.setPaneHandler(this);
                }
                //nodeHandler.setDocked(true);
                //changeDockedState((Dockable)node , true);
            }
        }

        @Override
        public Point2D modifyNodeSidePointer(DragPopup popup, Dockable target, double mouseX, double mouseY) {
            if ( popup.getSidePointerGrid().getChildren().contains(popup.getNodeSideButtons(Side.BOTTOM))) {
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
            //System.err.println("1 modifyNodeSidePointer !!!!!!!!!!!!!!!!!!!!! target=" + target + "; dockPane=" + p);            
            
            DockTabPane2 tabPane;// = null;
            TabPaneHandler paneHandler;// = null; 
            Point2D retval = null; 
            if ( p instanceof DockTabPane2) {
                paneHandler = ((DockTabPane2)p).paneHandler;
                tabPane = (DockTabPane2)p;
                Node node = DockUtil.findNode(tabPane.getTabList(), mouseX, mouseY);
                if ( node != null ) {
                    retval =  new Point2D(mouseX-5,mouseY-5);
                } else if (DockUtil.contains(tabPane.getTabHBox(), mouseX, mouseY)) {    
                    retval =  new Point2D(mouseX-5,mouseY-5);
                } else {
                    retval = tabPane.localToScreen(
                                    (tabPane.getWidth()  - popup.getSidePointerGrid().getWidth()) / 2, 
                                    (tabPane.getHeight() - popup.getSidePointerGrid().getHeight()) / 2);
                }
            }

            return retval;
        }

        @Override
        public void remove(Node dockNode) {
            TabTitleBar tb = null;
            for (Node tbNode : ((DockTabPane2) getDockPane()).getTabList()) {
                if (((TabTitleBar) tbNode).getOwner() == dockNode) {
                    tb = (TabTitleBar) tbNode;
                    break;
                }
            }
            if (tb != null) {
                tb.showContentTitleBar();
                ((DockTabPane2) getDockPane()).getTabListPane().getChildren().remove(tb);
                ((DockTabPane2) getDockPane()).getTabListPane().getChildren().remove(tb.getSeparator());
            }
            ((DockTabPane2) getDockPane()).getContents().remove(dockNode);
        }

    }//class TabPaneHandler
}//DockTabPane
