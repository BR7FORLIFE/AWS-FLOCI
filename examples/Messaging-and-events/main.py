from multiprocessing import Process
from producer import ProducerSQSSendMessage
from consumer import ConsumerSQSPrintMessage


if __name__ == "__main__":
    producer = Process(target= ProducerSQSSendMessage)
    consumer = Process(target=ConsumerSQSPrintMessage)

    # comenzamos a crear los procesos
    producer.start()
    consumer.start()