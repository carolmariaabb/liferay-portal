/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.model;

import com.liferay.ai.hub.quota.util.QuotaUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Guilherme Camacho
 */
public class AIHubQuotaChatModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testOnErrorDoesNotDebit() {
		AIHubQuotaChatModelListener aiHubQuotaChatModelListener =
			new AIHubQuotaChatModelListener(_COMPANY_ID, _USER_ID);

		ChatModelErrorContext chatModelErrorContext = Mockito.mock(
			ChatModelErrorContext.class);

		Mockito.when(
			chatModelErrorContext.error()
		).thenReturn(
			new RuntimeException()
		);

		try (MockedStatic<QuotaUtil> quotaUtilMockedStatic = Mockito.mockStatic(
				QuotaUtil.class)) {

			aiHubQuotaChatModelListener.onError(chatModelErrorContext);

			quotaUtilMockedStatic.verifyNoInteractions();
		}
	}

	@Test
	public void testOnResponseDebitsWithoutThoughts() throws Exception {
		AIHubQuotaChatModelListener aiHubQuotaChatModelListener =
			new AIHubQuotaChatModelListener(_COMPANY_ID, _USER_ID);

		ChatModelResponseContext chatModelResponseContext = _mock(400, 120, 0);

		try (MockedStatic<QuotaUtil> quotaUtilMockedStatic = Mockito.mockStatic(
				QuotaUtil.class)) {

			aiHubQuotaChatModelListener.onResponse(chatModelResponseContext);

			quotaUtilMockedStatic.verify(
				() -> QuotaUtil.updateUsage(_COMPANY_ID, 598L, 520L, _USER_ID));
		}
	}

	@Test
	public void testOnResponseDebitsWithThoughtsAsOutputRate()
		throws Exception {

		AIHubQuotaChatModelListener aiHubQuotaChatModelListener =
			new AIHubQuotaChatModelListener(_COMPANY_ID, _USER_ID);

		ChatModelResponseContext chatModelResponseContext = _mock(
			1560, 200, 150);

		try (MockedStatic<QuotaUtil> quotaUtilMockedStatic = Mockito.mockStatic(
				QuotaUtil.class)) {

			aiHubQuotaChatModelListener.onResponse(chatModelResponseContext);

			quotaUtilMockedStatic.verify(
				() -> QuotaUtil.updateUsage(
					_COMPANY_ID, 2000L, 1910L, _USER_ID));
		}
	}

	@Test
	public void testOnResponseSkipsWhenChatResponseIsNull() {
		AIHubQuotaChatModelListener aiHubQuotaChatModelListener =
			new AIHubQuotaChatModelListener(_COMPANY_ID, _USER_ID);

		ChatModelResponseContext chatModelResponseContext = Mockito.mock(
			ChatModelResponseContext.class);

		Mockito.when(
			chatModelResponseContext.chatResponse()
		).thenReturn(
			null
		);

		try (MockedStatic<QuotaUtil> quotaUtilMockedStatic = Mockito.mockStatic(
				QuotaUtil.class)) {

			aiHubQuotaChatModelListener.onResponse(chatModelResponseContext);

			quotaUtilMockedStatic.verifyNoInteractions();
		}
	}

	@Test
	public void testOnResponseSkipsWhenTokenCountsAreNull() {
		AIHubQuotaChatModelListener aiHubQuotaChatModelListener =
			new AIHubQuotaChatModelListener(_COMPANY_ID, _USER_ID);

		TokenUsage tokenUsage = Mockito.mock(TokenUsage.class);

		Mockito.when(
			tokenUsage.inputTokenCount()
		).thenReturn(
			null
		);

		Mockito.when(
			tokenUsage.totalTokenCount()
		).thenReturn(
			null
		);

		ChatResponse chatResponse = Mockito.mock(ChatResponse.class);

		Mockito.when(
			chatResponse.tokenUsage()
		).thenReturn(
			tokenUsage
		);

		ChatModelResponseContext chatModelResponseContext = Mockito.mock(
			ChatModelResponseContext.class);

		Mockito.when(
			chatModelResponseContext.chatResponse()
		).thenReturn(
			chatResponse
		);

		try (MockedStatic<QuotaUtil> quotaUtilMockedStatic = Mockito.mockStatic(
				QuotaUtil.class)) {

			aiHubQuotaChatModelListener.onResponse(chatModelResponseContext);

			quotaUtilMockedStatic.verifyNoInteractions();
		}
	}

	@Test
	public void testOnResponseSkipsWhenTokenUsageIsNull() {
		AIHubQuotaChatModelListener aiHubQuotaChatModelListener =
			new AIHubQuotaChatModelListener(_COMPANY_ID, _USER_ID);

		ChatResponse chatResponse = Mockito.mock(ChatResponse.class);

		Mockito.when(
			chatResponse.tokenUsage()
		).thenReturn(
			null
		);

		ChatModelResponseContext chatModelResponseContext = Mockito.mock(
			ChatModelResponseContext.class);

		Mockito.when(
			chatModelResponseContext.chatResponse()
		).thenReturn(
			chatResponse
		);

		try (MockedStatic<QuotaUtil> quotaUtilMockedStatic = Mockito.mockStatic(
				QuotaUtil.class)) {

			aiHubQuotaChatModelListener.onResponse(chatModelResponseContext);

			quotaUtilMockedStatic.verifyNoInteractions();
		}
	}

	private ChatModelResponseContext _mock(
		int inputTokenCount, int outputTokenCount, int thoughtsTokenCount) {

		TokenUsage tokenUsage = Mockito.mock(TokenUsage.class);

		Mockito.when(
			tokenUsage.inputTokenCount()
		).thenReturn(
			inputTokenCount
		);

		Mockito.when(
			tokenUsage.totalTokenCount()
		).thenReturn(
			inputTokenCount + outputTokenCount + thoughtsTokenCount
		);

		ChatResponse chatResponse = Mockito.mock(ChatResponse.class);

		Mockito.when(
			chatResponse.tokenUsage()
		).thenReturn(
			tokenUsage
		);

		ChatModelResponseContext chatModelResponseContext = Mockito.mock(
			ChatModelResponseContext.class);

		Mockito.when(
			chatModelResponseContext.chatResponse()
		).thenReturn(
			chatResponse
		);

		return chatModelResponseContext;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _USER_ID = RandomTestUtil.randomLong();

}