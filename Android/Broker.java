package com.example.myapplication;
//Tsopelas Efthimios 3130210
//Benos Anastasios 3130141
//Skoutelis Evangelos 3130188



import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.*;
import java.util.*;
import java.net.*;

import static java.util.Collections.*;

// Broker ( Server ) class
public class Broker extends Thread {

    public static List<Value> values = Collections.synchronizedList(new ArrayList<Value>());        //Create the list that all the Brokers will sent the items which they would recieve from the publishers

    public int ServerPort;

    public Broker(int ServerPort) {        // Constractor
        this.ServerPort = ServerPort;
    }      //Constractor

    public static void main(String[] args) throws IOException {

        for (int i = 0; i < Constants.numOfBrokersInThisPc; i++) {    //Creates the number of threads for the Brokers that we want to run in this PC
            System.out.println("Waiting for connections... I am the Broker in ip : " + Constants.this_ip + " with port : " + (Constants.StartingPort + i));
            Thread nhma = new Thread(new Broker(Constants.StartingPort + i));
            nhma.start();
            try {
                nhma.join();
            } catch (Exception e) {
                System.out.println("Interrupted");
            }
        }
    }

    @Override
    public void run() {                       // Create and starts 2 threads : the one to wait the publisher Clients and the other to wait the Consumers clients
synchronized (this) {
    new Thread(new Runnable() {
        @Override
        public void run() {
            ServerSocket s = InitializeAserverSocket(ServerPort);        //Initialize the port that broker would listen for publisher calls
            waitForPublishers(s);

        }
    }).start();

    new Thread(new Runnable() {
        @Override
        public void run() {
            ServerSocket c = InitializeAserverSocket(ServerPort + Constants.ServerSocket_Gap);      //Initialize the port that broker would listen for consumer calls
            waitForConsumers(c);

        }
    }).start();
}
        }


    public void waitForPublishers(ServerSocket pp) {        //waiting for publisher connection method which calls the PublisherHandler method

            try {
                Socket p = null;
                while (true) {

                    // socket object to receive incoming publisher requests
                    p = pp.accept();
                    // obtaining input and out streams
                    ObjectOutputStream dos = new ObjectOutputStream(p.getOutputStream());
                    ObjectInputStream dis = new ObjectInputStream(p.getInputStream());
                   // System.out.println("Assigning new thread for the publisher and I am the Broker in port : " + this.ServerPort);

                    // create a new thread object
                    Thread t = new PublisherHandler(p, dis, dos);         // Creates a PublisherHandler thread with the correct parameters
                    // Invoking the start() method
                    t.start();
                    t.join();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    public void waitForConsumers(ServerSocket cc) {                    //waiting for consumer connection method which calls the ConsumerHandler method
            try {
                Socket c = null;
                while (true) {
                    // socket object to receive incoming client requests
                    c = cc.accept();
                    ObjectOutputStream out = new ObjectOutputStream(c.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(c.getInputStream());
                    //System.out.println("Assigning new thread for this consumer and i am the Broker in port : " + (this.ServerPort + Constants.ServerSocket_Gap));

                    Thread t2 = new ConsumerHandler(c, in, out);                // Creates a ConsumerHandler thread with the correct parameters
                    // Invoking the start() method
                    t2.start();
                    t2.join();
                }

            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    public ServerSocket InitializeAserverSocket(int brokerPort) {
        ServerSocket s = null;
        try {
            s = new ServerSocket(brokerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }


    // PublisherHandler class
    class PublisherHandler extends Thread {

        final ObjectInputStream dis;
        final ObjectOutputStream dos;
        final Socket s;
        // Constructor
        public PublisherHandler(Socket s, ObjectInputStream dis, ObjectOutputStream dos) {
            this.s = s;
            this.dis = dis;
            this.dos = dos;
        }

        @Override
        public void run() {
                try {
                    System.out.println("Data loading to brokers ...  ");
                    dos.writeObject("Broker says : You are connected succesfully! in the Broker on port : " + s.getLocalPort());
                    dos.flush();

                    while (true) {
                        Object a = dis.readObject();
                        //ulopoihsh pull
                        if (a instanceof Value) {
                            Value v = (Value) a;

                            values.add(v);
                           // System.out.println("Line ID : " + v.lineNumber + " Lat : " + v.lat + " Lon : " + v.lon + " Name : " + v.lineName);

                        } else if (a instanceof String) {
                            if (a.equals("Finished")) {
                                break;
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

        }
        }


    // ConsumerHandler class
    class ConsumerHandler extends Thread {

        final ObjectInputStream in;
        final ObjectOutputStream out;
        final Socket c;

        // Constructor
        public ConsumerHandler(Socket c, ObjectInputStream in, ObjectOutputStream out) {
            this.c = c;
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            boolean Find_it = false;

                try {
                    //System.out.println("A new CONSUMER is connected : " + c);
                    out.writeObject("Broker says : You are connected succesfully!!  in the Broker on port : " + c.getLocalPort());
                    out.flush();
                    // receive the answer from client
                    String received = in.readObject().toString();
                    int message = (int) (in.readObject());

                    if (message == 1) {
                        int selected_broker_ip = Broker_Decider(received);
                        out.writeObject(selected_broker_ip);
                        out.flush();
                    }

                    received = in.readObject().toString();
                    message = (int) (in.readObject());

                    if (message == 0) {

                        System.out.println("-----------------------------------------");
                        System.out.println("Consumer wants the result of topic : " + received);
                        int route = (int) in.readObject();
                        System.out.println("Consumer wants the route : " + route);

                        int count = 0;
                        for (Value item : values) {
                            if ((item.busLineID.equals(received)) && (item.routeCode.equals(String.valueOf(route)))) {
                                count++;
                                Find_it = true ;
                            }
                        }
                        out.writeObject(count);

                        for (Value item2 : values) {

                            if ((item2.busLineID.equals(received)) && (item2.routeCode.equals(String.valueOf(route)))) {
                                out.writeObject(item2);
                                out.writeObject("ok");
                            }
                        }

                        if(Find_it == false){
                            Value x = new Value();
                            out.writeObject(x);
                            out.writeObject("Fail");
                        }else{
                            System.out.println("Info sent to requester Consumer !!!");
                            System.out.println("-----------------------------------------");
                        }
                        out.flush();
                    }

                } catch (IOException e) {
                   // e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    //e.printStackTrace();
                }
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
                return temp2;
            }
        }
        if (flag == false){
            return itr3.next();
        }
        return -1;
    }


    }





