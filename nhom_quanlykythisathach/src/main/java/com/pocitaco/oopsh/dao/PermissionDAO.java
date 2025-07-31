package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.Permission;
import com.pocitaco.oopsh.enums.UserRole;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PermissionDAO extends BaseDAO<Permission, Integer> {

    public PermissionDAO() {
        super("data/permissions.xml", "permissions");
    }

    @Override
    protected String getElementName() {
        return "permission";
    }

    @Override
    protected Permission elementToEntity(Element element) {
        Permission permission = new Permission();
        permission.setId(Integer.parseInt(getElementText(element, "id")));
        permission.setRole(UserRole.valueOf(getElementText(element, "role")));
        permission.setFunctionality(getElementText(element, "functionality"));
        permission.setHasAccess(Boolean.parseBoolean(getElementText(element, "hasAccess")));
        return permission;
    }

    @Override
    protected Element entityToElement(Document doc, Permission permission) {
        Element element = doc.createElement("permission");
        setElementText(doc, element, "id", String.valueOf(permission.getId()));
        setElementText(doc, element, "role", permission.getRole().name());
        setElementText(doc, element, "functionality", permission.getFunctionality());
        setElementText(doc, element, "hasAccess", String.valueOf(permission.hasAccess()));
        return element;
    }

    @Override
    protected Integer getEntityId(Permission entity) {
        return entity.getId();
    }

    @Override
    public Permission create(Permission entity) {
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
    public Optional<Permission> findById(Integer id) {
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
    public Permission update(Permission entity) {
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
    public List<Permission> findAll() {
        lock.readLock().lock();
        try {
            List<Permission> permissions = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                permissions.add(elementToEntity((Element) nodes.item(i)));
            }
            return permissions;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Permission> getAll() {
        return findAll();
    }

    @Override
    public Permission save(Permission entity) {
        if (entity.getId() == 0) { // Assuming 0 means new entity
            return create(entity);
        } else {
            return update(entity);
        }
    }
}