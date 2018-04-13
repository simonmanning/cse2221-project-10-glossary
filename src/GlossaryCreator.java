import components.map.Map;
import components.map.Map1L;
import components.sequence.Sequence;
import components.sequence.Sequence1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Creates a html glossary from a text file input.
 *
 * @author Simon Manning
 *
 */
public final class GlossaryCreator {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private GlossaryCreator() {
    }

    /**
     * Generates the header of the output HTML file opening html, head, title,
     * and body.
     *
     * @param out
     *            the output stream.
     * @param terms
     *            the sequence of terms in alphabetical order.
     * @param mapTerms
     *            the map of the terms with their associated definitions.
     * @param arrayTerms
     *            the list of terms in array format.
     * @param outputFile
     *            the output file location selected by the client.
     * @ensures homePage includes Glossary title and list of words stored in
     *          terms as links.
     */
    private static void createIndex(SimpleWriter out, Sequence<String> terms,
            Map<String, String> mapTerms, String[] arrayTerms,
            String outputFile) {

        //user input output file name and index location.

        String index = outputFile + "/index.html";
        SimpleWriter homePage = new SimpleWriter1L(index);

        //create header.

        homePage.println("<html>");
        homePage.println("<head>");
        homePage.println("<title>" + "Glossary" + "</title>");
        homePage.println("</head>");
        homePage.println("<body>");
        homePage.println(
                "<p style=\"font-size:20pt;\"> <b> <u> Simon's Glossary </u> </b></p>");
        homePage.println();
        homePage.println("<p style=\"font-size:16pt;\"><b>Index</b></p>");
        homePage.println("<ul>");

        //creates each term's page and creates a link to the recent created page.

        //loop while there are more terms to make pages for.
        while (terms.length() > 0) {

            String term = terms.remove(0);
            createPages(term, out, mapTerms, arrayTerms, outputFile);
            homePage.println(
                    "<li><a href=\"" + term + ".html\">" + term + "</a></li>");
        }

        //create footer.

        homePage.println("</ul>");
        homePage.println("</body>");
        homePage.println("</html>");

        homePage.close();

    }

    /**
     * Creates a page for each of the terms from the input file.
     *
     * @param term
     *            the term the page is created around
     * @param out
     *            the output stream
     * @param mapTerms
     *            the map of the terms and their definitions
     * @param arrayTerms
     *            all of the terms that are in the input file
     * @param outputFile
     *            the output file location
     * @ensures termPage contains the term as a header & the definition of the
     *          term displayed, followed by a Return to Index Page option.
     */
    private static void createPages(String term, SimpleWriter out,
            Map<String, String> mapTerms, String[] arrayTerms,
            String outputFile) {

        String page = outputFile + "/" + term + ".html";
        SimpleWriter termPage = new SimpleWriter1L(page);

        termPage.println("<html>");
        termPage.println("<head>");
        termPage.println("<title>" + term + "</title>");
        termPage.println("</head>");
        termPage.println("<body>");
        termPage.println("<p style=\"color:red;\"><b><i>" + term.toUpperCase()
                + "</b></i></p>");
        termPage.println();

        String definition = mapTerms.value(term);

        Set<Character> separatorSet = new Set1L<>();
        String separators = " ,";

        //creates the separator set
        generateElements(separators, separatorSet);

        String inputToPage = "";

        int x = 0;
        while (x < definition.length()) {

            String space = nextWordOrSeparator(definition, x, separatorSet);

            if (separatorSet.contains(space.charAt(0))) {
                inputToPage = inputToPage + space;

            } else {
                int i = 0;
                int counter = 0;
                while (i < arrayTerms.length) {

                    if (space.equals(arrayTerms[i])) {
                        inputToPage = inputToPage + "<a href=\"" + arrayTerms[i]
                                + ".html\">" + space + "</a>";
                        counter++;
                    }
                    i++;
                }

                if (counter == 0) {
                    inputToPage = inputToPage + space;
                }
            }
            x += space.length();
        }

        termPage.println(
                "<p style=\"text-align:left;\">" + inputToPage + ".</p>");
        termPage.println();
        termPage.println("Return to <a href=\"index.html\">index</a>.");
        termPage.println("</body>");
        termPage.println("</html>");

        termPage.close();
    }

    /**
     * Puts terms and their definitions into a map.
     *
     * @param in
     *            the input stream
     * @param mapTerms
     *            the map that will contain the terms with their associated
     *            definitions
     * @param terms
     *            a set of only the terms
     * @ensures mapTerms = terms and definitions of the input file in
     */
    private static void mapReader(SimpleReader in, Map<String, String> mapTerms,
            Set<String> terms) {

        //loops until the end of the input file.
        while (!in.atEOS()) {

            String newLine = in.nextLine();
            String definition = "";
            String term = "";

            boolean lineIsEmpty = true;

            if (newLine.equals("")) {

                lineIsEmpty = false;
            } else {

                term = newLine;
            }
            //loops until there is an empty line or the end of the file.
            while (lineIsEmpty && !in.atEOS()) {

                newLine = in.nextLine();
                if (!newLine.equals("")) {
                    //put definition and an empty line after it.
                    definition = definition + " " + newLine;
                } else {
                    lineIsEmpty = false;
                }
            }

            //paired terms and definitions are put in mapTerms.
            mapTerms.add(term, definition);
            terms.add(term);
        }

    }

    /**
     * Takes the set of terms and puts them in alphabetical order.
     *
     * @param terms
     *            the given set of terms
     * @replaces terms
     * @ensures terms = original terms set except the first word alphabetically.
     * @return the new set of terms in alphabetical order.
     */
    private static String alphabetize(Set<String> terms) {

        Set<String> orderTerms = new Set1L<>();
        String result = "";

        //while there are still terms to loop through
        while (terms.size() > 0 && result.equals("")) {
            int count = 0;
            //take out a word from terms to compare.
            String temp = terms.removeAny();

            //compare the word to each word in terms, ignoring case.
            for (String word : terms) {
                if (word.compareToIgnoreCase(temp) < 0) {
                    count++;
                }
            }

            //if word does not need to be placed differently.
            if (count == 0) {
                result = temp;
                //add the temp the ordered terms set.
            } else {
                orderTerms.add(temp);
            }
        }

        terms.add(orderTerms);
        return result;
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param strSet
     *            the {@code Set} to be replaced
     * @replaces strSet
     * @ensures strSet = entries(str)
     */

    private static void generateElements(String str, Set<Character> strSet) {
        assert str != null : "Violation of: str is not null";
        assert strSet != null : "Violation of: strSet is not null";

        int count = 0;
        char setPiece = ' ';
        strSet.clear();

        while (count < str.length()) {

            if (!strSet.contains(str.charAt(count))) {

                setPiece = str.charAt(count);
                strSet.add(setPiece);
            }

            count++;
        }

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int count = 0;
        char returnedPiece = ' ';
        String returned = "";

        if (separators.contains(text.charAt(position))) {

            while (count < text.substring(position, text.length()).length()) {

                returnedPiece = text.charAt(position + count);

                if (separators.contains(text.charAt(position + count))) {

                    returned = returned + returnedPiece;
                    count++;
                } else {

                    count = text.substring(position, text.length()).length();
                }
            }

        } else {
            while (count < text.substring(position, text.length()).length()) {
                returnedPiece = text.charAt(position + count);
                if (!separators.contains(text.charAt(position + count))) {
                    returned = returned + returnedPiece;
                    count++;
                } else {
                    count = text.substring(position, text.length()).length();
                }
            }

        }
        return returned;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        //ask for input file and folder to save to.
        out.print("Input file to use: ");
        String fileName = in.nextLine();
        SimpleReader inFile = new SimpleReader1L(fileName);
        out.print("Folder to save output to: ");
        String outputFile = in.nextLine();

        //create map and set to store the terms and definition(s) into.
        Map<String, String> mapTerms = new Map1L<>();
        Set<String> terms = new Set1L<>();
        mapReader(inFile, mapTerms, terms);

        //create sequence and array to use when storing alphabetical terms.
        Sequence<String> listOfTerms = new Sequence1L<>();
        String[] arrayTerms = new String[terms.size()];

        //order terms in alphabetical order for index page.
        //loop while there are still more terms.
        int x = 0;
        while (0 < terms.size()) {

            String nextTerm = alphabetize(terms);
            listOfTerms.add(listOfTerms.length(), nextTerm);
            arrayTerms[x] = nextTerm;
            x++;
        }

        createIndex(out, listOfTerms, mapTerms, arrayTerms, outputFile);

        in.close();
        out.close();
    }

}
