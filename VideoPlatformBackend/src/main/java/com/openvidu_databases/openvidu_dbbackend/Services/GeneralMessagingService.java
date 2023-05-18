package com.openvidu_databases.openvidu_dbbackend.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openvidu_databases.openvidu_dbbackend.Constant.TextError;
import com.openvidu_databases.openvidu_dbbackend.Models.SubmitResponse;
import com.openvidu_databases.openvidu_dbbackend.Models.WhatsappSubmit;
import com.openvidu_databases.openvidu_dbbackend.Models.message;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.openvidu_databases.openvidu_dbbackend.Constant.AllConstants.SUBMISSION_STATE;
@Service
public class GeneralMessagingService implements MessagingService {
    @Autowired
    RestTemplate restTemplate;
   // @Autowired
   // OpenViduService openViduService;
    private static final Logger logger = LoggerFactory.getLogger(GeneralMessagingService.class);
    @Value("${sms.text:-}")
    String smsText;
    @Value("${sms.url:-}")
    String smsUrl;
    @Value("${WA_URL:-}")
    String waUrl;
    @Value("${WA_API_KEY:-}")
    String waApiKey;

    @Override
    public SubmitResponse sendSms(HttpServletRequest request, HttpServletResponse response, Object... args) throws IOException, URISyntaxException {
        SubmitResponse submitResponse = null;
        try {
 //           SubmitResponse failedResponse = validateSessionAndRequest(args);
//            if (failedResponse != null)
//                return failedResponse;
            logger.info(smsText,smsUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            URI uri = new URI(createURL(smsUrl, args[0], URLEncoder.encode(createURL(smsText, args[1]), "UTF-8")));
            submitResponse = restTemplate.exchange(uri, HttpMethod.GET, entity, SubmitResponse.class).getBody();
            return submitResponse;
        } catch (HttpClientErrorException ex) {
            ResponseEntity<String> errorResponse = null;
            if (ex.getStatusCode() != null) {
                errorResponse = ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
            } else {
                errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                submitResponse = objectMapper.readValue(errorResponse.getBody(), SubmitResponse.class);
                return submitResponse;
            } catch (JsonProcessingException e) {
                logger.error("Error in Paring Bad Request {}",e.getMessage());
            }
        }
        throw new RuntimeException("Internal server error occurred.");
    }
    @Override
    public SubmitResponse sendWA(HttpServletRequest request,HttpServletResponse response,Object... args) throws OpenViduJavaClientException, OpenViduHttpException {
        SubmitResponse submitResponse = null;

        try {
            SubmitResponse failedResponse = validateSessionAndRequest(args);
            if (failedResponse != null)
                return failedResponse;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("apikey", waApiKey);
            logger.info(Arrays.toString(args));
            WhatsappSubmit body = new WhatsappSubmit();
            body.setFrom(String.valueOf(args[2]));
            body.setTo(String.valueOf(args[0]));
            body.setType(String.valueOf(args[3]));
            logger.info(String.valueOf(args[3]));
            ArrayList<String> placeHolders = new ArrayList<>();
            logger.info("Args 3 value = "+String.valueOf(args[3]));
            placeHolders.add((String) args[1]);
            logger.info("Args 1 value = "+(String) args[1]);
            message message = new message(String.valueOf(args[4]),placeHolders);
            logger.info("Value of message = "+String.valueOf(message));
            body.setMessage(message);
            ObjectMapper jsonBody=new ObjectMapper();
            String waBody=jsonBody.writeValueAsString(body);
            logger.info("WAbody = "+waBody);
            HttpEntity<String> entity = new HttpEntity<String>(waBody, headers);
            logger.info(String.valueOf(entity.getBody()));
            submitResponse=restTemplate.exchange(waUrl, HttpMethod.POST, entity, SubmitResponse.class).getBody();
            return submitResponse;
        }catch (HttpClientErrorException ex) {
            ResponseEntity<String> errorResponse = null;
            if (ex.getStatusCode() != null) {
                errorResponse = ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
            } else {
                errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                submitResponse = objectMapper.readValue(errorResponse.getBody(), SubmitResponse.class);
                return submitResponse;
            } catch (JsonProcessingException e) {
                logger.error("Error in Paring Bad Request {}",e.getMessage());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Internal server error occurred.");

    }
    private SubmitResponse validateSessionAndRequest(Object... args) throws OpenViduJavaClientException, OpenViduHttpException {
        logger.info(Arrays.toString(args));
        Optional<SubmitResponse> validationResponse = validateSession((String) args[0]);
        if (validationResponse.isPresent())
            return validationResponse.get();
        Optional<SubmitResponse> reqValidationResponse = validateRequest(String.valueOf(args[1]));
        return reqValidationResponse.orElse(null);
    }
    private Optional<SubmitResponse> validateRequest(String msisdn) {
        if (msisdn == null || msisdn.equalsIgnoreCase("")) {
            return Optional.of(getFailedSubmitResponse(TextError.MISSING_MSISDN, 0L));
        }
        return Optional.empty();
    }
    public SubmitResponse getFailedSubmitResponse(TextError error, Object... args) {
        SubmitResponse submitResponse = new SubmitResponse();
        submitResponse.setStatusCode(error.getCode());
        submitResponse.setDescription(String.format(error.getText(), args));
        submitResponse.setState((String) SUBMISSION_STATE);
        submitResponse.setTransactionId("0");
        return submitResponse;
    }
    @Override
    public Optional<SubmitResponse> validateSession(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        if(sessionId!=null) {
            logger.info("Validating Session by id {} ", sessionId);
//           Session session = openViduService.getSession(sessionId);
//            if(session!=null) {
//                int allowedParticipant = 5;
//                int activeParticipant = session.getActiveConnections().size();
//                if (activeParticipant > allowedParticipant) {
//                    logger.info("Participant limit Exceeded for SessionID : {}", sessionId);
//                    return Optional.of(getFailedSubmitResponse(TextError.THROTTELING_ERROR, 0L));
//                }
//            }else{
//                return Optional.empty();
//            }
//        }else {
//            return Optional.of(getFailedSubmitResponse(TextError.SESSION_ERROR));
        }
        return Optional.empty();
    }
    public String createURL (String url, Object ... params) {
        return new MessageFormat(url).format(params);
    }
}

