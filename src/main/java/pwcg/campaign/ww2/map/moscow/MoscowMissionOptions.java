package pwcg.campaign.ww2.map.moscow;

import pwcg.mission.options.MapSeasonalParameters;
import pwcg.mission.options.MissionOptions;

public class MoscowMissionOptions extends MissionOptions
{
	
    public MoscowMissionOptions()
    {
        super();

    	makeWinter();
    	makeSpring();
    	makeSummer();
        makeAutumn();
    }

	private void makeWinter()
	{
		winter.setHeightMap("graphics\\LANDSCAPE_Moscow_w\\height.hini");
    	winter.setTextureMap("graphics\\LANDSCAPE_Moscow_w\\textures.tini");
    	winter.setForrestMap("graphics\\LANDSCAPE_Moscow_w\\trees\\woods.wds");
    	winter.setGuiMap("moscow-winter");
    	winter.setSeasonAbbreviation(MapSeasonalParameters.WINTER_ABREV);
    	winter.setSeason(MapSeasonalParameters.WINTER_STRING);
	}

	private void makeSpring()
	{
		spring.setHeightMap("graphics\\LANDSCAPE_Moscow_a\\height.hini");
		spring.setTextureMap("graphics\\LANDSCAPE_Moscow_a\\textures.tini");
    	spring.setForrestMap("graphics\\LANDSCAPE_Moscow_a\\trees\\woods.wds");
    	spring.setGuiMap("moscow-autumn");
    	spring.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
    	spring.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}

	private void makeSummer()
	{
		summer.setHeightMap("graphics\\LANDSCAPE_Moscow_a\\height.hini");
		summer.setTextureMap("graphics\\LANDSCAPE_Moscow_a\\textures.tini");
		summer.setForrestMap("graphics\\LANDSCAPE_Moscow_a\\trees\\woods.wds");
    	summer.setGuiMap("moscow-autumn");
    	summer.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
    	summer.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}

	private void makeAutumn()
	{
		autumn.setHeightMap("graphics\\LANDSCAPE_Moscow_a\\height.hini");
		autumn.setTextureMap("graphics\\LANDSCAPE_Moscow_a\\textures.tini");
		autumn.setForrestMap("graphics\\LANDSCAPE_Moscow_a\\trees\\woods.wds");
		autumn.setGuiMap("moscow-autumn");
		autumn.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
		autumn.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}
}
