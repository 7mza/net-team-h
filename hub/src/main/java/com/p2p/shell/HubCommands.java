package com.p2p.shell;

import java.net.SocketException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.p2p.net.Hub;

@Component
public class HubCommands implements CommandMarker {

	@Autowired
	private Hub hub;

	@CliAvailabilityIndicator({ "!", "//", ":", "date", "script", "system properties", "version" })
	public boolean isDefaultAvailable() {
		return false;
	}

	@CliAvailabilityIndicator({ "start", "stop", "status", "libr", "list", "about" })
	public boolean isCommandAvailable() {
		return true;
	}

	@CliCommand(value = "start", help = "start hub")
	public String start(@CliOption(key = {
			"interface" }, mandatory = false, unspecifiedDefaultValue = "wlan0", help = "network interface on which to start hub : default to wlan0") final String intrfc) {
		try {
			return this.hub.start(intrfc);
		} catch (SocketException e) {
			return "can't get hub real ip";
		}
	}

	@CliCommand(value = "stop", help = "stop hub")
	public String stop() {
		return this.hub.stop();
	}

	@CliCommand(value = "status", help = "get hub's status")
	public String status() {
		return this.hub.status();
	}

	@CliCommand(value = "libr", help = "librarify a file in home folder")
	public String libr(@CliOption(key = { "name" }, mandatory = true, help = "file's name") final String fileName) {
		return this.hub.libr(fileName);
	}

	@CliCommand(value = "list", help = "list connected players")
	public String list() {
		return this.hub.list();
	}

	@CliCommand(value = "about", help = "app creators")
	public String about() {
		return "Baaziz, Amyar, Bendari - M1 WI - UJM 2015/16";
	}
	
	public String print(String msg) {
		return msg;
	}

}
