package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HostActivity extends AppCompatActivity {
    public static ServerSocket hostServerSocket;
    public static ArrayList<Socket> hostClientSockets;

    String hostIpAddress;
    String hostPortNumber;
    String stringFromClient = "";
    JSONObject jsonFromClient;
    int connectionCount = 0;

    public ArrayList<ClientInfo> clientInfos;

    Handler hostHandler = new Handler();
    Context context;

    TextView ipMessage;
    TextView ipAddress;
    TextView ipPort;
    TextView serverStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        hostClientSockets = new ArrayList<Socket>();
        clientInfos = new ArrayList<ClientInfo>();

        //Building the server info and opening the socket.
        try {
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            hostIpAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            ipMessage = (TextView) findViewById(R.id.ipAddressMessage);
            ipAddress = (TextView) findViewById(R.id.ipAddress);
            ipPort = (TextView) findViewById(R.id.ipPort);
            serverStatus = (TextView) findViewById(R.id.serverStatusText);

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
            Socket currentSocket = hostClientSockets.get(socketNumber);
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(
                            currentSocket.getOutputStream())), true);
            out.println(message);
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
                        Thread.currentThread().interrupt();
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
        if (messageFromClient != null) {
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
    }

    public class HostClientThread implements Runnable {
        @Override
        public void run() {
            try {
                final String ip = jsonFromClient.getString("ip");
                final String port = jsonFromClient.getString("port");
                final Socket newSocket = new Socket(ip, Integer.parseInt(port));
                hostClientSockets.add(newSocket);
                ClientInfo clientInfo = new ClientInfo(ip, port, hostClientSockets.size()
                        - 1);
                clientInfos.add(clientInfo);
                hostHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String messageType = "wait";
                        sendMessage("{'messageType':'" + messageType + "'}", ip);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void beginHosting (View v) {
        String sheet = "{\"blocks\":{\"block0\":{\"x\":\"228\",\"y\":\"0\",\"width\":\"122px\"," +
                "\"height\":\"122px\",\"type\":\"resourceDivImage\",\"value\":\"http://www" +
                ".underconsideration.com/brandnew/archives/dungeons_and_dragons_40_ampersand_flat" +
                ".png\",\"id\":\"resourceDiv0\",\"html\":\"<img id=\\\"image0\\\" " +
                "src=\\\"http://www.underconsideration" +
                ".com/brandnew/archives/dungeons_and_dragons_40_ampersand_flat.png\\\" " +
                "resourcetype=\\\"image\\\" resourcevalue=\\\"http://www.underconsideration" +
                ".com/brandnew/archives/dungeons_and_dragons_40_ampersand_flat.png\\\" " +
                "width=\\\"122\\\" height=\\\"122\\\" style=\\\"width: 122px; height: 122px;" +
                "\\\">\",\"childVal\":\"\"},\"block1\":{\"x\":\"0\",\"y\":\"24\"," +
                "\"width\":\"72px\",\"height\":\"40px\",\"type\":\"resourceDivLabel\"," +
                "\"value\":\"Health\",\"id\":\"resourceDiv1\",\"html\":\"<label id=\\\"label0\\\"" +
                " resourcetype=\\\"label\\\" style=\\\"width: 72px; height: 40px;\\\" " +
                "resourcevalue=\\\"Health\\\">Health</label>\",\"childVal\":\"\"}," +
                "\"block2\":{\"x\":\"72\",\"y\":\"24\",\"width\":\"50px\",\"height\":\"40px\"," +
                "\"type\":\"resourceDivVariable\",\"value\":\"null\",\"id\":\"resourceDiv2\"," +
                "\"html\":\"<input id=\\\"Health\\\" placeholder=\\\"Variable value\\\" " +
                "readonly=\\\"\\\" resourcetype=\\\"variable\\\" resourcevalue=\\\"0\\\" " +
                "width=\\\"50\\\" height=\\\"40\\\" style=\\\"width: 50px; height: 40px;\\\">\"," +
                "\"childVal\":\"\"},\"block3\":{\"x\":\"0\",\"y\":\"84\",\"width\":\"76px\"," +
                "\"height\":\"40px\",\"type\":\"resourceDivLabel\",\"value\":\"Attack\"," +
                "\"id\":\"resourceDiv3\",\"html\":\"<label id=\\\"label1\\\" " +
                "resourcetype=\\\"label\\\" style=\\\"width: 76px; height: 40px;\\\" " +
                "resourcevalue=\\\"Attack\\\">Attack</label>\",\"childVal\":\"\"}," +
                "\"block4\":{\"x\":\"72\",\"y\":\"84\",\"width\":\"50px\",\"height\":\"40px\"," +
                "\"type\":\"resourceDivVariable\",\"value\":\"\",\"id\":\"resourceDiv4\"," +
                "\"html\":\"<input id=\\\"Attack\\\" placeholder=\\\"Variable value\\\" " +
                "readonly=\\\"\\\" resourcetype=\\\"variable\\\" resourcevalue=\\\"\\\" " +
                "width=\\\"50\\\" height=\\\"40\\\" style=\\\"width: 50px; height: 40px;\\\">\"," +
                "\"childVal\":\"\"}},\"calculations\":{},\"name\":\"Test File\"," +
                "\"height\":\"500px\"}";
        for (ClientInfo clientInfo : clientInfos) {
            sendMessage("{'messageType':'newSheet','sheet':" + sheet + "}", clientInfo.ipAddress);
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
}
