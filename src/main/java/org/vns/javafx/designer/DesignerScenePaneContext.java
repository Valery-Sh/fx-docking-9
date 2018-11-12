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
package org.vns.javafx.designer;

import javafx.scene.Node;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.ScenePaneContext;

/**
 *
 * @author Olga
 */
public class DesignerScenePaneContext extends ScenePaneContext {
    
    public DesignerScenePaneContext(Dockable dockable) {
        super(dockable);
        init();
    }
    private void init(){
    }
    public static class DesignerScenePaneContextFactory extends ScenePaneContextFactory{
        @Override
        public ScenePaneContext getContext(Dockable dockable) {
            return new DesignerScenePaneContext(dockable);
        }
    } 
    

    @Override
    public boolean contains(Object obj) {
        if (obj == null ) {
            return false;
        }
        
        TreeViewEx tv = DesignerLookup.lookup(SceneGraphView.class).getTreeView();
        TreeItemEx item = EditorUtil.findTreeItemByObject(tv, obj);
  
        return Dockable.of(obj) != null && Dockable.of(obj).getContext().getLayoutContext() == this && item != null;
    }

    @Override
    public boolean isAcceptable(Dockable dockable) {
        System.err.println("IS ACCEPTABLE node = " + dockable.node());
        return super.isAcceptable(dockable);
    }
    
    @Override
    public void remove(Object obj) {
        System.err.println("DesignerScenePaneContext.remove obj = " + obj);
        if (!(obj instanceof Node)) {
            return;
        }
        Node dockNode = (Node) obj;
        if (!contains(dockNode)) {
            return;
        }
        TreeViewEx tv = DesignerLookup.lookup(SceneGraphView.class).getTreeView();
        TreeItemEx item = EditorUtil.findTreeItemByObject(tv, obj);        
        if ( item != null && item.getParent() == null ) {
            //
            // root item
            //
            //tv.setRoot(null); //09.11 was old: return;
            //return; // 09.11;
            SceneGraphView sgv = DesignerLookup.lookup(SceneGraphView.class);
            if ( sgv.getRoot() == null ) {
                return;
            }
            if ( sgv.getRoot().getScene() == null ) {
                return;
            }
            if ( sgv.getRoot() == sgv.getRoot().getScene().getRoot() ) {
                System.err.println("scene.setRoot(null");
                sgv.setRoot(null);
                sgv.getRoot().getScene().setRoot(null);
                return;
            } else {
                TreeItemBuilder builder = new TreeItemBuilder();
                TreeItemEx it = builder.build(sgv.getRoot().getScene().getRoot());
                //EditorUtil.findTreeItemByObject(tv, obj);        
                it = EditorUtil.findChildTreeItem(it, item.getValue());
                System.err.println("DesignerScenePaneContext remove it.getValue() = " + it.getValue());
                builder.updateOnMove(it);
                it = EditorUtil.findChildTreeItem(it, item.getValue());
                System.err.println("DesignerScenePaneContext remove it) = " + it);
                sgv.setRoot(null);
                tv.setRoot(null);
                return;
            }
        }
        new TreeItemBuilder().updateOnMove(item);
      //  if (DockRegistry.getInstance().getBeanRemover() != null) {
      //      DockRegistry.getInstance().getBeanRemover().remove(dockNode);
      //  }
    }
    
}
