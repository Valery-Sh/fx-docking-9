/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import static org.vns.javafx.dock.api.demo.TestTreeView.MousePosInfo.ANCHOR_OFFSET;
import static sun.management.snmp.util.JvmContextFactory.getUserData;

/**
 *
 * @author Valery
 */
public class TestTreeView extends Application {

    private Line vertLine = new Line();

    public TreeItem createItem(Node value, String id) {
        HBox hb = new HBox(value);
        AnchorPane ap = new AnchorPane(hb);
        ap.setStyle("-fx-backGround-color: aqua");
        AnchorPane.setBottomAnchor(hb, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(hb, ANCHOR_OFFSET);
        ap.setId(id);
        TreeItem<AnchorPane> ti = new TreeItem<>(ap);
        return ti;
    }

    public void drawLines(MousePosInfo info) {
        //if ( true ) return;
        if (info.getUpperTreeItem() == null) {
            return;
        }
        TreeView tv = info.getTreeView();
        tv.setPadding(Insets.EMPTY);
        Insets ins = tv.getInsets();
        Insets pins = tv.getPadding();
        
        AnchorPane ap = info.getUpperTreeItem().getValue();

        pins = ((TreeCell)ap.getUserData()).getInsets();

        Pane p = (Pane) info.getTreeView().getParent();

        Bounds bnd = MousePosInfo.screenNonValueLevelBounds(info.getTreeView(), info.getUpperTreeItem());
        
        int level = info.getTreeView().getTreeItemLevel(info.getUpperTreeItem());
        double gap = MousePosInfo.getRootStartGap(tv);
        Bounds arrowBnd = MousePosInfo.screenArrowBounds(info.getUpperTreeItem());
        
        double startY = bnd.getMinY() + bnd.getHeight() + pins.getBottom();
        if ( arrowBnd.getHeight() != 0  ) {
            startY = arrowBnd.getMinY() + arrowBnd.getHeight();
        }
        
        Bounds rootBounds = MousePosInfo.screenNonValueLevelBounds(info.getTreeView(), info.getTreeView().getRoot());                
        
        double startX = rootBounds.getMinX() +  rootBounds.getWidth() + gap * level;
        if ( arrowBnd.getWidth()!= 0  ) {
            startX = arrowBnd.getMinX() + arrowBnd.getWidth() / 2;
        }
        
        vertLine.setStartX(p.screenToLocal(startX, startY).getX());
        vertLine.setStartY(p.screenToLocal(startX, startY).getY());
        vertLine.setEndX(vertLine.getStartX());
        vertLine.setEndY(vertLine.getStartY() + 20);
        MousePosInfo.getRootStartGap(tv);
        vertLine.toFront();
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox rootPane = new VBox();
        Pane rootTreeViewPane = new Pane();
        rootPane.getChildren().add(rootTreeViewPane);
        Button drawButton = new Button("tttt");
        rootPane.getChildren().add(drawButton);
        rootTreeViewPane.getChildren().add(vertLine);
        vertLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");

        rootTreeViewPane.setId("DOCK PANE");

        TreeView<AnchorPane> tv = new TreeView<>();
        MousePosInfo.customize(tv);
        
        //tv.getSelectionModel().setSelectionMode(null);
        TreeItem<AnchorPane> tib1 = createItem(new Button("ROOT"), "id0");
        tv.setRoot(tib1);
        tv.setStyle("-fx-background-color: yellow");
        tv.getRoot().setExpanded(true);
        Button tvb2 = new Button("Button tvb2");
        TreeItem<AnchorPane> tib2 = createItem(tvb2, "id2");
        Button tvb3 = new Button("Button tvb3");
        
        TreeItem<AnchorPane> tib3 = createItem(tvb3, "id3");
        Button tvb3_1 = new Button("Button tvb3_1");
        TreeItem<AnchorPane> tib3_1 = createItem(tvb3_1, "id3_1");
        tib3.getChildren().add(tib3_1);
        
        Button tvb3_2 = new Button("Button tvb3_2");
        TreeItem<AnchorPane> tib3_2 = createItem(tvb3_2, "id3_2");
        tib3_1.getChildren().add(tib3_2);

        
        tv.getRoot().getChildren().add(tib2);
        tv.getRoot().getChildren().add(tib3);
        MousePosInfo mpi = new MousePosInfo(tv);
        
        tv.setOnMouseDragged(ev -> {
            if ( ev.getButton() != MouseButton.PRIMARY ) {
                return;
            }
            MousePosInfo m = mpi.getInstance(ev.getScreenX(), ev.getScreenY());
            drawLines(m);
        });
        
        rootTreeViewPane.getChildren().add(tv);
        tv.setOnMouseClicked(ev -> {
            //tv.getSelectionModel().clearSelection();
            mpi.getInstance(ev.getScreenX(), ev.getScreenY());
            drawLines(mpi);
        });
        tvb2.setOnAction(a -> {
            System.err.println("tvb2 CLICKED");

/*            System.err.println("tv.getTreeItem(0).getParent()=" + tv.getTreeItem(0).getParent());
            System.err.println("tv.getTreeItem(1).getParent()=" + tv.getTreeItem(1).getParent());
            System.err.println("tv.getTreeItem(2).getValue()=" + tv.getTreeItem(2).getParent());
            System.err.println("tv.getTreeItem(3).getValue()=" + tv.getTreeItem(3).getParent());

            System.err.println("tv.getTreeItem(0).getValue.getParent=" + ((Node) tv.getTreeItem(0).getValue()).getParent());
            System.err.println("tv.getTreeItem(1).getValue.getParent=" + ((Node) tv.getTreeItem(1).getValue()).getParent());
            System.err.println("tv.getTreeItem(2).getValue.getParent=" + ((Node) tv.getTreeItem(2).getValue()).getParent());
            System.err.println("tv.getTreeItem(3).getValue.getParent=" + ((Node) tv.getTreeItem(3).getValue()).getParent());

            System.err.println("tv.getTreeItem(0).getValue()=" + tv.getTreeItem(0).getValue());
            System.err.println("tv.getTreeItem(1).getValue()=" + tv.getTreeItem(1).getValue());
            System.err.println("tv.getTreeItem(2).getValue()=" + tv.getTreeItem(2).getValue());
            System.err.println("tv.getTreeItem(3).getValue()=" + tv.getTreeItem(3).getValue());

            System.err.println("tv.bounds=" + tv.getBoundsInParent());
            System.err.println("tv.getTreeItem(0).bounds=" + ((Node) tv.getTreeItem(0).getValue()).getBoundsInParent());
            System.err.println("tv.getTreeItem(1).bounds=" + ((Node) tv.getTreeItem(1).getValue()).getBoundsInParent());
            System.err.println("tv.getTreeItem(2).bounds=" + ((Node) tv.getTreeItem(2).getValue()).getBoundsInParent());
            System.err.println("tv.getTreeItem(3).bounds=" + ((Node) tv.getTreeItem(3).getValue()).getBoundsInParent());
            AnchorPane ap0 = (AnchorPane) tv.getTreeItem(0).getValue();

            AnchorPane ap1 = (AnchorPane) tv.getTreeItem(1).getValue();
            AnchorPane ap2 = (AnchorPane) tv.getTreeItem(2).getValue();
            AnchorPane ap3 = (AnchorPane) tv.getTreeItem(3).getValue();
            System.err.println("tv.ap0.id=" + ap0.getId());
            System.err.println("tv.ap1.id=" + ap1.getId());
            System.err.println("tv.ap2.id=" + ap2.getId());
            System.err.println("tv.ap3.id=" + ap3.getId());

            System.err.println("tv.ap0.bounds=" + ap0.localToScreen(ap0.getBoundsInLocal()));
            System.err.println("tv.ap1.bounds=" + ap1.localToScreen(ap1.getBoundsInLocal()));
            System.err.println("tv.ap2.bounds=" + ap2.localToScreen(ap2.getBoundsInLocal()));
            System.err.println("tv.ap3.bounds=" + ap3.localToScreen(ap3.getBoundsInLocal()));
*/
        });

        tvb3.setOnAction(a -> {
            System.err.println("getChildern().size()=" + tib3.getChildren().size());
            System.err.println("itemCount=" + tv.getExpandedItemCount());  
            
            System.err.println("treeItem=" + tv.getTreeItem(4));  
            
            //MousePosInfo.getCell(tib3).setTranslateY(20);
        });
        Scene scene = new Scene(rootPane);

        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        drawButton.setOnAction(a -> {
            Bounds bnd = MousePosInfo.screenValueBounds(tib3);
            mpi.getInstance(bnd.getMinX(),bnd.getMinY());
            drawLines(mpi);
            
        });
        stage.setOnShown(s -> {
            DockUtil.print(rootTreeViewPane);
        });
        stage.show();
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }

    public static class MousePosInfo {

        public static double ANCHOR_OFFSET = 4;
        private TreeItem<AnchorPane> treeItem;
        private boolean insideTreeViewBounds;
        private boolean insideTreeItemBounds;
        private boolean insideBottomOffset;
        private TreeItem<AnchorPane> upperTreeItem;
        private TreeView<AnchorPane> treeView;

        public MousePosInfo(TreeView treeView) {
            this.treeView = treeView;
        }

        public MousePosInfo getInstance(double x, double y) {
            clear();

            Bounds tvBnd = screenTreeViewBounds(treeView);

            if (tvBnd.contains(x, y)) {
                this.insideTreeViewBounds = true;
            }

            int i = 0;
            TreeItem<AnchorPane> lastUpper = null;

            while (treeView.getTreeItem(i) != null) {
                TreeItem<AnchorPane> item = treeView.getTreeItem(i);
                AnchorPane ap = item.getValue();
                Bounds apBnd = ap.localToScreen(ap.getBoundsInLocal());
                Bounds bnd = new BoundingBox(tvBnd.getMinX(), apBnd.getMinY(), tvBnd.getWidth(), apBnd.getHeight());
                if (bnd.contains(x, y)) {
                    treeItem = item;
                    System.err.println(i + ". " + " contains x=" + x + "; y = " + y + " contains: " + bnd.contains(x, y));
                    if (bnd.getMinY() + bnd.getHeight() - ANCHOR_OFFSET < y) {
                        //                System.err.println(i + ". " + " outside = true");
                        this.insideBottomOffset = true;
                    } else {
//                        System.err.println(i + ". " + " outside = false");
                    }
                    break;
                } else if (tvBnd.contains(x, y) && bnd.getMinY() + bnd.getHeight() < y) {
                    lastUpper = item;
                }
//                System.err.println("   --- contains x=" + x + "; y = " + y + " contains: " + bnd.contains(x, y));
                i++;
            }// while
            if (treeItem == null && lastUpper != null) {
                //
                // The point is between tow items or after the lastItem
                // and is inside treeView and ounside any item
                //
                treeItem = lastUpper;
                this.upperTreeItem = treeView.getRoot();
            } else if (treeItem != null) {
                int level = treeView.getTreeItemLevel(treeItem);
                int n = 0;
                if (level == 0) {
                    this.upperTreeItem = treeView.getRoot();
                } else {
                    AnchorPane ap = treeItem.getValue();
                    Bounds apBnd = ap.localToScreen(ap.getBoundsInLocal());
                    Bounds bnd = new BoundingBox(tvBnd.getMinX(), apBnd.getMinY(), tvBnd.getWidth() - apBnd.getWidth(), apBnd.getHeight());
                    double w = (tvBnd.getWidth() - apBnd.getWidth()) / (level + 1);
                    Point2D p = treeView.screenToLocal(x, y);

                    for (i = 0; i <= level; i++) {
                        if (i == level && p.getX() >= i * w) {
                            n = i;
                            break;
                        }
                        if (p.getX() >= i * w && p.getX() < (i + 1) * w) {
                            n = i;
                            break;
                        }
                    }
                    TreeItem parent = treeItem;
                    for (i = 0; i < (level - n); i++) {
                        parent = parent.getParent();
                    }
                    upperTreeItem = parent;
                }
                //System.err.println("UPPER PARENT idx = " + n);
                //System.err.println("UPPER PARENT ITEM = " + upperTreeItem);

            }

            return this;
        }

        private void clear() {

            treeItem = null;
            insideTreeViewBounds = false;
            insideBottomOffset = false;
            upperTreeItem = null;
        }

        private static void customize(TreeView<AnchorPane> treeView) {
            treeView.setCellFactory((TreeView<AnchorPane> tv) -> {
                TreeCell<AnchorPane> cell = new TreeCell<AnchorPane>() {
                    @Override
                    public void updateItem(AnchorPane item, boolean empty) {
                        super.updateItem(item, empty);
                        if ( empty || item == null ) {
                            this.getStyleClass().forEach(s->{
                                System.err.println("STYLE = " + s);
                            });
                            setText(null);
                            setGraphic(null);
                            //this.getTreeItem().setValue(item);
                        } else {
                            System.err.println("1 anchor.Type==" + item.getClass().getGenericSuperclass());                        
                            System.err.println("2 anchor.Type==" + item.getClass().getGenericSuperclass().getTypeName());                                                    
                            item.setUserData(this);
                            this.setGraphic(item);
                            //setText("My Text");
                            //setValue(new Double(20));
                            //this.getTreeItem().setValue(item);
                            this.getStyleClass().forEach(s->{
                                System.err.println("STYLE = " + s);
                            });
                            Pane p = ((Pane)getDisclosureNode());
                            
                            if ( ! p.getChildren().isEmpty()) {
                                System.err.println("DISCL = " + p.getChildren().get(0).getBoundsInParent());
                                System.err.println("P DISCL = " + p.getBoundsInParent());
                                Pane p1 = (Pane) p.getChildren().get(0);
                                System.err.println("DISCL P1 = " + p1.getChildren().size());
                            }
                            
                            
                        }
                    }
                };
                return cell;
            });
            
        }

        public static Bounds screenArrowBounds(TreeItem<AnchorPane> item) {
            TreeCell cell = getCell(item); 
            Bounds retval = new BoundingBox(0,0,0,0);
            if ( ! (cell.getDisclosureNode() instanceof Pane) ) {
                return retval;
            }
            
            if ( ((Pane)cell.getDisclosureNode()).getChildren().isEmpty()) {
                return retval;
            }
            Node arrow = ((Pane)cell.getDisclosureNode()).getChildren().get(0);
            Bounds b = ((Pane)cell.getDisclosureNode()).getChildren().get(0).getBoundsInLocal();
            if ( arrow.localToScreen(b) == null ) {
                return retval;
            }
            return arrow.localToScreen(b);
        }

        public static TreeCell getCell(TreeItem<AnchorPane> item) {
            AnchorPane ap = item.getValue();
            //return ap.localToScreen(ap.getBoundsInLocal());
            //item.getD
            return (TreeCell) item.getValue().getUserData();
        }
        
        
        public static Bounds screenValueBounds(TreeItem<AnchorPane> item) {
            AnchorPane ap = item.getValue();
            return ap.localToScreen(ap.getBoundsInLocal());
        }

        public static Bounds screenNonValueBounds(TreeView treeView, TreeItem<AnchorPane> item) {
            Bounds vBnd = screenValueBounds(item);
            Bounds tvBnd = screenTreeViewBounds(treeView);
            return new BoundingBox(tvBnd.getMinX(), vBnd.getMinY(), tvBnd.getWidth() - vBnd.getWidth(), vBnd.getHeight());
        }

        public static Bounds screenNonValueLevelBounds1(TreeView treeView, TreeItem<AnchorPane> item) {
            Bounds itemBnd = screenValueBounds(item);
            Bounds treeBnd = screenTreeViewBounds(treeView);
            
            Insets ins = ((TreeCell)item.getValue().getUserData()).getInsets();
            double wdelta = 0;
            double hdelta = 0;
            
            if (ins != null ) {
                wdelta = ins.getLeft() + ins.getRight();
            }
            int level = treeView.getTreeItemLevel(item);
/*            System.err.println("===============================");
            System.err.println("level = " + level);
            System.err.println("wdelta = " + wdelta);
            System.err.println("treeBnd.getWidth() - itemBnd.getWidth() = " + (treeBnd.getWidth() - itemBnd.getWidth()));
*/            
            double w = ((treeBnd.getWidth() - itemBnd.getWidth() - wdelta) / (level + 1));
            System.err.println("w = " + w);            
            Bounds nvBnd = new BoundingBox(treeBnd.getMinX() + w * level, itemBnd.getMinY(), w, itemBnd.getHeight());
            return nvBnd;
        }
        public static Bounds screenNonValueLevelBounds(TreeView treeView, TreeItem<AnchorPane> item) {
            
            Bounds itemBnd = screenValueBounds(item);
            Bounds treeBnd = screenTreeViewBounds(treeView);
            Bounds rootBnd = null;
            
            int level = treeView.getTreeItemLevel(item);
            
            //Bounds rootBnd = 
            Insets ins = ((TreeCell)item.getValue().getUserData()).getInsets();
            double wdelta = 0;
            double hdelta = 0;
            double gap = MousePosInfo.getRootStartGap(treeView);
            if ( level > 0 ) {
                
            }
            double itemWidth = MousePosInfo.screenNonValueBounds(treeView, treeView.getRoot()).getWidth() - gap;
            double itemWidth1 = MousePosInfo.screenNonValueBounds(treeView,  item).getWidth();
            System.err.println("item=" + item + "; gap=" + gap);
            System.err.println("itemWidth=" + itemWidth);
            System.err.println("itemWidth1=" + itemWidth1);
            //System.err.println("width=" + MousePosInfo.screenNonValueBounds(treeView, treeView.getRoot()).getWidth());
            //System.err.println("width1=" + itemWidth1);            
            if (ins != null ) {
                wdelta = ins.getLeft() + ins.getRight();
            }
/*            System.err.println("===============================");
            System.err.println("level = " + level);
            System.err.println("wdelta = " + wdelta);
            System.err.println("treeBnd.getWidth() - itemBnd.getWidth() = " + (treeBnd.getWidth() - itemBnd.getWidth()));
*/            
            double w = ((treeBnd.getWidth() - itemBnd.getWidth() - wdelta) / (level + 1));
            double xOffset = 0;
            
            rootBnd = screenNonValueBounds(treeView, treeView.getRoot());
            if ( level > 0 ) {
                //double x = rootBnd.getMinX() + rootBnd.getWidth() / 2;
                xOffset = rootBnd.getWidth() / 2 + gap * level;
                w = gap;
                //xOffset = itemWidth * level + gap;
            } else {
                w = rootBnd.getWidth() / 2;
                xOffset = 0;
            }
            System.err.println("level = " + level);
            System.err.println("w = " + w);
            System.err.println("xOffset = " + xOffset);            
            Bounds nvBnd = new BoundingBox(rootBnd.getMinX() + xOffset, itemBnd.getMinY(), w, itemBnd.getHeight());
            return nvBnd;
        }

        public static Bounds screenTreeViewBounds(TreeView treeView) {
            return treeView.localToScreen(treeView.getBoundsInLocal());
        }

        public static Bounds screenTreeItemBounds(TreeView treeView, TreeItem<AnchorPane> item) {
            AnchorPane ap = item.getValue();
            Bounds apBnd = screenValueBounds(item);
            Bounds tvBnd = screenTreeViewBounds(treeView);
            return new BoundingBox(tvBnd.getMinX(), apBnd.getMinY(), tvBnd.getWidth(), apBnd.getHeight());
        }
        public static double getRootStartGap(TreeView<AnchorPane> treeView  ) {
            double gap = 0;
            if ( treeView.getExpandedItemCount() > 1 ) {
                double rootX = screenValueBounds(treeView.getRoot()).getMinX();
                //double rootW = treeView.getRoot().getValue().loc;
                double itemX = screenValueBounds(treeView.getTreeItem(1)).getMinX();
                gap = itemX - rootX;
                System.err.println("**** gap=" + gap);
            }
            return gap;
        }
        
        public static double getRootStartGap1(TreeView<AnchorPane> treeView  ) {
            double gap = 0;
            if ( treeView.getExpandedItemCount() > 1 ) {
                double rootW = screenNonValueBounds(treeView, treeView.getRoot()).getWidth();
                double itemW = screenNonValueBounds(treeView, treeView.getTreeItem(1)).getWidth();
                System.err.println("X=" + (itemW - rootW) );
                gap = itemW - rootW;
            }
            return gap;
        }
        public TreeView<AnchorPane> getTreeView() {
            return treeView;
        }

        public TreeItem<AnchorPane> getTreeItem() {
            return treeItem;
        }

        public boolean isInsideBounds() {
            return insideTreeViewBounds;
        }

        public boolean isInsideBottomOffset() {
            return insideBottomOffset;
        }

        public TreeItem<AnchorPane> getUpperTreeItem() {
            return upperTreeItem;
        }

    }

    public static class DTreeItem<T> extends TreeItem {

        public DTreeItem() {
        }

        public DTreeItem(T value, Node graphic) {
            super(value, graphic);
        }

    }
    public static class DTreeView<T> extends TreeView {

        public DTreeView() {
        }

        public DTreeView(TreeItem root) {
            super(root);
        }

    }
    
}
