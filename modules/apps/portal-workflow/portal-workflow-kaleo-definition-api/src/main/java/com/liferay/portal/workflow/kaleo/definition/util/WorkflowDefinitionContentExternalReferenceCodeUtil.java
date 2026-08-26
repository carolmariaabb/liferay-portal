/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowException;

/**
 * @author Javier Gamarra
 */
public class WorkflowDefinitionContentExternalReferenceCodeUtil {

	public static String enrich(String content, long companyId)
		throws WorkflowException {

		if (Validator.isNull(content) ||
			!content.startsWith(StringPool.OPEN_CURLY_BRACE)) {

			return content;
		}

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(content);

			_enrich(jsonObject, companyId);

			return jsonObject.toString();
		}
		catch (JSONException jsonException) {
			throw new WorkflowException(
				"Unable to enrich content with external reference codes",
				jsonException);
		}
	}

	private static void _enrich(JSONObject jsonObject, long companyId) {
		JSONArray childNodesJSONArray = jsonObject.getJSONArray("#child-nodes");

		if (childNodesJSONArray == null) {
			return;
		}

		String tagName = jsonObject.getString("#tag-name");

		if (tagName.equals("role")) {
			_enrichRole(childNodesJSONArray, companyId);
		}

		for (int i = 0; i < childNodesJSONArray.length(); i++) {
			_enrich(childNodesJSONArray.getJSONObject(i), companyId);
		}
	}

	private static void _enrichRole(
		JSONArray childNodesJSONArray, long companyId) {

		String externalReferenceCode = _getChildValue(
			childNodesJSONArray, "external-reference-code");

		if (Validator.isNotNull(externalReferenceCode)) {
			Role role = RoleLocalServiceUtil.fetchRoleByExternalReferenceCode(
				externalReferenceCode, companyId);

			if (role != null) {
				_putChildValue(
					childNodesJSONArray, "role-id",
					String.valueOf(role.getRoleId()));

				return;
			}
		}

		long roleId = GetterUtil.getLong(
			_getChildValue(childNodesJSONArray, "role-id"));

		if (roleId == 0) {
			return;
		}

		Role role = RoleLocalServiceUtil.fetchRole(roleId);

		if ((role == null) ||
			Validator.isNull(role.getExternalReferenceCode())) {

			return;
		}

		_putChildValue(
			childNodesJSONArray, "external-reference-code",
			role.getExternalReferenceCode());
	}

	private static String _getChildValue(
		JSONArray childNodesJSONArray, String tagName) {

		for (int i = 0; i < childNodesJSONArray.length(); i++) {
			JSONObject childJSONObject = childNodesJSONArray.getJSONObject(i);

			if (tagName.equals(childJSONObject.getString("#tag-name"))) {
				return childJSONObject.getString("#value");
			}
		}

		return null;
	}

	private static void _putChildValue(
		JSONArray childNodesJSONArray, String tagName, String value) {

		for (int i = 0; i < childNodesJSONArray.length(); i++) {
			JSONObject childJSONObject = childNodesJSONArray.getJSONObject(i);

			if (tagName.equals(childJSONObject.getString("#tag-name"))) {
				childJSONObject.put("#value", value);

				return;
			}
		}

		childNodesJSONArray.put(
			JSONUtil.put(
				"#tag-name", tagName
			).put(
				"#value", value
			));
	}

}
