package com.p2p.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Util {
	private static final Logger log = LogManager.getLogger(Util.class.getName());

	private static final boolean APPEND = false;

	public static final int BOOK_SIZE = 4000; // TODO how to choose ?

	public File librarify(File file, String host, int port) throws IOException {
		log.info("librarifying " + file.getName());
		File libr = new File(System.getProperty("user.home") + File.separator + file.getName() + "." + "libr");
		List<String> lines = new ArrayList<>();
		String hub = host + ":" + port; // Address of the hub
		log.info("hub : " + hub);
		lines.add(hub);
		String stuff = file.getName(); // Name of the stuff
		log.info("stuff : " + stuff);
		lines.add(stuff);
		long stuffSize = file.length(); // Size of the stuff
		log.info("stuff size : " + stuffSize + " bytes");
		lines.add(String.valueOf(stuffSize));
		log.info("book size : " + BOOK_SIZE + " bytes"); // Size used for books
		lines.add(String.valueOf(BOOK_SIZE));
		long nbr = stuffSize / BOOK_SIZE; // Number of books
		if (stuffSize % BOOK_SIZE > 0)
			nbr++;
		log.info("number of books : " + nbr);
		lines.add(String.valueOf(nbr));
		// Slicing a stuff to books + for each book its SHA1
		log.info("slicing stuff to books");
		byte[] bytes = FileUtils.readFileToByteArray(file);
		int i = 0;
		while (i < stuffSize) {
			byte[] slice = Arrays.copyOfRange(bytes, i, i + BOOK_SIZE);
			String sha = this.byteArrayToHexString(slice);
			lines.add(sha);
			i += BOOK_SIZE;
		}
		// Write libr to File
		FileUtils.writeLines(libr, lines, System.getProperty("line.separator"), APPEND);
		log.info("librarifying done");
		return libr;
	}

	public String byteArrayToHexString(byte[] bytes) {
		return DigestUtils.sha1Hex(bytes);
	}

	public String fileToHexString(File file) throws FileNotFoundException, IOException {
		return DigestUtils.sha1Hex(new FileInputStream(file));
	}

	public InetAddress getIp(String networkInterface) throws SocketException {
		InetAddress host = null;
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface intrfc = interfaces.nextElement();
			if (intrfc.getName().trim().toLowerCase().equals(networkInterface.trim().toLowerCase())) {
				Enumeration<InetAddress> addresses = intrfc.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					if (addr.toString().split("\\.").length == 4)
						host = addr;
				}
			}
		}
		return host;
	}

}
