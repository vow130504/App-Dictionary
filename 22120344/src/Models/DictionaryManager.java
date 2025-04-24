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
    private static final String ANH_VIET_XML = "./22120344/Data/Anh_Viet.xml";
    private static final String VIET_ANH_XML = "./22120344/Data/Viet_Anh.xml";
    private static final String SEARCH_LOG_FILE = "./22120344/Data/SearchLog.txt";

    private Document anhVietDocument;
    private Document vietAnhDocument;
    private boolean isEnglishToVietnamese;

    public DictionaryManager() {
        try {
            anhVietDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(ANH_VIET_XML));
            vietAnhDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(VIET_ANH_XML));
            anhVietDocument.getDocumentElement().normalize();
            vietAnhDocument.getDocumentElement().normalize();
            isEnglishToVietnamese = true; // Default to English to Vietnamese
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String searchWord(String searchTerm) {
        if (isEnglishToVietnamese) {
            return searchInDocument(anhVietDocument, searchTerm);
        } else {
            return searchInDocument(vietAnhDocument, searchTerm);
        }
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

    public void setEnglishToVietnamese() {
        isEnglishToVietnamese = true;
    }

    public void setVietnameseToEnglish() {
        isEnglishToVietnamese = false;
    }

    public boolean isEnglishToVietnamese() {
        return isEnglishToVietnamese;
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
            e.printStackTrace();
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
            if (!wordFound) {
                String dictType = isEnglishToVietnamese ? "English to Vietnamese" : "Vietnamese to English";
                JOptionPane.showMessageDialog(null, "Word '" + word + "' does not exist in the " + dictType + " dictionary.",
                        "Word Not Found", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDictionaryToXML(Document document, boolean isEnglishToVietnamese) {
        try {
            String filename = isEnglishToVietnamese ? ANH_VIET_XML : VIET_ANH_XML;
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean wordExists(String word) {
        if (isEnglishToVietnamese) {
            return wordExistsInDocument(anhVietDocument, word);
        } else {
            return wordExistsInDocument(vietAnhDocument, word);
        }
    }

    public boolean wordExistsInSpecificDictionary(String word, boolean isEnglishToVietnamese) {
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
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SEARCH_LOG_FILE, true), StandardCharsets.UTF_8))) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String logEntry = dateFormat.format(date) + " " + searchTerm + "\n";
            bw.write(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getWordFrequency(Date startDate, Date endDate) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(SEARCH_LOG_FILE), StandardCharsets.UTF_8))) {
            String logEntry;
            while ((logEntry = br.readLine()) != null) {
                String[] parts = logEntry.split(" ", 3);
                if (parts.length >= 3) {
                    try {
                        Date logDate = dateFormat.parse(parts[0] + " " + parts[1]);
                        if (!logDate.before(startDate) && !logDate.after(endDate)) {
                            String word = parts[2].trim();
                            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordFrequency;
    }
}