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
                String big = null;
                String n1 = null;
                String ctt = null; // Used to iterate through the line items
                String store = null; // Used to store the store number
                String order = null; // Used to store PO number
                String drCr;
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
                        big = line.substring(begin, end+1);
                        System.out.println(big); 

                        /* Parse the PO number */
                        int dash = big.indexOf("**");
                        System.out.println("**: " + dash);
                        int star = big.indexOf("*", dash+2);
                        System.out.println("Star: " + star);
                        order = big.substring(dash+2, star);
                        System.out.println("Order #: " + order);

                        /* Parse the CR or DR memo flag */
                        int start = -1;
                        start = big.indexOf("CR");
                        if(start == -1)
                            start = big.indexOf("DR");
                        drCr = big.substring(start, start+2);
                        System.out.println("drCr: " + drCr);

                        /* Parse the N1*BY segment */
                        begin = line.indexOf("N1*BY");
                        end = line.indexOf("~", begin);
                        n1 = line.substring(begin, end+1);
                        System.out.println(n1); 

                        /* Parse the Cardinal account number */
                        dash = n1.indexOf("91*");
                        star = n1.indexOf("~");
                        store = n1.substring(dash+3, star);
                        System.out.println("Store #: " + store);

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
                            String it1 = null;
                            String qty = null;
                            String ndc = null;
                            String cin = null;

                            begin = line.indexOf("IT1", oldend);
                            end = line.indexOf("~", begin);

                            /* Parse each IT1 into respective parts */
                            /* QTY */
                            it1 = line.substring(begin, end+1);
                            int starBegin = it1.indexOf("IT1**");
                            int starEnd = it1.indexOf("*", starBegin+5);
                            qty = it1.substring(starBegin+5, starEnd);

                            /* NDC */
                            starBegin = it1.indexOf("N4*");
                            starEnd = it1.indexOf("*VC");
                            ndc = it1.substring(starBegin+4, starEnd);

                            /* CIN */
                            starBegin = it1.indexOf("VC*");
                            starEnd = it1.indexOf("~", starBegin);
                            cin = it1.substring(starBegin+3, starEnd);
                            System.out.println("Order: " + order + ", Store: " + store + ", QTY: " + qty + ", NDC: " + ndc + ", CIN: " + cin);

                            /* Write the file */
                            bufferedWriter.write(store);
                            bufferedWriter.write("|");
                            bufferedWriter.write(order);
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
