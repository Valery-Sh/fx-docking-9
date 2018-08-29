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
package org.vns.javafx.dock.api.designer.bean.editor;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;

/**
 *
 * @author Valery
 */
public class ScrollableContextMenu extends ContextMenu {
     /**
     * Create a new ScrollableContextMenu
     */
    public ScrollableContextMenu() {
        super();
    }

    /**
     * Create a new ScrollableContextMenu initialized with the given items
     */
    public ScrollableContextMenu(MenuItem... items) {
        this();
    }

     /** {@inheritDoc} */
    @Override protected Skin<?> createDefaultSkin() {
        return new ScrollabeContextMenuSkin(this);
    }
}
