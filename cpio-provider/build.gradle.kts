group = "org.ysb33r.groovy"
setProperty("archivesBaseName", "groovy-vfs-cpio-provider")

extra["moduleName"] = "groovy-vfs-cpio-provider"
extra["bintrayDescription"] = "A CPIO provider for Groovy VFS"
extra["bintrayTags"] = listOf("groovy", "vfs", "cpio", "archivers")

val groovyVer: String by extra
val vfsVersion: String by extra

dependencies {
    "compile"(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVer)
    "compile"("org.apache.commons:commons-vfs2:$vfsVersion")

    "testCompile"(project(":dsl"))
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to "Groovy VFS CPIO Provider",
            "Implementation-Version" to project.version
        )
    }
}

tasks.named<Test>("test") {
    systemProperty("TESTFSREADROOT", "${buildDir}/resources/test/test-archives")
    systemProperty("TESTFSWRITEROOT", "${buildDir}/tmp")
}
