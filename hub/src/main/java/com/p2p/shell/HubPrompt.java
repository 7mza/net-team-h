package com.p2p.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HubPrompt extends DefaultPromptProvider {

	@Override
	public String getPrompt() {
		return "hub> ";
	}

	@Override
	public String getProviderName() {
		return "hub shell prompt";
	}

}
