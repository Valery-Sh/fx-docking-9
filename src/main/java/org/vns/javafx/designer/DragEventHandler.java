package org.vns.javafx.designer;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import static org.vns.javafx.designer.EditorUtil.DRAGBOARD_KEY;
import static org.vns.javafx.designer.TreeItemBuilder.CELL_UUID;
import static org.vns.javafx.designer.TreeItemBuilder.NODE_UUID;

/**
 * This is the base class intended for implementing the handlers of the event
 * {@code DragEvent}.
 *
 *
 * @author Valery
 */
public abstract class DragEventHandler implements EventHandler<DragEvent> {

    private final SceneGraphView editor;
    private final TreeCell dragTargetCell;

    /**
     * Creates a new instance of the class for the specified object of type
     * {@link SceneGraphView} and an instance of {@code TreeCell}.
     *
     * @param editor the object of type SceneGraphEdotor
     * @param targetCell the cell this object is creating for
     */
    protected DragEventHandler(SceneGraphView editor, TreeCell targetCell) {
        this.editor = editor;
        this.dragTargetCell = targetCell;
    }

    /**
     * Returns a tree item which is an actual target of a drag gesture.
     * Delegates the execution to the eponymous one {@code SceneGraphView#getTargetTreeItem(javafx.scene.input.DragEvent, org.vns.javafx.dock.api.editor.TreeItemEx)
     * }
     *
     * @param ev the event of type {@code DragEvent }
     * @return a tree item which is an actual target of a drag gesture.
     */
    protected TreeItemEx getTargetTreeItem(DragEvent ev) {
        return (TreeItemEx) getEditor().getTargetTreeItem(ev, getTreeCellItem());
    }

    /**
     * Returns an object which is used to create and insert a new object of type {@code TreeItem
     * } in the {@link TreeViewEx }. Gets the gesture source from the event
     * specified as parameter. This source must have the type {@code Node} and
     * represents the node for which the {@code startDragAndDrop } process has
     * been initiated.
     *
     * The method uses the source's properties collection to extract an object
     * of type {@link DragGesture} and then applies the method 
     * {@link DragGesture#getGestureSourceObject() } to it.
     *
     * @param ev the event object of type DragEvent
     * @return the object which serves as a gesture source object.
     */
    public static Object getGestureSourceObject(DragEvent ev) {
        Object o = ev.getGestureSource();

        if (o == null) {
            return null;
        }
        Node node = (Node) o;
        DragGesture dg = (DragGesture) node.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        return dg.getGestureSourceObject();
    }

    /**
     * Returns a node which is considered to be a gesture source. The result
     * node is not necessary a node which is returned by invocation the method
     * {@code ev.getGestureSource}. We can start drag-and-drop on one node but
     * the method {@link DragGesture#getGestureSource() }
     * may return another.
     *
     * @param ev the event object of type {@code DragEvent }
     * @return the node which is considered to be a gesture source.
     */
    protected Node getGestureSource(DragEvent ev) {
        Node node = (Node) ev.getGestureSource();
        if (node == null) {
            return null;
        }

        DragGesture dg = (DragGesture) node.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        return dg.getGestureSource();
    }

    /**
     * Checks whether the specified event can be accepted during the
     * <i>drag-and-drop gesture</i> or <i>simple press-drag-release</i> process.
     * Extracts an object of type {@code Dragboard} and if it is not null then
     * compares a value of the property {@code url} to the constant
     * {@link TreeItemBuilder#CELL_UUID } or {@link TreeItemBuilder#NODE_UUID }.
     *
     * If the {@code dragboard} is equal to {@code null} then the method assumes
     * that the given {@code  DragEvent } has been fired artificially by calling
     * the method {@code Node.fireEvent}. This happens when a <i>drag-and-drop
     * gesture</i> cannot be done and a <i>simple press-drag-release</i> was
     * used. In this case the method assumes that an object of type {@code Node}
     * but not a {@code TreeCell} of the editor's {@code Treeview} is dragged
     * and invokes the method {@link #isSupportedDragSource(javafx.scene.Node)}.
     *
     * @param ev the processed event
     * @return true is the specified event can be accepted/ false otherwise
     */
    protected boolean isSupportedDragSource(DragEvent ev) {
        Dragboard dragboard = ev.getDragboard();
        Node dragSource = getGestureSource(ev);
        if (dragboard == null) {
            //
            // A simple Drag Gesture (a node is dragged)
            // 
            return dragSource != null && isSupportedDragSource(dragSource);
        }

        return dragSource != null && dragboard.hasUrl()
                && (dragboard.getUrl().equals(NODE_UUID)
                || dragboard.getUrl().equals(CELL_UUID));
    }

    /**
     * Checks whether the given {@code node} can be accepted during simple
     * press-drag-release gesture/
     *
     * @param sourceNode the node to be checked
     * @return true if the specified node can be accepted. false otherwise.
     */
    protected boolean isSupportedDragSource(Node sourceNode) {
        return sourceNode.getProperties().get(DRAGBOARD_KEY) == NODE_UUID;
    }

    @Override
    public abstract void handle(DragEvent event);

    /**
     * Checks whether the given event can be accepted and consumed by the event
     * handler. Fist invokes the method
     * {@link #isSupportedDragSource(javafx.scene.input.DragEvent)} and if the
     * resukt is {@code false} then returns the {@code false }. Defines the
     * target {@code TreeItem} which is an actual item which must accept the
     * dragged object. If the target is {@code null} then returns false. Then
     * finds a builder of type {@link TreeItemBuilder } for the target and then
     * it returns the result of applying the
     * {@code TreeItemBuilder#isAdmissiblePosition(javafx.scene.control.TreeView, org.vns.javafx.dock.api.editor.TreeItemEx, org.vns.javafx.dock.api.editor.TreeItemEx, java.lang.Object)
     * }
     * method to this object.
     *
     * @param ev the processed event
     * @return true if the specified event is admissible and can be accepted
     */
    public boolean isAdmissiblePosition(DragEvent ev) {

        if (!isSupportedDragSource(ev)) {
            return false;
        }
        TreeItemEx target = getTargetTreeItem(ev);

        if (target == null) {
            return false;
        }

//20.01        return target.getValue().getBuilder().isAdmissiblePosition(getEditor().getTreeView(), target, getTreeCellItem(), getGestureSourceObject(ev));
        return true;
    }

    /**
     * Check whether a rectangular indicator must be drawn around the specified
     * item.
     *
     * @param ev the processed event
     * @param place the item the rectangle indicator may be drawn around.
     * @return true if a rectangular indicator must be drawn. false otherwise
     */
    protected boolean isRectangleIndicator(DragEvent ev, TreeItemEx place) {
        boolean retval = false;
        Bounds[] bounds = getEditor().getDragIndicator().levelBoundsOf(place);
        int n = -1;
        for (int i = 0; i < bounds.length; i++) {
            if (bounds[i].contains(ev.getScreenX(), ev.getScreenY())) {
                n = i;
                break;
            }
        }

        if (n < 0) {
            retval = true;
        } else if (!place.isExpanded()) {
            int level = getEditor().getTreeView().getTreeItemLevel(place);
            if (n == level || n == level + 1 || n == level + 2) {
                retval = true;
            }
        }
        return retval;
    }

    /**
     * Displays a rectangle around a node or two lines vertically and
     * horizontally.
     *
     * @param ev the processed event
     */
    protected void drawIndicator(DragEvent ev) {
        getEditor().getDragIndicator().hideDrawShapes();
        TreeItemEx fromItem = getTargetTreeItem(ev);
        TreeItemEx toItem = getTreeCellItem();
        if (fromItem == toItem && isRectangleIndicator(ev, (TreeItemEx) toItem)) {
            getEditor().getDragIndicator().drawRectangle(toItem);
        } else {
            getEditor().getDragIndicator().drawLines(fromItem, toItem);
        }

    }

    /**
     * Return an object of type {@link SceneGraphView }.
     *
     * @return an object of type {@link SceneGraphView }.
     */
    public SceneGraphView getEditor() {
        return editor;
    }

    /**
     * Returns an object of type {@code TreeCell} which is a target cell during
     * dragging processing. It's a cell which the mouse cursor point to. This
     * cell is the one which was used as a parameter of the constructor of this
     * class
     *
     * @return Returns an object of type {@code TreeCell} which is a target cell
     */
    public TreeCell getTreeCell() {
        return dragTargetCell;
    }

    /**
     * Returns an object of type {@code TreeItem} which is the model of the cell
     * which was used as a parameter of the constructor of this class
     *
     * @return an object of type TreeItem for the cell this object was created
     */
    public TreeItemEx getTreeCellItem() {
        return (TreeItemEx) dragTargetCell.getTreeItem();
    }

    /**
     * Returns an object for witch the {@literal  TreeItem} of the
     * {@link #dragTargetCell} was created. .
     *
     * @param ev
     * @return
     */
    /*    protected Object getDragTargetObject(DragEvent ev) {
        return ((ItemValue) getTreeCellItem().getValue()).getTreeItemObject();
    }
     */
}
