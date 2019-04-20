package pwcg.campaign;

import pwcg.aar.ui.events.model.TransferEvent;
import pwcg.campaign.api.IRankHelper;
import pwcg.campaign.factory.RankFactory;
import pwcg.campaign.personnel.SquadronPersonnel;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadron.Squadron;
import pwcg.campaign.squadron.SquadronTransferFinder;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.RandomNumberGenerator;


public class TransferHandler 
{
    private Campaign campaign = null;
    private SquadronMember player = null;
    
    public TransferHandler (Campaign campaign, SquadronMember player)
    {
        this.campaign = campaign;
        this.player = player;
    }

	public TransferEvent transferPlayer(Squadron oldSquadron, Squadron newSquadron) throws PWCGException 
	{
	    int leaveTimeForTransfer = transferleaveTime();	
        changeInRankForServiceTransfer(oldSquadron.determineServiceForSquadron(campaign.getDate()), newSquadron.determineServiceForSquadron(campaign.getDate()));
        movePlayerToNewSquadron(newSquadron);
        TransferEvent transferEvent = createTransferEvent(leaveTimeForTransfer, oldSquadron, newSquadron);
		
        return transferEvent;
	}

	public void transferAI(SquadronMember squadronMember) throws PWCGException 
    {
        SquadronTransferFinder squadronTransferFinder = new SquadronTransferFinder(campaign, squadronMember);
        int newSquadronId = squadronTransferFinder.chooseSquadronForTransfer();
		SquadronPersonnel oldSquadronPersonnel = campaign.getPersonnelManager().getSquadronPersonnel(squadronMember.getSquadronId());
		SquadronPersonnel newSquadronPersonnel = campaign.getPersonnelManager().getSquadronPersonnel(newSquadronId);

		oldSquadronPersonnel.removeSquadronMember(squadronMember);
        squadronMember.setSquadronId(newSquadronId);
		newSquadronPersonnel.addSquadronMember(squadronMember);
    }

	private TransferEvent createTransferEvent(int leaveTimeForTransfer, Squadron oldSquad, Squadron newSquad) throws PWCGException
	{
		TransferEvent transferEvent = new TransferEvent(newSquad.getSquadronId());
        transferEvent.setPilotName(player.getNameAndRank());
        transferEvent.setDate(campaign.getDate());
        transferEvent.setSquadron(newSquad.determineDisplayName(campaign.getDate()));
        transferEvent.setTransferTo(newSquad.getSquadronId());
        transferEvent.setTransferFrom(oldSquad.getSquadronId());
        transferEvent.setLeaveTime(leaveTimeForTransfer);
		return transferEvent;
	}

	private void movePlayerToNewSquadron(Squadron newSquadron) throws PWCGException
	{        
		SquadronPersonnel oldSquadronPersonnel = campaign.getPersonnelManager().getSquadronPersonnel(player.getSquadronId());
		SquadronPersonnel newSquadronPersonnel = campaign.getPersonnelManager().getSquadronPersonnel(newSquadron.getSquadronId());

		oldSquadronPersonnel.removeSquadronMember(player);
		player.setSquadronId(newSquadron.getSquadronId());
		newSquadronPersonnel.addSquadronMember(player);
	}

	private int transferleaveTime() throws PWCGException 
	{
        int leaveTimeForTransfer = 5 + RandomNumberGenerator.getRandom(10);
        return leaveTimeForTransfer;
	}

    private void changeInRankForServiceTransfer(ArmedService oldService, ArmedService newService) throws PWCGException 
    {
        IRankHelper iRank = RankFactory.createRankHelper();        
        int rankPos = iRank.getRankPosByService(player.getRank(), oldService);
        
        int lowestRankPos = iRank.getLowestRankPosForService(newService);
        if (rankPos > lowestRankPos)
        {
            rankPos = lowestRankPos;
        }
        
        String newRank = iRank.getRankByService(rankPos, newService);
        player.setRank(newRank);
    }
}
