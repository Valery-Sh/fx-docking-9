/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.controls;

import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "items")
public class HPane___ extends Control {

    private DockSplitPane delegate;// = new DockPane();

    public HPane___() {
        delegate = new DockSplitPane();
        delegate.setOrientation(Orientation.HORIZONTAL);
    }

    protected DockSplitPane getDelegate() {
        if (delegate == null) {
            delegate = new DockSplitPane();
            delegate.setOrientation(Orientation.HORIZONTAL);
        }
        return delegate;
    }

    public ObservableList<Node> getItems() {
        return delegate.getItems();
    }

    protected void itemsChanged(ListChangeListener.Change<? extends Node> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List<? extends Node> list = change.getRemoved();
                for (Node t : list) {
                    //titleBarPane.getChildren().remove(t.getTitleBar());
                    //titleBarPane.getChildren().remove(t.getTitleBar().getSeparator());

                    int idx = change.getFrom();
                    if (!change.getList().isEmpty()) {
                        if (idx - 1 >= 0) {
                            idx--;
                        }
                        //change.getList().get(idx).setSelected(true);
                    }
                }

            } else if (change.wasAdded()) {
                //
                // For the current implementation only one element may be aded
                //
                List<? extends Node> list = change.getAddedSubList();
                for (Node t : list) {
                    int idx = change.getList().indexOf(t);
                    //titleBarPane.getChildren().add(idx, t.getTitleBar());
                    //titleBarPane.getChildren().add(idx + 1, t.getTitleBar().getSeparator());
                    //t.setSelected(true);
                }
            }
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new HPaneControlSkin(this);
    }

    public static class HPaneControlSkin extends SkinBase<HPane___> {

        public HPaneControlSkin(HPane___ control) {
            super(control);
            getChildren().add(control.getDelegate());
        }
    }

}
