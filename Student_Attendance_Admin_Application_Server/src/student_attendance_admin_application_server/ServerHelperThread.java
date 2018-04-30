/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student_attendance_admin_application_server;
import EntityPackage.Department;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
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
        else if(strOperation.equals("LOAD_DEPARTMENT_LIST_REQUEST"))
        {
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="SELECT * FROM Department";
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            ArrayList<Department> arrDeptList=new ArrayList<Department>();
            while(rs.next())
            {
                Department tempD=new Department();
                tempD.DeptId=rs.getInt("DeptId");
                tempD.DeptName=rs.getString("DeptName");
                tempD.DeptAbbr=rs.getString("DeptAbbr");
                arrDeptList.add(tempD);
            }
            outputToClient.writeObject(arrDeptList);
            outputToClient.flush();
        }
        else if(strOperation.equals("UPDATE_DEPARTMENT_REQUEST"))
        {
            System.out.println("update department");
            Department dept=(Department)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="UPDATE Department SET DeptName=?, DeptAbbr=? WHERE DeptId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setString(1, dept.DeptName);
            prepStmt.setString(2, dept.DeptAbbr);
            prepStmt.setInt(3, dept.DeptId);
            prepStmt.executeUpdate();
            outputToClient.writeUTF("UPDATE_DEPARTMENT_SUCCESSFUL");
            outputToClient.flush();
        }
        
        else if(strOperation.equals("CREATE_DEPARTMENT_REQUEST"))
        {
            System.out.println("create department");
            Department dept=(Department)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="INSERT INTO Department(DeptName,DeptAbbr) VALUES(?,?)";
            PreparedStatement prepStmt=dbConn.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            prepStmt.setString(1, dept.DeptName);
            prepStmt.setString(2, dept.DeptAbbr);
            prepStmt.executeUpdate();
            ResultSet rs=prepStmt.getGeneratedKeys();
            if(rs.next())
            {
                dept.DeptId=rs.getInt(1);
            }
            outputToClient.writeUTF("CREATE_DEPARTMENT_SUCCESSFUL");
            outputToClient.writeInt(dept.DeptId);
            outputToClient.flush();
        }
         else if(strOperation.equals("DELETE_DEPARTMENT_REQUEST"))
        {
            System.out.println("Delete department");
            int deptId=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="DELETE Department WHERE DeptId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setInt(1, deptId);
            prepStmt.executeUpdate();
            
            outputToClient.writeUTF("DELETE_DEPARTMENT_SUCCESSFUL");
            outputToClient.flush();
        }
        else
        {
            System.out.println("Wrong request");
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
