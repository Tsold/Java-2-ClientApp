package hr.algebra.sql;

import hr.algebra.model.Advertisement;
import hr.algebra.model.User;
import hr.algebra.model.Vehicle;
import hr.algebra.repository.ISqlRepo;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class SqlRepo implements ISqlRepo {

    private static final String VALIDATE_USER = "{call ValidateUser(?, ?)} " ;

    private static final String ADD_AD = "{call AddAdvertisement(?, ?, ?, ?, ?, ?)} " ;
    private static final String GET_ADS = "{ call GetAdvertisements }";
    private static final String DELETE_AD = "{ call DeleteAd(?) }";
    private static final String UPDATE_AD = "{ call UpdateIsTakenAd(?,?) }";
    private static final String UPDATE_NOT_TAKEN_AD = "{ call UpdateIsNotTakenAd(?) }";
    private static final String DELETE_ALL_ADS = "{ call DeleteAllAds }";

    private static final String GET_AVAILABLE_VEHICLES = "{ call GetVehicles }";
    private static final String GET_VEHICLE_INFO = "{ call GetVehicleInfo(?) }";
    private static final String ADD_VEHICLE = "{call AddVehicle(?, ?, ?, ?, ?, ?)} " ;
    private static final String DELETE_VEHICLE = "{ call DeleteVehicle(?) }";
    private static final String UPDATE_VEHICLE_TAKEN = "{ call VehicleTaken(?) }";
    private static final String UPDATE_VEHICLE_AVAILABLE = "{ call VehicleAvaible(?) }";
    private static final String DELETE_ALL_VEHICLES = "{ call DeleteAllVehicles2 }";



    @Override
    public User validateUser(String userName, String passWord) throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();

        try (Connection con = ds.getConnection();
             PreparedStatement stmt = con.prepareCall(VALIDATE_USER)) {

            stmt.setString(1, userName);
            stmt.setString(2, passWord);

            try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        return new User(
                                rs.getInt(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getInt(4)
                        );
                    }
            }
        }
        return new User();
    }


    //ADS
    @Override
    public List<Advertisement> getAllAds() throws Exception  {
        List<Advertisement> ads = new ArrayList<>();

        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection();
             PreparedStatement stmt = con.prepareCall(GET_ADS)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ads.add(new Advertisement(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString (3),
                        rs.getDouble(4),
                        rs.getDate(5),
                        rs.getBoolean(6),
                        rs.getInt(7)
                ));
            }
        }

        return ads;
    }



    int boolToInt(Boolean b) {
        return b.compareTo(false);
    }

    public void addAdvertisement(Advertisement ad) throws Exception{
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(ADD_AD);
            stmt.setString(1,ad.getTitle());
            stmt.setInt(2,ad.getVehicleID());
            stmt.setDouble(3,ad.getPrice());
            stmt.setString(4,ad.getDateAndTime().toString());
            stmt.setInt(5,boolToInt(ad.getTaken()));
            stmt.setInt(6,ad.getUserID());

            stmt.execute();

        }

    }

    @Override
    public void deleteAd(int ID) throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(DELETE_AD);
            stmt.setInt(1,ID);

            stmt.execute();

        }
    }

    @Override
    public void updateAd(int ID,int UserID) throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(UPDATE_AD);
            stmt.setInt(1,ID);
            stmt.setInt(2,UserID);

            stmt.execute();

        }
    }

    @Override
    public synchronized void updateNotTakenAd(int ID) throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(UPDATE_NOT_TAKEN_AD);
            stmt.setInt(1,ID);

            stmt.execute();

        }
    }

    @Override
    public void deleteAllAds() throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(DELETE_ALL_ADS);

            stmt.execute();

        }
    }

    //VEHICLE
    @Override
    public List<Vehicle> getVehicles() throws Exception  {
        List<Vehicle> vehicles = new ArrayList<>();

        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection();
             PreparedStatement stmt = con.prepareCall(GET_AVAILABLE_VEHICLES)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehicles.add(new Vehicle(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6),
                        rs.getInt(7)

                ));
            }
        }

        return vehicles;
    }

    @Override
    public Vehicle getSingleVehicle(int ID) throws Exception  {
        DataSource ds = DataSourceSingleton.getInstance();
        Vehicle vehicle = new Vehicle();

        try (Connection con = ds.getConnection();
             PreparedStatement stmt = con.prepareCall(GET_VEHICLE_INFO)) {

            stmt.setInt(1, ID);

            ResultSet rs = stmt.executeQuery();
            rs.next();

            vehicle.setIDVehicle(rs.getInt("IDVehicle"));
            vehicle.setVehicleTypeID(rs.getInt("VehicleTypeID"));
            vehicle.setMaker(rs.getString("Maker"));
            vehicle.setModel(rs.getString("Model"));
            vehicle.setProductionYear(rs.getString("ProductionYear"));
            vehicle.setInitialKm(rs.getInt("InicialKm"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return vehicle;
    }


    @Override
    public void updateVehicleAvailable(int ID) throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(UPDATE_VEHICLE_AVAILABLE);
            stmt.setInt(1,ID);

            stmt.execute();

        }
    }

    @Override
    public void updateVehicleNotAvailable(int ID) throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(UPDATE_VEHICLE_TAKEN);
            stmt.setInt(1,ID);

            stmt.execute();

        }
    }


    @Override
    public void deleteVehicle(int ID) throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(DELETE_VEHICLE);
            stmt.setInt(1,ID);

            stmt.execute();

        }
    }

    @Override
    public void addVehicle(Vehicle vehicle) throws Exception{
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(ADD_VEHICLE);
            stmt.setInt(1,vehicle.getVehicleTypeID());
            stmt.setString(2,vehicle.getMaker());
            stmt.setString(3,vehicle.getModel());
            stmt.setString(4,vehicle.getProductionYear());
            stmt.setInt(5,vehicle.getInitialKm());
            stmt.setInt(6,vehicle.getAvailable());


            stmt.execute();

        }

    }

    @Override
    public void deleteAllVehicles() throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection()) {

            CallableStatement stmt = con.prepareCall(DELETE_ALL_VEHICLES);

            stmt.execute();

        }
    }


}
