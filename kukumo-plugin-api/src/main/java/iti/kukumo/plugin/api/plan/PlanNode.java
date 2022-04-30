package iti.kukumo.plugin.api.plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import static iti.kukumo.plugin.api.lang.Functions.cast;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter @ToString @RequiredArgsConstructor @Builder @Jacksonized
public class PlanNode {

    private static final PlanSerializer serializer = new PlanSerializer();

    private final NodeType nodeType;
    private final String name;
    private final String displayName;
    private final String language;
    private final String id;
    private final String source;
    private final String keyword;
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = PlanNodeArgument.class)
    private final PlanNodeArgument argument;
    private final List<String> description;
    private final List<String> tags;
    private final SortedMap<String,String> properties;
    private final List<PlanNode> children;
    private final Result result;
    private final Instant startInstant;
    private final Instant finishInstant;
    private final Duration duration;
    private final SortedMap<Result,Long> childrenResultCount;


    public String toJSON() {
        return serializer.serialize(this);
    }



    // this wrapper type is intended only for serialization purposes
    public record PlanNodeArgument(
        Document document,
        DataTable dataTable
    ) {
        public PlanNodeArgument() {
            this(null,null);
        }

        public PlanNodeArgument(NodeArgument argument) {
            this(
                cast(argument,Document.class),
                cast(argument,DataTable.class)
            );
        }
    }

}
