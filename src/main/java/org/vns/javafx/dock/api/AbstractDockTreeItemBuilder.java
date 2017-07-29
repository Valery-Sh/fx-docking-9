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
import java.util.function.Consumer;
import javafx.scene.control.TreeItem;

/**
 * This is an implementation of the 
 * {@link  org.vns.javafx.dock.api.DockTreeItemBuilder } interface, intended for
 * use with {@link  org.vns.javafx.dock.api.DockTarget } objects.
 *
 * The class constructor requires an object of type {@code DockTarget} and
 * provides a {@link #getDockTarget() } method to access the {@code dockTarget}
 * object.
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractDockTreeItemBuilder implements DockTreeItemBuilder {

    private final DockTarget dockTarget;
    private Consumer<TreeItem<Properties>> notifyOnBuildFunction;

    /**
     * Created a new instance of the class for the specified object of type 
     * {@link  org.vns.javafx.dock.api.DockTarget }
     *
     * @param dockTarget the object this instance is to be created for.
     */
    protected AbstractDockTreeItemBuilder(DockTarget dockTarget) {
        this.dockTarget = dockTarget;
    }

    /**
     * Returns an object of type {@link  org.vns.javafx.dock.api.DockTarget }
     *
     * @return the object of type {@code DockTarget }
     */
    public DockTarget getDockTarget() {
        return this.dockTarget;
    }

    protected void notifyOnBuidItem(TreeItem<Properties> item) {
        if (notifyOnBuildFunction != null) {
            notifyOnBuildFunction.accept(item);
        }
    }
        @Override
        public void setOnBuildItem(Consumer<TreeItem<Properties>> consumer) {
            notifyOnBuildFunction = consumer;
        }

        @Override
        public Consumer<TreeItem<Properties>> getOnBuildItem() {
            return notifyOnBuildFunction;
        }
    

}
