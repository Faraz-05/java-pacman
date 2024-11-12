import java.awt.*;  // provides classes for creating and managing graphical user interfaces (GUIs), including components like buttons, graphics, and images.+
import java.awt.event.ActionEvent;  //triggers on user interaction i.e click or time events
import java.awt.event.ActionListener;  //This imports the ActionListener interface, which listens for and processes ActionEvents, often caused by user actions or timer events
import java.awt.event.KeyEvent;  //This imports the KeyEvent class, which represents key events in Java, such as pressing, releasing, or typing a key on the keyboard.
import java.awt.event.KeyListener;  //This imports the KeyListener interface, which listens for and processes KeyEvents. A class implementing KeyListener can detect and respond to keyboard actions.
import java.util.HashSet;  //stores unique elements without duplicates.
import java.util.Random;

import javax.swing.*;  //This imports all classes from the javax.swing package, which provides a set of lightweight components for building GUIs in Java. It includes elements like JPanel, JFrame, and ImageIcon.

public class PacMan extends JPanel implements ActionListener, KeyListener{  //actionlistener listen our action and draw the image of elemnets

    class Block {  //class to represent these objects like ghost , wall , food
        int x;
        int y;
        int width;
        int height;
        Image image;

        //just for the restarting the game we need to store the starting position i.e orignial pos x&y 
        //because a s the game goes on the ghost will move and the starting postion of them will be changing
        //hence it needs to be saved
        int startX;
        int startY;

        char direction = 'U';  //U D L R
        int velocityX = 0;
        int velocityY = 0;

        //Constructor
        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
                
        }
        

        void updateVelocity() {
            if(this.direction == 'U'){
                this.velocityX = 0;
                this.velocityY = -tileSize/4;  //pacman will move up by 8 pixels
            }
            else if(this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = tileSize/4;  //pacman will move down by 8 pixels
            }
            else if(this.direction == 'L'){
                this.velocityX = -tileSize/4;
                this.velocityY = 0;  //pacman will move left by 8 pixels
            }
            else if(this.direction == 'R'){
                this.velocityX = tileSize/4;
                this.velocityY = 0;  //pacman will move right by 8 pixels
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    //For the images we have to create the member variables
    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
        
    };

    //Creating a Hashset
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;
   
    Timer gameLoop; //for our game to successfully add each elent or draw it at regural interval we need timer

    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();

    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() {  //Constructor
        setPreferredSize(new Dimension(boardWidth , boardHeight));
        setBackground(Color.BLACK);

        addKeyListener(this);
        setFocusable(true);

        //load images
        //images will be stored in the lhs(variables) of the below mentioned statements
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();  //file path of where the img is
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();  //file path of where the img is
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();  //file path of where the img is
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();  //file path of where the img is
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();  //file path of where the img is

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();  //file path of where the img is
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();  //file path of where the img is
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();  //file path of where the img is
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();  //file path of where the img is

        loadMap(); //calling loadmap inside the constructor

        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];  //direction has 4 charcteres and random.next im port randomly no between 1-4 excluding 4
            ghost.updateDirection(newDirection);  //we assign each ghosts a direction and at each direction it will update the velocity at each direction
        }

        //how long it takes to start timer, millisceond gone between frames       
        //20fps (1000/50)
        gameLoop = new Timer (50, this);  //50-is the delay , this-refers to the pacman operator
        gameLoop.start();
    }

    public void loadMap() {  //going thru the tile map and creating the object i made a function
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        //iterating thru the map
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X') {  //black wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall); //this adds the wall to the hashset
                }
                else if (tileMapChar == 'b') {  //blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') {  //orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') {  //pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') {  //red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') {  //pacman
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') {  //food
                    Block food = new Block(null, x+14, y+14, 4, 4);
                    foods.add(food);
                }
            }
        }
            
    }

    // This line calls the paintComponent method of the superclass (JPanel). 
    //The super keyword refers to the parent class (in this case, JPanel), and super.paintComponent(g) calls JPanel's own paintComponent method.
    //Now as we have our objects lets create it in our game 
    public void paintComponent(Graphics g){
        super.paintComponent(g);  //invoke the function of same name in the Jpanel
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        
        //score 
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    //to actually move pacman we have to define a function 'move'
    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check wall collision
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {

            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }

            //to mask ethe ghost move
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            //to colllide with the walls
            for(Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth   ) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }

            if (pacman.x < 0) {
                pacman.x = boardWidth - pacman.width; // wrap to the right
            } else if (pacman.x + pacman.width > boardWidth) {
                pacman.x = 0; // wrap to the left
            }
            
                
        }

        //check food collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        //to reload the map once we eat all the dots and get all points
        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
        
    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        // to ensure that the game loop doesn’t throw errors if gameLoop is unexpectedly null or stopped
        try {
            if (gameLoop != null && gameLoop.isRunning()) {
                move();
                repaint();
            }
        } catch (Exception ex) {
            System.out.println("Error in game loop: " + ex.getMessage());
            gameLoop.stop();  ////if we call this function the game will stop
        }
    }
  
    

    @Override
    public void keyTyped(KeyEvent e) {  //whn u type on the key that has corresponding char we get char , but if we press arrows nothing will happen
       //not gonna use this
    }

    @Override
    public void keyPressed(KeyEvent e) {  //if i press on any key it will trigger this function also we can hold the key
        //not gonna use this too
    }

    @Override
    public void keyReleased(KeyEvent e) {  //when we press the key it ewill trigger the function but if i let go the trigering will stop
        //System.out.println("KeyEvent: "+ e.getKeyCode());
       
        //To ensure the game doesn’t throw errors on invalid or unhandled key presses
    try {
        //if game gets over we have to press any key to reset the postion and load all the points from the beginning
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        //this will update the direction as well as the velocity when we press a certain key
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        //to update the image whne we go left, right, up, down 
        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }

        }
        catch (Exception ex) {
            System.out.println("Error handling key press: " + ex.getMessage());
        }
    }
}

