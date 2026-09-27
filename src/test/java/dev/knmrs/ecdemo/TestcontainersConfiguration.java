package dev.knmrs.ecdemo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
// Spring Boot 3.5 系（Testcontainers 1.x）のパッケージ。
// Spring Boot 4 の Initializr が生成する org.testcontainers.postgresql.* とは別物。
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * テスト実行のたびに PostgreSQL のコンテナを立てて壊す。
 *
 * <p>{@code @ServiceConnection} は Spring Boot 3.1 で入った。
 * これが無かった時代は {@code @DynamicPropertySource} で
 * url / username / password を手で Environment に流し込む必要があった。
 * いまは「コンテナの Bean にこのアノテーションを付けるだけ」で
 * Spring Boot が接続情報を検出して DataSource を組み立てる。
 *
 * <p>イメージは latest ではなくタグを固定する。latest だと
 * ある日 PostgreSQL のメジャーバージョンが上がってロックの挙動が変わりうる。
 * Step 2 で排他制御を実測するので、そこがぶれると困る。
 */
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"));
	}

}
