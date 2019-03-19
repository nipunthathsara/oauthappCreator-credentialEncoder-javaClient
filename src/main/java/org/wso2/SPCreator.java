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
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.wso2.carbon.identity.application.common.model.xsd.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceIdentityApplicationManagementException;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceStub;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

public class SPCreator {
    private IdentityApplicationManagementServiceStub stub;

    public SPCreator(String cookie) throws MalformedURLException, AxisFault {
        URL baseUrl = new URL("https://wso2.is.com:9443");
        String serviceUrl = new URL(baseUrl, "services/IdentityApplicationManagementService").toString();
        stub = new IdentityApplicationManagementServiceStub(serviceUrl);

        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
    }

    public void createServiceProvider(ServiceProvider serviceProvider) throws RemoteException,
            IdentityApplicationManagementServiceIdentityApplicationManagementException {
        stub.createApplication(serviceProvider);
    }

    public ServiceProvider getServiceProvider(String name) throws RemoteException, IdentityApplicationManagementServiceIdentityApplicationManagementException {
       return stub.getApplication(name);
    }

    public void updateServiceProvider (ServiceProvider serviceProvider) throws RemoteException, IdentityApplicationManagementServiceIdentityApplicationManagementException {
        stub.updateApplication(serviceProvider);
    }
}
