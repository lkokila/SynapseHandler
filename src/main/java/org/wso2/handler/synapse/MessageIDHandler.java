/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.handler.synapse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.AbstractSynapseHandler;
import org.apache.synapse.MessageContext;
import org.slf4j.MDC;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;


public class MessageIDHandler extends AbstractSynapseHandler {
    private static final Log log = LogFactory.getLog(MessageIDHandler.class);
    private static final String __MESSAGE_ID__ = "__MESSAGE_ID__";
    private static final String LOG_KEY = "messageId";
    private long RequestIntime = 0;
    private long RequestOutTime = 0;
    private long ResponseInTime = 0;
    private long ResponseOutTime = 0;
    private long RequestGap = 0;
    private long ResponseGap = 0;

    private DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSSSSS");
    private Calendar cal = null;

    public boolean handleRequestInFlow(MessageContext messageContext) {

        String messageId = "";

        if (StringUtils.isEmpty(messageId)) {
            messageId = UUID.randomUUID().toString();
        }

        messageContext.setProperty(__MESSAGE_ID__, messageId);
        MDC.put(LOG_KEY, messageId + " - [REQUEST]");

        if (log.isDebugEnabled()) {
            Calendar cal = Calendar.getInstance();
            RequestIntime = System.currentTimeMillis();
            log.debug("transactionId=" + messageId + " ,requestInFlowTime=" + dateFormat.format(cal.getTime()));

        }
        return true;
    }

    public boolean handleRequestOutFlow(MessageContext messageContext) {
        String messageId = (String) messageContext.getProperty("__MESSAGE_ID__");

        if (log.isDebugEnabled()) {
            Calendar cal = Calendar.getInstance();
            RequestOutTime = System.currentTimeMillis();
            log.debug("transactionId=" + messageId + " ,requestOutFlowTime=" + dateFormat.format(cal.getTime()));

        }

        RequestGap = (RequestOutTime - RequestIntime);
        if (log.isDebugEnabled()) {
            log.debug("Request Time Gap in milisecond is " + RequestGap);
        }
        return true;
    }

    public boolean handleResponseInFlow(MessageContext messageContext) {

        String responseMessageId = (String) messageContext.getProperty("__MESSAGE_ID__");

        if (log.isDebugEnabled()) {
            Calendar cal = Calendar.getInstance();
            ResponseInTime = System.currentTimeMillis();
            log.debug("transactionId=" + responseMessageId + " ,responseInFlowTime=" +
                    dateFormat.format(cal.getTime()));
        }

        return true;
    }

    public boolean handleResponseOutFlow(MessageContext messageContext) {

        String responseMessageId = (String) messageContext.getProperty("__MESSAGE_ID__");

        if (log.isDebugEnabled()) {
            cal = Calendar.getInstance();

            ResponseOutTime = System.currentTimeMillis();

            log.debug("transactionId=" + responseMessageId + " ,responseOutFlowTime=" +
                    dateFormat.format(cal.getTime()));

        }
        ResponseGap = (ResponseOutTime - ResponseInTime);
        if (log.isDebugEnabled()) {
            log.debug("Response Time Gap in millisecond is" + ResponseGap);
        }
        return true;
    }
}
