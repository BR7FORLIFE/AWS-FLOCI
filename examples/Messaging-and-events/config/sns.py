import json
import boto3
from mypy_boto3_sns import SNSClient

# SNS es un servicio en el cual nos ayuda a distribuir copias de los eventos que pasan en SNS
# los cuales lo distribuye alrededor de todos los servicios los cuales se subcriben
# esto permite que varios servicios obtengan el mismo evento pero con comportamientos distintos

snsClient: SNSClient = boto3.client("sns", endpoint_url="http://localhost:4566")

# crear un topic
topic = snsClient.create_topic(Name="property-events")
arnTopic = topic["TopicArn"]

# subcribirse a un topic
subcription = snsClient.subscribe(
    TopicArn=arnTopic,  # ARN del topic
    Protocol="sns", # protocolo, que en este caso es sns
    Endpoint="" #ARN del recurso al cual se va a subcribir 
)

# publicar un evento
def publishEvent(event: dict[str, str]): 
    snsClient.publish(TopicArn=arnTopic, Message=json.dumps(event))

# desuscribirse a un topic
def unSubcribePropertyEvent():
    snsClient.unsubscribe(SubscriptionArn=subcription["SubscriptionArn"])

# borrar un topic
def deletePropertyEventTopic():
    snsClient.delete_topic(TopicArn=arnTopic)