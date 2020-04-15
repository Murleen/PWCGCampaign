package pwcg.campaign.io.json;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import pwcg.core.config.ConfigSet;
import pwcg.core.exception.PWCGException;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationIOJsonTest
{
    @Test
    public void readJsonFCTest() throws PWCGException
    {
        String path = System.getProperty("user.dir") + "\\FCData\\input\\Configuration\\";
        Map<String, ConfigSet> configSet = ConfigurationIOJson.readJson(path);
        assert (configSet.size() > 0);
    }

    @Test
    public void readJsonBoSTest() throws PWCGException
    {
        String path = System.getProperty("user.dir") + "\\BoSData\\input\\Configuration\\";
        Map<String, ConfigSet> configSet = ConfigurationIOJson.readJson(path);
        assert (configSet.size() > 0);
    }
}
