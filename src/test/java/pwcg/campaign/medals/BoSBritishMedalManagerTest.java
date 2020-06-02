package pwcg.campaign.medals;

import java.util.Date;

import javax.swing.ImageIcon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import pwcg.campaign.api.ICountry;
import pwcg.campaign.context.Country;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.factory.ArmedServiceFactory;
import pwcg.campaign.factory.CountryFactory;
import pwcg.campaign.factory.MedalManagerFactory;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.gui.image.ImageIconCache;
import pwcg.gui.utils.ContextSpecificImages;
import pwcg.product.bos.country.BoSServiceManager;
import pwcg.product.bos.medals.BritishMedalManager;

@RunWith(MockitoJUnitRunner.Silent.class) 
public class BoSBritishMedalManagerTest extends MedalManagerTestBase
{
	
    @Before
    public void setup() throws PWCGException
    {
    	PWCGContext.setProduct(PWCGProduct.BOS);
        super.setup();
        ICountry country = CountryFactory.makeCountryByCountry(Country.BRITAIN);
        medalManager = MedalManagerFactory.createMedalManager(country, campaign);
        medals.clear();
    }

    @Test
    public void testBritishMedals () throws PWCGException
    {               
        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19441001"));
        service = ArmedServiceFactory.createServiceManager().getArmedServiceById(BoSServiceManager.RAF, campaign.getDate());
        Mockito.when(player.determineService(ArgumentMatchers.<Date>any())).thenReturn(service);

        awardMedal(BritishMedalManager.PILOTS_BADGE, 0, 0);
        awardMedal(BritishMedalManager.DFC, 5, 1);
        awardMedal(BritishMedalManager.DFC_BAR_1, 10, 1);
        awardMedal(BritishMedalManager.DSO, 15, 1);
        awardMedal(BritishMedalManager.DSO_BAR, 25, 1);
        awardMedal(BritishMedalManager.VC, 40, 3);
    }

    @Test
    public void testBritishMedalsAlternateVCAward () throws PWCGException
    {               
        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19441001"));
        service = ArmedServiceFactory.createServiceManager().getArmedServiceById(BoSServiceManager.RAF, campaign.getDate());
        Mockito.when(player.determineService(ArgumentMatchers.<Date>any())).thenReturn(service);

        awardMedal(BritishMedalManager.PILOTS_BADGE, 0, 0);
        awardMedal(BritishMedalManager.DFC, 5, 1);
        awardMedal(BritishMedalManager.DFC_BAR_1, 10, 1);
        awardMedal(BritishMedalManager.DSO, 15, 1);
        awardMedal(BritishMedalManager.DSO_BAR, 25, 1);
        awardMedal(BritishMedalManager.VC, 30, 5);
    }

    @Test
    public void testVCFail () throws PWCGException
    {            
        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19441001"));
        service = ArmedServiceFactory.createServiceManager().getArmedServiceById(BoSServiceManager.RAF, campaign.getDate());
        Mockito.when(player.determineService(ArgumentMatchers.<Date>any())).thenReturn(service);

        awardMedal(BritishMedalManager.PILOTS_BADGE, 0, 0);
        awardMedal(BritishMedalManager.DFC, 5, 1);
        awardMedal(BritishMedalManager.DFC_BAR_1, 10, 1);
        awardMedal(BritishMedalManager.DSO, 15, 1);
        awardMedal(BritishMedalManager.DSO_BAR, 25, 1);

    	makeVictories(29);
        Medal medal = medalManager.award(campaign, player, service, 5);
        assert (medal == null);
    }

    @Test
    public void testImages () throws PWCGException
    {            
    	for (Medal medal : medalManager.getAllAwardsForService())
    	{
	        String medalPath = ContextSpecificImages.imagesMedals() + "Allied\\" + medal.getMedalImage();
	        System.out.println(medalPath);
	        ImageIcon medalIcon = ImageIconCache.getInstance().getImageIcon(medalPath);
	        assert (medalIcon != null);
    	}
    }
}
