package org.vns.javafx.dock;

import com.sun.javafx.scene.control.skin.LabeledText;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import static org.vns.javafx.dock.DockUtil.contains;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.FloatStageBuilder;
import org.vns.javafx.dock.api.StageBuilder;

/**
 *
 * @author Valery Shyshkin
 */
public class DockSideBar extends StackPane implements DockPaneTarget {

    private SidePaneHandler paneHandler;
    private Side side = Side.RIGHT;
    private Orientation orientation = Orientation.VERTICAL;
    private ToolBar toolBar;

    public DockSideBar() {
        init();

    }

    private void init() {
        paneHandler = new SidePaneHandler(this);
        toolBar = new ToolBar();
        toolBar.setStyle("-fx-background-color: aqua");

        getChildren().add(toolBar);
        toolBar.setOrientation(getOrientation());

        sceneProperty().addListener((v, ov, nv) -> {
            sceneChanged(ov, nv);
        });
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

        if (DockUtil.contains(toolBar, ev.getScreenX(), ev.getScreenY())) {
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
    public DockPaneHandler paneHandler() {
        return this.paneHandler;
    }

    public ObservableMap<Group, Container> getItems() {
        return paneHandler.getItems();
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        if (this.side == side) {
            return;
        }
        this.side = side;
        paneHandler.getItems().keySet().forEach(g -> {
            Button btn = (Button) g.getChildren().get(0);
            paneHandler.rotate(btn);
        });
        paneHandler.getItems().values().forEach(d -> {
            d.changeSize();
            d.changeSide();

        });
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Dockable dock(Dockable dockable, Side dockPos) {
        //!!! inherit DockPaneTarget.super.dock(dockable, dockPos); 
        return dock(dockable);
    }

    public Dockable dock(Dockable dockable) {
        return paneHandler.dock(dockable, null);
    }

    @Override
    public Pane pane() {
        return this;
    }

    public static class SidePaneHandler extends DockPaneHandler {

        private final ObservableMap<Group, Container> items = FXCollections.observableHashMap();

        public SidePaneHandler(DockSideBar dockPane) {
            super(dockPane);
            init();
        }

        private void init() {
            setUsedAsDockTarget(false);
        }

        @Override
        protected void inititialize() {
            DockRegistry.start();
        }

        @Override
        public FloatStageBuilder getStageBuilder(Dockable dockable) {
            return getItem(dockable).getStageBuilder();
            //return new StageBuilder(dockable.nodeHandler());
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
            if (d.nodeHandler().getProperties().getProperty("short-title") != null) {
                txt = d.nodeHandler().getProperties().getProperty("short-title");
            }
            return txt;
        }

        protected Node getButtonIcon(Dockable d) {
            return null;
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
            Group item = new Group(itemButton);
            Container container = new Container(dockable);
            getItems().put(item, container);

            //DockTabPane.TabTitleBar tab = new DockTabPane.TabTitleBar((Dockable) node, dockPane);
            //tab.setId("TabTitleBar:" + node.getId());
            int idx = -1;
            if (mousePos != null) {
                // Node tb = DockUtil.findNode(dockPane.getTabList(), mousePos.getX(), mousePos.getY());
                //if ( tb != null ) {
                //    idx = dockPane.getTabListPane().getChildren().indexOf(tb);
                //}
            }
            if (idx >= 0) {
                //  dockPane.getTabListPane().getChildren().add(idx,tab);
                //  dockPane.getTabListPane().getChildren().add(idx+1,tab.getSeparator());
            } else {
                //dockPane.getTabListPane().getChildren().add(tab);
                //dockPane.getTabListPane().getChildren().add(tab.getSeparator());

            }
            rotate(itemButton);
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

            if (getDockPane().getScene() != null && getDockPane().getScene().getWindow() != null && getDockPane().getScene().getWindow().isShowing()) {
                container.adjustScreenPos();
            }
            nodeHandler.setDocked(true);
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
            ((Stage) dockable.node().getScene().getWindow()).show();
        }

    }//class

    public static class Container implements ChangeListener<Number> {

        private final Dockable dockable;
        private StageBuilder stageBuilder;

        public Container(Dockable dockable) {
            this.dockable = dockable;
            stageBuilder = new StageBuilder(dockable.nodeHandler());
        }

        public Dockable getDockable() {
            return dockable;
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
            DockSideBar dsb = (DockSideBar) dockable.nodeHandler().getPaneHandler().getDockPane();
            ToolBar tb = dsb.toolBar;
            Point2D pos = tb.localToScreen(0, 0);
            switch (dsb.getSide()) {
                case TOP:
                    break;
                case BOTTOM:
                    break;
                case RIGHT:
                    stage.setY(pos.getY());
                    stage.setX(pos.getX() - ((Region) dockable.node()).getWidth());
                    stage.setHeight(tb.getHeight());
                    break;
                case LEFT:
                    stage.setY(pos.getY());
                    stage.setX(pos.getX() + tb.getWidth());
                    stage.setHeight(tb.getHeight());
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
}//class
