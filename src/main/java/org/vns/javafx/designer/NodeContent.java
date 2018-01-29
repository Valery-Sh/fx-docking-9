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


/**
 *
 * @author Valery Shyshkin
 */
public class NodeContent extends Property {
    
    public static String DEFAULT_STYLE_CLASS = "node-insert-content";
    public static String DEFAULT_TITLE = "insert content";
    
    private boolean hideWhenNull;
    private boolean replaceable;
    
    public NodeContent() {
    }

    public boolean isHideWhenNull() {
        return hideWhenNull;
    }

    public void setHideWhenNull(boolean hideWhenNull) {
        this.hideWhenNull = hideWhenNull;
    }

    public boolean isReplaceable() {
        return replaceable;
    }

    public void setReplaceable(boolean replaceable) {
        this.replaceable = replaceable;
    }
    
    @Override
    public boolean isDefault() {
        return super.isDefault();
    }

    
}
