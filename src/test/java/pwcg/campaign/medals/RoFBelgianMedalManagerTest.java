package pwcg.campaign.medals;

import java.util.Date;

import javax.swing.ImageIcon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pwcg.campaign.context.Country;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.factory.ArmedServiceFactory;
import pwcg.campaign.factory.MedalManagerFactory;
import pwcg.campaign.ww1.country.RoFServiceManager;
import pwcg.campaign.ww1.medals.BelgianMedalManager;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.gui.dialogs.ImageCache;
import pwcg.gui.utils.ContextSpecificImages;

@RunWith(MockitoJUnitRunner.class)
public class RoFBelgianMedalManagerTest extends MedalManagerTestBase
{
    @Before
    public void setup() throws PWCGException
    {
        PWCGContextManager.setRoF(true);
        super.setup();
        Mockito.when(country.isCountry(Country.BELGIUM)).thenReturn(true);
        medalManager = MedalManagerFactory.createMedalManager(campaign);
        medals.clear();
    }
    
    @Test
    public void testBelgianMedals () throws PWCGException
    {            	
        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19180801"));
	    service = ArmedServiceFactory.createServiceManager().getArmedServiceById(RoFServiceManager.AVIATION_MILITAIRE_BELGE, campaign.getDate());
        Mockito.when(player.determineService(Matchers.<Date>any())).thenReturn(service);

        awardMedal(BelgianMedalManager.PILOTS_BADGE, 0, 0);
		awardMedal(BelgianMedalManager.MILITARY_MEDAL, 2, 1);
		awardMedal(BelgianMedalManager.BEL_CROIX_DE_GUERRE, 3, 1);
		awardMedal(BelgianMedalManager.CROIX_DE_GUERRE, 5, 1);
		awardMedal(BelgianMedalManager.CROIX_DE_GUERRE_BRONZE_STAR, 7, 1);
		awardMedal(BelgianMedalManager.BEL_ORDRE_DE_LA_COURONNE, 8, 1);
		awardMedal(BelgianMedalManager.MEDAILLE_DE_HONNEUR, 10, 1);
		awardMedal(BelgianMedalManager.BEL_ORDRE_DE_LEOPOLD, 12, 1);
		awardMedal(BelgianMedalManager.LEGION_DE_HONNEUR, 30, 1);
    }

    @Test
    public void testMedalFail () throws PWCGException
    {            
        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19180801"));
	    service = ArmedServiceFactory.createServiceManager().getArmedServiceById(RoFServiceManager.USAS, campaign.getDate());
        Mockito.when(player.determineService(Matchers.<Date>any())).thenReturn(service);

        awardMedal(BelgianMedalManager.PILOTS_BADGE, 0, 0);
		awardMedal(BelgianMedalManager.MILITARY_MEDAL, 2, 1);
		awardMedal(BelgianMedalManager.BEL_CROIX_DE_GUERRE, 3, 1);
		awardMedal(BelgianMedalManager.CROIX_DE_GUERRE, 5, 1);
		awardMedal(BelgianMedalManager.CROIX_DE_GUERRE_BRONZE_STAR, 7, 1);
		awardMedal(BelgianMedalManager.BEL_ORDRE_DE_LA_COURONNE, 8, 1);
		awardMedal(BelgianMedalManager.MEDAILLE_DE_HONNEUR, 10, 1);
		awardMedal(BelgianMedalManager.BEL_ORDRE_DE_LEOPOLD, 12, 1);

    	makeVictories(29);
        Medal medal = medalManager.award(campaign, player, service, 2);
        assert (medal == null);
    }

    @Test
    public void testImages () throws PWCGException
    {            
    	for (Medal medal : medalManager.getAllAwardsForService())
    	{
	        String medalPath = ContextSpecificImages.imagesMedals() + "Allied\\" + medal.getMedalImage();
	        ImageIcon medalIcon = ImageCache.getInstance().getImageIcon(medalPath);
	        assert (medalIcon != null);
    	}
    }
}
