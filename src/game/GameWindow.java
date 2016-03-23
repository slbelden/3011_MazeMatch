
/**
 * @author Kim Buckner
 * @author James Scott
 * @author Colin Riley
 * @author Stephen Belden
 * @author Shaya Wolf
 * @author Neil Carrico
 * Date: Feb 19, 2016
 *
 * This is the actual "game". May/will have to make some major changes.
 * This is just a "hollow" shell.
 *
 * When you get done, I should see the buttons at the top in the "play" area
 * (not a pull-down menu). The only one that should do anything is Quit.
 *
 * Should also see something that shows where the 4x4 board and the "spare"
 * tiles will be when we get them stuffed in.
 */

package game;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GameWindow extends JFrame implements ActionListener {
   /**
    * because it is a serializable object, need this or javac complains a lot
    */
   public static final long serialVersionUID = 1;

   /*
    * Here I declare some buttons and declare an array to hold the grid
    * elements. But, you can do what you want.
    */
   public static JButton newButton,resetButton, quitButton;
   private int              startAt          = 1;

   /**
    * Constructor sets the window name using super(), changes the layout, which
    * you really need to read up on, and maybe you can see why I chose this one.
    *
    * @param s
    */

   public GameWindow(String s) {
      super(s);
      GridBagLayout gbl = new GridBagLayout();
      setLayout(gbl);
   }

   /**
    * For the buttons
    * 
    * @param e
    *           is the ActionEvent
    * 
    *           BTW can ask the event for the name of the object generating
    *           event. The odd syntax for non-java people is that "exit" for
    *           instance is converted to a String object, then that object's
    *           equals() method is called.
    */

   public void actionPerformed(ActionEvent e) {
      if ("exit".equals(e.getActionCommand()))
         System.exit(0);
      if ("reset".equals(e.getActionCommand()))
         System.out.println("reset pressed\n");
      if ("new".equals(e.getActionCommand()))
         System.out.println("new pressed\n");
   }

   /**
    * Establishes the inital board
    */

   public void setUp() {
      // actually create the array for elements, make sure it is big enough

      // Need to play around with the dimensionts and the gridx/y values
      // These constraints are going to be added to the pieces/parts I
      // stuff into the "GridBag".
      
      GridBagConstraints basic = new GridBagConstraints();
      basic.anchor = GridBagConstraints.FIRST_LINE_START;
      basic.gridx = startAt;
      basic.gridy = 0;
      basic.gridwidth = 1;
      basic.gridheight = 1;
      basic.fill = GridBagConstraints.BOTH;
      
      
   // adds a row of buffer space at the top of the window
      basic.fill = GridBagConstraints.HORIZONTAL;
      basic.weightx = .01;
      basic.weighty = .01;
      basic.gridx = 1;
      basic.gridy = 1;
      this.add(new JLabel(""), basic); 
      
      basic.fill = GridBagConstraints.HORIZONTAL;
      basic.weighty = .5;
      basic.gridx = 0;
      basic.gridy = 5;
      this.add(new JLabel(""), basic); 
     
      
      //this.addBuffer(basic);
      this.addButtons(basic);
      this.addSideTiles(basic);
      this.addGameGrid(basic);
      
      
      // Here I create 16 elements to put into my gameBoard

      // Now I add each one, modifying the default gridx/y and add
      // it along with the modified constraint
      
      return;

   }
   
   public void addSideTiles(GridBagConstraints basic) {
       
       Border inBorder = BorderFactory.createEtchedBorder();

       JLabel[] leftPanels = new JLabel[8];
       JLabel[] rightPanels = new JLabel[8];
       
       JPanel lRow4 = new JPanel(new GridLayout(2, 1));
       JPanel lRow5 = new JPanel(new GridLayout(4, 1));
       JPanel lRow6 = new JPanel(new GridLayout(2, 1));
       JPanel rRow4 = new JPanel(new GridLayout(2, 1));
       JPanel rRow5 = new JPanel(new GridLayout(4, 1));
       JPanel rRow6 = new JPanel(new GridLayout(2, 1));
       
       for (int i = 0; i < 16; i++) {
           JLabel label = new JLabel(new Integer(i + 1).toString());
           label.setPreferredSize(new Dimension(100, 100));
           label.setBorder(inBorder);
           
           if(i<8){
               leftPanels[i] = label;
               if(i < 2)
                   lRow4.add(label);
               else if(i < 6)
                   lRow5.add(label);
               else if(i < 8)
                   lRow6.add(label);
               
           }
           else{
               rightPanels[i-8] = label;
               if(i < 10)
                   rRow4.add(label);
               else if(i < 14)
                   rRow5.add(label);
               else
                   rRow6.add(label);
           }
       }

           
           basic.fill = GridBagConstraints.RELATIVE;
           basic.weightx = 0;
           basic.weighty = 0;
           
           basic.gridx = 0;
           basic.gridy = 1;
           this.add(lRow4, basic);
           
           basic.gridx = 0;
           basic.gridy = 2;
           this.add(lRow5, basic);
           
           basic.gridx = 0;
           basic.gridy = 3;
           this.add(lRow6, basic);
           
           basic.gridx = 3;
           basic.gridy = 1;
           this.add(rRow4, basic);
           
           basic.gridx = 3;
           basic.gridy = 2;
           this.add(rRow5, basic);
           
           basic.gridx = 3;
           basic.gridy = 3;
           this.add(rRow6, basic);
       }
   
   public void addGameGrid(GridBagConstraints basic){
      Border border = BorderFactory.createLineBorder(Color.black, 1);
      Border inborder = BorderFactory.createEtchedBorder();
      
      // creates a 4x4 gridlayout for the game grid. 
      JPanel panel = new JPanel(new GridLayout (4,4));
      

      panel.setBorder(border);
      
      JLabel[] grid = new JLabel[16];
      
      for(int i = 0;i < 16; i++){
          JLabel temp = new JLabel(new Integer(i+1).toString());
          temp.setBorder(inborder);
          temp.setPreferredSize(new Dimension(100,100));
          grid[i] = temp;
          panel.add(temp);
      }
      
      basic.fill = GridBagConstraints.CENTER;
      basic.weightx = 1;
      basic.gridx = 1;
      basic.gridy = 2;
      this.add(panel, basic);  
   }

   /**
    * Used by setUp() to configure the buttons on a button bar and add it to the
    * gameBoard
    * Takes a GridBagConstraints to position the buttons
    */
   public void addButtons(GridBagConstraints basic) {
      Border border = BorderFactory.createLineBorder(Color.black, 1);
      
      JPanel panel = new JPanel();
      panel.setPreferredSize(new Dimension(250,50));
      panel.setBorder(border);
      
      newButton = new JButton("New Game");    
      resetButton = new JButton("Reset");      
      quitButton = new JButton("Quit");
      
      newButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            // new game action
         }          
      });
      
      resetButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            // reset tiles action
         }          
      });
      
      quitButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            System.exit(0);
         }          
      });
      
      panel.add(newButton);
      panel.add(resetButton);
      panel.add(quitButton); 
      
      basic.fill = GridBagConstraints.RELATIVE;
      basic.weighty = 0.3;
      basic.gridx = 0;
      basic.gridy = 0;
      this.add(panel, basic); 
      
      
      
      return;
   }

};
