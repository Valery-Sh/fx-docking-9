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
package org.vns.javafx.dock.api.demo;

import java.lang.ref.WeakReference;
import javafx.beans.WeakListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Valery
 */
public class SampleBindingByInsets implements WeakListener {

    private final WeakReference<ObjectProperty<Insets>> editorInsets;
    private final WeakReference<ObjectProperty<Insets>> margin;
    private Parent node;

    private InsetsConverter converter;

    private boolean updating = false;
    private boolean bound = false;
    private final ChangeListener<? super Insets> editorListener = (v, ov, nv) -> {
        editorInsetsChanged(v, ov, nv);
    };
    private final ChangeListener<? super Insets> marginListener = (v, ov, nv) -> {
        marginChanged(v, ov, nv);
    };

    public SampleBindingByInsets(Parent node, ObjectProperty<Insets> editorInsets, ObjectProperty<Insets> margin) {
        this.editorInsets = new WeakReference<>(editorInsets);
        this.margin = new WeakReference<>(margin);
        this.node = node;
        converter = getInsetsConverter();
    }

    public void editorInsetsChanged(ObservableValue<? extends Insets> observable, Insets oldValue, Insets newValue) {
        if (!updating) {

            final ObjectProperty<Insets> edIns = editorInsets.get();
            final ObjectProperty<Insets> mIns = margin.get();

            if (edIns == null || mIns == null) {
                if (edIns != null) {
                    edIns.removeListener(editorListener);
                }
                if (mIns != null) {
                    mIns.removeListener(marginListener);
                }
                return;
            }

            try {
                updating = true;
                if (newValue == null) {
                    return;
                }
                edIns.set(converter.toMargin(newValue));
            } finally {
                updating = false;
            }

        }
    }

    public Insets getMarginInsets() {
        return margin.get().get();
    }

    public void setMarginInsets(Insets insets) {
        this.margin.get().set(insets);
    }
    public Parent getNode() {
        return node;
    }

    public void setNode(Parent node) {
        this.node = node;
    }
    
    
    
    @Override
    public boolean wasGarbageCollected() {
        return (editorInsets.get() == null) || (margin.get() == null);
    }

    @Override
    public int hashCode() {
        final ObjectProperty<Insets> ls = editorInsets.get();
        final ObjectProperty<Insets> sp = margin.get();
        final int hc1 = (ls == null) ? 0 : ls.hashCode();
        final int hc2 = (sp == null) ? 0 : sp.hashCode();
        return hc1 * hc2;
    }

/*    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final Object ls = editorInsets.get();
        final Object sp = margin.get();
        if ((ls == null) || (sp == null)) {
            return false;
        }
/
        if (obj instanceof MarginBinding) {
            final MarginBinding otherBinding = (MarginBinding) obj;
            final Object o1 = otherBinding.editorInsets.get();
            final Object o2 = otherBinding.margin.get();
            if ((o1 == null) || (o2 == null)) {
                return false;
            }

            if ((ls == o1) && (sp == o2)) {
                return true;
            }
            if ((ls == o2) && (sp == o1)) {
                return true;
            }
        }
        return false;
    }
*/
    public void marginChanged(ObservableValue<? extends Insets> observable, Insets oldValue, Insets newValue) {
        if (!updating) {

            final ObjectProperty<Insets> edIns = editorInsets.get();
            final ObjectProperty<Insets> mIns = margin.get();

            if (edIns == null || mIns == null) {
                if (edIns != null) {
                    edIns.removeListener(editorListener);
                }
                if (mIns != null) {
                    mIns.removeListener(marginListener);
                }
                return;
            }

            try {
                updating = true;
                if (newValue == null) {
                    return;
                }
                edIns.set(converter.fromMargin(newValue));
            } finally {
                updating = false;
            }

        }
    }

    public void bind() {
        if (converter == null) {
            return;
        }
        //margin.get().set( converter.toMargin(editorInsets.get().get() ) );
        margin.get().set(converter.toMargin(editorInsets.get().get()));
        editorInsets.get().addListener(editorListener);
        //stringRef.get().addListener(this);
        bound = true;
    }

    public void bindBidirectional() {
        if (converter == null) {
            return;
        }

        margin.get().set(converter.toMargin(editorInsets.get().get()));
        editorInsets.get().addListener(editorListener);
        margin.get().addListener(marginListener);
        bound = true;
    }

    public void unbind() {
        editorInsets.get().removeListener(editorListener);
        margin.get().removeListener(marginListener);
        //margin.get().set(null);

        bound = false;
    }

    public boolean isBound() {
        return bound;
    }

    protected InsetsConverter getInsetsConverter() {
        return new InsetsConverter(this);
/*        if (node.getParent() instanceof HBox) {
            return new HBoxConstraintConverter(node);
        } else if (node.getParent() instanceof VBox) {
            return new VBoxConstraintConverter(node);
        } else if (node.getParent() instanceof StackPane) {
            return new StackPaneConstraintConverter(node);
        } else if (node.getParent() instanceof FlowPane) {
            return new FlowPaneConstraintConverter(node);
        } else if (node.getParent() instanceof BorderPane) {
            return new BorderPaneConstraintConverter(node);
        } else if (node.getParent() instanceof GridPane) {
            return new GridPaneConstraintConverter(node);
        }
        return null;
        */
    }

    public static interface InsetsConverter1 {

        Insets fromConstraint(Insets from);

        Insets toConstraint(Insets to);

        void setNode(Parent node);
    }

    public static class InsetsConverter {

        private Parent node;
        private SampleBindingByInsets binding;
                
        public InsetsConverter(SampleBindingByInsets binding) {
            this.binding = binding;
            this.node = binding.getNode();
        }

        public Insets fromMargin(Insets marginInsets) {
            System.err.println("fromMargin() from marginInsets = " + marginInsets);
            if (marginInsets == null) {
                System.err.println("fromMargin() updateMargin(null)");
                binding.setMarginInsets(null);
                //return Insets.EMPTY;
                return null;
            }
            return new Insets(marginInsets.getTop(), marginInsets.getRight(), marginInsets.getBottom(), marginInsets.getLeft());
            //return new Insets(AnchorPane.getTopAnchor(node),AnchorPane.getRightAnchor(node),AnchorPane.getBottomAnchor(node),AnchorPane.getLeftAnchor(node));
        }

        public Insets toMargin(Insets insets) {
            System.err.println("toMargin() insets = " + insets);
            
            if ( insets == null ) {
                System.err.println("toMargin() updateMargin(null)");
                updateMargin(null);
                //return Insets.EMPTY;
                return null;
            }
            Insets ins = new Insets(insets.getTop(), insets.getRight(), insets.getBottom(), insets.getLeft());
            updateMargin(ins);
            return ins;
            /*            AnchorPane.setTopAnchor(node, insets.getTop());
            AnchorPane.setRightAnchor(node, insets.getRight());
            AnchorPane.setBottomAnchor(node, insets.getBottom());
            AnchorPane.setLeftAnchor(node, insets.getLeft());
            
            return new Insets(AnchorPane.getTopAnchor(node),AnchorPane.getRightAnchor(node),AnchorPane.getBottomAnchor(node),AnchorPane.getLeftAnchor(node));
             */
        }

        protected void updateMargin(Insets insets) {
            System.err.println("updateMargin insets = " + insets);
            if ( node.getParent() == null ) {
                System.err.println("updateMargin parent == null");
                binding.setMarginInsets(null);
            } else if (node.getParent() instanceof HBox) {
                HBox.setMargin(node, insets);
            } else if (node.getParent() instanceof VBox) {
                if ( insets.equals(Insets.EMPTY)) {
                    System.err.println("set updateMargin(NULL");
                    VBox.setMargin(node, null);
                } else {
                    System.err.println("set updateMargin insets = " + insets);
                    VBox.setMargin(node, insets);
                }
            } else if (node.getParent() instanceof StackPane) {
                StackPane.setMargin(node, insets);
            } else if (node.getParent() instanceof FlowPane) {
                FlowPane.setMargin(node, insets);
            } else if (node.getParent() instanceof BorderPane) {
                BorderPane.setMargin(node, insets);

            } else if (node.getParent() instanceof GridPane) {
                GridPane.setMargin(node, insets);
            }
        }

        public Parent getNode() {
            return node;
        }

        public void setNode(Parent node) {
            this.node = node;
        }

    }//InsetsConvertor

}
