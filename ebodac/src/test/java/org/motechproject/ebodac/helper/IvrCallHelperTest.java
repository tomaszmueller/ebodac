package org.motechproject.ebodac.helper;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicStatusLine;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.SubjectAgeRange;
import org.motechproject.ebodac.domain.enums.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.VotoLanguage;
import org.motechproject.ebodac.domain.VotoMessage;
import org.motechproject.ebodac.exception.EbodacInitiateCallException;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.VotoLanguageDataService;
import org.motechproject.ebodac.repository.VotoMessageDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.ivr.domain.HttpMethod;
import org.motechproject.ivr.repository.CallDetailRecordDataService;
import org.motechproject.ivr.service.OutboundCallService;
import org.motechproject.ivr.service.impl.OutboundCallServiceImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@PowerMockIgnore("org.apache.log4j.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({IvrCallHelper.class, OutboundCallServiceImpl.class })
public class IvrCallHelperTest {

    private static final int RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY = 15;

    @InjectMocks
    private IvrCallHelper ivrCallHelper = new IvrCallHelper();

    @Mock
    private ConfigService configService;

    @Mock
    private VotoMessageDataService votoMessageDataService;

    @Mock
    private VotoLanguageDataService votoLanguageDataService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private EnrollmentDataService enrollmentDataService;

    @Mock
    private OutboundCallService outboundCallService;

    @Mock
    private org.motechproject.ivr.service.ConfigService ivrConfigService;

    @Mock
    private StatusMessageService statusMessageService;

    @Mock
    private CallDetailRecordDataService callDetailRecordDataService;

    @Mock
    private EventRelay eventRelay;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldSetOnlyParentEnrollmentProviderIdIfThereAreNoDuplicatedEnrollments() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(3);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);
    }

    @Test
    public void shouldAddProvidersIdsForAllDuplicatedEnrollments() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1,2,3,4,5";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment parent = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);

        Set<Enrollment> enrollments = new LinkedHashSet<>();

        Enrollment enrollment = new Enrollment("2", campaignName, LocalDate.now(), 1L);
        enrollment.setParentEnrollment(parent);
        enrollments.add(enrollment);

        enrollment = new Enrollment("3", campaignName, LocalDate.now(), 1L);
        enrollment.setParentEnrollment(parent);
        enrollments.add(enrollment);

        enrollment = new Enrollment("4", campaignName, LocalDate.now(), 1L);
        enrollment.setParentEnrollment(parent);
        enrollments.add(enrollment);

        enrollment = new Enrollment("5", campaignName, LocalDate.now(), 1L);
        enrollment.setParentEnrollment(parent);
        enrollments.add(enrollment);

        parent.setDuplicatedEnrollments(enrollments);

        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(parent);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(RETRY_ATTEMPTS);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);
    }

    @Test
    public void shouldSetSendSmsIfVoiceFailsAndDetectVoiceMailParams() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(RETRY_ATTEMPTS);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);

        config.setSendSmsIfVoiceFails(false);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "0");

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);

        config.setDetectVoiceMail(false);
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "0");

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);
    }

    @Test
    public void shouldNotSendIvrCallWhenSendIvrCallsIsFalse() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(false);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(RETRY_ATTEMPTS);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(outboundCallService, never()).initiateCall(config.getIvrSettingsName(), callParams);
    }

    @Test
    public void shouldNotSendIvrCallWhenDisabledIvrCallsForStagesContainsSubjectStageId() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        subject.setStageId(4L);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setDisabledIvrCallsForStages("1,2, 4");
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(RETRY_ATTEMPTS);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(outboundCallService, never()).initiateCall(config.getIvrSettingsName(), callParams);

    }

    @Test
    public void shouldSendIvrCallWhenDisabledIvrCallsForStagesIsEmpty() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        subject.setStageId(4L);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setDisabledIvrCallsForStages(null);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(RETRY_ATTEMPTS);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);

    }

    @Test(expected = EbodacInitiateCallException.class)
    public void shouldThrowExceptionWhenSubjectNotFound() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String votoMessageId = "456";
        String votoLanguageId = "789";

        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(null);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(Language.English)).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(3);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
    }

    @Test(expected = EbodacInitiateCallException.class)
    public void shouldThrowExceptionWhenVotoLanguageNotFound() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(null);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(3);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
    }

    @Test(expected = EbodacInitiateCallException.class)
    public void shouldThrowExceptionWhenVotoMessageNotFound() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoLanguageId = "789";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(null);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(3);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldSendProperRequestToVoto() throws Exception {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(RETRY_ATTEMPTS);
        config.setRetryDelay(RETRY_DELAY);
        when(configService.getConfig()).thenReturn(config);

        org.motechproject.ivr.domain.Config ivrConfig = new org.motechproject.ivr.domain.Config("Voto", false, null, null, null, null, null, null, HttpMethod.POST, true, "votoUrl", false, null);
        when(ivrConfigService.hasConfig("Voto")).thenReturn(true);
        when(ivrConfigService.getConfig("Voto")).thenReturn(ivrConfig);

        org.apache.http.impl.client.DefaultHttpClient client = mock(org.apache.http.impl.client.DefaultHttpClient.class);
        PowerMockito.whenNew(org.apache.http.impl.client.DefaultHttpClient.class).withNoArguments().thenReturn(client);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, ""));

        ArgumentCaptor<HttpUriRequest> requestCaptor = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(client.execute(requestCaptor.capture())).thenReturn(response);

        outboundCallService = new OutboundCallServiceImpl(ivrConfigService, statusMessageService, callDetailRecordDataService, eventRelay);
        ivrCallHelper.setOutboundCallService(outboundCallService);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        assertEquals(1, requestCaptor.getAllValues().size());
        HttpUriRequest request = requestCaptor.getValue();

        assertEquals("POST", request.getMethod());
        assertEquals(0, request.getAllHeaders().length);

        HttpEntity entity = ((HttpPost) request).getEntity();

        Header header = entity.getContentType();

        assertEquals("Content-Type", header.getName());
        assertEquals("application/json; charset=UTF-8", header.getValue());

        InputStream inputStream = entity.getContent();
        String json = IOUtils.toString(inputStream);
        inputStream.close();

        Map<String, Object> params = new ObjectMapper().readValue(json, new TypeReference<HashMap>() {}); //NO CHECKSTYLE WhitespaceAround
        assertEquals(subjectPhoneNumber, params.get("subscriber_phone").toString());
        assertEquals("3", params.get("retry_attempts_short").toString());
        assertEquals("1", params.get("detect_voicemail_action").toString());
        assertEquals("1", params.get("subject_ids").toString());
        assertEquals("apiKey", params.get("api_key").toString());
        assertEquals(votoMessageId, params.get("message_id").toString());
        assertEquals("url", params.get("status_callback_url").toString());
        assertEquals("15", params.get("retry_delay_short").toString());
        assertEquals("1", params.get("retry_attempts_long").toString());
        assertEquals("1", params.get("send_sms_if_voice_fails").toString());
        List subscribers = (List) params.get("subscribers");
        assertEquals(1, subscribers.size());
        Map subscriber = (Map) subscribers.get(0);
        assertEquals(subjectPhoneNumber, subscriber.get("phone").toString());
        assertEquals(votoLanguageId, subscriber.get("language").toString());
    }

    @Test
    public void shouldAddAgeRangeMessageCodeIfAgeRangeSpecifiedAndParticipantIsInThisRange() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        subject.setDateOfBirth(LocalDate.now().minusYears(5));
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(RETRY_ATTEMPTS);
        config.setRetryDelay(RETRY_DELAY);
        config.setSubjectAgeRangeList(Collections.singletonList(new SubjectAgeRange(2, 17, 1L)));
        when(configService.getConfig()).thenReturn(config);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey + "-age:2-17")).thenReturn(votoMessage);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(votoMessageDataService, times(1)).findByMessageKey(messageKey + "-age:2-17");
    }

    @Test
    public void shouldNotAddAgeRangeMessageCodeIfParticipantIsNotInThisRange() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        subject.setDateOfBirth(LocalDate.now().minusYears(40));
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        Enrollment enrollment = new Enrollment(externalId, campaignName, LocalDate.now(), 1L);
        when(enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(RETRY_ATTEMPTS);
        config.setRetryDelay(RETRY_DELAY);
        config.setSubjectAgeRangeList(Collections.singletonList(new SubjectAgeRange(2, 17, 1L)));
        when(configService.getConfig()).thenReturn(config);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findByMessageKey(messageKey)).thenReturn(votoMessage);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(votoMessageDataService, times(1)).findByMessageKey(messageKey);
    }
}
