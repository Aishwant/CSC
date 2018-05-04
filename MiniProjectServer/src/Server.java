//Aishwant Ghimire

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {

	public static void main(String args[]) throws Exception {
		
		String username;
		String password;
		DataInputStream fromClient;
		DataOutputStream outToClient;
		Socket connectionSocket;
		byte[] buffer = new byte[1024];
		int bytesInBuffer;
		String message= null;
		ServerSocket welcomeSocket= new ServerSocket(6789);
		
		while(true) {
			
			// wait on welcoming socket accept() method for client contact to create
			// return new socket connecting to client
			
			System.out.println("Connecting.. to server");
			connectionSocket = welcomeSocket.accept();
			
			BufferedReader fromClientKey = new BufferedReader(new InputStreamReader(System.in));
			
			fromClient = new DataInputStream(connectionSocket.getInputStream());
			
			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			//username = fromClient.readLine();
			bytesInBuffer = fromClient.readInt();
			fromClient.read(buffer,0,bytesInBuffer);
			username = new String(buffer, 0, bytesInBuffer);
			
			bytesInBuffer = fromClient.readInt();
			fromClient.read(buffer,0,bytesInBuffer);
			password = new String(buffer, 0, bytesInBuffer);
			
			String ans = "y";
			if(username.equalsIgnoreCase("TestUser") && password.equalsIgnoreCase("helloworld")) {
				
				message = "Login Succesful";	
				outToClient.writeInt(message.length());
				outToClient.write(message.getBytes(), 0, message.length());

				Random rand = new Random();
				
				//User is assigned with a random String to help authenticate, secure connection and prevent from login in every time
				String userAuthenticator = rand.nextInt(50)*1000 + "UA";
				
				outToClient.writeInt(userAuthenticator.length());
				outToClient.write(userAuthenticator.getBytes(), 0, userAuthenticator.length());
				
				String AuthenticatorGot = userAuthenticator;
				
				File dir = new File("."); //Current directory
				String dirName = "";
				boolean dirNameAdd = false;
				
				while (AuthenticatorGot.equalsIgnoreCase(userAuthenticator)) {
					
					bytesInBuffer = fromClient.readInt();
					fromClient.read(buffer,0,bytesInBuffer);
					ans = new String(buffer,0, bytesInBuffer);
						
					if (!ans.equalsIgnoreCase("n")) { //when its not a logout option
						
						if(ans.equals("1")) {
							
							//read the file name
							bytesInBuffer = fromClient.readInt();
							fromClient.read(buffer,0,bytesInBuffer);
							String filename = new String(buffer,0, bytesInBuffer);
							
							FileOutputStream fos = null;
							DataOutputStream outToFile = null;
							
							try {
								
								if (!dirName.equals("") && !dirNameAdd)
									dirName = dirName+"/";
								
								fos = new FileOutputStream(dirName+filename);
								outToFile = new DataOutputStream(fos);
							}catch(Exception e) {
								System.out.println(e);
							}
							
							while((bytesInBuffer = fromClient.readInt()) > 0) {
								fromClient.read(buffer, 0, bytesInBuffer);
								outToFile.write(buffer,0,bytesInBuffer);
							}
							fos.close();
							outToFile.close();
							
						}else if(ans.equals("2")) {
							
							FileInputStream fis = null;
							DataInputStream inFromFile = null;
							
							bytesInBuffer = fromClient.readInt();
							fromClient.read(buffer, 0, bytesInBuffer);
							String filename = new String(buffer,0,bytesInBuffer);
							
							try {
								if (!dirName.equals("") && !dirNameAdd)
									dirName = dirName+"/";
								
								fis = new FileInputStream(dirName+filename);
								inFromFile = new DataInputStream(fis);
							}catch(Exception e){
								System.out.println(e);
							}
							
							while((bytesInBuffer = inFromFile.read(buffer)) >0) {
								outToClient.writeInt(bytesInBuffer);
								outToClient.write(buffer,0, bytesInBuffer);
							}
							
							outToClient.writeInt(0); //indicate end of line
							
							fis.close();
							inFromFile.close();
						}else if(ans.equals("3")) {
						
							String direct = "Working Directory = " + dir.getAbsolutePath();
							outToClient.writeInt(direct.length());
							outToClient.write(direct.getBytes(),0,direct.length());
							
							if (!dirName.equals(""))
								dir = new File(dirName);
							else 
								dir = new File(".");	
							
							File[] fileList = dir.listFiles();
							
							for(File file : fileList) {

								outToClient.writeInt(file.getName().length());
								outToClient.write(file.getName().getBytes(),0,file.getName().length());

							}
							
							outToClient.writeInt(0);
						
						}else if(ans.equals("4")){

							bytesInBuffer=fromClient.readInt();
							fromClient.read(buffer,0,bytesInBuffer);
							String dirNameClient = new String(buffer,0,bytesInBuffer);
							
							if (dirNameClient.equalsIgnoreCase("home"))
								dirName="";
							else { 
								if(!dirName.equals("")) dirName = dirName+dirNameClient;
								else dirName=dirNameClient;
							}
								
							//System.out.println("Current Directory: "+ dir.getAbsolutePath());
							System.setProperty("user.dir", dirName);
							//System.out.println("Current Directory: "+ dir.getAbsolutePath());
							
	
						}
					}else { //user chosen logout option
						
						userAuthenticator = "null";
						outToClient.writeInt(userAuthenticator.length());
						outToClient.write(userAuthenticator.getBytes(), 0, userAuthenticator.length());
						
					}
					
					bytesInBuffer = fromClient.readInt();
					fromClient.read(buffer,0,bytesInBuffer);
					AuthenticatorGot = new String(buffer,0, bytesInBuffer);
					
					outToClient.writeInt(userAuthenticator.length());
					outToClient.write(userAuthenticator.getBytes(), 0, userAuthenticator.length());
			
				}//closing while statement
			
			}else {
			
				message = "Couldn't connect to the server. Invalid Username or Password!";	
				outToClient.writeInt(message.length());
				outToClient.write(message.getBytes(), 0, message.length());
			
			}
			
			connectionSocket.close(); //close the connection
			
		}

	}
}


