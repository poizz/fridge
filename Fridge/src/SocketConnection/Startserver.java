package SocketConnection;

import RPIgetItem.BarcodeScannerListener;

public class Startserver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 BarcodeScannerListener.getInstance( );
		SocketServer server = new SocketServer(9000);

		new Thread(server).start();


	}

}

