/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import {ClayInput} from '@clayui/form';
import {useFormState} from 'data-engine-js-components-web';
import React, {useMemo, useState} from 'react';
import createNumberMask from 'text-mask-addons/dist/createNumberMask';
import {conformToMask} from 'vanilla-text-mask';

import {FieldBase} from '../FieldBase/ReactFieldBase.es';
import withConfirmationField from '../util/withConfirmationField.es';

const adaptiveMask = (rawValue, inputMaskFormat) => {
	const generateMask = (mask) => {
		const mandatorySize = mask.match(/9/g)?.length ?? 0;
		const nextZeroIndex = mask.indexOf('0');

		if (nextZeroIndex === -1) {
			return mask;
		}
		const inputNumbers = rawValue.match(/\d/g)?.length ?? 0;
		if (inputNumbers <= mandatorySize) {
			return mask.replaceAll('0', '');
		}

		return generateMask(mask.replace('0', '9'));
	};

	return [...generateMask(inputMaskFormat)].map((char) =>
		char === '9' ? /\d/ : char
	);
};

const getMaskedValue = (dataType, decimalSymbol, value, inputMaskFormat) => {
	let mask;

	if (inputMaskFormat) {
		mask = adaptiveMask(value, inputMaskFormat);
	}
	else {
		const config = {
			allowLeadingZeroes: true,
			allowNegative: true,
			includeThousandsSeparator: false,
			prefix: '',
		};

		if (dataType === 'double') {
			config.allowDecimal = true;
			config.decimalLimit = null;
			config.decimalSymbol = decimalSymbol;
		}
		mask = createNumberMask(config);

		if (typeof value === 'string') {
			if (!value) {
				return '';
			}
			value = value.replace(decimalSymbol, '.');
			if (dataType == 'integer' && value.includes(decimalSymbol)) {
				value = Number(value);
				value = Math.round(value);
			}
		}
		value = String(value).replace('.', decimalSymbol);
	}

	const {conformedValue} = conformToMask(value, mask, {
		guide: false,
		keepCharPositions: false,
		placeholderChar: '\u2000',
	});

	return conformedValue;
};

const Numeric = ({
	dataType = 'integer',
	defaultLanguageId,
	id,
	inputMaskFormat,
	localizedValue,
	name,
	onBlur,
	onChange,
	onFocus,
	placeholder,
	predefinedValue,
	readOnly,
	symbols: {decimalSymbol} = {decimalSymbol: '.'},
	value,
	...otherProps
}) => {
	const {editingLanguageId} = useFormState();
	const [currentValue, setCurrentValue] = useState();

	const formattedValue = useMemo(() => {
		const newValue =
			currentValue ??
			localizedValue?.[editingLanguageId] ??
			localizedValue?.[defaultLanguageId] ??
			value ??
			predefinedValue ??
			'';

		return getMaskedValue(
			dataType,
			decimalSymbol,
			newValue,
			inputMaskFormat
		);
	}, [
		currentValue,
		dataType,
		defaultLanguageId,
		editingLanguageId,
		inputMaskFormat,
		localizedValue,
		predefinedValue,
		decimalSymbol,
		value,
	]);

	return (
		<FieldBase
			{...otherProps}
			id={id}
			localizedValue={localizedValue}
			name={name}
			readOnly={readOnly}
		>
			<ClayInput
				dir={Liferay.Language.direction[editingLanguageId]}
				disabled={readOnly}
				id={id}
				lang={editingLanguageId}
				name={name}
				onBlur={onBlur}
				onChange={(event) => {
					setCurrentValue(event.target.value);
					onChange?.(event);
				}}
				onFocus={onFocus}
				placeholder={
					placeholder || inputMaskFormat?.replace(/\d/g, '_')
				}
				type="text"
				value={formattedValue}
			/>
		</FieldBase>
	);
};

export {Numeric};
export default withConfirmationField(Numeric);
