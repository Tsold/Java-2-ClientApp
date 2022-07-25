package hr.algebra.model;

import javafx.beans.property.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class Advertisement implements Serializable {

    private static final long serialVersionUID = 1L;

    private IntegerProperty idAdvertisement;
    private IntegerProperty vehicleID;
    private StringProperty title;
    private DoubleProperty price;
    private ObjectProperty<Date> dateAndTime;
    private BooleanProperty isTaken;
    private IntegerProperty userID;


    public Advertisement(){
        this.idAdvertisement = new SimpleIntegerProperty();
        this.vehicleID = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.dateAndTime = new SimpleObjectProperty<>();
        this.isTaken = new SimpleBooleanProperty();
        this.userID =new SimpleIntegerProperty();
    }

    public Advertisement(int idAdvertisement,int vehicleId, String title, double price, Date dateAndTime, Boolean isTaken, int userID) {
        this.idAdvertisement = new SimpleIntegerProperty(idAdvertisement);
        this.vehicleID = new SimpleIntegerProperty(vehicleId);
        this.title = new SimpleStringProperty(title);
        this.price = new SimpleDoubleProperty(price);
        this.dateAndTime = new SimpleObjectProperty<>(dateAndTime);
        this.isTaken = new SimpleBooleanProperty(isTaken);
        this.userID =new SimpleIntegerProperty(userID);
    }

    public Advertisement(String title, int vehicleId, double price, Date dateAndTime, Boolean isTaken, int userID) {
        this.title = new SimpleStringProperty(title);
        this.vehicleID = new SimpleIntegerProperty(vehicleId);
        this.price = new SimpleDoubleProperty(price);
        this.dateAndTime = new SimpleObjectProperty<>(dateAndTime);
        this.isTaken = new SimpleBooleanProperty(isTaken);
        this.userID =new SimpleIntegerProperty(userID);
    }




    public int getIdAdvertisement() {
        return idAdvertisement.get();
    }

    public void setIdAdvertisement(int idAdvertisement) {
        this.idAdvertisement.set(idAdvertisement);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public int getVehicleID() {
        return vehicleID.get();
    }

    public void setVehicleID(int vehicleId) {
        this.vehicleID.set(vehicleId);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public Date getDateAndTime() {
        return dateAndTime.get();
    }

    public void setDateAndTime(Date dateAndTime) {
        this.dateAndTime.set(dateAndTime);
    }

    public Boolean getTaken() {
        return isTaken.get();
    }

    public void setTaken(Boolean taken) {
        isTaken.set(taken);
    }

    public int getUserID() {
        return userID.get();
    }

    public void setUserID(int userID) {
        this.userID.set(userID);
    }


    // {Type}Property are not Serialazible - we must do it manually
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(idAdvertisement.get());
        oos.writeInt(vehicleID.get());
        oos.writeUTF(title.get());
        oos.writeDouble(price.get());
        oos.writeObject(dateAndTime.get());
        oos.writeBoolean(isTaken.get());
        oos.writeInt(userID.get());
    }

    // we must ensure invariants - validation must be provided, as in constructor
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        Integer serializedIdAd = ois.readInt();
        Integer serializedVehicleId = ois.readInt();
        String serializedTitle = ois.readUTF();
        Double serializedPrice = ois.readDouble();
        Date serializedDateAndTime = (Date)ois.readObject();
        Boolean serializedIsTaken = ois.readBoolean();
        Integer serializedUserID = ois.readInt();



        idAdvertisement = new SimpleIntegerProperty(serializedIdAd);
        vehicleID = new SimpleIntegerProperty(serializedVehicleId);
        title = new SimpleStringProperty(serializedTitle);
        price = new SimpleDoubleProperty(serializedPrice);
        dateAndTime = new SimpleObjectProperty<>(serializedDateAndTime);
        isTaken = new SimpleBooleanProperty(serializedIsTaken);
        userID =new SimpleIntegerProperty(serializedUserID);

    }

}
