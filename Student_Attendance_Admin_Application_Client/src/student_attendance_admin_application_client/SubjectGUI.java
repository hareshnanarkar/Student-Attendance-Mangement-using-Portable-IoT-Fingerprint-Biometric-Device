/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student_attendance_admin_application_client;

import EntityPackage.Subject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author admin
 */
public class SubjectGUI extends javax.swing.JPanel {

    /**
     * Creates new form SubjectGUI
     */
    ArrayList<Subject> arrSubjectList;
    public SubjectGUI() {
        initComponents();
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        jtxtSubjectId.setEditable(false);
        jtxtSubjectName.setEditable(false);
        jtxtSubjectAbbr.setEditable(false);
        jchkIsPractical.setEnabled(false);
        jbtnSave.setEnabled(false);
        jbtnDeleteCancel.setEnabled(false);
        loadSubjectList();
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
                        DefaultListModel<String> listModel=new DefaultListModel<>();
                        for(int i=0;i<arrSubjectList.size();i++)
                        {
                            listModel.addElement(arrSubjectList.get(i).SubjectName);
                        }
                        jlistSubject.setModel(listModel);
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlistSubject = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxtSubjectId = new javax.swing.JTextField();
        jtxtSubjectName = new javax.swing.JTextField();
        jtxtSubjectAbbr = new javax.swing.JTextField();
        jbtnCreate = new javax.swing.JButton();
        jbtnDeleteCancel = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();
        jchkIsPractical = new javax.swing.JCheckBox();

        jLabel1.setText("Add, Remove or Update Subject");

        jlistSubject.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlistSubjectValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jlistSubject);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Details :");

        jLabel3.setText("Subject Id :");

        jLabel4.setText("Subject Name :");

        jLabel5.setText("Subject Abbreviation :");

        jbtnCreate.setText("Create New Subject");
        jbtnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateActionPerformed(evt);
            }
        });

        jbtnDeleteCancel.setText("Delete");
        jbtnDeleteCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteCancelActionPerformed(evt);
            }
        });

        jbtnSave.setText("Save Changes");
        jbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveActionPerformed(evt);
            }
        });

        jchkIsPractical.setText("Is Practical Subject?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(245, 245, 245)
                        .addComponent(jbtnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(26, 26, 26)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5))
                                        .addGap(42, 42, 42))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jbtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(21, 21, 21)
                                        .addComponent(jbtnDeleteCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jchkIsPractical)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jtxtSubjectId)
                                        .addComponent(jtxtSubjectName)
                                        .addComponent(jtxtSubjectAbbr, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbtnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtxtSubjectId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jtxtSubjectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jtxtSubjectAbbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jchkIsPractical)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnDeleteCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jlistSubjectValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlistSubjectValueChanged
        // TODO add your handling code here:
        if(jlistSubject.getSelectedIndex()!=-1)
        {
            jtxtSubjectId.setText(""+ arrSubjectList.get(jlistSubject.getSelectedIndex()).SubjectId);
            jtxtSubjectName.setText(arrSubjectList.get(jlistSubject.getSelectedIndex()).SubjectName);
            jtxtSubjectAbbr.setText(arrSubjectList.get(jlistSubject.getSelectedIndex()).SubjectAbbr);
            System.out.println(arrSubjectList.get(jlistSubject.getSelectedIndex()).IsPractical);
            jchkIsPractical.setSelected(arrSubjectList.get(jlistSubject.getSelectedIndex()).IsPractical);
            jtxtSubjectId.setEditable(false);
            jtxtSubjectName.setEditable(true);
            jtxtSubjectAbbr.setEditable(true);
            jchkIsPractical.setEnabled(true);
            jbtnSave.setEnabled(true);
            jbtnDeleteCancel.setEnabled(true);
            jbtnSave.setText("Save Changes");
            jbtnDeleteCancel.setText("Delete");
        }
    }//GEN-LAST:event_jlistSubjectValueChanged

    private void jbtnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateActionPerformed
        // TODO add your handling code here:
        jlistSubject.clearSelection();
        jtxtSubjectId.setText("ID will be auto-generated");
        jtxtSubjectName.setText("");
        jtxtSubjectAbbr.setText("");
        jchkIsPractical.setSelected(false);
        jtxtSubjectId.setEditable(false);
        jtxtSubjectName.setEditable(true);
        jtxtSubjectAbbr.setEditable(true);
        jchkIsPractical.setEnabled(true);
        jbtnSave.setEnabled(true);
        jbtnDeleteCancel.setEnabled(true);
        jbtnSave.setText("Save");
        jbtnDeleteCancel.setText("Cancel");
    }//GEN-LAST:event_jbtnCreateActionPerformed

    private void jbtnDeleteCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteCancelActionPerformed
        // TODO add your handling code here:

        if(jbtnDeleteCancel.getText().equals("Delete"))
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
                            outputToServer.writeUTF("DELETE_SUBJECT_REQUEST");

                            outputToServer.writeInt(Integer.parseInt(jtxtSubjectId.getText()));
                            outputToServer.flush();
                            String result=inputFromServer.readUTF();
                            System.out.println(result);
                            if(result.equals("DELETE_SUBJECT_SUCCESSFUL"))
                            {
                                loadSubjectList();
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
    }//GEN-LAST:event_jbtnDeleteCancelActionPerformed

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed
        // TODO add your handling code here:
        if(jbtnSave.getText().equals("Save Changes"))
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
                            outputToServer.writeUTF("UPDATE_SUBJECT_REQUEST");
                            Subject sub=new Subject();
                            sub.SubjectId=Integer.parseInt(jtxtSubjectId.getText());
                            sub.SubjectName=jtxtSubjectName.getText();
                            sub.SubjectAbbr=jtxtSubjectAbbr.getText();
                            sub.IsPractical=jchkIsPractical.isSelected();
                            outputToServer.writeObject(sub);
                            outputToServer.flush();
                            String result=inputFromServer.readUTF();
                            System.out.println(result);
                            if(result.equals("UPDATE_SUBJECT_SUCCESSFUL"))
                            {
                                loadSubjectList();
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
                            outputToServer.writeUTF("CREATE_SUBJECT_REQUEST");
                            Subject sub=new Subject();

                            sub.SubjectName=jtxtSubjectName.getText();
                            sub.SubjectAbbr=jtxtSubjectAbbr.getText();
                            sub.IsPractical=jchkIsPractical.isSelected();
                            outputToServer.writeObject(sub);
                            outputToServer.flush();
                            String result=inputFromServer.readUTF();
                            System.out.println(result);
                            if(result.equals("CREATE_SUBJECT_SUCCESSFUL"))
                            {
                                sub.SubjectId=inputFromServer.readInt();
                                System.out.println(sub.SubjectId);
                                loadSubjectList();
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
    }//GEN-LAST:event_jbtnSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnCreate;
    private javax.swing.JButton jbtnDeleteCancel;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JCheckBox jchkIsPractical;
    private javax.swing.JList<String> jlistSubject;
    private javax.swing.JTextField jtxtSubjectAbbr;
    private javax.swing.JTextField jtxtSubjectId;
    private javax.swing.JTextField jtxtSubjectName;
    // End of variables declaration//GEN-END:variables
}
