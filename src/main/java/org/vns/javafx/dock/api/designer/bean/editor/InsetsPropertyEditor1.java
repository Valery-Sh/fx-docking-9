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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Skin;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.vns.javafx.dock.api.designer.DesignerLookup;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.DoublePropertyEditor;

/**
 *
 * @author Olga
 */
public class InsetsPropertyEditor1 extends AbstractPropertyEditor<Insets> {

    private final ObjectProperty<Insets> editorInsets = new SimpleObjectProperty<>();

    private final DoublePropertyEditor top;
    private final DoublePropertyEditor right;
    private final DoublePropertyEditor bottom;
    private final DoublePropertyEditor left;

    private final BooleanProperty decorated = new SimpleBooleanProperty(true);

    //private final BooleanProperty editable = new SimpleBooleanProperty(true);
    private ChangeListener<Insets> editorInsetslistener;

    private final ChangeListener<String> topValueInsetslistener = ((v, ov, nv) -> {
        setEditorInsets(new Insets(Double.valueOf(nv), getEditorInsets().getRight(), getEditorInsets().getBottom(), getEditorInsets().getLeft()));
    });
    private final ChangeListener<String> rightValueInsetslistener = ((v, ov, nv) -> {
        setEditorInsets(new Insets(getEditorInsets().getTop(), Double.valueOf(nv), getEditorInsets().getBottom(), getEditorInsets().getLeft()));
    });
    private final ChangeListener<String> bottomValueInsetslistener = ((v, ov, nv) -> {
        setEditorInsets(new Insets(getEditorInsets().getTop(), getEditorInsets().getRight(), Double.valueOf(nv), getEditorInsets().getLeft()));
    });
    private final ChangeListener<String> leftValueInsetslistener = ((v, ov, nv) -> {
        setEditorInsets(new Insets(getEditorInsets().getTop(), getEditorInsets().getRight(), getEditorInsets().getBottom(), Double.valueOf(nv)));
    });

    public InsetsPropertyEditor1() {
        this(0d);
    }

    public InsetsPropertyEditor1(double topRightBottomLeft) {
        this(topRightBottomLeft, topRightBottomLeft, topRightBottomLeft, topRightBottomLeft);
    }

    public InsetsPropertyEditor1(double top, double right, double bottom, double left) {
        this(null, top, right, bottom, left);
    }

    public InsetsPropertyEditor1(String name) {
        this(name, 0, 0, 0, 0);
    }

    public InsetsPropertyEditor1(String name, double top, double right, double bottom, double left) {
        super(name);
        this.editorInsets.set(new Insets(top, right, bottom, left));
        this.top = new DoublePropertyEditor();
        this.top.setMenuButtonAllignment(SidePos.NO);
        this.top.getStyleClass().add("top-inset");
        this.right = new DoublePropertyEditor();
        this.right.setMenuButtonAllignment(SidePos.NO);
        this.right.getStyleClass().add("right-inset");
        this.bottom = new DoublePropertyEditor();
        this.bottom.setMenuButtonAllignment(SidePos.NO);
        this.bottom.getStyleClass().add("bottom-inset");
        this.left = new DoublePropertyEditor();
        this.left.setMenuButtonAllignment(SidePos.NO);
        this.left.getStyleClass().add("left-inset");
        init();
    }

    private void init() {
        getStyleClass().add("insets-property-editor");
        editorInsetslistener = (v, ov, nv) -> {
            if (nv != null) {
                top.getTextField().setText(String.valueOf(nv.getTop()));
                right.getTextField().setText(String.valueOf(nv.getRight()));
                bottom.getTextField().setText(String.valueOf(nv.getBottom()));
                left.getTextField().setText(String.valueOf(nv.getLeft()));
            }
        };
        editorInsetsProperty().addListener(editorInsetslistener);
        editableProperty().addListener((v, ov, nv) -> {
            top.setEditable(nv);
            right.setEditable(nv);
            bottom.setEditable(nv);
            left.setEditable(nv);
        });
    }

    public Insets getEditorInsets() {
        return editorInsets.get();
    }

    public void setEditorInsets(Insets editorInsets) {
        this.editorInsets.set(editorInsets);
    }

    public ObjectProperty<Insets> editorInsetsProperty() {
        return editorInsets;
    }

    public BooleanProperty decoratedProperty() {
        return decorated;
    }

    public boolean isDecorated() {
        return decorated.get();
    }

    public void setDecorated(boolean decorated) {
        this.decorated.set(decorated);
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new InsetsPropertyEditorSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    public void bind(ReadOnlyProperty<Insets> property) {
        unbind();
        setBoundProperty(property);
        setEditable(false);
        editorInsetsProperty().removeListener(editorInsetslistener);
        removeTopRightBottopTopListeners();
        editorInsetsProperty().addListener(editorInsetslistener);
        setEditorInsets(new Insets(property.getValue().getRight(), property.getValue().getRight(), property.getValue().getBottom(), property.getValue().getLeft()));
        editorInsets.bind(property);
    }

    @Override
    public void bindBidirectional(Property<Insets> property) {
        unbind();
        setBoundProperty(property);
        setEditable(true);
        editorInsetsProperty().removeListener(editorInsetslistener);
        removeTopRightBottopTopListeners();
        editorInsetsProperty().addListener(editorInsetslistener);
        addTopRightBottopTopListeners();
//        if ( property.getValue() == null ) {

//        }
//        setEditorInsets(new Insets(property.getValue().getRight(), property.getValue().getRight(), property.getValue().getBottom(), property.getValue().getLeft()));
        editorInsets.bindBidirectional(property);

    }

    protected void removeTopRightBottopTopListeners() {
        top.getTextField().lastValidTextProperty().removeListener(topValueInsetslistener);
        right.getTextField().lastValidTextProperty().removeListener(rightValueInsetslistener);
        bottom.getTextField().lastValidTextProperty().removeListener(bottomValueInsetslistener);
        left.getTextField().lastValidTextProperty().removeListener(leftValueInsetslistener);
    }

    protected void addTopRightBottopTopListeners() {
        top.getTextField().lastValidTextProperty().addListener(topValueInsetslistener);
        right.getTextField().lastValidTextProperty().addListener(rightValueInsetslistener);
        bottom.getTextField().lastValidTextProperty().addListener(bottomValueInsetslistener);
        left.getTextField().lastValidTextProperty().addListener(leftValueInsetslistener);
    }

    @Override
    public void unbind() {
        editorInsets.unbind();
        removeTopRightBottopTopListeners();
        if (getBoundProperty() != null && (getBoundProperty() instanceof Property)) {
            editorInsets.unbindBidirectional((Property) getBoundProperty());
        }
        editorInsetsProperty().removeListener(editorInsetslistener);
        removeTopRightBottopTopListeners();
        
        setBoundProperty(null);
    }

    @Override
    public boolean isBound() {
        return editorInsets.isBound() || getBoundProperty() != null;
    }

    @Override
    protected Node createEditorNode() {
        return new GridPane();
    }

    public static class InsetsPropertyEditorSkin extends AbstractPropertyEditorSkin {

        private final Node[] graphics;
        private final GridPane grid;

        private final InsetsPropertyEditor1 control;

        public InsetsPropertyEditorSkin(InsetsPropertyEditor1 control) {
            super(control);
            this.control = control;

            graphics = new Node[]{new StackPane(), new StackPane(), new StackPane(), new StackPane()};
            grid = (GridPane) control.getEditorNode();

            control.decoratedProperty().addListener((v, ov, nv) -> {
                if (nv) {
                    decorateDefault();
                } else {
                    decorateImage();
                }
            });
            adustEditable(control.isEditable());
            control.editableProperty().addListener((v, ov, nv) -> {
                adustEditable(nv);
            });

            for (int i = 0; i < graphics.length; i++) {
                grid.add(graphics[i], i, 0);
            }

            if (control.isDecorated()) {
                decorateDefault();
            } else {
                decorateImage();
            }

            grid.add(control.top, 0, 1);
            grid.add(control.right, 1, 1);
            grid.add(control.bottom, 2, 1);
            grid.add(control.left, 3, 1);
            ColumnConstraints column0 = new ColumnConstraints();
            column0.setPercentWidth(25);
            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(25);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setPercentWidth(25);
            ColumnConstraints column3 = new ColumnConstraints();
            column3.setPercentWidth(25);

            grid.getColumnConstraints().addAll(column0, column1, column2, column3);

            grid.setHgap(10);

            //getChildren().add(grid);
        }

        private Side getSide(int idx) {
            Side side = null;

            switch (idx) {
                case 0:
                    side = Side.TOP;
                    break;
                case 1:
                    side = Side.RIGHT;
                    break;
                case 2:
                    side = Side.BOTTOM;
                    break;
                case 3:
                    side = Side.LEFT;
                    break;
                default:
                    break;
            }
            return side;

        }

        protected void decorateDefault() {
            for (int i = 0; i < graphics.length; i++) {
                StackPane sp = (StackPane) graphics[i];
                Side side = getSide(i);
                sp.getChildren().clear();
                sp.getChildren().add(createDefaultGraphic(side));
                sp.setStyle("-fx-alignment: center; -fx-padding: 0 0 2 0");
            }
        }

        protected void decorateImage() {
            for (int i = 0; i < graphics.length; i++) {
                StackPane sp = (StackPane) graphics[i];
                sp.setStyle("");
                Side side = getSide(i);
                String css = side.name().toLowerCase() + "-inset-pane";

                sp.getStyleClass().remove(css);
                sp.getStyleClass().add(css);

                css = side.name().toLowerCase() + "-inset-image";
                sp.getChildren().clear();
                ImageView iv = new ImageView();
                iv.getStyleClass().add(css);
                sp.getChildren().add(iv);
            }

        }

        protected Node createDefaultGraphic(Side side) {
            double cw = 12;
            double ch = 12;
            double sw = 1; //stroke width
            double swDelta = 0; //stroke width

            Canvas retval = new Canvas(cw, ch);
            GraphicsContext gc = retval.getGraphicsContext2D();

            gc.setFill(Color.LIGHTGRAY);
            gc.setStroke(Color.BLACK);
            gc.fillRect(0, 0, cw, ch);

            gc.clearRect(sw + 1, sw + 1, cw - 2 * sw - 2, ch - 2 * sw - 2);
            gc.setFill(Color.BLACK);
            if (null != side) {
                switch (side) {
                    case TOP:
                        gc.fillRect(sw, sw, cw - 2 * sw, sw + swDelta);
                        break;
                    case RIGHT:
                        gc.fillRect(cw - 2 * sw - swDelta, sw, sw + swDelta, cw - 2 * sw);
                        break;
                    case BOTTOM:
                        gc.fillRect(sw, ch - 2 * sw - swDelta, cw - 2 * sw, sw + swDelta);
                        break;
                    case LEFT:
                        gc.fillRect(sw, sw, sw + swDelta, ch - 2 * sw);
                        break;
                    default:
                        break;
                }
            }
            return retval;
        }

        protected void adustEditable(boolean editable) {
            control.top.setEditable(editable);
            control.right.setEditable(editable);
            control.bottom.setEditable(editable);
            control.left.setEditable(editable);
        }
    }// Skin

}//InsetsPropertyEditor
