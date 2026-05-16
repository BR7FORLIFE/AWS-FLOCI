# ☁️ AWS Learning Lab with Floci

> Un laboratorio personal para aprender cloud engineering, arquitectura distribuida y servicios AWS en un entorno local controlado usando Floci.

---

# 📚 Objetivo

Este repositorio documenta mi proceso de aprendizaje de:

* AWS
* Cloud Engineering
* Arquitectura distribuida
* Event-driven systems
* Containers
* Infrastructure as Code
* DevOps
* Observabilidad
* Networking
* Serverless

Todo ejecutándose localmente usando:

* Docker
* AWS CLI
* Floci

La idea es aprender cómo funcionan realmente los servicios cloud sin depender inicialmente de una cuenta AWS real.

---

# 🧠 Filosofía del proyecto

La nube no es magia.

La mayoría de servicios cloud modernos son:

```text
APIs + Networking + Containers + Storage + Orchestration
```

Este laboratorio busca entender:

* qué ocurre detrás del dashboard AWS,
* cómo se comunican los servicios,
* cómo funcionan los SDKs,
* y cómo construir arquitecturas modernas.

---

# 🏗️ Arquitectura general

```text
                ┌─────────────────┐
                │    Mi App       │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │    AWS SDK      │
                └────────┬────────┘
                         │ HTTP API
                         ▼
             http://localhost:4566
                         │
                         ▼
                ┌─────────────────┐
                │      Floci      │
                └────────┬────────┘
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
      S3              DynamoDB           SQS
```

---

# 🛠️ Stack utilizado

| Tecnología       | Propósito                |
| ---------------- | ------------------------ |
| AWS CLI          | Interactuar con APIs AWS |
| Floci            | Emulación local de AWS   |
| Docker           | Containers y servicios   |
| Terraform        | Infrastructure as Code   |
| Node.js / Python | SDKs AWS                 |
| GitHub Actions   | Automatización CI/CD     |
| Linux            | Entorno principal        |

---

# 📦 Servicios AWS que estudiaré

## Core Services

* [ ] S3
* [ ] EC2
* [ ] IAM
* [ ] VPC
* [ ] Route53

## Serverless

* [ ] Lambda
* [ ] API Gateway
* [ ] EventBridge

## Bases de datos

* [ ] DynamoDB
* [ ] RDS
* [ ] ElastiCache

## Mensajería y eventos

* [ ] SQS
* [ ] SNS
* [ ] Kinesis

## DevOps & Infra

* [ ] Terraform
* [ ] Docker
* [ ] Kubernetes
* [ ] ECS
* [ ] EKS

## Observabilidad

* [ ] CloudWatch
* [ ] Logging
* [ ] Metrics
* [ ] Tracing

---

# 🚀 Instalación

## 1. Instalar Docker

### Ubuntu/Debian

```bash
sudo apt update
sudo apt install docker.io -y
```

Iniciar Docker:

```bash
sudo systemctl start docker
sudo systemctl enable docker
```

Agregar usuario al grupo docker:

```bash
sudo usermod -aG docker $USER
newgrp docker
```

Verificar:

```bash
docker ps
```

---

## 2. Instalar AWS CLI

```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

Verificar:

```bash
aws --version
```

---

## 3. Configurar AWS CLI

```bash
aws configure
```

Usar:

```text
AWS Access Key ID: test
AWS Secret Access Key: test
Default region name: us-east-1
Default output format: json
```

---

# ⚙️ Variables de entorno

Agregar al `~/.bashrc`:

```bash
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1
export AWS_DEFAULT_OUTPUT=json

alias awslocal='aws --endpoint-url=http://localhost:4566'
```

Aplicar cambios:

```bash
source ~/.bashrc
```

---

# 🧪 Levantar Floci

```bash
docker run --rm -p 4566:4566 floci/floci:latest
```

---

# ✅ Verificar funcionamiento

## Crear bucket S3

```bash
awslocal s3 mb s3://mi-bucket
```

## Ver buckets

```bash
awslocal s3 ls
```

## DynamoDB

```bash
awslocal dynamodb list-tables
```

## SQS

```bash
awslocal sqs list-queues
```

---

# 🧠 Conceptos importantes aprendidos

## 1. AWS funciona principalmente mediante APIs HTTP

```text
AWS SDK → HTTP Requests → AWS APIs
```

---

## 2. El endpoint define a qué infraestructura hablas

AWS real:

```text
https://s3.us-east-1.amazonaws.com
```

Floci:

```text
http://localhost:4566
```

---

## 3. El SDK abstrae la complejidad

El SDK:

* firma requests,
* maneja autenticación,
* retries,
* serialización,
* paginación,
* errores.

---

## 4. Floci emula AWS localmente

Floci:

* escucha requests HTTP,
* interpreta protocolos AWS,
* responde como AWS,
* y ejecuta servicios localmente.

---

# 📁 Estructura del repositorio

```text
.
├── docs/
│   ├── notes/
│   ├── diagrams/
│   └── architecture/
│
├── projects/
│   ├── s3-lab/
│   ├── lambda-lab/
│   ├── dynamodb-lab/
│   ├── sqs-lab/
│   └── event-driven-lab/
│
├── terraform/
│   ├── networking/
│   ├── serverless/
│   └── storage/
│
├── scripts/
│   ├── bootstrap/
│   └── automation/
│
└── README.md
```

---

# 🧭 Roadmap de aprendizaje

## Fase 1 — Fundamentos

* [ ] Linux
* [ ] Networking
* [ ] HTTP
* [ ] Docker
* [ ] AWS CLI

---

## Fase 2 — AWS Core

* [ ] S3
* [ ] IAM
* [ ] EC2
* [ ] VPC

---

## Fase 3 — Arquitectura moderna

* [ ] Lambda
* [ ] Event-driven architecture
* [ ] SQS
* [ ] SNS
* [ ] API Gateway

---

## Fase 4 — Infraestructura

* [ ] Terraform
* [ ] Kubernetes
* [ ] CI/CD
* [ ] Observabilidad

---

# 🔬 Objetivos técnicos

Este laboratorio busca entender:

* Cómo funcionan las APIs cloud
* Cómo se comunican los microservicios
* Cómo funcionan los sistemas distribuidos
* Cómo funcionan los event brokers
* Cómo funciona el storage distribuido
* Cómo se automatiza infraestructura
* Cómo funcionan los containers
* Cómo se orquestan workloads

---

# 📖 Recursos útiles

## AWS

* [https://aws.amazon.com/](https://aws.amazon.com/)
* [https://docs.aws.amazon.com/](https://docs.aws.amazon.com/)
* [https://aws.amazon.com/cli/](https://aws.amazon.com/cli/)

## Floci

* [https://github.com/floci-io/floci](https://github.com/floci-io/floci)

## Terraform

* [https://developer.hashicorp.com/terraform](https://developer.hashicorp.com/terraform)

## Docker

* [https://docs.docker.com/](https://docs.docker.com/)

---

# 🎯 Meta final

Ser capaz de:

* diseñar infraestructura cloud,
* construir sistemas distribuidos,
* automatizar despliegues,
* entender networking cloud,
* trabajar con arquitecturas serverless,
* y construir aplicaciones cloud-native modernas.

---

# 📝 Notas

Este repositorio es principalmente:

* experimental,
* educativo,
* iterativo,
* y orientado a aprendizaje práctico.

### BR7FORLIFE
