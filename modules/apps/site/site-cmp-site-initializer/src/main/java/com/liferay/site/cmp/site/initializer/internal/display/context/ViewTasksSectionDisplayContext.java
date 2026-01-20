/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cmp.site.initializer.internal.util.ActionUtil;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Albuquerque
 */
public class ViewTasksSectionDisplayContext extends BaseSectionDisplayContext {

	public ViewTasksSectionDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		super(httpServletRequest, objectDefinition);

		_assetEntry = (AssetEntry)httpServletRequest.getAttribute(
			WebKeys.LAYOUT_ASSET_ENTRY);
	}

	public String getAPIURL() {
		StringBundler sb = new StringBundler(10);

		sb.append("/o/search/v1.0/search?emptySearch=true&");
		sb.append("filter=objectDefinitionId eq ");
		sb.append(objectDefinition.getObjectDefinitionId());

		if (_assetEntry != null) {
			sb.append(" and scopeGroupId eq ");
			sb.append(_assetEntry.getGroupId());
		}
		else {
			sb.append(" or keywords/any(k:startswith(k, '");
			sb.append(objectDefinition.getExternalReferenceCode());
			sb.append("'))&entryClassNames=");
			sb.append(HtmlUtil.escapeURL(objectDefinition.getClassName()));
			sb.append(StringPool.COMMA);
			sb.append(_CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN);
		}

		sb.append("&nestedFields=cmpProjectToCMPTasks,embedded");

		return sb.toString();
	}

	public CreationMenu getCreationMenu() {
		if (_assetEntry == null) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "createTask");
				dropdownItem.putData(
					"objectDefinitionId",
					String.valueOf(objectDefinition.getObjectDefinitionId()));
				dropdownItem.putData(
					"redirect",
					StringBundler.concat(
						themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
						GroupConstants.CMS_FRIENDLY_URL,
						"/add_task?objectDefinitionId=",
						objectDefinition.getObjectDefinitionId(), "&plid=",
						themeDisplay.getPlid(), "&projectGroupId=",
						_assetEntry.getGroupId(), "&projectId=",
						_assetEntry.getClassPK(), "&redirect=",
						themeDisplay.getURLCurrent()));
				dropdownItem.putData(
					"title",
					objectDefinition.getLabel(themeDisplay.getLocale()));
				dropdownItem.setIcon("forms");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "new-task"));
			}
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				httpServletRequest, "click-new-to-create-your-first-task")
		).put(
			"image", "/states/cmp_empty_state_tasks.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-tasks-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				StringBundler.concat(
					ActionUtil.getBaseEditTaskURL(
						objectDefinition, themeDisplay),
					"{embedded.id}?redirect=", themeDisplay.getURLCurrent()),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", "update", null,
				HashMapBuilder.<String, Object>put(
					"entryClassName", objectDefinition.getClassName()
				).build()),
			new FDSActionDropdownItem(
				StringBundler.concat(
					ActionUtil.getBaseViewTaskURL(
						objectDefinition, themeDisplay),
					"{embedded.id}?redirect=", themeDisplay.getURLCurrent()),
				"view", "actionLink",
				LanguageUtil.get(httpServletRequest, "view"), null, "get", null,
				HashMapBuilder.<String, Object>put(
					"entryClassName", objectDefinition.getClassName()
				).build()),
			new FDSActionDropdownItem(
				StringPool.BLANK, null, "assign-to",
				LanguageUtil.get(httpServletRequest, "assign-to-..."), null,
				"get", null,
				HashMapBuilder.<String, Object>put(
					"entryClassName", objectDefinition.getClassName()
				).build()),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), null, "delete",
				null,
				HashMapBuilder.<String, Object>put(
					"entryClassName", objectDefinition.getClassName()
				).build()),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, PortletKeys.MY_WORKFLOW_TASK,
						ActionRequest.RENDER_PHASE)
				).setMVCPath(
					"/edit_workflow_task.jsp"
				).setRedirect(
					themeDisplay.getURLCurrent()
				).setParameter(
					"workflowTaskId", "{embedded.id}"
				).buildString(),
				"view", "actionLinkWorkflowTask",
				LanguageUtil.get(httpServletRequest, "view"), null, "get", null,
				HashMapBuilder.<String, Object>put(
					"entryClassName", _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
				).build()),
			new FDSActionDropdownItem(
				null, "check", "approveWorkflowTask",
				LanguageUtil.get(httpServletRequest, "approve"), null,
				"changeTransition", null,
				HashMapBuilder.<String, Object>put(
					"embedded.assignedToMe", true
				).put(
					"embedded.completed", false
				).put(
					"entryClassName", _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
				).build()),
			new FDSActionDropdownItem(
				null, null, "assignToMeWorkflowTask",
				LanguageUtil.get(httpServletRequest, "assign-to-me"), null,
				"assignToMe", null,
				HashMapBuilder.<String, Object>put(
					"embedded.assignedToMe", false
				).put(
					"embedded.completed", false
				).put(
					"entryClassName", _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
				).build()),
			new FDSActionDropdownItem(
				null, null, "assignToWorkflowTask",
				LanguageUtil.get(httpServletRequest, "assign-to-..."), null,
				"assignToUser", null,
				HashMapBuilder.<String, Object>put(
					"embedded.completed", false
				).put(
					"entryClassName", _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
				).build()),
			new FDSActionDropdownItem(
				null, "times", "rejectWorkflowTask",
				LanguageUtil.get(httpServletRequest, "reject"), null,
				"changeTransition", null,
				HashMapBuilder.<String, Object>put(
					"embedded.assignedToMe", true
				).put(
					"embedded.completed", false
				).put(
					"entryClassName", _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
				).build()),
			new FDSActionDropdownItem(
				null, "date-time", "updateDueDateWorkflowTask",
				LanguageUtil.get(httpServletRequest, "update-due-date"), null,
				"updateDueDate", null,
				HashMapBuilder.<String, Object>put(
					"embedded.completed", false
				).put(
					"entryClassName", _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
				).build()));
	}

	private static final String _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN =
		"com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken";

	private final AssetEntry _assetEntry;

}