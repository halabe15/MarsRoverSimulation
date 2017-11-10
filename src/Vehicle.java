import java.util.ArrayList;

class Vehicle extends Entity {
	public boolean carryingSample;
	
	public Vehicle(Location l) {
		super(l);	
		this.carryingSample = false;
	}

	public void act(Field f, Mothership m, ArrayList<Rock> rocksCollected)
	{
		//actCollaborative(f,m,rocksCollected);
		actSimple(f,m,rocksCollected);
	}
	
	
	public void actCollaborative(Field f, Mothership m, ArrayList<Rock> rocksCollected){
		//complete this method
	}

	public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected){
		Location base = f.getNeighbour(location, Mothership.class);
		boolean atBase = (base != null) ? true : false;
		ArrayList<Location> adjLocations = f.getAllfreeAdjacentLocations(location);
		Location neighbouringSample = f.getNeighbour(location, Rock.class);
		
		
		// If carrying sample and at base, drop sample 
		if (carryingSample && atBase) {
			carryingSample = false;
		} 
		// If carrying sample and not at base, travel up gradient  
		else if (carryingSample && !atBase) {
			for (Location adjLoc : adjLocations) {
				if (f.getSignalStrength(adjLoc) > f.getSignalStrength(location)) {
					f.clearLocation(location);
					f.place(this, adjLoc);
					setLocation(adjLoc);
					return;
				}
			} 
		} 
		// If a sample is detected, pick up sample 
		else if (neighbouringSample != null) {
			f.clearLocation(neighbouringSample);
			carryingSample = true;
		} 
		// If nothing else to do, move randomly 
		else {
			Location neighbour = f.freeAdjacentLocation(location);
			if (neighbour != null) {
				f.clearLocation(location);
				f.place(this, neighbour);
				setLocation(neighbour);
				return;
			}
		}
	}
}
