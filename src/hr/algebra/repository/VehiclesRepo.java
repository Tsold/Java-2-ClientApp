package hr.algebra.repository;

import hr.algebra.model.Advertisement;
import hr.algebra.model.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VehiclesRepo implements Serializable {

    private static final long serialVersionUID = 4L;

    private ISqlRepo repo;


    private VehiclesRepo() {
    }

    private static final VehiclesRepo INSTANCE = new VehiclesRepo();

    private ObservableList<Vehicle> vehicles;

    private ObservableList<Vehicle> fVehicles;

    public static VehiclesRepo getInstance() {
        return INSTANCE;
    }

    public ObservableList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehs) {

        vehicles = FXCollections.observableArrayList(vehs);

        List<Vehicle> free = new ArrayList<>();
        vehs.forEach(v -> {
            if (v.getAvailable() == 1){
                free.add(v);
            }
        });
        fVehicles = FXCollections.observableArrayList(free);

    }

    public ObservableList<Vehicle> getFreeVehicles() {
        return fVehicles;
    }



    public Vehicle getSingleVehicle(int id){

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getIDVehicle() == id){
                return vehicle;
            }


        }
        return new Vehicle();
    }



    public void addVehicle(Vehicle veh){
        vehicles.add(veh);

        if (veh.getAvailable() == 1){
            fVehicles.add(veh);
        }
    }



    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(new ArrayList<>(vehicles));
    }


    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException, Exception {
        repo = RepoFactory.getRepository();
        List<Vehicle> serializedVeh = (List<Vehicle>) ois.readObject();

        try {
            INSTANCE.vehicles.clear();
            INSTANCE.fVehicles.clear();
            repo.deleteAllVehicles();
            serializedVeh.forEach(v -> {
                try {
                    repo.addVehicle(v);
                    INSTANCE.addVehicle(v);
                } catch (Exception ex) {
                    Logger.getLogger(AdvertisementRepo.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(AdvertisementRepo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    private Object readResolve() {

        return INSTANCE;
    }

}
