package org.vns.javafx.dock.api.editor;

import java.util.List;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableStringValue;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import static org.vns.javafx.dock.api.editor.EditorUtil.screenTreeItemBounds;
import static org.vns.javafx.dock.api.editor.TreeItemBuilder.CELL_UUID;
import static org.vns.javafx.dock.api.editor.TreeItemBuilder.NODE_UUID;

/**
 *
 * @author Valery
 */
public class SceneGraphEditor {

    private static final double LEVEL_SPACE = 15;

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

    private double parentOffset = 10;
    private Bounds disclosureBounds;

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
        editorPane.getStyleClass().add("tree-view-pane");
        //editorPane.setStyle("-fx-background-color: green; -fx-padding: 0 3 0 3; -fx-insets: 0; -fx-border-width: 0");
        TreeView tt = new TreeView() {
            void me() {

            }
        };
        vertLine.getStyleClass().add("tree-view-indicator");
        vertLine.getStyleClass().add("vert-line");
        horLine.getStyleClass().add("hor-line");
        horLine.getStyleClass().add("tree-view-indicator");
        itemRect.getStyleClass().add("tree-view-indicator");
        itemRect.getStyleClass().add("rect");

        //vertLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");
        //horLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");
        //itemRect.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2; -fx-background-color: transparent;-fx-fill: transparent");
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
        TreeItem<ItemValue> it = createSceneGraph(rootNode);
        it.setExpanded(true);
        treeView.setRoot(it);
        Platform.runLater(() -> {
            Node arrow = ((Pane) ((TreeCell) it.getValue().getCellGraphic().getParent()).getDisclosureNode()).getChildren().get(0);
            getItemParentOffset(it);
            getDisclosureBounds();
        });
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
                        //this.applyCss();
                        boolean isRoot = this.getTreeItem() == treeView.getRoot();

                        registerDragDetected(this);
                        registerDragDropped(this);
                        registerDragDone(this);

                        this.setOnMouseClicked(new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent ev) {
                                System.err.println("MOUSE CLICKED");
                                TreeItem<ItemValue> item = ((TreeCell) ev.getSource()).getTreeItem();
                                Object o = item.getValue().getTreeItemObject();
                                System.err.println("start = " + System.currentTimeMillis());
                                if ( ba == null ){
                                    ba = new BeanAdapter(o);
                                    sp = (Property)ba.getPropertyModel("text");
                                    System.err.println("model = " + sp);
                                    if ( ! sp.isBound() ) {
                                        sp.bind(tmpProp);
                                    }
                                    
                                }
                                
                                if (o instanceof Label) {
                                    System.err.println("textProperty=" + ba.get("text"));
                                    //((WritableStringValue)ba.getPropertyModel("text"))
                                    //((Label) o).setText("TEXT CHANGED " + (++tmp));
                                    
                                }
                                tmpProp.set("CHANGED " + (++tmp));
                                System.err.println("end = " + System.currentTimeMillis());
                            }
                        });
                    }
                }
            };
            return cell;
        });
    }
    public static BeanAdapter ba = null;
    public static Property sp = null;
    public static int tmp = 0;
    public static StringProperty tmpProp = new SimpleStringProperty("1");
    
    
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
            Platform.runLater(() -> {
                getItemParentOffset(cell.getTreeItem());
            });

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
        System.err.println("DROPPED");
        cell.setOnDragDropped((DragEvent ev) -> {
            TreeItem<ItemValue> targetItem = getDropTreeItem(ev, ((TreeCell) ev.getGestureTarget()).getTreeItem());
            //
            // Try transfer data to the place
            //
            Dragboard dragboard = ev.getDragboard();
            if (dragboard.hasUrl() && targetItem != null) {
                ItemValue targetValue = targetItem.getValue();
                TreeItem place = ((TreeCell) ev.getGestureTarget()).getTreeItem();
                System.err.println("DROPPED BEFORE ACCEPT");
                targetValue.getBuilder().accept(treeView, targetItem, place, (Node) ev.getGestureSource());

                ev.setDropCompleted(true);
            } else {
                ev.setDropCompleted(false);
            }
            ev.consume();

        });
    }

    protected TreeItem<ItemValue> findFirstVisibleTreeItem(double x, double y) {
        //treeView.getExpandedItemCount()
        TreeItem<ItemValue> retval = null;
        for (int i = 0; i < treeView.getExpandedItemCount(); i++) {
            if (isVisible(treeView.getTreeItem(i))) {
                Node cell = treeView.getTreeItem(i).getValue().getCellGraphic().getParent();

                if (cell.contains(cell.screenToLocal(x, y))) {
                    retval = treeView.getTreeItem(i);
                    break;

                }
            }
        }
        return retval;
    }

    protected TreeItem<ItemValue> findLastVisibleTreeItem(double x, double y) {
        //treeView.getExpandedItemCount()
        TreeItem<ItemValue> retval = null;
        for (int i = treeView.getExpandedItemCount() - 1; i >= 0; i--) {
            if (isVisible(treeView.getTreeItem(i))) {
                Node cell = treeView.getTreeItem(i).getValue().getCellGraphic().getParent();

                if (cell.contains(cell.screenToLocal(x, y))) {
                    retval = treeView.getTreeItem(i);
                    break;

                }
            }
        }
        return retval;
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

    public void expandAllItems(TreeItem item) {
        item.setExpanded(true);
        List<TreeItem> l = item.getChildren();
        for (TreeItem it : l) {
            expandAllItems(it);
        }
    }

    /*    protected void expand(TreeItem item ) {
        item.setExpanded(true);
        List<TreeItem> l = item.getChildren();
        for ( TreeItem it : l ) {
            it.setExpanded(true);
            expand(it);
        }
    }
     */
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
        Bounds lb = EditorUtil.screenTreeItemBounds(to);
        //Bounds rootBounds = EditorUtil.screenNonValueLevelBounds(treeView, treeView.getRoot());
        Bounds rootBounds = screenNonValueLevelBounds(treeView.getRoot(), to);
        treeView.setPadding(Insets.EMPTY);
        Insets pins = treeView.getPadding();

        AnchorPane ap = (AnchorPane) from.getValue().getCellGraphic();

        Pane p = getEditorPane();
        Bounds bnd = null;
        //if (ap.getScene() != null) {
        //bnd = EditorUtil.screenNonValueLevelBounds(treeView, from);
        bnd = screenNonValueLevelBounds(from, to);
        //}

        int level = treeView.getTreeItemLevel(from);

        //double gap = EditorUtil.getRootStartGap(treeView);
        double gap = getItemParentOffset(to);
        //Bounds arrowBnd = EditorUtil.screenArrowBounds(from);
        Bounds arrowBnd = screenArrowBounds(from, to);
        /////
        TreeCell c = ((TreeCell) to.getValue().getCellGraphic().getParent());

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

    public boolean isIndicatorVisible() {
        return itemRect.isVisible() || horLine.isVisible();
    }

    protected double rootNonValueWidth(TreeItem<ItemValue> sample) {
        int level = treeView.getTreeItemLevel(sample);
        double cellOffset = getItemParentOffset(sample);
        Bounds sampleBounds = screenNonValueBounds(sample);
        return sampleBounds.getWidth() - cellOffset * level;
    }

    protected boolean isVisible(TreeItem<ItemValue> item) {

        boolean retval = false;
        Node g = item.getValue().getCellGraphic();
        if (g.getScene() != null && g.getScene().getWindow() != null) {
            retval = true;
        }
        return retval;
    }

    public Bounds getDisclosureBounds() {
        TreeItem<ItemValue> root = treeView.getRoot();
        boolean b = false;
        if (!isVisible(root)) {
            return disclosureBounds;
        }
        TreeCell c = EditorUtil.getCell(root);
        if (c.getDisclosureNode() == null || ((Pane) c.getDisclosureNode()).getChildren().isEmpty()) {
            return disclosureBounds;
        }
        Node dn = ((Pane) c.getDisclosureNode()).getChildren().get(0);
        disclosureBounds = c.screenToLocal(dn.localToScreen(dn.getBoundsInLocal()));
        return disclosureBounds;
    }

    private double getOffset(TreeItem<ItemValue> item1, TreeItem<ItemValue> item2) {
        double x1 = item1.getValue().getCellGraphic().getBoundsInParent().getWidth();
        double x2 = item2.getValue().getCellGraphic().getBoundsInParent().getWidth();

        return x1 - x2 < 0 ? x2 - x1 : x1 - x2;

    }

    public double getItemParentOffset(TreeItem<ItemValue> item) {
        if (isVisible(item) && !item.getChildren().isEmpty() && isVisible(item.getChildren().get(0))) {
            parentOffset = getOffset(item, item.getChildren().get(0));
        } else if (isVisible(item) && item.getParent() != null && isVisible(item.getParent())) {
            parentOffset = getOffset(item, item.getParent());
        }
        return parentOffset;
    }

    protected Bounds screenArrowBounds(TreeItem<ItemValue> item, TreeItem<ItemValue> to) {
        Bounds b = getDisclosureBounds();
        int dif = treeView.getTreeItemLevel(item);
        double cellOffset = getItemParentOffset(item);

        double y = treeView.localToScreen(treeView.getBoundsInLocal()).getMinY();
        TreeCell c = EditorUtil.getCell(to);
        Bounds db = c.localToScreen(b);
        double x = db.getMinX();

        if (isVisible(item)) {
            y = EditorUtil.screenTreeItemBounds(item).getMinY();
            y += b.getMinY();
            x = EditorUtil.screenTreeItemBounds(item).getMinX();
            x += b.getMinX();
        }
        return new BoundingBox(x + dif * cellOffset, y, b.getWidth(), b.getHeight());
    }

    protected Bounds screenNonValueBounds(TreeItem<ItemValue> item) {

        Bounds vBnd = screenValueBounds(item);
        Bounds itBnd = screenTreeItemBounds(item);
        if (itBnd == null) {
            return null;
        }
        return new BoundingBox(itBnd.getMinX(), itBnd.getMinY(), itBnd.getWidth() - vBnd.getWidth(), itBnd.getHeight());
    }

    protected Bounds screenNonValueLevelBounds(TreeItem<ItemValue> item, TreeItem<ItemValue> sample) {
        Bounds sampleBnd = screenNonValueBounds(sample);

        int level = treeView.getTreeItemLevel(item);
        double gap = getItemParentOffset(sample);

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

        double y = treeView.localToScreen(treeView.getBoundsInLocal()).getMinY();

        if (item.getValue().getCellGraphic().getScene() != null) {
            y = screenValueBounds(item).getMinY();
        }
        Bounds nvBnd = new BoundingBox(sampleBnd.getMinX() + xOffset, y, w, sampleBnd.getHeight());
        return nvBnd;
    }

    private Bounds screenValueBounds(TreeItem<ItemValue> item) {
        AnchorPane ap = (AnchorPane) item.getValue().getCellGraphic();
        Bounds apBounds = ap.localToScreen(ap.getBoundsInLocal());
        if (apBounds == null) {
            return null;
        }
        Bounds cellBounds = screenTreeItemBounds(item);
        if (cellBounds == null) {
            return null;
        }

        double height = cellBounds.getHeight();
        double width = cellBounds.getMinX() + cellBounds.getWidth() - apBounds.getMinX();

        return new BoundingBox(apBounds.getMinX(), cellBounds.getMinY(), width, height);
    }

    protected Bounds screenValueBounds(TreeItem<ItemValue> item, TreeItem<ItemValue> sample) {
        AnchorPane ap = (AnchorPane) sample.getValue().getCellGraphic();
        if (ap.getScene() == null) {
            return null;
        }
        Bounds apBounds = ap.localToScreen(ap.getBoundsInLocal());
        Bounds cellBounds = null;
        int dif = treeView.getTreeItemLevel(item) - treeView.getTreeItemLevel(sample);
        double cellOffset = getItemParentOffset(sample);

        if (item.getValue().getCellGraphic().getScene() != null) {
            cellBounds = screenTreeItemBounds(item);
        } else {
            cellBounds = screenTreeItemBounds(sample);
        }
        double height = cellBounds.getHeight();
        double width = cellBounds.getMinX() + cellBounds.getWidth() - apBounds.getMinX() - dif * cellOffset;

        return new BoundingBox(apBounds.getMinX() - dif * cellOffset, treeView.localToScreen(treeView.getBoundsInLocal()).getMinY(), width, height);
    }

    public Bounds[] levelBoundsOf(TreeItem<ItemValue> item) {
        int level = treeView.getTreeItemLevel(item);
        double cellOffset = getItemParentOffset(item);
        Bounds[] bounds = new Bounds[level + 3];
        Bounds itemBounds = screenNonValueBounds(item);
        Bounds valueBounds = screenValueBounds(item);

        Bounds rootBounds = new BoundingBox(0, 0, (itemBounds.getWidth() - cellOffset * (level)), itemBounds.getHeight());
        double xOffset;
        double width;

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

    protected TreeItem<ItemValue> getDropTreeItem(DragEvent ev, TreeItem<ItemValue> item) {

        TreeItem<ItemValue> retval = null;
        //Object o = item.getValue().getTreeItemObject();
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
            if (n < 0) {
                retval = item;
                ((ItemValue) retval.getValue()).setDragDropQualifier(LAST);
            } else if (item.isLeaf()) {
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
                    retval = item.getParent();
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        retval = parentAtLevel(item, n);
                    } else {
                        retval = item.getParent();
                    }
                }
            } else if (!item.isExpanded()) {
                // not leaf and not expanded     
                if (n == level || n == level + 1 || n == level + 2) {
                    retval = item;
                } else if (n == level - 1) {
                    retval = item.getParent();
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        retval = parentAtLevel(item, n);
                    } else {
                        retval = item.getParent();
                    }
                }
            } else {
                retval = item;
                ((ItemValue) item.getValue()).setDragDropQualifier(FIRST);
            }
        }
        return retval;
    }

    protected void treeItemDragOver(DragEvent ev, TreeItem<ItemValue> item, boolean acceptable) {
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
            if (n < 0 && !acceptable) {
                return;
            } else if (n < 0) {
                itemRect.setVisible(true);
                drawRectangle(item);
            } else if (item.isLeaf()) {
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
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
                        itemRect.setVisible(true);
                        drawRectangle(item);
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

    protected void treeItemDragOver(DragEvent ev, TreeItem<ItemValue> item) {
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
            if (n < 0) {
                itemRect.setVisible(true);
                //if (true) return;
                drawRectangle(item);
            } else if (item.isLeaf()) {
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
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
                    itemRect.setVisible(true);
                    drawRectangle(item);
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

        public SceneGraphEditor getEditor() {
            return editor;
        }

        public TreeCell getTargetCell() {
            return targetCell;
        }

        public TreeItem<ItemValue> getTreeCellItem() {
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
        }

        /*        protected boolean isAcceptable(DragEvent ev, DefaultTreeItemBuilder builder) {
            Dragboard dragboard = ev.getDragboard();
            Object dragSource = getDragSource(ev);
            TreeItem it = getTreeCellItem();
            return (dragboard.hasUrl() && (dragboard.getUrl().equals(NODE_UUID) || dragboard.getUrl().equals(CELL_UUID)))
                    && builder != null
                    && builder.isDragTarget()
                    && builder.isAcceptable(getTreeCellItem(), dragSource);
        }
         */
 /*        protected boolean isSupportedDragSource(DragEvent ev, DefaultTreeItemBuilder builder) {
            Dragboard dragboard = ev.getDragboard();
            Object dragSource = getDragSource(ev);
            return (dragSource != null && dragboard.hasUrl()
                    && (dragboard.getUrl().equals(NODE_UUID)
                    || dragboard.getUrl().equals(CELL_UUID))
                    && builder != null);
        }
         */
        protected boolean isSupportedDragSource(DragEvent ev) {
            Dragboard dragboard = ev.getDragboard();
            Object dragSource = getDragSource(ev);
            return dragSource != null && dragboard.hasUrl()
                    && (dragboard.getUrl().equals(NODE_UUID)
                    || dragboard.getUrl().equals(CELL_UUID));
            //&& builder != null);
        }

        @Override
        public void handle(DragEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static class TreeItemCellDragEventHandler extends DragEventHandler {

        PauseTransition pt2;
        double sX = -1000;
        double sY = -1000;
        long sTime = System.currentTimeMillis();

        public TreeItemCellDragEventHandler(SceneGraphEditor editor, TreeCell targetCell) {
            super(editor, targetCell);
            PauseTransition pt2 = new PauseTransition(Duration.seconds(5));
        }

        @Override
        public void handle(DragEvent ev
        ) {
            if (ev.getEventType() == DragEvent.DRAG_OVER) {
                TreeView tv = getEditor().getTreeView();
                Bounds tvb = tv.localToScreen(tv.getBoundsInLocal());
                if (ev.getScreenY() < tvb.getMinY() + 15) {
                    return;
                } else if (ev.getScreenY() > tvb.getMinY() + tv.getHeight() - 15) {
                    return;
                }
                //
                // getDragTargetObject(ev) returns an object that corresponds the 
                // current mouse pos and not the actual place
                //
                getEditor().hideDrawShapes();
                if (!isAdmissiblePosition(ev)) {
                    ev.consume();
                } else {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(getPoint())) {
                        getEditor().treeItemDragOver(ev, (TreeItem) getTargetCell().getTreeItem());
                    }
                    ev.consume();
                }
            }
        }

        public boolean isAdmissiblePosition(DragEvent ev) {
            boolean retval = true;
            if (!isSupportedDragSource(ev)) {
                return false;
            }

            TreeItem<ItemValue> target = getEditor().getDropTreeItem(ev, getTreeCellItem());
            if (target == null) {
                return false;
            }
            //
            // Delegate to check if admissible to a DefaultTreeItemBuilder of the target item
            //
            retval = target.getValue().getBuilder().isAdmissiblePosition(getEditor().getTreeView(), target, getTargetCell().getTreeItem(), getDragSource(ev));
            return retval;
        }
    }//MouseEventHandler

    public static class TreeViewDragEventHandler extends DragEventHandler {

        public TreeViewDragEventHandler(SceneGraphEditor editor) {
            super(editor, null);
        }

        /*        @Override
        protected Object getDragTargetObject(DragEvent ev) {
            return ((ItemValue) getTreeCellItem().getValue()).getTreeItemObject();
        }
         */
        /**
         *
         * @return
         */
        @Override
        public TreeItem getTreeCellItem() {
            int sz = getEditor().getTreeView().getExpandedItemCount();
            return getEditor().getTreeView().getTreeItem(sz - 1);
        }
        double sX = -1000;
        double sY = -1000;
        long sTime = System.currentTimeMillis();

        @Override
        public void handle(DragEvent ev) {
            if (ev.getEventType() == DragEvent.DRAG_OVER) {

                TreeView tv = getEditor().getTreeView();

                Bounds tvb = tv.localToScreen(tv.getBoundsInLocal());
                if (ev.getScreenY() < tvb.getMinY() + 15) {
                    long t = System.currentTimeMillis();
                    if (ev.getScreenX() == sX && ev.getScreenY() == sY && t - sTime > 75) {

                        sTime = System.currentTimeMillis();
                        TreeItem<ItemValue> first = getEditor().findFirstVisibleTreeItem(ev.getScreenX(), ev.getScreenY());
                        if (first != null && tv.getRow(first) > 0) {
                            tv.scrollTo(tv.getRow(first) - 1);
                        }
                    } else if (ev.getScreenX() != sX || ev.getScreenY() != sY) {
                        sX = ev.getScreenX();
                        sY = ev.getScreenY();
                        sTime = System.currentTimeMillis();
                    }

                    //ev.consume();
                    return;
                } else if (ev.getScreenY() > tvb.getMinY() + tv.getHeight() - 15) {
                    long t = System.currentTimeMillis();
                    if (ev.getScreenX() == sX && ev.getScreenY() == sY && t - sTime > 75) {

                        sTime = System.currentTimeMillis();
                        TreeItem<ItemValue> last = getEditor().findLastVisibleTreeItem(ev.getScreenX(), ev.getScreenY());
                        if (last != null && tv.getRow(last) < tv.getExpandedItemCount()) {
                            tv.scrollTo(tv.getRow(last) + 1);
                        }
                    } else if (ev.getScreenX() != sX || ev.getScreenY() != sY) {
                        sX = ev.getScreenX();
                        sY = ev.getScreenY();
                        sTime = System.currentTimeMillis();
                    }

                    //ev.consume();
                    return;
                }

                TreeItemBuilder builder;// = TreeItemRegistry.getInstance().getBuilder(getDragTargetObject(ev));
                getEditor().hideDrawShapes();
                ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                if (!pt.equals(getPoint())) {
                    TreeItem item = getTreeCellItem();
                    if (item != null) {
                        TreeItem<ItemValue> it = getEditor().getTreeView().getRoot();
                        builder = TreeItemRegistry.getInstance().getBuilder(it.getValue().getTreeItemObject());
                        //if (builder.isAcceptable(it, getDragSource(ev))) {
                        getEditor().drawLines(getEditor().getTreeView().getRoot(), item);
                        //}
                    }
                }

                //}
            } else if (ev.getEventType() == DragEvent.DRAG_DROPPED) {
                getEditor().hideDrawShapes();
                TreeItem<ItemValue> targetItem = getEditor().getTreeView().getRoot();
                ItemValue targetValue = targetItem.getValue();
                //
                // Transfer the data to the place
                //
                Dragboard dragboard = ev.getDragboard();
                if (dragboard.hasUrl()) {
                    TreeItem target = getEditor().getTreeView().getRoot();
                    targetValue.getBuilder().accept(getEditor().getTreeView(), targetItem, target, (Node) ev.getGestureSource());
                    ev.setDropCompleted(true);

                } else {
                    ev.setDropCompleted(false);
                }

                //ev.consume();
            } else if (ev.getEventType() == DragEvent.DRAG_DONE) {
                getEditor().hideDrawShapes();
                //ev.consume();
            }
            ev.consume();
        }
    }//TreeViewDragEventHandler
}// SceneGraphEditor
