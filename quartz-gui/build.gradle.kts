plugins {
    id("java")
    id("fabric-loom").version("1.0-SNAPSHOT")
    id("maven-publish")
}

group = "io.github.prismwork.prismconfig"
version = "${rootProject.version}+mc${property("minecraft_version")}"

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com/releases/") {
        name = "TerraformersMC"
    }
    maven("https://maven.gegy.dev/") {
        name = "Gegy Maven"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    modImplementation("com.terraformersmc:modmenu:${property("modmenu_version")}")
    modImplementation("dev.lambdaurora:spruceui:${property("spruceui_version")}")?.let { include(it) }

    implementation(rootProject)?.let { include(it) }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from(rootProject.file("LICENSE").absolutePath)
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
            }
        }

        repositories {
            mavenLocal()
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
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
