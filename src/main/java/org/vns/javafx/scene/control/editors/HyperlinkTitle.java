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
package org.vns.javafx.scene.control.editors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;

/**
 *
 * @author Nastia
 */
public class HyperlinkTitle extends Hyperlink {
    
    private static final PseudoClass CSS_VALUE = PseudoClass.getPseudoClass("cssvalue");
    private BaseEditor editor = null;
    private String propName = null;
            
    public HyperlinkTitle(BaseEditor editor, String propName ) {
        super(Util.toDisplayName(propName));
        this.editor = editor;
        this.propName = propName;
        setId("title-" + propName);
        setOnAction(a -> {
            setVisited(false);
            Util.showInBrowser(editor);
        });
        init();
    }
    
    public HyperlinkTitle() {
    }

    public HyperlinkTitle(String text) {
        this(text, null);
    }

    public HyperlinkTitle(String text, Node graphic) {
        super(text);
        init();
    }

    private void init() {
        setTextFill(Color.BLACK);
        setBorder(Border.EMPTY);
    }

    @Override
    public String getUserAgentStylesheet() {
        return PropertyEditor.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    public boolean isCssValue() {
        return cssValue.get();
    }

    public void setCssValue(boolean cssValue) {
        this.cssValue.set(cssValue);
    }
    private final BooleanProperty cssValue = new BooleanPropertyBase(false) {

        @Override
        protected void invalidated() {
            pseudoClassStateChanged(CSS_VALUE, get());
        }

        @Override
        public Object getBean() {
            return HyperlinkTitle.this;
        }

        @Override
        public String getName() {
            return "cssvalue";
        }
    };

}
