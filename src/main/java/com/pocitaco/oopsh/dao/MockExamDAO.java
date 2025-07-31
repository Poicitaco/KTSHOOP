package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.MockExam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockExamDAO extends BaseDAO<MockExam, Integer> {

    public MockExamDAO() {
        super("data/mock_exams.xml", "mockExams");
    }

    @Override
    protected String getElementName() {
        return "mockExam";
    }

    @Override
    protected Integer getEntityId(MockExam mockExam) {
        return mockExam.getId();
    }

    @Override
    protected Element entityToElement(Document doc, MockExam mockExam) {
        Element element = doc.createElement(getElementName());
        setElementText(doc, element, "id", String.valueOf(mockExam.getId()));
        setElementText(doc, element, "name", mockExam.getName());
        setElementText(doc, element, "description", mockExam.getDescription());
        setElementText(doc, element, "filePath", mockExam.getFilePath());
        return element;
    }

    @Override
    protected MockExam elementToEntity(Element element) {
        MockExam mockExam = new MockExam();
        mockExam.setId(Integer.parseInt(getElementText(element, "id")));
        mockExam.setName(getElementText(element, "name"));
        mockExam.setDescription(getElementText(element, "description"));
        mockExam.setFilePath(getElementText(element, "filePath"));
        return mockExam;
    }

    @Override
    public MockExam create(MockExam entity) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element root = doc.getDocumentElement();
            entity.setId(generateNextId());
            root.appendChild(entityToElement(doc, entity));
            saveDocument(doc);
            return entity;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public MockExam save(MockExam entity) {
        if (entity.getId() == 0) {
            return create(entity);
        } else {
            return update(entity);
        }
    }

    @Override
    public Optional<MockExam> findById(Integer id) {
        lock.readLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(getElementText(element, "id")) == id) {
                    return Optional.of(elementToEntity(element));
                }
            }
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<MockExam> findAll() {
        lock.readLock().lock();
        try {
            List<MockExam> results = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                results.add(elementToEntity((Element) nodes.item(i)));
            }
            return results;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public MockExam update(MockExam entity) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(getElementText(element, "id")) == entity.getId()) {
                    Element parent = (Element) element.getParentNode();
                    parent.replaceChild(entityToElement(doc, entity), element);
                    saveDocument(doc);
                    return entity;
                }
            }
            throw new RuntimeException("Entity with id " + entity.getId() + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(getElementText(element, "id")) == id) {
                    element.getParentNode().removeChild(element);
                    saveDocument(doc);
                    return true;
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Convenience method for compatibility
    public List<MockExam> getAll() {
        return findAll();
    }
}
