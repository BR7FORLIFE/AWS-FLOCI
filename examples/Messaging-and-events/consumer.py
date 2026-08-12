# el consumer solo imprimira el mensaje desde sqs
from config.sqs import receiveMessage

def ConsumerSQSPrintMessage(): 
    response = receiveMessage()
    print(response)
