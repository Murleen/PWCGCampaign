package pwcg.campaign;

import java.util.Date;

import pwcg.campaign.api.Side;
import pwcg.campaign.context.FrontLinesForMap;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.io.json.BattleIOJson;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.CoordinateBox;
import pwcg.core.utils.Logger;

public class BattleManager
{
	private Battles battles = new Battles();

	public BattleManager()
	{
	}
	
	public void initialize()
	{
        try
        {
            battles = BattleIOJson.readJson();
        }
        catch (Exception e)
        {
            Logger.logException(e);
        }
	}

    public Battle getBattleForCampaign(FrontMapIdentifier mapId, Coordinate position, Date date) 
    {     
        try
        {
            for (Battle battle : battles.getBattles())
            {
                if (isBattleAtRightTime(date, battle))
                {
	                if (isBattleOnRightMap(battle, mapId))
	                {
                        if (isBattleNearPlayer(position, battle, mapId, date))
                        {
                            return battle;
                        }
                    }
                }
            }
        }
        catch (Throwable t)
        {
           Logger.logException(t);
        }
        
        return null;
    }

    private boolean isBattleOnRightMap(Battle battle, FrontMapIdentifier mapId)
	{
    	if (mapId == null)
    	{
    		return false;
    	}
    	
    	if (battle.getMap() == mapId)
    	{
    		return true;
    	}
    	
		return false;
	}

	private boolean isBattleAtRightTime(Date date, Battle battle) throws PWCGException
    {
        if (date.after(battle.getStartDate()) && date.before(battle.getStopDate()))
        {
        	return true;
        }
            
        return false;
    }
    
    private boolean isBattleNearPlayer(Coordinate position, Battle battle, FrontMapIdentifier matchingMap, Date date) throws PWCGException
    {
        FrontLinesForMap frontLineMarker =  PWCGContextManager.getInstance().getMapByMapId(matchingMap).getFrontLinesForMap(date);
        Coordinate closestFrontLines = frontLineMarker.findClosestFrontCoordinateForSide(position, Side.ALLIED);
        CoordinateBox coordinateBox = CoordinateBox.coordinateBoxFromCorners(battle.getSWCorner(), battle.getNECorner());

        if (coordinateBox.isInBox(closestFrontLines))
        {
            return true;
        }
        
        return false;
    }
}
