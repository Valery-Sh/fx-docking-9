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
package org.vns.javafx.dock.api;

import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

/**
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractPreferencesBuilder implements PreferencesBuilder {

    private final DockTargetController targetController;

    protected AbstractPreferencesBuilder(DockTargetController targetController) {
        this.targetController = targetController;
    }

    protected abstract void setXmlProperties(Pair<ObjectProperty, Properties> pair);

    protected abstract void buildChildren(TreeItem<Pair<ObjectProperty, Properties>> root);

    public DockTargetController getTargetController() {
        return this.targetController;
    }

    @Override
    public TreeItem<Pair<ObjectProperty, Properties>> build(String fieldName) {
        TreeItem<Pair<ObjectProperty, Properties>> retval = new TreeItem<>();
        Node node = getTargetController().getTargetNode();

        Pair<ObjectProperty, Properties> pair = new Pair(new SimpleObjectProperty(node), new Properties());
        retval.setExpanded(true);
        retval.setValue(pair);
        setXmlProperties(pair);
        pair.getValue().put(TREEITEM_ATTR, retval);
        if (fieldName != null) {
            pair.getValue().put(FIELD_NAME_ATTR, fieldName);
        }
        if (node.getId() != null) {
            pair.getValue().put("id", node.getId());
        }
        pair.getValue().put(TAG_NAME_ATTR, node.getClass().getSimpleName());
        pair.getValue().put(CLASS_NAME_ATTR, node.getClass().getName());
        pair.getValue().put(ISDOCKTARGET_ATTR, "yes");
        //pair.getValue().put(REGSTERED_ATTR, "yes");
        if (DockRegistry.isDockable(node)) {
            pair.getValue().put(ISDOCKABLE_ATTR, "yes");
        }

        buildChildren(retval);
//        getDockLoader().notifyTreeItemBuilt(retval);

        return retval;

    }

    @Override
    public Node restore(TreeItem<Pair<ObjectProperty, Properties>> targetRoot) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
