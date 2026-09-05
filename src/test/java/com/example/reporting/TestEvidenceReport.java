package com.example.reporting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class TestEvidenceReport {
    private TestEvidenceReport() {
    }

    public static void main(String[] args) throws Exception {
        Path projectDirectory = Paths.get("").toAbsolutePath();
        Path surefireDirectory = projectDirectory.resolve("target/surefire-reports");
        Path evidenceDirectory = projectDirectory.resolve("target/test-evidence");
        Path reportDirectory = projectDirectory.resolve("target/reports");
        Files.createDirectories(reportDirectory);

        List<TestResult> results = readResults(surefireDirectory, evidenceDirectory);
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\">")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
                .append("<title>Relatorio de evidencias dos testes</title>")
                .append("<style>body{font-family:Arial,sans-serif;margin:2rem;color:#202124}h1{margin-bottom:.4rem}.summary{margin-bottom:1.5rem}table{border-collapse:collapse;width:100%}th,td{border:1px solid #dadce0;padding:.65rem;text-align:left;vertical-align:top}th{background:#f1f3f4}.passed{color:#137333;font-weight:700}.failed{color:#c5221f;font-weight:700}img{max-width:480px;max-height:320px;border:1px solid #dadce0}a{color:#1a73e8}</style></head><body>")
                .append("<h1>Relatorio de evidencias dos testes</h1>")
                .append("<div class=\"summary\">Total: ").append(results.size())
                .append(" | Aprovados: ").append(results.stream().filter(TestResult::passed).count())
                .append(" | Falhas/erros: ").append(results.stream().filter(result -> !result.passed()).count())
                .append("</div><table><thead><tr><th>Classe</th><th>Teste</th><th>Status</th><th>Tempo</th><th>Evidencia</th></tr></thead><tbody>");

        for (TestResult result : results) {
            html.append("<tr><td>").append(escape(result.className()))
                    .append("</td><td>").append(escape(result.testName()))
                    .append("</td><td class=\"").append(result.passed() ? "passed" : "failed")
                    .append("\">").append(result.passed() ? "APROVADO" : "FALHOU")
                    .append("</td><td>").append(escape(result.time())).append(" s</td><td>");
            if (result.evidenceFile() != null) {
                String evidencePath = "../test-evidence/" + result.evidenceFile();
                html.append("<a href=\"").append(evidencePath).append("\"><img src=\"")
                        .append(evidencePath).append("\" alt=\"Screenshot de ")
                        .append(escape(result.testName())).append("\"></a>");
            } else {
                html.append("Nenhuma imagem encontrada");
            }
            html.append("</td></tr>");
        }

        html.append("</tbody></table></body></html>");
        Files.writeString(reportDirectory.resolve("test-evidence.html"), html.toString());
    }

    private static List<TestResult> readResults(Path surefireDirectory, Path evidenceDirectory) throws Exception {
        List<TestResult> results = new ArrayList<>();
        if (!Files.isDirectory(surefireDirectory)) {
            return results;
        }
        try (Stream<Path> files = Files.list(surefireDirectory)) {
            for (Path file : files.filter(path -> path.getFileName().toString().startsWith("TEST-")
                            && path.getFileName().toString().endsWith(".xml"))
                    .sorted(Comparator.comparing(Path::toString)).toList()) {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file.toFile());
                Element suite = document.getDocumentElement();
                String className = suite.getAttribute("name");
                NodeList testCases = suite.getElementsByTagName("testcase");
                for (int index = 0; index < testCases.getLength(); index++) {
                    Element testCase = (Element) testCases.item(index);
                    String testName = testCase.getAttribute("name");
                    boolean passed = testCase.getElementsByTagName("failure").getLength() == 0
                            && testCase.getElementsByTagName("error").getLength() == 0;
                        String evidencePrefix = sanitize(className.substring(className.lastIndexOf('.') + 1)
                            + "_" + testName);
                        String evidenceFile = findEvidenceFile(evidenceDirectory, evidencePrefix);
                    results.add(new TestResult(className, testName, passed,
                            testCase.getAttribute("time"), evidenceFile));
                }
            }
        }
        return results;
    }

    private static String findEvidenceFile(Path evidenceDirectory, String evidencePrefix) throws IOException {
        if (!Files.isDirectory(evidenceDirectory)) {
            return null;
        }
        try (Stream<Path> files = Files.list(evidenceDirectory)) {
            return files.filter(path -> path.getFileName().toString().startsWith(evidencePrefix)
                            && path.getFileName().toString().endsWith(".png"))
                    .map(path -> path.getFileName().toString())
                    .findFirst()
                    .orElse(null);
        }
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private record TestResult(String className, String testName, boolean passed, String time,
                              String evidenceFile) {
    }
}