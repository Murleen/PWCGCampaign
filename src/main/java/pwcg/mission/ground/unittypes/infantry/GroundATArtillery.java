package pwcg.mission.ground.unittypes.infantry;

import java.io.BufferedWriter;
import java.io.IOException;

import pwcg.campaign.Campaign;
import pwcg.campaign.factory.VehicleFactory;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.config.ConfigSimple;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.mission.ground.GroundUnitInformation;
import pwcg.core.utils.Logger;
import pwcg.core.utils.MathUtils;
import pwcg.mission.ground.unittypes.GroundDirectFireUnit;
import pwcg.mission.ground.vehicle.IVehicleFactory;
import pwcg.mission.mcu.McuSpawn;

public class GroundATArtillery extends GroundDirectFireUnit
{
    private Campaign campaign;

	public GroundATArtillery(Campaign campaign, GroundUnitInformation pwcgGroundUnitInformation) throws PWCGException
	{
	    super(pwcgGroundUnitInformation);
	    this.campaign = campaign;
	}	

    public void createUnits() throws PWCGException 
    {
        IVehicleFactory vehicleFactory = VehicleFactory.createVehicleFactory();
        spawningVehicle = vehicleFactory.createATArtillery(pwcgGroundUnitInformation.getCountry());
        spawningVehicle.setPosition(pwcgGroundUnitInformation.getPosition().copy());         
        spawningVehicle.setOrientation(pwcgGroundUnitInformation.getOrientation().copy());
        spawningVehicle.populateEntity();
        spawningVehicle.getEntity().setEnabled(1);
    }

	protected void createSpawners() throws PWCGException 
	{
        int numatGun = calcNumUnits();

		// Face towards enemy
		double atGunFacingAngle = MathUtils.calcAngle(pwcgGroundUnitInformation.getPosition(), pwcgGroundUnitInformation.getDestination());
		Orientation atGunOrient = new Orientation();
		atGunOrient.setyOri(atGunFacingAngle);
		
        // MGs are behind the lines
        double initialPlacementAngle = MathUtils.adjustAngle (atGunFacingAngle, 180.0);      
        Coordinate atGunCoords = MathUtils.calcNextCoord(pwcgGroundUnitInformation.getPosition(), initialPlacementAngle, 25.0);

        // Locate the target such that startCoords is the middle of the line
        double startLocationOrientation = MathUtils.adjustAngle (atGunFacingAngle, 270);             
        double machingGunSpacing = 100.0;
        atGunCoords = MathUtils.calcNextCoord(atGunCoords, startLocationOrientation, ((numatGun * machingGunSpacing) / 2));       
        
        // Direction in which subsequent units will be placed
        double placementOrientation = MathUtils.adjustAngle (atGunFacingAngle, 90.0);        

        for (int i = 0; i < numatGun; ++i)
        {   
            McuSpawn spawn = new McuSpawn();
            spawn.setName("AT Gun Spawn " + (i + 1));      
            spawn.setDesc("AT Gun Spawn " + (i + 1));
            spawn.setPosition(atGunCoords);

            spawners.add(spawn);

            // Calculate the  next gun position
            atGunCoords = MathUtils.calcNextCoord(atGunCoords, placementOrientation, machingGunSpacing);
        }       
	}	

    protected void calcNumUnitsByConfig() throws PWCGException 
    {
        ConfigManagerCampaign configManager = campaign.getCampaignConfigManager();
        String currentGroundSetting = configManager.getStringConfigParam(ConfigItemKeys.SimpleConfigGroundKey);
        if (currentGroundSetting.equals(ConfigSimple.CONFIG_LEVEL_LOW))
        {
            minRequested = 1;
            maxRequested = 2;
        }
        else if (currentGroundSetting.equals(ConfigSimple.CONFIG_LEVEL_MED))
        {
            minRequested = 1;
            maxRequested = 3;
        }
        else if (currentGroundSetting.equals(ConfigSimple.CONFIG_LEVEL_HIGH))
        {
            minRequested = 2;
            maxRequested = 4;
        }
    }

    public void write(BufferedWriter writer) throws PWCGException
    {     
        try
        {
            writer.write("Group");
            writer.newLine();
            writer.write("{");
            writer.newLine();
            
            writer.write("  Name = \"AT Gun\";");
            writer.newLine();
            writer.write("  Index = " + index + ";");
            writer.newLine();
            writer.write("  Desc = \"AT Gun\";");
            writer.newLine();
    
            pwcgGroundUnitInformation.getMissionBeginUnit().write(writer);
    
            spawnTimer.write(writer);
            spawningVehicle.write(writer);
            for (McuSpawn spawn : spawners)
            {
                spawn.write(writer);
            }
    
            if (attackTimer != null)
            {
                attackTimer.write(writer);
                attackEntity.write(writer);
            }
    
            writer.write("}");
            writer.newLine();
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
    }
}	
