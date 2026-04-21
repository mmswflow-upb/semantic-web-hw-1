package org.bighw1.big_hw_1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.file.Path;
import java.util.Map;

@Service
public class XmlService {

    private static final Logger log = LoggerFactory.getLogger(XmlService.class);

    private final XPathFactory xpathFactory = XPathFactory.newInstance();

    public Document load(Path filePath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler() {
            @Override
            public void warning(org.xml.sax.SAXParseException e) {
                log.warn("XML validation warning: {}", e.getMessage());
            }

            @Override
            public void error(org.xml.sax.SAXParseException e) throws SAXException {
                throw e;
            }
        });
        return builder.parse(filePath.toFile());
    }

    public void save(Document doc, Path filePath) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doc.getDoctype().getSystemId());
        transformer.transform(new DOMSource(doc), new StreamResult(filePath.toFile()));
    }

    public NodeList queryNodeList(Document doc, String expression) throws XPathExpressionException {
        XPath xpath = xpathFactory.newXPath();
        return (NodeList) xpath.compile(expression).evaluate(doc, XPathConstants.NODESET);
    }

    public Node queryNode(Document doc, String expression) throws XPathExpressionException {
        XPath xpath = xpathFactory.newXPath();
        return (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    }

    public String applyXslt(Document doc, InputStream xslStream, Map<String, String> params) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer(new StreamSource(xslStream));
        if (params != null) {
            params.forEach(transformer::setParameter);
        }
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}
