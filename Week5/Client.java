import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {

            Socket s = new Socket("127.0.01", 50000);

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
            Boolean finded= false;

            int j=0;



            while(!Receive.contains("NONE")){

            dout.write(("REDY\n").getBytes());
            Receive = (String) din.readLine();
            // Receive JOBN, JCPL and NONE
            String step10=Receive;

            System.out.println("message = " + Receive);
            if(Receive.contains("JOBN")){
               String[]Jobnarr=Receive.split(" ");
               jobID=Integer.parseInt(Jobnarr[2]);
            }

            if(finded==false){
            dout.write(("GETS All\n").getBytes());
            Receive = (String) din.readLine();
            //Receive DATA nRecs recSize
            System.out.println("message = " + Receive);

            String[]data=Receive.split(" ");
            int nRecs=Integer.parseInt(data[1]);


            dout.write(("OK\n").getBytes());
            for(int i=0;i<nRecs;i++){
            Receive = (String) din.readLine();

            String[] arr = Receive.split(" ");
            int id=Integer.parseInt(arr[1]);
            int core=Integer.parseInt(arr[4]);
            String temp=arr[0];
            
            
            if(largestCore==0){
                largestCore=core;
            }else{
                if (core>largestCore){
                    largestCore=core;
                   
                    type =arr[0];
                    finded=true;
                    count=0;
                }
                if(core==largestCore&&type.equals(temp)){
                    count++;
                }
            }

             System.out.println("message = " + Receive);
             System.out.println(count);

            }
            dout.write(("OK\n").getBytes());
            Receive = (String) din.readLine();
            System.out.println("message = " + Receive);

            
            }
            
           
            
            if(step10.contains("JOBN")){
                dout.write(("SCHD "+jobID+" "+type+" "+j+"\n").getBytes());
                Receive = (String) din.readLine();
                System.out.println("message = " + Receive);

                j++;
              }

                // j++;

                if(j>=count){
                    j=0;
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


            








            