package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.enums.UserStatus;
import com.pocitaco.oopsh.models.User;
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
 * Data Access Object for User management
 */
public class UserDAO {
    private static final String XML_FILE = "data/users.xml";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UserDAO() {
        initializeDataFile();
    }

    private void initializeDataFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            createDefaultUsers();
        }
    }

    private void createDefaultUsers() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("users");
            doc.appendChild(root);

            // Create default admin user
            User admin = new User("admin", "admin123", UserRole.ADMIN, "Quản trị viên", "admin@satheach.com");
            admin.setId(1);
            addUserToDocument(doc, root, admin);

            // Create default examiner user
            User examiner = new User("giamthi001", "gt123456", UserRole.EXAMINER, "Nguyễn Văn A",
                    "giamthi001@satheach.com");
            examiner.setId(2);
            examiner.setEmployeeId("GT001");
            examiner.setExperience(5);
            addUserToDocument(doc, root, examiner);

            // Create default candidate user
            User candidate = new User("thisinh001", "ts123456", UserRole.CANDIDATE, "Trần Thị B",
                    "thisinh001@email.com");
            candidate.setId(3);
            candidate.setCccd("123456789012");
            candidate.setBirthday(LocalDate.of(1995, 1, 15));
            candidate.setPhone("0901234567");
            candidate.setAddress("Hà Nội");
            addUserToDocument(doc, root, candidate);

            // Save to file
            saveDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addUserToDocument(Document doc, Element root, User user) {
        Element userElement = doc.createElement("user");

        userElement.appendChild(createElement(doc, "id", String.valueOf(user.getId())));
        userElement.appendChild(createElement(doc, "username", user.getUsername()));
        userElement.appendChild(createElement(doc, "password", user.getPassword()));
        userElement.appendChild(createElement(doc, "role", user.getRole().name()));
        userElement.appendChild(createElement(doc, "fullName", user.getFullName()));
        userElement.appendChild(createElement(doc, "email", user.getEmail()));
        userElement.appendChild(createElement(doc, "createdDate", user.getCreatedDate().format(DATE_FORMATTER)));
        userElement.appendChild(createElement(doc, "status", user.getStatus().name()));

        if (user.getEmployeeId() != null) {
            userElement.appendChild(createElement(doc, "employeeId", user.getEmployeeId()));
            userElement.appendChild(createElement(doc, "experience", String.valueOf(user.getExperience())));
        }

        if (user.getCccd() != null) {
            userElement.appendChild(createElement(doc, "cccd", user.getCccd()));
            userElement.appendChild(createElement(doc, "birthday", user.getBirthday().format(DATE_FORMATTER)));
            userElement.appendChild(createElement(doc, "phone", user.getPhone()));
            userElement.appendChild(createElement(doc, "address", user.getAddress()));
        }

        root.appendChild(userElement);
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

    public User authenticateUser(String username, String password, UserRole role) {
        List<User> users = getAllUsers();

        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password) &&
                    user.getRole() == role &&
                    user.getStatus() == UserStatus.ACTIVE) {
                return user;
            }
        }

        return null;
    }

    /**
     * Authenticate user without specifying role - system auto-detects role
     */
    public User authenticateUser(String username, String password) {
        List<User> users = getAllUsers();

        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password) &&
                    user.getStatus() == UserStatus.ACTIVE) {
                return user; // Return user with their assigned role
            }
        }

        return null;
    }

    public Optional<User> findByUsername(String username) {
        return getAllUsers().stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    public Optional<User> findById(int id) {
        return getAllUsers().stream().filter(user -> user.getId() == id).findFirst();
    }

    public Optional<User> get(int id) {
        return findById(id);
    }

    public User update(User user) {
        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                return user; // Return as-is if file doesn't exist
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList userNodes = doc.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element userElement = (Element) userNodes.item(i);
                if (Integer.parseInt(getElementText(userElement, "id")) == user.getId()) {
                    // Update the existing user element with new data
                    updateUserElement(doc, userElement, user);
                    
                    // Save the document
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(xmlFile);
                    transformer.transform(source, result);
                    
                    return user;
                }
            }
            throw new RuntimeException("User with id " + user.getId() + " not found");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating user", e);
        }
    }

    private void updateUserElement(Document doc, Element userElement, User user) {
        // Clear existing content and rebuild
        while (userElement.hasChildNodes()) {
            userElement.removeChild(userElement.getFirstChild());
        }
        
        userElement.appendChild(createElement(doc, "id", String.valueOf(user.getId())));
        userElement.appendChild(createElement(doc, "username", user.getUsername()));
        userElement.appendChild(createElement(doc, "password", user.getPassword()));
        userElement.appendChild(createElement(doc, "fullName", user.getFullName()));
        userElement.appendChild(createElement(doc, "email", user.getEmail()));
        userElement.appendChild(createElement(doc, "phone", user.getPhone()));
        userElement.appendChild(createElement(doc, "birthday", user.getBirthday() != null ? user.getBirthday().format(DATE_FORMATTER) : ""));
        userElement.appendChild(createElement(doc, "address", user.getAddress()));
        if (user.getEmployeeId() != null) {
            userElement.appendChild(createElement(doc, "employeeId", user.getEmployeeId()));
        }
        userElement.appendChild(createElement(doc, "role", user.getRole().name()));
        userElement.appendChild(createElement(doc, "status", user.getStatus().name()));
    }

    public List<User> getAllUsers() {
        return getAll();
    }

    public List<User> getAll() {
        List<User> users = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                return users;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList userNodes = doc.getElementsByTagName("user");

            for (int i = 0; i < userNodes.getLength(); i++) {
                Node userNode = userNodes.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;
                    User user = parseUserFromElement(userElement);
                    users.add(user);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    private User parseUserFromElement(Element userElement) {
        User user = new User();

        user.setId(Integer.parseInt(getElementText(userElement, "id")));
        user.setUsername(getElementText(userElement, "username"));
        user.setPassword(getElementText(userElement, "password"));
        user.setRole(UserRole.valueOf(getElementText(userElement, "role")));
        user.setFullName(getElementText(userElement, "fullName"));
        user.setEmail(getElementText(userElement, "email"));
        user.setCreatedDate(LocalDate.parse(getElementText(userElement, "createdDate"), DATE_FORMATTER));
        user.setStatus(UserStatus.valueOf(getElementText(userElement, "status")));

        // Optional fields for examiner
        String employeeId = getElementText(userElement, "employeeId");
        if (employeeId != null && !employeeId.isEmpty()) {
            user.setEmployeeId(employeeId);
            user.setExperience(Integer.parseInt(getElementText(userElement, "experience")));
        }

        // Optional fields for candidate
        String cccd = getElementText(userElement, "cccd");
        if (cccd != null && !cccd.isEmpty()) {
            user.setCccd(cccd);
            user.setBirthday(LocalDate.parse(getElementText(userElement, "birthday"), DATE_FORMATTER));
            user.setPhone(getElementText(userElement, "phone"));
            user.setAddress(getElementText(userElement, "address"));
        }

        return user;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    public void addUser(User user) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            int newId = getAllUsers().stream().mapToInt(User::getId).max().orElse(0) + 1;
            user.setId(newId);

            addUserToDocument(doc, root, user);
            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList userNodes = root.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Node userNode = userNodes.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;
                    if (Integer.parseInt(getElementText(userElement, "id")) == user.getId()) {
                        // Remove the old user element
                        root.removeChild(userElement);
                        // Add the updated user element
                        addUserToDocument(doc, root, user);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(XML_FILE));
            Element root = doc.getDocumentElement();

            NodeList userNodes = root.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Node userNode = userNodes.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;
                    if (Integer.parseInt(getElementText(userElement, "id")) == userId) {
                        root.removeChild(userElement);
                        break;
                    }
                }
            }

            saveDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<User> getUsersByRole(UserRole role) {
        return getAllUsers().stream()
                .filter(user -> user.getRole() == role)
                .toList();
    }
}
