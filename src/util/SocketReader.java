package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javafx.scene.control.TextField;
import screens.TickerScreenLong;

public class SocketReader {

	private int port;
	protected Socket socketConnection = null;
	private BufferedWriter output = null;
	private BufferedReader input = null;
	private Thread socketReaderThread;
	private Thread setupThread;
	private ServerSocket serverSocket;
	private TextField textFieldTIme;
	private TextField textFieldPrice;
	private DatagramSocket serverUDPSocket;
	private DatagramPacket receivePacket;
	
	private byte[] receiveData = new byte[1024];
	
//	   DatagramSocket serverSocket = new DatagramSocket(6666);
//       byte[] receiveData = new byte[1024];
//       byte[] sendData = new byte[1024];
//       while(true)
//          {
//             DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//             serverSocket.receive(receivePacket);
//             String sentence = new String( receivePacket.getData());
//             System.out.println("RECEIVED: " + sentence);
//             InetAddress IPAddress = receivePacket.getAddress();
//             int port = receivePacket.getPort();
//             String capitalizedSentence = sentence.toUpperCase();
//             sendData = capitalizedSentence.getBytes();
//             DatagramPacket sendPacket =
//             new DatagramPacket(sendData, sendData.length, IPAddress, port);
//             serverSocket.send(sendPacket);
//          }
	

	public SocketReader(TickerScreenLong screen) throws IOException {
		super();
		this.textFieldPrice = screen.getTickerTOSFeed();
		this.textFieldTIme = screen.getTickerTOSTime();
		setupConnection();
		screen.setScreensServerSocket(serverUDPSocket); 
	}

	private void setupConnection() throws IOException  {
		//	serverSocket = new ServerSocket(33443);
			serverUDPSocket =  new DatagramSocket(0);
			port = serverUDPSocket.getLocalPort();
			serverUDPSocket.setReuseAddress(true);
			//serverUDPSocket.setSoTimeout(5000);						
	}

	public void connect() throws IOException {
		
		receivePacket = new DatagramPacket(receiveData, receiveData.length);		
		
	

		socketReaderThread = new SocketReaderThread();
		socketReaderThread.start();
		//socketReaderThread.setDaemon(true);

	}


	class SocketReaderThread extends Thread {

		@Override
		public void run() {

//			try {
//				wait();
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}

			try {
				if (!serverUDPSocket.isClosed()) {
					
					 
					while (true) {
						
						sleep(1);
						
						DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
						
						serverUDPSocket.receive(receivePacket);
						
						String line = new String(receivePacket.getData()); 
						String[] out = formatInputTOSfeed(line);
						onMessage(out);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					socketConnection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	public void onMessage(final String [] line) {
		javafx.application.Platform.runLater(new Runnable() {
			@Override
			public void run() {
				textFieldPrice.setText(line[1]);
				textFieldTIme.setText(line[0]);
			}
		});
	}
	
	public Integer getPort() {
		return port;
	}
	
	public String[] formatInputTOSfeed(String line) {
		
		//	MarketTime=12:46:13.290
		
		//LocalTime=19:15:05.474,
//		Message=TOS,
//				MarketTime=13:15:05.011,
//				Symbol=AAPL.NQ,
//				Type=0,
//				Price=189.9500,Size=28,Source=1,Condition=?,Tick=?,Mmid=T,SubMarketId=32,Date=2018-06-01
		
	//	System.out.println(line);
		String segments[] = line.split(",");	
		
		String marketPriceParam = segments[5];		
		int indexOfEqualPrice = marketPriceParam.lastIndexOf("=");
		String outPrice = new String(marketPriceParam.substring(indexOfEqualPrice+1));
		
		String marketTimeParam = segments[2];
		int indexOfEqualTime = marketTimeParam.lastIndexOf("=");		
		String outTimePreFormat = new String(marketTimeParam.substring(indexOfEqualTime+1));		
		int indexOfDotTime = outTimePreFormat.lastIndexOf(".");
		String outTime = new String(outTimePreFormat.substring(0, indexOfDotTime)); 
		
//
//		System.out.println("outTimePreFormat "+ outTimePreFormat);
//		System.out.println("indexOfDotTime+1 "+ indexOfDotTime+1);
//		System.out.println("outTime "+ outTime);
//		System.out.println("outPrice "+ outPrice);
		
		
		return new String[] {outTime, outPrice};
		//return outPrice;		
	}
	
	
	
}