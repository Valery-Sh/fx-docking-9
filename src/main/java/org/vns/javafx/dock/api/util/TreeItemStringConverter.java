package org.vns.javafx.dock.api.util;

import java.io.ByteArrayInputStream;
import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import static org.vns.javafx.dock.api.PreferencesBuilder.*;
/**
 *
 * @author Valery
 */
public class TreeItemStringConverter extends StringConverter<TreeItem<Pair<ObjectProperty, Properties>>> {

    @Override
    public String toString(TreeItem<Pair<ObjectProperty, Properties>> treeItem) {
        StringBuilder sb = new StringBuilder();
        append(treeItem, sb);
        sb.append(System.lineSeparator());
        sb.append("</")
                .append(treeItem.getValue().getValue().getProperty(TAG_NAME_ATTR))
                .append(">");
        
        return sb.toString();
    }

    public void append(TreeItem<Pair<ObjectProperty, Properties>> treeItem, StringBuilder sb) {
        Pair<ObjectProperty, Properties> pair = treeItem.getValue();
        sb.append(System.lineSeparator());
        sb.append("<");
        sb.append(pair.getValue().getProperty(TAG_NAME_ATTR))
                .append(" ");
        pair.getValue().forEach((k, v) -> {
            if (!((String) k).startsWith("ignore:")) {
                sb.append((String) k)
                        .append("='")
                        .append((String) v)
                        .append("' ");

            }
        });
        sb.append("> ");
        treeItem.getChildren().forEach(it -> {
            append(it, sb);
            sb.append(System.lineSeparator());
            sb.append("</")
                    .append(it.getValue().getValue().getProperty(TAG_NAME_ATTR))
                    .append(">");

        });
    }


    @Override
    public TreeItem<Pair<ObjectProperty, Properties>> fromString(String strValue) {
        String str = "<root>" + strValue + "</root>";
        TreeItem<Pair<ObjectProperty, Properties>> item = new TreeItem<>();
        Pair<ObjectProperty, Properties> pair = new Pair<>(new SimpleObjectProperty(), new Properties());
        item.setValue(pair);
        item.setExpanded(true);
        pair.getValue().put(TREEITEM_ATTR, item);

        Document document = XmlDocBuilder.parse(new ByteArrayInputStream(str.getBytes()));
        Element rootEl = document.getDocumentElement();
        NodeList childs = rootEl.getChildNodes();
        Element firstEl = null;
        for ( int i=0; i < childs.getLength(); i++) {
            if (childs.item(i) instanceof Element) {
                firstEl = (Element) childs.item(i);
                break;
            }
        }
        if (firstEl == null ) {
            return item;
        }
        
        NamedNodeMap nnm = firstEl.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            if (nnm.item(i) instanceof Attr) {
                System.err.println(i + ") " + nnm.item(i).getClass().getSimpleName());
                Attr attr = (Attr) nnm.item(i);
                item.getValue().getValue().setProperty(attr.getName(), attr.getValue());
            }
        }
        NodeList nodeList = firstEl.getChildNodes();
        if (nodeList.getLength() == 0) {
            return item;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element) {
                Element el = (Element) nodeList.item(i);
                TreeItem<Pair<ObjectProperty, Properties>> it = new TreeItem<>();
                Pair<ObjectProperty, Properties> p = new Pair<>(new SimpleObjectProperty(), new Properties());
                it.setValue(p);
                p.getValue().put(TREEITEM_ATTR, it);
                build(it, el);
                item.getChildren().add(it);
                it.setExpanded(true);

            }
        }
        return item;
    }

    protected void build(TreeItem<Pair<ObjectProperty, Properties>> item, Element element) {

        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            if (attrs.item(i) instanceof Attr) {
                System.err.println(i + ") " + attrs.item(i).getClass().getSimpleName());
                Attr attr = (Attr) attrs.item(i);
                item.getValue().getValue().setProperty(attr.getName(), attr.getValue());
            }
        }
        NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element) {
                Element el = (Element) nodeList.item(i);
                TreeItem<Pair<ObjectProperty, Properties>> it = new TreeItem<>();
                Pair<ObjectProperty, Properties> p = new Pair<>(new SimpleObjectProperty(), new Properties());
                it.setValue(p);
                p.getValue().put(TREEITEM_ATTR, it);
                build(it, el);
                item.getChildren().add(it);
                it.setExpanded(true);


            }
        }

    }

    protected String buildTag(TreeItem<Pair<ObjectProperty, Properties>> item, StringBuilder sb) {
        item.getChildren().forEach(it -> {
            sb.append(buildTag(it, sb));
        });
        return sb.toString();
    }

}
