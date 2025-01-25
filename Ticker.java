/**
 * A Ticker is an class that will run other classes over time for you. Simply Make your class extent an Entity,
 * and then you may Overide the methods start, update, and remove. The class will start by running your entitiy
 * specific 'start' method. It will then run your specific 'update' method at a rate of 'FPS' frames per second.
 * 
 * Ticker is to be discarded when loop is terminated.
*/
import java.util.HashSet;
public class Ticker {
    // Keeps track of all entities present
    private final HashSet<Entity> entities;
    // Checks if the loop has terminated (even if loop has not begun has it terminated?)
    private boolean loop = true;
    // Have we run start yet?
    private boolean started = false;
    private long startTime;

    private final double FPS;
    private static final long SECOND = 1_000_000_000;

    private final static boolean DEBUG = true;

    /**
     * Initiates an instance of a Ticker with 60 FPS.
    */
    public Ticker(){
        this(60);
    }
    /**
     * Initiates an instance of a Ticker.
     * 
     * @param FPS the FPS of the application
    */
    public Ticker(double FPS) {
        this.FPS = FPS;
        this.entities = new HashSet<>();
        checkRep();
    }
    /**
     * CheckRep includes:
     * 
     * loop has not been terminated if not discarded
     * No null entities
     * All entity tickers should be equal to 'this'
     * 
    */
    private void checkRep() {
        if (!DEBUG) {
            return;
        }
        if (!loop) {
            throw new RuntimeException("Ticker has ended. Ticker object should be discarded.");
        }
        for (Entity entity: entities) {
            if (entity == null) {
                throw new RuntimeException("Null entity present.");
            }
            if (entity.ticker != this) {
                throw new RuntimeException("Entity without common entity present.");
            }
        }
    }
    /**
     * Adds the specified entity to 'this'.
     * 
     * Assigns the 'ticker' field to your entity and runs any code you have in
     * your entity's 'start' method.
     * 
     * @throws RuntimeException the entity you are trying to add is null
     * @throws RuntimeException the entity you are trying to add is already in 'this'
     * @param e entity to be added
    */
    public void addEntity(Entity e) {
        checkRep();
        if (e == null) {
            throw new RuntimeException("Cannot add a null entity.");
        }
        if (this.contains(e)) {
            throw new RuntimeException("Entity cannot be added as it is already present.");
        }
        entities.add(e);
        e.preStart(this);
        e.start();
        checkRep();
    }
    /**
     * Starts the program so that each entity's update method is run at a rate of FPS.
     * You can add entity's before or after running this method.
     * 
     * @throws RuntimeException 'this' has already started.
    */
    public void start() {
        checkRep();
        if (started) {
            throw new RuntimeException("TIcker has already started. You cannot run ticker when it is already running.");
        }
        started = true;
        startTime = System.nanoTime();
        long offset = startTime;
        while (loop) {
            long currentTime = System.nanoTime();
            if (currentTime - offset >= SECOND / FPS) {
                offset += SECOND / FPS;
            } else {
                continue;
            }
            HashSet<Entity> entityCopy = new HashSet<Entity>(entities);
            for (Entity entity: entityCopy) {
                entity.update();
            }
        }
    }
    /**
     * Removes the specified entity from 'this'.
     * 
     * Runs whatever you specify in your entity's 'remove' method.
     * 
     * @param entity the entity to be removed.
     * @throws RuntimeException 'entity' is not present.
    */
    public void remove(Entity entity) {
        checkRep();
        if (!this.contains(entity)) {
            throw new RuntimeException("Cannot remove entity that is not present.");
        }
        entity.remove();
        this.entities.remove(entity);
        checkRep();
    }
    /**
     * Checks to see if 'this' contains the specified 'entity'.
     * 
     * @param entity The entity we want to check for
     * @return true iff 'this' contains entity.
    */
    public boolean contains(Entity entity) {
        checkRep();
        return this.entities.contains(entity);
    }
    /**
     * Ends the loop so update is not called every FPS period of time.
     * 'this' is to be discarded after using this method.
     * Does not run any other code such as the remove method for your
     * Entity.
     * 
     * @throws RuntimeException ticker's loop has not started. AKA Ticker's 'start' method has not been run.
    */
    public void endLoop() {
        checkRep();
        if (!started) {
            throw new RuntimeException("Ticker has not started yet.");
        }
        loop = false;
    }
    /**
     * Gets time that 'start' method of 'this' Ticker has been running.
     * Returns time in nano-seconds
     * @return how long we have run 'start' methd in Ticker in nano-seconds.
     * @return 0 if start has not been run yet.
    */
    public long getRunTime() {
        if (!started) {
            return 0;
        }
        return System.nanoTime() - startTime;
    }
    /**
     * To use the Ticker class, make your object extend an Entity.
     * You may then put any code you want to run every period of time in the 'update'
     * method. start and remove can be used if you want to run code every time you add
     * an entity or remove an entity.
    */
    public static class Entity {
        // The ticker this entity was added to. Should be treated as final by user, but not as immutable
        // (Changing inner data OK, Changing class reference NOT OK)
        public Ticker ticker;
        // Write any code you want to run when you add this entity to ticker here.
        public void start() {}
        // This is where you write what code you want to run as the ticker updates
        public void update() {}
        // Runs what ever you want when removing your entity from 'this' Ticker
        public void remove() {}
        /**
         * Hidden methos automates setting ticker to 'this' within Ticker
        */
        private void preStart(Ticker ticker) {
            this.ticker = ticker;
        }
    }
}
