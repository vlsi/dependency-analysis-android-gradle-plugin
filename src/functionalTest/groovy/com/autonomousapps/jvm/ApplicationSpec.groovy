package com.autonomousapps.jvm

import com.autonomousapps.jvm.projects.ApplicationProject
import com.autonomousapps.kit.Plugin
import com.autonomousapps.kit.SourceType
import org.gradle.util.GradleVersion
import spock.lang.IgnoreIf
import spock.lang.Unroll

import static com.autonomousapps.utils.Runner.build
import static com.google.common.truth.Truth.assertThat

final class ApplicationSpec extends AbstractJvmSpec {

  @Unroll
  def "can analyze java-jvm application projects (#gradleVersion)"() {
    given:
    def project = new ApplicationProject()
    gradleProject = project.gradleProject

    when:
    build(gradleVersion, gradleProject.rootDir, ':buildHealth')

    then:
    assertThat(actualAdvice()).containsExactlyElementsIn(project.expectedAdvice)

    where:
    gradleVersion << gradleVersions().tap {
      it.removeAll {
        // This test is on a project that uses the `compile` configuration, removed in Gradle 7
        it.baseVersion >= GradleVersion.version('7.0').baseVersion
      }
    }
  }

  @Unroll
  def "can analyze kotlin-jvm application projects when kotlin-jvm is applied first(#gradleVersion)"() {
    given:
    def plugins = [Plugin.kotlinPluginNoVersion, Plugin.applicationPlugin]
    def project = new ApplicationProject(plugins, SourceType.KOTLIN)
    gradleProject = project.gradleProject

    when:
    build(gradleVersion, gradleProject.rootDir, ':buildHealth')

    then:
    assertThat(actualAdvice()).containsExactlyElementsIn(project.expectedAdvice)

    where:
    gradleVersion << gradleVersions()
  }

  @Unroll
  def "can analyze kotlin-jvm application projects when application is applied first(#gradleVersion)"() {
    given:
    def plugins = [Plugin.applicationPlugin, Plugin.kotlinPluginNoVersion]
    def project = new ApplicationProject(plugins, SourceType.KOTLIN)
    gradleProject = project.gradleProject

    when:
    build(gradleVersion, gradleProject.rootDir, ':buildHealth')

    then:
    assertThat(actualAdvice()).containsExactlyElementsIn(project.expectedAdvice)

    where:
    gradleVersion << gradleVersions()
  }
}
