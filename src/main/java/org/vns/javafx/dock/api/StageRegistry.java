package org.vns.javafx.dock.api;

import com.sun.javafx.stage.StageHelper;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery Shyshkin
 */
public class StageRegistry {

    private final ObservableList<Stage> stages = FXCollections.observableArrayList();

    private StageRegistry() {
    }

    public static StageRegistry getInstance() {
        return SingletonInstance.instance;
    }

    public static void register(Stage stage) {
        getInstance().doRegister(stage);
    }

    protected ObservableList<Stage> getStages() {
        return stages;
    }

    private void doRegister(Stage stage) {
        if (stage.isFocused()) {
            stages.add(0, stage);
        } else {
            stages.add(stage);
        }

        stage.focusedProperty().addListener((ov, oldValue, newValue) -> {
            //if ( true) return;
            if (newValue) {
                stages.remove(stage);
                stages.add(0, stage);
                stages.forEach(s -> {
                    System.out.println("============================================");
                    System.out.println("STAGE: " + s.getTitle());
                });

            }
        });
    }

    public int zorder(Stage stage) {
        if (!stages.contains(stage)) {
            register(stage);
        }
        return stages.indexOf(stage);
    }

    public Stage getTarget(double x, double y) {
        Stage retval = null;
        List<Stage> allStages = getStages(x, y);
        if (allStages.isEmpty()) {
            return null;
        }
        System.err.println("getTarget(x,y) allStages.sz=" + allStages.size());
        List<Stage> targetStages = new ArrayList<>();
        allStages.forEach(s -> {
            List<Node> ls = DockUtil.findNodes(s.getScene().getRoot(), (node) -> {
                Point2D p = node.screenToLocal(x, y);
                return node.contains(p) && (node instanceof DockPaneTarget)
                        && ((DockPaneTarget) node).getDelegate().zorder() == 0;
            });
            Node node = s.getScene().getRoot();
            Point2D p = node.screenToLocal(x, y);
            if ( node.contains(p) && (node instanceof DockPaneTarget && ((DockPaneTarget) node).getDelegate().zorder() == 0) ) {
                ls.add(0,node);
            }
            if ( ! ls.isEmpty() ) {
                targetStages.add(s);
            }
        });
        System.err.println("getTarget(x,y) targetStages.sz=" + targetStages.size());
        
        for ( Stage s1 : targetStages) {
        System.err.println("0) getTarget(x,y) s1.title=" + s1.getTitle() );
            
            retval = s1;
            for ( Stage s2 : allStages ) {
        System.err.println("1) getTarget(x,y) s2.title=" + s2.getTitle() );
                if ( s1 == s2 ) {
                    continue;
                }
        System.err.println("2) getTarget(x,y) s2.title=" + s2.getTitle() );
                
                if ( s1 != getTarget(s1,s2)) {
                    retval = null;
        System.err.println("11111 getTarget(x,y) s1.title=" + s1.getTitle() + "; s2.title=" + s2.getTitle() );
                    
                    break;
                }
            }
            if ( retval != null ) {
                break;
            }
        }
        if ( retval == null ) {
            System.err.println("END: getTarget(x,y) retval=" + retval);
        } else {
            System.err.println("END: getTarget(x,y) retval.title=" + retval.getTitle());
        }

        return retval;
    }
    public Stage getTarget(Stage s1, Stage s2) {
        Stage retval = null;
        s1.getScene().getRoot().setStyle("-fx-background-color: lightgray");
        s2.getScene().getRoot().setStyle("-fx-background-color: lightgray");

        Stage s = s1;

        boolean b1 = s1.isAlwaysOnTop();
        boolean b2 = s2.isAlwaysOnTop();
        s1.getScene().setFill(null);
        s2.getScene().setFill(null);
        s1.getScene().getRoot().setStyle("-fx-background-color: blue");
        s2.getScene().getRoot().setStyle("-fx-background-color: blue");
        System.err.println("zorder(s1) < zorder(s2) = " + (zorder(s1) < zorder(s2)) + "; stages.sz=" + stages.size());
        if (zorder(s1) < zorder(s2) && !b1 && !b2) {
            System.err.println("A.1");
            retval = s1;
        } else if (b1 && !b2) {
            System.err.println("A.2");
            retval = s1;
        } else if ( !b1 && b2 ) {
            System.err.println("A.3");

        } else if (zorder(s1) < zorder(s2)) {
            System.err.println("A.4");
            retval = s1;
        }

        if (retval != null) {
            System.err.println("COLOR @@@@@@@@@@ RED " + retval);
            retval.getScene().setFill(Color.RED);
            retval.getScene().getRoot().setStyle("-fx-background-color: red");
        }
        
        return retval;
    }
    public static List<Stage> getStages(double x, double y) {
        List<Stage> retlist = new ArrayList<>();
        StageHelper.getStages().forEach(s -> {
            if (!((x < s.getX() || x > s.getX() + s.getWidth()
                    || y < s.getY() || y > s.getY() + s.getHeight()))) {
                retlist.add(s);
            }
            s.getScene().getRoot().setStyle("-fx-background-color: lightgray");
            s.getScene().getRoot().setStyle("-fx-background-color: lightgray");
        });
        return retlist;
    }
    
    
    private static class SingletonInstance {

        private static final StageRegistry instance = new StageRegistry();
    }
}
