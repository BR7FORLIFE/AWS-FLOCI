import json
import boto3
from mypy_boto3_events import EventBridgeClient
from mypy_boto3_events.type_defs import PutEventsRequestEntryTypeDef

# ARN -> Amazon Resource Name

# evntBridge nos permite desacoplar servicios y nos da enrutamiento de eventos siguiendo unas reglas
client: EventBridgeClient = boto3.client("events", endpoint_url= "http://localhost:4566")

eventBus = client.create_event_bus(Name="general-event-bus") # general event bus
arnEventBus = eventBus["EventBusArn"]

# creamos una regla para recibir un evento de producer verificado
client.put_rule(Name="send-message-rule", EventPattern=json.dumps({
    "source": "config.eventBridge", # la fuente de quien envia el evento (productor)
    "detail-type": "SendMessage" # tipo de evento a la cual aplica esta regla
}), EventBusName="general-event-bus") # para que bus de eventos pertenece dicha regla

# creamos un put targets esto nos permite enviar el evento cuando se cumple la regla hacia algun 
# objetivo, servicios de aws, microservicios, etc. 
client.put_targets(
    EventBusName="general-event-bus", # a que bus de eventos aplica dicho accion o objetivo
    Rule="send-message-rule",  # a que regla en especifico de ese bus de eventos aplica este objetivo
    Targets=[
        {
            "Id": "message-queue", # identificador del target dentro de la rule 
            "Arn": "" # arn del servicio sqs que queremos llamar AWS
        }
    ] # lo que se pretende hacer, por ejemplo en nuestro caso enviaremos un mensaje a sqs
)

def put_event(event: PutEventsRequestEntryTypeDef): 
    client.put_events(Entries=[event])

# se pueden listar las reglas event bus
# se pueden borrar reglas en un event bus
# se pueden elinar targets dentro de un event bus
# se pueden listar targets dentro de un event bus


