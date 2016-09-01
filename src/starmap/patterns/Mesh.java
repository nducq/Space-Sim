package starmap.patterns;

import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.Stack;

import com.jogamp.opengl.math.Matrix4;

import main.CircumCircle;
import main.Main;
import main.Util;
import processing.core.PApplet;
import starmap.StarSystem;

public class Mesh extends Pattern{

	private final double MINIMUM_ANGLE = -10;
	private LinkedList<StarSystem[]> sysListDC = new LinkedList<StarSystem[]>(); 
	
	public Mesh(double x, double y, StarSystem root, Size size, LinkedList<StarSystem> systems) {
		super(x, y, root, size, systems);
	}

	@Override
	public void generateHuge() {
		int numLinks = rand.nextInt(30) + 20;
		double dist = 1000;
		
		generate(numLinks, dist);
	}

	@Override
	public void generateLarge() {
		int numLinks = rand.nextInt(20) + 15;
		double dist = 700;
		
		generate(numLinks, dist);
	}

	@Override
	public void generateMedium() {
		int numLinks = rand.nextInt(10) + 10;
		double dist = 500;
	
		generate(numLinks, dist);
	}

	@Override
	public void generateSmall() {
		int numLinks = rand.nextInt(8) + 7;
		double dist = 300;
		
		generate(numLinks, dist);
	}

	@Override
	public void generateTiny() {
		int numLinks = rand.nextInt(4) + 3;
		double dist = 200;
		
		generate(numLinks, dist);
	}
	
	private void generate(int numStars, double dist){
		sysListDC = new LinkedList<StarSystem[]>(); 
		
		for(int i = 0; i < numStars - numRootLinks; i++){
			double x = rand.nextDouble() * dist + root.getX() - (dist / 2);
			double y = rand.nextDouble() * (dist - Math.abs(x)) + root.getY() - ((dist - Math.abs(x)) / 2);
			StarSystem newSys = new StarSystem(x, y, "Tree - Tiny", null);
			
			systems.add(newSys);
				//outerSystems.add(newSys);
		}
		
		triangulateSystems(systems);
		//System.out.println("Set initial links");
		while(sysListDC.size() > 1){
			LinkedList<StarSystem[]> replacementList = new LinkedList<StarSystem[]>();
			for(int i = 0; i < Math.floor(sysListDC.size() / 2); i++){
				StarSystem[] newPair = bridgeLinks(sysListDC.get(i * 2), sysListDC.get((i * 2) + 1));
				if(i == Math.floor(sysListDC.size() / 2) - 1 && sysListDC.size() % 2 == 1){
					newPair = bridgeLinks(newPair, sysListDC.get(sysListDC.size() - 1));
					//for(StarSystem s: sysListDC.get(sysListDC.size() - 1))
					//	s.setName(s.getName() + "(Dud)");
				}
				
				replacementList.add(newPair);
			}
			sysListDC = replacementList;
			//System.out.println("Bridges Linked, " + sysListDC.size() + " chunk(s) remaining");
		}
		clean();
	}

	private void clean(){
		LinkedList<StarSystem> cullList = new LinkedList<StarSystem>();
		
		for(StarSystem s: systems){
			if(s.getLinks().size() < 2){
				cullList.add(s);
				System.out.println("Removing " + s.getName() + " with " + s.getLinks().size() + " - " + s.countReferences(systems) + " links.");
			}
		}
		
		/*for(StarSystem s: cullList){
			s.removeAllLinks();
			systems.remove(s);
		}*/
		
		for(StarSystem s: systems)
			Main.getSystemList().add(s);
	}
	
	private void triangulateSystems(LinkedList<StarSystem> systemList){
		StarSystem[] systemListArray = new StarSystem[systemList.size()];
		for(int i = 0; i < systemList.size(); i++)
			systemListArray[i] = systemList.get(i);
		sortSystemsByX(systemListArray);
		for(int i = 0; i < systemListArray.length; i++)
			systemListArray[i].setName("" + i);
		triangulateHull(systemListArray);
	}
	
	private StarSystem[] triangulateHull(StarSystem[] systemList){
		//System.out.println("recursive loop");
		if(systemList.length > 3){
			StarSystem[] arrOne = new StarSystem[systemList.length / 2];
			StarSystem[] arrTwo = new StarSystem[systemList.length - arrOne.length];
			for(int i = 0; i < arrOne.length; i++)
				arrOne[i] = systemList[i];
			for(int i = 0; i < arrTwo.length; i++)
				arrTwo[i] = systemList[i + arrOne.length];
			//bridgeLinks(arrOne, arrTwo);
			triangulateHull(arrOne);
			triangulateHull(arrTwo);
		}
		else if(systemList.length == 3){
			//System.out.println(" * Triangulating " + systemList[0].getName() + ", " + systemList[1].getName() + ", and " + systemList[2].getName());
			systemList[0].addLink(systemList[1]);
			systemList[1].addLink(systemList[2]);
			systemList[2].addLink(systemList[0]);
			
			sortSystemsByY(systemList);
			
			outerSystems.add(systemList[0]);
			outerSystems.add(systemList[2]);
			
			//System.out.println("Mesh: " + outerSystems.size());
			
			sysListDC.add(systemList);
		}
		else{
			//System.out.println(" * Linking " + systemList[0].getName() + " and " + systemList[1].getName());
			systemList[0].addLink(systemList[1]);
			
			outerSystems.add(systemList[0]);
			outerSystems.add(systemList[1]);
			
			//System.out.println("Mesh: " + outerSystems.size());
			
			sysListDC.add(systemList);		
		}
		return systemList;
	}
	
	private StarSystem[] bridgeLinks(StarSystem[] arrOne, StarSystem[] arrTwo){
		//System.out.println("bridging nodes " + arrOne[0].getName() + " and " + arrTwo[0].getName());
		
		StarSystem[] baseEdge = getLowestEdge(arrOne, arrTwo);		
		
		StarSystem baseLinkLL = baseEdge[0];
		StarSystem baseLinkRR = baseEdge[1];
		
		StarSystem[] ret = new StarSystem[arrOne.length + arrTwo.length];
		
		if(baseLinkLL == null || baseLinkRR == null){
			for(int i = 0; i < arrOne.length; i++)
				ret[i] = arrOne[i];
			for(int i = 0; i < arrTwo.length; i++)
				ret[i + arrOne.length] = arrTwo[i];
			
			return ret;
		}
		
		StarSystem finalRRCandidate = null;
		StarSystem finalLLCandidate = null;
		
		LinkedList<StarSystem> potentialRRCandidates = (LinkedList<StarSystem>) baseLinkRR.getLinks().clone();
		LinkedList<StarSystem> potentialLLCandidates = (LinkedList<StarSystem>) baseLinkLL.getLinks().clone();

		//sortPotentialCandidates(baseLinkLL, baseLinkRR, potentialRRCandidates, false);
		//sortPotentialCandidates(baseLinkLL, baseLinkRR, potentialLLCandidates, true);
		
		//baseLinkRR.addLink(baseLinkLL);
		
		//int counterRR = 0;
		//int counterLL = 0;
		/*process right edges*/
		while(potentialRRCandidates.size() > 0 && potentialLLCandidates.size() > 0 || true){
			//System.out.println(" * Working from base link " + baseLinkLL.getName() + " - " + baseLinkRR.getName());
			
			potentialRRCandidates = (LinkedList<StarSystem>) baseLinkRR.getLinks().clone();
			potentialLLCandidates = (LinkedList<StarSystem>) baseLinkLL.getLinks().clone();

			//System.out.println("==SORTING " + baseLinkRR.getName() + " CANDIDATES==");
			sortPotentialCandidates(baseLinkLL, baseLinkRR, potentialRRCandidates, true);
			//System.out.println("==SORTING " + baseLinkLL.getName() + " CANDIDATES==");
			sortPotentialCandidates(baseLinkLL, baseLinkRR, potentialLLCandidates, false);
			
			potentialLLCandidates.remove(baseLinkRR);
			potentialRRCandidates.remove(baseLinkLL);
			
			baseLinkLL.addLink(baseLinkRR);
			/*
			System.out.print("\tPotential RR Candidates: ");
			for(StarSystem s: potentialRRCandidates)
				System.out.print(s.getName() + " ");
			System.out.print("\n");

			System.out.print("\tPotential LL Candidates: ");
			for(StarSystem s: potentialLLCandidates)
				System.out.print(s.getName() + " ");
			System.out.print("\n");
			*/
			finalRRCandidate = null;
			finalLLCandidate = null;
			
			//potentialRRCandidates.remove(baseLinkRR);
			//potentialLLCandidates.remove(baseLinkLL);
			
			for(int i = 0; i < potentialRRCandidates.size(); i++){
				if(firstCriteriaMet(baseLinkRR, baseLinkLL, potentialRRCandidates.get(i), false)){
					if(i == potentialRRCandidates.size() - 1 || potentialRRCandidates.size() == 1){
						finalRRCandidate = potentialRRCandidates.get(i);
						//System.out.println("\t" + finalRRCandidate.getName() + " meets both criteria by default");
						break;
					}
					if (secondCriteriaMet(baseLinkRR, baseLinkLL, potentialRRCandidates.get(i), potentialRRCandidates.get(i + 1), false)){
						finalRRCandidate = potentialRRCandidates.get(i);
						//System.out.println("\t" + finalRRCandidate.getName() + " meets both criteria");
						break;
					}
					else{
						potentialRRCandidates.get(i).removeLink(baseLinkLL);
						potentialRRCandidates.get(i).removeLink(baseLinkRR);
						//System.out.println("removing link between " + baseLinkRR.getName() + " and " + potentialRRCandidates.get(i).getName() + " on the right");
					}
				}
				else
					break;
			}
			
			/*process left edges*/
			for(int i = 0; i < potentialLLCandidates.size(); i++){
				if(firstCriteriaMet(baseLinkRR, baseLinkLL, potentialLLCandidates.get(i), true)){
					if(i == potentialLLCandidates.size() - 1 || potentialLLCandidates.size() == 1){
						finalLLCandidate = potentialLLCandidates.get(i);
						//System.out.println("\t" + finalLLCandidate.getName() + " meets both criteria by default");
						break;
					}
					if (secondCriteriaMet(baseLinkRR, baseLinkLL, potentialLLCandidates.get(i), potentialLLCandidates.get(i + 1), true)){
						finalLLCandidate = potentialLLCandidates.get(i);
						//System.out.println("\t" + finalLLCandidate.getName() + " meets both criteria");
						break;
					}
					else{
						potentialLLCandidates.get(i).removeLink(baseLinkRR);
						potentialLLCandidates.get(i).removeLink(baseLinkLL);
						//System.out.println("removing link between " + baseLinkLL.getName() + " and " + potentialLLCandidates.get(i).getName() + " on the left");
					}
				}
				else
					break;
			}		
			
			if(finalRRCandidate != null){
				potentialRRCandidates.remove(finalRRCandidate);
				//System.out.println("Final right candidate: " + finalRRCandidate.getName());
			}
			//else
			//	System.out.println("Final right candidate: NULL");
			if(finalLLCandidate != null){
				potentialLLCandidates.remove(finalLLCandidate);
				//System.out.println("Final left candidate: " + finalLLCandidate.getName());
			}
			//else
			//	System.out.println("Final left candidate: NULL");
			
			if (finalRRCandidate == null && finalLLCandidate == null){
				break;
			}
			else if (finalRRCandidate != null && finalLLCandidate != null){
				if(withinCircumCircle(new StarSystem[]{baseLinkRR, baseLinkLL, finalRRCandidate}, finalLLCandidate)){
					baseLinkRR.addLink(finalLLCandidate);
					baseLinkLL = finalLLCandidate;
				}
				else{
					baseLinkLL.addLink(finalRRCandidate);
					baseLinkRR = finalRRCandidate;
				}
			}
			else if(finalRRCandidate != null){
				baseLinkLL.addLink(finalRRCandidate);
				baseLinkRR = finalRRCandidate;
			}
			else if(finalLLCandidate != null){
				baseLinkRR.addLink(finalLLCandidate);
				baseLinkLL = finalLLCandidate;
			}
		}
		for(int i = 0; i < arrOne.length; i++)
			ret[i] = arrOne[i];
		for(int i = 0; i < arrTwo.length; i++)
			ret[i + arrOne.length] = arrTwo[i];
		
		return ret;
	}	
	
	private boolean withinCircumCircle(StarSystem[] triangle, StarSystem point){
		double Ax = triangle[0].getX();
		double Ay = triangle[0].getY();
		double Bx = triangle[1].getX();
		double By = triangle[1].getY();
		double Cx = triangle[2].getX();
		double Cy = triangle[2].getY();
		
		double xMat[][] = new double[3][3];
		double yMat[][] = new double[3][3];
		double aMat[][] = new double[3][3];
		
		xMat[0][0] = (Ax * Ax) + (Ay * Ay);
		xMat[1][0] = Ay;
		xMat[2][0] = 1;
		xMat[0][1] = (Bx * Bx) + (By * By);
		xMat[1][1] = By;
		xMat[2][1] = 1;
		xMat[0][2] = (Cx * Cx) + (Cy * Cy);
		xMat[1][2] = Cy;
		xMat[2][2] = 1;
		
		yMat[0][0] = (Ax * Ax) + (Ay * Ay);
		yMat[1][0] = Ax;
		yMat[2][0] = 1;
		yMat[0][1] = (Bx * Bx) + (By * By);
		yMat[1][1] = Bx;
		yMat[2][1] = 1;
		yMat[0][2] = (Cx * Cx) + (Cy * Cy);
		yMat[1][2] = Cx;
		yMat[2][2] = 1;
	
		aMat[0][0] = Ax;
		aMat[1][0] = Ay;
		aMat[2][0] = 1;
		aMat[0][1] = Bx;
		aMat[1][1] = By;
		aMat[2][1] = 1;
		aMat[0][2] = Cx;
		aMat[1][2] = Cy;
		aMat[2][2] = 1;
		
		double a = computeDeterminant(aMat);
		double circumX = -(-computeDeterminant(xMat) / (2 * a));
		double circumY = -(computeDeterminant(yMat) / (2 * a));
		double rad = Util.pointDistance(circumX, circumY, triangle[0].getX(), triangle[0].getY());
		
		return (rad >= Util.pointDistance(circumX, circumY, point.getX(), point.getY()));
	}
	
	private boolean firstCriteriaMet(StarSystem basePointRR, StarSystem basePointLL, StarSystem potentialCandidate, boolean clockwise){
		double baseTheta = 0;
		if(!clockwise){
			baseTheta = Math.toDegrees(Math.atan2(basePointRR.getY() - basePointLL.getY(), basePointRR.getX() - basePointLL.getX()));
			while(baseTheta < 0)
				baseTheta += 360;
			baseTheta = 360 - baseTheta;
			//System.out.println(" * Base Theta (Counter Clockwise): " + baseTheta + " degrees from " + baseLL.getName() + " to " + baseRR.getName());
			baseTheta = - (360 - baseTheta);
		}
		else{
			baseTheta = Math.toDegrees(Math.atan2(basePointLL.getY() - basePointRR.getY(), basePointLL.getX() - basePointRR.getX()));
			while(baseTheta < 0)
				baseTheta += 360;
			//System.out.println(" * Base Theta (Clockwise): " + baseTheta + " degrees from " + baseRR.getName() + " to " + baseLL.getName());
			baseTheta = - (360 - baseTheta);
		}
		
		//LinkedList<StarSystem> cullList = new LinkedList<StarSystem>();
		
		double theta = 0;
		if(clockwise){				
			theta = Math.toDegrees(Math.atan2(potentialCandidate.getY() - basePointRR.getY(), potentialCandidate.getX() - basePointRR.getX()));
			while(theta < 0)
				theta += 360;
		}
		else{
			theta = Math.toDegrees(Math.atan2(potentialCandidate.getY() - basePointLL.getY(), potentialCandidate.getX() - basePointLL.getX()));
			while(theta < 0)
				theta += 360;
			theta = 360 - theta;
		}
		
		if(((theta - baseTheta) % 360) >= 180)
			return false;
		
		//if((basePointRR.getX() - basePointLL.getX()) * (potentialCandidate.getY() - basePointLL.getY()) - (basePointRR.getY() - basePointLL.getY()) * (potentialCandidate.getX() - basePointLL.getX()) < 0){
		//	return true;
		//}
		//System.out.println(" + " + potentialCandidate.getName() + " failed to meet first criteria");
		return true;
	}
	
	private boolean secondCriteriaMet(StarSystem basePointRR, StarSystem basePointLL, StarSystem potentialCandidate, StarSystem nextPC, boolean clockwise){
		double angleOne = 0.0;
		double angleTwo = 0.0;
		
		boolean circumCircle = withinCircumCircle(new StarSystem[]{basePointRR, basePointLL, potentialCandidate}, nextPC);
		boolean noCollision = false;
		boolean angleDifference = false;
		
		if(clockwise){
			angleOne = Math.toDegrees(Math.atan2(basePointLL.getY() - basePointRR.getY(), basePointLL.getX() - basePointRR.getX()));
			angleTwo = Math.toDegrees(Math.atan2(basePointLL.getY() - potentialCandidate.getY(), basePointLL.getX() - potentialCandidate.getX()));
			noCollision = !Main.sysLinkIntersect(potentialCandidate, basePointLL, this.systems);
		}
		else{
			angleOne = Math.toDegrees(Math.atan2(basePointRR.getY() - basePointLL.getY(), basePointRR.getX() - basePointLL.getX()));
			angleTwo = Math.toDegrees(Math.atan2(basePointRR.getY() - potentialCandidate.getY(), basePointRR.getX() - potentialCandidate.getX()));
			noCollision = !Main.sysLinkIntersect(potentialCandidate, basePointRR, this.systems);
		}
		
		angleDifference = Util.angleDifference(angleOne, angleTwo) >= MINIMUM_ANGLE;
		return (!circumCircle && angleDifference && noCollision);
	}
		
	private void sortSystemsByX(StarSystem[] systemList){
		//LinkedList<StarSystem> localCopy = (LinkedList<StarSystem>)systemList.clone();
		for(int i = 0; i < systemList.length; i++){
			for(int j = 0; j < systemList.length - 1; j++){
				if(systemList[j].getX() > systemList[j + 1].getX()){
					StarSystem temp = systemList[j];
					systemList[j] = systemList[j + 1];
					systemList[j + 1] = temp;
				}
				else if((systemList[j].getX() == systemList[j + 1].getX()) && (systemList[j].getY() > systemList[j + 1].getY())){
					StarSystem temp = systemList[j];
					systemList[j] = systemList[j + 1];
					systemList[j + 1] = temp;
				}
			}
		}
	}

	private void sortSystemsByY(StarSystem[] systemList){
		//LinkedList<StarSystem> localCopy = (LinkedList<StarSystem>)systemList.clone();
		for(int i = 0; i < systemList.length; i++){
			for(int j = 0; j < systemList.length - 1; j++){
				if(systemList[j].getY() < systemList[j + 1].getY()){
					StarSystem temp = systemList[j];
					systemList[j] = systemList[j + 1];
					systemList[j + 1] = temp;
				}
			}
		}
	}
	
	private void sortPotentialCandidates(StarSystem baseLL, StarSystem baseRR, LinkedList<StarSystem> systemList, boolean sortCW){
		LinkedList<Double> angleList = new LinkedList<Double>();
		
		double baseTheta = 0;
		if(!sortCW){
			baseTheta = Math.toDegrees(Math.atan2(baseRR.getY() - baseLL.getY(), baseRR.getX() - baseLL.getX()));
			while(baseTheta < 0)
				baseTheta += 360;
			baseTheta = 360 - baseTheta;
			//System.out.println(" * Base Theta (Counter Clockwise): " + baseTheta + " degrees from " + baseLL.getName() + " to " + baseRR.getName());
			baseTheta = - (360 - baseTheta);
		}
		else{
			baseTheta = Math.toDegrees(Math.atan2(baseLL.getY() - baseRR.getY(), baseLL.getX() - baseRR.getX()));
			while(baseTheta < 0)
				baseTheta += 360;
			//System.out.println(" * Base Theta (Clockwise): " + baseTheta + " degrees from " + baseRR.getName() + " to " + baseLL.getName());
			baseTheta = - (360 - baseTheta);
		}
		
		//LinkedList<StarSystem> cullList = new LinkedList<StarSystem>();
		
		for(StarSystem s: systemList){
			double theta = 0;
			if(sortCW){				
				theta = Math.toDegrees(Math.atan2(s.getY() - baseRR.getY(), s.getX() - baseRR.getX()));
				while(theta < 0)
					theta += 360;
				//System.out.println("Clockwise: " + theta + " (" + ((theta - baseTheta) % 360) + ") degrees from " + baseRR.getName() + " to " + s.getName());
				angleList.add(((theta - baseTheta) % 360));
			}
			else{
				theta = Math.toDegrees(Math.atan2(s.getY() - baseLL.getY(), s.getX() - baseLL.getX()));
				while(theta < 0)
					theta += 360;
				theta = 360 - theta;
				//System.out.println("Counter Clockwise: " + theta + " (" + ((theta - baseTheta) % 360) + ") degrees from " + baseLL.getName() + " to " + s.getName());
				angleList.add(((theta - baseTheta) % 360));
			}
		}
			
		for(int i = 0; i < angleList.size(); i++){
			for(int j = 0; j < angleList.size() - 1; j++){
				if(angleList.get(j) > angleList.get(j + 1)){
					double tempAngle = angleList.get(j);
					StarSystem tempNode = systemList.get(j);
					
					angleList.set(j, angleList.get(j + 1));
					systemList.set(j, systemList.get(j + 1));
					
					angleList.set(j + 1, tempAngle);
					systemList.set(j + 1, tempNode);
				}
			}
		}
	}
	
	public StarSystem[] getLowestEdge(StarSystem[] leftSystemList, StarSystem[] rightSystemList){
		StarSystem[] lowest = new StarSystem[2];
		
		sortSystemsByY(leftSystemList);
		sortSystemsByY(rightSystemList);
		
		StarSystem baseLL = leftSystemList[0];
		StarSystem baseRR = rightSystemList[0];
				
		int leftIndex = 0;
		int rightIndex = 0;
		
		lowest[0] = null;
		lowest[1] = null;
		
		for(int i = 0; i < Math.max(leftSystemList.length, rightSystemList.length); i++){
			if(Main.sysLinkIntersect(baseLL, baseRR, leftSystemList))
				leftIndex++;
			if(Main.sysLinkIntersect(baseLL, baseRR, rightSystemList))
				rightIndex++;
			if(leftIndex < leftSystemList.length && rightIndex < leftSystemList.length){
				baseLL = leftSystemList[leftIndex];
				baseRR = rightSystemList[rightIndex];
			}
			else
				return lowest;
		}
		
		lowest[0] = baseLL;
		lowest[1] = baseRR;
		
		//System.out.println("Lowest edge: " + baseLL.getName() + " - " + baseRR.getName());
		
		return lowest;
	}
	
	private double computeDeterminant(double[][] matrix){
		double determinant = 0;
		
		determinant = (matrix[0][0] * matrix[1][1] * matrix[2][2]) + (matrix[1][0] * matrix[2][1] * matrix[0][2]) + (matrix[2][0] * matrix[0][1] * matrix[1][2]);
		determinant = determinant - (matrix[2][0] * matrix[1][1] * matrix[0][2]) - (matrix[1][0] * matrix[0][1] * matrix[2][2]) - (matrix[0][0] * matrix[2][1] * matrix[1][2]);
		
		return determinant;
	}
}
