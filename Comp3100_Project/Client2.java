import java.io.*;
import java.net.*;

public class Client2 {
    public static void main(String[] args) {
        try {

            Socket s = new Socket("127.0.01", 50000);
            //Create Socket
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));

            dout.write(("HELO\n").getBytes());
            String Receive = (String) din.readLine();
            // Receive OK
            System.out.println("message = " + Receive);


            dout.write(("AUTH camden\n").getBytes());
            Receive = (String) din.readLine();
            // Receive OK
            System.out.println("message = " + Receive);

            int largestCore=0;
            int count=0;
            String type="";
            int jobID=0;
            int jcore=0;
            int jm=0;
            int jd=0;
            int id=0;
            Boolean finded=false;
            int j=0; //for schedule the job and reset the serverID



            while(!Receive.contains("NONE")){

            dout.write(("REDY\n").getBytes());
            Receive = (String) din.readLine();
            // Receive JOBN, JCPL and NONE
            String step10=Receive;
            System.out.println("message = " + Receive);


            //find jobID
            if(Receive.contains("JOBN")){
               String[]Jobnarr=Receive.split(" ");
               jobID=Integer.parseInt(Jobnarr[2]);
               jcore=Integer.parseInt(Jobnarr[4]);
               jm=Integer.parseInt(Jobnarr[5]);
               jd=Integer.parseInt(Jobnarr[6]);

            

            
            dout.write(("GETS Capable "+jcore+" "+jm+" "+jd+"\n").getBytes());
            Receive = (String) din.readLine();
            //Receive DATA nRecs recSize
            System.out.println("message = " + Receive);

            String[]data=Receive.split(" ");
            int nRecs=Integer.parseInt(data[1]);
            
            dout.write(("OK\n").getBytes());

            for(int i=0;i<nRecs;i++){
            Receive = (String) din.readLine();
            //Receive serverType serverID state curStartTime core

            String[] arr = Receive.split(" ");
            if(finded==false){
            id=Integer.parseInt(arr[1]);
            type=arr[0];
            finded=true;
            }
            //temp may diff with type but have same core number
            
         

             System.out.println("message = " + Receive);
    

            }

            dout.write(("OK\n").getBytes());
             
            Receive = (String) din.readLine();
            System.out.println("message = " + Receive);

            
            
            //schedule job
            if(step10.contains("JOBN")){
                dout.write(("SCHD "+jobID+" "+type+" "+id+"\n").getBytes());
                Receive = (String) din.readLine();
                System.out.println("message = " + Receive);
               finded=false;
              }

                }
            }
            
            dout.write(("QUIT\n").getBytes());
            Receive = (String) din.readLine();
            System.out.println("message= " + Receive);

            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}