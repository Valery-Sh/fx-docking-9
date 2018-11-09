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

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.DefaultProperty;

/**
 *
 * @author Valery
 */
public class Property extends NodeElement {

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

    public static String getDefaultPropertyAnnotation(Class<?> clazz) {
        String retval = null;
        DefaultProperty[] dp = (DefaultProperty[]) clazz.getAnnotationsByType(DefaultProperty.class);
        if (dp.length > 0) {
            retval = dp[0].value();
        }
        return retval;
    }

    public static String getDefaultPropertyAnnotation(String className) {
        String retval = null;
        try {
            Class clazz = Class.forName(className);
            DefaultProperty[] dp = (DefaultProperty[]) clazz.getAnnotationsByType(DefaultProperty.class);
            if (dp.length > 0) {
                retval = dp[0].value();
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("EXCEPTION " + ex.getMessage());
            Logger.getLogger(NodeDescriptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

}
