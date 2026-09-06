package br.com.bradesco;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class EvidenceFailureExtension implements TestExecutionExceptionHandler {
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Object testInstance = context.getRequiredTestInstance();
        if (testInstance instanceof PlaywrightTestBase testBase) {
            testBase.captureFailureScreenshot();
        }
        throw throwable;
    }
}