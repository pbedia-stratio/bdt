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

package com.stratio.qa.utils.vaultansible;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class VaultAnsibleContent {
    Logger logger = LoggerFactory.getLogger(VaultAnsibleContent.class);

    private final static String CHAR_ENCODING = "UTF-8";

    private byte[] salt;

    private byte[] hmac;

    private byte[] data;

    public VaultAnsibleContent(byte[] encryptedVault) throws IOException {
        byte[][] vaultContents = splitData(encryptedVault);
        salt = VaultAnsibleUtils.unhex(new String(vaultContents[0], CHAR_ENCODING));
        hmac = VaultAnsibleUtils.unhex(new String(vaultContents[1], CHAR_ENCODING));
        data = VaultAnsibleUtils.unhex(new String(vaultContents[2], CHAR_ENCODING));
    }

    public String toString() {
        logger.debug("Salt: {} - HMAC: {} - Data: {} - TargetLen: {}",
                salt.length, hmac.length, data.length, (salt.length + hmac.length + data.length) * 2);
        String saltString = VaultAnsibleUtils.hexit(salt);
        logger.debug("Salt String Length: {}", saltString.length());
        String hmacString = VaultAnsibleUtils.hexit(hmac);
        logger.debug("HMAC String Length: {}", hmacString.length());
        String dataString = VaultAnsibleUtils.hexit(data, -1);
        logger.debug("DATA String Length: {}", dataString.length());
        String complete =  saltString + "\n" + hmacString + "\n" + dataString;
        logger.debug("Complete: {} \n{}", complete.length(), complete);
        String result = VaultAnsibleUtils.hexit(complete.getBytes(), 80);
        logger.debug("Result: [{}] {}\n{}", complete.length() * 2, result.length(), result);
        return result;
    }

    private int[] getDataLengths(byte[] encodedData) throws IOException {
        int[] result = new int[3];

        int idx = 0;
        int saltLen = 0;
        while (encodedData[idx] != '\n') {
            saltLen++;
            idx++;
        }
        // Skip the newline
        idx++;
        if (idx == encodedData.length) {
            throw new IOException("Malformed data - salt incomplete");
        }
        result[0] = saltLen;

        int hmacLen = 0;
        while (encodedData[idx] != '\n') {
            hmacLen++;
            idx++;
        }
        // Skip the newline
        idx++;
        if (idx == encodedData.length) {
            throw new IOException("Malformed data - hmac incomplete");
        }
        result[1] = hmacLen;
        int dataLen = 0;
        while (idx < encodedData.length) {
            dataLen++;
            idx++;
        }
        result[2] = dataLen;

        return result;
    }

    private byte[][] splitData(byte[] encodedData) throws IOException {
        int[] partsLength = getDataLengths(encodedData);

        byte[][] result = new byte[3][];

        int idx = 0;
        int saltIdx = 0;
        result[0] = new byte[partsLength[0]];
        while (encodedData[idx] != '\n') {
            result[0][saltIdx++] = encodedData[idx++];
        }
        // Skip the newline
        idx++;
        if (idx == encodedData.length) {
            throw new IOException("Malformed data - salt incomplete");
        }
        int macIdx = 0;
        result[1] = new byte[partsLength[1]];
        while (encodedData[idx] != '\n') {
            result[1][macIdx++] = encodedData[idx++];
        }
        // Skip the newline
        idx++;
        if (idx == encodedData.length) {
            throw new IOException("Malformed data - hmac incomplete");
        }
        int dataIdx = 0;
        result[2] = new byte[partsLength[2]];
        while (idx < encodedData.length) {
            result[2][dataIdx++] = encodedData[idx++];
        }
        return result;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getHmac() {
        return hmac;
    }

    public byte[] getData() {
        return data;
    }
}
