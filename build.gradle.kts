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

sourceSets {
    main {
        java {
            exclude("example/**")
        }
    }
}

java {
    withSourcesJar()
    setSourceCompatibility(JavaVersion.VERSION_20)
    setTargetCompatibility(JavaVersion.VERSION_20)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}