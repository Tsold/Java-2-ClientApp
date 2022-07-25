package hr.algebra.repository;

import hr.algebra.model.Advertisement;
import hr.algebra.model.User;
import hr.algebra.model.Vehicle;

import java.util.List;

public interface ISqlRepo {

     //Ads
     User validateUser(String userName,String passWord) throws Exception;
     List<Advertisement> getAllAds() throws Exception;
     void addAdvertisement(Advertisement ad) throws Exception;
     void deleteAd(int ID) throws Exception;
     void updateAd(int ID,int UserID) throws Exception;
     void updateNotTakenAd(int ID) throws Exception;
     void deleteAllAds() throws Exception;


     //Vehicles
     List<Vehicle> getVehicles() throws Exception;
     Vehicle getSingleVehicle(int ID) throws Exception;
     void updateVehicleAvailable(int ID) throws Exception;
     void updateVehicleNotAvailable(int ID) throws Exception;
     void deleteVehicle(int ID) throws Exception;
     void addVehicle(Vehicle vehicle) throws Exception;
     void deleteAllVehicles() throws Exception;
}
