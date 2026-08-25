/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {IBulkActionFDSData} from '@liferay/site-cms-site-initializer';
import React, {useState} from 'react';

import {bulkChangeWorkflowTaskTransistion} from '../../utils/api';
import {
	displayBulkStateSuccessToast,
	displayErrorToast,
} from '../../utils/toastUtil';

type FDSItem = {embedded: {id: number}};

export default function BulkChangeWorkflowTransitionModalContent({
	closeModal,
	loadData,
	selectedData,
}: {
	closeModal: () => void;
	loadData: () => void;
	selectedData: IBulkActionFDSData;
}) {
	const [submitDisabled, setSubmitDisabled] = useState(false);

	const items = (selectedData.items ?? []) as FDSItem[];

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		setSubmitDisabled(true);

		const {error} = await bulkChangeWorkflowTaskTransistion(
			items.map((item) => ({
				comment: 'test',
				transitionName: 'approve',
				workflowTaskId: item.embedded.id,
			}))
		);

		if (!error) {
			displayBulkStateSuccessToast(items.length);

			closeModal();

			loadData();
		}
		else {
			displayErrorToast();

			setSubmitDisabled(false);
		}
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('update-state')}
			</ClayModal.Header>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={submitDisabled}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
