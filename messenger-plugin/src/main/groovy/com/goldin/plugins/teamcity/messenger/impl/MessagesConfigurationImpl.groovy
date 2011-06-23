package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Invariant
import org.gcontracts.annotations.Requires
import org.jdom.Element

/**
 * {@link MessagesConfiguration} implementation
 */
@Invariant({
    ( this.context && this.defaults ) &&
    ( this.ajaxRequestInterval  > 0 ) &&
    ( this.persistencyInterval  > 0 ) &&
    ( this.messagesLimitPerUser > 0 ) &&
    ( this.messageLengthLimit   > 0 ) &&
    ( this.logCategory       )        &&
    ( this.dateFormatPattern )        &&
    ( this.timeFormatPattern )
})
class MessagesConfigurationImpl implements MessagesConfiguration
{
    private final MessagesContext     context
    private final Map<String, String> defaults

    boolean    minify
    int        ajaxRequestInterval
    int        persistencyInterval
    int        messagesLimitPerUser
    int        messageLengthLimit
    String     logCategory
    String     dateFormatPattern
    String     timeFormatPattern
    DateFormat dateFormatter
    DateFormat timeFormatter


    MessagesConfigurationImpl ( MessagesContext context )
    {
        this.context  = context
        this.defaults = map( new XmlParser().parse( getClass().getResourceAsStream( '/default-config.xml' ))).asImmutable()
        readParams()
    }


    @Requires({ paramName && ( config != null ) && defaults })
    @Ensures({ result })
    private String param ( String paramName, Map<String, String> config = [:] ) { config[ paramName ] ?: defaults[ paramName ] }


    /**
     * Initializes configuration parameters using a config Map provided or default parameters.
     * @param config co
     */
    @Requires({ config != null })
    private void readParams ( Map<String, String> config = [:] )
    {
        this.minify               = Boolean.valueOf( param( 'minify', config ))
        this.ajaxRequestInterval  = param( 'ajaxRequestInterval',     config ) as int
        this.persistencyInterval  = param( 'persistencyInterval',     config ) as int
        this.messagesLimitPerUser = param( 'messagesLimitPerUser',    config ) as int
        this.messageLengthLimit   = param( 'messageLengthLimit',      config ) as int
        this.logCategory          = param( 'logCategory',             config )
        this.dateFormatPattern    = param( 'dateFormatPattern',       config )
        this.timeFormatPattern    = param( 'timeFormatPattern',       config )
        this.dateFormatter        = new SimpleDateFormat( dateFormatPattern, context.locale )
        this.timeFormatter        = new SimpleDateFormat( timeFormatPattern, context.locale )
    }


    /**
     * Converts {@link Node} or {@link Element} to {@code Map<String, String>}
     * of its elements for easier reading of data.
     *
     * @param o object to convert
     * @return object's mapping
     */
    @Requires({( o instanceof Node ) || ( o instanceof Element )})
    @Ensures({ result != null })
    private Map<String, String> map ( Object o )
    {
        if ( o instanceof Node )
        {
            (( Node ) o ).children().inject( [:] ){ Map<String, String> m, Node childNode ->

                assert childNode.name()
                String text = childNode.text().trim()
                if ( text ){ m[ childNode.name() ] = text }
                m
            }
        }
        else
        {
            (( Element ) o ).children.inject( [:] ){ Map<String, String> m, Element childElement ->

                assert childElement.name
                String text = childElement.text.trim()
                if ( text ){ m[ childElement.name ] = text }
                m
            }
        }
    }


    @Override
    @Requires({ root })
    void readFrom ( Element root )
    {
        Element rootNode = root.getChild( context.pluginName )
        if ( rootNode ) { readParams( map( rootNode )) }
    }
}
