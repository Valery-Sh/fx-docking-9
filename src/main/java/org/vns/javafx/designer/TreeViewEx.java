package org.vns.javafx.designer;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

/**
 *
 * @author Valery
 * @param <T> ??
 */
public class TreeViewEx<T> extends TreeView { //implements EventHandler<NodeDragEvent> {
    
    public static Pair<String,Object> clipBoardContent;
    
    private VirtualFlowEx<TreeCell> virtualFlow;
    
    public static final String LOOKUP_SELECTOR = "UUID-e651abfa-c321-4249-b78a-120db404b641";
    private final SceneGraphView sceneGraphView;
            
    private final NodeDragEvent nodeDragEvent = new NodeDragEvent((MouseEvent) null);
    private DragEvent dragEvent;
    private boolean dragAccepted;

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
        getStyleClass().add(LOOKUP_SELECTOR);
        getStyleClass().add("designer");
/*        rootProperty().addListener((v,ov,nv) -> {
            if ( nv != null && sceneGraphView.getRoot() == null ) {
                sceneGraphView.setRoot( (Node)((TreeItem)nv).getValue());
            } else if ( nv == null ) {
                System.err.println("SET ROOT NULL");
                sceneGraphView.setRoot(null);
            }
        });
*/
    }

    protected VirtualFlowEx<TreeCell> getVirtualFlow() {
        return virtualFlow;
    }

    public void setVirtualFlow(VirtualFlowEx<TreeCell> virtualFlow) {
        this.virtualFlow = virtualFlow;
    }
    
    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }
    
    public NodeDragEvent getNodeDragEvent(MouseEvent ev) {
        nodeDragEvent.setMouseEvent(ev);
        return nodeDragEvent;
    }

    public DragEvent getDragEvent() {
        return dragEvent;
    }
    public boolean isDragAccepted() {
        return dragAccepted;
    }

    public void notifyDragEvent(DragEvent dragEvent) {
        this.dragEvent = dragEvent;
    }
    public void notifyDragAccepted(boolean dragAccepted) {
        this.dragAccepted = dragAccepted;
    }

    public SceneGraphView getSceneGraphView() {
        return sceneGraphView;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TreeViewExSkin(this);
    }

    public VirtualScrollBar getVScrollBar() {
        return virtualFlow.getVScrollBar();
    }

    public VirtualScrollBar getHScrollBar() {
        return virtualFlow.getHScrollBar();
    }

/*    private boolean isInsideScrollBar(MouseEvent ev) {
        boolean retval = false;
        VirtualScrollBar sb = getVScrollBar();
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
*/
    public static class VirtualFlowEx<I> extends VirtualFlow {
        public VirtualScrollBar getVScrollBar() {
            return this.getVbar();
        }
        public VirtualScrollBar getHScrollBar() {
            return this.getHbar();
        }        
    }

}
