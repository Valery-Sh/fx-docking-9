package org.vns.javafx.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.Dockable;
import static org.vns.javafx.dock.api.properties.TitleBarProperty.CHOOSED_PSEUDO_CLASS;

/**
 *
 * @author Valery
 */
public class DockTitleBar extends HBox {

    
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
    private final Dockable dockNode;
    //private DockDragboard dragboard;

    private Label label;
    private Button closeButton;
    private Button stateButton;
    private Button pinButton;

    private BooleanProperty activeChoosedPseudoClassProperty = new SimpleBooleanProperty();
    
    public DockTitleBar(Dockable dockNode) {
        this.dockNode = dockNode;
        init();
    }

    public DockTitleBar(Dockable dockNode, String title) {
        this.dockNode = dockNode;
        init();
    }

    private void init() {

        label = new Label("Dock Title Bar");
        label.textProperty().bind(dockNode.nodeHandler().titleProperty());

        stateButton = new Button();
        closeButton = new Button();
        pinButton = new Button();

        Pane fillPane = new Pane();
        HBox.setHgrow(fillPane, Priority.ALWAYS);

        getChildren().addAll(label, fillPane, pinButton, stateButton, closeButton);

        label.getStyleClass().add(StyleClasses.LABEL.cssClass());
        
        closeButton.getStyleClass().add(StyleClasses.CLOSE_BUTTON.cssClass());
        stateButton.getStyleClass().add(StyleClasses.STATE_BUTTON.cssClass());
        pinButton.getStyleClass().add(StyleClasses.PIN_BUTTON.cssClass());
        this.getStyleClass().add(StyleClasses.TITLE_BAR.cssClass());
        setOnMouseClicked(ev -> {
            closeButton.requestFocus();
            dockNode.nodeHandler().node().toFront();
        });
        closeButton.addEventFilter(MouseEvent.MOUSE_CLICKED, this::closeButtonClicked);
        stateButton.setTooltip(new Tooltip("Undock pane"));
        closeButton.setTooltip(new Tooltip("Close pane"));
        pinButton.setTooltip(new Tooltip("Pin pane"));
    }
    
    public void removeButtons(Button... btns) {
        for ( Button b : btns) {
            getChildren().remove(b);
        }
    }
    protected void closeButtonClicked(MouseEvent ev) {
        DockNodeHandler sp = dockNode.nodeHandler();
        if (sp.isFloating() && (getScene().getWindow() instanceof Stage)) {
            ((Stage) getScene().getWindow()).close();
        } else {
            //sp.setFloating(true);
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

    public Button getCloseButton() {
        return closeButton;
    }

    public Button getStateButton() {
        return stateButton;
    }

    public Button getPinButton() {
        return pinButton;
    }
    public boolean isActiveChoosedPseudoClass() {
        return activeChoosedPseudoClassProperty.get();
    }

    public void setActiveChoosedPseudoClass(boolean newValue) {
        if ( newValue ) {
            turnOnChoosedPseudoClass();
        } else {
            turnOffChoosedPseudoClass();
        }
        this.activeChoosedPseudoClassProperty.set(newValue);
            
    }

    public BooleanProperty activeChoosedPseudoClassProperty() {
        return activeChoosedPseudoClassProperty;
    }
    protected void turnOffChoosedPseudoClass() {
        pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, false);                
    }

    protected void turnOnChoosedPseudoClass() {
        if ( activeChoosedPseudoClassProperty.get() ) {
            return;
        }
        pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, true);                
   }

}
