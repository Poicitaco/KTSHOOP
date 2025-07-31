package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.interfaces.CrudOperations;
import com.pocitaco.oopsh.models.SessionReport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionReportDAO extends BaseDAO<SessionReport, Integer> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SessionReportDAO() {
        super("session_reports.xml", "sessionReport");
    }

    @Override
    protected SessionReport elementToEntity(Element element) {
        SessionReport report = new SessionReport();
        report.setId(Integer.parseInt(element.getAttribute("id")));
        report.setSessionId(Integer.parseInt(element.getElementsByTagName("sessionId").item(0).getTextContent()));
        report.setExaminerId(Integer.parseInt(element.getElementsByTagName("examinerId").item(0).getTextContent()));
        report.setReportContent(element.getElementsByTagName("reportContent").item(0).getTextContent());
        report.setNotes(element.getElementsByTagName("notes").item(0).getTextContent());

        try {
            String dateStr = element.getElementsByTagName("createdDate").item(0).getTextContent();
            report.setCreatedDate(LocalDateTime.parse(dateStr, DATE_FORMATTER));
        } catch (Exception e) {
            report.setCreatedDate(LocalDateTime.now());
        }

        return report;
    }

    @Override
    protected Element entityToElement(Document doc, SessionReport report) {
        Element element = doc.createElement("sessionReport");

        element.setAttribute("id", String.valueOf(report.getId()));

        Element sessionIdElement = doc.createElement("sessionId");
        sessionIdElement.setTextContent(String.valueOf(report.getSessionId()));
        element.appendChild(sessionIdElement);

        Element examinerIdElement = doc.createElement("examinerId");
        examinerIdElement.setTextContent(String.valueOf(report.getExaminerId()));
        element.appendChild(examinerIdElement);

        Element reportContentElement = doc.createElement("reportContent");
        reportContentElement.setTextContent(report.getReportContent());
        element.appendChild(reportContentElement);

        Element notesElement = doc.createElement("notes");
        notesElement.setTextContent(report.getNotes());
        element.appendChild(notesElement);

        Element createdDateElement = doc.createElement("createdDate");
        createdDateElement.setTextContent(report.getCreatedDate().format(DATE_FORMATTER));
        element.appendChild(createdDateElement);

        return element;
    }

    @Override
    protected String getElementName() {
        return "sessionReport";
    }

    @Override
    protected Integer getEntityId(SessionReport report) {
        return report.getId();
    }

    @Override
    public SessionReport create(SessionReport report) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element rootElement = doc.getDocumentElement();

            Element reportElement = entityToElement(doc, report);
            rootElement.appendChild(reportElement);

            saveDocument(doc);
            return report;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public SessionReport update(SessionReport report) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(element.getAttribute("id")) == report.getId()) {
                    Element newElement = entityToElement(doc, report);
                    element.getParentNode().replaceChild(newElement, element);
                    saveDocument(doc);
                    return report;
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public SessionReport save(SessionReport report) {
        if (report.getId() == 0) {
            return create(report);
        } else {
            return update(report);
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
    public Optional<SessionReport> findById(Integer id) {
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
    public List<SessionReport> findAll() {
        lock.readLock().lock();
        try {
            List<SessionReport> reports = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                reports.add(elementToEntity((Element) nodes.item(i)));
            }
            return reports;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<SessionReport> getAll() {
        return findAll();
    }
}
