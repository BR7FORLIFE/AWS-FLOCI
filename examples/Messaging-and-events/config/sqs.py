import boto3
from mypy_boto3_sqs import SQSClient
import json

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