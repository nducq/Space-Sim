package starmap.patterns;

import java.util.LinkedList;
import java.util.Random;

import main.Main;
import main.Util;
import starmap.StarMapGenerator;
import starmap.StarSystem;

public class Tree extends Pattern{
	
	public Tree(double x, double y, StarSystem root, Size size, LinkedList<StarSystem> systems){
		super(x, y, root, size, systems);
	}
	
	public void generateHuge() {
		int numLinks = rand.nextInt(4) + 7;
		double avgTheta = (2 * Math.PI) / (double)(numLinks);
		double angularJitter = 0.25;
		double distJitter = 0.5;
		//double dist = 350;

		int[] sizeDistances = new int[]{450, 350, 200, 75};
		int[] sizeProbabilities = new int[]{35, 25, 15, 25};
		
		numRootLinks = 0;
		zeroTheta = rand.nextDouble() * Math.PI * 2;
		
		LinkedList<StarSystem> newLinks = new LinkedList<StarSystem>();
		
		for(int i = 0; i < numLinks; i++){
			int sizeIndex = chooseSizeIndex(sizeProbabilities);
			double dist = sizeDistances[sizeIndex];
			
			double theta = (avgTheta * (i + 1));
			
			theta = randomHelper(theta * (1.0 - angularJitter), theta * (1.0 + angularJitter));
			
			System.out.println("Generating node " + (i + 1) + " of " + (numLinks) + " at angle: " + (Math.toDegrees(theta) + Math.toDegrees(zeroTheta)));
			double x = root.getX() + (Math.cos(theta + zeroTheta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			double y = root.getY() + (Math.sin(theta + zeroTheta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			StarSystem newSys = new StarSystem(x, y, "Tree - Huge", null);

			if(!Main.sysLinkIntersect(root, newSys, removeDupes(systems))){
				newSys.addLink(root);
				Main.getSystemList().add(newSys);
				systems.add(newSys);
				
				Tree newTree = null;
				if(sizeIndex == 0)
					newTree = new Tree(0, 0, newSys, Size.LARGE, systems);
				if(sizeIndex == 1)
					newTree = new Tree(0, 0, newSys, Size.MEDIUM, systems);
				if(sizeIndex == 2)
					newTree = new Tree(0, 0, newSys, Size.SMALL, systems);
				if(sizeIndex == 3)
					newTree = new Tree(0, 0, newSys, Size.TINY, systems);
			}
		}
		for(StarSystem s: systems){
			if(s.getLinks().size() <= 1)
				outerSystems.add(s);
		}
	}

	public void generateLarge() {
		int numLinks = rand.nextInt(4) + 3;
		double avgTheta = (2 * Math.PI) / (double)(numLinks + numRootLinks);
		double angularJitter = 0.35;
		double distJitter = 0.35;
		
		int[] sizeDistances = new int[]{0, 250, 150, 75};
		int[] sizeProbabilities = new int[]{0, 35, 30, 35};
		
		for(int i = 0; i < numLinks - numRootLinks; i++){
			int sizeIndex = chooseSizeIndex(sizeProbabilities);
			double dist = sizeDistances[sizeIndex];
			
			double theta = (avgTheta * (i + 1));
			theta -= zeroTheta;
			theta = randomHelper(theta * (1.0 - angularJitter), theta * (1.0 + angularJitter));
			
			double x = root.getX() + (Math.cos(theta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			double y = root.getY() + (Math.sin(theta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			StarSystem newSys = new StarSystem(x, y, "Tree - Large", null);
			if(!Main.sysLinkIntersect(root, newSys, removeDupes(systems))){
				newSys.addLink(root);
				Main.getSystemList().add(newSys);
				systems.add(newSys);
				Tree newTree = null;
				if(sizeIndex == 1)
					newTree = new Tree(0, 0, newSys, Size.MEDIUM, systems);
				if(sizeIndex == 2)
					newTree = new Tree(0, 0, newSys, Size.SMALL, systems);
				if(sizeIndex == 3)
					newTree = new Tree(0, 0, newSys, Size.TINY, systems);
			}
		}
		for(StarSystem s: systems){
			if(s.getLinks().size() <= 1)
				outerSystems.add(s);
		}
	}

	public void generateMedium() {
		int numLinks = rand.nextInt(4) + 3;
		double avgTheta = (2 * Math.PI) / (double)(numLinks + numRootLinks);
		double angularJitter = 0.25;
		double distJitter = 0.25;
		
		int[] sizeDistances = new int[]{0, 0, 150, 75};
		int[] sizeProbabilities = new int[]{0, 0, 40, 60};
		
		LinkedList<StarSystem> newLinks = new LinkedList<StarSystem>();
		
		for(int i = 0; i < numLinks - numRootLinks; i++){
			int sizeIndex = chooseSizeIndex(sizeProbabilities);
			double dist = sizeDistances[sizeIndex];			
			
			double theta = (avgTheta * (i + 1));
			theta -= zeroTheta;
			theta = randomHelper(theta * (1.0 - angularJitter), theta * (1.0 + angularJitter));
			
			double x = root.getX() + (Math.cos(theta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			double y = root.getY() + (Math.sin(theta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			StarSystem newSys = new StarSystem(x, y, "Tree - Medium", null);
			if(!Main.sysLinkIntersect(root, newSys, removeDupes(systems))){
				newSys.addLink(root);
				Main.getSystemList().add(newSys);
				systems.add(newSys);
				newLinks.add(newSys);

				Tree newTree = null;
				if(sizeIndex == 2)
					newTree = new Tree(0, 0, newSys, Size.SMALL, systems);
				if(sizeIndex == 3)
					newTree = new Tree(0, 0, newSys, Size.TINY, systems);
			}
		}
		for(StarSystem s: systems){
			if(s.getLinks().size() <= 1)
				outerSystems.add(s);
		}
	}

	public void generateSmall() {
		int numLinks = rand.nextInt(6);
		double avgTheta = (2 * Math.PI) / (double)(numLinks + numRootLinks);
		double angularJitter = 0.25;
		double distJitter = 0.15;

		int[] sizeDistances = new int[]{0, 0, 0, 75};
		int[] sizeProbabilities = new int[]{0, 0, 0, 100};
		
		for(int i = 0; i < numLinks - numRootLinks; i++){
			int sizeIndex = chooseSizeIndex(sizeProbabilities);
			double dist = sizeDistances[sizeIndex];						
			
			double theta = (avgTheta * (i + 1));
			theta -= zeroTheta;
			theta = randomHelper(theta * (1.0 - angularJitter), theta * (1.0 + angularJitter));
			
			double x = root.getX() + (Math.cos(theta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			double y = root.getY() + (Math.sin(theta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			StarSystem newSys = new StarSystem(x, y, "Tree - Small", null);
			if(!Main.sysLinkIntersect(root, newSys, removeDupes(systems))){
				newSys.addLink(root);
				Main.getSystemList().add(newSys);
				systems.add(newSys);
				Tree newTree = null;
				if(sizeIndex == 3)
					newTree = new Tree(0, 0, newSys, Size.TINY, systems);
			}
			for(StarSystem s: systems){
				if(s.getLinks().size() <= 1)
					outerSystems.add(s);
			}
		}
	}

	public void generateTiny() {
		int numLinks = rand.nextInt(4);
		double avgTheta = (2 * Math.PI) / (double)(numLinks + numRootLinks);
		double angularJitter = 0.25;
		double distJitter = 0.15;
		double dist = 75;
		
		for(int i = 0; i < numLinks - numRootLinks; i++){
			double theta = (avgTheta * (i + 1));
			theta -= zeroTheta;
			theta = randomHelper(theta * (1.0 - angularJitter), theta * (1.0 + angularJitter));
			
			double x = root.getX() + (Math.cos(theta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			double y = root.getY() + (Math.sin(theta) * (dist + (rand.nextDouble() * dist * distJitter * 2) - (dist * distJitter)));
			StarSystem newSys = new StarSystem(x, y, "Tree - Tiny", null);
			if(!Main.sysLinkIntersect(root, newSys, removeDupes(systems))){
				newSys.addLink(root);
				Main.getSystemList().add(newSys);
				systems.add(newSys);
				outerSystems.add(newSys);
			}
		}
		for(StarSystem s: systems){
			if(s.getLinks().size() <= 1)
				outerSystems.add(s);
		}
	}
	
	private double randomHelper(double low, double high){ 
		System.out.println("Picking random number between " + low + " and " + high);
		
		double num = rand.nextDouble() * (high - low);
		
		return num + low;
	}
	
	private int chooseSizeIndex(int[] sizeProbabilities){
		int size = rand.nextInt(100);
		int ret = 0;
		for(int i: sizeProbabilities){
			if(size < i)
				return ret;
			ret++;
			size -= i;
		}
		return -1;
	}
}
