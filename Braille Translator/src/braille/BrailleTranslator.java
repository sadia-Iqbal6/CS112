package braille;

import java.util.ArrayList;

/**
 * Contains methods to translate Braille to English and English to Braille using
 * a BST.
 * Reads encodings, adds characters, and traverses tree to find encodings.
 * 
 * @author Seth Kelley
 * @author Kal Pandit
 */
public class BrailleTranslator {

    private TreeNode treeRoot;

    /**
     * Default constructor, sets symbols to an empty ArrayList
     */
    public BrailleTranslator() {
        treeRoot = new TreeNode(new Symbol(""), null, null);
    }



    /**
     * Reads encodings from an input file as follows:
     * - One line has the number of characters
     * - n lines with character (as char) and encoding (as string) space-separated
     * USE StdIn.readChar() to read character and StdIn.readLine() after reading
     * encoding
     * 
     * @param inputFile the input file name
     */
    public void createSymbolTree(String inputFile) {

        /* PROVIDED, DO NOT EDIT */

        StdIn.setFile(inputFile);
        int numberOfChars = Integer.parseInt(StdIn.readLine());
        for (int i = 0; i < numberOfChars; i++) {
            Symbol s = readSingleEncoding();
            addCharacter(s);
        }
    }



    /**
     * Reads one line from an input file and returns its corresponding
     * Symbol object
     * 
     * ONE line has a character and its encoding (space separated)
     * 
     * @return the symbol object
     */
    public Symbol readSingleEncoding() {
        // WRITE YOUR CODE HERE
        char character = StdIn.readChar(); //read the character.
        String encoding = StdIn.readString(); //read the encoding.
        StdIn.readLine(); //read the newline character
        Symbol leafnode = new Symbol(character, encoding); //create symbol object containing the character AND encoding
        return leafnode; //return object
    }



    /**
     * Adds a character into the BST rooted at treeRoot.
     * Traces encoding path (0 = left, 1 = right), starting with an empty root.
     * Last digit of encoding indicates position (left or right) of character within
     * parent.
     *
     * @param newSymbol the new symbol object to add
     */
    public void addCharacter(Symbol newSymbol) {
        // WRITE YOUR CODE HERE
        char character = newSymbol.getCharacter(); //get the letter
        String encoding = newSymbol.getEncoding(); //get the encoding
        String partialencoding = ""; //empty right now
        TreeNode ptr = treeRoot; //pointer starts at root

        for (int i = 0; i < encoding.length(); i++){ //traverse through the string encoding
            char path = encoding.charAt(i); //reads each letter in the encoding, either L (left) or R (right)
            partialencoding += path; //add new letter to the string we have

            if (path == 'L'){ //if the letter read is L
                if (ptr.getLeft() == null){ //and if the left node is empty
                    ptr.setLeft(new TreeNode(new Symbol(partialencoding), null, null)); //create new left node and insert the partialencoding
                }
                ptr = ptr.getLeft(); //update the pointer
            } else { //if the letter read is R and not L
                if (ptr.getRight() == null){ //and if the right node is empty
                    ptr.setRight(new TreeNode(new Symbol(partialencoding), null, null)); //create new right node and insert the partialencoding
                }
                ptr = ptr.getRight(); //update the pointer
            }
        }
        ptr.setSymbol(new Symbol(character, encoding)); //for the final node add the letter with the encoding
    }



    /**
     * Given a sequence of characters, traverse the tree based on the characters
     * to find the TreeNode it leads to
     * 
     * @param encoding Sequence of braille (Ls and Rs)
     * @return Returns the TreeNode of where the characters lead to, or null if there is no path
     */
    public TreeNode getSymbolNode(String encoding) {
        // WRITE YOUR CODE HERE
        TreeNode ptr = treeRoot; //pointer starts at root

        for (int i = 0; i < encoding.length(); i++){ //traverse the encoding string
            if (ptr == null){ //if character doesn't exist return null
                return null;
            }
            char path = encoding.charAt(i); //get the character at a certain string position i
            if (path == 'L'){ //if char is L go left
                ptr = ptr.getLeft();
            } else { //if char is R go right
                ptr = ptr.getRight();
            }
        }
        return ptr; //return the tree node
    }



    /**
     * Given a character to look for in the tree will return the encoding of the
     * character
     * 
     * @param character The character that is to be looked for in the tree
     * @return Returns the String encoding of the character
     */
    public String findBrailleEncoding(char character) {
        // WRITE YOUR CODE HERE
        return helper(treeRoot, character); //start recursive search from root using helper
    }

    //helper method similar to the ones from recitation
    private String helper(TreeNode ptr, char character){
        //Base case
        if (ptr == null){ //return null if not in tree
            return null;
        }
        //Case when its found
        if (ptr.getSymbol().getCharacter() == character){ //if the symbol's character matches the character needed
            return ptr.getSymbol().getEncoding(); //return the symbol's encoding
        }
        //Search left tree
        String leftTree = helper(ptr.getLeft(), character); //traverse left tree using recursive calls to ptr get left
        if (leftTree != null){ //if it is not null then that means it has been found in left tree
            return leftTree;
        }
        //if not in left & not null, then that means it exists in the right tree
        //so recursively search right tree and return
        return helper(ptr.getRight(), character);
    }



    /**
     * Given a prefix to a Braille encoding, return an ArrayList of all encodings that start with
     * that prefix
     *
     * @param start the prefix to search for
     * @return all Symbol nodes which have encodings starting with the given prefix
     */
    public ArrayList<Symbol> encodingsStartWith(String start) {
        // WRITE YOUR CODE HERE
        ArrayList<Symbol> chars = new ArrayList<>(); //create an empty arraylist (look at Lab 6 for help)
        TreeNode startNode = getSymbolNode(start); //Use getSymbolNode to find TreeNode that corresponds to the starting encoding (call it startNode).
        
        if (startNode == null){ //if node doesn't exist in the tree
            return chars; //return the empty array list
        }
        preOrder(startNode, chars); //Conduct a preorder traversal
        return chars; //return an ArrayList of TreeNode leaves.
    }

    //create a private helper method that does preOrder (same one from Lab 6)
    private void preOrder(TreeNode curr, ArrayList<Symbol> list) {
        if (curr == null){
            return;
        }
        //only add the leafnode so output doesn't add extra things
        if (curr.getSymbol() != null && curr.getLeft() == null && curr.getRight() == null){
            list.add(curr.getSymbol());
        }
        preOrder(curr.getLeft(), list);
        preOrder(curr.getRight(), list);
    }



    /**
     * Reads an input file and processes encodings six chars at a time.
     * Then, calls getSymbolNode on each six char chunk to get the
     * character.
     * 
     * Return the result of all translations, as a String.
     * @param input the input file
     * @return the translated output of the Braille input
     */
    public String translateBraille(String input) {
        // WRITE YOUR CODE HERE
        StdIn.setFile(input); //set input file
        String encodingWord = StdIn.readString(); //read the large untranslated encoding
        String word = ""; //create an empty string that will be the translated output

        for (int i = 0; i < encodingWord.length(); i+= 6){ //traverse through the encoding word (update +=6 because each 6 letters is a character)
            String chunk = encodingWord.substring(i, i+6); //create a six-character chunk within the long string
            TreeNode character = getSymbolNode(chunk); //call getSymbolNode on the chunk
            word += character.getSymbol().getCharacter(); //append the character to the string
            
        }
        return word; //return the translated word
    }




    /**
     * Given a character, delete it from the tree and delete any encodings not
     * attached to a character (ie. no children).
     * 
     * @param symbol the symbol to delete
     */
    public void deleteSymbol(char symbol) {
        // WRITE YOUR CODE HERE
        //Find the parent and target nodes to delete. Use getSymbolNode(String encoding) to find each
        String encoding = findBrailleEncoding(symbol); //get the encoding of the char
        TreeNode target = getSymbolNode(encoding); //get the target node
        TreeNode parent = getSymbolNode(encoding.substring(0, encoding.length() - 1)); //get its parent using substring 
        
        //If the node is a leaf, unhook it from its parent.
        if (parent != null && target.getLeft() == null && target.getRight() == null){ //if target is at a leafnode 
            if (encoding.charAt(encoding.length() - 1) == 'L'){ //if its to the left of the parent
                parent.setLeft(null); //unhook the target from the parent
            } else { //if its to the right
                parent.setRight(null); //unhook the target from the parent
            }
        }

        //use a backwards for loop to now go backward in the tree and delete any partial encodings that are empty/leafs
        for (int i = encoding.length() - 1; i > 0; i--){
            String partialEncoding = encoding.substring(0, i); //get partial encoding using substring
            TreeNode partialEncodingNode = getSymbolNode(partialEncoding); //get the partial encoding node

            if (partialEncoding != null && partialEncodingNode.getLeft() == null && partialEncodingNode.getRight() == null){ //if the encoding exists and it has no children
                TreeNode partialParent = getSymbolNode(partialEncoding.substring(0, i - 1)); //get the parent of the partial encoding
                if (partialEncoding.charAt(i - 1) == 'L'){ //if its to the left
                    partialParent.setLeft(null); //remove from the left
                } else { //if its to the right
                    partialParent.setRight(null); //remove from the right
                }
            }
        }
        //If treeRoot (the instance variable) is a leaf, its also deleted as well.
        if (treeRoot != null && treeRoot.getLeft() == null && treeRoot.getRight() == null){ //if treeRoot is a leaf
            treeRoot = null; //delete it
        }
    }



    

    public TreeNode getTreeRoot() {
        return this.treeRoot;
    }

    public void setTreeRoot(TreeNode treeRoot) {
        this.treeRoot = treeRoot;
    }

    public void printTree() {
        printTree(treeRoot, "", false, true);
    }

    private void printTree(TreeNode n, String indent, boolean isRight, boolean isRoot) {
        StdOut.print(indent);

        // Print out either a right connection or a left connection
        if (!isRoot)
            StdOut.print(isRight ? "|+R- " : "--L- ");

        // If we're at the root, we don't want a 1 or 0
        else
            StdOut.print("+--- ");

        if (n == null) {
            StdOut.println("null");
            return;
        }
        // If we have an associated character print it too
        if (n.getSymbol() != null && n.getSymbol().hasCharacter()) {
            StdOut.print(n.getSymbol().getCharacter() + " -> ");
            StdOut.print(n.getSymbol().getEncoding());
        }
        else if (n.getSymbol() != null) {
            StdOut.print(n.getSymbol().getEncoding() + " ");
            if (n.getSymbol().getEncoding().equals("")) {
                StdOut.print("\"\" ");
            }
        }
        StdOut.println();

        // If no more children we're done
        if (n.getSymbol() != null && n.getLeft() == null && n.getRight() == null)
            return;

        // Add to the indent based on whether we're branching left or right
        indent += isRight ? "|    " : "     ";

        printTree(n.getRight(), indent, true, false);
        printTree(n.getLeft(), indent, false, false);
    }

}
