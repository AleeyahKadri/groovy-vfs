// ============================================================================
// (C) Copyright Schalk W. Cronje 2014
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
//
// ============================================================================

group = "org.ysb33r.groovy"
base.archivesName.set("groovy-vfs-cloud-core")
// version = "0.1" + if (versionModifier.isNotEmpty()) "-$versionModifier" else ""

extra["moduleName"] = "groovy-vfs-cloud-core"
extra["bintrayDescription"] = "Library to support cloud providers for Groovy VFS"
extra["bintrayTags"] = listOf("groovy", "vfs", "cloud")
extra["jCloudsVersion"] = "1.7.2"

repositories {
    jcenter()
}

val groovyVer: String by extra
val vfsVersion: String by extra
val jCloudsVersion: String by extra

tasks.named<Jar>("manifest") {
    manifest {
        attributes(
            "Implementation-Title" to "Groovy VFS Cloud Provider Core",
            "Implementation-Version" to project.version
        )
    }
}

dependencies {
    "compile"(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVer)
    "compile"("org.apache.commons:commons-vfs2:$vfsVersion")
    "compile"("org.slf4j:slf4j-api:1.7.+")
    "compile"("org.apache.jclouds:jclouds-all:$jCloudsVersion")
    "compile"("org.apache.jclouds.driver:jclouds-jsch:$jCloudsVersion")
    // "compile"("org.apache.jclouds.driver:jclouds-slf4j:1.7.1")

    // Required for S3
    "testCompile"("org.apache.jclouds.provider:aws-s3:\${jCloudsVersion}")
}

tasks.named<Test>("test") {
    systemProperty("TESTFSREADROOT", File(projectDir, "src/test/resources/test-files").absolutePath)
    systemProperty("TESTFSWRITEROOT", File(buildDir, "tmp").absolutePath)
    systemProperty("S3ID", project.findProperty("AWSAccessKeyId") ?: "FOO")
    systemProperty("S3KEY", project.findProperty("AWSSecretKey") ?: "BAR")
    systemProperty("S3BUCKET", "groovy-vfs-test-bucket")

    // if (gradle.startParameter.isOffline) {
        systemProperty("TEST.OFFLINE", "1")
    // }
}

apply(from = "../gradle/publish.gradle.kts")
