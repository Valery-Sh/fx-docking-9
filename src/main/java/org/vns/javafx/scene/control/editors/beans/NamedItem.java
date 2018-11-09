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
package org.vns.javafx.scene.control.editors.beans;

/**
 *
 * @author Valery
 */
public interface NamedItem {
    public static final int BEFORE = 0;
    public static final int AFTER = 1;
    public static final int NOT_INSERT = -1;
    
    String getName();
    void setName(String name);
    String getDisplayName();
    void setDisplayName(String displayName);
    
    static String toDisplayName(String propName) {
        
        char[] str = propName.toCharArray();
        if ( Character.isDigit(str[0]) ) {
            return propName;
        }
        StringBuilder sb = new StringBuilder();
        int startPos = 0;
        
        while ( true ) {
            int endPos = getFirstWordPos(str, startPos);
            str[startPos] = Character.toUpperCase(str[startPos]);
            for ( int i = startPos; i <= endPos; i++  )  {
                sb.append(str[i]);
            }                  
            if ( endPos == str.length - 1 ) {
                break;
            }
            sb.append(' ');
            startPos = endPos + 1;
        }
        return sb.toString().trim();
    }

    static int getFirstWordPos(char[] str, int startPos) {
        int lastPos = startPos;
        //str[startPos] = Character.toUpperCase(str[startPos]);
        if (startPos == str.length - 1) {
            return lastPos;
        }
        //
        // Check whether first and cecond char are in upper case
        //
        
        if (Character.isUpperCase(str[startPos])&& Character.isUpperCase(str[startPos + 1]) ) {

            // try search lower case char
            for (int i = startPos + 1; i < str.length; i++) {
                if ( ! Character.isUpperCase(str[i]) ) {
                    lastPos = i - 1;
                    break;
                }
                lastPos = i;
            }
            return lastPos;
        }

        for (int i = startPos + 1; i < str.length; i++) {
            if (Character.isUpperCase(str[i])) {
                lastPos = i - 1;
                break;
            }
            lastPos = i;
        }
        return lastPos;
    }

}
