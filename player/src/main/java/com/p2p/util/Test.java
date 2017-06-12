package com.p2p.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

public class Test {

	public static void main(String[] args) throws IOException {
		File file = new File(System.getProperty("user.home") + File.separator + "book.pdf");
		byte[] bytes = FileUtils.readFileToByteArray(file);
		byte[] slice = Arrays.copyOfRange(bytes, Util.BOOK_SIZE * 0, Util.BOOK_SIZE * 0 + Util.BOOK_SIZE);
		Util util = new Util();
		String sha1 = util.byteArrayToHexString(slice);
		String before = Base64.encodeBase64URLSafeString(slice) + "%" + 0;
		System.out.println(before);
		String toto = before.split("%")[0];
		System.out.println(toto);
		String titi = before.split("%")[1];
		System.out.println(titi);
		byte[] tmp = Base64.decodeBase64(toto);
		String sha2 = util.byteArrayToHexString(tmp);
		System.out.println(sha1);
		System.out.println(sha2);
	}

}
