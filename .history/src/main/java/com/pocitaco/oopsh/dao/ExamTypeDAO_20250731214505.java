package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.ExamType;
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
 * Data Access Object for ExamType management
 */
public class ExamTypeDAO {
    private static final String XML_FILE = "data/exam_types.xml";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ExamTypeDAO() {
        initializeDataFile();
    }

    private void initializeDataFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            createDefaultExamTypes();
        }
    }

    private void createDefaultExamTypes() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("examTypes");
            doc.appendChild(root);

            // Create default exam types
            ExamType theory = new ExamType();
            theory.setId(1);
            theory.setName("Thi lý thuyết");
            theory.setDescription("Kỳ thi lý thuyết lái xe");
            theory.setDuration(30);
            theory.setPassingScore(80);
            theory.setFee(500000);
            addExamTypeToDocument(doc, root, theory);

            ExamType practical = new ExamType();
            practical.setId(2);
            practical.setName("Thi thực hành");
            practical.setDescription("Kỳ thi thực hành lái xe");
            practical.setDuration(45);
            practical.setPassingScore(70);
            practical.setFee(800000);
            addExamTypeToDocument(doc, root, practical);

            // Save to file
            saveDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addExamTypeToDocument(Document doc, Element root, ExamType examType) {
        Element examTypeElement = doc.createElement("examType");

        examTypeElement.appendChild(createElement(doc, "id", String.valueOf(examType.getId())));
        examTypeElement.appendChild(createElement(doc, "name", examType.getName()));
        examTypeElement.appendChild(createElement(doc, "description", examType.getDescription()));
        examTypeElement.appendChild(createElement(doc, "duration", String.valueOf(examType.getDuration())));
        examTypeElement.appendChild(createElement(doc, "passingScore", String.valueOf(examType.getPassingScore())));
        examTypeElement.appendChild(createElement(doc, "fee", String.valueOf(examType.getFee())));

        if (examType.getCreatedDate() != null) {
            examTypeElement.appendChild(
                    createElement(doc, "createdDate", examType.getCreatedDate().format(DATETIME_FORMATTER)));
        }

        root.appendChild(examTypeElement);
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

    public List<ExamType> getAll() {
        List<ExamType> examTypes = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                return examTypes;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList examTypeNodes = doc.getElementsByTagName("examType");

            for (int i = 0; i < examTypeNodes.getLength(); i++) {
                Node examTypeNode = examTypeNodes.item(i);
                if (examTypeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element examTypeElement = (Element) examTypeNode;
                    ExamType examType = parseExamTypeFromElement(examTypeElement);
                    examTypes.add(examType);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return examTypes;
    }

    private ExamType parseExamTypeFromElement(Element examTypeElement) {
        ExamType examType = new ExamType();

        examType.setId(Integer.parseInt(getElementText(examTypeElement, "id")));
        examType.setName(getElementText(examTypeElement, "name"));
        examType.setDescription(getElementText(examTypeElement, "description"));
        examType.setDuration(Integer.parseInt(getElementText(examTypeElement, "duration")));
        examType.setPassingScore(Integer.parseInt(getElementText(examTypeElement, "passingScore")));
        examType.setFee(Double.parseDouble(getElementText(examTypeElement, "fee")));

        String createdDateStr = getElementText(examTypeElement, "createdDate");
        if (createdDateStr != null && !createdDateStr.isEmpty()) {
            examType.setCreatedDate(LocalDateTime.parse(createdDateStr, DATETIME_FORMATTER));
        }

        return examType;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    public Optional<ExamType> get(int id) {
        return getAll().stream().filter(examType -> examType.getId() == id).findFirst();
    }

    public void addExamType(ExamType examType) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            int newId = getAll().stream().mapToInt(ExamType::getId).max().orElse(0) + 1;
            examType.setId(newId);
            examType.setCreatedDate(LocalDateTime.now());

            addExamTypeToDocument(doc, root, examType);
            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateExamType(ExamType examType) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList examTypeNodes = root.getElementsByTagName("examType");
            for (int i = 0; i < examTypeNodes.getLength(); i++) {
                Node examTypeNode = examTypeNodes.item(i);
                if (examTypeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element examTypeElement = (Element) examTypeNode;
                    if (Integer.parseInt(getElementText(examTypeElement, "id")) == examType.getId()) {
                        // Remove the old exam type element
                        root.removeChild(examTypeElement);
                        // Add the updated exam type element
                        addExamTypeToDocument(doc, root, examType);
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

            NodeList examTypeNodes = root.getElementsByTagName("examType");
            for (int i = 0; i < examTypeNodes.getLength(); i++) {
                Node examTypeNode = examTypeNodes.item(i);
                if (examTypeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element examTypeElement = (Element) examTypeNode;
                    if (Integer.parseInt(getElementText(examTypeElement, "id")) == id) {
                        root.removeChild(examTypeElement);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteById(int id) {
        try {
            delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}