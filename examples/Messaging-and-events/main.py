from multiprocessing import Process
from producer import ProducerSQSSendMessage
from consumer import ConsumerSQSPrintMessage


if __name__ == "__main__":
    # SQS Process
    producer = Process(target= ProducerSQSSendMessage)
    consumer = Process(target=ConsumerSQSPrintMessage)

    # comenzamos a crear los procesos de SQS
    producer.start()
    consumer.start()

    # SNS Process
    

    # comenzamos a crear los procesos de SNS
