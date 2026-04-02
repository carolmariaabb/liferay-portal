/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {getInitials} from '../utils/markdown';

interface UserMessageProps {
	text: string;
	userName: string;
}

export default function UserMessage({text, userName}: UserMessageProps) {
	return (
		<div className="aihub-msg-user">
			<span className="aihub-msg-user-text">{text}</span>

			<div className="aihub-msg-user-avatar">
				<span className="aihub-msg-user-initials">
					{getInitials(userName)}
				</span>
			</div>
		</div>
	);
}
