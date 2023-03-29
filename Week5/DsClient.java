import java.io.*;
import java.net.*;
import java.util.*;

public class DsClient {
    private static final int PORT = 50000;
    private static final String SERVER_ADDRESS = "localhost";
    private static Socket socket;
    private static BufferedReader input;
    private static PrintWriter output;
    private static int currentTime = 0;
    private static Map<Integer, Job> jobs = new HashMap<Integer, Job>();
    private static Map<Integer, Server> servers = new HashMap<Integer, Server>();
    private static Queue<Job> jobQueue = new LinkedList<Job>();

    public static void main(String[] args) {
        try {
            // establish connection with server
            socket = new Socket(SERVER_ADDRESS, PORT);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // receive system information from server
            receiveSystemInfo();

            // receive jobs from server and schedule them
            while (true) {
                String line = input.readLine();
                if (line.equals("EOS")) {
                    break;
                }
                jobs.put(job.getId(), job);
                scheduleJob(job);
            }

            // close connection with server
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveSystemInfo() throws IOException {
        String line = input.readLine();
        while (!line.equals("RUN")) {
            String[] tokens = line.split("\\s+");
            if (tokens[0].equals("server")) {
                int id = Integer.parseInt(tokens[1]);
                int type = Integer.parseInt(tokens[2]);
                int limit = Integer.parseInt(tokens[3]);
                int bootupTime = Integer.parseInt(tokens[4]);
                float rate = Float.parseFloat(tokens[5]);
                Server server = new Server(id, type, limit, bootupTime, rate);
                servers.put(id, server);
            }
            line = input.readLine();
        }
        currentTime = Integer.parseInt(line.split("\\s+")[1]);
    }

    private static void scheduleJob(Job job) {
        jobQueue.offer(job);
        if (jobQueue.size() == 1) {
            dispatchJob();
        }
    }

    private static void dispatchJob() {
        Job job = jobQueue.peek();
        Server largestServer = null;
        for (Server server : servers.values()) {
            if (server.canServe(job) && (largestServer == null || server.getType() > largestServer.getType())) {
                largestServer = server;
            }
        }
        if (largestServer != null) {
            largestServer.serve(job);
            jobQueue.poll();
            output.println("SCHD " + job.getId() + " " + largestServer.getId() + " " + currentTime);
        } else {
            System.out.println("No server available for job " + job.getId());
        }
    }
}


