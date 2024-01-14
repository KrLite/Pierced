plugins {
    base
    java
    `maven-publish`
}

group = property("maven_group").toString()
version = property("lib_version").toString()

base {
    archivesName.set(rootProject.property("archives_base_name").toString())
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.moandjiezana.toml:toml4j:${rootProject.property("toml4j_version")}")
}

sourceSets {
    main {
        java {
            exclude("example/**")
        }
    }
}

java {
    withSourcesJar()
    setSourceCompatibility(JavaVersion.VERSION_1_8)
    setTargetCompatibility(JavaVersion.VERSION_1_8)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
