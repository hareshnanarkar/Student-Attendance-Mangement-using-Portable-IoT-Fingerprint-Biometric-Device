/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EntityPackage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class AttendanceSheet implements Serializable{
    public int AttendanceSheetId;
    public String AttendanceDate;
    public int ProfId;
    public int BatchId;
    public String BatchName;
    public int SubId;
    public String SubName;
    public ArrayList<StudentAttedanceInfo> StudentAttInfoList;
    
}
