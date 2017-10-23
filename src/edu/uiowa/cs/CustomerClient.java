//Sam Christensen
//CustomerClient.java

package edu.uiowa.cs;

import java.sql.*;
import java.util.*;

public class CustomerClient extends Main{

    private Connection cnx;

    //Constructor
    public CustomerClient(){  
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
    
    public int reserve_bike(){
        try{   
            int CustID = getCustomerID();
            Scanner reader = new Scanner(System.in);
            System.out.println("What date do you want to reserve a bike for? ");
            String targetDate = reader.nextLine();
            targetDate = "'" + targetDate + "'";
            Statement s = cnx.createStatement(); 
            ResultSet rs = s.executeQuery("SELECT Bikes.BikeID, Bikes.model, Bikes.bikeCondition FROM Bikes WHERE Bikes.BikeID NOT IN(SELECT Bikes.BikeID FROM Bikes, Reservations WHERE Bikes.BikeID = Reservations.BikeID AND " +targetDate+ " = Reservations.date GROUP BY Bikes.BikeID)");
            if(!rs.next()){
                System.out.println("No avalible bikes for the selected day!");
                cnx.rollback();
                return -1; //Failure!
            }else{
                System.out.println("The Bike avalible on " +targetDate+ " are...");
                printResultSet(rs);
                System.out.println("Which bike would you like to rent? (BikeID)");
                int choice = reader.nextInt();   
                System.out.println("INSERT INTO Reservations VALUES(" +choice+ ", " +CustID+ ", " +targetDate+")");
                PreparedStatement ps = cnx.prepareStatement("INSERT INTO Reservations VALUES(" +choice+ ", " +CustID+ ", " +targetDate+")");
                ps.executeUpdate();
                cnx.commit();
                return 1; //Success!    
            }
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }
        return -1; //(Should never get here)
    }
    
    public int checkout_reserved_bike(){
        try{
            int CustID = getCustomerID();
            Statement s = cnx.createStatement();
            ResultSet rs = s.executeQuery("SELECT BikeID, date FROM Reservations WHERE Reservations.PersonID = " + CustID);
            if(!rs.next()){//No Reservation today
                System.out.println("You do not have a reservation for todays date!");
                cnx.rollback();
                return -1; //Failure!
            }else{//There is a reservation today
                //Checking that the date matches.
                String dateFromDB = rs.getString("date");
                String date = getDate();
                if(date.equals(dateFromDB)){
                    System.out.println("You have a reservation for todays date!");
                    int bikeID = rs.getInt("BikeID");
                    System.out.println("The bike you reserved has BikeID " + bikeID + "!");
                    //Reading in the time the bike is rented for.
                    Scanner reader = new Scanner(System.in);
                    System.out.println("How many hours do you want the bike for?");
                    int rentalTime = reader.nextInt();
                    //Constructing SQL command to add bike to services list.
                    String values = "VALUES(" +bikeID+ "," +CustID+ ",1," +rentalTime+ ")";
                    PreparedStatement ps = cnx.prepareStatement("INSERT INTO Services " + values);
                    //Commiting to database.
                    ps.executeUpdate();
                    cnx.commit();
                    return 1; //Success!       
                }else{
                    System.out.println("You do not have a reservation for todays date!");
                    cnx.rollback();
                    return -1; //Failure!
                }
            }     
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }
    return -1; //(should never get here)
    }
    
    public int rental_return(){
        try{
            int CustID = getCustomerID();   
            Statement s = cnx.createStatement();
            ResultSet rs = s.executeQuery("SELECT BikeID, PersonID, rentalTime FROM Services WHERE PersonID=" + CustID);
            //printResultSet(rs);
            if(!rs.next()){//There is not a checked out bike to the user.
                System.out.println("You do not have a bike checked out!");
                cnx.rollback();
                return -1; //Failure!
            }else{//There is a checked out bike to the user.
                int rentalTime = rs.getInt("rentalTime");
                int bikeID = rs.getInt("BikeID");
                System.out.println("You are returning the rented bike with BikeID " + bikeID);
                Statement s1 = cnx.createStatement();
                ResultSet rs1 = s1.executeQuery("SELECT rentalCost FROM Bikes WHERE BikeID=" + bikeID);
                //printResultSet(rs1);
                if(!rs1.next()){//Bike with that bikeID doesnt exist. 
                    System.out.println("A bike with that BikeID does not exist!");
                    cnx.rollback();
                    return -1; //Failure!
                }else{//Bike with bikeID exists.
                    float rentalCost = rs1.getInt("rentalCost");
                    //Reading in the time the bike is rented for.
                    Scanner reader = new Scanner(System.in);
                    System.out.println("How many hours have you had the bike for? ");
                    int rentalTimeExpired = reader.nextInt();
                    //Calculating rental price.
                    float rentalPrice = rentalCost * rentalTimeExpired;
                    if(rentalTime < rentalTimeExpired){ 
                        System.out.println("Your rental is late! you owe $" + rentalPrice + "!");
                    }else if(rentalTime > rentalTimeExpired){
                        System.out.println("Your rental is early! you owe $" + rentalPrice + "!");
                    }else{//rentalTime = rentalTimeExpired
                        System.out.println("Your rental is right on time! you owe $" + rentalPrice + "!");
                    }
                    //Constructing SQL command to add bike to services list.
                    PreparedStatement ps = cnx.prepareStatement("DELETE FROM Services WHERE BikeID=" + bikeID);
                    //Commiting to database.
                    ps.executeUpdate();
                    cnx.commit();
                    return 1; //Success!
                }
            }
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }  
        return -1; //(should never get here)
    }
    
    public int cancel_reservation(){
        try{
            int CustID = getCustomerID();  
            Statement s = cnx.createStatement();
            ResultSet rs = s.executeQuery("SELECT BikeID, date FROM Reservations WHERE PersonID=" + CustID);
            if(!rs.next()){//
                System.out.println("You dont have any reservations!");
                cnx.rollback();
                return -1; //Failure!
            }else{
                String dateInDB = rs.getString("date");
                String date = getDate();
                if(dateInDB.equals(date)){//Same date, cant cancel reservation.
                    System.out.println("You can not cancel your reservation on the day of the reservation!");
                    cnx.rollback();
                    return -1; //Failure!
                }else{
                    Statement s1 = cnx.createStatement();
                    ResultSet rs1 = s1.executeQuery("SELECT BikeID, date FROM Reservations WHERE PersonID=" + CustID);
                    System.out.println("------------");
                    System.out.println("BikeID  Date");
                    printResultSet(rs1);
                    System.out.println("------------");
                    Scanner reader = new Scanner(System.in);
                    System.out.println("What is the BikeID of the reservation you want to cancel?");
                    int bikeID = reader.nextInt();
                    PreparedStatement ps = cnx.prepareStatement("DELETE FROM Reservations WHERE BikeID=" + bikeID);
                    System.out.println("Cancelled reservation on " + dateInDB + " for bike with BikeID of " + bikeID + "!");
                    //Commiting to database.
                    ps.executeUpdate();
                    cnx.commit();
                    return 1; //Success!
                }
            }
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }
    return -1; //(should never get here)
    }     
    
    public int view_avalible_bikes(){
        try{
            Statement s = cnx.createStatement();
            ResultSet rs = s.executeQuery("SELECT BikeID, rentalCost FROM Bikes WHERE BikeID NOT IN (SELECT BikeID FROM Services)");
            if(!rs.next()){
                System.out.println("There are no bikes avalible!");
                cnx.rollback();
                return -1; //Failure!
            }else{
                System.out.println("The currently avalible bikes and prices are...");
                printResultSet(rs);  
                return 1;
            }
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }
        return -1; //(should never get here)
    }
    
    public int list_future_reservations(){
        try{
            int CustID = getCustomerID();
            Statement s = cnx.createStatement();
            ResultSet rs = s.executeQuery("SELECT date, BikeID FROM Reservations WHERE PersonID=" + CustID);
            if(!rs.next()){
                System.out.println("You do not have any future reservations!");
                cnx.rollback();
                return -1; //Failure!
            }else{
                System.out.println("Reservations for Customer " + CustID + "...");
                printResultSet(rs);
                return 1;
            }
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }
        return -1; //(should never get here)
    }
}