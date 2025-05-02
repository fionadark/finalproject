package augur;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Callable;
import com.fasterxml.jackson.databind.ObjectMapper;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.awt.Desktop;

@Command(name = "tarot", mixinStandardHelpOptions = true, version = "augur 1.0", description = "divines the future")

public class Tarot implements Callable<String> {

    // global scanner for all user input
    public static final Scanner SCANNER = new Scanner(System.in);

    @Option(names = { "-t", "--tarot" }, description = "divines the future")

    // method to ask a user if they want to perform another reading, begin one if
    // yes/ end program if no
    public void beginNewReading() {
        String answer;

        System.out.print("Would you like another reading? Enter Y/N: ");
        if (SCANNER.hasNextLine())
            answer = SCANNER.nextLine();
        else
            answer = "N"; // rather than throw an error

        // to start another reading, call .call() on a new instance of Tarot
        if (answer.equals("Y") || answer.equals("Yes")) {
            Tarot t = new Tarot();
            t.call();
            // end program if user chooses no
        } else if (answer.equals("N") || answer.equals("No")) {
            System.out.println("\nOkay, goodbye!\n");
            // throw an exception for weird input
        } else {
            throw new IllegalArgumentException("That wasn't an option, sorry!");
        }

    }

    // function will return true if a card should be reversed, false if it should
    // not
    // assuming cards are drawn in reverse position about 30% of the time
    public boolean reverseTrueOrFalse() {
        Random rand = new Random();
        int min = 1, max = 10;

        int chance = rand.nextInt(max - min + 1) + min;

        return chance <= 3;
    }

    // function will encode given image in base 64 and return result as a String
    public String getEncodedImgString(String imgName) {
        String img = System.getProperty("user.dir") + "/app/src/main/resources/images/" + imgName + ".jpg";
        String encodedImgString = "";

        // encode image in base 64
        try {
            byte[] imgAsBytes = Files.readAllBytes(Paths.get(img));
            encodedImgString = Base64.getEncoder().encodeToString(imgAsBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encodedImgString;
    }

    // function will return a String with the given image's html tag
    // if the card is reversed, img tag will include transform: rotate(180deg) to
    // show the image reversed
    public String getImgHTMLTag(boolean reversed, String img) {
        String tag = "";

        // check for invalid arguments
        if (img == null)
            throw new IllegalArgumentException("null argument");

        // if the card is reversed, alter img tag to flip the image 180 degrees
        if (reversed)
            tag = " <td> <img src=\"data:image/jpg;base64," + img
                    + "\" style=\"width:150px; height:auto; transform: rotate(180deg);\"> </td>";
        else
            tag = " <td> <img src=\"data:image/jpg;base64," + img + "\" style=\"width:150px; height:auto;\"> </td>";

        return tag;
    }

    // function will return a String with the meaning of the given card in a html
    // <td> tag to enter into the table
    // if the image is reversed, meaning_rev will be used instead of meaning_up
    public String getCardMeaning(boolean reversed, String cardName, String up_meaning, String rev_meaning) {
        String meaning = "";

        // check for invalid arguments
        if (cardName == null || up_meaning == null || rev_meaning == null) {
            throw new IllegalArgumentException("null argument");
        }

        // use correct meaning for if card is reversed or upright
        if (reversed)
            meaning = " <td><strong>" + cardName + "</strong><br><br>This card reversed represents: " + rev_meaning
                    + "</td>";
        else
            meaning = " <td><strong>" + cardName + "</strong><br><br>This card represents: " + up_meaning + "</td>";

        return meaning;
    }

    // function will return a String with the position of the card in the spread
    public String getCardPos(int numCards, int pos) {
        String[] positions = { "Your Past", "Your Present", "Your Future", "Your Current Challenge", "Your Conscious",
                "Your Subconscious", "The Cards Advice", "Your External Influences", "Your Hopes and Fears",
                "The Outcome" };
        String cardPos = "";

        // check for invalid arguments
        if (numCards < 1 || numCards > 10 || pos < 0 || pos > 9) {
            throw new IllegalArgumentException("invalid argument");
        }

        // set cardPos value depending on meaning of card's position in the spread
        if (numCards == 1)
            cardPos = " <td>" + positions[2] + "</td>";
        else if (numCards == 3 || numCards == 10)
            cardPos = " <td>" + positions[pos] + "</td>";
        else
            cardPos = " <td>The Future </td>";

        return cardPos;
    }

    // for each name_short in curNames, write the corresponding html tag to
    // index.html
    public void writeToFile(Spread curSpread, String path, List<String> curNames) {

        // truncate exisiting contents of index.html to 0
        File file = new File(path);
        if (file.exists())
            file.delete();

        // declare strBuilder that will hold values for rows of html table
        StringBuilder strBuilder = new StringBuilder();

        // save each row of the table to strBuilder
        for (int i = 0; i < curSpread.getNHits(); i++) {
            // open new row
            strBuilder.append("<tr>");

            // check if this card should be reversed
            boolean reversed = reverseTrueOrFalse();

            // get base 64 encoded path to tarot card image
            String encodedImgString = getEncodedImgString(curSpread.getCards().get(i).getNameShort());

            // append value for "Your Cards" column of table to strBuilder
            String imgHTMLTag = getImgHTMLTag(reversed, encodedImgString);
            strBuilder.append(imgHTMLTag);

            // append value for "Position" column of table to strBuilder
            String cardPos = getCardPos(curSpread.getNHits(), i);
            strBuilder.append(cardPos);

            // append value for "Meaning" column of table to strBuilder
            String cardMeaning = getCardMeaning(reversed, curSpread.getCards().get(i).getName(),
                    curSpread.getCards().get(i).getMeaningUp(), curSpread.getCards().get(i).getMeaningRev());
            strBuilder.append(cardMeaning);

            // close row
            strBuilder.append(" </tr>\n");

        }

        // insert strBuilder into html code that will be written to index.html
        String output = """
                <html>
                    <head>
                        <style>
                            h1 { color:white; text-align:center; padding-top:25px; padding-bottom:25px; }
                            body { background-color:pink; }
                            table { width:55%%; border:0; margin: auto; background-color:white; border-spacing:25px; }
                        </style>
                    </head>
                    <body>
                        <h1>Your future awaits...</h1>
                        <table>
                            <tr>
                                <th>Your Cards</th>
                                <th>Position</th>
                                <th>Meaning</th>
                            </tr>
                            %s
                        </table>
                    </body>
                </html>
                """.formatted(strBuilder.toString());

        // write to index.html
        try {
            Files.write(Paths.get(path), output.getBytes(), StandardOpenOption.CREATE);
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
            System.out.println("The card that presents itself to you is " + curSpread.getCards().get(0).getName() + "\n");
            System.out.println("This card represents: " + curSpread.getCards().get(0).getMeaningRev() + "\n");
        }
        // if the user has chosen a three-card spread...
        else if (userChoice.contains("3")) {
            System.out.println("Three cards present themselves to you...\n");
            System.out.println("Your past: " + curSpread.getCards().get(0).getName());
            System.out.println(curSpread.getCards().get(0).getMeaningRev() + "\n");
            System.out.println("Your present: " + curSpread.getCards().get(1).getName());
            System.out.println(curSpread.getCards().get(1).getMeaningRev() + "\n");
            System.out.println("Your future: " + curSpread.getCards().get(2).getName());
            System.out.println(curSpread.getCards().get(2).getMeaningRev() + "\n");
        }
        // if the user has chosen a celtic cross...
        else if (userChoice.contains("10")) {
            System.out.println("Ten cards present themselves to you...\n");
            System.out.println("Your present: " + curSpread.getCards().get(0).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(0).getMeaningUp() + "\n");
            System.out.println("Your current challenge: " + curSpread.getCards().get(1).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(1).getMeaningUp() + "\n");
            System.out.println("Your past: " + curSpread.getCards().get(2).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(2).getMeaningUp() + "\n");
            System.out.println("Your future: " + curSpread.getCards().get(3).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(3).getMeaningUp() + "\n");
            System.out.println("Your conscious: " + curSpread.getCards().get(4).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(4).getMeaningUp() + "\n");
            System.out.println("Your subconscious: " + curSpread.getCards().get(5).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(5).getMeaningUp() + "\n");
            System.out.println("The cards' advice: " + curSpread.getCards().get(6).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(6).getMeaningUp() + "\n");
            System.out.println("Your external influences: " + curSpread.getCards().get(7).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(7).getMeaningUp() + "\n");
            System.out.println("Your hopes and fears: " + curSpread.getCards().get(8).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(8).getMeaningUp() + "\n");
            System.out.println("Your outcome: " + curSpread.getCards().get(9).getName() + "\nThis card represents: "
                    + curSpread.getCards().get(9).getMeaningUp() + "\n");

        }
        // if the user has entered another answer
        else {
            System.out.println("No cards present themselves to you. Your future remains murky!\n");
            throw new IllegalArgumentException("invalid user input");
        }

    }

    // method to select which tarot spread to use:
    //      1) one card spread
    //      2) three card spread
    //      3) celtic cross (10 card spread)
    // method edits the given URL to return the correct # of cards from the tarot api for the selected spread and returns the URL string
    public String selectTarotSpread(String URLStarter) {
        String userInput;
        boolean spreadSelected = false;

        System.out.println(
                "\nTo begin, would you like to choose your own tarot spread or allow Augur to choose one for you?");
        System.out.println("1) Choose my own.");
        System.out.println("2) Allow Augur to choose.");
        System.out.print("Enter the # of your choice: ");
        userInput = SCANNER.nextLine();

        // option 1: allow user to chooce type of tarot reading
        if (userInput.contains("1")) {
            System.out.println("\nExcellent! Select the type of reading you would like, and prepare to look beyond the veil...\n");
            System.out.println("1) The One-Card Spread \nPerfect if you have a specific question you want answered!\n");
            System.out.println(
                    "2) The Three-Card Spread \nA simple but insightful reading of your past, present, and future.\n");
            System.out.println(
                    "3) The Celtic Cross \nA complex reading representing the many aspects of your life. Peer into your future, if you dare...\n");
            System.out.print("Enter the # of your choice: ");
            userInput = SCANNER.nextLine();
            if (userInput.contains("1"))
                URLStarter += "1"; // one card spread
            else if (userInput.contains("2"))
                URLStarter += "3"; // three card spread
            else if (userInput.contains("3"))
                URLStarter += "10"; // celtic cross (10 card spread)
            else
                URLStarter += "0";
        // option 2: provide guiding questions to choose type of tarot reading
        } else if (userInput.contains("2")) {
            System.out.println(
                    "\nExcellent! Use your inner eye to choose your answers to a few guiding questions, and Augur can select a tarot spread for you.");
            System.out.println("Do you want a simple or complex reading?");
            System.out.println("1) Simple.");
            System.out.println("2) Complex.");
            System.out.print("Enter the # of your choice: ");
            userInput = SCANNER.nextLine();
            // for a simple reading, give reader the one-card spread
            if (userInput.contains("1") && spreadSelected == false) {
                URLStarter += "1";
                spreadSelected = true;
            }
            System.out.println(
                    "\nWould you like to focus on how your past connects to your future, or just on your future?");
            System.out.println("1) The past and present.");
            System.out.println("2) Just the future.");
            System.out.print("Enter the # of your choice: ");
            userInput = SCANNER.nextLine();
            // for a reading of both past and present, give reader the three-card spread
            if (userInput.contains("1") && spreadSelected == false) {
                URLStarter += "3";
                spreadSelected = true;
            }
            System.out
                    .println("\nDo you want a general reading, or do you want to focus on one aspect of your future?");
            System.out.println("1) General reading.");
            System.out.println("2) Specific reading.");
            System.out.print("Enter the # of your choice: ");
            userInput = SCANNER.nextLine();
            // as default, give reader the celtic cross spread (10 cards)
            URLStarter += "10";
        } else {
            throw new IllegalArgumentException("Invalid input.");
        }

        return URLStarter;
    }

    @Override
    public String call() {

        // welcome message
        System.out.println("\nWelcome to Augur: The Free Tarot Tool!");
        System.out.println(
                "This is a command line interfaced designed to provide an authentic tarot card reading experience.");

        // select tarot spread
        String URLStarter = "https://tarotapi.dev/api/v1/cards/random?n=";
        String URLString = selectTarotSpread(URLStarter);
        System.out.println("\nReading the portents... Consulting the auguries... \n");

        int equalsIndex = URLString.indexOf("=");
        String userChoice = URLString.substring(equalsIndex + 1);
        
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
                File f = new File(System.getProperty("user.home") + "/index.html");

                // get absolute path of file
                String path = f.getAbsolutePath();

                // get list of the short_names of currentSpread
                List<String> currentNames = currentSpread.getShortNames();

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
        Tarot tarotReading = new Tarot();
        tarotReading.call();
    }
}