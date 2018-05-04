//Aishwant Ghimire

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

	public static void main(String args[]) throws Exception{
		
		String username;
		String password;
		byte[] buffer = new byte[1024];
		int bytesInBuffer; 
		
		Socket connectToServer = new Socket("localhost",6789);
		
		BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
		
		//To Send data to Server
		DataOutputStream dataToServer = new DataOutputStream(connectToServer.getOutputStream());
		
		System.out.print("Username: ");
		username = read.readLine();
		
		System.out.print("Password: ");
		password = read.readLine();
		
		dataToServer.writeInt(username.length());
		dataToServer.write(username.getBytes(), 0, username.length());
		
		dataToServer.writeInt(password.length());
		dataToServer.write(password.getBytes(), 0, password.length());
		
		DataInputStream fromServer = new DataInputStream(connectToServer.getInputStream());
		
		bytesInBuffer = fromServer.readInt();
		fromServer.read(buffer,0,bytesInBuffer);
		String message = new String(buffer, 0, bytesInBuffer);
		
		
		try {
			bytesInBuffer = fromServer.readInt();
			fromServer.read(buffer,0,bytesInBuffer);
			String connectionAuth = new String(buffer, 0, bytesInBuffer);
			
			System.out.println(message);
			
			while(!connectionAuth.equalsIgnoreCase("null")) {
				
				System.out.println("Choose from the following: ");
				System.out.println();
				System.out.println("\t 1. Upload");
				System.out.println("\t 2. Download");
				System.out.println("\t 3. list the files in the current Directory");
				System.out.println("\t 4. Change Directory");
				System.out.println("\t Logout (Press \"N\")");
				
				System.out.print("\t\t\t: ");
				String answer = read.readLine();
				
				System.out.println();
				System.out.println();
				
				dataToServer.writeInt(answer.length());
				dataToServer.write(answer.getBytes(), 0, answer.length());
				
				if(answer.equals("1")) {
					
					//get the filename from user
					System.out.print("\tEnter the file name: ");
					String filename = read.readLine();
					FileInputStream fis = null;
					DataInputStream inFromFile = null;
					
					try {
						fis = new FileInputStream(filename);
						inFromFile = new DataInputStream(fis);
					}catch(Exception e) {
						System.out.println(e);
					}
				
					//write the file name to server
					dataToServer.writeInt(filename.length());
					dataToServer.write(filename.getBytes(),0,filename.length());
					
					while((bytesInBuffer = inFromFile.read(buffer)) >0) {
						dataToServer.writeInt(bytesInBuffer);
						dataToServer.write(buffer,0, bytesInBuffer);
					}
					
					dataToServer.writeInt(0); //indicate end of line
					
					fis.close();
					inFromFile.close();
					
					System.out.println();
					
				}else if(answer.equals("2")){
					
					FileOutputStream fos = null;
					DataOutputStream outToFile = null;
					
					System.out.print("\tEnter the file name: ");
					String filename = read.readLine(); //getting the file name that the user wants to download from the server
					
					dataToServer.writeInt(filename.length());
					dataToServer.write(filename.getBytes(),0,filename.length());
					
					try {
						fos = new FileOutputStream(filename);
						outToFile = new DataOutputStream(fos);
					}catch(Exception e) {
						System.out.println(e);
					}
					
					while((bytesInBuffer = fromServer.readInt()) > 0) {
						fromServer.read(buffer, 0, bytesInBuffer);
						outToFile.write(buffer,0,bytesInBuffer);
					}
					fos.close();
					outToFile.close();
					
					System.out.println();
					
				}else if(answer.equals("3")) {
					
					bytesInBuffer=fromServer.readInt();
					fromServer.read(buffer, 0, bytesInBuffer);
					String direct = new String(buffer,0,bytesInBuffer);
					
					System.out.println("\t Current Directory: " + direct);
					
					while((bytesInBuffer=fromServer.readInt())>0) {
						fromServer.read(buffer, 0, bytesInBuffer);
						String dir = new String(buffer,0,bytesInBuffer);
						System.out.println(dir);
					}
					
					System.out.println();
				
				}else if(answer.equals("4")){
					
					System.out.print("\tChange directory to : \n\t Type: \n\t\t home (For the main directory) \n\t\t or choose the directory\n\t\t: ");
					String dir = read.readLine(); //getting the file name that the user wants to download from the server
					
					dataToServer.writeInt(dir.length());
					dataToServer.write(dir.getBytes(),0,dir.length());
					
					
				}
				
				dataToServer.writeInt(connectionAuth.length());
				dataToServer.write(connectionAuth.getBytes(), 0, connectionAuth.length());
				
				bytesInBuffer = fromServer.readInt();
				fromServer.read(buffer, 0, bytesInBuffer);
				connectionAuth = new String(buffer, 0, bytesInBuffer);
				
			}
			
		}catch(Exception e) {
			System.out.println(message);
		}
		
		
		
		connectToServer.close();

	}

//	public static void Upload() {
//		
//	}
}
