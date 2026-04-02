/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface QuickActionsProps {
	actions: string[];
	onAction: (label: string) => void;
}

export default function QuickActions({actions, onAction}: QuickActionsProps) {
	if (!actions.length) {
		return null;
	}

	return (
		<div className="aihub-quick-actions">
			{actions.map((label) => (
				<button
					className="aihub-quick-action"
					key={label}
					onClick={() => onAction(label)}
				>
					{label}
				</button>
			))}
		</div>
	);
}
