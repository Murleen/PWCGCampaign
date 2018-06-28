package pwcg.campaign.ww1.medals;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.ArmedService;
import pwcg.campaign.Campaign;
import pwcg.campaign.medals.Medal;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.core.exception.PWCGException;

public class AmericanMedalManager extends RoFMedalManager 
{
    public static int PILOTS_BADGE = 1;

	public static int DISTINGUISHED_SERVICE_MEDAL = 2;
	public static int DISTINGUISHED_FLYING_CROSS = 3;
	public static int MEDAL_OF_HONOR = 4;
	
	public static int WOUND_CHEVRON = 20;

    AmericanMedalManager (Campaign campaign)
    {
        super(campaign);
	        
        medals.put(PILOTS_BADGE, new Medal ("Pilots Badge",                                 "us_pb.jpg"));
		medals.put(DISTINGUISHED_SERVICE_MEDAL, new Medal ("Distinguished Service Medal",	"DSM.jpg"));
		medals.put(DISTINGUISHED_FLYING_CROSS, new Medal ("Distinguished Service Cross",	"DSC.jpg"));
		medals.put(MEDAL_OF_HONOR, new Medal ("Medal of Honor",								"MoH.jpg"));
		
		medals.put(WOUND_CHEVRON, new Medal ("Wound Chevron", 							"WoundChev.jpg"));
	} 

    protected Medal awardWings(SquadronMember pilot) 
    {
        if (!hasMedal(pilot, medals.get(PILOTS_BADGE)))
        {
            return medals.get(PILOTS_BADGE);
        }
        
        return null;
    }

	public Medal getWoundedAward(SquadronMember pilot, ArmedService service) 
	{
		return medals.get(WOUND_CHEVRON);
	}

	public Medal awardFighter(SquadronMember pilot, ArmedService service, int victoriesThisMission) 
	{
		if ((pilot.getSquadronMemberVictories().getAirToAirVictories() >= 5) && !hasMedal(pilot, medals.get(DISTINGUISHED_SERVICE_MEDAL)))
		{
			return medals.get(DISTINGUISHED_SERVICE_MEDAL);
		}
		else if (pilot.getSquadronMemberVictories().getAirToAirVictories() >= 10 && !hasMedal(pilot, medals.get(DISTINGUISHED_FLYING_CROSS)))
		{
			return medals.get(DISTINGUISHED_FLYING_CROSS);
		}
		else
		{
			if (!hasMedal(pilot, medals.get(MEDAL_OF_HONOR)))
			{
				if ((pilot.getSquadronMemberVictories().getAirToAirVictories() >= 15))
				{
					if (victoriesThisMission >= 3)
					{
						return medals.get(MEDAL_OF_HONOR);
					}
				}
				else if ((pilot.getSquadronMemberVictories().getAirToAirVictories() >= 20))
				{
					if (victoriesThisMission >= 2)
					{
						return medals.get(MEDAL_OF_HONOR);
					}
				}
			}
		}
		
		return null;
	}

    public Medal awardBomber(SquadronMember pilot, ArmedService service, int victoriesThisMission) 
    {
        if (!hasMedal(pilot, medals.get(DISTINGUISHED_SERVICE_MEDAL)))
        {
            if (pilot.getMissionFlown() >= 20 || pilot.getGroundVictories().size() > 10)
            {
                return medals.get(DISTINGUISHED_SERVICE_MEDAL);
            }
        }
        if (!hasMedal(pilot, medals.get(DISTINGUISHED_FLYING_CROSS)))
        {
            if (pilot.getMissionFlown() >= 40 || pilot.getGroundVictories().size() > 30)
            {
                return medals.get(DISTINGUISHED_FLYING_CROSS);
            }
        }
        if (!hasMedal(pilot, medals.get(MEDAL_OF_HONOR)))
        {
            if (pilot.getMissionFlown() >= 100 || pilot.getGroundVictories().size() > 100)
            {
                return medals.get(MEDAL_OF_HONOR);
            }
        }
        
        return awardFighter(pilot, service, victoriesThisMission);
    }

	@Override
	public List<Medal> getAllAwardsForService() throws PWCGException
	{
		List<Medal> medalsInOrder = new ArrayList<>();
		medalsInOrder.addAll(getWoundBadgesInOrder());
		medalsInOrder.addAll(getAllBadgesInOrder());
		return medalsInOrder;
	}

	@Override
	public List<Medal> getAllMedalsInOrder()
	{
		List<Medal> medalsInOrder = new ArrayList<>();
		medalsInOrder.add( medals.get(DISTINGUISHED_SERVICE_MEDAL));
		medalsInOrder.add( medals.get(DISTINGUISHED_FLYING_CROSS));
		medalsInOrder.add( medals.get(MEDAL_OF_HONOR));
		
		return medalsInOrder;
	}

	@Override
	public List<Medal> getWoundBadgesInOrder()
	{
		List<Medal> medalsInOrder = new ArrayList<>();
		medalsInOrder.add( medals.get(WOUND_CHEVRON));		
		return medalsInOrder;
	}

	@Override
	public List<Medal> getAllBadgesInOrder()
	{
		List<Medal> medalsInOrder = new ArrayList<>();
		medalsInOrder.add( medals.get(PILOTS_BADGE));		
		return medalsInOrder;
	}
}
