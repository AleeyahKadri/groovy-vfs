// ============================================================================
// (C) Copyright Schalk W. Cronje 2012 - 2014
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
// ============================================================================

group = "org.ysb33r.groovy"
setProperty("archivesBaseName", "groovy-vfs")

apply(plugin = "nebula.provided-base")

extra["moduleName"] = "groovy-vfs"
extra["bintrayDescription"] = "A DSL for Groovy that wraps Apache VFSL"
extra["bintrayTags"] = listOf("groovy", "vfs", "dsl")

val groovyVer: String by extra

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to "Groovy VFS",
            "Implementation-Version" to project.version
        )
    }
}

dependencies {
    "provided"(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVer)
    "testCompile"(group = "com.jcraft", name = "jsch", version = "0.1.48")
    
    /*
    Commons Collections Version 3.1     LRU Cache (optional)
    Apache Jackrabbit Version 1.5.2 or later     WebDAV
    jCIFS Version 0.8.3 or later.     CIFS (VFS sandbox)
    javamail Version 1.4
    */
    "testCompile"(group = "junit", name = "junit", version = "4.+")
    // "testCompile"("io.ratpack:ratpack-groovy:0.9.0-SNAPSHOT")
}

tasks.named<Test>("test") {
    systemProperty("TESTFSREADROOT", "${projectDir}")
    systemProperty("ratpack.port", "9999")
}

apply(from = "../gradle/publish.gradle.kts")

val websitePublishFolder: File by extra

tasks.register<Copy>("installDocs") {
    group = "documentation"
    description = "Copy groovydocs to an install directory"
    dependsOn("groovydoc")

    from(tasks.named("groovydoc"))
    into("${websitePublishFolder}/${project.version}/api/${project.name}")
}
