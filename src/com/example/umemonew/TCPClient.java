package com.example.umemonew;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.util.Log;


/***
 * 
 * @author ������
 * TCP�ͻ���
 */
public class TCPClient {
	
	
	public static int timer = 0;

	public Socket socket = null;
	
	public Protocol protocol = new Protocol();

	//Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
	//class at on asynckTask doInBackground
	public interface OnMessageReceived {
		public void messageReceived(String message);		//need not to implement

	}

	private String serverMessage;
	/**
	 * Specify the Server IP Address here. Whereas our Socket Server is started.
	 * */
	public static final String SERVERIP = "168.63.148.185";  
	public static int SERVERPORT;
	private OnMessageReceived mMessageListener = null;
	private boolean mRun = false;

	private PrintWriter out = null;			//send message
	public BufferedReader in = null;		//get message

	/**
	 *  Constructor of the class. OnMessagedReceived listens for the messages received from server
	 */
	public TCPClient(final OnMessageReceived listener,final int server_port) 
	{
		mMessageListener = listener;
		SERVERPORT=server_port;
	}

	/**
	 * Sends the message entered by client to the server
	 * @param message text entered by client
	 */
	public void sendMessage(String message){
		if (out != null && !out.checkError()) {
			Log.v("message: ",message); 
			out.println(message);
			out.flush();
		}
	}

	public void stopClient(){
		mRun = false;
	}

	/*************************************************/
	public void run(String username) {
		mRun = true;				//set run_flag true
		try {
			//here you must put your computer's IP address.
			InetAddress serverAddr = InetAddress.getByName(SERVERIP);

			Log.v("TCP Client", "Connecting...");
			
			//create a socket to make the connection with the server
			socket = new Socket(serverAddr, SERVERPORT);

			try {
				//to send the message to the server
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

				Log.v("TCP Client", "Sent.");
				Log.v("TCP Client", "Done.");

				// 	send user_name (just like shaking hands)
				//you should get the user_name of the user from the database and send it
				sendMessage(protocol.generateUsername(username));

				//to receive the message which the server sends back
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				//in this while the client listens for the messages sent by the server 
				while (mRun) {
					serverMessage = in.readLine();	
					if (serverMessage != null && mMessageListener != null) {
						//call the method messageReceived from MyActivity class
						mMessageListener.messageReceived(serverMessage);
						Log.v("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
					}
					serverMessage = null;
				}
			}
			catch (Exception e) 
			{
				Log.e("TCP Error", "Error", e);
				e.printStackTrace();
			}
			finally 
			{
				//the socket must be closed. It is not possible to reconnect to this socket
				// after it is closed, which means a new socket instance has to be created.
				socket.close();
			} 
		} catch (SocketTimeoutException e){
			Log.v("socket timeout","should restart");
		} catch (Exception e) {
			Log.e("TCP Error", "Error", e);
		}
		Log.v("TcpClient running","everything is over");
	}
}

