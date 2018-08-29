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

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.WindowEvent;

/**
 *
 * @author Valery
 */
public class ScrollableContextMenuSkin implements Skin<ScrollableContextMenu> {

    /* need to hold a reference to popupMenu here because getSkinnable() deliberately
     * returns null in PopupControlSkin. */
    private ScrollableContextMenu popupMenu;

    private final Region root;

    private final EventHandler<KeyEvent> keyListener = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getEventType() != KeyEvent.KEY_PRESSED) {
                return;
            }

            // We only care if the root container still has focus
            if (!root.isFocused()) {
                return;
            }

            final KeyCode code = event.getCode();
            switch (code) {
                case ENTER:
                case SPACE:
                    popupMenu.hide();
                    return;
                default:
                    return;
            }
        }
    };

    /**
     * 
     */
    public ScrollableContextMenuSkin(final ScrollableContextMenu popupMenu) {
        this.popupMenu = popupMenu;

        // When a contextMenu is shown, requestFocus on its content to enable
        // keyboard navigation.
        popupMenu.addEventHandler(Menu.ON_SHOWN, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Node cmContent = popupMenu.getSkin().getNode();
                if (cmContent != null) {
                    cmContent.requestFocus();
//                    if (cmContent instanceof ContextMenuContent) {
//                        Node accMenu = ((ContextMenuContent) cmContent).getItemsContainer();
//                        accMenu.notifyAccessibleAttributeChanged(AccessibleAttribute.VISIBLE);
//                    }
                }

                root.addEventHandler(KeyEvent.KEY_PRESSED, keyListener);
            }
        });
        popupMenu.addEventHandler(Menu.ON_HIDDEN, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Node cmContent = popupMenu.getSkin().getNode();
                if (cmContent != null) {
                    cmContent.requestFocus();
                }

                root.removeEventHandler(KeyEvent.KEY_PRESSED, keyListener);
            }
        });

        // For accessibility Menu.ON_HIDING does not work because isShowing is true
        // during the event, Menu.ON_HIDDEN does not work because the Window (in glass)
        // has already being disposed. The fix is to use WINDOW_HIDING (WINDOW_HIDDEN).
        popupMenu.addEventFilter(WindowEvent.WINDOW_HIDING, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Node cmContent = popupMenu.getSkin().getNode();
//                if (cmContent instanceof ContextMenuContent) {
//                    Node accMenu = ((ContextMenuContent) cmContent).getItemsContainer();
//                    accMenu.notifyAccessibleAttributeChanged(AccessibleAttribute.VISIBLE);
//                }
            }
        });

        //root = new ContextMenuContent(popupMenu);
        root = null;
        root.idProperty().bind(popupMenu.idProperty());
        root.styleProperty().bind(popupMenu.styleProperty());
        root.getStyleClass().addAll(popupMenu.getStyleClass()); // TODO needs to handle updates

    }

    @Override
    public ScrollableContextMenu getSkinnable() {
        return popupMenu;
    }

    @Override
    public Node getNode() {
        return root;
    }

    @Override
    public void dispose() {
        root.idProperty().unbind();
        root.styleProperty().unbind();
    }
}
