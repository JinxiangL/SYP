import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class MyButton extends JButton 
{

    private boolean isLastButton;

    public MyButton() 
    {
        // calls the parent constructor with no arguments
        super();
        initUI();
    }

    public MyButton(Image image) 
    {
        // calls the parent constructor with arguments
        super(new ImageIcon(image));
        initUI();
    }
    // set the interface of the game
    private void initUI() 
    {

        isLastButton = false;
        // creates a line border with the specified color and width. The width applies to all four sides of the border
        BorderFactory.createLineBorder(Color.gray);

        addMouseListener(new MouseAdapter() 
        {
            @Override // checks a method that override its parent method
            public void mouseEntered(MouseEvent e) 
            {
                setBorder(BorderFactory.createLineBorder(Color.yellow));
            }

            @Override // checks a method that override its parent method
            public void mouseExited(MouseEvent e)
            {
                setBorder(BorderFactory.createLineBorder(Color.gray));
            }
        });
    }

    public void setLastButton() 
    {     
        isLastButton = true;
    }

    public boolean isLastButton() 
    {
        return isLastButton;
    }
}

public class PuzzleEx extends JFrame 
{
    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;    
    private Image image;
    private MyButton lastButton;
    private int width, height;    
    
    private List<MyButton> buttons;
    private List<Point> solution;

    private final int NUMBER_OF_BUTTONS = 12;
    private final int DESIRED_WIDTH = 800;

    public PuzzleEx() 
    {
        initUI();
    }

    private void initUI() 
    {
        solution = new ArrayList<>();
        
        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));

        buttons = new ArrayList<>();
        // build the panel displayed on the screen
        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 3, 0, 0));

        try 
        {
			// make the picture fit the size of the panel
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h, BufferedImage.TYPE_INT_ARGB);

        } catch (IOException ex) {
            Logger.getLogger(PuzzleEx.class.getName()).log(Level.SEVERE, null, ex);
        }

        width = resized.getWidth(null);
        height = resized.getHeight(null);

        add(panel, BorderLayout.CENTER);

        for (int i = 0; i < 4; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
				// take an existing image and a filter object and uses them to produce image data for a new filtered version of the original image
                image = createImage(new FilteredImageSource(resized.getSource(), new CropImageFilter(j * width / 3, i * height / 4, (width / 3), height / 4)));
                
                MyButton button = new MyButton(image);
                // store the original information of position-object pairs for each button
                button.putClientProperty("position", new Point(i, j));

                if (i == 3 && j == 2) 
                {
					// remove the last piece of picture
                    lastButton = new MyButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("position", new Point(i, j));
                } 
                else 
                {
                    buttons.add(button);
                }
            }
        }
        // randomly permute the specified list 
        Collections.shuffle(buttons);
        buttons.add(lastButton);

        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) 
        {
            MyButton btn = buttons.get(i);
            // add the button into the panel
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(new ClickAction());
        }
        
        // size the frame so that all its contents are at or above their preferred sizes
        pack(); 
        setTitle("Puzzle");
        // cannot change the size of window
        setResizable(false);
        //  the window is placed in the center of the screen
        setLocationRelativeTo(null);
        // when the user close the window, then the game exit
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private int getNewHeight(int w, int h) 
    {
        double ratio = DESIRED_WIDTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    private BufferedImage loadImage() throws IOException 
    {
        BufferedImage bimg = ImageIO.read(new File("pic.jpg"));
        return bimg;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) throws IOException 
    {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private class ClickAction extends AbstractAction 
    {
		// When the action event occurs, that object's actionPerformed method is invoked.
        @Override
        public void actionPerformed(ActionEvent e)
        {
            checkButton(e);
            checkSolution();
        }

        private void checkButton(ActionEvent e) 
        {
            int lidx = 0;   
            for (MyButton button : buttons) 
            {
                if (button.isLastButton()) 
                {
					// get the index of last button
                    lidx = buttons.indexOf(button);
                }
            }

            JButton button = (JButton) e.getSource();
            // get the index of button clicked by the user
            int bidx = buttons.indexOf(button);
            
            if ((bidx - 1 == lidx) || (bidx + 1 == lidx) || (bidx - 3 == lidx) || (bidx + 3 == lidx)) 
            {
				// exchange the position of two buttons
                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }
		
		// re-build the panel
        private void updateButtons() 
        {
            panel.removeAll();
            for (JComponent btn : buttons) 
            {
                panel.add(btn);
            }
            // performs relayout of the panel
            panel.revalidate();
            panel.repaint();
        }
    }

    private void checkSolution() 
    {
        List<Point> current = new ArrayList<>();

        for (JComponent btn : buttons) 
        {
			// get the original information of position-object pairs for each button
            current.add((Point) btn.getClientProperty("position"));
        }
        // check if the user win the game
        if (compareList(solution, current)) 
        {
            JOptionPane.showMessageDialog(panel, "Finished", "Congratulation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static boolean compareList(List ls1, List ls2) 
    {
        return ls1.toString().contentEquals(ls2.toString());
    }

    public static void main(String[] args) 
    {
		// make sure the update of game status runs correctly
        EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                PuzzleEx puzzle = new PuzzleEx();
                puzzle.setVisible(true);
            }
        });
    }
}
