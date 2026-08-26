/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionLinkResource;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Victor Kammerer
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/workflow-definition-link.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE,
	service = WorkflowDefinitionLinkResource.class
)
public class WorkflowDefinitionLinkResourceImpl
	extends BaseWorkflowDefinitionLinkResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate
		<WorkflowDefinitionLink> {

	@Override
	public void delete(
			Collection<WorkflowDefinitionLink> workflowDefinitionLinks,
			Map<String, Serializable> parameters)
		throws Exception {

		for (WorkflowDefinitionLink workflowDefinitionLink :
				workflowDefinitionLinks) {

			com.liferay.portal.kernel.model.WorkflowDefinitionLink
				serviceBuilderWorkflowDefinitionLink =
					_workflowDefinitionLinkLocalService.
						fetchWorkflowDefinitionLinkByExternalReferenceCode(
							workflowDefinitionLink.getExternalReferenceCode(),
							_getGroupId(workflowDefinitionLink));

			if (serviceBuilderWorkflowDefinitionLink != null) {
				_workflowDefinitionLinkLocalService.
					deleteWorkflowDefinitionLink(
						serviceBuilderWorkflowDefinitionLink);
			}
		}
	}

	@Override
	public ExportImportDescriptor
		<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
			getExportImportDescriptor() {

		return new ExportImportDescriptor
			<com.liferay.portal.kernel.model.WorkflowDefinitionLink>() {

			@Override
			public String getKey() {
				return WorkflowDefinitionLinkResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "workflow-definition-links";
			}

			@Override
			public Class<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
				getModelClass() {

				return com.liferay.portal.kernel.model.WorkflowDefinitionLink.
					class;
			}

			@Override
			public String getPortletId() {
				return WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

		};
	}

	@Override
	public Page<WorkflowDefinitionLink>
			getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				contextCompany.getCompanyId(), externalReferenceCode);

		List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
			workflowDefinitionLinks =
				_workflowDefinitionLinkService.getWorkflowDefinitionLinks(
					contextCompany.getCompanyId(), workflowDefinition.getName(),
					workflowDefinition.getVersion());

		return Page.of(
			transform(
				ListUtil.subList(
					workflowDefinitionLinks, pagination.getStartPosition(),
					pagination.getEndPosition()),
				workflowDefinitionLink -> _toWorkflowDefinitionLink(
					workflowDefinitionLink)),
			pagination, workflowDefinitionLinks.size());
	}

	@Override
	public Page<WorkflowDefinitionLink> getWorkflowDefinitionLinksPage(
			Pagination pagination)
		throws Exception {

		long companyId = contextCompany.getCompanyId();

		return Page.of(
			transform(
				_workflowDefinitionLinkService.getWorkflowDefinitionLinks(
					companyId, pagination.getStartPosition(),
					pagination.getEndPosition()),
				workflowDefinitionLink -> _toWorkflowDefinitionLink(
					workflowDefinitionLink)),
			pagination,
			_workflowDefinitionLinkLocalService.getWorkflowDefinitionLinksCount(
				companyId));
	}

	@Override
	public Page<WorkflowDefinitionLink>
			getWorkflowDefinitionWorkflowDefinitionLinksPage(
				Long workflowDefinitionId, Pagination pagination)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				workflowDefinitionId);

		List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
			workflowDefinitionLinks =
				_workflowDefinitionLinkService.getWorkflowDefinitionLinks(
					contextCompany.getCompanyId(), workflowDefinition.getName(),
					workflowDefinition.getVersion());

		return Page.of(
			HashMapBuilder.put(
				"createBatch",
				addAction(
					ActionKeys.ADD_DEFINITION, workflowDefinitionId,
					"postWorkflowDefinitionWorkflowDefinitionLinkBatch",
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(
							"com.liferay.portal.workflow.kaleo.model." +
								"KaleoDefinition"))
			).build(),
			transform(
				ListUtil.subList(
					workflowDefinitionLinks, pagination.getStartPosition(),
					pagination.getEndPosition()),
				workflowDefinitionLink -> _toWorkflowDefinitionLink(
					workflowDefinitionLink)),
			pagination, workflowDefinitionLinks.size());
	}

	@Override
	public WorkflowDefinitionLink
			postWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink(
				String externalReferenceCode,
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		KaleoDefinition kaleoDefinition = _getOrAddEmptyKaleoDefinition(
			externalReferenceCode);

		return _toWorkflowDefinitionLink(
			_workflowDefinitionLinkService.addWorkflowDefinitionLink(
				workflowDefinitionLink.getExternalReferenceCode(),
				contextUser.getUserId(), contextCompany.getCompanyId(),
				_getGroupId(workflowDefinitionLink),
				workflowDefinitionLink.getClassName(), 0, 0,
				kaleoDefinition.getName(), kaleoDefinition.getVersion()));
	}

	@Override
	public WorkflowDefinitionLink postWorkflowDefinitionWorkflowDefinitionLink(
			Long workflowDefinitionId,
			WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(
				workflowDefinitionId);

		return _toWorkflowDefinitionLink(
			_workflowDefinitionLinkService.addWorkflowDefinitionLink(
				workflowDefinitionLink.getExternalReferenceCode(),
				contextUser.getUserId(), contextCompany.getCompanyId(),
				_getGroupId(workflowDefinitionLink),
				workflowDefinitionLink.getClassName(), 0, 0,
				workflowDefinition.getName(), workflowDefinition.getVersion()));
	}

	@Override
	public WorkflowDefinitionLink
			putWorkflowDefinitionLinkByExternalReferenceCode(
				String externalReferenceCode,
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		String workflowDefinitionName =
			workflowDefinitionLink.getWorkflowDefinitionName();
		Integer workflowDefinitionVersion =
			workflowDefinitionLink.getWorkflowDefinitionVersion();

		String workflowDefinitionExternalReferenceCode =
			workflowDefinitionLink.getWorkflowDefinitionExternalReferenceCode();

		if (Validator.isNotNull(workflowDefinitionExternalReferenceCode)) {
			KaleoDefinition kaleoDefinition = _getOrAddEmptyKaleoDefinition(
				workflowDefinitionExternalReferenceCode);

			workflowDefinitionName = kaleoDefinition.getName();
			workflowDefinitionVersion = kaleoDefinition.getVersion();
		}

		return _toWorkflowDefinitionLink(
			_workflowDefinitionLinkService.updateWorkflowDefinitionLink(
				externalReferenceCode, contextUser.getUserId(),
				contextCompany.getCompanyId(),
				_getGroupId(workflowDefinitionLink),
				workflowDefinitionLink.getClassName(), 0, 0,
				workflowDefinitionName, workflowDefinitionVersion));
	}

	private String _fetchWorkflowDefinitionExternalReferenceCode(
		com.liferay.portal.kernel.model.WorkflowDefinitionLink
			serviceBuilderWorkflowDefinitionLink) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(contextCompany.getCompanyId());

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.fetchKaleoDefinition(
				serviceBuilderWorkflowDefinitionLink.
					getWorkflowDefinitionName(),
				serviceContext);

		if (kaleoDefinition == null) {
			return null;
		}

		return kaleoDefinition.getExternalReferenceCode();
	}

	private long _getGroupId(WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		long groupId = GetterUtil.getLong(workflowDefinitionLink.getGroupId());

		if ((groupId != 0) ||
			Validator.isNull(
				workflowDefinitionLink.getGroupExternalReferenceCode())) {

			return groupId;
		}

		Group group = _groupService.fetchGroupByExternalReferenceCode(
			workflowDefinitionLink.getGroupExternalReferenceCode(),
			contextCompany.getCompanyId());

		if (group != null) {
			return group.getGroupId();
		}

		return 0;
	}

	private KaleoDefinition _getOrAddEmptyKaleoDefinition(
			String externalReferenceCode)
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(
					BatchEngineThreadLocal.isBatchImportInProcess())) {

			return _kaleoDefinitionLocalService.getOrAddEmptyKaleoDefinition(
				externalReferenceCode, contextCompany.getCompanyId(),
				contextUser.getUserId());
		}
	}

	private WorkflowDefinitionLink _toWorkflowDefinitionLink(
			com.liferay.portal.kernel.model.WorkflowDefinitionLink
				serviceBuilderWorkflowDefinitionLink)
		throws PortalException {

		Group group;

		if (serviceBuilderWorkflowDefinitionLink.getGroupId() != 0) {
			group = _groupService.getGroup(
				serviceBuilderWorkflowDefinitionLink.getGroupId());
		}
		else {
			group = null;
		}

		return new WorkflowDefinitionLink() {
			{
				setClassName(
					serviceBuilderWorkflowDefinitionLink::getClassName);
				setExternalReferenceCode(
					serviceBuilderWorkflowDefinitionLink::
						getExternalReferenceCode);
				setGroupExternalReferenceCode(
					() -> (group != null) ? group.getExternalReferenceCode() :
						StringPool.BLANK);
				setGroupId(
					() -> (group != null) ? group.getGroupId() :
						serviceBuilderWorkflowDefinitionLink.getGroupId());
				setId(
					serviceBuilderWorkflowDefinitionLink::
						getWorkflowDefinitionLinkId);
				setWorkflowDefinitionExternalReferenceCode(
					() -> _fetchWorkflowDefinitionExternalReferenceCode(
						serviceBuilderWorkflowDefinitionLink));
				setWorkflowDefinitionName(
					serviceBuilderWorkflowDefinitionLink::
						getWorkflowDefinitionName);
				setWorkflowDefinitionVersion(
					serviceBuilderWorkflowDefinitionLink::
						getWorkflowDefinitionVersion);
			}
		};
	}

	@Reference
	private GroupService _groupService;

	@Reference
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Reference
	private WorkflowDefinitionLinkService _workflowDefinitionLinkService;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}