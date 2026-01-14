import org.apache.tools.ant.filters.ReplaceTokens

group = "org.ysb33r.groovy"
setProperty("archivesBaseName", "groovy-vfs-smb-provider")

extra["moduleName"] = "groovy-vfs-smb-provider"
extra["bintrayDescription"] = "An SMB provider for Groovy VFS"
extra["bintrayTags"] = listOf("groovy", "vfs", "smb", "cifs")
extra["bintrayLicense"] = "LGPL-2.1"

val groovyVer: String by extra
val vfsVersion: String by extra

dependencies {
    "compile"(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVer)
    "compile"("org.apache.commons:commons-vfs2:$vfsVersion")
    "compile"("jcifs:jcifs:1.3.17")

    // "testCompile"(project(":dsl"))
    "testCompile"(project(":groovy-vfs"))
    // jlan project is disabled, commenting out this dependency
    // "testCompile"(fileTree(mapOf("dir" to "${project(":jlan").buildDir}/libs", "include" to "*.jar")))
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to "Groovy VFS SMB Provider",
            "Implementation-Version" to project.version
        )
    }
}

tasks.named<Test>("test") {
    systemProperty("JLANCONFIG", File(buildDir, "tmp/jlanserver.xml").absolutePath)
    systemProperty("SMBPORT", "1139")
    systemProperty("TESTFSWRITEROOT", File(projectDir, "build/tmp").absolutePath)
    systemProperty("ROOT", projectDir.absolutePath)
    
    doFirst {
        copy {
            from(File(projectDir, "src/test/resources")) {
                include("jlanserver.xml")
                filter(ReplaceTokens::class, "tokens" to mapOf("PROJECTDIR" to projectDir.absolutePath))
            }
            into(File(buildDir, "tmp"))
        }
    }

    // If operating system is Windows add jni path - apparently this is a known issue with JLAN under Windows
    if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
        systemProperty("java.library.path", "${System.getProperty("java.library.path")};${File(project(":jlan").buildDir, "alfresco/jni").absolutePath}")
    }
}

tasks.named("compileTestGroovy") {
    // jlan project is disabled
    // dependsOn(":jlan:jar")
}

configure<nl.javadude.gradle.plugins.license.LicenseExtension> {
    header = rootProject.file("config/lgpl2-header")
}

apply(from = "../gradle/publish.gradle.kts")
