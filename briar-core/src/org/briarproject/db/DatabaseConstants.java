package org.briarproject.db;

import org.briarproject.api.settings.Settings;

interface DatabaseConstants {

	/**
	 * The maximum number of offered messages from each contact that will be
	 * stored. If offers arrive more quickly than requests can be sent and this
	 * limit is reached, additional offers will not be stored.
	 */
	int MAX_OFFERED_MESSAGES = 1000;

	/**
	 * The namespace of the {@link Settings Settings}
	 * where the database schema version is stored.
	 */
	String DB_SETTINGS_NAMESPACE = "db";

	/**
	 * The {@link Settings Settings} key under which the
	 * database schema version is stored.
	 */
	String SCHEMA_VERSION_KEY = "schemaVersion";

	/**
	 * The {@link Settings Settings} key under which the
	 * minimum supported database schema version is stored.
	 */
	String MIN_SCHEMA_VERSION_KEY = "minSchemaVersion";

	/**
	 * The namespace of the {@link Settings Settings}
	 * where the unique device ID is stored.
	 */
	String DEVICE_SETTINGS_NAMESPACE = "device";

	/**
	 * The {@link Settings Settings} key under which the
	 * unique device ID is stored.
	 */
	String DEVICE_ID_KEY = "deviceId";
}
