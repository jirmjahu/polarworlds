plugins {
    id("java")
}

group = "net.jirmjahu"
version = "1.0.6"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.papermc)

    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}