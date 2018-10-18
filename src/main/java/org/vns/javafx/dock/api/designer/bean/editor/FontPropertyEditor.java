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

import java.util.List;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.vns.javafx.dock.api.designer.bean.editor.FontStringConverter.FamilyStringConverter;
import org.vns.javafx.dock.api.designer.bean.editor.FontStringConverter.SizeStringConverter;
import org.vns.javafx.dock.api.designer.bean.editor.FontStringConverter.StyleStringConverter;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.StringPropertyEditor;

/**
 *
 * @author Valery
 */
public class FontPropertyEditor extends ComboButtonPropertyEditor<Font> {

    private StringConverter<Font> stringConverter;
    private boolean editableBeforeBind;

    private StringPropertyEditor family;
    private StringPropertyEditor fontStyle;
    public SliderPropertyEditor size;

    private Labeled errorMessage;
    //public  DecimalPropertyEditor size;

    public FontPropertyEditor() {
        this(null);
    }

    public FontPropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
        stringConverter = new FontStringConverter();
        setPopupRoot(createPopupRoot());
    }

    public StringPropertyEditor getFamily() {
        return family;
    }

    public StringPropertyEditor getFontStyle() {
        return fontStyle;
    }

    public SliderPropertyEditor getSize() {
        return size;
    }

    @Override
    public void bind(ReadOnlyProperty<Font> property) {
        editableBeforeBind = isEditable();
        unbind();
        setBoundProperty(property);
        //setEditable(false);
        getButton().textProperty().bind(((ObjectExpression) property).asString());
    }

    @Override
    public void bindBidirectional(Property<Font> property) {
        editableBeforeBind = isEditable();
        unbind();
        setEditable(true);
        setBoundProperty(property);
        getButton().textProperty().bindBidirectional(property, stringConverter);
        if (getPopupRoot() != null) {
            
            family.getTextField().textProperty().bindBidirectional(property, new FamilyStringConverter(this));
            fontStyle.getTextField().textProperty().bindBidirectional(property, new StyleStringConverter(this));
            size.getTextField().textProperty().bindBidirectional(property, new SizeStringConverter(this));
        }

        /*        if (isRealTimeBinding()) {
            getTextField().textProperty().bindBidirectional(property, stringConverter);
        } else {
            getTextField().lastValidTextProperty().bindBidirectional(property, stringConverter);
        }
        createContextMenu(property);
         */
    }

    @Override
    public void unbind() {
        getButton().textProperty().unbind();
        if (getPopupRoot() != null) {
            family.getTextField().textProperty().unbind();
            fontStyle.getTextField().textProperty().unbind();
            size.getTextField().textProperty().unbind();
        }
        if (getBoundProperty() != null && (getBoundProperty() instanceof Property)) {
            getButton().textProperty().unbindBidirectional(getBoundProperty());
            family.getTextField().textProperty().unbindBidirectional(getBoundProperty());
            fontStyle.getTextField().textProperty().unbindBidirectional(getBoundProperty());
            size.getTextField().textProperty().unbindBidirectional(getBoundProperty());
            
        }
        setBoundProperty(null);

        setEditable(editableBeforeBind);
    }

    protected Parent createPopupRoot() {
        System.err.println("CREATE POPUP ROOT");
        VBox root = new VBox();
        root.setSpacing(10);
        root.setStyle("-fx-background-color: white;-fx-border-width: 1; -fx-border-color: gray ");
        root.setPadding(new Insets(10, 20, 20, 20));
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        //grid.setStyle("-fx-border-width: 1; -fx-border-color: gray");

        family = new StringPropertyEditor();
        Button familyButton = new Button();
        setDefaultButtonGraphic(familyButton);
        setDefaultLayout(familyButton);
        family.getButtons().add(familyButton);

        HyperlinkTitle familyTitle = family.getTitle();
        familyTitle.setText("Family");

        SmallContextMenu familyMenu = new SmallContextMenu();
        familyMenu.setContentHeight(500d);
        List<String> families = Font.getFamilies();
        families.sort((String o1, String o2) -> {
            if (o1.equals(o2)) {
                return 0;
            }
            return (o1.compareTo(o2));
        });

        for (String f : families) {
            MenuItem mi = new MenuItem(f);
            mi.setOnAction(a -> {
                System.err.println("family getTextField() = " + family.getTextField());
                family.getTextField().setText(mi.getText());
                List<String> styles = Util.getFontStyles(mi.getText(), 10);
            });
            familyMenu.getItems().add(mi);
        }
        familyButton.setContextMenu(familyMenu);
        familyButton.setOnAction(a -> {
            familyButton.getContextMenu().show(familyButton.getScene().getWindow());
        });
        addFamilyValidators();
        
        fontStyle = new StringPropertyEditor();
        HyperlinkTitle styleTitle = fontStyle.getTitle();
        styleTitle.setText("Style");
        ComboButton cbtn = new ComboButton();
        //    cbtn.getComboBox().getItems().add("Text1");
        fontStyle.getButtons().add(cbtn);
        addFontStyleValidators();
        
        size = new SliderPropertyEditor(0, 128, 0);
        HyperlinkTitle sizeTitle = size.getTitle();
        sizeTitle.setText("Size");
        size.getDecimalEditor().setRealTimeBinding(true);

        size.getDecimalEditor().setScale(0);

        sizeTitle.setText("Size");
        ComboButton.ItemsUpdater<String> updater = list -> {
            cbtn.getComboBox().getItems().clear();
            List<String> fontStyles = Util.getFontStyles(family.getTextField().getText(), Double.valueOf(size.getTextField().getText()));
            cbtn.getComboBox().getItems().addAll(fontStyles);
        };
        cbtn.setItemsUpdater(updater);

        grid.add(familyTitle, 0, 0);
        grid.add(family, 1, 0);

        grid.add(styleTitle, 0, 1);
        grid.add(fontStyle, 1, 1);

        grid.add(sizeTitle, 0, 2);
        grid.add(size, 1, 2);

        //grid.setStyle("-fx-border-width: 1");
        errorMessage = new Label("Error Messages");
        root.getChildren().addAll(errorMessage, grid);

        errorMessage.setAlignment(Pos.TOP_CENTER);
        errorMessage.setTextFill(Color.RED);
        errorMessage.setMaxWidth(1000);

        grid.setAlignment(Pos.CENTER);

        //family.bindBidirectional();
        return root;
    }

    protected void addFamilyValidators() {
        getFamily().getTextField().getValidators().add(item -> {
            boolean retval = false;
            if (Font.getFamilies().contains(item)) {
                retval = true;
            }
            //System.err.println("validator retval = " + retval);
            if (retval) {
                errorMessage.setVisible(false);
            } else {
                errorMessage.setText("No such font family found");
                errorMessage.setVisible(true);
            }
            return retval;
        });
    }

    protected void addFontStyleValidators() {
        getFontStyle().getTextField().getValidators().add(item -> {
            boolean retval = false;
            String fm = getFamily().getTextField().getText();
            double sz = Double.valueOf(getSize().getTextField().getText());

            if (Util.getFontStyles(fm, sz).contains(item)) {
                retval = true;
            }
            //System.err.println("validator retval = " + retval);
            if (retval) {
                errorMessage.setVisible(false);
            } else {
                errorMessage.setText("No such font style found");
                errorMessage.setVisible(true);
            }
            return retval;
        });
    }

    @Override
    public boolean isBound() {
        return getButton().textProperty().isBound() || getBoundProperty() != null
                || ( family != null && family.getTextField().textProperty().isBound())  
                || (fontStyle != null && fontStyle.getTextField().textProperty().isBound()) 
                || (size != null && size.getTextField().textProperty().isBound());
    }

}
