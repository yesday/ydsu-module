package ydsu.module.util.source

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static org.assertj.core.api.Assertions.assertThat

class LoggingBaseScriptIntTest {
    DockerClient dockerClient

    @BeforeAll
    void beforeAll() {
        dockerClient = new DockerClient('archlinux', 'src/main/docker/archlinux')
                .withBindMount(System.getenv('HOST_WORKDIR') ?: '$(pwd)', '/source')
        dockerClient.run()
        assertThat(dockerClient.execInContainer('java -version')).startsWith('openjdk version')
        assertThat(dockerClient.imageExists()).isTrue()
        assertThat(dockerClient.isRunning()).isTrue()
        dockerClient.execScriptInContainer('mkdir -p ~/appdata/ydsu/conf/yesday-ydsu-manage/')
        dockerClient.execScriptInContainer('cp /source/src/test/resources/ydsu/module/manage/conf/module-local.yml ~/appdata/ydsu/conf/yesday-ydsu-manage/module.yml')
        String out = dockerClient.execScriptInContainer('curl -s "https://raw.githubusercontent.com/yesday/ydsu/master/src/main/bash/install/ydsu-install.sh" | bash')
        assertThat(out).doesNotContain('error').doesNotContain('Exception')
                .contains('Loaded single module skeleton')
                .contains('Loaded single module inttestscript')
    }

    @AfterAll
    void afterAll() {
        dockerClient.stopAndRemove()
        assertThat(dockerClient.isRunning()).isFalse()
    }

    @Test
    void 'can log at all levels when invoked from within a shell script'() {
        assertThat(dockerClient.execInContainer('bash -ic printLoggingBaseScript'))
                .endsWith('Foo info\n' +
                        'WARN  Foo warn\n' +
                        'ERROR Foo error')
                .doesNotContain('Foo debug')
                .doesNotContain('DEBUG ')
                .doesNotContain('INFO ')
    }
}
