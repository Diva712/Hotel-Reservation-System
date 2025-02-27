package com.hotelmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
	
	private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
	private static final String username = "root";
	private static final String password = "root";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver Loaded Successfully !!");
			
		}
		catch(ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		
		try {
			
			Connection connection = DriverManager.getConnection(url,username,password);
			System.out.println("Connection established !!");
			while(true) {
				System.out.println();
				System.out.println("HOTEL MANAGEMENT SYSTEM");
				Scanner scanner = new Scanner(System.in);
				System.out.println("1. Reserve a room");
				System.out.println("2. View Reservations");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update Reservations");
				System.out.println("5. Delete Reservations");
				System.out.println("0. Exit");
				System.out.println("Choose an option:  ");
				int choise =  scanner.nextInt();
				switch(choise) {
				    case 1:
				    	reserveRoom(connection , scanner);
				    	break;
				    case 2:
				    	viewReservations(connection);
				    	break;
				    case 3:
				    	getRoomNumber(connection , scanner);
				    	break;
				    case 4:
				    	updateReservations(connection , scanner);
				    	break;
				    case 5:
				    	deleteReservations(connection , scanner);
				    	break;
				    case 0:
				    	exit();
				    	scanner.close();
				    	return;
				    default:
				    	System.out.println("Invalid choice. Try Again...");
				    	
				}
				
			}
			
		}
		catch(SQLException e) {
			System.err.println(e.getMessage());
		}catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	//reserve Room in to the Hotel
	private static void reserveRoom(Connection connection , Scanner scanner) {
		
		try {
			System.out.println("Enter guest name : ");
			String guestName = scanner.next();
			scanner.nextLine();
			
			System.out.println("Enter room number : ");
			int roomNumber = scanner.nextInt();
			scanner.nextLine();
			
			System.out.println("Enter guest contact number : ");
			String contactNumber = scanner.next();
			scanner.nextLine();
			
			
			  String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
	                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";
			  
			  try(Statement statement = connection.createStatement()){
				   int affectedRows = statement.executeUpdate(sql);

	                if (affectedRows > 0) {
	                    System.out.println("Reservation successful!");
	                } else {
	                    System.out.println("Reservation failed.");
	                }
			  }
			  catch(SQLException e) {
				  e.printStackTrace();
			  }	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void viewReservations(Connection connection ) {
		String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_data FROM reservations;";
		
		try(Statement statement = connection.createStatement();
				 ResultSet resultSet = statement.executeQuery(sql)){
			
			  System.out.println("Current Reservations:");
	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
	            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

			while(resultSet.next()) {
				int reservationId = resultSet.getInt("reservation_id");
				 String guestName = resultSet.getString("guest_name");
	                int roomNumber = resultSet.getInt("room_number");
	                String contactNumber = resultSet.getString("contact_number");
	                String reservationDate = resultSet.getTimestamp("reservation_data").toString();
	                
	                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
	                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
			}
			
			  System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void getRoomNumber(Connection connection , Scanner scanner) {
		try {
			
			 System.out.print("Enter reservation ID: ");
	            int reservationId = scanner.nextInt();
	            System.out.print("Enter guest name: ");
	            String guestName = scanner.next();
	            
	           String sql = "SELECT room_number FROM reservations " + "WHERE reservation_id = " + reservationId + " AND guest_name = '" + guestName + "'";
	           
	           
	           try(Statement statement = connection.createStatement();
	        		   ResultSet resultSet = statement.executeQuery(sql)){
	        	   if (resultSet.next()) {
	                    int roomNumber = resultSet.getInt("room_number");
	                    System.out.println("Room number for Reservation ID " + reservationId +
	                            " and Guest " + guestName + " is: " + roomNumber);
	                } else {
	                    System.out.println("Reservation not found for the given ID and guest name.");
	                }
	        	   
	           }
	           catch(SQLException e) {
	        	   e.printStackTrace();
	           }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void updateReservations(Connection connection  , Scanner scanner) {

		
		try {
			
			  System.out.print("Enter reservation ID to update: ");
	            int reservationId = scanner.nextInt();
	            scanner.nextLine(); 
	            
	            if(!reservationExists(connection , reservationId)) {
	            	System.out.println("Reservation not found for the given ID.");
	            	return;
	            }
	            
	            System.out.print("Enter new guest name: ");
	            String newGuestName = scanner.nextLine();
	            System.out.print("Enter new room number: ");
	            int newRoomNumber = scanner.nextInt();
	            System.out.print("Enter new contact number: ");
	            String newContactNumber = scanner.next();
	            
	            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
	                    "room_number = " + newRoomNumber + ", " +
	                    "contact_number = '" + newContactNumber + "' " +
	                    "WHERE reservation_id = " + reservationId;
	            
	            try(Statement statement = connection.createStatement()){
	            	int affectedRows = statement.executeUpdate(sql);
	            	
	            	if (affectedRows > 0) {
	                    System.out.println("Reservation updated successfully!");
	                } else {
	                    System.out.println("Reservation update failed.");
	                }
	            	
	            }
	            catch(SQLException e) {
	            	e.printStackTrace();
	            }
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private static void deleteReservations(Connection connection , Scanner scanner) {
		try {
			 System.out.print("Enter reservation ID to delete: ");
	            int reservationId = scanner.nextInt();

	            if (!reservationExists(connection, reservationId)) {
	                System.out.println("Reservation not found for the given ID.");
	                return;
	            }

	            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;
	            
	            try(Statement statement = connection.createStatement()){
	            	
	            	int affectedRows = statement.executeUpdate(sql);
	            	
	            	 if (affectedRows > 0) {
	                     System.out.println("Reservation deleted successfully!");
	                 } else {
	                     System.out.println("Reservation deletion failed.");
	                 }
	            	
	            }
	            catch(SQLException e) {
	            	e.printStackTrace();
	            }
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	private static boolean reservationExists(Connection connection, int reservationId) {

		 try {
	            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

	            try (Statement statement = connection.createStatement();
	                 ResultSet resultSet = statement.executeQuery(sql)) {

	                return resultSet.next(); 
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	}
	
	 public static void exit() throws InterruptedException {
	        System.out.print("Exiting System");
	        int i = 5;
	        while(i!=0){
	            System.out.print(".");
	            Thread.sleep(500);
	            i--;
	        }
	        System.out.println();
	        System.out.println("ThankYou For Using Hotel Reservation System!!!");
	    }
}
