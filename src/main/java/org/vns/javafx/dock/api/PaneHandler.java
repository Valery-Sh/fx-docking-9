package org.vns.javafx.dock.api;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.api.PaneHandler.SideIndicator.NODE_POINTER;
import static org.vns.javafx.dock.api.PaneHandler.SideIndicator.NODE_STYLE;
import static org.vns.javafx.dock.api.PaneHandler.SideIndicator.PANE_POINTER;
import static org.vns.javafx.dock.api.PaneHandler.SideIndicator.PANE_STYLE;

/**
 *
 * @author Valery
 */
public class PaneHandler {

    //private final ObjectProperty<Region> dockPaneProperty = new SimpleObjectProperty<>();
    private Region dockPane;

    private SideIndicatorTransformer paneTransformer;
    private SideIndicatorTransformer nodeTransformer;

    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();

    private boolean usedAsDockTarget = true;

    private DragPopup dragPopup;

    private final ObservableMap<Node, Dockable> notDockableItemsProperty = FXCollections.observableHashMap();

    private SidePointerModifier sidePointerModifier;

    protected PaneHandler(Region dockPane) {
        this.dockPane = dockPane;
        init();
    }

    protected PaneHandler(Dockable dockable) {
        //dockPaneProperty.set(dockPane);
        init();
    }

    private void init() {
        setSidePointerModifier(this::modifyNodeSidePointer);
        dragPopup = new DragPopup(new SideIndicator(SideIndicator.PANE_POINTER), new SideIndicator(SideIndicator.NODE_POINTER));
        inititialize();
    }

    protected ObservableMap<Node, Dockable> notDockableItemsProperty() {
        return notDockableItemsProperty;
    }

    public DragPopup getDragPopup() {
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

    public SideIndicatorTransformer getPaneTransformer() {
        if (paneTransformer == null) {
            paneTransformer = new SideIndicatorTransformer(SideIndicator.PANE_POINTER);
        }
        return paneTransformer;
    }

    public SideIndicatorTransformer getNodeTransformer() {
        if (nodeTransformer == null) {
            nodeTransformer = new SideIndicatorTransformer(SideIndicator.NODE_POINTER);
        }
        return nodeTransformer;
    }

    protected void createSideIndicatorTransformers() {
        //nodeTransformer = new SideIndicatorTransformer(indicator, style, Point2D.ZERO, topButtons, bottomButtons, leftButtons, rightButtons)
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

    public static class SideIndicator {

        public static int NODE_POINTER = 0;
        public static int PANE_POINTER = 2;
        public static final String NODE_STYLE = "target-node";
        public static final String PANE_STYLE = "target-pane";
        public static final String TOP_INDICATOR_STYLE = "drag-top-indicator";
        public static final String RIGHT_INDICATOR_STYLE = "drag-right-indicator";
        public static final String BOTTOM_INDICATOR_STYLE = "drag-bottom-indicator";
        public static final String LEFT_INDICATOR_STYLE = "drag-left-indicator";

        private String style;

        private Pane topButtons;
        private Pane bottomButtons;
        private Pane leftButtons;
        private Pane rightButtons;
        private int indicatorType;

        private Pane indicatorPane;

        private SideIndicatorTransformer transformer;

        public SideIndicator(int indicatorType) {
            this.indicatorType = indicatorType;
            init();
        }

        private void init() {
            getStyle();
            createIndicatorPane();
            //transformer = new SideIndicatorTransformer(indicatorType);
            //transformer.initialize(this, null, topButtons, bottomButtons, leftButtons, rightButtons);
        }

        protected void createIndicatorPane() {
            if (indicatorType == NODE_POINTER) {
                indicatorPane = new GridPane();
                indicatorPane.getStyleClass().add("dock-target-pos");
            } else {
                indicatorPane = new BorderPane();
                indicatorPane.getStyleClass().add("dock-target-pos");
            }
            set(indicatorPane);
            
            indicatorPane.setMouseTransparent(true);
        }

        public Pane getIndicatorPane() {
            return indicatorPane;
        }

        protected String getStyle() {
            if (style == null) {
                style = indicatorType == NODE_POINTER ? NODE_STYLE : PANE_STYLE;
            }
            return style;
        }

        protected void set(Pane targetPane) {
            if (targetPane instanceof BorderPane) {
                topButtons = createSideButtons(Side.TOP);
                ((BorderPane) targetPane).setTop(topButtons);
                rightButtons = createSideButtons(Side.RIGHT);
                ((BorderPane) targetPane).setRight(rightButtons);
                bottomButtons = createSideButtons(Side.BOTTOM);
                ((BorderPane) targetPane).setBottom(bottomButtons);
                leftButtons = createSideButtons(Side.LEFT);
                ((BorderPane) targetPane).setLeft(leftButtons);
            } else if (targetPane instanceof GridPane) {
                topButtons = createSideButtons(Side.TOP);
                ((GridPane) targetPane).add(topButtons, 1, 0);
                System.err.println("GRIDPANE isMouseTrans=" + topButtons.isMouseTransparent());
                bottomButtons = createSideButtons(Side.BOTTOM);
                ((GridPane) targetPane).add(bottomButtons, 1, 2);
                bottomButtons.setId("bottomButtonsPane");
                //bottomButtons.setMouseTransparent(true);                
                leftButtons = createSideButtons(Side.LEFT);
                leftButtons.setId("leftButtonsPane");
                ((GridPane) targetPane).add(leftButtons, 0, 1);
                leftButtons.setStyle("-fx-border-width: 2.0; -fx-border-color: green;");
                //leftButtons.setMouseTransparent(true);
                rightButtons = createSideButtons(Side.RIGHT);
                ((GridPane) targetPane).add(rightButtons, 2, 1);
                rightButtons.setId("rightButtonsPane");               
                //rightButtons.setMouseTransparent(true);                
            }
        }

        public Pane getTopButtons() {
            Pane retval;// = null;
            if ( transformer == null ) {
                retval = topButtons;
            } else {
                retval = transformer.getTopButtons();
            }
            return retval;
        }

        protected void setTopButtons(Pane topButtons) {
            this.topButtons = topButtons;
        }

        public Pane getBottomButtons() {
            Pane retval;// = null;
            if ( transformer == null ) {
                retval = bottomButtons;
            } else {
                retval = transformer.getBottomButtons();
            }
            return retval;

        }

        protected void setBottomButtons(Pane bottomButtons) {
            this.bottomButtons = bottomButtons;
        }

        public Pane getLeftButtons() {
            Pane retval;// = null;
            if ( transformer == null ) {
                retval = leftButtons;
            } else {
                retval = transformer.getLeftButtons();
            }
            return retval;

        }

        protected void setLeftButtons(Pane leftButtons) {
            this.leftButtons = leftButtons;
        }

        public Pane getRightButtons() {
            Pane retval;// = null;
            if ( transformer == null ) {
                retval = rightButtons;
            } else {
                retval = transformer.getRightButtons();
            }
            return retval;
        }

        protected void setRightButtons(Pane rightButtons) {
            this.rightButtons = rightButtons;
        }

        protected void restoreButtonsStyle() {
            restoreButtonsStyle(topButtons, TOP_INDICATOR_STYLE);
            restoreButtonsStyle(rightButtons, RIGHT_INDICATOR_STYLE);
            restoreButtonsStyle(bottomButtons, BOTTOM_INDICATOR_STYLE);
            restoreButtonsStyle(leftButtons, LEFT_INDICATOR_STYLE);
        }

        protected void restoreButtonsStyle(Pane pane, String paneStyle) {

            pane.getStyleClass().clear();
            pane.getStyleClass().add(paneStyle);

            pane.getChildren().forEach(node -> {
                node.getStyleClass().clear();
                node.getStyleClass().add("button");
                node.getStyleClass().add(getStyle());
            });
        }

        protected Pane createSideButtons(Side side) {
            Button b = new Button();
            //b.setMouseTransparent(false);
            StackPane p = new StackPane(b);

            switch (side) {
                case TOP:
                    p.getStyleClass().add(TOP_INDICATOR_STYLE);
                    b.getStyleClass().add(getStyle());
                    break;
                case BOTTOM:
                    p.getStyleClass().add(BOTTOM_INDICATOR_STYLE);
                    b.getStyleClass().add(getStyle());
                    break;
                case LEFT:
                    p.getStyleClass().add(LEFT_INDICATOR_STYLE);
                    b.getStyleClass().add(getStyle());
                    if ( indicatorType == NODE_POINTER ){
                        b.setId("NODE: Button Of LeftButtonsPane");
                    } else {
                        b.setId("PANE: Button Of LeftButtonsPane");
                    }
                    break;
                case RIGHT:
                    p.getStyleClass().add(RIGHT_INDICATOR_STYLE);
                    b.getStyleClass().add(getStyle());
                    break;
            }
            return p;
        }

        public Pane getButtons(Side side) {
            Pane retval = null;
            if (null != side) {
                switch (side) {
                    case TOP:
                        retval = getTopButtons();
                        break;
                    case BOTTOM:
                        retval = getBottomButtons();
                        break;
                    case LEFT:
                        retval = getLeftButtons();
                        break;
                    case RIGHT:
                        retval = getRightButtons();
                        break;
                }
            }
            return retval;
        }
        
        
        public void targetNodeChanged(PaneHandler paneHandler, Region node, double x, double y) {
            transform(paneHandler, node, x,y);
            transformer.targetNodeChanged(node);
        }

        public Point2D mousePosBy(PaneHandler targetPaneHandler, Region targetNode, double screenX, double screenY) {
            transform(targetPaneHandler, targetNode, screenX, screenY);
            return transformer.getMousePos();
        }                
        protected void transform(PaneHandler targetPaneHandler, Region targetNode, double screenX, double screenY) {
            if (indicatorType == NODE_POINTER) {
                transformer = targetPaneHandler.getNodeTransformer();
            } else {
                transformer = targetPaneHandler.getPaneTransformer();
            }

            if (transformer == null) {
                transformer = new SideIndicatorTransformer(indicatorType);
            }
            
            //restoreButtonsStyle();
            transformer.initialize(this, new Point2D(screenX, screenY), topButtons, bottomButtons, leftButtons, rightButtons);

            transformer.setTargetNode(targetNode);
            transformer.setTargetPaneHandler(targetPaneHandler);
            transformer.transform();
            /*            mousePos = new Point2D(screenX, screenY);
            PaneHandler paneHandler = dragPopup.getTargetPaneHandler();
            //System.err.println("!!!!!!!!!!!!!!!!!!!! NOT NULL " + paneHandler);                
            SidePointerModifier pm = paneHandler.getSidePointerModifier();
            if (pm != null) {
                System.err.println("!!!!!!!!!!!!!!!!!!!! NOT NULL");
                mousePos = pm.modify(dragPopup, target, screenX, screenY);
                if (mousePos == null) {
                    return null;
                } else {
                    return this;
                }
            }
            return null;
             */
        }
        public void sideIndicatorShowing(PaneHandler paneHandler, Region node) {
            transform(paneHandler, node, 0, 0);
            transformer.sideIndicatorShowing(node);
        }
        public void sideIndicatorHiding(PaneHandler paneHandler, Region node) {
            transform(paneHandler, node, 0, 0);
            transformer.sideIndicatorHiding(node);
        }
        
        public Point2D getMousePos() {
            return transformer.getMousePos();
        }

    }//class SideIndicator

    public static class SideIndicatorTransformer {

        public static final String SMALL_NODE_STYLE = "target-node-small";
        public static final String SMALL_PANE_STYLE = "target-pane-small";
        public static final String SMALL_TOP_INDICATOR_STYLE = "drag-top-indicator-small";
        public static final String SMALL_RIGHT_INDICATOR_STYLE = "drag-right-indicator-small";
        public static final String SMALL_BOTTOM_INDICATOR_STYLE = "drag-bottom-indicator-small";
        public static final String SMALL_LEFT_INDICATOR_STYLE = "drag-left-indicator-small";

        private PaneHandler targetPaneHandler;
        private Region targetNode;
        private String style;
        private Point2D mousePos;
        private Pane topButtons;
        private Pane bottomButtons;
        private Pane leftButtons;
        private Pane rightButtons;
        private SideIndicator indicator;
        private int indicatorType;

        private Scale smallbuttonsScale;

        public SideIndicatorTransformer(int indicatorType) {
            this(null, indicatorType);
        }

        public SideIndicatorTransformer(PaneHandler targetPaneHandler, int indicaterType) {
            this.targetPaneHandler = targetPaneHandler;
            this.indicatorType = indicaterType;
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

        public void transform() {
            if (targetPaneHandler != null ) {
                //resizeButtonPanes();
            }
        }
        public void sideIndicatorShowing(Region node) {
            if (targetPaneHandler != null && node != null) {
                resizeButtonPanes();
            }
            
        }
        public void sideIndicatorHiding(Region node) {
            if (targetPaneHandler == null || node == null) {
                resizeButtonPanes();
            }
            
        }
        
        public void targetNodeChanged(Region node) {
            if ( targetNode != node ) {
                
            }
        }
        
        protected void restoreButtonsStyle() {
            indicator.restoreButtonsStyle();
        }

        protected void resizeButtonPanes() {
            if ( targetPaneHandler != null && targetNode != null && intersects()) { 
                if (! indicator.indicatorPane.getTransforms().contains(smallbuttonsScale)) {
                    indicator.indicatorPane.getTransforms().add(smallbuttonsScale);

                    double w = indicator.indicatorPane.getWidth() / 2;
                    double h = indicator.indicatorPane.getHeight() / 2;
                    Point2D p = indicator.indicatorPane.localToParent(w,h);
                    smallbuttonsScale.setPivotX(w);
                    smallbuttonsScale.setPivotY(h);
                }
            } else {
                indicator.getIndicatorPane().getTransforms().remove(smallbuttonsScale);
            }

        }

        protected boolean intersects() {
            boolean retval = false;
            Pane thisPane = indicator.getIndicatorPane();

            if (indicatorType == NODE_POINTER && targetPaneHandler != null) {

                Node node = targetPaneHandler.getDragPopup().getPaneIndicator().getTopButtons();

                if (intersects(thisPane, node)) {
                    return true;
                }
                node = targetPaneHandler.getDragPopup().getPaneIndicator().getRightButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = targetPaneHandler.getDragPopup().getPaneIndicator().getBottomButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = targetPaneHandler.getDragPopup().getPaneIndicator().getLeftButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
            }
            return false;
        }

        public Boolean intersects(Node node1, Node node2) {
            if (node1 == null || node2 == null) {
                return false;
            }
            Bounds b1 = node1.localToScreen(node1.getBoundsInLocal());
            Bounds b2 = node2.localToScreen(node2.getBoundsInLocal());
            //System.err.println("INTERSECTS !!!!!!!!!!! " + b1.intersects(b2));
            return b1.intersects(b2);

        }

        protected void changeButtonPaneStyle(Pane pane, String style) {
            pane.getStyleClass().clear();
            pane.getStyleClass().add(style);
        }

        protected double getTargetHeight() {
            double retval = Double.MAX_VALUE;
            if (indicatorType == NODE_POINTER && targetNode != null) {
                retval = targetNode.getHeight();
            } else if (targetPaneHandler != null && indicatorType == PANE_POINTER) {
                retval = targetPaneHandler.getDockPane().getHeight();
            }
            return retval;
        }

        protected double getTargetWidth() {
            double retval = Double.MAX_VALUE;
            if (indicatorType == NODE_POINTER && targetNode != null) {
                retval = targetNode.getWidth();
            } else if (targetPaneHandler != null && indicatorType == PANE_POINTER) {
                retval = targetPaneHandler.getDockPane().getWidth();
            }
            return retval;

        }

        public String getStyle() {
            if (style == null) {
                style = indicatorType == NODE_POINTER ? NODE_STYLE : PANE_STYLE;
            }
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public Point2D getMousePos() {
            Point2D newPos = null;
            if (targetNode != null && indicator.getIndicatorPane() != null) {
                //System.err.println("getMousePos targetNode = " + targetNode);
                newPos = targetNode.localToScreen((targetNode.getWidth() - indicator.getIndicatorPane().getWidth()) / 2, (targetNode.getHeight() - indicator.getIndicatorPane().getHeight()) / 2);
            }

            return newPos;
        }

        public void setMousePos(Point2D mousePos) {
            this.mousePos = mousePos;
        }

        protected void setTargetPaneHandler(PaneHandler targetPaneHandler) {
            this.targetPaneHandler = targetPaneHandler;
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

        public void setRightButtons(Pane rightButtons) {
            this.rightButtons = rightButtons;
        }
    }//Transformer

}//class
