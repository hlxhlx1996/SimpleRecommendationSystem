
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Liuxuan Huang
 * liuxuanh@andrew.cmu.edu
 */
public class Main {
    private static final File oldFile = new File("RatingsInput.csv");
    private static final File newFile = new File("RatingsInput_modified.csv");
    private static final File userFile = new File("NewUsers.csv");
    private static final File outputFile = new File("NewUsers_completed.csv");
    private static HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> map = new HashMap<>();
    private static String[] movies = new String[1000];
    
    public static void main(String[] args) {
        // modify the ratings input file
        Refine refine = new Refine(oldFile, newFile);
        map = refine.ParseData(newFile, movies);
//        // print the map
//        for (int age: map.keySet()){
//            System.out.println(age);
//            for (int rating : map.get(age).keySet()) {
//                System.out.println(rating + " " + map.get(age).get(rating));
//            }
//        }
        
        // read the new user file and output the completed new file
        // declare a scanner to read the file and a printwriter to write to new file
        try (Scanner scanner1 = new Scanner(userFile)) {
            PrintWriter pw = new PrintWriter(outputFile);
            scanner1.useDelimiter("\r\n");
            int index = 0, age = 0, num = 0;
            
            pw.write(scanner1.next() + "\r\n"); // write the header first
            // loop line by line
            while(scanner1.hasNext()){
                String line = scanner1.next();
                // loop column by column
                Scanner scanner2 = new Scanner(line);
                scanner2.useDelimiter(",");
                while (scanner2.hasNext()) {
                    String data = scanner2.next();
                    switch (index) {
                        case (1): // age
                            age = Integer.parseInt(data);
                            pw.write(data + ",");
                            break;
                        case (2): // no. of files
                            num = Integer.parseInt(data);
                            pw.write(data + ",");
                            pw.write(recommend(age, num));
                            break;
                        case (3): // movies
                            // proceed to next line
                            index = -1;
                            pw.write("\r\n");
                            break;
                        default:
                            pw.write(data + ",");
                            break;
                    }
                    index ++;
                }
            }
            pw.close();
            scanner1.close();
            // catch exception
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    
    // give recommendation according to input
    public static String recommend(int age, int num) {
        StringBuilder r = new StringBuilder();
        String movieN;
        int counter = 0; // count current recommendation number
        int n = 1;
        r.append("\"");
        // find nearest age until there is an existing key in the dataset
        while (!map.containsKey(age)) {
            age += Math.pow(-1, n)*n;
            n++;
        }
        for (int i = 5; i > 0 ; i--) {
            if (map.get(age).containsKey(i)) {
                // get movie ids from map
                for (int j=0; j < map.get(age).get(i).size(); j++) {
                    if (counter == num) {
                        // remove the last comma
                        r.deleteCharAt(r.length() - 1);
                        r.append("\"");
                        return r.toString();
                    }
                    movieN = movies[map.get(age).get(i).get(j)];
                    r.append(movieN.substring(1, movieN.length()-1)).append(",");
                    counter ++;
                }
            }
        }
        // remove the last comma
        r.deleteCharAt(r.length() - 1); 
        r.append("\"");
        return r.toString();
    }
}