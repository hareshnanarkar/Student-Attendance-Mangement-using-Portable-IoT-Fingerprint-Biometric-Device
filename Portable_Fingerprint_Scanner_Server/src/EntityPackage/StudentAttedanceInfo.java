/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EntityPackage;

import java.io.Serializable;

/**
 *
 * @author admin
 */
public class StudentAttedanceInfo implements Serializable{
    public int RollNo;
    public int StudId;
    public String FirstName;
    public String LastName;
    public boolean Presence;
    public byte[] FingerPrintTemplate;
}
