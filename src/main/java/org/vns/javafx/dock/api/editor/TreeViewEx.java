package org.vns.javafx.dock.api.editor;

import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 *
 * @author Valery
 * @param <T>
 */
public class TreeViewEx<T> extends TreeView implements EventHandler<NodeDragEvent> {

    public static final String LOOKUP_SELECTOR = "UUID-e651abfa-c321-4249-b78a-120db404b641";
    private SceneGraphView sceneGraphView;

    public TreeViewEx(SceneGraphView editor) {
        super();
        this.sceneGraphView = editor;
        init();

    }

    public TreeViewEx(SceneGraphView editor, TreeItem<T> root) {
        super(root);
        this.sceneGraphView = editor;
        init();
    }

    private void init() {
        addEventFilter(NodeDragEvent.NODE_DRAG, this);
        getStyleClass().add(LOOKUP_SELECTOR);
    }

    public SceneGraphView getSceneGraphView() {
        return sceneGraphView;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TreeViewExSkin<>(this);
    }

    public VirtualScrollBar getScrollBar() {
        return ((TreeViewExSkin) getSkin()).getScrollBar();
    }

    private boolean isInsideScrollBar(MouseEvent ev) {
        boolean retval = false;
        VirtualScrollBar sb = getScrollBar();
        Bounds sbBounds = sb.localToScreen(sb.getBoundsInLocal());
        if (sbBounds.contains(ev.getScreenX(), ev.getScreenY())) {
            retval = true;
        }
        return retval;
    }

    private boolean isInsideTreeView(MouseEvent ev) {
        boolean retval = false;
        Bounds sbBounds = localToScreen(getBoundsInLocal());
        if (sbBounds.contains(ev.getScreenX(), ev.getScreenY())) {
            retval = true;
        }
        return retval;
    }

    private DragEvent createDragEvent(MouseEvent ev, EventType eventType, Object gestureSource, Object gestureTarget) {
        DragEvent retval = new DragEvent(
                eventType,
                null,
                ev.getSceneX(),
                ev.getSceneY(),
                ev.getScreenX(),
                ev.getScreenY(),
                TransferMode.MOVE,
                gestureSource,
                gestureTarget,
                null);
        return retval;

    }

    @Override
    public void handle(NodeDragEvent ev) {

        TreeItem<ItemValue> item = EditorUtil.findTreeItem(this, ev.getMouseEvent().getScreenX(), ev.getMouseEvent().getScreenY());

        DragEvent dragEvent;

        if (isInsideScrollBar(ev.getMouseEvent())) {
            dragEvent = createDragEvent(ev.getMouseEvent(), DragEvent.DRAG_ENTERED,
                    ev.getGestureSource(), getScrollBar());
        } else {
            dragEvent = createDragEvent(ev.getMouseEvent(), DragEvent.DRAG_EXITED,
                    ev.getGestureSource(), getScrollBar());
        }
        getScrollBar().fireEvent(dragEvent);

        EventType dragEventType = DragEvent.DRAG_OVER;
        if (ev.getMouseEvent().getEventType() == MouseEvent.MOUSE_RELEASED) {
            dragEventType = DragEvent.DRAG_DROPPED;
        }
        dragEvent = createDragEvent(ev.getMouseEvent(), dragEventType,
                ev.getGestureSource(),
                item != null ? item.getValue().getCellGraphic().getParent() : this);

        if (item != null) {
            System.err.println("1) DRAGEVENT TYPE = " + dragEvent.getEventType());
            item.getValue().getCellGraphic().getParent().fireEvent(dragEvent);
/*            if (dragEvent.getEventType() == DragEvent.DRAG_DROPPED) {
                dragEvent = createDragEvent(ev.getMouseEvent(), DragEvent.DRAG_DONE,
                        ev.getGestureSource(),
                        item != null ? item.getValue().getCellGraphic().getParent() : this);
                item.getValue().getCellGraphic().getParent().fireEvent(dragEvent);                        
            }
*/
            System.err.println("2) DRAGEVENT gestureSource = " + dragEvent.getGestureSource());
            System.err.println("3) DRAGEVENT        source = " + dragEvent.getSource());

        } else if (isInsideTreeView(ev.getMouseEvent())) {
            System.err.println("4) DRAGEVENT TYPE = " + dragEvent.getEventType());
            fireEvent(dragEvent);
        }
    }
}
