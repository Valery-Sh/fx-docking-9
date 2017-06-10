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

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 * 
 * When a DRAG_DETECTED event is raised on a {@code TreeCell}, an instance of 
 * this class is created.
 * The reference to the object is put as a value to the 
 * {@code properties} collection of the  {@code cell}.
 * The key to this {@code properties} collection is 
 * the value of the constant {@link EditorUtil#GESTURE_SOURCE_KEY }. 
 * Thus, when a gesture target handles the {@code DragEvent} it gets access
 * tho the instance of this class, for example
 * <pre>
 *   public void handle(DragEvent event) {
 *       Node node = (Node) ev.getGestureSource();
 *       DragGesture dg = (DragGesture) node.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
 *   }
 * </pre>
 * @see DragGesture
 * @see DragNodeGesture
 * 
 * @author Valery Shyshkin
 */
public class DragTreeViewGesture extends DragNodeGesture{
    
    private TreeItemEx sourceTreeItem;
    /**
     * Creates a new instance of the class for the specified parameter.
     * the following code sets the value of the property {@code gestureSourceObject}
     * <pre>
     *  TreeItem it = ((TreeCell)getGestureSource()).getTreeItem();
     *   setSourceGestureObject(((ItemValue)it.getValue()).getTreeItemObject());
     * </pre>
     * 
     * @param gestureSource the object of type {@code TreeCell} on which the 
     *    gesture is initiated.
     * @param treeItem ???
     */
    public DragTreeViewGesture(Node gestureSource, TreeItemEx treeItem) {
        super(gestureSource);
        this.sourceTreeItem = treeItem;
        init();
    }
    private void init() {
        setSourceGestureObject(sourceTreeItem.getValue().getTreeItemObject());
    } 

    public TreeItemEx getGestureSourceTreeItem() {
        return sourceTreeItem;
    }
    
    
}
