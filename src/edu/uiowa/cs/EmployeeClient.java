//Sam Christensen
//EmployeeClient.java

package edu.uiowa.cs;

import java.sql.*;
import java.util.*;

public class EmployeeClient extends Main{
 
    private Connection cnx;
    
    //Constructor
    public EmployeeClient(){  
        String host = "";
        String uName = "";
        String passwd = "";
        try {
            // Establish a connection to your database
            cnx = DriverManager.getConnection(host, uName, passwd);
            cnx.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            cnx.setAutoCommit(false);     
        }catch (SQLException e) {
            System.out.println("failed to connect to database " + e);
            System.exit(1);
            try{
                cnx.close();
            }catch (Exception e2) {}
        }
    }   
    //Close
    public void close() {
        try {
            cnx.close();
        } catch (Exception e) {}
    }
    
    //---Class specific methods go below---//
    
    public int record_bike_service(){
        try{
            Scanner reader = new Scanner(System.in);
            System.out.println("What is your Employee ID?");
            int EmpID = reader.nextInt();
            System.out.println("What bike are you performing service on?");
            int bikeID = reader.nextInt();
            System.out.println("What is the conditon of the bike?");
            Scanner reader2 = new Scanner(System.in);
            String description = reader2.nextLine(); 
            String values = bikeID+ "," +EmpID+ ",0,0";
            //Adds bike to Services as under repair.
            PreparedStatement ps = cnx.prepareStatement("INSERT INTO Services VALUES(" + values + ")");
            ps.executeUpdate();
            //cnx.commit();
            //Update bike description in Bikes table.
            PreparedStatement ps1 = cnx.prepareStatement("UPDATE Bikes SET bikeCondition = ? WHERE BikeID = ?");
            ps1.setString(1, description);
            ps1.setInt(2, bikeID);
            ps1.executeUpdate();
            cnx.commit();
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }
        return -1; //(should never get here)
    }
    
    public int view_bikes_in_service(){
        try{
            Statement s = cnx.createStatement();
            ResultSet rs = s.executeQuery("SELECT Bikes.BikeID, Bikes.model, Bikes.bikeCondition FROM Bikes, Services WHERE Bikes.BikeID = Services.BikeID AND Services.isRental = 0 GROUP BY Bikes.BikeID");
            if(!rs.next()){
                System.out.println("There are currently no bikes in service!");
                cnx.rollback();
                return -1; //Failure!
            }else{
                System.out.println("The Bikes currently in service are...");
                printResultSet(rs);
                System.out.println("test");
                return 1; //Success!
            }
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }
        return -1; //(should never get here) 
    }
    
    public int view_rented_bikes(){
        try{
            Statement s = cnx.createStatement();
            ResultSet rs = s.executeQuery("SELECT BikeID, PersonID FROM Services WHERE isRental = 1");
            if(!rs.next()){
                System.out.println("There are currently no bikes rented out!");
                cnx.rollback();
                return -1; //Failure!
            }else{
                System.out.println("The currently rented bikes are...");
                printResultSet(rs);
                return 1; //Success!  
            }
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }
        return -1; //(should never get here)
    }
}