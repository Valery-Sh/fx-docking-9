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
package org.vns.javafx.dock.api;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.vns.javafx.dock.api.PalettePane.PaletteCategory;
import org.vns.javafx.dock.api.PalettePane.PaletteItem;
import org.vns.javafx.dock.api.PalettePane.PaletteModel;

/**
 *
 * @author Valery
 */
public class PalettePaneSkin extends SkinBase<PalettePane> {
    
    protected static double DEFAULT_GRAPHIC_WIDTH = 16;
    
    private ScrollPane layout;
    private VBox titledPaneLayout;

    public PalettePaneSkin(final PalettePane control) {
        super(control);
        init();

    }

    private void init() {
        titledPaneLayout = buildPalette(getSkinnable().getModel());
        layout = new ScrollPane(titledPaneLayout) {
                @Override
                protected void layoutChildren() {
                    layout.setVbarPolicy(getSkinnable().getScrollPaneVbarPolicy());
                    resizeLabels();
                    super.layoutChildren();
                }
            };
        layout.setFitToWidth(true);

        getChildren().add(layout);

        getSkinnable().scrollPaneVbarPolicy().addListener( (ovalue, oldValue, newValue) -> {
            layout.setVbarPolicy(newValue);
        });
        getSkinnable().dragNodeProperty().addListener( (ovalue, oldValue, newValue) -> {
            if ( oldValue == null && newValue != null ) {
                titledPaneLayout.getChildren().add(0,newValue);
            } else if (oldValue != null ) {
                titledPaneLayout.getChildren().set(0,newValue);
            }
        });
        
    }
    public void setScrollPaneVbarPolicy(ScrollPane.ScrollBarPolicy value) {
        layout.setVbarPolicy(value);
    }
    protected VBox buildPalette(PaletteModel model) {
        VBox titledBox = new VBox();/* {
            @Override
            protected void layoutChildren() {
                // has to be called first or layout is not correct sometimes
                //modifyItemsSize();
                resizeLabels();
                super.layoutChildren();
            }
        };
*/
        titledBox.setStyle("-fx-background-color: green");

        for (int i = 0; i < model.getCategories().size(); i++) {
            TitledPane titledPane = new TitledPane();
            titledPane.setExpanded(false);
            titledPane.setMinWidth(30);

            titledBox.getChildren().add(titledPane);
            titledPane.setText(model.getCategories().get(i).getLabel().getText());
            titledPane.setContent(model.getCategories().get(i).getGraphic());
        }
        return titledBox;
    }

    protected List<Label> getLabels() {
        List<Label> list = new ArrayList<>();
        PaletteModel model = getSkinnable().getModel();
        model.getCategories().forEach((pc) -> {
            pc.getItems().forEach((it) -> {
                list.add(it.getLabel());
            });
        });
        return list;
    }
    

    private double getLabelWidth(Label label) {
        Text tx = new Text(label.getText());
        tx.setFont(label.getFont());
        double graphicWidth = DEFAULT_GRAPHIC_WIDTH;
        if (label.getGraphic() != null) {
            graphicWidth = label.getGraphic().getLayoutBounds().getWidth();
            System.err.println("0 - graphicWidth = " + graphicWidth);
        }
        System.err.println("1 - graphicWidth = " + graphicWidth);
        double width = tx.getLayoutBounds().getWidth()
                + label.getInsets().getLeft()
                + label.getInsets().getLeft()
                + label.getGraphicTextGap()
                + graphicWidth;
        return width;
    }

    private void resizeLabels() {
        double labelMinWidth = 20;
        final List<Label> labels = getLabels();

        double widest = labelMinWidth;
        for (Label label : labels) {
            widest = Math.max(getLabelWidth(label), widest);
        }

        for (Label label : labels) {
            //label.setMinWidth(widest);
            //label.setMaxWidth(widest);
            label.setPrefWidth(widest);
        }

    }

/*    private void sizeLabel(Label label, double min, double pref, double max) {
        label.setMinWidth(min);
        label.setPrefWidth(pref);
        label.setMaxWidth(max);
    }

    protected void modifyItemsSize() {
        //Platform.runLater(() -> {
        //treeView.layout();

        //double rootLabelWidth = rootLabel.getLayoutBounds().getWidth();
        PaletteModel model = getSkinnable().getModel();
        double maxTextWidth = 0;
        for (PaletteCategory pc : model.getCategories()) {
            for (PaletteItem pi : pc.getItems()) {
                System.err.println("PADDING pi.getLabel() = " + pi.getLabel().getPadding());
                System.err.println("PADDING pi.getLabel().pref = " + pi.getLabel().getPrefWidth());
                Text tx = new Text(pi.getLabel().getText());
                tx.setFont(pi.getLabel().getFont());
                System.err.println("  --- pi.getLabel() tx = " + tx.getBoundsInLocal().getWidth());
                System.err.println("  --- pi.getLabel() gap = " + pi.getLabel().getOpaqueInsets());

                //13if (tx.getLayoutBounds().getWidth() > maxTextWidth) {
                if (pi.getLabel().getWidth() > maxTextWidth) {
                    //13maxTextWidth = tx.getLayoutBounds().getWidth();
                    maxTextWidth = pi.getLabel().getWidth();
                    System.err.println("maxTextWidth = " + maxTextWidth);
                    System.err.println("   --- label = " + pi.getLabel());
                    System.err.println("   --- label.text = " + pi.getLabel().getText());
                }
            }
        }
        for (PaletteCategory pc : model.getCategories()) {
            for (PaletteItem pi : pc.getItems()) {
                //double ins = rootLabel.getInsets().getLeft() + rootLabel.getInsets().getRight();
                System.err.println("pi.getLabel().width=" + pi.getLabel().getWidth());
                //System.err.println("   --- rootLabel.width = " + rootLabel.getWidth());
                //13pi.getLabel().setMinWidth(rootLabelWidth + maxTextWidth);
                //13pi.getLabel().setMaxWidth(rootLabelWidth + maxTextWidth);
                pi.getLabel().setMinWidth(maxTextWidth);
                pi.getLabel().setMaxWidth(maxTextWidth);

                //System.err.println("rootLabelWidth=" + rootLabelWidth);
                //System.err.println("   --- pi.getLabel().minWidth=" + pi.getLabel().getMinWidth());
                //System.err.println("   ---  pi.getLabel() ins = " + ins);
            }
        }
        //titledBox.getChildren().remove(rootLabel);
        //});
    }
*/
}
