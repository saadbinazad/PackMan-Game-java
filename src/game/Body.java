package game;


import java.awt.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Body extends JPanel implements ActionListener {

    private final Font smallFont = new Font("Arial", Font.BOLD, 16);
    private boolean inGame = false;
    private boolean dying = false;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int MAX_GHOSTS = 12;
    private final int PACMAN_SPEED = 6;

    private int N_GHOSTS = 6;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image heart, ghost;
    private Image up, down, left, right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int keyX, keyY;

    private final short gameEnvironment[] = {
    	19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        17, 16, 16, 24, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 20,
        17, 16, 20,  0,  0,  0,  0,  0,  0,  0,  0,  0, 17, 16, 20,
        17, 16, 20,  0,  0,  0,  0,  0,  0,  0,  0,  0, 17, 16, 20,
        17, 16, 16, 18, 18, 18, 18, 18, 18, 18, 18, 18, 16, 16, 20,
        17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        17, 16, 24, 24, 16, 16, 16, 16, 16, 16, 16, 24, 24, 16, 20,
        17, 20,  0,  0, 17, 16, 16, 16, 16, 16, 20,  0,  0, 17, 20,
        17, 20,  0,  0, 17, 16, 16, 16, 16, 16, 20,  0,  0, 17, 20,
        17, 16, 18, 18, 16, 16, 16, 16, 16, 16, 16, 18, 18, 16, 20,
        17, 16, 16, 16, 16, 24, 16, 16, 16, 24, 16, 16, 16, 16, 20,
        17, 16, 16, 16, 20,  0, 25, 24, 28,  0, 17, 16, 16, 16, 20,
        17, 16, 16, 16, 20,  0,  0,  0,  0,  0, 17, 16, 16, 16, 20,
        17, 16, 16, 16, 16, 18, 18, 18, 18, 18, 16, 16, 16, 16, 20,
        25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    public Body() {

        loadImages();
        initVariables();
        addKeyListener(new KeyHandle());//keyAdapter
        setFocusable(true);//Interact with user input via the keyboard
        startingPoint();
    }
    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(40, this);
        timer.start();
    }
    
    private void startingPoint() {

    	lives = 3;
        score = 0;
        
        N_GHOSTS = 6;
        currentSpeed = 3;
        initialObstLvl();
    }
    private void initialObstLvl() { //making blocks according to the game environment

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = gameEnvironment[i];
        }

        startingOfGame();
    }

    private void startingOfGame() { // 1st level is always continues

    	int dx = 1;
        int rand;

        for (int i = 0; i < N_GHOSTS; i++) {

        	ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_y[i] = 4 * BLOCK_SIZE; //start position
            
            
            ghost_dx[i] = dx;
            ghost_dy[i] = 0;
            dx = -dx;
            rand = (int) (Math.random() * (currentSpeed + 1));

            if (rand > currentSpeed) {
                rand = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[rand];
        }

        pacman_x = 7 * BLOCK_SIZE;  //start position
        pacman_y = 8 * BLOCK_SIZE;
        pacmand_x = 0;	//initial direction to move (no movement)
        pacmand_y = 0;
        keyX = 0;		// initial direction controls (no key pressed  yet)
        keyY = 0;
        dying = false;
    }
    
    private void loadImages() {
    	 heart = new ImageIcon("/Users/sksaa/eclipse-workspace/JavaGame/heart.png").getImage();
    	ghost = new ImageIcon("/Users/sksaa/eclipse-workspace/JavaGame/ghost.gif").getImage();
    	right = new ImageIcon("/Users/sksaa/eclipse-workspace/JavaGame/right.gif").getImage();
    	left = new ImageIcon("/Users/sksaa/eclipse-workspace/JavaGame/left.gif").getImage();
    	  up = new ImageIcon("/Users/sksaa/eclipse-workspace/JavaGame/up.gif").getImage();
    	down = new ImageIcon("/Users/sksaa/eclipse-workspace/JavaGame/down.gif").getImage();

    }
    private void death() {

    	lives--;

        if (lives == 0) {
            inGame = false;
        
        }

        startingOfGame();
    }
     
    private void gameIsPlaying(Graphics2D g2d) 
    {

        if (dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

   

  

    private void checkMaze() { //score collection

        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }
            
            initialObstLvl(); //Just like adding a new level.
        }
    }

    

    private void moveGhosts(Graphics2D g2d) {

        int pos;
        int count;

        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

           if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {
    	g2d.drawImage(ghost, x, y, this);
        }

    private void movePacman() {

        int pos;
        short ch;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) 
        	
        {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) { //scoring
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (keyX != 0 || keyY != 0) {
                if (!((keyX == -1 && keyY == 0 && (ch & 1) != 0)
                        || (keyX == 1 && keyY == 0 && (ch & 4) != 0)
                        || (keyX == 0 && keyY == -1 && (ch & 2) != 0)
                        || (keyX == 0 && keyY == 1 && (ch & 8) != 0))) {
                    pacmand_x = keyX;
                    pacmand_y = keyY;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        } 
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) { //drawing pacman 

        if (keyX == -1) {
        	g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (keyX == 1) {
        	g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (keyY == -1) {
        	g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
        	g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }

    private void maze(Graphics2D g2d)//drawing maze (full area)
    
    {

        int i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(186, 20, 109));
                g2d.setStroke(new BasicStroke(5));
                
                if ((gameEnvironment[i] == 0)) { 
                	g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                 }

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(new Color(5, 243, 247));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
               }

                i++;
            }
        }
    }

    
    private void introMsg(Graphics2D g2d) {
   	 
      	 
        String start = "Press Enter to Begin...";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (SCREEN_SIZE)/4, 150);
        
    }
    private void scoreBoard(Graphics2D g) { //drawing score and lives
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String str = "Score: " + score;
        g.drawString(str, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);//position of heart.
        }
    }
   
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, 400, 400);// GameArea create

        maze(g2d);
        scoreBoard(g2d);

        if (inGame) 
        {
            gameIsPlaying(g2d);
        } else 
        {
            introMsg(g2d);
        }

        Toolkit.getDefaultToolkit().sync();//update display with the change (here, optional)
        g2d.dispose();//close the use of g2d obj.
    }
   

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();//update painting of components
    }
  //controlling through keys
    class KeyHandle extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                	keyX = -1;
                	keyY = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                	keyX = 1;
                	keyY = 0;
                } else if (key == KeyEvent.VK_UP) {
                	keyX = 0;
                	keyY = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                	keyX = 0;
                	keyY = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } 
            } else {
                if (key == KeyEvent.VK_ENTER) {
                    inGame = true;
                    startingPoint();
                }
            }
        }
}

		
	}