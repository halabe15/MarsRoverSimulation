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
					move(f, adjLoc);
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
				move(f, neighbour);
				return;
			}
		}
	}
	
	/**
	 * 
	 * Moves the vehicle to the specified location  
	 * 
	 * @param f Field the vehicle is operating in 
	 * @param destination Vehicle's desired position 
	 */
	private void move(Field f, Location destination) {
		f.clearLocation(location);
		f.place(this, destination);
		setLocation(destination);
	}
}
