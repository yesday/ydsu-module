package ydsu.module

import groovy.util.logging.Slf4j
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL

/**
 * Custom Extension which executes code only once after all tests are completed. This is temporary solution until
 * https://github.com/junit-team/junit5/issues/456 is released.
 *
 * Works in parallel mode.
 */
@Slf4j
class AfterAllTestsExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    @Override
    void beforeAll(ExtensionContext context) throws Exception {
        context.getRoot().getStore(GLOBAL).put(true, this)
    }

    /**
     * CloseableResource implementation, adding value into GLOBAL context is required to  registers a callback hook
     * With such steps close() method will be executed only once in the end of test execution
     */
    @Override
    void close() throws Exception {
        // clean data from system
        finalTearDownExecutedAfterAllTests();
    }

    void finalTearDownExecutedAfterAllTests() {
        log.info 'Execute a final tear down after all tests have been completed'
    }
}