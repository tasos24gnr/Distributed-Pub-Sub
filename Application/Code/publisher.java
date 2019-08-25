//Tsopelas Efthimios 3130210
//Benos Anastasios 3130141
//Skoutelis Evangelos 3130188

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.lang.Integer.parseInt;



public class publisher extends Thread {



    static List<Bus> leofwreia = new ArrayList<>();
    static List<Topic> topics = new ArrayList<>();                   //initialize the lists to save the objects from the reading files
    static List<Route> routs = new ArrayList<>();
    static List<SocketVar> sockets = new ArrayList<>();

    public int PublisherCode;

    public publisher(int PublisherCode){           // Constractor
        this.PublisherCode=PublisherCode;
    }          //Constractor

    public static void main(String[] args) {
        for (int i = 0; i < Constants.numberOfPublishersInThisPC; i++) {
           Thread nhma =  new Thread(new publisher(i));
           nhma.start();
            try {
                nhma.join();
            } catch (Exception e) {
                System.out.println("Interrupted");
            }
        }

        System.out.println("Sorted pointers for the Brokers based on their hashing ips + ports : ");
        for (Integer key : Constants.sortedBrokersMap.values()) {
            System.out.println(key);
        }
    }


    @Override
    public void run()  {
        synchronized(this) {
            try {
              List<SocketVar>  socketPerPublishers = ConnectWithBroker();

              if(this.PublisherCode==0) {
                  readBusLines();
                  readRouteCodes();
              }
            try{
                readBusPositions(socketPerPublishers);

                CloseConnections(socketPerPublishers);

            } catch(IOException ie) {
                ie.printStackTrace();
            }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }


    public void CloseConnections(List<SocketVar> socketPerPublishers) throws IOException {
        for (int p = 0; p < socketPerPublishers.size(); p++) {
            socketPerPublishers.get(p).out.writeObject("Finished");
        }
    }


    public  List<SocketVar> ConnectWithBroker() throws UnknownHostException{
         List<SocketVar> socketsPerPUB = new ArrayList<>();
        for (int b = 0; b < Constants.Ips.size(); b++) {
            for (int c = 0; c < Constants.NumberOfBrokersPerIp.get(b); c++){
                try {
                    Socket ps = new Socket(Constants.Ips.get(b), Constants.StartingPort + c);
                    ObjectOutputStream dos = new ObjectOutputStream(ps.getOutputStream());
                    ObjectInputStream dis = new ObjectInputStream(ps.getInputStream());

                    String received = (String)dis.readObject();
                    System.out.println(received);

                    SocketVar S = new SocketVar(ps,dos,dis);
                    sockets.add(S);
                    socketsPerPUB.add(S);


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return socketsPerPUB;
    }


    public static void readBusLines() {      //Read the busLines file
        // The name of the file to open.
        String fileName = "busLinesNew.txt";
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            leofwreia = new ArrayList<>();
            topics = new ArrayList<>();     // Create a topic list

            while((line = bufferedReader.readLine()) != null) {

                String[] parts = line.split(",");          // Splits the line
                String LineCode = parts[0];
                String LineId = parts[1];
                String DescriptionEnglish = parts[2];

                Topic topic = new Topic(null);
                topic.setBusLine(LineId);
                topics.add(topic);

                Bus bus = new Bus(LineCode, DescriptionEnglish, LineId);
                leofwreia.add(bus);

            }
            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '" + fileName + "'");
        }
    }



        public void readBusPositions(List <SocketVar> socketPerPublishers) throws IOException {
        int position2 = 0;
        int count = 0;

        while (position2 < Constants.Flags2.size()){
            if(Constants.Flags2.get(position2)==TRUE){
                position2++;
            }else{

                String objects = Constants.StringForPublishers2.get(position2);
                String[] lines = objects.split("\n");

                for (String var : lines) {

                    String[] parts = var.split(",");          // Splits the line
                    String LineCode = parts[0];
                    int RouteCode = Integer.parseInt(parts[1]);
                    String VehicleID = parts[2];
                    String RouteType = null;
                    String BusLineName = null;

                    String Lat = parts[3];
                    String Lon = parts[4];
                    double lat = Double.parseDouble(Lat);
                    double lon = Double.parseDouble(Lon);
                    String time = parts[5];

                    int j = 0;
                    while (j < routs.size() ) {

                        if (RouteCode == routs.get(j).RouteCode) {
                            RouteType = routs.get(j).RouteType;
                            BusLineName = routs.get(j).lineName;
                            j = routs.size();
                        }
                        j++;
                    }

                    int i = 0;
                    while (i < leofwreia.size() ) {


                        if (leofwreia.get(i).getLineNumber().equals(LineCode)) {

                            String Bus_lineID = leofwreia.get(i).busLineID;
                            Bus bus = new Bus(LineCode, RouteType, VehicleID, BusLineName, Bus_lineID, time);
                            Value value = new Value(LineCode, RouteType, VehicleID, BusLineName, Bus_lineID, time, lat, lon);


                            int pointer = Broker_Decider(bus.lineNumber);
                            int position = (pointer / 10) - 1;
                            InetAddress br_ip = Constants.Ips.get(position);
                            int br_port = Constants.StartingPort + (pointer % 10);


                            for (int p = 0; p < socketPerPublishers.size(); p++) {
                                if (br_ip == socketPerPublishers.get(p).s.getInetAddress() && br_port == socketPerPublishers.get(p).s.getPort()) {

                                    socketPerPublishers.get(p).out.writeObject(value);
                                    count++;
                                }
                            }
                            i = leofwreia.size();
                        }
                        i++;
                    }
                }
                Constants.Flags2.set(position2,TRUE);
                break;
            }
        }
        System.out.println(" --------------------------------- ");
    }


    public static void readRouteCodes() {                                             //Read the routCodes file
        // The name of the file to open.
        String fileName = "RouteCodesNew.txt";
        // This will reference one line at a time
        String line = null;
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            routs = new ArrayList<>();

            while((line = bufferedReader.readLine()) != null) {

                String[] parts = line.split(",");      // Splits the line

                int RouteCode= parseInt(parts[0]);
                int LineCode = parseInt( parts[1]);
                String RouteType = parts[2];
                String DecsriptionEnglish = parts[3];

                Route route = new Route(RouteCode, LineCode, RouteType, DecsriptionEnglish);
                routs.add(route);
            }
            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '" + fileName + "'");
        }
    }


    public static void CloseConnection() throws IOException {
        for ( SocketVar var : sockets){
            var.out.writeObject("Finished");
            var.out.flush();
        }
    }


    public static int Broker_Decider(String LineCode){

        BigInteger LineCodeHash=new BigInteger(Constants.HashTopic(String.valueOf(LineCode)),16);

        Iterator<BigInteger> itr = Constants.sortedBrokersMap.keySet().iterator();
        Iterator<Integer> itr2 = Constants.sortedBrokersMap.values().iterator();
        Iterator<Integer> itr3 = Constants.sortedBrokersMap.values().iterator();
        BigInteger temp ;
        Integer temp2 ;

        boolean flag = false ;

        while (itr.hasNext()){
            temp = itr.next();
            temp2 = itr2.next();
            if(LineCodeHash.compareTo(temp)==-1){
                flag = true;
               // System.out.println(temp2);
                return temp2;
            }
        }
        if (flag == false){
            return itr3.next();
        }

        return -1;
    }


    public static class SocketVar{
        public Socket s;
        public ObjectOutputStream out;
        public ObjectInputStream in ;

        public SocketVar(Socket s , ObjectOutputStream out , ObjectInputStream in){
            this.s = s ;
            this.out = out;
            this.in = in;
        }
    }

    }