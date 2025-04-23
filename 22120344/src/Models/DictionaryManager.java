package Models;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.*;
import java.util.*;

public class DictionaryManager {
    private static final String DATA_DIR = "Data/";
    private static final String ANH_VIET_XML = DATA_DIR + "Anh_Viet.xml";
    private static final String VIET_ANH_XML = DATA_DIR + "Viet_Anh.xml";
    private static final String SEARCH_LOG_FILE = "SearchLog.txt";

    private Document anhVietDocument;
    private Document vietAnhDocument;
    private boolean isEnglishToVietnamese;

    public DictionaryManager() {
        try {
            // Create Data directory if it doesn't exist
            new File(DATA_DIR).mkdirs();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Load or create XML files
            anhVietDocument = loadOrCreateXML(ANH_VIET_XML);
            vietAnhDocument = loadOrCreateXML(VIET_ANH_XML);

            anhVietDocument.getDocumentElement().normalize();
            vietAnhDocument.getDocumentElement().normalize();
            isEnglishToVietnamese = true;
        } catch (Exception e) {
            showErrorDialog("Error initializing dictionary", e);
        }
    }

    private Document loadOrCreateXML(String filePath) throws Exception {
        File xmlFile = new File(filePath);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        if (!xmlFile.exists()) {
            // Create new XML file with basic structure if it doesn't exist
            Document doc = builder.newDocument();
            Element root = doc.createElement("dictionary");
            doc.appendChild(root);
            saveXMLFile(doc, filePath);
            return doc;
        }

        return builder.parse(xmlFile);
    }

    private void saveXMLFile(Document document, String filePath) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);

        // Ensure parent directories exist
        new File(filePath).getParentFile().mkdirs();

        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    private void showErrorDialog(String title, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
                title + ": " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public String searchWord(String searchTerm) {
        Document document = isEnglishToVietnamese ? anhVietDocument : vietAnhDocument;
        return searchInDocument(document, searchTerm);
    }

    private String searchInDocument(Document document, String searchTerm) {
        NodeList recordList = document.getElementsByTagName("record");
        for (int i = 0; i < recordList.getLength(); i++) {
            Node recordNode = recordList.item(i);
            if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                Element recordElement = (Element) recordNode;
                String word = recordElement.getElementsByTagName("word").item(0).getTextContent().trim().toLowerCase();
                if (word.equals(searchTerm)) {
                    return recordElement.getElementsByTagName("meaning").item(0).getTextContent().trim();
                }
            }
        }
        return null;
    }

    public void setEnglishToVietnamese(boolean isEnglishToVietnamese) {
        this.isEnglishToVietnamese = isEnglishToVietnamese;
    }

    public void addWord(String word, String meaning, boolean isEnglishToVietnamese) {
        try {
            Document document = isEnglishToVietnamese ? anhVietDocument : vietAnhDocument;
            Element root = document.getDocumentElement();

            Element newRecord = document.createElement("record");
            Element newWord = document.createElement("word");
            newWord.appendChild(document.createTextNode(word));
            Element newMeaning = document.createElement("meaning");
            newMeaning.appendChild(document.createTextNode(meaning));

            newRecord.appendChild(newWord);
            newRecord.appendChild(newMeaning);
            root.appendChild(newRecord);

            saveDictionaryToXML(document, isEnglishToVietnamese);
        } catch (Exception e) {
            showErrorDialog("Error adding word", e);
        }
    }

    public void deleteWord(String word, boolean isEnglishToVietnamese) {
        try {
            Document document = isEnglishToVietnamese ? anhVietDocument : vietAnhDocument;
            NodeList recordList = document.getElementsByTagName("record");
            boolean wordFound = false;
            for (int i = 0; i < recordList.getLength(); i++) {
                Node recordNode = recordList.item(i);
                if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element recordElement = (Element) recordNode;
                    String wordInRecord = recordElement.getElementsByTagName("word").item(0).getTextContent().trim().toLowerCase();
                    if (wordInRecord.equals(word)) {
                        recordNode.getParentNode().removeChild(recordNode);
                        saveDictionaryToXML(document, isEnglishToVietnamese);
                        wordFound = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            showErrorDialog("Error deleting word", e);
        }
    }

    private void saveDictionaryToXML(Document document, boolean isEnglishToVietnamese) {
        try {
            String filename = isEnglishToVietnamese ? ANH_VIET_XML : VIET_ANH_XML;
            saveXMLFile(document, filename);
        } catch (Exception e) {
            showErrorDialog("Error saving dictionary", e);
        }
    }

    public boolean wordExists(String word) {
        Document document = isEnglishToVietnamese ? anhVietDocument : vietAnhDocument;
        return wordExistsInDocument(document, word);
    }

    private boolean wordExistsInDocument(Document document, String word) {
        NodeList recordList = document.getElementsByTagName("record");
        for (int i = 0; i < recordList.getLength(); i++) {
            Node recordNode = recordList.item(i);
            if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                Element recordElement = (Element) recordNode;
                String wordInRecord = recordElement.getElementsByTagName("word").item(0).getTextContent().trim().toLowerCase();
                if (wordInRecord.equals(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void logSearch(String searchTerm) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(SEARCH_LOG_FILE, true), StandardCharsets.UTF_8))) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String logEntry = dateFormat.format(new Date()) + " " + searchTerm + "\n";
            bw.write(logEntry);
        } catch (IOException e) {
            showErrorDialog("Error logging search", e);
        }
    }

    public Map<String, Integer> getWordFrequency(Date startDate, Date endDate) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(SEARCH_LOG_FILE), StandardCharsets.UTF_8))) {
            String logEntry;
            while ((logEntry = br.readLine()) != null) {
                String[] parts = logEntry.split(" ", 3);
                Date logDate = dateFormat.parse(parts[0] + " " + parts[1]);
                if (!logDate.before(startDate) && !logDate.after(endDate)) {
                    String word = parts[2].trim();
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                }
            }
        } catch (IOException | ParseException e) {
            showErrorDialog("Error getting word frequency", e);
        }
        return wordFrequency;
    }
}