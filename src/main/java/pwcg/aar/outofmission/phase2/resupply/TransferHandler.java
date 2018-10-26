package pwcg.aar.outofmission.phase2.resupply;

import pwcg.aar.outofmission.phase2.resupply.SquadronNeedFactory.SquadronNeedType;
import pwcg.campaign.ArmedService;
import pwcg.campaign.Campaign;
import pwcg.campaign.personnel.PersonnelReplacementsService;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;

public class TransferHandler
{
    private Campaign campaign;
    private ResupplyNeedBuilder transferNeedBuilder;

    private SquadronTransferData squadronTransferData = new SquadronTransferData();
    
    public TransferHandler(Campaign campaign, ResupplyNeedBuilder transferNeedBuilder)
    {
        this.campaign = campaign;
        this.transferNeedBuilder = transferNeedBuilder;
    }
    
    public SquadronTransferData determineSquadronMemberTransfers(ArmedService armedService) throws PWCGException
    {
        ServiceResupplyNeed serviceTransferNeed = transferNeedBuilder.determineNeedForService(SquadronNeedType.PERSONNEL);
        PersonnelReplacementsService serviceReplacements =  campaign.getPersonnelManager().getPersonnelReplacementsService(armedService.getServiceId());
        replaceForService(serviceTransferNeed, serviceReplacements);
        return squadronTransferData;
    }

    private void replaceForService(ServiceResupplyNeed serviceTransferNeed, PersonnelReplacementsService serviceReplacements) throws PWCGException
    {
        while (serviceTransferNeed.hasNeedySquadron())
        {
            ISquadronNeed selectedSquadronNeed = serviceTransferNeed.chooseNeedySquadron();
            if (serviceReplacements.hasReplacements())
            {
                SquadronMember replacement = serviceReplacements.useReplacement();        
                TransferRecord transferRecord = new TransferRecord(replacement, Squadron.REPLACEMENT, selectedSquadronNeed.getSquadronId());
                squadronTransferData.addTransferRecord(transferRecord);
                serviceTransferNeed.noteResupply(selectedSquadronNeed);
            }
            else
            {
                break;
            }
        }        
    }
}
