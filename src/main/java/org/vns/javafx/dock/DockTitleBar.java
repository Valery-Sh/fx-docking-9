package org.vns.javafx.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    private Label label;
    private Button closeButton;
    private Button stateButton;
    private Button pinButton;
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

        label = new Label(title);
        if ( dockNode != null ) {
            label.textProperty().bind(dockNode.getDockableContext().titleProperty());
        }

        stateButton = new Button();
        closeButton = new Button();
        pinButton = new Button();

        Pane fillPane = new Pane();
        HBox.setHgrow(fillPane, Priority.ALWAYS);

        getChildren().addAll(label, fillPane, pinButton, stateButton, closeButton);

        label.getStyleClass().add(StyleClasses.LABEL.cssClass());
        label.getStyleClass().add("title-bar-label");
        
        closeButton.getStyleClass().add(StyleClasses.CLOSE_BUTTON.cssClass());
        stateButton.getStyleClass().add(StyleClasses.STATE_BUTTON.cssClass());
        pinButton.getStyleClass().add(StyleClasses.PIN_BUTTON.cssClass());
        this.getStyleClass().add(StyleClasses.TITLE_BAR.cssClass());
        setOnMouseClicked(ev -> {
            closeButton.requestFocus();
            //03.09 dockNode.getDockableContext().dockable().node().toFront();
        });
        closeButton.addEventFilter(MouseEvent.MOUSE_CLICKED, this::closeButtonClicked);
        stateButton.setTooltip(new Tooltip("Undock pane"));
        closeButton.setTooltip(new Tooltip("Close pane"));
        pinButton.setTooltip(new Tooltip("Pin pane"));
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
        DockableContext sp = dockNode.getDockableContext();
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

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public void setCloseButton(Button closeButton) {
        this.closeButton = closeButton;
    }

    public void setStateButton(Button stateButton) {
        this.stateButton = stateButton;
    }

    public void setPinButton(Button pinButton) {
        this.pinButton = pinButton;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public Button getStateButton() {
        return stateButton;
    }

    public Button getPinButton() {
        return pinButton;
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
