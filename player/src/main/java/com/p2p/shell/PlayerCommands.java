package com.p2p.shell;

import java.io.IOException;
import java.net.SocketException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.p2p.net.Player;

@Component
public class PlayerCommands implements CommandMarker {

	@Autowired
	private Player player;

	@CliAvailabilityIndicator({ "!", "//", ":", "date", "script", "system properties", "version" })
	public boolean isDefaultAvailable() {
		return false;
	}

	@CliAvailabilityIndicator({ "start", "stop", "status", "connect", "disconnect", "about", "update", "libr",
			"getbook", "check", "write" })
	public boolean isCommandAvailable() {
		return true;
	}

	@CliCommand(value = "start", help = "start player")
	public String start(@CliOption(key = {
			"interface" }, mandatory = false, unspecifiedDefaultValue = "wlan0", help = "network interface on which to start player : default to wlan0") final String intrfc) {
		try {
			return this.player.start(intrfc);
		} catch (SocketException e) {
			return "can't get player real ip";
		}
	}

	@CliCommand(value = "stop", help = "stop player")
	public String stop() {
		return this.player.stop();
	}

	@CliCommand(value = "status", help = "get player's status")
	public String status() {
		return this.player.status();
	}

	@CliCommand(value = "connect", help = "connect to a hub")
	public String connect(@CliOption(key = { "ip" }, mandatory = true, help = "hub's ip adress") final String ip) {
		return player.connectToHub(ip);
	}

	@CliCommand(value = "disconnect", help = "disconnect from a hub")
	public String disconnect() {
		return player.disconnect();
	}

	@CliCommand(value = "about", help = "app creators")
	public String about() {
		return "Baaziz, Amyar, Bendari - M1 WI - UJM 2015/16";
	}

	@CliCommand(value = "update", help = "get list of connected players from hub")
	public void update() {
		this.player.update();
	}

	@CliCommand(value = "libr", help = "process a libr file in home folder")
	public String libr(
			@CliOption(key = { "name" }, mandatory = true, help = "libr file's name") final String librName) {
		try {
			return this.player.libr(librName);
		} catch (IOException e) {
			return "libr file not found";
		}
	}

	public String print(String msg) {
		return msg;
	}

	@CliCommand(value = "getbook", help = "get a book from a player")
	public String getBook(@CliOption(key = { "book" }, mandatory = true, help = "no of book") final int book) {
		return this.player.download(book);
	}

	@CliCommand(value = "download", help = "download a stuff from a player")
	public String download() {
		return this.player.download();
	}

	@CliCommand(value = "check", help = "")
	public String check() {
		return this.player.check();
	}

	@CliCommand(value = "write", help = "")
	public String write() {
		return this.player.write();
	}

}
