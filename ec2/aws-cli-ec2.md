# AWS CLI EC2 - Guía de Comandos

## Configuración Inicial

### Configurar credenciales
```bash
aws --configure
```

### Usar endpoint local (FLOCI)
```bash
aws --endpoint-url http://localhost:4566
```

### Alias útil para .bashrc o .zshrc
```bash
alias awslocal='aws --endpoint-url http://localhost:4566'
```

---

## Descripción de Zonas y Tipos de Instancias

Visualizar información disponible en tu región:

| Comando | Descripción |
|---------|-------------|
| `awslocal ec2 describe-regions` | Ver las regiones disponibles en AWS |
| `awslocal ec2 describe-availability-zones` | Ver las zonas de disponibilidad |
| `awslocal ec2 describe-instance-types` | Ver los tipos de instancias disponibles para EC2 |

---

## Configuración del Servicio EC2

### Conceptos Clave

> ⚠️ **NOTA IMPORTANTE**
> 
> Los grupos de seguridad son **independientes** de las instancias de EC2. Tú vinculas la instancia a un grupo de seguridad para aplicar sus políticas.

### 1. Crear Key Pair (PEM/RSA)

Se utiliza para conectarte a una instancia de EC2 por SSH de forma segura.

```bash
awslocal ec2 create-key-pair --key-name <nombre_de_la_clave>
```

**Parámetros:**
- `--key-name`: Nombre de la clave PEM o RSA a crear

---

### 2. Crear Grupo de Seguridad

Define un contenedor lógico para las políticas de red. El grupo de seguridad es como un firewall virtual que controla qué tráfico puede entrar o salir de una instancia, pero **aún no define reglas específicas**.

```bash
awslocal ec2 create-security-group \
  --group-name <nombre_del_grupo> \
  --description "la descripción del grupo"
```

**Parámetros:**
- `--group-name`: Nombre del grupo de seguridad
- `--description`: Descripción de la función del grupo

---

### 3. Configurar Políticas de Seguridad (Ingress)

Añade reglas de tráfico entrante al grupo de seguridad.

```bash
awslocal ec2 authorize-security-group-ingress \
  --group-name <nombre_del_grupo> \
  --protocol <protocolo> \
  --port <puerto> \
  --cidr <ip_o_rango>
```

**Parámetros:**
- `--group-name`: El grupo de seguridad al cual añadir las reglas
- `--protocol`: Protocolo a utilizar (`tcp`, `udp`, `icmp`, `-1` para todos)
- `--port`: Puerto o rango de puertos
- `--cidr`: IP, rango de IPs (ej: `192.168.1.0/24`) o todo internet (`0.0.0.0/0`)

---

### 4. Crear Instancia EC2

Lanza una nueva instancia de EC2 con la configuración especificada.

```bash
awslocal ec2 run-instances \
  --image-id <image_id> \
  --instance-type <tipo_instancia> \
  --key-name <key_id> \
  --security-groups <grupo_seguridad> \
  --min-count <minimo> \
  --max-count <maximo>
```

**Parámetros:**
- `--image-id`: Define el sistema operativo base de la VM
- `--instance-type`: Tipo de instancia (`micro`, `nano`, `small`, etc.)
- `--key-name`: SSH key pair a utilizar para acceso remoto
- `--security-groups`: Grupo de seguridad para vincular políticas
- `--min-count`: Mínimo de instancias a crear
- `--max-count`: Cantidad ideal de instancias a crear

---

## Comandos Auxiliares Útiles

Estos comandos te ayudan a inspeccionar y gestionar tus recursos:

```bash
# Listar todos los grupos de seguridad
awslocal ec2 describe-security-groups

# Listar todos los key pairs generados
awslocal ec2 describe-key-pairs

# Listar todas las imágenes disponibles (cuidado: puede haber muchas)
awslocal ec2 describe-images
```

> 💡 **Tip**: Para `describe-images`, considera filtrar por propietario para reducir el volumen de resultados.

---
