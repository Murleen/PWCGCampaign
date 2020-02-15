package pwcg.mission;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.factory.RankFactory;
import pwcg.campaign.skin.Skin;
import pwcg.campaign.skin.SkinTemplate;
import pwcg.campaign.skin.SkinsForPlane;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.skin.SkinTemplate.SkinTemplateInstance;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerGlobal;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.Logger;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.options.MapSeasonalParameters.Season;

public class MissionSkinTemplateGenerator {
    private int progress;

    public void generateSkins(Campaign campaign, List<IFlight> list) throws PWCGException
    {
        List<SkinTemplateInstance> skinsToGenerate = new ArrayList<>();
        int generateSkinLimit = ConfigManagerGlobal.getInstance().getIntConfigParam(ConfigItemKeys.GenerateSkinLimitKey);

        for (IFlight flight : list)
        {
            for (PlaneMcu plane : flight.getFlightPlanes().getPlanes())
            {
                SkinsForPlane skinsForPlane = PWCGContext.getInstance().getSkinManager().getSkinsForPlane(plane.getType());
                Skin skin = plane.getPlaneSkin();
                if (skin != null && skin.getTemplate() != null && (generateSkinLimit == 0 || skinsToGenerate.size() < generateSkinLimit))
                {
                    SkinTemplate template = skinsForPlane.getTemplate(plane.getPlaneSkin().getTemplate());
                    if (template == null)
                        continue;

                    Map<String, Object> params = new HashMap<>();
                    Squadron squadron = PWCGContext.getInstance().getSquadronManager().getSquadron(plane.getSquadronId());
                    Date date = campaign.getDate();
                    params.put("UNIT_ID_CODE", squadron.determineUnitIdCode(date));
                    params.put("SUB_UNIT_ID_CODE", squadron.determineSubUnitIdCode(date));
                    params.put("AIRCRAFT_ID_CODE", plane.getAircraftIdCode());
                    Season season = PWCGContext.getInstance().getCurrentMap().getMapWeather().getSeason(date);
                    params.put("WINTER", (season == Season.WINTER) ? 1 : 0);
                    SquadronMember pilot = plane.getPilot();
                    params.put("PILOT_RANK", RankFactory.createRankHelper().getRankPosByService(pilot.getRank(), pilot.determineService(date)));
                    params.put("PILOT_NAME_RANK", pilot.getNameAndRank());
                    params.put("PILOT_NAME_RANK_UC", pilot.getNameAndRank().toUpperCase());
                    int stripes = date.before(DateUtils.getDateYYYYMMDD("19440606")) ? 0 :
                                  date.before(DateUtils.getDateYYYYMMDD("19440706")) ? 2 :
                                  date.before(DateUtils.getDateYYYYMMDD("19450101")) ? 1 :
                                                                                       0;
                    params.put("STRIPES", stripes);

                    params.putAll(skin.getOverrides());
                    SkinTemplateInstance instance = template.instantiate(params);
                    if (!instance.skinExists()) {
                        System.out.println("Need to generate skin");
                        skinsToGenerate.add(instance);
                    }

                    Skin renderedSkin = new Skin();
                    System.out.println("Using skin: " + plane.getType() + " " + instance.getFilename());
                    renderedSkin.setSkinName(instance.getFilename());
                    plane.setPlaneSkin(renderedSkin);
                }
            }
        }

        if (!skinsToGenerate.isEmpty())
        {
            JDialog progressDialog = new JDialog((Frame)null, "Generating skins", true);
            JProgressBar progressBar = new JProgressBar(0, skinsToGenerate.size());

            progressDialog.add(new JLabel("Generating skins, please wait..."), BorderLayout.NORTH);

            progressDialog.add(progressBar, BorderLayout.CENTER);
            progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            progressDialog.setSize(300, 90);
            progressDialog.setLocationRelativeTo(null);

            progressBar.setStringPainted(true);

            progress = 0;

            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (SkinTemplateInstance instance : skinsToGenerate)
            {
                SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        System.out.println("Rendering " + instance.getFilename());
                        instance.generate();

                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        }
                        catch (ExecutionException e)
                        {
                            Logger.logException(e.getCause());
                        }
                        catch (InterruptedException e) {
                            Logger.logException(e);
                        }

                        progress++;
                        progressBar.setValue(progress);
                        if (progress == skinsToGenerate.size())
                            progressDialog.dispose();
                    }
                };

                executorService.submit(sw);
            }

            if (progress < skinsToGenerate.size())
                progressDialog.setVisible(true);

            executorService.shutdown();
        }
    }

}
