package hr.algebra.utils;

import hr.algebra.model.Advertisement;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import hr.algebra.model.Vehicle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXUtils {
    private static final String FILENAME_ADS = "advertisements.xml";
    private static final String FILENAME_VEHICLES = "vehicles.xml";

    public static ObservableList<Advertisement> loadAds() {
        ObservableList<Advertisement> ads = FXCollections.observableArrayList();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse(new File(FILENAME_ADS), new AdsHandler(ads));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(SAXUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ads;

    }

    public static ObservableList<Vehicle> loadVehicles() {
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse(new File(FILENAME_VEHICLES), new VehiclesHandler(vehicles));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(SAXUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vehicles;

    }

    private static class AdsHandler extends DefaultHandler {

        private final ObservableList<Advertisement> ads;

        public AdsHandler(ObservableList<Advertisement> ads) {
            this.ads = ads;
        }

        private Optional<AdTag> tag;
        private Advertisement ad;


        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String value = new String(ch, start, length);
            if (tag.isPresent()) {
                switch (tag.get()) {

                    case ID:
                        ad.setIdAdvertisement(Integer.parseInt(value));
                        break;
                    case IDVehicle:
                        ad.setVehicleID(Integer.parseInt(value));
                        break;
                        case TITLE:
                        ad.setTitle(value);
                        break;
                    case PRICE:
                        ad.setPrice(Double.parseDouble(value));
                        break;
                    case DATE:
                        ad.setDateAndTime(Date.valueOf(value));
                        break;
                    case ISTAKEN:
                        ad.setTaken(Boolean.parseBoolean(value));
                        break;
                    case USERID:
                        ad.setUserID(Integer.parseInt(value));
                        break;
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            tag = Optional.empty();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            tag = AdTag.from(qName);
            if (tag.isPresent()) {
                switch (tag.get()) {
                    case AD:

                        ad = new Advertisement();
                        ads.add(ad);
                        break;
                }
            }
        }

        @Override
        public void startDocument() throws SAXException {
            tag = Optional.empty();
        }

    }


    private static class VehiclesHandler extends DefaultHandler{

        private final ObservableList<Vehicle> vehs;

        public VehiclesHandler(ObservableList<Vehicle> vehs) {
            this.vehs = vehs;
        }

        private Optional<VehicleTag> tag;
        private Vehicle vehicle;



        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String value = new String(ch, start, length);
            if (tag.isPresent()) {
                switch (tag.get()) {

                    case IDVEHICLE:
                        vehicle.setIDVehicle(Integer.parseInt(value));
                        break;
                    case VEHICLETYPEID:
                        vehicle.setVehicleTypeID(Integer.parseInt(value));
                        break;
                    case MAKER:
                        vehicle.setMaker(value);
                        break;
                    case MODEL:
                        vehicle.setModel(value);
                        break;
                    case PRODUCTIONYEAR:
                        vehicle.setProductionYear(value);
                        break;
                    case INITIALKM:
                        vehicle.setInitialKm(Integer.parseInt(value));
                        break;
                    case AVAILABLE:
                        vehicle.setAvailable(Integer.parseInt(value));
                        break;
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            tag = Optional.empty();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            tag = VehicleTag.from(qName);
            if (tag.isPresent()) {
                switch (tag.get()) {
                    case VEHICLE:

                        vehicle = new Vehicle();
                        vehs.add(vehicle);
                        break;
                }
            }
        }

        @Override
        public void startDocument() throws SAXException {
            tag = Optional.empty();
        }

    }


    private enum AdTag {
        AD("advertisement"),
        ID("idAdvertisement"),
        IDVehicle("vehicleID"),
        TITLE("title"),
        PRICE("price"),
        DATE("date"),
        ISTAKEN("isTaken"),
        USERID("userID");

        private final String name;

        private AdTag(String name) {
            this.name = name;
        }

        private static Optional<AdTag> from(String name) {
            for (AdTag value : values()) {
                if (value.name.equals(name)) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }

    }

    private enum VehicleTag {
        VEHICLE("vehicle"),
        IDVEHICLE("IDVehicle"),
        VEHICLETYPEID("VehicleTypeID"),
        MAKER("Maker"),
        MODEL("Model"),
        PRODUCTIONYEAR("ProductionYear"),
        INITIALKM("InitialKm"),
        AVAILABLE("Available");

        private final String name;

        private VehicleTag(String name) {
            this.name = name;
        }

        private static Optional<VehicleTag> from(String name) {
            for (VehicleTag value : values()) {
                if (value.name.equals(name)) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }

    }

}
