package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private final String url;

    private final TextView urlTextView;

    private Socket socket;

    public ClientThread(String address, int port, String url, TextView urlTextView) {
        this.address = address;
        this.port = port;
        this.url = url;
        this.urlTextView = urlTextView;
    }

    @Override
    public void run() {
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(address, port);

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends the url to the server
            printWriter.println(url);
            printWriter.flush();

            String info;

            // reads the information from the server
            while ((info = bufferedReader.readLine()) != null) {
                final String finalizedUrl = info;

                // updates the UI with the information. This is done using postt() method to ensure it is executed on UI thread
                urlTextView.post(() -> urlTextView.setText(finalizedUrl));
            }
        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
