## Notas sobre POC - Spring Cloud Stream com TestContainers, Spring Cloud Test Binder, RabbitMQ e RestAssure

- Este projeto tem como objetivo colocar em prática conceitos que eu aprendi sobre o Spring Cloud Stream e como usá-lo junto com RabbitMQ através de teste integrados usando JUnit/TestConteiners/RestAssure como ferramentas de testes.

## Links que eu consultei para entender como usar o Spring Cloud Stream junto com RabbitMQ com exemplos de testes integrados usando Spring Cloud Stream com e TestContainers
 - [spring-cloud-stream-event-sourcing-testcontainers](https://github.com/ivangfr/spring-cloud-stream-event-sourcing-testcontainers)
 - [Exemplo de teste de integração no lado PRODUCER/PUBLISHER usando Spring Cloud Stream Binder Test e TestContainers com MySQL](https://github.com/ivangfr/spring-cloud-stream-event-sourcing-testcontainers/blob/master/user-service/src/test/java/com/ivanfranchin/userservice/user/UserEmitterTest.java)
 - [Exemplo de teste de integração no lado LISTENER/SUBSCRIPTION usando Spring Cloud Stream Binder Test e TestContainers com MySQL](https://github.com/ivangfr/spring-cloud-stream-event-sourcing-testcontainers/blob/master/user-service/src/test/java/com/ivanfranchin/userservice/user/UserEmitterTest.java)


## Notas da POC 

### Conceitos do Spring Cloud Stream

 - Middleware neutral core: núcleo ou aplicação que não depende de nenhuma tecnologia específica de Message Broker, ou seja, o código de negócio não sabe se está usando RabbitMQ, Apache Kafka, AWS Kenesis ou qualquer outro sistema de mensagens.
 - O Spring Cloud Stream é um Middleware neutral core
   - A aplicação comunica-se com o mundo através de bindins entre o destino das mensagens expostos através de brokers e a entrada de dados através do meu código. Os detalhes para especificar os binders são definidos através das configurações especificas da implementação do binder (definidas no arquivo de configuração)
 - O Spring Cloud Stream poder ser executado em modo standalone a partir da IDE (para testes). Em produção precisa criar um executável usando as ferramentas do Spring Boot providas pelo maven/gradle.
 - The Binder Abstraction: o Spring Cloud Stream fornece implementações de binder para Kafka e RabbitMQ. Também fornece um binder de test para testes de integração. O binder provê a comunicação entre aplicação/message broker.
 - No Spring Cloud Stream o nome da fila pode ser uma combinação do nome da exchange (destination) + nome do grupo (group)
 - Terminação in-0 e out-0 e Stream Bridge. Não é obrigatório, mas é a recomendação seguida ao nomear beans de entradas e saída de dados. 
   - in-0: quer dizer que o binding é do tipo entradas de dados. Informa que a aplicação irá consumir eventos publicados em algum canal. A implementação consiste em declarar uma classe que implementa a interface Consumer<T>. 
   - out-0: quer dizer que o binding é do tipo saída de dados. Informa que a aplicação irá produzir e publicar eventos em algum canal. A implementação consiste em declarar uma classe que implemente a interface Supplier<T> ou criar um Supplier sob demanda com StreamBridge. 
   - Stream Bridge: permite enviar mensagens para qualquer binding sem precisar declarar um Supplier fixo.
 - Sempre estar atento ao chaveamento de propriedades do Spring Cloud Stream no yml de configuração
   - No lado Consumer, o nome de funções especificadas devem ser o mesmo das classes que implementam a interface Consumer para que o Spring Cloud Function consiga catalogar.


A fila associada a um exchange deve estar sendo consumida por um Consumer<T>, a aplicação Producer apenas declara a exchange.

Para consumir as mensagens encaminhadas ao Exchange a aplicação Consumer precisa declarar uma classe que implementa a interface Consumer<T>.

Como o Spring Cloud Stream sabe que essa classe irá de fato consumir os eventos da fila?
R: PELO NOME DA CLASSE
Ao tornar essa classe um componente do Spring usando @componente o bean terá o mesmo nome da classe( se não for declarado explicitamente) e também por fazer esta classe implementa a interface Consumer<T>  fará com que o Spring Cloud Stream (através do Spring Cloud Function) entenda que ali será consumido o evento que chegou a fila. É muito importante ter atenção com o nome da classe e o nome do binding do Spring Cloud Stream que é configurado no arquivo de configuração ‘application.yml’ da aplicação. Deve sempre ter o mesmo nome, a classe e o binding.

Conceito de binder do Spring Cloud Stream: É uma abstração de alto nível para Message Brokers como Kafka ou RabbitMQ, que mapeia a aplicação com Broker sem expor a aplicação a detalhes do Broker a nível de código.

a nível de código, os bindings do Spring Cloud Stream passa a ser o bean declarado com o mesmo nome (da classe ou definida explicitamente) da classe e sendo esta classe uma implementação de Consumer<T> Esses são as condições para ver a associação bike Spring Cloud string código

Em testes de integração observei que não é necessário usar um TestContâiner do RabbitMQ, isso também serve para outros messages brokers. Como  Spring Cloud Stream abstrai todo o broker real não faz sentido mesmo usar TestContâiner  do Broker Real. A recomendação da documentação é usar a anotação @Import(TestChannelBinderConfiguration.class) para fornecer um binder de test durante o teste, esse binder não tem nenhum integração com o TestContainer do RabbitMq.

Outra observação importante que constatei sobre o binder de teste descrito acima: não tem integração real com o testContâiner do rabbitmq, ou seja, diferente de quando a aplicação é executada, o binder de test não envia de fato uma mensagem ao testContâiner do RabbitMQ. No teste de integração foi possível validar apenas o que foi salvo no banco de dados (dados do usuário) e o que foi enviado ao binding mas para o cenário de aplicação Producer de eventos acredito que está abordagem seja válida e adequada, afinal não encontrei uma forma de obter o conteúdo de uma mensagem enviada há um exchange (a única forma de saber isso seria implementar uma fila e associá-la ao exchange de destino mas isso é de responsabilidade de aplicações Consumer que declara suas próprias filas e associaram com o exchange de onde esperam receber mensagens via fila).


Cenário do teste de integração no contexto Producer: o cenário consiste em salvar dados de um usuário e fazer o envio destes dados via processamento assíncrono com RAbbitMQ via Spring Cloud Stream.

Resumo do teste de integração na aplicação Producer: Para este cenário a validação consistiu em saber se o que foi salvo no banco de dados e o que foi enviado ao binding específico do Spring Cloud Stream (mas não de fato ao exchange no rabbitmq) estão em conformidade com o que foi enviado via requisição.

As descrições acima para testes de integração valem apenas para o que consegui observar no comportamento da aplicação Producer, mas para a aplicação Consumer o cenário foi diferente.

Na aplicação Consumer (apenas para o profile de teste de integração) é possível desabilitar o binder do Spring Cloud Stream e substituí-lo por um binder real do RabbitMQ e com isso de fato fazer um real teste de integração para validar se o que foi consumido/pŕocessado é igual ao dados enviada para o exchange/fila. Nessa abordagem de teste de integração eu removi o binder de teste do Spring Cloud Stream para o usar o binder do RabbitMQ. Nessa abordagem o ideal é usar os recursos da api do Spring AMQP como RabbitTemplate para fazer o envio de mensagens para a fila/exchange e validar usando os dados da mensagem enviada e os dados do que foi salvo após o evento da fila ter sido processado através do Listener. 


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
 - application-it.yml do teste integrado do Consumer para o cenário em questão. Para o Producer usei a anotação @Import(TestChannelBinderConfiguration.class) para o cenário de teste. O motivo: uma mensagem enviada há um exchange não pode ser recuperada (apenas com uma fila associada ao exchange, mas este cenário é para a aplicação Consumer). Usando o TestBinder (fornecido pela anotação citada antes) do Spring Cloud Stream é possível obter a mensagem enviada ao binder (mas não diretamente ao que foi enviado RabbitMQ pelos limites do Binder de test como descrito acima):  
   - ```yaml
     spring:
      cloud:
        stream:
          test-binder: true        
     ```
    