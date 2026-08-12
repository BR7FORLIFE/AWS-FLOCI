import json
from datetime import datetime, timezone

# SQS
from config.sqs import sendMessage

def ProducerSQSSendMessage(): 
    obj: dict[str, str] = {
        "name": "bryan",
        "age": "19",
        "occupation": "software developer"
    }

    sendMessage(message=obj)

ProducerSQSSendMessage()

# EventBridge
from config.eventBridge import put_event

def ProducerEventBridgeSendMessage(): 
    put_event(event={
        "Detail": json.dumps({"message": "mensage desde eventBridge"}), # contenido del evento
        "DetailType": "SendMessage", # el tipo de evento !Importante para las rules de eventBridge
        "EventBusName": "general-event-bus", # nombre del event bus a donde queremos publicar el evento
        "Resources": "", # Opcional, representa los recursos relacionados con el evento (ej: s3::myBucket)
        "Source": "config.eventBridge", # quien produjo el evento? en este caso la fuente es este archivo
        "Time": datetime.now(timezone.utc), # momento en el que ocurreio el evento
        "TraceHeader": "" # sirve para transportar tracing, no importante en este caso
    })

ProducerEventBridgeSendMessage()