package org.vns.javafx.dock.api.util;

import java.io.ByteArrayInputStream;
import java.util.Properties;
import javafx.scene.control.TreeItem;
import javafx.util.StringConverter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import static org.vns.javafx.dock.api.save.DockTreeItemBuilder.*;
/**
 *
 * @author Valery
 */
public class TreeItemStringConverter extends StringConverter<TreeItem<Properties>> {

    @Override
    public String toString(TreeItem<Properties> treeItem) {
        StringBuilder sb = new StringBuilder();
        append(treeItem, sb);
        sb.append(System.lineSeparator());
        sb.append("</")
                .append(treeItem.getValue().getProperty(TAG_NAME_ATTR))
                .append(">");
        
        return sb.toString();
    }

    public void append(TreeItem<Properties> treeItem, StringBuilder sb) {
        Properties props = treeItem.getValue();
        sb.append(System.lineSeparator());
        sb.append("<");
        sb.append(props.getProperty(TAG_NAME_ATTR))
                .append(" ");
        props.forEach((k, v) -> {
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
                    .append(it.getValue().getProperty(TAG_NAME_ATTR))
                    .append(">");

        });
    }


    @Override
    public TreeItem<Properties> fromString(String strValue) {
        String str = "<root>" + strValue + "</root>";
        TreeItem<Properties> item = new TreeItem<>();
        Properties props = new Properties();
        item.setValue(props);
        item.setExpanded(true);
        props.put(TREEITEM_ATTR, item);

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
//                System.err.println(i + ") " + nnm.item(i).getClass().getSimpleName());
                Attr attr = (Attr) nnm.item(i);
                item.getValue().setProperty(attr.getName(), attr.getValue());
            }
        }
        NodeList nodeList = firstEl.getChildNodes();
        if (nodeList.getLength() == 0) {
            return item;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element) {
                Element el = (Element) nodeList.item(i);
                TreeItem<Properties> it = new TreeItem<>();
                Properties p = new Properties();
                it.setValue(p);
                p.put(TREEITEM_ATTR, it);
                build(it, el);
                item.getChildren().add(it);
                it.setExpanded(true);

            }
        }
        return item;
    }

    protected void build(TreeItem<Properties> item, Element element) {

        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            if (attrs.item(i) instanceof Attr) {
//                System.err.println(i + ") " + attrs.item(i).getClass().getSimpleName());
                Attr attr = (Attr) attrs.item(i);
                item.getValue().setProperty(attr.getName(), attr.getValue());
            }
        }
        NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element) {
                Element el = (Element) nodeList.item(i);
                TreeItem<Properties> it = new TreeItem<>();
                Properties p = new Properties();
                it.setValue(p);
                p.put(TREEITEM_ATTR, it);
                build(it, el);
                item.getChildren().add(it);
                it.setExpanded(true);


            }
        }

    }

    protected String buildTag(TreeItem<Properties> item, StringBuilder sb) {
        item.getChildren().forEach(it -> {
            sb.append(buildTag(it, sb));
        });
        return sb.toString();
    }

}
