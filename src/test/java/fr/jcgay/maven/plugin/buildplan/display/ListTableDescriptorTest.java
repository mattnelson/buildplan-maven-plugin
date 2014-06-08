/**
 * Copyright (C) 2012 Jean-Christophe Gay (contact@jeanchristophegay.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.jcgay.maven.plugin.buildplan.display;

import org.apache.maven.plugin.MojoExecution;
import org.junit.Test;

import static fr.jcgay.maven.plugin.buildplan.model.builder.MojoExecutionBuilder.aMojoExecution;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ListTableDescriptorTest {

    @Test
    public void should_add_all_size_and_the_separator_size_to_get_descriptor_width() {

        ListTableDescriptor descriptor = new ListTableDescriptor().setExecutionIdSize(1)
                                                                  .setGoalSize(2)
                                                                  .setPluginSize(3)
                                                                  .setPhaseSize(4);

        assertThat(descriptor.width()).isEqualTo(10 + ListTableDescriptor.SEPARATOR_SIZE);
    }

    @Test
    public void should_calculate_column_size_from_longest_element_in_each_column() {

        MojoExecution executionA = aMojoExecution().withArtifactId("plugin-a")
                                                   .withPhase("short-phase")
                                                   .withExecutionId("short-execution-id")
                                                   .withGoal("short-goal")
                                                   .build();
        MojoExecution executionB = aMojoExecution().withArtifactId("plugin-b-longer-than-a")
                                                   .withPhase("a-very-very-very-long-phase")
                                                   .withExecutionId("a-very-very-very-long-execution-id")
                                                   .withGoal("a-very-very-very-long-goal")
                                                   .build();

        ListTableDescriptor descriptor = ListTableDescriptor.of(asList(executionA, executionB));

        assertThat(descriptor.getPluginSize()).isEqualTo(executionB.getArtifactId().length());
        assertThat(descriptor.getExecutionIdSize()).isEqualTo(executionB.getExecutionId().length());
        assertThat(descriptor.getGoalSize()).isEqualTo(executionB.getGoal().length());
        assertThat(descriptor.getPhaseSize()).isEqualTo(executionB.getMojoDescriptor().getPhase().length());
    }

    @Test
    public void should_not_fail_when_finding_max_size_with_a_mojo_execution_with_a_null_mojo_descriptor() {

        MojoExecution executionA = aMojoExecution().withArtifactId("plugin-a")
                                                   .withPhase("phase-a")
                                                   .withExecutionId("execution-id-a")
                                                   .withGoal("goal-a")
                                                   .build();

        MojoExecution executionB = aMojoExecution().withArtifactId("plugin-b-longer-than-a")
                                                   .withExecutionId("execution-id-b-longer-than-b")
                                                   .withGoal("goal-b-longer-than-a")
                                                   .withoutMojoDescriptor()
                                                   .build();

        MojoExecution executionC = aMojoExecution().withArtifactId("plugin-c")
                                                   .withPhase(null)
                                                   .withExecutionId("execution-id-c")
                                                   .withGoal("goal-c")
                                                   .build();

        ListTableDescriptor result = ListTableDescriptor.of(asList(executionA, executionB, executionC));

        assertThat(result.getExecutionIdSize()).isEqualTo(executionB.getExecutionId().length());
        assertThat(result.getPluginSize()).isEqualTo(executionB.getArtifactId().length());
        assertThat(result.getGoalSize()).isEqualTo(executionB.getGoal().length());
        assertThat(result.getPhaseSize()).isEqualTo(executionA.getMojoDescriptor().getPhase().length());
    }

    @Test
    public void should_not_return_zero_as_max_size_when_phase_is_null() {

        MojoExecution execution = aMojoExecution().withArtifactId("plugin-a")
                .withPhase(null)
                .withExecutionId("execution-id-a")
                .withGoal("goal-a")
                .build();
        execution.setLifecyclePhase("phase-a");

        ListTableDescriptor result = ListTableDescriptor.of(asList(execution));

        assertThat(result.getPhaseSize()).isEqualTo(execution.getLifecyclePhase().length());
    }

    @Test
    public void should_return_column_title_as_max_size_when_source_is_null() {

        MojoExecution execution = aMojoExecution().withArtifactId(null)
                .withPhase(null)
                .withExecutionId(null)
                .withGoal(null)
                .build();
        execution.setLifecyclePhase(null);

        ListTableDescriptor result = ListTableDescriptor.of(asList(execution));

        assertThat(result.getPhaseSize()).isEqualTo(TableColumn.PHASE.title().length());
        assertThat(result.getExecutionIdSize()).isEqualTo(TableColumn.EXECUTION_ID.title().length());
        assertThat(result.getGoalSize()).isEqualTo(TableColumn.GOAL.title().length());
        assertThat(result.getPluginSize()).isEqualTo(TableColumn.ARTIFACT_ID.title().length());
    }

    @Test
    public void should_return_column_title_as_max_size_when_source_is_smaller() {

        MojoExecution execution = aMojoExecution().withArtifactId("a")
                .withPhase("a")
                .withExecutionId("a")
                .withGoal("a")
                .build();

        ListTableDescriptor result = ListTableDescriptor.of(asList(execution));

        assertThat(result.getPhaseSize()).isEqualTo(TableColumn.PHASE.title().length());
        assertThat(result.getExecutionIdSize()).isEqualTo(TableColumn.EXECUTION_ID.title().length());
        assertThat(result.getGoalSize()).isEqualTo(TableColumn.GOAL.title().length());
        assertThat(result.getPluginSize()).isEqualTo(TableColumn.ARTIFACT_ID.title().length());
    }

    @Test
    public void should_build_a_row_format_for_a_list_table_descriptor() {

        ListTableDescriptor descriptor = new ListTableDescriptor().setPluginSize(1)
                                                                  .setPhaseSize(2)
                                                                  .setExecutionIdSize(3)
                                                                  .setGoalSize(4);

        String result = descriptor.rowFormat();

        assertThat(result).isEqualTo("%-1s | %-2s | %-3s | %-4s");
    }
}
