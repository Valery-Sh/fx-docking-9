package org.vns.javafx.dock.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TopNodeHelper;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery
 */
public class ParentChainPopup {

    private final Region topNode;
    private List<Node> chain;
    private double x, y;

    private StackPane root;
    private VBox vb;
    private Popup popup;

    public ParentChainPopup(Region topNode) {
        this.topNode = topNode;
    }

    public void show(double x, double y) {
        this.x = x;
        this.y = y;
        //
        // Get a list of parent nodes
        //
        Predicate<Node> predicate = el -> {
            return (el instanceof Region);
        };
        show(x, y, predicate);
    }

    public void showDockPane(double x, double y) {
        //
        // Get a list of parent nodes
        //
        Predicate<Node> predicate = el -> {
            return DockRegistry.instanceOfDockTarget(el);
        };
        show(x, y, predicate);
    }

    public void show(double x, double y, Predicate<Node> predicate) {
        //
        // Get a list of parent nodes
        //
        chain = TopNodeHelper.getParentChain(topNode, predicate);
        show();
    }

    protected String getButtonStyle(int i) {
        return "parent-chain-button";
    }

    public void show() {
        popup = createPopup();
        createRoot();
        popup.getContent().add(root);
        popup.setOnShown(this::adjustWidths);

        popup.show(topNode, x, y);
        if (!chain.isEmpty()) {
            vb.getChildren().get(chain.size() - 1).requestFocus();
        }

    }

    public void createRoot() {

        vb = new VBox();
        root = new StackPane(vb);
        root.getStyleClass().add("parent-chain-root");
        vb.setAlignment(Pos.CENTER);
        StackPane.setAlignment(vb, Pos.CENTER);
        root.setStyle("-fx-border-width: 1; -fx-border-color: red");
        vb.setStyle("-fx-border-width: 1; -fx-border-color: blue");
        //vb.setPrefWidth(100);
        //vb.setPrefSize(100,100);
        for (int i = chain.size() - 1; i >= 0; i--) {
            String txt = getText(chain.get(i));
            if (txt == null) {
                txt = "Button" + i;
            }
            Button b = new Button(txt);
            vb.getChildren().add(b);

            b.getStyleClass().add(getButtonStyle(i));

            int xOffset = i * 5;
            int yOffset = i * 4;

            if (i != 0) {
                b.setTranslateX(xOffset);
                b.setTranslateY(yOffset);
            }
        }

    }

    public void adjustWidths(WindowEvent ev) {
        double maxWidth = -1;
        double maxHeight = -1;
        int idxW = -1;
        int idxH = -1;

        for (int i = 0; i < vb.getChildren().size(); i++) {
            if (((Region) vb.getChildren().get(i)).getWidth() > maxWidth) {
                maxWidth = ((Region) vb.getChildren().get(i)).getWidth();
                idxW = i;
            }
            if (((Region) vb.getChildren().get(i)).getHeight() > maxHeight) {
                maxHeight = ((Region) vb.getChildren().get(i)).getHeight();
                idxH = i;
            }

        }
        Button b = (Button) vb.getChildren().get(0);
        Insets ins = b.getInsets();
        if (idxW >= 0) {
            for (int i = 0; i < vb.getChildren().size(); i++) {
                if (i != idxW) {
                    ((Region) vb.getChildren().get(i)).minWidthProperty().bind(((Region) vb.getChildren().get(idxW)).widthProperty());
                }
            }
        }
        if (idxW >= 0) {
            vb.setPrefSize(100, 100);
        }

    }

    protected Popup createPopup() {
        Popup p = new Popup();
        return p;
    }

    protected String getText(Node node) {
        String txt;//
        if (DockRegistry.instanceOfDockTarget(node)) {
            txt = DockRegistry.dockTarget(node).getTargetContext().getTitle();
            return txt;

        } else if (DockRegistry.instanceOfDockable(node)) {
            txt = getButtonText(DockRegistry.dockable(node));
        } else {
            txt = node.getId();
        }
        return txt;
    }

    protected String getButtonText(Dockable d) {
        String txt = d.getDockableContext().getTitle();
        if (d.getDockableContext().getProperties().getProperty("user-title") != null) {
            txt = d.getDockableContext().getProperties().getProperty("user-title");
        } else if (d.getDockableContext().getProperties().getProperty("short-title") != null) {
            txt = d.getDockableContext().getProperties().getProperty("short-title");
        }
        if (txt == null || txt.trim().isEmpty()) {
            txt = "Dockable";
        }
        return txt;
    }

    public ContextMenu createContextMenu(double x, double y) {
        Predicate<Node> predicate = el -> {
            return DockRegistry.instanceOfDockTarget(el);
        };

        chain = TopNodeHelper.getParentChain(topNode, predicate);
        
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(getMenuItems(menu));
        return menu;
    }

    public MenuItem[] getMenuItems(ContextMenu menu) {
        List<MenuItem> items = new ArrayList<>();
        for (int i = 0; i < chain.size(); i++) {
            String txt = getText(chain.get(i));
            if (txt == null) {
                txt = "Button" + i;
            }
            MenuItem item = new MenuItem(txt);
            item.setUserData(chain.get(i));
            item.setOnAction(a -> {menu.setUserData(item.getUserData());});
            items.add(item);
        }
        return items.toArray(new MenuItem[0]);
    }
}