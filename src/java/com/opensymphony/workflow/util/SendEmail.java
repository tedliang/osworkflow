/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.FunctionProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * Sends an email to a group of users. The following arguments are expected:
 *
 * <ul>
 *  <li>to - comma seperated list of email addresses</li>
 *  <li>from - single email address</li>
 *  <li>subject - the message subject</li>
 *  <li>cc - comma seperated list of email addresses (optional)</li>
 *  <li>message - the message body</li>
 *  <li>smtpHost - the SMTP host that will relay the message</li>
 * </ul>
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.2 $
 */
public class SendEmail implements FunctionProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(SendEmail.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(Map transientVars, Map args, PropertySet ps) {
        String to = (String) args.get("to");
        String from = (String) args.get("from");
        String subject = (String) args.get("subject");
        String cc = (String) args.get("cc");
        String m = (String) args.get("message");
        String smtpHost = (String) args.get("smtpHost");

        if (log.isDebugEnabled()) {
            log.debug("Host: " + smtpHost);
        }

        if (log.isDebugEnabled()) {
            log.debug("To: " + to);
        }

        if (log.isDebugEnabled()) {
            log.debug("Cc: " + cc);
        }

        if (log.isDebugEnabled()) {
            log.debug("From: " + from);
        }

        if (log.isDebugEnabled()) {
            log.debug("Subject: " + subject);
        }

        if (log.isDebugEnabled()) {
            log.debug("Message: " + m);
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);

            Session sendMailSession = Session.getInstance(props, null);
            Transport transport = sendMailSession.getTransport("smtp");
            Message message = new MimeMessage(sendMailSession);

            message.setFrom(new InternetAddress(from));

            Set toSet = new HashSet();
            StringTokenizer st = new StringTokenizer(to, ", ");

            while (st.hasMoreTokens()) {
                String user = st.nextToken();
                toSet.add(new InternetAddress(user));
            }

            message.setRecipients(Message.RecipientType.TO, (InternetAddress[]) toSet.toArray(new InternetAddress[toSet.size()]));

            Set ccSet = null;

            if (cc != null) {
                ccSet = new HashSet();
                st = new StringTokenizer(cc, ", ");

                while (st.hasMoreTokens()) {
                    String user = st.nextToken();
                    ccSet.add(new InternetAddress(user));
                }
            }

            if ((ccSet != null) && (ccSet.size() > 0)) {
                message.setRecipients(Message.RecipientType.CC, (InternetAddress[]) ccSet.toArray(new InternetAddress[ccSet.size()]));
            }

            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(m);
            message.saveChanges();

            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            log.error("Error sending email:", e);
        }
    }
}
