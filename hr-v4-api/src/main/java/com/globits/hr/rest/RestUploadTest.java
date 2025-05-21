package com.globits.hr.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

@RestController
@RequestMapping("/api/test_upload")
public class RestUploadTest {
    private static final Logger logger = LoggerFactory.getLogger(RestUploadTest.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Event.class, new EventEditor());
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public void uploadFile(jakarta.servlet.http.HttpServletRequest req, @RequestParam("event") Event event) {
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) req;
        Iterator<String> filenames = mRequest.getFileNames();
        while (filenames.hasNext()) {
            MultipartFile file = mRequest.getFile(filenames.next());
            if (file != null) {
                logger.info(file.getContentType());
                logger.info(file.getOriginalFilename());
                logger.info("-----------------");
            }
        }

        logger.info("---------------- Event object ----------------");
        logger.info("Title -> " + event.getTitle());
        logger.info("Description -> " + event.getDescription());
        logger.info("---------------- additional data --------------");
        Enumeration<String> params = mRequest.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            logger.info(paramName + " --> " + mRequest.getParameter(paramName));
        }
    }
}

class Event implements Serializable {
    private static final long serialVersionUID = -3078112791353241758L;
    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

class EventEditor extends PropertyEditorSupport {
    private static final Logger logger = LoggerFactory.getLogger(EventEditor.class);

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        ObjectMapper mapper = new ObjectMapper();
        Event value = null;
        try {
            value = new Event();
            JsonNode root = mapper.readTree(text);
            value.setTitle(root.path("title").asText());
            value.setDescription(root.path("description").asText());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        setValue(value);
    }
}
