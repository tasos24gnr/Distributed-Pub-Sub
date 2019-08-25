//Tsopelas Efthimios 3130210
//Benos Anastasios 3130141
//Skoutelis Evangelos 3130188


// Java implementation for a client
// Save file as Client.java
package com.example.myapplication;


import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client Cosumer class
public class Consumer {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        int selected_broker;

        Scanner reader = new Scanner(System.in);  // Reading the topic and the route from user

        System.out.println("Enter the Line Code for the Bus Line that you are interested in : ");
        String topic = reader.next(); // Scans the next token of the input
        System.out.println("Enter your Route Code  : ");
        int routCode = reader.nextInt();

        reader.close();

        SocketVar S = ConnectWithBroker();

        String received = (String)S.in.readObject();
        System.out.println(received);

        S.out.writeObject(topic);
        int one = 1;
        S.out.writeObject(one);

        int received2 =(int)(S.in.readObject());
        System.out.println("Selected broker pointer : " + received2);
        System.out.println("Connecting to the broker that has the requesting Line Id");

        Value result2 ;
        String result5;

        System.out.println("--------------------------");

        if(received2!= Constants.sortedBrokersMap.firstEntry().getValue()){

            int pointer = received2;
            int position = (pointer / 10) - 1;
            InetAddress br_ip = Constants.Ips.get(position);
            int br_port = Constants.StartingPort + (pointer % 10);




            Socket c = new Socket(br_ip, br_port + Constants.ServerSocket_Gap);
            ObjectOutputStream out = new ObjectOutputStream(c.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(c.getInputStream());

            String received3 = (String) in.readObject();
            System.out.println(received3);

            out.writeObject(topic);
            int zero = 0;
            out.writeObject(zero);

            out.writeObject(topic);
            out.writeObject(zero);
            out.writeObject(routCode);
            out.flush();
            int loop = (int)in.readObject();


            result2 = (Value) in.readObject();
             result5 = (String) in.readObject();
            if(result5.equals("Fail")){
                System.out.println("The specific Line Id and route doesn't exist !! ");
            }else {
                for (int x = 1; x < loop; x++) {

                    System.out.println("Line Code : " + result2.lineNumber + ", Lat : " + result2.lat +        //printing the results
                            ", Lon : " + result2.lon + ", Line Description : " + result2.lineName +
                            " info : " + result2.info);

                    result2 = (Value) in.readObject();
                    result5 = (String) in.readObject();

                }
            }
        }else if(received2== Constants.sortedBrokersMap.firstEntry().getValue()) {

            S.out.writeObject(topic);
            int zero = 0;
            S.out.writeObject(zero);
            S.out.writeObject(routCode);
            S.out.flush();

            int loop2 = (int)S.in.readObject();


            result2 = (Value) S.in.readObject();
            result5 =(String) S.in.readObject();
            if(result2.equals("Fail")){
                System.out.println("The specific Line Id and route doesn't exist !! ");
            }else {
            for(int x=1; x<loop2; x++) {

                System.out.println("Line Code : " + result2.lineNumber + ", Lat : " + result2.lat +        //printing the results
                        ", Lon : " + result2.lon + ", Line Description : " + result2.lineName +
                        " info : " + result2.info);
                result2 = (Value) S.in.readObject();
                result5 = (String) S.in.readObject();

            }


        }

    }


            }

            public static SocketVar ConnectWithBroker() throws IOException {
                int pointer = Constants.sortedBrokersMap.firstEntry().getValue();
                int position = ( pointer / 10 ) - 1 ;
                InetAddress ip =  Constants.Ips.get(position);
                int port =  Constants.StartingPort + (pointer % 10) ;

                Socket c = new Socket(ip, port + Constants.ServerSocket_Gap);

                // obtaining input and out streams
                ObjectOutputStream out = new ObjectOutputStream(c.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(c.getInputStream());

                SocketVar var = new SocketVar(c,out,in);

                return  var ;


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



