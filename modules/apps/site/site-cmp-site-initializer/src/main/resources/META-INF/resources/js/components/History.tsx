/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import TaskHistory from './task/TaskHistory';

export default function History({
	apiURL,
	objectDefinitionExternalReferenceCode,
}: {
	apiURL: string;
	objectDefinitionExternalReferenceCode: string;
}) {
	if (objectDefinitionExternalReferenceCode === 'L_CMP_TASK') {
		return TaskHistory({apiURL});
	}
}
