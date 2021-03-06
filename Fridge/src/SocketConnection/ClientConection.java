package SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**

 */
public class ClientConection implements Runnable {


	protected Socket clientSocket = null;

	protected String serverText = null;

	PrintWriter printWriter;

	BufferedReader bufferedReader;

	communicationProtocol cP = new communicationProtocol();

	PrintWriter out;

	/**
	 * Constructor 
	 * @param clientSocket
	 * @param serverText
	 * @throws IOException
	 */
	public ClientConection(Socket clientSocket, String serverText) throws IOException {
		this.clientSocket = clientSocket;
		this.serverText = serverText;
		
		bufferedReader = new java.io.BufferedReader(new InputStreamReader(System.in));

	}
	/**
	 * Sends a msg to Client
	 * @param msg
	 */
	public void sendMsg(String msg){
		out.println("hey client");
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try{
			String message = null;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader (clientSocket.getInputStream()));
			printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			
			
			
			out = new PrintWriter(clientSocket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        String inputLine, outputLine;
			
			
			
	        out.println("Connected to the server!");
	        
            while ((inputLine = in.readLine()) != null) {
            	System.out.println(inputLine);         	
            	sendMsgToClient(cP.processInput(inputLine));

            }
			clientSocket.close();
		}catch(IOException e){
			
		}
		 catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * sends a msg to the client
	 * @param msg
	 */
	public void sendMsgToClient(String msg)
	{
		out.println(msg);
	}
}