/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import List from '@clayui/list';
import {AssigneeAvatar} from '@liferay/object-dynamic-data-mapping-form-field-type';
import {fetch} from 'frontend-js-web';
import React, {
	useCallback,
	useEffect,
	useImperativeHandle,
	useState,
} from 'react';

export const UPDATE_HISTORY = 'cmp-update-history';

export enum EventType {
	ADD = 'ADD',
	UPDATE = 'UPDATE',
}

type AuditFieldChange = {
	name: string;
	newValue: any;
	oldValue: any;
};

type Creator = {
	additionalName: string;
	contentType: string;
	externalReferenceCode: string;
	familyName: string;
	givenName: string;
	id: number;
	image?: string;
	name: string;
};

export type AuditEvent<T> = {
	auditFieldChanges?: AuditFieldChange[];
	creator: Creator;
	dateCreated: string;
	eventType: T | EventType;
};

type Data<T> = {
	auditEvents: AuditEvent<T>[];
};

export function joinWithAnd(items: string[]) {
	if (!items?.length) {
		return '';
	}

	return new Intl.ListFormat(Liferay.ThemeDisplay.getBCP47LanguageId(), {
		style: 'long',
		type: 'conjunction',
	}).format(items);
}

export interface HistoryHandle {
	refresh: () => void;
}

export default function History<T>({
	apiURL,
	getAuditEventLabel,
	innerRef,
}: {
	apiURL: string;
	getAuditEventLabel: (auditEvent: AuditEvent<T>) => React.ReactNode;
	innerRef?: React.RefObject<HistoryHandle>;
}) {
	const [auditEvents, setAuditEvents] = useState<AuditEvent<T>[]>([]);

	const fetchAuditEvents = useCallback(async () => {
		fetch(apiURL, {
			method: 'GET',
		}).then(async (response: Response) => {
			const data = (await response.json()) as Data<T>;

			setAuditEvents(data.auditEvents);
		});
	}, [apiURL]);

	useEffect(() => {
		fetchAuditEvents();
	}, [fetchAuditEvents]);

	useEffect(() => {
		Liferay.on(UPDATE_HISTORY, fetchAuditEvents);

		return () => {
			Liferay.detach(UPDATE_HISTORY, fetchAuditEvents);
		};
	}, [fetchAuditEvents]);

	useImperativeHandle(innerRef, () => ({
		refresh() {
			fetchAuditEvents();
		},
	}));

	return (
		<div className="history-container">
			<List>
				{auditEvents
					.filter(({auditFieldChanges, eventType}) => {
						return (
							eventType !== EventType.UPDATE ||
							!!auditFieldChanges?.length
						);
					})
					.map((auditEvent, index) => (
						<List.Item className="border-0" flex key={index}>
							<List.ItemField>
								<AssigneeAvatar
									image={auditEvent.creator?.image}
									name={auditEvent.creator?.name || ''}
								/>
							</List.ItemField>

							<List.ItemField expand>
								<List.ItemTitle className="text-weight-normal">
									{getAuditEventLabel(auditEvent)}
								</List.ItemTitle>

								<List.ItemText>
									{new Date(
										auditEvent.dateCreated
									).toLocaleString()}
								</List.ItemText>
							</List.ItemField>
						</List.Item>
					))}
			</List>
		</div>
	);
}
