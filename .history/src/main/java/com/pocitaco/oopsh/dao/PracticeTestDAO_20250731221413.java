package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.PracticeTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PracticeTestDAO extends BaseDAO<PracticeTest, Integer> {

    public PracticeTestDAO() {
        super("data/practice_tests.xml", "practiceTests");
    }

    @Override
    protected String getElementName() {
        return "practiceTest";
    }

    @Override
    protected PracticeTest elementToEntity(Element element) {
        PracticeTest practiceTest = new PracticeTest();
        practiceTest.setId(Integer.parseInt(getElementText(element, "id")));
        practiceTest.setName(getElementText(element, "name"));
        practiceTest.setDescription(getElementText(element, "description"));
        practiceTest.setFilePath(getElementText(element, "filePath"));
        return practiceTest;
    }

    @Override
    protected Element entityToElement(Document doc, PracticeTest practiceTest) {
        Element element = doc.createElement("practiceTest");
        setElementText(doc, element, "id", String.valueOf(practiceTest.getId()));
        setElementText(doc, element, "name", practiceTest.getName());
        setElementText(doc, element, "description", practiceTest.getDescription());
        setElementText(doc, element, "filePath", practiceTest.getFilePath());
        return element;
    }

    @Override
    protected Integer getEntityId(PracticeTest entity) {
        return entity.getId();
    }

    @Override
    public PracticeTest create(PracticeTest entity) {
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
    public Optional<PracticeTest> findById(Integer id) {
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
    public List<PracticeTest> findAll() {
        lock.readLock().lock();
        try {
            List<PracticeTest> practiceTests = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                practiceTests.add(elementToEntity((Element) nodes.item(i)));
            }
            return practiceTests;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<PracticeTest> getAll() {
        return findAll();
    }

    @Override
    public PracticeTest update(PracticeTest entity) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element root = doc.getDocumentElement();
            NodeList nodes = root.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(getElementText(element, "id")) == entity.getId()) {
                    root.replaceChild(entityToElement(doc, entity), element);
                    saveDocument(doc);
                    return entity;
                }
            }
            return null; // Or throw an exception if not found
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element root = doc.getDocumentElement();
            NodeList nodes = root.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(getElementText(element, "id")) == id) {
                    root.removeChild(element);
                    saveDocument(doc);
                    return true;
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public PracticeTest save(PracticeTest entity) {
        if (entity.getId() == 0) { // Assuming 0 means new entity
            return create(entity);
        } else {
            return update(entity);
        }
    }
}