package org.vns.javafx.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DockTitleBar extends HBox {

    public static final PseudoClass CHOOSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("choosed");
    
    public enum StyleClasses {
        TITLE_BAR,
        PIN_BUTTON,
        CLOSE_BUTTON,
        STATE_BUTTON,
        MENU_BUTTON,
        LABEL;

        public String cssClass() {
            String retval = null;
            switch (this) {
                case TITLE_BAR:
                    retval = "dock-title-bar";
                    break;
                case PIN_BUTTON:
                    retval = "dock-title-pin-button";
                    break;
                case CLOSE_BUTTON:
                    retval = "dock-title-close-button";
                    break;
                case STATE_BUTTON:
                    retval = "dock-title-state-button";
                    break;
                case MENU_BUTTON:
                    retval = "dock-title-menu-button";
                    break;
                case LABEL:
                    retval = "dock-title-label";
                    break;
            }
            return retval;
        }
    }
    private Dockable dockNode;
    //private DockDragboard dragboard;

    private final ObjectProperty<Label> label = new SimpleObjectProperty<>();
    private final  ObjectProperty<Button> closeButton = new SimpleObjectProperty<>();
    private final  ObjectProperty<Button> stateButton = new SimpleObjectProperty<>();
    private  final ObjectProperty<Button> pinButton = new SimpleObjectProperty<>();
    
    private String title;
    
    private BooleanProperty selectedPseudoClass = new SimpleBooleanProperty();

    public DockTitleBar() {
        this.title = "";
        init();
    }
    
    public DockTitleBar(String title) {
        this.title = title;
        init();
    }
    
    public DockTitleBar(Dockable dockNode) {
        this.dockNode = dockNode;
        init();
    }

    public DockTitleBar(Dockable dockNode, String title) {
        this.dockNode = dockNode;
        this.title = title;
        init();
    }

    private void init() {

        setLabel(new Label(title));
        if ( dockNode != null ) {
            getLabel().textProperty().bind(dockNode.getContext().titleProperty());
        }

        setStateButton(new Button());
        setCloseButton(new Button());
        setPinButton(new Button());
        
        

        Pane fillPane = new Pane();
        HBox.setHgrow(fillPane, Priority.ALWAYS);

        getChildren().addAll(getLabel(), fillPane, getPinButton(), getStateButton(), getCloseButton());

        getLabel().getStyleClass().add(StyleClasses.LABEL.cssClass());
        getLabel().getStyleClass().add("title-bar-label");
        
        getCloseButton().getStyleClass().add(StyleClasses.CLOSE_BUTTON.cssClass());
        getStateButton().getStyleClass().add(StyleClasses.STATE_BUTTON.cssClass());
        getPinButton().getStyleClass().add(StyleClasses.PIN_BUTTON.cssClass());
        this.getStyleClass().add(StyleClasses.TITLE_BAR.cssClass());
        setOnMouseClicked(ev -> {
            getCloseButton().requestFocus();
        });
        getCloseButton().addEventFilter(MouseEvent.MOUSE_CLICKED, this::closeButtonClicked);
        getStateButton().setTooltip(new Tooltip("Undock pane"));
        getCloseButton().setTooltip(new Tooltip("Close pane"));
        getPinButton().setTooltip(new Tooltip("Pin pane"));
    }
    
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public void removeButtons(Button... btns) {
        for ( Button b : btns) {
            getChildren().remove(b);
        }
    }
    protected void closeButtonClicked(MouseEvent ev) {
        if ( dockNode == null ) {
            return;
        }
        DockableContext sp = dockNode.getContext();
        if (sp.isFloating() && (getScene().getWindow() instanceof Stage)) {
            ((Stage) getScene().getWindow()).close();
        } else {
            ((Window) getScene().getWindow()).hide();
        }

    }
    public Dockable getOwner() {
        if (dockNode == null) {
            return null;
        }
        return dockNode;
    }
    public ObjectProperty<Label> labelProperty() {
        return label;
    }
    public Label getLabel() {
        return label.get();
    }

    public void setLabel(Label label) {
        this.label.set(label);
    }
    public ObjectProperty<Button> closeButtonProperty() {
        return closeButton;
    }
    public ObjectProperty<Button> stateButtonProperty() {
        return stateButton;
    }
    public ObjectProperty<Button> pinButtonProperty() {
        return pinButton;
    }

    public void setCloseButton(Button closeButton) {
        this.closeButton.set(closeButton);
    }

    public void setStateButton(Button stateButton) {
        this.stateButton.set(stateButton);
    }

    public void setPinButton(Button pinButton) {
        this.pinButton.set(pinButton);
    }

    public Button getCloseButton() {
        return closeButton.get();
    }

    public Button getStateButton() {
        return stateButton.get();
    }

    public Button getPinButton() {
        return pinButton.get();
    }
    public boolean isSelectedPseudoClass() {
        return selectedPseudoClass.get();
    }

    public void setSelectedPseudoClass(boolean newValue) {
        if ( newValue ) {
            turnOnSelectedPseudoClass();
        } else {
            turnOffSelectedPseudoClass();
        }
        this.selectedPseudoClass.set(newValue);
            
    }

    public BooleanProperty selectedPseudoClassProperty() {
        return selectedPseudoClass;
    }
    protected void turnOffSelectedPseudoClass() {
        pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, false);                
    }

    protected void turnOnSelectedPseudoClass() {
        if ( selectedPseudoClass.get() ) {
            return;
        }
        pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, true);                
   }

}
