plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.onarandombox.com/content/groups/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        setUrl("http://pack.minevn.net/repo/")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    // paper
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")

    // libs
    compileOnly("net.minevn:minevnlib-plugin:1.0.6")
    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:4.3.2-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")

    // JUnit
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")
    testImplementation("io.mockk:mockk:1.13.7")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get())
}

tasks {
    test {
        useJUnitPlatform()
    }

    val jarName = "YukiResetter"

    register("customCopy") {
        dependsOn(shadowJar)

        val path = project.properties["shadowPath"]
        if (path != null) {
            doLast {
                println(path)
                copy {
                    from("build/libs/$jarName.jar")
                    into(path)
                }
                println("Copied")
            }
        }
    }

    processResources {
        outputs.upToDateWhen { false }
        filesMatching(listOf("**/plugin.yml")) {
            expand(mapOf("version" to project.version.toString()))
            println("$name: set version to ${project.version}")
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        archiveFileName.set("$jarName.jar")
    }

    assemble {
        dependsOn(shadowJar, get("customCopy"))
    }
}
