/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portable_fingerprint_scanner_client;

import EntityPackage.StudentAttedanceInfo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author admin
 */
public class AttendanceSheetGUI extends javax.swing.JPanel {

    /**
     * Creates new form AttendanceSheetGUI
     */
    BiometricsGUI mainGUI;
    boolean Submitflag=false;
    boolean backflag=false;
    boolean logoutflag=false;
    JOptionPane jOptionpane;
    JDialog jdialog;
    
    public AttendanceSheetGUI(BiometricsGUI gui) {
        initComponents();
        mainGUI=gui;
        jbtnSubmit.setEnabled(false);
        jlblBatch.setText(mainGUI.attendanceSheet.BatchName);
        jlblSubject.setText(mainGUI.attendanceSheet.SubName);
        jlblDate.setText("Date : "+ mainGUI.attendanceSheet.AttendanceDate);
        loadStudentBatchList(mainGUI.attendanceSheet.BatchId);
        jlistStudent.setCellRenderer(new MyListCellRenderer());
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(30,0));
    }
    class MyListCellRenderer extends JLabel implements ListCellRenderer {

        int conta = 5;

        public MyListCellRenderer() {
            super();
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.toString());
            //System.out.println("Rendering");
                if(mainGUI.attendanceSheet.StudentAttInfoList.get(index).Presence)
                {
                    setBackground(new Color(204,255,204));
                }
                else
                {
                        setBackground(new Color(255,204,204));
                    
                }
//                setBackground(new Color(255,204,204));
//                if(index==2 || index==4)
//                    setBackground(new Color(204,255,204));
                setBorder(BorderFactory.createLineBorder(Color.BLACK));
                setFont(new Font("Tahoma", Font.PLAIN, 24));
            return this;
        }
    }
    public void loadStudentBatchList(int batchId)
    {
        try
        {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        System.out.println("Hiiiiiiiiiiiiiiiiii");
                        Socket sock=new Socket("192.168.43.189",9899);
                        ObjectInputStream inputFromServer=new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream outputToServer=new ObjectOutputStream(sock.getOutputStream());
                        BiometricClientServerCommands bcsCommands=new BiometricClientServerCommands();
                        outputToServer.writeInt(bcsCommands.LOAD_STUDENT_BATCH_LIST);
                        System.out.println("Batch Id : "+batchId);
                        outputToServer.writeInt(batchId);
                        outputToServer.flush();
                        
                        mainGUI.attendanceSheet.StudentAttInfoList=(ArrayList<StudentAttedanceInfo>)inputFromServer.readObject();
                        FingerPrintScanner Serialconnector=new FingerPrintScanner();
                        try
                        {   
                            
                            
                            if(Serialconnector.open("/dev/ttyUSB0"))
                            {
                                Serialconnector.deleteAllFingerPrintFromDevice();
                                Serialconnector.setTemplate(0, mainGUI.prof.FingerTemplate);
                                int dotcount=1;
                                for(int i=0;i<mainGUI.attendanceSheet.StudentAttInfoList.size();i++)
                                {
                                    if(backflag==true)
                                    {
                                        System.out.println("helllo 1");
                                        Serialconnector.LEDON();
                                        while(backflag==true)
                                        {
                                            
                                        System.out.println("helllo 2");
                                        int res=Serialconnector.identify1_N();
                                        if(res==0)
                                        {
                                            
                                        System.out.println("helllo 3");
                                        jdialog.setVisible(false);
                                        jdialog.dispose();
                                        mainGUI.remove(mainGUI.currJPanel);
                                        mainGUI.currJPanel=mainGUI.stackPanel.pop();
                                        mainGUI.add(mainGUI.currJPanel);
                                        mainGUI.repaint();
                                        mainGUI.validate();
                                        Serialconnector.LEDOFF();
                                        Serialconnector.close();
                                        return;
                                        }
                                        Thread.sleep(500);
                                        }
                                        
                                        
                                        Serialconnector.LEDOFF();
                                        System.out.println("helllo 5555");
                                    }
                                    if(logoutflag==true)
                                    {
                                        System.out.println("helllo 1");
                                        Serialconnector.LEDON();
                                        while(logoutflag==true)
                                        {
                                            
                                        System.out.println("helllo 2");
                                        int res=Serialconnector.identify1_N();
                                        if(res==0)
                                        {
                                            
                                        jdialog.setVisible(false);
                                        jdialog.dispose();
                                        mainGUI.prof=null;
                                        mainGUI.attendanceSheet=null;
                                        mainGUI.stackPanel.clear();
                                        mainGUI.remove(mainGUI.currJPanel);
                                        mainGUI.currJPanel=new InitialProfessorAuthenticationGUI(mainGUI);
                                        mainGUI.add(mainGUI.currJPanel);
                                        mainGUI.repaint();
                                        mainGUI.validate();
                                        Serialconnector.LEDOFF();
                                        Serialconnector.close();
                                        return;
                                        }
                                        Thread.sleep(500);
                                        }
                                        
                                        Serialconnector.LEDOFF();
                                        
                                    }
                                    System.out.println("i:"+ (i+1));
                                    Serialconnector.setTemplate(i+1, mainGUI.attendanceSheet.StudentAttInfoList.get(i).FingerPrintTemplate);
                                    
                                    String temp="<html>"+(i+1)+" out of "+ mainGUI.attendanceSheet.StudentAttInfoList.size()+" records loaded.<br/>Please wait";
                                    if(dotcount==1)
                                    {
                                        temp+=" .";
                                        dotcount=2;
                                    }
                                    else if(dotcount==2)
                                    {
                                        temp+=" . .";
                                        dotcount=3;
                                    }
                                    else
                                    {
                                        temp+=" . . .";
                                        dotcount=1;
                                    }
                                    temp+="</html>";
                                    jlblNotification.setText(temp);
                                    
                                }
                                DefaultListModel<String> listModel=new DefaultListModel<>();
                        
                                for(int i=0;i<mainGUI.attendanceSheet.StudentAttInfoList.size();i++)
                                {
                                    StudentAttedanceInfo stud=mainGUI.attendanceSheet.StudentAttInfoList.get(i);
                                    listModel.addElement(stud.RollNo+" - "+stud.FirstName+" "+ stud.LastName);
                                }
                                jlistStudent.setModel(listModel);
                                
                                jlblNotification.setForeground(new Color(0,102,51));
                                jlblNotification.setText("<html>Ready to record.<br/>Please circulate the device.</html>");
                                jbtnSubmit.setEnabled(true);
                                Serialconnector.LEDON();
                                while(true)
                                {
                                    
                                    int resultId=Serialconnector.identify1_N();
                                    if(backflag==true && resultId==0)
                                    {
                                        jdialog.setVisible(false);
                                        jdialog.dispose();
                                        mainGUI.remove(mainGUI.currJPanel);
                                        mainGUI.currJPanel=mainGUI.stackPanel.pop();
                                        mainGUI.add(mainGUI.currJPanel);
                                        mainGUI.repaint();
                                        mainGUI.validate();
                                        Serialconnector.LEDOFF();
                                        Serialconnector.close();
                                        return;
                                    }
                                    if(logoutflag==true && resultId==0)
                                    {
                                        jdialog.setVisible(false);
                                        jdialog.dispose();
                                        mainGUI.prof=null;
                                        mainGUI.attendanceSheet=null;
                                        mainGUI.stackPanel.clear();
                                        mainGUI.remove(mainGUI.currJPanel);
                                        mainGUI.currJPanel=new InitialProfessorAuthenticationGUI(mainGUI);
                                        mainGUI.add(mainGUI.currJPanel);
                                        mainGUI.repaint();
                                        mainGUI.validate();
                                        Serialconnector.LEDOFF();
                                        Serialconnector.close();
                                        return;
                                        
                                    }
                                    if(Submitflag==true && resultId==0)
                                    {
                                        System.out.println("Professor authenticated");
                                        jdialog.setVisible(false);
                                        jdialog.dispose();
                                        submitAttendanceSheet();
                                        break;
                                    }
                                    else if(resultId>0 && resultId<200)
                                    {
                                        mainGUI.attendanceSheet.StudentAttInfoList.get(resultId-1).Presence=true;
                                        System.out.println(mainGUI.attendanceSheet.StudentAttInfoList.get(resultId-1).FirstName+" is present");
                                        jlistStudent.ensureIndexIsVisible(resultId-1);
                                        jlistStudent.repaint();
                                        
                                    }
                                    else
                                        System.out.println("Wrong finger id");
                                    Thread.sleep(500);
                                    
                                }
                                
                                Serialconnector.LEDOFF();
                            }
                            
                            Serialconnector.close();
                        } catch (Exception ex) {
                            try{
                            Serialconnector.close();
                            }catch(Exception ee){}
                            ex.printStackTrace();
                            UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
                            JOptionPane.showMessageDialog(null, "<html>Communication with<br/>Fingerprint Scanner failed.<br/>Please retry.</html>");
                            
                            mainGUI.remove(mainGUI.currJPanel);
                            mainGUI.currJPanel=mainGUI.stackPanel.pop();
                            mainGUI.add(mainGUI.currJPanel);
                            mainGUI.repaint();
                            mainGUI.validate();
                            return;
                        } 
                    }    
                    catch(Exception e)
                    {
                            UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
                            JOptionPane.showMessageDialog(null, "<html>Connection with Server failed.<br/>Please retry.</html>");
                            mainGUI.remove(mainGUI.currJPanel);
                            mainGUI.currJPanel=mainGUI.stackPanel.pop();
                            mainGUI.add(mainGUI.currJPanel);
                            mainGUI.repaint();
                            mainGUI.validate();
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
            JOptionPane.showMessageDialog(null, "<html>Oops.<br/>Please retry.</html>");
            mainGUI.remove(mainGUI.currJPanel);
                            mainGUI.currJPanel=mainGUI.stackPanel.pop();
                            mainGUI.add(mainGUI.currJPanel);
                            mainGUI.repaint();
                            mainGUI.validate();                
            e.printStackTrace();
        }
    }
    public void submitAttendanceSheet()
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
                        BiometricClientServerCommands bcsCommands=new BiometricClientServerCommands();
                        outputToServer.writeInt(bcsCommands.STORE_ATTENDANCE_SHEET);
                        outputToServer.writeObject(mainGUI.attendanceSheet);
                        outputToServer.flush();
                        
                        int result=inputFromServer.readInt();
                        if(result==bcsCommands.STORE_ATTENDANCE_SUCCESSFUL)
                        {
                            mainGUI.attendanceSheet.AttendanceSheetId=inputFromServer.readInt();
                            mainGUI.remove(mainGUI.currJPanel);
                            mainGUI.currJPanel=new AttendanceSubmittedGUI(mainGUI);
                            mainGUI.add(mainGUI.currJPanel);
                            mainGUI.repaint();
                            mainGUI.validate();
                        }
                        else
                        {
                            UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
                            JOptionPane.showMessageDialog(null,"<html>Failed to Store Attendance.<br/>Please retry</html>");
                        }
                        
                    }    
                    catch(Exception e)
                    {
                            UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
                            JOptionPane.showMessageDialog(null,"<html>Failed to Store Attendance.<br/>Please retry</html>");
                        
                            e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch(Exception e)
        {
            UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
            JOptionPane.showMessageDialog(null,"<html>Failed to Store Attendance.<br/>Please retry</html>");
                        
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
        jLabel1 = new javax.swing.JLabel();
        jbtnBack = new javax.swing.JButton();
        jbtnLogout = new javax.swing.JButton();
        jbtnSubmit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jlblBatch = new javax.swing.JLabel();
        jlblSubject = new javax.swing.JLabel();
        jlblDate = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jlblNotification = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlistStudent = new javax.swing.JList<>();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
        setLayout(new java.awt.BorderLayout(10, 10));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Attendance Sheet");
        jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);

        jbtnBack.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jbtnBack.setMargin(new java.awt.Insets(15, 14, 15, 14));
        jbtnBack.setText("Back");
        jbtnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnBackActionPerformed(evt);
            }
        });
        jPanel1.add(jbtnBack, java.awt.BorderLayout.LINE_START);

        jbtnLogout.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jbtnLogout.setMargin(new java.awt.Insets(15, 14, 15, 14));
        jbtnLogout.setText("Logout");
        jbtnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnLogoutActionPerformed(evt);
            }
        });
        jPanel1.add(jbtnLogout, java.awt.BorderLayout.LINE_END);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jbtnSubmit.setFont(new java.awt.Font("Tahoma", 1, 32)); // NOI18N
        jbtnSubmit.setMargin(new java.awt.Insets(15, 14, 15, 14));
        jbtnSubmit.setText("Submit");
        jbtnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSubmitActionPerformed(evt);
            }
        });
        add(jbtnSubmit, java.awt.BorderLayout.PAGE_END);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
        jPanel2.setLayout(new java.awt.BorderLayout(10, 5));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.GridLayout(3, 1, 5, 5));

        jlblBatch.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jlblBatch.setText("Batch :");
        jPanel3.add(jlblBatch);

        jlblSubject.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jlblSubject.setText("subject :");
        jPanel3.add(jlblSubject);

        jlblDate.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jlblDate.setText("Date :");
        jPanel3.add(jlblDate);

        jPanel2.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
        jPanel4.setLayout(new java.awt.BorderLayout(5, 5));

        jlblNotification.setBackground(new java.awt.Color(255, 255, 255));
        jlblNotification.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jlblNotification.setForeground(new java.awt.Color(153, 0, 0));
        jlblNotification.setText("<html>Loading . . .<br/>Please wait . . .</html>");
        jPanel4.add(jlblNotification, java.awt.BorderLayout.NORTH);

        jlistStudent.setBackground(new java.awt.Color(255, 204, 204));
        jlistStudent.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jScrollPane1.setViewportView(jlistStudent);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSubmitActionPerformed
        // TODO add your handling code here:
        Submitflag=true;
        UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
        Object[] options = {"CANCEL"};
        jOptionpane= new JOptionPane("<html>Professor Authentication required<br/>to SUBMIT attendance.<br/>Please place your finger on<br/> biometrics?</html>",
                   JOptionPane.PLAIN_MESSAGE,
                   JOptionPane.CANCEL_OPTION,
                   null,
                   options,
                   options[0]);
        jdialog = jOptionpane.createDialog("Message");
        jdialog.setVisible(true);

            Submitflag=false;
           
    }//GEN-LAST:event_jbtnSubmitActionPerformed

    private void jbtnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBackActionPerformed
        // TODO add your handling code here:
        backflag=true;
        UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
        Object[] options = {"CANCEL"};
        jOptionpane= new JOptionPane("<html>Professor Authentication required<br/>to go BACK.<br/>Please place your finger on<br/> biometrics?</html>",
                   JOptionPane.PLAIN_MESSAGE,
                   JOptionPane.CANCEL_OPTION,
                   null,
                   options,
                   options[0]);
        jdialog = jOptionpane.createDialog("Message");
        jdialog.setVisible(true);

        backflag=false;
        
        
    }//GEN-LAST:event_jbtnBackActionPerformed
    
    private void jbtnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnLogoutActionPerformed
        // TODO add your handling code here:
        logoutflag=true;
        UIManager.put("OptionPane.messageFont",new FontUIResource(new Font("Tahoma", Font.PLAIN, 24)));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Tahoma",Font.PLAIN,24)));
        Object[] options = {"CANCEL"};
        jOptionpane= new JOptionPane("<html>Professor Authentication required<br/> to complete LOGOUT.<br/>Please place your finger on<br/> biometrics?</html>",
                   JOptionPane.PLAIN_MESSAGE,
                   JOptionPane.CANCEL_OPTION,
                   null,
                   options,
                   options[0]);
        jdialog = jOptionpane.createDialog("Message");
        jdialog.setVisible(true);

        logoutflag=false;
    }//GEN-LAST:event_jbtnLogoutActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnBack;
    private javax.swing.JButton jbtnLogout;
    private javax.swing.JButton jbtnSubmit;
    private javax.swing.JLabel jlblBatch;
    private javax.swing.JLabel jlblDate;
    private javax.swing.JLabel jlblNotification;
    private javax.swing.JLabel jlblSubject;
    private javax.swing.JList<String> jlistStudent;
    // End of variables declaration//GEN-END:variables
}

