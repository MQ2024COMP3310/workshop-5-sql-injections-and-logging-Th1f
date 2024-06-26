package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("//Users//irvinhansen//Desktop//University//COMP3310//workshop-5-sql-injections-and-logging-Th1f//resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }
    
    private static final Logger log = Logger.getLogger(App.class.getName());

    public static boolean hasSymbol(String s){
        String reg = "0-9|!@#$%^&*()-_=+[\\]{};:'\"<>,.?\\/\\|~";
        for (int i = 0; i < s.length(); i++) {
        char currentChar = s.charAt(i);
            if (reg.indexOf(currentChar) != -1) {
                return true; 
            }
        }
        return false;
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("//Users//irvinhansen//Desktop//University//COMP3310//workshop-5-sql-injections-and-logging-Th1f//resources//data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                wordleDatabaseConnection.addValidWord(i, line);
                logger.log(Level.INFO,"DATABASE ID:"+i+" WORD:"+line);
                i++;
            }

        } catch (IOException e) {
            System.out.println("Not able to load . Sorry!");
            System.out.println(e.getMessage());
            System.out.println("test");
            logger.log(Level.WARNING,"Exception",e);
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess+"'.");
                
                
                if(guess.length() == 4 && !hasSymbol(guess)){
                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                        
                        System.out.println("Success! It is in the the list.\n");
                    }else{
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                    }
                }else{
                    logger.log(Level.WARNING,"User has guessed" + guess);
                    System.out.println("Sorry. Please enter a 4 LETTER WORD .\n");
                }

                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING,"Exception",e);
            e.printStackTrace();
        }

    }
}