/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.quota;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.CountTokensResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

import com.liferay.ai.hub.internal.configuration.VertexAIConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.IOException;

import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class TokensUtil {

	public static long getTokensCount(long companyId, String text)
		throws PortalException {

		VertexAIConfiguration vertexAIConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				VertexAIConfiguration.class, companyId);

		String location = vertexAIConfiguration.location();
		String modelName = vertexAIConfiguration.modelName();

		if (Objects.equals(location, "global")) {
			location = "europe-central2";
			modelName = "gemini-2.5-flash";
		}

		try (VertexAI vertexAI = new VertexAI(
				vertexAIConfiguration.projectId(), location)) {

			GenerativeModel generativeModel = new GenerativeModel(
				modelName, vertexAI);

			CountTokensResponse countTokensResponse =
				generativeModel.countTokens(text);

			return countTokensResponse.getTotalTokens();
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

}