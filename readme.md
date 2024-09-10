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
  
## Step by step to run

1. in terminal, move to folder `/src/main/resources` and run command `docker compose up -d`;
2. run application in IDE or terminal;
3. send a request like example;

### Example of request

1) Analyse the model (UML)
```
curl --location 'http://localhost:8080/kafka/send' \
--form 'file=@"[path_to_uml_file]/example.uml"'
```

2) Retrieve the result
```
curl --location 'http://localhost:8080/kafka/inconsistencies/[code_from_first_request]'
```

## License

This repository is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License. You may not use the material for commercial purposes. See the [LICENSE](LICENSE) file for more details.
