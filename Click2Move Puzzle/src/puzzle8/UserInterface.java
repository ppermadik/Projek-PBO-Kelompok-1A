/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzle8;

/**
 *
 * @author Sabirin
 */
import javax.swing.*;
import java.awt.*;

public class UserInterface implements Runnable{
  // Frame untuk game
  private JFrame frame; 
  // PuzzleGrid berisi grid, logic, dan listener
  private PuzzleGrid pg; 
  // Resolusi windows game
  private int dimension; 
  // Jarak dari ujung board ke grid puzzle
  private int margin; 

  public UserInterface(int size, int dim, int mar){
    this.pg = new PuzzleGrid(size, dim, mar);
    this.dimension = dim;
    this.margin = mar;
  }

  public void run(){
    frame = new JFrame("CLick2Move Puzzle");
    
    frame.setPreferredSize(new Dimension(dimension + 200, dimension + margin));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setResizable(false);

    createComponents(frame.getContentPane());

    frame.pack();
    frame.setVisible(true);
  }

  private void createComponents(Container container){
    container.add(pg, BorderLayout.CENTER);
  }

  public JFrame getFrame(){
    return frame;
  }
}
