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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class HostActivity extends AppCompatActivity {
    public static ServerSocket hostServerSocket;
    public static ArrayList<Socket> hostClientSockets;

    String hostIpAddress;
    String hostPortNumber;
    String stringFromClient = "";
    JSONObject jsonFromClient;
    int connectionCount = 0;

    ArrayList<ClientInfo> clientInfos;

    Handler hostHandler = new Handler();
    Context context;

    TextView ipMessage = (TextView) findViewById(R.id.ipAddressMessage);
    TextView ipAddress = (TextView) findViewById(R.id.ipAddress);
    TextView ipPort = (TextView) findViewById(R.id.ipPort);
    TextView serverStatus = (TextView) findViewById(R.id.serverStatusText);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        //Building the server info and opening the socket.
        try {
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            hostIpAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            ipMessage.setText(R.string.show_this_ip);
            ipAddress.setText(hostIpAddress);

            Thread thread = new Thread(new HostServerThread());
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to host game.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void sendMessage(String message, String ipAddress) {
        int socketNumber = 0;
        for (ClientInfo clientInfo : clientInfos) {
            if (clientInfo.ipAddress.equals(ipAddress)) {
                socketNumber = clientInfo.clientNumber;
                break;
            }
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(
                            hostClientSockets.get(socketNumber).getOutputStream())), true);
            out.append(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to send to client.", Toast.LENGTH_LONG).show();
        }
    }

    public class HostServerThread implements Runnable {
        public void run() {
            Socket client = null;
            try {
                hostHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText(R.string.listening_for_players);
                    }
                });
                hostServerSocket = new ServerSocket(0);
                hostHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Set the port text to the port of the hostserversocket.
                        hostPortNumber = String.valueOf(hostServerSocket.getLocalPort());
                        ipPort.setText(hostPortNumber);
                    }
                });
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        client = hostServerSocket.accept();
                        HostCommThread hostCommThread = new HostCommThread(client);
                        new Thread(hostCommThread).start();
                        hostHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                connectionCount ++;
                                String totalPlayers = "Total connected players: " + connectionCount;
                                serverStatus.setText(totalPlayers);
                                Button button = (Button) findViewById(R.id.hostOkButton);
                                button.setEnabled(true);
                                button.setAlpha((float) 1);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        hostHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText(R.string.please_restart);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class HostCommThread implements Runnable {
        private Socket clientSocket;
        private BufferedReader bufferedReader;

        public HostCommThread(Socket socket) {
            this.clientSocket = socket;
            try {
                this.bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                this.clientSocket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
                hostHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText(R.string.failed_to_get_text);
                    }
                });
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    stringFromClient = bufferedReader.readLine();
                    hostHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText(stringFromClient);
                            handleReceivedMessage(stringFromClient);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void handleReceivedMessage (String messageFromClient) {
        try {
            jsonFromClient = new JSONObject(messageFromClient);
            String messageType = jsonFromClient.getString("messageType");
            if (messageType.equals("createConnection")) {
                Thread thread = new Thread(new HostClientThread());
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class HostClientThread implements Runnable {
        @Override
        public void run() {
            try {
                String ip = jsonFromClient.getString("ip");
                String port = jsonFromClient.getString("port");
                hostClientSockets.add(new Socket(ip, Integer.parseInt(port)));
                clientInfos.add(new ClientInfo(ip, port, hostClientSockets.size() - 1));
                sendMessage("IT WORKS!", ip);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to make socket to client.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class ClientInfo {
        String ipAddress = "";
        String portNumber = "";
        int clientNumber;

        ClientInfo(String ipAddress, String portNumber, int clientNumber) {
            this.ipAddress = ipAddress;
            this.portNumber = portNumber;
            this.clientNumber = clientNumber;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            hostServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            for (Socket clientSocket : hostClientSockets) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*Socket socket;
    ServerSocket serverSocket;
    Handler handler = new Handler();
    String ip;
    String portNumber;
    TextView serverStatusText;
    int connectionCount = 1;
    Context context;
    String readString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        serverStatusText = (TextView) findViewById(R.id.serverStatusText);
        serverStatusText.setGravity(Gravity.CENTER_HORIZONTAL);

        try {
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            TextView textView = (TextView) findViewById(R.id.ipAddressMessage);
            textView.setText("Show this IP address to the players");
            textView.setGravity(Gravity.CENTER_HORIZONTAL);

            textView = (TextView) findViewById(R.id.ipAddress);
            textView.setText(ip);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);

            Thread thread = new Thread(new HostThread());
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class HostThread implements Runnable {
        public void run() {
            Socket client = null;
            try {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatusText.setText("Listening for players...");
                    }
                });
                serverSocket = new ServerSocket(0);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        portNumber = String.valueOf(serverSocket.getLocalPort());

                        TextView textView = (TextView) findViewById(R.id.ipPort);
                        textView.setText(portNumber);
                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                });
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        client = serverSocket.accept();
                        CommThread commThread = new CommThread(client);
                        new Thread(commThread).start();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                connectionCount ++;
                                serverStatusText.setText("Total players: " + connectionCount);
                                Button button = (Button) findViewById(R.id.hostOkButton);
                                button.setEnabled(true);
                                button.setAlpha((float) 1);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatusText.setText("There was an issue with connecting " +
                                        "players.");
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class CommThread implements Runnable {
        private Socket clientSocket;
        private BufferedReader bufferedReader;

        public CommThread(Socket socket) {
            this.clientSocket = socket;
            try {
                this.bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                this.clientSocket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatusText.setText("Failed to get text.");
                    }
                });
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
                            serverStatusText.setText(readString);
                        }
                    });
                    try {
                        JSONObject jsonObject = new JSONObject(readString);
                        InetAddress address = InetAddress.getByName(jsonObject.getString("ip"));
                        socket = new Socket(address, Integer.parseInt(jsonObject.getString("port")));

                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println("Received!");

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    */
}
