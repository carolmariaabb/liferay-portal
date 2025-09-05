/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import Form from '@clayui/form';
import React from 'react';

export default function Assignee({label}: any) {
	return (
		<div className="p-4">
			<Form.Group>
				<label
					htmlFor="clay-autocomplete-1"
					id="clay-autocomplete-label-1"
				>
					{label}
				</label>

				<Autocomplete
					aria-labelledby="clay-autocomplete-label-1"
					defaultItems={['one', 'two', 'three', 'four', 'five']}
					id="clay-autocomplete-1"
					menuTrigger="focus"
					messages={{
						loading: 'Loading...',
						notFound: 'No results found',
					}}
					placeholder="Enter a number from One to Five"
				>
					{(item) => (
						<Autocomplete.Item key={item}>{item}</Autocomplete.Item>
					)}
				</Autocomplete>
			</Form.Group>
		</div>
	);
}
