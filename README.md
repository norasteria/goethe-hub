# Run
1. Run `Docker Desktop`, then go to project repository and run `docker compose up`. This will start the required subsystems (e.g. DB, redis)
2. Run the application

# Setup

### Installation
1. Install `Docker` & `Docker Compose`. (Alternatively you can just install `Docker Desktop` instead).
2. Install Java 21.

### System Settings
1. Increase Gradle Heap Size (Add `org.gradle.jvmargs=-Xmx8192m` to ` ~/.gradle/gradle.properties`).
2. Increase IntelliJ Memory Setting (Help > Change Memory Settings). Recommended value: 2048 MB.
3. (Optional) Increase Swap size.

# API Doc
Swagger URL: `{baseUrl}/swagger-ui/index.html`
