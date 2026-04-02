/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface AuthToken {
	accessToken: string;
	serviceURL: string;
	userToken: string;
}

export interface ChatMessage {
	sender: 'assistant' | 'error' | 'user';
	text: string;
}

export interface ChatbotConfig {
	introMessage?: string;
	placeholderMessage?: string;
	title?: string;
}

export interface WidgetConfig {
	accountId: string;
	accentColor: string;
	apiUrl: string;
	chatbotERC: string;
	introMessage: string;
	logoUrl: string;
	placeholder: string;
	privacyPolicyUrl: string;
	quickActions: string[];
	subtitle: string;
	title: string;
	userEmail: string;
	userName: string;
}
