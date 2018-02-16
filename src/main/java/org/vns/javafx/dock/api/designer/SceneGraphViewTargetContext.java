/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.designer;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.ContextLookup;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.dragging.DragManagerFactory;
import org.vns.javafx.dock.api.dragging.view.FloatViewFactory;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

/**
 *
 * @author Valery Shyshkin
 */
public class SceneGraphViewTargetContext extends TargetContext {

    public SceneGraphViewTargetContext(Node targetNode) {
        super(targetNode);
    }

    public SceneGraphViewTargetContext(Dockable dockable) {
        super(dockable);
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        lookup.putUnique(IndicatorManager.class, new DragIndicatorManager(this, ((SceneGraphView)getTargetNode()).getDragIndicator()));
        System.err.println("SceneGraphView initLookup");
        lookup.putUnique(DragManagerFactory.class, new TreeItemDragManagerFactory());
        lookup.putUnique(FloatViewFactory.class, new TreeItemFloatViewFactory());
        
    }
    @Override
    public boolean isDocked(Node node ) {
        System.err.println("@@@@ isDocked(node) = " + node);
        boolean retval = false;
        if ( node instanceof TreeCell ) {
            System.err.println("@@@@ instanceof Treecell 1");            
            TreeItemEx item = (TreeItemEx) ((TreeCell) node).getTreeItem();
            System.err.println("@@@@ item " + item);
            if ( item != null ) {
                System.err.println("@@@@ findByTreeItemObject(item) " + EditorUtil.findByTreeItemObject(item));
            }
            if ( item != null && EditorUtil.findByTreeItemObject(item) != null  ) {
                retval = true;
            }
        }
        System.err.println("@@@@ retval = " + retval);            
        System.err.println("------------------------------------");
        return retval;
    }
    
    @Override
    protected boolean doDock(Point2D mousePos, Node node  ) {
        System.err.println("DO DOCK node = " + node);
        return false;
    }

    @Override
    public Object getRestorePosition(Dockable dockable
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restore(Dockable dockable, Object restoreposition
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove(Node dockNode
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}