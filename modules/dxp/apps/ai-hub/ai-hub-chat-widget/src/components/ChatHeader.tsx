/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {CloseIcon} from './Icons';
import Logo from './Logo';

interface ChatHeaderProps {
	logoUrl: string;
	onClose: () => void;
	subtitle: string;
	title: string;
}

export default function ChatHeader({
	logoUrl,
	onClose,
	subtitle,
	title,
}: ChatHeaderProps) {
	return (
		<div className="aihub-header">
			<Logo className="aihub-header-logo" logoUrl={logoUrl} />

			<div className="aihub-header-info">
				<div className="aihub-header-title">{title}</div>

				<div className="aihub-header-subtitle">{subtitle}</div>
			</div>

			<button
				aria-label="Close"
				className="aihub-header-close"
				onClick={onClose}
			>
				<CloseIcon />
			</button>
		</div>
	);
}
