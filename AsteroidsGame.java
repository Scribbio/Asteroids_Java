import java.applet.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The AsteroidsGame class will contain the code that runs the game as a whole. It will be
 * the first class we create. AsteroidsGame will create and manage all the instances of the other
 * classes, take care of event handling (responding to user input), and connect the game into
 * internet browsers as an applet. 
 */

/**Java provides the Thread class and the Runnable interface for creating threads. Since we can
*only inherit from one class (and we already have from Applet), 
*we will make the AsteroidsGame class implement the Runnable interface.
*
*The Runnable interface defines a single method, run, meant to contain the code executed in the thread. 
*The Runnable object is passed to the Thread constructor, as in the HelloRunnable example:
*/
public class AsteroidsGame extends Applet implements Runnable, KeyListener { //KeyListener (from AWT package) is the listener interface for receiving key board strokes


    
    Thread thread; //Thread in Java is an independent path of execution which is used to run two task in parallel. 
                   //When two Threads run in parallel that is called multi-threading in Java. 

    int x, y ,xVelocity, yVelocity;     
    
    long startTime, endTime, framePeriod; 
       
    Dimension dim;
    Image img;
    Graphics g;
    
    Ship ship;
    boolean paused; //true if the game is paused. Enter is the pause key
    Shot[] shots; //Variable that stores the new array of Shots
    int numShots; //Stores the number of shots in the array
    boolean shooting; //true if the ship is currently shooting 
    
    Asteroid[] asteroids; //the array of asteroids
    int numAsteroids; //the number of asteroids currently in the aray
    double astRadius,minAstVel,maxAstVel; //values used to create asteroids
    int astNumHits,astNumSplit;
    
    int level;//the current level number
    
    public void init()
    //When an applet is launched in the browser, similar to main, the init() method of the specified applet is executed as the first order of business.
    { 
        

       resize(500, 500); // defines the app's dimensions
       
       shots=new Shot[41];//41 is a shot's life period plus one. Since at most one shot can be fired
                          //fired per frame, there will never be more than 41 shots if each one only lives for 40 frame.
                          
                         
       
       numAsteroids=0;
       level=0;//will be incremented to 1 when first level is set up
       astRadius=60; //values used to create the asteroids
       minAstVel=5;
       maxAstVel=5;
       astNumHits=3;
       astNumSplit=2;
       
       startTime = 0;
       endTime = 0;
       framePeriod = 25; // 25 milliseconds is a good frame 
       addKeyListener(this); //adds key listener
       
       ship = new Ship(250,250,0,.35,.98,.1,12);
       paused = false;
       shots=new Shot[41]; //Allocate the space for the array.
            //We allocate enough space to store the maximum number of
            //shots that can possibly be on the screen at one time.
            //41 is the max because no more than one shot can be fired per
            //frame and shots only last for 40s frames (40 is the value passed
            //in for lifeLeft when shots are created)
            
       numShots=0; //no shots on the screen to start with.
       shooting=false; //the ship is not shooting
       
       thread = new Thread(this); //create the thread
       thread.start(); //When a Thread is started in Java by using Thread.start() method it calls Runnable's run() method 
                       //Code written inside run() method is executed by this newly created thread. 
       
       xVelocity = 0; //the circle is not moving when the applet starts
       yVelocity = 0; 
       addKeyListener(this); //adds key listener
             
       framePeriod = 25; // 25 milliseconds is a good frame 
       
       dim = getSize(); //get the size of the applet, width and length
       img = createImage(dim.width, dim.height); // creates an off-screen drawable image to be used for double buffering
       g = img.getGraphics(); //The variable g is the Graphics object that we will use to draw on img.
                                             
    } 
    
    public void setUpNextLevel(){//starts a new level with one more asteroid
        level++;
        //create a new, inactive ship centred on the screen
        // I like .35 for acceleration, .98 for velocityDecay, and
        // .1 for rotationalSpeed. They give the controls a nice feel.
        ship=new Ship(250,250,0,.35,.98,.1,12);
        numShots=0; //no shots on the screen at beginning of level
        paused=false;
        shooting=false;
        //create an array large enough to hold the biggest number of asteroids possible on this level (plus one because
        //the split asteroids are created first, then the original one is deleted. The level number is equal to the
        //number of asteroids at it's start.
        
        asteroids=new Asteroid[level *(int)Math.pow(astNumSplit,astNumHits-1)+1];
        
        numAsteroids=level;
        
        //create asteroids in random spots on the screen
        for(int i=0;i<numAsteroids;i++)
            asteroids[i]=new Asteroid(Math.random()*dim.width,
                        Math.random()*dim.height,astRadius,minAstVel,maxAstVel,astNumHits,astNumSplit);
                        
    }
        
    
    
    
    public void paint(Graphics gfx){ //from awt         
        g.setColor(Color.black); //set colour of next "fill" method 
        g.fillRect(0,0,500,500); //Dimensions are same as app window size, makes background black
        
        for(int i=0; i<numShots;i++) //loop that calls draw() for each shot
            shots[i].draw(g);
        
        for(int i=0; i<numAsteroids;i++)
            asteroids[i].draw(g);
                                                             
        ship.draw(g);//draw the ship
        g.drawString("Level " + level,20,20); // draws text on screen
                                                                                   
        gfx.drawImage(img,0,0,this);
       
        //because the background is drawn before the circle, there is a slight delay in processing time resulting in a flicker.
        // we adopt the double buffering technique, instead of drawing your objects one by ne, you draw them on an image (g) then tell the renderer to draw the entire image.
         
        //We first use g to draw everything on img (the back buffer), 
        //then we copy img to the actual screen using gfx (the last line).
                                                                                   
                                                            
    }
    
    
    /**The update() method inherited from Applet first clears the screen, then makes a call to paint(). 
     * Clearing the screen is visible to the user and causes flickering. We will override it so that it makes a call to paint() without clearing the screen.
     */
    
    
    public void update(Graphics gfx){
        paint(gfx); // call paint without clearing the screen
    }
    
    
    /**
     * This interface only requires that we have one additional method, run(), in our class.
     * 
     * 
     * @repaint inherited from applet, repaint() buffers calls to update that proper timing can be used,
     * 
     */
   
    
       public void run(){   
       for(;;){//this infinite loop ends when the webpage is exited
                startTime=System.currentTimeMillis();
                
                //starts next level when all asteroids are destroyed
                if(numAsteroids<=0)
                    setUpNextLevel();
                
                if(!paused) ship.move(dim.width,dim.height); //move the ship
                //this loop moves each shot and deletes dead shots
                for(int i = 0; i<numShots; i++){
                    shots[i].move(dim.width, dim.height);
                    //removes shot if it has gone for too long without hitting anything
                    if(shots[i].getLifeLeft() <=0){
                        //shifts all the next shots up one space in the aray
                        deleteShot(i);
                        i--;//move the outer loop back one so the shot shifted up is not skipped
                    }
                
                    //move asteroids and check for collisions
                    updateAsteroids();//SEE NEW METHOD BELOW
                
                if(shooting && ship.canShoot()){
                    //add a shot on to the array if the ship is shooting
                    shots[numShots]=ship.shoot();
                    numShots++;
                }
            
               }
                repaint();
                //For our game to run smoothly, we want each frame to last for the same amount of time (frame period). 
                // The next 6 lines pause execution for 25milliseconds minus the time it took to move the circle and repaint. (code never runs consistently at same speed)
                // So that each loop takes exactly 25miliseconds (codein the loop can take different amounts of time to execute)
                try{
                        // mark end time 
                        endTime=System.currentTimeMillis();// This method returns a value of type long
                        // don’t sleep for a negative amount of time 
                        if(framePeriod-(endTime-startTime)>0)
                                    //Once the starting and ending times have
                                    //been obtained, the amount of time the loop took to execute so far can be determined by subtracting
                                    //startTime from endTime. This value is then subtracted from framePeriod (25) to obtain the amount of
                                    //time that the thread needs to sleep for                 
                                               
                            Thread.sleep(framePeriod - (endTime-startTime));
                            //The Thread.sleep() method pauses the execution of the thread for the number of milliseconds 
                            //passed in as a parameter. (During this time, the computer is free to run parts of other threads that it is executing.) 
                                                      
                        }catch(InterruptedException e){
                }
        }
    }
    
    private void deleteShot(int index){
       //delete shot and move all shots after it up in the array
        numShots--;
        for(int i=index;i<numShots;i++)     
            shots[i]=shots[i+1];
       shots[numShots]=null;
    }
       
    private void deleteAsteroid(int index){
        //delete asteroid and shift ones after it up in the array
        numAsteroids--;
        for(int i=index;i<numAsteroids;i++)
            asteroids[i]=asteroids[i+1];
        asteroids[numAsteroids]=null;
    }
    
    private void addAsteroid(Asteroid ast){
        //adds the asteroid passed in to the end of the array
        asteroids[numAsteroids]=ast;
        numAsteroids++;
    }
    
    private void updateAsteroids(){
        for(int i=0;i<numAsteroids;i++){
        // move each asteroid
        asteroids[i].move(dim.width,dim.height);
            
        //check for collisions with the ship, restart the
        //level if the ship gets hit
        if(asteroids[i].shipCollision(ship)){
        level--; //restart this level
        numAsteroids=0;
        return; //breaks the loop
        }

        //check for collisions with any of the shots
        for(int j=0;j<numShots;j++){

            if(asteroids[i].shotCollision(shots[j])){
                //if the shot hit an asteroid, delete the shot
                deleteShot(j);
                //split the asteroid up if needed
                if(asteroids[i].getHitsLeft()>1){
                    for(int k=0;k<asteroids[i].getNumSplit();k++)
                    addAsteroid(
                    asteroids[i].createSplitAsteroid(
                    minAstVel,maxAstVel));
                    }
                    //delete the original asteroid
                deleteAsteroid(i);
                j=numShots; //break out of inner loop - it has
                //already been hit, so don’t need to check
                    //for collision with other shots
                i--; //don’t skip asteroid shifted back into
                //the deleted asteroid's position
           }
        }
      }
   }
    
    
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            //These first two lines allow the asteroids to move
            //while the player chooses when to enter the game.
            //This happens when the player is starting a new life.
            if(!ship.isActive() && !paused)
                ship.setActive(true);
            else{
                paused=!paused; //enter is the pause button
                if(paused) // grays out the ship if paused
                    ship.setActive(false);
                else
                    ship.setActive(true);
            }
            }else if(paused || !ship.isActive()) //if the game is paused or ship is inactive, do not respond
                return;
                    //to the controls except for enter to unpause
            else if(e.getKeyCode()==KeyEvent.VK_UP)
                ship.setAccelerating(true);
            else if(e.getKeyCode()==KeyEvent.VK_LEFT)
                ship.setTurningLeft(true);
            else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
                ship.setTurningRight(true);
            else if(e.getKeyCode()==KeyEvent.VK_CONTROL)
                shooting=true; //start shooting when ctrl is push
    }
       

    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_UP)
            ship.setAccelerating(false);
        else if(e.getKeyCode()==KeyEvent.VK_LEFT)
            ship.setTurningLeft(false);
        else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
            ship.setTurningRight(false);
        else if(e.getKeyCode()==KeyEvent.VK_CONTROL)
            shooting=false; //Stop shooting when ctrl is released
    }

    public void keyTyped(KeyEvent e){ //invoked when a key is type, empty method in this game-  but still needed to implement listerner interface
    }
        
        
        
        
        
        
        
        
        
        
        
        
        
    }
   












    

