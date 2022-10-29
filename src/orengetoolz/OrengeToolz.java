/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orengetoolz;

/*
 * @author Monirujjaman musa
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


 class OrengeToolz implements Runnable{
          public static boolean PhoneValid(String mobNumber){
      //  boolean method returns only true or false             
      // Matching check using regula expression
      if (mobNumber.matches("\\d{10}"))  
        return true;  
      else if (mobNumber.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))  
        return true;  
      else if (mobNumber.matches("\\d{4}[-\\.\\s]\\d{3}[-\\.\\s]\\d{3}"))  
        return true;  
      else if (mobNumber.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))  
        return true;  

      else if (mobNumber.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))  
         return true;  
      else if (mobNumber.matches("\\(\\d{5}\\)-\\d{3}-\\d{3}"))  
        return true;  
     else if (mobNumber.matches("\\(\\d{4}\\)-\\d{3}-\\d{3}"))  
        return true;  
       //return false if any of the input matches is not found  
     else  
     return false;  
        
    }
          public static boolean EmailValid(String Email) {
       //  boolean method returns only true or false   
       // Matching check using regula expression
            String regex = "^(.+)@(.+)$";  
            Pattern pattern = Pattern.compile(regex); 
            Matcher matcher = pattern.matcher(Email); 
        // if matches return true,otherwise false 
         return true;
    }
               
//In my main thread call ( anotherThread.start(); ) , a new thread is created and then the run() method is executed.
    @Override
    public void run(){  

       Connection connection = null;
        //get the start time of full execution
       long start = System.nanoTime();
       //get the start time,end time of only query executiontime calculation 
       long startQueryTime;
       long endQueryTime;
      // Instead of executing a single query, we can execute a batch (group) of queries. 
      //It makes the performance fast.
       int batchSize = 20;  
       try{
       // DriverManager manages the set of Java Database Connectivity
       // DriverManager class attempts to establish a connection to the database by using the given database URL. 
       //DriverManager("database Link,username,password");
       //kindly dont use my uses database
       connection = DriverManager.getConnection("jdbc:mysql://43.243.204.141:3306/prantik","pial","48811558+8802");
       //it saves all the changes that have been done,but here data was not read,
       //so after full reading file then db save all change       
       connection.setAutoCommit(false);
                   
        startQueryTime = System.nanoTime();                
        String sql = "INSERT INTO ot_customer (customer_name, customer_branch, customer_city, customer_state, customer_any_number,customer_phone_number,customer_email,customer_ip) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        endQueryTime = System.nanoTime();                     
          
         //  It is used to execute parameterized query
          PreparedStatement statement = connection.prepareStatement(sql);

                   //To read the file
                    BufferedReader lineReader = new BufferedReader(new FileReader("E:\\1M-customers.txt"));
                   //BufferedReader lineReader = new BufferedReader(new FileReader("E:\\ot\\1m.txt"));

                    //start line reading from biggening       
                    String lineText = null;
                         
                    int count=0;

                   int countAgain=0; 
                  int countPhoneinvalid=0;
                  int countEmailinvalid=0;
                 //  read all line untill nul
               while ((lineText = lineReader.readLine()) != null) {
               
              //Seperate data by comma separetor or CSV                   
                String[] data = lineText.split(",");
               
                // This 4 line for personal analysis               
                int lengthCount=data.length;
                int dataSize=8;  
                System.out.println(countAgain+"---"+data.length);
                System.out.println(data[0]+"---"+data[1]);
                
              // every line data have to store in array
              // array initialization              
                String customerName = data[0];
                String customerBranch = data[1];
                String customerCity = data[2];
                String customerState = data[3];
                
                String customerAnyNumber = data[4];
                String customerPhoneNumber = data[5];
                String customerEmail = data[6];

             //in here naturally every line was 8 data size,but one data was size 7.              
              //the data--(Emileigh,Emery,Sacramento,CA,95823,9166169285,emileighe@yahoo.com,)
             //so forcefully i have entered here 0(zero)
             String customerIp;
                if( lengthCount != dataSize){
                   customerIp="0";  
                }else{
                   customerIp = data[7];
                     }
                
                // now assign data               
                statement.setString(1, customerName);
                statement.setString(2, customerBranch);
                statement.setString(3, customerCity);
                statement.setString(4, customerState);
                statement.setString(5, customerAnyNumber);
                
                // have to validate phone 
                if (PhoneValid(customerPhoneNumber))
                {
                System.out.println("valid Phone Number-"+customerPhoneNumber+"-length-"+customerPhoneNumber.length());
                statement.setString(6, customerPhoneNumber);
                }
                else{
                String testPhone=customerPhoneNumber;
                statement.setString(6, customerPhoneNumber);
                countPhoneinvalid++;
                            }
                // have to validate email            
                if (EmailValid(customerEmail))
                {
                System.out.println("valid Email-"+customerEmail+"-Has @----"+customerEmail.contains("@"));
                statement.setString(7, customerEmail);
                }
                else{
                statement.setString(7, customerEmail);
                countEmailinvalid++;
                }
               
                statement.setString(8, customerIp);
                 

                // used to single statements to a batch
                statement.addBatch();
                if (count % batchSize == 0) {
                    statement.executeBatch();
                }
                countAgain++;
             }
           // stop line reading               
            lineReader.close();
           // begins the execution of all the grouped  query together statements
            statement.executeBatch();
 
            //save data according to query            
            connection.commit();
           //close connection            
            connection.close();
          
            
            System.out.println("count Invalid Phone   "+countPhoneinvalid);
            System.out.println("count Invalid Email   "+countEmailinvalid);
            
            //get the end time for full execution
             long end = System.nanoTime();

            // Time calculation for full execution       
            long execution = (end - start);
            System.out.println("Full Execution time of Data read and store to Db");
            System.out.println(execution + " nanoseconds");
            // execution time in seconds
            double seconds = (double)execution / 1000000000.0;
            System.out.println(seconds + " seconds");
     
            // Time calculation for only Query execution   
            long executionQueryTime = (endQueryTime - startQueryTime);
            System.out.println("Query Execution time");
            System.out.println(executionQueryTime + " nanoseconds");
       
            double secondsofQuery = (double)executionQueryTime / 1000000000.0;
            System.out.println(secondsofQuery + " seconds");
          
       } 
          // from Java 7.0 accept multiple exceptions 
       catch (IOException | SQLException ex) {
            System.err.println(ex);
        }
}  


          //Main thread     
     public static void main(String[] args) {
      
         // anotherThread  .     
        OrengeToolz ot=new OrengeToolz();  
        Thread anotherThread =new Thread((Runnable) ot);   
        anotherThread.start(); 
    }
}

