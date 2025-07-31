package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.SessionReport;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for SessionReport management
 */
public class SessionReportDAO {
    private static final String XML_FILE = "data/session_reports.xml";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SessionReportDAO() {
        initializeDataFile();
    }

    private void initializeDataFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            createDefaultSessionReports();
        }
    }

    private void createDefaultSessionReports() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("sessionReports");
            doc.appendChild(root);

            // Create default session reports
            SessionReport report1 = new SessionReport();
            report1.setId(1);
            report1.setSessionId(1);
            report1.setExaminerId(2);
            report1.setReportContent("Kỳ thi diễn ra tốt, thí sinh làm bài nghiêm túc");
            report1.setNotes("Không có vấn đề gì đặc biệt");
            report1.setCreatedDate(LocalDateTime.now().minusDays(2));
            addSessionReportToDocument(doc, root, report1);

            SessionReport report2 = new SessionReport();
            report2.setId(2);
            report2.setSessionId(2);
            report2.setExaminerId(2);
            report2.setReportContent("Kỳ thi có một số thí sinh đến muộn");
            report2.setNotes("Cần nhắc nhở thí sinh về giờ giấc");
            report2.setCreatedDate(LocalDateTime.now().minusDays(1));
            addSessionReportToDocument(doc, root, report2);

            // Save to file
            saveDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSessionReportToDocument(Document doc, Element root, SessionReport report) {
        Element reportElement = doc.createElement("sessionReport");

        reportElement.appendChild(createElement(doc, "id", String.valueOf(report.getId())));
        reportElement.appendChild(createElement(doc, "sessionId", String.valueOf(report.getSessionId())));
        reportElement.appendChild(createElement(doc, "examinerId", String.valueOf(report.getExaminerId())));
        reportElement.appendChild(createElement(doc, "reportContent", report.getReportContent()));
        reportElement.appendChild(createElement(doc, "notes", report.getNotes()));
        reportElement
                .appendChild(createElement(doc, "createdDate", report.getCreatedDate().format(DATETIME_FORMATTER)));

        root.appendChild(reportElement);
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

    public List<SessionReport> findAll() {
        List<SessionReport> reports = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                return reports;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList reportNodes = doc.getElementsByTagName("sessionReport");

            for (int i = 0; i < reportNodes.getLength(); i++) {
                Node reportNode = reportNodes.item(i);
                if (reportNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element reportElement = (Element) reportNode;
                    SessionReport report = parseSessionReportFromElement(reportElement);
                    reports.add(report);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reports;
    }

    private SessionReport parseSessionReportFromElement(Element reportElement) {
        SessionReport report = new SessionReport();

        report.setId(Integer.parseInt(getElementText(reportElement, "id")));
        report.setSessionId(Integer.parseInt(getElementText(reportElement, "sessionId")));
        report.setExaminerId(Integer.parseInt(getElementText(reportElement, "examinerId")));
        report.setReportContent(getElementText(reportElement, "reportContent"));
        report.setNotes(getElementText(reportElement, "notes"));
        report.setCreatedDate(LocalDateTime.parse(getElementText(reportElement, "createdDate"), DATETIME_FORMATTER));

        return report;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    public Optional<SessionReport> get(int id) {
        return findAll().stream().filter(report -> report.getId() == id).findFirst();
    }

    public void addSessionReport(SessionReport report) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            int newId = findAll().stream().mapToInt(SessionReport::getId).max().orElse(0) + 1;
            report.setId(newId);

            addSessionReportToDocument(doc, root, report);
            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSessionReport(SessionReport report) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList reportNodes = root.getElementsByTagName("sessionReport");
            for (int i = 0; i < reportNodes.getLength(); i++) {
                Node reportNode = reportNodes.item(i);
                if (reportNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element reportElement = (Element) reportNode;
                    if (Integer.parseInt(getElementText(reportElement, "id")) == report.getId()) {
                        // Remove the old report element
                        root.removeChild(reportElement);
                        // Add the updated report element
                        addSessionReportToDocument(doc, root, report);
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

            NodeList reportNodes = root.getElementsByTagName("sessionReport");
            for (int i = 0; i < reportNodes.getLength(); i++) {
                Node reportNode = reportNodes.item(i);
                if (reportNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element reportElement = (Element) reportNode;
                    if (Integer.parseInt(getElementText(reportElement, "id")) == id) {
                        root.removeChild(reportElement);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SessionReport save(SessionReport report) {
        if (report.getId() == 0) {
            addSessionReport(report);
        } else {
            updateSessionReport(report);
        }
        return report;
    }
}
