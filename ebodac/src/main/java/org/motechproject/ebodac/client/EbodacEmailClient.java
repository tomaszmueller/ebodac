package org.motechproject.ebodac.client;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Component
public class EbodacEmailClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEmailClient.class);

    private static final String FETCHED_EMAIL_FLAG = "fetched";
    private static final String JOB_SUCCESS_STATUS = "COMPLETION";
    private static final String JOB_FAILURE_STATUS = "FAILURE";

    public Boolean hasNewJobCompletionMessage(String host, String user, String password) {
        LOGGER.info("Started checking for job completion emails");
        Properties properties = new Properties();

        Session session = Session.getDefaultInstance(properties);

        Boolean jobCompletion = false;
        try {
            Store store = session.getStore("imap");

            store.connect(host, user, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            Flags fetched = new Flags(FETCHED_EMAIL_FLAG);
            FlagTerm notFetched = new FlagTerm(fetched, false);
            Message[] messages = emailFolder.search(notFetched);

            for (Message message : messages) {
                String subject = message.getSubject();
                if (subject.contains(JOB_FAILURE_STATUS)) {
                    try {
                        LOGGER.error("Job failure, message content:\n" + message.getContent());
                    } catch (IOException e) {
                        LOGGER.error("Job failure, could not get the message content: " + subject);
                    }
                } else if (subject.contains(JOB_SUCCESS_STATUS)) {
                    LOGGER.info("New job completion message: " + subject);
                    jobCompletion = true;
                } else {
                    LOGGER.error("Message subject did not contain job status: " + subject);
                }
            }
            emailFolder.setFlags(messages, fetched, true);
            emailFolder.close(false);

            store.close();
        } catch (NoSuchProviderException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (MessagingException e) {
            LOGGER.error("Exception occurred when fetching new emails: " + e.getMessage(), e);
        }

        LOGGER.info("Finished checking for job completion emails, result: " + jobCompletion);
        return jobCompletion;
    }

    public void sendNewMessage(String host, final String user, final String password, Integer port, String subject, //NO CHECKSTYLE ParameterNumber
                                  List<String> recipients, String content, DataSource source, String fileName) {
        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException("Email host is empty");
        }

        if (StringUtils.isBlank(user) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Sender name or password is empty");
        }

        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("Recipients list is empty");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setSubject(subject);

            for (String to : recipients) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }

            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(content);
            multipart.addBodyPart(messageBodyPart);

            if (source != null) {
                messageBodyPart = new MimeBodyPart();
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(fileName);
                multipart.addBodyPart(messageBodyPart);
            }

            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Exception occurred when sending email: " + e.getMessage(), e);
        }
    }
}
