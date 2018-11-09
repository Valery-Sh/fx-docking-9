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
package org.vns.javafx.scene.control.paint.binding;

import java.lang.ref.WeakReference;
import java.util.function.DoubleFunction;
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
public class DoubleBinder implements  WeakListener {

    private  final WeakReference<ReadOnlyDoubleProperty> prop1;
    private  final WeakReference<ReadOnlyDoubleProperty> prop2;

    private boolean updating = false;
    private boolean bound = false;
    
    private final ChangeListener<? super Number> prop1Listener = ( (v,ov,nv)-> {
        this.prop1Changed(v, ov, nv);
    });
    
    private final ChangeListener<? super Number> prop2Listener = ( (v,ov,nv)-> {
        this.prop2Changed(v, ov, nv);
    }); 
    
    private DoubleFunction<Double> prop1Function;
    private DoubleFunction<Double> prop2Function;
    
    public DoubleBinder(ReadOnlyDoubleProperty prop1, ReadOnlyDoubleProperty prop2) {
        if ( !(prop1 instanceof Property) && ! ( prop2 instanceof Property)) {
            throw new RuntimeException(" At least one argument must be an instance of Property.");
        }
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

        if (obj instanceof DoubleBinder) {
            final DoubleBinder otherBinding = (DoubleBinder) obj;
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
    public void change(DoubleProperty prop, DoubleFunction byValue) {
        if ( prop == prop1.get()) {
            prop1Function = byValue;
            ((DoubleProperty)prop1.get()).set(prop1Function.apply(prop2.get().get()));
            prop1.get().addListener(prop1Listener);
            
        } else if ( prop == prop2.get()) {
            prop2Function = byValue;
//            ((DoubleProperty)prop2.get()).set(prop2Function.apply(prop1.get().get()));
            prop2.get().addListener(prop2Listener);
            
        } else {
            throw new RuntimeException("The property to change is not defined.");            
        }
        bound = true;
        //return this;
    }
    public void prop1Changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
/*        if ( ! ( prop2.get() instanceof DoubleProperty) ) {
            return;
        }
*/        
        if (!updating) {

            final ReadOnlyDoubleProperty p1 = prop1.get();
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
                p2.setValue(prop2Function.apply((Double)newValue));
            } finally {
                updating = false;
            }

        }
    }
    public void prop2Changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (!updating) {

            final DoubleProperty p1 = (DoubleProperty) prop1.get();
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
                p1.setValue(prop1Function.apply((Double)newValue));
            } finally {
                updating = false;
            }

        }
    }
    
/*    public void bind() {
        BiFunction f = (v1,v2) -> {return v2;};
        this.bind(f);
    }
*/
/*    public void bind() {
        
        Double prop1Value = prop1.get().get();
        Double prop2Value = prop2.get().get();
        
        if ( prop1Function != null ) {
            ((DoubleProperty)prop1.get()).set(prop1Function.apply(prop2Value));
            prop1.get().addListener(prop1Listener);
        }
        if ( prop2Function != null ) {
            ((DoubleProperty)prop2.get()).set(prop2Function.apply(prop1Value));
            prop2.get().addListener(prop1Listener);
        }
        
        bound = true;
    }
*/
    public void unbind() {
        prop1.get().removeListener(prop1Listener);
        prop2.get().removeListener(prop2Listener);
        bound = false;
    }

    public boolean isBound() {
        return bound;
    }

}
