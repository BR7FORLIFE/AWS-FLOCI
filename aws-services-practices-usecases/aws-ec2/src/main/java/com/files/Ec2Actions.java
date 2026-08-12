package com.files;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.AllocateAddressRequest;
import software.amazon.awssdk.services.ec2.model.AllocateAddressResponse;
import software.amazon.awssdk.services.ec2.model.AssociateAddressRequest;
import software.amazon.awssdk.services.ec2.model.AssociateAddressResponse;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairRequest;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DisassociateAddressRequest;
import software.amazon.awssdk.services.ec2.model.DisassociateAddressResponse;
import software.amazon.awssdk.services.ec2.model.DomainType;
import software.amazon.awssdk.services.ec2.model.ReleaseAddressRequest;
import software.amazon.awssdk.services.ec2.model.ReleaseAddressResponse;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesResponse;
import software.amazon.awssdk.services.ec2.paginators.DescribeInstancesPublisher;

public class Ec2Actions {
    private static Ec2AsyncClient ec2AsyncClient; // vamos crear un singleton del cliente EC2

    /**
     * 
     * @return retornamos un cliente de ec2 de una sola instancia (singleton)
     */
    public static Ec2AsyncClient getAsyncClient() {
        if (ec2AsyncClient == null) {
            // creamos el cliente http, como la idea es usar una api asincrona con future,
            // pues el cliente http
            // debe ser event-driven, asincrono basado en eventos , donde el mejor candidato
            // es netty

            /*
             * COMENTARIO OFICIAL POR PARTE DE AWS
             * The `NettyNioAsyncHttpClient` class is part of the AWS SDK for Java, version
             * 2,
             * and it is designed to provide a high-performance, asynchronous HTTP client
             * for interacting with AWS services.
             * It uses the Netty framework to handle the underlying network communication
             * and the Java NIO API to
             * provide a non-blocking, event-driven approach to HTTP requests and responses.
             */
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient
                    .builder()
                    .maxConcurrency(50)
                    .connectionTimeout(Duration.ofSeconds(60))
                    .readTimeout(Duration.ofSeconds(60))
                    .writeTimeout(Duration.ofSeconds(60))
                    .build();

            ClientOverrideConfiguration configuration = ClientOverrideConfiguration
                    .builder()
                    .apiCallAttemptTimeout(Duration.ofSeconds(60))
                    .apiCallTimeout(Duration.ofMinutes(2))
                    .build();

            Ec2AsyncClient ec2Client = Ec2AsyncClient
                    .builder()
                    .region(Region.US_EAST_1)
                    .httpClient(httpClient)
                    .overrideConfiguration(configuration)
                    .endpointOverride(URI.create("localhost:4566")) // servicio de floci
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create("test", "test")))
                    .build();

            Ec2Actions.ec2AsyncClient = ec2Client;
            return ec2Client;
        }

        return ec2AsyncClient;
    }

    // metodo para borrar el keiy pair que por permite conectarnos a la instancia de
    // ec2
    public CompletableFuture<DeleteKeyPairResponse> deleteKeyPairRequest(String keyPairName) {
        DeleteKeyPairRequest request = DeleteKeyPairRequest
                .builder()
                .keyName(keyPairName)
                .build();

        // usamos el cliente ec2 para notificar que queremos borrarr el keypair que
        // hemos generado
        // respuesta del servidor aws por medio del cliente de ec2 que hemos creado
        // previamente
        CompletableFuture<DeleteKeyPairResponse> res = getAsyncClient().deleteKeyPair(request);

        return res.whenComplete((resp, exception) -> {
            if (exception != null) {
                throw new RuntimeException("Error al borrar el keypair en el cliente de ec2");
            } else if (resp == null) {
                throw new RuntimeException("El servidor no respondió a tu peticion");
            }
        });
    }

    // metodo para borrar el security group vinculado a la instancia ec2
    public CompletableFuture<DeleteSecurityGroupResponse> unlinkSecurityGroup(String groupId) {
        DeleteSecurityGroupRequest request = DeleteSecurityGroupRequest
                .builder()
                .groupId(groupId)
                .build();

        CompletableFuture<DeleteSecurityGroupResponse> response = getAsyncClient().deleteSecurityGroup(request);

        return response.whenComplete((resp, exception) -> {
            if (exception != null) {
                throw new RuntimeException("Error al desvincular el security group vinculada a la instancia actual!");
            } else if (resp != null) {
                throw new RuntimeException("El servidor no respondió a tu peticion");
            }
        });
    }

    // metodo para detener la instancia de ec2
    public CompletableFuture<Object> terminatedInstance(String instanceId) {
        TerminateInstancesRequest request = TerminateInstancesRequest
                .builder()
                .instanceIds(instanceId)
                .build();

        CompletableFuture<TerminateInstancesResponse> response = getAsyncClient().terminateInstances(request);

        return response.thenCompose(terminateInstance -> {
            if (terminateInstance == null) {
                throw new RuntimeException("No hay respuesta del servidor para la instancia: " + instanceId);
            }

            return getAsyncClient().waiter()
                    .waitUntilInstanceTerminated(r -> r.instanceIds(instanceId))
                    .thenApply(waiterResponse -> null);
        }).exceptionally(err -> {
            throw new RuntimeException("Error al finalizar la instancia con la Id: " + instanceId);
        });
    }

    // ELIMINA EL ELASTICID Y LA ELIMINA TOTALMENTE DE LA INSTANCIA
    public CompletableFuture<ReleaseAddressResponse> releaseEC2AddressAsync(String allocId) {

        ReleaseAddressRequest request = ReleaseAddressRequest.builder()
                .allocationId(allocId)
                .build();

        CompletableFuture<ReleaseAddressResponse> response = getAsyncClient().releaseAddress(request);
        response.whenComplete((resp, ex) -> {
            if (ex != null) {
                throw new RuntimeException("No se pudo liberar la direccion IP estatica", ex);
            }
        });

        return response;
    }

    // este metodo desvincula el elasticID de la instancia actual (PERO SIGUE
    // EXISTIENDO ASI SEA DESVINCULADA)
    public CompletableFuture<DisassociateAddressResponse> disassociateAddressAsync(String associationId) {
        DisassociateAddressRequest addressRequest = DisassociateAddressRequest.builder()
                .associationId(associationId)
                .build();

        CompletableFuture<DisassociateAddressResponse> response = getAsyncClient().disassociateAddress(addressRequest);
        response.whenComplete((resp, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Error al desvincular la direccion elastica", ex);
            }
        });

        return response;
    }

    /**
     * Conceptos a tener en cuenta:
     * 
     * ElasticID -> es una IP publica reservada para cada cuenta de AWS No pertenece
     * a ninguna instancia
     * 
     * - todas las instancias AWS las reconoce por medio de su ID y nunca por su IP
     * 
     * - cuando una instancia detiene su ejecucion y se vuelve a ejecutar su IP
     * cambia,
     * la estrategia que sea usa es reserar un allocateAddress() esto permite
     * vincular esta IP
     * a alguna instancia y deja de usar una direccion temporal para usar la
     * reservada.
     * 
     * - cuando se llama el metodo allocateAddress() -> te retorna un
     * AllocateAddresResponse
     * donde contiene la id o allocationId y la IP de AWS que nos asignó
     * 
     * @param instanceId
     * @param allocationId
     * @return
     */
    public CompletableFuture<String> associateAddressAsync(String instanceId, String allocationId) {
        // queremos enviar una peticion para poder vincular un allocationId a la
        // instancia que deseamos crear
        // ya que por defecto una ip de uns instancia es efímero y esto perjudica por
        // ejemplo a una API
        // ya que al cambiar a cada rato su IP el servicio no funcionara y no se podrá
        // obtener respuesta
        AssociateAddressRequest associateRequest = AssociateAddressRequest.builder()
                .instanceId(instanceId)
                .allocationId(allocationId)
                .build();

        // nos comunicamos con AWS para solicitar vincular un allocationAddress a una
        // instancia
        CompletableFuture<AssociateAddressResponse> responseFuture = getAsyncClient()
                .associateAddress(associateRequest);
        return responseFuture.thenApply(response -> {
            if (response.associationId() != null) {
                return response.associationId();
            } else {
                throw new RuntimeException("El ID de asociación es nulo después de asociar la dirección.");
            }
        }).whenComplete((result, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Error al asociar la direccion", ex);
            }
        });
    }

    /**
     * este metodos nos permite reservar un nuevo elastic IP y devolver su
     * allocationId
     * 
     * @return nuevo allocateId
     */
    public CompletableFuture<String> allocateAddressAsync() {
        AllocateAddressRequest allocateRequest = AllocateAddressRequest.builder()
                .domain(DomainType.VPC)
                .build();

        CompletableFuture<AllocateAddressResponse> responseFuture = getAsyncClient().allocateAddress(allocateRequest);
        return responseFuture.thenApply(AllocateAddressResponse::allocationId).whenComplete((result, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Failed to allocate address", ex);
            }
        });
    }

    // metodo para solicitar informacion de una instancia o varias instancias en
    // concreto
    public CompletableFuture<String> describeEc2InstancesAsync(String newInstanceId) {
        DescribeInstancesRequest request = DescribeInstancesRequest
                .builder()
                .instanceIds(newInstanceId)
                .build();

        DescribeInstancesPublisher paginator = getAsyncClient().describeInstancesPaginator(request);
        AtomicReference<String> publicIpAddressRef = new AtomicReference<>();
        return paginator.subscribe(response -> {
            response.reservations().stream()
                    .flatMap(reservation -> reservation.instances().stream())
                    .filter(instance -> instance.instanceId().equals(newInstanceId))
                    .findFirst()
                    .ifPresent(instance -> publicIpAddressRef.set(instance.publicIpAddress()));
        }).thenApply(v -> {
            String publicIpAddress = publicIpAddressRef.get();
            if (publicIpAddress == null) {
                throw new RuntimeException("Instancia con la Id:  " + newInstanceId + " no se encuentra.");
            }
            return publicIpAddress;
        }).exceptionally(ex -> {
            throw new RuntimeException("Error al mostrar las instancias", ex);
        });
    }

}
