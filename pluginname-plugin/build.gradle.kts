plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven {
        setUrl("http://pack.minevn.net/repo/")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    // spigot
    // TODO: Chọn phiên spigot bản phù hợp (nên dùng paper)
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")

    // libs
    compileOnly("net.minevn:minevnlib-plugin:1.0.6") // TODO: Chọn phiên minevnlib bản phù hợp

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

    val jarName = "PluginName" // TODO: Thay đổi tên plugin

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
