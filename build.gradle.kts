plugins {
    kotlin("jvm") version "1.9.21"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

dependencies {
    implementation("org.jgrapht:jgrapht-core:1.5.2")
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
