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
package org.vns.javafx.olddesigner;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.olddesigner.SceneGraphView.ANCHOR_OFFSET;
import org.vns.javafx.dock.api.editor.bean.BeanAdapter;

/**
 *
 * @author Valery
 */
public class TreeItemBuilder {

    public static final String ACCEPT_TYPES_KEY = "tree-item-builder-accept-types";
    public static final String CELL_UUID = "uuid-29a4b479-0282-41f1-8ac8-21b4923235be";
    public static final String NODE_UUID = "uuid-f53db037-2e33-4c68-8ffa-06044fc10f81";

    //private final Object nodeObject;
    public TreeItemBuilder() {
        
        
    }

    public TreeItemEx build(Object obj) {
        return build(obj, null);
    }
    
    protected TreeItemEx build(Object obj, Property p) {
        TreeItemEx retval;
        if ( p != null && (p instanceof Placeholder) ) {
            retval = createPlaceholder(obj, (Placeholder)p);
        } else if ( p != null && (p instanceof Header) ) {
            retval = createHeaderItem(obj, (Header)p);
        } else {
            retval = createItem(obj);
        }
        if ( obj == null && ! (p instanceof Header)) {
            return retval;
        }
        NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(obj);

        BeanAdapter adapter = new BeanAdapter(obj);

        nc.getProperties().forEach(cp -> {
            int cpIdx = nc.getProperties().indexOf(cp);
            Object cpObj = adapter.get(cp.getName());
            boolean isplaceholder = false;
            boolean hideIfNull = false;
            if (cp instanceof Placeholder) {
                isplaceholder = true;
                hideIfNull = ((Placeholder) cp).isHideNull();
            }
            
            if (cp instanceof Header) {
                TreeItemEx headerItem = build(cpObj, (Header) cp);
                //23headerItem.getValue().setIndex(cpIdx);
                headerItem.getValue().setPropertyName(cp.getName());
                retval.getChildren().add(headerItem);                
                //23retval.getValue().setIndex(cpIdx);
                retval.getValue().setPropertyName(cp.getName());
                if (List.class.isAssignableFrom(cpObj.getClass())) {
                    
                    List ls = (List) cpObj;

                    for (int i = 0; i < ls.size(); i++) {
                        TreeItemEx item = build(ls.get(i));
                        //23item.getValue().setIndex(cpIdx);
                        item.getValue().setPropertyName(cp.getName());
                        headerItem.getChildren().add(item);
                    }
                } else {
                    TreeItemEx item = build(cpObj);
                    //23item.getValue().setIndex(cpIdx);
                    item.getValue().setPropertyName(cp.getName());
                    retval.getChildren().add(item);
                }
            } else if (!isplaceholder && cpObj != null) {
                if (List.class.isAssignableFrom(cpObj.getClass())) {
                    List ls = (List) cpObj;
                    for (int i = 0; i < ls.size(); i++) {
                        TreeItemEx item = build(ls.get(i));
                        //23item.getValue().setIndex(cpIdx);
                        item.getValue().setPropertyName(cp.getName());
                        retval.getChildren().add(item);
                    }
                } else {
                    TreeItemEx item = build(cpObj);
                    //23item.getValue().setIndex(nc.getProperties().indexOf(cp));
                    item.getValue().setPropertyName(cp.getName());
                    retval.getChildren().add(item);
                }
            //} else if ((!isplaceholder && cpObj == null) || (isplaceholder && hideIfNull && cpObj == null)) {
                // Do nothing
            } else if ( isplaceholder && ( cpObj != null || ! hideIfNull ) )  {
                    
//            } else {
                //TreeItemEx item = createPlaceholder(cpObj, (Placeholder) cp);
                TreeItemEx item = build(cpObj, (Placeholder) cp);
                //23item.getValue().setIndex(cpIdx);
                item.getValue().setPropertyName(cp.getName());
                retval.getChildren().add(item);
            }
        });
        return retval;
    }
    
    public final Node createItemContent(Object obj) {
        return createDefaultContent(obj);
    }

    protected HBox createDefaultContent(Object obj) {
        HBox box = new HBox(new HBox()); // placeholder 
        NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(obj);
        String tp = nc.getTitleProperty();
        String text = "";
        if (tp != null) {
            BeanAdapter adapter = new BeanAdapter(obj);
            text = (String) adapter.get(tp);
            if (text == null) {
                text = "";
            }
        }
        Label label = new Label((obj.getClass().getSimpleName() + " " + text).trim());
        String styleClass = nc.getStyleClass();
        if (styleClass == null) {
            styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        }
        label.getStyleClass().add(styleClass);
        box.getChildren().add(label);
        return box;
    }

    public final TreeItemEx createItem(Object obj) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        //anchorPane.setStyle("-fx-background-color: yellow");

        TreeItemEx item = new TreeItemEx();
        ItemValue itemValue = new ItemValue(item);
        item.setValue(itemValue);

        box.getChildren().add(createItemContent(obj));

        itemValue.setCellGraphic(anchorPane);
        itemValue.setTreeItemObject(obj);

        return item;
    }
    
    protected HBox createHeaderContent(Object obj, Header h) {
        HBox box = new HBox(new HBox()); // placeholder 
        String title = h.getTitle();
        
        if (title == null) {
            title = "";
        }
        Label label = new Label(title.trim());
        String styleClass = h.getStyleClass();
        if (styleClass == null) {
            styleClass = "tree-item-list-header";
        }
        label.getStyleClass().add(styleClass);
        box.getChildren().add(label);
        return box;
    }

    public final TreeItemEx createHeaderItem(Object obj, Header h) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        //anchorPane.setStyle("-fx-background-color: yellow");

        TreeItemEx item = new TreeItemEx();
        ItemValue itemValue = new ItemValue(item);
        item.setValue(itemValue);

        box.getChildren().add(createHeaderContent(obj, h));

        itemValue.setCellGraphic(anchorPane);
        // header cannot have an object
        //itemValue.setTreeItemObject(obj);

        return item;
    }

    public final TreeItemEx createPlaceholder(Object obj, Placeholder cp) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        //anchorPane.setStyle("-fx-background-color: yellow");
        TreeItemEx retval = new TreeItemEx();
        ItemValue itv = new ItemValue(retval);
        retval.setValue(itv);

        box.getChildren().add(createPlaceholderContent(obj, cp));
        itv.setCellGraphic(anchorPane);
        itv.setTreeItemObject(obj);

        //box.getChildren().add(createItemContent(obj));
        //23retval.getValue().setPlaceholder(true);
        return retval;
    }

    protected HBox createPlaceholderContent(Object obj, Placeholder cp) {
        Label iconLabel = new Label();
        HBox retval = new HBox(iconLabel);

        String style = cp.getStyleClass();
        if (style != null) {
            iconLabel.getStyleClass().add(style);
        }

        String title = cp.getTitle();
        if (title == null) {
            title = "";
        }

        if (obj == null) {
            iconLabel.setText(title.trim());
        } else {

            NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(obj);
            title = "";
            String tp = nc.getTitleProperty();
            if (tp != null) {
                BeanAdapter adapter = new BeanAdapter(obj);
                title = (String) adapter.get(tp);
                if (title == null) {
                    title = "";
                }
            }
            Label glb = new Label(obj.getClass().getSimpleName());

            glb.getStyleClass().add("tree-item-node-" + obj.getClass().getSimpleName().toLowerCase());
            retval.getChildren().add(glb);

            glb.setText(glb.getText() + " " + title.trim());
        }
        //hb.applyCss();
        return retval;
    }

    ////////////////////////////////////////////////////////////
    protected boolean isAcceptable(TreeItemEx target, Object toAccept) {
        if (toAccept == null) {
            return false;
        }
        boolean retval = true;
        NodeDescriptor nc;
        if (target.getObject() == null) {
            //
            // This is possible when target is a placeholder
            //
            TreeItemEx parent = (TreeItemEx) target.getParent();
            nc = NodeDescriptorRegistry.getInstance().getDescriptor(parent.getObject());
            Property cp = nc.getProperties().get(target.getValue().getIndex());
            BeanAdapter adapter = new BeanAdapter(parent.getObject());
            retval = adapter.getType(cp.getName()).isAssignableFrom(toAccept.getClass());
        } else {
            nc = NodeDescriptorRegistry.getInstance().getDescriptor(target.getObject());
        }

        return retval;
    }
    private Object getObject(TreeItemEx item) {
        if ( (item instanceof  )
    }
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
/*    public void updateOnMove(TreeItemEx child) {
        TreeItemEx parent = (TreeItemEx) child.getParent();
        Object obj = parent.getObject();
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(obj);
        Property prop = nd.getProperties().get( child.getValue().getIndex());
        BeanAdapter ba = new BeanAdapter(obj);
        Class propType = ba.getType(prop.getName());
        if ( List.class.isAssignableFrom(propType)) {
            List ls = (List) ba.get(prop.getName());
            ls.remove(child.getObject());
        } else {
            ba.put(prop.getName(), null);
        }
    }
*/
    public void addTreeItemObjectChangeListener(TreeItemEx item) {

        if (item.getObject() == null) {
            return;
        }
        removeTreeItemObjectChangeListener(item);
        Object listener = null;
        

        item.getValue().setChangeListener(listener);
    }
    public void removeTreeItemObjectChangeListener(TreeItemEx item) {
    }    
}
