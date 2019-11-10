package pwcg.product.bos.map.stalingrad;

import java.util.Calendar;
import java.util.Date;

import pwcg.mission.options.MapSeasonalParameters.Season;
import pwcg.product.bos.map.BoSMapWeatherBase;


public class StalingradMapWeather extends BoSMapWeatherBase
{
	public StalingradMapWeather()
	{
	    super();
	}	

    @Override
    public Season getSeason(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;

        Season season = Season.SUMMER;
        if (month == 11 || month == 12 || month == 1 || month == 2 || month == 3)
        {
            season = Season.WINTER;
        }
        else if (month == 4 | month == 5)
        {
            season = Season.SPRING;
        }
        else if (month == 6 || month == 7 || month == 8)
        {
            season = Season.SUMMER;
        }
        else if (month == 9 || month == 10)
        {
            season = Season.AUTUMN;
        }

        return season;
    }
}
