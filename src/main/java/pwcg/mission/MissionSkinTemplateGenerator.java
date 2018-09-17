package pwcg.mission;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.skin.Skin;
import pwcg.campaign.skin.SkinTemplate;
import pwcg.campaign.skin.SkinsForPlane;
import pwcg.campaign.skin.SkinTemplate.SkinTemplateInstance;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.Logger;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.plane.PlaneMcu;

public class MissionSkinTemplateGenerator {
    private int progress;

    public void generateSkins(Campaign campaign, List<IFlight> list) throws PWCGException
    {
        List<SkinTemplateInstance> skinsToGenerate = new ArrayList<>();

        for (IFlight flight : list)
        {
            for (PlaneMcu plane : flight.getFlightPlanes().getPlanes())
            {
                SkinsForPlane skinsForPlane = PWCGContext.getInstance().getSkinManager().getSkinsForPlane(plane.getType());
                Skin skin = plane.getPlaneSkin();
                if (skin != null && skin.getTemplate() != null)
                {
                    SkinTemplate template = skinsForPlane.getTemplate(plane.getPlaneSkin().getTemplate());
                    if (template == null)
                        continue;
                    SkinTemplateInstance instance = template.instantiate(campaign, plane, skin.getOverrides());
                    if (!instance.skinExists())
                        skinsToGenerate.add(instance);

                    Skin renderedSkin = new Skin();
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
