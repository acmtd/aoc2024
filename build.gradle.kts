plugins {
    kotlin("jvm") version "2.1.0"
}

sourceSets {
    main {
        kotlin.srcDir("src/main/kotlin")
    }
}

dependencies {
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.0")
    implementation("guru.nidi:graphviz-kotlin:0.18.1")
}

tasks {
    wrapper {
        gradleVersion = "8.11.1"
    }
}
