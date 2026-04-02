/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {BotIcon} from './Icons';

interface LogoProps {
	className: string;
	logoUrl: string;
}

export default function Logo({className, logoUrl}: LogoProps) {
	return (
		<div className={className}>
			{logoUrl ? <img alt="" src={logoUrl} /> : <BotIcon />}
		</div>
	);
}
