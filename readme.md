## Notas sobre POC - Spring Cloud Stream com TestContainers, Spring Cloud Test Binder, RabbitMQ e RestAssure

- Este projeto tem como objetivo colocar em prática conceitos que eu aprendi sobre o Spring Cloud Stream e como usá-lo junto com RabbitMQ através de teste integrados usando JUnit/TestConteiners/RestAssure como ferramentas de testes.
- A Prova de conceito consiste fazer a comunicação assincrona entre duas API's usando Spring Cloud Stream com RabbitMQ como message broker. Uma aplicação irá produzir um evento e isso será propagado para a outra aplicação através de uma exchange do Tipo Topic.

## Links que eu consultei para entender como usar o Spring Cloud Stream junto com RabbitMQ com exemplos de testes integrados usando Spring Cloud Stream com e TestContainers
 - [spring-cloud-stream-event-sourcing-testcontainers](https://github.com/ivangfr/spring-cloud-stream-event-sourcing-testcontainers)
 - [Exemplo de teste de integração no lado PRODUCER/PUBLISHER usando Spring Cloud Stream Binder Test e TestContainers com MySQL](https://github.com/ivangfr/spring-cloud-stream-event-sourcing-testcontainers/blob/master/user-service/src/test/java/com/ivanfranchin/userservice/user/UserEmitterTest.java)
 - [Exemplo de teste de integração no lado LISTENER/SUBSCRIPTION usando Spring Cloud Stream Binder Test e TestContainers com MySQL](https://github.com/ivangfr/spring-cloud-stream-event-sourcing-testcontainers/blob/master/user-service/src/test/java/com/ivanfranchin/userservice/user/UserEmitterTest.java)


## Notas da POC 

### Conceitos do Spring Cloud Stream

 - Middleware neutral core: núcleo ou aplicação que não depende de nenhuma tecnologia específica de Message Broker, ou seja, o código de negócio não sabe se está usando RabbitMQ, Apache Kafka, AWS Kenesis ou qualquer outro sistema de mensagens.
 - O Spring Cloud Stream é um Middleware neutral core
   - A aplicação comunica-se com o mundo através de bindins entre o destino das mensagens expostos através de brokers e a entrada de dados através do meu código. Os detalhes para especificar os binders são definidos através das configurações especificas da implementação do binder (definidas no arquivo de configuração)
 - The Binder Abstraction: o Spring Cloud Stream fornece implementações de binder para Kafka e RabbitMQ. Também fornece um binder de test para testes de integração. O binder provê a comunicação entre aplicação/message broker.
 - Com Rabbit, a fila padrão é destination.group (ex.: ead.user.event.ms-course). Pode-se customizar via propriedades do binder (ex.: queueNameGroupOnly).
 - Terminação in-0 e out-0 e Stream Bridge. Não é obrigatório, mas é a recomendação seguida ao nomear beans de entradas e saída de dados. 
   - in-0: quer dizer que o binding é do tipo entradas de dados. Informa que a aplicação irá consumir eventos publicados em algum canal. A implementação consiste em declarar uma classe que implementa a interface Consumer<T>. 
   - out-0: quer dizer que o binding é do tipo saída de dados. Informa que a aplicação irá produzir e publicar eventos em algum canal. A implementação consiste em declarar uma classe que implemente a interface Supplier<T> ou criar um Supplier sob demanda com StreamBridge. 
   - Stream Bridge: permite enviar mensagens para qualquer binding sem precisar declarar um Supplier fixo.
 - Sempre estar atento ao chaveamento de propriedades do Spring Cloud Stream no yml de configuração
   - No lado Consumer, o nome de funções especificadas no yml de configuração devem ser o mesmo dos beans que implementam a interface Consumer para que o Spring Cloud Function consiga catalogar. ## validar depoois


A fila associada a um exchange deve estar sendo consumida por um Consumer<T>, a aplicação Producer apenas declara a exchange.

Para consumir as mensagens encaminhadas ao Exchange a aplicação Consumer precisa declarar uma classe que implementa a interface Consumer<T>.

Como o Spring Cloud Stream sabe que essa classe irá de fato consumir os eventos da fila?
R: É o bean(caso não seja explicitamente definido, passa a ser o nome da classe) que implementa Supplier/Function/Consumer() e o que está definido em: **spring.cloud.function.definition**

Ao tornar essa classe um componente do Spring usando @componente o bean terá o mesmo nome da classe( se não for declarado explicitamente) e também por fazer esta classe implementa a interface Consumer<T>  fará com que o Spring Cloud Stream (através do Spring Cloud Function) entenda que ali será consumido o evento que chegou a fila. É muito importante ter atenção com o nome da classe e o nome do binding do Spring Cloud Stream que é configurado no arquivo de configuração ‘application.yml’ da aplicação. Deve sempre ter o mesmo nome, a classe e o binding.

Conceito de binder do Spring Cloud Stream: É uma abstração de alto nível para Message Brokers como Kafka ou RabbitMQ, que mapeia a aplicação com Broker sem expor a aplicação a detalhes do Broker a nível de código.

**No código, você declara um bean de função (ex.: Consumer<Message<UserEvent>> userEventListener). O SCS cria bindings userEventListener-in-0/-out-0 e você configura cada binding em spring.cloud.stream.bindings.<binding>.**

Ao executar testes de integração na aplicação Producer, observei que não é necessário usar um TestContâiner do RabbitMQ, isso também serve para outros messages brokers. O binder de test Spring Cloud Stream fornece um binder sem integração real com o contâiner. A recomendação da documentação é usar a anotação @Import(TestChannelBinderConfiguration.class) para fornecer um binder de test durante o teste, esse binder não tem nenhum integração com o TestContainer do RabbitMq.

O binder de teste não tem integração real com o testContâiner do rabbitmq, ou seja, diferente de quando a aplicação é executada, o binder de test não envia de fato uma mensagem ao testContâiner do RabbitMQ. No teste de integração foi possível validar apenas o que foi salvo no banco de dados (dados do usuário) e o que foi enviado ao binding mas para o cenário de aplicação Producer de eventos acredito que está abordagem seja válida e adequada, afinal não encontrei uma forma de obter o conteúdo de uma mensagem enviada há um exchange (a única forma de saber isso seria implementar uma fila e associá-la ao exchange de destino mas isso é de responsabilidade de aplicações Consumer que declara suas próprias filas e associaram com o exchange de onde esperam receber mensagens via fila).
 - **Com o Test Binder, valido payload e headers capturando mensagens em OutputDestination (Producer) ou injetando em InputDestination (Consumer), sem Rabbit real.**

**Para Producer, use o Test Binder e capture mensagens via OutputDestination (rápido e determinístico). Para Consumer ou cenários E2E, use RabbitMQ com Testcontainers e publique/consuma de filas reais via RabbitTemplate.**

Cenário do teste de integração no contexto Producer: o cenário consiste em salvar dados de um usuário e fazer o envio destes dados via processamento assíncrono com RAbbitMQ via Spring Cloud Stream.

Resumo do teste de integração na aplicação Producer: Para este cenário a validação consistiu em saber se o que foi salvo no banco de dados e o que foi enviado ao binding específico do Spring Cloud Stream (mas não de fato ao exchange no rabbitmq) estão em conformidade com o que foi enviado via requisição.

As descrições acima para testes de integração valem apenas para o que consegui observar no comportamento da aplicação Producer, mas para a aplicação Consumer o cenário foi diferente.

Na aplicação Consumer (apenas para o profile de teste de integração) é possível **substituir** o binder do Spring Cloud Stream por um binder real do RabbitMQ e com isso de fato fazer um real teste de integração para validar se o que foi consumido/pŕocessado é igual ao dados enviada para o exchange/fila. Nessa abordagem de teste de integração eu removi o binder de teste do Spring Cloud Stream para o usar o binder do RabbitMQ. Nessa abordagem o ideal é usar os recursos da api do Spring AMQP como RabbitTemplate para fazer o envio de mensagens para a fila/exchange e validar usando os dados da mensagem enviada e os dados do que foi salvo após o evento da fila ter sido processado através do Listener. 


## Configurações importantes para ambientes de teste de integração usando Spring Cloud Stream, RabbitMQ e TestContainers
 - No profile de teste de integração da aplicação Consumer eu precisei habilitar o binder do rabbitmq como padrão. Fiz isso para conseguir usar os recursos do rabbitmq para de forma programática fazer envio de mensagens para uma fila e válidar se o Listener se comporta como esperado. 
 - Ao fazer isso eu desabilito o Binder padrão do Spring Cloud Stream, por isso essa configuração **vale apenas** para ambientes de testes integrados locais da aplicação. O motivo disso é porque o binder de teste oferecido pelo Spring CLoud Stream usando a anotação @Import(TestChannelBinderConfiguration.class) fornece um binder para testes que não se integra ao contâiner do rabbit. Com o binder de teste do Spring Cloud Stream não foi possível fazer uso de recursos importantes do RAbbitMQ para o cenário em questão, ex: associar uma RoutingKey há uma fila e uma mensagem, até pode fazer mas na realidade o teste vai ignorar isso e apenas levar em conta o endereço do destino da mensagem. Porque RountingKey não é propriedade do Binder do Spring Cloud Stream e sim do Rabbit.
 - Dados o cenário acima, para consumers de eventos acredito que usar 
 - application-it.yml do teste integrado do Consumer para o cenário em questão:
   - ```yaml
     spring:
      cloud:
        stream:
          default-binder: rabbit        
     ```
 - application-it.yml do teste integrado do Consumer para o cenário em questão. Para o Producer usei a anotação @Import(TestChannelBinderConfiguration.class) para o cenário de teste. O motivo: Em Rabbit, exchanges não armazenam mensagens; apenas roteiam para filas (Para inspecionar mensagens, deve-se vincular uma fila (fixa ou temporária) ao exchange/rota e consuma dessa fila.). Usando o TestBinder (fornecido pela anotação citada antes) do Spring Cloud Stream é possível obter a mensagem enviada ao binder (mas não diretamente ao que foi enviado RabbitMQ pelos limites do Binder de test como descrito acima):  
   - ```yaml
     spring:
      cloud:
        stream:
          test-binder: true        
     ```
## Arquitetura
- Broker: RabbitMQ (exchange topic).
- Producer: publica UserCreatedEvent em ead.user.event com routingKey=ead.user.created.
- Consumer: userEventListener consome da fila ead.user.event.ms-course.

## Pŕe requisitos
- JDK 21
- Docker (para Testcontainers)
- Maven 3.9+

## Payloads & contratos
```json
{
  "userId": "3f7a1c24-9d0e-4a7e-8f9c-6c9acbf5b5c1",
  "userName": "rmelo",
  "email": "ronaldo.melo@example.com",
  "fullName": "Ronaldo Melo",
  "userStatus": "ACTIVE",
  "userType": "ADMIN",
  "phoneNumber": "+5591999990000",
  "imageUrl": "https://cdn.example.com/users/3f7a1c24/avatar.jpg",
  "actionType": "CREATED"
}

```
