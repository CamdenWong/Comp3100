import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyClient {
private static ArrayList<Servers> serverlist = new ArrayList<>();
private static Socket s;
private static DataOutputStream dout;
private static BufferedReader din;
private static int count=0;
private static String lastRec="";
private static int jobID=0;
private static Boolean finded=false;
private static String type="";
private static int j=0;


    public static void main(String[] args) throws Exception{ 
        MyClient c =new MyClient("127.0.01", 50000);
        c.send("HELO");
        c.Receive();
        c.send("AUTH camden");
        c.Receive();
        c.LRR();
        c.send("QUIT");
        c.Receive();
        c.close();
    }

   


    private MyClient(String address,int port) throws Exception {
         s = new Socket(address, port);
         dout = new DataOutputStream(s.getOutputStream());
         din = new BufferedReader(new InputStreamReader(s.getInputStream()));

    }

    


    public static void send(String str) throws Exception{
        dout.write((str+"\n").getBytes());
    }

    private static void Receive() throws Exception{
        lastRec=(String)din.readLine();
        System.out.println("message = " + lastRec);
    }

    public static String getLastRec(){
        return lastRec;
    }

    private static void close() throws Exception {
        dout.close();
        s.close();
    }



    private static void getServerslist()throws Exception{
       send("GETS All");
       Receive();
        getLastRec();
       String str=getLastRec();
       String[]data=str.split(" ");
       int nRecs=Integer.parseInt(data[1]);
      
       send("OK");

       for(int i=0;i<nRecs;i++){

        String datas=(String)din.readLine();
        System.out.println("message = " + datas);

        Servers server = new Servers(datas);
        serverlist.add(server);
       }

       Servers largestServer=findLargestServer();
       type=largestServer.Serverstype;
    //    System.out.println(largestServer.getServerType()+" "+count);

        send("OK");
        Receive();
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
                count=0;

            }
            if(server.getCore()==largestServer.getCore()&&server.Serverstype.equals(largestServer.Serverstype)){
               count++;
            }
        }
        return largestServer;
    }

    private static void LRR() throws Exception{
        while(!getLastRec().contains("NONE")){
        send("REDY");
        Receive();
        
        
        String step10=getLastRec();

        
        
       if(step10.contains("JOBN")){
               String[]Jobnarr=step10.split(" ");
               jobID=Integer.parseInt(Jobnarr[2]);
            }

        if(finded==false){
            getServerslist();
            finded=true;
            
        }
        

         if(step10.contains("JOBN")){
                send(("SCHD "+jobID+" "+type+" "+j));
                Receive();
                j++;
              }


                if(j>=count){
                    j=0;
                }

            }
    }


}


class Servers{
    private String DATA;
    public String Serverstype;
    private int core;
    
    public Servers (String DATA){
        String []data=DATA.split(" ");
        this.Serverstype=data[0];
        this.core=Integer.parseInt(data[4]);
    }
    public String getServerType(){
        return Serverstype;
    }
    public int getCore(){
        return core;
    }

}
