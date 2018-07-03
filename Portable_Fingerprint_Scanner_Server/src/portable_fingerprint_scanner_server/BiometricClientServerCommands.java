/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portable_fingerprint_scanner_server;

/**
 *
 * @author admin
 */
public class BiometricClientServerCommands {
    public final int LOAD_PROFESSOR_FINGERPRINT=1;
    public final int LOAD_PROFESSOR_FINGERPRINT_SUCCESSFUL=2;
    public final int PROFESSOR_NOT_FOUND=3;
    public final int LOAD_BATCH_SUBJECT_TAUGHT_BY_PROFESSOR=4;
    public final int LOAD_STUDENT_BATCH_LIST=5;
    public final int STORE_ATTENDANCE_SHEET=6;
    public final int STORE_ATTENDANCE_SUCCESSFUL=7;
    public final int ATTENDANCE_SHEET_CREATION_FAILED=8;
    public final int STORE_ATTENDANCE_FAILED=9;
}
