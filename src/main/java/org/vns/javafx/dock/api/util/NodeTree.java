/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.util;

import java.util.List;
import java.util.Stack;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.TopNodeHelper;

/**
 *
 * @author Valery
 */
public class NodeTree {
    private final Node node;

    public NodeTree(Node node) {
        this.node = node;
    }
    public void show(Pane root, double x, double y) {
        //
        // Get a list of parent nodes
        //
        List<Node> p = TopNodeHelper.getParentChain(node, el -> {return DockRegistry.isDockPaneTarget(node);}  );
        root.getChildren().add(getRegion(p));
        Popup popup = new Popup();
        popup.show(root,x,y);
    }
    
    public Region getRegion(List<Node> model) {
        TreeView<String> r = new TreeView<>();
        Stack<TreeItem<String>> stack = new Stack<>(); 
        for ( int i=0; i < model.size(); i++) {
            stack.push((TreeItem<String>) getItem(model.get(i)));
        }
        TreeItem<String> it = stack.pop();
        r.setRoot(it);
        while( ! stack.isEmpty()) {
            TreeItem<String> c = stack.peek();
            it.getChildren().add(c);
            it = c;
        }
        return r;        
    }
    public Object getItem(Node node) {
        String value = DockRegistry.dockPaneTarget(node).paneHandler().getTitle();
        TreeItem item = new TreeItem(value);
        return item;
    }
}
