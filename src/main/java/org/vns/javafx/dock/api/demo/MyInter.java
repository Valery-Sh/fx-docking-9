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
public interface MyInter {
    default MyInter me() {
        return this;
    }
    void print();
}
