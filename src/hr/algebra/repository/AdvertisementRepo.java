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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvertisementRepo implements Serializable {

    private static final long serialVersionUID = 3L;

    private ISqlRepo repo;


    private AdvertisementRepo() {
    }

    private static final AdvertisementRepo INSTANCE = new AdvertisementRepo();

    public static AdvertisementRepo getInstance() {
        return INSTANCE;
    }

    private ObservableList<Advertisement> ads;

    private ObservableList<Advertisement> tads;

    private ObservableList<Advertisement> fads;


    public ObservableList<Advertisement> getAds() {
        return ads;
    }

    public ObservableList<Advertisement> getTakenAds(int id) {
        return tads;
    }

    public ObservableList<Advertisement> getFreeAds() {
        return fads;
    }

    public void setAds(List<Advertisement> ad,int id) {

        ads = FXCollections.observableArrayList(ad);
        List<Advertisement> free = new ArrayList<>();
        List<Advertisement> taken = new ArrayList<>();
        ads.forEach(v -> {
            if (v.getTaken() == false){
                free.add(v);
            }
        });
        fads = FXCollections.observableArrayList(free);

        ads.forEach(v -> {
            if (v.getTaken() == true && v.getUserID() == id){
                taken.add(v);
            }
        });
        tads = FXCollections.observableArrayList(taken);


    }



    public void addAd(Advertisement ad){
        ads.add(ad);

        if (ad.getTaken() == false){
            fads.add(ad);
        }
        if (ad.getTaken() == true){
            tads.add(ad);
        }
    }




    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(new ArrayList<>(ads));
        if (!((VehiclesRepo.getInstance().getVehicles())== null))
        {
            oos.writeObject(new ArrayList<>(VehiclesRepo.getInstance().getVehicles()));
        }

    }


    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException, Exception {
        repo = RepoFactory.getRepository();
        List<Advertisement> serializedAds = (List<Advertisement>) ois.readObject();
        List<Vehicle> serializedVeh = new ArrayList<>();



        try {
            serializedVeh = (List<Vehicle>) ois.readObject();
        } catch (Exception ex) {
      //
        }

        try {
            INSTANCE.ads.clear();
            INSTANCE.fads.clear();
            INSTANCE.tads.clear();
            repo.deleteAllAds();
            if (!serializedVeh.isEmpty())
            {
                if (!((VehiclesRepo.getInstance().getVehicles())== null)) {
                    VehiclesRepo.getInstance().getVehicles().clear();
                    VehiclesRepo.getInstance().getFreeVehicles().clear();
                    repo.deleteAllVehicles();
                    serializedVeh.forEach(v -> {
                        try {
                            repo.addVehicle(v);
                            VehiclesRepo.getInstance().addVehicle(v);
                        } catch (Exception ex) {
                               Logger.getLogger(AdvertisementRepo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
            serializedAds.forEach(a -> {
                try {
                    repo.addAdvertisement(a);
                    INSTANCE.addAd(a);
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




    public void swapAds(ObservableList<Advertisement> ads) {
        try {
            repo = RepoFactory.getRepository();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ads.clear();
        this.fads.clear();
        this.tads.clear();
        try {
            repo.deleteAllAds();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ads.forEach(a -> {
            addAd(a);
            try {
                repo.addAdvertisement(a);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void swapAds(ObservableList<Advertisement> ads,ObservableList<Vehicle> vehs) {
        try {
            repo = RepoFactory.getRepository();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ads.clear();
        this.fads.clear();
        this.tads.clear();
        VehiclesRepo.getInstance().getVehicles().clear();
        VehiclesRepo.getInstance().getFreeVehicles().clear();
        try {
            repo.deleteAllAds();
            repo.deleteAllVehicles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        vehs.forEach(v -> {
            VehiclesRepo.getInstance().addVehicle(v);
        });

        VehiclesRepo.getInstance().getFreeVehicles().forEach(veh ->{
            try {
                repo.addVehicle(veh);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ads.forEach(a -> {
            addAd(a);
            try {
                repo.addAdvertisement(a);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
