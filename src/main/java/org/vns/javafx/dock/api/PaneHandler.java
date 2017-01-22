package org.vns.javafx.dock.api;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.WindowEvent;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class PaneHandler {

    //private final ObjectProperty<Region> dockPaneProperty = new SimpleObjectProperty<>();
    private Region dockPane;
    private String title;
    private PaneIndicatorTransformer paneTransformer;
    private NodeIndicatorTransformer nodeTransformer;

    private PaneSideIndicator paneIndicator;
    private NodeSideIndicator nodeIndicator;

    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();

    private boolean usedAsDockTarget = true;

    private DragPopup dragPopup;

    private final ObservableMap<Node, Dockable> notDockableItemsProperty = FXCollections.observableHashMap();

    private SidePointerModifier sidePointerModifier;

    protected PaneHandler(Region dockPane) {
        this.dockPane = dockPane;
        System.err.println("PaneHandler: dockPane=" + dockPane);
        init();
    }

    protected PaneHandler(Dockable dockable) {
        //dockPaneProperty.initIndicatorPane(dockPane);
        init();
    }

    private void init() {
        setSidePointerModifier(this::modifyNodeSidePointer);
        //dragPopup = new DragPopup(new PaneSideIndicator(), new NodeSideIndicator());

        inititialize();
    }

    public String getTitle() {
        if (title != null) {
            return title;
        }
        title = getDockPane().getId();
        if (title == null) {
            title = getDockPane().getClass().getName();
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected ObservableMap<Node, Dockable> notDockableItemsProperty() {
        return notDockableItemsProperty;
    }

    protected void setDragPopup(DragPopup dragPopup) {
        System.err.println("PaneHandler: setDragpopup=" + dragPopup.getPaneHandler().getDockPane());
        this.dragPopup = dragPopup;
    }

    public DragPopup getDragPopup() {
        if (dragPopup != null) {
            return dragPopup;
        }
        if (getDockPane() != null) {
            dragPopup = new DragPopup(this);
        }
        return dragPopup;
    }

    protected void initSplitDelegate() {
    }

    protected void inititialize() {
        DockRegistry.start();
        initSplitDelegate();
        initListeners();

    }

    protected void initListeners() {
        if (getDockPane() == null) {
            return;
        }
        getDockPane().sceneProperty().addListener((Observable observable) -> {
            if (getDockPane().getScene() != null) {
                focusedDockNode.bind(getDockPane().getScene().focusOwnerProperty());
            }
        });

        focusedDockNode.addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            Node newNode = DockUtil.getImmediateParent(newValue, (p) -> {
                return DockRegistry.isDockable(p);
            });
            if (newNode != null) {
                Dockable n = DockRegistry.dockable(newNode).nodeHandler().getImmediateParent(newValue);
                if (n != null && n != newNode) {
                    newNode = (Node) n;
                }
                DockRegistry.dockable(newNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
            Node oldNode = DockUtil.getImmediateParent(oldValue, (p) -> {
                return DockRegistry.isDockable(p);
            });

            if (oldNode != null && oldNode != newNode) {
                DockRegistry.dockable(oldNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(false);
            } else if (oldNode != null && !DockRegistry.dockable(oldNode).nodeHandler().titleBarProperty().isActiveChoosedPseudoClass()) {
                DockRegistry.dockable(oldNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        });

    }

    protected ObservableMap<Node, Dockable> getNotDockableItems() {
        return this.notDockableItemsProperty;
    }

    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

    public Point2D modifyNodeSidePointer(DragPopup popup, Dockable target, double mouseX, double mouseY) {
        return null;
    }

    protected void setPaneTransformer(PaneIndicatorTransformer paneTransformer) {
        this.paneTransformer = paneTransformer;
    }

    protected void setNodeTransformer(NodeIndicatorTransformer nodeTransformer) {
        this.nodeTransformer = nodeTransformer;
    }

    public NodeIndicatorTransformer getNodeTransformer() {
        if (nodeTransformer == null) {
            nodeTransformer = createNodeIndicatorTransformer();
        }
        return nodeTransformer;
    }

    protected NodeIndicatorTransformer createNodeIndicatorTransformer() {
        return new NodeIndicatorTransformer();
    }

    public PaneIndicatorTransformer getPaneTransformer() {
        if (paneTransformer == null) {
            paneTransformer = createPaneIndicatorTransformer();
        }
        return paneTransformer;
    }

    protected PaneIndicatorTransformer createPaneIndicatorTransformer() {
        return new PaneIndicatorTransformer();
    }

    ///
    public NodeSideIndicator getNodeIndicator() {
        if (nodeIndicator == null) {
            nodeIndicator = createNodeIndicator();
        }
        return nodeIndicator;
    }

    protected NodeSideIndicator createNodeIndicator() {
        return new NodeSideIndicator(this);
    }

    public PaneSideIndicator getPaneIndicator() {
        if (paneIndicator == null) {
            paneIndicator = createPaneIndicator();
        }
        return paneIndicator;
    }

    protected PaneSideIndicator createPaneIndicator() {
        return new PaneSideIndicator(this);
    }

    public SidePointerModifier getSidePointerModifier() {
        return sidePointerModifier;
    }

    public void setSidePointerModifier(SidePointerModifier sidePointerModifier) {
        this.sidePointerModifier = sidePointerModifier;
    }

    public Region getDockPane() {
        return this.dockPane;
    }

    public void setDockPane(Region dockPane) {
        this.dockPane = dockPane;
        System.err.println("PaneHandler setDockPane");
    }

    protected boolean isDocked(Node node) {
        return false;
    }

    protected void changeDockedState(Dockable dockable, boolean docked) {
        dockable.nodeHandler().setDocked(docked);
    }

    public void undock(Node node) {
        if (!isDocked(node)) {
            return;
        }
        if (DockRegistry.isDockable(node)) {
            DockRegistry.dockable(node).nodeHandler().setDocked(false);
        }
    }

    protected Dockable dock(Point2D mousePos, Node node, Side nodeDockPos, Side paneDockPos, Node target) {
        Dockable retval = null;
        if (paneDockPos != null) {
            dock(mousePos, DockRegistry.dockable(node), paneDockPos);
        } else if (nodeDockPos != null) {
            Dockable t = target == null ? null : DockRegistry.dockable(target);
            dock(mousePos, DockRegistry.dockable(node), nodeDockPos, t);
        }
        return retval;
    }

    protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos, Dockable target) {
        if (isDocked(dockable.node())) {
            return dockable;
        }
        if (!(dockable instanceof Node) && !DockRegistry.getDockables().containsKey(dockable.node())) {
            DockRegistry.getDockables().put(dockable.node(), dockable);
        }
        dockable.nodeHandler().setFloating(false);

        doDock(mousePos, dockable.node(), dockPos, target);
        changeDockedState(dockable, true);
        return dockable;
    }

    protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos) {
        if (isDocked(dockable.node())) {
            return dockable;
        }
        dockable.nodeHandler().setFloating(false);
        dockable = convert(dockable, DockConverter.BEFORE_DOCK);

        doDock(mousePos, dockable.node(), dockPos);
        return dockable;
    }

    protected Dockable convert(Dockable source, int when) {
        Dockable retval = source;
        if (source instanceof DockConverter) {
            retval = ((DockConverter) source).convert(source, when);
        }
        return retval;
    }

    public Dockable dock(Dockable dockable, Side dockPos) {
        return dock(null, dockable, dockPos);
    }

    public Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
        return dock(null, dockable, dockPos, target);
    }

    protected void doDock(Point2D mousePos, Node node, Side dockPos) {
    }

    protected void doDock(Point2D mousePos, Node node, Side dockPos, Dockable targetDockable) {
    }

    public FloatStageBuilder getStageBuilder(Dockable dockable) {
        return new FloatStageBuilder(dockable.nodeHandler());
    }

    @FunctionalInterface
    public interface SidePointerModifier {

        /**
         *
         * @param mouseX
         * @param mouseY
         * @param target
         * @return null than a default position of node indicator is used or a
         * new position of node indicator
         */
        Point2D modify(DragPopup popup, Dockable target, double mouseX, double mouseY);
    }

    public interface DockConverter {

        public static final int BEFORE_DOCK = 0;
        public static final int AFTER_DOCK = 0;

        Dockable convert(Dockable source, int when);
    }//interface DockConverter

    public void remove(Node dockNode) {
    }

    public abstract static class SideIndicator {

        private final PaneHandler paneHandler;

        private Rectangle dockPlace;
        private Rectangle dockPlace2;

        private Pane topButtons;
        private Pane bottomButtons;
        private Pane leftButtons;
        private Pane rightButtons;
        private Pane centerButtons;

        private Button selectedButton;

        private Pane indicatorPane;

        //private final Map<Node, PaneHandler> sideButtonMap = new HashMap<>();
        private SideIndicatorTransformer transformer;

        protected SideIndicator(PaneHandler paneHandler) {
            this.paneHandler = paneHandler;
            init();
        }

        private void init() {
            indicatorPane = createIndicatorPane();
            dockPlace = new Rectangle();
            dockPlace.getStyleClass().add("dock-place");
            indicatorPane.getChildren().add(dockPlace);
            dockPlace2 = new Rectangle();
            dockPlace2.getStyleClass().add("dock-place2");
            indicatorPane.getChildren().add(0,dockPlace2);

        }

        public Rectangle getDockPlace() {
            return dockPlace;
        }
        public Rectangle getDockPlace2() {
            return dockPlace2;
        }

        public PaneHandler getPaneHandler() {
            return paneHandler;
        }

        protected abstract Pane createIndicatorPane();

        public Pane getIndicatorPane() {
            return indicatorPane;
        }

        public Button getSelectedButton() {
            return selectedButton;
        }

        public Pane getTopButtons() {
            Pane retval;
            if (transformer == null) {
                retval = topButtons;
            } else {
                retval = transformer.getTopButtons();
            }
            return retval;
        }

        public Pane getBottomButtons() {
            Pane retval;
            if (transformer == null) {
                retval = bottomButtons;
            } else {
                retval = transformer.getBottomButtons();
            }
            return retval;

        }

        public Pane getLeftButtons() {
            Pane retval;
            if (transformer == null) {
                retval = leftButtons;
            } else {
                retval = transformer.getLeftButtons();
            }
            return retval;

        }

        public Pane getRightButtons() {
            Pane retval;
            if (transformer == null) {
                retval = rightButtons;
            } else {
                retval = transformer.getRightButtons();
            }
            return retval;
        }

        public Pane getCenterButtons() {
            Pane retval;
            if (transformer == null) {
                retval = centerButtons;
            } else {
                retval = transformer.getCenterButtons();
            }
            return retval;
        }

        protected abstract String getStylePrefix();

        protected void restoreButtonsStyle() {
            restoreButtonsStyle(topButtons, getStylePrefix() + "-top-button");
            restoreButtonsStyle(rightButtons, getStylePrefix() + "-right-button");
            restoreButtonsStyle(bottomButtons, getStylePrefix() + "-bottom-button");
            restoreButtonsStyle(leftButtons, getStylePrefix() + "-left-button");
            restoreButtonsStyle(centerButtons, getStylePrefix() + "-center-button");
        }

        protected void restoreButtonsStyle(Pane pane, String style) {
            if (pane == null) {
                return;
            }
            pane.getChildren().forEach(node -> {
                node.getStyleClass().clear();
                node.getStyleClass().add("button");
                node.getStyleClass().add(style);
            });
        }

        protected String getCenterButonPaneStyle() {
            return getStylePrefix() + "-" + "center-button-pane";
        }

        protected String getButonPaneStyle(Side side) {
            String style = null;
            String prefix = getStylePrefix();
            switch (side) {
                case TOP:
                    style = prefix + "-" + "top-button-pane";
                    break;
                case RIGHT:
                    style = prefix + "-" + "right-button-pane";
                    break;
                case BOTTOM:
                    style = prefix + "-" + "bottom-button-pane";
                    break;
                case LEFT:
                    style = prefix + "-" + "laft-button-pane";
                    break;
            }
            return style;
        }

        protected String getCenterButtonStyle() {
            return getStylePrefix() + "-" + "center-button";
        }

        protected String getButtonStyle(Side side) {
            String style = null;
            String prefix = getStylePrefix();
            switch (side) {
                case TOP:
                    style = prefix + "-" + "top-button";
                    break;
                case RIGHT:
                    style = prefix + "-" + "right-button";
                    break;
                case BOTTOM:
                    style = prefix + "-" + "bottom-button";
                    break;
                case LEFT:
                    style = prefix + "-" + "left-button";
                    break;
            }
            return style;
        }

        protected Pane createCenterButtons() {
            Button btn = new Button();
            centerButtons = new StackPane(btn);
            centerButtons.getStyleClass().add(getCenterButonPaneStyle());
            btn.getStyleClass().add(getCenterButtonStyle());
            return centerButtons;
        }

        protected Pane createSideButtons(Side side) {

            Button btn = new Button();
            StackPane retval = new StackPane();
            retval.getChildren().add(btn);
            retval.setAlignment(Pos.CENTER);
            retval.getStyleClass().add(getButonPaneStyle(side));
            btn.getStyleClass().add(getButtonStyle(side));
            switch (side) {
                case TOP:
                    topButtons = retval;
                    break;
                case RIGHT:
                    rightButtons = retval;
                    break;
                case BOTTOM:
                    bottomButtons = retval;
                    break;
                case LEFT:
                    leftButtons = retval;
                    break;
            }

            return retval;
        }
        public void hideDockPlace() {
            getDockPlace().setVisible(false);
            getDockPlace2().setVisible(false);
        }

        public void showDockPlace(Button selButton, Side side) {
            setSelectedButton(selButton);
            transform();
            transformer.showDockPlace(side);
        }
        
        public void showDockPlace(Region targetNode, double screenX, double screenY) {
            transform(targetNode, screenX, screenY);
            transformer.showDockPlace();
        }

        public Point2D mousePosBy(Region targetNode, double screenX, double screenY) {
            transform(targetNode, screenX, screenY);
            return transformer.mousePos();
        }

        public Point2D mousePosBy(double screenX, double screenY) {
            transform(screenX, screenY);
            return transformer.mousePosByPaneHandler();
        }

        public void setSelectedButton(Button selectedButton) {
            this.selectedButton = selectedButton;
        }

        protected abstract SideIndicatorTransformer getTransformer();

        protected void transform() {
            transform(null, 0, 0);
        }

        protected void transform(Region targetNode) {
            transform(targetNode, 0, 0);
        }

        protected void transform(double screenX, double screenY) {
            transform(null, screenX, screenY);
        }

        protected void transform(Region targetNode, double screenX, double screenY) {
            transformer = getTransformer();
            transformer.initialize(this, new Point2D(screenX, screenY), topButtons, bottomButtons, leftButtons, rightButtons);

            transformer.setTargetNode(targetNode);
            transformer.transform();
        }

        public void indicatorBeforeShow(Region node) {
            transform(node);
            transformer.indicatorBeforeShow(node);
        }

        public void windowBeforeShow(Region node) {
            transform(node);
            transformer.windowBeforeShow(node);
        }

        public void windowAfterShow(Region node) {
            transform(node);
            transformer.windowAfterShow(node);
        }

        public void windowOnShown(WindowEvent ev, Region node) {
            transform(node);
            transformer.windowOnShown(ev, node);
        }

        public void windowOnShowing(WindowEvent ev, Region node) {
            transform(node);
            transformer.windowOnShowing(ev, node);
        }

        public void windowOnHidden(WindowEvent ev, Region node) {
            transform(node);
            transformer.windowOnHidden(ev, node);
        }

        public void windowOnHiding(WindowEvent ev, Region node) {
            transform(node);
            transformer.windowOnHiding(ev, node);
        }

        public void beforeShow(Region node) {
            transform(node);
            transformer.indicatorBeforeShow(node);
        }

        public void afterShow(Region node) {
            transform(node);
            transformer.indicatorAfterShow(node);
        }

        public void onShown(WindowEvent ev, Region node) {
            transform(node);
            transformer.indicatorOnShown(ev, node);
        }

        public void onShowing(WindowEvent ev, Region node) {
            transform(node);
            transformer.indicatorOnShowing(ev, node);
        }

        public void onHidden(WindowEvent ev, Region node) {
            transform(node);
            transformer.indicatorOnHidden(ev, node);
        }

        public void onHiding(WindowEvent ev, Region node) {
            transform(node);
            transformer.indicatorOnHiding(ev, node);
        }


        /*        public void sideIndicatorShowing(PaneHandler paneHandler, Region node) {
            transform(node);
            transformer.sideIndicatorShowing(node);
        }
        
        public void sideIndicatorShown(PaneHandler paneHandler, Region node) {
            transform(node);
            transformer.sideIndicatorShown(node);
        }

        public void sideIndicatorHidden(PaneHandler paneHandler, Region node) {
            transform(node);
            transformer.sideIndicatorHidden(node);
        }
         */
        public Point2D getMousePos() {
            return transformer.mousePos();
        }

    }//class SideIndicator

    public static class NodeSideIndicator extends SideIndicator {

        public NodeSideIndicator(PaneHandler paneHandler) {
            super(paneHandler);
        }

        @Override
        protected Pane createIndicatorPane() {
            GridPane indicatorPane = new GridPane();
            indicatorPane.getStyleClass().add(getStylePrefix());
            indicatorPane.setMouseTransparent(true);

            Pane buttons = createSideButtons(Side.TOP);
            indicatorPane.add(buttons, 1, 0);
            buttons = createSideButtons(Side.BOTTOM);
            indicatorPane.add(buttons, 1, 2);
            buttons = createSideButtons(Side.LEFT);
            indicatorPane.add(buttons, 0, 1);
            buttons = createSideButtons(Side.RIGHT);
            indicatorPane.add(buttons, 2, 1);

            buttons = createCenterButtons();
            indicatorPane.add(buttons, 1, 1);

            return indicatorPane;
        }

        @Override
        protected String getStylePrefix() {
            return "drag-node-indicator";
        }

        @Override
        protected NodeIndicatorTransformer getTransformer() {
            return getPaneHandler().getNodeTransformer();
        }
    }//class SideIndicator

    public static class PaneSideIndicator extends SideIndicator {

        public PaneSideIndicator(PaneHandler paneHandler) {
            super(paneHandler);
        }

        @Override
        protected Pane createIndicatorPane() {
            BorderPane indicatorPane = new BorderPane();
            indicatorPane.getStyleClass().add(getStylePrefix());
            indicatorPane.setMouseTransparent(true);
            Pane buttons = createSideButtons(Side.TOP);
            indicatorPane.setTop(buttons);
            buttons = createSideButtons(Side.RIGHT);
            indicatorPane.setRight(buttons);
            buttons = createSideButtons(Side.BOTTOM);
            indicatorPane.setBottom(buttons);
            buttons = createSideButtons(Side.LEFT);
            indicatorPane.setLeft(buttons);

            buttons = createCenterButtons();
            indicatorPane.setCenter(buttons);

            return indicatorPane;
        }

        @Override
        protected String getStylePrefix() {
            return "drag-pane-indicator";
        }

        @Override
        protected PaneIndicatorTransformer getTransformer() {
            return getPaneHandler().getPaneTransformer();
        }
    }//class SideIndicator

    public static class PaneIndicatorTransformer extends SideIndicatorTransformer {

        public PaneIndicatorTransformer() {
            super();
        }

        /*        @Override
        public void sideIndicatorShown(Region node) {}

        @Override
        public void sideIndicatorHidden(Region node) {}
         */
        @Override
        public Point2D mousePos() {
            return getMousePos();
        }
        @Override
        protected void showDockPlace2(Region pane, Side side) {
            Rectangle dockPlace = getIndicator().getDockPlace2();
            dockPlace.setWidth(pane.getWidth()-1);
            dockPlace.setHeight(pane.getHeight()-1);
            Point2D p = dockPlace.localToParent(0, 0);
            dockPlace.setX(p.getX()+1);
            dockPlace.setY(p.getY()+1);
            dockPlace.setVisible(true);
        }
        
    }//Transformer

    public static class NodeIndicatorTransformer extends SideIndicatorTransformer {

        public NodeIndicatorTransformer() {
        }

        /*        @Override
        public void sideIndicatorShown(Region node) {
            if (getTargetPaneHandler() != null && node != null) {
                resizeButtonPanes();
            }
        }

        @Override
        public void sideIndicatorHidden(Region node) {
            if (getTargetPaneHandler() == null || node == null) {
                resizeButtonPanes();
            }

        }
         */
        @Override
        public void indicatorOnShown(WindowEvent ev, Region node) {
            if (getTargetPaneHandler() != null && node != null) {
                resizeButtonPanes();
            }
        }

        @Override
        public void indicatorOnHidden(WindowEvent ev, Region node) {
            if (getTargetPaneHandler() == null || node == null) {
                resizeButtonPanes();
            }

        }

        @Override
        protected void resizeButtonPanes() {
            if (getTargetPaneHandler() != null && getTargetNode() != null && intersects()) {
                if (!getIndicator().getIndicatorPane().getTransforms().contains(getSmallbuttonsScale())) {
                    getIndicator().getIndicatorPane().getTransforms().add(getSmallbuttonsScale());

                    double w = getIndicator().getIndicatorPane().getWidth() / 2;
                    double h = getIndicator().getIndicatorPane().getHeight() / 2;
                    Point2D p = getIndicator().getIndicatorPane().localToParent(w, h);
                    getSmallbuttonsScale().setPivotX(w);
                    getSmallbuttonsScale().setPivotY(h);
                }
            } else {
                getIndicator().getIndicatorPane().getTransforms().remove(getSmallbuttonsScale());
            }

        }

        protected boolean intersects() {
            boolean retval = false;
            Pane thisPane = getIndicator().getIndicatorPane();

            if (getTargetPaneHandler() != null) {

                Node node = getTargetPaneHandler().getDragPopup().getPaneIndicator().getTopButtons();

                if (intersects(thisPane, node)) {
                    return true;
                }
                node = getTargetPaneHandler().getDragPopup().getPaneIndicator().getRightButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = getTargetPaneHandler().getDragPopup().getPaneIndicator().getBottomButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = getTargetPaneHandler().getDragPopup().getPaneIndicator().getLeftButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Point2D mousePos() {
            Point2D newPos = null;
            if (getTargetNode() != null && getIndicator().getIndicatorPane() != null) {
                newPos = getTargetNode().localToScreen((getTargetNode().getWidth() - getIndicator().getIndicatorPane().getWidth()) / 2, (getTargetNode().getHeight() - getIndicator().getIndicatorPane().getHeight()) / 2);
            }
            return newPos;
        }

    }//Transformer

    public abstract static class SideIndicatorTransformer {

        //private PaneHandler targetPaneHandler;
        private Region targetNode;
        //private String style;
        private Point2D mousePos;
        private Pane topButtons;
        private Pane rightButtons;
        private Pane bottomButtons;
        private Pane leftButtons;
        private Pane centerButtons;
        private SideIndicator indicator;

        private final Scale smallbuttonsScale;

        public SideIndicatorTransformer() {
            //this.targetPaneHandler = targetPaneHandler;
            this.smallbuttonsScale = new Scale(0.5, 0.5);
        }

        /**
         *
         * @param indicator
         * @param mousePos may be null for instance when default transformer
         * @param topButtons
         * @param bottomButtons
         * @param leftButtons
         * @param rightButtons
         */
        public void initialize(SideIndicator indicator, Point2D mousePos, Pane topButtons, Pane bottomButtons, Pane leftButtons, Pane rightButtons) {
            this.indicator = indicator;

            this.mousePos = mousePos;
            this.topButtons = topButtons;

            this.bottomButtons = bottomButtons;
            this.leftButtons = leftButtons;
            this.rightButtons = rightButtons;
        }

        public PaneHandler getTargetPaneHandler() {
            return getIndicator().getPaneHandler();
        }

        public Region getTargetNode() {
            return targetNode;
        }

        public SideIndicator getIndicator() {
            return indicator;
        }

        public Scale getSmallbuttonsScale() {
            return smallbuttonsScale;
        }

        public void transform() {
        }
//        public void beginUpdateIndicator(Region node){}

        public void indicatorBeforeShow(Region node) {
        }

        public void indicatorAfterShow(Region node) {
        }

        public void indicatorOnShowing(WindowEvent ev, Region node) {
        }

        public void indicatorOnShown(WindowEvent ev, Region node) {
        }

        public void indicatorOnHiding(WindowEvent ev, Region node) {
        }

        public void indicatorOnHidden(WindowEvent ev, Region node) {
        }

        public void windowBeforeShow(Region node) {
        }

        public void windowAfterShow(Region node) {
        }

        public void windowOnShowing(WindowEvent ev, Region node) {
        }

        public void windowOnShown(WindowEvent ev, Region node) {
        }

        public void windowOnHiding(WindowEvent ev, Region node) {
        }

        public void windowOnHidden(WindowEvent ev, Region node) {
        }

//        public void endUpdateIndicator(Region node){}
        /*        public void sideIndicatorShowing(Region node) {}

        public void sideIndicatorShown(Region node) {}
        
        public void sideIndicatorHidden(Region node) {}
         */
        public void targetNodeChanged(Region node) {
        }

        protected void resizeButtonPanes() {
        }

        public Boolean intersects(Node node1, Node node2) {
            if (node1 == null || node2 == null) {
                return false;
            }
            Bounds b1 = node1.localToScreen(node1.getBoundsInLocal());
            Bounds b2 = node2.localToScreen(node2.getBoundsInLocal());
            return b1.intersects(b2);

        }

        protected void changeButtonPaneStyle(Pane pane, String style) {
            pane.getStyleClass().clear();
            pane.getStyleClass().add(style);
        }

        protected Point2D getMousePos() {
            return mousePos;
        }

        public Point2D mousePos() {
            Point2D newPos = null;
            if (targetNode != null && indicator.getIndicatorPane() != null) {
                newPos = targetNode.localToScreen((targetNode.getWidth() - indicator.getIndicatorPane().getWidth()) / 2, (targetNode.getHeight() - indicator.getIndicatorPane().getHeight()) / 2);
            }
            return newPos;
        }
        
        public void showDockPlace() {
        }
        protected void showDockPlace2(Region forNode, Side side) {
            
        }
        public void showDockPlace(Side side) {
            Region pane = getTargetPaneHandler().getDockPane();
            Button selected = getIndicator().getSelectedButton();
            if (selected != null && selected.getUserData() != null) {
                pane = ((PaneHandler) selected.getUserData()).getDockPane();
            }
            Rectangle dockPlace = getIndicator().getDockPlace();
            showDockPlace2(pane, side);
            
            switch (side) {
                case TOP:
                    dockPlace.setWidth(pane.getWidth());
                    dockPlace.setHeight(pane.getHeight() / 2);
                    Point2D p = dockPlace.localToParent(0, 0);
                    dockPlace.setX(p.getX());
                    dockPlace.setY(p.getY());
                    break;
                case BOTTOM:
                    dockPlace.setWidth(pane.getWidth());
                    dockPlace.setHeight(pane.getHeight() / 2);
                    p = dockPlace.localToParent(0, pane.getHeight() - dockPlace.getHeight());
                    dockPlace.setX(p.getX());
                    dockPlace.setY(p.getY());
                    break;
                case LEFT:
                    dockPlace.setWidth(pane.getWidth() / 2);
                    dockPlace.setHeight(pane.getHeight());
                    p = dockPlace.localToParent(0, 0);
                    dockPlace.setX(p.getX());
                    dockPlace.setY(p.getY());
                    break;
                case RIGHT:
                    dockPlace.setWidth(pane.getWidth() / 2);
                    dockPlace.setHeight(pane.getHeight());
                    p = dockPlace.localToParent(pane.getWidth() - dockPlace.getWidth(), 0);
                    dockPlace.setX(p.getX());
                    dockPlace.setY(p.getY());
                    break;
            }
            dockPlace.setVisible(true);
            dockPlace.toFront();
        }

        public Point2D mousePosByPaneHandler() {
            return null;
        }

        public void sideButtonSelected() {
        }

        public void setMousePos(Point2D mousePos) {
            this.mousePos = mousePos;
        }

        protected void setTargetNode(Region targetNode) {
            this.targetNode = targetNode;
        }

        public Pane getTopButtons() {
            return topButtons;
        }

        public void setTopButtons(Pane topButtons) {
            this.topButtons = topButtons;
        }

        public Pane getBottomButtons() {
            return bottomButtons;
        }

        public void setBottomButtons(Pane bottomButtons) {
            this.bottomButtons = bottomButtons;
        }

        public Pane getLeftButtons() {
            return leftButtons;
        }

        public void setLeftButtons(Pane leftButtons) {
            this.leftButtons = leftButtons;
        }

        public Pane getRightButtons() {
            return rightButtons;
        }

        public Pane getCenterButtons() {
            return centerButtons;
        }

        public void setRightButtons(Pane rightButtons) {
            this.rightButtons = rightButtons;
        }

    }//Transformer

}//class
