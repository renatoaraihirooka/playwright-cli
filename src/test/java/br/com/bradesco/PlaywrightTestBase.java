package br.com.bradesco;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@ExtendWith(EvidenceFailureExtension.class)
public abstract class PlaywrightTestBase {
    protected Playwright playwright;
    protected Browser browser;
    protected Page page;
    private EvidenceMode evidenceMode;
    private boolean failureScreenshotCaptured;

    @BeforeEach
    void setUp() {
        evidenceMode = loadEvidenceMode();
        failureScreenshotCaptured = false;
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
        if (page != null && (evidenceMode == EvidenceMode.ALL_ACTIONS
                || evidenceMode == EvidenceMode.FINAL_SCREEN)) {
            attachScreenshot("final-state");
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Step("Navegar para {url}")
    protected void navigateWithEvidence(String url) {
        try {
            page.navigate(url);
            captureActionScreenshot("navigate");
        } catch (RuntimeException exception) {
            captureFailureScreenshot("navigate-failure");
            throw exception;
        }
    }

    @Step("Preencher {description}")
    protected void fillWithEvidence(String description, Locator locator, String value) {
        try {
            locator.fill(value);
            captureActionScreenshot(description);
        } catch (RuntimeException exception) {
            captureFailureScreenshot(description + "-failure");
            throw exception;
        }
    }

    @Step("Pressionar Enter em {description}")
    protected void pressEnterWithEvidence(String description, Locator locator) {
        try {
            locator.press("Enter");
            captureActionScreenshot(description);
        } catch (RuntimeException exception) {
            captureFailureScreenshot(description + "-failure");
            throw exception;
        }
    }

    @Step("Validar texto de {description}")
    protected void assertTextWithEvidence(String description, Locator locator, String expectedText) {
        try {
            org.junit.jupiter.api.Assertions.assertEquals(expectedText, locator.textContent().trim());
            captureActionScreenshot(description);
        } catch (AssertionError | RuntimeException exception) {
            captureFailureScreenshot(description + "-failure");
            throw exception;
        }
    }

    @Step("Validar estado invalido de {description}")
    protected void assertInvalidWithEvidence(String description, Locator locator) {
        try {
            boolean invalid = (Boolean) locator.evaluate("element => !element.checkValidity()");
            org.junit.jupiter.api.Assertions.assertTrue(invalid, "O campo deveria estar invalido");
            captureActionScreenshot(description);
        } catch (AssertionError | RuntimeException exception) {
            captureFailureScreenshot(description + "-failure");
            throw exception;
        }
    }

    void captureFailureScreenshot() {
        captureFailureScreenshot("failure-state");
    }

    private void captureActionScreenshot(String name) {
        if (evidenceMode == EvidenceMode.ALL_ACTIONS) {
            attachScreenshot(name);
        }
    }

    private void captureFailureScreenshot(String name) {
        if (evidenceMode == EvidenceMode.FAILURE_ONLY && !failureScreenshotCaptured) {
            failureScreenshotCaptured = true;
            attachScreenshot(name);
        }
    }

    private EvidenceMode loadEvidenceMode() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/test.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel carregar test.properties", exception);
        }
        String configuredMode = System.getProperty(
                "evidence.mode",
                properties.getProperty("evidence.mode")
        );
        return EvidenceMode.from(configuredMode);
    }

    private void attachScreenshot(String name) {
        if (page == null) {
            return;
        }
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
    }
}