/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portable_fingerprint_scanner_server;
import EntityPackage.*;
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
        int operation=inputFromClient.readInt();
        System.out.println("operation to be performed : "+operation);
        BiometricClientServerCommands bcsCommand=new BiometricClientServerCommands();
        if(operation==bcsCommand.LOAD_PROFESSOR_FINGERPRINT)
        {
            System.out.println("load professor fingerprint");
            int profId=inputFromClient.readInt();
            
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="SELECT * FROM Professor WHERE ProfId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setInt(1, profId);
            ResultSet rs=prepStmt.executeQuery();
            if(rs.next())
            {
                Professor prof=new Professor();
                prof.ProfId=rs.getInt("ProfId");
                prof.FirstName=rs.getString("FirstName");
                prof.LastName=rs.getString("LastName");
                prof.FingerTemplate=rs.getBytes("FingerPrintTemplate");
                System.out.println("Size:"+prof.FingerTemplate.length);
                outputToClient.writeInt(bcsCommand.LOAD_PROFESSOR_FINGERPRINT_SUCCESSFUL);
                outputToClient.writeObject(prof);
                outputToClient.flush();
                
            }
            else
            {
                outputToClient.writeInt(bcsCommand.PROFESSOR_NOT_FOUND);
                outputToClient.flush();
            }
        }
        else if(operation==bcsCommand.LOAD_BATCH_SUBJECT_TAUGHT_BY_PROFESSOR)
        {
            int profId=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            
            String query="SELECT Batch.BatchId, BatchName, Subjects.SubId,SubName " +
                        "from BatchSubjectProfessor " +
                        "join Batch on BatchSubjectProfessor.BatchId=Batch.BatchId " +
                        "join Subjects on BatchSubjectProfessor.SubId=Subjects.SubId " +
                        "where ProfId="+profId;
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            ArrayList<BatchSubject> arrBatchSubjectList=new ArrayList<BatchSubject>();
            
            while(rs.next())
            {
                BatchSubject temBS=new BatchSubject();
                temBS.BatchId=rs.getInt("BatchId");
                temBS.BatchName=rs.getString("BatchName");
                temBS.SubId=rs.getInt("SubId");
                temBS.SubName=rs.getString("SubName");
                arrBatchSubjectList.add(temBS);
            }
            outputToClient.writeObject(arrBatchSubjectList);
            outputToClient.flush();
        }
        
        else if(operation==bcsCommand.LOAD_STUDENT_BATCH_LIST)
        {
            int batchId=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            
            String query="SELECT RollNo,Student.StudId,FirstName,LastName,FingerPrintTemplate from StudentBatch " +
                            "join Student on StudentBatch.StudId=Student.StudId " +
                            "where BatchId="+batchId;
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            ArrayList<StudentAttedanceInfo> arrBatchStudentList=new ArrayList<StudentAttedanceInfo>();
            
            while(rs.next())
            {
                StudentAttedanceInfo temSAI=new StudentAttedanceInfo();
                temSAI.RollNo=rs.getInt("RollNo");
                temSAI.StudId=rs.getInt("StudId");
                temSAI.FirstName=rs.getString("FirstName");
                temSAI.LastName=rs.getString("LastName");
                temSAI.FingerPrintTemplate=rs.getBytes("FingerPrintTemplate");
                arrBatchStudentList.add(temSAI);
            }
            outputToClient.writeObject(arrBatchStudentList);
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
