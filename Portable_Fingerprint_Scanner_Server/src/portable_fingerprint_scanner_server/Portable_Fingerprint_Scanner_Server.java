/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portable_fingerprint_scanner_server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author admin
 */
public class Portable_Fingerprint_Scanner_Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try
        {
        ServerSocket serverSock=new ServerSocket(9899);
        while(true)
        {
            try {
                
               
                Socket clientSock=serverSock.accept();
                Thread t=new Thread(new ServerHelperThread(clientSock));
                t.start();
                System.out.println("Connection established with a new client");
            }
            catch(Exception e)
            {
                System.out.println("Failed to establish connection with a new client");
                e.printStackTrace();
            }
        }
        }
        catch(Exception e)
        {
            System.out.print("Server Failed to Start\n");
        }
    }
    
}
