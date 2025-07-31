package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.Registration;
import com.pocitaco.oopsh.enums.RegistrationStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegistrationDAO extends BaseDAO<Registration, Integer> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public RegistrationDAO() {
        super("data/registrations.xml", "registration");
    }

    @Override
    protected Registration elementToEntity(Element element) {
        Registration registration = new Registration();
        registration.setId(Integer.parseInt(element.getAttribute("id")));
        registration.setUserId(Integer.parseInt(element.getElementsByTagName("userId").item(0).getTextContent()));
        registration
                .setExamTypeId(Integer.parseInt(element.getElementsByTagName("examTypeId").item(0).getTextContent()));
        registration.setStatus(element.getElementsByTagName("status").item(0).getTextContent());

        try {
            String dateStr = element.getElementsByTagName("registrationDate").item(0).getTextContent();
            registration.setRegistrationDate(LocalDate.parse(dateStr, DATE_FORMATTER));
        } catch (Exception e) {
            registration.setRegistrationDate(LocalDate.now());
        }

        return registration;
    }

    @Override
    protected Element entityToElement(Document doc, Registration registration) {
        Element element = doc.createElement("registration");

        element.setAttribute("id", String.valueOf(registration.getId()));

        Element userIdElement = doc.createElement("userId");
        userIdElement.setTextContent(String.valueOf(registration.getUserId()));
        element.appendChild(userIdElement);

        Element examTypeIdElement = doc.createElement("examTypeId");
        examTypeIdElement.setTextContent(String.valueOf(registration.getExamTypeId()));
        element.appendChild(examTypeIdElement);

        Element statusElement = doc.createElement("status");
        statusElement.setTextContent(registration.getStatus());
        element.appendChild(statusElement);

        Element registrationDateElement = doc.createElement("registrationDate");
        registrationDateElement.setTextContent(registration.getRegistrationDate().format(DATE_FORMATTER));
        element.appendChild(registrationDateElement);

        return element;
    }

    @Override
    protected Integer getEntityId(Registration registration) {
        return registration.getId();
    }

    @Override
    public Registration create(Registration registration) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element rootElement = doc.getDocumentElement();

            registration.setId(generateNextId());
            rootElement.appendChild(entityToElement(doc, registration));

            saveDocument(doc);
            return registration;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Registration save(Registration registration) {
        if (registration.getId() == 0) { // Assuming 0 means new entity
            return create(registration);
        } else {
            return update(registration);
        }
    }

    @Override
    public Optional<Registration> findById(Integer id) {
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
    public Registration update(Registration registration) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element root = doc.getDocumentElement();
            NodeList nodes = doc.getElementsByTagName(getElementName());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(element.getAttribute("id")) == registration.getId()) {
                    root.replaceChild(entityToElement(doc, registration), element);
                    saveDocument(doc);
                    return registration;
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Registration> findAll() {
        lock.readLock().lock();
        try {
            List<Registration> registrations = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                registrations.add(elementToEntity((Element) nodes.item(i)));
            }
            return registrations;
        } finally {
            lock.readLock().unlock();
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
    protected String getElementName() {
        return "registration";
    }

    public List<Registration> findByUserId(int userId) {
        lock.readLock().lock();
        try {
            List<Registration> registrations = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                int elementUserId = Integer.parseInt(element.getElementsByTagName("userId").item(0).getTextContent());
                if (elementUserId == userId) {
                    registrations.add(elementToEntity(element));
                }
            }
            return registrations;
        } finally {
            lock.readLock().unlock();
        }
    }
}