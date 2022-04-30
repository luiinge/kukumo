package iti.kukumo.plugin.api.plan;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import iti.kukumo.plugin.api.KukumoPluginException;
import static iti.kukumo.plugin.api.lang.Functions.also;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class PlanSerializer {

    private static final ObjectMapper OBJECT_MAPPER = also(new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        .enable(SerializationFeature.INDENT_OUTPUT),
        it -> it.setVisibility(
            it.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        ));


    public PlanNode deserialize(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, PlanNode.class);
    }


    public String serialize(PlanNode node) {
        try {
            return OBJECT_MAPPER.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new KukumoPluginException(e);
        }
    }


}
