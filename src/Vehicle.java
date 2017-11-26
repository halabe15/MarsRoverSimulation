import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Models a Rover Vehicle, which roams the landscape on Mars. Its objective is to collect Rock samples
 * and bring them back to the Mothership. 
 * 
 * Its primary method is act(), which calls either its simple, collaborative or optimised behaviour depending
 * on which one the user wants to use (the other method calls should be commented out). 
 * 
 * Descriptions of each behaviour in the act methods' subsumption hierarchies:
 * 
 * (1) if carrying a sample and at the base then drop sample 
 * (2) if carrying a sample and not at the base then travel up gradient 
 * (3) if detect a sample then pick sample 
 * (4) if true then move randomly 
 * (5) if carrying a sample and not at the base then drop two crumbs and travel up gradient
 * (6) if sense crumbs then pick up 1 and travel down gradient 
 * (7) if true then move with intent (to the first found available location)
 * (8) if sense crumbs then pick up 1 and travel to its location 
 * 
 * @author lawtonbs
 *
 */
class Vehicle extends Entity {
	public boolean carryingSample;
	
	public Vehicle(Location l) {
		super(l);	
		this.carryingSample = false;
	}

	public void act(Field f, Mothership m, ArrayList<Rock> rocksCollected)
	{
		//actCollaborative(f,m,rocksCollected);
		//actSimple(f,m,rocksCollected);
		actOptimised(f,m,rocksCollected);
	}
	
	/**
	 * Act according to the optimised subsumption hierarchy below.
	 * 
	 * (1) ≺ (5) ≺ (3) ≺ (8) ≺ (7)
	 * 
	 * Details on the individual behaviours can be found in the class-level comments. 
	 * 
	 * N.B. behaviour (5) defaults to behaviour (7) if moving up a gradient isn't possible, i.e.
	 * none of the adjacent locations have a stronger signal strength. Without this, the Vehicles can get
	 * caught in a loop and never leave their location -- meaning the simulation never ends. This optimisation also
	 * differs from the original in that a crumb is dropped regardless of whether or not the vehicle travels up the gradient.
	 * 
	 * @param f Field the vehicle is operating in 
	 * @param m Vehicle's Mothership 
	 * @param rocksCollected Rocks collected and returned to Mothership 
	 */
	public void actOptimised(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
		boolean atBase = f.isNeighbourTo(location, Mothership.class);		
		Location adjacentRockLocation = f.getNeighbour(location, Rock.class);
		Location adjacentCrumb = senseCrumbs(f);
		
		if (carryingSample && atBase) {				// (1)					
			m.incrementRockCount();
			carryingSample = false;
		} else if (carryingSample && !atBase) {		// (5)*
			f.dropCrumbs(location, 2);
			if (!moveUpGradient(f)) {
				moveToFirstAvailable(f);
			}
		} else if (adjacentRockLocation != null) {	// (3)
			Rock rock = (Rock) f.getObjectAt(adjacentRockLocation);
			rocksCollected.add(rock);
			f.clearLocation(adjacentRockLocation);
			carryingSample = true;
		} else if (adjacentCrumb != null) {			// (8)
			f.pickUpACrumb(adjacentCrumb);
			if (!move(f, adjacentCrumb)) {
				moveToFirstAvailable(f);
			}
		} else {									// (7)
			moveToFirstAvailable(f);
		}
	}
	
	/**
	 * Act according to the collaborative subsumption hierarchy below.
	 * 
	 * (1) ≺ (5) ≺ (3) ≺ (6) ≺ (4)
	 * 
	 * Details on the individual behaviours can be found in the class-level comments. 
	 * 
	 * N.B. behaviour (5) defaults to behaviour (4) if moving up a gradient isn't possible, i.e.
	 * none of the adjacent locations have a stronger signal strength. Without this, the Vehicles can get
	 * caught in a loop and never leave their location -- meaning the simulation never ends. 
	 * 
	 * @param f Field the vehicle is operating in 
	 * @param m Vehicle's Mothership 
	 * @param rocksCollected Rocks collected and returned to Mothership 
	 */
	public void actCollaborative(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
		boolean atBase = f.isNeighbourTo(location, Mothership.class);		
		Location adjacentRockLocation = f.getNeighbour(location, Rock.class);
		Location adjacentCrumb = senseCrumbs(f);
		
		if (carryingSample && atBase) {				// (1)				
			m.incrementRockCount();
			carryingSample = false;
		} else if (carryingSample && !atBase) {		// (5)
			Location previous = location;
			if (moveUpGradient(f)) {
				f.dropCrumbs(previous, 2);
			} else {
				moveRandomly(f);
			}
		} else if (adjacentRockLocation != null) {	// (3)
			Rock rock = (Rock) f.getObjectAt(adjacentRockLocation);
			rocksCollected.add(rock);
			f.clearLocation(adjacentRockLocation);
			carryingSample = true;
		} else if (adjacentCrumb != null) {			// (6)
			f.pickUpACrumb(adjacentCrumb);
			moveDownGradient(f);
		} else {									// (4)
			moveRandomly(f);
		}
	}
	
	/**
	 * Act according to the simple, non-collaborative subsumption hierarchy below:
	 * 
	 * (1) ≺ (2) ≺ (3) ≺ (4)
	 * 
	 * Details on the individual behaviours can be found in the class-level comments. 
	 * 
	 * 
	 * N.B. behaviour (2) defaults to behaviour (4) if moving up a gradient isn't possible, i.e.
	 * none of the adjacent locations have a stronger signal strength. Without this, the Vehicles can get
	 * caught in a loop and never leave their location -- meaning the simulation never ends. 
	 * 
	 * @param f Field the Vehicle is operating in 
	 * @param m Vehicle's Mothership 
	 * @param rocksCollected Rocks collected and returned to the Mothership 
	 */
	public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
		boolean atBase = f.isNeighbourTo(location, Mothership.class);		
		Location adjacentRockLocation = f.getNeighbour(location, Rock.class);
		
		if (carryingSample && atBase) {				// (1)
			m.incrementRockCount();
			carryingSample = false;
		} else if (carryingSample && !atBase) {		// (2)
			if (!moveUpGradient(f)) {
				moveRandomly(f);
			}
		} else if (adjacentRockLocation != null) {	// (3)
			Rock rock = (Rock) f.getObjectAt(adjacentRockLocation);
			rocksCollected.add(rock);
			f.clearLocation(adjacentRockLocation);
			carryingSample = true;
		} else {									// (4)
			moveRandomly(f);
		}
	}
	
	/**
	 * 
	 * Moves the vehicle to the specified location, if it's free. 
	 * 
	 * @param f Field the vehicle is operating in 
	 * @param destination Vehicle's desired position 
	 * 
	 * @return Whether or not the Vehicle was moved to the destination 
	 */
	private boolean move(Field f, Location destination) {
		if (f.getObjectAt(destination) == null) {
			f.clearLocation(location);
			f.place(this, destination);
			setLocation(destination);
			
			return true;
		} else {
			return false;
		}
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
				return move(f, adjacent);
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
				return move(f, adjacent);
			}
		}
		return false; 
	}
	
	/**
	 * Moves the vehicle to a randomly selected adjacent space, if one is free
	 * 
	 * @param f Field the vehicle is operating in 
	 */
	private void moveRandomly(Field f) {
		ArrayList<Location> adjacentLocations = f.getAllfreeAdjacentLocations(location);
		int randomNum = ThreadLocalRandom.current().nextInt(0, adjacentLocations.size());
		if (adjacentLocations.size() > 0) {
			move(f, adjacentLocations.get(randomNum));
		}
	}
	
	/**
	 * Moves the vehicle to the first found adjacent location, if one is free 
	 * 
	 * @param f Field the vehicle is operating in 
	 */
	private void moveToFirstAvailable(Field f) {
		Location next = f.freeAdjacentLocation(location);
		move(f, next);
	}
	
	/**
	 * Senses crumbs in locations adjacent the Vehicle. If some are found, returns the
	 * Location containing the highest number of crumbs. Otherwise, null.
	 * 
	 * @param f Field the vehicle is operating in  
	 * @param adjacentLocations locations adjacent to the vehicle's position 
	 * @return Location containing (the most) crumbs if somewhere found adjacent to the vehicle, or null
	 * if none were found 
	 */
	private Location senseCrumbs(Field f) {
		ArrayList<Location> adjacentLocations = f.getAllfreeAdjacentLocations(location);
		int highestCrumbCount = 0;
		Location adjacentCrumbLocation = null;
		
		for (Location adjacent : adjacentLocations) {
			if (f.getCrumbQuantityAt(adjacent) > highestCrumbCount) {
				adjacentCrumbLocation = adjacent;
				highestCrumbCount = f.getCrumbQuantityAt(location);
			}
		}
		return adjacentCrumbLocation;
	}
}
