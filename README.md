# ec-demo

EC サイトのバックエンドを題材に、**壊してから直す**やり方で Java / Spring Boot の実務論点を学ぶための練習用プロジェクト。

商品・在庫・カート・注文・決済という、実際の改修案件と対応する形で1本のアプリを段階的に育てていく。
サンプルを並べるのではなく、**正しく動いているときは見えない挙動**（排他制御・冪等性・認可）を、意図的に壊して観測してから直す。

## 進め方

各 Step は Issue → ブランチ → PR の単位で進める。PR には「何を壊して、何を観測して、どう直したか」を書く。

| | やること |
|---|---|
| 1 | 実装して動かす。**テスト結果・SQLログ・HTTPレスポンスを見る** |
| 2 | **先に壊す。** 失敗するテストを書いて、なぜ失敗するかを実物で確認する |
| 3 | 直す |
| 4 | 手を動かす課題を残す |

## Step

| Step | 題材 | 状態 |
|---|---|---|
| 0 | 土台（jakarta の対比・Flyway・Testcontainers・ログ設定） | ✅ |
| 1 | ドメイン（record / sealed + switch / BigDecimal / Bean Validation） | |
| 2 | トランザクション境界 → 在庫引当の排他制御 → N+1 | |
| 3 | 冪等性（二重注文の防止） | |
| 4 | Spring Security 6 | |
| 5 | 外部連携（決済代行のモック・Webhook） | |
| 6 | テストの型（テストスライスの使い分け） | |
| 7 | レガシー改修（特性化テスト・影響範囲） | |
| 8 | CI（GitHub Actions） | |

## 技術構成

| | |
|---|---|
| Java | 21（Temurin）。`build.gradle` の toolchain で宣言 |
| Spring Boot | **3.5.16**（3.5 系の最終 OSS リリース） |
| DB | PostgreSQL 17。**Testcontainers** がテストのたびにコンテナを立てて壊す |
| マイグレーション | Flyway |
| ビルド | Gradle（Wrapper 同梱。Gradle 8.14.3） |

### なぜ Spring Boot 4 系ではないのか

start.spring.io は 2026-09 時点で 3 系を選択肢に出さない（4.0.8 / 4.1.1 のみ）が、あえて 3.5.16 に固定している。

1. 改修案件が 4 系である可能性は低く、日本語の情報量も 3 系が圧倒的
2. **4 系は Spring Security 7 を載せる。** Step 4 で学ぶのは Security 6 なのでずれる

## 動かし方

Docker が動いていることが前提（Testcontainers が PostgreSQL を起動する）。

```bash
./gradlew test
```

PostgreSQL のローカルインストールは不要。

## ログで見えるようにしているもの

`application.properties` で有効にしてある。Step 2 以降で必要になる。

| 目的 | 設定 |
|---|---|
| 発行された SQL とバインド値 | `org.hibernate.SQL=DEBUG` / `org.hibernate.orm.jdbc.bind=TRACE` |
| トランザクション境界 | `JpaTransactionManager=DEBUG` / `TransactionInterceptor=TRACE` |
| HikariCP のプール状態 | `HikariPool=DEBUG` |

`maximum-pool-size=10` / `connection-timeout=3000` は **Step 2 で枯渇させるために意図的に小さくしている**。本番の設定値ではない。
