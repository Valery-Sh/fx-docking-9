package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.vns.javafx.dock.api.DockNodeController;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.FloatStageBuilder;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.DragPopup;
import org.vns.javafx.dock.api.IndicatorPopup;
import org.vns.javafx.dock.api.SideIndicator;
import org.vns.javafx.dock.api.SideIndicatorTransformer.NodeIndicatorTransformer;
import org.vns.javafx.dock.api.StageBuilder;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty(value = "items")
public class DockSideBar extends Control implements Dockable, DockPaneTarget, ListChangeListener {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private DockNodeController nodeController = new DockNodeController(this);

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockNodeController nodeController() {
        return this.nodeController;
    }

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
    private SidePaneController paneController;

    private final ObjectProperty<Side> sideProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<Rotation> rotationProperty = new SimpleObjectProperty<>();

    private final BooleanProperty hideOnExitProperty = new SimpleBooleanProperty(false);

    private final CustomToolBar delegate = new CustomToolBar();

    private final ObservableList<Dockable> items = FXCollections.observableArrayList();

    public DockSideBar() {
        init();

    }

    public DockSideBar(Dockable... items) {
        super();
        this.items.addAll(items);
    }

    private void init() {
        paneController = new SidePaneController(this);
        setOrientation(Orientation.HORIZONTAL);
        getStyleClass().clear();
        getStyleClass().add("dock-side-bar");

        sceneProperty().addListener((v, ov, nv) -> {
            sceneChanged(ov, nv);
        });

        sideProperty.addListener((v, ov, nv) -> {
            paneController.getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        rotationProperty.addListener((v, ov, nv) -> {
            paneController.getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        getDelegate().orientationProperty().addListener((v, ov, nv) -> {
            paneController.getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });

        setSide(Side.TOP);
        setRotation(Rotation.DEFAULT);

        items.addListener(this);
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public ObservableList<Dockable> getItems() {
        return items;
    }

////////////////////////////////////////////////////
    @Override
    public void onChanged(Change change) {
        itemsChanged(change);

    }

    protected void itemsChanged(ListChangeListener.Change<? extends Dockable> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List<? extends Dockable> list = change.getRemoved();
                for (Dockable d : list) {
                    paneController.undock(d.node());
                }

            }
            if (change.wasAdded()) {
                List<? extends Dockable> list = change.getAddedSubList();
                for (Dockable d : list) {
                    dock(d);
                }
            }
        }//while
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

////////////////////////////////////////////////////    
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
        paneController.getItemMap().keySet().forEach(g -> {
            Button btn = (Button) g.getChildren().get(0);
            btn.setRotate(rotation.getAngle());
        });
        this.rotationProperty.set(rotation);

    }

    protected void sceneChanged(Scene oldValue, Scene newValue) {
        newValue.windowProperty().addListener((v, ov, nv) -> {
            stageChanged(ov, nv);
        });
        //Stage parent = (Stage) newValue.getWindow();
    }

    protected void stageChanged(Window oldValue, Window newValue) {
        if (!(newValue instanceof Stage)) {
            return;
        }
        Stage stage = (Stage) newValue;
        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, this::stageClicked);
        stage.setTitle("owner");
        getSideItems().values().forEach(d -> {
            d.adjustScreenPos();
        });
    }

    protected void stageClicked(MouseEvent ev) {
        if (localToScreen(getBoundsInLocal()).contains(ev.getScreenX(), ev.getScreenY())) {
            return;
        }

        paneController.getItemMap().forEach((g, d) -> {
            if (d.getDockable().node().getScene() != null && d.getDockable().node().getScene().getWindow() != null) {
                Window w = d.getDockable().node().getScene().getWindow();
                if (w instanceof Stage) {
                    ((Stage) w).close();
                }
            }
        });
    }

    @Override
    public DockTargetController paneController() {
        return this.paneController;
    }

    protected ObservableMap<Group, Container> getSideItems() {
        return paneController.getItemMap();
    }

    public Button getButton(Dockable dockable) {
        Button retval = null;
        for (Map.Entry<Group, Container> en : getSideItems().entrySet()) {
            if (en.getValue().getDockable() == dockable) {
                retval = (Button) en.getKey().getChildren().get(0);
                break;
            }
        }
        return retval;
    }

    public List<Button> getButtons() {
        List<Button> retval = new ArrayList<>();
        for (Group g : getSideItems().keySet()) {
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
        return getDelegate().getOrientation();
    }

    public void setOrientation(Orientation orientation) {
        getDelegate().setOrientation(orientation);
    }

    public Button dock(Dockable dockable) {
        return this.dock(dockable, null);
    }

    public Button dock(Dockable dockable, String btnText) {
        if (btnText != null) {
            dockable.nodeController().getProperties().setProperty("user-title", btnText);
        }
        paneController.dock(dockable, null);
        return getButton(dockable);

    }

    @Override
    public Region pane() {
        return this;
    }

    public CustomToolBar getDelegate() {
        return delegate;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockSideBarSkin(this);
    }

    public static class DockSideBarSkin extends SkinBase<DockSideBar> {

        public DockSideBarSkin(DockSideBar control) {
            super(control);
            getChildren().add(control.getDelegate());
        }
    }

    public static class SidePaneController extends DockTargetController {

        private final ObservableMap<Group, Container> itemMap = FXCollections.observableHashMap();

        public SidePaneController(Region dockPane) {
            super(dockPane);
            init();
        }

        private void init() {
            //setSidePointerModifier(this::modifyNodeSidePointer);
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
            for (Container c : itemMap.values()) {
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
            for (Container c : itemMap.values()) {
                if (c.getDockable().node() == node) {
                    retval = true;
                    break;
                }
            }
            return retval;
        }

        protected String getButtonText(Dockable d) {
            String txt = d.nodeController().getTitle();
            if (d.nodeController().getProperties().getProperty("user-title") != null) {
                txt = d.nodeController().getProperties().getProperty("user-title");
            } else if (d.nodeController().getProperties().getProperty("short-title") != null) {
                txt = d.nodeController().getProperties().getProperty("short-title");
            }
            if (txt == null || txt.trim().isEmpty()) {
                txt = "Dockable";
            }
            return txt;
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

        public boolean hasWindow(Dockable d) {
            boolean retval = false;
            if (d.node().getScene() != null && d.node().getScene().getWindow() != null) {
                retval = true;
            }
            return retval;
        }

        @Override
        protected boolean doDock(Point2D mousePos, Node node, Side dockPos) {
            Dockable dockable = DockRegistry.dockable(node);
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                if (node.getScene().getWindow().isShowing()) {
                    //((Region) node).setPrefHeight(node.getScene().getWindow().getHeight());
                }
                ((Stage) node.getScene().getWindow()).close();
            }
            Button itemButton = new Button(getButtonText(dockable));

            itemButton.getStyleClass().add("item-button");
            Group item = new Group(itemButton);
            Container container = new Container(dockable);
            getItemMap().put(item, container);

            int idx = -1;
            ToolBar tb = ((DockSideBar) getDockPane()).getDelegate();
            if (mousePos != null) {
                Node sb = findNode(tb.getItems(), mousePos.getX(), mousePos.getY());
                if (sb != null && (sb instanceof Group)   ) {
                    idx = tb.getItems().indexOf(sb);
                }
            }
            System.err.println("INDEX == " + idx);
            if (idx >= 0 ) {
                System.err.println("INSEX == " + tb.getItems().get(idx));
            }
            itemButton.setRotate(((DockSideBar) getDockPane()).getRotation().getAngle());
            itemButton.setOnAction(a -> {
                a.consume();
                if (dockable.node().getScene().getWindow().isShowing()) {
                    dockable.node().getScene().getWindow().hide();
                    return;
                }
                getItemMap().values().forEach(d -> {
                    d.getDockable().node().getScene().getWindow().hide();
                });
                show(itemButton);
                container.changeSize();
            });
            if ( idx >=0 ) {
                ((DockSideBar) getDockPane()).getDelegate().getItems().add(idx,item);
            } else {
                ((DockSideBar) getDockPane()).getDelegate().getItems().add(item);
            }
            DockNodeController nodeHandler = dockable.nodeController();
            if (nodeHandler.getPaneController() == null || nodeHandler.getPaneController() != this) {
                nodeHandler.setPaneController(this);
            }
            container.getStageBuilder().setSupportedCursors(getSupportedCursors());
            Stage stage = container.getStageBuilder().createStage(dockable);
            stage.setAlwaysOnTop(true);
            stage.setOnShowing(e -> {
                if (getDockPane().getScene() != null && getDockPane().getScene().getWindow() != null) {
                    if (stage.getOwner() == null) {
                        stage.initOwner(getDockPane().getScene().getWindow());
                    }
                }
            });

            if (getDockPane().getScene() != null && getDockPane().getScene().getWindow() != null && getDockPane().getScene().getWindow().isShowing()) {
                container.adjustScreenPos();
            }
            container.setDocked(true);
            return true;
        }

        public Node findNode(List<Node> list, double x, double y) {
            Node retval = null;
            for (Node node : list) {
                if (!(node instanceof Group)) {
                    continue;
                }
                Region r = (Region) ((Group)node).getChildren().get(0);
                if (DockUtil.contains(r, x, y)) {
                    retval = node;
                    break;
                }
            }
            return retval;
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
            for (Map.Entry<Group, Container> en : itemMap.entrySet()) {
                if (en.getValue().getDockable().node() == dockNode) {
                    r = en.getKey();
                    break;
                }
            }
            if (r != null) {
                itemMap.get(r).removeListeners();
                itemMap.remove(r);
                ((DockSideBar) getDockPane()).getDelegate().getItems().remove(r);
                ((DockSideBar) getDockPane()).getItems().remove(DockRegistry.dockable(dockNode));

            }
        }

        protected ObservableMap<Group, Container> getItemMap() {
            return itemMap;
        }

        public void show(Button btn) {
            Group group = (Group) btn.getParent();

            //container.getStageBuilder().createStage(dockable).setAlwaysOnTop(true);
            Dockable dockable = getItemMap().get(group).getDockable();
            /*            if (!hasWindow(dockable)) {
                getItemMap().get(group).getStageBuilder().createStage(dockable).setAlwaysOnTop(true);
                if (!dockable.node().getScene().getWindow().isShowing()) {
                    if (getDockPane().getScene() != null && getDockPane().getScene().getWindow() != null) {
                        ((Stage) dockable.node().getScene().getWindow()).initOwner(getDockPane().getScene().getWindow());
                    }
                }
            }
             */
            getItemMap().get(group).changeSize();
            ((Stage) dockable.node().getScene().getWindow()).show();
        }

        @Override
        protected NodeIndicatorTransformer createNodeIndicatorTransformer() {
            return new SideBarIndicatorTransformer();
        }

        /*        @Override
        protected PaneIndicatorTransformer createPaneIndicatorTransformer() {
            return null;
        }
         */
        public static class SideBarIndicatorTransformer extends NodeIndicatorTransformer {

            public SideBarIndicatorTransformer() {
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

                //if (getTargetNode() != null) {
                //    return getMousePos(); // source data
                //return null;
                //}
                Point2D retval;// = null;// = super.mousePos();

                SideIndicator paneIndicator = ((DragPopup)getTargetPaneController().getDragPopup()).getPaneIndicator();
                //Pane topBtns;//= null;
                //Button topPaneButton = null;
                //if (paneIndicator != null) {
                Pane topBtns = paneIndicator.getTopButtons();
                Button topPaneButton = (Button) topBtns.getChildren().get(0);
                //}
                if (getIndicator().getIndicatorPane().getChildren().contains(getBottomButtons())
                        || getIndicator().getIndicatorPane().getChildren().contains(getLeftButtons())
                        || getIndicator().getIndicatorPane().getChildren().contains(getRightButtons())) {
                    getIndicator().getIndicatorPane().getChildren().clear();
                    //if (paneIndicator != null) {
                    paneIndicator.getIndicatorPane().getChildren().remove(paneIndicator.getTopButtons());
                    paneIndicator.getIndicatorPane().getChildren().remove(paneIndicator.getRightButtons());
                    paneIndicator.getIndicatorPane().getChildren().remove(paneIndicator.getBottomButtons());
                    paneIndicator.getIndicatorPane().getChildren().remove(paneIndicator.getLeftButtons());
                    topBtns = paneIndicator.getTopButtons();
                    ((GridPane) getIndicator().getIndicatorPane()).add(topBtns, 0, 1);
                    //}
                }

                //DockSideBar sideBar = (DockSideBar) getTargetPaneController().getDockPane();
//                double mouseX = getIndicatorPosition().getX();
//                double mouseY = getIndicatorPosition().getY();
                double x = getMousePos().getX();
                double y = getMousePos().getY();

                //retval = centerPosOf(sideBar, topPaneButton);
                retval = new Point2D(x - 15, y - 3);
                topPaneButton.pseudoClassStateChanged(TABOVER_PSEUDO_CLASS, true);

                return retval;
            }

            private Point2D centerPosOf(DockSideBar sideBar, Region r) {
                double x = sideBar.localToScreen(sideBar.getBoundsInLocal()).getMinX();
                double y = sideBar.localToScreen(sideBar.getBoundsInLocal()).getMinY();
                return new Point2D(x + (sideBar.getWidth() - r.getWidth()) / 2, y + (sideBar.getHeight() - r.getHeight()) / 2);
            }

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
            stageBuilder = new StageBuilder(dockable.nodeController());
        }

        public void setDocked(boolean docked) {
            DockSideBar sb = getSideBar();
            if (sb.isHideOnExit()) {
                addMouseExitListener();
            }
            hideOnExitProperty.bind(sb.hideOnExitProperty());
            floatingProperty.bind(dockable.nodeController().floatingProperty());
            floatingProperty.addListener((v, oldValue, newValue) -> {
                if (newValue) {
                    removeMouseExitListener();
                }
            });
            hideOnExitProperty.addListener((v, oldValue, newValue) -> {
                if (newValue) {
                    addMouseExitListener();
                } else {
                    removeMouseExitListener();
                }
            });
        }

        public DockSideBar getSideBar() {
            return (DockSideBar) dockable.nodeController().getPaneController().getDockPane();
        }

        public void addMouseExitListener() {
            mouseExitEventListener = this::mouseExited;
            dockable.node().getScene().getWindow().addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
        }

        public void removeMouseExitListener() {
            if (mouseExitEventListener == null) {
                return;
            }
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
            if (!ev.isPrimaryButtonDown() && !dockable.nodeController().isFloating()) {
                dockable.node().getScene().getWindow().hide();
            }
            ev.consume();
        }

        public void adjustScreenPos() {
            SidePaneController handler = (SidePaneController) dockable.nodeController().getPaneController();
            Stage ownerStage = (Stage) ((DockSideBar) handler.getDockPane()).getScene().getWindow();

            /*            ownerStage.xProperty().removeListener(this);
            ownerStage.yProperty().removeListener(this);
            ownerStage.widthProperty().removeListener(this);
            ownerStage.heightProperty().removeListener(this);            
             */
            ownerStage.xProperty().addListener(this);
            ownerStage.yProperty().addListener(this);
            ownerStage.widthProperty().addListener(this);
            ownerStage.heightProperty().addListener(this);

            changeSize();

        }

        public void removeListeners() {
            SidePaneController handler = (SidePaneController) dockable.nodeController().getPaneController();
            Stage ownerStage = (Stage) ((DockSideBar) handler.getDockPane()).getScene().getWindow();
            ownerStage.xProperty().removeListener(this);
            ownerStage.yProperty().removeListener(this);
            ownerStage.widthProperty().removeListener(this);
            ownerStage.heightProperty().removeListener(this);
            stageBuilder.removeListeners(dockable);
        }

        protected void changeSide() {
            SidePaneController handler = (SidePaneController) dockable.nodeController().getPaneController();
            stageBuilder.setSupportedCursors(handler.getSupportedCursors());
        }

        protected void changeSize() {
            /*            if (dockable.node().getScene() == null || dockable.node().getScene().getWindow() == null) {
                return;
            }
             */
            System.err.println("dockable.node().getScene()=" + dockable.node().getScene());
            Stage stage = (Stage) dockable.node().getScene().getWindow();
            DockSideBar sb = (DockSideBar) dockable.nodeController().getPaneController().getDockPane();
            if (!stage.isShowing()) {
                return;
            }
            Point2D pos = sb.localToScreen(0, 0);
            switch (sb.getSide()) {
                case TOP:
                    stage.setX(pos.getX());
                    stage.setY(pos.getY() + sb.getHeight());
                    stage.setWidth(sb.getWidth());
//                    stage.setHeight(dockable.node().getHeight());

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

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            changeSize();
        }

        public StageBuilder getStageBuilder() {
            return stageBuilder;
        }
    }

    public static class CustomToolBar extends ToolBar {

        public CustomToolBar() {

        }

        public CustomToolBar(Node... items) {
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

}//class
