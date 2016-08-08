package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
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

public class JoinActivity extends AppCompatActivity {
    public static ServerSocket joinServerSocket;
    public static Socket joinClientSocket;

    EditText ipEntry;
    EditText portEntry;
    Button connectButton;
    TextView waitMessage;
    TextView ipLabel;
    TextView portLabel;

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

        ipEntry = (EditText) findViewById(R.id.ipEntry);
        portEntry = (EditText) findViewById(R.id.portEntry);
        connectButton = (Button) findViewById(R.id.connectButton);
        waitMessage = (TextView) findViewById(R.id.waitText);
        ipLabel = (TextView) findViewById(R.id.ipLabel);
        portLabel = (TextView) findViewById(R.id.portLabel);
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
                joinServerSocket = new ServerSocket(0);
                Thread thread = new Thread(new ClientServerThread());
                thread.start();
                clientHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        clientPortNumber = String.valueOf(joinServerSocket.getLocalPort());
                        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                        clientIpAddress = Formatter.formatIpAddress(wm.getConnectionInfo()
                                .getIpAddress());
                        String messageType = "createConnection";
                        sendMessage("{'messageType':" + messageType + ",'ip':" + clientIpAddress +
                                ",'port':" + clientPortNumber + "}");
                        connectButton.setEnabled(false);
                        connectButton.setVisibility(View.INVISIBLE);
                    }
                });
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

    public class ClientServerThread implements Runnable {
        @Override
        public void run() {
            Socket client = null;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        client = joinServerSocket.accept();
                        ClientCommThread clientCommThread = new ClientCommThread(client);
                        new Thread(clientCommThread).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                            if (stringFromServer != null) {
                                handleReceivedMessage(stringFromServer);
                            }
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
            out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to send to server.", Toast.LENGTH_LONG).show();
        }
    }

    public void handleReceivedMessage(String message) {
        if (message != null && !message.equals("null")) {
            try {
                jsonFromServer = new JSONObject(message);
                String messageType = jsonFromServer.getString("messageType");
                if (messageType.equals("wait")) {
                    waitMessage.setText(R.string.please_wait);
                    ipEntry.setVisibility(View.INVISIBLE);
                    portEntry.setVisibility(View.INVISIBLE);
                    ipLabel.setVisibility(View.INVISIBLE);
                    portLabel.setVisibility(View.INVISIBLE);
                } else if (messageType.equals("newSheet")) {
                    String newSheet = "";
                    try {
                        newSheet = jsonFromServer.getString("sheet");
                        Intent intent = new Intent(context, SheetUI.class);
                        intent.putExtra("loadType", "web");
                        intent.putExtra("sheet", newSheet);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Failed to load that sheet!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
}
