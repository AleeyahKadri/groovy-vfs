// ============================================================================
// (C) Copyright Schalk W. Cronje 2013
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
//
// ============================================================================

/*
// Plugin publishing and gradle test dependencies are not available
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("com.gradle.publish:plugin-publish-plugin:0.9.1")
        classpath("org.ysb33r.gradle:gradletest:0.5.4")
    }
}

apply(plugin = "com.gradle.plugin-publish")
apply(plugin = "org.ysb33r.gradletest")
*/

group = "org.ysb33r.gradle"
setProperty("archivesBaseName", "vfs-gradle-plugin")

extra["gradleID"] = "org.ysb33r.vfs"
extra["moduleName"] = "vfs-gradle-plugin"
extra["bintrayDescription"] = "This is a plugin for Gradle that utilises the Groovy VFS DSL"
extra["bintrayTags"] = listOf("gradle", "groovy", "vfs", "groovy-vfs")
extra["bintrayAttributes"] = mapOf("gradle-plugin" to "${project.group}:${project.property("archivesBaseName")}:org.ysb33r.vfs")

val dslProject = dependencies.project(mapOf("path" to ":groovy-vfs", "configuration" to "default"))
extra["dslProject"] = dslProject

configurations.all {
    exclude(module = "groovy-all")
}

dependencies {
    "compile"(dslProject)
    // "compile"(project(mapOf("path" to ":dsl", "configuration" to "default")))
    "compile"(gradleApi())
    "compile"(localGroovy())

    "compile"("org.slf4j:jcl-over-slf4j:1.7.2")

    "testCompile"(project(":test-servers"))

    // gradleTest configuration doesn't exist since the plugin is commented out
    // "gradleTest"(project(mapOf("path" to ":dsl", "configuration" to "default")))
    // "gradleTest"(dslProject)
    // "gradleTest"("commons-httpclient:commons-httpclient:3.1")
}

tasks.named<Test>("test") {
    systemProperty("TESTFSREADROOT", projectDir)
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to project.extra["moduleName"],
            "Implementation-Version" to project.version
        )
    }
}

apply(from = "../gradle/publish.gradle.kts")

/*
// Plugin publishing configuration commented out due to unavailable dependencies
configure<com.gradle.publish.PluginBundleExtension> {
    description = extra["bintrayDescription"] as String
    website = "http://ysb33r.github.io/groovy-vfs"
    vcsUrl = "https://github.com/ysb33r/groovy-vfs.git"

    tags = extra["bintrayTags"] as List<String>

    plugins {
        create("vfsPlugin") {
            id = extra["gradleID"] as String
            displayName = "Gradle VFS plugin"
        }
    }

    mavenCoordinates {
        groupId = project.group as String
        artifactId = project.property("archivesBaseName") as String
    }
}

tasks.named("publishPlugins") {
    onlyIf { !(version as String).endsWith("SNAPSHOT") }
}

configure<org.ysb33r.gradle.gradletest.GradleTestExtension> {
    versions("2.0", "2.2", "2.3", "2.5", "2.8", "2.10", "2.12", "2.13")

    onlyIf { !gradle.startParameter.isOffline }
}
*/
