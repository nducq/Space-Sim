package starmap;

//import java.awt.Color;
import java.util.LinkedList;

import Terrestrial.bodies.PlanetSubType;
import Terrestrial.bodies.StarSubType;
import Terrestrial.bodies.StellarObject;
import Terrestrial.bodies.Type;
import main.Main;
import main.Util;
import starmap.StarSystem;
import starmap.patterns.Mesh;
import starmap.patterns.Pattern;
import starmap.patterns.Size;
import starmap.patterns.Tree;

public class StarMapGenerator {
	private static final double MAX_DISTANCE = 600;
	private static final double MINIMUM_ANGLE = 20;
	private static final double MIN_DISTANCE = 50;
	
	private static final int NUM_ROOTS = 30;
	
	public static final int CELL_WIDTH = 100;
	public static final int CELL_HEIGHT = 100;
	
	public static final int GRID_WIDTH = 200;
	public static final int GRID_HEIGHT = 200;
	
	public static final int[] SIZE_PROBABILITY = {5,10,45,30,10};
	
	private static boolean[][] mapGrid = new boolean[GRID_WIDTH][GRID_HEIGHT];
	
	public static void generateStarMap(StarSystem root){
		LinkedList<Pattern> patternList = new LinkedList<Pattern>();
		
		Pattern newTree = null;
		
		if(Main.rand.nextInt(100) > -1)
			newTree = new Tree(0, 0, root, starmap.patterns.Size.HUGE, new LinkedList<StarSystem>());
		else
			newTree = new Mesh(0, 0, root, starmap.patterns.Size.HUGE, new LinkedList<StarSystem>());			
		
		//newTree.translate(-1000, 0);
		newTree.addToGrid();

		patternList.add(newTree);
		
		//newTree.printCollisionMask();
		//newTree.printNeighborCollisionMask();
		
		for(int i = 0; i < Main.NUM_ROOTS; i++){
			Size newSize;
			int sizeIndex = Main.rand.nextInt(100);

			if(sizeIndex < SIZE_PROBABILITY[0])
				newSize = Size.HUGE;
			else if(sizeIndex < SIZE_PROBABILITY[0] + SIZE_PROBABILITY[1])
				newSize = Size.LARGE;
			else if(sizeIndex < SIZE_PROBABILITY[0] + SIZE_PROBABILITY[1] + SIZE_PROBABILITY[2])
				newSize = Size.MEDIUM;
			else if(sizeIndex < SIZE_PROBABILITY[0] + SIZE_PROBABILITY[1] + SIZE_PROBABILITY[2] + SIZE_PROBABILITY[3])
				newSize = Size.SMALL;
			else
				newSize = Size.TINY;
			
			StarSystem altRoot = new StarSystem(0, 0, "Root " + (i + 2), null);
			Main.getSystemList().add(altRoot);
			
			Pattern testTree = null;
			
			if(Main.rand.nextInt(100) > 25)
				testTree = new Tree(0, 0, altRoot, newSize, new LinkedList<StarSystem>());
			else
				testTree = new Mesh(0, 0, altRoot, newSize, new LinkedList<StarSystem>());
			
			int[] coords = findClosestMatch(testTree.getMapGrid());
			
			//System.out.println("Root " + (i + 2) + " Pattern X: " + coords[0] + "\nRoot " + (i + 2) + " Pattern Y: " + coords[1]);
			
			coords[0] = coords[0] + testTree.getOriginX() + 1;
			coords[1] = coords[1] + testTree.getOriginY() + 1;
			
			testTree.translate((double)((coords[0] - (GRID_WIDTH / 2)) * CELL_WIDTH), (double)((coords[1] - (GRID_HEIGHT / 2)) * CELL_HEIGHT));
			testTree.addToGrid();
			
			testTree.printCollisionMask();
			
			patternList.add(testTree);
		}
		
		/*for(StarSystem s: (new Tree(0, 0, root, starmap.patterns.Size.HUGE)).getSystems()){
			if(!Main.getSystemList().contains(s))
				Main.getSystemList().add(s);
		}*/
		
		LinkedList<StarSystem> cullList = new LinkedList<StarSystem>();
		for(StarSystem s: Main.getSystemList()){
			if(cullList.contains(s))
				continue;
			StarSystem closest = findClosestNode(s);
			if(closest == null)
				continue;
			if(Util.pointDistance(s.getX(), s.getY(), closest.getX(), closest.getY()) < MIN_DISTANCE){
				for(StarSystem l: (LinkedList<StarSystem>)closest.getLinks().clone()){
					s.addLink(l);
					s.setX((s.getX() + closest.getX()) / 2);
					s.setY((s.getY() + closest.getY()) / 2);
					//closest.removeLink(l);
				}
				cullList.add(closest);
			}
		}
		
		for(StarSystem s: cullList){
			s.removeAllLinks();
			Main.getSystemList().remove(s);
		}
		cullList.clear();
		
		for(Pattern p: patternList){
			for(StarSystem s: p.getOuterSystems()){
				//s.setColor(0, 0, 255);
				StarSystem potentialLink = findClosestOuterNode(p, patternList, s);
				if(!Main.sysLinkIntersect(s, potentialLink) && Util.pointDistance(s.getX(), s.getY(), potentialLink.getX(), potentialLink.getY()) <= MAX_DISTANCE){
					s.addLink(potentialLink);
					System.out.println("Creating outer edge");
				}
			}
		}

		for(StarSystem s: Main.getSystemList()){
			for(StarSystem l: s.getLinks()){
				if(Main.sysLinkIntersect(s, l) || !Main.getSystemList().contains(l))
					cullList.add(l);
			}
			for(StarSystem l: cullList)
				s.removeLink(l);
			cullList.clear();
		}
		
		for(StarSystem s: Main.getSystemList()){
			LinkedList<StarSystem> linksCopy = (LinkedList<StarSystem>) s.getLinks().clone();
			sortLinksByAngle(s, linksCopy);
			double angleOne = 0;
			double angleTwo = 0;
			for(int i = 0; i < linksCopy.size() - 1; i++){
				angleOne = Math.atan2(linksCopy.get(i).getY() - s.getY(), linksCopy.get(i).getX() - s.getX());
				angleTwo = Math.atan2(linksCopy.get(i + 1).getY() - s.getY(), linksCopy.get(i + 1).getX() - s.getX());
				
				if(Util.angleDifference(Math.toDegrees(angleOne), Math.toDegrees(angleTwo)) < MINIMUM_ANGLE)
					cullList.add(linksCopy.get(i + 1));
			}
			if(linksCopy.size() >= 2){
				angleOne = Math.atan2(linksCopy.get(0).getY() - s.getY(), linksCopy.get(0).getX() - s.getX());
				angleTwo = Math.atan2(linksCopy.get(linksCopy.size() - 1).getY() - s.getY(), linksCopy.get(linksCopy.size() - 1).getX() - s.getX());
				if(Util.angleDifference(Math.toDegrees(angleOne), Math.toDegrees(angleTwo)) < MINIMUM_ANGLE)
					s.removeLink(linksCopy.get(linksCopy.size() - 1));
			}
			for(StarSystem l: cullList){
				while(s.getLinks().contains(l))
					s.removeLink(l);
			}
			cullList.clear();
		}
		
		for(StarSystem s: Main.getSystemList()){
			if(s.getLinks().size() == 0)
				cullList.add(s);
		}
		
		for(StarSystem s: cullList)
			Main.getSystemList().remove(s);
		
		cullList.clear();
		
		for(StarSystem s: Main.getSystemList())
			s.setName(" ");
		
		root.setName("Sol");
		
		//Name the new systems
		int nameCount = 0;
		
		LinkedList<String> nameList = new LinkedList<String>();
		for(String name: Main.getSystemNameList().keySet())
			nameList.add(name);
		
		Util.shuffleList(nameList);
		
		for(String name: nameList){
			int dist = Main.getSystemNameList().get(name);
			LinkedList<StarSystem> systemList = Main.getSystemsInRange((int)(dist * 0.5), (int)(dist * 1.5), root);
			LinkedList<StarSystem> finalCandidates = new LinkedList<StarSystem>(); 
			//Util.shuffleList(systemList);
			for(StarSystem s: systemList){
				if(s.getName().equals(" "))
					finalCandidates.add(s);
			}
			if(finalCandidates.size() > 0){
				int nameIndex = Main.rand.nextInt(finalCandidates.size());
				finalCandidates.get(nameIndex).setName(name);
				finalCandidates.get(nameIndex).setColor(0xFF0000FF);
				nameCount++;
			}
		}
		
		//generate solar systems
		for(StarSystem s: Main.getSystemList()){
			if(s.getID() != 128){
				//s.setName(s.getID() + "");
				s.setSun(Main.generateSun(s, s.getName(), 0, null, null));
				s.assignPlanetsToStar(s.getSun());
				s.setAge(generateAge(s));
				
				int abundanceRoll = Main.rand.nextInt(20) + 1 + (int)s.getAge();
				
				if(abundanceRoll <= 9)
					s.setRelativeAbundance(2);
				else if(abundanceRoll >= 10 || abundanceRoll <= 12)
					s.setRelativeAbundance(1);
				else if(abundanceRoll >= 13 || abundanceRoll <= 18)
					s.setRelativeAbundance(0);
				else if(abundanceRoll >= 19 || abundanceRoll <= 21)
					s.setRelativeAbundance(-1);
				else if(abundanceRoll >= 22)
					s.setRelativeAbundance(-3);
					
				s.setRelativeAbundance(0);
			}
		}
		
		int[] habitableSystems = {0, 0, 0, 0, 0};
		
		for(StarSystem s: Main.getSystemList()){
			double lowestColonyCost = Double.MAX_VALUE;
			s.setColor(0xFFFFFFFF);
			
			for(StellarObject spob: s.getSpobs()){
				if(spob.getType() != Type.PLANET)
					continue;
				if(spob.getColonyCost() < lowestColonyCost || lowestColonyCost < 0){
					lowestColonyCost = spob.getColonyCost();
					if(lowestColonyCost < 0.50)
						System.out.println("Habitable planet found in the " + s.getName() + " system (CC: " + lowestColonyCost + ")");
				}
			}
			
			if(lowestColonyCost < 0.50){
				s.setColor(0xFF0000FF);
				habitableSystems[4]++;
			}
			else if(lowestColonyCost < 2.0){
				s.setColor(0xFF00FFFF);
				habitableSystems[3]++;
			}
			else if(lowestColonyCost < 4.0){
				s.setColor(0xFF00FF00);
				habitableSystems[2]++;
			}
			else if(lowestColonyCost < 8.0){
				s.setColor(0xFFFF0000);
				habitableSystems[1]++;
			}
			else
				habitableSystems[0]++;
		}
		
		System.out.println(nameCount + " stars named (" + nameList.size() + " possible names)");
		System.out.println(habitableSystems[0] + " systems completely uninhabitable");
		System.out.println(habitableSystems[1] + " systems barely inhabitable");
		System.out.println(habitableSystems[2] + " systems mostly inhabitable");
		System.out.println(habitableSystems[3] + " systems slightly inhabitable");
		System.out.println(habitableSystems[4] + " systems inhabitable");
		
		System.out.println("\nPlanet Distribution:");
		int[] planetTypes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		int planetTotal = 0;
		
		for(StarSystem s: Main.getSystemList()){
			for(StellarObject spob: s.getSpobs()){
				if(spob.getType() != Type.PLANET)
					continue;
				planetTypes[((Enum<PlanetSubType>) spob.getSubType()).ordinal()]++;
				planetTotal++;
			}
		}
		
		System.out.println(((double)planetTypes[0] / (double)planetTotal * 100) + "% Terrestrial");
		System.out.println(((double)planetTypes[1] / (double)planetTotal * 100) + "% Dwarf");
		System.out.println(((double)planetTypes[2] / (double)planetTotal * 100) + "% Super Terrestrial");
		System.out.println(((double)planetTypes[3] / (double)planetTotal * 100) + "% Jovian");
		System.out.println(((double)planetTypes[4] / (double)planetTotal * 100) + "% Super Jovian");
		System.out.println(((double)planetTypes[5] / (double)planetTotal * 100) + "% Brown Dwarf");
	}
	
	private static StarSystem findClosestNode(StarSystem src){
		StarSystem closest = null;
		for(StarSystem s: Main.getSystemList()){
			if(s == src)
				continue;
			if(closest == null || Util.pointDistance(s.getX(), s.getY(), src.getX(), src.getY()) < Util.pointDistance(closest.getX(), closest.getY(), src.getX(), src.getY()))
				closest = s;
		}
		return closest;
	}
	
	private static StarSystem findClosestOuterNode(Pattern srcPattern, LinkedList<Pattern> patternList, StarSystem src){
		StarSystem closestNode = null;
		for(Pattern trg: patternList){
			if(trg == srcPattern)
				continue;
			for(StarSystem trgSys: trg.getSystems()){
				if(trgSys == src || src.getLinks().contains(trgSys))
					continue;
				if(closestNode == null || Util.pointDistance(src.getX(), src.getY(), trgSys.getX(), trgSys.getY()) < Util.pointDistance(src.getX(), src.getY(), closestNode.getX(), closestNode.getY()))
					closestNode = trgSys;
			}
		}
		
		System.out.println("Closest Node to " + src.getName() + " is " + closestNode.getName());
		
		return closestNode;
	}
	
	public static void setGrid(int x, int y){
		if(x >= 0 && y >= 0 && x < mapGrid.length && y < mapGrid[0].length)
			mapGrid[x][y] = true;
	}
	
	public static boolean getGrid(int x, int y){
		return mapGrid[x][y];
	}
	
	public static int[] findClosestMatch(boolean[][] newPattern){
		int coords[] = new int[]{0, 0};
		int matchCount = 0;
		
		for(int i = 0; i < (mapGrid.length); i++){
			for(int j = 0; j < (mapGrid[0].length); j++){
				if(!patternCollision(newPattern, i, j)){
					int tempMatchCount = numAdjacent(newPattern, i, j);
					if(tempMatchCount > matchCount){
						coords[0] = i;
						coords[1] = j;
						matchCount = tempMatchCount;
					}
				}
			}
		}
		
		return coords;
	}
	
	public static boolean patternCollision(boolean[][] newPattern, int x, int y){
		for(int i = 0; i < newPattern.length; i++){
			for(int j = 0; j < newPattern[0].length; j++){
				if ((i + x < GRID_WIDTH) && (j + y < GRID_HEIGHT) && newPattern[i][j] && mapGrid[i + x][j + y])
					return true;
			}
		}
		return false;
	}
	
	public static int numAdjacent(boolean[][] newPattern, int x, int y){
		int numAdjacent = 0;
		boolean[][] matchPattern = new boolean[newPattern.length + 2][newPattern[0].length + 2];
		
		for(int i = 1; i < newPattern.length + 1; i++){
			for(int j = 1; j < newPattern[0].length + 1; j++){
				if(newPattern[i - 1][j - 1]){
					matchPattern[i - 1][j - 1] = true;
					matchPattern[i][j - 1] = true;
					matchPattern[i + 1][j - 1] = true;
					matchPattern[i - 1][j] = true;
					matchPattern[i + 1][j] = true;
					matchPattern[i - 1][j + 1] = true;
					matchPattern[i][j + 1] = true;
					matchPattern[i + 1][j + 1] = true;
				}
			}
		}

		for(int i = 0; i < newPattern.length; i++){
			for(int j = 0; j < newPattern[0].length; j++){
				if(newPattern[i][j])
					matchPattern[i + 1][j + 1] = false;
			}
		}

		for(int i = -1; i < matchPattern.length - 1; i++){
			for(int j = -1; j < matchPattern[0].length - 1; j++){
				if ((i + x) < mapGrid.length && 
					(j + y) < mapGrid[0].length &&
					(i + x - 1) >= 0 && 
					(j + y - 1) >= 0 &&
					matchPattern[i + 1][j + 1] &&
					mapGrid[i + x][j + y])
					numAdjacent++;
			}
		}
		
		return numAdjacent;
	}
	
	public static void sortLinksByAngle(StarSystem src, LinkedList<StarSystem> links){
		for(int i = 0; i < links.size(); i++){
			for(int j = 0; j < links.size() - 1; j++){
				if(Math.atan2(links.get(j).getY() - src.getY(), links.get(j).getX() - src.getX()) > Math.atan2(links.get(j + 1).getY() - src.getY(), links.get(j + 1).getX() - src.getX())){
					StarSystem tempNode = links.get(j);					
					links.set(j, links.get(j + 1));
					links.set(j + 1, tempNode);
				}
			}
		}
	}
	
	private static double generateAge(StarSystem s){
		StellarObject star = s.getStars().get(0);
		double age = 0;
		
		if(star.getSubType() == StarSubType.BLUE || star.getSubType() == StarSubType.BLUE_SUB_GIANT || star.getSubType() == StarSubType.BLUE_GIANT){
			double maxAge = 10 * star.getMass() / star.getLuminosity();
			age = maxAge * (Main.rand.nextInt(10) + 1) / 10;
		}
		if(star.getSubType() == StarSubType.BLUE_WHITE || star.getSubType() == StarSubType.BLUE_WHITE_SUB_GIANT || star.getSubType() == StarSubType.BLUE_WHITE_GIANT){
			switch(Main.rand.nextInt(10)){
				case 0:
				case 1:
					age = 0.1;
					star.setLuminosity(star.getLuminosity() * 0.8);
					break;
				case 2:
				case 3:
					age = 0.2;
					star.setLuminosity(star.getLuminosity() * 0.9);
					break;
				case 4:
				case 5:
					age = 0.3;
					break;
				case 6:
				case 7:
					age = 0.4;
					star.setLuminosity(star.getLuminosity() * 1.1);
					break;
				case 8:
				case 9:
					age = 0.5;
					star.setLuminosity(star.getLuminosity() * 1.2);
					break;
			}
			if(star.getSpectralClass() >= 5)
				age *= 2 + Main.rand.nextInt(1);
		}
		else if(star.getSubType() == StarSubType.WHITE || star.getSubType() == StarSubType.WHITE_SUB_GIANT || star.getSubType() == StarSubType.WHITE_GIANT){
			int ageRoll = Main.rand.nextInt(10);
			switch(ageRoll){
				case 0:
					age = 0.3;
					star.setLuminosity(star.getLuminosity() * 0.6);
					break;
				case 1:
					age = 0.6;
					star.setLuminosity(star.getLuminosity() * 0.7);
					break;
				case 2:
					age = 1.0;
					star.setLuminosity(star.getLuminosity() * 0.8);
					break;
				case 3:
					age = 1.3;
					star.setLuminosity(star.getLuminosity() * 0.9);
					break;
				case 4:
					age = 1.6;
					break;
				case 5:
					age = 2.0;
					star.setLuminosity(star.getLuminosity() * 1.1);
					break;
				case 6:
					age = 2.3;
					star.setLuminosity(star.getLuminosity() * 1.2);
					break;
				case 7:
					age = 2.6;
					star.setLuminosity(star.getLuminosity() * 1.3);
					break;
				case 8:
					age = 2.9;
					star.setLuminosity(star.getLuminosity() * 1.4);
					break;
				case 9:
					age = 3.2;
					star.setLuminosity(star.getLuminosity() * 1.5);
					break;
			}
			if(star.getSpectralClass() >= 5)
				age = ((double) ageRoll + 1) / 2;
		}
		else if(star.getSubType() == StarSubType.YELLOW || star.getSubType() == StarSubType.YELLOW_SUB_GIANT || star.getSubType() == StarSubType.YELLOW_GIANT){
			age = Main.rand.nextInt(10) + 1;
			star.setLuminosity(star.getLuminosity() * ((1.0 - ((double) age - 5)) / 10.0));
		}
		else if(star.getSubType() == StarSubType.ORANGE || star.getSubType() == StarSubType.ORANGE_SUB_GIANT || star.getSubType() == StarSubType.ORANGE_GIANT)
			age = Main.rand.nextInt(10) + 1;
		else if(star.getSubType() == StarSubType.RED || star.getSubType() == StarSubType.RED_GIANT || star.getSubType() == StarSubType.BROWN_DWARF || star.getSubType() == StarSubType.WHITE_DWARF)
			age = Main.rand.nextInt(10) + 1;
		return age;
	}
}
