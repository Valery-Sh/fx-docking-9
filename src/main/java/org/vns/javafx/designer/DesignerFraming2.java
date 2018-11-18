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

import org.vns.javafx.dock.api.dragging.view.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Selection;

/**
 *
 * @author Olga
 */
public class DesignerFraming2 extends AbstractNodeFraming {

    @Override
    protected void initializeOnShow(Node node) {

        //System.err.println("DesignerFraming2: initializeOnShow node = " + node);
        SceneView sv = DesignerLookup.lookup(SceneView.class);
        Parent p = EditorUtil.getTopParentOf(node);
        if (p != null && node != sv.getRoot()) {
            FramePane resizePane = SceneView.getResizeFrame();
            resizePane.setBoundNode(node);
            if (node.getParent() != null && node.getParent() != sv.getRoot()) {
                FramePane parentPane = SceneView.getParentFrame();
                parentPane.setBoundNode(node.getParent());
            }
        }

        Selection sel = DockRegistry.lookup(Selection.class);
        if (sel != null) {
            sel.notifySelected(node);
        }
    }

    @Override
    protected void finalizeOnHide(Node node) {
        Parent p = EditorUtil.getTopParentOf(node);
        if (p != null) {
            FramePane resizePane = SceneView.getResizeFrame();
            if (resizePane != null) {
                resizePane.setBoundNode(null);
            }
            FramePane parentPane = SceneView.getParentFrame();
            if (parentPane != null) {
                parentPane.setBoundNode(null);
            }

        }

    }

    public DesignerFraming2() {

    }

}
