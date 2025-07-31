package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.Result;
import com.pocitaco.oopsh.enums.ResultStatus;
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
 * Data Access Object for Result management
 */
public class ResultDAO {
    private static final String XML_FILE = "data/results.xml";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ResultDAO() {
        initializeDataFile();
    }

    private void initializeDataFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            createDefaultResults();
        }
    }

    private void createDefaultResults() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("results");
            doc.appendChild(root);

            // Create default results
            Result result1 = new Result();
            result1.setId(1);
            result1.setUserId(3); // candidate
            result1.setExamTypeId(1); // theory exam
            result1.setScore(85.0);
            result1.setExamDate(LocalDate.now().minusDays(5));
            result1.setStatus(ResultStatus.PASSED);
            addResultToDocument(doc, root, result1);

            Result result2 = new Result();
            result2.setId(2);
            result2.setUserId(3); // candidate
            result2.setExamTypeId(2); // practical exam
            result2.setScore(78.0);
            result2.setExamDate(LocalDate.now().minusDays(3));
            result2.setStatus(ResultStatus.PASSED);
            addResultToDocument(doc, root, result2);

            // Save to file
            saveDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addResultToDocument(Document doc, Element root, Result result) {
        Element resultElement = doc.createElement("result");

        resultElement.appendChild(createElement(doc, "id", String.valueOf(result.getId())));
        resultElement.appendChild(createElement(doc, "userId", String.valueOf(result.getUserId())));
        resultElement.appendChild(createElement(doc, "examTypeId", String.valueOf(result.getExamTypeId())));
        resultElement.appendChild(createElement(doc, "score", String.valueOf(result.getScore())));
        resultElement.appendChild(createElement(doc, "examDate", result.getExamDate().format(DATE_FORMATTER)));
        resultElement.appendChild(createElement(doc, "status", result.getStatus().name()));

        root.appendChild(resultElement);
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

    public List<Result> findAll() {
        List<Result> results = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                return results;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList resultNodes = doc.getElementsByTagName("result");

            for (int i = 0; i < resultNodes.getLength(); i++) {
                Node resultNode = resultNodes.item(i);
                if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element resultElement = (Element) resultNode;
                    Result result = parseResultFromElement(resultElement);
                    results.add(result);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    private Result parseResultFromElement(Element resultElement) {
        Result result = new Result();

        result.setId(Integer.parseInt(getElementText(resultElement, "id")));
        result.setUserId(Integer.parseInt(getElementText(resultElement, "userId")));
        result.setExamTypeId(Integer.parseInt(getElementText(resultElement, "examTypeId")));
        result.setScore(Double.parseDouble(getElementText(resultElement, "score")));
        result.setExamDate(LocalDate.parse(getElementText(resultElement, "examDate"), DATE_FORMATTER));
        result.setStatus(ResultStatus.valueOf(getElementText(resultElement, "status")));

        return result;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    public Optional<Result> get(int id) {
        return findAll().stream().filter(result -> result.getId() == id).findFirst();
    }

    public void addResult(Result result) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            int newId = findAll().stream().mapToInt(Result::getId).max().orElse(0) + 1;
            result.setId(newId);

            addResultToDocument(doc, root, result);
            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateResult(Result result) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList resultNodes = root.getElementsByTagName("result");
            for (int i = 0; i < resultNodes.getLength(); i++) {
                Node resultNode = resultNodes.item(i);
                if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element resultElement = (Element) resultNode;
                    if (Integer.parseInt(getElementText(resultElement, "id")) == result.getId()) {
                        // Remove the old result element
                        root.removeChild(resultElement);
                        // Add the updated result element
                        addResultToDocument(doc, root, result);
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

            NodeList resultNodes = root.getElementsByTagName("result");
            for (int i = 0; i < resultNodes.getLength(); i++) {
                Node resultNode = resultNodes.item(i);
                if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element resultElement = (Element) resultNode;
                    if (Integer.parseInt(getElementText(resultElement, "id")) == id) {
                        root.removeChild(resultElement);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Result save(Result result) {
        if (result.getId() == 0) {
            addResult(result);
        } else {
            updateResult(result);
        }
        return result;
    }
}