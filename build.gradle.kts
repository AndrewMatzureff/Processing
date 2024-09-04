import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
    application
}

group = "com.matzua"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Add Dagger dependencies
    implementation("com.google.dagger:dagger:2.52")
    annotationProcessor("com.google.dagger:dagger-compiler:2.52")

    implementation("org.projectlombok:lombok:1.18.26")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // required if you want to use Mockito for unit tests
    testImplementation("org.mockito:mockito-core:2.24.5")

    // lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    compileOnly("org.slf4j:slf4j-api:1.7.+")
    implementation("org.slf4j:slf4j-log4j12:1.7.29")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

}

//for including in the copy task
val natives = copySpec {
    from("natives")
}

tasks {
    register("copyNatives", Copy::class) {

        into("build/libs/natives")
        with(natives)
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.assemble {
    dependsOn(tasks.named("copyNatives").get())
}

application {
    mainClass.set("com.matzua.Main")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.matzua.Main"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies otherwise a "NoClassDefFoundError" error
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}