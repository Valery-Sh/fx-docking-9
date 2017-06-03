package org.vns.javafx.dock.api.editor;

import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.geometry.VerticalDirection;

/**
 *
 * @author Valery
 */
public class ScrollAnimation {
    
    private final TreeViewEx treeView;
    private AnimationTimer timer;
    private VerticalDirection direction;
    private double increment = 0.007;
    private boolean running;
    
    public ScrollAnimation(TreeViewEx treeView) {
        this.treeView = treeView;
        init();
    }

    private void init() {
        timer = new AnimationTimer() {

            private long lastUpdate = 0;

            @Override
            public void handle(long time) {
                VirtualScrollBar scrollBar = treeView.getVScrollBar();
                if (lastUpdate <= 0) {
                    lastUpdate = time;
                }
                if (direction.equals(VerticalDirection.UP)) {
                    scrollBar.setValue(scrollBar.getValue() - increment);
                } else {
                    scrollBar.setValue(scrollBar.getValue() + increment);
                }
                if (scrollBar.getValue() <= 0 && direction.equals(VerticalDirection.UP) ) {
                    stop();
                    running = false;
                }
                if (scrollBar.getValue() > 1 && direction.equals(VerticalDirection.DOWN) ) {
                    stop();
                    running = false;
                }
                lastUpdate = time;
            }
        };
    }

    public VerticalDirection getDirection() {
        return direction;
    }

    public void setDirection(VerticalDirection direction) {
        this.direction = direction;
    }

    public double getIncrement() {
        return increment;
    }

    public void setIncrement(double increment) {
        this.increment = increment;
    }

    public void start(double x, double y) {
        if ( isRunning() ) {
            return;
        }
        VirtualScrollBar sb = treeView.getVScrollBar();
        Bounds sbBounds = sb.localToScreen(sb.getBoundsInLocal());
        if (!sbBounds.contains(x, y)) {
            return;
        }
        if (y <= sbBounds.getMinY() + sbBounds.getHeight() / 3) {
            direction = VerticalDirection.UP;
        } else if ( y > sbBounds.getMinY() + sbBounds.getHeight() - sbBounds.getHeight() / 3) {
            direction = VerticalDirection.DOWN;
        } else {
            timer.stop();
            running = false;
            return;
        }
        timer.start();
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        timer.stop();
        running = false;
    }

}
