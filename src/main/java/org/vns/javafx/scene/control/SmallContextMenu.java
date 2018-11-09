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
package org.vns.javafx.scene.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;

/**
 *
 * @author Nastia
 */
public class SmallContextMenu extends ContextMenu {

    private final DoubleProperty contentHeight = new SimpleDoubleProperty();

    public SmallContextMenu() {
        this(new MenuItem[0]);
    }

    public SmallContextMenu(MenuItem... mis) {
        super(mis);
        setOnShown(e -> {
            Node content = getSkin().getNode();
            if (content instanceof Region) {
                ((Region) content).setMaxHeight(getContentHeight());
            }
        });

    }

    public DoubleProperty contentHeightproperty() {
        return contentHeight;
    }

    public Double getContentHeight() {
        return contentHeight.get();
    }

    public void setContentHeight(Double ch) {
        contentHeight.set(ch);
    }
}
