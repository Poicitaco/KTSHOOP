package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.interfaces.CrudOperations;
import com.pocitaco.oopsh.models.StudyMaterial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudyMaterialDAO extends BaseDAO<StudyMaterial, Integer> {

    public StudyMaterialDAO() {
        super("study_materials.xml", "studyMaterial");
    }

    @Override
    protected StudyMaterial elementToEntity(Element element) {
        StudyMaterial material = new StudyMaterial();
        material.setId(Integer.parseInt(element.getAttribute("id")));
        material.setTitle(element.getElementsByTagName("title").item(0).getTextContent());
        material.setDescription(element.getElementsByTagName("description").item(0).getTextContent());
        material.setFileUrl(element.getElementsByTagName("fileUrl").item(0).getTextContent());

        return material;
    }

    @Override
    protected Element entityToElement(Document doc, StudyMaterial material) {
        Element element = doc.createElement("studyMaterial");

        element.setAttribute("id", String.valueOf(material.getId()));

        Element titleElement = doc.createElement("title");
        titleElement.setTextContent(material.getTitle());
        element.appendChild(titleElement);

        Element descriptionElement = doc.createElement("description");
        descriptionElement.setTextContent(material.getDescription());
        element.appendChild(descriptionElement);

        Element fileUrlElement = doc.createElement("fileUrl");
        fileUrlElement.setTextContent(material.getFileUrl());
        element.appendChild(fileUrlElement);

        return element;
    }

    @Override
    protected String getElementName() {
        return "studyMaterial";
    }

    @Override
    protected Integer getEntityId(StudyMaterial material) {
        return material.getId();
    }

    @Override
    public StudyMaterial create(StudyMaterial material) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element rootElement = doc.getDocumentElement();

            Element materialElement = entityToElement(doc, material);
            rootElement.appendChild(materialElement);

            saveDocument(doc);
            return material;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public StudyMaterial update(StudyMaterial material) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(element.getAttribute("id")) == material.getId()) {
                    Element newElement = entityToElement(doc, material);
                    element.getParentNode().replaceChild(newElement, element);
                    saveDocument(doc);
                    return material;
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public StudyMaterial save(StudyMaterial material) {
        if (material.getId() == 0) {
            return create(material);
        } else {
            return update(material);
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
                if (Integer.parseInt(element.getAttribute("id")) == id) {
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

    @Override
    public Optional<StudyMaterial> findById(Integer id) {
        lock.readLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(element.getAttribute("id")) == id) {
                    return Optional.of(elementToEntity(element));
                }
            }
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<StudyMaterial> findAll() {
        lock.readLock().lock();
        try {
            List<StudyMaterial> materials = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                materials.add(elementToEntity((Element) nodes.item(i)));
            }
            return materials;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<StudyMaterial> getAll() {
        return findAll();
    }
}
