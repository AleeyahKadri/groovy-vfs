import org.gradle.internal.os.OperatingSystem

buildscript {
    dependencies {
        classpath("org.ysb33r.gradle:vfs-gradle-plugin:%%VERSION%%")
        classpath(fileTree(mapOf("dir" to "../../repo", "include" to "*.jar")))
    }
}

apply(plugin = "org.ysb33r.vfs")

extra["gvfsGithubRoot"] = "https://github.com/ysb33r/groovy-vfs/archive"

val gvfsGithubRoot: String by extra

tasks.register("copyCustom") {
    doLast {
        mkdir("${buildDir}/${name}")
        ant.withGroovyBuilder {
            "vfs" {
                "cp"(
                    "${gvfsGithubRoot}/development.zip",
                    file("${buildDir}/${name}"),
                    "overwrite" to true
                )
            }
        }
    }
}

tasks.register<org.ysb33r.gradle.vfs.tasks.VfsCopy>("copyTask") {
    from("${gvfsGithubRoot}/master.zip")
    into(file("${buildDir}/${name}"))
}

tasks.register("runGradleTest") {
    dependsOn("copyCustom", "copyTask")
}
