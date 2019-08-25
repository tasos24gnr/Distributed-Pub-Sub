//Tsopelas Efthimios 3130210
//Benos Anastasios 3130141
//Skoutelis Evangelos 3130188


import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Collections;

public class Constants {




     public static int numberOfTotalBrokers = 9 ;           //Number of Total brokers in the system
     public static int numberOfTotalPublishers = 9 ;         //Number of Total publishers in the system
     public static int numberOfTotalComputers = 1 ;          //Number of Total computers in the system                     <------------------------------------------------------------------------
     public static int numberOfPublishersInThisPC = 9;      //Number of Publishers that would run in this pc
     public static int numOfBrokersInThisPc = 9;            //Number of Brokers that would run in this PC



     public static int numberOfLinesThatPubWillRead1 = 0;                     //The number of lines that publishers will read from the files
     public static int numberOfLinesThatPubWillRead2 = 0;
     public static int numberOfLinesThatPubWillRead3 = 0;

     public static int StartingPort = 7000 ;
     public static int ServerSocket_Gap = 2000;



    public static InetAddress this_ip = null;              //Mentain the localhost IP
    static {
        try {
            this_ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


     static int count;
     public static List<InetAddress> Ips = new ArrayList<InetAddress>(numberOfTotalComputers);                        // List for the computer's Ips in the system
     public static List<Integer> NumberOfBrokersPerIp = new ArrayList<Integer>(numberOfTotalComputers);              // List for the numbers of Brokers in a specific computer ( ip ) in the system
     public static List<Integer> NumberOfPublishersPerIp = new ArrayList<Integer>(numberOfTotalComputers);          // List for the numbers of Publishers in a specific computer ( ip ) in the system
     static List<BigInteger> HashingBrokers = new ArrayList<>();
     static List<Integer> BrokersPointers = new ArrayList<>();
     static TreeMap<BigInteger, Integer> sortedBrokersMap = new TreeMap<BigInteger,Integer>();



     public static List<String>  StringForPublishers1 = new ArrayList<String>(numberOfTotalPublishers);              // Lists that mentain the txt's informasion so the publishers can read by a distributional way
     public static List<Boolean> Flags1 = new ArrayList<Boolean>(numberOfTotalPublishers);

     public static List<String>  StringForPublishers2 = new ArrayList<String>(numberOfTotalPublishers);
     public static List<Boolean> Flags2 = new ArrayList<Boolean>(numberOfTotalPublishers);

     public static List<String>  StringForPublishers3 = new ArrayList<String>(numberOfTotalPublishers);
     public static List<Boolean> Flags3 = new ArrayList<Boolean>(numberOfTotalPublishers);


        static{
        for (int i = 0; i < numberOfTotalPublishers; i++) {         // initialize the boolean lists
            Flags2.add(i, Boolean.FALSE);
        }




        try {
             Ips.add(0, InetAddress.getByName("localhost"));                        // <-----------------------------------------------------------------------------

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


             NumberOfBrokersPerIp.add(0, 9);                               // <-------------------------------------------------------------------------------
             // NumberOfBrokersPerIp.add(1, x);


             NumberOfPublishersPerIp.add(0, 9);                            // <-------------------------------------------------------------------------------
             //NumberOfPublishersPerIp.add(1, x);




      int CountingLines2 = CountTheLines("busPositionsNew.txt");

      int lastLines2 = CountingLines2 % numberOfTotalPublishers;                     // initialize how many lines the last publisher will have to read extra from the files

      numberOfLinesThatPubWillRead2 = CountingLines2 / numberOfTotalPublishers;       // initialize how many lines will the publishers read from the files

      WhatWillThePublishersRead("busPositionsNew.txt", numberOfLinesThatPubWillRead2, lastLines2, StringForPublishers2);     // initialize the strings lists that publishers will read


      InitializeHashingBrokers(HashingBrokers);
      InitializeBrokersPointers(BrokersPointers);
      InitializeTreeMap(sortedBrokersMap);


  }

    public static int CountTheLines(String NameOfFile){         // Count the lines of the files so that we can distribute the work among the publishers later
        count = 0;
        // The name of the file to open.
        String fileName = NameOfFile;
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }



    public static void WhatWillThePublishersRead(String NameOfFile ,  int numberOfLines , int LastLines , List<String> StringForPublishers){
            // The name of the file to open.
            String fileName = NameOfFile;
            // This will reference one line at a time
            String line = null;
            try {
                // FileReader reads text files in the default encoding.
                FileReader fileReader = new FileReader(fileName);
                // Always wrap FileReader in BufferedReader.
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                for(int i=0; i<numberOfTotalPublishers; i++) {
                    count = 0;
                    String temp = null;

                    if (i == numberOfTotalPublishers - 1){
                        numberOfLines = numberOfLines + LastLines;
                    }

                    while ((line = bufferedReader.readLine()) != null){
                        if(temp==null){
                            temp = line ;
                            count=1;
                        }else {
                            temp = temp + "\n" + line;
                            count++;
                        }

                        if (count == numberOfLines) {
                            StringForPublishers.add(i, temp);
                            break;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static String HashTopic(String md5) {        //Hash Function
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hashInBytes = md.digest(md5.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void InitializeHashingBrokers(List<BigInteger> HashingBrokers ) {                        //INITIALIZE AND SORT THE HASHING VALUES OF IPES AND SPORTS
            int port;
            String ipSINport;
        for (int b = 0; b < Constants.Ips.size(); b++) {
            int count = 0;
            for (int c = 0; c < Constants.NumberOfBrokersPerIp.get(b); c++) {
                port = Constants.StartingPort + count;
                ipSINport = Constants.Ips.get(b).toString().concat(String.valueOf(port));
                String HashingValue = HashTopic(ipSINport);
                BigInteger HashValue= new BigInteger(HashingValue,16);
                HashingBrokers.add(HashValue);
                count++;
            }
        }
    }


    public static void InitializeBrokersPointers(List<Integer> BrokersPointers ){
        for (int b = 0; b < Constants.Ips.size(); b++) {
            for (int c = 0; c < Constants.NumberOfBrokersPerIp.get(b); c++) {
                int pointer = 0;
                pointer = (b+1) * 10 + c ;
                BrokersPointers.add(pointer);
            }
        }
    }

    public static void InitializeTreeMap(TreeMap<BigInteger,Integer> BrokersValues){
            for(int i = 0 ; i < HashingBrokers.size() ; i++){
                BrokersValues.put(HashingBrokers.get(i) , BrokersPointers.get(i));
            }
    }


    }







