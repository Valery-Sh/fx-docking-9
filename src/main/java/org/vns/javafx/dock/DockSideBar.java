package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DragPopup;
import org.vns.javafx.dock.api.FloatStageBuilder;
import org.vns.javafx.dock.api.PaneHandler;
import org.vns.javafx.dock.api.StageBuilder;

/**
 *
 * @author Valery Shyshkin
 */
public class DockSideBar extends StackPane implements DockPaneTarget {
    
    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    public enum Rotation {
        DEFAULT(0),
        DOWN_UP(-90), // usualy if Side.LEFT
        UP_DOWN(90);  // usually when SideRight

        private double angle;

        Rotation(double value) {
            this.angle = value;
        }

        public double getAngle() {
            return angle;
        }

    }
    private SidePaneHandler paneHandler;

    private ObjectProperty<Side> sideProperty = new SimpleObjectProperty<>();

    private ToolBar toolBar;

    private ObjectProperty<Rotation> rotationProperty = new SimpleObjectProperty<>();

    private final BooleanProperty hideOnExitProperty = new SimpleBooleanProperty(false);

    public DockSideBar() {
        init();
    }

    private void init() {
        paneHandler = new SidePaneHandler(this);
        toolBar = new ToolBar();
        //toolBar.setStyle("-fx-background-color: aqua");
        getStyleClass().add("dock-side-bar");
        toolBar.getStyleClass().add("tool-bar");

        getChildren().add(toolBar);
        toolBar.setOrientation(getOrientation());

        sceneProperty().addListener((v, ov, nv) -> {
            sceneChanged(ov, nv);
        });

        sideProperty.addListener((v, ov, nv) -> {
            paneHandler.getItems().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        rotationProperty.addListener((v, ov, nv) -> {
            paneHandler.getItems().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        toolBar.orientationProperty().addListener((v, ov, nv) -> {
            paneHandler.getItems().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        
        setSide(Side.RIGHT);
        setRotation(Rotation.UP_DOWN);
    }

    public ObjectProperty<Rotation> rotationProperty() {
        return rotationProperty;
    }

    public BooleanProperty hideOnExitProperty() {
        return hideOnExitProperty;
    }

    public boolean isHideOnExit() {
        return hideOnExitProperty.get();
    }

    public void setHideOnExit(boolean value) {
        hideOnExitProperty.set(value);
    }

    public Rotation getRotation() {
        return rotationProperty.get();
    }

    public void setRotation(Rotation rotation) {
        paneHandler.getItems().keySet().forEach(g -> {
            Button btn = (Button) g.getChildren().get(0);
            btn.setRotate(rotation.getAngle());
        });
        this.rotationProperty.set(rotation);

    }

    protected void sceneChanged(Scene oldValue, Scene newValue) {
        newValue.windowProperty().addListener((v, ov, nv) -> {
            stageChanged(ov, nv);
        });
        Stage parent = (Stage) newValue.getWindow();
    }

    protected void stageChanged(Window oldValue, Window newValue) {
        if (!(newValue instanceof Stage)) {
            return;
        }
        Stage stage = (Stage) newValue;
        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, this::stageClicked);
        getItems().values().forEach(d -> {
            d.adjustScreenPos();
        });
    }

    protected void stageClicked(MouseEvent ev) {
        //11.01if (DockUtil.paneIndicatorContains(toolBar, ev.getScreenX(), ev.getScreenY())) {
        if (toolBar.localToScreen(toolBar.getBoundsInLocal()).contains(ev.getScreenX(), ev.getScreenY()) ) {
            return;
        }

        paneHandler.getItems().forEach((g, d) -> {
            Window w = d.getDockable().node().getScene().getWindow();
            if (w instanceof Stage) {
                ((Stage) w).close();
            }
        });
    }

    @Override
    public PaneHandler paneHandler() {
        return this.paneHandler;
    }

    protected ObservableMap<Group, Container> getItems() {
        return paneHandler.getItems();
    }
    public Button getButton(Dockable dockable) {
        Button retval = null;
        for ( Map.Entry<Group,Container> en : getItems().entrySet()) {
            if ( en.getValue().getDockable() == dockable ) {
                retval = (Button) en.getKey().getChildren().get(0);
                break;
            }
        }
        return retval;
    }
    public List<Button> getButtons() {
        List<Button> retval = new ArrayList<>();
        for ( Group g : getItems().keySet()) {
            retval.add((Button) g.getChildren().get(0));
        }
        return retval;
    }
    
    public Side getSide() {
        return sideProperty.get();
    }

    public void setSide(Side side) {
        sideProperty.set(side);
    }

    public Orientation getOrientation() {
        return toolBar.getOrientation();
    }

    public void setOrientation(Orientation orientation) {
        toolBar.setOrientation(orientation);
    }

    public Button dock(Dockable dockable) {
        return this.dock(dockable, null);
    }
    
    public Button dock(Dockable dockable, String btnText) {
        if ( btnText != null ) {
            dockable.nodeHandler().getProperties().setProperty("user-title", btnText);
        }
        paneHandler.dock(dockable, null);
        return getButton(dockable);
        
    }

    @Override
    public Pane pane() {
        return this;
    }

    public static class SidePaneHandler extends PaneHandler {

        private final ObservableMap<Group, Container> items = FXCollections.observableHashMap();

        public SidePaneHandler(DockSideBar dockPane) {
            super(dockPane);
            init();
        }

        private void init() {
            //setUsedAsDockTarget(false);
            setSidePointerModifier(this::modifyNodeSidePointer);
        }

        @Override
        protected void inititialize() {
            DockRegistry.start();
        }

        @Override
        public FloatStageBuilder getStageBuilder(Dockable dockable) {
            return getItem(dockable).getStageBuilder();
        }

        protected Container getItem(Dockable d) {
            Container retval = null;
            for (Container c : items.values()) {
                if (c.getDockable() == d) {
                    retval = c;
                    break;
                }
            }
            return retval;
        }

        @Override
        protected boolean isDocked(Node node) {
            boolean retval = false;
            for (Container c : items.values()) {
                if (c.getDockable().node() == node) {
                    retval = true;
                    break;
                }
            }
            return retval;
        }

        protected String getButtonText(Dockable d) {
            String txt = d.nodeHandler().getTitle();
            if (d.nodeHandler().getProperties().getProperty("user-title") != null) {
                txt = d.nodeHandler().getProperties().getProperty("user-title");
            } else if (d.nodeHandler().getProperties().getProperty("short-title") != null) {
                txt = d.nodeHandler().getProperties().getProperty("short-title");
            }
            if ( txt == null || txt.trim().isEmpty() ) {
                txt = "Dockable";
            }
            return txt;
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
            //DockSideBar dockPane = (DockSideBar) getDockPane();
            Button itemButton = new Button(getButtonText(dockable));
            
            itemButton.getStyleClass().add("item-button");
            Group item = new Group(itemButton);
            Container container = new Container(dockable);
            getItems().put(item, container);

            //DockTabPane.TabTitleBar tab = new DockTabPane2.TabTitleBar((Dockable) node, dockPane);
            //tab.setId("TabTitleBar:" + node.getId());
            int idx = -1;
            if (mousePos != null) {
                // Node sb = DockUtil.findNode(dockPane.getTabs(), mousePos.getX(), mousePos.getY());
                //if ( sb != null ) {
                //    idx = dockPane.getTabsPane().getChildren().indexOf(sb);
                //}
            }
            if (idx >= 0) {
                //  dockPane.getTabsPane().getChildren().add(idx,tab);
                //  dockPane.getTabsPane().getChildren().add(idx+1,tab.getSeparator());
            } else {
                //dockPane.getTabsPane().getChildren().add(tab);
                //dockPane.getTabsPane().getChildren().add(tab.getSeparator());

            }
            itemButton.setRotate(((DockSideBar) getDockPane()).getRotation().getAngle());
            itemButton.setOnAction(a -> {
                a.consume();
                if (dockable.node().getScene().getWindow().isShowing()) {
                    dockable.node().getScene().getWindow().hide();
                    return;
                }
                getItems().values().forEach(d -> {
                    d.getDockable().node().getScene().getWindow().hide();
                });
                show(itemButton);
                container.changeSize();
            });

            ((DockSideBar) getDockPane()).toolBar.getItems().add(item);
            DockNodeHandler nodeHandler = dockable.nodeHandler();
            if (nodeHandler.getPaneHandler() == null || nodeHandler.getPaneHandler() != this) {
                nodeHandler.setPaneHandler(this);
            }

            container.getStageBuilder().setSupportedCursors(getSupportedCursors());

            container.getStageBuilder().createStage(dockable).setAlwaysOnTop(true);
            Stage st = (Stage) container.getDockable().node().getScene().getWindow();

            if (getDockPane().getScene() != null && getDockPane().getScene().getWindow() != null && getDockPane().getScene().getWindow().isShowing()) {
                container.adjustScreenPos();
            }
            //nodeHandler.setDocked(true);
            //changeDockedState(dockable, true);
            container.setDocked(true);

        }

        public Cursor[] getSupportedCursors() {
            List<Cursor> list = new ArrayList<>();
            switch (((DockSideBar) getDockPane()).getSide()) {
                case RIGHT:
                    list.add(Cursor.W_RESIZE);
                    break;
                case LEFT:
                    list.add(Cursor.E_RESIZE);
                    break;
                case BOTTOM:
                    list.add(Cursor.N_RESIZE);
                    break;
                case TOP:
                    list.add(Cursor.S_RESIZE);
                    break;
            }

            return list.toArray(new Cursor[0]);
        }

        protected void rotate(Button btn) {
            //btn.setRotate(0);
            if (btn.getRotate() == 90) {

            }

            switch (((DockSideBar) getDockPane()).getSide()) {
                case RIGHT:
                    btn.setRotate(90);
                    break;
                case LEFT:
                    btn.setRotate(-90);
                    break;
                default:
                    btn.setRotate(0);
                    break;
            }
        }

        @Override
        public void remove(Node dockNode) {
            Group r = null;
            for (Map.Entry<Group, Container> en : items.entrySet()) {
                if (en.getValue().getDockable().node() == dockNode) {
                    r = en.getKey();
                    break;
                }
            }
            if (r != null) {
                items.get(r).removeListeners();
                items.remove(r);
                ((DockSideBar) getDockPane()).toolBar.getItems().remove(r);
            }
        }

        protected ObservableMap<Group, Container> getItems() {
            return items;
        }

        public void show(Button btn) {
            Group group = (Group) btn.getParent();
            Dockable dockable = getItems().get(group).getDockable();
            getItems().get(group).changeSize();
            ((Stage) dockable.node().getScene().getWindow()).show();
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

            DockSideBar sideBar;// = null;
            Point2D retval = null;
            if (p instanceof DockSideBar) {
                sideBar = (DockSideBar) p;
                /*                Node node = DockUtil.findNode(sideBar.getTabs(), mouseX, mouseY);
                if ( node != null ) {
                    retval =  new Point2D(mouseX-5,mouseY-5);
                } else if (DockUtil.paneIndicatorContains(sideBar.getTabHBox(), mouseX, mouseY)) {    
                    retval =  new Point2D(mouseX-5,mouseY-5);
                } else {
                    retval = sideBar.localToScreen(
                                    (sideBar.getWidth()  - popup.getSidePointerGrid().getWidth()) / 2, 
                                    (sideBar.getHeight() - popup.getSidePointerGrid().getHeight()) / 2);
                }
                 */
                retval = sideBar.localToScreen((sideBar.getWidth() - popup.getSidePointerGrid().getWidth()) / 2,
                        (sideBar.getHeight() - popup.getSidePointerGrid().getHeight()) / 2);

            }

            return retval;
        }

    }//class

    public static class Container implements ChangeListener<Number> {

        private final Dockable dockable;
        private final StageBuilder stageBuilder;

        private final BooleanProperty hideOnExitProperty = new SimpleBooleanProperty(false);
        private final BooleanProperty floatingProperty = new SimpleBooleanProperty(false);

        private EventHandler<MouseEvent> mouseExitEventListener;

        public Container(Dockable dockable) {
            this.dockable = dockable;
            stageBuilder = new StageBuilder(dockable.nodeHandler());
        }

        public void setDocked(boolean docked) {
            DockSideBar sb = getSideBar();
            if (sb.isHideOnExit()) {
                System.err.println("1 hideOnExitProperty.addListener");
                addMouseExitListener();
            }
            hideOnExitProperty.bind(sb.hideOnExitProperty());
            floatingProperty.bind(dockable.nodeHandler().floatingProperty());
            floatingProperty.addListener((v, oldValue, newValue) -> {
                if (newValue) {
                    removeMouseExitListener();
                }
            });
            hideOnExitProperty.addListener((v, oldValue, newValue) -> {
                System.err.println("hideOnExitProperty.addListener");
                if (newValue) {
                    addMouseExitListener();
                } else {
                    removeMouseExitListener();
                }
            });
        }

        /*        public BooleanProperty hideOnExitProperty() {
            return hideOnExitProperty;
        }

        public boolean isHideOnExit() {
            return hideOnExitProperty.get();
        }

        public void setHideOnExit(boolean value) {
            hideOnExitProperty.set(value);
        }
         */
        public DockSideBar getSideBar() {
            return (DockSideBar) dockable.nodeHandler().getPaneHandler().getDockPane();
        }

        public void addMouseExitListener() {
            mouseExitEventListener = this::mouseExited;
            dockable.node().getScene().getWindow().addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
        }

        public void removeMouseExitListener() {
            System.err.println("removeMouseExitListener");
            dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
            dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
        }

        public Dockable getDockable() {
            return dockable;
        }

        protected void mouseExited(MouseEvent ev) {
            if ((ev.getSource() instanceof Window) && DockUtil.contains((Window) ev.getSource(), ev.getScreenX(), ev.getScreenY())) {
                return;
            }
            if (!ev.isPrimaryButtonDown() && !dockable.nodeHandler().isFloating()) {
                dockable.node().getScene().getWindow().hide();
            }
            ev.consume();
        }

        public void adjustScreenPos() {
            SidePaneHandler handler = (SidePaneHandler) dockable.nodeHandler().getPaneHandler();
            Stage ownerStage = (Stage) ((DockSideBar) handler.getDockPane()).getScene().getWindow();
            ownerStage.xProperty().addListener(this);
//            ownerStage.xProperty().addListener((v, ov, nv) -> {
//                changeSize();
//            });
            ownerStage.yProperty().addListener(this);
//            ownerStage.yProperty().addListener((v, ov, nv) -> {
//                changeSize();
//            });
            ownerStage.widthProperty().addListener(this);

//            ownerStage.widthProperty().addListener((v, ov, nv) -> {
//                changeSize();
//            });
            ownerStage.heightProperty().addListener(this);
//            ownerStage.heightProperty().addListener((v, ov, nv) -> {
//                changeSize();
//            });
            changeSize();
        }

        public void removeListeners() {
            SidePaneHandler handler = (SidePaneHandler) dockable.nodeHandler().getPaneHandler();
            Stage ownerStage = (Stage) ((DockSideBar) handler.getDockPane()).getScene().getWindow();
            ownerStage.xProperty().removeListener(this);
            ownerStage.yProperty().removeListener(this);
            ownerStage.widthProperty().removeListener(this);
            ownerStage.heightProperty().removeListener(this);
            stageBuilder.removeListeners(dockable);
        }

        protected void changeSide() {
            SidePaneHandler handler = (SidePaneHandler) dockable.nodeHandler().getPaneHandler();
            stageBuilder.setSupportedCursors(handler.getSupportedCursors());
        }

        protected void changeSize() {
            Stage stage = (Stage) dockable.node().getScene().getWindow();
            DockSideBar sb = (DockSideBar) dockable.nodeHandler().getPaneHandler().getDockPane();
            ToolBar tb = sb.toolBar;
            Point2D pos = sb.localToScreen(0, 0);

            /*            System.err.println("  sideBar.height=" + sb.getHeight());
            System.err.println("  sideBar.width=" + sb.getWidth());
            System.err.println("  titleBar.height=" + tb.getHeight());
            System.err.println("  titleBar.width=" + tb.getWidth());
            System.err.println("  pos.getX=" + pos.getX());
            System.err.println("stage.getX=" + stage.getX());
            System.err.println("stage.getWidth=" + stage.getWidth());
             */
            //System.err.println("dockable.getWidth=" + ((Region) dockable.node()).getWidth());
//            if (sb.getOrientation() == Orientation.VERTICAL) {
            switch (sb.getSide()) {
                case TOP:
                    stage.setX(pos.getX());
                    stage.setY(pos.getY() + sb.getHeight());
                    stage.setWidth(sb.getWidth());
                    break;
                case BOTTOM:
                    stage.setX(pos.getX());
                    stage.setY(pos.getY() - stage.getHeight());
                    stage.setWidth(sb.getWidth());
                    break;
                case RIGHT:
                    stage.setY(pos.getY());
                    //stage.setX(pos.getX() - ((Region) dockable.node()).getWidth());
                    stage.setX(pos.getX() - stage.getWidth());
                    stage.setHeight(sb.getHeight());
                    break;
                case LEFT:
                    stage.setY(pos.getY());
                    stage.setX(pos.getX() + sb.getWidth());
                    stage.setHeight(sb.getHeight());
                    break;
            }

            /*            } else {
                switch (sb.getSide()) {
                    case TOP:
                        stage.setX(pos.getX());
                        stage.setY(pos.getY() + sb.getHeight());
                        stage.setWidth(sb.getWidth());
                        break;
                    case BOTTOM:
                        stage.setX(pos.getX());
                        stage.setY(pos.getY() - stage.getHeight());
                        stage.setWidth(sb.getWidth());
                        break;
                    case RIGHT:
                        stage.setY(pos.getY());
                        stage.setX(pos.getX() - stage.getWidth());
                        stage.setHeight(sb.getHeight());
                        break;
                    case LEFT:
                        stage.setY(pos.getY());
                        stage.setX(pos.getX() + sb.getWidth());
                        stage.setHeight(sb.getHeight());
                        break;
                }
            }
             */
 /*            System.err.println("---------------------------------");
            System.err.println("1  sideBar.height=" + sb.getHeight());
            System.err.println("1  sideBar.width=" + sb.getWidth());
            System.err.println("1  titleBar.height=" + tb.getHeight());
            System.err.println("1  titleBar.width=" + tb.getWidth());
            System.err.println("1  pos.getX=" + pos.getX());
            System.err.println("1 stage.getX=" + stage.getX());
            System.err.println("1 stage.getWidth=" + stage.getWidth());
            System.err.println("1 dockable.getWidth=" + ((Region) dockable.node()).getWidth());

            System.err.println("==================================");
             */
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            changeSize();
        }

        public StageBuilder getStageBuilder() {
            return stageBuilder;
        }

    }
}//class
