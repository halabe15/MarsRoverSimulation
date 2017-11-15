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

	public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
		Location base = f.getNeighbour(location, Mothership.class);
		boolean atBase = (base != null) ? true : false;
		
		ArrayList<Location> adjacentLocations = f.getAllfreeAdjacentLocations(location);
		
		Location adjacentRockSample = f.getNeighbour(location, Rock.class);
		
		if (carryingSample && atBase) {
			carryingSample = false;
		} else if (carryingSample && !atBase) {
			for (Location adjLoc : adjacentLocations) {
				// If there's free adjacent location with a stronger signal, move there 
				if (f.getSignalStrength(adjLoc) > f.getSignalStrength(location)) {
					move(f, adjLoc);
					return;
				}
			} 
		} else if (adjacentRockSample != null) {
			f.clearLocation(adjacentRockSample);
			carryingSample = true;
		} else {
			Location neighbour = f.freeAdjacentLocation(location);
			if (neighbour != null) {
				move(f, neighbour);
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
