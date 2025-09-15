package investigation;

import investigation.Incident;
import java.util.ArrayList; 

/*  
 * This class represents a cyber crime investigation.  It contains a directory of hackers, which is a resizing
 * hash table. The hash table is an array of HNode objects, which are linked lists of Hacker objects.  
 * 
 * The class contains methods to add a hacker to the directory, remove a hacker from the directory.
 * You will implement these methods, to create and use the HashTable, as well as analyze the data in the directory.
 * 
 * @author Colin Sullivan
 */
public class CyberCrimeInvestigation {
       
    private HNode[] hackerDirectory;
    private int numHackers = 0; 

    public CyberCrimeInvestigation() {
        hackerDirectory = new HNode[10];
    }

    /**
     * Initializes the hacker directory from a file input.
     * @param inputFile
     */
    public void initializeTable(String inputFile) { 
        // DO NOT EDIT
        StdIn.setFile(inputFile);  
        while(!StdIn.isEmpty()){
            addHacker(readSingleHacker());
        }
    }

    /**
     * Reads a single hackers data from the already set file,
     * Then returns a Hacker object with the data, including 
     * the incident data.
     * 
     * StdIn.setFile() has already been called for you.
     * 
     * @param inputFile The name of the file to read hacker data from.
     */
     public Hacker readSingleHacker(){ 
        String hackerName = StdIn.readLine(); //Hacker Name
        String IPHash = StdIn.readLine(); //IP Address Hash
        String location = StdIn.readLine(); //Location
        String OS = StdIn.readLine(); //OS
        String webServer = StdIn.readLine(); //Web Server
        String date = StdIn.readLine(); //Date
        String URL = StdIn.readLine(); //URL Hash
        //Create an Incident object, using the OS, Web Server, Date, Location, IP Address Hash, and URL Hash
        Incident incident = new Incident(OS, webServer, date, location, IPHash, URL);
        //Create a Hacker object using the name, and Incident
        Hacker hacker = new Hacker(hackerName);
        hacker.addIncident(incident);
        
        return hacker; //return hacker object 
    }

    /**
     * Adds a hacker to the directory.  If the hacker already exists in the directory,
     * instead adds the given Hacker's incidents to the existing Hacker's incidents.
     * 
     * After a new insertion (NOT if a hacker already exists), checks if the number of 
     * hackers in the table is >= table length divided by 2. If so, calls resize()
     * 
     * @param toAdd
     */
    public void addHacker(Hacker toAdd) {
        // WRITE YOUR CODE HERE
        int tableLength = hackerDirectory.length; //tableLength is the length of the hackerDirectory array
        int index = toAdd.hashCode() % tableLength; //Calculate which Hash Table index this hacker belongs to
        HNode ptr = hackerDirectory[index]; //set a ptr to the beginning of the index
        
        //check the Linked List found at that Hash Table index:
        if (ptr == null){ //If the list is empty
            hackerDirectory[index] = new HNode(toAdd); // add that Hacker to the list
            numHackers++;
            //AFTER INSERT, check if the # of hackers in table >= length/2.
            if (numHackers >= (tableLength/2)){
                resize(); //If it is, then call the resize()
            }
            return; //exit method once a hacker is added
        }
        //traverse the linked list if not empty:
        HNode ptr2 = null; //create another ptr for later use
        while (ptr != null){
            if (ptr.getHacker().getName().equals(toAdd.getName())){ //If the hacker’s name already exists in that list
                ptr.getHacker().getIncidents().addAll(toAdd.getIncidents()); //add all of the toAdd Hacker’s incidents to the existing Hacker object
                return; //exit method once a incident is added
            }
            ptr2 = ptr; //this will be the node before ptr
            ptr = ptr.getNext(); //traverse
        }
        //If the hacker’s name is not found in the list at that index,
        //by now ptr is null, so ptr2 is the prev node before null
        ptr2.setNext(new HNode(toAdd)); //add the hacker to the END of the list
        numHackers++; //increment numHackers by 1
        //AFTER INSERT, check if the # of hackers in table >= length/2. 
        if (numHackers >= (tableLength/2)){
            resize(); //If it is, then call the resize()
        }

    }

    /**
     * Resizes the hacker directory to double its current size.  Rehashes all hackers
     * into the new doubled directory.
     */
    private void resize() {
        // WRITE YOUR CODE HERE 
        HNode[] temp = hackerDirectory; //Create a temp HNode[] variable, and set it to the existing hackerDirectory array
        numHackers = 0; //numHackers will be reset to 0
        hackerDirectory = new HNode[hackerDirectory.length * 2]; //set the hackerDirectory instance variable to a brand new HNode[] object, with its size as double the old hackerDirectory’s length
        //loop through the old hackerDirectory (temp)
        for (int i = 0; i < temp.length; i++){
            HNode ptr = temp[i]; //this will go through each linked list in the array
            while (ptr != null){ //loop through the linked lists
                addHacker(ptr.getHacker()); //call addHacker() method on each hacker, to reinsert them into the larger table.
                ptr = ptr.getNext(); //continue
            }
        }
        
    }

    /**
     * Searches the hacker directory for a hacker with the given name.
     * Returns null if the Hacker is not found
     * 
     * @param toSearch
     * @return The hacker object if found, null otherwise.
     */
    public Hacker search(String toSearch) {
        // WRITE YOUR CODE HERE 
        int index = Math.abs(toSearch.hashCode()) % hackerDirectory.length; //determine which index the Hacker should be at
        HNode ptr = hackerDirectory[index]; //create a ptr that starts at the index needed
        while (ptr != null){ //traverse the linked list
            if (ptr.getHacker().getName().equals(toSearch)){ //if the given Hacker name is in the hackerDirectory
                return ptr.getHacker(); //return hacker object if found
            }
            ptr = ptr.getNext(); //continue looking 
        }
        return null; //If it is not found, return null
    }

    /**
     * Removes a hacker from the directory.  Returns the removed hacker object.
     * If the hacker is not found, returns null.
     * 
     * @param toRemove
     * @return The removed hacker object, or null if not found.
     */
    public Hacker remove(String toRemove) {
        // WRITE YOUR CODE HERE 
        int index = Math.abs(toRemove.hashCode()) % hackerDirectory.length; //determine which index the Hacker should be at
        HNode ptr = hackerDirectory[index]; //create a ptr that starts at the index needed
        HNode prev = null; //make another ptr that points to the node before ptr
        while (ptr != null){ //traverse the linked list
            if (ptr.getHacker().getName().equals(toRemove)){ //if the name is found 
                if (prev == null){ //if the hacker to delete is at the front
                    hackerDirectory[index] = ptr.getNext(); //skip over the hacker
                } else { //deleting from front or end
                    prev.setNext(ptr.getNext()); //set the prev to ptr.next, this will skip and delete ptr
                }
                numHackers--; //decrement numHackers by one
                return ptr.getHacker(); //return the removed hacker object
            }
            prev = ptr; //make prev the old ptr
            ptr = ptr.getNext(); //move ptr to next hacker
        }
        return null; //If it is not found, return null
    } 

    /**
     * Merges two hackers into one based on number of incidents.
     * 
     * @param hacker1 One hacker
     * @param hacker2 Another hacker to attempt merging with
     * @return True if the merge was successful, false otherwise.
     */
    public boolean mergeHackers(String hacker1, String hacker2) {  
        // WRITE YOUR CODE HERE 
        //use your search() method to find both Hacker objects in hackerDirectory
        Hacker HackerA = search(hacker1);
        Hacker HackerB = search(hacker2);
        if (HackerA == null || HackerB == null){ //If either is not found
            return false; //return false
        } else { //If both hackers are found, merge the hacker with LESS incidents into the one with more. 
            if (HackerA.numIncidents() >= HackerB.numIncidents()){ //if hacker A >= hacker B
                HackerA.getIncidents().addAll(HackerB.getIncidents()); //add all hacker B's incidents to A
                HackerA.addAlias(HackerB.getName()); //add Hacker B's name as an alias for Hacker A
                remove(hacker2); //remove Hacker B, using your remove method
            } else { //if B > A
                HackerB.getIncidents().addAll(HackerA.getIncidents()); //add all hacker A's incidents to B
                HackerB.addAlias(HackerA.getName()); //add Hacker A's name as an alias for Hacker B
                remove(hacker1); //remove Hacker A, using your remove method
            }
        }
        return true; //Return true if successful.
    }

    /**
     * Gets the top n most wanted Hackers from the directory, and
     * returns them in an arraylist. 
     * 
     * You should use the provided MaxPQ class to do this. You can
     * add all hackers, then delMax() n times, to get the top n hackers.
     * 
     * @param n
     * @return Arraylist containing top n hackers
     */
    public ArrayList<Hacker> getNMostWanted(int n) {
        // WRITE YOUR CODE HERE
        // USE the MaxPQ class from lecture
        MaxPQ<Hacker> pq = new MaxPQ<Hacker>(); //Create a new MaxPQ object, which holds Hacker objects
        for (int i = 0; i < hackerDirectory.length; i++){ //traverse through the hackerdirectory table
            HNode ptr = hackerDirectory[i]; //set a ptr to the beginning of the index
            while (ptr != null){ //go through the linked list at each index
                if (ptr.getHacker() != null){ //if there is a hacker at that node
                    pq.insert(ptr.getHacker()); //insert every hacker in the table into the MaxPQ object
                }
                ptr = ptr.getNext(); //continue
            }
        }
        ArrayList<Hacker> mostWanted = new ArrayList<>(); //Create an ArrayList to hold the top N hackers
        int i = 0;
        while (i < n && !pq.isEmpty()){ //traverse through the pq for the top n hackers
            mostWanted.add(pq.delMax()); //call delMax() N times, and add each deleted hacker to your ArrayList
            i++; //increment
        }
        return mostWanted; //return Most Wanted ArrayList.
    }

    /**
     * Gets all hackers that have been involved in incidents at the given location.
     * 
     * You should check all hackers, and ALL of each hackers incidents.
     * You should not add a single hacker more than once.
     * 
     * @param location
     * @return Arraylist containing all hackers who have been involved in incidents at the given location.
     */
    public ArrayList<Hacker> getHackersByLocation(String location) {
        // WRITE YOUR CODE HERE
        ArrayList<Hacker> matchingHackers = new ArrayList<>(); //Create an ArrayList of Hackers, to store any matching Hackers.
        for (int i = 0; i < hackerDirectory.length; i++){ //traverse through the hackerdirectory table
            HNode ptr = hackerDirectory[i]; //set a ptr to the beginning of the index
            while (ptr != null){ //go through the linked list at each index
                Hacker hacker = ptr.getHacker(); //get the hacker
                ArrayList<Incident> incidents = hacker.getIncidents(); //get the incidents of the hacker
                for (int j = 0; j < incidents.size(); j++){ //traverse the incedents to find the location 
                    Incident incident = incidents.get(j); //look through each incident until location is chosen
                    if (incident.getLocation().equals(location)){ //if the hacker's location matches the given location 
                        matchingHackers.add(ptr.getHacker()); //add to list
                        break; //once location is found, leave incident list and look at next hacker
                    }
                }
                ptr = ptr.getNext(); //continue to next hacker 
            }
        }
        return matchingHackers; //return list of hackers for the given location
    }
  

    /**
     * PROVIDED--DO NOT MODIFY!
     * Outputs the entire hacker directory to the terminal. 
     */
     public void printHackerDirectory() { 
        System.out.println(toString());
    } 

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.hackerDirectory.length; i++) {
            HNode headHackerNode = hackerDirectory[i];
            while (headHackerNode != null) {
                if (headHackerNode.getHacker() != null) {
                    sb.append(headHackerNode.getHacker().toString()).append("\n");
                    ArrayList<Incident> incidents = headHackerNode.getHacker().getIncidents();
                    for (Incident incident : incidents) {
                        sb.append("\t" +incident.toString()).append("\n");
                    }
                }
                headHackerNode = headHackerNode.getNext();
            } 
        }
        return sb.toString();
    }

    public HNode[] getHackerDirectory() {
        return hackerDirectory;
    }
}
