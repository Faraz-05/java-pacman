import javax.swing.JFrame;  //This imports the JFrame class from the javax.swing package, which is essential for creating a window in a Java Swing application

public class App {
    public static void main(String[] args) {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");   //creating a window (title of our window)
        //frame.setVisible(true);  //frame shpould be visible to the user
        frame.setSize(boardWidth , boardHeight);  //size of our game frame
        frame.setLocationRelativeTo(null);  //frame should be present in between of our screen
        frame.setResizable(false);  //making sure that user cant resize the frame of our game
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Creating an instance of this Jpanel
        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);  //This panel should be added into our window
        frame.pack();  //Adjusts the JFrame to fit the preferred size of the components it contains, based on the panel's dimensions.
        pacmanGame.requestFocus();  //Ensures that the PacMan panel receives keyboard focus so that it can capture key inputs for controlling Pac-Man
        frame.setVisible(true);  //Makes the JFrame visible, displaying the game window to the user.



    }
}
