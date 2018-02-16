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
package org.vns.javafx.dock.api.designer;


/**
 *
 * @author Valery
 */
public class Property extends NodeElement{
    private String name;
    
    private NodeDescriptor descriptor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected boolean isDefault() {
        return getName().equals(getDescriptor().getDefaultContentProperty().getName());
    }

    protected NodeDescriptor getDescriptor() {
        return descriptor;
    }

    protected void setDescriptor(NodeDescriptor descriptor) {
        this.descriptor = descriptor;
    }


    /*
     *  ------ STATIC HELPER METHODS -------
     * 
     * 
     */
     public static Object getValue(TreeItemEx item, String propertyName) {
         Object retval = null;
         return retval;
     }
    
}
