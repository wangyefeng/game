package org.wyf.game.logic.player;

import io.grpc.stub.StreamObserver;
import org.wyf.game.logic.database.repository.PlayerRepository;
import org.wyf.game.proto.struct.PlayerExistServiceGrpc;
import org.wyf.game.proto.struct.Rpc.PbPlayerExistReq;
import org.wyf.game.proto.struct.Rpc.PbPlayerExistResp;
import org.wyf.game.proto.struct.Rpc.PbPlayerExistResp.Builder;
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
