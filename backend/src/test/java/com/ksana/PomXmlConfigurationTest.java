package com.ksana;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that verify the structure and configuration of backend/pom.xml,
 * specifically validating the checkstyle plugin configuration following
 * the removal of the explicit encoding element.
 */
class PomXmlConfigurationTest {

    private static Document pomDocument;
    private static XPath xpath;

    @BeforeAll
    static void loadPomXml() throws Exception {
        // Locate pom.xml relative to the project root
        Path pomPath = resolvePomPath();
        assertTrue(pomPath.toFile().exists(),
                "pom.xml must exist at: " + pomPath.toAbsolutePath());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setExpandEntityReferences(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        pomDocument = builder.parse(pomPath.toFile());
        pomDocument.getDocumentElement().normalize();

        xpath = XPathFactory.newInstance().newXPath();
    }

    // -------------------------------------------------------------------------
    // Tests for the PR change: encoding element commented out in checkstyle config
    // -------------------------------------------------------------------------

    @Test
    void checkstyleConfig_shouldNotHaveExplicitEncoding() throws Exception {
        // After the PR change, <encoding>UTF-8</encoding> is commented out,
        // so it must NOT appear as a live XML element in the checkstyle configuration.
        NodeList encodingNodes = (NodeList) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']//configuration/encoding",
                pomDocument, XPathConstants.NODESET);
        assertEquals(0, encodingNodes.getLength(),
                "The checkstyle plugin configuration must not contain an active <encoding> element "
                        + "(it was intentionally commented out in this PR)");
    }

    @Test
    void checkstyleConfig_shouldNotHaveEncodingAnywhere() throws Exception {
        // Regression: no encoding element should exist anywhere inside
        // the checkstyle plugin's configuration block.
        String encodingValue = (String) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']//configuration/encoding/text()",
                pomDocument, XPathConstants.STRING);
        assertTrue(encodingValue == null || encodingValue.isBlank(),
                "No encoding text content should be present in checkstyle configuration");
    }

    // -------------------------------------------------------------------------
    // Tests for checkstyle plugin configuration correctness after the change
    // -------------------------------------------------------------------------

    @Test
    void checkstyleConfig_shouldHaveCorrectConfigLocation() throws Exception {
        String configLocation = (String) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']//configuration/configLocation/text()",
                pomDocument, XPathConstants.STRING);
        assertEquals(
                "${maven.multiModuleProjectDirectory}/checkstyle/checkstyle.xml",
                configLocation,
                "configLocation must reference the project-level checkstyle.xml via multiModuleProjectDirectory");
    }

    @Test
    void checkstyleConfig_shouldHaveConsoleOutputEnabled() throws Exception {
        String consoleOutput = (String) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']//configuration/consoleOutput/text()",
                pomDocument, XPathConstants.STRING);
        assertEquals("true", consoleOutput,
                "consoleOutput must be true so checkstyle violations are visible in build logs");
    }

    @Test
    void checkstyleConfig_shouldFailOnError() throws Exception {
        String failsOnError = (String) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']//configuration/failsOnError/text()",
                pomDocument, XPathConstants.STRING);
        assertEquals("true", failsOnError,
                "failsOnError must be true to break the build on checkstyle violations");
    }

    @Test
    void checkstyleConfig_shouldNotLinkXRef() throws Exception {
        String linkXRef = (String) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']//configuration/linkXRef/text()",
                pomDocument, XPathConstants.STRING);
        assertEquals("false", linkXRef,
                "linkXRef must be false to avoid cross-reference report generation");
    }

    @Test
    void checkstylePlugin_shouldHaveCorrectVersion() throws Exception {
        String version = (String) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']/version/text()",
                pomDocument, XPathConstants.STRING);
        assertEquals("3.3.1", version,
                "maven-checkstyle-plugin version must be 3.3.1");
    }

    @Test
    void checkstylePlugin_shouldHaveValidateExecutionInValidatePhase() throws Exception {
        String phase = (String) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']//executions/execution[id='validate']/phase/text()",
                pomDocument, XPathConstants.STRING);
        assertEquals("validate", phase,
                "Checkstyle execution must be bound to the 'validate' phase");
    }

    @Test
    void checkstylePlugin_shouldHaveCheckGoal() throws Exception {
        String goal = (String) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']//executions/execution[id='validate']/goals/goal/text()",
                pomDocument, XPathConstants.STRING);
        assertEquals("check", goal,
                "Checkstyle execution must use the 'check' goal");
    }

    @Test
    void checkstylePlugin_shouldBePresent() throws Exception {
        // Sanity check: the plugin element itself must exist
        Node pluginNode = (Node) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']",
                pomDocument, XPathConstants.NODE);
        assertNotNull(pluginNode,
                "maven-checkstyle-plugin must be declared in the build/plugins section");
    }

    // -------------------------------------------------------------------------
    // Boundary / negative cases
    // -------------------------------------------------------------------------

    @Test
    void checkstyleConfig_shouldHaveExactlyFourConfigurationChildren() throws Exception {
        // After commenting out encoding, the active configuration elements
        // are: configLocation, consoleOutput, failsOnError, linkXRef (4 elements).
        NodeList children = (NodeList) xpath.evaluate(
                "//plugin[artifactId='maven-checkstyle-plugin']//configuration/*",
                pomDocument, XPathConstants.NODESET);
        assertEquals(4, children.getLength(),
                "Checkstyle configuration must contain exactly 4 active elements "
                        + "(configLocation, consoleOutput, failsOnError, linkXRef) "
                        + "after encoding was commented out");
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private static Path resolvePomPath() {
        // When Maven runs tests the working directory is the module root (backend/)
        Path candidate = Paths.get("pom.xml");
        if (candidate.toFile().exists()) {
            return candidate;
        }
        // Fallback for IDEs that may set the working directory to the repo root
        return Paths.get("backend", "pom.xml");
    }
}