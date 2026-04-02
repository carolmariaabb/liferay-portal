/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 *
 * Standalone, Liferay-agnostic AI Hub Chat Widget.
 *
 * Usage:
 *   <script
 *     id="aihub-chat-widget-script"
 *     src="widget.min.js"
 *     data-api-url="https://your-instance.com"
 *     data-account-id="12345"
 *     data-chatbot-erc="MY_CHATBOT"
 *     data-user-email="user@example.com"
 *     data-user-name="Jane Doe"
 *     data-privacy-policy-url="https://example.com/privacy"
 *   ></script>
 */

import React from 'react';
import {createRoot} from 'react-dom/client';

import {setApiUrl} from './api';
import ChatWidget from './components/ChatWidget';
import {WidgetConfig} from './types';
import './widget.css';

const WIDGET_ID = 'aihub-chat-widget';

if (!document.getElementById(WIDGET_ID)) {
	const scriptTag = document.getElementById('aihub-chat-widget-script');

	if (!scriptTag) {
		console.error(
			'[AI Hub Chat] Script tag with id="aihub-chat-widget-script" not found.'
		);
	}
	else {
		const accountId =
			scriptTag.getAttribute('data-account-id') || '';

		if (accountId) {
			let quickActions: string[] = [];

			try {
				const qa =
					scriptTag.getAttribute('data-quick-actions');

				if (qa) {
					quickActions = JSON.parse(qa) as string[];
				}
			}
			catch {
				console.warn(
					'[AI Hub Chat] Invalid data-quick-actions JSON.'
				);
			}

			const config: WidgetConfig = {
				accountId,
				accentColor:
					scriptTag.getAttribute('data-accent-color') ||
					'#0b5fff',
				apiUrl:
					scriptTag.getAttribute('data-api-url') ||
					window.location.origin,
				chatbotERC:
					scriptTag.getAttribute('data-chatbot-erc') || '',
				introMessage:
					scriptTag.getAttribute('data-intro-message') || '',
				logoUrl:
					scriptTag.getAttribute('data-logo-url') || '',
				placeholder:
					scriptTag.getAttribute('data-placeholder') || '',
				privacyPolicyUrl:
					scriptTag.getAttribute('data-privacy-policy-url') ||
					'',
				quickActions,
				subtitle:
					scriptTag.getAttribute('data-subtitle') ||
					'AI Assistant',
				title:
					scriptTag.getAttribute('data-title') ||
					'AI Assistant',
				userEmail:
					scriptTag.getAttribute('data-user-email') || '',
				userName:
					scriptTag.getAttribute('data-user-name') || '',
			};

			const container = document.createElement('div');

			container.id = WIDGET_ID;

			if (config.accentColor !== '#0b5fff') {
				container.style.setProperty(
					'--aihub-accent',
					config.accentColor
				);
			}

			document.body.appendChild(container);

			setApiUrl(config.apiUrl);

			createRoot(container).render(<ChatWidget config={config} />);
		}
	}
}
