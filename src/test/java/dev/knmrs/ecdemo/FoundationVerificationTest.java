package dev.knmrs.ecdemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step 0 の到達確認。
 *
 * <p>確かめるのは3つだけ。
 * <ol>
 *   <li>Testcontainers が本物の PostgreSQL を立てたか（H2 ではないこと）</li>
 *   <li>Flyway が V1 を適用したか</li>
 *   <li>マイグレーションで作ったテーブルが実在するか</li>
 * </ol>
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FoundationVerificationTest {

	@Autowired
	JdbcTemplate jdbc;

	@Test
	@DisplayName("H2ではなく本物のPostgreSQLに繋がっている")
	void 本物のPostgreSQLに繋がっている() {
		String version = jdbc.queryForObject("SELECT version()", String.class);
		System.out.println("[Step0] DB version = " + version);

		// H2 の互換モードでは version() がそもそも存在しない or 別の文字列を返す。
		// ここが PostgreSQL であることが、Step 2 のロック実測の前提になる。
		assertThat(version).startsWith("PostgreSQL 17");
	}

	@Test
	@DisplayName("Flywayが V1 を適用し、履歴テーブルに記録されている")
	void Flywayが適用済みである() {
		List<String> applied = jdbc.queryForList(
				"SELECT version || ' : ' || description FROM flyway_schema_history ORDER BY installed_rank",
				String.class);
		applied.forEach(row -> System.out.println("[Step0] flyway history = " + row));

		assertThat(applied).isNotEmpty();
		assertThat(applied.getFirst()).startsWith("1 : ");
	}

	@Test
	@DisplayName("マイグレーションで作ったテーブルが実在する")
	void テーブルが存在する() {
		List<String> tables = jdbc.queryForList(
				"SELECT table_name FROM information_schema.tables "
						+ "WHERE table_schema = 'public' ORDER BY table_name",
				String.class);
		System.out.println("[Step0] tables = " + tables);

		assertThat(tables).contains("product", "stock", "flyway_schema_history");
	}
}
