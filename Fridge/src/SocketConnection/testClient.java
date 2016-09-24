package SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class testClient {
	
	static PrintWriter out;
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		String name = "jan";
		Socket socket = new Socket("localhost",9000);
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		BufferedReader bufferedReader = new java.io.BufferedReader(new InputStreamReader(System.in));
		String message = null;
		String fromUser;
		
		BufferedReader in;
		
		BufferedReader recBufRed = new BufferedReader(new InputStreamReader (socket.getInputStream()));
		
		out = new PrintWriter(socket.getOutputStream(), true);
	    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		  while ((message = in.readLine()) != null) {
			  
              System.out.println("Server: " + message);         
              fromUser = bufferedReader.readLine();
              
              if (fromUser != null) {
                  System.out.println("Client: " + fromUser);
                  sendMsgToServer(fromUser);
              }
          }
					
	}
	
	public static void sendMsgToServer(String msg)
	{
		out.println(msg);
	}

}
