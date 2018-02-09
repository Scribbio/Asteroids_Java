import java.awt.*;

//make sure to add info on move here!


/**
 *The Shot class will draw and move the shots fired by the player. These shots will
`* disappear if they go too long without hitting an asteroid.
 */
public class Shot {

  final double shotSpeed=12; //the speed at which the shots move, in pixels per frame

  double x,y,xVelocity,yVelocity; //variables for movement

  int lifeLeft; //causes the shot to eventually disappear if it doesn’t
              //hit anything
 public Shot(double x, double y, double angle, double shipXVel, double shipYVel, int lifeLeft){
    this.x = x;
    this.y = y;
    
    // add the velocity of the ship to the shot velocity
    // (so the shot's velocity is relative to the ship's velocity)
    xVelocity = shotSpeed*Math.cos(angle)+shipXVel;
    yVelocity = shotSpeed*Math.sin(angle)+shipYVel;
    
    // the number of frames the shot will last for before
    // disappearing if it doesn't hit anything
    this.lifeLeft = lifeLeft;
 }
    
 public void move(int scrnWidth, int scrnHeight){
    lifeLeft--; // used to make shot disappear if it goes too long
                // without hitting anything
    
    x+=xVelocity; // move the shot
    y+=yVelocity;
    
    if(x<0) x+=scrnWidth; // wrap the shot around to the opposite side of the screen if needed
    
    else if(x>scrnWidth)x-=scrnWidth;
    
    if(y<0) y+=scrnHeight;
    
    else if(y>scrnHeight) y-=scrnHeight;
    
 }

 public void draw(Graphics g){
    g.setColor(Color.yellow); //set shot color
    //draw circle of radius 3 centered at the closest point
    //with integer coordinates (.5 added to x-1 and y-1 for rounding)
    g.fillOval((int)(x-.5), (int)(y-.5), 3, 3);
 }

 public double getX(){
    return x;
 }

 public double getY(){
    return y;
 }

 public int getLifeLeft(){
    return lifeLeft;
 }
}
    
    
    

