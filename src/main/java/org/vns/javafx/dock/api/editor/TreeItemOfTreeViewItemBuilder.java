/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api.editor;

import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 */
public class TreeItemOfTreeViewItemBuilder extends AbstractTreeItemBuilder {
    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        retval = createItem(obj);
        TreeItem item = (TreeItem) obj;
        if ( item.getValue() != null ) {
            
        }
        return retval;
    }
    
    @Override
    public boolean isAcceptable(Object target,Object accepting) {
        return accepting instanceof TreeItem;
    }

    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        
    }

    @Override
    public void updateOnMove(TreeItemEx item) {
        
    }
    
}
