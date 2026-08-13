import boto3
from mypy_boto3_sqs import SQSClient
import json

# SQS es un servicio que nos permite tener una cola de eventos donde los servicios subcritos pueden recibir
# dicho evento y procesarlo, lo que hace es distribuir los distintos eventos alrededor de los distintos 
# servicios, si un servicio procesa correctamente el evento, este mismo puede eliminar el evento de SQS
# cada evento es procesado por un servicio lo que conlleva que varios servicios tengan varios eventos
# diferentes a diferencia de SNS donde todos reciben una copia del mismo evento (servicios que se han subcrito)

client: SQSClient = boto3.client("sqs", endpoint_url="http://localhost:4566") # servicio de floci ejecutando
queue = client.create_queue(QueueName="messages")

queueUrl = queue["QueueUrl"] # url donde esta la cola messages

def sendMessage(message: dict[str, str]):
    client.send_message(QueueUrl=queueUrl, MessageBody= json.dumps(message))

def receiveMessage(): 
    response = client.receive_message(QueueUrl=queueUrl)
    return response["Messages"][0]

def removeMessage(receiptHandle: str): 
    client.delete_message(QueueUrl=queueUrl, ReceiptHandle=receiptHandle)