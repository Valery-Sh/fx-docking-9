/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.editor;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import static org.vns.javafx.dock.api.editor.EditorUtil.screenTreeItemBounds;
import static org.vns.javafx.dock.api.editor.SceneGraphView.ANCHOR_OFFSET;

/**
 *
 * @author Valery
 */
public class DragIndicator {
    private static final double LEVEL_SPACE = 15;

    
    private final SceneGraphView sceneGraphView;
    private final TreeViewEx<ItemValue> treeView;
    
    private double parentOffset = 10;
    private Bounds disclosureBounds;
    private final Line vertLine = new Line();
    private final Line horLine = new Line();
    private final Rectangle itemRect = new Rectangle();
    private DragIndicator dragIndicator;
    
    private final Pane indicatorPane = new Pane();    
    
    public DragIndicator(SceneGraphView sceneGraphView) {
        this.sceneGraphView = sceneGraphView;
        this.treeView = (TreeViewEx<ItemValue>) sceneGraphView.getTreeView();
    }
/*    protected TreeItem<ItemValue> findTreeItem(Object sourceGesture) {
        return findTreeItem(treeView.getRoot(), sourceGesture);
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
*/
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
    protected Bounds getDisclosureBounds() {
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

    protected double getItemParentOffset(TreeItem<ItemValue> item) {
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
            System.err.println("screenNonValueBounds NULL item obj = " + item.getValue().getTreeItemObject());
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

    protected Bounds[] levelBoundsOf(TreeItem<ItemValue> item) {
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

    protected TreeItem getParentTarget(TreeItem item, int targetLevel) {
        TreeItem<ItemValue> retval = null;
        //int level = treeView.getTreeItemLevel(item);
        //
        // n < level - 1
        //
        TreeItem<ItemValue> it = item;
        int n = targetLevel;
        int row = treeView.getRow(it);

        //System.err.println("exp item count = " + treeView.getExpandedItemCount());
        //System.err.println("n = " + n + "    --- lev = " + treeView.getTreeItemLevel(treeView.getTreeItem(row + 1)) );
        TreeItem<ItemValue> next = treeView.getTreeItem(row + 1);
        int nextRowLevel = 0;
        if (next != null) {
            //System.err.println("next obj = " + next.getValue().getTreeItemObject());
            nextRowLevel = treeView.getTreeItemLevel(treeView.getTreeItem(row + 1));
        } else {
            //System.err.println(" set to n = " + n);
            retval = parentAtLevel(item, n);
            //System.err.println("RETVAL obj=" + retval.getValue().getTreeItemObject());
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
    
    protected void drawRectangle(TreeItem item) {
        hideDrawShapes();
        Bounds lb = EditorUtil.screenTreeItemBounds(item);
        //System.err.println("LB = " + lb);
        if (lb == null) {
            return;
        }

        lb = sceneGraphView.getTreeViewPane().screenToLocal(lb);
        //System.err.println("LB1 = " + lb);

        itemRect.setX(lb.getMinX());
        itemRect.setY(lb.getMinY());
        itemRect.setWidth(lb.getWidth());
        itemRect.setHeight(lb.getHeight());

        //System.err.println("itemrect X=" + itemRect.getX() + "; Y=" + itemRect.getY());
        itemRect.toFront();
        itemRect.setVisible(true);
        //StackPane.setAlignment(rootNode, Pos.CENTER);

    }

    protected void drawLines(TreeItem<ItemValue> from, TreeItem<ItemValue> to) {
        System.err.println("DRAW LINES");
        Bounds lb = EditorUtil.screenTreeItemBounds(to);
        //Bounds rootBounds = EditorUtil.screenNonValueLevelBounds(treeView, treeView.getRoot());
        Bounds rootBounds = screenNonValueLevelBounds(treeView.getRoot(), to);
        treeView.setPadding(Insets.EMPTY);
        Insets pins = treeView.getPadding();

        AnchorPane ap = (AnchorPane) from.getValue().getCellGraphic();

        Pane p = sceneGraphView.getTreeViewPane();
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

        lb = sceneGraphView.getTreeViewPane().screenToLocal(lb);
        horLine.setStartX(lb.getMinX());
        horLine.setStartY(lb.getMinY() + lb.getHeight());
        horLine.setEndY(horLine.getStartY());
        horLine.setEndX(horLine.getStartX() + lb.getWidth());

        vertLine.setEndY(horLine.getStartY());

        horLine.toFront();
        horLine.setVisible(true);

    }

    protected double rootNonValueWidth(TreeItem<ItemValue> sample) {
        int level = treeView.getTreeItemLevel(sample);
        double cellOffset = getItemParentOffset(sample);
        Bounds sampleBounds = screenNonValueBounds(sample);
        System.err.println("rootNonValueWidth sample obj =  " + sample.getValue().getTreeItemObject() );
        System.err.println("rootNonValueWidth sampleBounds =  " + sampleBounds );
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
    
}
