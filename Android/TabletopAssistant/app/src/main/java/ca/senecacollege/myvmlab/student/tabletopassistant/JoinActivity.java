package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class JoinActivity extends AppCompatActivity {
    public static ServerSocket joinServerSocket;
    public static Socket joinClientSocket;

    EditText ipEntry = (EditText) findViewById(R.id.ipEntry);
    EditText portEntry = (EditText) findViewById(R.id.portEntry);
    Button connectButton = (Button) findViewById(R.id.connectButton);

    String hostIpAddress;
    String hostPortNumber;

    String clientIpAddress;
    String clientPortNumber;

    String stringFromServer = "";
    JSONObject jsonFromServer;

    Handler clientHandler = new Handler();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
    }

    public void connectToServer (View v) {
        try {
            hostIpAddress = ipEntry.getText().toString();
            hostPortNumber = portEntry.getText().toString();

            Thread thread = new Thread(new JoinClientThread());
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to start!", Toast.LENGTH_LONG).show();
        }
    }

    public class JoinClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress address = InetAddress.getByName(hostIpAddress);
                joinClientSocket = new Socket(address, Integer.parseInt(hostPortNumber));
                String messageType = "createConnection";
                sendMessage("{'messageType':" + messageType + ",'ip':" + clientIpAddress +
                        ",'port':" + clientPortNumber + "}");
                clientHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Connected!", Toast.LENGTH_LONG).show();
                    }
                });
                connectButton.setEnabled(false);
                connectButton.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                clientHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Failed to connect!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        }
    }

    public class ClientCommThread implements Runnable {
        private Socket clientSocket;
        private BufferedReader bufferedReader;

        public ClientCommThread(Socket socket) {
            this.clientSocket = socket;
            try {
                this.bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                this.clientSocket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    stringFromServer = bufferedReader.readLine();
                    clientHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            handleReceivedMessage(stringFromServer);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(String message) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(joinClientSocket.getOutputStream())), true);
            out.append(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to send to server.", Toast.LENGTH_LONG).show();
        }
    }

    public void handleReceivedMessage(String message) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            joinClientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            joinServerSocket.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

/*    EditText editText;
    String ipAddress;
    String port;
    Socket socket;
    ServerSocket serverSocket;
    Context context;
    Handler handler = new Handler();
    String readString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
    }

    public void connectSocket (View v) {
        try {
            editText = (EditText) findViewById(R.id.ipEntry);
            ipAddress = editText.getText().toString();
            editText = (EditText) findViewById(R.id.portEntry);
            port = editText.getText().toString();

            Thread thread = new Thread(new ClientThread());
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to start!", Toast.LENGTH_LONG).show();
        }
    }

    public void sendMessage() {
        try {
            serverSocket = new ServerSocket(0);

            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String clientIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            String clientPort = String.valueOf(serverSocket.getLocalPort());

            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
            out.println("{'ip':" + clientIp + ",'port':" + clientPort + "}");

            Thread thread = new Thread(new GetJsonThread());
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to send text.", Toast.LENGTH_LONG).show();
        }
    }

    public class GetJsonThread implements Runnable {
        @Override
        public void run() {
            Socket client = null;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    client = serverSocket.accept();
                    CommThread commThread = new CommThread(client);
                    new Thread(commThread).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class CommThread implements Runnable {
        private Socket socket;
        private BufferedReader bufferedReader;

        public CommThread(Socket socket) {
            this.socket = socket;
            try {
                this.bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                this.socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    readString = bufferedReader.readLine();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = (TextView) findViewById(R.id.messageBox);
                            textView.setText(readString);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress address = InetAddress.getByName(ipAddress);
                socket = new Socket(address, Integer.parseInt(port));
                sendMessage();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Connected!", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Failed to connect!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
*/
}
