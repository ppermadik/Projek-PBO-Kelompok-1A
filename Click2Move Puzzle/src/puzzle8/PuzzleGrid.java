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
import java.awt.event.*;
import java.awt.*;
import java.sql.*;
import java.util.Random;


public class PuzzleGrid extends JPanel{
    // Ukuran game puzzle
    private int size; 
    // Jumlah tiles yang akan ada di games
    private int nbTiles; 
    // Ukuran dimensi grid puzzle
    private int dimension; 
    // Warna foreground
    private static final Color FOREGROUND_COLOR = new Color(214, 103, 59); 
    // Objek random untuk mengacak puzzle
    private static final Random RANDOM = new Random(); 
    // Menyimpan tile di array 1 dimensi
    private int[] tiles; 
    // Ukuran masing-masung tile
    private int tileSize; 
    // Tempat blank harus berada
    private int blankPos; 
    // Jarak dari ujung board ke grid puzzle
    private int margin; 
    // Ukuran grid UI untuk puzzle
    private int gridSize; 
    // Jumlah klik selama satu sesi game
    private int clickNum;
    // Jumlah klik paling sedikit yang dibutuhkan untuk menyelesaikan game
    private int highScore=9999;
    // menyimpan waktu terbaik
    private String bestTime="24 h 59 m 59 s";
    //penghitung waktu
    int hour, minute, second;
    //penghenti thread
    private boolean stop;
    // Bernilai true jika game berakhir
    private boolean gameOver;
    // Objek Logix game puzzle
    private Logic logic; 
    // ActionListener menggunakan mouse alias MouseListener
    private ClickListener cl; 
    //objek yang digunakan untuk menjalankan method di class Query
    private Query db = new Query();
    //untuk menampung data yang telah diambil dari database
    private ResultSet rs;

    public PuzzleGrid(int size, int dim, int mar){
        this.size = size;
        this.dimension = dim;
        this.margin = mar;
        this.clickNum = 0;
        setData();


        // Menghitung jumlah tile
        this.nbTiles = size * size - 1; // Jumlah tile tidak termasuk bagian <blank>
        this.tiles = new int[size * size];

        // Menghitung ukuran grid puzzle dan tile puzzle
        this.gridSize = (dimension - 2 * margin);
        this.tileSize = gridSize / size;

        // Instansiasi Logic dan ClickListener
        this.logic = new Logic();
        this.cl = new ClickListener(this);

        // Mengatur ukuran PuzzleGrid berdasarkan dimension
        setPreferredSize(new Dimension(dimension + 200, dimension + margin));
        setBackground(Color.WHITE);
        setForeground(FOREGROUND_COLOR);
        setFont(new Font("Courier New", Font.BOLD, 30));

        // Set gameOver di construct agar game dapat dimainkan
        gameOver = true;

        // Menambah MouseListener menggunakan class ClickListener
        addMouseListener(cl);

        // Reset Button
        JButton reset = new JButton("Reset");
        add(reset);
        reset.addActionListener(new ActionListener(){
          @Override
          public void actionPerformed(ActionEvent ae){
            // Jika tiles yang bernilai 0 tidak di posisi akhir, akan memunculkan warning message
            if(tiles[size*size - 1] != 0)
              JOptionPane.showMessageDialog(null, 
                                            "Pindahkan posisi kosong ke ujung kanan bawah sebelum mereset!", 
                                            "Tidak bisa reset!", 
                                            JOptionPane.WARNING_MESSAGE,
                                            new ImageIcon("../img/error.png"));
            else{
              newGame();
              repaint();
            }
          }
        });
        // Memulai game
        newGame();
      }

    // Menampilan tulisan untuk memulai game ketika gameOver
    private void drawStart(Graphics2D g){
      if(gameOver){
        g.setFont(getFont().deriveFont(Font.BOLD, 18));
        g.setColor(FOREGROUND_COLOR);
        String s = "Klik di mana saja untuk memulai game.";
        g.drawString(s, ((getWidth() - g.getFontMetrics().stringWidth(s)) / 2) - 90, 
                      getHeight() - margin + 20);
        db.insertData(getClickNum(), getTime());
        setData();
      }
    }

    // Menggambar sebuah angka di tengah objeknya berdasarkan (x, y)
    private void drawNumber(Graphics2D g, String s, int x, int y){
      FontMetrics fm = g.getFontMetrics();
      int asc = fm.getAscent();
      int desc = fm.getDescent();
      g.drawString(s, x + (tileSize - fm.stringWidth(s)) /2,
                    y + (asc +(tileSize - (asc + desc)) / 2));
    }

    // Menampilkan best time di board
    private void drawBestTime(Graphics2D g){
      g.setFont(getFont().deriveFont(Font.BOLD, 20));
      g.setColor(FOREGROUND_COLOR);
      g.drawString("Best Time", dimension + 15, (int) (0.35*dimension));
      g.drawString(bestTime, dimension + 10, (int) (0.4*dimension));
    }

    // Menampilkan highscore di board
    private void drawHighScore(Graphics2D g){
      g.setFont(getFont().deriveFont(Font.BOLD, 20));
      g.setColor(FOREGROUND_COLOR);
      g.drawString("High Score", dimension + 15, (int) (0.45*dimension));
      g.drawString(String.valueOf(highScore), dimension + 70, (int) (0.5*dimension));
    }

    // Menampilkan waktu di board
    private void drawTime(Graphics2D g){
      g.setFont(getFont().deriveFont(Font.BOLD, 20));
      g.setColor(FOREGROUND_COLOR);
      g.drawString("Time", dimension + 55, (int) (0.55*dimension));
      g.drawString(String.valueOf(hour) + " h " + String.valueOf(minute) + " m " + String.valueOf(second) + " s", dimension + 10, (int) (0.6*dimension));
    }

    // Menampilkan jumlah klik user selama satu sesi game
    private void drawClickNum(Graphics2D g){
      g.setFont(getFont().deriveFont(Font.BOLD, 20));
      g.setColor(FOREGROUND_COLOR);
      g.drawString("Click Numbers", dimension, (int) (0.65*dimension));
      g.drawString(String.valueOf(this.clickNum), (dimension + 70), (int) (0.7*dimension));
    }


    // Menggambar keseluruhan grid berdasarkan size
    private void drawGrid(Graphics2D g){
      for(int i = 0; i < tiles.length; i++){
        // Menentukan row & column untuk setiap tile puzzle
        int row = i / size;
        int col = i % size;

        // Memasukkan koordinat ke UI
        int x = margin + col * tileSize;
        int y = margin + row * tileSize;

        // Tiles kosong yang akan disimpan di posisi terakhir
        if(tiles[i] == 0){
          if(gameOver){
            g.setColor(FOREGROUND_COLOR);
            drawNumber(g, "Done!", x, y);
          }
          continue;
        }

        // Menggambar tile lain atau tile normal
        g.setColor(getForeground());
        g.fillRoundRect(x, y, tileSize, tileSize, 25, 25);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, tileSize, tileSize, 25, 25);
        g.setColor(Color.WHITE);

        // By Default, tile akan diisi menggunakan angka yang menjadi value tiles[i]
        drawNumber(g, String.valueOf(tiles[i]), x, y);
      }
    }

    public void newGame(){
      do{
        reset(); // Mengembalikan semua tile seperti semula
        shuffle(); // Mengacak tile
      } while(!logic.isSolvable(this)); // Looping dilakukan selama Puzzle tidak bisa diselesaikan
      gameOver = false;
      stopThread();
    }

    // Menyusun ulang tiles seperti keadaan semula
    private void reset(){
      for(int i = 0; i < tiles.length; i++){
        tiles[i] = (i + 1) % tiles.length;
      }

      // Menyimpan blankPos di posisi teralhir dari array
      blankPos = tiles.length - 1;

      // Menyimpan nilai 0 untuk clickNum
      this.clickNum = 0;
    }

    private void shuffle(){
      // Mengacak semua tiles tanpa blankPos
      int n = nbTiles;

      while(n > 1){
        int r = RANDOM.nextInt(n--);
        int tmp = tiles[r];
        tiles[r] = tiles[n];
        tiles[n] = tmp;
      }
    }

    //Thread waktu
    private class Time extends Thread{
      @Override
      public void run(){
          try{
            for (hour= 0; hour<=24; hour++){
              for (minute = 0; minute<=60; minute++){
                  for (second = 0; second<=60; second++){
                      repaint(); //berfungsi untuk tetap mengupdate tampilan waktu
                      sleep(1000);
                      if(stop){break;}
                  }
                  if(stop){break;}
              }
              if(stop){break;}
            }
          }catch(InterruptedException e){
            System.out.println(e.getMessage());
          }
      }
    }

    public int getDimension(){
      return this.dimension;
    }
    public int getMargin(){
      return this.margin;
    }
    public int getGridSize(){
      return this.size;
    }
    public int[] getTiles(){
      return this.tiles;
    }
    public boolean getGameStatus(){
      return this.gameOver;
    }
    public void setGameOver(){
      this.gameOver = true;
    }
    public void addClickNum(){
      this.clickNum++;
    }
    public int getClickNum(){
      return this.clickNum;
    }
    public void setHighScore(int clickNum){
      setData();
    }
    public String getTime(){
        String waktu = String.valueOf(hour) + " h " + String.valueOf(minute) + " m " + String.valueOf(second) + " s";
        return waktu;
    }
    public void startThread(){
      stop = false;
      // objek untuk mengatur thread
      Time time = new Time();
      time.start();
    }
    public void stopThread(){
      stop = true;
    }

    // Paint panel menggunakan method ini
    @Override
    protected void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D gtd = (Graphics2D) g;
      gtd.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      drawGrid(gtd);
      drawStart(gtd);
      //Menggambar best time di method ini
      drawBestTime(gtd);
      //Menggambar time di method ini
      drawTime(gtd);
      // Menggambar clickNum di method ini
      drawClickNum(gtd);
      // Menggambar highscore di method ini
      drawHighScore(gtd);
    }

    private void setData(){
      // mengambil data best time dan hidhscore
        rs = db.getData();
        if(rs == null){
          highScore = 9999;
          bestTime = "24 h 59 m 59 s";
        }
        else{
          try{
            while(rs.next()){
              if(rs.getString("waktu").compareTo(bestTime)<0 && rs.getInt("highscore")<highScore){
                highScore  = rs.getInt("highscore");
                bestTime = rs.getString("waktu");
              }
            }
          }catch(Exception e){
            e.printStackTrace();
          }
          
        }
    }

}
