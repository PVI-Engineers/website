package com.pviengineers.careersapi.service;

import com.pviengineers.careersapi.dto.ContactInquiryRequest;
import com.pviengineers.careersapi.dto.ContactInquiryResponse;
import com.pviengineers.careersapi.model.ContactInquiry;
import com.pviengineers.careersapi.repository.ContactInquiryRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

@Service
public class ContactInquiryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactInquiryService.class);

    private final ContactInquiryRepository repository;
    private final JavaMailSender mailSender;
    private final Optional<SesClient> sesClient;

    @Value("${app.mail.contact-address}")
    private String contactMailAddress;

    @Value("${app.mail.from-address}")
    private String fromAddress;

    @Value("${app.mail.provider:smtp}")
    private String mailProvider;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public ContactInquiryService(
            ContactInquiryRepository repository,
            JavaMailSender mailSender,
            ObjectProvider<SesClient> sesClientProvider
    ) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.sesClient = Optional.ofNullable(sesClientProvider.getIfAvailable());
    }

    @Transactional
    public ContactInquiryResponse submit(ContactInquiryRequest request) {
        ContactInquiry entity = new ContactInquiry();
        entity.setInquiryRef(generateInquiryRef());
        entity.setName(request.name().trim());
        entity.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        entity.setPhone(request.phone().trim());
        entity.setCompany(request.company() == null ? "" : request.company().trim());
        entity.setInquiryType(request.inquiryType().trim());
        entity.setMessage(request.message().trim());

        ContactInquiry saved = repository.saveAndFlush(entity);
        dispatchNotifications(saved);

        return new ContactInquiryResponse(
                saved.getInquiryRef(),
                "Inquiry submitted successfully."
        );
    }

    private void dispatchNotifications(ContactInquiry inquiry) {
        if (!isMailConfigurationReady()) {
            LOGGER.warn(
                    "Inquiry {} saved, but mail configuration is incomplete. Skipping notification emails.",
                    inquiry.getInquiryRef()
            );
            return;
        }

        try {
            sendCompanyNotification(inquiry);
        } catch (Exception ex) {
            LOGGER.error(
                    "Inquiry {} saved, but company notification email failed.",
                    inquiry.getInquiryRef(),
                    ex
            );
        }

        try {
            sendSubmitterAcknowledgement(inquiry);
        } catch (Exception ex) {
            LOGGER.error(
                    "Inquiry {} saved, but submitter acknowledgement email failed.",
                    inquiry.getInquiryRef(),
                    ex
            );
        }
    }

    private String generateInquiryRef() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
        return "PVI-CON-" + datePart + "-" + randomPart;
    }

    private boolean isMailConfigurationReady() {
        if (resolveFromAddress().isBlank()) {
            return false;
        }

        if (contactMailAddress == null || contactMailAddress.isBlank()) {
            return false;
        }

        if (isSesProvider() && sesClient.isEmpty()) {
            return false;
        }

        return true;
    }

    private void sendCompanyNotification(ContactInquiry inquiry) {
        try {
            String senderAddress = resolveFromAddress();
            MimeMessage message = createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

            helper.setFrom(senderAddress);
            helper.setTo(contactMailAddress);
            helper.setReplyTo(inquiry.getEmail());
            helper.setSubject("New Contact Inquiry | " + inquiry.getInquiryType() + " | " + inquiry.getInquiryRef());
            helper.setText(buildCompanyMailBody(inquiry), false);

            dispatchMimeMessage(message);
        } catch (Exception ex) {
            throw new RuntimeException("Inquiry saved, but failed to send company notification email.", ex);
        }
    }

    private void sendSubmitterAcknowledgement(ContactInquiry inquiry) {
        try {
            String senderAddress = resolveFromAddress();
            MimeMessage message = createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(senderAddress);
            helper.setTo(inquiry.getEmail());
            helper.setSubject("PVI ENGINEERS | Inquiry Received | " + inquiry.getInquiryRef());
            helper.setText(buildSubmitterMailBody(inquiry), false);
            dispatchMimeMessage(message);
        } catch (Exception ex) {
            throw new RuntimeException("Inquiry saved, but failed to send submitter acknowledgement email.", ex);
        }
    }

    private MimeMessage createMimeMessage() {
        if (isSesProvider()) {
            return new MimeMessage(Session.getInstance(new Properties()));
        }

        return mailSender.createMimeMessage();
    }

    private void dispatchMimeMessage(MimeMessage message) throws MessagingException, IOException {
        if (!isSesProvider()) {
            mailSender.send(message);
            return;
        }

        SesClient client = sesClient.orElseThrow(
                () -> new IllegalArgumentException("SES mail provider is selected but SES client is unavailable.")
        );

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            message.writeTo(outputStream);
            RawMessage rawMessage = RawMessage.builder()
                    .data(SdkBytes.fromByteArray(outputStream.toByteArray()))
                    .build();

            SendRawEmailRequest request = SendRawEmailRequest.builder()
                    .rawMessage(rawMessage)
                    .build();

            client.sendRawEmail(request);
        }
    }

    private boolean isSesProvider() {
        return "ses".equalsIgnoreCase(mailProvider);
    }

    private String resolveFromAddress() {
        if (fromAddress != null && !fromAddress.isBlank()) {
            return fromAddress.trim();
        }

        return mailUsername == null ? "" : mailUsername.trim();
    }

    private String buildCompanyMailBody(ContactInquiry inquiry) {
        return """
                A new contact inquiry has been submitted.

                Inquiry Reference: %s
                Submitted On: %s

                Contact Details
                ---------------
                Name: %s
                Email: %s
                Phone: %s
                Company / Organization: %s
                Inquiry Type: %s

                Message
                -------
                %s
                """.formatted(
                inquiry.getInquiryRef(),
                inquiry.getCreatedAt(),
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getPhone(),
                inquiry.getCompany().isBlank() ? "-" : inquiry.getCompany(),
                inquiry.getInquiryType(),
                inquiry.getMessage()
        );
    }

    private String buildSubmitterMailBody(ContactInquiry inquiry) {
        return """
                Dear %s,

                Thank you for contacting PVI ENGINEERS.

                We have received your inquiry successfully.
                Inquiry Reference: %s
                Inquiry Type: %s

                Our team will review your message and connect with you shortly.

                Regards,
                Team PVI ENGINEERS
                """.formatted(
                inquiry.getName(),
                inquiry.getInquiryRef(),
                inquiry.getInquiryType()
        );
    }
}
