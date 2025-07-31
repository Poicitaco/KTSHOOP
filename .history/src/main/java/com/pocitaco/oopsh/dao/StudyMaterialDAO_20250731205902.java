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
        material.setName(element.getElementsByTagName("name").item(0).getTextContent());
        material.setDescription(element.getElementsByTagName("description").item(0).getTextContent());
        material.setFilePath(element.getElementsByTagName("filePath").item(0).getTextContent());
        
        return material;
    }

    @Override
    protected void entityToElement(StudyMaterial material, Element element) {
        Document doc = element.getOwnerDocument();
        
        element.setAttribute("id", String.valueOf(material.getId()));
        
        Element nameElement = doc.createElement("name");
        nameElement.setTextContent(material.getName());
        element.appendChild(nameElement);
        
        Element descriptionElement = doc.createElement("description");
        descriptionElement.setTextContent(material.getDescription());
        element.appendChild(descriptionElement);
        
        Element filePathElement = doc.createElement("filePath");
        filePathElement.setTextContent(material.getFilePath());
        element.appendChild(filePathElement);
    }

    @Override
    protected Integer getEntityId(StudyMaterial material) {
        return material.getId();
    }

    @Override
    public StudyMaterial save(StudyMaterial material) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element rootElement = doc.getDocumentElement();
            
            Element materialElement = doc.createElement(getElementName());
            entityToElement(material, materialElement);
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
                    // Clear existing children
                    while (element.hasChildNodes()) {
                        element.removeChild(element.getFirstChild());
                    }
                    entityToElement(material, element);
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
    public void delete(Integer id) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(element.getAttribute("id")) == id) {
                    element.getParentNode().removeChild(element);
                    saveDocument(doc);
                    break;
                }
            }
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
