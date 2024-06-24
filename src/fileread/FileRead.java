/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileread;
import java.io.*;


/**
 *
 * @author Jeff Pitts <japitts59@hotmail.com>
 */
public class FileRead {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length == 0)
        {
            System.out.println("USAGE: java -jar FileRead.jar <file list>");
        }
        else
        {
            for(String s: args)
            {
                String fileName = s; 
                String fileWrite = fileName + ".csv";
                String line = null;               
                String bigLoop = null;
                String[] big = null;
                String n1Loop = null;
                String[] n1 = null;
                String ctt = null; // Used to iterate through the line items
                String itdLoop = null;
                String[] itd = null;

                String invDate = null;
                String invNum = null;
                String PODate = null;
                String PONum = null;
                String drCr;
                String store = null; // Used to store the store number


                String termsNetDay = null;
                String termsNetDueDate = null;
                String tds = null;
                Float  tdsFloat;

                            
                try {
                    FileReader fileReader = new FileReader(fileName);
                    FileWriter fileWriter = new FileWriter(fileWrite);
                    /* Add code to remove CSV files if they exist */
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    while ((line = bufferedReader.readLine()) != null) {
                        /* Parse the BIG segment */
                        int begin = line.indexOf("BIG");
                        int end = line.indexOf("~", begin);
                        bigLoop = line.substring(begin, end);

                        big = bigLoop.split("\\*");
                        invDate = big[1];
                        invNum = big[2];
                        PODate = big[3];
                        PONum = big[4];
                        drCr = big[7];

                        System.out.println("Inv Date:" + invDate);
                        System.out.println("Inv Num:" + invNum);
                        System.out.println("PO Date:" + PODate);
                        System.out.println("PO Num:" + PONum);
                        System.out.println("drCr: " + drCr);

                        /* Parse the N1*BY segment for the Cardinal account number*/
                        begin = line.indexOf("N1*BY");
                        end = line.indexOf("~", begin);
                        n1Loop = line.substring(begin, end);
                        n1 = n1Loop.split("\\*");
                        store = n1[4];
                        System.out.println("Store #: " + store);
                        

                        /* Parse the ITD segment */
                        begin = line.indexOf("ITD");
                        end = line.indexOf("~", begin);
                        itdLoop = line.substring(begin, end);

                        itd = itdLoop.split("\\*");

                    	termsNetDueDate = itd[6];    
                        termsNetDay = itd[7];

                        System.out.println("Terms Net Due Date: " + termsNetDueDate);
                        System.out.println("Terms Net Day: " + termsNetDay);

                        /* Parse the TDS segment */
                        begin = line.indexOf("TDS*");
                        end = line.indexOf("~", begin);
                        tds = line.substring(begin+4, end);
                        tdsFloat = Float.parseFloat(tds)/100;
                        System.out.println(tdsFloat);


                        /* Parse the CTT segment */
                        begin = line.indexOf("CTT*");
                        end = line.indexOf("~", begin);

                        // This is how many IT1 segments we have 
                        ctt = line.substring(begin+4, end); 
                        System.out.println("CTT: " + ctt);
                        int oldend = line.indexOf("IT1"); //Used to keep jumping forward

                        
                        /* Iterate over the IT1's */
                        for(int i = 0; i < Integer.parseInt(ctt); i++)
                        {
                            /* Declarations */
                            String it1Loop = null;
                            String[] it1 = null;
                            String qty = null;
                            String unit = null;
                            String unitPrice = null;
                            String upc = null;
                            String ndc = null;
                            String cin = null;
                            String pidLoop = null;
                            String[] pid = null;
                            String prodDesc = null;

                            begin = line.indexOf("IT1", oldend);
                            end = line.indexOf("~", begin);
                            it1Loop = line.substring(begin, end);
                            it1 = it1Loop.split("\\*");
                            /* Parse each IT1 into respective parts */
                            qty = it1[2];
                            unit = it1[3];
                            unitPrice = it1[4];
                            upc = it1[7];
                            ndc = it1[9];
                            cin = it1[11];
                            /* Parse PID loop */                            
                            begin = line.indexOf("PID", oldend);
                            end = line.indexOf("~", begin);
                            pidLoop = line.substring(begin, end);
                            pid = pidLoop.split("\\*");
                            prodDesc = pid[5];
                            
                            System.out.println("Order: " + PONum + ", Store: " + store + ", QTY: " + qty + ", Unit: " + unit + ", Unit Price: " + unitPrice + ", UPC: " + upc + 
                                ", NDC: " + ndc + ", CIN: " + cin + ", Prod Desc: " + prodDesc);


                            System.out.println("Total: " + tds);

                            /* Write the file */
                            bufferedWriter.write(store);
                            bufferedWriter.write("|");
                            bufferedWriter.write(PONum);
                            bufferedWriter.write("|");
                            bufferedWriter.write(ndc);
                            bufferedWriter.write("|");
                            bufferedWriter.write(cin);
                            bufferedWriter.write("|");
                            if(drCr.contentEquals("CR"))
                                bufferedWriter.write("-"+qty);
                            else
                                bufferedWriter.write(qty);
                            bufferedWriter.write(System.lineSeparator());

                            /* Jump to the next IT1 */
                            oldend = end;
                        }
                    }
                    /* Close out files */
                    bufferedReader.close();
                    bufferedWriter.close();

                } catch (IOException x) {
                     System.err.format("IOException: %s%n", x);
                }
            }
        }
    }
}
