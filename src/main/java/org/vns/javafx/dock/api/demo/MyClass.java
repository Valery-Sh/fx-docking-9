/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

/**
 *
 * @author Valery
 */
public class MyClass implements MyInter {

    public int fld = 25;

    @Override
    public void print() {
        System.err.println("fld=" + ((MyClass) me()).fld);
    }

}
