/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.designer.bean.editor.paint;

import java.lang.ref.WeakReference;
import java.util.function.BiFunction;
import javafx.beans.WeakListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Valery
 */
public class NumberBidirectionalBinding implements  WeakListener {

    private final WeakReference<DoubleProperty> prop1;
    private final WeakReference<ReadOnlyDoubleProperty> prop2;

    private boolean updating = false;
    private boolean bound = false;
    private final ChangeListener<? super Number> prop1Listener = ( (v,ov,nv)-> {
        this.prop1Changed(v, ov, nv);
    });
    
    private final ChangeListener<? super Number> prop2Listener = ( (v,ov,nv)-> {
        this.prop2Changed(v, ov, nv);
    }); 
    
    private BiFunction<Integer,Double,Double> bf = (v1,v2) -> {return v2;};    
    
    public NumberBidirectionalBinding(DoubleProperty prop1,ReadOnlyDoubleProperty prop2) {
        this.prop1 = new WeakReference<>(prop1);
        this.prop2 = new WeakReference<>(prop2);
    }


    @Override
    public boolean wasGarbageCollected() {
        return (prop1.get() == null) || (prop2.get() == null);
    }

    @Override
    public int hashCode() {
        final Double ls1 = prop1.get().get();
        final Double ls2 = prop2.get().get();
        final int hc1 = (ls1 == null) ? 0 : ls1.hashCode();
        final int hc2 = (ls2 == null) ? 0 : ls2.hashCode();
        return hc1 * hc2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final Object ls = prop1.get().get();
        final Object sp = prop2.get().get();
        if ((ls == null) || (sp == null)) {
            return false;
        }

        if (obj instanceof NumberBidirectionalBinding) {
            final NumberBidirectionalBinding otherBinding = (NumberBidirectionalBinding) obj;
            final Object o1 = otherBinding.prop1.get();
            final Object o2 = otherBinding.prop2.get();
            if ((o1 == null) || (o2 == null)) {
                return false;
            }

            if ((ls == o1) && (sp == o2)) {
                return true;
            }
            if ((ls == o2) && (sp == o1)) {
                return true;
            }
        }
        return false;
    }

    public void prop1Changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if ( ! ( prop2.get() instanceof DoubleProperty) ) {
            return;
        }
        if (!updating) {

            final DoubleProperty p1 = prop1.get();
            final DoubleProperty p2 = (DoubleProperty) prop2.get();

            if (p1 == null || p2 == null) {
                if (p1 != null) {
                    p1.removeListener(prop1Listener);
                }
                if (p2 != null) {
                    p2.removeListener(prop2Listener);
                }
                return;
            }

            try {
                updating = true;
                p2.setValue(bf.apply(2, (Double)newValue));
            } finally {
                updating = false;
            }

        }
    }
    public void prop2Changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (!updating) {

            final DoubleProperty p1 = prop1.get();
            final ReadOnlyDoubleProperty p2 = prop2.get();

            if (p1 == null || p2 == null) {
                if (p1 != null) {
                    p1.removeListener(prop1Listener);
                }
                if (p2 != null) {
                    p2.removeListener(prop2Listener);
                }
                return;
            }

            try {
                updating = true;
                p1.setValue(bf.apply(1, (Double)newValue));
            } finally {
                updating = false;
            }

        }
    }
    
    public void bind() {
        BiFunction f = (v1,v2) -> {return v2;};
        this.bind(f);
/*        prop1.get().set(prop2.get().get());
        prop2.get().addListener(prop2Listener);
        bound = true;
*/        
    }
    public void bind(BiFunction<Integer,Double,Double> bf) {
        this.bf = bf;
        Double prop1Value = prop1.get().get();
        Double prop2Value = prop2.get().get();
        prop1.get().set(bf.apply(1,prop2Value));
        prop2.get().addListener(prop2Listener);
        bound = true;
    }
    public void bindBidirectional() {
        BiFunction f = (v1,v2) -> {return v2;};
        this.bindBidirectional(f);
    }
    public void bindBidirectional(BiFunction<Integer,Double,Double> bf) {
        this.bf = bf;
        if ( ! (prop2.get() instanceof Property) ) {
            return;
        }
        Double prop1Value = prop1.get().get();
        Double prop2Value = prop2.get().get();
        
        prop1.get().set(bf.apply(1,prop2Value));

        prop1.get().addListener(prop1Listener);
        prop2.get().addListener(prop2Listener);
        bound = true;
    }
    public void unbind() {
        prop1.get().removeListener(prop1Listener);
        prop2.get().removeListener(prop2Listener);
        bound = false;
    }

    public boolean isBound() {
        return bound;
    }

}
