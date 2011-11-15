package net.sf.briar.db;

import java.io.File;
import java.sql.Connection;

import net.sf.briar.api.crypto.Password;
import net.sf.briar.api.db.DatabaseComponent;
import net.sf.briar.api.db.DatabaseDirectory;
import net.sf.briar.api.db.DatabaseMaxSize;
import net.sf.briar.api.db.DatabasePassword;
import net.sf.briar.api.protocol.GroupFactory;
import net.sf.briar.api.transport.ConnectionContextFactory;
import net.sf.briar.api.transport.ConnectionWindowFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class DatabaseModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DatabaseCleaner.class).to(DatabaseCleanerImpl.class);
	}

	@Provides
	Database<Connection> getDatabase(@DatabaseDirectory File dir,
			@DatabasePassword Password password, @DatabaseMaxSize long maxSize,
			ConnectionContextFactory connectionContextFactory,
			ConnectionWindowFactory connectionWindowFactory,
			GroupFactory groupFactory) {
		return new H2Database(dir, password, maxSize, connectionContextFactory,
				connectionWindowFactory, groupFactory);
	}

	@Provides @Singleton
	DatabaseComponent getDatabaseComponent(Database<Connection> db,
			DatabaseCleaner cleaner) {
		return new DatabaseComponentImpl<Connection>(db, cleaner);
	}
}
