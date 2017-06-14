package org.vns.javafx.dock.api.editor;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;

/**
 *
 * @author Valery
 */
public class ListViewItemBuilder<T> extends AbstractListBasedTreeItemBuilder {
    
    @Override
    public boolean isAcceptable(Object target,Object accepting) {
        String[] types = getAcceptTypes(target);
        if ( types == null || types.length == 0  ) {
            return false;
        } 
        if ( accepting == null ) {
            return false;
        }
        if ( types[0] != null && ("*".equals(types[0]) || "all".equals(types[0].toLowerCase()) ) )   {
            return true;
        }
        boolean retval = false;
        for ( String clazz : types ) {
            if ( accepting.getClass().getName().equals(clazz) ) {
                retval = true;
                break;
            }
        }
        return retval;
    }
    
    @Override
    public ObservableList<T> getList(Object obj) {
        return ((ListView<T>)obj).getItems();
    }
    protected String[] getAcceptTypes(Object target) {
        if ( target instanceof Node ) {
            String str = (String) ((Node)target).getProperties().get(TreeItemBuilder.ACCEPT_TYPES_KEY);
            if ( str == null || str.trim().isEmpty() ) {
                return null;
            }
            return str.split(",");
            
        }
        return null;
    }

}//TabPaneItemBuilder

