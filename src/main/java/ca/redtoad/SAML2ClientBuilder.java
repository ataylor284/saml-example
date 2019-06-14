package ca.redtoad;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;

public class SAML2ClientBuilder {

    public SAML2Client build() {
        SAML2Configuration config = new SAML2Configuration();
        config.setIdentityProviderMetadataResourceUrl(getClass().getResource("/idp-metadata.xml").toString());
        config.setSpLogoutRequestBindingType(SAMLConstants.SAML2_REDIRECT_BINDING_URI);

        // keytool -genkey -keyalg RSA -alias saml -keypass changeit -keystore trust.keystore -storepass changeit
        config.setKeystorePath("trust.keystore");
        config.setKeystorePassword("changeit");
        config.setPrivateKeyPassword("changeit");
        config.setKeystoreAlias("saml");

        SAML2Client saml2Client = new SAML2Client(config);
        saml2Client.setName("SAMLExample");
        saml2Client.setCallbackUrl("http://localhost:8080/login/finish");
        saml2Client.init();

        return saml2Client;
    }
}
