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


            while(!str.contains("NONE")){
            dout.write(("REDY\n").getBytes());
            str = (String) din.readLine();
            System.out.println("message = " + str);
            dout.write(("OK\n").getBytes());
            str = (String) din.readLine();
            System.out.println("message = " + str);
            }
            
            dout.write(("QUIT\n").getBytes());
            str = (String) din.readLine();
            System.out.println("message= " + str);

            dout.close();
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }



    public static void Find(String str) {
        String tempCore="";
        int Core=0;
        String tempname="";

        String []arrofStr=str.split(" ");
        tempCore=arrofStr[4];
        
    }
}
