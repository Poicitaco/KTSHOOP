package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.interfaces.CrudOperations;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract base DAO class implementing common XML operations
 * Uses Template Method pattern and provides thread-safe operations
 */
public abstract class BaseDAO<T, ID> implements CrudOperations<T, ID> {

    protected final String xmlFilePath;
    protected final String rootElementName;
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    public BaseDAO(String xmlFilePath, String rootElementName) {
        this.xmlFilePath = xmlFilePath;
        this.rootElementName = rootElementName;
        initializeXMLFile();
    }

    /**
     * Initialize XML file if it doesn't exist
     */
    private void initializeXMLFile() {
        File file = new File(xmlFilePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();

                Element root = doc.createElement(rootElementName);
                doc.appendChild(root);

                saveDocument(doc);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize XML file: " + xmlFilePath, e);
            }
        }
    }

    /**
     * Load XML document with read lock
     */
    protected Document loadDocument() {
        lock.readLock().lock();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new File(xmlFilePath));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load XML document", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Save XML document with write lock
     */
    protected void saveDocument(Document doc) {
        lock.writeLock().lock();
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty("indent", "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save XML document", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Generate next ID for new entities
     */
    protected int generateNextId() {
        Document doc = loadDocument();
        NodeList nodes = doc.getElementsByTagName(getElementName());
        int maxId = 0;

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String idText = getElementText(element, "id");
            if (idText != null) {
                try {
                    int id = Integer.parseInt(idText);
                    maxId = Math.max(maxId, id);
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }

        return maxId + 1;
    }

    /**
     * Helper method to get text content of an element
     */
    protected String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent();
        }
        return null;
    }

    /**
     * Helper method to set text content of an element
     */
    protected void setElementText(Document doc, Element parent, String tagName, String value) {
        Element element = doc.createElement(tagName);
        element.setTextContent(value != null ? value : "");
        parent.appendChild(element);
    }

    /**
     * Abstract method to get element name for this entity type
     */
    protected abstract String getElementName();

    /**
     * Abstract method to convert XML element to entity
     */
    protected abstract T elementToEntity(Element element);

    /**
     * Abstract method to convert entity to XML element
     */
    protected abstract Element entityToElement(Document doc, T entity);

    /**
     * Abstract method to extract ID from entity
     */
    protected abstract ID getEntityId(T entity);

    @Override
    public long count() {
        Document doc = loadDocument();
        NodeList nodes = doc.getElementsByTagName(getElementName());
        return nodes.getLength();
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id) != null;
    }
}
