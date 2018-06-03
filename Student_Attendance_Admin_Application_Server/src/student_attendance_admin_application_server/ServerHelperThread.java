/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student_attendance_admin_application_server;
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
        else if(strOperation.equals("LOAD_COURSE_LIST_REQUEST"))
        {
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="SELECT * FROM Course";
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            ArrayList<Course> arrCourseList=new ArrayList<Course>();
            while(rs.next())
            {
                Course tempD=new Course();
                tempD.CourseId=rs.getString("CourseId");
                tempD.CourseName=rs.getString("CourseName");
                arrCourseList.add(tempD);
            }
            outputToClient.writeObject(arrCourseList);
            outputToClient.flush();
        }
        else if(strOperation.equals("UPDATE_COURSE_REQUEST"))
        {
            System.out.println("update course");
            Course course=(Course)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="UPDATE Course SET CourseName=? WHERE CourseId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setString(1, course.CourseName);
            prepStmt.setString(2, course.CourseId);
            prepStmt.executeUpdate();
            outputToClient.writeUTF("UPDATE_COURSE_SUCCESSFUL");
            outputToClient.flush();
        }
        
        else if(strOperation.equals("CREATE_COURSE_REQUEST"))
        {
            System.out.println("create Course");
            Course course=(Course)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="INSERT INTO Course(CourseId,CourseName) VALUES(?,?)";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setString(1, course.CourseId);
            prepStmt.setString(2, course.CourseName);
            prepStmt.executeUpdate();
            outputToClient.writeUTF("CREATE_COURSE_SUCCESSFUL");
            outputToClient.flush();
        }
         else if(strOperation.equals("DELETE_COURSE_REQUEST"))
        {
            System.out.println("Delete course");
            String courseId=inputFromClient.readUTF();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="DELETE Course WHERE CourseId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setString(1, courseId);
            prepStmt.executeUpdate();
            
            outputToClient.writeUTF("DELETE_COURSE_SUCCESSFUL");
            outputToClient.flush();
        }
         else if(strOperation.equals("CREATE_STUDENT_REQUEST"))
        {
            System.out.println("create student");
            Student stud=(Student)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="INSERT INTO Student(FirstName,MiddleName,LastName,StudEmailId,DeptId,FingerPrintTemplate)"
                    + " VALUES(?,?,?,?,?,?)";
            PreparedStatement prepStmt=dbConn.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            prepStmt.setString(1, stud.FirstName);
            prepStmt.setString(2, stud.MiddleName);
            prepStmt.setString(3, stud.LastName);
            prepStmt.setString(4, stud.EmailId);
            prepStmt.setInt(5, stud.DeptId);
            prepStmt.setBytes(6, stud.FingerTemplate);
            
            prepStmt.executeUpdate();
            ResultSet rs=prepStmt.getGeneratedKeys();
            if(rs.next())
            {
                stud.StudId=rs.getInt(1);
            }
            outputToClient.writeUTF("CREATE_STUDENT_SUCCESSFUL");
            outputToClient.writeInt(stud.StudId);
            outputToClient.flush();
        }
         else if(strOperation.equals("LOAD_PROFESSOR_LIST_REQUEST"))
        {
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="SELECT * FROM Professor";
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            ArrayList<Professor> arrProfessorList=new ArrayList<Professor>();
            while(rs.next())
            {
                Professor tempP=new Professor();
                tempP.ProfId=rs.getInt("ProfId");
                tempP.FirstName=rs.getString("FirstName");
                tempP.LastName=rs.getString("LastName");
                arrProfessorList.add(tempP);
            }
            outputToClient.writeObject(arrProfessorList);
            outputToClient.flush();
        }
          else if(strOperation.equals("CREATE_PROFESSOR_REQUEST"))
        {
            System.out.println("create professor");
            Professor prof=(Professor)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="INSERT INTO Professor(FirstName,MiddleName,LastName,ProfEmailId,DeptId,FingerPrintTemplate)"
                    + " VALUES(?,?,?,?,?,?)";
            PreparedStatement prepStmt=dbConn.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            prepStmt.setString(1, prof.FirstName);
            prepStmt.setString(2, prof.MiddleName);
            prepStmt.setString(3, prof.LastName);
            prepStmt.setString(4, prof.EmailId);
            prepStmt.setInt(5, prof.DeptId);
            prepStmt.setBytes(6, prof.FingerTemplate);
            
            prepStmt.executeUpdate();
            ResultSet rs=prepStmt.getGeneratedKeys();
            if(rs.next())
            {
                prof.ProfId=rs.getInt(1);
            }
            outputToClient.writeUTF("CREATE_PROFESSOR_SUCCESSFUL");
            outputToClient.writeInt(prof.ProfId);
            outputToClient.flush();
        }
        else if(strOperation.equals("LOAD_SUBJECT_LIST_REQUEST"))
        {
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="SELECT * FROM Subjects";
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            ArrayList<Subject> arrSubjectList=new ArrayList<Subject>();
            while(rs.next())
            {
                Subject tempS=new Subject();
                tempS.SubjectId=rs.getInt("SubId");
                tempS.SubjectName=rs.getString("SubName");
                tempS.SubjectAbbr=rs.getString("SubAbbreviation");
                tempS.IsPractical=rs.getBoolean("IsPractical");
                System.out.println(tempS.IsPractical);
                arrSubjectList.add(tempS);
            }
            outputToClient.writeObject(arrSubjectList);
            outputToClient.flush();
        }
        else if(strOperation.equals("UPDATE_SUBJECT_REQUEST"))
        {
            System.out.println("update subject");
            Subject sub=(Subject)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="UPDATE Subjects SET SubName=?, SubAbbr=?, IsPractical=? WHERE SubId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setString(1, sub.SubjectName);
            prepStmt.setString(2, sub.SubjectAbbr);
            prepStmt.setInt(3, sub.SubjectId);
            prepStmt.setBoolean(4, sub.IsPractical);
            prepStmt.executeUpdate();
            outputToClient.writeUTF("UPDATE_SUBJECT_SUCCESSFUL");
            outputToClient.flush();
        }
        
        else if(strOperation.equals("CREATE_SUBJECT_REQUEST"))
        {
            System.out.println("create Subject");
            Subject sub=(Subject)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="INSERT INTO Subjects(SubName,SubAbbreviation,IsPractical) VALUES(?,?,?)";
            PreparedStatement prepStmt=dbConn.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            prepStmt.setString(1, sub.SubjectName);
            prepStmt.setString(2, sub.SubjectAbbr);
            prepStmt.setBoolean(3, sub.IsPractical);
            prepStmt.executeUpdate();
            ResultSet rs=prepStmt.getGeneratedKeys();
            if(rs.next())
            {
                sub.SubjectId=rs.getInt(1);
            }
            outputToClient.writeUTF("CREATE_SUBJECT_SUCCESSFUL");
            outputToClient.writeInt(sub.SubjectId);
            outputToClient.flush();
        }
         else if(strOperation.equals("DELETE_SUBJECT_REQUEST"))
        {
            System.out.println("Delete subject");
            int subId=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="DELETE Subjects WHERE SubId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setInt(1, subId);
            prepStmt.executeUpdate();
            
            outputToClient.writeUTF("DELETE_SUBJECT_SUCCESSFUL");
            outputToClient.flush();
        }
         else if(strOperation.equals("LOAD_BATCH_LIST_REQUEST"))
        {
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="select BatchId,BatchName,Semester,AcademicYear,BatchYear,IsActive,Batch.CourseId,CourseName from Batch join Course on Batch.Courseid=Course.CourseId";
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            ArrayList<Batch> arrBatchList=new ArrayList<Batch>();
            while(rs.next())
            {
                Batch tempB=new Batch();
                tempB.BatchId=rs.getInt("BatchId");
                tempB.BatchName=rs.getString("BatchName");
                tempB.Semester=rs.getInt("Semester");
                tempB.AcademicYear=rs.getInt("AcademicYear");
                tempB.BatchYear=rs.getInt("BatchYear");
                tempB.IsActive=rs.getBoolean("IsActive");
                tempB.course=new Course();
                tempB.course.CourseId=rs.getString("CourseId");
                tempB.course.CourseName=rs.getString("CourseName");
                System.out.println(rs.getString("CourseName"));
                arrBatchList.add(tempB);
            }
            outputToClient.writeObject(arrBatchList);
            outputToClient.flush();
        }
        else if(strOperation.equals("UPDATE_BATCH_REQUEST"))
        {
            System.out.println("update Batch");
            Batch batch=(Batch)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="UPDATE Batch SET BatchName=?, Semester=?, AcademicYear=?, BatchYear=?, IsActive=?, CourseId=? WHERE BatchId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setString(1, batch.BatchName);
            prepStmt.setInt(2, batch.Semester);
            prepStmt.setInt(3, batch.AcademicYear);
            prepStmt.setInt(4, batch.BatchYear);
            prepStmt.setBoolean(5, batch.IsActive);
            prepStmt.setString(6, batch.course.CourseId);
            prepStmt.setInt(7, batch.BatchId);
            prepStmt.executeUpdate();
            outputToClient.writeUTF("UPDATE_BATCH_SUCCESSFUL");
            outputToClient.flush();
        }
        
        else if(strOperation.equals("CREATE_BATCH_REQUEST"))
        {
            System.out.println("create Batch");
            Batch batch=(Batch)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="INSERT INTO Batch(BatchName,Semester,AcademicYear,BatchYear,IsActive,CourseId) VALUES(?,?,?,?,?,?)";
            PreparedStatement prepStmt=dbConn.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            prepStmt.setString(1, batch.BatchName);
            prepStmt.setInt(2, batch.Semester);
            prepStmt.setInt(3, batch.AcademicYear);
            prepStmt.setInt(4, batch.BatchYear);
            prepStmt.setBoolean(5, batch.IsActive);
            prepStmt.setString(6, batch.course.CourseId);
            prepStmt.executeUpdate();
            ResultSet rs=prepStmt.getGeneratedKeys();
            if(rs.next())
            {
                batch.BatchId=rs.getInt(1);
            }
            outputToClient.writeUTF("CREATE_BATCH_SUCCESSFUL");
            outputToClient.writeInt(batch.BatchId);
            outputToClient.flush();
        }
         else if(strOperation.equals("DELETE_BATCH_REQUEST"))
        {
            System.out.println("Delete Batch");
            int batchId=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="DELETE Batch WHERE BatchId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setInt(1, batchId);
            prepStmt.executeUpdate();
            
            outputToClient.writeUTF("DELETE_BATCH_SUCCESSFUL");
            outputToClient.flush();
        }
         else if(strOperation.equals("ADD_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST"))
        {
            System.out.println("add bath subject professor");
            int batchId=(int)inputFromClient.readObject();
            int subjectId=(int)inputFromClient.readObject();
            int profId=(int)inputFromClient.readObject();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="INSERT INTO BatchSubjectProfessor(BatchId,SubId,ProfId) VALUES(?,?,?)";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setInt(1, batchId);
            prepStmt.setInt(2, subjectId);
            prepStmt.setInt(3, profId);
            prepStmt.executeUpdate();
            outputToClient.writeUTF("ADD_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST_SUCCESSFUL");
            outputToClient.flush();
        }
        else if(strOperation.equals("LOAD_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST"))
        {
            int batchId=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            
            String query="select Subjects.SubId,SubName,Professor.ProfId,FirstName,LastName " + 
                        "from BatchSubjectProfessor " +
                        "join Subjects on BatchSubjectProfessor.SubId=Subjects.SubId " +
                        "join Professor on BatchSubjectProfessor.ProfId=Professor.ProfId "  +
                        " where BatchSubjectProfessor.BatchId="+batchId;
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            ArrayList<SubjectProfessor> arrSubProfList=new ArrayList<SubjectProfessor>();
            
            while(rs.next())
            {
                SubjectProfessor temSP=new SubjectProfessor();
                temSP.SubId=rs.getInt("SubId");
                temSP.SubName=rs.getString("SubName");
                temSP.ProfId=rs.getInt("ProfId");
                temSP.ProfFirstName=rs.getString("FirstName");
                temSP.ProfLastName=rs.getString("LastName");
                arrSubProfList.add(temSP);
            }
            outputToClient.writeObject(arrSubProfList);
            outputToClient.flush();
        }
         else if(strOperation.equals("DELETE_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST"))
        {
            System.out.println("Delete Batch subject Professor");
            int batchId=inputFromClient.readInt();
            int subId=inputFromClient.readInt();
            int profId=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="DELETE BatchSubjectProfessor WHERE BatchId=? and SubId=? and ProfId=?";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setInt(1, batchId);
            prepStmt.setInt(2, subId);
            prepStmt.setInt(3, profId);
            
            prepStmt.executeUpdate();
            
            outputToClient.writeUTF("DELETE_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST_SUCCESSFUL");
            outputToClient.flush();
        } 
         
        else if(strOperation.equals("SEARCH_STUDENT_REQUEST"))
        {
            int studId=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            
            String query="select StudId,FirstName,LastName from Student where StudId="+studId;
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            
            
            
            if(rs.next())
            {
                outputToClient.writeUTF("SEARCH_STUDENT_REQUEST_SUCCESSFUL");
                Student stud=new Student();
                stud.StudId=rs.getInt("StudId");
                stud.FirstName=rs.getString("FirstName");
                stud.LastName=rs.getString("LastName");
                outputToClient.writeObject(stud);
            }
            else
                outputToClient.writeUTF("STUDENT_NOT_FOUND");
            outputToClient.flush();
        }
        
         else if(strOperation.equals("ADD_STUDENT_BATCH_MAPPING_REQUEST"))
        {
            System.out.println("add batch student");
            int batchId=inputFromClient.readInt();
            int studId=inputFromClient.readInt();
            int rollNo=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            String query="INSERT INTO StudentBatch(BatchId,StudId,ROllNo) VALUES(?,?,?)";
            PreparedStatement prepStmt=dbConn.prepareStatement(query);
            prepStmt.setInt(1, batchId);
            prepStmt.setInt(2, studId);
            prepStmt.setInt(3, rollNo);
            prepStmt.executeUpdate();
            outputToClient.writeUTF("ADD_STUDENT_BATCH_MAPPING_REQUEST_SUCCESSFUL");
            outputToClient.flush();
        }
         
        else if(strOperation.equals("LOAD_STUDENT_BATCH_MAPPING_REQUEST"))
        {
            int batchId=inputFromClient.readInt();
            String dbURL = "jdbc:sqlserver://ADMIN-PC:1433;databaseName=StudentAttendanceDB;user=haresh;password=haresh@123";
            Connection dbConn = DriverManager.getConnection(dbURL);
            
            String query="select RollNo,Student.StudId,FirstName,LastName from Student " +
                    "join StudentBatch on StudentBatch.StudId=Student.StudId " +
                    "where BatchId=" + batchId + " order by RollNo";
            
            Statement stmt=dbConn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            ArrayList<StudentBatch> arrStudBatchList=new ArrayList<StudentBatch>();
            
            while(rs.next())
            {
                StudentBatch temSB=new StudentBatch();
                temSB.RollNo=rs.getInt("RollNo");
                temSB.StudID=rs.getInt("StudId");
                temSB.FirstName=rs.getString("FirstName");
                temSB.LastName=rs.getString("LastName");
                arrStudBatchList.add(temSB);
            }
            outputToClient.writeObject(arrStudBatchList);
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
