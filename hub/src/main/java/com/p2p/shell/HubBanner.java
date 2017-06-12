package com.p2p.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HubBanner extends DefaultBannerProvider {

	public String getBanner() {
		StringBuffer b = new StringBuffer();
		b.append(" ██░ ██  █    ██  ▄▄▄▄   " + OsUtils.LINE_SEPARATOR);
		b.append("▓██░ ██▒ ██  ▓██▒▓█████▄ " + OsUtils.LINE_SEPARATOR);
		b.append("▒██▀▀██░▓██  ▒██░▒██▒ ▄██" + OsUtils.LINE_SEPARATOR);
		b.append("░▓█ ░██ ▓▓█  ░██░▒██░█▀  " + OsUtils.LINE_SEPARATOR);
		b.append("░▓█▒░██▓▒▒█████▓ ░▓█  ▀█▓" + OsUtils.LINE_SEPARATOR);
		b.append(" ▒ ░░▒░▒░▒▓▒ ▒ ▒ ░▒▓███▀▒" + OsUtils.LINE_SEPARATOR);
		b.append(" ▒ ░▒░ ░░░▒░ ░ ░ ▒░▒   ░ " + OsUtils.LINE_SEPARATOR);
		b.append(" ░  ░░ ░ ░░░ ░ ░  ░    ░ " + OsUtils.LINE_SEPARATOR);
		b.append(" ░  ░  ░   ░      ░      " + OsUtils.LINE_SEPARATOR);
		b.append("                       ░ " + OsUtils.LINE_SEPARATOR);
		b.append("version: " + this.getVersion());
		return b.toString();
	}

	public String getVersion() {
		return "2.0-SNAPSHOT";
	}

	public String getWelcomeMessage() {
		return "welcome to hub shell";
	}

	@Override
	public String getProviderName() {
		return "hub";
	}

}