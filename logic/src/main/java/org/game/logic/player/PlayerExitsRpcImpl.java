package org.game.logic.player;

import io.grpc.stub.StreamObserver;
import org.game.logic.database.repository.PlayerRepository;
import org.game.proto.struct.PlayerExistServiceGrpc;
import org.game.proto.struct.Rpc.PbPlayerExistReq;
import org.game.proto.struct.Rpc.PbPlayerExistResp;
import org.game.proto.struct.Rpc.PbPlayerExistResp.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerExitsRpcImpl extends PlayerExistServiceGrpc.PlayerExistServiceImplBase {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void exists(PbPlayerExistReq req, StreamObserver<PbPlayerExistResp> responseObserver) {
        int playerId = req.getId();
        Builder reply = PbPlayerExistResp.newBuilder();
        reply.setExist(Players.containsPlayer(playerId) || playerRepository.existsById(playerId));
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

}
