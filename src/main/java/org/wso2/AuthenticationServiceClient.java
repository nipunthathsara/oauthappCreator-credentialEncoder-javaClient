/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Properties;

/**
 * This class provides functionality to authenticate the tenant admin to use the stub.
 */
public class AuthenticationServiceClient {
    private static AuthenticationAdminStub authenticationAdminStub;

    /**
     * Configuring AuthenticationAdmin stub.
     * @throws AxisFault
     * @throws MalformedURLException
     */
    public AuthenticationServiceClient() throws AxisFault, MalformedURLException {
        System.setProperty("javax.net.ssl.trustStore", "./client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");

        URL baseUrl = new URL("https://wso2.is.com:9443");
        String serviceUrl = new URL(baseUrl, "services/AuthenticationAdmin").toString();

        authenticationAdminStub = new AuthenticationAdminStub(serviceUrl);
    }

    /**
     * This method invokes the authenticationAdmin stub to authenticate the provided user and returns the cookie.
     * @param userName
     * @param password
     * @return
     * @throws RemoteException
     * @throws LoginAuthenticationExceptionException
     */
    public String authenticate(String userName, String password) throws RemoteException, LoginAuthenticationExceptionException {
        if (authenticationAdminStub.login(userName, password, null)) {

            ServiceContext serviceContext = authenticationAdminStub.
                    _getServiceClient().getLastOperationContext().getServiceContext();
            return (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
        } else {
            return null;
        }
    }
}
