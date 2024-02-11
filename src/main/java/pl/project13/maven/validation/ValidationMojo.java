/*
 * This file is part of git-commit-id-maven-plugin
 * Originally invented by Konrad 'ktoso' Malawski <konrad.malawski@java.pl>
 *
 * git-commit-id-maven-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * git-commit-id-maven-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with git-commit-id-maven-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.project13.maven.validation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * The git-commit-id-maven-plugin comes equipped with an additional
 * validation utility which can be used to verify if your project properties are set as you
 * would like to have them set.
 * This feature ships with an additional mojo execution and for instance allows to check if
 * the version is not a snapshot build. If you are interested in the config checkout the
 * <a href="https://github.com/git-commit-id/git-commit-id-maven-plugin#validate-if-properties-are-set-as-expected">
 *   validation utility documentation</a>.
 * <p>
 * Each validation you may want to perform can be configured using a {@link ValidationProperty}.
 *
 * <b>Note</b>: This configuration will only be taken into account when the additional goal
 * `validateRevision` is configured inside an execution like so
 * <pre>{@code
 * <plugin>
 *   <groupId>io.github.git-commit-id</groupId>
 *   <artifactId>git-commit-id-maven-plugin</artifactId>
 *   <executions>
 *      <execution>
 *        <id>validate-the-git-infos</id>
 *        <goals><goal>validateRevision</goal></goals>
 *        <phase>package</phase>
 *      </execution>
 *    </executions>
 * }</pre>
 *
 * @since 2.2.2
 */
@Mojo(name = "validateRevision", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class ValidationMojo extends AbstractMojo {
  @Parameter(defaultValue = "true")
  private boolean validationShouldFailIfNoMatch;

  @Parameter private List<ValidationProperty> validationProperties;

  @Override
  public void execute() throws MojoExecutionException {
    if (validationProperties != null && validationShouldFailIfNoMatch) {
      for (ValidationProperty validationProperty : validationProperties) {
        String name = validationProperty.getName();
        String value = validationProperty.getValue();
        String shouldMatchTo = validationProperty.getShouldMatchTo();
        if ((value != null) && (shouldMatchTo != null)) {
          validateIfValueAndShouldMatchToMatches(name, value, shouldMatchTo);
        } else {
          printLogMessageWhenValueOrShouldMatchToIsEmpty(name, value, shouldMatchTo);
        }
      }
    }
  }

  private void validateIfValueAndShouldMatchToMatches(
      String name, String value, String shouldMatchTo) throws MojoExecutionException {
    Pattern pattern = Pattern.compile(shouldMatchTo);
    Matcher matcher = pattern.matcher(value);
    if (!matcher.find()) {
      String commonLogMessage = "Expected '" + value + "' to match with '" + shouldMatchTo + "'!";
      if (name != null) {
        throw new MojoExecutionException("Validation '" + name + "' failed! " + commonLogMessage);
      } else {
        throw new MojoExecutionException(
            "Validation of an unidentified validation (please set the name property-tag to be able"
                + " to identify the validation) failed! "
                + commonLogMessage);
      }
    }
  }

  private void printLogMessageWhenValueOrShouldMatchToIsEmpty(
      String name, String value, String shouldMatchTo) {
    String commonLogMessage =
        "since one of the values was null! (value = '"
            + value
            + "'; shouldMatchTo = '"
            + shouldMatchTo
            + "').";
    if (name != null) {
      getLog().warn("Skipping validation '" + name + "' " + commonLogMessage);
    } else {
      getLog()
          .warn(
              "Skipping an unidentified validation (please set the name property-tag to be able to"
                  + " identify the validation) "
                  + commonLogMessage);
    }
  }

  public void setValidationShouldFailIfNoMatch(boolean validationShouldFailIfNoMatch) {
    this.validationShouldFailIfNoMatch = validationShouldFailIfNoMatch;
  }

  public void setValidationProperties(List<ValidationProperty> validationProperties) {
    this.validationProperties = validationProperties;
  }
}
