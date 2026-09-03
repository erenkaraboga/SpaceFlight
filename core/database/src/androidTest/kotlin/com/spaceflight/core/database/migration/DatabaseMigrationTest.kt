package com.spaceflight.core.database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.spaceflight.core.database.SpaceflightDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Runs the two real schema transitions this app has shipped against the schema JSON snapshots
 * Room already exports to `core/database/schemas/` (KSP's `room.schemaLocation` arg in this
 * module's build.gradle.kts). Each test writes a row under the *old* schema, runs the
 * [AutoMigration] declared on [SpaceflightDatabase], and checks the row survived -- the exact
 * scenario `fallbackToDestructiveMigration` used to fail silently (see DatabaseModule).
 *
 * Instrumented (androidTest), not a JVM unit test: MigrationTestHelper needs a real
 * SupportSQLiteDatabase and Instrumentation, so this requires a device or emulator and is not
 * part of the `testDebugUnitTest` step the CI workflow currently runs.
 */
@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SpaceflightDatabase::class.java,
    )

    @Test
    fun migrate1To2_dropsUpdatedAt_articleSurvives() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                """
                INSERT INTO articles
                    (id, title, summary, imageUrl, newsSite, url, authors, publishedAt,
                     updatedAt, isFeatured, launchCount, eventCount)
                VALUES
                    (1, 'Starship static fire', 'A summary', 'https://img', 'NASA',
                     'https://example.com/1', 'Ada Lovelace', 0, 0, 0, 0, 0)
                """.trimIndent(),
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(TEST_DB, 2, true)

        migrated.query("SELECT title FROM articles WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("Starship static fire", cursor.getString(0))
        }
    }

    @Test
    fun migrate2To3_dropsLastRefreshedAt_remoteKeySurvives() {
        helper.createDatabase(TEST_DB, 2).apply {
            execSQL(
                "INSERT INTO remote_keys (id, nextOffset, endReached, lastRefreshedAt) " +
                    "VALUES (1, 20, 0, 0)",
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(TEST_DB, 3, true)

        migrated.query("SELECT nextOffset FROM remote_keys WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(20, cursor.getInt(0))
        }
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}
