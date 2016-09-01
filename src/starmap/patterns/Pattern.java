package starmap.patterns;

import java.util.LinkedList;
import java.util.Random;

import main.Main;
import starmap.StarMapGenerator;
import starmap.StarSystem;


public abstract class Pattern {

	protected LinkedList<StarSystem> outerSystems;
	protected LinkedList<StarSystem> systems;
	protected StarSystem root;
	protected Random rand;
	protected double zeroTheta;
	protected int numRootLinks;
	protected static boolean[][] mapGrid;
	protected int originX;
	protected int originY;
	protected StarSystem topMost;
	protected StarSystem bottomMost;
	protected StarSystem leftMost;
	protected StarSystem rightMost;
	
	public Pattern(double x, double y, StarSystem root, Size size, LinkedList<StarSystem> systems) {
		this.root = root;
		
		topMost= null;
		bottomMost= null;
		leftMost= null;
		rightMost= null;
		
		rand = Main.rand;
		this.systems = systems;
		outerSystems = new LinkedList<StarSystem>();
		this.systems.add(root);
		
		if(root.getLinks().size() == 0){
			zeroTheta = 0;
			numRootLinks = 0;
		}
		else{
			zeroTheta = Math.atan2(root.getY() - root.getLinks().get(0).getY(),  root.getX() - root.getLinks().get(0).getX());
			zeroTheta = (Math.PI * 2) - zeroTheta - Math.PI;
			//System.out.println("Zero theta: " + Math.toDegrees(zeroTheta));
			numRootLinks = 1;
		}
		
		switch(size){
			case HUGE:
				generateHuge();
				break;
			case LARGE:
				generateLarge();
				break;
			case MEDIUM:
				generateMedium();
				break;
			case SMALL:
				generateSmall();
				break;
			case TINY:
				generateTiny();
				break;
		}
		
		/*
		LinkedList<StarSystem> cullList = new LinkedList<StarSystem>();
		for(StarSystem s: this.systems){
			if(s.getLinks().size() < 2)
				cullList.add(s);
		}
		for(StarSystem s: this.outerSystems){
			if(s.getLinks().size() < 2)
				cullList.add(s);
		}
		for(StarSystem s: Main.getSystemList()){
			if(s.getLinks().size() < 2)
				cullList.add(s);
		}
		
		for(StarSystem s: cullList){
			while(this.systems.contains(s))
				this.systems.remove(s);
			if(!this.systems.contains(s))
				System.out.println("inner systems don't contain " + s.getName());
			
			while(this.outerSystems.contains(s))
				this.outerSystems.remove(s);
			if(!this.outerSystems.contains(s))
				System.out.println("outer systems don't contain " + s.getName());
			
			while(Main.getSystemList().contains(s))
				Main.getSystemList().remove(s);
			if(!Main.getSystemList().contains(s))
				System.out.println("main systems don't contain " + s.getName());
		}
		*/
		
		for(StarSystem s: this.systems){
			if(topMost == null || s.getY() < topMost.getY())
				topMost = s;
			if(bottomMost == null || s.getY() > bottomMost.getY())
				bottomMost = s;
			if(leftMost == null || s.getX() < leftMost.getX())
				leftMost = s;
			if(rightMost == null || s.getX() > rightMost.getX())
				rightMost = s;
		}
		
		int width = (int)Math.ceil(( (root.getX() - leftMost.getX()) + (StarMapGenerator.CELL_WIDTH / 2)) / StarMapGenerator.CELL_WIDTH) +
					(int)Math.ceil(( (rightMost.getX() - root.getX()) + (StarMapGenerator.CELL_WIDTH / 2)) / StarMapGenerator.CELL_WIDTH);
		int height = (int)Math.ceil(( (root.getY() - topMost.getY()) + (StarMapGenerator.CELL_HEIGHT / 2)) / StarMapGenerator.CELL_HEIGHT) +
					 (int)Math.ceil(( (bottomMost.getY() - root.getY()) + (StarMapGenerator.CELL_HEIGHT / 2)) / StarMapGenerator.CELL_HEIGHT);
		
		width = Math.max(width, 1);
		height = Math.max(height, 1);
		
		//System.out.println("Grid width: " + Math.max(width, 2) + "\nGrid height: " + Math.max(height, 2));
		//System.out.println("Leftmost X: " + (leftMost.getX() - root.getX()) + "\nRightmost X: " + (rightMost.getX() - root.getX()));
		//System.out.println("Topmost Y: " + (topMost.getY() - root.getY()) + "\nBottommost Y: " + (bottomMost.getY() - root.getY()));
		
		mapGrid = new boolean[width][height];
		
		for(StarSystem s: this.systems){
			double currX = s.getX() - (Math.floor(leftMost.getX() / StarMapGenerator.CELL_WIDTH) * StarMapGenerator.CELL_WIDTH); // + (StarMapGenerator.CELL_WIDTH / 2);
			double currY = s.getY() - (Math.floor(topMost.getY() / StarMapGenerator.CELL_HEIGHT) * StarMapGenerator.CELL_HEIGHT);// + (StarMapGenerator.CELL_HEIGHT / 2);
			//System.out.println("Grid position:\nx: " + ((int)currX / StarMapGenerator.CELL_WIDTH) + "\twidth: " + (width));
			//System.out.println("y: " + ((int)currY / StarMapGenerator.CELL_HEIGHT) + "\theight: " + (height));
			if(currX <= 0)
				currX = 1;
			if(currY <= 0)
				currY = 1;
			
			mapGrid[(int)Math.ceil(currX / StarMapGenerator.CELL_WIDTH) - 1][(int)Math.ceil(currY / StarMapGenerator.CELL_HEIGHT) - 1] = true;
			
			if (s == this.root){
				originX = (int)Math.ceil(currX / StarMapGenerator.CELL_WIDTH) - 1;
				originY = (int)Math.ceil(currY / StarMapGenerator.CELL_HEIGHT) - 1;
			}
			//mapGrid[(int)Math.ceil(s.getX() / StarMapGenerator.CELL_WIDTH) + (width / 2) - (int)Math.ceil(leftMost.getY() / StarMapGenerator.CELL_WIDTH)][(int)Math.ceil(s.getY() / StarMapGenerator.CELL_HEIGHT) + (height / 2) - (int)Math.ceil(topMost.getY() / StarMapGenerator.CELL_HEIGHT)] = true;
		}
		
		for(StarSystem s: this.systems){
			for(StarSystem l: s.getLinks())
				addLinkToLocalGrid(s, l);
		}
	}
	/*Abstract methods*/
	public abstract void generateHuge();
	public abstract void generateLarge();
	public abstract void generateMedium();
	public abstract void generateSmall();
	public abstract void generateTiny();
	
	public LinkedList<StarSystem> getOuterSystems() {
		return outerSystems;
	}

	public LinkedList<StarSystem> getSystems() {
		return systems;
	}
	
	public boolean[][] getMapGrid(){
		return mapGrid;
	}
	
	public LinkedList<StarSystem> removeDupes(LinkedList<StarSystem> src){
		LinkedList<StarSystem> newList = new LinkedList<StarSystem>();
		for(StarSystem s: src){
			if(!newList.contains(s))
				newList.add(s);
		}
		return newList;
	}
	
	public void translate(double x, double y){
		for(StarSystem s: removeDupes(systems)){
			s.setX(s.getX() + x);
			s.setY(s.getY() + y);
		}
	}
	
	public void addToGrid(){
		for(StarSystem s: systems){
			//if(!Main.getSystemList().contains(s))
				s.addToGrid();
		}
		for(StarSystem s: systems){
			for(StarSystem l: s.getLinks())
				addLinkToGlobalGrid(s, l);
		}
	}
	
	public void addLinkToLocalGrid(StarSystem src, StarSystem trg){
		double dx = (trg.getX() - src.getX());
		double dy = (int) (trg.getY() - src.getY());
		for(double x = src.getX(); x <  trg.getX(); x += 5.0){
			double y = src.getY() + dy * (x - src.getX()) / dx;

			double currX = (x - (Math.floor(leftMost.getX() / StarMapGenerator.CELL_WIDTH) * StarMapGenerator.CELL_WIDTH)); // + (StarMapGenerator.CELL_WIDTH / 2);
			double currY = (y - (Math.floor(topMost.getY() / StarMapGenerator.CELL_HEIGHT) * StarMapGenerator.CELL_HEIGHT));// + (StarMapGenerator.CELL_HEIGHT / 2);
			//System.out.println("Grid position:\nx: " + ((int)currX / StarMapGenerator.CELL_WIDTH) + "\twidth: " + (width));
			//System.out.println("y: " + ((int)currY / StarMapGenerator.CELL_HEIGHT) + "\theight: " + (height));
			if(currX <= 0)
				currX = 1;
			if(currY <= 0)
				currY = 1;
			
			int gridX = (int)Math.ceil(currX / StarMapGenerator.CELL_WIDTH) - 1;
			int gridY = (int)Math.ceil(currY / StarMapGenerator.CELL_HEIGHT) - 1;
			if(gridX >= mapGrid.length)
				gridX = mapGrid.length - 1;
			if(gridY >= mapGrid[0].length)
				gridY = mapGrid[0].length - 1;
			mapGrid[gridX][gridY] = true;	
		}
	}
	
	public void addLinkToGlobalGrid(StarSystem src, StarSystem trg){		
		double dx = (trg.getX() - src.getX());
		double dy = (trg.getY() - src.getY());
		for(double x = src.getX(); x <  trg.getX(); x += 5.0){
			double y = src.getY() + (dy * (x - src.getX()) / dx);

			StarMapGenerator.setGrid((int)Math.ceil(x / StarMapGenerator.CELL_WIDTH) + (StarMapGenerator.GRID_WIDTH / 2) - 1, (int)Math.ceil(y / StarMapGenerator.CELL_HEIGHT) + (StarMapGenerator.GRID_HEIGHT / 2) - 1);
		};
	}
	
	public void printCollisionMask(){
		System.out.println("Origin: (" + originX + ", " + originY + ")");
		for(int i = 0; i < mapGrid[0].length; i++){
			for(int j = 0; j < mapGrid.length; j++){
				if(j == originX && i == originY)
					System.out.print("X ");
				else if(mapGrid[j][i])
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.print("\n");
		}
		
		System.out.print("\n");
	}
	
	public void printNeighborCollisionMask(){
		boolean[][] matchPattern = new boolean[mapGrid.length + 2][mapGrid[0].length + 2];
		
		for(int i = 1; i < mapGrid.length + 1; i++){
			for(int j = 1; j < mapGrid[0].length + 1; j++){
				if(mapGrid[i - 1][j - 1]){
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

		for(int i = 0; i < mapGrid.length; i++){
			for(int j = 0; j < mapGrid[0].length; j++){
				if(mapGrid[i][j])
					matchPattern[i + 1][j + 1] = false;
			}
		}
		
		for(int i = 0; i < matchPattern[0].length; i++){
			for(int j = 0; j < matchPattern.length; j++){
				if(matchPattern[j][i])
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.print("\n");
		}
		
		System.out.print("\n");
	}
	
	public int getOriginX(){
		return originX;
	}
	
	public int getOriginY(){
		return originY;
	}
	
}
