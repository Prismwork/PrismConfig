import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "io.github.prismwork"
version = "0.1.0"

val include: Configuration by configurations.creating

fun relocatePackage(name: String) : String {
    return "$group.prismconfig.libs.$name"
}

tasks.named<ShadowJar>("shadowJar") {
    from("LICENSE")
    configurations = listOf(include)
    relocate("blue.endless.jankson", relocatePackage("jankson"))
    relocate("com.google.gson", relocatePackage("gson"))
    relocate("org.jetbrains.annotations", relocatePackage("jb.annotations"))
    relocate("org.intellij.lang.annotations", relocatePackage("ij.annotations"))
}

repositories {
    mavenCentral()
    maven {
        name = "QuiltMC Release"
        url = uri("https://maven.quiltmc.org/repository/release/")
    }
    maven {
        name = "QuiltMC Snapshot"
        url = uri("https://maven.quiltmc.org/repository/snapshot/")
    }
}

dependencies {
    /* Utilities */
    implementation("org.jetbrains:annotations:23.1.0")
    include("org.jetbrains:annotations:23.1.0")

    /* Serialization */
    implementation("com.google.code.gson:gson:2.10")
    include("com.google.code.gson:gson:2.10")
    implementation("blue.endless:jankson:1.2.1")
    include("blue.endless:jankson:1.2.1")
    // implementation("org.quiltmc:quilt-json5:1.0.2")
}

tasks.jar {
    from("LICENSE")
    finalizedBy(tasks.shadowJar)
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "$group"
            artifactId = name
            version = version

            from(components["java"])
        }
    }

    repositories {
        if (System.getenv("MAVEN_USERNAME") != null && System.getenv("MAVEN_PASSWORD") != null) {
            maven {
                name = "release"
                url = uri("https://maven.nova-committee.cn/releases")

                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
            maven {
                name = "snapshot"
                url = uri("https://maven.nova-committee.cn/snapshots")

                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
}
