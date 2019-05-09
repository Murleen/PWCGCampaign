package pwcg.product.bos.map.prokhorovka;

import pwcg.mission.options.MapSeasonalParameters;
import pwcg.mission.options.MissionOptions;


public class ProkhorovkaMissionOptions extends MissionOptions
{
    public ProkhorovkaMissionOptions()
    {
        super();

    	makeWinter();
    	makeSpring();
    	makeSummer();
        makeAutumn();
    }

	private void makeWinter()
	{
		winter.setHeightMap("graphics\\LANDSCAPE_Prokhorovka\\height.hini");
    	winter.setTextureMap("graphics\\LANDSCAPE_Prokhorovka\\textures.tini");
    	winter.setForrestMap("graphics\\LANDSCAPE_Prokhorovka\\trees\\woods.wds");
    	winter.setGuiMap("prokhorovka");
    	winter.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
    	winter.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}

	private void makeSpring()
	{
		spring.setHeightMap("graphics\\LANDSCAPE_Prokhorovka\\height.hini");
		spring.setTextureMap("graphics\\LANDSCAPE_Prokhorovka\\textures.tini");
		spring.setForrestMap("graphics\\LANDSCAPE_Prokhorovka\\trees\\woods.wds");
    	spring.setGuiMap("prokhorovka");
    	spring.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
    	spring.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}

	private void makeSummer()
	{
		summer.setHeightMap("graphics\\LANDSCAPE_Prokhorovka\\height.hini");
		summer.setTextureMap("graphics\\LANDSCAPE_Prokhorovka\\textures.tini");
		summer.setForrestMap("graphics\\LANDSCAPE_Prokhorovka\\trees\\woods.wds");
    	summer.setGuiMap("prokhorovka");
    	summer.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
    	summer.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}

	private void makeAutumn()
	{
		autumn.setHeightMap("graphics\\LANDSCAPE_Prokhorovka\\height.hini");
		autumn.setTextureMap("graphics\\LANDSCAPE_Prokhorovka\\textures.tini");
		autumn.setForrestMap("graphics\\LANDSCAPE_Prokhorovka\\trees\\woods.wds");
		autumn.setGuiMap("prokhorovka");
    	autumn.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
    	autumn.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}
}
