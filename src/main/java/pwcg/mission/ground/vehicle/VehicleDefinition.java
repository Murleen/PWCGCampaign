package pwcg.mission.ground.vehicle;

import java.util.Date;
import java.util.List;

import pwcg.campaign.context.Country;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.IWeight;

public class VehicleDefinition implements IVehicleDefinition, IWeight
{
    private String scriptDir;
    private String modelDir;
    private String vehicleName;
    private String vehicleType;
    private String displayName;
    private List<Country> countries;
    private Date startDate;
    private Date endDate;
    private int rarityWeight;
    private VehicleClass vehicleClass;
    private String associatedBlock;
    
    @Override
    public String getScriptDir()
    {
        return scriptDir;
    }

    @Override
    public String getModelDir()
    {
        return modelDir;
    }

    @Override
    public String getVehicleType()
    {
        return vehicleType;
    }

    @Override
    public String getVehicleName()
    {
        return vehicleName;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public int getRarityWeight()
    {
        return rarityWeight;
    }

    @Override
    public int getWeight()
    {
        return rarityWeight;
    }

    @Override
    public String getAssociatedBlock() {
        return associatedBlock;
    }

    @Override
    public boolean shouldUse(VehicleRequestDefinition requestDefinition) throws PWCGException
    {
        if (vehicleClass != requestDefinition.getVehicleClass())
        {
            return false;
        }
        
        if (!DateUtils.isDateInRange(requestDefinition.getDate(), startDate, endDate))
        {
            return false;
        }
        
        for (Country vehicleCountry : countries)
        {
            if (vehicleCountry == requestDefinition.getCountry())
            {
                return true;
            }
        }
        return false;
    }
}
