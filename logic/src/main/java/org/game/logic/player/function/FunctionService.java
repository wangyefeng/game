package org.game.logic.player.function;

import org.game.config.entity.CfgFunction;
import org.game.logic.AbstractGameService;
import org.game.logic.entity.FunctionInfo;
import org.game.logic.repository.FunctionRepository;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FunctionService extends AbstractGameService<FunctionInfo, FunctionRepository> {

    private static final Logger log = LoggerFactory.getLogger(FunctionService.class);

    private Map<ModuleEnum, Module> extendModuleMap = new HashMap<>();

    @Override
    public void register(PbRegisterReq registerMsg) {
        entity = new FunctionInfo(player.getId());
    }

    @Override
    public void loginResp(Builder loginResp) {
        // do nothing
    }

    public void registerModule(Module module) {
        extendModuleMap.put(module.getModuleEnum(), module);
    }

    public void open(CfgFunction cfgFunction, boolean isSend) {
        log.info("玩家{}打开功能{}", player.getId(), cfgFunction.getId());
        entity.addFunctionId(cfgFunction.getId());
        FunctionEnum functionEnum = FunctionEnum.getByType(cfgFunction.getType());
        for (ModuleEnum moduleEnum : functionEnum.getModules()) {
            Module module = extendModuleMap.get(moduleEnum);
            if (module != null) {
                try {
                    module.open(cfgFunction, isSend);
                } catch (Exception e) {
                    log.error("玩家{}打开功能{}失败", player.getId(), cfgFunction.getId(), e);
                }
            }
        }
    }

    public void close(CfgFunction cfgFunction, boolean isSend) {
        int functionId = cfgFunction.getId();
        entity.getFunctionIds().remove(functionId);
        FunctionEnum functionEnum = FunctionEnum.getByType(cfgFunction.getType());
        for (ModuleEnum moduleEnum : functionEnum.getModules()) {
            Module module = extendModuleMap.get(moduleEnum);
            if (module != null) {
                try {
                    module.close(cfgFunction, isSend);
                } catch (Exception e) {
                    log.error("玩家{}关闭功能{}失败", player.getId(), functionId, e);
                }
            }
        }
    }
}
