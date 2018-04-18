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
package org.vns.javafx.dock.api.designer.bean;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Olga
 */
public class InsetsPropertyEditor extends Control implements PropertyEditor<Insets> {

    private final ObjectProperty<Insets> editorInsets = new SimpleObjectProperty<>();

    private final DoubleTextField top;
    private final DoubleTextField right;
    private final DoubleTextField bottom;
    private final DoubleTextField left;

    private boolean createDefaultGraphics;
    
    private Node[] graphics;

    private final BooleanProperty editable = new SimpleBooleanProperty();

    private ChangeListener<Insets> editorInsetslistener;

    private final ChangeListener<Number> topValueInsetslistener = ((v, ov, nv) -> {
        setEditorInsets(new Insets((double) nv, getEditorInsets().getRight(), getEditorInsets().getBottom(), getEditorInsets().getLeft()));
    });
    private final ChangeListener<Number> rightValueInsetslistener = ((v, ov, nv) -> {
        setEditorInsets(new Insets(getEditorInsets().getTop(), (double) nv, getEditorInsets().getBottom(), getEditorInsets().getLeft()));
    });
    private final ChangeListener<Number> bottomValueInsetslistener = ((v, ov, nv) -> {
        setEditorInsets(new Insets(getEditorInsets().getTop(), getEditorInsets().getRight(), (double) nv, getEditorInsets().getLeft()));
    });
    private final ChangeListener<Number> leftValueInsetslistener = ((v, ov, nv) -> {
        setEditorInsets(new Insets(getEditorInsets().getTop(), getEditorInsets().getRight(), getEditorInsets().getBottom(), (double) nv));
    });

    public InsetsPropertyEditor(double topRightBottomLeft) {
        this(topRightBottomLeft, topRightBottomLeft, topRightBottomLeft, topRightBottomLeft);
    }

    public InsetsPropertyEditor(double top, double right, double bottom, double left) {
        this.editorInsets.set(new Insets(top, right, bottom, left));
        this.top = new DoubleTextField();
        this.right = new DoubleTextField();
        this.bottom = new DoubleTextField();
        this.left = new DoubleTextField();
        init();
    }

    private void init() {
        createDefaultGraphics = true;
        editorInsetslistener = (v, ov, nv) -> {
            if (nv != null) {
                top.setText(String.valueOf(nv.getTop()));
                right.setText(String.valueOf(nv.getRight()));
                bottom.setText(String.valueOf(nv.getBottom()));
                left.setText(String.valueOf(nv.getLeft()));
            }
        };
        editorInsetsProperty().addListener(editorInsetslistener);

    }

    public Node[] getGraphics() {
        return graphics;
    }

    /*    public void setGraphics(Node... node) {
    topGraphic = node[0];
    if (node.length > 1) {
    rightGraphic = node[1];
    }
    if (node.length > 2) {
    bottomGraphic = node[2];
    }
    if (node.length > 3) {
    bottomGraphic = node[3];
    }
    }
     */
    public void setGraphics(Node... graphics) {
        this.graphics = graphics;
    }

    public BooleanProperty editableProperty() {
        return editable;
    }

    @Override
    public boolean isEditable() {
        return editable.get();
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable.set(editable);
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

    public boolean isCreateDefaultGraphics() {
        return createDefaultGraphics;
    }

    public void setCreateDefaultGraphics(boolean createDefaultGraphics) {
        this.createDefaultGraphics = createDefaultGraphics;
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new InsetsPropertyEditorSkin(this);
    }

    @Override
    public void bind(Property<Insets> property) {
        editorInsetsProperty().removeListener(editorInsetslistener);
        removeTopRightBottopTopListeners();
        editorInsetsProperty().addListener(editorInsetslistener);
        setEditorInsets(new Insets(property.getValue().getRight(), property.getValue().getRight(), property.getValue().getBottom(), property.getValue().getLeft()));
        editorInsets.bind(property);
    }

    @Override
    public void bindBidirectional(Property<Insets> property) {
        editorInsetsProperty().removeListener(editorInsetslistener);
        removeTopRightBottopTopListeners();
        editorInsetsProperty().addListener(editorInsetslistener);
        addTopRightBottopTopListeners();
        setEditorInsets(new Insets(property.getValue().getRight(), property.getValue().getRight(), property.getValue().getBottom(), property.getValue().getLeft()));
        editorInsets.bindBidirectional(property);

    }

    protected void removeTopRightBottopTopListeners() {
        top.valueProperty().removeListener(topValueInsetslistener);
        right.valueProperty().removeListener(rightValueInsetslistener);
        bottom.valueProperty().removeListener(bottomValueInsetslistener);
        left.valueProperty().removeListener(leftValueInsetslistener);
    }

    protected void addTopRightBottopTopListeners() {
        top.valueProperty().addListener(topValueInsetslistener);
        right.valueProperty().addListener(rightValueInsetslistener);
        bottom.valueProperty().addListener(bottomValueInsetslistener);
        left.valueProperty().addListener(leftValueInsetslistener);

    }

    @Override
    public void unbind() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBound() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class InsetsPropertyEditorSkin extends SkinBase<InsetsPropertyEditor> {

        private Node[] graphics;

        private final GridPane grid;
      

        public InsetsPropertyEditorSkin(InsetsPropertyEditor control) {
            super(control);
            graphics = getSkinnable().getGraphics();

            grid = new GridPane();

            int row = 0;

            if (graphics != null) {
                row = 1;
                for (int i = 0; i < graphics.length; i++) {
                    StackPane sp = new StackPane();
                    sp.setPadding(new Insets(0,0,2,0));
                    if (graphics[i] == null) {
                        Side side = null;
                        switch (i) {
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
                        System.err.println("CREATE DEFAULT GRAPHIC");
                        graphics[i] = createDefaultGraphic(side);
                        sp.getChildren().add(graphics[i]);
                        sp.setAlignment(Pos.CENTER);
                        
                    }
                    grid.add(sp, i, 0);
                }
            }

            //grid = new HBox(getSkinnable().getTextField(),getSkinnable().getSlider());
            grid.add(getSkinnable().top, 0, row);
            grid.add(getSkinnable().right, 1, row);
            grid.add(getSkinnable().bottom, 2, row);
            grid.add(getSkinnable().left, 3, row);

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

            getChildren().add(grid);
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
    }// Skin

}
