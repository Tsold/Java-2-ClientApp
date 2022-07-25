package hr.algebra.model;

import javafx.beans.property.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Vehicle implements Serializable {

    private static final long serialVersionUID = 2L;

    private IntegerProperty IDVehicle;
    private IntegerProperty VehicleTypeID;
    private StringProperty Maker;
    private StringProperty Model;
    private StringProperty ProductionYear;
    private IntegerProperty InitialKm;
    private IntegerProperty Available;


    public Vehicle(){
            this.IDVehicle = new SimpleIntegerProperty();
            this.VehicleTypeID = new SimpleIntegerProperty();
            this.Maker = new SimpleStringProperty();
            this.Model = new SimpleStringProperty();
            this.ProductionYear = new SimpleStringProperty();
            this.InitialKm = new SimpleIntegerProperty();
            this.Available =new SimpleIntegerProperty();
    }


    public Vehicle(int IDVehicle, int vehicleTypeID, String maker, String model, String productionYear, int inicialKm) {
        this.IDVehicle = new SimpleIntegerProperty(IDVehicle);
        this.VehicleTypeID = new SimpleIntegerProperty(vehicleTypeID);
        this.Maker = new SimpleStringProperty(maker);
        this.Model = new SimpleStringProperty(model);
        this.ProductionYear = new SimpleStringProperty(productionYear);
        this.InitialKm = new SimpleIntegerProperty(inicialKm);
        this.Available =new SimpleIntegerProperty();
    }

    public Vehicle(int IDVehicle, int vehicleTypeID, String maker, String model, String productionYear, int initialKm, int avaible) {
        this.IDVehicle = new SimpleIntegerProperty(IDVehicle);
        this.VehicleTypeID = new SimpleIntegerProperty(vehicleTypeID);
        this.Maker = new SimpleStringProperty(maker);
        this.Model = new SimpleStringProperty(model);
        this.ProductionYear = new SimpleStringProperty(productionYear);
        this.InitialKm = new SimpleIntegerProperty(initialKm);
        this.Available =new SimpleIntegerProperty(avaible);
    }

    public Vehicle(int IDVehicle, String maker, String model) {
        this.IDVehicle = new SimpleIntegerProperty(IDVehicle);
        this.Maker = new SimpleStringProperty(maker);
        this.Model = new SimpleStringProperty(model);
    }

    public Vehicle(int vehicleTypeID, String maker, String model, String productionYear, int initialKm, int avaible) {
        this.VehicleTypeID = new SimpleIntegerProperty(vehicleTypeID);
        this.Maker = new SimpleStringProperty(maker);
        this.Model = new SimpleStringProperty(model);
        this.ProductionYear = new SimpleStringProperty(productionYear);
        this.InitialKm = new SimpleIntegerProperty(initialKm);
        this.Available =new SimpleIntegerProperty(avaible);
    }




    public int getIDVehicle() {
        return IDVehicle.get();
    }

    public void setIDVehicle(int IDVehicle) {
        this.IDVehicle.set(IDVehicle);
    }

    public int getVehicleTypeID() {
        return VehicleTypeID.get();
    }

    public void setVehicleTypeID(int vehicleTypeID) {
        this.VehicleTypeID.set(vehicleTypeID);
    }

    public String getMaker() {
        return Maker.get();
    }

    public void setMaker(String maker) {
        this.Maker.set(maker);
    }

    public String getModel() {
        return Model.get();
    }

    public void setModel(String model) {
        this.Model.set(model);
    }

    public String getProductionYear() {
        return ProductionYear.get();
    }

    public void setProductionYear(String productionYear) {
        this.ProductionYear.set(productionYear);
    }

    public int getInitialKm() {
        return InitialKm.get();
    }

    public void setInitialKm(int initialKm) {
        this.InitialKm.set(initialKm);
    }

    public int getAvailable() {
        return Available.get();
    }

    public void setAvailable(int available) {
        this.Available.set(available);
    }

    @Override
    public String toString() {
        return Maker.get() +" " + Model.get();
    }


    public String toFullInfo() {
        return "Maker: "+Maker.get() +"\n" +"Model: " +Model.get()+ "\n" + "Production Year: "+ ProductionYear.get() + "\n" + "Current Km: "+ getInitialKm();
    }

    public String toSendInfo() {
        return "Maker: "+Maker.get()+ "\n" + "Model Year: "+ ProductionYear.get() + "\n" + "Around: "+ getInitialKm() +" Km";
    }



    // {Type}Property are not Serialazible - we must do it manually
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(IDVehicle.get());
        oos.writeInt(VehicleTypeID.get());
        oos.writeUTF(Maker.get());
        oos.writeUTF(Model.get());
        oos.writeUTF(ProductionYear.get());
        oos.writeInt(InitialKm.get());
        oos.writeInt(Available.get());
    }

    // we must ensure invariants - validation must be provided, as in constructor
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        Integer serializedIdVehicle = ois.readInt();
        Integer serializedVehicleType = ois.readInt();
        String serializedMaker = ois.readUTF();
        String serializedModel = ois.readUTF();
        String serializedProductionYear = ois.readUTF();
        Integer serializedKm = ois.readInt();
        Integer serializedAvailable = ois.readInt();



        IDVehicle = new SimpleIntegerProperty(serializedIdVehicle);
        VehicleTypeID = new SimpleIntegerProperty(serializedVehicleType);
        Maker = new SimpleStringProperty(serializedMaker);
        Model = new SimpleStringProperty(serializedModel);
        ProductionYear = new SimpleStringProperty(serializedProductionYear);
        InitialKm = new SimpleIntegerProperty(serializedKm);
        Available =new SimpleIntegerProperty(serializedAvailable);

    }
}
