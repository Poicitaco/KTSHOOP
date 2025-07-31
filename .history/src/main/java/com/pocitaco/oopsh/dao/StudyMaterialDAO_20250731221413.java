package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.StudyMaterial;
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
 * Data Access Object for StudyMaterial management
 */
public class StudyMaterialDAO {
    private static final String XML_FILE = "data/study_materials.xml";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StudyMaterialDAO() {
        initializeDataFile();
    }

    private void initializeDataFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            createDefaultStudyMaterials();
        }
    }

    private void createDefaultStudyMaterials() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("studyMaterials");
            doc.appendChild(root);

            // Create default study materials
            StudyMaterial material1 = new StudyMaterial();
            material1.setId(1);
            material1.setTitle("Luật Giao thông đường bộ");
            material1.setDescription("Tài liệu học tập về luật giao thông đường bộ");
            material1.setFileUrl("/materials/luat_giao_thong.pdf");
            material1.setCreatedDate(LocalDateTime.now().minusDays(30));
            addStudyMaterialToDocument(doc, root, material1);

            StudyMaterial material2 = new StudyMaterial();
            material2.setId(2);
            material2.setTitle("Kỹ thuật lái xe cơ bản");
            material2.setDescription("Hướng dẫn kỹ thuật lái xe cơ bản");
            material2.setFileUrl("/materials/ky_thuat_lai_xe.pdf");
            material2.setCreatedDate(LocalDateTime.now().minusDays(20));
            addStudyMaterialToDocument(doc, root, material2);

            // Save to file
            saveDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addStudyMaterialToDocument(Document doc, Element root, StudyMaterial material) {
        Element materialElement = doc.createElement("studyMaterial");

        materialElement.appendChild(createElement(doc, "id", String.valueOf(material.getId())));
        materialElement.appendChild(createElement(doc, "title", material.getTitle()));
        materialElement.appendChild(createElement(doc, "description", material.getDescription()));
        materialElement.appendChild(createElement(doc, "fileUrl", material.getFileUrl()));
        materialElement
                .appendChild(createElement(doc, "createdDate", material.getCreatedDate().format(DATETIME_FORMATTER)));

        root.appendChild(materialElement);
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

    public List<StudyMaterial> findAll() {
        List<StudyMaterial> materials = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                return materials;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList materialNodes = doc.getElementsByTagName("studyMaterial");

            for (int i = 0; i < materialNodes.getLength(); i++) {
                Node materialNode = materialNodes.item(i);
                if (materialNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element materialElement = (Element) materialNode;
                    StudyMaterial material = parseStudyMaterialFromElement(materialElement);
                    materials.add(material);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return materials;
    }

    private StudyMaterial parseStudyMaterialFromElement(Element materialElement) {
        StudyMaterial material = new StudyMaterial();

        material.setId(Integer.parseInt(getElementText(materialElement, "id")));
        material.setTitle(getElementText(materialElement, "title"));
        material.setDescription(getElementText(materialElement, "description"));
        material.setFileUrl(getElementText(materialElement, "fileUrl"));
        material.setCreatedDate(
                LocalDateTime.parse(getElementText(materialElement, "createdDate"), DATETIME_FORMATTER));

        return material;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    public Optional<StudyMaterial> get(int id) {
        return findAll().stream().filter(material -> material.getId() == id).findFirst();
    }

    public void addStudyMaterial(StudyMaterial material) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            int newId = findAll().stream().mapToInt(StudyMaterial::getId).max().orElse(0) + 1;
            material.setId(newId);

            addStudyMaterialToDocument(doc, root, material);
            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStudyMaterial(StudyMaterial material) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList materialNodes = root.getElementsByTagName("studyMaterial");
            for (int i = 0; i < materialNodes.getLength(); i++) {
                Node materialNode = materialNodes.item(i);
                if (materialNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element materialElement = (Element) materialNode;
                    if (Integer.parseInt(getElementText(materialElement, "id")) == material.getId()) {
                        // Remove the old material element
                        root.removeChild(materialElement);
                        // Add the updated material element
                        addStudyMaterialToDocument(doc, root, material);
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

            NodeList materialNodes = root.getElementsByTagName("studyMaterial");
            for (int i = 0; i < materialNodes.getLength(); i++) {
                Node materialNode = materialNodes.item(i);
                if (materialNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element materialElement = (Element) materialNode;
                    if (Integer.parseInt(getElementText(materialElement, "id")) == id) {
                        root.removeChild(materialElement);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StudyMaterial save(StudyMaterial material) {
        if (material.getId() == 0) {
            addStudyMaterial(material);
        } else {
            updateStudyMaterial(material);
        }
        return material;
    }
}
