plugins {
    id("java")
}

group = "net.jirmjahu"
version = "1.0.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.papermc)

    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}