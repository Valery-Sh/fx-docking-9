package org.vns.javafx.designer;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import static org.vns.javafx.designer.EditorUtil.screenTreeItemBounds;
import static org.vns.javafx.designer.SceneGraphView.ANCHOR_OFFSET;
import static org.vns.javafx.designer.SceneGraphView.FIRST;
import static org.vns.javafx.designer.SceneGraphView.LAST;

/**
 *
 * @author Valery
 */
public class DragIndicator {

    private static final double LEVEL_SPACE = 15;

    private final SceneGraphView sceneGraphView;
    private final TreeViewEx<ItemValue> treeView;

    //private double parentOffset = 10;
    //private Bounds disclosureBounds;
    private final Line vertLine = new Line();
    private final Line horLine = new Line();
    private final Rectangle itemRect = new Rectangle();

    private final Pane indicatorPane = new Pane();

    public DragIndicator(SceneGraphView sceneGraphView) {
        this.sceneGraphView = sceneGraphView;
        this.treeView = (TreeViewEx<ItemValue>) sceneGraphView.getTreeView();
    }

    protected TreeItemEx findTreeItem(TreeItemEx item, Object sourceGesture) {
        TreeItem retval = null;
        for (TreeItem<ItemValue> it : item.getChildren()) {
            
            if (it.getValue().getTreeItemObject() == sourceGesture) {
                retval = it;
                break;
            }
            retval = findTreeItem((TreeItemEx) it, sourceGesture);
            if (retval != null) {
                break;
            }
        }
        return (TreeItemEx) retval;
    }

    public void initIndicatorPane() {
        indicatorPane.setMouseTransparent(true);
        indicatorPane.getChildren().addAll(horLine, vertLine, itemRect);
        sceneGraphView.getTreeViewPane().getChildren().add(indicatorPane);
        indicatorPane.toFront();

        vertLine.getStyleClass().add("tree-view-indicator");
        vertLine.getStyleClass().add("vert-line");
        horLine.getStyleClass().add("hor-line");
        horLine.getStyleClass().add("tree-view-indicator");
        itemRect.getStyleClass().add("tree-view-indicator");
        itemRect.getStyleClass().add("rect");

        vertLine.setMouseTransparent(true);
        horLine.setMouseTransparent(true);
        itemRect.setMouseTransparent(true);

    }

    protected Bounds getDisclosureBounds(TreeItemEx item) {

        TreeCell c = EditorUtil.getCell(item);
        //
        // Get StackPane as parent of disclousureArrow
        //
        Region dn = (Region) ((Pane) c.getDisclosureNode()).getChildren().get(0);

        if (dn.getHeight() <= 0) {
            dn = (Region) c.getDisclosureNode();
        }
        if (dn.localToScreen(dn.getBoundsInLocal()) != null) {
            return c.screenToLocal(dn.localToScreen(dn.getBoundsInLocal()));
        } else {
            return new BoundingBox(0, 0, 0, 0);
        }
    }

    /*    protected Bounds getDisclosureBounds() {
        TreeItem<ItemValue> root = treeView.getRoot();
        boolean b = false;
        if (!isVisibleOnTop(root)) {
            return disclosureBounds;
        }
        TreeCell c = EditorUtil.getCell(root);
        if (c.getDisclosureNode() == null || ((Pane) c.getDisclosureNode()).getChildren().isEmpty()) {
            System.err.println("EMPTY !!!!!!!!!!!!!!!!!!!!!!");

            return disclosureBounds;
        }
        Node dn = ((Pane) c.getDisclosureNode()).getChildren().get(0);
        //Node dn = ((Pane) c.getDisclosureNode());
        disclosureBounds = c.screenToLocal(dn.localToScreen(dn.getBoundsInLocal()));

        return disclosureBounds;
    }
     */
    protected void setDisclosureBounds(TreeCell cell) {
        Pane d = (Pane) cell.getDisclosureNode();

        if (d.getChildren().isEmpty()) {
            return;
        }
        Node dn = d.getChildren().get(0);
        //disclosureBounds = cell.screenToLocal(dn.localToScreen(dn.getBoundsInLocal()));
    }

    private double getOffset(TreeItemEx item1, TreeItemEx item2) {
        double x1 = item1.getValue().getCellGraphic().getBoundsInParent().getWidth();
        double x2 = item2.getValue().getCellGraphic().getBoundsInParent().getWidth();
        return x1 - x2 < 0 ? x2 - x1 : x1 - x2;
    }

    protected double getItemParentOffset(TreeItemEx item) {
        double parentOffset = 10;
        int level = treeView.getTreeItemLevel(item);
        if (isVisibleOnTop(item) && !item.getChildren().isEmpty() && isVisibleOnTop((TreeItemEx) item.getChildren().get(0))) {
            parentOffset = getOffset(item, (TreeItemEx) item.getChildren().get(0));
        } else if (isVisibleOnTop(item) && item.getParent() != null && isVisibleOnTop((TreeItemEx) item.getParent())) {
            parentOffset = getOffset(item, (TreeItemEx)item.getParent());
        } else if (isVisibleOnTop(item) && item.getParent() != null && level > 0) {
            parentOffset = screenNonValueBounds(item).getWidth() / (level + 1);
        }
        return parentOffset;
    }

    protected Bounds screenArrowBounds(TreeItemEx from, TreeItemEx to) {
        TreeItemEx item = isVisibleOnTop(from) ? from : to;
        //System.err.println("screenArrowBounds item obj = " + item.getValue().getTreeItemObject());
        Bounds b = getDisclosureBounds(item);
        //System.err.println("getDisclosureBounds(item) = " + getDisclosureBounds(item));

        int dif = treeView.getTreeItemLevel(to) - treeView.getTreeItemLevel(from);
        double cellOffset = getItemParentOffset(item);

        double y = treeView.localToScreen(treeView.getBoundsInLocal()).getMinY();
       // System.err.println("screenArrowBounds y = " + y);
        TreeCell c = EditorUtil.getCell(to);
        Bounds db = c.localToScreen(b);
        double x = db.getMinX();

        if (isVisibleOnTop(from)) {
            //System.err.println("screenArrowBounds 1 ");
            y = EditorUtil.screenTreeItemBounds(from).getMinY();
            y += b.getMinY();
            x = EditorUtil.screenTreeItemBounds(from).getMinX();
            x += b.getMinX();
            //System.err.println("screenArrowBounds 1 y=" + y);
            cellOffset = 0;
        } else {
            cellOffset = -cellOffset;
            //System.err.println("screenArrowBounds 2 y=" + y);

        }
        return new BoundingBox(x + dif * cellOffset, y, b.getWidth(), b.getHeight());
    }

    protected Bounds screenNonValueBounds(TreeItemEx item) {

        Bounds vBnd = screenValueBounds(item);
        Bounds itBnd = screenTreeItemBounds(item);
        if (itBnd == null) {
            return null;
        }
        return new BoundingBox(itBnd.getMinX(), itBnd.getMinY(), itBnd.getWidth() - vBnd.getWidth(), itBnd.getHeight());
    }

    protected Bounds screenNonValueLevelBounds(TreeItemEx item, TreeItemEx sample) {
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

        //if (item.getValue().getCellGraphic().getScene() != null) {
        if (isVisibleOnTop(item)) {
            y = screenValueBounds(item).getMinY();
            //System.err.println("visible y=" + y);
        }
        //System.err.println(" --   y=" + y);
        Bounds nvBnd = new BoundingBox(sampleBnd.getMinX() + xOffset, y, w, sampleBnd.getHeight());
        return nvBnd;
    }

    private Bounds screenValueBounds(TreeItemEx item) {
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

    protected Bounds screenValueBounds(TreeItemEx item, TreeItemEx sample) {
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

    protected Bounds[] levelBoundsOf(TreeItemEx item) {
        int level = treeView.getTreeItemLevel(item);
        double cellOffset = getItemParentOffset(item);
        Bounds[] bounds = new Bounds[level + 3];
        Bounds itemBounds = screenNonValueBounds(item);
//        System.err.println(" levelBoundsOf itemBounds = " + itemBounds);        
        Bounds valueBounds = screenValueBounds(item);
//        System.err.println("levelBoundsOf item = " + item.getValue().getTreeItemObject());
        Bounds rootBounds = new BoundingBox(0, 0, (itemBounds.getWidth() - cellOffset * (level)), itemBounds.getHeight());
//        System.err.println(" levelBoundsOf rootBounds = " + rootBounds);        
//        System.err.println(" levelBoundsOf cellOffset = " + cellOffset);        
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
//        for ( int i=0; i < bounds.length; i++)
//            System.err.println("i=  " + i +") levelBoundsOf " + bounds[i]);
        return bounds;
    }

    /*    protected Bounds levelBoundsOf(TreeItem<ItemValue> item, int forLevel) {
        int level = treeView.getTreeItemLevel(item);
        double cellOffset = getItemParentOffset(item);
        Bounds bounds;
        Bounds itemBounds = screenNonValueBounds(item);
        //Bounds valueBounds = screenValueBounds(item);

        Bounds rootBounds = new BoundingBox(0, 0, (itemBounds.getWidth() - cellOffset * (level)), itemBounds.getHeight());
        double xOffset;
        double width;

        //  for (int i = 0; i <= level; i++) {
        if (forLevel == 0) {
            xOffset = 0;
            width = rootBounds.getWidth() / 2;
        } else {
            xOffset = rootBounds.getWidth() / 2 + cellOffset * (forLevel - 1);
            width = cellOffset;
        }
        System.err.println("levelBoundsOf xOffset = " + xOffset);
        bounds = new BoundingBox(itemBounds.getMinX() + xOffset, itemBounds.getMinY() + itemBounds.getHeight() - LEVEL_SPACE, width, LEVEL_SPACE);

        return bounds;
    }
     */
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

    protected TreeItem getParentTarget(TreeItemEx item, int targetLevel) {
        TreeItem<ItemValue> retval = null;
        TreeItem<ItemValue> it = item;
        int n = targetLevel;
        int row = treeView.getRow(it);
        TreeItem<ItemValue> next = treeView.getTreeItem(row + 1);
        int nextRowLevel = 0;
        if (next != null) {
            nextRowLevel = treeView.getTreeItemLevel(treeView.getTreeItem(row + 1));
        } else {
            retval = parentAtLevel(item, n);
            return parentAtLevel(item, n);
        }

        while (true) {
            if (row == treeView.getExpandedItemCount() - 1 || nextRowLevel <= n + 1) {
                retval = parentAtLevel(item, n);
                break;
            }
            n++;
            nextRowLevel = n + 1;
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

    protected void drawRectangle(TreeItemEx item) {
        hideDrawShapes();
        Bounds lb = EditorUtil.screenTreeItemBounds(item);
        if (lb == null) {
            return;
        }

        lb = sceneGraphView.getTreeViewPane().screenToLocal(lb);

        itemRect.setX(lb.getMinX());
        itemRect.setY(lb.getMinY());
        itemRect.setWidth(lb.getWidth());
        itemRect.setHeight(lb.getHeight());

        itemRect.toFront();
        itemRect.setVisible(true);
    }

    protected void drawLines(TreeItemEx from, TreeItemEx to) {
        //Bounds lb = EditorUtil.screenTreeItemBounds(to);
        Bounds lb = EditorUtil.screenHorVisibleBounds(treeView,to);
//        System.err.println("is HBar  Visible " + treeView.getHScrollBar().isVisible());
        if ( treeView.getHScrollBar().isVisible()) {
//            System.err.println("HBar ist Visible");
        }
//        System.err.println("drawLinew lb=" + lb);
//        System.err.println("drawLinew treeView = " + treeView.localToScreen(treeView.getBoundsInLocal()));
        Bounds rootBounds = screenNonValueLevelBounds((TreeItemEx) treeView.getRoot(), to);

        Insets pins = treeView.getInsets();

        //AnchorPane ap = (AnchorPane) from.getValue().getCellGraphic();
        Pane p = sceneGraphView.getTreeViewPane();
        Bounds bnd = null;

        bnd = screenNonValueLevelBounds(from, to);
//        System.err.println("drawLines bnd = " + bnd);
        int level = treeView.getTreeItemLevel(from);
        double gap = getItemParentOffset(to);
//        System.err.println("drawLines gap = " + gap + "; level = " + level);

        Bounds arrowBnd = screenArrowBounds(from, to);
//        System.err.println("drawLines arrowBnd = " + arrowBnd);

        //double startY = bnd.getMinY() + bnd.getHeight();
        double startY = bnd.getMinY();
        if (!isVisibleOnTop(from)) {
            startY += pins.getTop();
        }
        //System.err.println("!!!!! " + bnd + "; insets=" + pins);
        //System.err.println("   ---  " + );

        if (arrowBnd.getHeight() != 0) {
            //System.err.println(" height > 0 !!!! ");
            startY = arrowBnd.getMinY();// + arrowBnd.getHeight();
            if (!isVisibleOnTop(from)) {
                startY += pins.getTop();
            } else {
                startY += arrowBnd.getHeight();
            }

        }

        double startX = rootBounds.getMinX() + rootBounds.getWidth() + gap * level;

        if (arrowBnd.getWidth() != 0) {
            startX = arrowBnd.getMinX() + arrowBnd.getWidth() / 2;
        }

        vertLine.setStartX(p.screenToLocal(startX, startY).getX());
        vertLine.setStartY(p.screenToLocal(startX, startY).getY());
//        System.err.println("vertLine X=" + p.screenToLocal(startX, startY).getX());
        vertLine.setEndX(vertLine.getStartX());
//        System.err.println("vertLine end X=" + vertLine.getStartX());
        hideDrawShapes();
        vertLine.toFront();
        vertLine.setVisible(true);
        //
        // --- Horizontal line ----------
        //

        //lb = sceneGraphView.getTreeViewPane().screenToLocal(lb);
        lb = treeView.screenToLocal(lb);
        horLine.setStartX(lb.getMinX());
        horLine.setStartY(lb.getMinY() + lb.getHeight());
        horLine.setEndY(horLine.getStartY());
        horLine.setEndX(horLine.getStartX() + lb.getWidth());

        vertLine.setEndY(horLine.getStartY());
//        System.err.println("vertLine Y=" + vertLine.getStartY());
//        System.err.println("vertLine end Y=" + vertLine.getEndY());

        horLine.toFront();
        horLine.setVisible(true);

    }

    protected double rootNonValueWidth(TreeItemEx sample) {
        int level = treeView.getTreeItemLevel(sample);
        double cellOffset = getItemParentOffset(sample);
        Bounds sampleBounds = screenNonValueBounds(sample);
        return sampleBounds.getWidth() - cellOffset * level;
    }

    public boolean isVisibleOnTop(TreeItemEx item) {
        TreeCell cell = (TreeCell) item.getValue().getCellGraphic().getParent();
        if (cell == null || !(cell.getScene() != null && cell.getScene().getWindow() != null)) {
            return false;
        }
        Bounds noInsBounds = EditorUtil.screenInsetsFreeBounds(treeView);
        Node g  = item.getValue().getCellGraphic().getParent();
        Bounds cellBounds = g.localToScreen(g.getBoundsInLocal());
/*
        Bounds b = item.getValue().getCellGraphic().getBoundsInParent();
        b = treeView.screenToLocal(cell.localToScreen(b));
        
        boolean v2 = noInsBounds.intersects(EditorUtil.translate(cellBounds, 0, -3));
        boolean v1 = (b.getMinY() - treeView.getInsets().getTop() + b.getHeight() - 3 > 0);
        //boolean v2 = b2.contains(b1.getMinX(), b1.getMinY() + b1.getHeight() + 3);

        System.out.println("    --- v1 = " + (v1));
        System.out.println("    --- v2 = " + (v2));
        if ( v1 != v2 ) {
            System.err.println("V1 != V2 !!!!!!!!!!!!!!!!!!!!");
        }
*/
        return noInsBounds.intersects(EditorUtil.translate(cellBounds, 0, -3));
        //return (b.getMinY() - treeView.getInsets().getTop() + b.getHeight() - 3 > 0);
    }


    protected void printBounds(TreeItemEx item) {
        boolean retval = false;
        TreeCell cell = (TreeCell) item.getValue().getCellGraphic().getParent();

        if (cell == null || !(cell.getScene() != null && cell.getScene().getWindow() != null)) {
            System.err.println("printBounds =  NULL");
        }
        System.err.println("cell.bounds     = " + cell.getBoundsInLocal());
        System.err.println("treeView.bounds = " + treeView.getBoundsInLocal());
        System.err.println("cell.boundsInParent    = " + cell.getBoundsInParent());
        System.err.println("treeView.boundsParent  = " + treeView.getBoundsInParent());

        treeView.intersects(cell.getBoundsInLocal());
    }

    
    protected TreeItemEx getTargetTreeItem(double x, double y, TreeItemEx item) {

        TreeItemEx retval = null;

        hideDrawShapes();
        if (item != null) {
            Bounds[] bounds = levelBoundsOf(item);
            int n = -1;
            for (int i = 0; i < bounds.length; i++) {
                if (bounds[i].contains(x, y)) {
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
                    retval = (TreeItemEx) item.getParent();
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        retval = (TreeItemEx) getParentTarget(item, n);
                    } else {
                        retval = (TreeItemEx) item.getParent();
                    }
                }
            } else if (!item.isExpanded()) {
                // not leaf and not expanded     
                if (n == level || n == level + 1 || n == level + 2) {
                    retval = item;
                } else if (n == level - 1) {
                    retval = (TreeItemEx) item.getParent();
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        retval = (TreeItemEx) parentAtLevel(item, n);
                    } else {
                        retval = (TreeItemEx) item.getParent();
                    }
                }
            } else {
                retval = item;
                ((ItemValue) item.getValue()).setDragDropQualifier(FIRST);
            }
        }
        return retval;
    }

}
