/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.*;
import java.util.*;
/**
 *
 * @author AshwinKumar
 */
public class waterFlow {
    
    public static int startTime,totalTime;
    public static HashMap<String,Node> graph = new  HashMap<String,Node>();
    public static ArrayList<String> destinationNodes;
    public static String task;
    BufferedWriter writer ;
    PrintWriter pw ;
    
    public void readInput(String fileName) throws IOException{
         
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);
            writer = new BufferedWriter(new FileWriter("output.txt"));
            pw = new PrintWriter(writer);
            try ( // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                int numberOfTestCases = Integer.parseInt(bufferedReader.readLine());
                for (int testCases = 1;testCases<=numberOfTestCases;testCases++){
                    task = bufferedReader.readLine();
                    String source = bufferedReader.readLine();
                    graph.put(source, new Node(source));
                    String destinations = bufferedReader.readLine();
                    destinations.trim();
                    destinationNodes = new ArrayList<>(Arrays.asList(destinations.split(" ")));
                    for (String dest : destinationNodes) {
                        graph.put(dest, new Node(dest));
                    }
                    
                    String middleNodes = bufferedReader.readLine();
                    middleNodes.trim();
                    String middle[] = middleNodes.split(" ");
                    for(String mid : middle){
                        graph.put(mid, new Node(mid));
                    }
                    int numberOfPipes = Integer.parseInt(bufferedReader.readLine());
                    for(int pipes = 1;pipes<=numberOfPipes;pipes++){
                        String pipe = bufferedReader.readLine();
                        processPipe(pipe);
                    }
                    String time = bufferedReader.readLine();
                    bufferedReader.readLine();
                    startTime = Integer.parseInt(time);
                    totalTime+=startTime;
                    switch(task) {
                        case "BFS" : {
                            doBfs(graph.get(source));
                        }break;
                        case "DFS" : {
                            doDfs (graph.get(source));
                        }
                        break;
                        case "UCS" : {
                            doUcs (graph.get(source));
                        }
                    }
                    graph.clear();
                    startTime = totalTime = 0;
                    
                }
                writer.close();
                pw.close();
                // Always close files.
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "cant open'" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "cant read file'" 
                + fileName + "'");                   
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        finally {
             writer.close();
            pw.close();
            // Always close files.
         
        }
    }
    void processPipe(String pipe){
        
        String pipeDetails[]=pipe.split(" ");
        Node n = graph.get(pipeDetails[0]);
        if(n==null){
            graph.put(pipeDetails[0],new Node(pipeDetails[0]));
        }
        Node adjacentNode = new Node(pipeDetails[1],Integer.parseInt(pipeDetails[2]));
   
        switch(task) {
                case "BFS" : {
                    n.adjacentNodes.add(adjacentNode);
                   sortList(n.adjacentNodes);
                   
                }break;
                case "DFS" : {
                     n.adjacentNodes.add(adjacentNode);
                    sortListReverseAlphabatically(n.adjacentNodes);
                }  
                break;
                case "UCS" : {
                    int numberOfTimes = Integer.parseInt(pipeDetails[3]);
                    for(int offtime=0;offtime<numberOfTimes;offtime++){
                        String range[] = pipeDetails[offtime+4].split("-");
                        for(int fillBoolean = Integer.parseInt(range[0]);fillBoolean<=Integer.parseInt(range[1]);fillBoolean++){
                            adjacentNode.offTimes[fillBoolean] = false;
                        }
                    }
                     n.adjacentNodes.add(adjacentNode);
                    
                }    
        }       
        
        
    }    
    void sortList(ArrayList<Node> nodes){
        Collections.sort(nodes,new Comparator<Node>() {
         @Override
        public int compare(Node s1, Node s2) {
                return s1.name.compareTo(s2.name);
        }
    });
    }
    void sortListReverseAlphabatically(ArrayList<Node> nodes){
        Collections.sort(nodes,new Comparator<Node>() {
         @Override
        public int compare(Node s1, Node s2) {
                return -s1.name.compareTo(s2.name);
        }
    });
    }
    void sortUCSList(ArrayList<Node> nodes){
        Collections.sort(nodes,new Comparator<Node>() {
         @Override
        public int compare(Node s1, Node s2) {
                return s1.name.compareTo(s2.name);
        }
    });
    }
    void doBfs(Node root) {
        String goal=null;
        Queue q = new LinkedList();
        q.add(root);
        root.visited = true;
        waterFlow.graph.get(root.name).visited = true;
        while(!q.isEmpty()){
           Node n = (Node)q.poll();
           for(Node adjacent : n.adjacentNodes){
				if(!adjacent.visited&&!waterFlow.graph.get(adjacent.name).visited){
					adjacent.visited=true;
                                        
                                        if(destinationNodes.contains(adjacent.name)){
                                          waterFlow.graph.get(adjacent.name).parent = n.name;

                                            goal = adjacent.name;
                                            q.clear();
                                            break;
                                            
                                        }
                                        else if(!q.contains(waterFlow.graph.get(adjacent.name))){
                                            q.add(waterFlow.graph.get(adjacent.name));
                                            waterFlow.graph.get(adjacent.name).parent = n.name;
                                        }
					
				}
            }
          
        }
        if(goal!=null){
        System.out.print(goal+ " ");
        pw.print(goal+ " ");
        while(goal!=root.name){
            goal=waterFlow.graph.get(goal).parent;
            totalTime ++;
        }
        System.out.println(totalTime%24);
        pw.println(totalTime%24);
        }
        else {
            System.out.println("None");
            pw.println("None");
        }
            
    }
    void doUcs(Node root) {
        
        String goal="";
        PriorityQueue<Node> pqueue = new PriorityQueue<Node>(1, 
            new Comparator<Node>(){
                //override compare method
                public int compare(Node i, Node j){
                    if(i.pathCost > j.pathCost){
                        return 1;
                    }

                    else if (i.pathCost < j.pathCost){
                        return -1;
                    }

                    else if(i.pathCost==j.pathCost){
                        return i.name.compareTo(j.name);
                    }
                    return 0;
                }
            }

        );
        root.pathCost = startTime;
        pqueue.add(root);
         root.visited = true;
    waterFlow.graph.get(root.name).visited = true;
    while(!pqueue.isEmpty()){
			Node n = (Node) pqueue.poll();
                        waterFlow.graph.get(n.name).visited = true;
                        n.visited = true;
                         //System.out.print(n.name+"--->");
                         if(destinationNodes.contains(n.name)){
                                            goal = n.name;
                                            break;
                        }
			for(Node adjacent : n.adjacentNodes){
                                if(adjacent.offTimes[n.pathCost%24])
				if(adjacent!=null&&!adjacent.visited&&!waterFlow.graph.get(adjacent.name).visited){
					adjacent.visited = true;
                                        int costForNode = n.pathCost + adjacent.cost;
                                        if(!pqueue.contains(waterFlow.graph.get(adjacent.name))){
                                            waterFlow.graph.get(adjacent.name).pathCost = costForNode;
                                            pqueue.add(waterFlow.graph.get(adjacent.name));
                                        }
                                        else if(waterFlow.graph.get(adjacent.name).pathCost>costForNode){
                                            pqueue.remove(waterFlow.graph.get(adjacent.name));
                                            waterFlow.graph.get(adjacent.name).pathCost = costForNode;
                                            pqueue.add(waterFlow.graph.get(adjacent.name));       
                                        }
				}
			}
		}
                if(goal!=null&&goal!=""){
                    System.out.println(goal+" "+waterFlow.graph.get(goal).pathCost%24);
                    pw.println(goal+" "+waterFlow.graph.get(goal).pathCost%24);
                }
                    
                else{
                    System.out.println("None");
                    pw.println("None");
                }
        
    }
    void doDfs(Node root){
    String goal=null;    
    Stack s = new Stack();
    s.add(root);
    root.visited = true;
    waterFlow.graph.get(root.name).visited = true;
    while(!s.isEmpty()){
			Node n = (Node) s.pop();
                        waterFlow.graph.get(n.name).visited = true;
                        n.visited = true;
                        //System.out.print(n.name+"--->");
                         if(destinationNodes.contains(n.name)){
                                            goal = n.name;
                                            s.clear();
                                            break;
                                        }
			for(Node adjacent : n.adjacentNodes){
				if(adjacent!=null&&!adjacent.visited&&!waterFlow.graph.get(adjacent.name).visited){
                                            waterFlow.graph.get(adjacent.name).parent = n.name;
                                            s.push(waterFlow.graph.get(adjacent.name));
                                        
				}
			}
		}
        
        if(goal!=null){
        System.out.print(goal+ " ");
        pw.print(goal+ " ");
        while(goal!=root.name){
            goal=waterFlow.graph.get(goal).parent;
            totalTime ++;
        }
        System.out.println(totalTime%24);
        pw.println(totalTime%24);
        }
        else {
            System.out.println("None");
            pw.println("None");
        }
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws IOException{

        long st = System.currentTimeMillis();
        waterFlow ai = new waterFlow();
        if(args[1]!=null)
        ai.readInput(args[1]); 
        long et = System.currentTimeMillis();
        System.out.println("Time"+(et-st));

    }
    
}
class Node {
     Boolean visited;
     String name;
     int cost;
     int pathCost;
     String parent;
     Boolean offTimes[]= new Boolean[24];
     ArrayList<Node> adjacentNodes = new ArrayList<>();
    public Node (String name) {
        this.name = name;
        this.visited =false;
    } 
    public Node(String name,int cost){
       this.name = name;
       this.cost = cost;
       this.visited = false;
       Arrays.fill(offTimes, Boolean.TRUE);
    }
 
}