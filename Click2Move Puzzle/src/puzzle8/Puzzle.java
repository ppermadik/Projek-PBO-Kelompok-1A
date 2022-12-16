/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzle8;

import javax.swing.SwingUtilities;

public class Puzzle {
  public static void main(String[] args) {
    Menu mn = new Menu();
    SwingUtilities.invokeLater((Runnable) mn);
  }
  
  void setVisible(boolean b){
      throw new UnsupportedOperationException("Not supported yet.");
  }
}
