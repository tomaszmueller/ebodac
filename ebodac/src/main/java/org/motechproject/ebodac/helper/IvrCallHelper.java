package org.motechproject.ebodac.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.VotoLanguage;
import org.motechproject.ebodac.domain.VotoMessage;
import org.motechproject.ebodac.exception.EbodacInitiateCallException;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.VotoLanguageDataService;
import org.motechproject.ebodac.repository.VotoMessageDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ivr.service.OutboundCallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IvrCallHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(IvrCallHelper.class);

    @Autowired
    private ConfigService configService;

    @Autowired
    private VotoMessageDataService votoMessageDataService;

    @Autowired
    private VotoLanguageDataService votoLanguageDataService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private EnrollmentDataService enrollmentDataService;

    private OutboundCallService outboundCallService;

    public void initiateIvrCall(String campaignName, String messageKey, String externalId) {
        Config config = configService.getConfig();

        Subject subject = getSubject(externalId);
        if (config.getSendIvrCalls() != null && config.getSendIvrCalls() && checkIfCallsForThisStageAreEnabled(config, subject.getStageId())) {
            String votoLanguageId = getVotoLanguageId(subject.getLanguage(), externalId);
            String votoMessageId = getVotoMessageId(messageKey, externalId);

            JsonObject subscriber = new JsonObject();
            subscriber.addProperty(EbodacConstants.PHONE, subject.getPhoneNumber());
            subscriber.addProperty(EbodacConstants.LANGUAGE, votoLanguageId);

            JsonArray subscriberArray = new JsonArray();
            subscriberArray.add(subscriber);

            Gson gson = new GsonBuilder().serializeNulls().create();
            String subscribers = gson.toJson(subscriberArray);

            StringBuilder subjectIds = new StringBuilder(externalId);

            Enrollment enrollment = enrollmentDataService.findBySubjectIdAndCampaignName(externalId, campaignName);

            if (enrollment != null && enrollment.getDuplicatedEnrollments() != null) {
                for (Enrollment e : enrollment.getDuplicatedEnrollments()) {
                    if (EnrollmentStatus.ENROLLED.equals(e.getStatus())) {
                        subjectIds.append(",");
                        subjectIds.append(e.getExternalId());
                    }
                }
            }

            Map<String, String> callParams = new HashMap<>();
            callParams.put(EbodacConstants.API_KEY, config.getApiKey());
            callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
            callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
            callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
            callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, config.getSendSmsIfVoiceFails() ? "1" : "0");
            callParams.put(EbodacConstants.DETECT_VOICEMAIL, config.getDetectVoiceMail() ? "1" : "0");
            callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
            callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
            callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
            callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds.toString());
            callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subject.getPhoneNumber());

            LOGGER.info("Initiating call: {}", callParams.toString());

            outboundCallService.initiateCall(config.getIvrSettingsName(), callParams);
        }
    }

    private boolean checkIfCallsForThisStageAreEnabled(Config config, Long stageId) {
        if (StringUtils.isBlank(config.getDisabledIvrCallsForStages())) {
            return true;
        }
        String stagesWithoutSpaces = config.getDisabledIvrCallsForStages().replaceAll("\\s+", "");
        String[] ignoredCalls = stagesWithoutSpaces.split(",");
        for (int i = 0; i < ignoredCalls.length; i++) {
            String numberAsString = ignoredCalls[i];
            if (StringUtils.isNotBlank(numberAsString) && Long.valueOf(numberAsString).equals(stageId)) {
                return false;
            }
        }
        return true;
    }

    private Subject getSubject(String subjectId) {
        Subject subject = subjectService.findSubjectBySubjectId(subjectId);
        if (subject == null) {
            throw new EbodacInitiateCallException("Cannot initiate call, because Provider with id: %s not found", "", subjectId);
        }

        if (subject.getLanguage() == null) {
            throw new EbodacInitiateCallException("Cannot initiate call for Provider with id: %s, because provider Language is null", "",
                    subjectId);
        }

        return subject;
    }

    private String getVotoLanguageId(Language language, String subjectId) {
        VotoLanguage votoLanguage = votoLanguageDataService.findByLanguage(language);
        if (votoLanguage == null) {
            throw new EbodacInitiateCallException("Cannot initiate call for Provider with id: %s, because Voto Language for language: %s not found", "",
                    subjectId, language.toString());
        }

        return votoLanguage.getVotoId();
    }

    private String getVotoMessageId(String messageKey, String subjectId) {
        VotoMessage votoMessage = votoMessageDataService.findByMessageKey(messageKey);
        if (votoMessage == null) {
            throw new EbodacInitiateCallException("Cannot initiate call for Provider with id: %s, because Voto Message with key: %s not found", "",
                    subjectId, messageKey);
        }

        return votoMessage.getVotoIvrId();
    }

    @Autowired
    public void setOutboundCallService(OutboundCallService outboundCallService) {
        this.outboundCallService = outboundCallService;
    }
}
