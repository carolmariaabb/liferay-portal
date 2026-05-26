/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.model;

import com.liferay.ai.hub.internal.quota.LiferayTokenConverter;
import com.liferay.ai.hub.internal.quota.TokenSource;
import com.liferay.ai.hub.quota.util.QuotaUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;

import java.util.Map;

/**
 * @author Guilherme Camacho
 */
public class AIHubQuotaChatModelListener implements ChatModelListener {

	public AIHubQuotaChatModelListener(long companyId, long userId) {
		_companyId = companyId;
		_userId = userId;
	}

	@Override
	public void onError(ChatModelErrorContext chatModelErrorContext) {
		_log.error(chatModelErrorContext.error());
	}

	@Override
	public void onResponse(ChatModelResponseContext chatModelResponseContext) {
		ChatResponse chatResponse = chatModelResponseContext.chatResponse();

		if (chatResponse == null) {
			return;
		}

		TokenUsage tokenUsage = chatResponse.tokenUsage();

		if (tokenUsage == null) {
			return;
		}

		Integer inputTokenCount = tokenUsage.inputTokenCount();
		Integer totalTokenCount = tokenUsage.totalTokenCount();

		if ((inputTokenCount == null) || (totalTokenCount == null)) {
			return;
		}

		long milliLRTCount = LiferayTokenConverter.convert(
			Map.of(
				TokenSource.VERTEX_INPUT, (long)inputTokenCount.intValue(),
				TokenSource.VERTEX_OUTPUT,
				(long)(totalTokenCount - inputTokenCount)));

		try {
			QuotaUtil.updateUsage(
				_companyId, milliLRTCount, totalTokenCount, _userId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AIHubQuotaChatModelListener.class);

	private final long _companyId;
	private final long _userId;

}