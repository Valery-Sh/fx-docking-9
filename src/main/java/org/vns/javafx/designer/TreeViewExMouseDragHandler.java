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

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.dragging.DefaultMouseDragHandler;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.bean.BeanAdapter;

/**
 *
 * @author Valery
 */
public class TreeViewExMouseDragHandler extends DefaultMouseDragHandler {

    public TreeViewExMouseDragHandler(DockableContext context) {
        super(context);
    }

    @Override
    public void mousePressed(MouseEvent ev) {
        setStartMousePos(null);
        Point2D pos = new Point2D(ev.getX(), ev.getY());
        Point2D screenPos = new Point2D(ev.getScreenX(), ev.getScreenY());

        if (!ev.isPrimaryButtonDown()) {
            return;
        }
        TreeViewEx treeView = (TreeViewEx) getContext().dockable().node();
        SceneGraphView sgv = treeView.getSceneGraphView();
        TreeItemEx item = sgv.getTreeItem(screenPos);
        //
        // We don't drag the root item (item.getParent cannot be null )
        //
        if (item != null && item.getParent() != null && item.getValue() != null) {
            if ( item.getPropertyName() != null ) {
                //NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(item.getParent().getValue());
                BeanAdapter ba = new BeanAdapter(item.getParent().getValue());
                if ( ba.isReadOnly(item.getPropertyName())) {
                    return;
                }
            }
            
            Label lb = new Label(item.getValue().getClass().getSimpleName());
            lb.getStyleClass().add("tree-item-node-" + item.getValue().getClass().getSimpleName().toLowerCase());            
            BorderStroke stroke = new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT);
            Border border = new Border(stroke);
            lb.setBorder(border);
            lb.setPadding(new Insets(4,4,4,4));
            new Scene(lb); // to be able create snapshot
            
            WritableImage wi;// = null;
            
            wi = lb.snapshot(null, null);
            ImageView node = new ImageView(wi);
            node.setOpacity(0.75);
            
            DragContainer dc = new DragContainer(node, item.getValue());
            dc.setDragAsObject(true);
            dc.setDragSource(DockLayout.of(sgv).getLayoutContext());
            getContext().setDragContainer(dc);
            getContext().setResizable(false);
            setStartMousePos(pos);
        } 
    }

    /*    @Override
    protected void prepare() {
        DragContainer dc = getContext().getDragContainer();
        if ( dc != null && dc.getPlaceholder() != null && dc.getValue() != null && dc.getValue() instanceof Tab ) {
            Tab tab = (Tab) dc.getValue();
            if ( tab.getTabPane() != null ) {
                tab.getTabPane().getTabs().remove(tab);
            }
        }
    }
     */
}
