/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;

/**
 *
 * @author Valery
 */
public class DragNodeGesture implements DragGesture {
    
    private Node sourceGesture;
    private Object sourceGestureObject;

    public DragNodeGesture(Node sourceGesture) {
        this.sourceGesture = sourceGesture;
        this.sourceGestureObject = sourceGesture;
    }
    protected void setSourceGestureObject(Object sourceGestureObject) {
        this.sourceGestureObject = sourceGestureObject;
    }
    
    @Override
    public Node getGestureSource() {
        return sourceGesture;
    }

    @Override
    public Object getGestureSourceObject() {
        return sourceGestureObject;
    }
    
}
