package org.vns.javafx.dock.api;

import com.sun.glass.ui.Application;
import com.sun.javafx.stage.StageHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery Shyshkin
 */
public class StageRegistry {

    private final ObservableList<Stage> stages = FXCollections.observableArrayList();
    private final Map<Stage, Window> owners = new HashMap<>();

    private StageRegistry() {
    }

    public static StageRegistry getInstance() {
        return SingletonInstance.instance;
    }

    public static void register(Stage stage) {
        getInstance().doRegister(stage);
    }

    private boolean registerDone;

    public static void start() {
        if (!getInstance().registerDone) {
            getInstance().registerDone = true;
            javafx.application.Platform.runLater(() -> {
                getInstance().updateRegistry();
            });
            StageHelper.getStages().forEach(s -> {
                //
                // Add in reverse order
                //
                getInstance().stages.add(0, s);

            });
            StageHelper.getStages().addListener(getInstance()::onChangeStages);

        }
    }

    protected void onChangeStages(ListChangeListener.Change<? extends Stage> change) {
        while (change.next()) {
            if (change.wasPermutated()) {

            } else if (change.wasUpdated()) {

            } else if (change.wasReplaced()) {
            } else {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(s -> {
                        getInstance().stages.remove(s);
                        getInstance().owners.remove(s);
                    });
                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(s -> {
                        register(s);
                        if (s.getOwner() != null) {
                            getInstance().owners.put(s, s.getOwner());
                        }
                    });

                    Platform.runLater(() -> {
                        updateRegistry();
                    });

                }
            }
        }

    }

    protected void updateRegistry() {

        StageHelper.getStages().forEach(s -> {
            if (s.getOwner() != null) {
                int idx1 = StageHelper.getStages().indexOf(s);
                int idx2 = StageHelper.getStages().indexOf(s.getOwner());
                if (idx1 < idx2) {
                    owners.remove(s);
                }
            }
        });
    }

    public static ObservableList<Stage> getStages() {
        return getInstance().stages;
    }

    private void doRegister(Stage stage) {
        stage.focusedProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue) {
                stages.remove(stage);
                stages.add(0, stage);
                System.out.println("============================================");
                stages.forEach(s -> {
                    System.out.println("STAGE: " + s.getTitle());
                });
            }
        });
    }

    public static Window getOwner(Stage stage) {
        return getInstance().owners.get(stage);
    }

    private boolean isChild(Stage parent, Stage child) {
        boolean retval = false;
        Stage win = child;
        while (true) {
            Window w = owners.get(win);
            if (w == null) {
                break;
            }
            if (w == parent) {
                retval = true;
                break;
            }
            if (w instanceof Stage) {
                win = (Stage) w;
            }

        }
        return retval;
    }

    public int zorder(Stage stage) {
        if (!stages.contains(stage)) {
            register(stage);
        }
        return stages.indexOf(stage);
    }

    public Stage getTarget(double x, double y, Stage excl) {
        Stage retval = null;
        List<Stage> allStages = getStages(x, y, excl);
        if (allStages.isEmpty()) {
            return null;
        }
        //System.err.println("getTarget(x,y) allStages.sz=" + allStages.size());
        List<Stage> targetStages = new ArrayList<>();
        allStages.forEach(s -> {
            List<Node> ls = DockUtil.findNodes(s.getScene().getRoot(), (node) -> {
                Point2D p = node.screenToLocal(x, y);
                return node.contains(p) && (node instanceof DockPaneTarget)
                        && ((DockPaneTarget) node).getDelegate().zorder() == 0;
            });
            Node node = s.getScene().getRoot();
            Point2D p = node.screenToLocal(x, y);
            if (node.contains(p) && (node instanceof DockPaneTarget && ((DockPaneTarget) node).getDelegate().zorder() == 0)) {
                ls.add(0, node);
            }
            if (!ls.isEmpty()) {
                targetStages.add(s);
            }
        });
        System.err.println("getTarget(x,y) targetStages.sz=" + targetStages.size());

        for (Stage s1 : targetStages) {
            System.err.println("00) getTarget(x,y) s1.title=" + s1.getTitle() + "; allStages.sz=" + allStages.size());
            System.err.println("0) getTarget(x,y) s1.title=" + s1.getTitle());

            retval = s1;
            for (Stage s2 : allStages) {
                System.err.println("1) getTarget(x,y) s2.title=" + s2.getTitle());
                if (s1 == s2) {
                    continue;
                }
                System.err.println("2) getTarget(x,y) s2.title=" + s2.getTitle());

                if (s1 != getTarget(s1, s2)) {
                    retval = null;
                    System.err.println("11111 getTarget(x,y) s1.title=" + s1.getTitle() + "; s2.title=" + s2.getTitle());

                    break;
                }
            }
            if (retval != null) {
                break;
            }
        }
        System.err.println("3) retval=" + retval + "; title=" + (retval==null ? null: retval.getTitle()) );
        
/*        if (retval == null) {
            System.err.println("END: getTarget(x,y) retval=" + retval);
        } else {
            System.err.println("END: getTarget(x,y) retval.title=" + retval.getTitle());
        }
*/
        return retval;
    }

    public Stage getTarget(Stage s1, Stage s2) {
        Stage retval = null;
        //Window own1 = s1.getOwner();
        //Window own2 = s2.getOwner();

        //s1.getScene().getRoot().setStyle("-fx-background-color: lightgray");
        //s2.getScene().getRoot().setStyle("-fx-background-color: lightgray");

        Stage s = s1;

        boolean b1 = s1.isAlwaysOnTop();
        boolean b2 = s2.isAlwaysOnTop();
        //s1.getScene().setFill(null);
        //s2.getScene().setFill(null);
        //s1.getScene().getRoot().setStyle("-fx-background-color: blue");
        //s2.getScene().getRoot().setStyle("-fx-background-color: blue");
        //System.err.println("zorder(s1) < zorder(s2) = " + (zorder(s1) < zorder(s2)) + "; stages.sz=" + stages.size());
        if (isChild(s1, s2)) {
            //retval must be null s2 is a child window of s1
            System.err.println("!!!!!!!!!! isChild(s1, s2) s1.title=" + s1.getTitle() + "; s2.title=" + s2.getTitle());

        } else if (isChild(s2, s1)) {
            System.err.println("!!!!!!!!!! isChild(s1, s2) s1.title=" + s1.getTitle() + "; s2.title=" + s2.getTitle());
            retval = s1;
        } else if (zorder(s1) < zorder(s2) && !b1 && !b2) {
            System.err.println("A.1");
            retval = s1;
        } else if (zorder(s1) < zorder(s2) && b1 && b2) {
            retval = s1;
        } else if (b1 && !b2) {
            System.err.println("A.2");
            retval = s1;
        } else if (!b1 && b2) {
            System.err.println("A.3");
        }
        String t = null;
        if ( retval != null ) {
            t = retval.getTitle();
        }
        System.err.println("!!!!!!!!!! retval = " + retval + "; title=" + t);
        if (retval != null) {
            //System.err.println("COLOR @@@@@@@@@@ RED " + retval);
            //retval.getScene().setFill(Color.RED);
            //retval.getScene().getRoot().setStyle("-fx-background-color: red");
        }

        return retval;
    }

    public static List<Stage> getStages(double x, double y, Stage excl) {
        List<Stage> retlist = new ArrayList<>();
        StageHelper.getStages().forEach(s -> {
            if (!((x < s.getX() || x > s.getX() + s.getWidth()
                    || y < s.getY() || y > s.getY() + s.getHeight()))) {
                if ( s != excl )   { 
                    retlist.add(s);
                }
            }
            //s.getScene().getRoot().setStyle("-fx-background-color: lightgray");
            //s.getScene().getRoot().setStyle("-fx-background-color: lightgray");
        });
        return retlist;
    }

    private static class SingletonInstance {

        private static final StageRegistry instance = new StageRegistry();
    }
}
