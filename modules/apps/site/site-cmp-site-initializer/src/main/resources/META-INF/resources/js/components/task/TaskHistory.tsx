/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FDS_EVENT} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef} from 'react';

import History, {
	AuditEvent,
	EventType,
	HistoryHandle,
	joinWithAnd,
} from '../History';

enum TaskEventType {
	CMP_ADD_ASSET = 'CMP_ADD_ASSET',
	CMP_REMOVE_ASSET = 'CMP_REMOVE_ASSET',
}

type TaskEvent = EventType | TaskEventType;

const RELATED_ASSETS_SECTION =
	'com.liferay.site.cms.site.initializer-relatedAssetsSection';

const FIELD_NAME: {[key: string]: string} = {
	assignTo: Liferay.Language.get('assignee'),
	description: Liferay.Language.get('description'),
	dueDate: Liferay.Language.get('due-date'),
	state: Liferay.Language.get('state'),
	title: Liferay.Language.get('title'),
};

export default function TaskHistory({apiURL}: {apiURL: string}) {
	const historyRef = useRef<HistoryHandle>(null);

	const getAuditEventLabel = (auditEvent: AuditEvent<TaskEvent>) => {
		if (auditEvent.eventType === EventType.ADD) {
			return sub(Liferay.Language.get('x-created-a-x'), [
				<strong key="creatorName">{auditEvent.creator?.name}</strong>,
				<strong key="type">{Liferay.Language.get('task')}</strong>,
			]);
		}
		else if (auditEvent.eventType === TaskEventType.CMP_ADD_ASSET) {
			return sub(Liferay.Language.get('x-added-the-asset-x'), [
				<strong key="creatorName">{auditEvent.creator?.name}</strong>,
				<strong key="changedField">
					{auditEvent.auditFieldChanges?.[0].name}
				</strong>,
			]);
		}
		else if (auditEvent.eventType === TaskEventType.CMP_REMOVE_ASSET) {
			return sub(Liferay.Language.get('x-removed-the-asset-x'), [
				<strong key="creatorName">{auditEvent.creator?.name}</strong>,
				<strong key="changedField">
					{auditEvent.auditFieldChanges?.[0].name}
				</strong>,
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

	useEffect(() => {
		const handleFDSDisplayUpdated = ({id}: {id: string}) => {
			if (id === RELATED_ASSETS_SECTION) {
				historyRef.current?.refresh();
			}
		};

		Liferay.on(FDS_EVENT.DISPLAY_UPDATED, handleFDSDisplayUpdated);

		return () => {
			Liferay.detach(FDS_EVENT.DISPLAY_UPDATED, handleFDSDisplayUpdated);
		};
	}, []);

	return (
		<History<TaskEvent>
			apiURL={apiURL}
			getAuditEventLabel={getAuditEventLabel}
			innerRef={historyRef}
		/>
	);
}
