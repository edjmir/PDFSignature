/*
 * Copyright (c) 2020. Benjamín Guzmán
 * Author: Benjamín Guzmán <9benjaminguzman@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fos;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

public class Main
{
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, IOException, SignatureException
	{
		KeyPairGen keyPairGen = new KeyPairGen();
		keyPairGen.createAndSaveKeys(new File("/tmp/nopass.pem"));
		keyPairGen.createAndSaveKeys(new File("/tmp/pass.pem"), "pass");

		// verify nopass.pem and pass.pem are the same key with
		// openssl rsa -in nopass.pem | sha256sum
		// openssl rsa -in pass.pem | sha256sum
		// the checksums for both SHOULD be the same

		PrivateKeySigner signer = new PrivateKeySigner();
		signer.loadPrivateKey(new File("/tmp/nopass.pem"), null);
		System.out.println(
			Base64.getEncoder().encodeToString(signer.sign(new File("/tmp/pass.pem")))
		);

		// verify the signature is correct with
		// openssl dgst -sha256 -sign nopass.pem -out pass.sha256 pass.pem && base64 pass.sha256 --wrap=0
		// the encoded signature SHOULD be the same
	}
}
