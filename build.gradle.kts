plugins {
    base
    java
    `maven-publish`
}

group = property("maven_group").toString()
version = property("lib_version").toString()

base {
    archivesName.set(rootProject.property("archives_name").toString())
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
