package ydsu.module.util.source

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assumptions.assumeThat

class DockerClientIntTest {
    DockerClient dockerClient

    @BeforeAll
    void beforeAll() {
        dockerClient = new DockerClient('archlinux', 'src/main/docker/archlinux')
                .withBindMount(System.getenv('HOST_WORKDIR') ?: '$(pwd)', '/source')
                .withEnv('YDSU_REPO', '/source')
        //dockerClient.removeImage()
        //assertThat(dockerClient.imageExists()).isFalse()
        dockerClient.run()
        assertThat(dockerClient.execInContainer('java -version')).startsWith('openjdk version')
        assertThat(dockerClient.imageExists()).isTrue()
        assertThat(dockerClient.isRunning()).isTrue()
    }

    @AfterAll
    void afterAll() {
        dockerClient.stopAndRemove()
        assertThat(dockerClient.isRunning()).isFalse()
    }

    @Test
    void 'can execute single command as script'() {
        assertThat(dockerClient.execScriptInContainer('java -version')).startsWith('openjdk version')
    }

    @Test
    void 'env variables evaluate to host\'s env'() {
        String hostHome = System.getProperty('user.home')
        String containerHome = '/home/ydsu'
        assumeThat(hostHome).isNotEqualTo(containerHome)

        assertThat(dockerClient.execInContainer('echo $HOME'))
                .isEqualTo(hostHome)
        assertThat(dockerClient.execInContainer('echo \$HOME'))
                .isEqualTo(hostHome)
        assertThat(dockerClient.execInContainer('echo \\$HOME'))
                .isEqualTo('$HOME')
    }

    @Test
    void 'env variables evaluate to container\'s env in script'() {
        String hostHome = System.getProperty('user.home')
        String containerHome = '/home/ydsu'
        assumeThat(hostHome).isNotEqualTo(containerHome)

        // command
        assertThat(dockerClient.execInContainer("bash -c 'echo \$HOME'"))
                .isEqualTo(containerHome)

        // script
        assertThat(dockerClient.execScriptInContainer('echo $HOME'))
                .isEqualTo(containerHome)
        assertThat(dockerClient.execScriptInContainer('echo \$HOME'))
                .isEqualTo(containerHome)
        assertThat(dockerClient.execScriptInContainer('echo \\$HOME'))
                .isEqualTo('$HOME')
        assertThat(dockerClient.execScriptInContainer('echo ~'))
                .isEqualTo(containerHome)
    }

    @Test
    void 'can execute multiline scripts'() {
        String script = """
mkdir /home/ydsu/temp
cd /home/ydsu/temp
pwd
echo ~
echo \$HOME
"""
        assertThat(dockerClient.execScriptInContainer(script))
                .isEqualTo('/home/ydsu/temp\n' +
                        '/home/ydsu\n' +
                        '/home/ydsu')
    }

    @Test
    void 'can use single quotes in multiline scripts'() {
        String script = """
echo 'single quoted text'
"""
        assertThat(dockerClient.execScriptInContainer(script))
                .isEqualTo('single quoted text')
    }

    @Test
    void 'can use double quotes in multiline scripts'() {
        String script = """
echo "\$HOME double quoted text"
"""
        assertThat(dockerClient.execScriptInContainer(script))
                .isEqualTo('/home/ydsu double quoted text')
    }

    @Test
    void 'can use redirection in multiline scripts'() {
        String script = """
cd ~
echo 'blah blah' > blah.txt
cat blah.txt
"""
        assertThat(dockerClient.execScriptInContainer(script))
                .isEqualTo('blah blah')
    }
}
