
package org.vns.javafx.dock.api.editor;

import com.sun.javafx.scene.control.skin.TreeViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;

/**
 *
 * @author Valery
 * @param <T> ???
 */
public class TreeViewExSkin<T> extends TreeViewSkin {

    private VirtualFlowEx<TreeCell> exFlow;

    public TreeViewExSkin(TreeView treeView) {
        super(treeView);
    }

    @Override
    protected VirtualFlowEx<TreeCell> createVirtualFlow() {
        return (exFlow = (VirtualFlowEx<TreeCell>) new VirtualFlowEx<TreeCell>());
    }

    public VirtualFlowEx<TreeCell> getExFlow() {
        return exFlow;
    }

    public VirtualScrollBar getVScrollBar() {
        return exFlow.getVScrollBar();
    }
    public VirtualScrollBar getHScrollBar() {
        return exFlow.getHScrollBar();
    }

    public static class VirtualFlowEx<I> extends VirtualFlow {
        public VirtualScrollBar getVScrollBar() {
            return this.getVbar();
        }
        public VirtualScrollBar getHScrollBar() {
            return this.getHbar();
        }        
    }
}
