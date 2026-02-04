/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useRef} from 'react';

import History, {
	AuditEvent,
	EventType,
	HistoryHandle,
	joinWithAnd,
} from '../History';

const FIELD_NAME: {[key: string]: string} = {
	description: Liferay.Language.get('description'),
	dueDate: Liferay.Language.get('due-date'),
	r_userToCMPProjectManager_userId: Liferay.Language.get('manager'),
	r_userToCMPProjectSponsor_userId: Liferay.Language.get('sponsor'),
	state: Liferay.Language.get('state'),
	title: Liferay.Language.get('title'),
};

export default function ProjectHistory({apiURL}: {apiURL: string}) {
	const historyRef = useRef<HistoryHandle>(null);

	const getAuditEventLabel = (auditEvent: AuditEvent<EventType>) => {
		if (auditEvent.eventType === EventType.ADD) {
			return sub(Liferay.Language.get('x-created-a-x'), [
				<strong key="creatorName">{auditEvent.creator?.name}</strong>,
				<strong key="type">{Liferay.Language.get('project')}</strong>,
			]);
		}

		return sub(Liferay.Language.get('x-updated-the-x'), [
			<strong key="creatorName">{auditEvent.creator?.name}</strong>,
			<strong key="changedFields">
				{joinWithAnd(
					auditEvent.auditFieldChanges?.map(
						(auditFieldChange) =>
							FIELD_NAME[auditFieldChange.name] ??
							auditFieldChange.name
					) || []
				)}
			</strong>,
		]);
	};

	return (
		<History
			apiURL={apiURL}
			getAuditEventLabel={getAuditEventLabel}
			innerRef={historyRef}
		/>
	);
}
