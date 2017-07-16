package org.vns.javafx.dock.api.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Valery
 */
public class XmlDocBuilder {

    private static final Logger LOG = Logger.getLogger(XmlDocBuilder.class.getName());

    public static Document createDocument(String rootName) {
        //  NETBEANS:   document = XMLUtil.createDocument(rootName, null, null, null);
        Document doc = null;
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(false);
            //domFactory.setIgnoringComments(false);
            //domFactory.setIgnoringElementContentWhitespace(true);
            //domFactory.setCoalescing(true);
            //domFactory.setExpandEntityReferences(true);            
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            builder.setEntityResolver(new ParserEntityResolver());
            //doc = builder.parse(rootName);
            doc = builder.newDocument();
            //
            // ????????? Now we must examine NetBeans 
            //
            return doc;

        } catch (DOMException | ParserConfigurationException ex) {
            //out("Utils: getContextPropertiesByBuildDir EXCEPTION " + ex.getMessage());
            Logger.getLogger(XmlDocBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;
    }

    public static Document parse(Path xmlPath) {

        Document doc = null;
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(false);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            builder.setEntityResolver(new ParserEntityResolver());
            doc = builder.parse(Files.newInputStream(xmlPath));
            return doc;

        } catch (DOMException | ParserConfigurationException | IOException | SAXException ex) {
            Logger.getLogger(XmlDocBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;

        // ------ NetBeans
        // try {
        //FileObject pomFo = FileUtil.toFileObject(xmlPath.toFile());
        //Files.newInputStream(xmlPath);
        //InputSource source = new InputSource(Files.newInputStream(xmlPath));
        //doc = XMLUtil.parse(source, false, false, null, new XmlDocBuilder.ParseEntityResolver());
        //} catch (IOException | DOMException | SAXException ex) {
        //LOG.log(Level.INFO, ex.getMessage());
        //}
    }

    public static Document parse(InputStream is) {
        Document doc = null;
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(false);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            builder.setEntityResolver(new ParserEntityResolver());
            doc = builder.parse(is);
            return doc;

        } catch (DOMException | ParserConfigurationException | IOException | SAXException ex) {
            Logger.getLogger(XmlDocBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;

        /*Document d = null;
        try {
            InputSource source = new InputSource(is);
            d = XMLUtil.parse(source, false, false, null, new ParseEntityResolver());
        } catch (IOException | DOMException | SAXException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return d;
         */
    }

    public static synchronized void save(Document document, Path target) {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        try {
            File file = target.toFile();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);

        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XmlDocBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(XmlDocBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static class ParserEntityResolver implements EntityResolver {

        @Override
        public InputSource resolveEntity(String pubid, String sysid)
                throws SAXException, IOException {
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }
    }

}
