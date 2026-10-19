/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.entry.preview;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryPreviewVersionProviderUtil {

	public static ObjectEntryVersion fetchPreviewObjectEntryVersion(
		ObjectEntry objectEntry) {

		for (ObjectEntryPreviewVersionProvider
				objectEntryPreviewVersionProvider : _serviceTrackerList) {

			ObjectEntryVersion objectEntryVersion =
				objectEntryPreviewVersionProvider.
					fetchPreviewObjectEntryVersion(objectEntry);

			if (objectEntryVersion != null) {
				return objectEntryVersion;
			}
		}

		return null;
	}

	private static final ServiceTrackerList<ObjectEntryPreviewVersionProvider>
		_serviceTrackerList;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryPreviewVersionProviderUtil.class);

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundle.getBundleContext(), ObjectEntryPreviewVersionProvider.class);
	}

}