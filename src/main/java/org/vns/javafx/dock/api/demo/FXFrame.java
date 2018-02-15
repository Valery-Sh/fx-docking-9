package org.vns.javafx.dock.api.demo;

import com.sun.javafx.stage.StageHelper;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.vns.javafx.dock.DockBorderPane;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

public class FXFrame {

    JFrame frame;
    JPanel jPanel;
    JButton jbutton;
    JFXPanel fxPanel;
    Scene scene;
    DockNode formNode;
    //Stage formStage;
    DockBorderPane mainBorderPane;
    //Stage floatStage;
    ObjectProperty<Integer> jwindowState = new SimpleObjectProperty<>(-1);
    ObjectProperty<Integer> jwindowActivities = new SimpleObjectProperty<>(-1);

    public FXFrame() {
    }

    private void initAndShowGUI() {
        // This method is invoked on the EDT thread
        fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(300, 200);
        frame.setVisible(true);

        Platform.setImplicitExit(false); //prevens hangs on repeated creating
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);

                jwindowState.addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                        System.err.println("WINDOW STATE CHANGED oldValue = " + oldValue + "; newValue=" + newValue);
                    }

                });
                jwindowActivities.addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                        System.err.println("WINDOW ACTIVITIES CHANGED oldValue = " + oldValue + "; newValue=" + newValue);
                    }

                });

                Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
                Dockable.initDefaultStylesheet(null);
            }
        });
    }

    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        scene = createScene();
        fxPanel.setScene(scene);
//        BorderPane bp = new BorderPane();
//        scene.getWindow().getProperties().put(JFXDragManager.DRAG_FLOATING_STAGE, floatStage);
//        scene.getWindow().getProperties().put(JFXDragManager.DRAG_PANE_KEY, mainBorderPane);        
        DockRegistry.register(scene.getWindow());
    }

    private Scene createScene() {
        mainBorderPane = new DockBorderPane();
        scene = new Scene(mainBorderPane, Color.AQUA);
        formNode = new DockNode("To Edit");
        Button b = new Button(" TTTTTT");
        mainBorderPane.setBottom(b);
        Button content = new Button("DockNode Content");
        content.setOnAction((ActionEvent a) -> {
            Node parent = content.getParent();
            while (parent != null) {
                System.err.println("PARENT: " + parent.getClass().getName());
                parent = parent.getParent();
            }
            PopupControl pc = ((PopupControl) content.getScene().getWindow());
            System.err.println("maxW=" + ((Pane) pc.getScene().getRoot()).getMaxWidth());
            System.err.println("minW=" + ((Pane) pc.getScene().getRoot()).getMinWidth());
            System.err.println("prefW=" + ((Pane) pc.getScene().getRoot()).getPrefWidth());
            System.err.println("winW=" + pc.getScene().getWindow().getWidth());
            Platform.runLater(() -> {
                //((Pane)pc.getScene().getRoot()).setMaxSize(600, 600);
                //((Pane)pc.getScene().getRoot()).setMinSize(600, 600);
                ((Pane) pc.getScene().getRoot()).setPrefSize(600, 600);
                //pc.setWidth(500);
            });

        });
        formNode.setContent(content);
        mainBorderPane.setCenter(formNode);
        //System.err.println("1 FORM NODE TARGET CONTROLLER " + formNode.getContext().getTargetContext());
        DockNode sceneGraph = new DockNode("Node hierarchy");
        Button sgBtn1 = new Button("Scene Grah Btn");
        DockSideBar mainLeftSideBar = new DockSideBar();
        sgBtn1.setOnAction(a -> {
            //Stage w = (Stage) sgBtn1.getScene().getWindow();   
            //w.setTitle("Valery+");
            //com.sun.javafx.stage.EmbeddedWindow ew = ((com.sun.javafx.stage.EmbeddedWindow) w.getOwner());
            //System.err.println("OWNER = " + ew.isFocused());
            System.err.println("   -- mainLeftSideBar = " + mainLeftSideBar.isVisible());
            for (Stage s : StageHelper.getStages()) {
                System.err.println("stage = " + s);
            }
        });
        sceneGraph.setContent(sgBtn1);

        mainLeftSideBar.getItems().add(Dockable.of(sceneGraph));
        mainBorderPane.setRight(mainLeftSideBar);
        mainLeftSideBar.setOrientation(Orientation.VERTICAL);
        mainLeftSideBar.setRotation(DockSideBar.Rotation.DOWN_UP);
        mainLeftSideBar.setSide(Side.RIGHT);
        //mainLeftSideBar.setHideOnExit(true);
        return scene;

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //initAndShowGUI();
                FXFrame fxframe = new FXFrame();
                fxframe.frame = new JFrame("Swing and JavaFX");
                fxframe.jbutton = new JButton("Again");

                fxframe.frame.addWindowStateListener(new WindowStateListener() {
                    @Override
                    public void windowStateChanged(WindowEvent e) {
                        System.err.println("OLD STATE = " + e.getOldState());
                        System.err.println("NEW STATE = " + e.getNewState());
                        fxframe.jwindowState.set(e.getNewState());
                    }
                });
                fxframe.frame.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                    }

                    @Override
                    public void windowIconified(WindowEvent e) {
                        System.err.println("PARAM_STR = " + e.paramString());
                        fxframe.jwindowActivities.set(1);
                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {
                        System.err.println("PARAM_STR = " + e.paramString());
                        fxframe.jwindowActivities.set(0);

                    }

                    @Override
                    public void windowActivated(WindowEvent e) {
                        System.err.println("PARAM_STR = " + e.paramString());
                        fxframe.jwindowActivities.set(2);
                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {
                        System.err.println("PARAM_STR = " + e.paramString());
                        fxframe.jwindowActivities.set(3);
                    }
                });

                fxframe.jbutton.addActionListener(l -> {
                    Platform.runLater(() -> {
                        System.err.println("fxframe.scene.getWindow() = " + fxframe.scene.getWindow());
                        fxframe.frame.remove(fxframe.jPanel);
                        fxframe.initAndShowGUI();

                    });

                });
                fxframe.initAndShowGUI();
            }
        }
        );
    }

    /*    public static class FXPanel extends JFXPanel {

        public FXPanel() {
            super();

      }

        @Override
        protected void processMouseMotionEvent(MouseEvent e) {
            //customSendMouseEventToFX(e);
            //System.err.println("processMouseMotionEvent");
            super.processMouseMotionEvent(e);
        }
        private boolean isCapturingMouse = false;
        private void customSendMouseEventToFX(MouseEvent e) {
            // FX only supports 3 buttons so don't send the event for other buttons
            switch (e.getID()) {
                case MouseEvent.MOUSE_DRAGGED:
                case MouseEvent.MOUSE_PRESSED:
                case MouseEvent.MOUSE_RELEASED:
                    if (e.getButton() > 3) {
                        return;
                    }
                    break;
            }

            int extModifiers = e.getModifiersEx();
            // Fix for RT-15457: we should report no mouse button upon mouse release, so
            // *BtnDown values are calculated based on extMofifiers, not e.getButton()
            boolean primaryBtnDown = (extModifiers & MouseEvent.BUTTON1_DOWN_MASK) != 0;
            boolean middleBtnDown = (extModifiers & MouseEvent.BUTTON2_DOWN_MASK) != 0;
            boolean secondaryBtnDown = (extModifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0;
            // Fix for RT-16558: if a PRESSED event is consumed, e.g. by a Swing Popup,
            // subsequent DRAGGED and RELEASED events should not be sent to FX as well
            System.err.println("isCapturingMouse = " + isCapturingMouse);
            if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
                if (!isCapturingMouse) {
                    return;
                }

            } else if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                isCapturingMouse = true;
            } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                if (!isCapturingMouse) {
                    return;
                }
                isCapturingMouse = primaryBtnDown || middleBtnDown || secondaryBtnDown;
            } else if (e.getID() == MouseEvent.MOUSE_CLICKED) {
                // Don't send click events to FX, as they are generated in Scene
                return;
            }
            // A workaround until JDK-8065131 is fixed.
            boolean popupTrigger = false;
            if (e.getID() == MouseEvent.MOUSE_PRESSED || e.getID() == MouseEvent.MOUSE_RELEASED) {
                popupTrigger = e.isPopupTrigger();
            }
            if (e.isPopupTrigger()) {
            }
        }
    }
     */
}
