package pwcg.mission.flight.escort;

import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGMissionGenerationException;
import pwcg.mission.MissionBeginUnit;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.ground.factory.TargetFactory;
import pwcg.mission.ground.org.IGroundUnitCollection;


public class EscortedByPlayerFlightBuilder
{
    private IFlightInformation escortFlightInformation;
    private IFlightInformation escortedFlightInformation;
	
	public EscortedByPlayerFlightBuilder (IFlightInformation escortFlightInformation) throws PWCGException 
	{
	    this.escortFlightInformation = escortFlightInformation;
	}
	
	public EscortedByPlayerFlight createEscortedFlight() throws PWCGException
    {
        MissionBeginUnit missionBeginUnit = buildEscortedFlightInformation();
        EscortedByPlayerFlight PlayerEscortedFlightEscortedByPlayer = buildEscortedFlight(missionBeginUnit);        
        return PlayerEscortedFlightEscortedByPlayer;
	}

    private EscortedByPlayerFlight buildEscortedFlight(MissionBeginUnit missionBeginUnit) throws PWCGException
    {
        EscortedByPlayerFlight playerEscortedFlight = new EscortedByPlayerFlight (escortedFlightInformation);
		playerEscortedFlight.createFlight();
		
        IGroundUnitCollection targetUnit = createTargetForPlayerEscortedFlight();
        playerEscortedFlight.addLinkedGroundUnit(targetUnit);
        
        return playerEscortedFlight;
    }

    private MissionBeginUnit buildEscortedFlightInformation() throws PWCGException
    {
        Squadron friendlyBomberSquadron = determineSquadronToBeEscorted();
        MissionBeginUnit missionBeginUnit = new MissionBeginUnit(friendlyBomberSquadron.determineCurrentPosition(escortFlightInformation.getCampaign().getDate()));     
        
        this.escortedFlightInformation = EscortedByPlayerFlightInformationBuilder.buildEscortedByPlayerFlightInformation(
                escortFlightInformation, friendlyBomberSquadron);
        return missionBeginUnit;
    }

    private Squadron determineSquadronToBeEscorted() throws PWCGException
    {
        Squadron friendlyBombSquadron = PWCGContext.getInstance().getSquadronManager().getSquadronByProximityAndRoleAndSide(
                escortFlightInformation.getCampaign(), 
                escortFlightInformation.getSquadron().determineCurrentPosition(escortFlightInformation.getCampaign().getDate()), 
                Role.ROLE_BOMB, 
                escortFlightInformation.getSquadron().determineSquadronCountry(escortFlightInformation.getCampaign().getDate()).getSide());
        
        if (friendlyBombSquadron == null)
        {
            throw new PWCGMissionGenerationException ("Escort mission with no viable squadrons to be escorted - please create another mission");
        }
        return friendlyBombSquadron;
    }

    private IGroundUnitCollection createTargetForPlayerEscortedFlight() throws PWCGException
    {
        TargetFactory targetBuilder = new TargetFactory(escortedFlightInformation);
        targetBuilder.buildTarget();
        return targetBuilder.getGroundUnits();
    }
}
