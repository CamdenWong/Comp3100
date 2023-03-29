import java.io.*;
import java.net.*;

public class MyClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("127.0.01", 50000);

            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));

            dout.write(("HELO\n").getBytes());
            String str = (String) din.readLine();
            System.out.println("message = " + str);
            dout.write(("AUTH Camden\n").getBytes());
            str = (String) din.readLine();
            System.out.println("message = " + str);

            dout.write(("REDY\n").getBytes());
            str = (String) din.readLine();
            System.out.println("message = " + str);


            dout.write(("GETS All\n").getBytes());
            str = (String) din.readLine();
            System.out.println("message = " + str);

            dout.write(("OK\n").getBytes());
            str = (String) din.readLine();


            int largestCore= 0;
            int count=0;
            String type="";

            while(true){
                if(str.equals(".")){
                break;
            }
            dout.write(("OK\n").getBytes());
            str = (String) din.readLine();
            String[] arr = str.split(" ");

            
            if(arr.length>1){
            int id=Integer.parseInt(arr[1]);
            int core=Integer.parseInt(arr[4]);
            
            
            if(largestCore==0){
                largestCore=core;
            }else{
                if (core>=largestCore){
                    largestCore=core;
                    count=id;
                    type =arr[0];
                }
            }
            System.out.println("message = " + str);
            }
            }
                for(int j=0;j<=10;j++){
                    for(int i=0;i<=count;i++){
                dout.write(("SCHD"+" "+j+" "+type+" "+i+"\n").getBytes());
               str = (String) din.readLine();
                System.out.println("message = " + str);
            }
                }

            

            dout.write(("LSTQ "+type+"*"+"\n").getBytes());
            str = (String) din.readLine();
            System.out.println("message = " + str);

            dout.write(("OK\n").getBytes());
            str = (String) din.readLine();
            System.out.println("message = " + str);
            // dout.write(("OK\n").getBytes());
            // str = (String) din.readLine();
            // System.out.println("message = " + str);



            
            dout.write(("QUIT\n").getBytes());
            str = (String) din.readLine();
            System.out.println("message= " + str);

            dout.close();
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
