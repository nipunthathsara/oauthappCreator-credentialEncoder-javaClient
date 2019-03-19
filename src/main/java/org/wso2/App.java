package org.wso2;

import org.apache.commons.codec.binary.Base64;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.identity.application.common.model.xsd.InboundAuthenticationConfig;
import org.wso2.carbon.identity.application.common.model.xsd.InboundAuthenticationRequestConfig;
import org.wso2.carbon.identity.application.common.model.xsd.Property;
import org.wso2.carbon.identity.application.common.model.xsd.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceIdentityApplicationManagementException;
import org.wso2.carbon.identity.oauth.stub.OAuthAdminServiceIdentityOAuthAdminException;
import org.wso2.carbon.identity.oauth.stub.dto.OAuthConsumerAppDTO;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws RemoteException, MalformedURLException, LoginAuthenticationExceptionException, OAuthAdminServiceIdentityOAuthAdminException {
        System.out.println("Hello World!");
        AuthenticationServiceClient authenticationServiceClient = new AuthenticationServiceClient();
        String cookie = authenticationServiceClient.authenticate("admin", "admin");
        System.out.println("******" + cookie);
        System.out.println("Count : " + args[0] + " Starting index : " + args[1] + " mode : " + args[2]);

        if (args[2].equals("create")) {
            int count = Integer.parseInt(args[0]);
            int errorcount = 0;
            for (int i = Integer.parseInt(args[1]); i < Integer.parseInt(args[1]) + count; i++) {
                System.out.println("Executing the attempt : " + i);
                try {
                    createSP(i, cookie);
                } catch (Exception e) {
                    errorcount++;
                    e.printStackTrace();
                }
            }
            System.out.println("############# ERROR COUNT########## " + errorcount);
        } else if (args[2].equals("encode")){
            getAndEncodeClientCredentials(new OauthAppCreator(cookie), args[1], Integer.parseInt(args[0]));
        } else {
            getEncodedUserCredentials(args[0], Integer.parseInt(args[1]));
        }

    }

    public static void createSP(int i, String cookie) throws MalformedURLException, RemoteException, IdentityApplicationManagementServiceIdentityApplicationManagementException, OAuthAdminServiceIdentityOAuthAdminException {
        String name = Integer.toString(i);

        //Create empty service provider
        SPCreator spCreator = new SPCreator(cookie);
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setApplicationName(name);
        spCreator.createServiceProvider(serviceProvider);

        // Create oauth app
        OauthAppCreator oauthAppCreator = new OauthAppCreator(cookie);
        OAuthConsumerAppDTO oAuthConsumerAppDTO = new OAuthConsumerAppDTO();
        oAuthConsumerAppDTO.setOAuthVersion("OAuth-2.0");
        oAuthConsumerAppDTO.setApplicationAccessTokenExpiryTime(3600);
        oAuthConsumerAppDTO.setApplicationName(name);
        oAuthConsumerAppDTO.setCallbackUrl("http://wso2.is.com:8080/playground2/oauth2client");
        oAuthConsumerAppDTO.setGrantTypes("refresh_token urn:ietf:params:oauth:grant-type:saml2-bearer implicit password client_credentials iwa:ntlm authorization_code urn:ietf:params:oauth:grant-type:jwt-bearer");
        oAuthConsumerAppDTO.setPkceMandatory(false);
        oAuthConsumerAppDTO.setPkceSupportPlain(true);
        oAuthConsumerAppDTO.setRefreshTokenExpiryTime(84601);
        oAuthConsumerAppDTO.setUserAccessTokenExpiryTime(3600);

        System.out.println("Creating oauth app : " + name);
        oauthAppCreator.createOauthApp(oAuthConsumerAppDTO);

        System.out.println("Retrieving oauth app : " + name);
        OAuthConsumerAppDTO oauthapp = oauthAppCreator.getOauthApp(name);

        // Update sp with oauth app details
        serviceProvider = spCreator.getServiceProvider(name);
        InboundAuthenticationConfig inboundAuthenticationConfig = new InboundAuthenticationConfig();
        InboundAuthenticationRequestConfig requestConfig = new InboundAuthenticationRequestConfig();
        requestConfig.setInboundAuthKey(oauthapp.getOauthConsumerKey());
        requestConfig.setInboundAuthType("oauth2");
        Property[] properties = new Property[1];
        Property property = new Property();
            property.setName("oauthConsumerSecret");
        property.setValue(oauthapp.getOauthConsumerSecret());
        properties[0] = property;
        requestConfig.setProperties(properties);
        inboundAuthenticationConfig.addInboundAuthenticationRequestConfigs(requestConfig);
        serviceProvider.setInboundAuthenticationConfig(inboundAuthenticationConfig);
        spCreator.updateServiceProvider(serviceProvider);
    }

    public static void getAndEncodeClientCredentials (OauthAppCreator oauthAppCreator, String startingIndex, int count) {
        int errorcount = 0;
        for (int i = Integer.parseInt(startingIndex); i < Integer.parseInt(startingIndex) + count; i++) {
            try {
                OAuthConsumerAppDTO app = oauthAppCreator.getOauthApp(Integer.toString(i));
                String credentials = app.getOauthConsumerKey() + ":" + app.getOauthConsumerSecret();
                String encodedCredentials = new String (Base64.encodeBase64(credentials.getBytes()));
                System.out.println(encodedCredentials);
            } catch (Exception e) {
                errorcount++;
                e.printStackTrace();
            }
        }
        System.out.println("############# ERROR COUNT########## " + errorcount);
    }

    public static void  getEncodedUserCredentials (String startingIndex, int count) {
        for (int i = Integer.parseInt(startingIndex); i < Integer.parseInt(startingIndex) + count; i++) {
            String username = "user" + i;
            String password = "qazqaz";
            String cred = username + ":" + password;
            String encodedCred = new String (Base64.encodeBase64(cred.getBytes()));
            System.out.println(encodedCred);
        }
    }
}
