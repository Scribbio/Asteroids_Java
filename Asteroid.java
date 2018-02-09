import java.awt.*;
/**
 * The Asteroid class will draw and move the asteroids. Additionally, it will contain
 * methods to detect collisions with the player’s ship and the player’s shots. When the ship and an
 * asteroid collide, the player will have to start the level over again. When the asteroid is hit by a
 * shot, it will either split itself up into smaller asteroids or disappear completely. 
 */
public class Asteroid
{    
    double x, y, xVelocity, yVelocity, radius;
    //Just like the Shot class, Asteroid will need variables to store its location (x and y)
    //and velocity (xVelocity and yVelocity). It will also need variables for its size (radius)    
        
    int hitsLeft, numSplit;
    // Each time the asteroid is shot, it will break up into numSplit smaller asteroids that
    //each have one fewer hitsLeft than the asteroid that was shot. When an asteroid only has one hit
    //left, it will completely disappear when it is shot instead of breaking up any further.
    

    public Asteroid(double x,double y,double radius,double minVelocity, double maxVelocity,int hitsLeft,int numSplit){
        this.x=x;
        this.y=y;
        this.radius=radius;
        this.hitsLeft=hitsLeft; //number of shots left to destroy it
        this.numSplit=numSplit; //number of smaller asteroids it breaks up into when shot
        
        
        //calculates a random direction and a random velocity between minVelocity and maxVelocity
        double vel=minVelocity + Math.random()*(maxVelocity-minVelocity);
        double dir=2*Math.PI*Math.random(); // random direction
        xVelocity=vel*Math.cos(dir);
        yVelocity=vel*Math.sin(dir); 
    }
   
    public void move(int scrnWidth, int scrnHeight){
        x+=xVelocity; //move the asteroid
        y+=yVelocity;
        //wrap around code allowing the asteroid to go off the screen to a distance equal to its radius before entering on the
        //other side. Otherwise, it would go halfway off the sceen,then disappear and reappear halfway on the other side
        //of the screen. Otherwise, if the ship were close to the other edge, the asteroid could appear on top of the shi, giving
        //the player no time to reach
        if(x<0-radius)
            x+=scrnWidth+2*radius;
        else if(x>scrnWidth+radius)
            x-=scrnWidth+2*radius;
        if(y<0-radius)
            y+=scrnHeight+2*radius;
        else if(y>scrnHeight+radius)
            y-=scrnHeight+2*radius;
    }

    public void draw(Graphics g){
        g.setColor(Color.gray); // set color for the asteroid
        // draw the asteroid centered at (x,y)
        g.fillOval((int)(x-radius+.5),(int)(y-radius+.5),(int)(2*radius),(int)(2*radius));
    }

    
    /**
    *returns hitsLeft so that the AsteroidsGame class can
    *determine whether the asteroid should be split up further or not.
    */   
    public int getHitsLeft(){
        //used by AsteroidsGame to determine whether the asteroid should
        //be split up into smaller asteroids or destroyed completely.
        return hitsLeft;
    }

    /**
     * returns the number of smaller asteroids that this asteroid should split into when hit by a shot.
     * 
     */
    public int getNumSplit(){
        return numSplit;
    }

   /**
    * Collision detection is the programming necessary to determine when one object in a game collides with another. 
    * In asteroids, the only collisions we need to detect and respond to are collisions between a shot and an asteroid and collisions between the ship and an asteroid. 
    * Because these both involve an asteroid, we will implement the methods that detect collisions in the Asteroid class. 
    * 
    * To greatly simplify the calculations needed to detect collisions, we will approximate the shape of the ship with a circle. 
    *
    * If the sum of the radii of two circles is greater than the distance between the centers of those circles, then the circles are touching (or overlapping) each other.
    *
    * Using the distance formula, the circles have collided if the following statement is true,
    * 
    * r1+r2 > ((x1–x2)2 + (y1–y2)2)2
    * 
    * Where (x1,y1), (x2,y2) are the centers of the circles and r1, r2 are their radii.
    * 
    * Because calculating square roots requires more processing time than multiplying, we can make our program more efficient by squaring both sides of the inequality.
    * (Squaring the radii before texting equality)
    * 
    * Then, we can determine if the circles have collided by using this inequality:
    * 
    * (r1+r2)2 > (x1–x2)2 + (y1–y2)2
    * 
    
    */
        
    public boolean shipCollision(Ship ship){
   // It does not check for collisions if the ship is not active, (the player is waiting to start a new life or the game is paused).
    
    if(Math.pow(radius+ship.getRadius(),2) > Math.pow(ship.getX()-x,2)+ Math.pow(ship.getY()-y,2) && ship.isActive())
       return true;
    
    return false;
   }

   /**
    * (please refer to above first)
    * When determining if a shot has collided with an asteroid, we will treat the shots as points with no radius.This leaves only the radius of the 
    * asteroid to be squared on the left side of the inequality.
    * 
    * if(ship radius 2) > (x of shot - x of this aseroid)2 + (y of shot - y of this asteroid)2  
    * 
    */
   
   public boolean shotCollision(Shot shot){
    // Same idea as shipCollision, but using shotRadius = 0
    
      
    if(Math.pow(radius,2) > Math.pow(shot.getX()-x,2)+ Math.pow(shot.getY()-y,2))
            return true;
    return false;
    }

   /**
    * We need one last method that will create the smaller asteroids when the asteroid is shot.
    * 
    * A good way to determine the size of the smaller asteroids is to make the sum of their areas equal to the area of the original asteroid. 
    * 
    * This can be done simply by setting their radii equal to the radius of the original asteroid divided by the square root of numSplit. 
    * 
    * The smaller asteroids are given one less hit left than the original asteroid. They will split up into the same number of asteroids as the original 
    * if they still have a hit left. The code for the method is given in Code 6.3.
    * 
    * 
    */ 
    
    
   public Asteroid createSplitAsteroid(double minVelocity,double maxVelocity)
   {
    //when this asteroid gets hit by a shot, this method is called
    //numSplit times by AsteroidsGame to create numSplit smaller
    //asteroids. Dividing the radius by sqrt(numSplit) makes the
    //sum of the areas taken up by the smaller asteroids equal to
    //the area of this asteroid. Each smaller asteroid has one
    //less hit left before being completely destroyed.
    return new Asteroid(x,y,radius/Math.sqrt(numSplit),minVelocity,maxVelocity,hitsLeft-1,numSplit);
   }







}
