# Gestão de pedidos

API REST feita com Java 17, Spring Boot 3.2.12, JPA, H2 e Swagger.

## Executar

Use um JDK 17 ou 21 e execute na pasta do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Swagger: http://localhost:8080/swagger-ui/index.html

Console H2: http://localhost:8080/h2-console

No console, use a URL `jdbc:h2:mem:pedidosdb`, usuário `sa` e senha vazia. Os dados ficam em memória e são apagados quando a aplicação encerra.

## Rotas

| Método | Rota | Resultado |
| --- | --- | --- |
| GET | /pedidos | Lista os pedidos |
| GET | /pedidos/{id} | Busca um pedido |
| POST | /pedidos | Cria um pedido |
| PUT | /pedidos/{id} | Atualiza um pedido |
| DELETE | /pedidos/{id} | Exclui um pedido |

Exemplo de JSON para criar ou atualizar:

```json
{
  "cliente": "Mateus",
  "valorTotal": 125.50
}
```

O cliente é obrigatório e o valor total deve ser informado e maior que zero. A criação retorna 201, a exclusão retorna 204 e um ID inexistente retorna 404. Dados inválidos retornam 400 com timestamp, status, mensagem e erros por campo.

## Testes

```powershell
.\mvnw.cmd test
```

Os testes verificam o CRUD com H2, as validações, os retornos 404 e a documentação Swagger.

## Gerar o JAR

```powershell
.\mvnw.cmd clean package
java -jar target/gestao-pedidos-0.0.1-SNAPSHOT.jar
```
