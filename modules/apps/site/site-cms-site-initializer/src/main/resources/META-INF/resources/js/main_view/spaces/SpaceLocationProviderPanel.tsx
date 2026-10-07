/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput, ClayRadio} from '@clayui/form';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import FieldWrapper from '../../common/components/forms/FieldWrapper';
import SpacePanel from './SpacePanel';

export const GOOGLE_MAPS = 'GoogleMaps';
export const OPEN_STREET_MAP = 'OpenStreetMap';


export default function SpaceLocationProviderPanel({
	errorMessage,
	googleMapsAPIKey,
	mapProviderKey,
	onChangeAPIKey,
	onChangeMapProviderKey,
	storedAPIKey,
}: {
	errorMessage?: string;
	googleMapsAPIKey: string;
	mapProviderKey: string;
	onChangeAPIKey: (value: string) => void;
	onChangeMapProviderKey: (value: string) => void;
	storedAPIKey?: string;
}) {
	const [visible, setVisible] = useState(false);

	const masked =
		!visible && !!storedAPIKey && googleMapsAPIKey === storedAPIKey;

	return (
		<SpacePanel title={Liferay.Language.get('location-provider')}>
			<>
				<p className="mb-4">
					{Liferay.Language.get(
						'choose-the-map-provider-used-by-location-fields-on-assets-in-this-space'
					)}
				</p>

				<ClayForm.Group>
					<label className="d-block">
						{Liferay.Language.get('provider')}
					</label>

					<ClayRadio
						checked={mapProviderKey === OPEN_STREET_MAP}
						label={Liferay.Language.get('openstreetmap')}
						name="mapProviderKey"
						onChange={() => onChangeMapProviderKey(OPEN_STREET_MAP)}
						value={OPEN_STREET_MAP}
					/>

					<p className="ml-4 small text-secondary">
						{Liferay.Language.get(
							'no-api-key-required.-uses-the-public-openstreetmap-tile-service'
						)}
					</p>

					<ClayRadio
						checked={mapProviderKey === GOOGLE_MAPS}
						label={Liferay.Language.get('google-maps')}
						name="mapProviderKey"
						onChange={() => onChangeMapProviderKey(GOOGLE_MAPS)}
						value={GOOGLE_MAPS}
					/>

					<p className="ml-4 small text-secondary">
						{Liferay.Language.get(
							'requires-a-google-cloud-api-key-with-billing-enabled'
						)}
					</p>

					{mapProviderKey === GOOGLE_MAPS && (
						<div className="ml-4">
							<FieldWrapper
								errorMessage={errorMessage}
								feedbackId="feedback-googleMapsAPIKey"
								fieldId="googleMapsAPIKey"
								label={Liferay.Language.get('api-key')}
								required
							>
								<ClayInput.Group>
									<ClayInput.GroupItem prepend>
										<ClayInput
											aria-describedby={
												errorMessage
													? 'feedback-googleMapsAPIKey'
													: undefined
											}
											id="googleMapsAPIKey"
											insetAfter
											name="googleMapsAPIKey"
											onChange={({target: {value}}) =>
												onChangeAPIKey(value)
											}
											readOnly={masked}
											required
											type={
												visible || masked
													? 'text'
													: 'password'
											}
											value={
												masked
													? '•'.repeat(googleMapsAPIKey.length)
													: googleMapsAPIKey
											}
										/>

										<ClayInput.GroupInsetItem after>
											<ClayButtonWithIcon
												aria-label={sub(
													Liferay.Language.get(
														visible
															? 'hide-x'
															: 'show-x'
													),
													[
														Liferay.Language.get(
															'api-key'
														),
													]
												)}
												displayType="unstyled"
												onClick={() =>
													setVisible(!visible)
												}
												symbol={
													visible ? 'hidden' : 'view'
												}
											/>
										</ClayInput.GroupInsetItem>
									</ClayInput.GroupItem>
								</ClayInput.Group>
							</FieldWrapper>

							<p className="small text-secondary">
								{Liferay.Language.get(
									'enable-the-maps-javascript-api-places-api-and-geocoding-api-for-this-key-in-google-cloud'
								)}
							</p>
						</div>
					)}
				</ClayForm.Group>
			</>
		</SpacePanel>
	);
}
