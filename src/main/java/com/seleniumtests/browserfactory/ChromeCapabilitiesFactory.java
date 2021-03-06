/*
 * Orignal work: Copyright 2015 www.seleniumtests.com
 * Modified work: Copyright 2016 www.infotel.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seleniumtests.browserfactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.seleniumtests.core.SeleniumTestsContext;
import com.seleniumtests.driver.DriverConfig;
import com.seleniumtests.driver.DriverMode;
import com.seleniumtests.util.FileUtility;
import com.seleniumtests.util.OSUtility;

public class ChromeCapabilitiesFactory extends ICapabilitiesFactory {

    public DesiredCapabilities createCapabilities(final DriverConfig webDriverConfig) {

        DesiredCapabilities capability = null;
        capability = DesiredCapabilities.chrome();
        capability.setBrowserName(DesiredCapabilities.chrome().getBrowserName());

        ChromeOptions options = new ChromeOptions();
        if (webDriverConfig.getUserAgentOverride() != null) {
            options.addArguments("--user-agent=" + webDriverConfig.getUserAgentOverride());
        }

        capability.setCapability(ChromeOptions.CAPABILITY, options);

        if (webDriverConfig.isEnableJavascript()) {
            capability.setJavascriptEnabled(true);
        } else {
            capability.setJavascriptEnabled(false);
        }

        capability.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        if (webDriverConfig.getBrowserVersion() != null) {
            capability.setVersion(webDriverConfig.getBrowserVersion());
        }

        if (webDriverConfig.getWebPlatform() != null) {
            capability.setPlatform(webDriverConfig.getWebPlatform());
        }

        if (webDriverConfig.getProxyHost() != null) {
            Proxy proxy = webDriverConfig.getProxy();
            capability.setCapability(CapabilityType.PROXY, proxy);
        }

        if (webDriverConfig.getChromeBinPath() != null) {
            capability.setCapability("chrome.binary", webDriverConfig.getChromeBinPath());
        }

        // Set ChromeDriver for local mode
        if (webDriverConfig.getMode() == DriverMode.LOCAL) {
            String chromeDriverPath = webDriverConfig.getChromeDriverPath();
            if (chromeDriverPath == null) {
                try {
                    if (System.getenv("webdriver.chrome.driver") != null) {
                        System.out.println("get Chrome driver from property:"
                                + System.getenv("webdriver.chrome.driver"));
                        System.setProperty("webdriver.chrome.driver", System.getenv("webdriver.chrome.driver"));
                    } else {
                        handleExtractResources();
                    }
                } catch (IOException ex) {
                	logger.error(ex);
                }
            } else {
                System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            }
        }

        return capability;
    }

    public void handleExtractResources() throws IOException {
        String dir = Paths.get(SeleniumTestsContext.getRootPath(), "tools", "drivers", Platform.getCurrent().family().toString().toLowerCase()).toString();
        dir = FileUtility.decodePath(dir);

        if (OSUtility.isWindows()) {
            System.setProperty("webdriver.chrome.driver", dir + "\\chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", dir + "/chromedriver");
            new File(dir + "/chromedriver").setExecutable(true);
        }
    }

}
