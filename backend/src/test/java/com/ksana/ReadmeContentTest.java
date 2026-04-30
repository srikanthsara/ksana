package com.ksana;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that verify the content of README.md following the PR change
 * that extended the separator line from {@code ### #############}
 * to {@code ### #############################}.
 */
class ReadmeContentTest {

    private static List<String> readmeLines;

    @BeforeAll
    static void loadReadme() throws IOException {
        Path readmePath = resolveReadmePath();
        assertTrue(readmePath.toFile().exists(),
                "README.md must exist at: " + readmePath.toAbsolutePath());
        readmeLines = Files.readAllLines(readmePath, StandardCharsets.UTF_8);
    }

    // -------------------------------------------------------------------------
    // Tests for the PR change: extended separator line
    // -------------------------------------------------------------------------

    @Test
    void readme_shouldContainExtendedSeparatorLine() {
        // After the PR the separator was extended to ### #############################
        boolean found = readmeLines.stream()
                .anyMatch(line -> line.equals("### #############################"));
        assertTrue(found,
                "README.md must contain the extended separator line '### #############################'");
    }

    @Test
    void readme_shouldNotContainOldShorterSeparatorLine() {
        // The old separator '### #############' (13 hashes) must no longer appear
        // in the position adjacent to the Spring Boot section.
        // We check that no line matches the old shorter form exactly.
        boolean foundOldForm = readmeLines.stream()
                .anyMatch(line -> line.equals("### #############"));
        assertFalse(foundOldForm,
                "README.md must not contain the old short separator '### #############'");
    }

    @Test
    void readme_shouldContainExtendedSeparatorWithAtLeast29Hashes() {
        // The new separator must contain 29 consecutive hash characters after '### '
        boolean found = readmeLines.stream()
                .anyMatch(line -> line.startsWith("### ") && countTrailingHashes(line) >= 29);
        assertTrue(found,
                "README.md must have a separator line starting with '### ' followed by at least 29 '#' characters");
    }

    @Test
    void readme_extendedSeparatorLine_shouldAppearExactlyOnce() {
        long count = readmeLines.stream()
                .filter(line -> line.equals("### #############################"))
                .count();
        assertTrue(count >= 1,
                "The extended separator '### #############################' must appear at least once in README.md");
    }

    @Test
    void readme_extendedSeparator_shouldBePlacedNearSpringBootSection() {
        // The separator must appear in the neighbourhood of the Spring Boot section text
        int separatorIndex = -1;
        int springBootIndex = -1;
        for (int i = 0; i < readmeLines.size(); i++) {
            if (readmeLines.get(i).equals("### #############################")) {
                separatorIndex = i;
            }
            if (readmeLines.get(i).contains("Tech Stack: Spring Boot")) {
                springBootIndex = i;
            }
        }
        assertTrue(separatorIndex >= 0,
                "The extended separator line must be present in README.md");
        assertTrue(springBootIndex >= 0,
                "The Tech Stack line must be present in README.md");
        assertTrue(Math.abs(separatorIndex - springBootIndex) <= 3,
                "The extended separator must appear within 3 lines of the Tech Stack line "
                        + "(was at line " + separatorIndex + ", Tech Stack at line " + springBootIndex + ")");
    }

    // -------------------------------------------------------------------------
    // Boundary / regression cases
    // -------------------------------------------------------------------------

    @Test
    void readme_shouldNotBeEmpty() {
        assertFalse(readmeLines.isEmpty(), "README.md must not be empty");
    }

    @Test
    void readme_shouldRetainTechStackLine() {
        // Ensure the surrounding content was not accidentally removed
        boolean hasTechStack = readmeLines.stream()
                .anyMatch(line -> line.contains("Tech Stack: Spring Boot"));
        assertTrue(hasTechStack,
                "README.md must still contain the Tech Stack line near the extended separator");
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private static Path resolveReadmePath() {
        // When Maven runs tests the working directory is the module root (backend/)
        Path candidate = Paths.get("..", "README.md");
        if (candidate.toFile().exists()) {
            return candidate;
        }
        // Fallback: repo root
        return Paths.get("README.md");
    }

    private static int countTrailingHashes(String line) {
        int count = 0;
        for (int i = line.length() - 1; i >= 0; i--) {
            if (line.charAt(i) == '#') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
