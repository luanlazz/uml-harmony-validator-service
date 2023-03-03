# Continuous detection of Conflicts and Inconsistencies in UML

## Description

The program analyzes UML diagrams class and sequence to find inconsistencies based on a UML file provided on the request.

### List of inconsistencies

- Várias definições de Classes com mesmo nome (Cm);
- Várias definições de Objetos com mesmo nome (Om);
- Classe não instanciada no SD (CnSD);
- Objeto sem Classe no CD (CnCD);
- Mensagem na direção Errada (ED);
- Mensagem sem Nome (EnN);
- Mensagem sem Método (EcM);
- Classe abstrata instanciada no SD (CaSD);
- Mensagem para função privada em CD (EpM)

#### Contribution

UML Parser is based on the [Eclipse modeling models reader](https://github.com/hammadirrshad/emf-uml-model-reader). Selected parsers needed to analyze inconsistencies were used. In addition, some small modifications/adaptations in treatment.

## Instructions to run

1. in terminal, move to `/src/main/resources` and run command `docker compose up -d`;
2. run application in IDE or terminal;
3. send a request like example;

### Example of request

```
curl --location --request POST 'http://localhost:8080/kafka/send?filePath=%2Fhome%2Fluan%2FDocuments%2Fprojects%2Feclipse-workspace-papyrus%2FPapyrus%2FPapyrus.uml&version=001'
```