package org.vns.javafx.dock.api.editor;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import static org.vns.javafx.dock.api.editor.EditorUtil.getCell;
import static org.vns.javafx.dock.api.editor.EditorUtil.getRootStartGap;
import static org.vns.javafx.dock.api.editor.EditorUtil.screenNonValueBounds;
import static org.vns.javafx.dock.api.editor.EditorUtil.screenTreeItemBounds;
import static org.vns.javafx.dock.api.editor.EditorUtil.screenValueBounds;
import static org.vns.javafx.dock.api.editor.TreeItemBuilder.CELL_UUID;
import static org.vns.javafx.dock.api.editor.TreeItemBuilder.NODE_UUID;

/**
 *
 * @author Valery
 */
public class SceneGraphEditor {

    private static final double LEVEL_SPACE = 10;

    public static final int LAST = 0;
    public static final int FIRST = 2;

    private TreeViewDragEventHandler mouseDragHandler;

    public static double ANCHOR_OFFSET = 4;

    private final TreeView<ItemValue> treeView;
    private final Pane editorPane = new Pane();
    private final Line vertLine = new Line();
    private final Line horLine = new Line();
    private final Rectangle itemRect = new Rectangle();
    private final Node rootNode;

    public SceneGraphEditor(Node rootNode) {
        this.treeView = new TreeView<>();
        this.rootNode = rootNode;
        init();
    }

    private void init() {
        getEditorPane().getChildren().add(treeView);
        addStageListeners();
        customizeCell();
        mouseDragHandler = new TreeViewDragEventHandler(this);

        treeView.addEventHandler(DragEvent.ANY, mouseDragHandler);

        editorPane.getChildren().addAll(horLine, vertLine, itemRect);
        editorPane.setStyle("-fx-background-color: green; -fx-padding: 0 3 0 3; -fx-insets: 0; -fx-border-width: 0");
        vertLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");
        horLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");
        //itemRect.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2; -fx-background-color: transparent;-fx-fill: transparent");
        itemRect.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2; -fx-background-color: transparent;-fx-fill: transparent");
        vertLine.setMouseTransparent(true);
        horLine.setMouseTransparent(true);
        itemRect.setMouseTransparent(true);

        if (treeView.getScene() != null && treeView.getScene().getWindow() != null && treeView.getScene().getWindow().isShowing()) {
            windowShown(null);
        }
    }

    public Pane getEditorPane() {
        return editorPane;
    }

    public TreeView<ItemValue> getTreeView() {
        return treeView;
    }

    protected TreeItem createSceneGraph(Node node) {
        TreeItem item = null;
        if (TreeItemRegistry.getInstance().exists(node)) {
            item = TreeItemRegistry.getInstance().getBuilder(node).build(node);
        }
        return item;
    }

    protected void addStageListeners() {

        if (treeView.getScene() != null && treeView.getScene().getWindow() != null) {
            if (treeView.getScene().getWindow().isShowing()) {
                windowShown(null);
            } else {
                treeView.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_SHOWN, this::windowShown);
            }
        } else if (treeView.getScene() == null) {
            treeView.sceneProperty().addListener(this::sceneChanged);
        } else if (treeView.getScene() != null) {
            treeView.getScene().windowProperty().addListener(this::windowChanged);
        }
    }

    protected void sceneChanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        if (newValue != null) {
            newValue.windowProperty().addListener(this::windowChanged);
        }
    }

    protected void windowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        if (newValue != null) {
            newValue.addEventFilter(WindowEvent.WINDOW_SHOWN, this::windowShown);
        }
    }

    protected void windowShown(WindowEvent ev) {
        TreeItem it = createSceneGraph(rootNode);
        it.setExpanded(true);
        treeView.setRoot(it);
    }

    protected void customizeCell() {
        treeView.setCellFactory((TreeView<ItemValue> tv) -> {
            TreeCell<ItemValue> cell = new TreeCell<ItemValue>() {
                @Override
                public void updateItem(ItemValue value, boolean empty) {
                    super.updateItem(value, empty);

                    if (empty || value == null) {
                        setText(null);
                        setGraphic(null);
                        //setStyle(null);
                        if (this.getUserData() != null) {
                            this.removeEventHandler(DragEvent.ANY, (TreeItemCellDragEventHandler) this.getUserData());
                        }
                        this.setOnDragDetected(null);
                        this.setOnDragDropped(null);
                        this.setOnDragDone(null);
                    } else {
                        //if ( value.getTreeItemObject() instanceof VBox )
                        //this.setStyle("-fx-background-color: yellow");
                        this.setGraphic(value.getCellGraphic());
                        TreeItemCellDragEventHandler h = new TreeItemCellDragEventHandler(SceneGraphEditor.this, this);
                        this.addEventHandler(DragEvent.ANY, h);
                        this.setUserData(h);
                        //System.err.println("this.getBounds " + this.localToScreen(this.getBoundsInLocal()));

                        registerDragDetected(this);
                        registerDragDropped(this);
                        registerDragDone(this);
                    }
                }
            };
            return cell;
        });
    }

    protected void registerDragDetected(TreeCell cell) {
        cell.setOnDragDetected(ev -> {
            Dragboard dragboard = cell.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            DragGesture dg = new DragTreeCellGesture(cell);
            cell.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
            ClipboardContent content = new ClipboardContent();
            content.putUrl(CELL_UUID);
            dragboard.setContent(content);
            treeView.getSelectionModel().clearSelection();
            ev.consume();
        });
    }

    protected void registerDragDone(TreeCell cell) {
        cell.setOnDragDone(ev -> {
            ItemValue sourceValue = ((TreeCell<ItemValue>) ev.getGestureSource()).getTreeItem().getValue();
            hideDrawShapes();
            ev.consume();
        });
    }

    protected void registerDragDropped(TreeCell cell) {
        cell.setOnDragDropped((DragEvent ev) -> {
            //System.err.println("registerDragDropped");
            //ItemValue sourceValue = ((TreeCell<ItemValue>) ev.getGestureSource()).getTreeItem().getValue();
            TreeItem<ItemValue> targetItem = getTargetTreeItem(ev, ((TreeCell) ev.getGestureTarget()).getTreeItem());
            ItemValue targetValue = targetItem.getValue();
            //
            // Transfer the data to the target
            //
            Dragboard dragboard = ev.getDragboard();
            if (dragboard.hasUrl()) {
                TreeItem target = ((TreeCell) ev.getGestureTarget()).getTreeItem();
                System.err.println("---- DROPPED before accept targetItem=" + targetItem.getValue().getTreeItemObject());
                targetValue.getBuilder().accept(treeView, targetItem, target, (Node) ev.getGestureSource());
                System.err.println("---- DROPPED after accept ");
                
                ev.setDropCompleted(true);
            } else {
                ev.setDropCompleted(false);
            }
            ev.consume();
        });
    }

    protected TreeItem<ItemValue> findTreeItem(Object sourceGesture) {
        return findTreeItem(getTreeView().getRoot(), sourceGesture);
    }

    protected TreeItem<ItemValue> findTreeItem(TreeItem<ItemValue> item, Object sourceGesture) {
        TreeItem retval = null;
        for (TreeItem<ItemValue> it : item.getChildren()) {
            if (it.getValue().getTreeItemObject() == sourceGesture) {
                retval = it;
                break;
            }
            retval = findTreeItem(it, sourceGesture);
            if (retval != null) {
                break;
            }
        }
        return retval;
    }

    protected void hideDrawShapes() {
        itemRect.setVisible(false);
        vertLine.setVisible(false);
        horLine.setVisible(false);
        itemRect.toBack();
        vertLine.toBack();
        horLine.toBack();

    }

    protected void drawRectangle(TreeItem item) {
        hideDrawShapes();
        Bounds lb = EditorUtil.screenTreeItemBounds(item);
        if (lb == null) {
            return;
        }

        lb = editorPane.screenToLocal(lb);
        itemRect.setX(lb.getMinX());
        itemRect.setY(lb.getMinY());
        itemRect.setWidth(lb.getWidth());
        itemRect.setHeight(lb.getHeight());
        itemRect.toFront();
        itemRect.setVisible(true);
    }

    protected void drawLines(TreeItem<ItemValue> from, TreeItem<ItemValue> to) {
        treeView.setPadding(Insets.EMPTY);
        Insets pins = treeView.getPadding();

        AnchorPane ap = (AnchorPane) from.getValue().getCellGraphic();

        Pane p = getEditorPane();
        Bounds bnd =null;
        Bounds arrowBnd = null;
        Bounds rootBounds = null;
        
        if ( ap.getScene() != null && treeView.getRoot().getValue().getCellGraphic().getScene() != null) {
            System.err.println("      --- DrawLines scene != null");
            System.err.println("      --- DrawLines from = " + from.getValue().getTreeItemObject());
            bnd = EditorUtil.screenNonValueLevelBounds(treeView, from);
            System.err.println("      --- DrawLines nonValuLevelBounds = " + bnd);
            
            arrowBnd = EditorUtil.screenArrowBounds(from);
            System.err.println("      --- DrawLines screenArrowBounds = " + arrowBnd);
            
            if ( treeView.getRoot().getValue().getCellGraphic().getScene() != null ) {
                System.err.println("      --- DrawLines root.scene != null");
                rootBounds = EditorUtil.screenNonValueLevelBounds(treeView, treeView.getRoot());
            } else {
                System.err.println("      --- DrawLines root.scene == null");                
                rootBounds = screenNonValueLevelBounds(treeView.getRoot(), to);
            }

        } else {
            System.err.println("      --- DrawLines scene == null");
            bnd = screenNonValueLevelBounds(from, to);
            System.err.println("         --- DrawLines nonValuLevelBounds = " + bnd);            
            arrowBnd = screenArrowBounds(from, to);
            System.err.println("         --- DrawLines screenArrowBounds = " + arrowBnd);            
            rootBounds = screenNonValueLevelBounds(treeView.getRoot(), to);
            System.err.println("         --- DrawLines rootBounds = " + rootBounds);            
        }

        int level = treeView.getTreeItemLevel(from);

        double gap = EditorUtil.getRootStartGap(treeView);

        double startY = bnd.getMinY() + bnd.getHeight() + pins.getBottom();
        if (arrowBnd.getHeight() != 0) {
            startY = arrowBnd.getMinY() + arrowBnd.getHeight();
        }

        double startX = rootBounds.getMinX() + rootBounds.getWidth() + gap * level;

        if (arrowBnd.getWidth() != 0) {
            startX = arrowBnd.getMinX() + arrowBnd.getWidth() / 2;
        }

        vertLine.setStartX(p.screenToLocal(startX, startY).getX());
        vertLine.setStartY(p.screenToLocal(startX, startY).getY());

        vertLine.setEndX(vertLine.getStartX());

        hideDrawShapes();
        vertLine.toFront();
        vertLine.setVisible(true);
        //
        // --- Horizontal line ----------
        //
        Bounds lb = EditorUtil.screenTreeItemBounds(to);


        lb = editorPane.screenToLocal(lb);
        horLine.setStartX(lb.getMinX());
        horLine.setStartY(lb.getMinY() + lb.getHeight());
        horLine.setEndY(horLine.getStartY());
        horLine.setEndX(horLine.getStartX() + lb.getWidth());

        vertLine.setEndY(horLine.getStartY());

        horLine.toFront();
        horLine.setVisible(true);

    }

    protected void drawLines2(TreeItem<ItemValue> from, TreeItem<ItemValue> to) {
        Bounds lb = EditorUtil.screenTreeItemBounds(to);
        Bounds rootBounds = EditorUtil.screenNonValueLevelBounds(treeView, treeView.getRoot());
        
        treeView.setPadding(Insets.EMPTY);
        Insets pins = treeView.getPadding();

        AnchorPane ap = (AnchorPane) from.getValue().getCellGraphic();

        Pane p = getEditorPane();
        Bounds bnd = null;
        if ( ap.getScene() != null ) {
            bnd = EditorUtil.screenNonValueLevelBounds(treeView, from);
        }

        int level = treeView.getTreeItemLevel(from);

        double gap = EditorUtil.getRootStartGap(treeView);
        Bounds arrowBnd = EditorUtil.screenArrowBounds(from);

        double startY = bnd.getMinY() + bnd.getHeight() + pins.getBottom();
        if (arrowBnd.getHeight() != 0) {
            startY = arrowBnd.getMinY() + arrowBnd.getHeight();
        }

        double startX = rootBounds.getMinX() + rootBounds.getWidth() + gap * level;

        if (arrowBnd.getWidth() != 0) {
            startX = arrowBnd.getMinX() + arrowBnd.getWidth() / 2;
        }

        vertLine.setStartX(p.screenToLocal(startX, startY).getX());
        vertLine.setStartY(p.screenToLocal(startX, startY).getY());

        vertLine.setEndX(vertLine.getStartX());

        hideDrawShapes();
        vertLine.toFront();
        vertLine.setVisible(true);
        //
        // --- Horizontal line ----------
        //
        


        lb = editorPane.screenToLocal(lb);
        horLine.setStartX(lb.getMinX());
        horLine.setStartY(lb.getMinY() + lb.getHeight());
        horLine.setEndY(horLine.getStartY());
        horLine.setEndX(horLine.getStartX() + lb.getWidth());

        vertLine.setEndY(horLine.getStartY());

        horLine.toFront();
        horLine.setVisible(true);

    }
    protected Bounds screenValueBounds(TreeItem<ItemValue> item, TreeItem<ItemValue> sample) {
        AnchorPane ap = (AnchorPane) sample.getValue().getCellGraphic();
        if ( ap.getScene() == null ) {
            //System.err.println("******** NULL screenValueBounds");
            return null;
        }
        //TreeCell sampleCell = (TreeCell) ap.getParent();
        
//        System.err.println("screenValueBounds scene=" + ap.getScene());
//        System.err.println("screenValueBounds item.getId=" + ((Node)item.getValue().getTreeItemObject()));
//        System.err.println("screenValueBounds 1 ap=" + ap + "; ap.getBoundsInLocal=" + ap.getBoundsInLocal());        
        Bounds apBounds = ap.localToScreen(ap.getBoundsInLocal());
//System.err.println("screenValueBounds 3 apBounds=" + apBounds + "; item=" + item.getValue().getTreeItemObject());        
//System.err.println("screenValueBounds 3.1  ap.getLayoutBounds=" + ap.getLayoutBounds());        
        Bounds cellBounds = null;
        int dif = treeView.getTreeItemLevel(item)-treeView.getTreeItemLevel(sample);
        double cellOffset = EditorUtil.getRootStartGap(treeView);
        
        if ( item.getValue().getCellGraphic().getScene() != null ) {
            cellBounds = screenTreeItemBounds(item);
        } else {
            cellBounds = screenTreeItemBounds(sample);
        }
//System.err.println("screenValueBounds 4 cellBounds=" + cellBounds);        
        
        double height = cellBounds.getHeight();
        double width  = cellBounds.getMinX() + cellBounds.getWidth() - apBounds.getMinX() - dif * cellOffset;
        
        //return new BoundingBox(apBounds.getMinX(), cellBounds.getMinY(), width , height);
        return new BoundingBox(apBounds.getMinX() - dif * cellOffset, treeView.localToScreen(treeView.getBoundsInLocal()).getMinY(), width , height);
    }
    
    protected double rootNonValueWidth(TreeItem<ItemValue> sample) {
        int level = treeView.getTreeItemLevel(sample);
        double cellOffset = EditorUtil.getRootStartGap(treeView);
        Bounds sampleBounds = EditorUtil.screenNonValueBounds(sample);
        return sampleBounds.getWidth() - cellOffset * level;
    }
            
    protected Bounds screenNonValueLevelBounds(TreeItem<ItemValue> item, TreeItem<ItemValue> sample) {
        Bounds sampleBnd = EditorUtil.screenNonValueBounds(sample);

        int level = treeView.getTreeItemLevel(item);
        double gap = getRootStartGap(treeView);

        double w;// = ((treeBnd.getWidth() - sampleBnd.getWidth() - wdelta) / (level + 1));
        double xOffset;// = 0;

        double rootWidth = rootNonValueWidth(sample);
        
        if (level > 0) {
            xOffset = rootWidth / 2 + gap * level;
            w = gap;
        } else {
            w = rootWidth / 2;
            xOffset = 0;
        }
        Bounds nvBnd = new BoundingBox(sampleBnd.getMinX() + xOffset, treeView.localToScreen(treeView.getBoundsInLocal()).getMinY(), w, sampleBnd.getHeight());
        return nvBnd;
    }
    
    protected Bounds screenArrowBounds(TreeItem<ItemValue> item, TreeItem<ItemValue> sample) {
        TreeCell cell = getCell(sample);
        Bounds retval = new BoundingBox(0, 0, 0, 0);
        if (!(cell.getDisclosureNode() instanceof Pane)) {
            return retval;
        }

        if (((Pane) cell.getDisclosureNode()).getChildren().isEmpty()) {
            return retval;
        }
        Node arrow = ((Pane) cell.getDisclosureNode()).getChildren().get(0);
        System.err.println("      === screenArrowBounds sample = " + sample.getValue().getTreeItemObject());        
        System.err.println("      === screenArrowBounds arrow = " + arrow + "; arrow scene=" + arrow.getScene());        
        Bounds b = arrow.getBoundsInLocal();
        System.err.println("      === screenArrowBounds arrow local = " + b);                
        int dif = treeView.getTreeItemLevel(item)-treeView.getTreeItemLevel(sample);
        double cellOffset = EditorUtil.getRootStartGap(treeView);
        b = arrow.localToScreen(b);
        System.err.println("      === screenArrowBounds b = " + b);
        double y = treeView.localToScreen(treeView.getBoundsInLocal()).getMinY();
        if ( item.getValue().getCellGraphic().getScene() != null ) {
            System.err.println("      === screenArrowBounds 1");
            y = EditorUtil.screenTreeItemBounds(item).getMinY();
            System.err.println("      === screenArrowBounds 2 y=" + y);
        }
        b = new BoundingBox(b.getMinX()-dif * cellOffset, y, b.getWidth(), b.getHeight());
        return b;
    }
    
    public Bounds[] levelBoundsOf(TreeItem item) {
        int level = treeView.getTreeItemLevel(item);
        //System.err.println("level = " + level);
        double cellOffset = EditorUtil.getRootStartGap(treeView);

        Bounds[] bounds = new Bounds[level + 3];
        TreeItem rootItem = treeView.getRoot();
        //Bounds rootBounds = EditorUtil.screenNonValueBounds(treeView, rootItem);

        Bounds itemBounds = EditorUtil.screenNonValueBounds(item);
        Bounds valueBounds = EditorUtil.screenValueBounds(item);

        //Bounds rootBounds = EditorUtil.screenNonValueBounds(rootItem);
        Bounds rootBounds = null;
        //System.err.println("levelBoundsOf 1 = " + rootBounds);
        if (rootBounds == null) {
            // root item is hidden
            //cellOffset * (level - 1);
            //double offset = itemBounds.getWidth() / 2 + cellOffset * (i - 1);    
            rootBounds = new BoundingBox(0, 0, (itemBounds.getWidth() - cellOffset * (level)), itemBounds.getHeight());
            //System.err.println("levelBoundsOf 2 = " + rootBounds);
        }
        /*        System.err.println("rootBounds=" + rootBounds);        
        System.err.println("itemBounds=" + rootBounds);        
        System.err.println("root valueBounds=" + EditorUtil.screenValueBounds(rootItem));        
        System.err.println("valueBounds=" + valueBounds);        
         */
        double xOffset;// = 0;
        double width;// = 0;

        for (int i = 0; i <= level; i++) {
            if (i == 0) {
                xOffset = 0;
                width = rootBounds.getWidth() / 2;
            } else {
                xOffset = rootBounds.getWidth() / 2 + cellOffset * (i - 1);
                width = cellOffset;
            }
            bounds[i] = new BoundingBox(itemBounds.getMinX() + xOffset, itemBounds.getMinY() + itemBounds.getHeight() - LEVEL_SPACE, width, LEVEL_SPACE);
            if (i == level && level > 0) {
                bounds[i + 1] = new BoundingBox(itemBounds.getMinX() + xOffset, itemBounds.getMinY() + itemBounds.getHeight() - LEVEL_SPACE, itemBounds.getWidth() - cellOffset, LEVEL_SPACE);
                bounds[i + 2] = new BoundingBox(valueBounds.getMinX(), itemBounds.getMinY() + itemBounds.getHeight() - ANCHOR_OFFSET - 2, valueBounds.getWidth(), ANCHOR_OFFSET + 1);
            } else if (i == level && level == 0) {
                bounds[i + 1] = new BoundingBox(itemBounds.getMinX() + width, itemBounds.getMinY() + itemBounds.getHeight() - LEVEL_SPACE, width + cellOffset, LEVEL_SPACE);
                bounds[i + 2] = new BoundingBox(valueBounds.getMinX(), itemBounds.getMinY() + itemBounds.getHeight() - ANCHOR_OFFSET, valueBounds.getWidth(), ANCHOR_OFFSET + 1);
            }
        }
        return bounds;
    }

    protected TreeItem<ItemValue> getTargetTreeItem_OLD(DragEvent ev, TreeItem item) {
        TreeItem<ItemValue> retval = null;
        hideDrawShapes();
        if (item != null) {
            //System.err.println("getTargetTreeItem 1");
            Bounds[] bounds = levelBoundsOf(item);
            //System.err.println("getTargetTreeItem 1.1");
            int n = -1;
            for (int i = 0; i < bounds.length; i++) {
                if (bounds[i].contains(ev.getScreenX(), ev.getScreenY())) {
                    n = i;
                    break;
                }
            }
            int level = treeView.getTreeItemLevel(item);
            if (n < 0) {
                retval = item;
                ((ItemValue) retval.getValue()).setDragDropQualifier(LAST);
                //System.err.println("getTargetTreeItem 2");
                //drawRectangle(item);
            } else if (item.isLeaf()) {
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
                    //if (level == n || n +1 == level || n+2 == level) {
                    //drawLines(item.getParent(), item);
                    retval = item.getParent();
                    //System.err.println("getTargetTreeItem 3");
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        //drawLines(parentAtLevel(item, n), item);
                        retval = parentAtLevel(item, n);
                        //  System.err.println("getTargetTreeItem 4");
                    } else {
                        //drawLines(item.getParent(), item);
                        retval = item.getParent();
                        //System.err.println("getTargetTreeItem 5");
                    }
                }
            } else if (!item.isExpanded()) {
                //System.err.println("getTargetTreeItem 6");
                // not leaf and not expanded     
                if (n == level || n == level + 1 || n == level + 2) {
                    //if ( acceptable ) {
                    //    drawLines(item, item);
                    //}
                    //System.err.println("getTargetTreeItem 7");
                } else if (n == level - 1) {
                    //drawLines(item.getParent(), item);
                    retval = item.getParent();
                    //System.err.println("getTargetTreeItem 8" + retval.getValue().getTreeItemObject());
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        //drawLines(parentAtLevel(item, n), item);
                        retval = parentAtLevel(item, n);
                        //  System.err.println("getTargetTreeItem 9");
                    } else {
                        //drawLines(item.getParent(), item);
                        retval = item.getParent();
                        //System.err.println("getTargetTreeItem 10");
                    }
                }
            } else {
                //drawLines(item, item);
                retval = item;
                ((ItemValue) item.getValue()).setDragDropQualifier(FIRST);
                //System.err.println("getTargetTreeItem 11");
            }
        }
        return retval;
    }

    protected TreeItem parentAtLevel(TreeItem item, int level) {
        TreeItem it = item;
        while (it != null) {
            if (treeView.getTreeItemLevel(it) == level) {
                break;
            }
            it = it.getParent();
        }
        return it;
    }

    protected TreeItem<ItemValue> getTargetTreeItem(DragEvent ev, TreeItem<ItemValue> item) {
        if ( item.getValue().getCellGraphic().getScene() == null ) {
            System.err.println("++++++++ found null");
        }
        TreeItem<ItemValue> retval = null;
        hideDrawShapes();
        if (item != null) {
            //System.err.println("getTargetTreeItem 1");
            Bounds[] bounds = levelBoundsOf(item);
            //System.err.println("getTargetTreeItem 1.1");
            int n = -1;
            for (int i = 0; i < bounds.length; i++) {
                if (bounds[i].contains(ev.getScreenX(), ev.getScreenY())) {
                    n = i;
                    break;
                }
            }
            int level = treeView.getTreeItemLevel(item);
            if (n < 0) {
                retval = item;
                ((ItemValue) retval.getValue()).setDragDropQualifier(LAST);
                //System.err.println("getTargetTreeItem 2");
                //drawRectangle(item);
            } else if (item.isLeaf()) {
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
                    //if (level == n || n +1 == level || n+2 == level) {
                    //drawLines(item.getParent(), item);
                    retval = item.getParent();
                    //System.err.println("getTargetTreeItem 3");
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        //drawLines(parentAtLevel(item, n), item);
                        retval = parentAtLevel(item, n);
                        //  System.err.println("getTargetTreeItem 4");
                    } else {
                        //drawLines(item.getParent(), item);
                        retval = item.getParent();
                        //System.err.println("getTargetTreeItem 5");
                    }
                }
            } else if (!item.isExpanded()) {
                //System.err.println("getTargetTreeItem 6");
                // not leaf and not expanded     
                if (n == level || n == level + 1 || n == level + 2) {
                    //if ( acceptable ) {
                    //    drawLines(item, item);
                    //}
                    //System.err.println("getTargetTreeItem 7");
                } else if (n == level - 1) {
                    //drawLines(item.getParent(), item);
                    retval = item.getParent();
                    //System.err.println("getTargetTreeItem 8" + retval.getValue().getTreeItemObject());
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        //drawLines(parentAtLevel(item, n), item);
                        retval = parentAtLevel(item, n);
                        //  System.err.println("getTargetTreeItem 9");
                    } else {
                        //drawLines(item.getParent(), item);
                        retval = item.getParent();
                        //System.err.println("getTargetTreeItem 10");
                    }
                }
            } else {
                //drawLines(item, item);
                retval = item;
                ((ItemValue) item.getValue()).setDragDropQualifier(FIRST);
                //System.err.println("getTargetTreeItem 11");
            }
        }
        return retval;
    }

    protected void treeItemDragOver(DragEvent ev, TreeItemEx item, boolean acceptable) {
        hideDrawShapes();
        if (item != null) {
            Bounds[] bounds = levelBoundsOf(item);
            int n = -1;
            for (int i = 0; i < bounds.length; i++) {
                if (bounds[i].contains(ev.getScreenX(), ev.getScreenY())) {
                    n = i;
                    break;
                }
            }
            
            int level = treeView.getTreeItemLevel(item);
System.err.println("   --- treeItemDragOver 1 n=" + n + "; level=" + level);            
            if (n < 0 && !acceptable) {
                return;
            } else if (n < 0) {
                itemRect.setVisible(true);
                drawRectangle(item);
            } else if (item.isLeaf()) {
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
System.err.println("   --- treeItemDragOver 2 level=" + level);                    
                    drawLines(item.getParent(), item);
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        drawLines(parentAtLevel(item, n), item);
                    } else {
                        drawLines(item.getParent(), item);
                    }
                }
            } else if (!item.isExpanded()) {
                // not leaf and not expanded     
                if (n == level || n == level + 1 || n == level + 2) {
                    if (acceptable) {
                        drawLines(item, item);
                    }
                } else if (n == level - 1) {
                    drawLines(item.getParent(), item);
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        drawLines(parentAtLevel(item, n), item);
                    } else {
                        drawLines(item.getParent(), item);
                    }
                }
            } else {
                drawLines(item, item);
            }
        }
    }

    public static class DragEventHandler implements EventHandler<DragEvent> {

        private final SceneGraphEditor editor;
        private final TreeCell targetCell;

        private Point2D point = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);

        protected DragEventHandler(SceneGraphEditor editor, TreeCell targetCell) {
            this.editor = editor;
            this.targetCell = targetCell;
        }

        public Point2D getPoint() {
            return point;
        }

        /*        public void setPoint(Point2D point) {
            this.point = point;
        }
         */
        public SceneGraphEditor getEditor() {
            return editor;
        }

        public TreeCell getTargetCell() {
            return targetCell;
        }

        public TreeItem getTreeCellItem() {
            return targetCell.getTreeItem();
        }

        /**
         * Returns an object for witch the {@literal  TreeItem} of the
         * {@link #targetCell} was created. .
         *
         * @param ev
         * @return
         */
        protected Object getDragTargetObject(DragEvent ev) {
            return ((ItemValue) getTreeCellItem().getValue()).getTreeItemObject();
        }

        protected Object getDragSource(DragEvent ev) {
            Object o = ev.getGestureSource();
            if (o == null) {
                return null;
            }
            //Object retval = null;
            Node node = (Node) o;
            DragGesture dg = (DragGesture) node.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
            return dg.getGestureSourceObject();

            /*            if ((o instanceof Node) && !(o instanceof TreeCell)) {
                Node node = (Node) o;
                DragGesture dg = (DragGesture) node.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
                retval = dg.getGestureSourceObject();
            } else if (o instanceof TreeCell) {
                TreeItem item = ((TreeCell) o).getTreeItem();
                if (item instanceof TreeItemEx) {
                    retval = ((ItemValue) item.getValue()).getTreeItemObject();
                }

            }
             */
            //System.err.println("getDragSource  retval=" + retval);
            //return retval;
        }

        protected boolean isAcceptable(DragEvent ev, TreeItemBuilder builder) {
            Dragboard dragboard = ev.getDragboard();
            Object dragSource = getDragSource(ev);
            TreeItem it = getTreeCellItem();
            Object o = ((ItemValue) it.getValue()).getTreeItemObject();

            /*boolean b = (dragboard.hasUrl() && (dragboard.getUrl().equals(NODE_UUID) || dragboard.getUrl().equals(CELL_UUID)))
                    && builder != null
                    && builder.isDragTarget()
                    && builder.isAcceptable(getTreeCellItem(), dragSource);
             */
            return (dragboard.hasUrl() && (dragboard.getUrl().equals(NODE_UUID) || dragboard.getUrl().equals(CELL_UUID)))
                    && builder != null
                    && builder.isDragTarget()
                    && builder.isAcceptable(getTreeCellItem(), dragSource);
        }

        protected boolean isDragPlace(DragEvent ev, TreeItemBuilder builder) {
            Dragboard dragboard = ev.getDragboard();
            Object dragSource = getDragSource(ev);
            return (dragSource != null && dragboard.hasUrl()
                    && (dragboard.getUrl().equals(NODE_UUID)
                    || dragboard.getUrl().equals(CELL_UUID))
                    && builder != null);
        }

        @Override
        public void handle(DragEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static class TreeItemCellDragEventHandler extends DragEventHandler {

        public TreeItemCellDragEventHandler(SceneGraphEditor editor, TreeCell targetCell) {
            super(editor, targetCell);
        }

        /*        @Override
        public void handle(DragEvent ev) {
            if (ev.getEventType() == DragEvent.DRAG_OVER) {
                //
                // getDragTargetObject(ev) returns a n object that corresponds the 
                // current mouse pos and not the actual target
                //
                TreeItemBuilder builder = TreeItemRegistry.getInstance().getBuilder(getDragTargetObject(ev));
                System.err.println("TREECELL getDragTargetObject=" + getDragTargetObject(ev));
                System.err.println("TREECELL isDragPalce()=" + isDragPlace(ev, builder));
                System.err.println("TREECELL builder.class=" + builder.getClass().getSimpleName());

                System.err.println("TREECELL builder.isDragPalce()=" + builder.isDragPlace(getDragTargetObject(ev), getDragSource(ev)));
                getEditor().hideDrawShapes();
                if (isAcceptable(ev, builder)) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(getPoint())) {
                        if (ev.getAcceptingObject() != null && (ev.getAcceptingObject() instanceof TreeCell)) {
                            TreeCell cell = (TreeCell) ev.getAcceptingObject();
                            if (cell != null) {
                                getEditor().treeItemDragOver(ev, (TreeItemEx) cell.getTreeItem(), true);
                            }
                        }
                    }
                    ev.consume();
                } else {
                    Boolean b = false;
                    System.err.println("!!! targetObject 1" + (TreeCell)ev.getGestureTarget());
                    if ( (TreeCell)ev.getGestureTarget() != null ) {
                        TreeItem<ItemValue> target = getEditor().getTreeCellItem(ev, ((TreeCell)ev.getGestureTarget()).getTreeItem());
                        System.err.println("!!! targetObject 2");
                    
                        Object targetObject = target.getValue().getTreeItemObject();
                        System.err.println("targetObject=" + targetObject);
                        TreeItemBuilder targetBuilder = TreeItemRegistry.getInstance().getBuilder(targetObject);    
                        System.err.println("targetBuilder=" + targetBuilder);
                        b = targetBuilder.isAcceptable(target, targetObject);                        
                    }
                    if (isDragPlace(ev, builder) && b) {
                        ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                        if (!pt.equals(getPoint())) {
                            if (ev.getAcceptingObject() != null && (ev.getAcceptingObject() instanceof TreeCell)) {
                                TreeCell cell = (TreeCell) ev.getAcceptingObject();
                                if (cell != null) {
                                    //editor.drawLines(ev, (TreeItemEx) cell.getTreeItem());
                                    getEditor().treeItemDragOver(ev, (TreeItemEx) cell.getTreeItem(), false);
                                }
                            }
                        }
                        ev.consume();
                    }
                }
            }

        }//handle()
         */
        @Override
        public void handle(DragEvent ev) {
            //System.err.println("TreeCell HANDLE=" + getTargetCell());
            if (ev.getEventType() == DragEvent.DRAG_OVER) {
//                TreeItem<ItemValue> item = getTreeCellItem().getParent();
//        AnchorPane ap = (AnchorPane) ((ItemValue)item.getValue()).getCellGraphic();
//        TreeCell c = (TreeCell) ev.getGestureSource();
/*        System.err.println("111 screenValueBounds scene=" + c.getScene());
        System.err.println("111 screenValueBounds item.getId=" + ((Node)item.getValue().getTreeItemObject()).getId());
        System.err.println("111 screenValueBounds 1 ap=" + ap + "; ap.getBoundsInLocal=" + ap.getBoundsInLocal());        
        System.err.println("222 screenValueBounds 1 c=" + c + "; c.getBoundsInLocal=" + c.getBoundsInLocal());        
        System.err.println("333 screenValueBounds 1 c=" + c + "; c.getBoundsInParent=" + c.getBoundsInParent());        
                 */
                //
                // getDragTargetObject(ev) returns a n object that corresponds the 
                // current mouse pos and not the actual target
                //
                TreeItemBuilder builder = TreeItemRegistry.getInstance().getBuilder(getDragTargetObject(ev));
                /*                System.err.println("TREECELL getDragTarget=" + getDragTargetObject(ev));
                System.err.println("TREECELL isDragPalce()=" + isDragPlace(ev, builder));
                System.err.println("TREECELL builder.class=" + builder.getClass().getSimpleName());

                System.err.println("TREECELL builder.isDragPalce()=" + builder.isDragPlace(getDragTargetObject(ev), getDragSource(ev)));
                System.err.println("TREECELL isAcceptable=" + isAcceptable(ev, builder));
                 */
                //System.err.println("!!! targetObject 1 " + (TreeCell) ev.getGestureTarget());
                //System.err.println("------------------------------------------");
                getEditor().hideDrawShapes();
                if (isAcceptable(ev, builder)) {
                    System.err.println("handle 1");
                    //System.err.println("gestureTarget " + (TreeCell)ev.getGestureTarget());                    
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(getPoint())) {
                        getEditor().treeItemDragOver(ev, (TreeItemEx) getTargetCell().getTreeItem(), true);
                        /*                        if (ev.getAcceptingObject() != null && (ev.getAcceptingObject() instanceof TreeCell)) {
                            TreeCell cell = (TreeCell) ev.getAcceptingObject();
                            if (cell != null) {
                                getEditor().treeItemDragOver(ev, (TreeItemEx) cell.getTreeItem(), true);
                            }
                        }
                         */

                        ev.consume();
                    }
                } else {
                    Boolean b = false;
                    TreeItem<ItemValue> target = getEditor().getTargetTreeItem(ev, getTreeCellItem());
                    System.err.println("+++target = " + target.getValue().getTreeItemObject());
                    if (target != null) {
                        Object targetObject = target.getValue().getTreeItemObject();
                        //System.err.println("targetObject=" + targetObject);
                        //System.err.println("targetObject == " + (getDragSource(ev) == targetObject));
                        //System.err.println("targetObject dragSource=" + getDragSource(ev));
                        TreeItemBuilder targetBuilder = TreeItemRegistry.getInstance().getBuilder(targetObject);
                        b = targetBuilder.isAcceptable(target, getDragSource(ev)) && (getDragSource(ev) != targetObject);
                        //System.err.println("targetObject b=" + b);
                    }
                    //                  }
                    if (isDragPlace(ev, builder) && b) {
                        System.err.println("handle 2");

                        ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                        if (!pt.equals(getPoint())) {
                            //getEditor().treeItemDragOver(ev, (TreeItemEx) getTargetCell().getTreeItem(), false);
                            System.err.println("handle 3  ev.getAcceptingObject=" + ev.getAcceptingObject());

                            if (ev.getAcceptingObject() != null && (ev.getAcceptingObject() instanceof TreeCell)) {
                                TreeCell cell = (TreeCell) ev.getAcceptingObject();
                                System.err.println("handle 4 cell = " + ((ItemValue)cell.getTreeItem().getValue()).getTreeItemObject());
                                if (cell != null) {
                                    getEditor().treeItemDragOver(ev, (TreeItemEx) cell.getTreeItem(), false);
                                }
                            }
                        }
                    }
                    ev.consume();
                }
            }

        }
    }//MouseEventHandler

    public static class TreeViewDragEventHandler extends DragEventHandler {

        public TreeViewDragEventHandler(SceneGraphEditor editor) {
            super(editor, null);
        }

        @Override
        protected Object getDragTargetObject(DragEvent ev) {
            return ((ItemValue) getTreeCellItem().getValue()).getTreeItemObject();
        }

        /**
         *
         * @return
         */
        @Override
        public TreeItem getTreeCellItem() {
            int sz = getEditor().getTreeView().getExpandedItemCount();
            return getEditor().getTreeView().getTreeItem(sz - 1);
        }

        @Override
        public void handle(DragEvent ev) {
            //System.err.println("TreeView HANDLE");
            if (ev.getEventType() == DragEvent.DRAG_OVER) {
                //System.err.println("HANDLE HANDLE 1");
                TreeItemBuilder builder = TreeItemRegistry.getInstance().getBuilder(getDragTargetObject(ev));
                //System.err.println("HANDLE HANDLE 2");
                getEditor().hideDrawShapes();
                //System.err.println("TREEVIEW getDragTarget=" + getDragTargetObject(ev));
                //System.err.println("TREEVIEW isDragPalce()=" + isDragPlace(ev, builder));
                //System.err.println("TREEVIEW builder.class=" + builder.getClass().getSimpleName());

                //System.err.println("TREEVIEW builder.isDragPalce()=" + builder.isDragPlace(getDragTargetObject(ev), getDragSource(ev)));
                if (isDragPlace(ev, builder) && builder.isDragPlace(getDragTargetObject(ev), getDragSource(ev))) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(getPoint())) {
                        //setPoint(pt);
                        TreeItem item = getTreeCellItem();
                        if (item != null) {

                            TreeItem<ItemValue> it = getEditor().getTreeView().getRoot();
                            builder = TreeItemRegistry.getInstance().getBuilder(it.getValue().getTreeItemObject());

                            if (builder.isAcceptable(it, getDragSource(ev))) {
                                getEditor().drawLines(getEditor().getTreeView().getRoot(), item);
                            }
                        }
                    }

                }

            } else if (ev.getEventType() == DragEvent.DRAG_DROPPED) {
                getEditor().hideDrawShapes();
                //System.err.println("AFTER HIDE");
                //TreeItem<ItemValue> targetItem = getEditor().getTreeCellItem(ev, getEditor().getTreeView().getRoot());
                TreeItem<ItemValue> targetItem = getEditor().getTreeView().getRoot();
                ItemValue targetValue = targetItem.getValue();
                //
                // Transfer the data to the target
                //
                Dragboard dragboard = ev.getDragboard();
                if (dragboard.hasUrl()) {
                    TreeItem target = getEditor().getTreeView().getRoot();
                    //TreeItemBuilder tib = targetValue.getBuilder();
                    targetValue.getBuilder().accept(getEditor().getTreeView(), targetItem, target, (Node) ev.getGestureSource());
                    ev.setDropCompleted(true);

                } else {
                    ev.setDropCompleted(false);
                }

                ev.consume();

            } else if (ev.getEventType() == DragEvent.DRAG_DONE) {
                // Check how the data transfer happened. If it was moved, clear the text in the source.
                //TransferMode modeUsed = ev.getTransferMode();
                getEditor().hideDrawShapes();
                ev.consume();
            }
            //System.err.println("CONSUME");
            ev.consume();
        }
    }//TreeViewDragEventHandler
}// SceneGraphEditor
