# Playwright Java com Maven e JUnit 5

Estrutura inicial para testes de UI com Java 17, Maven, JUnit 5 e Playwright Java.

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.9 ou superior
- Node.js, usado pelo instalador dos navegadores do Playwright

## Instalação

Baixe os navegadores do Playwright uma vez na máquina:

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

## Executar os testes

```bash
mvn test
```

Para executar somente o teste smoke:

```bash
mvn -Dtest=SmokeTest test
```

O teste usa Chromium em modo headless por padrão. Para depurar visualmente, altere `setHeadless(false)` em `PlaywrightTestBase`.