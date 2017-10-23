//Sam Christensen
//Main.java

package edu.uiowa.cs;

import java.sql.*;
import java.util.*;

public class Main{
    
    private static int CustomerID;
    private static String date = "";
    static Scanner reader = new Scanner(System.in);
    
    public static void main(String[] args){
        System.out.println("Main Start!");  
        //Allow them to set date?
        
        System.out.println("Employee or Customer?");
        String input = reader.next();
        
        //Employee Demo zone
        if(input.equals("Employee") || input.equals("employee") || input.equals("e")){
            System.out.println("Starting client as an employee!");
            EmployeeClient client = new EmployeeClient();
            
            //----------------------------------------Scenario 1
            
            //View all bikes in service.
            client.view_rented_bikes();
            
            //Sign in as an employee and report service on some bike.
            client.record_bike_service();
            
            //View rented Bikes.
            //client.view_rented_bikes();
            
            System.out.println("End of Scenario 1");
            //----------------------------------------
            
            //Client closes.
            client.close();
        }
        //Customer Demo zone
        else if(input.equals("Customer") || input.equals("customer") || input.equals("c")){
            System.out.println("Starting client as a customer!");
            CustomerClient client = new CustomerClient();
            
            //----------------------------------------Scenario 2
            //Takes in current date.
            client.setDate();
            
            //Customer A signs in and checks out a reserved bike.
            client.setCustomerID();
            client.checkout_reserved_bike();
            
            //Customer B signs in and checks out a reserved bike.
            client.setCustomerID();
            client.checkout_reserved_bike();
            
            //Customer B returns his bike.
            //Dont need to sign in here.
            client.rental_return();
            
            //Customer A returns the bike.
            client.setCustomerID();
            client.rental_return();
            
            System.out.println("End of Scenario 2");
            //----------------------------------------
            
            //----------------------------------------Scenario 3
            
            //Set the date.
            client.setDate();
            
            //Customer A Reserves a bike.
            client.getCustomerID();
            client.reserve_bike();
            
            //Customer B Reserves a bike.
            client.getCustomerID();
            client.reserve_bike();
            
            //Customer A Attempts to cancel his reservation.
            client.getCustomerID();
            client.reserve_bike();
            System.out.println("End of Scenario 3");
            //----------------------------------------
            
            //Client closes.
            client.close();
        }
        else{
            System.out.println("Invalid input!");
            System.exit(1);
        }
    }
    
    //---Utility methods below---//
    
    //printResultSet
    public void printResultSet(ResultSet rs){
        try{
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnNumber = rsmd.getColumnCount();
        
        while(rs.next()){
            for(int i = 1; i <= columnNumber; i++){
                System.out.print(rs.getString(i) + " ");
            }
            System.out.println();
        }
        }catch(SQLException ex){
            System.err.println("Exception " + ex + " thrown!");
        }
    }
    
    //getCustomerID()
    public int getCustomerID(){
        return this.CustomerID;
    } 
    //setCustomerID()
    public void setCustomerID(){
        Scanner r = new Scanner(System.in);
        System.out.println("What is your ID number?");
        CustomerID = r.nextInt();
    }
    //getDate()
    public String getDate(){
        return this.date;
    }
    //setDate()
    public void setDate(){
        Scanner r = new Scanner(System.in);
        System.out.println("What is the current date?");
        date = r.nextLine();
    } 
}
