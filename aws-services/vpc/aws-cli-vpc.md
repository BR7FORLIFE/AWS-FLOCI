# 🌐 VPC - Virtual Private Cloud (Amazon)

> **MANEJO DE NETWORKING**

## ¿Qué es una VPC?

Un VPC es mi mini red privada dentro de AWS. Dentro de dicha mini red puedo tener:

- 🏢 Mi propio datacenter virtual
- 🔢 Con IPs
- 🛣️ Rutas
- 🔐 Firewalls
- 📍 Subredes

---

## 📐 Arquitectura Correcta

```
                    Internet
                      ↓
            Internet Gateway
                      ↓
                    VPC
                   /    \
        Public Subnet   Private Subnet
             ↓                ↓
         EC2 pública      DB / backend privado
```

---

## 🎯 Temas a Profundizar

### CIDR

**CIDR** define el rango de IPs y el tamaño de la red.

**Ejemplo:** `10.0.0.0/16`
- El `/16` define qué tan grande es la red
- Ejemplo: `/32` → entre más grande el número, más pequeña es la red (y viceversa)

#### Tabla de Referencia

| CIDR | Tamaño Aproximado |
|------|------------------|
| /16  | ~65k IPs         |
| /24  | 256 IPs          |
| /32  | 1 IP             |

### 🏛️ Analogía

- **VPC** = ciudad
- **Subnets** = barrios
- **IPs** = casas

---

## ¿Qué es una SUBNET?

Una subnet es una subdivición de una VPC o un segmento de red. Hay dos tipos: **PÚBLICOS** y **PRIVADOS**.

### 🔓 Subnets Públicos

Pueden comunicarse con internet. Ejemplos de servicios:
- Nginx
- Frontend
- Load Balancer
- Etc.

Se comunican mediante **tablas de routing**.

### 🔒 Subnets Privados

No tienen acceso directo a internet. Ejemplos:
- Backends
- Bases de datos
- Servicios que no quieres exponer

No poseen tablas de routing.

### Comunicación

La comunicación entre subnet e internet se realiza mediante un **Internet Gateway (IGW)**.

#### Flujo de Comunicación

```
     EC2
      ↓
 Route Table
      ↓
Internet Gateway
      ↓
   Internet
```

---

## ¿Qué es un NAT Gateway?

Permite salida a internet **sin permitir entradas de internet**.

**Ejemplo:** En los Subnet Privados queremos llamar a internet (para pedir servicios), pero no queremos que internet interactúe con nosotros. El NAT permite hablar externamente pero no hay tráfico interno de internet.

### Caso de Uso

El backend hace una actualización de paquetes de Linux. El flujo sería:

```
  Private EC2
       ↓
  NAT Gateway
       ↓
Internet Gateway
       ↓
   Internet
```
