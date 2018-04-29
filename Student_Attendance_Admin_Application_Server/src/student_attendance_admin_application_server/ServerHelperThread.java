/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student_attendance_admin_application_server;
import java.net.*;
import java.io.*;
import java.sql.*;
/**
 *
 * @author admin
 */
public class ServerHelperThread implements Runnable {
    Socket sockClient;
    ObjectInputStream inputFromClient;
    ObjectOutputStream outputToClient;
    public ServerHelperThread(Socket clientSocket)
    {
        sockClient=clientSocket;
    }
    @Override
    public void run() {
        try
        {
            
        outputToClient=new ObjectOutputStream(sockClient.getOutputStream());
        //outputToClient.flush();
        inputFromClient=new ObjectInputStream(sockClient.getInputStream());
        System.out.println("Server helper thread started successfully");
        String strOperation=inputFromClient.readUTF();
        System.out.println("operation to be performed : "+strOperation);
        
        if(strOperation.equals("LOGIN_REQUEST"))
        {
            System.out.println("Login request");
            String adminname=inputFromClient.readUTF();
            String adminpassword=inputFromClient.readUTF();
            System.out.println(adminname+" , "+adminpassword);
            
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="SELECT * FROM Administrator WHERE AdminUsername=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setString(1, adminname);
            ResultSet rs=prepStmt.executeQuery();
            if(rs.next())
            {
                if(rs.getString("AdminPassword").equals(adminpassword))
                {
                    outputToClient.writeUTF("LOGIN_SUCCESSFUL");
                    outputToClient.flush();
                }
                else
                {
                    outputToClient.writeUTF("LOGIN_FAILED_WRONG_PASSWORD");
                    outputToClient.flush();
                }
                
            }
            else
            {
                outputToClient.writeUTF("LOGIN_FAILED_WRONG_USERNAME");
                outputToClient.flush();
            }
        }
        else
        {
            System.out.println("Login request");
        }
        inputFromClient.close();
        outputToClient.close();
        sockClient.close();
        
        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
