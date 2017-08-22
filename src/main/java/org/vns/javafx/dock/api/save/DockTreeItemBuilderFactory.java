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
package org.vns.javafx.dock.api.save;

import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockTabPane;
import org.vns.javafx.dock.DockTabPane.TabPaneContext;
import org.vns.javafx.dock.api.DockPaneContext;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.save.builder.DockPaneTreeItemBuilder;
import org.vns.javafx.dock.api.save.builder.DockTabPaneTreeItemBuilder;

/**
 *
 * @author Valery
 */
public class DockTreeItemBuilderFactory {
    
    public DockTreeItemBuilder getItemBuilder(DockTarget target) {
        DockTreeItemBuilder  retval = null;
        if ( target instanceof DockPane) {
            retval = new DockPaneTreeItemBuilder(target);
        } else if ( target instanceof DockTabPane) {
            retval = new DockTabPaneTreeItemBuilder(target);
        }
        return retval;
    }
}
