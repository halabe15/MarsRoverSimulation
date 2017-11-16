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
		Location adjacentCrumb = senseCrumbs(f, adjacentLocations);
		
		if (carryingSample && atBase) {
			carryingSample = false;
		} else if (carryingSample && !atBase) {
			Location previous = location;
			if (moveUpGradient(f)) {
				f.dropCrumbs(previous, 2);
			} else {
				moveRandomly(f);
			}
		} else if (adjacentRockSample != null) {
			f.clearLocation(adjacentRockSample);
			carryingSample = true;
		} else if (adjacentCrumb != null) {
			if (moveDownGradient(f)) {
				f.pickUpACrumb(adjacentCrumb);
			} else {
				moveRandomly(f);
			}
		} else {
			moveRandomly(f);
		}
	}

	public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
		Location base = f.getNeighbour(location, Mothership.class);
		boolean atBase = (base != null) ? true : false;
				
		Location adjacentRockSample = f.getNeighbour(location, Rock.class);
		
		if (carryingSample && atBase) {
			carryingSample = false;
		} else if (carryingSample && !atBase) {
			moveUpGradient(f);
		} else if (adjacentRockSample != null) {
			f.clearLocation(adjacentRockSample);
			carryingSample = true;
		} else {
			moveRandomly(f);
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
	 * Moves the vehicle up the gradient and returns true, if there's an adjacent
	 * space with a signal strength greater than the vehicle's current location. 
	 * If there are no adjacent spaces with a higher signal strength, the vehicle 
	 * won't move and the method will return false 
	 * 
	 * @param f Field the vehicle is operating in 
	 * @return Whether or not the vehicle could move up the gradient 
	 */
	private boolean moveUpGradient(Field f) {
		ArrayList<Location> adjacentLocations = f.getAllfreeAdjacentLocations(location);
		for (Location adjacent : adjacentLocations) {
			if (f.getSignalStrength(adjacent) > f.getSignalStrength(location)) {
				move(f, adjacent);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Moves the vehicle down the gradient and returns true, if there's an adjacent
	 * space with a signal strength less than the vehicle's current location. 
	 * If there are no adjacent spaces with a lower signal strength, the vehicle 
	 * won't move and the method will return false 
	 * 
	 * @param f Field the vehicle is operating in 
	 * @return Whether or not the vehicle could move down the gradient 
	 */
	private boolean moveDownGradient(Field f) {
		ArrayList<Location> adjacentLocations = f.getAllfreeAdjacentLocations(location);
		for (Location adjacent : adjacentLocations) {
			if (f.getSignalStrength(adjacent) < f.getSignalStrength(location)) {
				move(f, adjacent);
				return true;
			}
		}
		return false; 
	}
	
	/**
	 * Moves the vehicle to any adjacent space, if there are any free 
	 * 
	 * @param f Field the vehicle is operating in 
	 */
	private void moveRandomly(Field f) {
		Location adjacent = f.freeAdjacentLocation(location);
		if (adjacent != null) {
			move(f, adjacent);
		}
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
