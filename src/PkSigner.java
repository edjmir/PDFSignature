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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

public class PrivateKeySigner
{
	private static final Pattern justBase64Regex = Pattern.compile("[\\s]+", Pattern.DOTALL | Pattern.MULTILINE);

	private RSAPrivateKey privateKey;

	public PrivateKeySigner() {

	}

	public RSAPrivateKey loadPrivateKey(File fromFile, String passphrase) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		if (this.privateKey != null)
			return this.privateKey;

		// read content file
		List<String> lines  = Files.readAllLines(fromFile.toPath());
		String content = String.join("", lines);

		// remove noise
		content = content.replace("-----BEGIN RSA PRIVATE KEY-----", "")
			.replace("-----END RSA PRIVATE KEY-----", "");
		content = justBase64Regex.matcher(content)
			.replaceAll("");

		// get as bytes
		byte[] privKeyBytes = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));

		// TODO: do decryption here

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		KeySpec keySpec = new PKCS8EncodedKeySpec(privKeyBytes, "RSA");
		return (this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec));
	}

	public byte[] sign(File file) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, IllegalStateException
	{
		if (this.privateKey == null)
			throw new IllegalStateException("The private key should be loaded first!");

		Signature signer = Signature.getInstance("SHA256withRSA");
		signer.initSign(this.privateKey);
		signer.update(Files.readAllBytes(file.toPath()));
		return signer.sign();
	}

	private static byte[] decryptBytes(byte[] encryptedBytesArr)
	{
		// TODO: complete
		return null;
	}
}
