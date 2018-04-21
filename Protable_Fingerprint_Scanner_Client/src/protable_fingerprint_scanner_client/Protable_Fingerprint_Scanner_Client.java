/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protable_fingerprint_scanner_client;

/**
 *
 * @author admin
 */



import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Protable_Fingerprint_Scanner_Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        FingerPrintScanner Serialconnector = new FingerPrintScanner();
    	BufferedReader br;
        try
        {
        	br = new BufferedReader(new InputStreamReader(System.in));
        	 
        	if(Serialconnector.open("COM8"))
        	{
        		while(true)
        		{
        			System.out.print("\n\nFingerPrint Example Program\n1.LED ON\n2.LED OFF\n3.Finger Pressed\n4.GetEnrollCount\n5.Enroll New Fingerprint\n6.Identity\n7.Delete All\n");
        			System.out.print("Enter Input:");
        			
        			int input = Integer.parseInt(br.readLine());
        			
        			switch(input)
        			{
        				case 1:
        					if(Serialconnector.LEDON())
                                                    System.out.println("LED is turned ON");
                                                else
                                                    System.out.println("Failed to turn LED ON");
    					break;
        				case 2:
        					if(Serialconnector.LEDOFF())
                                                    System.out.println("LED is turned OFF");
                                                else
                                                    System.out.println("Failed to turn LED OFF");
    					break;
        				case 3:
        					System.out.println("Finger Pressed:" + Serialconnector.IsPressFinger());
        					break;
        				case 4:
        					System.out.println("Enroll Count:" + Serialconnector.GetEnrollCount());
        					break;
        				case 5:
        					System.out.println("*******EnrollMent*******\nEnter Fingerprint ID:");
        					int enrollid  = Integer.parseInt(br.readLine());
        					if(enrollid==-1)
                                                    Serialconnector.newEnrollmentWithoutSaving();
        					else
                                                    Serialconnector.newEnrollment(enrollid);
        					
        					
        					break;
        				case 6:
        					// Identify fingerprint test
                                                if(Serialconnector.GetEnrollCount()<1)
                                                {
                                                    System.out.println("No finger print is loaded into biometric to idenitfy");
                                                    break;
                                                }
                                                    
        					System.out.println("Please place your finger");
        					Boolean result= false;
                                                int loopcounter=0;
        					while(result==false)
        					{
        						Serialconnector.CaptureFinger(false);
        						int id = Serialconnector.identify1_N();
        						if (id <200)
        						{
        							System.out.println("Verified ID:" + id);
        							result =true;
        						}
        						else
        						{
        							System.out.println("Finger not found");
        							result =false;
        						}
                                                        loopcounter++;
                                                        if(loopcounter>100)
                                                            break;
        					}
        					break;
                                        case 7:
                                                if(Serialconnector.GetEnrollCount()<1)
                                                {
                                                    System.out.println("No finger print found in biometric which can be deleted");
                                                    break;
                                                }
                                                  
                                                System.out.println("Deleted All fingerprint from biometric device:" + Serialconnector.deleteAllFingerPrintFromDevice());
                                                break;
    					default:
    						System.out.println("Invaild Input");
    						break;
        			}
        		}
        	}
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
    
}
