
package org.vns.javafx.designer;

import com.sun.javafx.scene.control.skin.TreeViewSkin;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import org.vns.javafx.designer.TreeViewEx.VirtualFlowEx;

/**
 *
 * @author Valery Shyshkin
 * 
 */
public class TreeViewExSkin extends TreeViewSkin<TreeView> {

    public TreeViewExSkin(TreeViewEx treeView) {
        super(treeView);
    }

    @Override
    protected VirtualFlowEx<TreeCell> createVirtualFlow() {
        VirtualFlowEx<TreeCell> retval = (VirtualFlowEx<TreeCell>) new VirtualFlowEx<TreeCell>();
        ((TreeViewEx)getSkinnable()).setVirtualFlow(retval);
        return retval;
    }

}
