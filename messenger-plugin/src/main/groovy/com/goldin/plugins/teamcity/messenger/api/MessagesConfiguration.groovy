package com.goldin.plugins.teamcity.messenger.api

import java.text.DateFormat
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.jdom.Element

/**
 * Configuration data
 */
interface MessagesConfiguration
{
    @Requires({ root })
    void readFrom ( Element root )


    @Ensures({ result })
    String getLogCategory()


    boolean isMinify ()


    @Ensures({ result > 0 })
    int    getAjaxRequestInterval()


    @Ensures({ result > 0 })
    int    getPersistencyInterval()


    @Ensures({ result > 0 })
    int    getMessagesLimitPerUser()


    @Ensures({ result > 0 })
    int    getMessageLengthLimit()


    @Ensures({ result })
    String getDateFormatPattern()


    @Ensures({ result })
    String getTimeFormatPattern()


    @Ensures({ result })
    DateFormat getDateFormatter()


    @Ensures({ result })
    DateFormat getTimeFormatter()
}
