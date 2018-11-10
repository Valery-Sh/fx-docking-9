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
package org.vns.javafx.dock.api;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 *
 * @author Nastia
 */
public class Scope {
    /**
     * The identifier of the scope
     */
    private final Object id;
    private BiPredicate<LayoutContext,Dockable> filter; 

    public Scope(Object id) {
        this.id = id;
    }
    public Scope(Object id, BiPredicate<LayoutContext,Dockable> filter) {
        this.id = id;
        this.filter = filter;
    }
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Scope other = (Scope) obj;
        return true;
    }

    public BiPredicate<LayoutContext, Dockable> getFilter() {
        return filter;
    }

    public void setFilter(BiPredicate<LayoutContext, Dockable> filter) {
        this.filter = filter;
    }
    
}
