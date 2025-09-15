package rumaps;

import java.util.*;

/**
 * This class represents the information that can be attained from the Rutgers University Map.
 * 
 * The RUMaps class is responsible for initializing the network, streets, blocks, and intersections in the map.
 * 
 * You will complete methods to initialize blocks and intersections, calculate block lengths, find reachable intersections,
 * minimize intersections between two points, find the fastest path between two points, and calculate a path's information.
 * 
 * Provided is a Network object that contains all the streets and intersections in the map
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class RUMaps {
    
    private Network rutgers;

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Constructor for the RUMaps class. Initializes the streets and intersections in the map.
     * For each block in every street, sets the block's length, traffic factor, and traffic value.
     * 
     * @param mapPanel The map panel to display the map
     * @param filename The name of the file containing the street information
     */
    public RUMaps(MapPanel mapPanel, String filename) {
        StdIn.setFile(filename);
        int numIntersections = StdIn.readInt();
        int numStreets = StdIn.readInt();
        StdIn.readLine();
        rutgers = new Network(numIntersections, mapPanel);
        ArrayList<Block> blocks = initializeBlocks(numStreets);
        initializeIntersections(blocks);

        for (Block block: rutgers.getAdjacencyList()) {
            Block ptr = block;
            while (ptr != null) {
                ptr.setLength(blockLength(ptr));
                ptr.setTrafficFactor(blockTrafficFactor(ptr));
                ptr.setTraffic(blockTraffic(ptr));
                ptr = ptr.getNext();
            }
        }
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     * 
     * @param filename The name of the file containing the street information
     */
    public RUMaps(String filename) {
        this(null, filename);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     */
    public RUMaps() { 
        
    }

    /**
     * Initializes all blocks, given a number of streets.
     * the file was opened by the constructor - use StdIn to continue reading the file
     * @param numStreets the number of streets
     * @return an ArrayList of blocks
     */
    public ArrayList<Block> initializeBlocks(int numStreets) {
        // WRITE YOUR CODE HERE
        ArrayList<Block> newBlocks = new ArrayList<>(); //Create an ArrayList of blocks.
        for (int i = 0; i < numStreets; i++){ //for each street
            String streetName = StdIn.readLine(); //Read the street name
            int numOfblocks = StdIn.readInt(); //and its number of blocks.
            for (int j = 0; j < numOfblocks; j++){ //for each block 
                int blockNumber = StdIn.readInt(); //Read its block number (as an int)
                int numOfpoints = StdIn.readInt(); //number of points (as an int)
                double roadSize = StdIn.readDouble(); //and road size (as a double)
                StdIn.readLine(); //leaves trailing newline characters unread
                Block blockObject = new Block(roadSize, streetName, blockNumber); //initialize a Block object
                for (int k = 0; k < numOfpoints; k++){ //for each point 
                    int x = StdIn.readInt(); //read its x coordinate
                    int y = StdIn.readInt(); //and y coordinates
                    Coordinate coordinateObject = new Coordinate(x, y); //create a new Coordinate object
                    if (k == 0){ //If this is the first point
                        blockObject.startPoint(coordinateObject); //call block.startPoint on the coordinate
                    } else { //if it’s not the first point
                        blockObject.nextPoint(coordinateObject); //call block.nextPoint on the coordinate
                    }
                }
                newBlocks.add(blockObject); //add the block object to the resulting blocks ArrayList

            }
            StdIn.readLine(); //add a StdIn.readLine() statement at the end of loop for each street
        }
        return newBlocks; //Return the ArrayList of blocks that now stores all blocks you read from the input file
    }

    /**
     * This method traverses through each block and finds
     * the block's start and end points to create intersections. 
     * 
     * It then adds intersections as vertices to the "rutgers" graph if
     * they are not already present, and adds UNDIRECTED edges to the adjacency
     * list.
     * 
     * Note that .addEdge(__) ONLY adds edges in one direction (a -> b). 
     */
    public void initializeIntersections(ArrayList<Block> blocks) {
        // WRITE YOUR CODE HERE
        for (int i = 0; i < blocks.size(); i++){ //For each block
            Block block = blocks.get(i); //first get the block at the current index
            ArrayList<Coordinate> blockCoordinates = block.getCoordinatePoints(); //get all coordinates from the block 
            Coordinate startPoint = blockCoordinates.get(0); //grab its starting point (given in index 0 of coordinate array list)
            Coordinate endPoint = blockCoordinates.get(blockCoordinates.size() - 1); //and its ending point (given in index (size – 1) of coordinate array list)
            //Identify if an intersection exists for BOTH the start and end points: 
            //use the findIntersection method on your network instance and pass in the start and end coordinates. It’ll return its index. 
            int startIndex = rutgers.findIntersection(startPoint); //use the rutgers instance variable to find intersection index for start point
            int endIndex = rutgers.findIntersection(endPoint); //and for end point
            if (startIndex == -1){ //If the starting intersection doesn’t exist
                Intersection start = new Intersection(startPoint); //create a new Intersection object with the starting coordinate
                block.setFirstEndpoint(start); //set the block’s first endpoint to the intersection
                rutgers.addIntersection(start); //add the intersection to the network.
            } else { //if the starting intersection exists 
                Intersection existingStart = rutgers.getIntersections()[startIndex]; //find the intersection in the intersections array
                block.setFirstEndpoint(existingStart); //update its first endpoint accordingly
            }
            if (endIndex == -1){ //If the ending intersection doesn’t exist 
                Intersection end = new Intersection(endPoint); //create a new Intersection object with the ending coordinate
                block.setLastEndpoint(end); //set the block’s LAST endpoint to the intersection
                rutgers.addIntersection(end); //add the intersection to the network
            } else { //if the ending intersection exists 
                Intersection existingEnd = rutgers.getIntersections()[endIndex]; //find the intersection in the intersections array
                block.setLastEndpoint(existingEnd); //update its first endpoint accordingly
            }
            //Use the index of the intersections you just added (or found) to determine the index its edges should go into – 
            //you can use findIntersection to do this.
            int starteEdge = rutgers.findIntersection(startPoint);
            int endEdge = rutgers.findIntersection(endPoint);
            Block deepCopy = block.copy(); //USE the block.copy() method in order to create a deep copy of the current block.
            deepCopy.setFirstEndpoint(block.getLastEndpoint()); //Block a stores an edge from first -> last. 
            deepCopy.setLastEndpoint(block.getFirstEndpoint()); //Block b stores an edge from last -> first.
            //now the deep copy block (B -> A) is the reverse of the regular block (A -> B)
            rutgers.addEdge(starteEdge, block); //add block A to its corresponding index (The original block gets added to the starting intersection (A -> B))
            rutgers.addEdge(endEdge, deepCopy); //add block B to its corresponding index (The reversed block gets added to the ending intersection (B -> A).)
        }
    }

    /**
     * Calculates the length of a block by summing the distances between consecutive points for all points in the block.
     * 
     * @param block The block whose length is being calculated
     * @return The total length of the block
     */
    public double blockLength(Block block) {
        // WRITE YOUR CODE HERE
        ArrayList<Coordinate> coordinatePoints = block.getCoordinatePoints(); //Grab the block’s coordinate points
        double totalLength = 0.0; //set length as 0.0 temporarily
        for (int i = 0; i < coordinatePoints.size() - 1; i++){ //iterate through each pair of points
            Coordinate a = coordinatePoints.get(i); //get the first coordinate 
            Coordinate b = coordinatePoints.get(i + 1); //get the second coordinate pair 
            totalLength += coordinateDistance(a, b); //Sum the distance between each pair (USE the coordinateDistance method)
        }
        return totalLength; // Replace this line, it is provided so the code compiles
    }

    /**
     * Use a DFS to traverse through blocks, and find the order of intersections
     * traversed starting from a given intersection (as source).
     * 
     * Implement this method recursively, using a helper method.
     */
    public ArrayList<Intersection> reachableIntersections(Intersection source) {
        // WRITE YOUR CODE HERE
        //create an ArrayList of Intersections to track the order of all intersections visited
        ArrayList<Intersection> orderVisited = new ArrayList<>(); 
        //create a hashmap that holds the intersection as the key and the boolean true or false as the value (same as marked[] array from lecture slides)
        HashMap<Intersection, Boolean> marked = new HashMap<>();
        //initialize all intersections as false (same as marked[v]=false from slides)
        for (Intersection v : rutgers.getIntersections()) { //enhanced for loop structure from class 
            marked.put(v, false); //marked[w] = false
        }
        int index = rutgers.findIntersection(source.getCoordinate()); //since adj takes an index, find the index of source 
        DFS(index, orderVisited, marked); //call private recursive method starting with the source intersection 

        return orderVisited; //return the ArrayList of orderVisited
    }
    private void DFS(int index, ArrayList<Intersection> order, HashMap<Intersection, Boolean> marked){
        Intersection curr = rutgers.getIntersections()[index]; //get the the itersection of the index
        if (marked.get(curr)){ //base case
            return;
        }
        marked.put(curr, true); //start by marking the source intersection as true (visited)
        order.add(curr); //add the visited intersection to the list to track the order 
        Block ptr = rutgers.adj(index); //gets the front of the LL of neighbors
        while (ptr != null){ //traverse the LL of neighbors 
            Intersection neighbor = ptr.other(curr); // get the neighboring intersection connected to the current intersection from the ptr block
            int neighborIndex = rutgers.findIntersection(neighbor.getCoordinate()); // find the index of the neighboring intersection in the rutgers network
            if (!marked.get(neighbor)){ //if the neighbor hasn't been visited yet
                DFS(neighborIndex, order, marked); //recursively call DFS on neighbor
            }
            ptr = ptr.getNext(); //move ptr to next block in LL

        }
        
    }
    

    /**
     * Finds and returns the path with the least number of intersections (nodes) from the start to the end intersection.
     * 
     * - If no path exists, return an empty ArrayList.
     * - This graph is large. Find a way to eliminate searching through intersections that have already been visited.
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least number of turns, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> minimizeIntersections(Intersection start, Intersection end) {
        // WRITE YOUR CODE HERE
        ArrayList<Intersection> path = new ArrayList<>(); //arraylist with the path from the source intersection to a target intersection
        int numIntersections = rutgers.getIntersections().length; //get the num of intersections 
        boolean [] marked = new boolean[numIntersections]; //create an array to track visited
        Intersection[] edgeTo = new Intersection[numIntersections]; //Use the edgeTo array to store predecessors of each vertex
        Queue<Intersection> q = new Queue<>(); //queue for BFS
        int startIndex = rutgers.findIntersection(start.getCoordinate()); //get the index of the start intersection
        q.enqueue(start); //enque start
        marked[startIndex] = true; //mark it as visited
        edgeTo[startIndex] = null; //edgeTo[source] should be null, as there is no cycle
        //follow the BFS method from lecture
        while (!q.isEmpty()){
            Intersection v = q.dequeue(); //get the next intersection from the queue
            Block ptr = rutgers.adj(rutgers.findIntersection(v.getCoordinate())); //get the front of the LL of neighbors
            while (ptr != null){ //traverse the neighbors 
                Intersection neighbor = ptr.other(v); //get the neighboring intersection connected to the current intersection v
                int neighborIndex = rutgers.findIntersection(neighbor.getCoordinate()); //get the index of the neighbor
                if (!marked[neighborIndex]){ //if the neighbor hasn't been visited yet
                    q.enqueue(neighbor); //add it to queue
                    marked[neighborIndex] = true; //mark as visited
                    edgeTo[neighborIndex] = v; //set the currect intersection v as the predecessor of neighbor
                }
                ptr = ptr.getNext(); //continue
            }
        }
        //go reverse now (Reverse the path so that it starts from the start)
        Intersection v = end; //v = target
        while (v != null){
            path.add(v); //add the current intersection to path
            v = edgeTo[rutgers.findIntersection(v.getCoordinate())]; //now move to the predecessor we saved earlier
        }
        Collections.reverse(path); //reverse path to start from source
        return path; //return the shortest path
    }

    /**
     * Finds the path with the least traffic from the start to the end intersection using a variant of Dijkstra's algorithm.
     * The traffic is calculated as the sum of traffic of the blocks along the path.
     * 
     * What is this variant of Dijkstra?
     * - We are using traffic as a cost - we extract the lowest cost intersection from the fringe.
     * - Once we add the target to the done set, we're done. 
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least traffic, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> fastestPath(Intersection start, Intersection end) {
        // WRITE YOUR CODE HERE
        //follow the algorithm from the slide deck for Dijkstra alg
        ArrayList<Intersection> path = new ArrayList<>(); //arraylist with the path from the source intersection to a target intersection
        ArrayList<Intersection> fringe = new ArrayList<>(); //list of intersections still yet to be visited, use an ArrayList to implement the fringe
        int numIntersections = rutgers.getIntersections().length; //get the num of intersections
        boolean[] done = new boolean[numIntersections]; //intersections with known optimal path from source
        double[] d = new double[numIntersections]; //d(v): optimal distance from source to parameter vertex
        Intersection[] pred = new Intersection[numIntersections]; //pred(v): previous vertex on optimal path from source to parameter vertex
        int startIndex = rutgers.findIntersection(start.getCoordinate()); //get the index of the start intersection
        for (int i = 0; i < numIntersections; i++){ //for each intersection
            if (i != startIndex){ //other than the source/start
                pred[i] = null; //set all pred to null
                d[i] = Double.POSITIVE_INFINITY; //set all distances to infinity

            }
        }
        d[startIndex] = 0; //set the distance of the source/start to 0, Optimal distance from source to parameter vertex
        pred[startIndex] = null; //set the pred of the source/start to null
        fringe.add(start); //add source/start to fringe
        while (!fringe.isEmpty()){ //while the fringe is not empty fringe = to-do list, not ideal yet
            int minFrinIndex = 0; //assuming the first index on fringe is the min rn
            double minDist = d[rutgers.findIntersection(fringe.get(0).getCoordinate())]; // initialize the first index in fringe arraylist is the min distance
            for (int i = 0; i < fringe.size(); i++){ //loop thtrough fringe arraylist to find the min distance
                int index = rutgers.findIntersection(fringe.get(i).getCoordinate()); //the index of the current intersection
                if (d[index] < minDist){ //if the distance of the current intersection is less than the min
                    minFrinIndex = i; //set the new min index to the current index in the fringe array 
                    minDist = d[index]; //set the new min distance to the current one
                }
            }
            Intersection minIntersection = fringe.remove(minFrinIndex); //remove the min distance intersection from the fringe
            int minIndex = rutgers.findIntersection(minIntersection.getCoordinate()); //get the index of the removed intersection from fringe 
            done[minIndex] = true; //add the min distance removed to done array (marking it true that we found the optimal path for this intersection)
            Block ptr = rutgers.adj(minIndex); //get the front of the LL of neighbors
            while (ptr != null){ //for each neighbor w of m
                Intersection neighbor = ptr.other(rutgers.getIntersections()[minIndex]); //get the neighboring intersection
                int neighborIndex = rutgers.findIntersection(neighbor.getCoordinate()); //get the index of the neighbor
                if (!done[neighborIndex]){ //if not in the done set
                    if (d[neighborIndex] == Double.POSITIVE_INFINITY ){ //if d(w) is infinity
                        d[neighborIndex] = d[minIndex] + ptr.getTraffic(); //d(w) = d(m) + w(m,w) (w(m, w) is given by edge weight (use traffic for weights))
                        fringe.add(rutgers.getIntersections()[neighborIndex]); //add w to fringe
                        pred[neighborIndex] = rutgers.getIntersections()[minIndex]; //pred(w) = m
                    } else if (d[neighborIndex] > (d[minIndex] + ptr.getTraffic())){ //else if d(w) > (d(m) + w(m,w))
                        d[neighborIndex] = d[minIndex] + ptr.getTraffic(); //d(w) = d(m) + w(m,w)
                        pred[neighborIndex] = rutgers.getIntersections()[minIndex]; //pred(w) = m
                    }

                }
                ptr = ptr.getNext(); //continue
            }

        }
        //go reverse now (Reverse the path so that it starts from the start)
        Intersection v = end; //v = target
        while (v != null){
            path.add(v); //add the current intersection to path
            v = pred[rutgers.findIntersection(v.getCoordinate())]; //now move to the prev intersection
        }
        Collections.reverse(path); //reverse path to start from source
        return path; // Replace this line, it is provided so the code compiles
    }

    /**
     * Calculates the total length, average experienced traffic factor, and total traffic for a given path of blocks.
     * 
     * You're given a list of intersections (vertices); you'll need to find the edge in between each pair.
     * 
     * Compute the average experienced traffic factor by dividing total traffic by total length.
     *  
     * @param path The list of intersections representing the path
     * @return A double array containing the total length, average experienced traffic factor, and total traffic of the path (in that order)
     */
    public double[] pathInformation(ArrayList<Intersection> path) {
        // WRITE YOUR CODE HERE
        double totalLength = 0; //initialize total length to 0 for now
        double totalTraffic = 0; //initialize total traffic to 0 for now
        for (int i = 0; i < path.size() - 1 ; i++){ //iterate through each pair of intersections (For each pair of intersections on this incident path)
            Intersection a = path.get(i); //the first intersection
            Intersection b = path.get(i + 1); //the second intersection
            int indexA = rutgers.findIntersection(a.getCoordinate()); //get the index of the intersection a in the network
            Block ptr = rutgers.adj(indexA); //set a ptr by using the .adj method to traverse the neighbors of a
            while (ptr != null){ //traverse 
                if (ptr.other(a).equals(b)){ //if the neighboring intersection of the edge ptr matches the pair b (use .equals when comparing intersections)
                    totalLength += ptr.getLength(); //increment the total length by the edge's length
                    totalTraffic += ptr.getTraffic(); //increment the total traffic by the edge's traffic
                }
                ptr = ptr.getNext(); //continue the loop
            }
        }
        double avgTraffic = totalTraffic/totalLength;
        //return an array of 3 doubles:
        //index 0 stores the total length, index 1 the total traffic divided by the total length, and index 2 the total traffic.
        return new double[] {totalLength, avgTraffic, totalTraffic};
    }

    /**
     * Calculates the Euclidean distance between two coordinates.
     * PROVIDED - do not modify
     * 
     * @param a The first coordinate
     * @param b The second coordinate
     * @return The Euclidean distance between the two coordinates
     */
    private double coordinateDistance(Coordinate a, Coordinate b) {
        // PROVIDED METHOD

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Calculates and returns a randomized traffic factor for the block based on a Gaussian distribution.
     * 
     * This method generates a random traffic factor to simulate varying traffic conditions for each block:
     * - < 1 for good (faster) conditions
     * - = 1 for normal conditions
     * - > 1 for bad (slower) conditions
     * 
     * The traffic factor is generated with a Gaussian distribution centered at 1, with a standard deviation of 0.2.
     * 
     * Constraints:
     * - The traffic factor is capped between a minimum of 0.5 and a maximum of 1.5 to avoid extreme values.
     * 
     * @param block The block for which the traffic factor is calculated
     * @return A randomized traffic factor for the block
     */
    public double blockTrafficFactor(Block block) {
        double rand = StdRandom.gaussian(1, 0.2);
        rand = Math.max(rand, 0.5);
        rand = Math.min(rand, 1.5);
        return rand;
    }

    /**
     * Calculates the traffic on a block by the product of its length and its traffic factor.
     * 
     * @param block The block for which traffic is being calculated
     * @return The calculated traffic value on the block
     */
    public double blockTraffic(Block block) {
        // PROVIDED METHOD
        
        return block.getTrafficFactor() * block.getLength();
    }

    public Network getRutgers() {
        return rutgers;
    }




    
    








}
