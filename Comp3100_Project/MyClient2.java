import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyClient2 {
    private static ArrayList<Servers> serverlist = new ArrayList<>();
    private static Socket s;
    private static DataOutputStream dout;
    private static BufferedReader din;
    private static int count = 0;
    private static String lastRec = "";
    private static int jobID = 0;
    private static int jcore = 0;
    private static int jm = 0;
    private static int jd = 0;
    private static int id = 0;
    private static Boolean finded = false;
    private static String type = "";

    public static void main(String[] args) throws Exception {
        MyClient2 c = new MyClient2("127.0.01", 50000);
        c.send("HELO");
        c.Receive();
        c.send("AUTH camden");
        c.Receive();
        c.stage2();
        c.send("QUIT");
        c.Receive();
        c.close();
    }

    // create socket
    private MyClient2(String address, int port) throws Exception {
        s = new Socket(address, port);
        dout = new DataOutputStream(s.getOutputStream());
        din = new BufferedReader(new InputStreamReader(s.getInputStream()));

    }

    public static void send(String str) throws Exception {
        dout.write((str + "\n").getBytes());
    }

    private static void Receive() throws Exception {
        lastRec = (String) din.readLine();
    }

    public static String getLastRec() {
        return lastRec;
    }

    private static void close() throws Exception {
        dout.close();
        s.close();
    }

    private static void getStage2server() throws Exception {
        send(("GETS Avail " + jcore + " " + jm + " " + jd));
        Receive();
        getLastRec();
        String str = getLastRec();
        String[] data = str.split(" ");

        // Receive DATA nRecs recSize
        int nRecs = Integer.parseInt(data[1]);
        send("OK");

        if (nRecs == 0) {
            send("OK");
            Receive();
            Receive();
            getStage2Capable();

        } else {

            for (int i = 0; i < nRecs; i++) {
                String datas = (String) din.readLine();

                // create server and add to list
                Servers server = new Servers(datas);
                serverlist.add(server);
            }

            send("OK");

            Receive();

            // find the firstServertype
            Servers server = findfirstServer();
            type = server.Serverstype;
            id = server.getid();
            serverlist.clear();
        }

    }

    private static void getStage2Capable() throws Exception {
        send(("GETS Capable " + jcore + " " + jm + " " + jd));
        Receive();
        Receive();

        getLastRec();
        String str = getLastRec();
        String[] data = str.split(" ");
        // Receive DATA nRecs recSize
        int nRecs = Integer.parseInt(data[1]);
        send("OK");

        for (int i = 0; i < nRecs; i++) {
            String datas = (String) din.readLine();

            // create server and add to list
            Servers server = new Servers(datas);
            serverlist.add(server);
        }
        // Receive();
        send("OK");

        Receive();

        // find the largerServertype
        Servers Server = LessWaitingtimeServer();
        // Servers Server = findfirstServer();
        type = Server.Serverstype;
        id = Server.getid();
        serverlist.clear();

    }

    private static Servers LessWaitingtimeServer() throws Exception {
        int minWaitingTime = Integer.MAX_VALUE;
        int minCoreCount = Integer.MAX_VALUE;
        Servers selectedServer = null;

        for (Servers server : serverlist) {
            send(("EJWT " + server.getServerType() + " " + server.getid()));
            Receive();
            int waitingTime = Integer.parseInt(getLastRec());

            if (waitingTime < minWaitingTime) {
                minWaitingTime = waitingTime;
                minCoreCount = server.getCore();
                selectedServer = server;
            } else if (waitingTime == minWaitingTime) {
                if (server.getCore() < minCoreCount) {
                    minCoreCount = server.getCore();
                    selectedServer = server;
                } else if (server.getCore() == minCoreCount && server.getid() < selectedServer.getid()) {
                    selectedServer = server;
                }
            }
        }

        return selectedServer;
    }

    public static Servers findfirstServer() {
        if (serverlist.isEmpty()) {
            return null;
        }
        Servers firstServers = serverlist.get(0);
        return firstServers;
    }

    private static void stage2() throws Exception {

        while (!getLastRec().contains("NONE")) {
            send("REDY");
            // Receive JOBN, JCPL and NONE
            Receive();
            String step10 = getLastRec();

            if (step10.contains("JOBN")) {
                String[] Jobnarr = step10.split(" ");
                jobID = Integer.parseInt(Jobnarr[2]);
                jcore = Integer.parseInt(Jobnarr[4]);
                jm = Integer.parseInt(Jobnarr[5]);
                jd = Integer.parseInt(Jobnarr[6]);

                getStage2server();

                // schedule job

                send(("SCHD " + jobID + " " + type + " " + id));
                Receive();

            }

        }

    }

}

class Servers {
    public String Serverstype;
    private int Servercore;
    private int Serverid;

    public Servers(String DATA) {
        String[] data = DATA.split(" ");
        this.Serverstype = data[0];
        this.Servercore = Integer.parseInt(data[4]);
        this.Serverid = Integer.parseInt(data[1]);
    }

    public String getServerType() {
        return Serverstype;
    }

    public int getCore() {
        return Servercore;
    }

    public int getid() {
        return Serverid;
    }

}
