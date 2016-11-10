package SocketConnection;

	
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import RPIgetItem.BarcodeScannerListener;
import RPIgetItem.JsonAPI;

	public class SocketServer implements Runnable{

	    protected int          serverPort   = 9000;
	    protected ServerSocket serverSocket = null;
	    protected boolean      isStopped    = false;
	    protected Thread       runningThread= null;

	    /**
	     * Constuctor wich sets the port and starts the BarcodescannerListener
	     * @param port
	     */
	    public SocketServer(int port){
	        this.serverPort = port;
	        BarcodeScannerListener.getInstance().startListener();
	    }

	    /* (non-Javadoc)
	     * @see java.lang.Runnable#run()
	     */
	    public void run(){
	        synchronized(this){
	            this.runningThread = Thread.currentThread();
	        }
	        openServerSocket();
	        while(! isStopped()){
	            Socket clientSocket = null;
	            try {
	                clientSocket = this.serverSocket.accept();
	            } catch (IOException e) {
	                if(isStopped()) {
	                    System.out.println("Server Stopped.") ;
	                    return;
	                }
	                throw new RuntimeException(
	                    "Error accepting client connection", e);
	            }
	            try {
					new Thread(
					    new ClientConection(
					        clientSocket, "Multithreaded Server")
					).start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        System.out.println("Server Stopped.") ;
	    }


	    /**
	     * returns the status of the SocketConnectionServer
	     * @return object of the class SocketServer
	     */
	    private synchronized boolean isStopped() {
	        return this.isStopped;
	    }

	    /**
	     *Stops the SocketConnectionServer
	     */
	    public synchronized void stop(){
	        this.isStopped = true;
	        try {
	            this.serverSocket.close();
	        } catch (IOException e) {
	            throw new RuntimeException("Error closing server", e);
	        }
	    }

	    /**
	     * Opens Socket of the SocketConnection 
	     */
	    private void openServerSocket() {
	        try {
	            this.serverSocket = new ServerSocket(this.serverPort);
	        } catch (IOException e) {
	            throw new RuntimeException("Cannot open port 8080", e);
	        }
	    }

	
}
