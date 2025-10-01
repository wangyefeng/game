package org.wyf.game.config.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.wyf.game.common.util.JsonUtil;
import org.wyf.game.config.ConfigException;
import org.wyf.game.config.entity.CfgGlobal;
import org.wyf.game.config.repository.CfgGlobalRepository;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CfgGlobalService extends CfgService<CfgGlobal, CfgGlobalRepository, String> {

    private GlobalValue globalValue;

    @Override
    public void init() throws ConfigException {
        super.init();
        Map<String, String> cfgMap = getAllCfg().stream().collect(Collectors.toMap(CfgGlobal::getKey, CfgGlobal::getValue));
        String json = JsonUtil.toJson(cfgMap);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        try {
            globalValue = mapper.readValue(json, GlobalValue.class);
        } catch (Exception e) {
            throw new ConfigException("全局配置转json异常", e);
        }
    }

    public GlobalValue getGlobalValue() {
        return globalValue;
    }

    public record GlobalValue() {

        private static class StringToIntArrayDeserializer extends JsonDeserializer<int[]> {
            @Override
            public int[] deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
                return new ObjectMapper().readValue(p.getText(), int[].class);
            }
        }
    }
}
