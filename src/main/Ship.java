package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

import Terrestrial.bodies.StellarObject;
import parts.Part;
import processing.core.PApplet;
import starmap.StarSystem;
import main.Main;
import java.awt.event.KeyEvent;

public class Ship {
	private enum AIPhases {BRAKE, FIND_TARGET, ACCELERATE, DONE};
	
	private boolean playerControlled = false;
	private LinkedList<Part> partList = new LinkedList<Part>();
	private LinkedList<Dimension> partPositions = new LinkedList<Dimension>();
	private AffineTransform at = new AffineTransform();
	
	private double trajectory = 0.0;
	private double theta = Math.random() * 360.0;
	private double rcs = 4.0;
	private double acc = 0.2;
	private double warpAcc = 4.0;
	private double xSpeed = 0.0;
	private double ySpeed = 0.0;
	private double speed = 0.0;
	private double maxSpeed = 75000;
	
	private long x = 0L;
	private long y = 0L;
	
	private int targetIndex = 0;
	private boolean autoPilot = false;
	private boolean targetPlanet = false;
	private AIPhases autoPilotMode = AIPhases.BRAKE;
	
	private int engineAlpha = 0;
	private int engineAlphaDelta = 20;
	
	private double targetX;
	private double targetY;
	
	private double targetDirection;
	private String targetName;
	
	public Ship(){
		
	}
	
	public Ship(boolean playerControlled){
		this.playerControlled = playerControlled;
	}
	
	public void step(Main main){
		int[] keyState = main.getKeyState();
		
		Main.backgroundX[0]=(int) (x * 0.2);
	    Main.backgroundY[0]=(int) (y * 0.2);

	    Main.backgroundX[1]=(int) (x * 0.2);
	    Main.backgroundY[1]=(int) (y * 0.2);

	    Main.backgroundX[2]=(int) (x * 0.3);
	    Main.backgroundY[2]=(int) (y * 0.3);

	    Main.backgroundX[3]=(int) (x * 0.4);
	    Main.backgroundY[3]=(int) (y * 0.4);
	    
		if(theta < 0)
			theta += 360;
		if(theta > 360)
			theta -= 360;
		
		targetX = Main.getCurrentSystem().getSpobs().get(targetIndex).getX();
		targetY = Main.getCurrentSystem().getSpobs().get(targetIndex).getY();
		
		if(targetPlanet){
			targetDirection = Math.toDegrees(Math.atan2(targetY - y, targetX - x));
			targetName = Main.getCurrentSystem().getSpobs().get(targetIndex).getName();
		}	
		else{
			if(Main.getTargetSystem() != null){
				targetDirection = Math.toDegrees(Math.atan2(Main.getTargetSystem().getY() - Main.getCurrentSystem().getY(), Main.getTargetSystem().getX() - Main.getCurrentSystem().getX()));
				targetName = Main.getTargetSystem().getName();
			}
			else{
				targetDirection = 0.0;
				targetName = "No Target";
			}
		}
		
		if(autoPilot == false){
			if(keyState[KeyEvent.VK_UP] == Game.KEY_HELD && (speed < maxSpeed || Util.angleDifference(theta, Math.toDegrees(trajectory)) > 1.0)){
				trajectory = Math.atan2(ySpeed, xSpeed);
				
				if(speed <= maxSpeed){
					speed = Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2));
					xSpeed += Math.cos(Math.toRadians(theta)) * acc;
					ySpeed += Math.sin(Math.toRadians(theta)) * acc;
				}
				else{
					speed = maxSpeed;
					xSpeed = Math.cos(trajectory) * maxSpeed;
					ySpeed = Math.sin(trajectory) * maxSpeed;
					//System.out.println("xSpeed: " + xSpeed + "\nySpeed: "+ ySpeed);
				}
				engineAlpha += engineAlphaDelta;
				if (engineAlpha > 255)
					engineAlpha = 255;
			}
			else{
				engineAlpha -= engineAlphaDelta;
				if (engineAlpha < 0)
					engineAlpha = 0;
			}
			if(keyState[KeyEvent.VK_LEFT] == Game.KEY_HELD)
				theta -= rcs;
			if(keyState[KeyEvent.VK_RIGHT] == Game.KEY_HELD)
				theta += rcs;
		}
		else{
			//find and maneuver towards selected stellar object
			double trajectoryDeg = Math.toDegrees(trajectory);
			
			if(autoPilotMode == AIPhases.BRAKE && speed > 0){
				if(Util.angleDifference(theta - rcs, trajectoryDeg) < Util.angleDifference(theta + rcs, trajectoryDeg))
				    theta += rcs;
				else
					theta -= rcs;
				
				if(Util.angleDifference(theta, trajectoryDeg + 180) <= rcs){
					theta = Math.toDegrees(trajectory) + 180;
				}
				
				if(theta == Math.toDegrees(trajectory) + 180){
					if(speed > 0){
						speed -= warpAcc;
						xSpeed = Math.cos(trajectory) * speed;
						ySpeed = Math.sin(trajectory) * speed;
						
						if(speed <= 0){
							speed = 0;
							if((Util.pointDistance(x, y, targetX, targetY) > 100 && targetPlanet) || (!targetPlanet && (Main.getTargetSystem() == Main.getCurrentSystem() || Main.getTargetSystem() != null)))
								autoPilotMode = AIPhases.FIND_TARGET;
							else
								autoPilotMode = AIPhases.DONE;
						}
					}
				}
			}
			
			if(autoPilotMode == AIPhases.FIND_TARGET){
				if(Util.angleDifference(theta - rcs, targetDirection) < Util.angleDifference(theta + rcs, targetDirection))
				    theta -= rcs;
				else
					theta += rcs;
				
				if(Util.angleDifference(theta, targetDirection) <= rcs){
					theta = targetDirection;
					autoPilotMode = AIPhases.ACCELERATE;
				}
			}
			
			//accelerate towards selected stellar object
			if(autoPilotMode == AIPhases.ACCELERATE){
				trajectory = Math.atan2(ySpeed, xSpeed);
				StellarObject target = Main.getCurrentSystem().getSpobs().get(targetIndex);
				double dist = Util.pointDistance(x, y, target.getX(), target.getY());
				
				//if(targetPlanet && (dist - speed) < (Util.stoppingDistance(speed, warpAcc) + (speed * Math.floor(180 / rcs))))
				if(targetPlanet && Util.pointDistance(x, y, target.getX(), target.getY()) < (Main.SHORT_JUMP_ARRIVAL_DISTANCE + Util.stoppingDistance(speed, warpAcc) + (speed * Math.floor(180 / rcs))) * 1.1)
					autoPilotMode = AIPhases.BRAKE;
				else if(!targetPlanet && Main.getTargetSystem() == null)
					autoPilotMode = AIPhases.BRAKE;					
				
				if((speed < maxSpeed || Util.angleDifference(theta, Math.toDegrees(trajectory)) > 1.0)){
					if(speed <= maxSpeed){
						speed = Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2));
						xSpeed += Math.cos(Math.toRadians(theta)) * warpAcc;
						ySpeed += Math.sin(Math.toRadians(theta)) * warpAcc;
					}
					else{
						speed = maxSpeed;
						xSpeed = Math.cos(trajectory) * maxSpeed;
						ySpeed = Math.sin(trajectory) * maxSpeed;
					}
				}
				
				//"Warp Speed" hit when traveling to another system
				if(!targetPlanet && speed >= Main.WARP_SPEED && Main.getTargetSystem() != null){
					targetIndex = 0;
					Main.jump(Main.getTargetSystem());
					
					x = (long) (Math.cos(Math.PI + trajectory) * Main.ARRIVAL_DISTANCE);
					y = (long) (Math.sin(Math.PI + trajectory) * Main.ARRIVAL_DISTANCE);
				}
				
				//"Warp Speed" hit when traveling to another planet within the current system
				if(targetPlanet && speed >= Main.WARP_SPEED && Util.pointDistance(x, y, target.getX(), target.getY()) > (Main.SHORT_JUMP_ARRIVAL_DISTANCE + Util.stoppingDistance(speed, warpAcc) + (speed * Math.floor(180 / rcs))) * 1.1){
					//this is just to get the warp flash to appear
					Main.jump(Main.getCurrentSystem());
					
					long xStop = (long) (Math.cos(Math.PI + trajectory) * (Util.stoppingDistance(speed, warpAcc) + (speed * Math.floor(180 / rcs))));
					long yStop = (long) (Math.sin(Math.PI + trajectory) * (Util.stoppingDistance(speed, warpAcc) + (speed * Math.floor(180 / rcs))));
					
					x = (long) ((Math.cos(Math.PI + trajectory) * Main.SHORT_JUMP_ARRIVAL_DISTANCE) + target.getX()) + xStop;
					y = (long) ((Math.sin(Math.PI + trajectory) * Main.SHORT_JUMP_ARRIVAL_DISTANCE) + target.getY()) + yStop;
				}
			}
		}
		
		if(keyState[KeyEvent.VK_BACK_QUOTE] == Game.KEY_PRESSED)
			targetPlanet = !targetPlanet;
		
		if(keyState[KeyEvent.VK_TAB] == Game.KEY_PRESSED){
			if(speed != 0)
				autoPilotMode = AIPhases.BRAKE;
			else
				autoPilotMode = AIPhases.FIND_TARGET;
			targetIndex++;
			if(targetIndex >= Main.getCurrentSystem().getSpobs().size())
				targetIndex = 0;
		}
		
		if(keyState[KeyEvent.VK_SPACE] == Game.KEY_PRESSED){
			autoPilot = !autoPilot;
			if(autoPilot){
				if(speed != 0)
					autoPilotMode = AIPhases.BRAKE;
				else
					autoPilotMode = AIPhases.FIND_TARGET;
			}
		}
			
		x += xSpeed;
		y += ySpeed;
		 
		if(playerControlled){
			main.viewXView = (int)x - (Main.WIN_WIDTH / 2);
			main.viewYView = (int)y - (Main.WIN_HEIGHT / 2);
		}
	}
	
	public void draw(Main main){
		main.stroke(main.color(0, 0, 0), 0);
		main.pushMatrix();
		
		main.translate((float)(x - main.viewXView), (float)(y - main.viewYView));
		main.rotate((float) Math.toRadians(theta));
		
		main.fill(main.color(255, 255, 255));
		main.triangle(-20, 10, -20, -10, 20, 0);
		
		main.fill(main.color(255, 255, 0), engineAlpha);
		main.triangle(-20, 5, -20, -5, -28, 0);
		main.fill(main.color(255, 255, 255), 255);
		
		main.popMatrix();
		
		main.fill(main.color(255, 255, 255), 255);
		main.text("Speed: " + speed + "\nMax Speed: " + maxSpeed + "\nHeading: " + theta + "\nTrajectory: " + Math.toDegrees(trajectory) + "\nTrajectory Difference: " + Util.angleDifference(theta, Math.toDegrees(trajectory)) + "\nRetro Difference: " + Util.angleDifference(theta, Math.toDegrees(trajectory) + 180), 15, 15);
		
		if(targetPlanet)
			main.text("Target: " + targetName + " - " + Main.getCurrentSystem().getSpobs().get(targetIndex).getSubType() + "\nTargetDistance: " + Util.pointDistance(x, y, targetX, targetY) + "\nTarget Direction: " + targetDirection + "\nAutopilot Phase: " + autoPilotMode.toString(), 300, 15);	
		else{
			if(Main.getTargetSystem() != null && Main.getTargetSystem().isExplored())
				main.text("Target: " + targetName + " System\nTarget Direction: " + targetDirection + "\nAutopilot Phase: " + autoPilotMode.toString(), 300, 15);
			else
				main.text("Target: Unidentified System\nTarget Direction: " + targetDirection + "\nAutopilot Phase: " + autoPilotMode.toString(), 300, 15);				
		}
	}
	public double getTheta(){
		return theta;
	}
	
	public long getX(){
		return x;
	}
	
	public long getY(){
		return y;
	}
	
	public void setTargetIndex(int targetIndex){
		this.targetIndex = targetIndex;
	}
	
	public int getTargetIndex(){
		return targetIndex;
	}
}
