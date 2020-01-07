
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Liuxuan Huang
 */
public class Refine {
    private PrintWriter pw = null;
    public Refine() {}
    public Refine(File oldFile, File newFile){
        // declare a scanner to read the file and a printwriter to write to new file
        try (Scanner scanner = new Scanner(oldFile)) {
            pw = new PrintWriter(newFile);
            scanner.useDelimiter(",");
            // loop line by line
            while(scanner.hasNext()){
                String data = scanner.next();
                // For empty columns, get the next number, remove the left quote, and write to file
                if (data.equals("")) {
                    data = scanner.next();
                    // remove the left quote from the number
                    pw.write(data.substring(1) + ",");
                    // put the left quote back to the movie name
                    pw.write("\"");
                    data = scanner.next();
                    data = CapitalizeMovieName(data);
                }
                // write to file
                pw.write(data + ",");
            }
            pw.close();
            scanner.close();
            // catch exception
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    
    // capitalizing first letter of each word in movie name given
    private String CapitalizeMovieName(String data){
        StringBuilder string = new StringBuilder();
        try (Scanner scanner = new Scanner(data)){ // new scanner for the movie name only
            scanner.useDelimiter(" ");
            while (scanner.hasNext()) {
                String word = scanner.next();
                word = word.substring(0,1).toUpperCase() + word.substring(1);
                string.append(word).append(" ");
            }
            // remove the last space
            string.deleteCharAt(string.length() - 1);
        }
        return (string.toString());
    }

    // sparsing data from file given into hashmap
    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> ParseData(File file, String[] movieMap){
        HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> map = new HashMap<>();
        String data, mName, word;
        int index = 0, age = 0, mid = 0, rating = 0;

        // declare a scanner to read the file
        try (Scanner scanner1 = new Scanner(file)) {
            scanner1.useDelimiter("\r\n");
            // loop line by line
            scanner1.next();
            while(scanner1.hasNext()){
                // skip the first line.
                data = scanner1.next();
                // declare another scanner to read the line, loop word by word
                Scanner scanner2 = new Scanner(data);
                scanner2.useDelimiter(",");
                while(scanner2.hasNext()) {
                    word = scanner2.next();
                    switch (index) {
                        // skip the header in file
                        case (2): // userAge
                            age = Integer.parseInt(word);
                            break;
                        case (3): // movieID
                            mid = Integer.parseInt(word);
                            break;
                        case (4): // movieName
                            mName = word;
                            movieMap[mid] = mName;
                            break;
                        case (5): // rating
                            //System.out.println(word);
                            rating = Integer.parseInt(word);
                            // put everything to map
                            // check if entry already exists
                            if (map.containsKey(age)) {
                                if (map.get(age).containsKey(rating)) {
                                    map.get(age).get(rating).add(mid); // add the movie if already exists
                                }
                                else {
                                    // declare a new list if not
                                    ArrayList<Integer> movieList = new ArrayList<>();
                                    movieList.add(mid);
                                    map.get(age).put(rating, movieList);
                                }
                            }
                            else {
                                // declare a new inner map for rating
                                HashMap<Integer, ArrayList<Integer>> inner = new HashMap<>();
                                ArrayList<Integer> movieList = new ArrayList<>();
                                movieList.add(mid);
                                inner.put(rating, movieList);
                                map.put(age, inner);
                            }
                            index = -1; // proceed to next line
                            break;
                        default: // userID, userName
                            break;
                    }
                    index ++;
                }
            }
            scanner1.close();
            // catch exception
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return map;
    }
}


