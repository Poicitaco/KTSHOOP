package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.models.Payment;
import com.pocitaco.oopsh.enums.PaymentStatus;
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
 * Data Access Object for Payment management
 */
public class PaymentDAO {
    private static final String XML_FILE = "data/payments.xml";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PaymentDAO() {
        initializeDataFile();
    }

    private void initializeDataFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            createDefaultPayments();
        }
    }

    private void createDefaultPayments() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("payments");
            doc.appendChild(root);

            // Create default payments
            Payment payment1 = new Payment();
            payment1.setId(1);
            payment1.setUserId(3); // candidate
            payment1.setExamTypeId(1); // theory exam
            payment1.setAmount(500000.0);
            payment1.setPaymentDate(LocalDateTime.now().minusDays(10));
            payment1.setStatus(PaymentStatus.PAID);
            addPaymentToDocument(doc, root, payment1);

            Payment payment2 = new Payment();
            payment2.setId(2);
            payment2.setUserId(3); // candidate
            payment2.setExamTypeId(2); // practical exam
            payment2.setAmount(800000.0);
            payment2.setPaymentDate(LocalDateTime.now().minusDays(5));
            payment2.setStatus(PaymentStatus.PAID);
            addPaymentToDocument(doc, root, payment2);

            // Save to file
            saveDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPaymentToDocument(Document doc, Element root, Payment payment) {
        Element paymentElement = doc.createElement("payment");

        paymentElement.appendChild(createElement(doc, "id", String.valueOf(payment.getId())));
        paymentElement.appendChild(createElement(doc, "userId", String.valueOf(payment.getUserId())));
        paymentElement.appendChild(createElement(doc, "examTypeId", String.valueOf(payment.getExamTypeId())));
        paymentElement.appendChild(createElement(doc, "amount", String.valueOf(payment.getAmount())));
        paymentElement
                .appendChild(createElement(doc, "paymentDate", payment.getPaymentDate().format(DATETIME_FORMATTER)));
        paymentElement.appendChild(createElement(doc, "status", payment.getStatus().name()));

        root.appendChild(paymentElement);
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

    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                return payments;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList paymentNodes = doc.getElementsByTagName("payment");

            for (int i = 0; i < paymentNodes.getLength(); i++) {
                Node paymentNode = paymentNodes.item(i);
                if (paymentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element paymentElement = (Element) paymentNode;
                    Payment payment = parsePaymentFromElement(paymentElement);
                    payments.add(payment);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payments;
    }

    private Payment parsePaymentFromElement(Element paymentElement) {
        Payment payment = new Payment();

        payment.setId(Integer.parseInt(getElementText(paymentElement, "id")));
        payment.setUserId(Integer.parseInt(getElementText(paymentElement, "userId")));
        payment.setExamTypeId(Integer.parseInt(getElementText(paymentElement, "examTypeId")));
        payment.setAmount(Double.parseDouble(getElementText(paymentElement, "amount")));
        payment.setPaymentDate(LocalDateTime.parse(getElementText(paymentElement, "paymentDate"), DATETIME_FORMATTER));
        payment.setStatus(PaymentStatus.valueOf(getElementText(paymentElement, "status")));

        return payment;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    public Optional<Payment> get(int id) {
        return findAll().stream().filter(payment -> payment.getId() == id).findFirst();
    }

    public void addPayment(Payment payment) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            int newId = findAll().stream().mapToInt(Payment::getId).max().orElse(0) + 1;
            payment.setId(newId);

            addPaymentToDocument(doc, root, payment);
            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePayment(Payment payment) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList paymentNodes = root.getElementsByTagName("payment");
            for (int i = 0; i < paymentNodes.getLength(); i++) {
                Node paymentNode = paymentNodes.item(i);
                if (paymentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element paymentElement = (Element) paymentNode;
                    if (Integer.parseInt(getElementText(paymentElement, "id")) == payment.getId()) {
                        // Remove the old payment element
                        root.removeChild(paymentElement);
                        // Add the updated payment element
                        addPaymentToDocument(doc, root, payment);
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

            NodeList paymentNodes = root.getElementsByTagName("payment");
            for (int i = 0; i < paymentNodes.getLength(); i++) {
                Node paymentNode = paymentNodes.item(i);
                if (paymentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element paymentElement = (Element) paymentNode;
                    if (Integer.parseInt(getElementText(paymentElement, "id")) == id) {
                        root.removeChild(paymentElement);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Payment save(Payment payment) {
        if (payment.getId() == 0) {
            addPayment(payment);
        } else {
            updatePayment(payment);
        }
        return payment;
    }
}