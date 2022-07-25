package hr.algebra.utils;

import hr.algebra.model.Advertisement;
import hr.algebra.model.Vehicle;
import javafx.beans.property.*;
import javafx.collections.ObservableList;


import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class DOMUtils {
    private static final String FILENAME_ADS = "advertisements.xml";
    private static final String FILENAME_VEHICLES = "vehicles.xml";

    public static void saveAdvertisements(ObservableList<Advertisement> ads) throws ParserConfigurationException {

        try {
            Document document = createDocument("advertisements");
            ads.forEach(a -> document.getDocumentElement().appendChild(createAdElement(a, document)));
            saveDocument(document, FILENAME_ADS);
        } catch (TransformerException e) {
            Logger.getLogger(DOMUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    public static void saveVehicles(ObservableList<Vehicle> vehs) throws ParserConfigurationException {

        try {
            Document document = createDocument("vehicles");
            vehs.forEach(a -> document.getDocumentElement().appendChild(createVehicleElement(a, document)));
            saveDocument(document, FILENAME_VEHICLES);
        } catch (TransformerException e) {
            Logger.getLogger(DOMUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static Document createDocument(String root) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        return domImplementation.createDocument(null, root, null);
    }


    private static Node createAdElement(Advertisement a, Document document) {
        Element element = document.createElement("advertisement");
        element.appendChild(createElement(document, "idAdvertisement", String.valueOf(a.getIdAdvertisement())));
        element.appendChild(createElement(document, "vehicleID",String.valueOf(a.getVehicleID())));
        element.appendChild(createElement(document, "title", a.getTitle()));
        element.appendChild(createElement(document, "price", String.valueOf(a.getPrice())));
        element.appendChild(createElement(document, "date", String.valueOf(a.getDateAndTime())));
        element.appendChild(createElement(document, "isTaken", String.valueOf(a.getTaken())));
        element.appendChild(createElement(document, "userID", String.valueOf(a.getUserID())));
        return element;
    }

    private static Node createVehicleElement(Vehicle v, Document document) {
        Element element = document.createElement("vehicle");
        element.appendChild(createElement(document, "IDVehicle", String.valueOf(v.getIDVehicle())));
        element.appendChild(createElement(document, "VehicleTypeID",String.valueOf(v.getVehicleTypeID())));
        element.appendChild(createElement(document, "Maker", v.getMaker()));
        element.appendChild(createElement(document, "Model", v.getModel()));
        element.appendChild(createElement(document, "ProductionYear", v.getProductionYear()));
        element.appendChild(createElement(document, "InitialKm", String.valueOf(v.getInitialKm())));
        element.appendChild(createElement(document, "Available", String.valueOf(v.getAvailable())));
        return element;
    }

    private static void saveDocument(Document document, String fileName) throws TransformerConfigurationException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        //transformer.transform(new DOMSource(document), new StreamResult(System.out));
        transformer.transform(new DOMSource(document), new StreamResult(new File(fileName)));
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    public static ObservableList<Advertisement> loadAds() {
        ObservableList<Advertisement> ads = FXCollections.observableArrayList();
        try {
            Document document = createDocument(new File(FILENAME_ADS));
            NodeList nodes = document.getElementsByTagName("advertisement");
            for (int i = 0; i < nodes.getLength(); i++) {
                // dangerous class cast exception
                ads.add(processAdsNode((Element) nodes.item(i)));
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Logger.getLogger(DOMUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return ads;
    }


    public static ObservableList<Vehicle> loadVehicles() {
        ObservableList<Vehicle> vehs = FXCollections.observableArrayList();
        try {
            Document document = createDocument(new File(FILENAME_VEHICLES));
            NodeList nodes = document.getElementsByTagName("vehicle");
            for (int i = 0; i < nodes.getLength(); i++) {
                // dangerous class cast exception
                vehs.add(processVehiclesNode((Element) nodes.item(i)));
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Logger.getLogger(DOMUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return vehs;
    }

    private static Document createDocument(File file) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        return document;
    }


    private static Advertisement processAdsNode(Element element) {
        return new Advertisement(

                Integer.valueOf(element.getElementsByTagName("idAdvertisement").item(0).getTextContent()),
                Integer.valueOf(element.getElementsByTagName("vehicleID").item(0).getTextContent()),
                element.getElementsByTagName("title").item(0).getTextContent(),
                Double.valueOf(element.getElementsByTagName("price").item(0).getTextContent()),
                Date.valueOf(element.getElementsByTagName("date").item(0).getTextContent()),
                Boolean.valueOf(element.getElementsByTagName("isTaken").item(0).getTextContent()),
                Integer.valueOf(element.getElementsByTagName("userID").item(0).getTextContent()));

    }


    private static Vehicle processVehiclesNode(Element element) {
        return new Vehicle(

                Integer.valueOf(element.getElementsByTagName("IDVehicle").item(0).getTextContent()),
                Integer.valueOf(element.getElementsByTagName("VehicleTypeID").item(0).getTextContent()),
                element.getElementsByTagName("Maker").item(0).getTextContent(),
                element.getElementsByTagName("Model").item(0).getTextContent(),
                element.getElementsByTagName("ProductionYear").item(0).getTextContent(),
                Integer.valueOf(element.getElementsByTagName("InitialKm").item(0).getTextContent()),
                Integer.valueOf(element.getElementsByTagName("Available").item(0).getTextContent()));


    }
}
