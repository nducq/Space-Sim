package init;

import java.util.ArrayList;
import java.util.Iterator;

import Terrestrial.resources.Mineral;
import governments.Faction;

public class InitializeFactions implements Iterable<Faction>{
	private final Faction colonialUnion;
	private final Faction velyeanEmpire;
	private final Faction centauriEmpire;
	
	public InitializeFactions(){
		colonialUnion = new Faction("Colonial Union", "Union", "Union", 0xFF304B96, 8);
		velyeanEmpire = new Faction("Velyean Empire", "Velyea", "Velyean", 0xFF416D51, 10);
		centauriEmpire = new Faction("Centauri Empire", "Centauri", "Centauri", 0xFFFFA000, 10);
	}

	@Override
	public Iterator<Faction> iterator() {
		ArrayList<Faction> factionList = new ArrayList<Faction>();
		
		factionList.add(colonialUnion);
		factionList.add(velyeanEmpire);
		factionList.add(centauriEmpire);
		
		Iterator<Faction> it = new Iterator<Faction>(){

			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < factionList.size();
			}

			@Override
			public Faction next() {
				index++;
				return factionList.get(index - 1);
			}
			
		};
		
		return it;
	}

}
