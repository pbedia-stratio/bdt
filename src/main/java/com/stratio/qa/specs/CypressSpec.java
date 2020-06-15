/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.qa.specs;

import com.stratio.qa.utils.RemoteSSHConnection;
import com.stratio.qa.utils.RemoteSSHConnectionsUtil;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Date;

/**
 * Cypress Steps.
 *
 * @see <a href="CommandExecutionSpec-annotations.html">Command Execution Steps &amp; Matching Regex</a>
 */
public class CypressSpec extends BaseGSpec {

    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public CypressSpec(CommonG spec) {
        this.commonspec = spec;
    }

    @Given("^I run on Cypress with host '(.+?)' and token '(.+?)' and testcase '(.+?)' with path '(.+?)'( and store video evidences with path '(.+?)')?( and with exit status '(\\d+)')?( and save the value in environment variable '(.+?)')?$")
    public void executeCypresswithURLwithVideo(String url, String token, String testcase, String path, String videopath, Integer sExitStatus, String envVar) throws Exception {
        Integer exitStatus = sExitStatus == null ? 0 : sExitStatus;
        String Command;
        if (videopath == null) {
            Command = "CYPRESS_BASE_URL=https://" + url + " CYPRESS_TOKEN=" + token + " npx cypress run --spec cypress/integration" + path + "/" + testcase + ".spec.ts";
        } else {
            Command = "CYPRESS_BASE_URL=https://" + url + " CYPRESS_TOKEN=" + token + " npx cypress run --spec cypress/integration" + path + "/" + testcase + ".spec.ts --config trashAssetsBeforeRuns=false,videoUploadOnPasses=true,videosFolder=" + videopath;

        }
        commonspec.runLocalCommand(Command);
        commonspec.runCommandLoggerAndEnvVar(exitStatus, envVar, Boolean.TRUE);
        Assertions.assertThat(commonspec.getCommandExitStatus()).isEqualTo(exitStatus);

    }

    @Given("^I run all Cypress tests with host '(.+?)' and token '(.+?)'( and with exit status '(\\d+)')?( and save the value in environment variable '(.+?)')?$")
    public void executeAllCypressTests(String url, String token, Integer sExitStatus, String envVar) throws Exception {
        Integer exitStatus = sExitStatus == null ? 0 : sExitStatus;
        String Command = "CYPRESS_BASE_URL=https://" + url + " CYPRESS_TOKEN=" + token + " npx cypress run  ";
        commonspec.runLocalCommand(Command);
        commonspec.runCommandLoggerAndEnvVar(exitStatus, envVar, Boolean.TRUE);
        Assertions.assertThat(commonspec.getCommandExitStatus()).isEqualTo(exitStatus);

    }

}

