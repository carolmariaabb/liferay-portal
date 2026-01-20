/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DateRenderer, IInternalRenderer} from '@liferay/frontend-data-set-web';
import {AssigneeValue} from '@liferay/object-dynamic-data-mapping-form-field-type';
import {
	ACTIONS,
	ActionItem,
	AssignToModalContent,
	SimpleActionLinkRenderer,
	TransitionWorkflowStateModalContent,
	UpdateDueDateModalContent,
	addOnClickToCreationMenuItems,
	deleteItemAction,
} from '@liferay/site-cms-site-initializer';
import React from 'react';

import {openCMPModal} from '../../utils/openCMPModal';
import StateLabel from '../StateLabel';
import EditAssigneeModalContent from '../modal/EditAssigneeModalContent';
import AssigneeRenderer from './cell_renderers/AssigneeRenderer';

const _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN =
	'com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken';

type action = {
	data: {
		id: string;
	};
};

type AssigneeRole = {
	name: string;
};

interface ItemData {
	embedded: {
		assignTo: AssigneeValue;
		assigneePerson?: {
			name: string;
		};
		assigneeRoles: AssigneeRole[];
		dateDue: string;
		dueDate: string;
		id: number;
		objectReviewed: {
			assetTitle: string;
		};
		r_cmpProjectToCMPTasks_c_cmpProject: {
			title: string;
		};
		state: {
			key: string;
			name: string;
		};
		title: string;
	};
	entryClassName: string;
}

function getAssignee(itemData: ItemData) {
	if (itemData.entryClassName === _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN) {
		if (itemData.embedded.assigneePerson) {
			return itemData.embedded.assigneePerson.name;
		}

		return itemData.embedded.assigneeRoles
			.map((assigneeRole) => assigneeRole.name)
			.join(', ');
	}

	return itemData.embedded.assignTo.name;
}

function getDueDate(itemData: ItemData) {
	if (itemData.entryClassName === _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN) {
		return itemData.embedded.dateDue;
	}

	return itemData.embedded.dueDate;
}

function getProject(itemData: ItemData) {
	if (itemData.entryClassName === _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN) {
		return '-';
	}

	return itemData.embedded.r_cmpProjectToCMPTasks_c_cmpProject.title;
}

function getStateLabel(itemData: ItemData) {
	if (itemData.entryClassName === _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN) {
		return '-';
	}

	return StateLabel(itemData.embedded.state);
}

function getSimpleActionLinkRenderer({
	actions,
	itemData,
	options,
}: {
	actions: ActionItem[];
	itemData: ItemData;
	options: {actionId: string};
}) {
	if (itemData.entryClassName === _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN) {
		return SimpleActionLinkRenderer({
			actions,
			itemData,
			options: {
				actionId: 'actionLinkWorkflowTask',
			},
			value: itemData.embedded.objectReviewed.assetTitle,
		});
	}

	return SimpleActionLinkRenderer({
		actions,
		itemData,
		options,
		value: itemData.embedded.title,
	});
}

const WORKFLOW_TASK_MODALS: Record<
	string,
	(baseProps: {
		closeModal: () => void;
		dueDate: string;
		loadData: () => Promise<void>;
		workflowTaskId: number;
	}) => JSX.Element
> = {
	approveWorkflowTask: (props) => (
		<TransitionWorkflowStateModalContent
			{...props}
			transitionName="approve"
		/>
	),
	assignToMeWorkflowTask: (props) => (
		<AssignToModalContent {...props} assignable={false} />
	),
	assignToWorkflowTask: (props) => (
		<AssignToModalContent {...props} assignable={true} />
	),
	rejectWorkflowTask: (props) => (
		<TransitionWorkflowStateModalContent
			{...props}
			transitionName="reject"
		/>
	),
	updateDueDateWorkflowTask: (props) => (
		<UpdateDueDateModalContent {...props} />
	),
};

export default function TasksFDSPropsTransformer({
	creationMenu,
	itemsActions = [],
	...otherProps
}: {
	creationMenu: any;
	itemsActions?: any[];
}) {
	return {
		...otherProps,
		creationMenu: {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: ({itemData}: {itemData: ItemData}) =>
						AssigneeRenderer({assignee: getAssignee(itemData)}),
					name: 'assigneeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}: {itemData: ItemData}) =>
						DateRenderer({value: getDueDate(itemData)}),
					name: 'dueDateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}: {itemData: ItemData}) =>
						getProject(itemData),
					name: 'projectTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({
						actions,
						itemData,
						options,
					}: {
						actions: ActionItem[];
						itemData: ItemData;
						options: {actionId: string};
					}) =>
						getSimpleActionLinkRenderer({
							actions,
							itemData,
							options,
						}),
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}: {itemData: ItemData}) =>
						getStateLabel(itemData),
					name: 'stateTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		itemsActions: itemsActions.map((action) => {
			if (action?.data?.id === 'delete') {
				return {
					...action,
					className: 'text-danger',
				};
			}

			return action;
		}),
		async onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: action;
			itemData: ItemData;
			loadData: () => Promise<void>;
		}) {
			if (action?.data?.id === 'delete') {
				await deleteItemAction(itemData, loadData);
			}
			else if (action?.data?.id === 'assign-to') {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<EditAssigneeModalContent
							closeModal={closeModal}
							loadData={loadData}
							taskId={String(itemData.embedded.id)}
							value={itemData.embedded.assignTo}
						/>
					),
					size: 'md',
				});
			}

			if (
				itemData.entryClassName ===
				_CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
			) {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						WORKFLOW_TASK_MODALS[action.data.id]({
							closeModal,
							dueDate: itemData.embedded.dateDue,
							loadData,
							workflowTaskId: itemData.embedded.id,
						}),
					size: 'md',
				});
			}
		},
	};
}
