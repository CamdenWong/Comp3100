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


    // private static void getStage2server() throws Exception {
    //     send(("GETS Avail " + jcore + " " + jm + " " + jd));
    //     Receive();
    //     getLastRec();
    //     String str = getLastRec();
    //     String[] data = str.split(" ");

    //     // Receive DATA nRecs recSize
    //     int nRecs = Integer.parseInt(data[1]);
    //     send("OK");

    //     if(nRecs==0){
    //         getStage2Capable();
    //     }else {
    //     for (int i = 0; i < nRecs; i++) {
    //         String datas = (String) din.readLine();
    //         System.out.println("message = " + datas);

    //         // create server and add to list
    //         Servers server = new Servers(datas);
    //         serverlist.add(server);
    //     }
    //     send("OK");
    //     // Receive .
    //     Receive();
    //     // find the fisrstServertype
    //     Servers Server = findfirstServer();
    //     type = Server.Serverstype;
    //     id = Server.getid();
    //     serverlist.clear();

       
    // }
    // }

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
           
        send(("GETS Capable " + jcore + " " + jm + " " + jd));
        Receive();
        Receive();
        getLastRec();
        String str2 = getLastRec();
        System.out.println(str2);
        String[] data2 = str2.split(" ");
        // Receive DATA nRecs recSize
        int nRecs2 = Integer.parseInt(data2[1]);
         send("OK");
      

        for (int i = 0; i < nRecs2; i++) {
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

       
        } else if(nRecs > 0){
            // send("OK");
            for (int i = 0; i < nRecs; i++) {
                //  System.out.println(nRecs);
                String datas = (String) din.readLine();
                System.out.println("message = " + datas);
    
                // create server and add to list
                Servers server = new Servers(datas);
                serverlist.add(server);
            }
            
            // find the firstServertype
            Servers server = findfirstServer();
            type = server.getServerType();
            id = server.getid();
            serverlist.clear();
        }
    
        send("OK");
        // Receive .
        Receive();
    }
    

    
    // private static void getStage2Capable() throws Exception {
    //     send(("GETS Capable " + jcore + " " + jm + " " + jd));
    //     Receive();
    //     getLastRec();
    //     String str = getLastRec();
    //     String[] data = str.split(" ");
    //     // Receive DATA nRecs recSize
    //     int nRecs = Integer.parseInt(data[1]);
    //     send("OK");

    //     for (int i = 0; i < nRecs; i++) {
    //         String datas = (String) din.readLine();
    //         System.out.println("message = " + datas);

    //         // create server and add to list
    //         Servers server = new Servers(datas);
    //         serverlist.add(server);
    //     }
    //     send("OK");
    //     // Receive .
    //     Receive();

    //     // find the largerServertype
    //     Servers Server = LessWaitingtimeServer();
    //     type = Server.Serverstype;
    //     serverlist.clear();

       

    // }

    

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


    message = OK
message = OK
message = JOBN 5 0 796 1 400 1500
message = DATA 12 124
message = small 0 inactive -1 2 4000 16000 0 0
message = small 1 inactive -1 2 4000 16000 0 0
message = small 2 inactive -1 2 4000 16000 0 0
message = small 3 inactive -1 2 4000 16000 0 0
message = medium 0 inactive -1 4 16000 64000 0 0
message = medium 1 inactive -1 4 16000 64000 0 0
message = medium 2 inactive -1 4 16000 64000 0 0
message = medium 3 inactive -1 4 16000 64000 0 0
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 29 1 1784 2 1300 3600
message = DATA 11 124
message = small 1 inactive -1 2 4000 16000 0 0
message = small 2 inactive -1 2 4000 16000 0 0
message = small 3 inactive -1 2 4000 16000 0 0
message = medium 0 inactive -1 4 16000 64000 0 0
message = medium 1 inactive -1 4 16000 64000 0 0
message = medium 2 inactive -1 4 16000 64000 0 0
message = medium 3 inactive -1 4 16000 64000 0 0
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 68 2 3515 1 1000 600
message = DATA 11 124
message = small 0 active 65 1 3600 14500 0 1
message = small 2 inactive -1 2 4000 16000 0 0
message = small 3 inactive -1 2 4000 16000 0 0
message = medium 0 inactive -1 4 16000 64000 0 0
message = medium 1 inactive -1 4 16000 64000 0 0
message = medium 2 inactive -1 4 16000 64000 0 0
message = medium 3 inactive -1 4 16000 64000 0 0
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 96 3 1744 4 2500 6500
message = DATA 8 124
message = medium 0 inactive -1 4 16000 64000 0 0
message = medium 1 inactive -1 4 16000 64000 0 0
message = medium 2 inactive -1 4 16000 64000 0 0
message = medium 3 inactive -1 4 16000 64000 0 0
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 110 4 277 1 900 900
message = DATA 9 124
message = small 2 inactive -1 2 4000 16000 0 0
message = small 3 inactive -1 2 4000 16000 0 0
message = medium 1 inactive -1 4 16000 64000 0 0
message = medium 2 inactive -1 4 16000 64000 0 0
message = medium 3 inactive -1 4 16000 64000 0 0
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 132 5 3452 4 1000 3900
message = DATA 7 124
message = medium 1 inactive -1 4 16000 64000 0 0
message = medium 2 inactive -1 4 16000 64000 0 0
message = medium 3 inactive -1 4 16000 64000 0 0
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 152 6 2847 4 900 3800
message = DATA 6 124
message = medium 2 inactive -1 4 16000 64000 0 0
message = medium 3 inactive -1 4 16000 64000 0 0
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 172 7 3732 4 3900 6900
message = DATA 5 124
message = medium 3 inactive -1 4 16000 64000 0 0
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 182 8 546 2 300 4000
message = DATA 5 124
message = small 3 inactive -1 2 4000 16000 0 0
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 206 9 136 4 3300 2000
message = DATA 4 124
message = large 0 inactive -1 8 32000 256000 0 0
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 210 10 2363 2 300 800
message = DATA 3 124
message = large 1 inactive -1 8 32000 256000 0 0
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 226 11 5166 5 4400 8000
message = DATA 2 124
message = large 2 inactive -1 8 32000 256000 0 0
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 228 12 5062 2 1200 700
message = DATA 1 124
message = large 3 inactive -1 8 32000 256000 0 0
message = .
message = OK
message = JOBN 231 13 1804 3 1400 1000
message = DATA 0 124
message = .
message = DATA 8 124
message = medium 0 active 156 0 13500 57500 0 1
message = medium 1 active 192 0 15000 60100 0 1
message = medium 2 active 212 0 15100 60200 0 1
message = medium 3 booting 232 0 12100 57100 1 0
message = large 0 booting 266 4 28700 254000 1 0
message = large 1 booting 270 6 31700 255200 1 0
message = large 2 booting 286 3 27600 248000 1 0
message = large 3 booting 288 6 30800 255300 1 0
message = .
message = 0
message = 0
message = 0
message = 3732
message = 136
message = 2363
message = 5166
message = 5062
message = 5062
message = 
message = OK
message = JCPL 1495 10 large 1
message = JOBN 1509 14 691 6 5800 6700
message = JOBN 1509 14 691 6 5800 6700
message = DATA 2 124
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 4 out of bounds for length 3
        at Servers.<init>(MyClient2.java:486)
        at MyClient2.getStage2server(MyClient2.java:366)
        at MyClient2.stage2(MyClient2.java:462)
        at MyClient2.main(MyClient2.java:290)

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
