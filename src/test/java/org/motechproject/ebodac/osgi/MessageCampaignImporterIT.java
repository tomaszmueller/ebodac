package org.motechproject.ebodac.osgi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.importer.MessageCampaignImporter;
import org.motechproject.messagecampaign.domain.campaign.CampaignRecord;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MessageCampaignImporterIT extends BasePaxIT {

    @Inject
    MessageCampaignService messageCampaignService;

    MessageCampaignImporter messageCampaignImporter;

    @Before
    public void cleanBefore() throws Exception {
        messageCampaignImporter = new MessageCampaignImporter();
        messageCampaignImporter.bind(messageCampaignService, null);
        for (CampaignRecord campaignRecord : messageCampaignService.getAllCampaignRecords())
        {
            messageCampaignService.deleteCampaign(campaignRecord.getName());
        }
    }

    @After
    public void cleanAfter() {
        for (CampaignRecord campaignRecord : messageCampaignService.getAllCampaignRecords())
        {
            messageCampaignService.deleteCampaign(campaignRecord.getName());
        }
    }

    @Test
    public void shouldImportMessageCampaignRecords(){
        List<CampaignRecord> campaignRecordList = messageCampaignService.getAllCampaignRecords();
        assertEquals(0, campaignRecordList.size());
        messageCampaignImporter.importMessageCampaigns(getClass().getResourceAsStream("/message-campaign.json"));
        campaignRecordList = messageCampaignService.getAllCampaignRecords();
        assertEquals(1, campaignRecordList.size());
        assertNotNull(messageCampaignService.getCampaignRecord("TestScreening"));
    }


}
