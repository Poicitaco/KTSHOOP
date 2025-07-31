package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.enums.ScheduleStatus;
import com.pocitaco.oopsh.enums.TimeSlot;
import com.pocitaco.oopsh.models.ExamSchedule;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for ExamSchedule management
 */
public class ExamScheduleDAO {
    private static final String XML_FILE = "data/exam_schedules.xml";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ExamScheduleDAO() {
        initializeDataFile();
    }

    private void initializeDataFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            createDefaultSchedules();
        }
    }

    private void createDefaultSchedules() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("examSchedules");
            doc.appendChild(root);

            // Create some default exam schedules
            ExamSchedule schedule1 = new ExamSchedule();
            schedule1.setId(1);
            schedule1.setExamTypeId(1);
            schedule1.setExaminerId(2);
            schedule1.setExamDate(LocalDate.now().plusDays(7));
            schedule1.setTimeSlot(TimeSlot.MORNING);
            schedule1.setMaxCandidates(20);
            schedule1.setRegisteredCandidates(15);
            schedule1.setStatus(ScheduleStatus.OPEN);
            schedule1.setLocation("Trung tâm sát hạch Hà Nội");
            addScheduleToDocument(doc, root, schedule1);

            ExamSchedule schedule2 = new ExamSchedule();
            schedule2.setId(2);
            schedule2.setExamTypeId(2);
            schedule2.setExaminerId(2);
            schedule2.setExamDate(LocalDate.now().plusDays(14));
            schedule2.setTimeSlot(TimeSlot.AFTERNOON);
            schedule2.setMaxCandidates(15);
            schedule2.setRegisteredCandidates(8);
            schedule2.setStatus(ScheduleStatus.OPEN);
            schedule2.setLocation("Trung tâm sát hạch Hà Nội");
            addScheduleToDocument(doc, root, schedule2);

            // Save to file
            saveDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addScheduleToDocument(Document doc, Element root, ExamSchedule schedule) {
        Element scheduleElement = doc.createElement("examSchedule");

        scheduleElement.appendChild(createElement(doc, "id", String.valueOf(schedule.getId())));
        scheduleElement.appendChild(createElement(doc, "examTypeId", String.valueOf(schedule.getExamTypeId())));
        scheduleElement.appendChild(createElement(doc, "examinerId", String.valueOf(schedule.getExaminerId())));
        scheduleElement.appendChild(createElement(doc, "examDate", schedule.getExamDate().format(DATE_FORMATTER)));
        scheduleElement.appendChild(createElement(doc, "timeSlot", schedule.getTimeSlot().name()));
        scheduleElement.appendChild(createElement(doc, "maxCandidates", String.valueOf(schedule.getMaxCandidates())));
        scheduleElement.appendChild(createElement(doc, "registeredCandidates", String.valueOf(schedule.getRegisteredCandidates())));
        scheduleElement.appendChild(createElement(doc, "status", schedule.getStatus().name()));
        scheduleElement.appendChild(createElement(doc, "location", schedule.getLocation()));

        if (schedule.getCreatedDate() != null) {
            scheduleElement.appendChild(createElement(doc, "createdDate", schedule.getCreatedDate().format(DATETIME_FORMATTER)));
        }

        root.appendChild(scheduleElement);
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

    public List<ExamSchedule> getAllSchedules() {
        List<ExamSchedule> schedules = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                return schedules;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList scheduleNodes = doc.getElementsByTagName("examSchedule");

            for (int i = 0; i < scheduleNodes.getLength(); i++) {
                Node scheduleNode = scheduleNodes.item(i);
                if (scheduleNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element scheduleElement = (Element) scheduleNode;
                    ExamSchedule schedule = parseScheduleFromElement(scheduleElement);
                    schedules.add(schedule);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return schedules;
    }

    private ExamSchedule parseScheduleFromElement(Element scheduleElement) {
        ExamSchedule schedule = new ExamSchedule();

        schedule.setId(Integer.parseInt(getElementText(scheduleElement, "id")));
        schedule.setExamTypeId(Integer.parseInt(getElementText(scheduleElement, "examTypeId")));
        schedule.setExaminerId(Integer.parseInt(getElementText(scheduleElement, "examinerId")));
        schedule.setExamDate(LocalDate.parse(getElementText(scheduleElement, "examDate"), DATE_FORMATTER));
        schedule.setTimeSlot(TimeSlot.valueOf(getElementText(scheduleElement, "timeSlot")));
        schedule.setMaxCandidates(Integer.parseInt(getElementText(scheduleElement, "maxCandidates")));
        schedule.setRegisteredCandidates(Integer.parseInt(getElementText(scheduleElement, "registeredCandidates")));
        schedule.setStatus(ScheduleStatus.valueOf(getElementText(scheduleElement, "status")));
        schedule.setLocation(getElementText(scheduleElement, "location"));

        String createdDateStr = getElementText(scheduleElement, "createdDate");
        if (createdDateStr != null && !createdDateStr.isEmpty()) {
            schedule.setCreatedDate(LocalDateTime.parse(createdDateStr, DATETIME_FORMATTER));
        }

        return schedule;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    public Optional<ExamSchedule> findById(int id) {
        return getAllSchedules().stream().filter(schedule -> schedule.getId() == id).findFirst();
    }

    public List<ExamSchedule> findByExaminerId(int examinerId) {
        return getAllSchedules().stream()
                .filter(schedule -> schedule.getExaminerId() == examinerId)
                .toList();
    }

    public List<ExamSchedule> findByDate(LocalDate date) {
        return getAllSchedules().stream()
                .filter(schedule -> schedule.getExamDate().equals(date))
                .toList();
    }

    public List<ExamSchedule> findByStatus(ScheduleStatus status) {
        return getAllSchedules().stream()
                .filter(schedule -> schedule.getStatus() == status)
                .toList();
    }

    public List<ExamSchedule> findUpcomingSchedules() {
        LocalDate today = LocalDate.now();
        return getAllSchedules().stream()
                .filter(schedule -> schedule.getExamDate().isAfter(today) || schedule.getExamDate().equals(today))
                .filter(schedule -> schedule.getStatus() == ScheduleStatus.OPEN)
                .toList();
    }

    public void addSchedule(ExamSchedule schedule) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            int newId = getAllSchedules().stream().mapToInt(ExamSchedule::getId).max().orElse(0) + 1;
            schedule.setId(newId);
            schedule.setCreatedDate(LocalDateTime.now());

            addScheduleToDocument(doc, root, schedule);
            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSchedule(ExamSchedule schedule) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList scheduleNodes = root.getElementsByTagName("examSchedule");
            for (int i = 0; i < scheduleNodes.getLength(); i++) {
                Node scheduleNode = scheduleNodes.item(i);
                if (scheduleNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element scheduleElement = (Element) scheduleNode;
                    if (Integer.parseInt(getElementText(scheduleElement, "id")) == schedule.getId()) {
                        // Remove the old schedule element
                        root.removeChild(scheduleElement);
                        // Add the updated schedule element
                        addScheduleToDocument(doc, root, schedule);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSchedule(int scheduleId) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList scheduleNodes = root.getElementsByTagName("examSchedule");
            for (int i = 0; i < scheduleNodes.getLength(); i++) {
                Node scheduleNode = scheduleNodes.item(i);
                if (scheduleNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element scheduleElement = (Element) scheduleNode;
                    if (Integer.parseInt(getElementText(scheduleElement, "id")) == scheduleId) {
                        root.removeChild(scheduleElement);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isScheduleFull(int scheduleId) {
        Optional<ExamSchedule> schedule = findById(scheduleId);
        return schedule.map(s -> s.getRegisteredCandidates() >= s.getMaxCandidates()).orElse(false);
    }

    public void incrementRegisteredCandidates(int scheduleId) {
        Optional<ExamSchedule> scheduleOpt = findById(scheduleId);
        if (scheduleOpt.isPresent()) {
            ExamSchedule schedule = scheduleOpt.get();
            schedule.setRegisteredCandidates(schedule.getRegisteredCandidates() + 1);
            updateSchedule(schedule);
        }
    }

    public void decrementRegisteredCandidates(int scheduleId) {
        Optional<ExamSchedule> scheduleOpt = findById(scheduleId);
        if (scheduleOpt.isPresent()) {
            ExamSchedule schedule = scheduleOpt.get();
            if (schedule.getRegisteredCandidates() > 0) {
                schedule.setRegisteredCandidates(schedule.getRegisteredCandidates() - 1);
                updateSchedule(schedule);
            }
        }
    }
}