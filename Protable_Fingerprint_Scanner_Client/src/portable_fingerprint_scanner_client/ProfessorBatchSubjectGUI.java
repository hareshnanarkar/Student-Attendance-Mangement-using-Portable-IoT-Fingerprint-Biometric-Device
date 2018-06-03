/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portable_fingerprint_scanner_client;

import EntityPackage.AttendanceSheet;
import EntityPackage.BatchSubject;
import EntityPackage.SubjectProfessor;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author admin
 */
public class ProfessorBatchSubjectGUI extends javax.swing.JPanel {

    /**
     * Creates new form ProfessorBatchSubjectGUI
     */
    BiometricsGUI mainGUI;
    ArrayList<BatchSubject> arrBatchSubject;
    public ProfessorBatchSubjectGUI(BiometricsGUI gui) {
        initComponents();
        mainGUI=gui;
        jlblHiUser.setText("Hi, "+mainGUI.prof.FirstName);
        jlblHiUser.setHorizontalAlignment(SwingConstants.RIGHT);
        loadBatchSubjectList(mainGUI.prof.ProfId);
    }
    
    
    public void loadBatchSubjectList(int profId)
    {
        try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        Socket sock=new Socket("192.168.43.189",9899);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        BiometricClientServerCommands bcsCommand=new BiometricClientServerCommands();
                        outputToServer.writeInt(bcsCommand.LOAD_BATCH_SUBJECT_TAUGHT_BY_PROFESSOR);
                        outputToServer.writeInt(profId);
                        outputToServer.flush();
                        arrBatchSubject=(ArrayList<BatchSubject>)inputFromServer.readObject();
                        DefaultTableModel model = new DefaultTableModel(){
                             @Override
                            public boolean isCellEditable(int row, int column) {
                               //all cells false
                               return false;
                            }
                        };
                        model.addColumn("Batch Name");
                        model.addColumn("Subject Name");
                        
                        
                        for(int i=0;i<arrBatchSubject.size();i++)
                        {
                            BatchSubject tempBS=arrBatchSubject.get(i);
                            model.addRow(new Object[]{tempBS.BatchName,tempBS.SubName});
                        }
                        jtblProfBatchSubject.setModel(model);
                        jtblProfBatchSubject.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 24));
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

        jPanel1 = new javax.swing.JPanel();
        jlblHiUser = new javax.swing.JLabel();
        jbtnProceed = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblProfBatchSubject = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
        setLayout(new java.awt.BorderLayout(10, 10));

        jPanel1.setLayout(new java.awt.BorderLayout(10, 10));

        jlblHiUser.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jlblHiUser.setText("jLabel1");
        jPanel1.add(jlblHiUser, java.awt.BorderLayout.NORTH);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jbtnProceed.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jbtnProceed.setText("Proceed");
        jbtnProceed.setMargin(new java.awt.Insets(30, 14, 30, 14));
        jbtnProceed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnProceedActionPerformed(evt);
            }
        });
        add(jbtnProceed, java.awt.BorderLayout.PAGE_END);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
        jPanel2.setLayout(new java.awt.BorderLayout(5, 5));

        jScrollPane2.setBackground(new java.awt.Color(204, 204, 255));

        jtblProfBatchSubject.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jtblProfBatchSubject.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jtblProfBatchSubject.setRowHeight(40);
        jtblProfBatchSubject.setRowMargin(5);
        jScrollPane2.setViewportView(jtblProfBatchSubject);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel2.setText("Select Batch and Subject");
        jPanel2.add(jLabel2, java.awt.BorderLayout.NORTH);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnProceedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnProceedActionPerformed
        // TODO add your handling code here:
        if(jtblProfBatchSubject.getSelectedRow()>=0)
        {
            mainGUI.attendanceSheet=new AttendanceSheet();
            mainGUI.attendanceSheet.ProfId=mainGUI.prof.ProfId;
            mainGUI.attendanceSheet.BatchId=arrBatchSubject.get(jtblProfBatchSubject.getSelectedRow()).BatchId;
            System.out.println("Batch Id :" + arrBatchSubject.get(jtblProfBatchSubject.getSelectedRow()).BatchId);
            mainGUI.attendanceSheet.BatchName=arrBatchSubject.get(jtblProfBatchSubject.getSelectedRow()).BatchName;
            mainGUI.attendanceSheet.BatchId=arrBatchSubject.get(jtblProfBatchSubject.getSelectedRow()).SubId;
            mainGUI.attendanceSheet.SubName=arrBatchSubject.get(jtblProfBatchSubject.getSelectedRow()).SubName;
            
            mainGUI.remove(mainGUI.currJPanel);
            mainGUI.currJPanel=new AttendanceDateGUI(mainGUI);
            mainGUI.add(mainGUI.currJPanel,BorderLayout.CENTER);
            mainGUI.validate();
        }
        else
        {
            UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
            JOptionPane.showMessageDialog(null,"Please select Batch and Subject" );
                                    
        }
    }//GEN-LAST:event_jbtnProceedActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnProceed;
    private javax.swing.JLabel jlblHiUser;
    private javax.swing.JTable jtblProfBatchSubject;
    // End of variables declaration//GEN-END:variables
}