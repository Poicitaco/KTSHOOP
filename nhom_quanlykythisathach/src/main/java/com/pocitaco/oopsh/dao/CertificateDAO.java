package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.interfaces.CrudOperations;
import com.pocitaco.oopsh.models.Certificate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CertificateDAO extends BaseDAO<Certificate, Integer> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CertificateDAO() {
        super("certificates.xml", "certificate");
    }

    @Override
    protected Certificate elementToEntity(Element element) {
        Certificate certificate = new Certificate();
        certificate.setId(Integer.parseInt(element.getAttribute("id")));
        certificate
                .setCandidateId(Integer.parseInt(element.getElementsByTagName("candidateId").item(0).getTextContent()));
        certificate.setExamId(Integer.parseInt(element.getElementsByTagName("examId").item(0).getTextContent()));
        certificate.setCertificateNumber(element.getElementsByTagName("certificateNumber").item(0).getTextContent());
        certificate.setScore(Double.parseDouble(element.getElementsByTagName("score").item(0).getTextContent()));
        certificate.setGrade(element.getElementsByTagName("grade").item(0).getTextContent());

        try {
            String dateStr = element.getElementsByTagName("issuedDate").item(0).getTextContent();
            certificate.setIssuedDate(LocalDate.parse(dateStr, DATE_FORMATTER));
        } catch (Exception e) {
            certificate.setIssuedDate(LocalDate.now());
        }

        return certificate;
    }

    @Override
    protected Element entityToElement(Document doc, Certificate certificate) {
        Element element = doc.createElement("certificate");

        element.setAttribute("id", String.valueOf(certificate.getId()));

        Element candidateIdElement = doc.createElement("candidateId");
        candidateIdElement.setTextContent(String.valueOf(certificate.getCandidateId()));
        element.appendChild(candidateIdElement);

        Element examIdElement = doc.createElement("examId");
        examIdElement.setTextContent(String.valueOf(certificate.getExamId()));
        element.appendChild(examIdElement);

        Element certificateNumberElement = doc.createElement("certificateNumber");
        certificateNumberElement.setTextContent(certificate.getCertificateNumber());
        element.appendChild(certificateNumberElement);

        Element scoreElement = doc.createElement("score");
        scoreElement.setTextContent(String.valueOf(certificate.getScore()));
        element.appendChild(scoreElement);

        Element gradeElement = doc.createElement("grade");
        gradeElement.setTextContent(certificate.getGrade());
        element.appendChild(gradeElement);

        Element issuedDateElement = doc.createElement("issuedDate");
        issuedDateElement.setTextContent(certificate.getIssuedDate().format(DATE_FORMATTER));
        element.appendChild(issuedDateElement);

        return element;
    }

    @Override
    protected String getElementName() {
        return "certificate";
    }

    @Override
    protected Integer getEntityId(Certificate certificate) {
        return certificate.getId();
    }

    @Override
    public Certificate create(Certificate certificate) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element rootElement = doc.getDocumentElement();

            Element certificateElement = entityToElement(doc, certificate);
            rootElement.appendChild(certificateElement);

            saveDocument(doc);
            return certificate;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Certificate update(Certificate certificate) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(element.getAttribute("id")) == certificate.getId()) {
                    Element newElement = entityToElement(doc, certificate);
                    element.getParentNode().replaceChild(newElement, element);
                    saveDocument(doc);
                    return certificate;
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Certificate save(Certificate certificate) {
        if (certificate.getId() == 0) {
            return create(certificate);
        } else {
            return update(certificate);
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
    public Optional<Certificate> findById(Integer id) {
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
    public List<Certificate> findAll() {
        lock.readLock().lock();
        try {
            List<Certificate> certificates = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                certificates.add(elementToEntity((Element) nodes.item(i)));
            }
            return certificates;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Certificate> getAll() {
        return findAll();
    }

    public List<Certificate> findByCandidateId(int candidateId) {
        lock.readLock().lock();
        try {
            List<Certificate> certificates = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                int elementCandidateId = Integer
                        .parseInt(element.getElementsByTagName("candidateId").item(0).getTextContent());
                if (elementCandidateId == candidateId) {
                    certificates.add(elementToEntity(element));
                }
            }
            return certificates;
        } finally {
            lock.readLock().unlock();
        }
    }
}
