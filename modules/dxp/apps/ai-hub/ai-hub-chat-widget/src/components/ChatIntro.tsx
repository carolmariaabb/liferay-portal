/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import Logo from './Logo';

interface ChatIntroProps {
	introMessage: string;
	logoUrl: string;
	subtitle: string;
	title: string;
}

export default function ChatIntro({
	introMessage,
	logoUrl,
	subtitle,
	title,
}: ChatIntroProps) {
	return (
		<div className="aihub-intro">
			<Logo className="aihub-intro-logo" logoUrl={logoUrl} />

			<div className="aihub-intro-name">{title}</div>

			<div className="aihub-intro-badge">{subtitle}</div>

			<p className="aihub-intro-text">
				{introMessage || 'Hi! How can I help you today?'}
			</p>
		</div>
	);
}
