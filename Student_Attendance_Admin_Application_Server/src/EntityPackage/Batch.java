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
public class Batch implements Serializable{
    public int BatchId;
    public String BatchName;
    public int Semester;
    public int AcademicYear;
    public int BatchYear;
    public boolean IsActive;
    public Course course;
    
    
    
}
