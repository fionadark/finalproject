package augur;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.Callable;
import com.fasterxml.jackson.databind.ObjectMapper;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.awt.Desktop;

@Command(name = "tarot", mixinStandardHelpOptions = true, version = "tarot 1.0", description = "divines the future")

public class Tarot implements Callable<String> {

    // global scanner for all user input
    public static Scanner scan = new Scanner(System.in);

    @Option(names = { "-t", "--tarot" }, description = "divines the future")

    // method to ask a user if they want to perform another reading
    // if yes, begins another reading by calling .call() on a new instance of Tarot
    public void beginNewReading() {
        String answer;

        System.out.print("Would you like another reading? Enter Y/N: ");
        if (scan.hasNextLine())
            answer = scan.nextLine();
        else
            answer = "N"; // rather than throw an error

        // to start another reading, call .call() on a new instance of Tarot
        if (answer.contains("Y")) {
            Tarot t = new Tarot();
            t.call();
            // end program if the user does not want another reading
        } else {
            System.out.println("\nOkay, goodbye!\n");
        }

    }

    // method that will return a List of type String containing the name_short vars
    // for each Card in a given Spread
    public List<String> getShortNames(Spread curSpread) {
        List<String> names = new ArrayList<String>();

        for (int i = 0; i < curSpread.nhits; i++) {
            names.add(curSpread.cards.get(i).name_short);
        }

        return names;
    }

<<<<<<< HEAD
    // for each name_short in curNames, write the corresponding html tag to
    // index.html
=======
    // function will return true if a card should be reversed, false if it should not
    // assuming cards are drawn in reverse position about 30% of the time
    public boolean reverseTrueOrFalse() {
        Random rand = new Random();
        int min = 1, max = 10;

        int chance = rand.nextInt(max - min + 1) + min;

        if(chance < 3) return true;
        else return false;
    }

    // for each name_short in curNames, write the corresponding html tag to index.html
>>>>>>> 4fcc7425518e6d27e080a7bac5117e718e1736d6
    public void writeToFile(Spread curSpread, String path, List<String> curNames) {

        // truncate exisiting contents of index.html to 0
        File file = new File(path);
        if(file.exists()) file.delete(); 

        // declare String variables and List<String> to write to index.html
        String style = "<style> body { background-color:pink; } table { width:55%; border:0; margin: auto; background-color:white; border-spacing:25px; } </style>";
        String title = "<h1 style=\"color:white; text-align:center; padding-top:25px; padding-bottom:25px;\">Your future awaits...</h1>";
        String table = "<table> <tr> <th>Your Cards</th> <th>Position</th> <th>Meaning</th> </tr>";
        String closeTable = "</table>";
        List<String> rows = new ArrayList<String>();
        String[] position = { "Your Past", "Your Present", "Your Future", "Your Current Challenge", "Your Conscious",
                "Your Subconscious", "The Cards Advice", "Your External Influences", "Your Hopes and Fears",
                "The Outcome" };

        // save each row of the table to rows List<String>
        for (int i = 0; i < curSpread.nhits; i++) {
            String row = "<tr>";

            // check if this card should be reversed
            boolean reversed = reverseTrueOrFalse();

<<<<<<< HEAD
            // the position value of the card depends on the type of spread (# of cards)
            if (curSpread.nhits == 1)
                row += " <td>" + position[2] + "</td>";
            else if (curSpread.nhits == 3 || curSpread.nhits == 10)
                row += " <td>" + position[i] + "</td>";
            else
                row += " <td>The Future </td>";

            // set the meaning value of the row and save to rows
            row += " <td><strong>" + curSpread.cards.get(i).name + "</strong><br><br>This card represents: "
                    + curSpread.cards.get(i).meaning_up + "</td>";
            row += " </tr>\n";
            rows.add(row);
=======
            // convert image to base64 and insert it as the image for the row
            try {
                // get path to correct image
                String newimgname = System.getProperty("user.dir") + "/src/main/resources/images/" + curSpread.cards.get(i).name_short + ".jpg";

                // convert the image to base64
                byte[] imgAsBytes = Files.readAllBytes(Paths.get(newimgname));
                String imgAsBytesString = Base64.getEncoder().encodeToString(imgAsBytes);

                // set the image value of the row using the base64 String
                // if the card is reversed, alter img tag to flip the image 180 degrees
                if(reversed) {
                    row += " <td> <img src=\"data:image/jpg;base64," + imgAsBytesString + "\" style=\"width:150px; height:auto; transform: rotate(180deg);\"> </td>";
                } else {
                    row += " <td> <img src=\"data:image/jpg;base64," + imgAsBytesString + "\" style=\"width:150px; height:auto;\"> </td>";
                }

                // the position value of the card depends on the type of spread (# of cards)
                if(curSpread.nhits == 1) row += " <td>" + position[2] + "</td>";
                else if(curSpread.nhits == 3 || curSpread.nhits == 10) row += " <td>" + position[i] + "</td>";
                else row += " <td>The Future </td>";

                // set the meaning value of the row and save to rows
                // if the card is upright use meaning_up, if it is reversed use meaning_rev
                if(reversed) {
                    row += " <td><strong>" + curSpread.cards.get(i).name + "</strong><br><br>This card reversed represents: " + curSpread.cards.get(i).meaning_rev + "</td>";
                } else {
                    row += " <td><strong>" + curSpread.cards.get(i).name + "</strong><br><br>This card represents: " + curSpread.cards.get(i).meaning_up + "</td>";
                }

                row += " </tr>\n";
                rows.add(row);

            } catch(IOException e) {
                e.printStackTrace();
            }

>>>>>>> 4fcc7425518e6d27e080a7bac5117e718e1736d6
        }

        // write to index.html
        try {
            // begin table
            Files.write(Paths.get(path), style.getBytes(), StandardOpenOption.CREATE);
            Files.write(Paths.get(path), title.getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get(path), table.getBytes(), StandardOpenOption.APPEND);

            // write rows to table
            for (String x : rows) {
                Files.write(Paths.get(path), x.getBytes(), StandardOpenOption.APPEND);
            }

            // close table
            Files.write(Paths.get(path), closeTable.getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // method to print various tarot spreads based on user's choice
    // work in progress... there has to be a more efficient way to do all this
    // printing
    public void tellFuture(Spread curSpread, String userChoice) {

        // if the user has chosen a one-card spread...
        if (userChoice.contains("1")) {
            System.out.println("The card that presents itself to you is " + curSpread.cards.get(0).name + "\n");
            System.out.println("This card represents: " + curSpread.cards.get(0).meaning_rev + "\n");
        }
        // if the user has chosen a three-card spread...
        else if (userChoice.contains("2")) {
            System.out.println("Three cards present themselves to you...\n");
            System.out.println("Your past: " + curSpread.cards.get(0).name);
            System.out.println(curSpread.cards.get(0).meaning_rev + "\n");
            System.out.println("Your present: " + curSpread.cards.get(1).name);
            System.out.println(curSpread.cards.get(1).meaning_rev + "\n");
            System.out.println("Your future: " + curSpread.cards.get(2).name);
            System.out.println(curSpread.cards.get(2).meaning_rev + "\n");
        }
        // if the user has chosen a celtic cross...
        else if (userChoice.contains("3")) {
            System.out.println("Ten cards present themselves to you...\n");
            System.out.println("Your present: " + curSpread.cards.get(0).name + "\nThis card represents: "
                    + curSpread.cards.get(0).meaning_up + "\n");
            System.out.println("Your current challenge: " + curSpread.cards.get(1).name + "\nThis card represents: "
                    + curSpread.cards.get(1).meaning_up + "\n");
            System.out.println("Your past: " + curSpread.cards.get(2).name + "\nThis card represents: "
                    + curSpread.cards.get(2).meaning_up + "\n");
            System.out.println("Your future: " + curSpread.cards.get(3).name + "\nThis card represents: "
                    + curSpread.cards.get(3).meaning_up + "\n");
            System.out.println("Your conscious: " + curSpread.cards.get(4).name + "\nThis card represents: "
                    + curSpread.cards.get(4).meaning_up + "\n");
            System.out.println("Your subconscious: " + curSpread.cards.get(5).name + "\nThis card represents: "
                    + curSpread.cards.get(5).meaning_up + "\n");
            System.out.println("The cards' advice: " + curSpread.cards.get(6).name + "\nThis card represents: "
                    + curSpread.cards.get(6).meaning_up + "\n");
            System.out.println("Your external influences: " + curSpread.cards.get(7).name + "\nThis card represents: "
                    + curSpread.cards.get(7).meaning_up + "\n");
            System.out.println("Your hopes and fears: " + curSpread.cards.get(8).name + "\nThis card represents: "
                    + curSpread.cards.get(8).meaning_up + "\n");
            System.out.println("Your outcome: " + curSpread.cards.get(9).name + "\nThis card represents: "
                    + curSpread.cards.get(9).meaning_up + "\n");

        }
        // if the user has entered another answer
        else {
            System.out.println("No cards present themselves to you. Your future remains murky!\n");
        }

    }

    @Override
    public String call() {

        String URLString = "https://tarotapi.dev/api/v1/cards/random?n=";

        // ask user for type of spread and set number of cards
        String userChoice;

        System.out.println("Which kind of reading would you like?\n");
        System.out.println("1) The One-Card Spread \nPerfect if you have a specific question you want answered!\n");
        System.out.println(
                "2) The Three-Card Spread \nA simple but insightful reading of your past, present, and future.\n");
        System.out.println(
                "3) The Celtic Cross \nA complex reading representing the many aspects of your life. Peer into your future, if you dare...\n");
        System.out.print("Enter the # of your choice: ");

        userChoice = scan.nextLine();

        if (userChoice.contains("1"))
            URLString += "1";
        else if (userChoice.contains("2"))
            URLString += "3";
        else if (userChoice.contains("3"))
            URLString += "10";
        else
            URLString += "0";

        System.out.println("\nReading the portents... Consulting the auguries... \n");

        // establish connection to API URL
        try {
            URI u = new URI(URLString);
            URL url = u.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // check if connection was successful before continuing!
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("connection failure");
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                // write data into one big string
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                // read data from String inline into Spread currentSpread
                ObjectMapper mapper = new ObjectMapper();
                Spread currentSpread = mapper.readValue(inline, Spread.class);

                // call tellFuture with currentSpread to print out the cards
                tellFuture(currentSpread, userChoice);

                // close the secondary scanner
                scanner.close();

                // get url of index.html and make sure it exists
<<<<<<< HEAD
                URL indexURL = getClass().getResource("index.html");
                assert indexURL != null;
                // File f = new File(indexURL.toURI());
                File x = new File("/Users/dark/app/src/main/resources/index.html");
=======
                File f = new File(System.getProperty("user.home") + "/index.html");
>>>>>>> 4fcc7425518e6d27e080a7bac5117e718e1736d6

                // make sure f exists before continuing
                // if(!f.exists()) System.out.println("index.html does not exist");

                // get absolute path of file
                String path = f.getAbsolutePath();

                // get list of the short_names of currentSpread
                List<String> currentNames = getShortNames(currentSpread);

                // write html tag into index.html for each card in currentNames/currentSpread
                writeToFile(currentSpread, path, currentNames);

                // open index.html
                Desktop.getDesktop().open(f);
            }

        } catch (URISyntaxException | IOException exception) {
            exception.printStackTrace();
        }

        // check if user wants another reading
        beginNewReading();

        return "";
    }

    public static void main(String[] args) {
        Tarot check = new Tarot();
        check.call();
    }
}