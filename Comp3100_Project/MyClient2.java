import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyClient2 {
    private static ArrayList<Servers> serverlist = new ArrayList<>();
    private static Socket s;
    private static DataOutputStream dout;
    private static BufferedReader din;
    private static String lastRec = "";
    private static int jobID = 0;
    private static int jcore = 0;
    private static int jm = 0;
    private static int jd = 0;
    private static int id = 0;
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
        //first find the server that current availability 
        send(("GETS Avail " + jcore + " " + jm + " " + jd));
        Receive();
        getLastRec();
        String str = getLastRec();
        String[] data = str.split(" ");

        // Receive DATA nRecs recSize
        int nRecs = Integer.parseInt(data[1]);
        send("OK");

        // means current not have
        if (nRecs == 0) {
            // to find sever that eventually can proive
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
            type = server.getServerType();
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

        send("OK");
        Receive();

        // find the Server that have the less estimated waiting time with the job
        Servers Server = LessWaitingtimeServer();
        type = Server.getServerType();
        id = Server.getid();
        serverlist.clear();

    }

    private static Servers LessWaitingtimeServer() throws Exception {
        int minWaitingTime = Integer.MAX_VALUE;
        int minCoreCount = Integer.MAX_VALUE;
        Servers selectedServer = null;

        for (Servers server : serverlist) {
            //find each server's waiting time with the job
            send(("EJWT " + server.getServerType() + " " + server.getid()));
            Receive();
            int waitingTime = Integer.parseInt(getLastRec());

            //find the less one
            if (waitingTime < minWaitingTime) {
                minWaitingTime = waitingTime;
                minCoreCount = server.getCore();
                selectedServer = server;
                //If the waiting time is the same, find a less curr core server which avoid wasting core
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
    public String ServerType;
    private int ServerCore;
    private int ServerId;

    public Servers(String DATA) {
        String[] data = DATA.split(" ");
        this.ServerType = data[0];
        this.ServerCore = Integer.parseInt(data[4]);
        this.ServerId = Integer.parseInt(data[1]);
    }

    public String getServerType() {
        return ServerType;
    }

    public int getCore() {
        return ServerCore;
    }

    public int getid() {
        return ServerId;
    }

}
