/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.quota.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author Feliphe Marinho
 */
public class QuotaUtil {

	public static void checkUsage(long companyId, long tokensCount, long userId)
		throws PortalException {

		ObjectEntry objectEntry = _fetchQuotaObjectEntry(companyId, userId);

		if (objectEntry == null) {
			return;
		}

		try (Closeable closeable = _lock(objectEntry.getObjectEntryId())) {
			objectEntry = ObjectEntryLocalServiceUtil.getObjectEntry(
				objectEntry.getObjectEntryId());

			long usage =
				MapUtil.getLong(objectEntry.getValues(), "usage") + tokensCount;

			if (usage > MapUtil.getLong(objectEntry.getValues(), "limit")) {
				throw new UnsupportedOperationException(
					"You have exceeded your token quota");
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static void refreshUsage(
			long companyId, String externalReferenceCode, long userId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA", companyId);

		if (objectDefinition == null) {
			return;
		}

		ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.fetchObjectEntry(
			externalReferenceCode, 0, objectDefinition.getObjectDefinitionId());

		if (objectEntry == null) {
			return;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		long lrtUsage = MapUtil.getLong(values, "lrtUsage");
		long usage = MapUtil.getLong(values, "usage");

		if ((lrtUsage == 0) && (usage == 0)) {
			return;
		}

		Date currentDate = new Date();
		Date lastRefreshDate = (Date)values.get("lastRefreshDate");

		if ((lastRefreshDate != null) &&
			Objects.equals(
				YearMonth.from(_toYearMonth(currentDate)),
				YearMonth.from(_toYearMonth(lastRefreshDate)))) {

			return;
		}

		try (Closeable closeable = _lock(objectEntry.getObjectEntryId())) {
			objectEntry = ObjectEntryLocalServiceUtil.getObjectEntry(
				objectEntry.getObjectEntryId());

			ObjectEntryLocalServiceUtil.partialUpdateObjectEntry(
				userId, objectEntry.getObjectEntryId(), 0,
				HashMapBuilder.<String, Serializable>put(
					"lastRefreshDate", currentDate.toString()
				).put(
					"lrtUsage", 0L
				).put(
					"usage", 0L
				).build(),
				_getServiceContext(companyId, userId));

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				objectEntry, "REFRESH", null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"accountEntryId",
				MapUtil.getLong(
					values, "r_accountToAIHubQuotas_accountEntryId"));

			AuditRouterUtil.route(auditMessage);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static void updateUsage(
			long companyId, long milliLRTCount, long tokensCount, long userId)
		throws PortalException {

		ObjectEntry objectEntry = _fetchQuotaObjectEntry(companyId, userId);

		if (objectEntry == null) {
			return;
		}

		try (Closeable closeable = _lock(objectEntry.getObjectEntryId())) {
			objectEntry = ObjectEntryLocalServiceUtil.getObjectEntry(
				objectEntry.getObjectEntryId());

			ObjectEntryLocalServiceUtil.partialUpdateObjectEntry(
				userId, objectEntry.getObjectEntryId(), 0,
				HashMapBuilder.<String, Serializable>put(
					"lrtUsage",
					MapUtil.getLong(objectEntry.getValues(), "lrtUsage") +
						milliLRTCount
				).put(
					"usage",
					MapUtil.getLong(objectEntry.getValues(), "usage") +
						tokensCount
				).build(),
				_getServiceContext(companyId, userId));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static ObjectEntry _fetchQuotaObjectEntry(
			long companyId, long userId)
		throws PortalException {

		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			userId);

		if (accountEntry == null) {
			return null;
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA", companyId);

		if (objectDefinition == null) {
			return null;
		}

		User user = UserLocalServiceUtil.getUser(userId);

		String externalReferenceCode =
			"quota-" + accountEntry.getAccountEntryId();

		if (user.isServiceAccountUser()) {
			externalReferenceCode =
				"guest-quota-" + accountEntry.getAccountEntryId();
		}

		return ObjectEntryLocalServiceUtil.fetchObjectEntry(
			externalReferenceCode, 0, objectDefinition.getObjectDefinitionId());
	}

	private static ServiceContext _getServiceContext(
		long companyId, long userId) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return serviceContext;
	}

	private static Closeable _lock(long objectEntryId) throws PortalException {
		String updatedOwner = PortalUUIDUtil.generate();

		long deadline = System.currentTimeMillis() + (10 * Time.SECOND);

		while (true) {
			Lock lock = LockManagerUtil.lock(
				QuotaUtil.class.getName(), String.valueOf(objectEntryId), null,
				updatedOwner);

			if (Objects.equals(lock.getOwner(), updatedOwner)) {
				break;
			}

			if (System.currentTimeMillis() >= deadline) {
				throw new PortalException(new TimeoutException());
			}

			try {
				Thread.sleep(50);
			}
			catch (InterruptedException interruptedException) {
				Thread thread = Thread.currentThread();

				thread.interrupt();

				throw new PortalException(interruptedException);
			}
		}

		return () -> LockManagerUtil.unlock(
			QuotaUtil.class.getName(), String.valueOf(objectEntryId),
			updatedOwner);
	}

	private static YearMonth _toYearMonth(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());

		return YearMonth.from(instant.atZone(ZoneId.systemDefault()));
	}

}