/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student_attendance_admin_application_client;

import EntityPackage.Batch;
import EntityPackage.Course;
import EntityPackage.Professor;
import EntityPackage.Student;
import EntityPackage.StudentBatch;
import EntityPackage.Subject;
import EntityPackage.SubjectProfessor;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author admin
 */
public class BatchMapperGUI extends javax.swing.JPanel {

    /**
     * Creates new form BatchMapperGUI
     */
    ArrayList<Batch> arrBatchList;
    ArrayList<Subject> arrSubjectList;
    ArrayList<Professor> arrProfessorList;
    ArrayList<SubjectProfessor> arrSubProfList;
    ArrayList<StudentBatch> arrStudBatchList;
    public BatchMapperGUI() {
        initComponents();
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        jtxtBatchId.setEditable(false);
        jtxtBatchName.setEditable(false);
        jcmbProfessor.setEnabled(false);
        jcmbSubject.setEnabled(false);
        jbtnAddSubjectProfessorMap.setEnabled(false);
        jbtnDeleteSubjectProfessorMap.setEnabled(false);
        
        jbtnAddStudent.setEnabled(false);
        jbtnSearch.setEnabled(false);
        jbtnSaveUpdateStudent.setEnabled(false);
        jbtnRemoveStudent.setEnabled(false);
        jtxtSearchId.setEditable(false);
        jtxtStudId.setEditable(false);
        jtxtStudName.setEditable(false);
        jtxtStudRollNo.setEditable(false);
        jtxtSearchId.setText("");
        jtxtStudId.setText("");
        jtxtStudName.setText("");
        jtxtStudRollNo.setText("");
        loadBatchList();
        loadSubjectList();
        loadProfessorList();
        
    }

    public void loadBatchList()
    {
        try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("127.0.0.1",9898);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        outputToServer.writeUTF("LOAD_BATCH_LIST_REQUEST");
                        outputToServer.flush();
                        arrBatchList=(ArrayList<Batch>)inputFromServer.readObject();
                        DefaultListModel<String> listModel=new DefaultListModel<>();
                        for(int i=0;i<arrBatchList.size();i++)
                        {
                            listModel.addElement(arrBatchList.get(i).BatchName);
                            System.out.println(arrBatchList.get(i).course.CourseId);
                            System.out.println(arrBatchList.get(i).course.CourseName);
                        }
                        jlistBatch.setModel(listModel);
                    }
                    catch(Exception e)
                    {
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void loadSubjectList()
    {
        try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("127.0.0.1",9898);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        outputToServer.writeUTF("LOAD_SUBJECT_LIST_REQUEST");
                        outputToServer.flush();
                        arrSubjectList=(ArrayList<Subject>)inputFromServer.readObject();
                        jcmbSubject.removeAllItems();
                        for(int i=0;i<arrSubjectList.size();i++)
                        {
                            jcmbSubject.addItem(arrSubjectList.get(i).SubjectId + " - "+ arrSubjectList.get(i).SubjectName);
                        }
                    }
                    catch(Exception e)
                    {
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void loadProfessorList()
    {
        try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("127.0.0.1",9898);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        outputToServer.writeUTF("LOAD_PROFESSOR_LIST_REQUEST");
                        outputToServer.flush();
                        arrProfessorList=(ArrayList<Professor>)inputFromServer.readObject();
                        jcmbProfessor.removeAllItems();
                        for(int i=0;i<arrProfessorList.size();i++)
                        {
                            jcmbProfessor.addItem(arrProfessorList.get(i).ProfId + " - "+ arrProfessorList.get(i).FirstName + " "+ arrProfessorList.get(i).LastName);
                        }
                    }
                    catch(Exception e)
                    {
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void loadBatchSubjectProfessorMappingList(int batchId)
    {
        try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("127.0.0.1",9898);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        outputToServer.writeUTF("LOAD_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST");
                        System.out.println("Batch Id : "+batchId);
                        outputToServer.writeInt(batchId);
                        outputToServer.flush();
                        arrSubProfList=(ArrayList<SubjectProfessor>)inputFromServer.readObject();
                        DefaultTableModel model = new DefaultTableModel();
                        model.addColumn("Subject Id");
                        model.addColumn("Subject Name");
                        model.addColumn("Professor Id");
                        model.addColumn("Professor Name");
                        for(int i=0;i<arrSubProfList.size();i++)
                        {
                            SubjectProfessor tempSP=arrSubProfList.get(i);
                            model.addRow(new Object[]{tempSP.SubId,tempSP.SubName,tempSP.ProfId,tempSP.ProfFirstName +" "+ tempSP.ProfLastName});
                        }
                        jtblSubProf.setModel(model);
                    }    
                    catch(Exception e)
                    {
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void loadStudentBatchMappingList(int batchId)
    {
        try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("127.0.0.1",9898);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        outputToServer.writeUTF("LOAD_STUDENT_BATCH_MAPPING_REQUEST");
                        System.out.println("Batch Id : "+batchId);
                        outputToServer.writeInt(batchId);
                        outputToServer.flush();
                        arrStudBatchList=(ArrayList<StudentBatch>)inputFromServer.readObject();
                        DefaultTableModel model = new DefaultTableModel();
                        model.addColumn("Roll No");
                        model.addColumn("Student Id");
                        model.addColumn("Student Name");
                        for(int i=0;i<arrStudBatchList.size();i++)
                        {
                            StudentBatch tempSB=arrStudBatchList.get(i);
                            model.addRow(new Object[]{tempSB.RollNo,tempSB.StudID,tempSB.FirstName +" "+ tempSB.LastName});
                        }
                        jtblStudentBatch.setModel(model);
                    }    
                    catch(Exception e)
                    {
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jlistBatch = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtBatchId = new javax.swing.JTextField();
        jtxtBatchName = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jcmbSubject = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jcmbProfessor = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblSubProf = new javax.swing.JTable();
        jbtnAddSubjectProfessorMap = new javax.swing.JButton();
        jbtnDeleteSubjectProfessorMap = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtblStudentBatch = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jtxtStudId = new javax.swing.JTextField();
        jtxtStudName = new javax.swing.JTextField();
        jtxtStudRollNo = new javax.swing.JTextField();
        jbtnSearch = new javax.swing.JButton();
        jbtnSaveUpdateStudent = new javax.swing.JButton();
        jbtnRemoveStudent = new javax.swing.JButton();
        jtxtSearchId = new javax.swing.JTextField();
        jbtnAddStudent = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        jlistBatch.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlistBatchValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jlistBatch);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Details :");

        jLabel3.setText("Batch Id :");

        jLabel4.setText("Batch Name :");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setText("Map Professor and Subject");

        jLabel6.setText("Select Subject :");

        jLabel7.setText("Select Professor :");

        jScrollPane2.setViewportView(jtblSubProf);

        jbtnAddSubjectProfessorMap.setText("Add");
        jbtnAddSubjectProfessorMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddSubjectProfessorMapActionPerformed(evt);
            }
        });

        jbtnDeleteSubjectProfessorMap.setText("Delete selected row");
        jbtnDeleteSubjectProfessorMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteSubjectProfessorMapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcmbSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(jcmbProfessor, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jbtnAddSubjectProfessorMap)))
                        .addGap(0, 178, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnDeleteSubjectProfessorMap)
                .addGap(28, 28, 28))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jcmbSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jcmbProfessor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnAddSubjectProfessorMap))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnDeleteSubjectProfessorMap)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setText("Students Details ");

        jtblStudentBatch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(jtblStudentBatch);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setText("Student Id : ");

        jLabel10.setText("Student Name :");

        jLabel11.setText("Batch Roll No. :");

        jtxtStudId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtStudIdActionPerformed(evt);
            }
        });

        jbtnSearch.setText("Search");
        jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSearchActionPerformed(evt);
            }
        });

        jbtnSaveUpdateStudent.setText("Save");
        jbtnSaveUpdateStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveUpdateStudentActionPerformed(evt);
            }
        });

        jbtnRemoveStudent.setText("Remove");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel9))
                                .addGap(43, 43, 43)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxtStudName, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                                    .addComponent(jtxtStudRollNo)
                                    .addComponent(jtxtStudId)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jtxtSearchId, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(jbtnSaveUpdateStudent)
                        .addGap(47, 47, 47)
                        .addComponent(jbtnRemoveStudent)))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnSearch)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jtxtSearchId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jtxtStudId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtStudName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtStudRollNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnSaveUpdateStudent)
                    .addComponent(jbtnRemoveStudent))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jbtnAddStudent.setText("Add New Student to Batch");
        jbtnAddStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddStudentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnAddStudent)
                        .addGap(66, 66, 66))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jbtnAddStudent))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtBatchId, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jtxtBatchName, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtxtBatchId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jtxtBatchName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jLabel1.setText("Add, Remove or Update Batch");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jlistBatchValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlistBatchValueChanged
        // TODO add your handling code here:
        if(jlistBatch.getSelectedIndex()!=-1)
        {
            jtxtBatchId.setText(""+ arrBatchList.get(jlistBatch.getSelectedIndex()).BatchId);
            jtxtBatchName.setText(arrBatchList.get(jlistBatch.getSelectedIndex()).BatchName);
            loadBatchSubjectProfessorMappingList(arrBatchList.get(jlistBatch.getSelectedIndex()).BatchId);
            loadStudentBatchMappingList(arrBatchList.get(jlistBatch.getSelectedIndex()).BatchId);
            jbtnAddStudent.setEnabled(true);
            jcmbProfessor.setEnabled(true);
            jcmbSubject.setEnabled(true);
            jbtnDeleteSubjectProfessorMap.setEnabled(true);
            jbtnAddSubjectProfessorMap.setEnabled(true);
        }
    }//GEN-LAST:event_jlistBatchValueChanged

    private void jbtnAddSubjectProfessorMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddSubjectProfessorMapActionPerformed
        // TODO add your handling code here:
        try
            {
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{

                            Socket sock=new Socket("127.0.0.1",9898);
                            ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                            ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                            outputToServer.writeUTF("ADD_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST");
                            int batchId=Integer.parseInt(jtxtBatchId.getText());
                            int subjectId=arrSubjectList.get(jcmbSubject.getSelectedIndex()).SubjectId;
                            int profId=arrProfessorList.get(jcmbProfessor.getSelectedIndex()).ProfId;
                            outputToServer.writeObject(batchId);
                            outputToServer.writeObject(subjectId);
                            outputToServer.writeObject(profId);
                            outputToServer.flush();
                            String result=inputFromServer.readUTF();
                            System.out.println(result);
                            if(result.equals("ADD_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST_SUCCESSFUL"))
                            {
                                
                                loadBatchSubjectProfessorMappingList(batchId);
                            }
                            else
                            {

                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
    }//GEN-LAST:event_jbtnAddSubjectProfessorMapActionPerformed

    private void jbtnDeleteSubjectProfessorMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteSubjectProfessorMapActionPerformed
        // TODO add your handling code here:
           try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("127.0.0.1",9898);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        outputToServer.writeUTF("DELETE_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST");
                        
                        int batchId=Integer.parseInt(jtxtBatchId.getText());
                        int subId=arrSubProfList.get(jtblSubProf.getSelectedRow()).SubId;
                        int profId=arrSubProfList.get(jtblSubProf.getSelectedRow()).ProfId;
                        
                        outputToServer.writeInt(batchId);
                        outputToServer.writeInt(subId);
                        outputToServer.writeInt(profId);
                        outputToServer.flush();
                        String result=inputFromServer.readUTF();
                        System.out.println(result);
                        if(result.equals("DELETE_BATCH_SUBJECT_PROFESSOR_MAPPING_REQUEST_SUCCESSFUL"))
                        {
                            loadBatchSubjectProfessorMappingList(batchId);
                        }
                        else
                        {
                            
                        }
                    }
                    catch(Exception e)
                    {
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } 
    }//GEN-LAST:event_jbtnDeleteSubjectProfessorMapActionPerformed

    private void jtxtStudIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtStudIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtStudIdActionPerformed

    private void jbtnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSearchActionPerformed
        // TODO add your handling code here:
           try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("127.0.0.1",9898);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        outputToServer.writeUTF("SEARCH_STUDENT_REQUEST");
                        
                        int studId=Integer.parseInt(jtxtSearchId.getText());
                        
                        outputToServer.writeInt(studId);
                        outputToServer.flush();
                        String result=inputFromServer.readUTF();
                        System.out.println(result);
                        if(result.equals("SEARCH_STUDENT_REQUEST_SUCCESSFUL"))
                        {
                            Student stud=(Student)inputFromServer.readObject();
                            jtxtStudId.setText(""+ stud.StudId);
                            jtxtStudName.setText(stud.FirstName+" "+stud.LastName);
                            jtxtStudId.setEditable(false);
                            jtxtStudName.setEditable(false);
                            jtxtStudRollNo.setEditable(true);
                            jbtnSaveUpdateStudent.setText("Save");
                            jbtnSaveUpdateStudent.setEnabled(true);
                            
                        }
                        else
                        {
                            jtxtStudId.setText("");
                            jtxtStudName.setText("");
                            jtxtStudRollNo.setText("");
                            jtxtStudId.setEditable(false);
                            jtxtStudName.setEditable(false);
                            jtxtStudRollNo.setEditable(false);
                            jbtnSaveUpdateStudent.setText("Save");
                            jbtnSaveUpdateStudent.setEnabled(false);
                        }
                    }
                    catch(Exception e)
                    {
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } 
    }//GEN-LAST:event_jbtnSearchActionPerformed

    private void jbtnAddStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddStudentActionPerformed
        // TODO add your handling code here:
        jbtnSearch.setEnabled(true);
        jtxtSearchId.setEditable(true);
    }//GEN-LAST:event_jbtnAddStudentActionPerformed

    private void jbtnSaveUpdateStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveUpdateStudentActionPerformed
        // TODO add your handling code here:
        
        int batchId=Integer.parseInt(jtxtBatchId.getText());
        int studId=Integer.parseInt(jtxtStudId.getText());
        int rollNo=Integer.parseInt(jtxtStudRollNo.getText());
        if(jbtnSaveUpdateStudent.getText().equals("Save Changes"))
        {
            try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("127.0.0.1",9898);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        outputToServer.writeUTF("ADD_BATCH_STUDENT_MAPPING_REQUEST");
                        //outputToServer.writeObject();
                        outputToServer.flush();
                        String result=inputFromServer.readUTF();
                        System.out.println(result);
                        if(result.equals("UPDATE_DEPARTMENT_SUCCESSFUL"))
                        {
                            
                        }
                        else
                        {
                            
                        }
                    }
                    catch(Exception e)
                    {
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        }
        else
        {
        try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("127.0.0.1",9898);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        outputToServer.writeUTF("ADD_STUDENT_BATCH_MAPPING_REQUEST");
                        outputToServer.writeInt(batchId);
                        outputToServer.writeInt(studId);
                        outputToServer.writeInt(rollNo);
                        outputToServer.flush();
                        String result=inputFromServer.readUTF();
                        System.out.println(result);
                        if(result.equals("ADD_STUDENT_BATCH_MAPPING_REQUEST_SUCCESSFUL"))
                        {
                            loadStudentBatchMappingList(batchId);
                        }
                        else
                        {
                            
                        }
                    }
                    catch(Exception e)
                    {
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        }
        
    }//GEN-LAST:event_jbtnSaveUpdateStudentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton jbtnAddStudent;
    private javax.swing.JButton jbtnAddSubjectProfessorMap;
    private javax.swing.JButton jbtnDeleteSubjectProfessorMap;
    private javax.swing.JButton jbtnRemoveStudent;
    private javax.swing.JButton jbtnSaveUpdateStudent;
    private javax.swing.JButton jbtnSearch;
    private javax.swing.JComboBox<String> jcmbProfessor;
    private javax.swing.JComboBox<String> jcmbSubject;
    private javax.swing.JList<String> jlistBatch;
    private javax.swing.JTable jtblStudentBatch;
    private javax.swing.JTable jtblSubProf;
    private javax.swing.JTextField jtxtBatchId;
    private javax.swing.JTextField jtxtBatchName;
    private javax.swing.JTextField jtxtSearchId;
    private javax.swing.JTextField jtxtStudId;
    private javax.swing.JTextField jtxtStudName;
    private javax.swing.JTextField jtxtStudRollNo;
    // End of variables declaration//GEN-END:variables
}
