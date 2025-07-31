package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.Certificate;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Certificate management
 */
public class CertificateDAO {
    private static final String XML_FILE = "data/certificates.xml";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CertificateDAO() {
        initializeDataFile();
    }

    private void initializeDataFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            createDefaultCertificates();
        }
    }

    private void createDefaultCertificates() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("certificates");
            doc.appendChild(root);

            // Create default certificates
            Certificate cert1 = new Certificate();
            cert1.setId(1);
            cert1.setCandidateId(3);
            cert1.setExamId(1);
            cert1.setCertificateNumber("CERT-2024-001");
            cert1.setScore(85.0);
            cert1.setGrade("A");
            cert1.setIssuedDate(LocalDate.now().minusDays(30));
            addCertificateToDocument(doc, root, cert1);

            Certificate cert2 = new Certificate();
            cert2.setId(2);
            cert2.setCandidateId(3);
            cert2.setExamId(2);
            cert2.setCertificateNumber("CERT-2024-002");
            cert2.setScore(78.0);
            cert2.setGrade("B");
            cert2.setIssuedDate(LocalDate.now().minusDays(15));
            addCertificateToDocument(doc, root, cert2);

            // Save to file
            saveDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCertificateToDocument(Document doc, Element root, Certificate certificate) {
        Element certElement = doc.createElement("certificate");

        certElement.appendChild(createElement(doc, "id", String.valueOf(certificate.getId())));
        certElement.appendChild(createElement(doc, "candidateId", String.valueOf(certificate.getCandidateId())));
        certElement.appendChild(createElement(doc, "examId", String.valueOf(certificate.getExamId())));
        certElement.appendChild(createElement(doc, "certificateNumber", certificate.getCertificateNumber()));
        certElement.appendChild(createElement(doc, "score", String.valueOf(certificate.getScore())));
        certElement.appendChild(createElement(doc, "grade", certificate.getGrade()));
        certElement.appendChild(createElement(doc, "issuedDate", certificate.getIssuedDate().format(DATE_FORMATTER)));

        root.appendChild(certElement);
    }

    private Element createElement(Document doc, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        return element;
    }

    private void saveDocument(Document doc) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(XML_FILE));
        transformer.transform(source, result);
    }

    public List<Certificate> findAll() {
        List<Certificate> certificates = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                return certificates;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList certNodes = doc.getElementsByTagName("certificate");

            for (int i = 0; i < certNodes.getLength(); i++) {
                Node certNode = certNodes.item(i);
                if (certNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element certElement = (Element) certNode;
                    Certificate certificate = parseCertificateFromElement(certElement);
                    certificates.add(certificate);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return certificates;
    }

    private Certificate parseCertificateFromElement(Element certElement) {
        Certificate certificate = new Certificate();

        certificate.setId(Integer.parseInt(getElementText(certElement, "id")));
        certificate.setCandidateId(Integer.parseInt(getElementText(certElement, "candidateId")));
        certificate.setExamId(Integer.parseInt(getElementText(certElement, "examId")));
        certificate.setCertificateNumber(getElementText(certElement, "certificateNumber"));
        certificate.setScore(Double.parseDouble(getElementText(certElement, "score")));
        certificate.setGrade(getElementText(certElement, "grade"));
        certificate.setIssuedDate(LocalDate.parse(getElementText(certElement, "issuedDate"), DATE_FORMATTER));

        return certificate;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    public Optional<Certificate> get(int id) {
        return findAll().stream().filter(cert -> cert.getId() == id).findFirst();
    }

    public void addCertificate(Certificate certificate) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            int newId = findAll().stream().mapToInt(Certificate::getId).max().orElse(0) + 1;
            certificate.setId(newId);

            addCertificateToDocument(doc, root, certificate);
            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCertificate(Certificate certificate) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList certNodes = root.getElementsByTagName("certificate");
            for (int i = 0; i < certNodes.getLength(); i++) {
                Node certNode = certNodes.item(i);
                if (certNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element certElement = (Element) certNode;
                    if (Integer.parseInt(getElementText(certElement, "id")) == certificate.getId()) {
                        // Remove the old certificate element
                        root.removeChild(certElement);
                        // Add the updated certificate element
                        addCertificateToDocument(doc, root, certificate);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList certNodes = root.getElementsByTagName("certificate");
            for (int i = 0; i < certNodes.getLength(); i++) {
                Node certNode = certNodes.item(i);
                if (certNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element certElement = (Element) certNode;
                    if (Integer.parseInt(getElementText(certElement, "id")) == id) {
                        root.removeChild(certElement);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Certificate save(Certificate certificate) {
        if (certificate.getId() == 0) {
            addCertificate(certificate);
        } else {
            updateCertificate(certificate);
        }
        return certificate;
    }
}
