package com.p2p.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PlayerBanner extends DefaultBannerProvider {

	public String getBanner() {
		StringBuffer buf = new StringBuffer();
		buf.append(" ██▓███   ██▓    ▄▄▄     ▓██   ██▓▓█████  ██▀███  " + OsUtils.LINE_SEPARATOR);
		buf.append("▓██░  ██▒▓██▒   ▒████▄    ▒██  ██▒▓█   ▀ ▓██ ▒ ██▒" + OsUtils.LINE_SEPARATOR);
		buf.append("▓██░ ██▓▒▒██░   ▒██  ▀█▄   ▒██ ██░▒███   ▓██ ░▄█ ▒" + OsUtils.LINE_SEPARATOR);
		buf.append("▒██▄█▓▒ ▒▒██░   ░██▄▄▄▄██  ░ ▐██▓░▒▓█  ▄ ▒██▀▀█▄  " + OsUtils.LINE_SEPARATOR);
		buf.append("▒██▒ ░  ░░██████▒▓█   ▓██▒ ░ ██▒▓░░▒████▒░██▓ ▒██▒" + OsUtils.LINE_SEPARATOR);
		buf.append("▒▓▒░ ░  ░░ ▒░▓  ░▒▒   ▓▒█░  ██▒▒▒ ░░ ▒░ ░░ ▒▓ ░▒▓░" + OsUtils.LINE_SEPARATOR);
		buf.append("░▒ ░     ░ ░ ▒  ░ ▒   ▒▒ ░▓██ ░▒░  ░ ░  ░  ░▒ ░ ▒░" + OsUtils.LINE_SEPARATOR);
		buf.append("░░         ░ ░    ░   ▒   ▒ ▒ ░░     ░     ░░   ░ " + OsUtils.LINE_SEPARATOR);
		buf.append("             ░  ░     ░  ░░ ░        ░  ░   ░     " + OsUtils.LINE_SEPARATOR);
		buf.append("                          ░ ░                     " + OsUtils.LINE_SEPARATOR);
		buf.append("version: " + this.getVersion());
		return buf.toString();
	}

	public String getVersion() {
		return "2.0-SNAPSHOT";
	}

	public String getWelcomeMessage() {
		return "welcome to player shell";
	}

	@Override
	public String getProviderName() {
		return "player";
	}

}