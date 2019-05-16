package pwcg.product.bos.map.velikieluki;

import pwcg.mission.options.MapSeasonalParameters;
import pwcg.mission.options.MissionOptions;


public class VelikieLukiMissionOptions extends MissionOptions
{
    public VelikieLukiMissionOptions()
    {
        super();

    	makeWinter();
    	makeSpring();
    	makeSummer();
        makeAutumn();
    }

	private void makeWinter()
	{
		winter.setHeightMap("graphics\\LANDSCAPE_velikie_luki\\height.hini");
    	winter.setTextureMap("graphics\\LANDSCAPE_velikie_luki\\textures.tini");
    	winter.setForrestMap("graphics\\LANDSCAPE_velikie_luki\\trees\\woods.wds");
    	winter.setGuiMap("vluki");
    	winter.setSeasonAbbreviation(MapSeasonalParameters.WINTER_ABREV);
    	winter.setSeason(MapSeasonalParameters.WINTER_STRING);
	}

	private void makeSpring()
	{
        spring.setHeightMap("graphics\\LANDSCAPE_velikie_luki\\height.hini");
        spring.setTextureMap("graphics\\LANDSCAPE_velikie_luki\\textures.tini");
        spring.setForrestMap("graphics\\LANDSCAPE_velikie_luki\\trees\\woods.wds");
        spring.setGuiMap("vluki");
        spring.setSeasonAbbreviation(MapSeasonalParameters.WINTER_ABREV);
        spring.setSeason(MapSeasonalParameters.WINTER_STRING);
	}

	private void makeSummer()
	{
        summer.setHeightMap("graphics\\LANDSCAPE_velikie_luki\\height.hini");
        summer.setTextureMap("graphics\\LANDSCAPE_velikie_luki\\textures.tini");
        summer.setForrestMap("graphics\\LANDSCAPE_velikie_luki\\trees\\woods.wds");
        summer.setGuiMap("vluki");
        summer.setSeasonAbbreviation(MapSeasonalParameters.WINTER_ABREV);
        summer.setSeason(MapSeasonalParameters.WINTER_STRING);
	}

	private void makeAutumn()
	{
        autumn.setHeightMap("graphics\\LANDSCAPE_velikie_luki\\height.hini");
        autumn.setTextureMap("graphics\\LANDSCAPE_velikie_luki\\textures.tini");
        autumn.setForrestMap("graphics\\LANDSCAPE_velikie_luki\\trees\\woods.wds");
        autumn.setGuiMap("vluki");
        autumn.setSeasonAbbreviation(MapSeasonalParameters.WINTER_ABREV);
        autumn.setSeason(MapSeasonalParameters.WINTER_STRING);
	}
}
