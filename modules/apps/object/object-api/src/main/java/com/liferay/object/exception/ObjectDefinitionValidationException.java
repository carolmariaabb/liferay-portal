/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.exception;

import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

/**
 * @author Caio Farias
 */
public class ObjectDefinitionValidationException extends PortalException {

	public List<ValidationError> getValidationErrors() {
		return _validationErrors;
	}

	public void setValidationErrors(List<ValidationError> validationErrors) {
		_validationErrors = validationErrors;
	}

	public static class ValidationError {

		public ValidationError(
			String className, String errorMessage, String exceptionClassName,
			String propertyName, Object propertyValue) {

			_className = className;
			_errorMessage = errorMessage;
			_exceptionClassName = exceptionClassName;
			_propertyName = propertyName;
			_propertyValue = propertyValue;
		}

		public String getClassName() {
			return _className;
		}

		public String getErrorMessage() {
			return _errorMessage;
		}

		public String getExceptionClassName() {
			return _exceptionClassName;
		}

		public String getPropertyName() {
			return _propertyName;
		}

		public Object getPropertyValue() {
			return _propertyValue;
		}

		private final String _className;
		private final String _errorMessage;
		private final String _exceptionClassName;
		private final String _propertyName;
		private final Object _propertyValue;

	}

	private List<ValidationError> _validationErrors;

}