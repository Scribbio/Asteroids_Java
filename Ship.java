import java.awt.*; //needed for graphics class

/**
* The Ship class will draw and move the player’s ship. Because the setting of the game is
* in space, the ship’s movement will include drifting. Ship will also create new instances of Shot
* when the player presses the fire key. 
*/
public class Ship
{
   
    //To draw the ship, we will use arrays of x and y coordinates that can be passed into the Graphics.drawPolygon() method.
    

    //To ensure that the shape of the ship remains constant, we will use two arrays of constant values 
    //(origXPts and origYPts) that we will translate to the ship’s location.
    //To simplify rotation calculations, these constant values will be coordinates relative to the center of rotation of the ship.
    
    final double[] origXPts={14,-10,-6,-10}, origYPts={-6,-23, -6}, origFlameXPts={-6,-23,-6}, origFlameYPts={-3,0,3} ;
        
    final int radius=6;  
    
    double x, y, angle, xVelocity, yVelocity, acceleration,velocityDecay, rotationalSpeed; //variables used in movement
    
    boolean turningLeft, turningRight, accelerating, active;
    
    int[] xPts, yPts, flameXPts, flameYPts; //store the current locations of the points used to draw the ship and its flame
    
    int shotDelay, shotDelayLeft; //used to determine the rate of firing
    
    public Ship(double x, double y, double angle, double acceleration,
                double velocityDecay, double rotationalSpeed,
                int shotDelay) {
        // this.x refers to the Ship's x, x refers to the x parameter
        this.x=x;
        this.y=y;
        this.angle=angle;
        this.acceleration=acceleration;
        this.velocityDecay=velocityDecay;
        this.rotationalSpeed=rotationalSpeed;
        
        xVelocity=0; // not moving
        yVelocity=0;
        
        turningLeft=false; // not turning
        turningRight=false;
        accelerating=false; // not accelerating
        
        active=false; // start off paused
        
        xPts=new int[4]; // allocate space for the arrays
        yPts=new int[4];
        
        flameXPts=new int[3];
        flameYPts=new int[3];
        
        this.shotDelay=shotDelay; // # of frames between shots
        shotDelayLeft=0; // ready to shoot
   
    }
    
        public void draw(Graphics g)
        {
               if(accelerating && active){ //draw flame if active 
                for(int i=0; i<3; i++){
                        //Since origFlameXPts and origFlameYPts are relative to the rotation point of the ship, 
                        //we will first rotate their values to the correct direction.
                        //The formulas to rotate a point (X, Y) around the origin by the angle A are:
                        
                        //newX = X*cos(A) – Y*sin(A)
                        //newY = X*sin(A) + Y*cos(A)
                        
                        //Note that the angle is in radians, not degrees (The angle made when the radius is wrapped round the circle:                    
            
                        //After rotating the points, we must move them to the location of the ship by adding x and y 
                        //(the coordinates for the center of the ship). 
                        
                        //Finally, we round the values to the nearest integer by adding .5 and casting them as integers, which truncates
                        //the decimal (i.e. .5 + .5 = 1.0 truncates to 1; .4 + .5 = 0.9 truncates to 0). 
                        
                                               
                        //We cast the values as integers because the drawPolygon() method we will use only accepts integers as parameters.
   
                    
                        flameXPts[i]=(int) (origFlameYPts[i]*Math.cos(angle)-origFlameYPts[i]*Math.sin(angle)+ x+.5);
                        
                        flameXPts[i]=(int) (origFlameYPts[i]*Math.sin(angle)+ origFlameYPts[i]*Math.cos(angle)+y+.5);
                }
                 
                 //Once we have created arrays of integers representing the polygons, we simply set the
                 //color we want them drawn and then make a call to drawPolygon().
                
                g.setColor(Color.red); //set color of flame
                g.fillPolygon(flameXPts,flameYPts,3); // the 1st two parameters are an array of y and y cordinates, 
                                                      //3 is number of points
                                                      
                //to understand the fillpolygon method, see here: https://www.youtube.com/watch?v=bh15DiZPeH8
               
               }
              
               //calculate the polygon for the ship, then draw it
                
               for(int i=0;i<4;i++){
                  xPts[i]=(int)(origXPts[i]*Math.cos(angle)- //rotate
                    origXPts[i]*Math.sin(angle)+ x+.5); //translate and round
                  yPts[i]=(int)(origXPts[i]*Math.sin(angle)+ //rotate
                    origYPts[i]*Math.cos(angle)+ y+.5); //translate and round
                }
               
                if(active) // active means game is running (not paused)
                    g.setColor(Color.white);
                else // draw the ship dark gray if the game is paused
                        g.setColor(Color.darkGray);
                g.fillPolygon(xPts,yPts,4); // 4 is the number of points
        }
                        
       public void move(int scrnWidth, int scrnHeight){
       
        if(shotDelayLeft>0) shotDelayLeft--;  //move() is called every frame that the game is run, so this ticks down the shot delay
        
        if(turningLeft) angle-=rotationalSpeed; //this is backwards from typical polar coordinates because positive y is downward.

        if(turningRight) angle+=rotationalSpeed;  //Because of that, adding to the angle is rotating clockwise (to the right)

        if(angle>(2*Math.PI)) //Keep angle within bounds of 0 to 2*PI
            angle-=(2*Math.PI);
            else if(angle<0)
            angle+=(2*Math.PI);

        if(accelerating){ //adds accel to velocity in direction pointed
            //calculates components of accel and adds them to velocity
            xVelocity+=acceleration*Math.cos(angle);
            yVelocity+=acceleration*Math.sin(angle);
        }

        x+=xVelocity; //move the ship by adding velocity to position
        y+=yVelocity;
        
        xVelocity*=velocityDecay; //slows ship down by percentages (velDecay
        yVelocity*=velocityDecay; //should be a decimal between 0 and 1

        if(x<0) //wrap the ship around to the opposite side of the screen
        x+=scrnWidth; //when it goes out of the screen's bounds
        else if(x>scrnWidth)
        x-=scrnWidth;

        if(y<0)
        y+=scrnHeight;
        else if(y>scrnHeight)
        y-=scrnHeight;
    }
    
    public void setAccelerating(boolean accelerating){
        this.accelerating=accelerating; //start or stop accelerating the ship
    }
    
    public void setTurningLeft(boolean turningLeft){
        this.turningLeft=turningLeft; //start or stop turning the ship
    }

    public void setTurningRight(boolean turningRight){
        this.turningRight=turningRight;
    }

    public double getX(){
        return x; // returns the ship’s x location
    }

    public double getY(){
        return y;
    }

    public double getRadius(){
        return radius; // returns radius of circle that approximates the ship
    }

    public void setActive(boolean active){
        this.active=active; //used when the game is paused or unpaused
    }

    public boolean isActive(){
        return active;
    }

    public boolean canShoot(){
        if(shotDelayLeft>0) //checks to see if the ship is ready to
            return false; //shoot again yet or if it needs to wait longer
        else
            return true;
    }
    
    public Shot shoot(){
        shotDelayLeft=shotDelay; //set delay till next shot can be fired
    
        //a life of 40 makes the shot travel about the width of the
        //screen before disappearing
        return new Shot(x,y,angle,xVelocity,yVelocity,40);
    }
    
    
    
    
}





    
    
    

