/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portable_fingerprint_scanner_client;

/**
 *
 * @author admin
 */

import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPortIdentifier;
import gnu.io.CommPort;
import gnu.io.SerialPort;
import java.io.FileOutputStream;

public class FingerPrintScanner {
    InputStream in;
    OutputStream out;
    CommPortIdentifier portIdentifier;
    CommPort commPort;
    SerialPort serialPort;
    
    byte[] command=new byte[2];
    byte[] parameter=new byte[4];
    private final char[] hexArray="0123456789ABCDEF".toCharArray();
    
    final byte COMMAND_START_CODE_1 = 0x55;         // Static byte to mark the beginning of a command packet    -never changes
    final byte COMMAND_START_CODE_2 = (byte)0xAA;   // Static byte to mark the beginning of a command packet	-never changes
    final byte COMMAND_DEVICE_ID_1 = 0x01;          // Device ID Byte 1 (lesser byte)				-theoretically never changes
    final byte COMMAND_DEVICE_ID_2 = 0x00;          // Device ID Byte 2 (greater byte)				-theoretically never changes
    
    final byte DATA_START_CODE_1 = 0x5A;         // Static byte to mark the beginning of a command packet    -never changes
    final byte DATA_START_CODE_2 = (byte)0xA5;   // Static byte to mark the beginning of a command packet	-never changes
    
    int error;
    Boolean ack;
    byte[] parameterBytes = new byte[4];
    byte[] responseBytes = new byte[2];
    byte[] rawBytes = new byte[12];
    
    public static class Commands
    {
	public static final short NotSet= 0x00,	// Default value for enum. Scanner will return error if sent this.
		Open			= 0x01,	// Open Initialization
		Close			= 0x02,	// Close Termination
		UsbInternalCheck	= 0x03,	// UsbInternalCheck Check if the connected USB device is valid
		ChangeEBaudRate		= 0x04,	// ChangeBaudrate Change UART baud rate
		SetIAPMode		= 0x05,	// SetIAPMode Enter IAP Mode In this mode, FW Upgrade is available
		CmosLed			= 0x12,	// CmosLed Control CMOS LED
		GetEnrollCount		= 0x20,	// Get enrolled fingerprint count
		CheckEnrolled		= 0x21,	// Check whether the specified ID is already enrolled
		EnrollStart		= 0x22,	// Start an enrollment
		Enroll1			= 0x23,	// Make 1st template for an enrollment
		Enroll2			= 0x24,	// Make 2nd template for an enrollment
		Enroll3			= 0x25,	// Make 3rd template for an enrollment, merge three templates into one template, save merged template to the database
		IsPressFinger		= 0x26,	// Check if a finger is placed on the sensor
		DeleteID		= 0x40,	// Delete the fingerprint with the specified ID
		DeleteAll		= 0x41,	// Delete all fingerprints from the database
		Verify1_1		= 0x50,	// Verification of the capture fingerprint image with the specified ID
		Identify1_N		= 0x51,	// Identification of the capture fingerprint image with the database
		VerifyTemplate1_1	= 0x52,	// Verification of a fingerprint template with the specified ID
		IdentifyTemplate1_N	= 0x53,	// Identification of a fingerprint template with the database
		CaptureFinger		= 0x60,	// Capture a fingerprint image(256x256) from the sensor
		MakeTemplate		= 0x61,	// Make template for transmission
		GetImage		= 0x62,	// Download the captured fingerprint image(256x256)
		GetRawImage		= 0x63,	// Capture & Download raw fingerprint image(320x240)
		GetTemplate		= 0x70,	// Download the template of the specified ID
		SetTemplate		= 0x71,	// Upload the template of the specified ID
		GetDatabaseStart	= 0x72,	// Start database download, obsolete
		GetDatabaseEnd		= 0x73,	// End database download, obsolete
		UpgradeFirmware		= 0x80,	// Not supported
		UpgradeISOCDImage	= 0x81,	// Not supported
		Ack			= 0x30,	// Acknowledge.
		Nack			= 0x31;	// Non-acknowledge
	}
    
    public static class ErrorCodes
    {
	public static final int NO_ERROR    = 0x0000,	// Default value. no error
            NACK_TIMEOUT                    = 0x1001,	// Obsolete, capture timeout
            NACK_INVALID_BAUDRATE           = 0x1002,	// Obsolete, Invalid serial baud rate
            NACK_INVALID_POS                = 0x1003,	// The specified ID is not between 0~199
            NACK_IS_NOT_USED                = 0x1004,	// The specified ID is not used
            NACK_IS_ALREADY_USED            = 0x1005,	// The specified ID is already used
            NACK_COMM_ERR                   = 0x1006,	// Communication Error
            NACK_VERIFY_FAILED              = 0x1007,	// 1:1 Verification Failure
            NACK_IDENTIFY_FAILED            = 0x1008,	// 1:N Identification Failure
            NACK_DB_IS_FULL                 = 0x1009,	// The database is full
            NACK_DB_IS_EMPTY                = 0x100A,	// The database is empty
            NACK_TURN_ERR                   = 0x100B,	// Obsolete, Invalid order of the enrollment (The order was not as: EnrollStart -> Enroll1 -> Enroll2 -> Enroll3)
            NACK_BAD_FINGER                 = 0x100C,	// Too bad fingerprint
            NACK_ENROLL_FAILED              = 0x100D,	// Enrollment Failure
            NACK_IS_NOT_SUPPORTED           = 0x100E,	// The specified command is not supported
            NACK_DEV_ERR                    = 0x100F,	// Device Error, especially if Crypto-Chip is trouble
            NACK_CAPTURE_CANCELED           = 0x1010,	// Obsolete, The capturing is canceled
            NACK_INVALID_PARAM              = 0x1011,	// Invalid parameter
            NACK_FINGER_IS_NOT_PRESSED      = 0x1012,	// Finger is not pressed
            INVALID                         = 0XFFFF;	// Used when parsing fails
    }
    
    public Boolean open(String portName) throws Exception
    {
        portIdentifier=CommPortIdentifier.getPortIdentifier(portName);
        if(portIdentifier.isCurrentlyOwned())
        {
            System.out.println("Port is currently in used by another process");
            return false;
        }
        else
        {
            commPort=portIdentifier.open(this.getClass().getName(),2000);
            if(commPort instanceof SerialPort)
            {
                serialPort=(SerialPort)commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                
                in=serialPort.getInputStream();
                out=serialPort.getOutputStream();
                
                
                parameter = new byte[]{0x00,0x00,0x00,0x00};
		sendRequest(Commands.Open);
		byte[] res = getResponse(0);
                
                return true;
            }
            else
            {
                System.out.println("Error: Given port is not a serial port.");
                return false;
            }
        }
        
    }
    public void close()
    {
        if(commPort!=null)
	{
            commPort.close();
            commPort = null;
	}
		
	if(portIdentifier!=null)
            portIdentifier=null;
    }
    
    public Boolean LEDON() throws Exception
    {
	parameter = new byte[]{0x01,0x00,0x00,0x00};
	sendRequest(Commands.CmosLed);
	byte[] res = getResponse(0);
	return ack;
    }
	
    public Boolean LEDOFF() throws Exception
    {
        parameter = new byte[]{0x00,0x00,0x00,0x00};
	sendRequest(Commands.CmosLed);
	byte[] res = getResponse(0);
	return ack;
	}
    
    public int GetEnrollCount() throws Exception
    {
	parameter = new byte[]{0x00,0x00,0x00,0x00};
	sendRequest(Commands.GetEnrollCount);
	byte[] res = getResponse(0);
	return intFromParameter();
    }
    
    Boolean CheckEnrolled(int id) throws Exception
    {
        sendRequest(Commands.CheckEnrolled);
	setParameterFromInt(id);
	byte[] res = getResponse(0);
	Boolean retval = false;
	retval = ack;
	return retval;
    }
    
    public Boolean IsPressFinger() throws Exception
    {
	LEDON();
	parameter = new byte[]{0x00,0x00,0x00,0x00};
	sendRequest(Commands.IsPressFinger);
	byte[] res = getResponse(200);
	Boolean retval = false;
	int pval = parameterBytes[0];
	pval += parameterBytes[1];
	pval += parameterBytes[2];
	pval += parameterBytes[3];
	if (pval == 0)
            retval = true;
	return retval;
    }
    
    Boolean CaptureFinger(Boolean highquality) throws Exception
    {
	if (highquality)
            setParameterFromInt(1);
	else
            setParameterFromInt(0);

	sendRequest(Commands.CaptureFinger);
	byte[] res = getResponse(0);
	Boolean retval = ack;
	return retval;
    }
    
    public Boolean newEnrollment(int enrollid) throws Exception
    {
    	LEDON();
		
	Boolean usedid = true;
	while (usedid == true)
	{
            usedid = CheckEnrolled(enrollid);
            if (usedid==true)
            {
		enrollid++;
		System.out.println("Request Enroll ID# was already enrolled. So Assigning New Enroll Id #" + enrollid);
            }
	}
	enrollStart(enrollid);
	
        // enroll
        
	System.out.print("Press finger to Enroll #");
	System.out.println(enrollid);
	while(IsPressFinger() == false) 
            Thread.sleep(100);
	Boolean bret = CaptureFinger(true);
	int iret = 0;
	if (bret != false)
	{
            System.out.println("Remove finger");
            enroll1(); 
            while(IsPressFinger() == true) 
                Thread.sleep(100);
            System.out.println("Press same finger again");
            while(IsPressFinger() == false) 
                Thread.sleep(100);
            bret = CaptureFinger(true);
            if (bret != false)
            {
		System.out.println("Remove finger");
		enroll2();
		while(IsPressFinger() == true) 
                    Thread.sleep(10);
		System.out.println("Press same finger yet again");
		while(IsPressFinger() == false) 
                    Thread.sleep(10);
		bret = CaptureFinger(true);
		if (bret != false)
		{
                    System.out.println("Remove finger");
                    iret = enroll3();
                    if (iret == 0)
                    {
			System.out.println("Enrolling Successfull for ID #"+enrollid);
                        LEDOFF();
			return true;
                    }
                    else
                    {
                        System.out.println("Enrolling Failed with error code:");
			System.out.println(iret);
                        LEDOFF();
			return false;
                    }
		}
                else 
		{
                    System.out.println("Failed to capture third finger");
                        LEDOFF();
                    return false;
		}
            }
            else 
            {
		System.out.println("Failed to capture second finger");
                        LEDOFF();
		return false;
            }
	}
	else 
	{
            System.out.println("Failed to capture first finger");
                        LEDOFF();
            return false; 
	}
        
    }
    public byte[] newEnrollmentWithoutSaving() throws Exception
    {
        byte[] template=null;
    	LEDON();
	
	enrollStart(-1);
	
        // enroll
        
	System.out.print("Press finger to Enroll #");
	System.out.println(-1);
	while(IsPressFinger() == false) 
            Thread.sleep(100);
	Boolean bret = CaptureFinger(true);
	int iret = 0;
	if (bret != false)
	{
            System.out.println("Remove finger");
            enroll1(); 
            while(IsPressFinger() == true) 
                Thread.sleep(100);
            System.out.println("Press same finger again");
            while(IsPressFinger() == false) 
                Thread.sleep(100);
            bret = CaptureFinger(true);
            if (bret != false)
            {
		System.out.println("Remove finger");
		enroll2();
		while(IsPressFinger() == true) 
                    Thread.sleep(10);
		System.out.println("Press same finger yet again");
		while(IsPressFinger() == false) 
                    Thread.sleep(10);
		bret = CaptureFinger(true);
		if (bret != false)
		{
                    System.out.println("Remove finger");
                    iret = enroll3();
                    if (iret == 0)
                    {
			System.out.println("Enrolling Successfull for ID #"+ -1);
                        byte[] datapack=getDataPacket(2000);
                        template=getTemplateStoredInDataPacket(datapack);
                        for(int i=0;i<498;i++)
                            System.out.print(template[i]+"\t");
                        //FileOutputStream fout=new FileOutputStream("temp.dat");
                        //fout.write(template);
                        //System.out.println("data is written in file");
                        
                        
                    }
                    else
                    {
                        System.out.println("Enrolling Failed with error code:");
			System.out.println(iret);

                        
                    }
		}
                else 
		{
                    System.out.println("Failed to capture third finger");
                
                }
            }
            else 
            {
		System.out.println("Failed to capture second finger");
        
            }
	}
	else 
	{
            System.out.println("Failed to capture first finger");
        
        }
        LEDOFF();
        return template;
    }
    
    public int enrollStart(int id) throws Exception
    {
	setParameterFromInt(id);
	sendRequest(Commands.EnrollStart);
	byte[] res = getResponse(0);
	int retval = 0;
	if (ack == false)
	{
            if (error == ErrorCodes.NACK_DB_IS_FULL) retval = 1;
            if (error == ErrorCodes.NACK_INVALID_POS) retval = 2;
            if (error == ErrorCodes.NACK_IS_ALREADY_USED) retval = 3;
	}
	return retval;
    }
    
    public int enroll1() throws Exception
    {
        sendRequest(Commands.Enroll1);
	byte[] res = getResponse(0);
		
	int retval = intFromParameter();
	if (retval < 200) retval = 3; else retval = 0;
	if (ack == false)
	{
            if (error == ErrorCodes.NACK_ENROLL_FAILED) retval = 1;
            if (error == ErrorCodes.NACK_BAD_FINGER) retval = 2;
	}
	if (ack) return 0; else return retval;
    }
	
    public int enroll2() throws Exception
    {
	sendRequest(Commands.Enroll2);
	byte[] res = getResponse(0);
	
	int retval = intFromParameter();
	if (retval < 200) retval = 3; else retval = 0;
	if (ack == false)
	{
            if (error == ErrorCodes.NACK_ENROLL_FAILED) retval = 1;
            if (error == ErrorCodes.NACK_BAD_FINGER) retval = 2;
	}
	if (ack) return 0; else return retval;
    }
	
    public int enroll3() throws Exception
    {
	sendRequest(Commands.Enroll3);
	byte[] res = getResponse(0);
	
	int retval = intFromParameter();
	if (retval < 200) retval = 3; else retval = 0;
	if (ack == false)
	{
            if (error == ErrorCodes.NACK_ENROLL_FAILED) retval = 1;
            if (error == ErrorCodes.NACK_BAD_FINGER) retval = 2;
	}
	if (ack) return 0; else return retval;
    }
    public int identify1_N() throws Exception
	{
            if(IsPressFinger() != true )
                    return 200;
            CaptureFinger(true);
            sendRequest(Commands.Identify1_N);
            byte[] res = getResponse(100);
		
            int retval = intFromParameter();
		return retval;
	}
    public Boolean verify1_1(int id) throws Exception
    {
        LEDON();
        while(IsPressFinger() != true) 
            Thread.sleep(100);
        CaptureFinger(true);
        setParameterFromInt(id);
        sendRequest(Commands.Verify1_1);
        byte res[]=getResponse(0);
        int val=intFromParameter();
        Boolean result=ack;
        LEDOFF();
        return result;
        
    }
    public byte[] getTemplateStoredInDataPacket(byte[] datapacket)
    {
        byte[] template=new byte[498];
        int  i;
        for(i=0;i<498;i++)
        {
            template[i]=datapacket[i+4];
        }
        
        return template;
    }
    
    public Boolean deleteAllFingerPrintFromDevice() throws Exception
    {
	
	parameter = new byte[]{0x00,0x00,0x00,0x00};
	sendRequest(Commands.DeleteAll);
	byte[] res = getResponse(50);
	Boolean retval = false;
	int pval = parameterBytes[0];
	pval += parameterBytes[1];
	pval += parameterBytes[2];
	pval += parameterBytes[3];
	if (pval == 0)
            retval = true;
	return retval;
    }
    
    
    public Boolean setTemplate(int id,byte template[]) throws Exception
    {
        setParameterFromInt(id);
        sendRequest(Commands.SetTemplate);
        byte[] res=getResponse(10);
        int result=intFromParameter();
        if(ack==true)
        {
            System.err.println(template.length);
            sendTemplateDataPacket(template);
            res=getResponse(50);
            result=intFromParameter();
            if(ack==true)
            {
                System.out.println("Successsssssssssssss");
            }
        }
        return false;
    }
    public void sendTemplateDataPacket(byte template[])throws Exception
    {
        purgeResponse();
        int checksum=0;
        checksum+= DATA_START_CODE_1&0xff;
	checksum+= DATA_START_CODE_2&0xff;
	checksum+= COMMAND_DEVICE_ID_1&0xff;
	checksum+= COMMAND_DEVICE_ID_2&0xff;
        
        byte datapacket[]=new byte[504];
        datapacket[0]=DATA_START_CODE_1;
        datapacket[1]=DATA_START_CODE_2;
        datapacket[2]=COMMAND_DEVICE_ID_1;
        datapacket[3]=COMMAND_DEVICE_ID_2;
        int i;
        for(i=4;i<498+4;i++)
        {
            datapacket[i]=template[i-4];
            
            checksum+= template[i-4]&0xff;
        }
        //System.err.println(i);
        datapacket[i]=getLowByte(checksum);
        i++;
        datapacket[i]=getHighByte(checksum);
        //System.out.println("hiii "+ datapacket[503]+""+datapacket[502]);
        this.out.write(datapacket);
	this.out.flush();
        
	
        
    }
    //*************************************************
    //--------------------------------
    //--------------------------------
    
    //From here on all functions are helper functions 
    
    //--------------------------------
    //--------------------------------
    //*************************************************
    
    public void sendRequest(short cmd) throws Exception
    {
	purgeResponse();
        
	this.out.write(getPacketBytes(cmd));
	//this.out.write("\n".getBytes());
        this.out.flush();
	}
    byte[] getResponse(int timeout) throws Exception
    {
	byte firstbyte = 0;
	Boolean done = false;
		
//	if(timeout==0)
//            Thread.sleep(200);
//	else
//            Thread.sleep(timeout);
	
	while (done == false)
	{
            firstbyte = (byte)this.in.read();
            if (firstbyte == COMMAND_START_CODE_1)
            {
                done = true;
            }
	}
        
        byte[] resp = new byte[12];
        resp[0] = firstbyte;
        for (int i=1; i < 12; i++)
        {
            while (this.in.available() == 0) Thread.sleep(5);
		resp[i]= (byte) this.in.read();
            }
        
	return formResponsePacket(resp);
    }
    byte[] getDataPacket(int timeout) throws Exception
    {
	byte firstbyte = 0;
	Boolean done = false;
		
	if(timeout==0)
            Thread.sleep(200);
	else
            Thread.sleep(timeout);
		
	while (done == false)
	{
            firstbyte = (byte)this.in.read();
            if (firstbyte == DATA_START_CODE_1)
            {
                done = true;
            }
	}
        byte[] resp = new byte[504];
        resp[0] = firstbyte;
        for (int i=1; i < 503; i++)
        {
            while (this.in.available() == 0) Thread.sleep(5);
		resp[i]= (byte) this.in.read();
                //System.out.println("i :"+i);
            }
	return resp;
    }
    
    void purgeResponse()
    {
        byte[] temp;
	try
	{
            int c = this.in.available();
            temp = new byte[c];
            this.in.read(temp);
            this.in.reset();	
	}
	catch(Exception exp)
	{
	
	}
	finally
	{
            temp = null;
	}
    }
    
    byte[] getPacketBytes(int cmd)
    {
	byte[] packetbytes= new byte[12];

	command[0] = getLowByte(cmd);
	command[1] = getHighByte(cmd);

		
	packetbytes[0] = COMMAND_START_CODE_1;
	packetbytes[1] = COMMAND_START_CODE_2;
	packetbytes[2] = COMMAND_DEVICE_ID_1;
	packetbytes[3] = COMMAND_DEVICE_ID_2;
	packetbytes[4] = parameter[0];
	packetbytes[5] = parameter[1];
	packetbytes[6] = parameter[2];
	packetbytes[7] = parameter[3];
	packetbytes[8] = command[0];
	packetbytes[9] = command[1];
	
	int checksum = calculateRequestPacketChecksum();
	packetbytes[10] = getLowByte(checksum);
	packetbytes[11] = getHighByte(checksum);
			
	return packetbytes;
    } 
    byte[] formResponsePacket(byte[] buffer)
    {
        System.out.println(buffer[8]);
	if (buffer[8] == 0x30)
            ack = true; 
	else 
            ack = false;

	int checksum = calculateResponseChecksum(buffer, 10);
	byte checksum_low = getLowByte(checksum);
	byte checksum_high = getHighByte(checksum);

	error = parseResponseErrorCodeFromBytes(buffer[5], buffer[4]);
	parameterBytes[0] = buffer[4];
        parameterBytes[1] = buffer[5];
	parameterBytes[2] = buffer[6];
	parameterBytes[3] = buffer[7];
	responseBytes[0]=buffer[8];
	responseBytes[1]=buffer[9];
	for (int i=0; i < 12; i++)
	{
            rawBytes[i]=buffer[i];
	}
	return rawBytes;
    }
    byte getHighByte(int w)
    {
	//System.out.println("High Input:" +w);
	//System.out.println("High Ouput:" + (byte) ((byte)(w>>8)&0x00FF));
	return (byte) ((byte)(w>>8)&0x00FF);
    }

    // Returns the low byte from a word
    byte getLowByte(int w)
    {
    	//System.out.println("Low Input:" + w);
	//System.out.println("Low Ouput:" + (byte) ((byte)w&0x00FF));
	return (byte) ((byte)w&0x00FF);
    }
    int calculateRequestPacketChecksum()
    {
	int w = 0;
	w += COMMAND_START_CODE_1&0xff;
	w += COMMAND_START_CODE_2&0xff;
	w += COMMAND_DEVICE_ID_1&0xff;
	w += COMMAND_DEVICE_ID_2&0xff;
	w += parameter[0]&0xff;
	w += parameter[1]&0xff;
	w += parameter[2]&0xff;
	w += parameter[3]&0xff;
	w += command[0]&0xff;
	w += command[1]&0xff;

	return w;
    }
    int calculateResponseChecksum(byte[] buffer, int length)
    {
	int checksum = 0;
	for (int i=0; i<length; i++)
	{
            checksum +=buffer[i]&0xff;
	}
	return checksum;
    }
    int parseResponseErrorCodeFromBytes(byte high, byte low)
    {
	int e = ErrorCodes.INVALID;
	if (high == 0x00)
	{
		
	}
	else {
            switch(low)
            {
		case 0x00: e = ErrorCodes.NO_ERROR; break;
		case 0x01: e = ErrorCodes.NACK_TIMEOUT; break;
		case 0x02: e = ErrorCodes.NACK_INVALID_BAUDRATE; break;
		case 0x03: e = ErrorCodes.NACK_INVALID_POS; break;
		case 0x04: e = ErrorCodes.NACK_IS_NOT_USED; break;
		case 0x05: e = ErrorCodes.NACK_IS_ALREADY_USED; break;
		case 0x06: e = ErrorCodes.NACK_COMM_ERR; break;
		case 0x07: e = ErrorCodes.NACK_VERIFY_FAILED; break;
		case 0x08: e = ErrorCodes.NACK_IDENTIFY_FAILED; break;
		case 0x09: e = ErrorCodes.NACK_DB_IS_FULL; break;
		case 0x0A: e = ErrorCodes.NACK_DB_IS_EMPTY; break;
		case 0x0B: e = ErrorCodes.NACK_TURN_ERR; break;
		case 0x0C: e = ErrorCodes.NACK_BAD_FINGER; break;
		case 0x0D: e = ErrorCodes.NACK_ENROLL_FAILED; break;
		case 0x0E: e = ErrorCodes.NACK_IS_NOT_SUPPORTED; break;
		case 0x0F: e = ErrorCodes.NACK_DEV_ERR; break;
		case 0x10: e = ErrorCodes.NACK_CAPTURE_CANCELED; break;
		case 0x11: e = ErrorCodes.NACK_INVALID_PARAM; break;
		case 0x12: e = ErrorCodes.NACK_FINGER_IS_NOT_PRESSED; break;
            }
	}
	return e;
    }
    
    void setParameterFromInt(int i)
    {
	parameter[0] = (byte) (i & 0x000000ff);
	parameter[1] = (byte) ((i & 0x0000ff00) >> 8);
	parameter[2] = (byte) ((i & 0x00ff0000) >> 16);
	parameter[3] = (byte) ((i & 0xff000000) >> 24);
    }
    
    int intFromParameter()
    {
	int retval = 0;
	retval = (retval << 8) + parameterBytes[3];
	retval = (retval << 8) + parameterBytes[2];
	retval = (retval << 8) + parameterBytes[1];
	retval = (retval << 8) + parameterBytes[0];
	return retval;
    }
    int CalculateChecksum(byte[] buffer, int length)
	{
		int checksum = 0;
		for (int i=0; i<length; i++)
		{
			checksum +=buffer[i]&0xff;
		}
		return checksum;
	}
}
