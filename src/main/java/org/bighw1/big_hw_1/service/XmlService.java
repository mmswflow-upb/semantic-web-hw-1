package org.bighw1.big_hw_1.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.file.Path;

// Handles all raw XML work: loading, saving, XPath queries, and XSLT transforms.
// Everything else goes through this class, not through the DOM directly.
@Service
public class XmlService {

    // Load an XML file from disk into an in-memory DOM Document.
    // The DocumentBuilder is set up to validate against the DTD referenced in the file.
    public Document load(Path filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        // Suppress "DTD not found" console noise when the DTD is in the same folder.
        builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler() {
            @Override
            public void warning(org.xml.sax.SAXParseException e) {
                // ignore warnings
            }
        });
        return builder.parse(filePath.toFile());
    }

    // Write a DOM Document back to disk, pretty-printed.
    public void save(Document doc, Path filePath) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doc.getDoctype().getSystemId());
        transformer.transform(new DOMSource(doc), new StreamResult(filePath.toFile()));
    }

    // Run an XPath expression and return the matching nodes.
    public NodeList queryNodeList(Document doc, String expression) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(expression);
        return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    }

    // Run an XPath expression and return a single string value.
    public String queryString(Document doc, String expression) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (String) xpath.compile(expression).evaluate(doc, XPathConstants.STRING);
    }

    // Run an XPath expression and return a single node.
    public Node queryNode(Document doc, String expression) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    }

    // Apply an XSLT stylesheet to a DOM and return the resulting HTML as a string.
    // The caller can pass in stylesheet parameters via the params map.
    public String applyXslt(Document doc, InputStream xslStream, java.util.Map<String, String> params) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer(new StreamSource(xslStream));
        if (params != null) {
            for (var entry : params.entrySet()) {
                transformer.setParameter(entry.getKey(), entry.getValue());
            }
        }
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}
