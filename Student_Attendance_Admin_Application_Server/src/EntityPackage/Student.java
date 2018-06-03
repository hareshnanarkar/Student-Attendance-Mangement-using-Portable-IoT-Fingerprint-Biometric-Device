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
public class Student implements Serializable {
    public int StudId;
    public String FirstName;
    public String MiddleName;
    public String LastName;
    public String StudUserId;
    public String StudPassword;
    public String EmailId;
    public byte[] FingerTemplate;
    public int DeptId;
    
    
}
