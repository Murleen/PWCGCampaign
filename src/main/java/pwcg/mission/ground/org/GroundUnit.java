package pwcg.mission.ground.org;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.api.ICountry;
import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.constants.AiSkillLevel;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.PWCGLogger;
import pwcg.mission.ground.GroundUnitInformation;
import pwcg.mission.ground.unittypes.GroundUnitSpawningTrainBuilder;
import pwcg.mission.ground.unittypes.GroundUnitSpawningVehicleBuilder;
import pwcg.mission.ground.vehicle.IVehicle;
import pwcg.mission.ground.vehicle.VehicleClass;
import pwcg.mission.mcu.McuSpawn;
import pwcg.mission.mcu.McuTimer;
import pwcg.mission.target.TargetType;


public abstract class GroundUnit implements IGroundUnit
{    
    public static final int GROUND_UNIT_ACTIVATE_DISTANCE = 30000;

    protected int index = IndexGenerator.getInstance().getNextIndex();  
    protected GroundUnitInformation pwcgGroundUnitInformation;
    protected IVehicle vehicle;

    private VehicleClass vehicleClass;

    private List<IGroundAspect> groundElements = new ArrayList<>();
    private McuTimer spawnTimer = new McuTimer();
    private List <McuSpawn> spawners = new ArrayList<>();
    
    abstract protected List<Coordinate> createSpawnerLocations() throws PWCGException;
    abstract protected void addAspects() throws PWCGException;

    public GroundUnit(VehicleClass vehicleClass, GroundUnitInformation pwcgGroundUnitInformation) 
    {
        this.vehicleClass = vehicleClass;
        this.pwcgGroundUnitInformation = pwcgGroundUnitInformation;
    }

    @Override
    public void createGroundUnit() throws PWCGException 
    {
        createSpawns();
        createTargetAssociations();
        addAspects();
        linkElements();
    }

    private void linkElements() throws PWCGException 
    {
        IGroundAspect previousElement = null;
        for (IGroundAspect element : groundElements)
        {
            if (previousElement == null)
            {
                spawnTimer.setTarget(element.getEntryPoint());
            }
            else
            {
                previousElement.linkToNextElement(element.getEntryPoint());
            }
            previousElement = element;
        }
    }


    @Override
    public TargetType getTargetType()
    {
        return pwcgGroundUnitInformation.getTargetType();
    }

    @Override
    public boolean isUnitEngagedInCombat()
    {
        if (pwcgGroundUnitInformation.getTargetType() == TargetType.TARGET_ASSAULT ||
            pwcgGroundUnitInformation.getTargetType() == TargetType.TARGET_DEFENSE ||
            pwcgGroundUnitInformation.getTargetType() == TargetType.TARGET_INFANTRY)
        {
            return true;
        }

        return false;
    }

    @Override
    public void write(BufferedWriter writer) throws PWCGException 
    {      
        try
        {
            writer.write("Group");
            writer.newLine();
            writer.write("{");
            writer.newLine();
            
            writer.write("  Name = \"" + vehicleClass.getName() + "\";");
            writer.newLine();
            writer.write("  Index = " + index + ";");
            writer.newLine();
            writer.write("  Desc = \"" + vehicleClass.getName() + "\";");
            writer.newLine();

            writeMcus(writer);
            
            writer.write("}");
            writer.newLine();
        }
        catch (IOException e)
        {
            PWCGLogger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
    }

    private void writeMcus(BufferedWriter writer) throws PWCGException
    {
        vehicle.write(writer);
        spawnTimer.write(writer);
        for (McuSpawn spawn : spawners)
        {
            spawn.write(writer);
        }
        for (IGroundAspect groundElement : groundElements)
        {
            groundElement.write(writer);
        }
    }

    @Override
    public ICountry getCountry() throws PWCGException
    {
        return pwcgGroundUnitInformation.getCountry();
    }
    
    @Override
    public Coordinate getPosition() throws PWCGException
    {
        return pwcgGroundUnitInformation.getPosition();
    }
    
    @Override
    public String getName() throws PWCGException
    {
        return pwcgGroundUnitInformation.getName();
    }

    @Override
    public List<McuSpawn> getSpawners()
    {
        return spawners;
    }

    @Override
    public IVehicle getVehicle()
    {
        return vehicle;
    }

    @Override
    public VehicleClass getVehicleClass()
    {
        return vehicleClass;
    }
    
    @Override
    public void setAiLevel(AiSkillLevel aiLevel)
    {
        vehicle.setAiLevel(aiLevel);
    }

    @Override
    public int getEntryPoint()
    {
        return spawnTimer.getIndex();
    }
    
    @Override
    public void validate() throws PWCGException
    {
        GroundUnitValidator validator = new GroundUnitValidator(this);
        validator.validate();
        
    }
    
    McuTimer getSpawnTimer()
    {
        return spawnTimer;
    }

    protected List<IGroundAspect> getGroundElements()
    {
        return groundElements;
    }
    
    protected void addGroundElement(IGroundAspect groundElement)
    {
        groundElements.add(groundElement);
    }

    private void createSpawns() throws PWCGException 
    {
        createVehicle();
        createSpawnTimer();
        createSpawners();
        createTargetAssociations();
        createObjectAssociations();
    }

    private void createSpawnTimer() 
    {
        spawnTimer.setName("Spawn Timer");
        spawnTimer.setDesc("Spawn Timer");
        spawnTimer.setPosition(pwcgGroundUnitInformation.getPosition().copy());
    }
    
    private void createVehicle() throws PWCGException 
    {       
        if (vehicleClass == VehicleClass.TrainLocomotive)
        {
            GroundUnitSpawningTrainBuilder trainBuilder = new GroundUnitSpawningTrainBuilder(pwcgGroundUnitInformation);
            this.vehicle = trainBuilder.createTrainToSpawn();
        }
        else
        {
            this.vehicle = GroundUnitSpawningVehicleBuilder.createVehicleToSpawn(pwcgGroundUnitInformation, vehicleClass);
        }
    }

    private void createSpawners() throws PWCGException 
    {        
        for (Coordinate spawnLocation : createSpawnerLocations())
        {            
            McuSpawn spawn = new McuSpawn();
            spawn.setName("Artillery Spawn");      
            spawn.setDesc("Artillery Spawn");
            spawn.setPosition(spawnLocation.copy());
            spawners.add(spawn);
        }
    }

    private void createTargetAssociations()
    {
        for (McuSpawn spawn : spawners)
        {            
            spawnTimer.setTarget(spawn.getIndex());
        }        
    }

    private void createObjectAssociations() 
    {
        for (McuSpawn spawn : spawners)
        {
            spawn.setObject(vehicle.getEntity().getIndex());
        }
    }
    
    public GroundUnitType getGroundUnitType()
    {
        return vehicleClass.getGroundUnitType();
    }
}	

