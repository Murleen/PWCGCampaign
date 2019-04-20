package pwcg.aar.integration;

import java.util.HashMap;
import java.util.Map;

import pwcg.aar.inmission.phase3.reconcile.victories.singleplayer.PlayerDeclarations;
import pwcg.aar.inmission.phase3.reconcile.victories.singleplayer.PlayerVictoryDeclaration;
import pwcg.campaign.squadmember.SquadronMember;

public class PlayerDeclarationsBuilder
{    
    public PlayerDeclarationsBuilder ()
    {
    }
    
    public Map<Integer, PlayerDeclarations> makePlayerDeclarations(SquadronMember player)
    {
        Map<Integer, PlayerDeclarations> playerDeclarations = new HashMap<>();
        PlayerDeclarations playerDeclarationSet = new PlayerDeclarations();
        PlayerVictoryDeclaration victoryDeclaration1 = new PlayerVictoryDeclaration();
        victoryDeclaration1.setAircraftType("yak1s69");

        PlayerVictoryDeclaration victoryDeclaration2 = new PlayerVictoryDeclaration();
        victoryDeclaration2.setAircraftType("il2m41");
        
        playerDeclarationSet.addDeclaration(victoryDeclaration1);
        playerDeclarationSet.addDeclaration(victoryDeclaration2);

        playerDeclarations.put(player.getSerialNumber(), playerDeclarationSet);
        return playerDeclarations;
    }
}
