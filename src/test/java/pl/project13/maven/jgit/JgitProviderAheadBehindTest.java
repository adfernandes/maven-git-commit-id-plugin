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

package pl.project13.maven.jgit;

import java.nio.file.Paths;
import pl.project13.core.JGitProvider;
import pl.project13.maven.git.AheadBehindTest;

/**
 * Testcases to verify that the {@link JGitProvider} works properly.
 */
public class JgitProviderAheadBehindTest extends AheadBehindTest<JGitProvider> {

  @Override
  public void extraSetup() {
    gitProvider.setRepository(localRepositoryGit.getRepository());
  }

  @Override
  protected JGitProvider gitProvider() {
    return JGitProvider.on(
        Paths.get(localRepository.getRoot().getAbsolutePath(), ".git").toFile(), null);
  }
}
