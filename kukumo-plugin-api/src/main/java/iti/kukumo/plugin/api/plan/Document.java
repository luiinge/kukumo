package iti.kukumo.plugin.api.plan;

import java.util.function.UnaryOperator;

public record Document(
    String contentType,
    String content
) implements NodeArgument {


    @Override
    public NodeArgument copy(UnaryOperator<String> replacingVariablesMethod) {
        return new Document(contentType, replacingVariablesMethod.apply(content));
    }

}
