package org.wyf.game.proto.struct;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.73.0)",
    comments = "Source: rpc.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PlayerExistServiceGrpc {

  private PlayerExistServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "org.game.proto.struct.PlayerExistService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Rpc.PbPlayerExistReq,
      Rpc.PbPlayerExistResp> getExistsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "exists",
      requestType = Rpc.PbPlayerExistReq.class,
      responseType = Rpc.PbPlayerExistResp.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Rpc.PbPlayerExistReq,
      Rpc.PbPlayerExistResp> getExistsMethod() {
    io.grpc.MethodDescriptor<Rpc.PbPlayerExistReq, Rpc.PbPlayerExistResp> getExistsMethod;
    if ((getExistsMethod = PlayerExistServiceGrpc.getExistsMethod) == null) {
      synchronized (PlayerExistServiceGrpc.class) {
        if ((getExistsMethod = PlayerExistServiceGrpc.getExistsMethod) == null) {
          PlayerExistServiceGrpc.getExistsMethod = getExistsMethod =
              io.grpc.MethodDescriptor.<Rpc.PbPlayerExistReq, Rpc.PbPlayerExistResp>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "exists"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Rpc.PbPlayerExistReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Rpc.PbPlayerExistResp.getDefaultInstance()))
              .setSchemaDescriptor(new PlayerExistServiceMethodDescriptorSupplier("exists"))
              .build();
        }
      }
    }
    return getExistsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PlayerExistServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PlayerExistServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PlayerExistServiceStub>() {
        @java.lang.Override
        public PlayerExistServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PlayerExistServiceStub(channel, callOptions);
        }
      };
    return PlayerExistServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static PlayerExistServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PlayerExistServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PlayerExistServiceBlockingV2Stub>() {
        @java.lang.Override
        public PlayerExistServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PlayerExistServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return PlayerExistServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PlayerExistServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PlayerExistServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PlayerExistServiceBlockingStub>() {
        @java.lang.Override
        public PlayerExistServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PlayerExistServiceBlockingStub(channel, callOptions);
        }
      };
    return PlayerExistServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PlayerExistServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PlayerExistServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PlayerExistServiceFutureStub>() {
        @java.lang.Override
        public PlayerExistServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PlayerExistServiceFutureStub(channel, callOptions);
        }
      };
    return PlayerExistServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void exists(Rpc.PbPlayerExistReq request,
                        io.grpc.stub.StreamObserver<Rpc.PbPlayerExistResp> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExistsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PlayerExistService.
   */
  public static abstract class PlayerExistServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PlayerExistServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PlayerExistService.
   */
  public static final class PlayerExistServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PlayerExistServiceStub> {
    private PlayerExistServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlayerExistServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PlayerExistServiceStub(channel, callOptions);
    }

    /**
     */
    public void exists(Rpc.PbPlayerExistReq request,
                       io.grpc.stub.StreamObserver<Rpc.PbPlayerExistResp> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExistsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PlayerExistService.
   */
  public static final class PlayerExistServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<PlayerExistServiceBlockingV2Stub> {
    private PlayerExistServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlayerExistServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PlayerExistServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public Rpc.PbPlayerExistResp exists(Rpc.PbPlayerExistReq request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExistsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service PlayerExistService.
   */
  public static final class PlayerExistServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PlayerExistServiceBlockingStub> {
    private PlayerExistServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlayerExistServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PlayerExistServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public Rpc.PbPlayerExistResp exists(Rpc.PbPlayerExistReq request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExistsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PlayerExistService.
   */
  public static final class PlayerExistServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PlayerExistServiceFutureStub> {
    private PlayerExistServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlayerExistServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PlayerExistServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Rpc.PbPlayerExistResp> exists(
        Rpc.PbPlayerExistReq request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExistsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXISTS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXISTS:
          serviceImpl.exists((Rpc.PbPlayerExistReq) request,
              (io.grpc.stub.StreamObserver<Rpc.PbPlayerExistResp>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getExistsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              Rpc.PbPlayerExistReq,
              Rpc.PbPlayerExistResp>(
                service, METHODID_EXISTS)))
        .build();
  }

  private static abstract class PlayerExistServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PlayerExistServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Rpc.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PlayerExistService");
    }
  }

  private static final class PlayerExistServiceFileDescriptorSupplier
      extends PlayerExistServiceBaseDescriptorSupplier {
    PlayerExistServiceFileDescriptorSupplier() {}
  }

  private static final class PlayerExistServiceMethodDescriptorSupplier
      extends PlayerExistServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PlayerExistServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PlayerExistServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PlayerExistServiceFileDescriptorSupplier())
              .addMethod(getExistsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
