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
    private static int j = 0;

    public static void main(String[] args) throws Exception {
        MyClient2 c = new MyClient2("127.0.01", 50000);
        c.send("HELO");
        c.Receive();
        c.send("AUTH camden");
        c.Receive();
        // c.LRR();
        // c.FirstCap()
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
        System.out.println("message = " + lastRec);
    }

    public static String getLastRec() {
        return lastRec;
    }

    private static void close() throws Exception {
        dout.close();
        s.close();
    }

    private static void getServerslist() throws Exception {
        send("GETS All");
        Receive();
        getLastRec();
        String str = getLastRec();
        String[] data = str.split(" ");

        // Receive DATA nRecs recSize
        int nRecs = Integer.parseInt(data[1]);
        send("OK");

        for (int i = 0; i < nRecs; i++) {
            String datas = (String) din.readLine();
            System.out.println("message = " + datas);

            // create server and add to list
            Servers server = new Servers(datas);
            serverlist.add(server);
        }

        // find the largerServertype
        Servers largestServer = findLargestServer();
        type = largestServer.Serverstype;

        send("OK");
        // Receive .
        Receive();
    }

    private static void getfirstserver() throws Exception {
        send(("GETS Capable " + jcore + " " + jm + " " + jd));
        Receive();
        getLastRec();
        String str = getLastRec();
        String[] data = str.split(" ");

        // Receive DATA nRecs recSize
        int nRecs = Integer.parseInt(data[1]);
        send("OK");

        for (int i = 0; i < nRecs; i++) {
            String datas = (String) din.readLine();
            System.out.println("message = " + datas);

            // create server and add to list
            Servers server = new Servers(datas);
            serverlist.add(server);
        }
        

        // find the largerServertype
        Servers largestServer = findfirstServer();
        type = largestServer.Serverstype;
        serverlist.clear();


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

        if(nRecs==0){
            getStage2Capable();
        }else if(nRecs>0){
        for (int i = 0; i < nRecs; i++) {
            String datas = (String) din.readLine();
            System.out.println("message = " + datas);

            // create server and add to list
            Servers server = new Servers(datas);
            serverlist.add(server);
        }
        send("OK");
        // Receive .
        Receive();
        // find the fisrstServertype
        Servers Server = findfirstServer();
        type = Server.Serverstype;
        id = Server.getid();
        serverlist.clear();

       
    }
    }

    
    private static void getStage2Capable() throws Exception {
        send(("GETS Capable " + jcore + " " + jm + " " + jd));
        Receive();
        getLastRec();
        String str = getLastRec();
        String[] data = str.split(" ");
        // Receive DATA nRecs recSize
        int nRecs = Integer.parseInt(data[1]);
        send("OK");

        for (int i = 0; i < nRecs; i++) {
            String datas = (String) din.readLine();
            System.out.println("message = " + datas);

            // create server and add to list
            Servers server = new Servers(datas);
            serverlist.add(server);
        }
        send("OK");
        // Receive .
        Receive();

        // find the largerServertype
        Servers Server = LessWaitingtimeServer();
        type = Server.Serverstype;
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
    

    

    public static Servers findLargestServer() {
        if (serverlist.isEmpty()) {
            return null;
        }
        Servers largestServer = serverlist.get(0);
        for (int i = 1; i < serverlist.size(); i++) {
            Servers server = serverlist.get(i);

            if (server.getCore() > largestServer.getCore()) {
                largestServer = server;
                count = 0;
            }

            if (server.getCore() == largestServer.getCore() && server.Serverstype.equals(largestServer.Serverstype)) {
                count++;
            }
        }
        return largestServer;
    }

    public static Servers findfirstServer() {
        if (serverlist.isEmpty()) {
            return null;
        }
        Servers firstServers = serverlist.get(0);
        return firstServers;
    }

    private static void LRR() throws Exception {
        while (!getLastRec().contains("NONE")) {
            send("REDY");
            Receive();
            String step10 = getLastRec();

            if (step10.contains("JOBN")) {
                String[] Jobnarr = step10.split(" ");
                jobID = Integer.parseInt(Jobnarr[2]);
            }

            // obtain the serverinfo
            if (finded == false) {
                getServerslist();
                finded = true;
                // only run 1 time
            }

            // schedule job
            if (step10.contains("JOBN")) {
                send(("SCHD " + jobID + " " + type + " " + j));
                Receive();
                j++;
            }

            // reset
            if (j >= count) {
                j = 0;
            }

        }
    }

    private static void FirstCap() throws Exception {
        while (!getLastRec().contains("NONE")) {
            send("REDY");
            Receive();
            String step10 = getLastRec();

            if (step10.contains("JOBN")) {
                String[] Jobnarr = step10.split(" ");
                jobID = Integer.parseInt(Jobnarr[2]);
                jcore = Integer.parseInt(Jobnarr[4]);
                jm = Integer.parseInt(Jobnarr[5]);
                jd = Integer.parseInt(Jobnarr[6]);

                if (finded == false) {
                    getfirstserver();
                    finded = true;
                }

                // schedule job
                if (step10.contains("JOBN")) {
                    send(("SCHD " + jobID + " " + type + " " + id));
                    Receive();
                    finded = false;
                }
            }

        }
    }

    private static void stage2() throws Exception{

        while (!getLastRec().contains("NONE")) {
            send("REDY");
            Receive();
            String step10 = getLastRec();

            if (step10.contains("JOBN")) {
                String[] Jobnarr = step10.split(" ");
                jobID = Integer.parseInt(Jobnarr[2]);
                jcore = Integer.parseInt(Jobnarr[4]);
                jm = Integer.parseInt(Jobnarr[5]);
                jd = Integer.parseInt(Jobnarr[6]);

                if (finded == false) {
                    getStage2server();
                    finded = true;
                }

                // schedule job
                if (step10.contains("JOBN")) {
                    send(("SCHD " + jobID + " " + type + " " + id));
                    Receive();
                    finded = false;
                }
            }

        }

    }

   
}


class Servers {
    private String DATA;
    public String Serverstype;
    private int core;
    private int id;

    public Servers(String DATA) {
        String[] data = DATA.split(" ");
        this.Serverstype = data[0];
        this.core = Integer.parseInt(data[4]);
        this.id = Integer.parseInt(data[1]);
    }

    public String getServerType() {
        return Serverstype;
    }

    public int getCore() {
        return core;
    }

    public int getid() {
        return id;
    }

}
