//This program calculates the Hamiltonian circuit of least weight by
//applying the iterative method of improvment on an initial random
//Hamiltonian cycle.

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.*;

public class HamiltonianCycle{
  public static void main (String[] cheese){
    Scanner in = new Scanner(System.in);
    System.out.print("\nEnter the number of vertices of the graph\n(The graphics get funky after approx. 1000 verticies.): ");
    int n = in.nextInt();

    //Create the frame to display the cycle
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    final LinesComponent comp = new LinesComponent(); 
    comp.setPreferredSize(new Dimension(800, 800)); 
    frame.getContentPane().add(comp, BorderLayout.CENTER); 
    frame.getContentPane().setBackground(Color.black);
    frame.pack(); 
    frame.setVisible(true); 
    
    try{
        Thread.sleep(5); 
    }
    catch(InterruptedException e){
        Thread.currentThread().interrupt();
    }

    //Create a new graph with (number of points, x-axis bound, y-axis bound)
    ConnectGraph g = new ConnectGraph(n, 800, 800);
    //Generate the circuit and iteratively improve
    Long stim = System.currentTimeMillis();
    g.GeneratePoints(comp);
    Long etim = System.currentTimeMillis();

    System.out.println(etim-stim);
  }
}


class ConnectGraph{
  int N, w, h, length, improvements = 0;
  //These hold the positions of the vertices that
      //are to be compared
  int v, u, a, b;
  boolean improved = false;
  Graph graph;
  Vertex[] points;
  Vertex p,q;
  //Contains the hamiltonian circuit that is to be updated
  Circuit circuit = new Circuit();

  Random r = new Random();


  double initialDistance = 0.0;

  public ConnectGraph(int n, int w, int h){
    N = n;
    graph = new Graph(N+1);
    points = new Vertex[N];
    this.w = w;
    this.h = h;
  }

  //Generate a random x coord within the range of the width
  public int randomX(){ 
    int x = Math.abs(r.nextInt()) % w;
    return x;
  }

  //Generate a random y coord within the range of the height
  public int randomY(){
    int y = Math.abs(r.nextInt()) % h;
    return y;
  }

  public void GeneratePoints(LinesComponent comp){
    //Creating N points, adding them to the list
    for(int i = 0; i < N; i++){
      p = new Vertex(randomX(),randomY());
    
      while(circuit.isMade(p)){
        p.decrement();
        p = new Vertex(randomX(),randomY());
      }


      circuit.add(p);
      points[i] = p;

      //Calculating the distance between each Vertex and adding the edge weight to the graph
      //calculateWeights(i);
      for(int j = 0 ; j < i; j++){
        q = points[j];

        double dis = Math.pow((Math.pow(((int)q.getX() - (int)p.getX()), 2))+
                             (Math.pow(((int)q.getY() - (int)p.getY()), 2)), .5);
        
        graph.addEdge(i, j, dis);
        graph.addEdge(j, i, dis);


          
        System.out.println("Adding Edge : (" + i +","+ j + ")  " + graph.getWeight(i,j));
      }
    }

    System.out.println("\nInitial Circuit...\n");
    
    System.out.println(circuit + "\n");
    int init = 0; 
    int fini = 0;

    for(int i = 0; i < circuit.size()-1; i++){
      init += graph.adjMat[circuit.get(i).getKey()][circuit.get(i+1).getKey()];
    }

    comp.createLines(circuit);

    Improve(comp);

    for(int i = 0; i < circuit.size()-1; i++){
      fini += graph.adjMat[circuit.get(i).getKey()][circuit.get(i+1).getKey()];
    }
    System.out.println("\nFinal Circuit...\n");

    System.out.println(circuit + "\n");
    System.out.println("Points: " + circuit.size());
    System.out.println("Intitial circuit weight: " + init);
    System.out.println("Final circuit weight: " + fini);
    System.out.println("Improvments: " + improvements);
  }
  
////////METHOD FOR IMPROVEMENT////////
  public void Improve(LinesComponent comp){
    //algorithm for improvement
    //Repeats until no improvements have been made
    do{
      improved = false;
      for(int i = 0; i < circuit.size(); i++){
        v = i % circuit.size();
        u = (i+1) % circuit.size();
        length = circuit.size()-1+i;
        for(int j = i+2; j < length; j++){
          a = j % circuit.size();
          b = (j+1) % circuit.size();

          //System.out.println("i = " + i + " j = " + j);
          
          if( graph.adjMat[circuit.get(v).getKey()][circuit.get(u).getKey()] + graph.adjMat[circuit.get(a).getKey()][circuit.get(b).getKey()] > 
              graph.adjMat[circuit.get(v).getKey()][circuit.get(a).getKey()] + graph.adjMat[circuit.get(u).getKey()][circuit.get(b).getKey()]){
              circuit.swap(v, u, a, b);

              comp.clearLines();
              comp.createLines(circuit);

              try{
                Thread.sleep(30); 
              }catch(InterruptedException e){ Thread.currentThread().interrupt();}

              improvements++;
              improved = true;
          }
        }
      }
    }while(improved);
  }
}

//Class Graph is used to store the weights of the edges
  //between each vertex set
class Graph{
  public double adjMat[][];
  public int numV;
 
  public Graph(int numV) {
    this.numV = numV;
    adjMat = new double[numV][numV];
  }
 
  public void addEdge(int i, int j, double w) {
    adjMat[i][j] = w;
    adjMat[j][i] = w;
  }

  public double getWeight(int i, int j){
    return adjMat[i][j];
  }

}

class Circuit{

  private Vertex first;
  private int size = 0;

  public Circuit(){ first = null; }
  public Circuit(Vertex p){
      first = p;
      size++;
  }

  public int size(){ return size; }
  private void setFirst(Vertex p){ first = p; }

  public Vertex getFirst(){ return first; }
  public Vertex getNext(Vertex p){ return p.getNext(); }
  public Vertex getPrev(Vertex p){ return p.getPrev(); }

  //Returns the vertex at index j of the Circuit
  public Vertex get(int j){
    Vertex cont = first;
    for(int i = 0; i < j; i++)
        cont = cont.getNext();
    return cont;
  }

  public void add(Vertex u){
    if(size == 0) setFirst(u);
    else if(size == 1){ 
      first.setNext(u);
      first.setPrev(u);
      u.setNext(first);
      u.setPrev(first);
    }
    else{
      Vertex last = first.getPrev();
      last.setNext(u);
      first.setPrev(u);
      u.setNext(first);
      u.setPrev(last);
    }
    size++;
  }

  //Changes the order of the cicuit if needed
  public void swap(int i, int iPlus, int j, int jPlus){
    Vertex a = get(i);
    Vertex aPlus = get(iPlus);
    Vertex b = get(j);
    Vertex bPlus = get(jPlus);
    a.setNext(b);
    aPlus.setPrev(bPlus);
    b.setNext(a);
    bPlus.setPrev(aPlus);
    
    Vertex cont = b;
    //Do-while invokes the exhange method to set the proper path 
    do{
        cont.exchange();
        cont = cont.getNext();
    }while(!cont.equals(bPlus));
  }

  //Checks to see if Vertex p has the same coords as
    //an existing Vertex
  public boolean isMade(Vertex p){
    Vertex cont = first;
    for(int i = 0; i < size; i++)
        if(cont.equals(p))
            return true;
    return false;
  }

  public String toString(){
    String s = "";
    s += "[";
    for(int i = 0; i < size; i++){
      if(i % 5 == 0 && i != 0)
        s += "\n";
      if(i != 0)
        s += " ";
      s += get(i) + ", ";
    }
    s += "\b\b]";
    return s;
  }
}

class Vertex extends Point{
  private Vertex prev, next; 
  //Counter must be static, else problems
  private static int count = 0;
  //The key is the reference value to the position of the
    //vertex in the adjacency matrix. This eliminates the need
    //to alter the values within the matrix in anyway
  private int key;

  public Vertex (int x, int y){
    this(x, y, null, null);
  }

  public Vertex (int x, int y, Vertex newPrev, Vertex newNext){
    //The key is incremented each time a new vertex is made
    key = count++;
    prev = newPrev;
    next = newNext;
    this.x = x;
    this.y = y;
  }

  public Vertex getNext() { return next; }
  public Vertex getPrev() { return prev; }
  public int getKey(){ return key; }

  public void setNext(Vertex newNext){ next = newNext; }
  public void setPrev(Vertex newPrev){ prev = newPrev; }

  //This is envoked if a vertex is made with the same coords 
    //as a previously made one
  public void decrement(){ count--;}
 
  public boolean equals(Vertex p){
    if(this.getX() == p.getX() && this.getY() == p.getY())
      return true;
    return false;
  }

  //Swaps the reference of next an previous
  public void exchange(){
    Vertex temp = next;
    next = prev;
    prev = temp;
  }
}

class LinesComponent extends JComponent{
  private static class Line{
    final int x1;
    final int y1;
    final int x2;
    final int y2;
    private Color color;
    private BasicStroke thickness; 
    
    public Line(int x1, int y1, int x2, int y2, Color color) {
      this.x1 = x1; 
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      thickness = new BasicStroke(1); 
      this.color = color;
    }

    public void setColor(Color color){this.color = color;}
    public void setThickness(int t){thickness = new BasicStroke(t);}

    public BasicStroke getBasicStroke(){return thickness;}
    public int getX1(){return x1;}
    public int getX2(){return x2;}
    public int getY1(){return y1;}
    public int getY2(){return y2;}
    }

    public LinkedList<Line> lines = new LinkedList<Line>(); //List of all lines

    public void addLine(int x1, int y1, int x2, int y2) {
        addLine(x1, y1, x2, y2, Color.yellow);//Adds the line, with default color BLACK
    }

    //Creates the edges based on the circuit
    public void createLines(Circuit circuit){
      Vertex contem = circuit.getFirst(); //Start of circuit
      Vertex next = contem.getNext(); 
      //Adding circuit edges
      addLine((int)contem.getX(), (int)contem.getY(), (int)next.getX(), (int)next.getY());
      contem = contem.getNext();
      next = contem.getNext();
      while(next != circuit.getFirst().getNext()){
          addLine((int)contem.getX(), (int)contem.getY(), 
                  (int)next.getX(), (int)next.getY());
          contem = contem.getNext();
          next = contem.getNext();
      }
    }

    //Add a line to the circuit and repaint
    public void addLine(int x1, int y1, int x2, int y2, Color color){
        lines.add(new Line(x1,y1,x2,y2, color));        
        repaint();
    }

    //Clear the lists in order to update the cycle
    public void clearLines(){
        lines.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g; 

        try{
            for (Line line : lines) {
                g2.setStroke(line.getBasicStroke());
                g2.setColor(line.color); 
                g2.draw(new Line2D.Float(line.x1, line.y1, line.x2, line.y2)); 
            }
        }catch(Exception e){}
    }
}