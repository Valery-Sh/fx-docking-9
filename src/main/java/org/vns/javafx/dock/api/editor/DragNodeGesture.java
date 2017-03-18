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
