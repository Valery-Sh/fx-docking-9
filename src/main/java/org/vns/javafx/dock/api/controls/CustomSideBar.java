/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.controls;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;

/**
 *
 * @author Valery
 */
public class CustomSideBar extends Control {

    private Skin toolBarSkin;
    
    private CustomToolBar delegate = new CustomToolBar();

    public CustomSideBar() {
        //getChildren().add(delegate);
    }
    public CustomSideBar(Node... items) {
        this();
        delegate.getItems().addAll(items);
    }

    protected double computePrefHeight(double h) {
        return delegate.computePrefHeight(h);
    }

    protected double computePrefWidth(double w) {
        return delegate.computePrefWidth(w);
    }

    protected double computeMinHeight(double h) {
        return delegate.computeMinHeight(h);
    }

    protected double computeMinWidth(double w) {
        return delegate.computeMinWidth(w);
    }

    protected double computeMaxHeight(double h) {
        return delegate.computeMaxHeight(h);
    }

    protected double computeMaxWidth(double w) {
        return delegate.computeMaxWidth(w);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomSideBarSkin(this);
    }

    public static class CustomSideBarSkin extends SkinBase<CustomSideBar> {

        public CustomSideBarSkin(CustomSideBar control) {
            super(control);
            getChildren().add(control.delegate);
        }

    }

    public static class CustomToolBar extends ToolBar {

        public CustomToolBar() {

        }

        protected double computePrefHeight(double h) {
            return super.computePrefHeight(h);
        }

        protected double computePrefWidth(double w) {
            return super.computePrefWidth(w);
        }

        protected double computeMinHeight(double h) {
            return super.computeMinHeight(h);
        }

        protected double computeMinWidth(double w) {
            return super.computeMinWidth(w);
        }

        protected double computeMaxHeight(double h) {
            return super.computeMaxHeight(h);
        }

        protected double computeMaxWidth(double w) {
            return super.computeMaxWidth(w);
        }

        /*        @Override
        protected double computePrefHeight(double width, double topInset,
                double rightInset, double bottomInset, double leftInset) {
            return topInset + bottomInset + 200;
        }

        @Override
        protected double computePrefWidth(double height, double topInset,
                double rightInset, double bottomInset, double leftInset) {
            return rightInset + leftInset + 200;
        }

        @Override
        protected double computeMinHeight(double width, double topInset,
                double rightInset, double bottomInset, double leftInset) {
            return 20 + topInset + bottomInset;
        }

        @Override
        protected double computeMinWidth(double height, double topInset,
                double rightInset, double bottomInset, double leftInset) {
            return 20 + rightInset + leftInset;
        }

        @Override
        protected double computeMaxWidth(double height, double topInset,
                double rightInset, double bottomInset, double leftInset) {
            return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }
         */
    }
}
