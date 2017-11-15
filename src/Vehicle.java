import java.util.ArrayList;

class Vehicle extends Entity {
	public boolean carryingSample;
	
	public Vehicle(Location l) {
		super(l);	
		this.carryingSample = false;
	}

	public void act(Field f, Mothership m, ArrayList<Rock> rocksCollected)
	{
		actCollaborative(f,m,rocksCollected);
		//actSimple(f,m,rocksCollected);
	}
	
	
	public void actCollaborative(Field f, Mothership m, ArrayList<Rock> rocksCollected){
		Location base = f.getNeighbour(location, Mothership.class);
		boolean atBase = (base != null) ? true : false;
		
		ArrayList<Location> adjacentLocations = f.getAllfreeAdjacentLocations(location);
		
		Location adjacentRockSample = f.getNeighbour(location, Rock.class);
		
		Location adjacentCrumbs = senseCrumbs(f, adjacentLocations);
		
		if (carryingSample && atBase) {
			carryingSample = false;
		} else if (carryingSample && !atBase) {
			f.dropCrumbs(location, 2);
			// Travel up the gradient if there's a free space  
			for (Location adjacent : adjacentLocations) {
				if (f.getSignalStrength(adjacent) > f.getSignalStrength(location)) {
					move(f, adjacent);
					return;
				}
			}
		} else if (adjacentRockSample != null) {
			f.clearLocation(adjacentRockSample);
			carryingSample = true;
		} else if (adjacentCrumbs != null) {
			f.pickUpACrumb(adjacentCrumbs);
			// Travel down the gradient if there's a free space
			for (Location adjacent : adjacentLocations) {
				if (f.getSignalStrength(adjacent) < f.getSignalStrength(location)) {
					move(f, adjacent);
					return;
				}
			}
		} else {
			Location adjacent = f.freeAdjacentLocation(location);
			if (adjacent != null) {
				move(f, adjacent);
			}
		}
	}

	public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
		Location base = f.getNeighbour(location, Mothership.class);
		boolean atBase = (base != null) ? true : false;
		
		ArrayList<Location> adjacentLocations = f.getAllfreeAdjacentLocations(location);
		
		Location adjacentRockSample = f.getNeighbour(location, Rock.class);
		
		if (carryingSample && atBase) {
			carryingSample = false;
		} else if (carryingSample && !atBase) {
			// Travel up the gradient if there's a free space  
			for (Location adjacent : adjacentLocations) {
				if (f.getSignalStrength(adjacent) > f.getSignalStrength(location)) {
					move(f, adjacent);
					return;
				}
			} 
		} else if (adjacentRockSample != null) {
			f.clearLocation(adjacentRockSample);
			carryingSample = true;
		} else {
			Location adjacent = f.freeAdjacentLocation(location);
			if (adjacent != null) {
				move(f, adjacent);
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
	
	/**
	 * 
	 * @param f Field the vehicle is operating in  
	 * @param adjacentLocations locations adjacent to the vehicle's position 
	 * @return Location containing crumbs if somewhere found adjacent to the vehicle, or null
	 * if none were found 
	 */
	private Location senseCrumbs(Field f, ArrayList<Location> adjacentLocations) {
		for (Location adjacent : adjacentLocations) {
			if (f.getCrumbQuantityAt(adjacent) > 0) {
				return adjacent;
			}
		}
		return null;
	}
}
