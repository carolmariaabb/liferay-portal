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

package com.liferay.dynamic.data.mapping.internal.data.engine.content.type;

import com.liferay.data.engine.content.type.DataDefinitionContentType;
import com.liferay.dynamic.data.mapping.constants.DDMConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	immediate = true, property = "content.type=forms",
	service = DataDefinitionContentType.class
)
public class DDMFormDataDefinitionContentType
	implements DataDefinitionContentType {

	@Override
	public boolean allowEmptyDataDefinition() {
		return true;
	}

	@Override
	public long getClassNameId() {
		return _portal.getClassNameId(DDMFormInstance.class);
	}

	@Override
	public String getContentType() {
		return "forms";
	}

	@Override
	public String getPortletResourceName() {
		return DDMConstants.RESOURCE_NAME;
	}

	@Reference
	private Portal _portal;

}